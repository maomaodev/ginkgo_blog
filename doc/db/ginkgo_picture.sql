
CREATE DATABASE `ginkgo_picture`;

USE `ginkgo_picture`;

/*Table structure for table `t_file` */

DROP TABLE IF EXISTS `t_file`;

CREATE TABLE `t_file` (
  `uid` char(32) NOT NULL COMMENT '唯一uid',
  `file_old_name` varchar(255) DEFAULT NULL COMMENT '旧文件名',
  `pic_name` varchar(255) DEFAULT NULL COMMENT '文件名',
  `pic_url` varchar(255) DEFAULT NULL COMMENT '文件地址',
  `pic_expanded_name` varchar(255) DEFAULT NULL COMMENT '文件扩展名',
  `file_size` int DEFAULT '0' COMMENT '文件大小',
  `file_sort_uid` char(32) DEFAULT NULL COMMENT '文件分类uid',
  `admin_uid` char(32) DEFAULT NULL COMMENT '管理员uid',
  `user_uid` char(32) DEFAULT NULL COMMENT '用户uid',
  `status` tinyint(1) unsigned NOT NULL DEFAULT '1' COMMENT '状态',
  `create_time` datetime NOT NULL DEFAULT now() COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT now() COMMENT '更新时间',
  `qi_niu_url` varchar(255) DEFAULT NULL COMMENT '七牛云地址',
  PRIMARY KEY (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='文件表';


/*Table structure for table `t_file_sort` */

DROP TABLE IF EXISTS `t_file_sort`;

CREATE TABLE `t_file_sort` (
  `uid` char(32) NOT NULL COMMENT '唯一uid',
  `project_name` varchar(255) DEFAULT NULL COMMENT '项目名',
  `sort_name` varchar(255) DEFAULT NULL COMMENT '分类名',
  `url` varchar(255) DEFAULT NULL COMMENT '分类路径',
  `status` tinyint(1) unsigned NOT NULL DEFAULT '1' COMMENT '状态',
  `create_time` datetime NOT NULL DEFAULT now() COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT now() COMMENT '更新时间',
  PRIMARY KEY (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='文件分类表';

