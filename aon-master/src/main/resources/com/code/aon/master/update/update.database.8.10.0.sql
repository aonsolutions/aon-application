# Database: aon_master
# Version: Actualizacion de la version 8.10.0 a la version 8.10.1.
# Created by: girazu
# Creation Date: 21/11/2014 09:50

BEGIN;

ALTER TABLE `fs_model180_detail` ADD `location` varchar(1) collate latin1_spanish_ci default NULL COMMENT 'Situacion del inmueble';
ALTER TABLE `fs_model180_detail` ADD `cadasdral_reference` varchar(45) collate latin1_spanish_ci default NULL COMMENT 'Refercia catastral';
ALTER TABLE `fs_model180_detail` ADD `street_type` varchar(5) collate latin1_spanish_ci default NULL COMMENT 'Tipo de via';
ALTER TABLE `fs_model180_detail` ADD `street_name` varchar(50) collate latin1_spanish_ci default NULL COMMENT 'Nombre de la via';
ALTER TABLE `fs_model180_detail` ADD `number_type` varchar(3) collate latin1_spanish_ci default NULL COMMENT 'Tipo de numero de via';
ALTER TABLE `fs_model180_detail` ADD `number` varchar(5) collate latin1_spanish_ci default NULL COMMENT 'Numero de via';
ALTER TABLE `fs_model180_detail` ADD `number_suffix` varchar(3) collate latin1_spanish_ci default NULL COMMENT 'Calificador del numero de via';
ALTER TABLE `fs_model180_detail` ADD `block` varchar(3) collate latin1_spanish_ci default NULL COMMENT 'Direccion. Bloque.';
ALTER TABLE `fs_model180_detail` ADD `hall` varchar(3) collate latin1_spanish_ci default NULL COMMENT 'Direccion. Portal.';
ALTER TABLE `fs_model180_detail` ADD `stair` varchar(3) collate latin1_spanish_ci default NULL COMMENT 'Direccion. Escalera.';
ALTER TABLE `fs_model180_detail` ADD `floor` varchar(3) collate latin1_spanish_ci default NULL COMMENT 'Direccion. Planta.';
ALTER TABLE `fs_model180_detail` ADD `door` varchar(3) collate latin1_spanish_ci default NULL COMMENT 'Direccion. Puerta.';
ALTER TABLE `fs_model180_detail` ADD `complement` varchar(40) collate latin1_spanish_ci default NULL COMMENT 'Direccion. Complemento.';
ALTER TABLE `fs_model180_detail` ADD `city` varchar(30) collate latin1_spanish_ci default NULL COMMENT 'Localidad o poblacion.';
ALTER TABLE `fs_model180_detail` ADD `town` varchar(30) collate latin1_spanish_ci default NULL COMMENT 'Municipio.';
ALTER TABLE `fs_model180_detail` ADD `town_code` varchar(5) collate latin1_spanish_ci default NULL COMMENT 'Codigo de municipio..';
ALTER TABLE `fs_model180_detail` ADD `province_code` varchar(2) collate latin1_spanish_ci default NULL COMMENT 'Codigo de provincia.';
ALTER TABLE `fs_model180_detail` ADD `zip` varchar(5) collate latin1_spanish_ci default NULL COMMENT 'Codigo postal.';

UPDATE `db_version` SET `version_number` = '8.10.1';

COMMIT;
