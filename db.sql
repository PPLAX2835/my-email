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

