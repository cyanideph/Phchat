-- Runtime policy hardening for authenticated self-owned interactions.
create policy content_reactions_select_self on public.content_reactions for select to authenticated using (user_id = auth.uid());
create policy content_reactions_insert_self on public.content_reactions for insert to authenticated with check (user_id = auth.uid());
create policy content_reactions_delete_self on public.content_reactions for delete to authenticated using (user_id = auth.uid());

create policy content_saves_select_self on public.content_saves for select to authenticated using (user_id = auth.uid());
create policy content_saves_insert_self on public.content_saves for insert to authenticated with check (user_id = auth.uid());
create policy content_saves_delete_self on public.content_saves for delete to authenticated using (user_id = auth.uid());

create policy poll_votes_select_self on public.poll_votes for select to authenticated using (user_id = auth.uid());
create policy poll_votes_insert_self on public.poll_votes for insert to authenticated with check (user_id = auth.uid());

create policy user_check_ins_select_self on public.user_check_ins for select to authenticated using (user_id = auth.uid());
create policy user_check_ins_insert_self on public.user_check_ins for insert to authenticated with check (user_id = auth.uid());

create policy message_reads_select_self on public.message_reads for select to authenticated using (user_id = auth.uid());
create policy message_reads_insert_self on public.message_reads for insert to authenticated with check (user_id = auth.uid());
create policy message_reads_update_self on public.message_reads for update to authenticated using (user_id = auth.uid()) with check (user_id = auth.uid());
