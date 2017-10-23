# Database: aon_master
# Version: Actualizacion de la version 9.10.0 a la version 9.11.0.
# Created by: ecastellano
# Creation Date: 23/10/2017 12:20

BEGIN;

	*****************************************
	** ERROR ( por si se escapa) ************
	*****************************************


# Añadir campos nuevos en la tabla fs_model180 
ALTER TABLE `fs_model180` ADD `creation_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion';
ALTER TABLE `fs_model180` ADD `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion';
ALTER TABLE `fs_model180` ADD `modification_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion';
ALTER TABLE `fs_model180` ADD `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion';

# Añadir campos nuevos en la tabla fs_model184 
ALTER TABLE `fs_model184` ADD `creation_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion';
ALTER TABLE `fs_model184` ADD `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion';
ALTER TABLE `fs_model184` ADD `modification_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion';
ALTER TABLE `fs_model184` ADD `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion';

# Añadir campos nuevos en la tabla fs_model190 
ALTER TABLE `fs_model190` ADD `creation_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion';
ALTER TABLE `fs_model190` ADD `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion';
ALTER TABLE `fs_model190` ADD `modification_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion';
ALTER TABLE `fs_model190` ADD `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion';

# Añadir campos nuevos en la tabla fs_model193 
ALTER TABLE `fs_model193` ADD `creation_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion';
ALTER TABLE `fs_model193` ADD `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion';
ALTER TABLE `fs_model193` ADD `modification_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion';
ALTER TABLE `fs_model193` ADD `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion';

# Añadir campos nuevos en la tabla fs_mod349 y modificar tipo del campo replaced_number
ALTER TABLE `fs_mod349` ADD `document` varchar(9) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'NIF';
ALTER TABLE `fs_mod349` ADD `name` varchar(45) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Nombre';
ALTER TABLE `fs_mod349` ADD `contact_phone` varchar(9) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Telefono Persona de Contacto';  
ALTER TABLE `fs_mod349` ADD `contact_person` varchar(100) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Persona de Contacto';
ALTER TABLE `fs_mod349` ADD `periodicity_change` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Indicador Cambio Periodicidad';
ALTER TABLE `fs_mod349` ADD `creation_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion';
ALTER TABLE `fs_mod349` ADD `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion';
ALTER TABLE `fs_mod349` ADD `modification_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion';
ALTER TABLE `fs_mod349` ADD `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion';
ALTER TABLE `fs_mod349` MODIFY `number` varchar(13) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de declaracion';
ALTER TABLE `fs_mod349` MODIFY `replaced_number` varchar(13) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de declaracion anterior';

# Modificar campos en la tabla fs_mod349_detail
ALTER TABLE `fs_mod349_detail` DROP `rectified_amount`;
ALTER TABLE `fs_mod349_detail` ADD `rectified_amount` double(15,3) NOT NULL DEFAULT '0.000' COMMENT 'Importe rectificado';
    
# Actualizar los campos nuevos    
UPDATE `fs_mod349` 
  SET `document` = (SELECT `document` FROM `enterprise` INNER JOIN `registry` ON `registry`.`id`=`enterprise`.`registry` WHERE `enterprise`.`domain`=`fs_mod349`.`domain`),
      `name` = (SELECT `registry`.`name` FROM `enterprise` INNER JOIN `registry` ON `registry`.`id`=`enterprise`.`registry` WHERE `enterprise`.`domain`=`fs_mod349`.`domain`),
      `contact_person` = (SELECT `value` FROM `app_param` where `app_param`.`domain`=`fs_mod349`.`domain` and app_param.name='FS_CONCTACT_PERSON'),
      `contact_phone` = (SELECT `value` FROM `app_param` where `app_param`.`domain`=`fs_mod349`.`domain` and app_param.name='FS_CONCTACT_PHONE');


UPDATE `db_version` SET `version_number` = '9.11.0';

COMMIT;
