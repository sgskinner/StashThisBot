CREATE DATABASE atbot;

USE atbot;

CREATE TABLE archive_result_t (
  result_id                BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  submission_url           TEXT            NOT NULL,
  parent_comment_author    TEXT            NOT NULL,
  parent_comment_id        VARCHAR(255)    NOT NULL,
  parent_comment_url       TEXT            NOT NULL,
  summoning_comment_author TEXT            NOT NULL,
  summoning_comment_id     VARCHAR(255)    NOT NULL,
  summoning_comment_url    TEXT            NOT NULL,
  request_date             DATETIME        NOT NULL,
  serviced_date            DATETIME        NOT NULL,
  PRIMARY KEY (result_id)
);
CREATE UNIQUE INDEX p_cmt_id_idx ON archive_result_t (parent_comment_id);


CREATE TABLE atbot_url_t (
  url_id        BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  result_id_fk  BIGINT UNSIGNED NOT NULL,
  archived_url  TEXT            NOT NULL,
  original_url  TEXT            NOT NULL,
  last_archived DATETIME        NOT NULL,
  PRIMARY KEY (url_id),
  FOREIGN KEY result_id_fk (result_id_fk) REFERENCES archive_result_t (result_id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);
CREATE INDEX rslt_id_idx ON atbot_url_t (result_id_fk);

CREATE USER atbot@localhost
  IDENTIFIED BY 'password';


GRANT SELECT, INSERT, UPDATE, DELETE, DROP, ALTER, CREATE TEMPORARY TABLES ON atbot.* TO atbot@localhost;

