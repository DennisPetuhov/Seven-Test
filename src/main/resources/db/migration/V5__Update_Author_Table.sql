ALTER TABLE author
    RENAME COLUMN date_of_birth TO creation_date;

ALTER TABLE author
    RENAME COLUMN first_name TO full_name;

ALTER TABLE author
    DROP COLUMN last_name;