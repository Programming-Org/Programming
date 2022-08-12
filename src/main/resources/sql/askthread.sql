CREATE TABLE IF NOT EXISTS askthread (
    member_id     varchar(255) not null,
    guild_id     varchar(255) not null,
    amount       int not null,
    time_stamp   timestamp not null default current_timestamp
)