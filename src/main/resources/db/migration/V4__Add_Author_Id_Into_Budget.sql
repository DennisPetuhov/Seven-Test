alter table budget
    add column author_id int null;
alter table budget
    add constraint fk_budget_author foreign key (author_id) references author (id) on delete set null on update cascade;