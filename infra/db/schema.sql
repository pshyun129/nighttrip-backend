-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema nighttrip
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema nighttrip
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `nighttrip` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci ;
USE `nighttrip` ;

-- -----------------------------------------------------
-- Table `nighttrip`.`city`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `nighttrip`.`city` (
  `city_id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(50) NOT NULL,
  `region` VARCHAR(50) NULL DEFAULT NULL,
  `description` VARCHAR(1000) NOT NULL,
  `image_url` VARCHAR(500) NULL DEFAULT NULL,
  PRIMARY KEY (`city_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `nighttrip`.`users`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `nighttrip`.`users` (
  `user_id` BIGINT NOT NULL AUTO_INCREMENT,
  `email` VARCHAR(100) NOT NULL,
  `password` VARCHAR(255) NOT NULL,
  `nickname` VARCHAR(50) NOT NULL,
  `profile_image_url` VARCHAR(500) NULL DEFAULT NULL,
  `role` ENUM('USER', 'ADMIN') NOT NULL DEFAULT 'USER',
  `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
  `provider` VARCHAR(20) NOT NULL DEFAULT 'LOCAL',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted_at` DATETIME NULL DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE INDEX `uk_users_email` (`email` ASC) VISIBLE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `nighttrip`.`course`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `nighttrip`.`course` (
  `course_id` BIGINT NOT NULL AUTO_INCREMENT,
  `city_id` BIGINT NOT NULL,
  `user_id` BIGINT NULL DEFAULT NULL,
  `title` VARCHAR(100) NOT NULL,
  `description` VARCHAR(100) NOT NULL,
  `theme` VARCHAR(50) NULL DEFAULT NULL,
  `start_time` TIME NULL DEFAULT NULL,
  `end_time` TIME NULL DEFAULT NULL,
  `total_duration_minutes` INT NULL DEFAULT NULL,
  `total_travel_minutes` INT NULL DEFAULT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted_at` DATETIME NULL DEFAULT NULL,
  `transport` VARCHAR(10) NOT NULL,
  PRIMARY KEY (`course_id`),
  INDEX `fk_course_city` (`city_id` ASC) VISIBLE,
  INDEX `fk_course_user` (`user_id` ASC) VISIBLE,
  CONSTRAINT `fk_course_city`
    FOREIGN KEY (`city_id`)
    REFERENCES `nighttrip`.`city` (`city_id`)
    ON DELETE CASCADE,
  CONSTRAINT `fk_course_user`
    FOREIGN KEY (`user_id`)
    REFERENCES `nighttrip`.`users` (`user_id`)
    ON DELETE SET NULL)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `nighttrip`.`place`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `nighttrip`.`place` (
  `place_id` BIGINT NOT NULL AUTO_INCREMENT,
  `city_id` BIGINT NOT NULL,
  `name` VARCHAR(100) NOT NULL,
  `category` VARCHAR(50) NOT NULL,
  `address` VARCHAR(255) NULL DEFAULT NULL,
  `road_address` VARCHAR(255) NULL DEFAULT NULL,
  `latitude` DECIMAL(10,7) NOT NULL,
  `longitude` DECIMAL(10,7) NOT NULL,
  `phone` VARCHAR(30) NULL DEFAULT NULL,
  `description` VARCHAR(1000) NULL DEFAULT NULL,
  `image_url` VARCHAR(500) NULL DEFAULT NULL,
  `opening_time` VARCHAR(10) NULL DEFAULT NULL,
  `closing_time` VARCHAR(10) NULL,
  `deleted_at` DATETIME NULL DEFAULT NULL,
  PRIMARY KEY (`place_id`),
  INDEX `fk_place_city` (`city_id` ASC) VISIBLE,
  CONSTRAINT `fk_place_city`
    FOREIGN KEY (`city_id`)
    REFERENCES `nighttrip`.`city` (`city_id`)
    ON DELETE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `nighttrip`.`course_place`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `nighttrip`.`course_place` (
  `course_place_id` BIGINT NOT NULL AUTO_INCREMENT,
  `course_id` BIGINT NOT NULL,
  `place_id` BIGINT NOT NULL,
  `sequence` INT NOT NULL,
  `travel_minutes_from_previous` INT NOT NULL DEFAULT '0',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`course_place_id`),
  INDEX `fk_course_place_course` (`course_id` ASC) VISIBLE,
  INDEX `fk_course_place_place` (`place_id` ASC) VISIBLE,
  CONSTRAINT `fk_course_place_course`
    FOREIGN KEY (`course_id`)
    REFERENCES `nighttrip`.`course` (`course_id`)
    ON DELETE CASCADE,
  CONSTRAINT `fk_course_place_place`
    FOREIGN KEY (`place_id`)
    REFERENCES `nighttrip`.`place` (`place_id`)
    ON DELETE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `nighttrip`.`place_favorite`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `nighttrip`.`place_favorite` (
  `user_id` BIGINT NOT NULL,
  `place_id` BIGINT NOT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`, `place_id`),
  INDEX `fk_place_favorite_place` (`place_id` ASC) VISIBLE,
  CONSTRAINT `fk_place_favorite_place`
    FOREIGN KEY (`place_id`)
    REFERENCES `nighttrip`.`place` (`place_id`)
    ON DELETE CASCADE,
  CONSTRAINT `fk_place_favorite_user`
    FOREIGN KEY (`user_id`)
    REFERENCES `nighttrip`.`users` (`user_id`)
    ON DELETE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `nighttrip`.`tag`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `nighttrip`.`tag` (
  `tag_id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(50) NOT NULL,
  `can_use` TINYINT NOT NULL DEFAULT 1,
  PRIMARY KEY (`tag_id`),
  UNIQUE INDEX `uk_tag_name` (`name` ASC) VISIBLE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `nighttrip`.`place_tag`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `nighttrip`.`place_tag` (
  `place_id` BIGINT NOT NULL,
  `tag_id` BIGINT NOT NULL,
  `confidence` DOUBLE NOT NULL DEFAULT 1.0,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `can_use` TINYINT NOT NULL DEFAULT 1,
  PRIMARY KEY (`place_id`, `tag_id`),
  INDEX `fk_place_tag_tag` (`tag_id` ASC) VISIBLE,
  CONSTRAINT `fk_place_tag_place`
    FOREIGN KEY (`place_id`)
    REFERENCES `nighttrip`.`place` (`place_id`)
    ON DELETE CASCADE,
  CONSTRAINT `fk_place_tag_tag`
    FOREIGN KEY (`tag_id`)
    REFERENCES `nighttrip`.`tag` (`tag_id`)
    ON DELETE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `nighttrip`.`review`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `nighttrip`.`review` (
  `review_id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `place_id` BIGINT NOT NULL,
  `content` TEXT NOT NULL,
  `visit_date` DATE NULL DEFAULT NULL,
  `visibility` VARCHAR(20) NOT NULL DEFAULT 'PUBLIC',
  `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
  `like_count` INT NOT NULL DEFAULT '0',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted_at` DATETIME NULL DEFAULT NULL,
  PRIMARY KEY (`review_id`),
  INDEX `fk_review_place` (`place_id` ASC) VISIBLE,
  INDEX `fk_review_user` (`user_id` ASC) VISIBLE,
  CONSTRAINT `fk_review_place`
    FOREIGN KEY (`place_id`)
    REFERENCES `nighttrip`.`place` (`place_id`)
    ON DELETE CASCADE,
  CONSTRAINT `fk_review_user`
    FOREIGN KEY (`user_id`)
    REFERENCES `nighttrip`.`users` (`user_id`)
    ON DELETE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `nighttrip`.`review_image`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `nighttrip`.`review_image` (
  `review_image_id` BIGINT NOT NULL AUTO_INCREMENT,
  `review_id` BIGINT NOT NULL,
  `image_url` VARCHAR(500) NOT NULL,
  `sort_order` INT NOT NULL DEFAULT '0',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`review_image_id`),
  INDEX `fk_review_image_review` (`review_id` ASC) VISIBLE,
  CONSTRAINT `fk_review_image_review`
    FOREIGN KEY (`review_id`)
    REFERENCES `nighttrip`.`review` (`review_id`)
    ON DELETE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `nighttrip`.`review_like`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `nighttrip`.`review_like` (
  `user_id` BIGINT NOT NULL,
  `review_id` BIGINT NOT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`, `review_id`),
  INDEX `fk_review_like_review` (`review_id` ASC) VISIBLE,
  CONSTRAINT `fk_review_like_review`
    FOREIGN KEY (`review_id`)
    REFERENCES `nighttrip`.`review` (`review_id`)
    ON DELETE CASCADE,
  CONSTRAINT `fk_review_like_user`
    FOREIGN KEY (`user_id`)
    REFERENCES `nighttrip`.`users` (`user_id`)
    ON DELETE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;