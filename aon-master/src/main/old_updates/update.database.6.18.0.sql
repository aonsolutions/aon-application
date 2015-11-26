# Database: aon_master
# Version: Actualizacion de la version 6.18.0 a la version 6.18.1.
# Created by: girazu
# Creation Date: 10/10/2011 18:30
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



BEGIN;

#
# TABLE `campaign_type`
#

CREATE TABLE `campaign_type` (
 `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
 `enterprise` int(4) NOT NULL COMMENT 'Identificador de la Empresa',
 `description` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion',
 `active` tinyint(1) NOT NULL default '0' COMMENT 'Activo si o no',
 PRIMARY KEY (`id`),
 KEY `IDX_CAMPAIGN_TYPE_ENTERPRISE` (`enterprise`),
 CONSTRAINT `FK_CAMPAIGN_TYPE_ENTERPRISE` FOREIGN KEY (`enterprise`) REFERENCES `enterprise` (`registry`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Tipos de Campañas';

#
# TABLE `campaign`
#

ALTER TABLE `campaign` DROP FOREIGN KEY `campaign_ibfk_2`;
ALTER TABLE `campaign` DROP INDEX `activity_type`;
ALTER TABLE `campaign` DROP `activity_type`;
ALTER TABLE `campaign` ADD `enterprise` int(4) default NULL COMMENT 'Identificador de la Empresa' AFTER `id`;
UPDATE `campaign` SET `enterprise` = (SELECT MIN(`registry`) FROM `company`);
ALTER TABLE `campaign` MODIFY `enterprise` int(4) NOT NULL COMMENT 'Identificador de la Empresa';
ALTER TABLE `campaign` ADD `campaign_type` int(4) default NULL COMMENT 'Identificador del Tipo de Campaña' AFTER `enterprise`;
ALTER TABLE `campaign` CHANGE `type` `manual` tinyint(1) NOT NULL default '0' COMMENT 'Tipo de Campaña';
ALTER TABLE `campaign` ADD KEY `IDX_CAMPAIGN_ENTERPRISE` (`enterprise`);
ALTER TABLE `campaign` ADD CONSTRAINT `FK_CAMPAIGN_ENTERPRISE` FOREIGN KEY (`enterprise`) REFERENCES `enterprise` (`registry`);
ALTER TABLE `campaign` ADD KEY `IDX_CAMPAIGN_CAMPAIGN_TYPE` (`campaign_type`);
ALTER TABLE `campaign` ADD CONSTRAINT `FK_CAMPAIGN_CAMPAIGN_TYPE` FOREIGN KEY (`campaign_type`) REFERENCES `campaign_type` (`id`);

#
# TABLE `process`
#

ALTER TABLE `process` ADD `enterprise` int(4) default NULL COMMENT 'Identificador de la Empresa' AFTER `id`;
UPDATE `process` SET `enterprise` = (SELECT MIN(`registry`) FROM `company`);
ALTER TABLE `process` MODIFY `enterprise` int(4) NOT NULL COMMENT 'Identificador de la Empresa';
ALTER TABLE `process` ADD KEY `IDX_PROCESS_ENTERPRISE` (`enterprise`);
ALTER TABLE `process` ADD CONSTRAINT `FK_PROCESS_ENTERPRISE` FOREIGN KEY (`enterprise`) REFERENCES `enterprise` (`registry`);
ALTER TABLE `process` CHANGE `status` `active` tinyint(1) NOT NULL default '1' COMMENT 'Activo si o no';
UPDATE `process` set `active` = IF(`active` = 0,1,0);

#
# TABLE `workgroup`
#

ALTER TABLE `workgroup` ADD `enterprise` int(4) default NULL COMMENT 'Identificador de la Empresa' AFTER `id`;
UPDATE `workgroup` SET `enterprise` = (SELECT MIN(`registry`) FROM `company`);
ALTER TABLE `workgroup` ADD KEY `IDX_WORKGROUP_ENTERPRISE` (`enterprise`);
ALTER TABLE `workgroup` ADD CONSTRAINT `FK_WORKGROUP_ENTERPRISE` FOREIGN KEY (`enterprise`) REFERENCES `enterprise` (`registry`);

#
# TABLE `task_holder`
#

ALTER TABLE `task_holder` ADD `enterprise` int(4) default NULL AFTER `registry`;
UPDATE `task_holder` SET `enterprise` = (SELECT MIN(`registry`) FROM `company`);
ALTER TABLE `task_holder` MODIFY `enterprise` int(4) NOT NULL COMMENT 'Identificador de la Empresa';
ALTER TABLE `task_holder` ADD KEY `IDX_TASK_HOLDER_ENTERPRISE` (`enterprise`);
ALTER TABLE `task_holder` ADD CONSTRAINT `FK_TASK_HOLDER_ENTERPRISE` FOREIGN KEY (`enterprise`) REFERENCES `enterprise` (`registry`);
INSERT INTO registry(`document`,`name`,`alias`,`type`) SELECT `id`,`name`,'###{@$&}###',0 FROM `user` WHERE (`id` IN (SELECT `user_id` FROM `task`) OR `id` IN (SELECT `user_id` FROM `daily_tracking`));
ALTER TABLE `task_holder` ADD `user_id` int(4) default NULL COMMENT 'Identificador del Usuario';
INSERT INTO `task_holder` (`registry`,`enterprise`,`user_id`) (SELECT `id`,(SELECT MIN(`registry`) FROM `company`),`document` FROM registry WHERE `alias` = '###{@$&}###');
UPDATE `registry` SET `alias` = null, `document` = null WHERE `alias` = '###{@$&}###';
ALTER TABLE `task_holder` ADD KEY `IDX_TASK_HOLDER_USER` (`user_id`);
ALTER TABLE `task_holder` ADD CONSTRAINT `FK_TASK_HOLDER_USER` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`);
UPDATE `task_holder` SET `active` = (SELECT `active` FROM `user` WHERE `user`.`id` = `task_holder`.`user_id`);

