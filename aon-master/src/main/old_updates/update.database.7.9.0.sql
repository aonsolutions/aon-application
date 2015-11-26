# Database: aon_master
# Version: Actualizacion de la version 7.9.0 a la version 7.10.0.
# Created by: girazu
# Creation Date: 25/01/2013 14:40
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.

BEGIN;

ALTER TABLE `customer` ADD `account` int(4) default NULL COMMENT 'Identificador de la Cuenta Contable';
ALTER TABLE `customer` ADD KEY `IDX_CUSTOMER_ACCOUNT` (`account`);
ALTER TABLE `customer` ADD CONSTRAINT `FK_CUSTOMER_ACCOUNT` FOREIGN KEY (`account`) REFERENCES `account` (`id`);
UPDATE `customer` SET `account` = (SELECT MAX(`account`) FROM `customer_account` WHERE `customer` = `customer`.`registry`);

ALTER TABLE `creditor` ADD `account` int(4) default NULL COMMENT 'Identificador de la Cuenta Contable';
ALTER TABLE `creditor` ADD KEY `IDX_CREDITOR_ACCOUNT` (`account`);
ALTER TABLE `creditor` ADD CONSTRAINT `FK_CREDITOR_ACCOUNT` FOREIGN KEY (`account`) REFERENCES `account` (`id`);
UPDATE `creditor` SET `account` = (SELECT MAX(`account`) FROM `creditor_account` WHERE `creditor` = `creditor`.`registry`);

ALTER TABLE `supplier` ADD `account` int(4) default NULL COMMENT 'Identificador de la Cuenta Contable';
ALTER TABLE `supplier` ADD KEY `IDX_SUPPLIER_ACCOUNT` (`account`);
ALTER TABLE `supplier` ADD CONSTRAINT `FK_SUPPLIER_ACCOUNT` FOREIGN KEY (`account`) REFERENCES `account` (`id`);
UPDATE `supplier` SET `account` = (SELECT MAX(`account`) FROM `supplier_account` WHERE `supplier` = `supplier`.`registry`);

ALTER TABLE `loan` ADD `account` int(4) default NULL COMMENT 'Identificador de la Cuenta Contable';
ALTER TABLE `loan` ADD KEY `IDX_LOAN_ACCOUNT` (`account`);
ALTER TABLE `loan` ADD CONSTRAINT `FK_LOAN_ACCOUNT` FOREIGN KEY (`account`) REFERENCES `account` (`id`);
UPDATE `loan` SET `account` = (SELECT MAX(`account`) FROM `loan_account` WHERE `loan` = `loan`.`id`);

ALTER TABLE `pm_type_detail` ADD `account` int(4) default NULL COMMENT 'Identificador de la Cuenta Contable';
ALTER TABLE `pm_type_detail` ADD KEY `IDX_PM_TYPE_DETAIL_ACCOUNT` (`account`);
ALTER TABLE `pm_type_detail` ADD CONSTRAINT `FK_PM_TYPE_DETAIL_ACCOUNT` FOREIGN KEY (`account`) REFERENCES `account` (`id`);
UPDATE `pm_type_detail` SET `account` = (SELECT MAX(`account`) FROM `pm_type_detail_account` WHERE `pm_type_detail` = `pm_type_detail`.`id`);

ALTER TABLE `rbank` ADD `account` int(4) default NULL COMMENT 'Identificador de la Cuenta Contable';
ALTER TABLE `rbank` ADD KEY `IDX_RBANK_ACCOUNT` (`account`);
ALTER TABLE `rbank` ADD CONSTRAINT `FK_RBANK_ACCOUNT` FOREIGN KEY (`account`) REFERENCES `account` (`id`);
UPDATE `rbank` SET `account` = (SELECT MAX(`account`) FROM `rbank_account` WHERE `rbank` = `rbank`.`id`);

DROP TABLE `customer_account`;
DROP TABLE `creditor_account`;
DROP TABLE `supplier_account`;
DROP TABLE `loan_account`;
DROP TABLE `pm_type_detail_account`;
DROP TABLE `rbank_account`;

