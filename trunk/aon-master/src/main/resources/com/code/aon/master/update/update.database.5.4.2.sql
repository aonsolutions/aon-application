# Database: aon_master
# Version: Actualizacion de la version 5.4.2 a la version 5.5.0.
# Created by: girazu
# Creation Date: 28/09/2010 19:01
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



BEGIN;

ALTER TABLE `fbatch` ADD `security_level` tinyint(2) default '0' COMMENT 'Nivel de seguridad';

ALTER TABLE `amortization` ADD `security_level` tinyint(2) default '0' COMMENT 'Nivel de seguridad';

ALTER TABLE `person` MODIFY `registry` int(4) NOT NULL default '0' COMMENT 'Registro de la Persona';

ALTER TABLE `customer` MODIFY `taxfree` tinyint(1) default '0' COMMENT 'Indica si el Cliente esta exento de Impuestos';

UPDATE `customer` SET `taxfree` = 0 WHERE `taxfree` IS NULL;


UPDATE `db_version` SET `version_number` = '5.5.0';

COMMIT;
