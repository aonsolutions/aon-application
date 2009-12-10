# Database: aon_cms_master
# Version: Actualizacion de la version 2.0.3 a la version 2.0.4
# Created by: jdiez
# Creation Date: 16/02/2009 11:29
# Comentarios: este script unicamente realiza los cambios necesarios en la estructura de la base de datos AonCMS.
#              Luego depende de cada uno el ir apañando los datos de sus aplicaciones a la nueva estructura.

ALTER TABLE `link_i18n` ADD `description` text collate latin1_spanish_ci COMMENT 'Pequeña descripcion del link';

UPDATE `db_version` SET `version_number` = '2.0.4';

COMMIT;
