# Database: aon_master
# Version: Actualizacion de la version 7.18.0 a la version 7.19.0.
# Created by: girazu
# Creation Date: 19/06/2013 17:25
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.

BEGIN;

ALTER TABLE `fs_model` ADD `document` varchar(9) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'NIF';
ALTER TABLE `fs_model` ADD `surname` varchar(30) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Apellidos';
ALTER TABLE `fs_model` ADD `name` varchar(45) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre';
ALTER TABLE `fs_model` ADD `street_initial` varchar(2) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Sigla via';
ALTER TABLE `fs_model` ADD `street_name` varchar(17) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre de la via publica';
ALTER TABLE `fs_model` ADD `street_number` varchar(4) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de la via publica';
ALTER TABLE `fs_model` ADD `street_stair` varchar(2) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Escalera';
ALTER TABLE `fs_model` ADD `street_floor` varchar(2) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Piso';
ALTER TABLE `fs_model` ADD `street_door` varchar(2) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Puerta';
ALTER TABLE `fs_model` ADD `phone` varchar(9) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Telefono';
ALTER TABLE `fs_model` ADD `town` varchar(20) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Municipio';
ALTER TABLE `fs_model` ADD `province` varchar(15) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Provincia';
ALTER TABLE `fs_model` ADD `zip` varchar(5) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo Postal';
ALTER TABLE `fs_model` ADD `admon_aeat` varchar(5) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo de Administracion AEAT';
ALTER TABLE `fs_model` ADD `contact_person` varchar(100) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Persona de Contacto';
ALTER TABLE `fs_model` ADD `contact_phone` varchar(9) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Telf. Fijo';
ALTER TABLE `fs_model` ADD `contact_cellular` varchar(9) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Telf. Movil';
ALTER TABLE `fs_model` ADD `contact_email` varchar(100) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Email';
ALTER TABLE `fs_model` ADD `creation_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion';
ALTER TABLE `fs_model` ADD `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion';
ALTER TABLE `fs_model` ADD `modification_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion';
ALTER TABLE `fs_model` ADD `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion';


UPDATE `db_version` SET `version_number` = '7.19.0';

COMMIT;
