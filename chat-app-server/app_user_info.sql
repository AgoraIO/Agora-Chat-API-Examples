create database app_server character set utf8mb4;
	
CREATE TABLE `app_user_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_account` varchar(32) NOT NULL COMMENT '用户的账号',
  `user_password` varchar(32) DEFAULT NULL COMMENT '用户的密码',
  `chat_user_name` varchar(32) NOT NULL COMMENT '环信用户名',
  `agora_uid` varchar(20) NOT NULL COMMENT '声网用户id',
  `user_nickname` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`id`,`user_account`)
) ENGINE=InnoDB AUTO_INCREMENT=48 DEFAULT CHARSET=utf8mb4;
