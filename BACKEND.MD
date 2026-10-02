PHCHAT_BACKEND（PHN Chat 后端）全面分析报告

> 生成日期：2026-10-02 ｜ 分析来源：Supabase 项目实时检查

一、项目基本信息

项目	值	
项目名	PHCHAT_BACKEND	
Project Ref	`mauhdrdnlrvjkekxencu`	
API URL	https://mauhdrdnlrvjkekxencu.supabase.co	
组织	cuspsczzquuabltvsmzd	
区域	ap-northeast-1（东京）	
状态	ACTIVE_HEALTHY	
Postgres	17.6（engine 17，ga 通道）	
创建时间	2026-09-25	
Edge Functions	无（全部为数据库内 RPC，纯 Postgres 架构）	

同一组织下另有 UZZAPANDROID_BACKEND（ap-southeast-1），注意本项目的迁移名为 `initial_uzzap_backend_foundation`、Storage 策略名以 `uzzap_` 开头——PHCHAT 与 UZZAP 是同一套"UZZAP"聊天后端体系的两个项目。

二、数据库 Schema（public 全部 30 张表，全部启用 RLS）

用户与社交
- profiles：核心表。id（FK→auth.users）、username（唯一，3–32字符）、display_name、avatar_path、bio、status_text、is_active、last_seen_at。几乎所有表都 FK 指向它。`handle_new_user` 触发器在 auth.users 注册时自动建档。
- relationships（follow/block）、user_favorites、profile_visits（访问记录）、profile_comments + profile_comment_votes（个人主页留言墙，body 1–4000 字符校验）、featured_profiles（推荐位，支持排序 position、featured_until 过期）。

聊天室（Rooms）
- rooms：slug 唯一、kind 枚举（public/private/group）、province_code（省级聊天室——菲律宾定位）、is_locked、view_only、members_can_invite、pinned_message_id、announcement。
- room_members：复合主键 (room_id, user_id)；role 枚举（owner/admin/moderator/member）；nickname、muted_until、banned_until、last_read_at、chat_notifications_enabled、is_pinned。
- room_messages：kind 枚举（text/system/media/reply/sticker）、body、reply_to_id（自引用）、metadata jsonb、软删除 deleted_at、edited_at。
- 配套：message_mentions、message_reactions、room_message_reactions（两套并存）、message_replies、message_reads（已读回执）、room_invites、room_co_hosts + room_co_host_requests（pending/accepted/declined/cancelled 生命周期）、room_member_strikes（处罚记分）。

私信（Conversations）
- conversations（kind 复用 room_kind 枚举）、conversation_members、conversation_messages、conversation_invites。结构与 rooms 对称。

内容/社区
- contents（帖子/wiki，kind 文本默认 post，is_published/is_featured/is_hidden）、content_comments（树形 parent_id）、content_reactions、content_saves、poll_options + poll_votes（投票）、content_categories + content_category_items（分类，slug 正则校验）、content_comment_votes（±1 投票）。
- media：统一媒体表，bucket+path+尺寸+时长，可挂到 content/room/message。

治理与互动
- notifications（type 文本 + payload jsonb，read_at）、notification_preferences（10 类通知开关）。
- moderation_actions（warn/mute/kick/ban/unban/delete_message/lock_room/unlock_room/strike）、reports（open/reviewing/resolved/dismissed）、user_check_ins（每日签到+streak+points，幂等）。

三、业务逻辑层（约 120 个数据库函数）

public RPC（安全调用者 invoker 模式为主，客户端直调）
- 消息：send_room_message / send_conversation_message / reply_to / edit / delete / send_room_sticker / attach_room_message_media / set_room_message_mentions / mark_message_read
- 房间管理：create_room / join_room / leave_room / set_room_lock / set_room_pinned_message / set_room_chat_settings / set_room_member_chat_preferences
- 私信：create_conversation / create_conversation_invite / respond_conversation_invite / mark_conversation_read
- 管理/治理：moderate_room_member / moderate_room_message / kick_room_member（含 allow_rejoin 契约）/ strike_room_member / list_moderation_history / list_room_reports / list_room_banned_members / list_room_muted_members
- 内容：create_content / edit_content / delete_content / repost_content / set_content_featured / set_content_visibility / vote_content_poll / get_content_poll_results / toggle_content_reaction / toggle_content_save / add_content_comment / toggle_content_comment_vote / assign/remove_content_category
- 社交：toggle_follow / toggle_block / toggle_favorite / list_followers / list_following / list_blocked_users / record_profile_visit / list_profile_visitors / add/delete_profile_comment / toggle_profile_comment_vote / set_featured_profile / unfeature_profile / reorder_featured_profiles
- 发现/列表：list_public_chats / search_public_chats / list_public_rooms / search_public_rooms / list_online_users / list_online_room_members / list_content_feed / search_content / list_featured_profiles / get_engagement_leaderboard / check_in（签到）
- 通知：list_notifications / mark_notification_read / mark_all_notifications_read / clear_notifications / delete_notification / get/set_notification_preferences

