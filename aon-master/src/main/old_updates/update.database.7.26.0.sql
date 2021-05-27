# Database: aon_master
# Version: Actualizacion de la version 7.26.0 a la version 7.26.1.
# Created by: girazu
# Creation Date: 17/12/2013 13:00
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.

BEGIN;

CREATE TABLE `contract_info` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `contract` int(4) default NULL COMMENT 'Identificador del Contrato',
  `name` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Nombre',
  `expression` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Expresion',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio ',
  `end_date` date default NULL COMMENT 'Fecha de finalizacion',
  `creation_user` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Usuario de creacion',
  `creation_date` datetime default NULL COMMENT 'Fecha de creacion',
  `modification_user` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Usuario de modificacion',
  `modification_date` datetime default NULL COMMENT 'Fecha de modificacion',
  PRIMARY KEY  (`id`),
  KEY `IDX_CONTRACT_INFO_CONTRACT` (`contract`),
  KEY `IDX_CONTRACT_INFO_DOMAIN` (`domain`),
  CONSTRAINT `FK_CONTRACT_INFO_CONTRACT` FOREIGN KEY (`contract`) REFERENCES `contract` (`id`),
  CONSTRAINT `FK_CONTRACT_INFO_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Informacion temporal del Contrato';

INSERT INTO `contract_info` (domain,name,contract,expression,start_date,end_date)
    SELECT domain,name,contract,expression,start_date,end_date FROM contract_data WHERE name IN ('CENTRO_FORMATIVO','CURSO_FORMATIVO','HORARIO_LABORAL','HORARIO_LECTIVO','ID_CONTRATO_SEPE','ID_PRORROGA_SEPE','ID_TRANSFORMACION_SEPE','ENTERPRISE_CLAUSES');

ALTER TABLE `contract_attach` MODIFY `attach_date` datetime default NULL COMMENT 'Fecha del Archivo Adjunto';

ALTER TABLE `contract_batch` ADD `outcome_file` mediumblob COMMENT 'Archivo Adjunto en binario';
ALTER TABLE `contract_batch` ADD `income_file` mediumblob COMMENT 'Archivo respuesta en binario';
ALTER TABLE `contract_batch` CHANGE `red_notify_date` `outcome_file_date` datetime default NULL COMMENT 'Fecha de creacion';
ALTER TABLE `contract_batch` CHANGE `red_notify_id` `communication_id` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Identificador resultante de la comunicacion';
ALTER TABLE `contract_batch` CHANGE `red_response_date` `income_file_date` datetime default NULL COMMENT 'Fecha de respuesta';
ALTER TABLE `contract_batch` MODIFY `date` datetime default NULL COMMENT 'Fecha de creacion';

ALTER TABLE `contrata_batch` ADD `communication_id` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Identificador resultante de la comunicacion';
ALTER TABLE `contrata_batch` ADD `outcome_file` mediumblob COMMENT 'Archivo Adjunto en binario';
ALTER TABLE `contrata_batch` ADD `outcome_file_date` datetime default NULL COMMENT 'Fecha de creacion';
ALTER TABLE `contrata_batch` ADD `income_file` mediumblob COMMENT 'Archivo respuesta en binario';
ALTER TABLE `contrata_batch` ADD `income_file_date` datetime default NULL COMMENT 'Fecha de respuesta';

ALTER TABLE `certifica2_batch` ADD `communication_id` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Identificador resultante de la comunicacion';
ALTER TABLE `certifica2_batch` ADD `income_file` mediumblob COMMENT 'Archivo respuesta en binario';
ALTER TABLE `certifica2_batch` ADD `income_file_date` datetime default NULL COMMENT 'Fecha de respuesta';
ALTER TABLE `certifica2_batch` ADD `outcome_file` mediumblob COMMENT 'Archivo Adjunto en binario';
ALTER TABLE `certifica2_batch` ADD `outcome_file_date` datetime default NULL COMMENT 'Fecha de creacion';
ALTER TABLE `certifica2_batch` MODIFY `date` datetime default NULL COMMENT 'Fecha de la remesa';
ALTER TABLE `certifica2_batch` MODIFY `sign` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Huella digital obtenida de la respuesta';
ALTER TABLE `certifica2_batch` MODIFY `status` int(4) default NULL COMMENT 'Estado de la remesa';

ALTER TABLE `fan_batch` ADD `communication_id` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Identificador resultante de la comunicacion';
ALTER TABLE `fan_batch` ADD `income_file` mediumblob COMMENT 'Archivo respuesta en binario';
ALTER TABLE `fan_batch` ADD `income_file_date` datetime default NULL COMMENT 'Fecha de respuesta';
ALTER TABLE `fan_batch` ADD `outcome_file` mediumblob COMMENT 'Archivo Adjunto en binario';
ALTER TABLE `fan_batch` ADD `outcome_file_date` datetime default NULL COMMENT 'Fecha de creacion';
ALTER TABLE `fan_batch` MODIFY `date` datetime default NULL COMMENT 'Fecha de creacion';

ALTER TABLE `leave_batch` ADD `communication_id` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Identificador resultante de la comunicacion';
ALTER TABLE `leave_batch` ADD `income_file` mediumblob COMMENT 'Archivo respuesta en binario';
ALTER TABLE `leave_batch` ADD `income_file_date` datetime default NULL COMMENT 'Fecha de respuesta';
ALTER TABLE `leave_batch` ADD `outcome_file` mediumblob COMMENT 'Archivo Adjunto en binario';
ALTER TABLE `leave_batch` ADD `outcome_file_date` datetime default NULL COMMENT 'Fecha de creacion';

ALTER TABLE `contrata_batch_detail` ADD `status` tinyint(2) NOT NULL default '0' COMMENT 'Estado de la linea de la remesa';
ALTER TABLE `certifica2_batch_detail` ADD `status` tinyint(2) NOT NULL default '0' COMMENT 'Estado de la linea de la remesa';
ALTER TABLE `contract_batch_detail` ADD `status` tinyint(2) NOT NULL default '0' COMMENT 'Estado de la linea de la remesa'; 


UPDATE `db_version` SET `version_number` = '7.26.1';

COMMIT;
