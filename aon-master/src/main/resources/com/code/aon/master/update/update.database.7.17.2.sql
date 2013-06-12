# Database: aon_master
# Version: Actualizacion de la version 7.17.2 a la version 7.18.0.
# Created by: girazu
# Creation Date: 12/06/2013 18:00
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.

BEGIN;

ALTER TABLE `domain` MODIFY `name` varchar(253) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Nombre del Dominio'; 
ALTER TABLE `domain` MODIFY `owner` varchar(256) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Emails del creador del Dominio';
ALTER TABLE `domain` ADD `expirationDate` date DEFAULT NULL COMMENT 'Fecha de Expiracion del Dominio'; 

ALTER TABLE `mk_action` DROP FOREIGN KEY `FK_MK_ACTION_MK_TEMPLATE`;
ALTER TABLE `mk_action` DROP KEY `IDX_MK_ACTION_MK_TEMPLATE`;
ALTER TABLE `mk_action` DROP `template`;

ALTER TABLE `mk_action` ADD `news` int(4) DEFAULT NULL COMMENT 'Identificador de la Noticia';
ALTER TABLE `mk_action` ADD KEY `IDX_MK_ACTION_NEWS` (`news`);
ALTER TABLE `mk_action` ADD CONSTRAINT `FK_MK_ACTION_NEWS` FOREIGN KEY (`news`) REFERENCES `news` (`id`);


UPDATE `db_version` SET `version_number` = '7.18.0';

COMMIT;
