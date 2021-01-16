create database `ginkgo_blog`;

use `ginkgo_blog`;

/* table structure for table `t_admin` */
drop table if exists `t_admin`;

create table `t_admin` (
    `id` char(32) comment '管理员id',
    `name` varchar(255) not null comment '管理员名',
    `password` char(60) not null comment '密码',
    `nickname` varchar(255) comment '昵称',
    `gender` tinyint(1) comment '性别，0:女，1:男',
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
    `role_id` char(32) comment '角色id',
    `login_count` int unsigned default 0 comment '登录次数',
    `last_login_time` datetime comment '最后登录时间',
    `last_login_ip` varchar(255) comment '最后登录ip',
    `is_delete` bool default 0 comment '是否删除，0:否，1:是',
    `create_time` datetime default now() comment '创建时间',
    `update_time` datetime default now() comment '更新时间',
    primary key (`id`)
) engine = InnoDB default charset = utf8 comment = '管理员表';


/* table structure for table `t_role` */
drop table if exists `t_role`;

create table `t_role` (
    `id` char(32) comment '角色id',
    `name` varchar(255) not null comment '角色名',
    `summary` varchar(255) comment '角色介绍',
    `category_menu_ids` text comment '角色管辖的菜单的id',
    `is_delete` bool default 0 comment '是否删除，0:否，1:是',
    `create_time` datetime default now() comment '创建时间',
    `update_time` datetime default now() comment '更新时间',
    primary key (`id`)
) engine = InnoDB default charset = utf8 comment = '角色表';


/* table structure for table `t_category_menu` */
drop table if exists `t_category_menu`;

create table `t_category_menu` (
    `id` char(32) comment '菜单id',
    `name` varchar(255) not null comment '菜单名称',
    `level` tinyint comment '菜单级别',
    `summary` varchar(255) comment '菜单简介',
    `parent_id` char(32) comment '菜单父id',
    `url` varchar(255) comment 'url地址',
    `icon` varchar(255) comment '图标',
    `order` int default 0 comment '排序字段，越大越靠前',
    `is_show` bool default 0 comment '是否显示，0:否，1:是',
    `is_delete` bool default 0 comment '是否删除，0:否，1:是',
    `create_time` datetime default now() comment '创建时间',
    `update_time` datetime default now() comment '更新时间',
    primary key (`id`)
) engine = InnoDB default charset = utf8 comment = '菜单类别表';


/* table structure for table `t_blog` */
drop table if exists `t_blog`;

create table t_blog (
    `id` char(32) comment '博客id',
    `title` varchar(255) comment '博客标题',
    `content` longtext not null comment '博客内容',
    `author` varchar(255) comment '博客作者',
    `summary` varchar(255) comment '博客简介',
    `click_count` int default 0 comment '博客点击数',
    `collect_count` int default 0 comment '博客收藏数',
    `level` tinyint default 0 comment '推荐等级，0:正常',
    `order` int default 0 comment '排序字段，越大越靠前',
    `tag_id` char(32) comment '博客标签id',
    `picture_id` char(32) comment '标题图片id',
    `admin_id` char(32) comment '管理员id',
    `blog_sort_id` char(32) comment '博客分类id',
    `is_publish` bool default 1 comment '是否发布，0:否，1:是',
    `is_original` bool default 1 comment '是否原创，0:否，1:是',
    `is_comment` bool default 1 comment '是否开启评论，0:否，1:是',
    `is_delete` bool default 0 comment '是否删除，0:否，1:是',
    `create_time` datetime default now() comment '创建时间',
    `update_time` datetime default now() comment '更新时间',
    primary key (`id`)
) engine = InnoDB default charset = utf8 comment = '博客表';


/* table structure for table `t_blog_sort` */
drop table if exists `t_blog_sort`;

create table t_blog_sort (
    `id` char(32) comment '博客分类id',
    `name` varchar(255) comment '博客分类名',
    `content` varchar(255) comment '分类简介',
    `click_count` int default 0 comment '分类点击数',
    `order` int default 0 comment '排序字段，越大越靠前',
    `is_delete` bool default 0 comment '是否删除，0:否，1:是',
    `create_time` datetime default now() comment '创建时间',
    `update_time` datetime default now() comment '更新时间',
    primary key (`id`)
) engine = InnoDB default charset = utf8 comment = '博客分类表';


/* table structure for table `t_tag` */
drop table if exists `t_tag`;

create table t_tag (
    `id` char(32) comment '博客标签id',
    `name` varchar(255) comment '标签名',
    `click_count` int default 0 comment '标签点击数',
    `order` int default 0 comment '排序字段，越大越靠前',
    `is_delete` bool default 0 comment '是否删除，0:否，1:是',
    `create_time` datetime default now() comment '创建时间',
    `update_time` datetime default now() comment '更新时间',
    primary key (`id`)
) engine = InnoDB default charset = utf8 comment = '博客标签表';


/* table structure for table `t_picture` */
drop table if exists `t_picture`;

create table t_picture (
    `id` char(32) comment '图片id',
    `name` varchar(255) comment '图片名',
    `picture_sort_id` char(32) comment '图片分类id',
    `is_delete` bool default 0 comment '是否删除，0:否，1:是',
    `create_time` datetime default now() comment '创建时间',
    `update_time` datetime default now() comment '更新时间',
    primary key (`id`)
) engine = InnoDB default charset = utf8 comment = '图片表';


