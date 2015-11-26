# Database: aon_master
# Version: Actualizacion de la version 7.1.7 a la version 7.1.8.
# Created by: girazu
# Creation Date: 30/08/2012 11:45
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.


BEGIN;

ALTER TABLE `contact` ADD `domain` int(4) NOT NULL default 1 COMMENT 'Identificador del Dominio' AFTER `id`;
UPDATE `contact`, `user` SET `contact`.`domain` = `user`.`domain` WHERE `contact`.`user_id` = `user`.`id`;
ALTER TABLE `contact` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';
ALTER TABLE `contact` ADD KEY `IDX_CONTACT_DOMAIN` (`domain`);
ALTER TABLE `contact` ADD CONSTRAINT `FK_CONTACT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);
ALTER TABLE `contact` MODIFY `user_id` int(4) default NULL COMMENT 'Identificador del Usuario';

ALTER TABLE `contact_detail` ADD `domain` int(4) NOT NULL default 1 COMMENT 'Identificador del Dominio' AFTER `id`;
UPDATE `contact_detail`, `contact` SET `contact_detail`.`domain` = `contact`.`domain` WHERE `contact_detail`.`contact_group` = `contact`.`id`;
ALTER TABLE `contact_detail` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';
ALTER TABLE `contact_detail` ADD KEY `IDX_CONTACT_DETAIL_DOMAIN` (`domain`);
ALTER TABLE `contact_detail` ADD CONSTRAINT `FK_CONTACT_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `contact_data` ADD `domain` int(4) NOT NULL default 1 COMMENT 'Identificador del Dominio' AFTER `id`;
UPDATE `contact_data`, `contact` SET `contact_data`.`domain` = `contact`.`domain` WHERE `contact`.`contact_data` = `contact_data`.`id`;
ALTER TABLE `contact_data` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';
ALTER TABLE `contact_data` ADD KEY `IDX_CONTACT_DATA_DOMAIN` (`domain`);
ALTER TABLE `contact_data` ADD CONSTRAINT `FK_CONTACT_DATA_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

CREATE TABLE `profile_action_denied` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `profile` int(4) NOT NULL COMMENT 'Identificador del Perfil',
  `action_id` int(4) NOT NULL COMMENT 'Identificador de la Accion',
  PRIMARY KEY  (`id`),
  KEY `IDX_PROFILE_ACTION_DENIED_DOMAIN` (`domain`),
  KEY `IDX_PROFILE_ACTION_DENIED_PROFILE` (`profile`),
  KEY `IDX_PROFILE_ACTION_DENIED_ACTION` (`action_id`),
  CONSTRAINT `FK_PROFILE_ACTION_DENIED_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_PROFILE_ACTION_DENIED_PROFILE` FOREIGN KEY (`profile`) REFERENCES `profile` (`id`),
  CONSTRAINT `FK_PROFILE_ACTION_DENIED_ACTION` FOREIGN KEY (`action_id`) REFERENCES `action` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Acciones Inhabilitadas en el Perfil';


ALTER TABLE `absence` DROP FOREIGN KEY `absence_fk_1`;
ALTER TABLE `absence` DROP KEY `course_alumn`;
ALTER TABLE `absence` ADD KEY `IDX_ABSENCE_COURSE_ALUMN` (`course_alumn`);
ALTER TABLE `absence` ADD CONSTRAINT `FK_ABSENCE_COURSE_ALUMN` FOREIGN KEY (`course_alumn`) REFERENCES `course_alumn` (`id`);

ALTER TABLE `alumn_loan` DROP FOREIGN KEY `alumn_loan_fk1`;
ALTER TABLE `alumn_loan` DROP KEY `customer`;
ALTER TABLE `alumn_loan` ADD KEY `IDX_ALUMN_LOAN_CUSTOMER` (`customer`);
ALTER TABLE `alumn_loan` ADD CONSTRAINT `FK_ALUMN_LOAN_CUSTOMER` FOREIGN KEY (`customer`) REFERENCES `customer` (`registry`);

ALTER TABLE `course` DROP FOREIGN KEY `course_fk`;
ALTER TABLE `course` DROP FOREIGN KEY `course_fk1`;
ALTER TABLE `course` DROP FOREIGN KEY `course_fk2`;
ALTER TABLE `course` DROP FOREIGN KEY `course_fk3`;
ALTER TABLE `course` DROP KEY `subject`;
ALTER TABLE `course` DROP KEY `level`;
ALTER TABLE `course` DROP KEY `academic_year`;
ALTER TABLE `course` DROP KEY `workplace`;
ALTER TABLE `course` ADD KEY `IDX_COURSE_ACADEMIC_YEAR` (`academic_year`);
ALTER TABLE `course` ADD KEY `IDX_COURSE_LEVEL` (`level`);
ALTER TABLE `course` ADD KEY `IDX_COURSE_SUBJECT` (`subject`);
ALTER TABLE `course` ADD KEY `IDX_COURSE_WORKPLACE` (`workplace`);
ALTER TABLE `course` ADD CONSTRAINT `FK_COURSE_ACADEMIC_YEAR` FOREIGN KEY (`academic_year`) REFERENCES `academic_year` (`id`);
ALTER TABLE `course` ADD CONSTRAINT `FK_COURSE_LEVEL` FOREIGN KEY (`level`) REFERENCES `course_level` (`id`);
ALTER TABLE `course` ADD CONSTRAINT `FK_COURSE_SUBJECT` FOREIGN KEY (`subject`) REFERENCES `course_subject` (`id`);
ALTER TABLE `course` ADD CONSTRAINT `FK_COURSE_WORKPLACE` FOREIGN KEY (`workplace`) REFERENCES `workplace` (`id`);

ALTER TABLE `course_academicskill` DROP FOREIGN KEY `course_academic_skill_fk_1`;
ALTER TABLE `course_academicskill` DROP FOREIGN KEY `course_academic_skill_fk_2`;
ALTER TABLE `course_academicskill` DROP KEY `course`;
ALTER TABLE `course_academicskill` DROP KEY `academic_skill`;
ALTER TABLE `course_academicskill` ADD KEY `IDX_COURSE_ACADEMIC_SKILL_COURSE` (`course`);
ALTER TABLE `course_academicskill` ADD KEY `IDX_COURSE_ACADEMIC_SKILL_ACADEMIC_SKILL` (`academic_skill`);
ALTER TABLE `course_academicskill` ADD CONSTRAINT `FK_COURSE_ACADEMIC_SKILL_COURSE` FOREIGN KEY (`course`) REFERENCES `course` (`id`);
ALTER TABLE `course_academicskill` ADD CONSTRAINT `FK_COURSE_ACADEMIC_SKILL_ACADEMIC_SKILL` FOREIGN KEY (`academic_skill`) REFERENCES `academic_skill` (`id`);

ALTER TABLE `course_alumn` DROP FOREIGN KEY `course_alumns_fk`;
ALTER TABLE `course_alumn` DROP FOREIGN KEY `course_alumns_fk1`;
ALTER TABLE `course_alumn` DROP KEY `course`;
ALTER TABLE `course_alumn` DROP KEY `customer`;
ALTER TABLE `course_alumn` ADD KEY `IDX_COURSE_ALUMN_COURSE` (`course`);
ALTER TABLE `course_alumn` ADD KEY `IDX_COURSE_ALUMN_CUSTOMER` (`customer`);
ALTER TABLE `course_alumn` ADD CONSTRAINT `FK_COURSE_ALUMN_COURSE` FOREIGN KEY (`course`) REFERENCES `course` (`id`);
ALTER TABLE `course_alumn` ADD CONSTRAINT `FK_COURSE_ALUMN_CUSTOMER` FOREIGN KEY (`customer`) REFERENCES `customer` (`registry`);

ALTER TABLE `course_evaluation` DROP FOREIGN KEY `course_evaluation_skill_fk_1`;
ALTER TABLE `course_evaluation` DROP FOREIGN KEY `course_evaluation_skill_fk_2`;
ALTER TABLE `course_evaluation` DROP KEY `course`;
ALTER TABLE `course_evaluation` DROP KEY `quality_skill`;
ALTER TABLE `course_evaluation` ADD KEY `IDX_COURSE_EVALUATION_COURSE` (`course`);
ALTER TABLE `course_evaluation` ADD KEY `IDX_COURSE_EVALUATION_QUALITY_SKILL` (`quality_skill`);
ALTER TABLE `course_evaluation` ADD CONSTRAINT `FK_COURSE_EVALUATION_COURSE` FOREIGN KEY (`course`) REFERENCES `course` (`id`);
ALTER TABLE `course_evaluation` ADD CONSTRAINT `FK_COURSE_EVALUATION_QUALITY_SKILL` FOREIGN KEY (`quality_skill`) REFERENCES `quality_skill` (`id`);

ALTER TABLE `course_instructor` DROP FOREIGN KEY `course-instructor_fk`;
ALTER TABLE `course_instructor` DROP FOREIGN KEY `course-instructor_fk1`;
ALTER TABLE `course_instructor` DROP KEY `course`;
ALTER TABLE `course_instructor` DROP KEY `employee`;
ALTER TABLE `course_instructor` ADD KEY `IDX_COURSE_INSTRUCTOR_COURSE` (`course`);
ALTER TABLE `course_instructor` ADD KEY `IDX_COURSE_INSTRUCTOR_EMPLOYEE` (`employee`);
ALTER TABLE `course_instructor` ADD CONSTRAINT `FK_COURSE_INSTRUCTOR_COURSE` FOREIGN KEY (`course`) REFERENCES `course` (`id`);
ALTER TABLE `course_instructor` ADD CONSTRAINT `FK_COURSE_INSTRUCTOR_EMPLOYEE` FOREIGN KEY (`employee`) REFERENCES `instructor` (`registry`);

ALTER TABLE `course_observation` DROP FOREIGN KEY `course_observation_skill_fk_1`;
ALTER TABLE `course_observation` DROP KEY `course`;
ALTER TABLE `course_observation` ADD KEY `IDX_COURSE_OBSERVATION_COURSE` (`course`);
ALTER TABLE `course_observation` ADD CONSTRAINT `FK_COURSE_OBSERVATION_COURSE` FOREIGN KEY (`course`) REFERENCES `course` (`id`);

ALTER TABLE `course_schedule` DROP FOREIGN KEY `course_schedule_fk`;
ALTER TABLE `course_schedule` DROP KEY `course`;
ALTER TABLE `course_schedule` ADD KEY `IDX_COURSE_SCHEDULE_COURSE` (`course`);
ALTER TABLE `course_schedule` ADD CONSTRAINT `FK_COURSE_SCHEDULE_COURSE` FOREIGN KEY (`course`) REFERENCES `course` (`id`);

ALTER TABLE `evaluation_observation` DROP FOREIGN KEY `evaluation_observation_ibfk_1`;
ALTER TABLE `evaluation_observation` DROP KEY `alumn`;
ALTER TABLE `evaluation_observation` ADD KEY `IDX_EVALUATION_OBSERVATION_ALUMN` (`alumn`);
ALTER TABLE `evaluation_observation` ADD CONSTRAINT `FK_EVALUATION_OBSERVATION_ALUMN` FOREIGN KEY (`alumn`) REFERENCES `course_alumn` (`id`);

ALTER TABLE `mark` DROP FOREIGN KEY `mark_ibfk_1`;
ALTER TABLE `mark` DROP FOREIGN KEY `mark_subject_fk`;
ALTER TABLE `mark` DROP KEY `subject`;
ALTER TABLE `mark` DROP KEY `customer`;
ALTER TABLE `mark` DROP KEY `subject_customer_evaluation`;
ALTER TABLE `mark` ADD KEY `IDX_MARK_ALUMN` (`alumn`);
ALTER TABLE `mark` ADD KEY `IDX_MARK_SUBJECT` (`subject`);
ALTER TABLE `mark` ADD UNIQUE KEY `IDX_MARK_SUBJECT_ALUMN_EVALUATION` (`subject`,`alumn`,`evaluation`);
ALTER TABLE `mark` ADD CONSTRAINT `FK_MARK_ALUMN` FOREIGN KEY (`alumn`) REFERENCES `course_alumn` (`id`);
ALTER TABLE `mark` ADD CONSTRAINT `FK_MARK_SUBJECT` FOREIGN KEY (`subject`) REFERENCES `course_academicskill` (`id`);

ALTER TABLE `absence` ADD `domain` int(4) NOT NULL default 1 COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `absence` ADD KEY `IDX_ABSENCE_DOMAIN` (`domain`);
ALTER TABLE `absence` ADD CONSTRAINT `FK_ABSENCE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `academic_skill` ADD `domain` int(4) NOT NULL default 1 COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `academic_skill` ADD KEY `IDX_ACADEMIC_SKILL_DOMAIN` (`domain`);
ALTER TABLE `academic_skill` ADD CONSTRAINT `FK_ACADEMIC_SKILL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `academic_year` ADD `domain` int(4) NOT NULL default 1 COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `academic_year` ADD KEY `IDX_ACADEMIC_YEAR_DOMAIN` (`domain`);
ALTER TABLE `academic_year` ADD CONSTRAINT `FK_ACADEMIC_YEAR_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `alumn_loan` ADD `domain` int(4) NOT NULL default 1 COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `alumn_loan` ADD KEY `IDX_ALUMN_LOAN_DOMAIN` (`domain`);
ALTER TABLE `alumn_loan` ADD CONSTRAINT `FK_ALUMN_LOAN_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `course` ADD `domain` int(4) NOT NULL default 1 COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `course` ADD KEY `IDX_COURSE_DOMAIN` (`domain`);
ALTER TABLE `course` ADD CONSTRAINT `FK_COURSE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `course_academicskill` ADD `domain` int(4) NOT NULL default 1 COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `course_academicskill` ADD KEY `IDX_COURSE_ACADEMIC_SKILL_DOMAIN` (`domain`);
ALTER TABLE `course_academicskill` ADD CONSTRAINT `FK_COURSE_ACADEMIC_SKILL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `course_alumn` ADD `domain` int(4) NOT NULL default 1 COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `course_alumn` ADD KEY `IDX_COURSE_ALUMN_DOMAIN` (`domain`);
ALTER TABLE `course_alumn` ADD CONSTRAINT `FK_COURSE_ALUMN_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `course_evaluation` ADD `domain` int(4) NOT NULL default 1 COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `course_evaluation` ADD KEY `IDX_COURSE_EVALUATION_DOMAIN` (`domain`);
ALTER TABLE `course_evaluation` ADD CONSTRAINT `FK_COURSE_EVALUATION_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `course_instructor` ADD `domain` int(4) NOT NULL default 1 COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `course_instructor` ADD KEY `IDX_COURSE_INSTRUCTOR_DOMAIN` (`domain`);
ALTER TABLE `course_instructor` ADD CONSTRAINT `FK_COURSE_INSTRUCTOR_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `course_level` ADD `domain` int(4) NOT NULL default 1 COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `course_level` ADD KEY `IDX_COURSE_LEVEL_DOMAIN` (`domain`);
ALTER TABLE `course_level` ADD CONSTRAINT `FK_COURSE_LEVEL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `course_observation` ADD `domain` int(4) NOT NULL default 1 COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `course_observation` ADD KEY `IDX_COURSE_OBSERVATION_DOMAIN` (`domain`);
ALTER TABLE `course_observation` ADD CONSTRAINT `FK_COURSE_OBSERVATION_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `course_schedule` ADD `domain` int(4) NOT NULL default 1 COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `course_schedule` ADD KEY `IDX_COURSE_SCHEDULE_DOMAIN` (`domain`);
ALTER TABLE `course_schedule` ADD CONSTRAINT `FK_COURSE_SCHEDULE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `course_subject` ADD `domain` int(4) NOT NULL default 1 COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `course_subject` ADD KEY `IDX_COURSE_SUBJECT_DOMAIN` (`domain`);
ALTER TABLE `course_subject` ADD CONSTRAINT `FK_COURSE_SUBJECT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `evaluation_observation` ADD `domain` int(4) NOT NULL default 1 COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `evaluation_observation` ADD KEY `IDX_EVALUATION_OBSERVATION_DOMAIN` (`domain`);
ALTER TABLE `evaluation_observation` ADD CONSTRAINT `FK_EVALUATION_OBSERVATION_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `mark` ADD `domain` int(4) NOT NULL default 1 COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `mark` ADD KEY `IDX_MARK_DOMAIN` (`domain`);
ALTER TABLE `mark` ADD CONSTRAINT `FK_MARK_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `observation` ADD `domain` int(4) NOT NULL default 1 COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `observation` ADD KEY `IDX_OBSERVATION_DOMAIN` (`domain`);
ALTER TABLE `observation` ADD CONSTRAINT `FK_OBSERVATION_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `qualification` ADD `domain` int(4) NOT NULL default 1 COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `qualification` ADD KEY `IDX_QUALIFICATION_DOMAIN` (`domain`);
ALTER TABLE `qualification` ADD CONSTRAINT `FK_QUALIFICATION_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `quality_skill` ADD `domain` int(4) NOT NULL default 1 COMMENT 'Identificador del Dominio' AFTER `id`;
ALTER TABLE `quality_skill` ADD KEY `IDX_QUALITY_SKILL_DOMAIN` (`domain`);
ALTER TABLE `quality_skill` ADD CONSTRAINT `FK_QUALITY_SKILL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);


UPDATE `db_version` SET `version_number` = '7.1.8';

COMMIT;
