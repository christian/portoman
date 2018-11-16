# --- !Ups

ALTER TABLE securities ADD COLUMN previous_close_price DECIMAL(10, 2);

# --- !Downs

ALTER TABLE securities DROP COLUMN previous_close_price;
