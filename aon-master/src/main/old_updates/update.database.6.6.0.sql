# Database: aon_master
# Version: Actualizacion de la version 6.6.0 a la version 6.6.1.
# Created by: girazu
# Creation Date: 28/06/2011 11:48
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



BEGIN;

ALTER TABLE `workplace` DROP FOREIGN KEY `FK_WORKPLACE_AGREEMENT`;

ALTER TABLE `workplace` DROP FOREIGN KEY `FK_WORKPLACE_CALENDAR`;

ALTER TABLE `workplace` DROP FOREIGN KEY `FK_WORKPLACE_ENTERPRISE_ACTIVITY`;

ALTER TABLE `workplace` DROP INDEX `IDX_WORKPLACE_CALENDAR`;

ALTER TABLE `workplace` DROP INDEX `IDX_WORKPLACE_AGREEMENT`;

ALTER TABLE `workplace` DROP INDEX `IDX_WORKPLACE_ENTERPRISE_ACTIVITY`;

ALTER TABLE `workplace` DROP COLUMN `agreement`;

ALTER TABLE `workplace` DROP COLUMN `calendar`;

ALTER TABLE `workplace` DROP COLUMN `enterprise_activity`;

CREATE TABLE `payroll_workplace` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `workplace` int(4) NOT NULL COMMENT 'Identificador del Centro de Trabajo',
  `agreement` int(4) default NULL COMMENT 'Identificador del Convenio',
  `enterprise_activity` int(4) default NULL COMMENT 'Identificador de la Actividad',
  `calendar` int(4) default NULL COMMENT 'Identificador del Calendario',
  PRIMARY KEY  (`id`),
  KEY `IDX_PAYROLL_WORKPLACE_WORKPLACE` (`workplace`),
  KEY `IDX_PAYROLL_WORKPLACE_CALENDAR` (`calendar`),
  KEY `IDX_PAYROLL_WORKPLACE_ENTERPRISE_ACTIVITY` (`enterprise_activity`),
  KEY `IDX_PAYROLL_WORKPLACE_AGREEMENT` (`agreement`),
  CONSTRAINT `FK_PAYROLL_WORKPLACE_WORKPLACE` FOREIGN KEY (`workplace`) REFERENCES `workplace` (`id`),
  CONSTRAINT `FK_PAYROLL_WORKPLACE_AGREEMENT` FOREIGN KEY (`agreement`) REFERENCES `agreement` (`id`),
  CONSTRAINT `FK_PAYROLL_WORKPLACE_CALENDAR` FOREIGN KEY (`calendar`) REFERENCES `calendar` (`id`),
  CONSTRAINT `FK_PAYROLL_WORKPLACE_ENTERPRISE_ACTIVITY` FOREIGN KEY (`enterprise_activity`) REFERENCES `enterprise_activity` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Datos laborales del Centro de Trabajo';

ALTER TABLE `rbank` MODIFY `alias` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Alias de la Cuenta Bancaria';

ALTER TABLE `rbank` MODIFY `active` tinyint(1) NOT NULL default '1' COMMENT 'Indica si la Cuenta Bancaria esta activa o no';


UPDATE `db_version` SET `version_number` = '6.6.1';

COMMIT;
