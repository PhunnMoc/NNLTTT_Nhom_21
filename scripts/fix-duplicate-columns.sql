USE language_center;

ALTER TABLE teachers DROP COLUMN `fullName`;
ALTER TABLE students DROP COLUMN `fullName`;

ALTER TABLE user_accounts DROP COLUMN `password_hash`;
ALTER TABLE user_accounts DROP COLUMN `related_id`;
