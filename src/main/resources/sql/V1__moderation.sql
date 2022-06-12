CREATE TABLE IF NOT EXISTS moderation
(
    id           serial not null primary key,
    guild_id     varchar(255) not null,
    user_id      varchar(255) not null,
    moderator_id varchar(255) not null,
    time_stamp   timestamp not null default current_timestamp,
    reason       varchar(255) not null
)