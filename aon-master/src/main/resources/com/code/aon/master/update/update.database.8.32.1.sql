# Database: aon_master
# Version: Actualizacion de la version 8.32.1 a la version 8.32.2.
# Created by: girazu
# Creation Date: 26/10/2015 11:35

BEGIN;

ALTER TABLE `hotel` DROP FOREIGN KEY `FK_HOTEL_ITEM_NO_SHOW`;
ALTER TABLE `hotel` DROP KEY `IDX_HOTEL_ITEM_NO_SHOW`;
ALTER TABLE `hotel` DROP `item_no_show`;
ALTER TABLE `hotel` DROP FOREIGN KEY `FK_HOTEL_ITEM_PENALTY`;
ALTER TABLE `hotel` DROP KEY `IDX_HOTEL_ITEM_PENALTY`;
ALTER TABLE `hotel` DROP `item_penalty`;
ALTER TABLE `hotel` DROP FOREIGN KEY `FK_HOTEL_ITEM_ADVANCE`;
ALTER TABLE `hotel` DROP KEY `IDX_HOTEL_ITEM_ADVANCE`;
ALTER TABLE `hotel` DROP `item_advance`;


UPDATE `db_version` SET `version_number` = '8.32.2';

COMMIT;