/* table structure for table `t_picture_sort` */
drop table if exists `t_picture_sort`;

create table t_picture_sort (
    `id` char(32) comment '图片分类id',
    `name` varchar(255) comment '图片分类名',
    `order` int default 0 comment '排序字段，越大越靠前',
    `is_show` bool default 0 comment '是否显示，0:否，1:是',
    `is_delete` bool default 0 comment '是否删除，0:否，1:是',
    `create_time` datetime default now() comment '创建时间',
    `update_time` datetime default now() comment '更新时间',
    primary key (`id`)
) engine = InnoDB default charset = utf8 comment = '图片分类表';


/* table structure for table `t_collect` */
drop table if exists `t_collect`;

create table t_collect (
    `id` char(32) comment '收藏id',
    `user_id` char(32) not null comment '用户id',
    `blog_id` char(32) not null comment '博客id',
    `is_delete` bool default 0 comment '是否删除，0:否，1:是',
    `create_time` datetime default now() comment '创建时间',
    `update_time` datetime default now() comment '更新时间',
    primary key (`id`)
) engine = InnoDB default charset = utf8 comment = '收藏表';


/* table structure for table `t_user` */
drop table if exists `t_user`;

create table `t_user` (
    `id` char(32) comment '用户id',
    `name` varchar(255) not null comment '用户名',
    `password` char(60) not null comment '密码',
    `nickname` varchar(255) comment '昵称',
    `gender` tinyint(1) comment '性别，0:女，1:男',
    `birthday` date comment '出生日期',
    `avatar` varchar(255) comment '头像',
    `phone_number` char(11) comment '手机号',
    `qq` varchar(255) comment 'QQ',
    `wechat` varchar(255) comment '微信',
    `email` varchar(255) comment '邮箱',
    `valid_code` varchar(255) comment '验证码',
    `occupation` varchar(255) comment '职业',
    `summary` varchar(255) comment '自我介绍',
    `login_count` int unsigned default 0 comment '登录次数',
    `last_login_time` datetime comment '最后登录时间',
    `last_login_ip` varchar(255) comment '最后登录ip',
    `ip_source` varchar(255) comment 'ip来源',
    `browser` varchar(255) comment '浏览器',
    `os` varchar(255) comment '操作系统',
    `source` varchar(255) comment '账号来源',
    `platform_id` varchar(255) comment '平台id',
    `is_comment` bool default 1 comment '是否开启评论，0:禁言，1:正常',
    `is_email_notify` bool default 0 comment '是否开启邮件通知，0:否，1:是',
    `is_delete` bool default 0 comment '是否删除，0:否，1:是',
    `create_time` datetime default now() comment '创建时间',
    `update_time` datetime default now() comment '更新时间',
    primary key (`id`)
) engine = InnoDB default charset = utf8 comment = '用户表';


/* table structure for table `t_comment` */
drop table if exists `t_comment`;

create table t_comment (
    `id` char(32) comment '评论id',
    `user_id` char(32) comment '用户id',
    `first_comment_id` char(32) comment '一级评论id',
    `to_comment_id` char(32) comment '回复某条评论的id',
    `to_user_id` char(32) comment '回复某个人的id',
    `blog_id` char(32) comment '博客id',
    `content` varchar(1024) comment '评论内容',
    `source` varchar(255) comment '评论来源',
    `type` tinyint default 0 comment '评论类型，0:评论，1:点赞',
    `is_delete` bool default 0 comment '是否删除，0:否，1:是',
    `create_time` datetime default now() comment '创建时间',
    `update_time` datetime default now() comment '更新时间',
    primary key (`id`)
) engine = InnoDB default charset = utf8 comment = '评论表';


/* table structure for table `t_system_log` */
drop table if exists `t_system_log`;

create table t_system_log (
    `id` char(32) comment '系统日志id',
    `username` varchar(255) not null comment '用户名',
    `ip` varchar(255) comment '请求ip',
    `url` varchar(255) comment '请求url',
    `type` varchar(255) comment '请求方式',
    `path` varchar(255) comment '请求类路径',
    `method` varchar(255) comment '请求方法',
    `params` varchar(255) comment '请求参数',
    `description` varchar(255) comment '请求描述',
    `ip_source` varchar(255) comment 'ip来源',
    `duration` int unsigned default 0 comment '请求花费的时间',
    `is_delete` bool default 0 comment '是否删除，0:否，1:是',
    `create_time` datetime default now() comment '创建时间',
    `update_time` datetime default now() comment '更新时间',
    primary key (`id`)
) engine = InnoDB default charset = utf8 comment = '系统日志表';


/* table structure for table `t_exception_log` */
drop table if exists `t_exception_log`;

create table t_exception_log (
    `id` char(32) comment '异常日志id',
    `json` text comment '异常对象json格式',
    `message` text comment '异常信息',
    `ip` varchar(255) comment '请求ip',
    `method` varchar(255) comment '请求方法',
    `params` varchar(255) comment '请求参数',
    `description` varchar(255) comment '请求描述',
    `ip_source` varchar(255) comment 'ip来源',
    `is_delete` bool default 0 comment '是否删除，0:否，1:是',
    `create_time` datetime default now() comment '创建时间',
    `update_time` datetime default now() comment '更新时间',
    primary key (`id`)
) engine = InnoDB default charset = utf8 comment = '异常日志表';

