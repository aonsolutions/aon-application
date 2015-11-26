# Database: aon_master
# Version: Actualizacion de la version 4.1.0 a la version 4.2.0.
# Created by: girazu
# Creation Date: 01/09/2009 17:07
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



ALTER TABLE `offer` ADD `scope` int(4) default '1' COMMENT 'Ambito del Presupuesto';

ALTER TABLE `offer` MODIFY `scope` int(4) NOT NULL COMMENT 'Ambito del Presupuesto';

ALTER TABLE `offer` ADD KEY `IDX_OFFER_SCOPE` (`scope`);

ALTER TABLE `offer` ADD CONSTRAINT `FK_OFFER_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`);

ALTER TABLE `offer` ADD `number_of_pymnts` smallint(2) default '0' COMMENT 'Numero de Vencimientos';

ALTER TABLE `offer` ADD `days_to_first_pymnt` smallint(2) default '0' COMMENT 'Dias al primer Vencimiento';

ALTER TABLE `offer` ADD `days_between_pymnts` smallint(2) default '0' COMMENT 'Dias entre Vencimientos';

ALTER TABLE `offer` ADD `pymnt_days` varchar(8) collate latin1_spanish_ci default '0' COMMENT 'Dias de pago';

ALTER TABLE `offer` ADD `bank` int(4) default NULL COMMENT 'Identificador de la Entidad Bancaria';

ALTER TABLE `offer` ADD KEY `IDX_OFFER_BANK` (`bank`);

ALTER TABLE `offer` ADD CONSTRAINT `FK_OFFER_BANK` FOREIGN KEY (`bank`) REFERENCES `bank` (`id`);

ALTER TABLE `offer` ADD `bank_account` varchar(30) collate latin1_spanish_ci default NULL COMMENT 'Numero de cuenta en la Entidad Bancaria';

ALTER TABLE `offer_detail` ADD `line` smallint(2) default '1' COMMENT 'Numero de línea del Detalle dentro del Presupuesto' AFTER `offer`;

ALTER TABLE `offer_detail` MODIFY `item` int(4) default NULL COMMENT 'Identificador del Articulo';

ALTER TABLE `offer_detail` MODIFY `description` varchar(1024) collate latin1_spanish_ci default NULL COMMENT 'Descripción del Articulo';

ALTER TABLE `delivery_detail` MODIFY `line` smallint(2) default '0' COMMENT 'Numero de linea del Detalle dentro del Albaran' AFTER `delivery`;

ALTER TABLE `invoice` ADD `scope` int(4) default '1' COMMENT 'Ambito de la Factura';

ALTER TABLE `invoice` MODIFY `scope` int(4) NOT NULL COMMENT 'Ambito de la Factura';

ALTER TABLE `invoice` ADD KEY `IDX_INVOICE_SCOPE` (`scope`);

ALTER TABLE `invoice` ADD CONSTRAINT `FK_INVOICE_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`);

ALTER TABLE `account_period` ADD `status` tinyint(2) default '0' COMMENT 'Estado del Ejercicio';
  
ALTER TABLE `loan` ADD `fee_amount` double(15,3) default '0.000' COMMENT 'Importe de la cuota';

ALTER TABLE `loan` ADD `recurrence` int(4) default '0' COMMENT 'Periodicidad';

ALTER TABLE `loan` ADD `pay_day` int(4) default '1' COMMENT 'Dia de Pago';

CREATE TABLE `account_entry_link` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `account_entry_from` int(4) NOT NULL COMMENT 'Asiento original',
  `account_entry_to` int(4) NOT NULL COMMENT 'Asiento vinculado',
  PRIMARY KEY  (`id`),
  KEY `IDX_ACCOUNT_ENTRY_LINK_FROM` (`account_entry_from`),
  KEY `IDX_ACCOUNT_ENTRY_LINK_TO` (`account_entry_to`),
  CONSTRAINT `FK_ACCOUNT_ENTRY_LINK_FROM` FOREIGN KEY (`account_entry_from`) REFERENCES `account_entry` (`id`),
  CONSTRAINT `FK_ACCOUNT_ENTRY_LINK_TO` FOREIGN KEY (`account_entry_to`) REFERENCES `account_entry` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Relacion entre Asientos Contables';

INSERT IGNORE INTO `account` (`id`, `description`, `alias`, `entryEnabled`, `level`) VALUES 
  ('461','Indemnizaciones',NULL,0,3),
  ('4610','Indemnizaciones',NULL,0,4),
  ('461000000','Indemnizaciones',NULL,1,5);


UPDATE `db_version` SET `version_number` = '4.2.0';

COMMIT;
