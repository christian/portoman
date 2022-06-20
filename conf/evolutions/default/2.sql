# --- !Ups

ALTER TABLE securities ADD COLUMN day_change_percent real;
ALTER TABLE securities ADD COLUMN day_change real;

# --- !Downs

ALTER TABLE securities DROP COLUMN day_change;
ALTER TABLE securities DROP COLUMN day_change_percent;
