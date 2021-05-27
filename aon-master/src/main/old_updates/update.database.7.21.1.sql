# Database: aon_master
# Version: Actualizacion de la version 7.21.1 a la version 7.22.0.
# Created by: girazu
# Creation Date: 02/09/2013 16:00
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.

BEGIN;

CREATE TABLE `sepe_batch_attach` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico del Archivo Adjunto',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `source_batch` int(4) NOT NULL default '0' COMMENT 'Identificador de la remesa',
  `source_type` tinyint(2) default NULL COMMENT 'Tipo de la remesa',
  `mimeType` tinyint(2) default '0' COMMENT 'Mime Type del Archivo Adjunto',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion del Archivo Adjunto',
  `data` mediumblob COMMENT 'Archivo Adjunto en binario',
  `type` tinyint(2) default NULL COMMENT 'Tipo de Archivo Adjunto',
  `scope` int(4) default NULL COMMENT 'Ambito del Archivo Adjunto',
  `attach_date` date default NULL COMMENT 'Fecha del Archivo Adjunto',
  PRIMARY KEY  (`id`),
  KEY `IDX_SEPE_BATCH_ATTACH_SCOPE` (`scope`),
  KEY `IDX_SEPE_BATCH_ATTACH_DOMAIN` (`domain`),
  CONSTRAINT `FK_SEPE_BATCH_ATTACH_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_SEPE_BATCH_ATTACH_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Archivos Adjuntos de remesas de SEPE';

insert into `sepe_batch_attach` (domain,source_batch,source_type,mimeType,description,data,type,scope,attach_date)
select domain,source_batch,source_type,mimeType,description,data,type,scope,attach_date from payroll_batch_attach where source_type in (3, 4);
update `sepe_batch_attach` set source_type = 0 where source_type = 3;
update `sepe_batch_attach` set source_type = 1 where source_type = 4;
delete from `payroll_batch_attach` where source_type in (3, 4);

ALTER TABLE `contrata_batch` add `type` tinyint(2) default '0' COMMENT 'Tipo de remesa a comunicar al SEPE';
ALTER TABLE `raddress` add `municipality_code` varchar(5) collate latin1_spanish_ci default NULL COMMENT 'Codigo del municipio';

DROP TABLE `certifica2_batch_data`;

ALTER TABLE `contract` drop `document`;
ALTER TABLE `contract` change `status` `sepe_status` tinyint(2) default '0' COMMENT 'Estado de notificacion del contrato al SEPE';
ALTER TABLE `contract` add `ss_status` tinyint(2) default '0' COMMENT 'Estado de notificacion del contrato a la Seguridad Social';

ALTER TABLE `certifica2_batch_detail` drop `enterprise_nif`;
ALTER TABLE `certifica2_batch_detail` drop `ccc`;
ALTER TABLE `certifica2_batch_detail` drop `document`;
ALTER TABLE `certifica2_batch_detail` drop `name`;
ALTER TABLE `certifica2_batch_detail` drop `first_surname`;
ALTER TABLE `certifica2_batch_detail` drop `second_surname`;
ALTER TABLE `certifica2_batch_detail` drop `ss_number`;
ALTER TABLE `certifica2_batch_detail` drop `quote_group`;
ALTER TABLE `certifica2_batch_detail` drop `contract_type`;
ALTER TABLE `certifica2_batch_detail` drop `contract_duration`;
ALTER TABLE `certifica2_batch_detail` drop `contract_duration_indicator`;
ALTER TABLE `certifica2_batch_detail` drop `occupation_code`;
ALTER TABLE `certifica2_batch_detail` drop `public_association_charge`;
ALTER TABLE `certifica2_batch_detail` drop `dedication_percent`;
ALTER TABLE `certifica2_batch_detail` drop `enterprise_start_date`;
ALTER TABLE `certifica2_batch_detail` drop `expire_date`;
ALTER TABLE `certifica2_batch_detail` drop `expire_end_date`;
ALTER TABLE `certifica2_batch_detail` drop `ere`;
ALTER TABLE `certifica2_batch_detail` drop `ere_reduction_percent`;
ALTER TABLE `certifica2_batch_detail` drop `other_reduction_percent`;
ALTER TABLE `certifica2_batch_detail` drop `reduction_cause_code`;
ALTER TABLE `certifica2_batch_detail` drop `salary_period_start_date`;
ALTER TABLE `certifica2_batch_detail` drop `salary_period_end_date`;
ALTER TABLE `certifica2_batch_detail` drop `salary_processing_days`; 


UPDATE `db_version` SET `version_number` = '7.22.0';

COMMIT;