private schema（安全核心）
- 权限判定 SECURITY DEFINER 辅助：is_room_member / is_room_staff / is_conversation_member / is_conversation_staff / is_room_co_host / notification_allowed
- definer 实现 + public invoker 包装：create_conversation、markread、touch_room_presence、create_notification、get_content_poll_results
- 触发器函数：broadcast（7 个 realtime 广播）+ notify（6 个通知生成）

四、Realtime 架构

- Publication `supabase_realtime`：room_members、room_messages、message_reactions、conversation_messages、notifications。
- Broadcast 触发器：room_messages、conversation_messages、message_reads、message_replies、room_message_reactions、profile_comments、room_members、notifications 均有 `*_realtime_broadcast` 触发器 → private.broadcast，经授权频道广播（harden_chat_realtime_authorization / authorize_user_notification_channels 迁移加固过）。
- 通知触发器：content_comments、content_reactions、relationships、message_mentions、room_message_reactions、profile_comments 自动产生 notifications（受 notification_allowed 偏好过滤）。

五、Storage

3 个 bucket：`avatars`（public）、`content-media`（private）、`room-media`（private）。storage.objects 上有 11 条 `uzzap_*` 策略（头像增删改、内容/房间媒体增删改查）。

六、RLS 与权限模型

- 全部 30 张表启用 RLS，约 90 条策略，均作用于 `authenticated` 角色，无 anon 访问。
- 模式：自资源自管理（profiles_update_self 等）；房间资源按成员/职员（staff=owner/admin/moderator）判定；私信仅成员可见；私信成员删除仅限本人或管理员；举报仅举报人或职员可见。
- 注册即建档：auth.users → `on_auth_user_created` → public.handle_new_user → profiles。

七、迁移史（54 个，2026-09-25 → 09-30）

四个阶段：
1. 09-25 基础搭建：uzzap 基础、成员安全、FK 索引、chat 契约、storage、realtime 基础与加固、mentions/media/sticker 契约、房间控制 v2、co-host、kick-rejoin、strikes、投票、留言墙、分类、签到榜单、featured profiles、私信完成。
2. 09-25 晚间修复链：私信 create_conversation 的 auth 上下文/RLS 递归/授权连环修复（约 10 个 hotfix 迁移），pgTap 回归测试启用。
3. 09-26：注册自动建档、`close_full_system_audit_security_gaps`（全系统安全审计）。
4. 09-29  09-30：索引与 RLS 契约加固、definer search_path 加固、投票结果 RPC 及边界加固。

八、扩展

已启用：pgcrypto、uuid-ossp、pg_stat_statements、supabase_vault、pgtap（数据库回归测试）。

九、当前数据规模（2026-10-02）

处于早期测试阶段：1 个用户（user_cb041e8e）、5 个房间、11 条房间消息、8 条内容、4 条评论、3 个收藏、2 条通知。私信、举报、投票、co-host 等表为空。

十、发现的问题

安全（1 个 WARN）
- Leaked Password Protection 未启用：Auth 未对接 HaveIBeenPwned 校验泄露密码。修复：https://supabase.com/docs/guides/auth/password-security#password-strength-and-leaked-password-protection

性能（70 个 INFO）
- 70 个索引从未使用（unused_index），覆盖几乎所有表——因数据量极小属正常，随流量增长再评估是否保留。详情：https://supabase.com/docs/guides/database/database-linter?lint=0005_unused_index
- 注意：存在重复索引对，如 `moderation_room_created_idx` 与 `moderation_actions_room_created_idx` 疑似冗余。

架构观察
- 无 Edge Functions、无 Cron——所有业务逻辑在 Postgres RPC + 触发器内，客户端直连 PostgREST/Realtime。
- message_reactions 与 room_message_reactions 功能重叠（双轨并存）。
