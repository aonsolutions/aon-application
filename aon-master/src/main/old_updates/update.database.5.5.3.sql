# Database: aon_master
# Version: Actualizacion de la version 5.5.3 a la version 5.5.4.
# Created by: girazu
# Creation Date: 20/10/2010 18:38
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



BEGIN;

ALTER TABLE `finance` ADD `rdocument` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Numero de Documento del Cliente o Proveedor' AFTER `registry`;

ALTER TABLE `finance` ADD `rname` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Nombre completo del Cliente o Proveedor' AFTER `rdocument`;

UPDATE `finance` SET `rdocument` = (SELECT `rdocument` FROM `invoice` WHERE `invoice`.`id` = `finance`.`invoice`) WHERE `invoice` IS NOT NULL;

UPDATE `finance` SET `rname` = (SELECT `rname` FROM `invoice` WHERE `invoice`.`id` = `finance`.`invoice`) WHERE `invoice` IS NOT NULL;

UPDATE `finance` SET `concept` = 
	(SELECT CONCAT(ELT((`type` + 1),"R","E","R","G"),"-",IF(`series` IS NULL OR `series` = "","",CONCAT(`series`,"/")),LPAD(""+`number`,6,"0")) 
		FROM `invoice` WHERE `invoice`.`id` = `finance`.`invoice`)
WHERE `invoice` IS NOT NULL;

ALTER TABLE `finance` MODIFY `concept` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Concepto del Vencimiento';

ALTER TABLE `finance` ADD `scope` int(4) default '1' COMMENT 'Ambito del Vencimiento';

ALTER TABLE `finance` MODIFY `scope` int(4) NOT NULL COMMENT 'Ambito del Vencimiento';

UPDATE `finance` SET `scope` = (SELECT `scope` FROM `invoice` WHERE `invoice`.`id` = `finance`.`invoice`) WHERE `invoice` IS NOT NULL;

ALTER TABLE `finance` ADD KEY `IDX_FINANCE_SCOPE` (`scope`);

ALTER TABLE `finance` ADD CONSTRAINT `FK_FINANCE_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`);

ALTER TABLE `fs_renting` ADD `administration` tinyint(2) default '0' COMMENT 'Administracion' AFTER `period`;

ALTER TABLE `fs_renting_detail` ADD `percent` double(15,3) default '0.000' COMMENT 'Porcentaje de retencion' AFTER `paid_returns`;

ALTER TABLE `fs_renting_detail` ADD `accrual_period` int(4) default '0' COMMENT 'Periodo de devengo' AFTER `account_deposit`;

ALTER TABLE `fs_renting_detail` MODIFY `province` varchar(2) collate latin1_spanish_ci default NULL COMMENT 'Provincia';

UPDATE `invoice` SET `security_level` = 0 WHERE `security_level` IS NULL;

UPDATE `finance` SET `security_level` = 0 WHERE `security_level` IS NULL;

UPDATE `fbatch` SET `security_level` = 0 WHERE `security_level` IS NULL;

UPDATE `amortization` SET `security_level` = 0 WHERE `security_level` IS NULL;


UPDATE `db_version` SET `version_number` = '5.5.4';

COMMIT;
