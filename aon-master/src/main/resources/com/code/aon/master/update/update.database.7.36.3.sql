# Version: Upgrade from version 7.36.3 to 7.36.4
# Created by: ecastellano@esferalia.com
# Creation Date: 2/07/2014 

ALTER TABLE `fs_model200_detail` MODIFY `key` varchar(5) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Clave Casilla';

UPDATE `db_version` SET `version_number` = '7.36.4';

