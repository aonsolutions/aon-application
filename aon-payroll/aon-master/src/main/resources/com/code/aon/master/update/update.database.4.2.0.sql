# Database: aon_master
# Version: Actualizacion de la version 4.2.0 a la version 4.3.0.
# Created by: girazu
# Creation Date: 16/09/2009 10:07
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



ALTER TABLE `sales` ADD `scope` int(4) NOT NULL default '1' COMMENT 'Ambito del Pedido';

ALTER TABLE `sales` ADD KEY `IDX_SALES_SCOPE` (`scope`);

ALTER TABLE `sales` ADD CONSTRAINT `FK_SALES_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`);

ALTER TABLE `sales` ADD `number_of_pymnts` smallint(2) default '0' COMMENT 'Numero de Vencimientos';

ALTER TABLE `sales` ADD `days_to_first_pymnt` smallint(2) default '0' COMMENT 'Dias al primer Vencimiento';

ALTER TABLE `sales` ADD `days_between_pymnts` smallint(2) default '0' COMMENT 'Dias entre Vencimientos';

ALTER TABLE `sales` ADD `pymnt_days` varchar(8) collate latin1_spanish_ci default '0' COMMENT 'Dias de pago';

ALTER TABLE `sales` ADD `bank` int(4) default NULL COMMENT 'Identificador de la Entidad Bancaria';

ALTER TABLE `sales` ADD KEY `IDX_SALES_BANK` (`bank`);

ALTER TABLE `sales` ADD CONSTRAINT `FK_SALES_BANK` FOREIGN KEY (`bank`) REFERENCES `bank` (`id`);

ALTER TABLE `sales` ADD `bank_account` varchar(30) collate latin1_spanish_ci default NULL COMMENT 'Numero de cuenta en la Entidad Bancaria';

ALTER TABLE `sales_detail` MODIFY `line` smallint(2) default '1' COMMENT 'Numero de línea del Detalle dentro del Pedido';

ALTER TABLE `sales_detail` MODIFY `description` varchar(1024) collate latin1_spanish_ci default NULL COMMENT 'Descripcion del Detalle de Pedido';

ALTER TABLE `sales_detail` ADD `source` tinyint(2) default '0' COMMENT 'Origen del Detalle de Pedido';

ALTER TABLE `sales_detail` ADD `offer_detail` int(4) default NULL COMMENT 'Identificador del Detalle del Presupuesto Origen';

ALTER TABLE `sales_detail` ADD KEY `IDX_SALES_DETAIL_OFFER_DETAIL` (`offer_detail`);

ALTER TABLE `sales_detail` ADD CONSTRAINT `FK_SALES_DETAIL_OFFER_DETAIL` FOREIGN KEY (`offer_detail`) REFERENCES `offer_detail` (`id`);

ALTER TABLE `sales_detail` ADD `delivered` double default '0' COMMENT 'Cantidad entregada del Detalle de Pedido';

ALTER TABLE `delivery` ADD `pay_method` int(4) default NULL COMMENT 'Identificador de la Forma de Pago' AFTER `issue_time`;

ALTER TABLE `delivery` ADD `workplace` int(4) default NULL COMMENT 'Identificador del Centro de Trabajo';

ALTER TABLE `delivery` ADD KEY `IDX_DELIVERY_WORKPLACE` (`workplace`);

ALTER TABLE `delivery` ADD CONSTRAINT `FK_DELIVERY_WORKPLACE` FOREIGN KEY (`workplace`) REFERENCES `workplace` (`id`);

ALTER TABLE `delivery` ADD `scope` int(4) NOT NULL default '1' COMMENT 'Ambito del Albaran';

ALTER TABLE `delivery` ADD KEY `IDX_DELIVERY_SCOPE` (`scope`);

ALTER TABLE `delivery` ADD CONSTRAINT `FK_DELIVERY_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`);

ALTER TABLE `delivery` ADD `number_of_pymnts` smallint(2) default '0' COMMENT 'Numero de Vencimientos';

ALTER TABLE `delivery` ADD `days_to_first_pymnt` smallint(2) default '0' COMMENT 'Dias al primer Vencimiento';

ALTER TABLE `delivery` ADD `days_between_pymnts` smallint(2) default '0' COMMENT 'Dias entre Vencimientos';

