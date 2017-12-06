# Database: aon_master
# Version: Actualizacion de la version 9.14.0 a la version 9.14.1
# Created by: ecastellano
# Creation Date: 05/12/2017 13:10

BEGIN;

ALTER TABLE `fs_mod349` ADD `diff_enabled` tinyint(1) DEFAULT '1' COMMENT 'Declaracion por diferencia' AFTER `periodicity_change`;
ALTER TABLE `fs_mod349` ADD `contact_mail` varchar(50) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Mail Persona de Contacto' AFTER `contact_phone`;
  
ALTER TABLE `fs_model180` ADD `contact_mail` varchar(50) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Mail Persona de Contacto' AFTER `contact_phone`;

ALTER TABLE `fs_model184` ADD `contact_mail` varchar(50) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Mail Persona de Contacto' AFTER `contact_phone`;

ALTER TABLE `fs_model190` ADD `contact_mail` varchar(50) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Mail Persona de Contacto' AFTER `contact_phone`;

ALTER TABLE `fs_model193` ADD `contact_mail` varchar(50) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Mail Persona de Contacto' AFTER `contact_phone`;

ALTER TABLE `fs_mod347` ADD `document` varchar(9) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'NIF';
ALTER TABLE `fs_mod347` ADD `name` varchar(40) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre';
ALTER TABLE `fs_mod347` ADD `contact_phone` varchar(9) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Telefono Persona de Contacto';  
ALTER TABLE `fs_mod347` ADD `contact_person` varchar(40) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Persona de Contacto';
ALTER TABLE `fs_mod347` ADD `contact_mail` varchar(50) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Mail Persona de Contacto';
ALTER TABLE `fs_mod347` ADD `representative_document` varchar(9) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'NIF Representante Legal';
ALTER TABLE `fs_mod347` ADD `creation_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion';
ALTER TABLE `fs_mod347` ADD `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion';
ALTER TABLE `fs_mod347` ADD `modification_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion';
ALTER TABLE `fs_mod347` ADD `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion';
ALTER TABLE `fs_mod347` MODIFY `number` varchar(13) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de declaracion';
ALTER TABLE `fs_mod347` MODIFY `replaced_number` varchar(13) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de declaracion anterior';

ALTER TABLE `fs_mod347_detail` ADD `representative_document` varchar(9) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'NIF Representante Legal' AFTER `document`;

UPDATE `db_version` SET `version_number` = '9.14.1';

COMMIT;
