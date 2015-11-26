# Database: aon_master
# Version: Actualizacion de la version 7.21.0 a la version 7.21.1.
# Created by: girazu
# Creation Date: 10/07/2013 16:30
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.

BEGIN;

ALTER TABLE `project_reservation_service` ADD `meal_plan` tinyint(2) default '0' COMMENT 'Regimen' AFTER `description`;

ALTER TABLE `sales` ADD `purchase_generated` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Indica si se han generado los Pedidos de Compra derivados' AFTER `bank_account`; 

INSERT INTO `role` VALUES (22, 'AccountingManager');
INSERT INTO `application_role` (`id`, `application`, `role`) VALUES (236, 28, 22);
INSERT INTO `profile_role` (`domain`, `profile`, `application_role`) SELECT `domain`, `profile`, 236 FROM `profile_role` WHERE `application_role` = 228;


UPDATE `db_version` SET `version_number` = '7.21.1';

COMMIT;
