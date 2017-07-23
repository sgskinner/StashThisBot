USE atbot;

INSERT INTO archive_result_t
(result_id,
 submission_url,
 parent_comment_author,
 parent_comment_id,
 parent_comment_url,
 summoning_comment_author,
 summoning_comment_id,
 summoning_comment_url,
 request_date,
 serviced_date)
VALUES
  (1,
   'https://www.reddit.com/r/ArchiveThisBotSandbox/comments/6nin60/seed_post_0_test_test_test/',
   'sgskinner',
   'dk9pnws',
   'https://www.reddit.com/r/ArchiveThisBotSandbox/comments/6nin60/seed_post_0_test_test_test/dk9pnws/',
   'sgskinner',
   'dk9po93',
   'https://www.reddit.com/r/ArchiveThisBotSandbox/comments/6nin60/seed_post_0_test_test_test/dk9po93/',
    NOW(),
    NOW());

INSERT INTO archive_result_t
(result_id,
 submission_url,
 parent_comment_author,
 parent_comment_id,
 parent_comment_url,
 summoning_comment_author,
 summoning_comment_id,
 summoning_comment_url,
 request_date,
 serviced_date)
VALUES
  (2,
   'https://www.reddit.com/r/ArchiveThisBotSandbox/comments/6nin60/blahblahblah/',
   'sgskinner',
   'sdfjhklsad',
   'https://www.reddit.com/r/ArchiveThisBotSandbox/comments/6nin60/seed_post_0_test_test_test/dk9pnws/',
   'sgskinner',
   'opwieruo',
   'https://www.reddit.com/r/ArchiveThisBotSandbox/comments/6nin60/foofoofoo/dk9po93/',
   NOW(),
   NOW());


insert into atbot_url_t
(result_id,
 archived_url,
 original_url,
 last_archived)
  VALUES
    (1,
    'https://web.archive.org/web/20170717041419/https://www.usatoday.com/story/news/nation-now/2017/07/15/southern-california-fire-evacuates-thousands/481873001/',
    'https://www.usatoday.com/story/news/nation-now/2017/07/15/southern-california-fire-evacuates-thousands/481873001/',
    '2017-06-24 21:15:10');

insert into atbot_url_t
(result_id,
 archived_url,
 original_url,
 last_archived)
VALUES
  (1,
   'SECONDhttps://web.archive.org/web/20170717041419/https://www.usatoday.com/story/news/nation-now/2017/07/15/southern-california-fire-evacuates-thousands/481873001/',
   'SECONDhttps://www.usatoday.com/story/news/nation-now/2017/07/15/southern-california-fire-evacuates-thousands/481873001/',
   '2017-06-24 21:15:10');

insert into atbot_url_t
(result_id,
 archived_url,
 original_url,
 last_archived)
VALUES
  (2,
   'THIRDhttps://web.archive.org/web/20170717041419/https://www.usatoday.com/story/news/nation-now/2017/07/15/southern-california-fire-evacuates-thousands/481873001/',
   'THIRDhttps://www.usatoday.com/story/news/nation-now/2017/07/15/southern-california-fire-evacuates-thousands/481873001/',
   '2017-06-24 21:15:10');
