CREATE TABLE article
(
    id              INT PRIMARY KEY,
    feed_id         INT          NOT NULL,
    title           VARCHAR(255) NOT NULL,
    author          VARCHAR(255),
    html            LONGTEXT,
    url             VARCHAR(255) NOT NULL,
    is_saved        int          NOT NULL,
    is_read         int          NOT NULL,
    created_on_time BIGINT       NOT NULL
);
