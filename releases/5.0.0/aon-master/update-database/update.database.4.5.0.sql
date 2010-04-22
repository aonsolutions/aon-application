# Database: aon_master
# Version: Actualizacion de la version 4.5.0 a la version 4.6.0.
# Created by: girazu
# Creation Date: 03/11/2009 09:57
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



ALTER TABLE `rattach` ADD `scope` int(4) default NULL COMMENT 'Ambito del Archivo Adjunto';

ALTER TABLE `rattach` ADD KEY `IDX_RATTACH_SCOPE` (`scope`);

ALTER TABLE `rattach` ADD CONSTRAINT `FK_RATTACH_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`);

ALTER TABLE `company` MODIFY `calendar` int(4) default NULL COMMENT 'Identificador del Calendario Laboral' AFTER `active`;

ALTER TABLE `customer` MODIFY `surcharge` tinyint(1) default '0' COMMENT 'Indica si el Cliente tiene recargo de equivalencia';

ALTER TABLE `customer` ADD `transaction` tinyint(2) default '0' COMMENT 'Tipo de transacciones del Cliente' AFTER `withholding`;

ALTER TABLE `supplier` ADD `transaction` tinyint(2) default '0' COMMENT 'Tipo de transacciones del Proveedor' AFTER `withholding`;

ALTER TABLE `creditor` ADD `transaction` tinyint(2) default '0' COMMENT 'Tipo de transacciones del Acreedor' AFTER `withholding`;

ALTER TABLE `target` ADD `surcharge` tinyint(1) default '0' COMMENT 'Indica si el Cliente Potencial tiene recargo de equivalencia';

ALTER TABLE `target` ADD `withholding` tinyint(1) default '0' COMMENT 'Indica si el Cliente Potencial aplica retencion de impuestos';

ALTER TABLE `target` ADD `transaction` tinyint(2) default '0' COMMENT 'Tipo de transacciones del Cliente Potencial';

ALTER TABLE `target` ADD `status` tinyint(2) default '0' COMMENT 'Estado del Cliente Potencial';

ALTER TABLE `asset_activity` ADD `status` tinyint(2) NOT NULL default '0' COMMENT 'Estado de la Solicitud';

CREATE TABLE `expense` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion del Gasto',
  `unit_price` double(15,3) default '0.000' COMMENT 'Precio unitario',
  PRIMARY KEY `id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Gastos';

CREATE TABLE `expense_account` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `registry` int(4) default NULL COMMENT 'Registry que realiza los Gastos',
  `expense_holder_type` tinyint(2) default NULL COMMENT 'Tipo de Registry',
  `status` tinyint(2) default NULL COMMENT 'Estado del Gasto',
  `issue_date` date default NULL COMMENT 'Fecha del Gasto',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion del Gasto',
  `comments` text collate latin1_spanish_ci COMMENT 'Comentarios acerca del Gasto',
  PRIMARY KEY  (`id`),
  KEY `registry` (`registry`),
  CONSTRAINT `FK_EXPENSE_ACCOUNT_REGISTRY_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT="Gastos";

CREATE TABLE `expense_account_detail` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `expense_account` int(4) NOT NULL COMMENT 'Identidicador del Gasto',
  `expense` int(4) NOT NULL COMMENT 'Gasto',
  `quantity` double(15,3) default '0.000' COMMENT 'Cantidad del Gasto',
  `price` double(15,3) default '0.000' COMMENT 'Precio unitario del Gasto',
  `amount` double(15,3) default '0.000' COMMENT 'Precio total del Gasto',
  PRIMARY KEY  (`id`),
  KEY `IDX_EXPENSE_ACCOUNT_DETAIL_EXPENSE_ACCOUNT` (`expense_account`),
  KEY `IDX_EXPENSE_ACCOUNT_DETAIL_EXPENSE` (`expense`),
  CONSTRAINT `FK_EXPENSE_ACCOUNT_DETAIL_EXPENSE` FOREIGN KEY (`expense`) REFERENCES `expense` (`id`),
  CONSTRAINT `FK_EXPENSE_ACCOUNT_DETAIL_EXPENSE_ACCOUNT` FOREIGN KEY (`expense_account`) REFERENCES `expense_account` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT="Detalles del Gasto";

DROP TABLE `ec_catalogue`;

CREATE TABLE `ec_catalogue` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `catalogue` int(4) NOT NULL COMMENT 'Identificador del Catalogo',
  `catalogue_img` mediumblob COMMENT 'Imagen para el Catalogo',
  `catalogue_icon` blob COMMENT 'Icono del Catalogo',
  `type` tinyint(2) default '0' COMMENT 'Tipo de Catalogo',
  `visible` tinyint(1) default '0' COMMENT 'Indica si es visible en internet',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `IDX_ECCATALOGUE_CATALOGUE` (`catalogue`),
  CONSTRAINT `FK_ECCATALOGUE_CATALOGUE` FOREIGN KEY (`catalogue`) REFERENCES `catalogue` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Catalogos del ECommerce';

