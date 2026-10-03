alter table public.profiles add column if not exists province text;

create or replace function public.handle_new_user()
returns trigger
language plpgsql
security definer
set search_path = public
as $function$
declare
  requested_username text;
  base_username text;
  candidate text;
  suffix integer := 0;
  requested_province text;
begin
  requested_username := nullif(btrim(new.raw_user_meta_data->>'username'), '');
  requested_province := nullif(btrim(new.raw_user_meta_data->>'province'), '');

  base_username := left(
    regexp_replace(
      coalesce(requested_username, 'user_' || replace(left(new.id::text, 8), '-', '')),
      '[^a-zA-Z0-9_]+',
      '_',
      'g'
    ),
    32
  );

  if char_length(base_username) < 3 then
    base_username := 'user_' || replace(left(new.id::text, 8), '-', '');
  end if;

  candidate := base_username;

  while exists (select 1 from public.profiles where username = candidate) loop
    suffix := suffix + 1;
    candidate := left(base_username, 32 - char_length(suffix::text) - 1) || '_' || suffix::text;
  end loop;

  insert into public.profiles (id, username, display_name, province)
  values (new.id, candidate, nullif(btrim(new.raw_user_meta_data->>'display_name'), ''), requested_province)
  on conflict (id) do update
    set province = coalesce(excluded.province, public.profiles.province);

  return new;
end;
$function$;
