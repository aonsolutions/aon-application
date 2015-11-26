# Database: aon_master
# Version: Actualizacion de la version 6.18.7 a la version 6.18.8.
# Created by: girazu
# Creation Date: 29/11/2011 18:55
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.


BEGIN;

ALTER TABLE `raddinfo` MODIFY `value` varchar(128) collate latin1_spanish_ci NOT NULL COMMENT 'Valor del atributo adicional';


UPDATE `db_version` SET `version_number` = '6.18.8';

COMMIT;
