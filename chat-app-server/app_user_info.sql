create database app_server character set utf8mb4;
	
CREATE TABLE `app_user_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_account` varchar(32) NOT NULL COMMENT 'user account',
  `user_password` varchar(32) DEFAULT NULL COMMENT 'user password',
  `agora_chat_user_name` varchar(32) NOT NULL COMMENT 'Agora Chat user name',
  `agora_chat_user_uuid` varchar(36) DEFAULT NULL COMMENT 'Agora Chat user uuid',
  PRIMARY KEY (`id`,`user_account`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
