create
database app_server character set utf8mb4;

CREATE TABLE `app_user_info`
(
    `id`             bigint       NOT NULL AUTO_INCREMENT,
    `user_account`   varchar(512) NOT NULL COMMENT '用户的账号',
    `user_nickname`  varchar(64)  DEFAULT NULL COMMENT '用户的昵称',
    `user_password`  varchar(32)  DEFAULT NULL COMMENT '用户的密码',
    `avatar_url`     varchar(200) DEFAULT NULL COMMENT '用户头像url',
    `chat_user_name` varchar(512) NOT NULL COMMENT '声网用户名',
    `agora_uid`      varchar(20)  NOT NULL COMMENT '声网用户id',
    `appkey`         varchar(512) NOT NULL COMMENT 'appkey',
    `created_at`     datetime(6) DEFAULT NULL COMMENT '用户创建时间',
    PRIMARY KEY (`id`, `user_account`),
    UNIQUE KEY `uniq_appkey_useraccount` (`appkey`(100),`user_account`),
    KEY              `idx_appkey_agorauid` (`appkey`,`agora_uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
