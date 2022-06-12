CREATE TABLE IF NOT EXISTS moderation
(
    id           INT          AUTO_INCREMENT primary key NOT NULL,
    guild_id     VARCHAR(255) NOT NULL,
    user_id      VARCHAR(255) NOT NULL,
    moderator_id VARCHAR(255) NOT NULL,
    reason       VARCHAR(255) NOT NULL
)