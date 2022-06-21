CREATE TABLE features
(
    id   BIGSERIAL NOT NULL,
    name VARCHAR(45) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE producers
(
    id   BIGSERIAL NOT NULL,
    name varchar(45) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE phones
(
    id          BIGSERIAL NOT NULL,
    producer_id BIGINT NOT NULL,
    price       DECIMAL(11) NOT NULL,
    model       VARCHAR(45) NOT NULL,
    color       VARCHAR(45) NULL,
    PRIMARY KEY (id),
    CONSTRAINT producers_fk
        FOREIGN KEY (producer_id)
            REFERENCES producers (id)
            ON DELETE NO ACTION
            ON UPDATE NO ACTION
);
CREATE INDEX producers_fk_idx ON phones(producer_id DESC NULLS LAST);

CREATE TABLE phones_features
(
    id         BIGSERIAL NOT NULL,
    phone_id   BIGINT NOT NULL,
    feature_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT phones_features_features_fk
        FOREIGN KEY (feature_id)
            REFERENCES features (id)
            ON DELETE NO ACTION
            ON UPDATE NO ACTION,
    CONSTRAINT phones_features_phones_fk
        FOREIGN KEY (phone_id)
            REFERENCES phones (id)
            ON DELETE NO ACTION
            ON UPDATE NO ACTION
);
CREATE INDEX phones_features_features_fk_idx ON phones_features(phone_id ASC NULLS LAST);
CREATE INDEX phones_features_phones_fk_idx ON phones_features(feature_id ASC NULLS LAST);

-- # INSERT FEATURES
INSERT INTO features (name) VALUES ('NFC');
INSERT INTO features (name) VALUES ('IPS');
INSERT INTO features (name) VALUES ('WiFI');
INSERT INTO features (name) VALUES ('Bluetooth');

-- # INSERT PRODUCERS
INSERT INTO producers (name) VALUES ('Apple');
INSERT INTO producers (name) VALUES ('Nokia');
INSERT INTO producers (name) VALUES ('Samsung');
INSERT INTO producers (name) VALUES ('Oppo');
INSERT INTO producers (name) VALUES ('Xiaomi');
INSERT INTO producers (name) VALUES ('LG');

-- # INSERT PHONES
INSERT INTO phones (producer_id, price, model, color) VALUES (1, 1000, 'XL', 'RED');
INSERT INTO phones (producer_id, price, model, color) VALUES (2, 2010, 'Phone 123', 'BLACK');
INSERT INTO phones (producer_id, price, model, color) VALUES (2, 800, 'Phone 012', 'YELLOW');
INSERT INTO phones (producer_id, price, model, color) VALUES (1, 1100, 'Phone 012', 'WHITE');
INSERT INTO phones (producer_id, price, model, color) VALUES (5, 2010, 'Phone 145', 'BLACK');
INSERT INTO phones (producer_id, price, model, color) VALUES (3, 800, 'Phone 199', 'YELLOW');
INSERT INTO phones (producer_id, price, model, color) VALUES (1, 1000, 'Phone 229', 'RED');
INSERT INTO phones (producer_id, price, model, color) VALUES (3, 2010, 'Phone 199', 'BLACK');
INSERT INTO phones (producer_id, price, model, color) VALUES (4, 800, 'Phone 177', 'YELLOW');
INSERT INTO phones (producer_id, price, model, color) VALUES (1, 1310, 'Phone 116', 'RED');
INSERT INTO phones (producer_id, price, model, color) VALUES (2, 2010, 'Phone 019', 'WHITE');
INSERT INTO phones (producer_id, price, model, color) VALUES (2, 800, 'Phone 212', 'YELLOW');
INSERT INTO phones (producer_id, price, model, color) VALUES (4, 100, 'Phone 229', 'YELLOW');
INSERT INTO phones (producer_id, price, model, color) VALUES (1, 210, 'Phone 196', 'RED');
INSERT INTO phones (producer_id, price, model, color) VALUES (2, 10500, 'Phone 219', 'WHITE');
INSERT INTO phones (producer_id, price, model, color) VALUES (6, 800, 'Phone 312', 'YELLOW');
INSERT INTO phones (producer_id, price, model, color) VALUES (6, 800, 'Phone 229', 'WHITE');
INSERT INTO phones (producer_id, price, model, color) VALUES (6, 800, 'Phone 153', 'RED');
INSERT INTO phones (producer_id, price, model, color) VALUES (6, 900, 'Phone 150', 'RED');

-- # ADD FEATURES TO PHONES
INSERT INTO phones_features (phone_id, feature_id) VALUES (1, 1);
INSERT INTO phones_features (phone_id, feature_id) VALUES (1, 2);
INSERT INTO phones_features (phone_id, feature_id) VALUES (2, 2);
INSERT INTO phones_features (phone_id, feature_id) VALUES (3, 1);
INSERT INTO phones_features (phone_id, feature_id) VALUES (3, 3);
INSERT INTO phones_features (phone_id, feature_id) VALUES (4, 1);
INSERT INTO phones_features (phone_id, feature_id) VALUES (4, 2);
INSERT INTO phones_features (phone_id, feature_id) VALUES (4, 3);
INSERT INTO phones_features (phone_id, feature_id) VALUES (5, 1);
INSERT INTO phones_features (phone_id, feature_id) VALUES (5, 3);
INSERT INTO phones_features (phone_id, feature_id) VALUES (6, 2);
INSERT INTO phones_features (phone_id, feature_id) VALUES (7, 1);
INSERT INTO phones_features (phone_id, feature_id) VALUES (7, 2);
INSERT INTO phones_features (phone_id, feature_id) VALUES (7, 4);
INSERT INTO phones_features (phone_id, feature_id) VALUES (8, 3);
INSERT INTO phones_features (phone_id, feature_id) VALUES (8, 4);
INSERT INTO phones_features (phone_id, feature_id) VALUES (9, 4);
INSERT INTO phones_features (phone_id, feature_id) VALUES (10, 4);
INSERT INTO phones_features (phone_id, feature_id) VALUES (12, 1);
INSERT INTO phones_features (phone_id, feature_id) VALUES (12, 4);
INSERT INTO phones_features (phone_id, feature_id) VALUES (13, 1);
INSERT INTO phones_features (phone_id, feature_id) VALUES (14, 1);
INSERT INTO phones_features (phone_id, feature_id) VALUES (14, 2);
INSERT INTO phones_features (phone_id, feature_id) VALUES (14, 3);
INSERT INTO phones_features (phone_id, feature_id) VALUES (14, 4);
INSERT INTO phones_features (phone_id, feature_id) VALUES (15, 1);
INSERT INTO phones_features (phone_id, feature_id) VALUES (15, 2);
INSERT INTO phones_features (phone_id, feature_id) VALUES (15, 3);
INSERT INTO phones_features (phone_id, feature_id) VALUES (15, 4);
INSERT INTO phones_features (phone_id, feature_id) VALUES (16, 1);
INSERT INTO phones_features (phone_id, feature_id) VALUES (16, 2);
INSERT INTO phones_features (phone_id, feature_id) VALUES (16, 3);
INSERT INTO phones_features (phone_id, feature_id) VALUES (16, 4);
INSERT INTO phones_features (phone_id, feature_id) VALUES (17, 1);
INSERT INTO phones_features (phone_id, feature_id) VALUES (17, 2);
INSERT INTO phones_features (phone_id, feature_id) VALUES (17, 3);
INSERT INTO phones_features (phone_id, feature_id) VALUES (17, 4);
INSERT INTO phones_features (phone_id, feature_id) VALUES (18, 1);
INSERT INTO phones_features (phone_id, feature_id) VALUES (18, 2);
INSERT INTO phones_features (phone_id, feature_id) VALUES (18, 3);
INSERT INTO phones_features (phone_id, feature_id) VALUES (18, 4);
INSERT INTO phones_features (phone_id, feature_id) VALUES (19, 1);
INSERT INTO phones_features (phone_id, feature_id) VALUES (19, 2);
INSERT INTO phones_features (phone_id, feature_id) VALUES (19, 3);
INSERT INTO phones_features (phone_id, feature_id) VALUES (19, 4);
