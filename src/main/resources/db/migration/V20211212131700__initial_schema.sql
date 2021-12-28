create table clients
(
    id varchar(255) not null
        constraint users_pkey
            primary key,
    created_at timestamp not null,
    updated_at timestamp not null,
    email_address varchar(255) not null
);
