USE language_center;

INSERT INTO user_accounts (`username`, `password_hash`, `passwordHash`, `role`, `status`, `failedLoginCount`)
SELECT 'superadmin', TO_BASE64(UNHEX(SHA2('admin123', 256))), TO_BASE64(UNHEX(SHA2('admin123', 256))), 'SUPER_ADMIN', 'ACTIVE', 0
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM user_accounts WHERE `username` = 'superadmin');
