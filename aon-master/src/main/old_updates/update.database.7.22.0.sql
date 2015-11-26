# Database: aon_master
# Version: Actualizacion de la version 7.22.0 a la version 7.22.1.
# Created by: rtrepiana
# Creation Date: 04/09/2013 17:00
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.

BEGIN;


ALTER TABLE `salary_data` modify `name` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Nombre';

ALTER TABLE `salary_data` add `salary` int(4) NOT NULL COMMENT 'Recibo del pago de salarios';
ALTER TABLE `salary_data` ADD KEY `IDX_SALARY_DATA_SALARY` (`salary`); 
ALTER TABLE `salary_data` ADD CONSTRAINT `FK_SALARY_DATA_SALARY` FOREIGN KEY (`salary`) REFERENCES `salary`(`id`); 


UPDATE `db_version` SET `version_number` = '7.22.1';

COMMIT;
