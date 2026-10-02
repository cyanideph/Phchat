-- Runtime policy hardening for authenticated self-owned interaction tables.
-- The existing *_self policies use statement-stable (select auth.uid()) evaluation.
-- This migration removes redundant per-command policies and leaves one ALL policy
-- per self-owned interaction table. user_check_ins intentionally remains publicly
-- readable for the leaderboard while inserts stay restricted to the current user.

drop policy if exists content_reactions_select_self on public.content_reactions;
drop policy if exists content_reactions_insert_self on public.content_reactions;
drop policy if exists content_reactions_delete_self on public.content_reactions;
drop policy if exists content_reactions_self on public.content_reactions;
create policy content_reactions_self on public.content_reactions
  for all to authenticated
  using (user_id = (select auth.uid()))
  with check (user_id = (select auth.uid()));

drop policy if exists content_saves_select_self on public.content_saves;
drop policy if exists content_saves_insert_self on public.content_saves;
drop policy if exists content_saves_delete_self on public.content_saves;
drop policy if exists content_saves_self on public.content_saves;
create policy content_saves_self on public.content_saves
  for all to authenticated
  using (user_id = (select auth.uid()))
  with check (user_id = (select auth.uid()));

drop policy if exists poll_votes_select_self on public.poll_votes;
drop policy if exists poll_votes_insert_self on public.poll_votes;
drop policy if exists poll_votes_self on public.poll_votes;
create policy poll_votes_self on public.poll_votes
  for all to authenticated
  using (user_id = (select auth.uid()))
  with check (user_id = (select auth.uid()));

drop policy if exists message_reads_select_self on public.message_reads;
drop policy if exists message_reads_insert_self on public.message_reads;
drop policy if exists message_reads_update_self on public.message_reads;
drop policy if exists message_reads_self on public.message_reads;
create policy message_reads_self on public.message_reads
  for all to authenticated
  using (user_id = (select auth.uid()))
  with check (user_id = (select auth.uid()));

drop policy if exists user_check_ins_select_self on public.user_check_ins;
drop policy if exists user_check_ins_insert_self on public.user_check_ins;
create policy user_check_ins_insert_self on public.user_check_ins
  for insert to authenticated
  with check (user_id = (select auth.uid()));
