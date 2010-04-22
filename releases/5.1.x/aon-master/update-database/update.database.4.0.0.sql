# Database: aon_master
# Version: Actualizacion de la version 4.0.0 a la version 4.1.0.
# Created by: ecastellano
# Creation Date: 31/07/2009 12:53
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.
#              Como girazu está de vacances, creo éste script. No incluyo invoice_fee.


ALTER TABLE `rmedia` ADD `administrative` tinyint(1) default '1' COMMENT 'Indica si el Contacto es de caracter administrativo';

ALTER TABLE `rmedia` ADD `commercial` tinyint(1) default '1' COMMENT 'Indica si el Contacto es de caracter comercial';

ALTER TABLE `rmedia` ADD `technical` tinyint(1) default '1' COMMENT 'Indica si el Contacto es de caracter tecnico';


UPDATE `db_version` SET `version_number` = '4.1.0';

COMMIT;
