SET REFERENTIAL_INTEGRITY FALSE;
TRUNCATE TABLE FRIENDS      RESTART IDENTITY;
TRUNCATE TABLE FILMS_GENRES  RESTART IDENTITY;
TRUNCATE TABLE GENRES        RESTART IDENTITY;
TRUNCATE TABLE FILMS_LIKES   RESTART IDENTITY;
TRUNCATE TABLE FILMS        RESTART IDENTITY;
TRUNCATE TABLE MPA_RATINGS   RESTART IDENTITY;
TRUNCATE TABLE USERS        RESTART IDENTITY;
TRUNCATE TABLE EVENTS        RESTART IDENTITY;
TRUNCATE TABLE FILMS_DIRECTORS  RESTART IDENTITY;
TRUNCATE TABLE DIRECTORS      RESTART IDENTITY;
TRUNCATE TABLE REVIEWS      RESTART IDENTITY;
TRUNCATE TABLE REVIEWS_LIKES    RESTART IDENTITY;
SET REFERENTIAL_INTEGRITY TRUE;

INSERT INTO GENRES (GENRE_NAME)
VALUES ('Комедия'),
       ('Драма'),
       ('Мультфильм'),
       ('Триллер'),
       ('Документальный'),
       ('Боевик');

INSERT INTO MPA_RATINGS (MPA_NAME)
VALUES ('G'),
       ('PG'),
       ('PG-13'),
       ('R'),
       ('NC-17');