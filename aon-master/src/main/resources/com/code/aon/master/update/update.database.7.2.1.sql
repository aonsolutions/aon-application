# Database: aon_master
# Version: Actualizacion de la version 7.2.1 a la version 7.2.2.
# Created by: eagirrezabal
# Creation Date: 04/12/2012 12:58
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.

BEGIN;

ALTER TABLE `contract` ADD `model` tinyint(2) default NULL COMMENT 'Indica el modelo de codumento del contrato';

UPDATE `db_version` SET `version_number` = '7.2.2';

COMMIT;
