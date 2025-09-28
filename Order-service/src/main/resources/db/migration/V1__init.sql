CREATE table t_orders
(
    id           bigint(200) not null AUTO_INCREMENT,
    order_number varchar(255) default null,
    sku_code     varchar(255),
    price        decimal(19, 2),
    quantity     int(11),
    PRIMARY KEY (id)
);