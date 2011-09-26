# Database: aon_master
# Version: Actualizacion de la version 6.17.0 a la version 6.18.0.
# Created by: rtrepiana
# Creation Date: 08/09/2011 11:00
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



BEGIN;

ALTER TABLE `salary` 
	ADD `total_irpf` double(15,3) NOT NULL default 0.00 COMMENT 'Total retención aplicada ';

UPDATE salary 
	SET total_irpf=(SELECT sum(amount)  FROM  salary_deduction WHERE type=6 AND salary_deduction.salary = salary.id );
	
UPDATE `db_version` SET `version_number` = '6.18.0';

COMMIT;
