# Database: aon_master
# Version: Actualizacion de la version 7.15.1 a la version 7.15.2.
# Created by: girazu
# Creation Date: 16/04/2013 13:30
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.

BEGIN;

ALTER TABLE `newsletter` ADD `highlightFirst` tinyint(1) default '0' COMMENT 'Indica si el Boletin destaca la primera noticia o no';
ALTER TABLE `newsletter` MODIFY `date` datetime NOT NULL COMMENT 'Fecha del Boletin';

ALTER TABLE `news` MODIFY `init_date` datetime DEFAULT NULL COMMENT 'Fecha Noticia';
ALTER TABLE `news` MODIFY `end_date` datetime DEFAULT NULL COMMENT 'Fecha fin Noticia';


UPDATE `db_version` SET `version_number` = '7.15.2';

COMMIT;
