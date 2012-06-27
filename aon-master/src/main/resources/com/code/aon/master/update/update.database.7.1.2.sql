# Database: aon_master
# Version: Actualizacion de la version 7.1.2 a la version 7.1.3.
# Created by: girazu
# Creation Date: 27/06/2012 16:30
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.


BEGIN;

INSERT INTO `role` VALUES 
  (19,'DocumentManager'),
  (20,'Payroll'),
  (21,'Fiscal');    

INSERT INTO `application_role` VALUES
  (233,28,19),
  (234,28,20),
  (235,28,21);

DELETE `profile_role` FROM `profile_role`, `application_role` WHERE `profile_role`.`application_role` = `application_role`.`id` AND `application_role`.`role` = 1;
DELETE FROM `application_role` WHERE `role` = 1;
DELETE FROM `role` WHERE `role`.`id` = 1;

ALTER TABLE `project_reservation_guest` ADD `codigo_barras` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Codigo de pulsera';

CREATE TABLE `finance_pos` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `finance` int(4) NOT NULL COMMENT 'Identificador de Vencimiento',
  `code` char(16) collate latin1_spanish_ci NOT NULL COMMENT 'Codigo de autorizacion',
  `xml_response` text collate latin1_spanish_ci COMMENT 'XML de respuesta',
  PRIMARY KEY  (`id`),
  KEY `IDX_FINANCE_POS_DOMAIN` (`domain`),
  KEY `IDX_FINANCE_POS_FINANCE` (`finance`),
  CONSTRAINT `FK_FINANCE_POS_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_FINANCE_POS_FINANCE` FOREIGN KEY (`finance`) REFERENCES `finance` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Vencimientos de TPV';

ALTER TABLE `pos` ADD `pin_pad` tinyint(1) NOT NULL default '0' COMMENT 'Indicador de si es un Pin Pad';  
ALTER TABLE `pos` ADD `commerce` varchar(20) collate latin1_spanish_ci default NULL COMMENT 'Clave de firma del comercio';
ALTER TABLE `pos` ADD `signature_password` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Clave de firma del comercio';
ALTER TABLE `pos` ADD `terminal` varchar(4) collate latin1_spanish_ci default NULL COMMENT 'Numero de terminal';
ALTER TABLE `pos` ADD `port_configuration` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Configuracion de puerto';
ALTER TABLE `pos` ADD `pos_version` varchar(8) collate latin1_spanish_ci default NULL COMMENT 'Version actual';

ALTER TABLE `mk_action` ADD `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion de la Accion';

ALTER TABLE `campaign` ADD `scope` int(4) NOT NULL default 1 COMMENT 'Identificador del Ambito';
ALTER TABLE `campaign` MODIFY `scope` int(4) NOT NULL COMMENT 'Identificador del Ambito';
ALTER TABLE `campaign` ADD KEY `IDX_CAMPAIGN_SCOPE` (`scope`);
ALTER TABLE `campaign` ADD CONSTRAINT `FK_CAMPAIGN_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`);

ALTER TABLE `action_entry` ADD `domain` int(4) NOT NULL default 1 COMMENT 'Identificador del Dominio' AFTER `id`;
UPDATE `action_entry`, `session` SET `action_entry`.`domain` = `session`.`domain` WHERE `action_entry`.`session_id` = `session`.`id`;
ALTER TABLE `action_entry` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';
ALTER TABLE `action_entry` ADD KEY `IDX_ACTION_ENTRY_DOMAIN` (`domain`);
ALTER TABLE `action_entry` ADD CONSTRAINT `FK_ACTION_ENTRY_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `action_favorite` ADD `domain` int(4) NOT NULL default 1 COMMENT 'Identificador del Dominio' AFTER `id`;
UPDATE `action_favorite`, `user` SET `action_favorite`.`domain` = `user`.`domain` WHERE `action_favorite`.`user_id` = `user`.`id`;
ALTER TABLE `action_favorite` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';
ALTER TABLE `action_favorite` ADD KEY `IDX_ACTION_FAVORITE_DOMAIN` (`domain`);
ALTER TABLE `action_favorite` ADD CONSTRAINT `FK_ACTION_FAVORITE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `application_user` ADD `domain` int(4) NOT NULL default 1 COMMENT 'Identificador del Dominio' AFTER `id`;
UPDATE `application_user`, `domain_application` SET `application_user`.`domain` = `domain_application`.`domain` WHERE `application_user`.`domain_application` = `domain_application`.`id`;
ALTER TABLE `application_user` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';
ALTER TABLE `application_user` ADD KEY `IDX_APPLICATION_USER_DOMAIN` (`domain`);
ALTER TABLE `application_user` ADD CONSTRAINT `FK_APPLICATION_USER_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `application_user_profile` ADD `domain` int(4) NOT NULL default 1 COMMENT 'Identificador del Dominio' AFTER `id`;
UPDATE `application_user_profile`, `application_user` SET `application_user_profile`.`domain` = `application_user`.`domain` WHERE `application_user_profile`.`application_user` = `application_user`.`id`;
ALTER TABLE `application_user_profile` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';
ALTER TABLE `application_user_profile` ADD KEY `IDX_APPLICATION_USER_PROFILE_DOMAIN` (`domain`);
ALTER TABLE `application_user_profile` ADD CONSTRAINT `FK_APPLICATION_USER_PROFILE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `domain_application_module` ADD `domain` int(4) NOT NULL default 1 COMMENT 'Identificador del Dominio' AFTER `id`;
UPDATE `domain_application_module`, `domain_application` SET `domain_application_module`.`domain` = `domain_application`.`domain` WHERE `domain_application_module`.`domain_application` = `domain_application`.`id`;
ALTER TABLE `domain_application_module` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';
ALTER TABLE `domain_application_module` ADD KEY `IDX_DOMAIN_APPLICATION_MODULE_DOMAIN` (`domain`);
ALTER TABLE `domain_application_module` ADD CONSTRAINT `FK_DOMAIN_APPLICATION_MODULE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);

ALTER TABLE `profile_module_denied` ADD `domain` int(4) NOT NULL default 1 COMMENT 'Identificador del Dominio' AFTER `id`;
UPDATE `profile_module_denied`, `profile` SET `profile_module_denied`.`domain` = `profile`.`domain` WHERE `profile_module_denied`.`profile` = `profile`.`id`;
ALTER TABLE `profile_module_denied` MODIFY `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio';
ALTER TABLE `profile_module_denied` ADD KEY `IDX_PROFILE_MODULE_DENIED_DOMAIN` (`domain`);
ALTER TABLE `profile_module_denied` ADD CONSTRAINT `FK_PROFILE_MODULE_DENIED_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);


UPDATE `db_version` SET `version_number` = '7.1.3';

COMMIT;
