# Database: aon_master
# Version: Actualizacion de la version 6.21.1 a la version 6.22.0.
# Created by: girazu
# Creation Date: 27/01/2012 11:20
# Comentarios: CREACION DE LA TABLA DOMAIN Y EL CAMPO DOMAIN EN TODAS LAS TABLAS.


BEGIN;

ALTER TABLE `fs_vat` ADD `prorata` double(5,2) default '100.00' COMMENT 'Porcentaje de prorrata'; 


UPDATE `db_version` SET `version_number` = '6.22.0';

COMMIT;
