# Database: aon_master
# Version: Actualizacion de la version 1.3.0 a la version 1.3.1
# Created by: girazu
# Creation Date: 10/03/2008 12:25
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



ALTER TABLE `account_summary` drop `entry_month`;

ALTER TABLE `account_summary` ADD `security_level` tinyint(2) default '0' COMMENT 'Nivel de seguridad del Acumulado' AFTER `account`;

ALTER TABLE `account_entry` MODIFY `security_level` tinyint(2) default '0' COMMENT 'Nivel de seguridad del Asiento';

UPDATE `account_entry` SET `security_level` = 0 WHERE `security_level` IS NULL;

ALTER TABLE `finance` MODIFY `security_level` tinyint(2) default '0' COMMENT 'Nivel de seguridad del Vencimiento';

UPDATE `finance` SET `security_level` = 0 WHERE `security_level` IS NULL;

ALTER TABLE `customer_fee` MODIFY `security_level` tinyint(2) default '0' COMMENT 'Nivel de seguridad de la Cuota';

UPDATE `customer_fee` SET `security_level` = 0 WHERE `security_level` IS NULL;

ALTER TABLE `income` MODIFY `security_level` tinyint(2) default '0' COMMENT 'Nivel de seguridad del Albaran';

UPDATE `income` SET `security_level` = 0 WHERE `security_level` IS NULL;

ALTER TABLE `purchase` MODIFY `security_level` tinyint(2) default '0' COMMENT 'Nivel de seguridad del Pedido';

UPDATE `purchase` SET `security_level` = 0 WHERE `security_level` IS NULL;

ALTER TABLE `leasing` drop `interest`;

ALTER TABLE `leasing` drop `expenses`;

ALTER TABLE `leasing` ADD `security_level` tinyint(2) default '0' COMMENT 'Nivel de Seguridad';

ALTER TABLE `leasing` ADD `fixed_asset_account` char(12) collate latin1_spanish_ci NOT NULL COMMENT 'Cuenta de inmobilizado';

ALTER TABLE `leasing` ADD `vat` int(4) NOT NULL COMMENT 'IVA del leasing';

ALTER TABLE `leasing` ADD KEY `fixed_asset_account` (`fixed_asset_account`);

ALTER TABLE `leasing` ADD CONSTRAINT `leasing_fk1` FOREIGN KEY (`fixed_asset_account`) REFERENCES `account` (`id`);

ALTER TABLE `leasing_account` ADD KEY `leasing` (`leasing`);

ALTER TABLE `leasing_account` ADD CONSTRAINT `leasing_account_fk` FOREIGN KEY (`leasing`) REFERENCES `leasing` (`id`);

ALTER TABLE `leasing_account` ADD KEY `account` (`account`);

ALTER TABLE `leasing_account` ADD CONSTRAINT `leasing_account_fk1` FOREIGN KEY (`account`) REFERENCES `account` (`id`);

ALTER TABLE `loan` ADD `security_level` tinyint(2) default '0' COMMENT 'Nivel de Seguridad';


UPDATE `db_version` SET `version_number` = '1.3.1';

COMMIT;
