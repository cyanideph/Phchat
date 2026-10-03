grant select, insert, update, delete on table public.user_check_ins to authenticated;

create or replace function public.list_room_messages(
  p_room_id uuid,
  p_before_created_at timestamptz default null,
  p_before_id uuid default null,
  p_limit integer default 50
)
returns jsonb
language plpgsql
stable
set search_path to 'public', 'pg_temp'
as $function$
declare
  v_limit integer := least(greatest(coalesce(p_limit,50),1),100);
  v_items jsonb := '[]'::jsonb;
  v_has_more boolean := false;
  v_cursor jsonb := null;
begin
  if not private.is_room_member(p_room_id) then
    raise exception 'not authorized';
  end if;
  with page as (
    select rm.*, row_number() over(order by rm.created_at desc,rm.id desc) rn
    from public.room_messages rm
    where rm.room_id=p_room_id
      and (p_before_created_at is null or rm.created_at<p_before_created_at or (rm.created_at=p_before_created_at and rm.id<p_before_id))
    order by rm.created_at desc,rm.id desc
    limit v_limit+1
  ), enriched as (
    select page.*,
      coalesce(nullif(trim(coalesce(p.display_name,'')||' '||coalesce(p.username,'')),''),p.username,'Member') sender_name,
      (page.sender_id=(select auth.uid())) mine,
      coalesce((select jsonb_object_agg(x.reaction,x.cnt) from (select mr.reaction,count(*) cnt from public.message_reactions mr where mr.message_id=page.id group by mr.reaction)x),'{}'::jsonb) reaction_counts,
      coalesce((select jsonb_agg(mr.reaction order by mr.reaction) from public.message_reactions mr where mr.message_id=page.id and mr.user_id=(select auth.uid())),'[]'::jsonb) my_reactions,
      exists(select 1 from public.message_reads mrd where mrd.message_id=page.id and mrd.user_id=(select auth.uid())) is_read
    from page left join public.profiles p on p.id=page.sender_id
  )
  select coalesce(jsonb_agg((to_jsonb(x)-'rn') order by x.created_at desc,x.id desc) filter(where x.rn<=v_limit),'[]'::jsonb),
    count(*)>v_limit,
    case when count(*)>v_limit then jsonb_build_object('created_at',max(x.created_at) filter(where x.rn=v_limit),'id',(select x2.id from enriched x2 where x2.rn=v_limit)) else null end
  into v_items,v_has_more,v_cursor from enriched x;
  return jsonb_build_object('items',v_items,'has_more',v_has_more,'next_cursor',v_cursor);
end;
$function$;

create or replace function public.delete_room_message(p_message_id uuid)
returns public.room_messages
language plpgsql
set search_path to 'public'
as $function$
declare v_message public.room_messages;
begin
  update public.room_messages m
  set deleted_at=now(), body='[Message deleted]'
  where m.id=p_message_id and (m.sender_id=(select auth.uid()) or private.is_room_staff(m.room_id)) and m.deleted_at is null
  returning * into v_message;
  if not found then raise exception using errcode='42501',message='Message cannot be deleted'; end if;
  return v_message;
end;
$function$;