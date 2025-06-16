-- =========================================
-- DokodaPepper データベーススキーマ
-- =========================================

-- ユーザーテーブル
create table users (
  id bigint primary key generated always as identity,
  username text not null,
  password text not null,
  created_at timestamp default current_timestamp,
  updated_at timestamp default current_timestamp
);

-- 都道府県テーブル
create table prefectures (
  id bigint primary key generated always as identity,
  name text unique not null
);

-- 市区町村テーブル
create table cities (
  id bigint primary key generated always as identity,
  name text not null,
  prefecture_id bigint not null,
  unique (name, prefecture_id),
  constraint fk_cities_prefectureid foreign key (prefecture_id) references prefectures(id) on delete cascade
);

-- 投稿テーブル
create table posts (
  id bigint primary key generated always as identity,
  user_id bigint not null,
  city_id bigint,
  prefecture_id bigint,
  content text,
  latitude double precision,
  longitude double precision,
  address text,
  created_at timestamp default current_timestamp,
  updated_at timestamp default current_timestamp,
  constraint fk_posts_userid foreign key (user_id) references users(id) on delete cascade,
  constraint fk_posts_cityid foreign key (city_id) references cities(id) on delete set null,
  constraint fk_posts_prefectureid foreign key (prefecture_id) references prefectures(id) on delete set null
);

-- 「見つけた！」中間テーブル
create table found_it (
  user_id bigint not null,
  post_id bigint not null,
  found_at timestamp default current_timestamp,
  primary key (user_id, post_id),
  constraint fk_foundit_user foreign key (user_id) references users(id) on delete cascade,
  constraint fk_foundit_post foreign key (post_id) references posts(id) on delete cascade
);

-- 投稿画像テーブル
create table post_images (
  id bigint primary key generated always as identity,
  post_id bigint not null,
  image_url text not null,
  sort_order int default 0,
  created_at timestamp default current_timestamp,
  updated_at timestamp default current_timestamp,
  constraint fk_postimages_postid foreign key (post_id) references posts(id) on delete cascade
);

-- コメントテーブル
create table comments (
  id bigint primary key generated always as identity,
  post_id bigint not null,
  user_id bigint not null,
  content text not null,
  created_at timestamp default current_timestamp,
  updated_at timestamp default current_timestamp,
  constraint fk_comments_postid foreign key (post_id) references posts(id) on delete cascade,
  constraint fk_comments_userid foreign key (user_id) references users(id) on delete cascade
);

-- 通報テーブル
create table reports (
  user_id bigint not null,
  post_id bigint not null,
  reported_at timestamp default current_timestamp,
  primary key (user_id, post_id),
  constraint fk_reports_user foreign key (user_id) references users(id) on delete cascade,
  constraint fk_reports_post foreign key (post_id) references posts(id) on delete cascade
);

-- ============================
-- インデックス（検索高速化）
-- ============================

-- posts 関連
create index idx_posts_userid on posts(user_id);
create index idx_posts_cityid on posts(city_id);
create index idx_posts_prefectureid on posts(prefecture_id);

-- found_it 関連
create index idx_foundit_userid on found_it(user_id);
create index idx_foundit_postid on found_it(post_id);

-- post_images
create index idx_postimages_postid on post_images(post_id);

-- comments
create index idx_comments_postid on comments(post_id);
create index idx_comments_userid on comments(user_id);

-- reports
create index idx_reports_userid on reports(user_id);
create index idx_reports_postid on reports(post_id);

-- ============================
-- 既存データの prefecture_id 補完
-- ============================

update posts
set prefecture_id = cities.prefecture_id
from cities
where posts.city_id = cities.id;
