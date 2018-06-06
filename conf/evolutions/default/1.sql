# Users schema

# --- !Ups

CREATE TABLE securities (
    id bigint(20) NOT NULL AUTO_INCREMENT,
    name varchar(255) NOT NULL,
    ticker varchar(10) NOT NULL,
    close_price decimal(10, 2),
    PRIMARY KEY (id)
);

CREATE TABLE positions (
    id bigint(20) NOT NULL AUTO_INCREMENT,
    status varchar(20) NOT NULL,
    security_id bigint(20) NOT NULL,
    total_units bigint(20) NOT NULL,
    total_cost decimal(15, 2),
    PRIMARY KEY (id),
    FOREIGN KEY(security_id) REFERENCES securities(id)
);

CREATE TABLE transactions (
    id bigint(20) NOT NULL AUTO_INCREMENT,
    uuid char(36) NOT NULL,
    type varchar(10) NOT NULL,
    security_id bigint(20) NOT NULL,
    units bigint(20) NOT NULL,
    price decimal(15, 2) NOT NULL,
    created_at timestamp,
    position_id bigint(11),
    commission decimal(15, 2),
    notes varchar(255),
    PRIMARY KEY (id),
    FOREIGN KEY(position_id) REFERENCES positions(id),
    FOREIGN KEY(security_id) REFERENCES securities(id)
);

# --- !Downs

SET FOREIGN_KEY_CHECKS=0;
DROP TABLE transactions;
DROP TABLE positions;
DROP TABLE securities;
SET FOREIGN_KEY_CHECKS=1;