ALTER TABLE `delivery` ADD `pymnt_days` varchar(8) collate latin1_spanish_ci default '0' COMMENT 'Dias de pago';

ALTER TABLE `delivery` ADD `bank` int(4) default NULL COMMENT 'Identificador de la Entidad Bancaria';

ALTER TABLE `delivery` ADD KEY `IDX_DELIVERY_BANK` (`bank`);

ALTER TABLE `delivery` ADD CONSTRAINT `FK_DELIVERY_BANK` FOREIGN KEY (`bank`) REFERENCES `bank` (`id`);

ALTER TABLE `delivery` ADD `bank_account` varchar(30) collate latin1_spanish_ci default NULL COMMENT 'Numero de cuenta en la Entidad Bancaria';

ALTER TABLE `delivery_detail` MODIFY `delivery` int(4) NOT NULL COMMENT 'Identificador del Albaran de Venta';

ALTER TABLE `delivery_detail` MODIFY `item` int(4) NOT NULL COMMENT 'Identificador del Articulo del Detalle de Albaran';

ALTER TABLE `delivery_detail` MODIFY `warehouse` int(4) NOT NULL COMMENT 'Identificador del Almacen';

ALTER TABLE `delivery_detail` MODIFY `description` varchar(1024) collate latin1_spanish_ci default NULL COMMENT 'Descripcion del Detalle de Albaran';

ALTER TABLE `delivery_detail` ADD `type` tinyint(2) default '0' COMMENT 'Tipo de Detalle de Albaran' AFTER `discount_expr`;

ALTER TABLE `delivery_detail` ADD `source` tinyint(2) default '0' COMMENT 'Origen del Detalle de Albaran' AFTER `type`;

ALTER TABLE `offer` MODIFY `scope` int(4) NOT NULL default '1' COMMENT 'Ambito del Presupuesto';

ALTER TABLE `offer_detail` MODIFY `quantity` double(15,3) default '0' COMMENT 'Cantidad del Articulo';

ALTER TABLE `offer_detail` MODIFY `price` double(15,3) default '0' COMMENT 'Precio del Articulo';

ALTER TABLE `invoice` MODIFY `scope` int(4) NOT NULL default '1' COMMENT 'Ambito de la Factura';

ALTER TABLE `item` ADD `internet` tinyint(1) default '0' COMMENT 'Visible en internet';

