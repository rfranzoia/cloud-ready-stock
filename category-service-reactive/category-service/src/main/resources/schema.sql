create table categories (
                            id serial primary key,
                            name varchar(255)
);

alter table categories owner to stock;
