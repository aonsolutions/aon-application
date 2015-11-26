# Database: aon_master
# Version: Actualizacion de la version 7.10.3 a la version 7.10.4.
# Created by: eagirrezabal
# Creation Date: 11/02/2013 11:55
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.

BEGIN;

ALTER TABLE `contract`
ADD `category_description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Categoria o grupo profesional';


UPDATE `db_version` SET `version_number` = '7.10.4';

COMMIT;
