-- Clean database

DROP TABLE IF EXISTS CLICK;
DROP TABLE IF EXISTS SHORTURL;

-- ShortURL

CREATE TABLE SHORTURL
(
    HASH    VARCHAR(30) PRIMARY KEY, -- Key
    TARGET  VARCHAR(1024),           -- Original URL
    SPONSOR VARCHAR(1024),           -- Sponsor URL
    CREATED TIMESTAMP,               -- Creation date
    OWNER   VARCHAR(255),            -- User id
    MODE    INTEGER,                 -- Redirect mode
    SAFE    BOOLEAN,                 -- Safe target
    IP      VARCHAR(20),             -- IP
    COUNTRY VARCHAR(50)              -- Country
);

-- Click

CREATE TABLE CLICK
(
    ID       BIGINT GENERATED ALWAYS AS IDENTITY,
    HASH     VARCHAR(10) NOT NULL,
    CREATED  TIMESTAMP,
    REFERRER VARCHAR(1024),
    BROWSER  VARCHAR(50),
    PLATFORM VARCHAR(50),
    IP       VARCHAR(20),
    COUNTRY  VARCHAR(50),
    FOREIGN KEY (HASH) REFERENCES SHORTURL (HASH)
)
