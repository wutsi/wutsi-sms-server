INSERT INTO T_VERIFICATION(id, phone_number, code, language, status, created, expires, verified)
    VALUES
        (100, '+23774511100', '000000', 'fr', 1, '2021-01-10', '2100-01-01', null),
        (101, '+23774511199', '111111', 'en', 1, '2010-01-10', '2100-01-01', null),
        (102, '+23774511199', '222222', 'en', 2, '2010-01-10', '2100-01-01', '2010-01-11'),
        (103, '+23774511199', '333333', 'en', 1, '2010-01-10', '2011-01-01', null)
;
