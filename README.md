# Ginkgo Blog

## 1. 系统架构



## 2. 数据库设计

### 2.1 管理员表

|    字段名    |    表示代码     |   数据类型   |   约束条件   |
| :----------: | :-------------: | :----------: | :----------: |
|   唯一 id    |       id        |   char(32)   |     主键     |
|    用户名    |    username     | varchar(255) |     非空     |
|     密码     |    password     |   char(60)   |     非空     |
|     昵称     |    nickname     | varchar(255) |              |
|     性别     |     gender      |   char(1)    |              |
|   出生日期   |    birthday     |     date     |              |
|     头像     |     avatar      | varchar(255) |              |
|    手机号    |  phone_number   |   char(11)   |              |
|      QQ      |       qq        | varchar(255) |              |
|     微信     |     wechat      | varchar(255) |              |
|    GitHub    |     github      | varchar(255) |              |
|    Gitee     |      gitee      | varchar(255) |              |
|     邮箱     |      email      | varchar(255) |              |
|    验证码    |   valid_code    | varchar(255) |              |
|     职业     |   occupation    | varchar(255) |              |
|   自我介绍   |     summary     | varchar(255) |              |
|     履历     |     resume      |     text     |              |
|   角色 id    |     role_id     |   char(32)   |              |
|   登录次数   |   login_count   | int unsigned |    默认 0    |
| 最后登录时间 | last_login_time |   datetime   |              |
| 最后登录 IP  |  last_login_ip  | varchar(255) |              |
|   是否删除   |     deleted     |     bool     |    默认 0    |
|   创建时间   |   create_time   |   datetime   | 默认当前时间 |
|   更新时间   |   update_time   |   datetime   | 默认当前时间 |

### 2.2 用户表





### 2.3 博客表

