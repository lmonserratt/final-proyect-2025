
-- ===========================================
--  Database: dms_movies
--  Author: Luis Augusto Monserratt Alvarado
--  Description: Full setup script for Movie Manager DMS (MySQL version)
-- ===========================================

CREATE DATABASE IF NOT EXISTS dms_movies
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_0900_ai_ci;

USE dms_movies;

DROP TABLE IF EXISTS movies;

CREATE TABLE movies (
                        movie_id         VARCHAR(10)  NOT NULL,
                        title            VARCHAR(200) NOT NULL,
                        director         VARCHAR(120) NOT NULL,
                        release_year     INT          NOT NULL,
                        duration_minutes INT          NOT NULL,
                        genre            VARCHAR(80)  NOT NULL,
                        rating           DOUBLE       NOT NULL,
                        CONSTRAINT pk_movies PRIMARY KEY (movie_id),
                        CONSTRAINT chk_release_year CHECK (release_year BETWEEN 1888 AND 2100),
                        CONSTRAINT chk_duration CHECK (duration_minutes BETWEEN 1 AND 999),
                        CONSTRAINT chk_rating CHECK (rating BETWEEN 0.0 AND 10.0)
);

INSERT INTO movies (movie_id, title, director, release_year, duration_minutes, genre, rating) VALUES
                                                                                                  ('AVA2015','Avatar','James Cameron',2009,162,'Science Fiction',8.0),
                                                                                                  ('INT2010','Inception','Christopher Nolan',2010,148,'Science Fiction',9.0),
                                                                                                  ('GOD1972','The Godfather','Francis Ford Coppola',1972,175,'Crime',9.2),
                                                                                                  ('DKN2008','The Dark Knight','Christopher Nolan',2008,152,'Action',9.0),
                                                                                                  ('PUL1994','Pulp Fiction','Quentin Tarantino',1994,154,'Crime',8.9),
                                                                                                  ('FOR1994','Forrest Gump','Robert Zemeckis',1994,142,'Drama',8.8),
                                                                                                  ('MAT1999','The Matrix','Lana Wachowski, Lilly Wachowski',1999,136,'Science Fiction',8.7),
                                                                                                  ('LOT2001','The Fellowship of the Ring','Peter Jackson',2001,178,'Fantasy',8.8),
                                                                                                  ('LOT2002','The Two Towers','Peter Jackson',2002,179,'Fantasy',8.7),
                                                                                                  ('LOT2003','The Return of the King','Peter Jackson',2003,201,'Fantasy',9.0),
                                                                                                  ('STA1977','Star Wars: A New Hope','George Lucas',1977,121,'Science Fiction',8.6),
                                                                                                  ('GUA2014','Guardians of the Galaxy','James Gunn',2014,121,'Action',8.0),
                                                                                                  ('TIT1997','Titanic','James Cameron',1997,195,'Romance',7.9),
                                                                                                  ('GLA2000','Gladiator','Ridley Scott',2000,155,'Action',8.5),
                                                                                                  ('AVA2022','Avatar: The Way of Water','James Cameron',2022,192,'Science Fiction',7.6),
                                                                                                  ('JUR1993','Jurassic Park','Steven Spielberg',1993,127,'Adventure',8.2),
                                                                                                  ('SCH1993','Schindler''s List','Steven Spielberg',1993,195,'Drama',9.0),
                                                                                                  ('FUR2015','Mad Max: Fury Road','George Miller',2015,120,'Action',8.1),
                                                                                                  ('WHI2014','Whiplash','Damien Chazelle',2014,106,'Drama',8.5),
                                                                                                  ('PAR2019','Parasite','Bong Joon-ho',2019,132,'Thriller',8.6);

USE dms_movies;
SELECT COUNT(*) FROM movies;
SELECT * FROM movies LIMIT 20;