create table daily_prompts
(
    id          bigint auto_increment
        primary key,
    prompt_text text                                not null,
    date        date                                not null,
    posted_at   timestamp default CURRENT_TIMESTAMP null,
    constraint date
        unique (date)
);

create table prompts
(
    id             bigint auto_increment
        primary key,
    date_generated datetime(6)  null,
    text           varchar(255) null
);

create table user
(
    id              bigint auto_increment
        primary key,
    bio             varchar(255) null,
    created_at      varchar(255) null,
    password        varchar(255) not null,
    profile_picture varchar(255) null,
    username        varchar(255) not null,
    constraint UKsb8bbouer5wak8vyiiy4pf2bx
        unique (username)
);

create table user_followers
(
    user_id     bigint not null,
    follower_id bigint not null,
    constraint FK70tvmgyh1ha7f6unsw8s6jej8
        foreign key (follower_id) references user (id),
    constraint FKokc5w6fibhnthvwnxjxyrlfc1
        foreign key (user_id) references user (id)
);

create table users
(
    id              bigint auto_increment
        primary key,
    username        varchar(50)                         not null,
    password        varchar(255)                        not null,
    bio             text                                null,
    profile_picture varchar(255)                        null,
    created_at      timestamp default CURRENT_TIMESTAMP null,
    constraint username
        unique (username)
);

create table drawings
(
    id          bigint auto_increment
        primary key,
    user_id     bigint                              not null,
    image_url   varchar(255)                        null,
    description varchar(255)                        null,
    likes_count int       default 0                 null,
    created_at  timestamp default CURRENT_TIMESTAMP null,
    constraint drawings_ibfk_1
        foreign key (user_id) references users (id),
    constraint FKmj7kyv2o016j8usgcha41odx3
        foreign key (user_id) references user (id)
);

create table drawing_likes
(
    user_id    bigint not null,
    drawing_id bigint not null,
    primary key (user_id, drawing_id),
    constraint drawing_likes_ibfk_1
        foreign key (user_id) references users (id)
            on delete cascade,
    constraint drawing_likes_ibfk_2
        foreign key (drawing_id) references drawings (id)
            on delete cascade
);

create index drawing_id
    on drawing_likes (drawing_id);

create table followers
(
    follower_id  bigint not null,
    following_id bigint not null,
    primary key (follower_id, following_id),
    constraint followers_ibfk_1
        foreign key (follower_id) references users (id)
            on delete cascade,
    constraint followers_ibfk_2
        foreign key (following_id) references users (id)
            on delete cascade
);

create index following_id
    on followers (following_id);

create table user_settings
(
    id                    bigint auto_increment
        primary key,
    user_id               bigint                      not null,
    theme                 varchar(20) default 'light' null,
    notifications_enabled tinyint(1)  default 1       null,
    constraint user_settings_ibfk_1
        foreign key (user_id) references users (id)
);

create index user_id
    on user_settings (user_id);