#
# TABLE `task_holder_workgroup`
#

CREATE TABLE `task_holder_workgroup` (
 `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico' ,
 `task_holder` int(4) NOT NULL COMMENT 'Identificador del Responsable de la Tarea' ,
 `workgroup` int(4) NOT NULL COMMENT 'Identificador del Grupo de Trabajo' ,
 PRIMARY KEY (`id`) ,
 KEY `IDX_TASK_HOLDER_WORKGROUP_TASK_HOLDER` (`task_holder`),
 KEY `IDX_TASK_HOLDER_WORKGROUP_WORKGROUP` (`workgroup`),
 CONSTRAINT `FK_TASK_HOLDER_WORKGROUP_TASK_HOLDER` FOREIGN KEY (`task_holder`) REFERENCES `task_holder` (`registry`),
 CONSTRAINT `FK_TASK_HOLDER_WORKGROUP_WORKGROUP` FOREIGN KEY (`workgroup`) REFERENCES `workgroup` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT = 'Relacion entre Usuarios y Grupos de Trabajo';

INSERT INTO `task_holder_workgroup` (`task_holder`,`workgroup`)
	SELECT `task_holder`.`registry`,`user_workgroup`.`workgroup` 
		FROM `user_workgroup`,`task_holder`
		WHERE `user_workgroup`.`user_id` = `task_holder`.`user_id`;

#
# TABLE `process_detail`
#

ALTER TABLE `process_detail` CHANGE `status` `active` tinyint(1) NOT NULL default '1' COMMENT 'Activo si o no';
UPDATE `process_detail` SET `active` = IF(`active` = 0,1,0);

#
# TABLE `project_type`
#
ALTER TABLE `dossier_type` RENAME TO `project_type`;
ALTER TABLE `project_type` ADD `enterprise` int(4) default NULL AFTER `id`;
UPDATE `project_type` SET `enterprise` = (SELECT MIN(`registry`) FROM `company`);
ALTER TABLE `project_type` MODIFY `enterprise` int(4) NOT NULL COMMENT 'Identificador de la Empresa';
ALTER TABLE `project_type` ADD `active` tinyint(1) NOT NULL default 1 COMMENT 'Activo si o no';


#
# TABLE `activity_type`
#
ALTER TABLE `activity_type` DROP FOREIGN KEY `activity_type_fk`;
ALTER TABLE `activity_type` DROP INDEX `dossier_type`;
ALTER TABLE `activity_type` CHANGE `dossier_type` `project_type` int(4) default NULL COMMENT 'Tipo de Proyecto';
ALTER TABLE `activity_type` ADD KEY `IDX_ACT_TYPE_PROJECT_TYPE` (`project_type`);
ALTER TABLE `activity_type` ADD CONSTRAINT `FK_ACT_TYPE_PROJECT_TYPE` FOREIGN KEY (`project_type`) REFERENCES `project_type` (`id`);
ALTER TABLE `activity_type` ADD `active` tinyint(1) NOT NULL default 1 COMMENT 'Activo si o no';

#
# Tabla `project`
#
ALTER TABLE `project` ADD `project_type` int(4) default NULL COMMENT 'Tipo de Proyecto' AFTER `date`;
ALTER TABLE `project` ADD `enterprise` int(4) default NULL AFTER `id`;
UPDATE `project` SET `enterprise` = (SELECT MIN(`registry`) FROM `company`);
ALTER TABLE `project` MODIFY `enterprise` int(4) NOT NULL COMMENT 'Identificador de la Empresa';
ALTER TABLE `project` ADD KEY `IDX_PROJECT_ENTERPRISE` (`enterprise`);
ALTER TABLE `project` ADD CONSTRAINT `FK_PROJECT_ENTERPRISE` FOREIGN KEY (`enterprise`) REFERENCES `enterprise` (`registry`);
ALTER TABLE `project` ADD KEY `IDX_PROJECT_PROJECT_TYPE` (`project_type`); 
ALTER TABLE `project` ADD CONSTRAINT `FK_PROJECT_PROJECT_TYPE` FOREIGN KEY (`project_type`) REFERENCES `project_type` (`id`);
INSERT INTO project(`id`,`enterprise`,`registry`,`name`,`date`,`project_type`,`dossier`,`active`) 
 SELECT `id`,(SELECT MIN(`registry`) FROM `company`),`customer`,`number`,'2000-01-01',`dossier_type`,1,IF(status=0,1,0) FROM dossier;


#
# Tabla `campaign_project`
#
ALTER TABLE `campaign_dossier` DROP FOREIGN KEY `campaign_dossier_fk_1`;
ALTER TABLE `campaign_dossier` DROP FOREIGN KEY `campaign_dossier_fk_2`;
ALTER TABLE `campaign_dossier` DROP INDEX `dossier`;
ALTER TABLE `campaign_dossier` DROP INDEX `campaign`;
ALTER TABLE `campaign_dossier` CHANGE `dossier` `project` int(4) NOT NULL COMMENT 'Identificador del Expediente';
ALTER TABLE `campaign_dossier` ADD KEY `IDX_CMP_PRJ_CAMPAIGN` (`campaign`);
ALTER TABLE `campaign_dossier` ADD CONSTRAINT `FK_CMP_PRJ_CAMPAIGN` FOREIGN KEY (`campaign`) REFERENCES `campaign` (`id`);
ALTER TABLE `campaign_dossier` ADD KEY `IDX_CMP_PRJ_PROJECT` (`project`);
ALTER TABLE `campaign_dossier` ADD CONSTRAINT `FK_CMP_PRJ_PROJECT` FOREIGN KEY (`project`) REFERENCES `project` (`id`);
ALTER TABLE `campaign_dossier` RENAME TO `campaign_project`;


#
# Tabla `process_task`
#
ALTER TABLE `activity_process` DROP FOREIGN KEY `activity_process_fk_1`;
ALTER TABLE `activity_process` DROP FOREIGN KEY `activity_process_fk_3`;
ALTER TABLE `activity_process` DROP FOREIGN KEY `activity_process_fk_4`;
ALTER TABLE `activity_process` DROP FOREIGN KEY `activity_process_fk_2`;
ALTER TABLE `activity_process` DROP INDEX `task`;
ALTER TABLE `activity_process` DROP INDEX `activity`;
ALTER TABLE `activity_process` DROP INDEX `process_detail`;
ALTER TABLE `activity_process` DROP `activity`;
ALTER TABLE `activity_process` MODIFY `campaign` int(4) default NULL COMMENT 'Identificador de la Campaña';
ALTER TABLE `activity_process` ADD KEY `IDX_PROCESS_TASK_CAMPAIGN` (`campaign`);
ALTER TABLE `activity_process` ADD KEY `IDX_PROCESS_TASK_PRC_DET` (`process_detail`);
ALTER TABLE `activity_process` ADD KEY `IDX_PROCESS_TASK_TASK` (`task`);
ALTER TABLE `activity_process` ADD CONSTRAINT `FK_PROCESS_TASK_CAMPAIGN` FOREIGN KEY (`campaign`) REFERENCES `campaign` (`id`);
ALTER TABLE `activity_process` ADD CONSTRAINT `FK_PROCESS_TASK_PRC_DET` FOREIGN KEY (`process_detail`) REFERENCES `process_detail` (`id`);
ALTER TABLE `activity_process` ADD CONSTRAINT `FK_PROCESS_TASK_TASK` FOREIGN KEY (`task`) REFERENCES `task` (`id`);
ALTER TABLE `activity_process` RENAME TO `process_task`;

#
# Tabla `daily_tracking`
#
ALTER TABLE `daily_tracking` DROP FOREIGN KEY `daily_tracking_fk_1`;
ALTER TABLE `daily_tracking` DROP FOREIGN KEY `daily_tracking_fk_2`;
ALTER TABLE `daily_tracking` DROP FOREIGN KEY `daily_tracking_fk_3`;
ALTER TABLE `daily_tracking` DROP FOREIGN KEY `daily_tracking_fk_4`;
ALTER TABLE `daily_tracking` DROP FOREIGN KEY `daily_tracking_fk_5`;
ALTER TABLE `daily_tracking` DROP INDEX `dossier`;
ALTER TABLE `daily_tracking` DROP INDEX `activity`; 
ALTER TABLE `daily_tracking` DROP INDEX `job_type`;
ALTER TABLE `daily_tracking` DROP INDEX `customer`;
ALTER TABLE `daily_tracking` DROP INDEX `user`;
ALTER TABLE `daily_tracking` ADD `task` int(4) default NULL COMMENT 'Identificador de la Tarea que provoca el Parte' AFTER `comments`;
ALTER TABLE `daily_tracking` CHANGE `user_id` `task_holder` int(4) NOT NULL COMMENT 'Identificador del Usuario que realiza el Parte';
ALTER TABLE `daily_tracking` CHANGE `customer` `registry` int(4) default NULL COMMENT 'Identificador del Cliente asociado al Parte';
ALTER TABLE `daily_tracking` CHANGE `dossier` `project` int(4) default NULL COMMENT 'Identificador del Expediente asociado al Parte';
ALTER TABLE `daily_tracking` CHANGE `activity` `activity_type` int(4) default NULL COMMENT 'Identificador de la Actividad asociada al Parte';
UPDATE `daily_tracking` SET `activity_type` = (SELECT `activity_type` FROM `activity` WHERE `activity`.`id` = `daily_tracking`.`activity_type`);
ALTER TABLE `daily_tracking` ADD KEY `IDX_DT_ACT_TYPE` (`activity_type`);
ALTER TABLE `daily_tracking` ADD KEY `IDX_DT_JOB_TYPE` (`job_type`);
ALTER TABLE `daily_tracking` ADD KEY `IDX_DT_PROJECT` (`project`);
ALTER TABLE `daily_tracking` ADD KEY `IDX_DT_REGISTRY` (`registry`);
ALTER TABLE `daily_tracking` ADD KEY `IDX_DT_TASK` (`task`);
ALTER TABLE `daily_tracking` ADD KEY `IDX_DT_TASK_HOLDER` (`task_holder`); 
UPDATE `daily_tracking` SET `task_holder` = (SELECT `registry` FROM `task_holder` WHERE `task_holder`.`user_id` = `daily_tracking`.`task_holder`);
ALTER TABLE `daily_tracking` ADD CONSTRAINT `FK_DT_ACT_TYPE` FOREIGN KEY (`activity_type`) REFERENCES `activity_type` (`id`);
ALTER TABLE `daily_tracking` ADD CONSTRAINT `FK_DT_JOB_TYPE` FOREIGN KEY (`job_type`) REFERENCES `job_type` (`id`);
ALTER TABLE `daily_tracking` ADD CONSTRAINT `FK_DT_PROJECT` FOREIGN KEY (`project`) REFERENCES `project` (`id`);
ALTER TABLE `daily_tracking` ADD CONSTRAINT `FK_DT_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`); 
ALTER TABLE `daily_tracking` ADD CONSTRAINT `FK_DT_TASK` FOREIGN KEY (`task`) REFERENCES `task` (`id`);
ALTER TABLE `daily_tracking` ADD CONSTRAINT `FK_DT_TASK_HOLDER` FOREIGN KEY (`task_holder`) REFERENCES `task_holder` (`registry`);
ALTER TABLE `daily_tracking` ADD `enterprise` int(4) default NULL AFTER `id`;
UPDATE `daily_tracking` set `enterprise` = (SELECT MIN(`registry`) FROM `company`);
ALTER TABLE `daily_tracking` MODIFY `enterprise` int(4) NOT NULL COMMENT 'Identificador de la Empresa';
ALTER TABLE `daily_tracking` ADD KEY `IDX_DT_ENTERPRISE` (`enterprise`);
ALTER TABLE `daily_tracking` ADD CONSTRAINT `FK_DT_ENTERPRISE` FOREIGN KEY (`enterprise`) REFERENCES `enterprise` (`registry`);


#
# Tabla `task`
#
ALTER TABLE `task` DROP FOREIGN KEY `task_fk_5`;
ALTER TABLE `task` DROP FOREIGN KEY `task_fk_4`;
ALTER TABLE `task` DROP FOREIGN KEY `task_fk_3`;
ALTER TABLE `task` DROP FOREIGN KEY `task_fk_2`;
ALTER TABLE `task` DROP FOREIGN KEY `task_fk_1`;
ALTER TABLE `task` DROP INDEX `user`;
ALTER TABLE `task` DROP INDEX `activity`;
ALTER TABLE `task` DROP INDEX `sender`;
ALTER TABLE `task` DROP INDEX `dossier`;
ALTER TABLE `task` DROP INDEX `workgroup`;
ALTER TABLE `task` MODIFY `start_date` date NOT NULL COMMENT 'Fecha de inicio de la Tarea';
ALTER TABLE `task` CHANGE `user_id` `task_holder` int(4) default NULL COMMENT 'Identificador del Usuario asociado a la Tarea';
ALTER TABLE `task` CHANGE `dossier` `project` int(4) default NULL COMMENT 'Identificador del Expediente';
ALTER TABLE `task` CHANGE `activity` `activity_type` int(4) default NULL COMMENT 'Identificador de la Actividad';
ALTER TABLE `task` ADD `enterprise` int(4) default NULL AFTER `id`;
ALTER TABLE `task` ADD `registry` int(4) default NULL AFTER `project`;
UPDATE `task` set 
 `enterprise` = (SELECT MIN(`registry`) FROM `company`),
 `registry` = (SELECT `registry` FROM `project` WHERE `project`.`id` = `task`.`project`),
 `task_holder` = (SELECT `registry` FROM `task_holder` WHERE `task_holder`.`user_id` = `task`.`task_holder`),
 `sender` = (SELECT `registry` FROM `task_holder` WHERE `task_holder`.`user_id` = `task`.`sender`),
 `activity_type` = (SELECT `activity_type` FROM `activity` WHERE `activity`.`id` = `task`.`activity_type`);
ALTER TABLE `task` MODIFY `enterprise` int(4) NOT NULL COMMENT 'Identificador de la Empresa';
ALTER TABLE `task` ADD KEY `IDX_TASK_ACTIVITY_TYPE` (`activity_type`);
ALTER TABLE `task` ADD KEY `IDX_TASK_ENTERPRISE` (`enterprise`);
ALTER TABLE `task` ADD KEY `IDX_TASK_PROJECT` (`project`);
ALTER TABLE `task` ADD KEY `IDX_TASK_REGISTRY` (`registry`);
ALTER TABLE `task` ADD KEY `IDX_TASK_SENDER` (`sender`);
ALTER TABLE `task` ADD KEY `IDX_TASK_TASK_HOLDER` (`task_holder`);
ALTER TABLE `task` ADD KEY `IDX_TASK_WORKGROUP` (`workgroup`);
ALTER TABLE `task` ADD CONSTRAINT `FK_TASK_ACTIVITY_TYPE` FOREIGN KEY (`activity_type`) REFERENCES `activity_type` (`id`);
ALTER TABLE `task` ADD CONSTRAINT `FK_TASK_ENTERPRISE` FOREIGN KEY (`enterprise`) REFERENCES `enterprise` (`registry`);
ALTER TABLE `task` ADD CONSTRAINT `FK_TASK_PROJECT` FOREIGN KEY (`project`) REFERENCES `project` (`id`);
ALTER TABLE `task` ADD CONSTRAINT `FK_TASK_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`);
ALTER TABLE `task` ADD CONSTRAINT `FK_TASK_SENDER` FOREIGN KEY (`sender`) REFERENCES `task_holder` (`registry`);
ALTER TABLE `task` ADD CONSTRAINT `FK_TASK_TASK_HOLDER` FOREIGN KEY (`task_holder`) REFERENCES `task_holder` (`registry`);
ALTER TABLE `task` ADD CONSTRAINT `FK_TASK_WORKGROUP` FOREIGN KEY (`workgroup`) REFERENCES `workgroup` (`id`);

#
# Tabla `project_activity`
#
ALTER TABLE `activity` DROP FOREIGN KEY `activity_fk_3`;
ALTER TABLE `activity` DROP FOREIGN KEY `activity_ibfk_1`;
ALTER TABLE `activity` DROP FOREIGN KEY `activity_ibfk_2`;
ALTER TABLE `activity` DROP INDEX `activity_type`;
ALTER TABLE `activity` DROP INDEX `dossier`;
ALTER TABLE `activity` DROP INDEX `workgroup`;
ALTER TABLE `activity` CHANGE `dossier` `project` int(4) NOT NULL COMMENT 'Identificador del Expendiente';
ALTER TABLE `activity` ADD `active` tinyint(1) NOT NULL default 1 COMMENT 'Activo, si o no';
ALTER TABLE `activity` ADD KEY `IDX_PRJ_ACT_ACTIVITY_TYPE` (`activity_type`);
ALTER TABLE `activity` ADD KEY `IDX_PRJ_ACT_PROJECT` (`project`);
ALTER TABLE `activity` ADD KEY `IDX_PRJ_ACT_WORKGROUP` (`workgroup`);
ALTER TABLE `activity` ADD CONSTRAINT `FK_PRJ_ACT_ACTIVITY_TYPE` FOREIGN KEY (`activity_type`) REFERENCES `activity_type` (`id`);
ALTER TABLE `activity` ADD CONSTRAINT `FK_PRJ_ACT_PROJECT` FOREIGN KEY (`project`) REFERENCES `project` (`id`);
ALTER TABLE `activity` ADD CONSTRAINT `FK_PRJ_ACT_WORKGROUP` FOREIGN KEY (`workgroup`) REFERENCES `workgroup` (`id`);
ALTER TABLE `activity` RENAME TO `project_activity`;


#
# Tabla `project_dossier`
#
ALTER TABLE `dossier` DROP FOREIGN KEY `dossier_ibfk_1`;
ALTER TABLE `dossier` DROP FOREIGN KEY `dossier_ibfk_2`;
ALTER TABLE `dossier` DROP INDEX `dossier_type`;
ALTER TABLE `dossier` DROP `dossier_type`;
ALTER TABLE `dossier` DROP INDEX `number`;
ALTER TABLE `dossier` DROP INDEX `customer`;
ALTER TABLE `dossier` CHANGE `id` `project` int(4) NOT NULL COMMENT 'Identificador unico del Expediente';
ALTER TABLE `dossier` DROP PRIMARY KEY;
ALTER TABLE `dossier` ADD PRIMARY KEY (`project`);
ALTER TABLE `dossier` MODIFY `location` varchar(64) COLLATE 'latin1_spanish_ci' default NULL COMMENT 'Ubicacion del Expediente';
ALTER TABLE `dossier` ADD UNIQUE INDEX `IDX_PRJ_DOSSIER_NUMBER` (`number`);
ALTER TABLE `dossier` ADD KEY `IDX_PRJ_DOSSIER_CUSTOMER` (`customer`);
ALTER TABLE `dossier` ADD KEY `FK_PRJ_DOSSIER_PROJECT` (`project`);
ALTER TABLE `dossier` ADD CONSTRAINT `FK_PRJ_DOSSIER_CUSTOMER` FOREIGN KEY (`customer`) REFERENCES `customer` (`registry`);
ALTER TABLE `dossier` ADD CONSTRAINT `FK_PRJ_DOSSIER_PROJECT` FOREIGN KEY (`project`) REFERENCES `project` (`id`);
ALTER TABLE `dossier` RENAME TO `project_dossier`;


UPDATE `db_version` SET `version_number` = '6.18.1';

COMMIT;
