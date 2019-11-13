-- Clean database

DROP TABLE CLICK IF EXISTS;
DROP TABLE SHORTURL IF EXISTS;

-- ShortURL

CREATE TABLE SHORTURL
(
    HASH    VARCHAR(30) PRIMARY KEY, -- Key
    TARGET  VARCHAR(1024),           -- Original URL
    CREATED TIMESTAMP,               -- Creation date
    SAFE    BOOLEAN,                 -- Safe target
    IP      VARCHAR(20),             -- IP
    CODE VARCHAR(50)              -- Country
);

-- Click

CREATE TABLE CLICK
(
    ID       BIGINT IDENTITY,                                             -- KEY
    HASH     VARCHAR(10) NOT NULL FOREIGN KEY REFERENCES SHORTURL (HASH), -- Foreing key
    CREATED  TIMESTAMP,                                                   -- Creation date
    BROWSER  VARCHAR(50),                                                 -- Browser
    PLATFORM VARCHAR(50),                                                 -- Platform
    IP       VARCHAR(20),                                                 -- IP
    COUNTRY  VARCHAR(50),                                                  -- Country
    GC       VARCHAR(2)
)