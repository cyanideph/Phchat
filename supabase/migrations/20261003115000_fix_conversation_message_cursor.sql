create or replace function public.list_conversation_messages(
  p_conversation_id uuid,
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
  if not private.is_conversation_member(p_conversation_id) then
    raise exception 'not authorized';
  end if;

  with page as (
    select cm.*, row_number() over(order by cm.created_at desc,cm.id desc) rn
    from public.conversation_messages cm
    where cm.conversation_id=p_conversation_id
      and (p_before_created_at is null or cm.created_at<p_before_created_at or (cm.created_at=p_before_created_at and cm.id<p_before_id))
    order by cm.created_at desc,cm.id desc
    limit v_limit+1
  )
  select
    coalesce(jsonb_agg(to_jsonb(x)-'rn' order by x.created_at desc,x.id desc) filter(where x.rn<=v_limit),'[]'::jsonb),
    count(*)>v_limit,
    case when count(*)>v_limit then jsonb_build_object(
      'created_at',max(x.created_at) filter(where x.rn=v_limit),
      'id',(select x2.id from page x2 where x2.rn=v_limit)
    ) else null end
  into v_items,v_has_more,v_cursor
  from page x;

  return jsonb_build_object('items',v_items,'has_more',v_has_more,'next_cursor',v_cursor);
end;
$function$;