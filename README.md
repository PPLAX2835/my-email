# my-email

## 邮箱系统，公司面试作业 目标效果是类似qq邮箱那种，使用一个账号，这个账号下可以管理多个自己邮箱

‍

‍

## 技术栈

​`Spring Boot`​ `My Batis`​ `Redis`​ `MySQL`​ `Docker`​

​`Vue`​ `Element-ui`​

‍

## 环境

Java 8

MySQL 8.0.33

Redis 7.0.5

npm 6.14.16

‍

### 展示

​![image](assets/image-20231103205443-4anq3ca.png)​

​![image](assets/image-20231103205505-afb3ki2.png)​

​![image](assets/image-20231103205525-l5qv2no.png)​

​![image](assets/image-20231103205543-n5b2krt.png)​

​![image](assets/image-20231103205556-s8dp1xn.png)​

​![image](assets/image-20231103205616-9albzos.png)​

‍

‍

## Docker

### redis

```yaml
version: '3'
services:
  redis:
    image: registry.cn-hangzhou.aliyuncs.com/zhengqing/redis:7.0.5                    # 镜像'redis:7.0.5'
    container_name: redis                                                             # 容器名为'redis'
    restart: unless-stopped                                                                   # 指定容器退出后的重启策略为始终重启，但是不考虑在Docker守护进程启动时就已经停止了的容器
    command: redis-server /etc/redis/redis.conf --requirepass 123456 --appendonly no # 启动redis服务并添加密码为：123456,默认不开启redis-aof方式持久化配置
#    command: redis-server --requirepass 123456 --appendonly yes # 启动redis服务并添加密码为：123456,并开启redis持久化配置
    environment:                        # 设置环境变量,相当于docker run命令中的-e
      TZ: Asia/Shanghai
      LANG: en_US.UTF-8
    volumes:                            # 数据卷挂载路径设置,将本机目录映射到容器目录
      - "./redis/data:/data"
      - "./redis/config/redis.conf:/etc/redis/redis.conf"  # `redis.conf`文件内容`http://download.redis.io/redis-stable/redis.conf`
    ports:                              # 映射端口
      - "6379:6379"
```

MySQL

```yaml
version: '3'
services:
  mysql:
    image: registry.cn-hangzhou.aliyuncs.com/zhengqing/mysql:8.0  # 原镜像`mysql:8.0`
    container_name: mysql8                                    # 容器名为'mysql8'
    restart: unless-stopped                                               # 指定容器退出后的重启策略为始终重启，但是不考虑在Docker守护进程启动时就已经停止了的容器
    volumes:                                                      # 数据卷挂载路径设置,将本机目录映射到容器目录
      - "./mysql/my.cnf:/etc/mysql/my.cnf"
      - "./mysql/data:/var/lib/mysql"
#      - "./mysql/conf.d:/etc/mysql/conf.d"
      - "./mysql/mysql-files:/var/lib/mysql-files"
    environment:                        # 设置环境变量,相当于docker run命令中的-e
      TZ: Asia/Shanghai
      LANG: en_US.UTF-8
      MYSQL_ROOT_PASSWORD: root         # 设置root用户密码
      MYSQL_DATABASE: demo              # 初始化的数据库名称
    privileged: true
    user: root
    ports:                              # 映射端口
      - "3308:3306"
```

‍

### 数据库准备

```sql
drop database if exists myemail_db;

create database if not exists myemail_db;

use myemail_db;

create table `user` (
                        `uid` bigint auto_increment comment '主键',
                        `username` varchar(30) not null unique comment '用户登录名',
                        `password` varchar(30) not null comment '用户登录密码',
                        `phone` varchar(11) comment '用户电话',
                        `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '创建时间',
                        primary key (`uid`)
);

insert into user (`username`, `password`) values ('PPLAX', '123456');

create table `email` (
                         `email_id` bigint primary key auto_increment comment '主键',
                         `uid` bigint not null comment '外键，user',
                         `email_address` varchar(50) not null comment '邮箱地址',
                         `email_password` varchar(50) not null comment '邮箱密码或授权码',
                         `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '创建时间'
);

create table `email_log` (
                             `log_id` bigint primary key auto_increment comment '主键',
                             `uid` bigint comment '外键，user',
                             `type` enum('send', 'receive') default 'send' comment '类型',
                             `sender_email` varchar(50) not null comment '发件者邮箱',
                             `receiver_email` varchar(50) not null comment '收件人邮箱',
                             `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '创建时间'
);

create table `menu` (
                        `menu_id` integer primary key auto_increment comment '主键',
                        `icon` varchar(30) default '' comment '图标',
                        `name` varchar(30) default '' comment '选项名',
                        `url` varchar(30) comment '跳转地址',
                        `parent_menu_id` integer comment '父级菜单id'
);

insert into `menu` (menu_id, icon, name, url, parent_menu_id) values (1, '', '收件', '', null), (2, '', '发件', '', null), (3, '', '收件箱', 'receive/emails', 1), (4, '', '垃圾邮箱', 'receive/spam', 1), (5, '', '已发送', 'send/sent', 2), (6, '', '写邮件', 'send/write', 2);


```

### 前端项目

cd到`vueproject`​目录下

```bash
npm install
```

```bash
npm run dev
```
