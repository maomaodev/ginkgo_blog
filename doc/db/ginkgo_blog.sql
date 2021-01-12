create database `ginkgo_blog`;

use `ginkgo_blog`;

drop table if exists `t_admin`;

create table `t_admin` (
    `id` char(32) comment '唯一id',
    `username` varchar(2) not null comment '用户名',
    `password` char(60) not null comment '密码',
    `nickname` varchar(255) comment '昵称',
    `gender` char(1) comment '性别，0表示女，1表示男',
    `birthday` date comment '出生日期',
    `avatar` varchar(255) comment '头像',
    `phone_number` char(11) comment '手机号',
    `qq` varchar(255) comment 'QQ',
    `wechat` varchar(255) comment '微信',
    `github` varchar(255) comment 'GitHub',
    `gitee` varchar(255) comment 'Gitee',
    `email` varchar(255) comment '邮箱',
    `valid_code` varchar(255) comment '验证码',
    `occupation` varchar(255) comment '职业',
    `summary` varchar(255) comment '自我介绍',
    `resume` text comment '履历',
    `role_id` char(32) comment '角色 id',
    `login_count` int unsigned default 0 comment '登录次数',
    `last_login_time` datetime comment '最后登录时间',
    `last_login_ip` varchar(255) comment '最后登录 IP',
    `deleted` bool default 0 comment '是否删除，0表示未删除，1表示删除',
    `create_time` datetime default now() comment '创建时间',
    `update_time` datetime default now() comment '更新时间',
    primary key (`id`)
) engine = InnoDB default charset = utf8 comment = '管理员表';