# --- !Ups

ALTER TABLE securities ADD COLUMN day_change_percent DECIMAL(10, 2);
ALTER TABLE securities ADD COLUMN day_change DECIMAL(10, 2);

# --- !Downs

ALTER TABLE securities DROP COLUMN day_change;
ALTER TABLE securities DROP COLUMN day_change_percent;
