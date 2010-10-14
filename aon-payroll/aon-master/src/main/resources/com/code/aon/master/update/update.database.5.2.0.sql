# Database: aon_master
# Version: Actualizacion de la version 5.2.0 a la version 5.2.1.
# Created by: girazu
# Creation Date: 19/05/2010 10:55
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



BEGIN;

DROP TABLE `target_third_party`;

CREATE TABLE `target_supplier` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `target` int(4) NOT NULL COMMENT 'Identificador del Cliente Potencial',
  `supplier` int(4) NOT NULL COMMENT 'Identificador del Proveedor',
  `target_external_code` varchar(15) collate latin1_spanish_ci default NULL COMMENT 'Codigo del Cliente Potencial para el Proveedor',
  `tariff` int(4) default NULL COMMENT 'Identificador de Tarifa',
  `pay_method` int(4) default NULL COMMENT 'Identificador de la Forma de Pago',
  `number_of_pymnts` smallint(2) default '0' COMMENT 'Numero de Vencimientos',
  `days_to_first_pymnt` smallint(2) default '0' COMMENT 'Dias al primer Vencimiento',
  `days_between_pymnts` smallint(2) default '0' COMMENT 'Dias entre Vencimientos',
  `pymnt_days` varchar(8) collate latin1_spanish_ci default '0' COMMENT 'Dias de pago',
  `bank` int(4) default NULL COMMENT 'Identificador de la Entidad Bancaria',
  `bank_account` varchar(30) collate latin1_spanish_ci default NULL COMMENT 'Numero de cuenta en la Entidad Bancaria',
  PRIMARY KEY  (`id`),
  KEY `IDX_TARGET_SUPPLIER_TARGET` (`target`),
  KEY `IDX_TARGET_SUPPLIER_SUPPLIER` (`supplier`),
  KEY `IDX_TARGET_SUPPLIER_TARIFF` (`tariff`),
  KEY `IDX_TARGET_SUPPLIER_PAY_METHOD` (`pay_method`),
  KEY `IDX_TARGET_SUPPLIER_BANK` (`bank`),
  CONSTRAINT `FK_TARGET_SUPPLIER_TARGET` FOREIGN KEY (`target`) REFERENCES `target` (`registry`),
  CONSTRAINT `FK_TARGET_SUPPLIER_SUPPLIER` FOREIGN KEY (`supplier`) REFERENCES `supplier` (`registry`),
  CONSTRAINT `FK_TARGET_SUPPLIER_TARIFF` FOREIGN KEY (`tariff`) REFERENCES `tariff` (`id`),
  CONSTRAINT `FK_TARGET_SUPPLIER_PAY_METHOD` FOREIGN KEY (`pay_method`) REFERENCES `pay_method` (`id`),
  CONSTRAINT `FK_TARGET_SUPPLIER_BANK` FOREIGN KEY (`bank`) REFERENCES `bank` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Relacion de Clientes Potenciales con Proveedores';

ALTER TABLE `offer` DROP FOREIGN KEY `FK_OFFER_THIRD_PARTY`;

ALTER TABLE `offer` DROP INDEX `IDX_OFFER_THIRD_PARTY`;

ALTER TABLE `offer` DROP `third_party`;

ALTER TABLE `offer` ADD `version` smallint(2) NOT NULL default '0' COMMENT 'Numero de version de Presupuesto' AFTER `number`;

ALTER TABLE `offer` ADD `supplier` int(4) default NULL COMMENT 'Identificador del Proveedor' AFTER `seller`;

ALTER TABLE `offer` ADD KEY `IDX_OFFER_SUPPLIER` (`supplier`);

ALTER TABLE `offer` ADD CONSTRAINT `FK_OFFER_SUPPLIER` FOREIGN KEY (`supplier`) REFERENCES `supplier` (`registry`);

ALTER TABLE `offer` DROP KEY `series`;

ALTER TABLE `offer` ADD UNIQUE KEY `series` (`series`,`number`,`version`);

ALTER TABLE `account_entry` ADD `comments` text collate latin1_spanish_ci COMMENT 'Comentarios del Asiento';

ALTER TABLE `rnote` ADD `security_level` tinyint(2) default '0' COMMENT 'Nivel de seguridad de la Nota';

ALTER TABLE `process_detail` ADD `comments` text collate latin1_spanish_ci COMMENT 'Comentarios del Detalle de Proceso';


UPDATE `db_version` SET `version_number` = '5.2.1';

COMMIT;
