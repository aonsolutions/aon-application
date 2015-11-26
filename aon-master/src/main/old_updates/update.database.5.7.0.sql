# Database: aon_master
# Version: Actualizacion de la version 5.7.0 a la version 5.7.1.
# Created by: girazu
# Creation Date: 17/12/2010 13:37
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



BEGIN;

ALTER TABLE `course_academicskill` MODIFY `weight` int(4) NOT NULL default '1' COMMENT 'Peso de la Aptitud para calcular la Nota media';

UPDATE `finance` SET `concept` = 
	(SELECT CONCAT(ELT((`type` + 1),"R","E","R","G"),"-",IF(`series` IS NULL OR `series` = "","",CONCAT(`series`,"/")),LPAD(""+`number`,6,"0")) 
		FROM `invoice` WHERE `invoice`.`id` = `finance`.`invoice`)
	WHERE (`rname` IS NULL OR `rname` = '') AND `invoice` IS NOT NULL;

UPDATE `finance` SET `rdocument` = (SELECT `rdocument` FROM `invoice` WHERE `invoice`.`id` = `finance`.`invoice`) 
	WHERE (`rname` IS NULL OR `rname` = '') AND `invoice` IS NOT NULL;

UPDATE `finance` SET `rname` = (SELECT `rname` FROM `invoice` WHERE `invoice`.`id` = `finance`.`invoice`) 
	WHERE (`rname` IS NULL OR `rname` = '') AND `invoice` IS NOT NULL;


UPDATE `db_version` SET `version_number` = '5.7.1';

COMMIT;
