CREATE TABLE IF NOT EXISTS tempban
(
    id           serial       not null primary key,
    guild_id     varchar(255) not null,
    user_id      varchar(255) not null,
    moderator_id varchar(255) not null,
    duration     interval     not null,
    start        timestamp    not null,
    reason       varchar(255) not null
)