CREATE DATABASE atbot;

USE atbot;

CREATE TABLE archive_result_t (
  id                       BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  submission_url           TEXT            NOT NULL,
  parent_comment_author    TEXT            NOT NULL,
  parent_comment_id        VARCHAR(255)    NOT NULL,
  parent_comment_url       TEXT            NOT NULL,
  summoning_comment_author TEXT            NOT NULL,
  summoning_comment_id     VARCHAR(255)    NOT NULL,
  summoning_comment_url    TEXT            NOT NULL,
  request_date             DATETIME        NOT NULL,
  serviced_date            DATETIME        NOT NULL,
  PRIMARY KEY (id)
);
CREATE UNIQUE INDEX p_cmt_id_idx ON archive_result_t (parent_comment_id);


CREATE TABLE atbot_url_t (
  id                 BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  archive_result_id  BIGINT UNSIGNED NOT NULL,
  archived_url       TEXT,
  original_url       TEXT            NOT NULL,
  last_archived      DATETIME,
  PRIMARY KEY (id),
  FOREIGN KEY result_id_fk (archive_result_id) REFERENCES archive_result_t (id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);
CREATE INDEX rslt_id_idx ON atbot_url_t (archive_result_id);


CREATE TABLE blacklisted_user_t (
  id           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  username     VARCHAR(255)    NOT NULL,
  date_created DATETIME        NOT NULL,
  reason       TEXT            NOT NULL,
  PRIMARY KEY (id)
);
CREATE INDEX username ON blacklisted_user_t (username);


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


CREATE USER atbot@localhost
  IDENTIFIED BY 'password';


GRANT SELECT, INSERT, UPDATE, DELETE, DROP, ALTER, CREATE TEMPORARY TABLES ON atbot.* TO atbot@localhost;

