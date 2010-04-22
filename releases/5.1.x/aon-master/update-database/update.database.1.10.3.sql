# Database: aon_master
# Version: Actualizacion de la version 1.10.3 a la version 1.10.4
# Created by: girazu
# Creation Date: 20/11/2008 17:12
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



INSERT INTO `geozone` (`name`) VALUES ('ESPAÑA');

UPDATE `geotree` SET `parent` = (SELECT max(`id`) FROM `geozone`) WHERE `parent` = 0;

UPDATE `geotree` SET `child` = (SELECT max(`id`) FROM `geozone`) WHERE `child` = 0;

DELETE FROM `geozone` WHERE `id` = 0;


UPDATE `db_version` SET `version_number` = '1.10.4';

COMMIT;
