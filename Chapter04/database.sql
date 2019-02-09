CREATE DATABASE IF NOT EXISTS chapter04;

USE chapter04;

CREATE TABLE IF NOT EXISTS chapter04.people (
    name        VARCHAR(100),
    title       VARCHAR(10),
    description VARCHAR(100),
    PRIMARY KEY (name)
);

DELETE FROM chapter04.people;

INSERT INTO chapter04.people VALUES ('Gru', 'Felonius', 'Where are the minions?');
INSERT INTO chapter04.people VALUES ('Nefario', 'Dr.', 'Why ... why are you so old?');
INSERT INTO chapter04.people VALUES ('Agnes', '', 'Your unicorn is so fluffy!');
INSERT INTO chapter04.people VALUES ('Edith', '', "Don't touch anything!");
INSERT INTO chapter04.people VALUES ('Vector', '', 'Committing crimes with both direction and magnitude!');
INSERT INTO chapter04.people VALUES ('Dave', 'Minion', 'Ngaaahaaa! Patalaki patalaku Big Boss!!');
