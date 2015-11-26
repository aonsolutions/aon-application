# Database: aon_master
# Version: Actualizacion de la version 5.6.1 a la version 5.6.2.
# Created by: girazu
# Creation Date: 19/11/2010 11:21
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



BEGIN;

ALTER TABLE `finance` MODIFY `scope` int(4) NOT NULL default '1' COMMENT 'Ambito del Vencimiento';

UPDATE `finance` SET `concept` = 
	(SELECT CONCAT(ELT((`type` + 1),"R","E","R","G"),"-",IF(`series` IS NULL OR `series` = "","",CONCAT(`series`,"/")),LPAD(""+`number`,6,"0")) 
		FROM `invoice` WHERE `invoice`.`id` = `finance`.`invoice`)
WHERE `rname` IS NULL AND `invoice` IS NOT NULL;

UPDATE `finance` SET `rdocument` = (SELECT `rdocument` FROM `invoice` WHERE `invoice`.`id` = `finance`.`invoice`) 
	WHERE `rname` IS NULL AND `invoice` IS NOT NULL;

UPDATE `finance` SET `rname` = (SELECT `rname` FROM `invoice` WHERE `invoice`.`id` = `finance`.`invoice`) 
	WHERE `rname` IS NULL AND `invoice` IS NOT NULL;


UPDATE `db_version` SET `version_number` = '5.6.2';

COMMIT;
