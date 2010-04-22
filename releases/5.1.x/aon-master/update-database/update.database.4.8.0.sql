# Database: aon_master
# Version: Actualizacion de la version 4.8.0 a la version 4.8.1.
# Created by: girazu
# Creation Date: 14/01/2010 16:11
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



BEGIN;

ALTER TABLE `customer` ADD `delivery_grouped` tinyint(1) default '1' COMMENT 'Indica si el Cliente desea agrupar Albaranes en una sola Factura';

ALTER TABLE `customer` ADD `delivery_valuated` tinyint(1) default '1' COMMENT 'Indica si el Cliente desea imprimir el Albaran valorado';


UPDATE `db_version` SET `version_number` = '4.8.1';

COMMIT;
