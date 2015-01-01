DROP TABLE IF EXISTS pattern;

CREATE TABLE pattern (
  id         SERIAL,
  type       VARCHAR DEFAULT 'webharvest',
  name       VARCHAR DEFAULT '',
  url        VARCHAR DEFAULT '',
  starturl   VARCHAR DEFAULT '',
  icon       VARCHAR DEFAULT '',
  pattern    TEXT NOT NULL,
  subpattern TEXT DEFAULT NULL,
  dateformat VARCHAR DEFAULT 'dd.mm.yy',
  deleted    INT  NOT NULL  DEFAULT '0',
  hidden     INT  NOT NULL  DEFAULT '0',
  countrycode VARCHAR NOT NULL DEFAULT 'DE_de',
  PRIMARY KEY (id)
);

DROP TABLE IF EXISTS term_weight;
CREATE TABLE term_weight (
  term varchar DEFAULT NULL,
  weight float NOT NULL DEFAULT '0',
  deleted int DEFAULT '0',
  PRIMARY KEY (term)
);

DROP TABLE IF EXISTS categories;
CREATE TABLE categories (
  id serial,
  term varchar DEFAULT NULL,
  category varchar DEFAULT NULL,
  hidden int NOT NULL DEFAULT '0',
  deleted int DEFAULT '0',
  PRIMARY KEY (id)
);

DROP TABLE IF EXISTS user_rules;
CREATE TABLE user_rules (
  id SERIAL,
  rule_type int DEFAULT NULL,
  rule_input varchar DEFAULT NULL,
  priority_change int DEFAULT '0',
  PRIMARY KEY (id)
);