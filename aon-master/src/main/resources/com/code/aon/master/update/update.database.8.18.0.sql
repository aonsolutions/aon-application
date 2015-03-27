# Database: aon_master
# Version: Actualizacion de la version 8.18.0 a la version 8.20.0.
# Created by: girazu
# Creation Date: 02/03/2015 11:15

BEGIN;

ALTER TABLE `product` ADD `serializable` tinyint(1) default '0' COMMENT 'Indica si el Producto es serializable' AFTER `inventoriable`;
ALTER TABLE `product` ADD `lotable` tinyint(1) default '0' COMMENT 'Indica si el Producto es loteable' AFTER `serializable`;
ALTER TABLE `product` ADD `manufactured` tinyint(1) default '0' COMMENT 'Indica si el Producto es elaborado' AFTER `type`;

ALTER TABLE `item` ADD `serial_number` varchar(32) COLLATE latin1_spanish_ci default NULL COMMENT 'Numero de serie' AFTER `description`;
ALTER TABLE `item` ADD `serial_date` date default NULL COMMENT 'Fecha de serializacion' AFTER `serial_number`;

ALTER TABLE `user` ADD `allowConcurrent` tinyint(1) default '0' COMMENT 'Indica si el Usuario admite Sesiones concurrentes' AFTER `active`;


UPDATE `db_version` SET `version_number` = '8.20.0';

COMMIT;
