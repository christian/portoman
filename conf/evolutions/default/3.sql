# --- !Ups

ALTER TABLE securities ADD COLUMN previous_close_price real;

# --- !Downs

ALTER TABLE securities DROP COLUMN previous_close_price;
