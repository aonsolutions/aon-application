# Database: aon_master
# Version: Actualizacion de la version 5.3.1 a la version 5.3.2.
# Created by: girazu
# Creation Date: 08/07/2010 13:59
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



BEGIN;

ALTER TABLE `workactivity` DROP FOREIGN KEY `workactivity_ibfk_2`;

ALTER TABLE `workactivity` DROP INDEX `calendar`;

ALTER TABLE `workactivity` DROP `calendar`;

ALTER TABLE `workplace` DROP FOREIGN KEY `workplace_ibfk_2`;

ALTER TABLE `workplace` DROP INDEX `calendar`;

ALTER TABLE `workplace` DROP `calendar`;

ALTER TABLE `company` DROP FOREIGN KEY `company_ibfk_1`;

ALTER TABLE `company` DROP INDEX `calendar`;

ALTER TABLE `company` DROP `calendar`;

ALTER TABLE `employee` DROP FOREIGN KEY `employee_ibfk_2`;

ALTER TABLE `employee` DROP INDEX `calendar`;

ALTER TABLE `employee` DROP `calendar`;

ALTER TABLE `workactivity` MODIFY `workplace` int(4) NOT NULL COMMENT 'Identificador del Centro de Trabajo';

ALTER TABLE `workplace` ADD `economicAgreement` tinyint(2) DEFAULT '0' COMMENT 'Concierto Economico del Centro de Trabajo' AFTER `address`;

ALTER TABLE `employee` ADD `workactivity` int(4) default NULL COMMENT 'Identificador de Actividad' AFTER `registry`;

ALTER TABLE `employee` ADD KEY `IDX_EMPLOYEE_WORKACTIVITY` (`workactivity`);

ALTER TABLE `employee` ADD CONSTRAINT `FK_EMPLOYEE_WORKACTIVITY` FOREIGN KEY (`workactivity`) REFERENCES `workactivity` (`id`);

ALTER TABLE `workactivity` ADD `enterpriseCCC` int(4) NOT NULL COMMENT 'Cuenta de Cotización asociada a la Actividad' AFTER `workplace`;

ALTER TABLE `workactivity` ADD KEY `IDX_WORKACTIVITY_ENTERPRISECCC` (`enterpriseCCC`);

ALTER TABLE `workactivity` ADD CONSTRAINT `FK_WORKACTIVITY_ENTERPRISECCC` FOREIGN KEY (`enterpriseCCC`) REFERENCES `enterprise_ccc` (`id`);


UPDATE `db_version` SET `version_number` = '5.3.2';

COMMIT;
