# Database: aon_master
# Version: Actualizacion de la version 7.0.13 a la version 7.0.14.
# Created by: girazu
# Creation Date: 02/05/2012 16:40
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.


BEGIN;

ALTER TABLE `reservation_request` ADD `remarks` varchar(166) collate latin1_spanish_ci default NULL COMMENT 'Observaciones' AFTER `request_counter`;

ALTER TABLE `pos` ADD `initial_amount` double(15,2) default '0.00' COMMENT 'Importe inicial de apertura por defecto';

ALTER TABLE `contact_group_detail` DROP FOREIGN KEY `FK_CONTACT_GROUP_DETAIL_CONTACT_GROUP`;
ALTER TABLE `contact_group_detail` DROP KEY `IDX_CONTACT_GROUP_DETAIL_CONTACT_GROUP`;
ALTER TABLE `contact_group_detail` DROP FOREIGN KEY `FK_CONTACT_GROUP_DETAIL_CONTACT`;
ALTER TABLE `contact_group_detail` DROP KEY `IDX_CONTACT_GROUP_DETAIL_CONTACT`;

ALTER TABLE `contact` DROP FOREIGN KEY `FK_CONTACT_USER`;
ALTER TABLE `contact` DROP KEY `IDX_CONTACT_USER`;
ALTER TABLE `contact` DROP `user_id`;
ALTER TABLE `contact` DROP `displayName`;

ALTER TABLE `contact_group` DROP FOREIGN KEY `FK_CONTACT_GROUP_USER`;
ALTER TABLE `contact_group` DROP KEY `IDX_CONTACT_GROUP_USER`;

RENAME TABLE `contact` TO `contact_data`;

RENAME TABLE `contact_group` TO `contact`;

ALTER TABLE `contact` ADD `contact_data` int(4) default NULL COMMENT 'Identificador de la Información del Contacto';
ALTER TABLE `contact` ADD KEY `IDX_CONTACT_CONTACT_DATA` (`contact_data`);
ALTER TABLE `contact` ADD CONSTRAINT `FK_CONTACT_CONTACT_DATA` FOREIGN KEY (`contact_data`) REFERENCES `contact_data` (`id`);  
ALTER TABLE `contact` ADD KEY `IDX_CONTACT_USER` (`user_id`);
ALTER TABLE `contact` ADD CONSTRAINT `FK_CONTACT_USER` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`);

ALTER TABLE `contact_group_detail` ADD KEY `IDX_CONTACT_DETAIL_CONTACT` (`contact`);
ALTER TABLE `contact_group_detail` ADD CONSTRAINT `FK_CONTACT_DETAIL_CONTACT` FOREIGN KEY (`contact`) REFERENCES `contact` (`id`);
ALTER TABLE `contact_group_detail` ADD KEY `IDX_CONTACT_DETAIL_CONTACT_GROUP` (`contact_group`);
ALTER TABLE `contact_group_detail` ADD CONSTRAINT `FK_CONTACT_DETAIL_CONTACT_GROUP` FOREIGN KEY (`contact_group`) REFERENCES `contact` (`id`);

RENAME TABLE `contact_group_detail` TO `contact_detail`;


UPDATE `db_version` SET `version_number` = '7.0.14';

COMMIT;
