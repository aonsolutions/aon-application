# Database: aon_master
# Version: Actualizacion de la version 7.1.5 a la version 7.1.6.
# Created by: girazu
# Creation Date: 25/07/2012 15:00
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.


BEGIN;

ALTER TABLE `finance` ADD `payroll` tinyint(1) default '0' COMMENT 'Indica si el Vencimiento es de Nominas';
ALTER TABLE `finance` ADD `finance_group` int(4) default NULL COMMENT 'Identificador unico del Vencimiento agrupador';
ALTER TABLE `finance` ADD KEY `IDX_FINANCE_FINANCE` (`finance_group`);
ALTER TABLE `finance` ADD CONSTRAINT `FK_FINANCE_FINANCE` FOREIGN KEY (`finance_group`) REFERENCES `finance` (`id`);


UPDATE `db_version` SET `version_number` = '7.1.6';

COMMIT;
