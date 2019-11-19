-- Clean database

DROP TABLE IF EXISTS CLICK;
DROP TABLE IF EXISTS SHORTURL;

CREATE TABLE SHORTURL
(
    HASH    VARCHAR(30) PRIMARY KEY,                    -- Key
    TARGET  VARCHAR(1024),                              -- Original URL
    CREATED TIMESTAMP default current_timestamp,        -- Creation date
    SAFE    BOOLEAN,                                    -- Safe target
    IP      VARCHAR(20),                                -- IP
    CODE    VARCHAR(50)                                 -- Access code
);

-- Click

CREATE TABLE CLICK
(
    ID       BIGSERIAL NOT NULL,
    HASH     VARCHAR(10) NOT NULL,
    CREATED  TIMESTAMP default current_timestamp,
    BROWSER  VARCHAR(50),
    PLATFORM VARCHAR(50),
    IP       VARCHAR(20),
    COUNTRY  VARCHAR(50),
    GC       VARCHAR(2),
    FOREIGN KEY (HASH) REFERENCES SHORTURL (HASH)
)
