# Users schema

# --- !Ups

CREATE TABLE securities (
    id integer primary key AUTOINCREMENT,
    name char(255) NOT NULL,
    ticker char(10) NOT NULL,
    close_price real,
    updated_at timestamp
);

CREATE TABLE positions (
    id integer primary key AUTOINCREMENT,
    status char(20) NOT NULL,
    security_id integer NOT NULL,
    total_units integer NOT NULL,
    total_cost real,
    FOREIGN KEY(security_id) REFERENCES securities(id)
);

CREATE TABLE transactions (
    id integer primary key AUTOINCREMENT,
    uuid char(36) NOT NULL,
    type char(10) NOT NULL,
    security_id integer NOT NULL,
    units integer NOT NULL,
    price real NOT NULL,
    created_at timestamp,
    position_id integer,
    commission real,
    notes char(255),
    FOREIGN KEY(position_id) REFERENCES positions(id),
    FOREIGN KEY(security_id) REFERENCES securities(id)
);

# --- !Downs

DROP TABLE transactions;
DROP TABLE positions;
DROP TABLE securities;