DROP TABLE `ec_config`;

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
  `title_note1` varchar(64) collate latin1_spanish_ci default 'Titulo de la nota legal 1',
  `title_note2` varchar(64) collate latin1_spanish_ci default 'Titulo de la nota legal 2',
  `title_note3` varchar(64) collate latin1_spanish_ci default 'Titulo de la nota legal 3',
  `email` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Email de contacto',
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
  CONSTRAINT `FK_ECCONFIG_TARIFF` FOREIGN KEY (`tariff`) REFERENCES `tariff` (`id`),
  CONSTRAINT `FK_ECCONFIG_VISA` FOREIGN KEY (`visa`) REFERENCES `pay_method` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Configuracion del ECommerce';

DROP TABLE `ec_target`;

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

CREATE TABLE `ec_offer_pay_info` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `offer` int(4) NOT NULL COMMENT 'Identificador del Presupuesto',
  `payment_status` tinyint(2) default NULL COMMENT 'Estado del pago',
  `authorization_number` int(4) default NULL COMMENT 'Numero de autorizacion',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `IDX_EC_OFFER_PAY_INFO_OFFER` (`offer`),
  CONSTRAINT `FK_EC_OFFER_PAY_INFO_OFFER` FOREIGN KEY (`offer`) REFERENCES `offer` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT="Informacion acerca de los Pagos en el ECommerce ";

CREATE TABLE `ec_paymethod` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `pay_method` int(4) NOT NULL COMMENT 'Identificador de la Forma de Pago',
  `user_name` varchar(32) collate latin1_spanish_ci default 'Null' COMMENT 'Nombre de Usuario',
  `password` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Contraseña para la pasarela de pago',
  `signature` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Identificador unico de la empresa para pasarela',
  PRIMARY KEY `id` (`id`),
  UNIQUE KEY `IDX_ECPAYMETHOD_PAYMETHOD` (`pay_method`),
  CONSTRAINT `FK_ECPAYMETHOD_PAYMETHOD` FOREIGN KEY (`pay_method`) REFERENCES `pay_method` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT="Formas de Pago del ECommerce";

ALTER TABLE `target_segment` DROP FOREIGN KEY `target_segment_fk`;

ALTER TABLE `target_segment` DROP FOREIGN KEY `target_segment_fk1`;

ALTER TABLE `target_segment` DROP KEY `target`;

ALTER TABLE `target_segment` DROP KEY `segment`;

ALTER TABLE `commercial_segment` RENAME `segment`;

ALTER TABLE `segment` MODIFY `name` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Nombre del Segmento';

ALTER TABLE `target_segment` RENAME `rsegment`;

ALTER TABLE `rsegment` COMMENT 'Segmentos de Personas o Empresas';

ALTER TABLE `rsegment` MODIFY `target` int(4) NOT NULL COMMENT 'Identificador de Persona o Empresa';

ALTER TABLE `rsegment` CHANGE `target` `registry` int(4) NOT NULL COMMENT 'Identificador de Persona o Empresa';

ALTER TABLE `rsegment` MODIFY `segment` int(4) NOT NULL COMMENT 'Identificador del Segmento';

ALTER TABLE `rsegment` ADD KEY `IDX_REGISTRY_SEGMENT_REGISTRY` (`registry`);

ALTER TABLE `rsegment` ADD CONSTRAINT `FK_REGISTRY_SEGMENT_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`);

ALTER TABLE `rsegment` ADD KEY `IDX_REGISTRY_SEGMENT_SEGMENT` (`segment`);

ALTER TABLE `rsegment` ADD CONSTRAINT `FK_REGISTRY_SEGMENT_SEGMENT` FOREIGN KEY (`segment`) REFERENCES `segment` (`id`);

ALTER TABLE `process` ADD `status` tinyint(2) NOT NULL default '0' COMMENT 'Estado del Proceso';

ALTER TABLE `process_detail` ADD `priority` tinyint(2) default '0' COMMENT 'Prioridad de la Tarea';

ALTER TABLE `process_detail` ADD `status` tinyint(2) NOT NULL default '0' COMMENT 'Estado de la Accion';

CREATE TABLE `raddinfo` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `registry` int(4) NOT NULL COMMENT 'Identificador de la Persona o Empresa',
  `attribute` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Atributo adicional',
  `value` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Valor del atributo adicional',
  `value_date` date NOT NULL COMMENT 'Fecha del valor del atributo',
  PRIMARY KEY `id` (`id`),
  KEY `IDX_RADDINFO_REGISTRY` (`registry`),
  CONSTRAINT `FK_RADDINFO_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT="Informacion adicional de la Persona o Empresa";


UPDATE `db_version` SET `version_number` = '4.6.0';

COMMIT;
