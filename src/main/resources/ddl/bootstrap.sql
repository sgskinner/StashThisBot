CREATE DATABASE stashbot;

USE stashbot;

CREATE TABLE stash_result_t (
  id                       BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  submission_url           TEXT            NOT NULL,
  summoning_comment_author TEXT            NOT NULL,
  summoning_comment_id     VARCHAR(128)    NOT NULL,
  summoning_comment_url    TEXT            NOT NULL,
  target_postable_author   TEXT            NOT NULL,
  target_postable_id       VARCHAR(128)    NOT NULL,
  target_postable_url      TEXT            NOT NULL,
  request_date             DATETIME        NOT NULL,
  serviced_date            DATETIME        NOT NULL,
  PRIMARY KEY (id)
);
CREATE UNIQUE INDEX target_id_idx ON stash_result_t (target_postable_id);


CREATE TABLE stash_url_t (
  id                 BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  stash_result_id    BIGINT UNSIGNED NOT NULL,
  stashed_url        TEXT,
  original_url       TEXT            NOT NULL,
  last_stashed       DATETIME,
  PRIMARY KEY (id),
  FOREIGN KEY result_id_fk (stash_result_id) REFERENCES stash_result_t (id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);
CREATE INDEX rslt_id_idx ON stash_url_t (stash_result_id);


CREATE TABLE blacklisted_user_t (
  id           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  username     VARCHAR(128)    NOT NULL,
  date_created DATETIME        NOT NULL,
  reason       TEXT            NOT NULL,
  PRIMARY KEY (id)
);
CREATE INDEX username ON blacklisted_user_t (username);


CREATE TABLE blacklisted_subreddit_t (
  id           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  name         VARCHAR(128)    NOT NULL,
  date_created DATETIME        NOT NULL,
  note         TEXT            NOT NULL,
  PRIMARY KEY (id)
);
CREATE INDEX sub_name_idx ON blacklisted_subreddit_t (name);


CREATE TABLE reddit_polling_time_t (
  id           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  date         DATETIME        NOT NULL,
  PRIMARY KEY (id)
);


CREATE TABLE auth_polling_time_t (
  id           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  date         DATETIME        NOT NULL,
  success      BOOLEAN         NOT NULL,
  PRIMARY KEY (id)
);


CREATE TABLE scraped_url_t (
  id           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  date         DATETIME        NOT NULL,
  url          TEXT            NOT NULL,
  PRIMARY KEY (id)
);


CREATE USER stashbot@localhost
IDENTIFIED BY 'password';

GRANT SELECT, INSERT, UPDATE, DELETE, DROP, ALTER, CREATE TEMPORARY TABLES ON stashbot.* TO stashbot@localhost;
grant all privileges on `stashbot`.* to stashbot@localhost;

-- Newer installs of mysql will use unix auth, which we don't want
USE mysql;
UPDATE user SET plugin ='mysql_native_password' WHERE User = 'stashbot';

select * from user where user = 'stashbot';

DROP DATABASE IF EXISTS stashbot;

CREATE DATABASE stashbot;
grant all privileges on `stashbot`.* to stashbot@localhost;