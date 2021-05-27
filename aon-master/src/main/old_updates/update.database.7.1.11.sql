# Database: aon_master
# Version: Actualizacion de la version 7.1.11 a la version 7.1.12.
# Created by: girazu
# Creation Date: 30/10/2012 18:15
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.

BEGIN;

SET @Domain = 1;

INSERT INTO `task_holder` (`registry`, `domain`) SELECT `registry`, @Domain FROM `instructor`;

ALTER TABLE `course_instructor` DROP FOREIGN KEY `FK_COURSE_INSTRUCTOR_EMPLOYEE`;
ALTER TABLE `course_instructor` DROP KEY `IDX_COURSE_INSTRUCTOR_EMPLOYEE`;
ALTER TABLE `course_instructor` CHANGE `employee` `task_holder` int(4) NOT NULL COMMENT 'Identificador del Profesor';
ALTER TABLE `course_instructor` ADD KEY `IDX_COURSE_INSTRUCTOR_TASK_HOLDER` (`task_holder`);
ALTER TABLE `course_instructor` ADD CONSTRAINT `FK_COURSE_INSTRUCTOR_TASK_HOLDER` FOREIGN KEY (`task_holder`) REFERENCES `task_holder` (`registry`);

DROP TABLE `instructor`;

INSERT INTO `segment` (`id`, `domain`, `name`) SELECT `id`, @Domain, `description` FROM `customer_segment`;
INSERT INTO `rsegment` (`domain`, `registry`, `segment`) SELECT @Domain, `registry`, `segment` FROM `customer` WHERE segment IS NOT NULL;


UPDATE `db_version` SET `version_number` = '7.1.12';

COMMIT;
