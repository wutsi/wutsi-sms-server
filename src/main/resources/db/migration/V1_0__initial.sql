CREATE TABLE T_VERIFICATION(
    id            SERIAL NOT NULL,
    phone_number  VARCHAR(30) NOT NULL,
    code          VARCHAR(10) NOT NULL,
    language      VARCHAR(2) NOT NULL,
    status        INT NOT NULL DEFAULT 0,
    created       TIMESTAMPTZ NOT NULL DEFAULT now(),
    expires       TIMESTAMPTZ NOT NULL,
    verified      TIMESTAMPTZ,

    PRIMARY KEY (id)
);
