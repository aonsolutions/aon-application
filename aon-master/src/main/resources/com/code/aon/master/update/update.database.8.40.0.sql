# Database: aon_master
# Version: Actualizacion de la version 8.40.0 a la version 8.41.0.
# Created by: girazu
# Creation Date: 15/01/2016 10:25

BEGIN;

ALTER TABLE `invoice_tax` ADD `deductible_percent` double(15,3) DEFAULT '100.000' COMMENT 'Porcentaje de deducibilidad' AFTER `withholding_type`;

ALTER TABLE `fs_model184_detail` ADD `location` varchar(1) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Situacion del inmueble';
ALTER TABLE `fs_model184_detail` ADD `cadasdral_reference` varchar(45) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Referencia catastral';
ALTER TABLE `fs_model184_detail` ADD `staff_expenses` double(15,3) DEFAULT 0 COMMENT 'Gastos de personal';
ALTER TABLE `fs_model184_detail` ADD `asset_acquisition` double(15,3) DEFAULT 0 COMMENT 'Adquisicion a terceros de bienes y servicios';
ALTER TABLE `fs_model184_detail` ADD `tax_deduction` double(15,3) DEFAULT 0 COMMENT 'Tributos fiscalmente deducibles y gastos financieros';
ALTER TABLE `fs_model184_detail` ADD `other_tax_deduction` double(15,3) DEFAULT 0 COMMENT 'Otros gastos fiscalmente deducibles';

ALTER TABLE `amortization` ADD `invest_asset` int(4) DEFAULT NULL COMMENT 'Identificador del Bien afecto' AFTER `comments`;
ALTER TABLE `amortization` ADD KEY `IDX_AMORTIZATION_INVEST_ASSET` (`invest_asset`);
ALTER TABLE `amortization` ADD CONSTRAINT `FK_AMORTIZATION_INVEST_ASSET` FOREIGN KEY (`invest_asset`) REFERENCES `invest_asset` (`id`);

ALTER TABLE `account_entry` ADD `activity` int(4) DEFAULT NULL COMMENT 'Identificador de la Actividad' AFTER `account_period`;
ALTER TABLE `account_entry` ADD KEY `IDX_ACCOUNT_ENTRY_ACTIVITY` (`activity`);
ALTER TABLE `account_entry` ADD CONSTRAINT `FK_ACCOUNT_ENTRY_ACTIVITY` FOREIGN KEY (`activity`) REFERENCES `enterprise_activity` (`id`);

ALTER TABLE `fs_activity` ADD `activity` int(4) DEFAULT NULL COMMENT 'Identificador de la Actividad' AFTER `year`;
ALTER TABLE `fs_activity` ADD KEY `IDX_FS_ACTIVITY_ACTIVITY` (`activity`);
ALTER TABLE `fs_activity` ADD CONSTRAINT `FK_FS_ACTIVITY_ACTIVITY` FOREIGN KEY (`activity`) REFERENCES `enterprise_activity` (`id`);

CREATE TABLE `iae` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `section` varchar(1) collate latin1_spanish_ci NOT NULL COMMENT 'Seccion',
  `epigraph` varchar(8) collate latin1_spanish_ci NOT NULL COMMENT 'Epigrafe',
  `title` varchar(255) collate latin1_spanish_ci NOT NULL COMMENT 'Titulo',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='IAE';

ALTER TABLE `enterprise_activity` ADD `iae` int(4) DEFAULT NULL COMMENT 'Epigrafe IAE' AFTER `enterprise`;
ALTER TABLE `enterprise_activity` ADD KEY `IDX_ENTERPRISE_ACTIVITY_IAE` (`iae`);
ALTER TABLE `enterprise_activity` ADD CONSTRAINT `FK_ENTERPRISE_ACTIVITY_IAE` FOREIGN KEY (`iae`) REFERENCES `iae` (`id`);
ALTER TABLE `enterprise_activity` ADD `surcharge` tinyint(1) default '0' COMMENT 'Indica si la Actividad tiene de recargo de equivalencia' AFTER `cnae2009`;
ALTER TABLE `enterprise_activity` ADD `vat_regime` tinyint(2) default NULL COMMENT 'Regimen de IVA' AFTER `surcharge`;
ALTER TABLE `enterprise_activity` ADD `retention_regime` tinyint(2) default NULL COMMENT 'Regimen de IRPF' AFTER `vat_regime`;
ALTER TABLE `enterprise_activity` ADD `start_date` date default NULL COMMENT 'Fecha de inicio' AFTER `retention_regime`; 
ALTER TABLE `enterprise_activity` ADD `end_date` date default NULL COMMENT 'Fecha de fin' AFTER `start_date`;
ALTER TABLE `enterprise_activity` ADD `prorata` double(5,2) default '100.00' COMMENT 'Porcentaje de prorrata' AFTER `end_date`;
ALTER TABLE `enterprise_activity` ADD `prorata_type` tinyint(1) default '0' COMMENT 'Indica el tipo de prorrata' AFTER `prorata`;

ALTER TABLE `invest_asset` MODIFY `retention_percent` double default '0' COMMENT 'Porcentaje de afectacion de imposicion directa';


UPDATE `db_version` SET `version_number` = '8.41.0';

COMMIT;
