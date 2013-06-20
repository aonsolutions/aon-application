# Database: aon_master
# Version: Actualizacion de la version 7.0.12 a la version 7.0.13.
# Created by: girazu
# Creation Date: 26/04/2012 10:40
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.


BEGIN;

ALTER TABLE `action_denied` ADD `domain` int(4) NOT NULL default '0' COMMENT 'Identificador del Dominio' AFTER `id`;
UPDATE `action_denied` SET `action_denied`.`domain` = (SELECT `user`.`domain` FROM `user` WHERE `action_denied`.`user_id` = `user`.`id`);
ALTER TABLE `action_denied` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';
ALTER TABLE `action_denied` ADD KEY `IDX_ACTION_DENIED_DOMAIN` (`domain`);
ALTER TABLE `action_denied` ADD CONSTRAINT `FK_ACTION_DENIED_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);
  
ALTER TABLE `session` ADD `domain` int(4) NOT NULL default '0' COMMENT 'Identificador del Dominio' AFTER `id`;
UPDATE `session` SET `session`.`domain` = (SELECT `user`.`domain` FROM `user` WHERE `session`.`user_id` = `user`.`id`);
ALTER TABLE `session` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';
ALTER TABLE `session` ADD KEY `IDX_SESSION_DOMAIN` (`domain`);
ALTER TABLE `session` ADD CONSTRAINT `FK_SESSION_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

CREATE TABLE `domain_application_module` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain_application` int(4) NOT NULL COMMENT 'Identificador de la Aplicacion del Dominio',
  `module` tinyint(2) NOT NULL COMMENT 'Modulo de la Aplicacion del Dominio',
  PRIMARY KEY  (`id`),
  KEY `IDX_DOMAIN_APPLICATION_MODULE_DOMAIN_APPLICATION` (`domain_application`),
  CONSTRAINT `FK_DOMAIN_APPLICATION_MODULE_DOMAIN_APPLICATION` FOREIGN KEY (`domain_application`) REFERENCES `domain_application` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Modulos de las Aplicaciones del Dominio';

CREATE TABLE `profile_module_denied` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `profile` int(4) NOT NULL COMMENT 'Identificador del Perfil',
  `module` tinyint(2) NOT NULL COMMENT 'Modulo Inhabilitado para el Perfil',
  PRIMARY KEY  (`id`),
  KEY `IDX_PROFILE_MODULE_DENIED_PROFILE` (`profile`),
  CONSTRAINT `FK_PROFILE_MODULE_DENIED_PROFILE` FOREIGN KEY (`profile`) REFERENCES `profile` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Modulos Inhabilitados en el Perfil';


UPDATE `db_version` SET `version_number` = '7.0.13';

COMMIT;
