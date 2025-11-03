-- MySQL dump 10.13  Distrib 9.5.0, for macos26.0 (arm64)
--
-- Host: localhost    Database: dms_movies
-- ------------------------------------------------------
-- Server version	9.5.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Current Database: `dms_movies`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `dms_movies` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `dms_movies`;

--
-- Table structure for table `movies`
--

DROP TABLE IF EXISTS `movies`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `movies` (
  `movie_id` varchar(10) NOT NULL,
  `title` varchar(200) NOT NULL,
  `director` varchar(120) NOT NULL,
  `release_year` int NOT NULL,
  `duration_minutes` int NOT NULL,
  `genre` varchar(80) NOT NULL,
  `rating` double NOT NULL,
  PRIMARY KEY (`movie_id`),
  CONSTRAINT `chk_duration` CHECK ((`duration_minutes` between 1 and 999)),
  CONSTRAINT `chk_rating` CHECK ((`rating` between 0.0 and 10.0)),
  CONSTRAINT `chk_release_year` CHECK ((`release_year` between 1888 and 2100))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `movies`
--

LOCK TABLES `movies` WRITE;
/*!40000 ALTER TABLE `movies` DISABLE KEYS */;
INSERT INTO `movies` VALUES ('AVA2015','Avatar','James Cameron',2009,162,'Science Fiction',8),('AVA2022','Avatar: The Way of Water','James Cameron',2022,192,'Science Fiction',7.6),('DKN2008','The Dark Knight','Christopher Nolan',2008,152,'Action',9),('FOR1994','Forrest Gump','Robert Zemeckis',1994,142,'Drama',8.8),('FUR2015','Mad Max: Fury Road','George Miller',2015,120,'Action',8.1),('GLA2000','Gladiator','Ridley Scott',2000,155,'Action',8.5),('GOD1972','The Godfather','Francis Ford Coppola',1972,175,'Crime',9.2),('GUA2014','Guardians of the Galaxy','James Gunn',2014,121,'Action',8),('INT2010','Inception','Christopher Nolan',2010,148,'Science Fiction',9),('JUR1993','Jurassic Park','Steven Spielberg',1993,127,'Adventure',8.2),('LOT2001','The Fellowship of the Ring','Peter Jackson',2001,178,'Fantasy',8.8),('LOT2002','The Two Towers','Peter Jackson',2002,179,'Fantasy',8.7),('LOT2003','The Return of the King','Peter Jackson',2003,201,'Fantasy',9),('MAT1999','The Matrix','Lana Wachowski, Lilly Wachowski',1999,136,'Science Fiction',8.7),('PAR2019','Parasite','Bong Joon-ho',2019,132,'Thriller',8.6),('PUL1994','Pulp Fiction','Quentin Tarantino',1994,154,'Crime',8.9),('SCH1993','Schindler\'s List','Steven Spielberg',1993,195,'Drama',9),('STA1977','Star Wars: A New Hope','George Lucas',1977,121,'Science Fiction',8.6),('TIT1997','Titanic','James Cameron',1997,195,'Romance',7.9),('WHI2014','Whiplash','Damien Chazelle',2014,106,'Drama',8.5);
/*!40000 ALTER TABLE `movies` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'dms_movies'
--

--
-- Dumping routines for database 'dms_movies'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-11-03  0:20:21
