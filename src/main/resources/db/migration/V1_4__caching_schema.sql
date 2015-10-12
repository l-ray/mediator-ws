DROP TABLE IF EXISTS cache;

CREATE TABLE cache (
  hashCode   INT  NOT NULL,
  value      bytea,
  PRIMARY KEY (hashCode)
);