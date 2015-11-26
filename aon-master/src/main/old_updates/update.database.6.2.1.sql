# Database: aon_master
# Version: Actualizacion de la version 6.2.1 a la version 6.2.2.
# Created by: girazu
# Creation Date: 18/03/2011 14:06
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



BEGIN;

ALTER TABLE `bank_statement_link` ADD `linked_bank_statement_link` int(4) default NULL COMMENT 'Identificador del Enlace de Extracto bancario asociado';

ALTER TABLE `bank_statement_link` ADD KEY `IDX_BANK_STATEMENT_LINK_BANK_STATEMENT_LINK` (`linked_bank_statement_link`);

ALTER TABLE `bank_statement_link` ADD CONSTRAINT `FK_BANK_STATEMENT_LINK_BANK_STATEMENT_LINK` FOREIGN KEY (`linked_bank_statement_link`) REFERENCES `bank_statement_link` (`id`);

CREATE TABLE `contract_leave_detail` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `type` tinyint(2) NOT NULL default '0' COMMENT 'Tipo de parte',
  `contract_leave` int(4) NOT NULL COMMENT 'Contrato',
  `college_number` varchar(8) collate latin1_spanish_ci default NULL COMMENT 'Numero de colegiado',
  `confirm_order` tinyint(2) default NULL COMMENT 'Numero de orden del parte de confirmacion',
  `cias` varchar(11) collate latin1_spanish_ci default NULL COMMENT 'codigo identificacion area sanitaria',
  `date` date NOT NULL COMMENT 'Fecha del parte',
  `processed` tinyint(1) NOT NULL default '0' COMMENT 'Indica si esta procesado',
  PRIMARY KEY  (`id`),
  KEY `IDX_CONTRACT_LEAVE_DETAIL_CONTRACT_LEAVE` (`contract_leave`),
  CONSTRAINT `FK_CONTRACT_LEAVE_DETAIL_CONTRACT_LEAVE` FOREIGN KEY (`contract_leave`) REFERENCES `contract_leave` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalle de las bajas';

ALTER TABLE `contract_leave` ADD `discharge_cause` tinyint(2) default NULL COMMENT 'Causa del alta';

CREATE TABLE `leave_batch` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `date` datetime default NULL COMMENT 'Fecha de la Remesa',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Remesas de partes IT';

CREATE TABLE `leave_batch_detail` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `leave_batch` int(4) NOT NULL COMMENT 'Identificador unico de la remesa',
  `contract_leave_detail` int(4) NOT NULL COMMENT 'Identificador unico del parte',
  PRIMARY KEY  (`id`),
  KEY `IDX_LEAVE_BATCH_DETAIL_LEAVE_BATCH` (`leave_batch`),
  KEY `IDX_LEAVE_BATCH_DETAIL_CONTRACT_LEAVE_DETAIL` (`contract_leave_detail`),
  CONSTRAINT `FK_LEAVE_BATCH_DETAIL_LEAVE_BATCH` FOREIGN KEY (`leave_batch`) REFERENCES `leave_batch` (`id`),
  CONSTRAINT `FK_LEAVE_BATCH_DETAIL_CONTRACT_LEAVE_DETAIL` FOREIGN KEY (`contract_leave_detail`) REFERENCES `contract_leave_detail` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalle de remesas de partes IT';


UPDATE `db_version` SET `version_number` = '6.2.2';

COMMIT;