CREATE TABLE `ec_target` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `target` int(4) NOT NULL COMMENT 'Identificador del Cliente Potencial',
  `login` varchar(48) collate latin1_spanish_ci NOT NULL COMMENT 'Login del Cliente Potencial',
  `password` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Password del Cliente Potencial',
  `type` tinyint(2) default '0' COMMENT 'Tipo de conexion',
  `last_access` date default NULL COMMENT 'Ultima fecha de conexion',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `IDX_ECTARGET_LOGIN` (`login`),
  KEY `IDX_ECTARGET_TARGET` (`target`),
  CONSTRAINT `FK_ECTARGET_TARGET` FOREIGN KEY (`target`) REFERENCES `target` (`registry`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Clientes Potenciales del ECommerce';

CREATE TABLE `ec_catalogue` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `catalogue` int(4) NOT NULL COMMENT 'Identificador del Catalogo',
	`catalogue_img` mediumblob COMMENT 'Imagen para el Catalogo',
  `type` tinyint(2) default '0' COMMENT 'Tipo de Catalogo',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `IDX_ECCATALOGUE_CATALOGUE` (`catalogue`),
  CONSTRAINT `FK_ECCATALOGUE_CATALOGUE` FOREIGN KEY (`catalogue`) REFERENCES `catalogue` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Catalogos del ECommerce';

CREATE TABLE `ec_config` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `active` tinyint(1) default '0' COMMENT 'Indica si es la configuracion activa',
  `name` varchar(64) collate latin1_spanish_ci NOT NULL default '' COMMENT 'Nombre del Catalogo de internet',
  `skin` tinyint(2) NOT NULL COMMENT 'Tipo de skin a utilizar',
  `header_img` mediumblob COMMENT 'Imagen de cabecera',
  `series` char(5) collate latin1_spanish_ci default NULL COMMENT 'Serie de los presupuestos que se van a grabar',
  `commerce` tinyint(1) default '0' COMMENT 'Indica si el ECommerce permite grabar un presupuesto',
  `show_login` tinyint(2) default '0' COMMENT 'Indica la forma de autenticarse en el ECommerce',
  `price` tinyint(2) default '0' COMMENT 'Indica la forma de mostrar los precios en el Catalogo',
  `tax_in_price` tinyint(2) default '0' COMMENT 'Indica si los precios van a mostrarse con Impuestos incluidos',
  `discount` tinyint(2) default '0' COMMENT 'Indica si adicionalmente se va a mostrar el precio original del Producto',
  `bank_transfer` int(4) default NULL COMMENT 'Forma de pago por transferencia bancaria',
  `cash_on_delivery` int(4) default NULL COMMENT 'Forma de pago por contrarreembolso',
  `visa` int(4) default NULL COMMENT 'Forma de pago con tarjeta',
  `paypal` int(4) default NULL COMMENT 'Forma de pago por paypal',
  `bank_draft` int(4) default NULL COMMENT 'Forma de pago por giro bancario',
  `legal_note1` text collate latin1_spanish_ci COMMENT 'Politica de privacidad',
  `legal_note2` text collate latin1_spanish_ci COMMENT 'Nota legal',
  `legal_note3` text collate latin1_spanish_ci COMMENT 'Proteccion de datos',
  `tariff` int(4) NOT NULL COMMENT 'Identificador de Tarifa para Ecommerce',
  `header_color` varchar(8) collate latin1_spanish_ci default '' COMMENT 'Color del background del header',
  `telephone` varchar(12) collate latin1_spanish_ci default '' COMMENT 'Telefono de contacto',
  `row_items` tinyint(2) default '2' COMMENT 'Numero de articulos por fila',
  `left_banner` mediumblob COMMENT 'Banner de la izquierda',
  `right_banner` mediumblob COMMENT 'Banner de la derecha',
  `welcome_banner` mediumblob COMMENT 'Banner de bienvenida',
  `ecommerce_status` tinyint(2) default '0' COMMENT 'Estado del comercio electronico',
  `shipping_costs` double(15,2) default NULL COMMENT 'Gastos de envio',
  `free_shipping` double(15,2) default NULL COMMENT 'Gastos de envio gratis a partir de esta cantidad',
  `title_note1` varchar(64) collate latin1_spanish_ci default 'Titulo de la Nota Legal 1',
  `title_note2` varchar(64) collate latin1_spanish_ci default 'Titulo de la Nota Legal 2',
  `title_note3` varchar(64) collate latin1_spanish_ci default 'Titulo de la Nota Legal 3',
  PRIMARY KEY  (`id`),
  KEY `IDX_ECCONFIG_BANK_TRANSFER` (`bank_transfer`),
  KEY `IDX_ECCONFIG_PAYPAL` (`paypal`),
  KEY `IDX_ECCONFIG_BANK_DRAFT` (`bank_draft`),
  KEY `IDX_ECCONFIG_CASH_ON_DELIVERY` (`cash_on_delivery`),
  KEY `IDX_ECCONFIG_VISA` (`visa`),
  KEY `IDX_ECCONFIG_TARIFF` (`tariff`),
  CONSTRAINT `FK_ECCONFIG_BANK_DRAFT` FOREIGN KEY (`bank_draft`) REFERENCES `pay_method` (`id`),
  CONSTRAINT `FK_ECCONFIG_BANK_TRANSFER` FOREIGN KEY (`bank_transfer`) REFERENCES `pay_method` (`id`),
  CONSTRAINT `FK_ECCONFIG_CASH_ON_DELIVERY` FOREIGN KEY (`cash_on_delivery`) REFERENCES `pay_method` (`id`),
  CONSTRAINT `FK_ECCONFIG_PAYPAL` FOREIGN KEY (`paypal`) REFERENCES `pay_method` (`id`),
  CONSTRAINT `FK_ECCONFIG_VISA` FOREIGN KEY (`visa`) REFERENCES `pay_method` (`id`),
  CONSTRAINT `FK_ECCONFIG_TARIFF` FOREIGN KEY (`tariff`) REFERENCES `tariff` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Configuracion del ECommerce';


UPDATE `db_version` SET `version_number` = '4.3.0';

COMMIT;
