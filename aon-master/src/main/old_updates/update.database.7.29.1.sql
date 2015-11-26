# Database: aon_master
# Version: Actualizacion de la version 7.29.1 a la version 7.30.0.
# Created by: ecastellano
# Creation Date: 26/02/2014 15:30
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.

BEGIN;

ALTER TABLE `fs_mod347_detail` ADD COLUMN `sheet` VARCHAR(1) CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci' NOT NULL DEFAULT 'D' COMMENT 'Tipo de hoja (Declarado o Inmueble)'  AFTER `fs_mod347`; 
ALTER TABLE `fs_mod347_detail` ADD COLUMN `asset_amount` double(15,3) DEFAULT '0.000' COMMENT 'Importe de las operaciones de inmuebles' AFTER `fourth_quarter_amount`;
ALTER TABLE `fs_mod347_detail` ADD COLUMN `asset_first_quarter_amount` double(15,3) DEFAULT '0.000' COMMENT 'Importe de las operaciones de inmuebles del primer trimestre' AFTER `asset_amount`;
ALTER TABLE `fs_mod347_detail` ADD COLUMN `asset_second_quarter_amount` double(15,3) DEFAULT '0.000' COMMENT 'Importe de las operaciones de inmuebles del segundo trimestre' AFTER `asset_first_quarter_amount`;
ALTER TABLE `fs_mod347_detail` ADD COLUMN `asset_third_quarter_amount` double(15,3) DEFAULT '0.000' COMMENT 'Importe de las operaciones de inmuebles del tercer trimestre' AFTER `asset_second_quarter_amount`; 
ALTER TABLE `fs_mod347_detail` ADD COLUMN `asset_fourth_quarter_amount` double(15,3) DEFAULT '0.000' COMMENT 'Importe de las operaciones de inmuebles del cuarto trimestre' AFTER `asset_third_quarter_amount`;
ALTER TABLE `fs_mod347_detail` ADD COLUMN `cash_amount` double(15,3) DEFAULT '0.000' COMMENT 'Importe de las operaciones en metalico' AFTER `asset_fourth_quarter_amount`;
ALTER TABLE `fs_mod347_detail` ADD COLUMN `cash_year` INT NULL COMMENT 'Ejercicio en el que se hubieran declarado las operaciones en metalico.'  AFTER `cash_amount`;
ALTER TABLE `fs_mod347_detail` ADD COLUMN `insurance_operation` TINYINT(1) NULL DEFAULT 0 COMMENT 'Las Entidades Aseguradoras maracaran este campo para identificar las operaciones de seguros'  AFTER `cash_year`; 
ALTER TABLE `fs_mod347_detail` ADD COLUMN `business_premise_rental` TINYINT(1) NULL DEFAULT 0 COMMENT 'Se marcara este campo para operaciones de arrendamiento de locales de negocio,'  AFTER `insurance_operation`; 
ALTER TABLE `fs_mod347_detail` ADD COLUMN `asset_location` VARCHAR(1) CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci' NULL COMMENT 'Situacion del inmueble'  AFTER `business_premise_rental`;
ALTER TABLE `fs_mod347_detail` ADD COLUMN `cadasdral_reference` VARCHAR(45) CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci' NULL COMMENT 'Refercia catastral'  AFTER `asset_location`;
ALTER TABLE `fs_mod347_detail` ADD COLUMN `asset_street_type` VARCHAR(5) CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci' NULL COMMENT 'Tipo de via'  AFTER `cadasdral_reference`;
ALTER TABLE `fs_mod347_detail` ADD COLUMN `asset_street` VARCHAR(50) CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci' NULL COMMENT 'Nombre de la via'  AFTER `asset_street_type`;
ALTER TABLE `fs_mod347_detail` ADD COLUMN `asset_street_number_type` VARCHAR(3) CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci' NULL COMMENT 'Tipo de numero de via'  AFTER `asset_street`; 
ALTER TABLE `fs_mod347_detail` ADD COLUMN `asset_street_number` VARCHAR(5) CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci' NULL COMMENT 'Numero de via'  AFTER `asset_street_number_type`;
ALTER TABLE `fs_mod347_detail` ADD COLUMN `asset_street_number_suffix` VARCHAR(3) CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci' NULL COMMENT 'Calificador del numero de via'  AFTER `asset_street_number`; 
ALTER TABLE `fs_mod347_detail` ADD COLUMN `asset_street_block` VARCHAR(3) CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci' NULL COMMENT 'Direccion. Bloque.'  AFTER `asset_street_number_suffix`;
ALTER TABLE `fs_mod347_detail` ADD COLUMN `asset_street_hall` VARCHAR(3) CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci' NULL COMMENT 'Direccion. Portal.'  AFTER `asset_street_block`;
ALTER TABLE `fs_mod347_detail` ADD COLUMN `asset_street_stair` VARCHAR(3) CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci' NULL COMMENT 'Direccion. Escalera.'  AFTER `asset_street_hall`; 
ALTER TABLE `fs_mod347_detail` ADD COLUMN `asset_street_floor` VARCHAR(3) CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci' NULL COMMENT 'Direccion. Planta.'  AFTER `asset_street_stair`;
ALTER TABLE `fs_mod347_detail` ADD COLUMN `asset_street_door` VARCHAR(3) CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci' NULL COMMENT 'Direccion. Puerta.'  AFTER `asset_street_floor`;
ALTER TABLE `fs_mod347_detail` ADD COLUMN `asset_street_complement` VARCHAR(40) CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci' NULL COMMENT 'Direccion. Complemento.'  AFTER `asset_street_door`; 
ALTER TABLE `fs_mod347_detail` ADD COLUMN `asset_street_city` VARCHAR(30) CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci' NULL COMMENT 'Direccion. Complemento.'  AFTER `asset_street_complement`;
ALTER TABLE `fs_mod347_detail` ADD COLUMN `asset_street_town` VARCHAR(30) CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci' NULL COMMENT 'Direccion. Complemento.'  AFTER `asset_street_city`;
ALTER TABLE `fs_mod347_detail` ADD COLUMN `asset_street_town_code` VARCHAR(5) CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci' NULL COMMENT 'Direccion. Complemento.'  AFTER `asset_street_town`; 
ALTER TABLE `fs_mod347_detail` ADD COLUMN `asset_street_province` VARCHAR(2) CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci' NULL COMMENT 'Direccion. Complemento.'  AFTER `asset_street_town_code`; 
ALTER TABLE `fs_mod347_detail` ADD COLUMN `asset_street_zip` VARCHAR(5) CHARACTER SET 'latin1' COLLATE 'latin1_spanish_ci' NULL COMMENT 'Direccion. Complemento.'  AFTER `asset_street_province`;

UPDATE `db_version` SET `version_number` = '7.30.0';

COMMIT;
