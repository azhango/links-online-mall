-- 创建数据库
create database if not exists mumumall;

-- 进入数据库
use mumumall;

-- 用户表
create table if not exists user
(
    id
    bigint
    auto_increment
    comment
    '用户主键id'
    primary
    key,
    user_account
    varchar
(
    256
) not null comment '用户账号',
    user_password varchar
(
    256
) not null comment '用户密码，MD5加密',
    user_avatar varchar
(
    1024
) null comment '用户头像',
    nickname varchar
(
    256
) null comment '用户昵称',
    gender tinyint null comment '性别',
    personalized_signature varchar
(
    1024
) null comment '个性签名',
    email_address varchar
(
    256
) null comment '邮件地址',
    user_role varchar
(
    256
) default 'user' not null comment '角色，user，admin',
    create_time datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete tinyint default 0 not null comment '是否删除 默认0不删除 1-删除'
    ) comment '用户表';
INSERT INTO `user` (`user_account`, `user_password`, `user_avatar`, `user_role`)
VALUES ('admin', 'e9cd4dbc2aed67b001142aded0b3c283', 'https://sdn.geekzu.org/avatar/', 'admin');

-- 目录表
create table if not exists category
(
    id
    bigint
    auto_increment
    comment
    '分类Id'
    primary
    key,
    category_name
    varchar
(
    256
) not null comment '目录名称',
    type int null comment '分类目录级别',
    parent_id int not null comment '父目录id',
    order_num int null comment '展示目录时的排序',
    create_time datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
    ) comment '目录表';

-- 商品表
create table product
(
    id             bigint auto_increment comment '商品主键id'
        primary key,
    product_name   varchar(100)                       not null comment '商品名称',
    image          varchar(500)                       not null comment '产品图片,相对路径地址',
    detail         varchar(500)                       null comment '商品详情',
    category_id    bigint                             not null comment '分类id',
    price          int                                not null comment '价格,单位-分',
    stock          int                                not null comment '库存数量',
    product_status tinyint  default 1                 not null comment '商品上架状态：0-下架，1-上架',
    create_time    datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time    datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
) comment '商品表';

-- 购物车
create table cart
(
    id          bigint auto_increment comment '购物车id',
    product_id  bigint                             not null comment '商品id',
    user_id     bigint                             not null comment '用户id',
    quantity    int                                null comment '商品数量',
    selected    tinyint                            null comment '是否已勾选 0-未勾选 1-已勾选',
    create_time datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint table_name_pk
        primary key (id)
) comment '购物车';

-- 订单表
create table mall_order
(
    id               bigint auto_increment comment '主键id',
    order_no         varchar(128)                       not null comment '订单号(非主键id)',
    user_id          bigint                             not null comment '用户id',
    total_price      int null comment '订单总价格',
    receiver_name    varchar(32)                        not null comment '收货人姓名快照',
    receiver_mobile  varchar(32)                        not null comment '收货人手机号快照',
    receiver_address varchar(128)                       not null comment '收货人地址快照',
    order_status     int      default 10                not null comment '订单状态:0-用户已取消/10-未付款(默认)/20-已付款/30-已发货/40交易完成',
    postage          int      default 0 null comment '运费 默认为0',
    payment_type     tinyint                            null comment '支付类型 1-在线支付',
    delivery_time    datetime                           null comment '发货时间',
    pay_time         datetime                           null comment '支付时间',
    end_time         datetime                           null comment '交易完成时间',
    create_time      datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time      datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete        tinyint  default 0                 not null comment '是否删除 默认0不删除 1-删除',
    constraint order_pk
        primary key (id)
) comment '订单表';

-- 多订单项目表
create table order_item
(
    id           bigint auto_increment comment '主键id',
    order_no     varchar(128)                       not null comment '归属订单id',
    product_id   bigint                             not null comment '商品id',
    product_name varchar(128)                       not null comment '商品名称',
    product_img  varchar(1024)                      null comment '商品图片',
    unit_price   int      default 0                 null comment '单价(下单时的快照)默认0',
    quantity     int      default 1                 null comment '商品数量',
    total_price  int      default 0                 null comment '商品总价',
    create_time  datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time  datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint order_item_pk
        primary key (id)
) comment '多订单项目表';
