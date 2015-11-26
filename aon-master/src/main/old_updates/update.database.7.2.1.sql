# Database: aon_master
# Version: Actualizacion de la version 7.2.1 a la version 7.5.0.
# Created by: girazu
# Creation Date: 06/12/2012 14:20
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.

BEGIN;

ALTER TABLE `contract` ADD `model` tinyint(2) default NULL COMMENT 'Indica el modelo de documento del contrato';

ALTER TABLE `amortization_type` DROP FOREIGN KEY `FK_AMORTIZATION_TYPE_FIXED_ASSET_ACCOUNT`;
ALTER TABLE `amortization_type` DROP FOREIGN KEY `FK_AMORTIZATION_TYPE_ALLOCATION_ACCOUNT`;
ALTER TABLE `amortization_type` DROP FOREIGN KEY `FK_AMORTIZATION_TYPE_ACCUMULATED_ACCOUNT`;
ALTER TABLE `amortization_type` DROP INDEX `IDX_AMORTIZATION_TYPE_ALLOCATION_ACCOUNT`;
ALTER TABLE `amortization_type` DROP INDEX `IDX_AMORTIZATION_TYPE_ACCUMULATED_ACCOUNT`;
ALTER TABLE `amortization_type` DROP INDEX `IDX_AMORTIZATION_TYPE_FIXED_ASSET_ACCOUNT`;

UPDATE `amortization_type` SET `fixed_asset_account` = (SELECT `code` FROM `account` WHERE `account`.`id` =`fixed_asset_account`);
UPDATE `amortization_type` SET `accumulated_account` = (SELECT `code` FROM `account` WHERE `account`.`id` =`accumulated_account`);
UPDATE `amortization_type` SET `allocation_account` = (SELECT `code` FROM `account` WHERE `account`.`id` =`allocation_account`);

ALTER TABLE `amortization_type` CHANGE COLUMN `fixed_asset_account` `fixed_asset_account` VARCHAR(4) NOT NULL COMMENT 'Cuenta de inmovilizado';
ALTER TABLE `amortization_type` CHANGE COLUMN `accumulated_account` `accumulated_account` VARCHAR(4) NOT NULL COMMENT 'Cuenta de amortizacion acumulada';  
ALTER TABLE `amortization_type` CHANGE COLUMN `allocation_account` `allocation_account` VARCHAR(4) NOT NULL COMMENT 'Cuenta para la dotacion de la amortizacion';  

ALTER TABLE `amortization` DROP FOREIGN KEY `FK_AMORTIZATION_AMORTIZATION_TYPE`;
ALTER TABLE `amortization` DROP INDEX `IDX_AMORTIZATION_AMORTIZATION_TYPE`;
ALTER TABLE `amortization` DROP COLUMN `amortization_type`;

ALTER TABLE `finance` ADD `advance` tinyint(1) default '0' COMMENT 'Indica si el Vencimiento es un anticipo' AFTER `scope`;

ALTER TABLE `domain` CHANGE `documentManagement` `disableDomainManagement` tinyint(1) NOT NULL default '0' COMMENT 'Indica si el Dominio tiene deshabilitado el mantenimiento de Dominios o no';
UPDATE `domain` SET `disableDomainManagement` = 0;


UPDATE `db_version` SET `version_number` = '7.5.0';

COMMIT;
