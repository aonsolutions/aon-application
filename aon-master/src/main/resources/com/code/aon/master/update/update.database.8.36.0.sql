# Database: aon_master
# Version: Actualizacion de la version 8.36.0 a la version 8.37.0.
# Created by: girazu
# Creation Date: 18/12/2015 11:35

BEGIN;

CREATE TABLE `invest_asset` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `description` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion del Bien',
  `type` tinyint(2) NOT NULL COMMENT 'Tipo de Bien',
  `regime` tinyint(2) NOT NULL COMMENT 'Regimen',
  `start_date` date default NULL COMMENT 'Fecha de alta',
  `end_date` date default NULL COMMENT 'Fecha de baja',
  `vat_percent` double default '0' COMMENT 'Porcentaje de afectacion de IVA',
  `retention_percent` double default '0' COMMENT 'Porcentaje de afectacion de IRPF',
  PRIMARY KEY  (`id`),
  KEY `IDX_INVEST_ASSET_DOMAIN` (`domain`),
  CONSTRAINT `FK_INVEST_ASSET_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Bienes afectos o de inversion';

ALTER TABLE `invoice` ADD `invest_asset` int(4) DEFAULT NULL COMMENT 'Identificador del Bien afecto' AFTER `activity`;
ALTER TABLE `invoice` ADD KEY `IDX_INVOICE_INVEST_ASSET` (`invest_asset`);
ALTER TABLE `invoice` ADD CONSTRAINT `FK_INVOICE_INVEST_ASSET` FOREIGN KEY (`invest_asset`) REFERENCES `invest_asset` (`id`);

ALTER TABLE `invoice_detail` ADD `invest_asset` int(4) DEFAULT NULL COMMENT 'Identificador del Bien afecto' AFTER `invoice`;
ALTER TABLE `invoice_detail` ADD KEY `IDX_INVOICE_DETAIL_INVEST_ASSET` (`invest_asset`);
ALTER TABLE `invoice_detail` ADD CONSTRAINT `FK_INVOICE_DETAIL_INVEST_ASSET` FOREIGN KEY (`invest_asset`) REFERENCES `invest_asset` (`id`);


UPDATE `db_version` SET `version_number` = '8.37.0';

COMMIT;
