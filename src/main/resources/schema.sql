CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT GENERATED BY DEFAULT AS IDENTITY,
    email VARCHAR(255) NOT NULL,
    name  VARCHAR(255) NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS items
(
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY,
    available   BOOLEAN      NOT NULL,
    description VARCHAR(255) NOT NULL,
    name        VARCHAR(255) NOT NULL,
    owner_id    BIGINT       NOT NULL,
    CONSTRAINT pk_items PRIMARY KEY (id),
    CONSTRAINT fk_items_users FOREIGN KEY (owner_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS bookings
(
    id         BIGINT GENERATED BY DEFAULT AS IDENTITY,
    end_date   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    start_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    status     VARCHAR(255)                NOT NULL,
    booker_id  BIGINT                      NOT NULL,
    item_id    BIGINT                      NOT NULL,
    CONSTRAINT pk_booking PRIMARY KEY (id),
    CONSTRAINT fk_booking_users FOREIGN KEY (booker_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_booking_items FOREIGN KEY (item_id) REFERENCES items (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS comments
(
    id        BIGINT GENERATED BY DEFAULT AS IDENTITY,
    text      VARCHAR                     NOT NULL,
    item_id   BIGINT                      NOT NULL,
    author_id BIGINT                      NOT NULL,
    created   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_comments PRIMARY KEY (id),
    CONSTRAINT fk_comments_users FOREIGN KEY (author_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_comments_items FOREIGN KEY (item_id) REFERENCES items (id) ON DELETE CASCADE
);

ALTER TABLE users
    ADD CONSTRAINT email_unique UNIQUE (email);