# Database: aon_master
# Version: Actualizacion de la version 7.0.3 a la version 7.0.4.
# Created by: girazu
# Creation Date: 25/01/2012 20:45
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.


BEGIN;

ALTER TABLE `proposal` DROP FOREIGN KEY `FK_PROPOSAL_DEPARTMENT`;
ALTER TABLE `proposal` DROP INDEX `IDX_PROPOSAL_DEPARTMENT`;
ALTER TABLE `proposal` CHANGE `department` `workplace_department` int(4) default NULL COMMENT 'Identificador del Departamento';
ALTER TABLE `proposal` ADD KEY `IDX_PROPOSAL_WORKPLACE_DEPARTMENT` (`workplace_department`);
ALTER TABLE `proposal` ADD CONSTRAINT `FK_PROPOSAL_WORKPLACE_DEPARTMENT` FOREIGN KEY (`workplace_department`) REFERENCES `workplace_department` (`id`);


UPDATE `db_version` SET `version_number` = '7.0.4';

COMMIT;
