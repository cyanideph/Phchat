create or replace function public.list_content_feed(
  p_room_id uuid default null,
  p_author_id uuid default null,
  p_before_created_at timestamptz default null,
  p_before_id uuid default null,
  p_limit integer default 30
)
returns jsonb
language plpgsql
set search_path to 'public'
as $function$
declare
  v_limit int := least(greatest(coalesce(p_limit,30),1),50);
  v_items jsonb;
  v_more boolean;
begin
  if p_room_id is not null and not private.is_room_member(p_room_id) then
    raise exception using errcode='42501',message='Not a room member';
  end if;

  with q as (
    select c.*,
      to_jsonb(p) as author,
      (select count(*) from public.content_reactions cr where cr.content_id=c.id) as likes_count,
      exists(select 1 from public.content_reactions cr where cr.content_id=c.id and cr.user_id=(select auth.uid())) as is_liked,
      (select count(*) from public.content_comments cc where cc.content_id=c.id and cc.deleted_at is null) as comments_count,
      exists(select 1 from public.content_saves cs where cs.content_id=c.id and cs.user_id=(select auth.uid())) as is_saved,
      coalesce((
        select jsonb_agg(jsonb_build_object(
          'id',po.id,'label',po.label,'position',po.position,
          'vote_count',(select count(*) from public.poll_votes pv where pv.option_id=po.id),
          'is_selected',exists(select 1 from public.poll_votes pv where pv.option_id=po.id and pv.user_id=(select auth.uid()))
        ) order by po.position)
        from public.poll_options po where po.content_id=c.id
      ), '[]'::jsonb) as poll_options
    from public.contents c
    left join public.profiles p on p.id=c.author_id
    where c.is_published and not c.is_hidden
      and (p_room_id is null or c.room_id=p_room_id)
      and (p_author_id is null or c.author_id=p_author_id)
      and (p_before_created_at is null or (c.created_at,c.id)<(p_before_created_at,p_before_id))
    order by c.created_at desc,c.id desc
    limit v_limit+1
  )
  select coalesce(jsonb_agg(to_jsonb(q) order by q.created_at desc,q.id desc),'[]'::jsonb),count(*)>v_limit
  into v_items,v_more from q;

  return jsonb_build_object(
    'items',
    case when v_more then (
      select jsonb_agg(x) from jsonb_array_elements(v_items) with ordinality e(x,n) where n<=v_limit
    ) else v_items end,
    'has_more',v_more
  );
end
$function$;

create or replace function public.list_conversations()
returns table(
  id uuid,
  participant_id uuid,
  participant_username text,
  participant_display_name text,
  participant_bio text,
  participant_status_text text,
  participant_is_active boolean,
  participant_last_seen_at timestamptz,
  last_message text,
  last_message_kind message_kind,
  last_message_at timestamptz,
  unread_count bigint,
  is_pinned boolean
)
language sql
stable
set search_path to 'public'
as $function$
  select
    c.id, other.user_id, p.username, p.display_name, p.bio, p.status_text,
    p.is_active, p.last_seen_at, lm.body, lm.kind, lm.created_at,
    (
      select count(*) from public.conversation_messages unread
      where unread.conversation_id=c.id
        and unread.sender_id <> (select auth.uid())
        and unread.deleted_at is null
        and unread.created_at > coalesce(me.last_read_at,'epoch'::timestamptz)
    ) as unread_count,
    false as is_pinned
  from public.conversation_members me
  join public.conversations c on c.id=me.conversation_id
  join lateral (
    select cm.user_id from public.conversation_members cm
    where cm.conversation_id=c.id and cm.user_id <> (select auth.uid())
    order by cm.joined_at asc limit 1
  ) other on true
  join public.profiles p on p.id=other.user_id
  left join lateral (
    select m.body,m.kind,m.created_at from public.conversation_messages m
    where m.conversation_id=c.id
    order by m.created_at desc,m.id desc limit 1
  ) lm on true
  where me.user_id=(select auth.uid())
  order by coalesce(lm.created_at,c.updated_at) desc,c.id desc
$function$;

grant execute on function public.list_conversations() to authenticated;
