# Database: aon_master
# Version: Actualizacion de la version 7.2.0 a la version 7.2.1.
# Created by: eagirrezabal
# Creation Date: 03/12/2012 20:12
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.

BEGIN;

ALTER TABLE `account` ADD `cost_center` char(32) collate latin1_spanish_ci default NULL COMMENT 'Centro de Costo';

ALTER TABLE `contract` ADD `model` tinyint(2) default NULL COMMENT 'Indica el modelo de codumento del contrato';

UPDATE `db_version` SET `version_number` = '7.2.1';

COMMIT;