UPDATE `account` SET `description` = TRIM(TRAILING ' ' FROM `description`) WHERE SUBSTRING(`description`, -1) = ' ';

CREATE TABLE `contrata_batch` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `date` datetime default NULL COMMENT 'Fecha de la Remesa',
  `status` tinyint(2) NOT NULL default '0' COMMENT 'Indica el estado de la Remesa',
  PRIMARY KEY  (`id`),
  KEY `IDX_CONTRATA_BATCH_DOMAIN` (`domain`),
  CONSTRAINT `FK_CONTRATA_BATCH_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Remesas de altas de Contratos';

CREATE TABLE `contrata_batch_detail` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `contrata_batch` int(4) NOT NULL COMMENT 'Identificador de la Remesa',
  `contract` int(4) NOT NULL COMMENT 'Identificador del Contrato',
  PRIMARY KEY  (`id`),
  KEY `IDX_CONTRATA_BATCH_DETAIL_DOMAIN` (`domain`),
  KEY `IDX_CONTRATA_BATCH_DETAIL_CONTRACT` (`contract`),
  KEY `IDX_CONTRATA_BATCH_DETAIL_CONTRATA_BATCH` (`contrata_batch`),
  CONSTRAINT `FK_CONTRATA_BATCH_DETAIL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_CONTRATA_BATCH_DETAIL_CONTRACT` FOREIGN KEY (`contract`) REFERENCES `contract` (`id`),
  CONSTRAINT `FK_CONTRATA_BATCH_DETAIL_CONTRATA_BATCH` FOREIGN KEY (`contrata_batch`) REFERENCES `contrata_batch` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalle de Remesas de altas de Contratos';

CREATE TABLE `payroll_batch_attach` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `source_batch` int(4) NOT NULL default '0' COMMENT 'Identificador de la Remesa',
  `source_type` tinyint(2) default NULL COMMENT 'Tipo de la Remesa',
  `mimeType` tinyint(2) default '0' COMMENT 'Mime Type del Archivo Adjunto',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion del Archivo Adjunto',
  `data` mediumblob COMMENT 'Archivo Adjunto en binario',
  `type` tinyint(2) default NULL COMMENT 'Tipo de Archivo Adjunto',
  `scope` int(4) default NULL COMMENT 'Ambito del Archivo Adjunto',
  `attach_date` date default NULL COMMENT 'Fecha del Archivo Adjunto',
  PRIMARY KEY  (`id`),
  KEY `IDX_PAYROLL_BATCH_ATTACH_DOMAIN` (`domain`),
  KEY `IDX_PAYROLL_BATCH_ATTACH_SCOPE` (`scope`),
  CONSTRAINT `FK_PAYROLL_BATCH_ATTACH_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_PAYROLL_BATCH_ATTACH_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Archivos Adjuntos de Remesas de Laboral';

INSERT INTO `payroll_batch_attach` (domain,source_batch,source_type,mimeType,description,data,type,scope,attach_date)
SELECT domain,leave_batch,0,mimeType,description,data,type,scope,attach_date FROM leave_batch_attach;

INSERT INTO `payroll_batch_attach` (domain,source_batch,source_type,mimeType,description,data,type,scope,attach_date)
SELECT domain,fan_batch,1,mimeType,description,data,type,scope,attach_date FROM fan_batch_attach;

INSERT INTO `payroll_batch_attach` (domain,source_batch,source_type,mimeType,description,data,type,scope,attach_date)
SELECT domain,contract_batch,2,mimeType,description,data,type,scope,attach_date FROM contract_batch_attach;

INSERT INTO `payroll_batch_attach` (domain,source_batch,source_type,mimeType,description,data,type,scope,attach_date)
SELECT domain,certifica2_batch,3,mimeType,description,data,type,scope,attach_date FROM certifica2_batch_attach;

DROP TABLE leave_batch_attach;
DROP TABLE fan_batch_attach;
DROP TABLE contract_batch_attach;
DROP TABLE certifica2_batch_attach;


UPDATE `db_version` SET `version_number` = '7.10.0';

COMMIT;
