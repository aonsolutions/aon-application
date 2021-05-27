# Database: aon_master
# Version: Actualizacion de la version 6.19.4 a la version 6.20.0.
# Created by: girazu
# Creation Date: 28/12/2011 08:35
# Comentarios: Ninguno


BEGIN;

CREATE TABLE `hotel` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `code` varchar(16) collate latin1_spanish_ci NOT NULL COMMENT 'Codigo del Hotel',
  `scope` int(4) NOT NULL COMMENT 'Identificador del Ambito',
  `workplace` int(4) NOT NULL COMMENT 'Identificador del Centro de Trabajo',
  `active` tinyint(1) default '1' COMMENT 'Indica si el Hotel esta activo o no',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `IDX_HOTEL_CODE` (`code`),
  KEY `IDX_HOTEL_SCOPE` (`scope`),
  KEY `IDX_HOTEL_WORKPLACE` (`workplace`),
  CONSTRAINT `FK_HOTEL_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`),
  CONSTRAINT `FK_HOTEL_WORKPLACE` FOREIGN KEY (`workplace`) REFERENCES `workplace` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Hoteles';

CREATE TABLE `project_reservation` (
  `project` int(4) NOT NULL COMMENT 'Identificador del Proyecto',
  `hotel` int(4) NOT NULL COMMENT 'Identificador del Hotel',
  `code` varchar(16) collate latin1_spanish_ci NOT NULL COMMENT 'Codigo de la Reserva',
  `creation_date` datetime NOT NULL COMMENT 'Fecha de creacion',
  `start_date` date NOT NULL COMMENT 'Fecha de entrada',
  `end_date` date NOT NULL COMMENT 'Fecha de salida',
  `seller` int(4) default NULL COMMENT 'Identificador del canal de venta',
  `agency` int(4) default NULL COMMENT 'Identificador de la agencia de viajes',
  `agency_commission_percent` double(5,2) default '0.00' COMMENT 'Porcentaje de comision de la agencia',
  `agency_commission_amount` double(15,2) default '0.00' COMMENT 'Importe de comision de la agencia',
  `agency_rebate` tinyint(1) NOT NULL COMMENT 'Indica si la agencia trabaja en modo descuento o no',
  `company` int(4) default NULL COMMENT 'Identificador de la empresa',
  `discount_percent` double(5,2) default '0.00' COMMENT 'Porcentaje de descuento',
  `discount_amount` double(15,2) default '0.00' COMMENT 'Importe de descuento',
  `booking_holder` tinyint(2) NOT NULL COMMENT 'Titular de la Reserva',
  `tariff` int(4) default NULL COMMENT 'Identificador de la Tarifa',
  `taxable_base` double(15,2) default '0.00' COMMENT 'Base imponible',
  `vat_quota` double(15,2) default '0.00' COMMENT 'Cuota de IVA',
  `other_tax_quota` double(15,2) default '0.00' COMMENT 'Cuota de otros Impuestos',
  `total` double(15,2) default '0.00' COMMENT 'Importe Total',
  `remarks` text collate latin1_spanish_ci COMMENT 'Observaciones',
  `status` tinyint(2) NOT NULL COMMENT 'Estado de la Reserva',
  PRIMARY KEY  (`project`),
  KEY `IDX_PROJECT_RESERVATION_CODE` (`code`),
  KEY `IDX_PROJECT_RESERVATION_HOTEL` (`hotel`),
  KEY `IDX_PROJECT_RESERVATION_SELLER` (`seller`),
  KEY `IDX_PROJECT_RESERVATION_AGENCY` (`agency`),
  KEY `IDX_PROJECT_RESERVATION_COMPANY` (`company`),
  KEY `IDX_PROJECT_RESERVATION_TARIFF` (`tariff`),
  CONSTRAINT `FK_PROJECT_RESERVATION_PROJECT` FOREIGN KEY (`project`) REFERENCES `project` (`id`),
  CONSTRAINT `FK_PROJECT_RESERVATION_HOTEL` FOREIGN KEY (`hotel`) REFERENCES `hotel` (`id`),
  CONSTRAINT `FK_PROJECT_RESERVATION_SELLER` FOREIGN KEY (`seller`) REFERENCES `seller` (`registry`),
  CONSTRAINT `FK_PROJECT_RESERVATION_AGENCY` FOREIGN KEY (`agency`) REFERENCES `customer` (`registry`),
  CONSTRAINT `FK_PROJECT_RESERVATION_COMPANY` FOREIGN KEY (`company`) REFERENCES `customer` (`registry`),
  CONSTRAINT `FK_PROJECT_RESERVATION_TARIFF` FOREIGN KEY (`tariff`) REFERENCES `tariff` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Reservas de Hotel';

CREATE TABLE `project_reservation_guest` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `project_reservation` int(4) NOT NULL COMMENT 'Identificador de la Reserva',
  `guest_index` tinyint(2) NOT NULL COMMENT 'Numero de Huesped',
  `name` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Nombre',
  `surname` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Apellidos',
  `treatment` varchar(4) collate latin1_spanish_ci default NULL COMMENT 'Tratamiento',
  `email` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Email',
  `phone` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Telefono',
  `address` varchar(256) collate latin1_spanish_ci default NULL COMMENT 'Direccion',
  `zip` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Codigo postal',
  `city` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Ciudad',
  `province` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Provincia',
  `country` varchar(2) collate latin1_spanish_ci default NULL COMMENT 'Pais',
  PRIMARY KEY  (`id`),
  KEY `IDX_PROJECT_RESERVATION_GUEST_PROJECT_RESERVATION` (`project_reservation`),
  CONSTRAINT `FK_PROJECT_RESERVATION_GUEST_PROJECT_RESERVATION` FOREIGN KEY (`project_reservation`) REFERENCES `project_reservation` (`project`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Huespedes por Reserva';

CREATE TABLE `project_reservation_room` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `project_reservation` int(4) NOT NULL COMMENT 'Identificador de la Reserva',
  `room_index` tinyint(2) NOT NULL COMMENT 'Numero de Habitacion',
  `item` int(4) NOT NULL COMMENT 'Identificador del Tipo de Habitacion',
  `units` tinyint(2) default '0' COMMENT 'Numero de habitaciones',
  `adults` tinyint(2) default '0' COMMENT 'Numero de adultos',
  `children` tinyint(2) default '0' COMMENT 'Numero de niños',
  PRIMARY KEY  (`id`),
  KEY `IDX_PROJECT_RESERVATION_ROOM_PROJECT_RESERVATION` (`project_reservation`),
  KEY `IDX_PROJECT_RESERVATION_ROOM_ITEM` (`item`),
  CONSTRAINT `FK_PROJECT_RESERVATION_ROOM_PROJECT_RESERVATION` FOREIGN KEY (`project_reservation`) REFERENCES `project_reservation` (`project`),
  CONSTRAINT `FK_PROJECT_RESERVATION_ROOM_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Habitaciones por Reserva';

CREATE TABLE `project_reservation_service` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `project_reservation` int(4) NOT NULL COMMENT 'Identificador de la Reserva',
  `service_index` tinyint(2) NOT NULL COMMENT 'Numero de Servicio',
  `effective_date` date NOT NULL COMMENT 'Fecha de efecto',
  `item` int(4) NOT NULL COMMENT 'Identificador del Servicio',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion',
  `quantity` double(15,2) default '0.00' COMMENT 'Cantidad',
  `price` double(15,2) default '0.00' COMMENT 'Precio',
  `taxable_base` double(15,2) default '0.00' COMMENT 'Base imponible',
  PRIMARY KEY  (`id`),
  KEY `IDX_PROJECT_RESERVATION_SERVICE_PROJECT_RESERVATION` (`project_reservation`),
  KEY `IDX_PROJECT_RESERVATION_SERVICE_ITEM` (`item`),
  CONSTRAINT `FK_PROJECT_RESERVATION_SERVICE_PROJECT_RESERVATION` FOREIGN KEY (`project_reservation`) REFERENCES `project_reservation` (`project`),
  CONSTRAINT `FK_PROJECT_RESERVATION_SERVICE_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Servicios por Reserva';

ALTER TABLE `workplace` MODIFY `description` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Descripcion del Centro de Trabajo';
ALTER TABLE `workplace` CHARACTER SET latin1 COLLATE latin1_spanish_ci;

ALTER TABLE `account_entry_detail` DROP FOREIGN KEY `account_entry_detail_ibfk_4`;
ALTER TABLE `account_entry_detail` DROP KEY `account_entry`;
ALTER TABLE `account_entry_detail` ADD KEY `IDX_ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY` (`account_entry`);
ALTER TABLE `account_entry_detail` ADD CONSTRAINT `FK_ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY` FOREIGN KEY (`account_entry`) REFERENCES `account_entry` (`id`);
ALTER TABLE `account_entry_detail` DROP FOREIGN KEY `FK_ACCOUNT_ENTRY_DETAIL_BAL_ACCOUNT`;
ALTER TABLE `account_entry_detail` DROP KEY `IDX_ACCOUNT_ENTRY_DETAIL_BAL_ACCOUNT`;
ALTER TABLE `account_entry_detail` ADD KEY `IDX_ACCOUNT_ENTRY_DETAIL_BALANCING_ACCOUNT` (`balancing_account`);
ALTER TABLE `account_entry_detail` ADD CONSTRAINT `FK_ACCOUNT_ENTRY_DETAIL_BALANCING_ACCOUNT` FOREIGN KEY (`balancing_account`) REFERENCES `account` (`id`);

ALTER TABLE `account_entry_fbatch` DROP FOREIGN KEY `account_entry_fbatch_fk`;
ALTER TABLE `account_entry_fbatch` DROP KEY `account_entry`;
ALTER TABLE `account_entry_fbatch` ADD KEY `IDX_ACCOUNT_ENTRY_FBATCH_ACCOUNT_ENTRY` (`account_entry`);
ALTER TABLE `account_entry_fbatch` ADD CONSTRAINT `FK_ACCOUNT_ENTRY_FBATCH_ACCOUNT_ENTRY` FOREIGN KEY (`account_entry`) REFERENCES `account_entry` (`id`);
ALTER TABLE `account_entry_fbatch` DROP FOREIGN KEY `account_entry_fbatch_fk1`;
ALTER TABLE `account_entry_fbatch` DROP KEY `fbatch`;
ALTER TABLE `account_entry_fbatch` ADD KEY `IDX_ACCOUNT_ENTRY_FBATCH_FBATCH` (`fbatch`);
ALTER TABLE `account_entry_fbatch` ADD CONSTRAINT `FK_ACCOUNT_ENTRY_FBATCH_FBATCH` FOREIGN KEY (`fbatch`) REFERENCES `fbatch` (`id`);

ALTER TABLE `account_entry_finance_tracking` DROP FOREIGN KEY `account_entry_finance_tracking_fk`;
ALTER TABLE `account_entry_finance_tracking` DROP KEY `account_entry`;
ALTER TABLE `account_entry_finance_tracking` ADD KEY `IDX_ACCOUNT_ENTRY_FINANCE_TRACKING_ACCOUNT_ENTRY` (`account_entry`);
ALTER TABLE `account_entry_finance_tracking` ADD CONSTRAINT `FK_ACCOUNT_ENTRY_FINANCE_TRACKING_ACCOUNT_ENTRY` FOREIGN KEY (`account_entry`) REFERENCES `account_entry` (`id`);
ALTER TABLE `account_entry_finance_tracking` DROP FOREIGN KEY `account_entry_finance_tracking_fk1`;
ALTER TABLE `account_entry_finance_tracking` DROP KEY `finance_tracking`;
ALTER TABLE `account_entry_finance_tracking` ADD KEY `IDX_ACCOUNT_ENTRY_FINANCE_TRACKING_FINANCE_TRACKING` (`finance_tracking`);
ALTER TABLE `account_entry_finance_tracking` ADD CONSTRAINT `FK_ACCOUNT_ENTRY_FINANCE_TRACKING_FINANCE_TRACKING` FOREIGN KEY (`finance_tracking`) REFERENCES `finance_tracking` (`id`);

ALTER TABLE `account_entry_invoice` DROP FOREIGN KEY `account_entry_invoice_ibfk_1`;
ALTER TABLE `account_entry_invoice` DROP KEY `account_entry`;
ALTER TABLE `account_entry_invoice` ADD KEY `IDX_ACCOUNT_ENTRY_INVOICE_ACCOUNT_ENTRY` (`account_entry`);
ALTER TABLE `account_entry_invoice` ADD CONSTRAINT `FK_ACCOUNT_ENTRY_INVOICE_ACCOUNT_ENTRY` FOREIGN KEY (`account_entry`) REFERENCES `account_entry` (`id`);
ALTER TABLE `account_entry_invoice` DROP FOREIGN KEY `account_entry_invoice_fk_2`;
ALTER TABLE `account_entry_invoice` DROP KEY `invoice`;
ALTER TABLE `account_entry_invoice` ADD KEY `IDX_ACCOUNT_ENTRY_INVOICE_INVOICE` (`invoice`);
ALTER TABLE `account_entry_invoice` ADD CONSTRAINT `FK_ACCOUNT_ENTRY_INVOICE_INVOICE` FOREIGN KEY (`invoice`) REFERENCES `invoice` (`id`);

ALTER TABLE `action_denied` DROP FOREIGN KEY `FK_ACTION_DENIED_ACTION_ID`;
ALTER TABLE `action_denied` DROP KEY `IDX_ACTION_DENIED_ACTION_ID`;
ALTER TABLE `action_denied` ADD KEY `IDX_ACTION_DENIED_ACTION` (`action_id`);
ALTER TABLE `action_denied` ADD CONSTRAINT `FK_ACTION_DENIED_ACTION` FOREIGN KEY (`action_id`) REFERENCES `action` (`id`);
ALTER TABLE `action_denied` DROP FOREIGN KEY `FK_ACTION_DENIED_USER_ID`;
ALTER TABLE `action_denied` DROP KEY `IDX_ACTION_DENIED_USER_ID`;
ALTER TABLE `action_denied` ADD KEY `IDX_ACTION_DENIED_USER` (`user_id`);
ALTER TABLE `action_denied` ADD CONSTRAINT `FK_ACTION_DENIED_USER` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`);

ALTER TABLE `action_entry` DROP FOREIGN KEY `FK_ACTION_ENTRY_ACTION_ID`;
ALTER TABLE `action_entry` DROP KEY `IDX_ACTION_ENTRY_ACTION_ID`;
ALTER TABLE `action_entry` ADD KEY `IDX_ACTION_ENTRY_ACTION` (`action_id`);
ALTER TABLE `action_entry` ADD CONSTRAINT `FK_ACTION_ENTRY_ACTION` FOREIGN KEY (`action_id`) REFERENCES `action` (`id`);
ALTER TABLE `action_entry` DROP FOREIGN KEY `FK_ACTION_ENTRY_SESSION_ID`;
ALTER TABLE `action_entry` DROP KEY `IDX_ACTION_ENTRY_SESSION_ID`;
ALTER TABLE `action_entry` ADD KEY `IDX_ACTION_ENTRY_SESSION` (`session_id`);
ALTER TABLE `action_entry` ADD CONSTRAINT `FK_ACTION_ENTRY_SESSION` FOREIGN KEY (`session_id`) REFERENCES `session` (`id`);

ALTER TABLE `action_favorite` DROP FOREIGN KEY `FK_ACTION_FAVORITE_ACTION_ID`;
ALTER TABLE `action_favorite` DROP KEY `IDX_ACTION_FAVORITE_ACTION_ID`;
ALTER TABLE `action_favorite` ADD KEY `IDX_ACTION_FAVORITE_ACTION` (`action_id`);
ALTER TABLE `action_favorite` ADD CONSTRAINT `FK_ACTION_FAVORITE_ACTION` FOREIGN KEY (`action_id`) REFERENCES `action` (`id`);
ALTER TABLE `action_favorite` DROP FOREIGN KEY `FK_ACTION_FAVORITE_USER_ID`;
ALTER TABLE `action_favorite` DROP KEY `IDX_ACTION_FAVORITE_USER_ID`;
ALTER TABLE `action_favorite` ADD KEY `IDX_ACTION_FAVORITE_USER` (`user_id`);
ALTER TABLE `action_favorite` ADD CONSTRAINT `FK_ACTION_FAVORITE_USER` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`);

ALTER TABLE `activity_type` DROP FOREIGN KEY `FK_ACT_TYPE_PROJECT_TYPE`;
ALTER TABLE `activity_type` DROP KEY `IDX_ACT_TYPE_PROJECT_TYPE`;
ALTER TABLE `activity_type` ADD KEY `IDX_ACTIVITY_TYPE_PROJECT_TYPE` (`project_type`);
ALTER TABLE `activity_type` ADD CONSTRAINT `FK_ACTIVITY_TYPE_PROJECT_TYPE` FOREIGN KEY (`project_type`) REFERENCES `project_type` (`id`);

ALTER TABLE `agreement_level` DROP FOREIGN KEY `FK_LEVEL_AGREEMENT`;
ALTER TABLE `agreement_level` DROP KEY `IDX_LEVEL_AGREEMENT`;
ALTER TABLE `agreement_level` ADD KEY `IDX_AGREEMENT_LEVEL_AGREEMENT` (`agreement`);
ALTER TABLE `agreement_level` ADD CONSTRAINT `FK_AGREEMENT_LEVEL_AGREEMENT` FOREIGN KEY (`agreement`) REFERENCES `agreement` (`id`);

ALTER TABLE `agreement_level_category` DROP FOREIGN KEY `FK_CATEGORY_AGREEMENT_LEVEL`;
ALTER TABLE `agreement_level_category` DROP KEY `IDX_CATEGORY_AGREEMENT_LEVEL`;
ALTER TABLE `agreement_level_category` ADD KEY `IDX_AGREEMENT_LEVEL_CATEGORY_AGREEMENT_LEVEL` (`agreement_level`);
ALTER TABLE `agreement_level_category` ADD CONSTRAINT `FK_AGREEMENT_LEVEL_CATEGORY_AGREEMENT_LEVEL` FOREIGN KEY (`agreement_level`) REFERENCES `agreement_level` (`id`);

ALTER TABLE `agreement_payment` DROP FOREIGN KEY `FK_PAYMENT_AGREEMENT`;
ALTER TABLE `agreement_payment` DROP KEY `IDX_PAYMENT_AGREEMENT`;
ALTER TABLE `agreement_payment` ADD KEY `IDX_AGREEMENT_PAYMENT_AGREEMENT` (`agreement`);
ALTER TABLE `agreement_payment` ADD CONSTRAINT `FK_AGREEMENT_PAYMENT_AGREEMENT` FOREIGN KEY (`agreement`) REFERENCES `agreement` (`id`);

ALTER TABLE `alarm` DROP FOREIGN KEY `alarm_fk`;
ALTER TABLE `alarm` DROP KEY `user`;
ALTER TABLE `alarm` ADD KEY `IDX_ALARM_USER` (`user_id`);
ALTER TABLE `alarm` ADD CONSTRAINT `FK_ALARM_USER` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`);

ALTER TABLE `amortization` DROP FOREIGN KEY `fk_amortization_amortization_type`;
ALTER TABLE `amortization` DROP KEY `amortization_type`;
ALTER TABLE `amortization` ADD KEY `IDX_AMORTIZATION_AMORTIZATION_TYPE` (`amortization_type`);
ALTER TABLE `amortization` ADD CONSTRAINT `FK_AMORTIZATION_AMORTIZATION_TYPE` FOREIGN KEY (`amortization_type`) REFERENCES `amortization_type` (`id`);
ALTER TABLE `amortization` DROP FOREIGN KEY `FK_AMORTIZATION_FIX_ACCOUNT`;
ALTER TABLE `amortization` DROP KEY `IDX_AMORTIZATION_FIX_ACCOUNT`;
ALTER TABLE `amortization` ADD KEY `IDX_AMORTIZATION_FIXED_ASSET_ACCOUNT` (`fixed_asset_account`);
ALTER TABLE `amortization` ADD CONSTRAINT `FK_AMORTIZATION_FIXED_ASSET_ACCOUNT` FOREIGN KEY (`fixed_asset_account`) REFERENCES `account` (`id`);
ALTER TABLE `amortization` DROP FOREIGN KEY `FK_AMORTIZATION_ACC_ACCOUNT`;
ALTER TABLE `amortization` DROP KEY `IDX_AMORTIZATION_ACC_ACCOUNT`;
ALTER TABLE `amortization` ADD KEY `IDX_AMORTIZATION_ACCUMULATED_ACCOUNT` (`accumulated_account`);
ALTER TABLE `amortization` ADD CONSTRAINT `FK_AMORTIZATION_ACCUMULATED_ACCOUNT` FOREIGN KEY (`accumulated_account`) REFERENCES `account` (`id`);
ALTER TABLE `amortization` DROP FOREIGN KEY `FK_AMORTIZATION_ALL_ACCOUNT`;
ALTER TABLE `amortization` DROP KEY `IDX_AMORTIZATION_ALL_ACCOUNT`;
ALTER TABLE `amortization` ADD KEY `IDX_AMORTIZATION_ALLOCATION_ACCOUNT` (`allocation_account`);
ALTER TABLE `amortization` ADD CONSTRAINT `FK_AMORTIZATION_ALLOCATION_ACCOUNT` FOREIGN KEY (`allocation_account`) REFERENCES `account` (`id`);

ALTER TABLE `amortization_detail` DROP FOREIGN KEY `fk_amortization_detail_amortization`;
ALTER TABLE `amortization_detail` DROP KEY `amortization`;
ALTER TABLE `amortization_detail` ADD KEY `IDX_AMORTIZATION_DETAIL_AMORTIZATION` (`amortization`);
ALTER TABLE `amortization_detail` ADD CONSTRAINT `FK_AMORTIZATION_DETAIL_AMORTIZATION` FOREIGN KEY (`amortization`) REFERENCES `amortization` (`id`);
ALTER TABLE `amortization_detail` DROP FOREIGN KEY `fk_amortization_detail_account_entry`;
ALTER TABLE `amortization_detail` DROP KEY `account_entry`;
ALTER TABLE `amortization_detail` ADD KEY `IDX_AMORTIZATION_DETAIL_ACCOUNT_ENTRY` (`account_entry`);
ALTER TABLE `amortization_detail` ADD CONSTRAINT `FK_AMORTIZATION_DETAIL_ACCOUNT_ENTRY` FOREIGN KEY (`account_entry`) REFERENCES `account_entry` (`id`);

ALTER TABLE `amortization_type` DROP FOREIGN KEY `FK_AMORTIZATION_TYPE_FIX_ACCOUNT`;
ALTER TABLE `amortization_type` DROP KEY `IDX_AMORTIZATION_TYPE_FIX_ACCOUNT`;
ALTER TABLE `amortization_type` ADD KEY `IDX_AMORTIZATION_TYPE_FIXED_ASSET_ACCOUNT` (`fixed_asset_account`);
ALTER TABLE `amortization_type` ADD CONSTRAINT `FK_AMORTIZATION_TYPE_FIXED_ASSET_ACCOUNT` FOREIGN KEY (`fixed_asset_account`) REFERENCES `account` (`id`);
ALTER TABLE `amortization_type` DROP FOREIGN KEY `FK_AMORTIZATION_TYPE_ACC_ACCOUNT`;
ALTER TABLE `amortization_type` DROP KEY `IDX_AMORTIZATION_TYPE_ACC_ACCOUNT`;
ALTER TABLE `amortization_type` ADD KEY `IDX_AMORTIZATION_TYPE_ACCUMULATED_ACCOUNT` (`accumulated_account`);
ALTER TABLE `amortization_type` ADD CONSTRAINT `FK_AMORTIZATION_TYPE_ACCUMULATED_ACCOUNT` FOREIGN KEY (`accumulated_account`) REFERENCES `account` (`id`);
ALTER TABLE `amortization_type` DROP FOREIGN KEY `FK_AMORTIZATION_TYPE_ALL_ACCOUNT`;
ALTER TABLE `amortization_type` DROP KEY `IDX_AMORTIZATION_TYPE_ALL_ACCOUNT`;
ALTER TABLE `amortization_type` ADD KEY `IDX_AMORTIZATION_TYPE_ALLOCATION_ACCOUNT` (`allocation_account`);
ALTER TABLE `amortization_type` ADD CONSTRAINT `FK_AMORTIZATION_TYPE_ALLOCATION_ACCOUNT` FOREIGN KEY (`allocation_account`) REFERENCES `account` (`id`);

ALTER TABLE `asset_activity` DROP FOREIGN KEY `asset_activity_fk1`;
ALTER TABLE `asset_activity` DROP KEY `asset`;
ALTER TABLE `asset_activity` ADD KEY `IDX_ASSET_ACTIVITY_ASSET` (`asset`);
ALTER TABLE `asset_activity` ADD CONSTRAINT `FK_ASSET_ACTIVITY_ASSET` FOREIGN KEY (`asset`) REFERENCES `asset` (`id`);

ALTER TABLE `balance_detail` DROP FOREIGN KEY `fk_balance_detail_balance`;
ALTER TABLE `balance_detail` DROP KEY `idx_balance`;
ALTER TABLE `balance_detail` ADD KEY `IDX_BALANCE_DETAIL_BALANCE` (`balance`);
ALTER TABLE `balance_detail` ADD CONSTRAINT `FK_BALANCE_DETAIL_BALANCE` FOREIGN KEY (`balance`) REFERENCES `balance` (`id`);

ALTER TABLE `campaign` DROP FOREIGN KEY `campaign_ibfk_1`;
ALTER TABLE `campaign` DROP KEY `process`;
ALTER TABLE `campaign` ADD KEY `IDX_CAMPAIGN_PROCESS` (`process`);
ALTER TABLE `campaign` ADD CONSTRAINT `FK_CAMPAIGN_PROCESS` FOREIGN KEY (`process`) REFERENCES `process` (`id`);
ALTER TABLE `campaign` DROP FOREIGN KEY `campaign_ibfk_3`;
ALTER TABLE `campaign` DROP KEY `workgroup`;
ALTER TABLE `campaign` ADD KEY `IDX_CAMPAIGN_WORKGROUP` (`workgroup`);
ALTER TABLE `campaign` ADD CONSTRAINT `FK_CAMPAIGN_WORKGROUP` FOREIGN KEY (`workgroup`) REFERENCES `workgroup` (`id`);

ALTER TABLE `campaign_project` DROP FOREIGN KEY `FK_CMP_PRJ_CAMPAIGN`;
ALTER TABLE `campaign_project` DROP KEY `IDX_CMP_PRJ_CAMPAIGN`;
ALTER TABLE `campaign_project` ADD KEY `IDX_CAMPAIGN_PROJECT_CAMPAIGN` (`campaign`);
ALTER TABLE `campaign_project` ADD CONSTRAINT `FK_CAMPAIGN_PROJECT_CAMPAIGN` FOREIGN KEY (`campaign`) REFERENCES `campaign` (`id`);
ALTER TABLE `campaign_project` DROP FOREIGN KEY `FK_CMP_PRJ_PROJECT`;
ALTER TABLE `campaign_project` DROP KEY `IDX_CMP_PRJ_PROJECT`;
ALTER TABLE `campaign_project` ADD KEY `IDX_CAMPAIGN_PROJECT_PROJECT` (`project`);
ALTER TABLE `campaign_project` ADD CONSTRAINT `FK_CAMPAIGN_PROJECT_PROJECT` FOREIGN KEY (`project`) REFERENCES `project` (`id`);

ALTER TABLE `catalogue_category` DROP FOREIGN KEY `catalogue_category_fk1`;
ALTER TABLE `catalogue_category` DROP KEY `catalogue`;
ALTER TABLE `catalogue_category` ADD KEY `IDX_CATALOGUE_CATEGORY_CATALOGUE` (`catalogue`);
ALTER TABLE `catalogue_category` ADD CONSTRAINT `FK_CATALOGUE_CATEGORY_CATALOGUE` FOREIGN KEY (`catalogue`) REFERENCES `catalogue` (`id`);
ALTER TABLE `catalogue_category` DROP FOREIGN KEY `catalogue_category_fk2`;
ALTER TABLE `catalogue_category` DROP KEY `category`;
ALTER TABLE `catalogue_category` ADD KEY `IDX_CATALOGUE_CATEGORY_PCATEGORY` (`category`);
ALTER TABLE `catalogue_category` ADD CONSTRAINT `FK_CATALOGUE_CATEGORY_PCATEGORY` FOREIGN KEY (`category`) REFERENCES `pcategory` (`id`);

ALTER TABLE `catalogue_item` DROP FOREIGN KEY `catalogue_item_fk1`;
ALTER TABLE `catalogue_item` DROP KEY `catalogue`;
ALTER TABLE `catalogue_item` ADD KEY `IDX_CATALOGUE_ITEM_CATALOGUE` (`catalogue`);
ALTER TABLE `catalogue_item` ADD CONSTRAINT `FK_CATALOGUE_ITEM_CATALOGUE` FOREIGN KEY (`catalogue`) REFERENCES `catalogue` (`id`);
ALTER TABLE `catalogue_item` DROP FOREIGN KEY `catalogue_item_fk2`;
ALTER TABLE `catalogue_item` DROP KEY `item`;
ALTER TABLE `catalogue_item` ADD KEY `IDX_CATALOGUE_ITEM_ITEM` (`item`);
ALTER TABLE `catalogue_item` ADD CONSTRAINT `FK_CATALOGUE_ITEM_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`);

ALTER TABLE `certifica2_batch` ADD KEY `IDX_CERTIFICA2_BATCH_ENTERPRISE` (`enterprise`);
ALTER TABLE `certifica2_batch` ADD CONSTRAINT `FK_CERTIFICA2_BATCH_ENTERPRISE` FOREIGN KEY (`enterprise`) REFERENCES `enterprise` (`registry`);

ALTER TABLE `cnae2009_rate` DROP FOREIGN KEY `FK_RATE_CNAE2009`;
ALTER TABLE `cnae2009_rate` DROP KEY `IDX_RATE_CNAE2009`;
ALTER TABLE `cnae2009_rate` ADD KEY `IDX_CNAE2009_RATE_CNAE2009` (`cnae2009`);
ALTER TABLE `cnae2009_rate` ADD CONSTRAINT `FK_CNAE2009_RATE_CNAE2009` FOREIGN KEY (`cnae2009`) REFERENCES `cnae2009` (`id`);

ALTER TABLE `commercial_tracking` DROP FOREIGN KEY `FK_COMMERCIAL_TRACKING_PROJECT`;
ALTER TABLE `commercial_tracking` DROP KEY `IDX_COMMERCIAL_TRACKING_PROJECT`;
ALTER TABLE `commercial_tracking` ADD KEY `IDX_COMMERCIAL_TRACKING_PROJECT_COMMERCIAL` (`project`);
ALTER TABLE `commercial_tracking` ADD CONSTRAINT `FK_COMMERCIAL_TRACKING_PROJECT_COMMERCIAL` FOREIGN KEY (`project`) REFERENCES `project_commercial` (`project`);
ALTER TABLE `commercial_tracking` DROP FOREIGN KEY `FK_COMMERCIAL_TRACKING_NEXT`;
ALTER TABLE `commercial_tracking` DROP KEY `IDX_COMMERCIAL_TRACKING_NEXT`;
ALTER TABLE `commercial_tracking` ADD KEY `IDX_COMMERCIAL_TRACKING_NEXT_COMMERCIAL_TRACKING` (`next_commercial_tracking`);
ALTER TABLE `commercial_tracking` ADD CONSTRAINT `FK_COMMERCIAL_TRACKING_NEXT_COMMERCIAL_TRACKING` FOREIGN KEY (`next_commercial_tracking`) REFERENCES `commercial_tracking` (`id`);

ALTER TABLE `company` DROP FOREIGN KEY `fk_comp_rgty`;
ALTER TABLE `company` ADD CONSTRAINT `FK_COMPANY_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`);

ALTER TABLE `contract` DROP KEY `IDX_CONTRACT_CCC`;
ALTER TABLE `contract` DROP KEY `IDX_CONTRACT_ENTERPRISE_ACTIVITY`;
ALTER TABLE `contract` ADD KEY `IDX_CONTRACT_ENTERPRISE_ACTIVITY` (`enterprise_activity`);
ALTER TABLE `contract` DROP KEY `FK_CONTRACT_ENTERPRISE_ACTIVITY`;

ALTER TABLE `contract_bonus` DROP FOREIGN KEY `FK_BONUS_CONTRACT`;
ALTER TABLE `contract_bonus` DROP KEY `IDX_BONUS_CONTRACT`;
ALTER TABLE `contract_bonus` ADD KEY `IDX_CONTRACT_BONUS_CONTRACT` (`contract`);
ALTER TABLE `contract_bonus` ADD CONSTRAINT `FK_CONTRACT_BONUS_CONTRACT` FOREIGN KEY (`contract`) REFERENCES `contract` (`id`);
ALTER TABLE `contract_bonus` DROP FOREIGN KEY `FK_BONUS_CONCEPT`;
ALTER TABLE `contract_bonus` DROP KEY `IDX_BONUS_CONCEPT`;
ALTER TABLE `contract_bonus` ADD KEY `IDX_CONTRACT_BONUS_BONUS_CONCEPT` (`bonus_concept`);
ALTER TABLE `contract_bonus` ADD CONSTRAINT `FK_CONTRACT_BONUS_BONUS_CONCEPT` FOREIGN KEY (`bonus_concept`) REFERENCES `bonus_concept` (`id`);

ALTER TABLE `contract_data` DROP FOREIGN KEY `FK_CONTRACT_CONSTANT_CONTRACT`;
ALTER TABLE `contract_data` DROP KEY `IDX_CONTRACT_CONSTANT_CONTRACT`;
ALTER TABLE `contract_data` ADD KEY `IDX_CONTRACT_DATA_CONTRACT` (`contract`);
ALTER TABLE `contract_data` ADD CONSTRAINT `FK_CONTRACT_DATA_CONTRACT` FOREIGN KEY (`contract`) REFERENCES `contract` (`id`);

ALTER TABLE `contract_deduction` DROP FOREIGN KEY `FK_DEDUCTION_CONTRACT`;
ALTER TABLE `contract_deduction` DROP KEY `IDX_DEDUCTION_CONTRACT`;
ALTER TABLE `contract_deduction` ADD KEY `IDX_CONTRACT_DEDUCTION_CONTRACT` (`contract`);
ALTER TABLE `contract_deduction` ADD CONSTRAINT `FK_CONTRACT_DEDUCTION_CONTRACT` FOREIGN KEY (`contract`) REFERENCES `contract` (`id`);

ALTER TABLE `contract_embargo` DROP FOREIGN KEY `FK_EMBARGO_CONTRACT`;
ALTER TABLE `contract_embargo` DROP KEY `IDX_EMBARGO_CONTRACT`;
ALTER TABLE `contract_embargo` ADD KEY `IDX_CONTRACT_EMBARGO_CONTRACT` (`contract`);
ALTER TABLE `contract_embargo` ADD CONSTRAINT `FK_CONTRACT_EMBARGO_CONTRACT` FOREIGN KEY (`contract`) REFERENCES `contract` (`id`);

ALTER TABLE `contract_leave` DROP FOREIGN KEY `FK_LEAVE_CONTRACT`;
ALTER TABLE `contract_leave` DROP KEY `IDX_LEAVE_CONTRACT`;
ALTER TABLE `contract_leave` ADD KEY `IDX_CONTRACT_LEAVE_CONTRACT` (`contract`);
ALTER TABLE `contract_leave` ADD CONSTRAINT `FK_CONTRACT_LEAVE_CONTRACT` FOREIGN KEY (`contract`) REFERENCES `contract` (`id`);

ALTER TABLE `contract_payment` DROP FOREIGN KEY `FK_PAYMENT_CONTRACT`;
ALTER TABLE `contract_payment` DROP KEY `IDX_PAYMENT_CONTRACT`;
ALTER TABLE `contract_payment` ADD KEY `IDX_CONTRACT_PAYMENT_CONTRACT` (`contract`);
ALTER TABLE `contract_payment` ADD CONSTRAINT `FK_CONTRACT_PAYMENT_CONTRACT` FOREIGN KEY (`contract`) REFERENCES `contract` (`id`);

ALTER TABLE `creditor` DROP FOREIGN KEY `creditor_ibfk_1`;
ALTER TABLE `creditor` ADD CONSTRAINT `FK_CREDITOR_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`);
ALTER TABLE `creditor` DROP FOREIGN KEY `creditor_ibfk_2`;
ALTER TABLE `creditor` DROP KEY `scope`;
ALTER TABLE `creditor` ADD KEY `IDX_CREDITOR_SCOPE` (`scope`);
ALTER TABLE `creditor` ADD CONSTRAINT `FK_CREDITOR_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`);

ALTER TABLE `creditor_account` DROP FOREIGN KEY `creditor_account_ibfk_1`;
ALTER TABLE `creditor_account` DROP KEY `creditor`;
ALTER TABLE `creditor_account` ADD KEY `IDX_CREDITOR_ACCOUNT_CREDITOR` (`creditor`);
ALTER TABLE `creditor_account` ADD CONSTRAINT `FK_CREDITOR_ACCOUNT_CREDITOR` FOREIGN KEY (`creditor`) REFERENCES `creditor` (`registry`);

ALTER TABLE `customer` DROP FOREIGN KEY `customer_ibfk_1`;
ALTER TABLE `customer` ADD CONSTRAINT `FK_CUSTOMER_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`);
ALTER TABLE `customer` DROP FOREIGN KEY `customer_ibfk_2`;
ALTER TABLE `customer` DROP KEY `idx_ctmr_trff`;
ALTER TABLE `customer` ADD KEY `IDX_CUSTOMER_TARIFF` (`tariff`);
ALTER TABLE `customer` ADD CONSTRAINT `FK_CUSTOMER_TARIFF` FOREIGN KEY (`tariff`) REFERENCES `tariff` (`id`);
ALTER TABLE `customer` DROP FOREIGN KEY `customer_ibfk_3`;
ALTER TABLE `customer` DROP KEY `scope`;
ALTER TABLE `customer` ADD KEY `IDX_CUSTOMER_SCOPE` (`scope`);
ALTER TABLE `customer` ADD CONSTRAINT `FK_CUSTOMER_SCOPE` FOREIGN KEY (`scope`) REFERENCES `scope` (`id`);
ALTER TABLE `customer` DROP FOREIGN KEY `customer_fk`;
ALTER TABLE `customer` DROP KEY `segment`;
ALTER TABLE `customer` ADD KEY `IDX_CUSTOMER_SEGMENT` (`segment`);
ALTER TABLE `customer` ADD CONSTRAINT `FK_CUSTOMER_SEGMENT` FOREIGN KEY (`segment`) REFERENCES `customer_segment` (`id`);

ALTER TABLE `customer_account` DROP FOREIGN KEY `customer_account_ibfk_1`;
ALTER TABLE `customer_account` DROP KEY `customer`;
ALTER TABLE `customer_account` ADD KEY `IDX_CUSTOMER_ACCOUNT_CUSTOMER` (`customer`);
ALTER TABLE `customer_account` ADD CONSTRAINT `FK_CUSTOMER_ACCOUNT_CUSTOMER` FOREIGN KEY (`customer`) REFERENCES `customer` (`registry`);

ALTER TABLE `customer_fee` DROP FOREIGN KEY `customer_fee_ibfk_1`;
ALTER TABLE `customer_fee` DROP KEY `customer`;
ALTER TABLE `customer_fee` ADD KEY `IDX_CUSTOMER_FEE_CUSTOMER` (`customer`);
ALTER TABLE `customer_fee` ADD CONSTRAINT `FK_CUSTOMER_FEE_CUSTOMER` FOREIGN KEY (`customer`) REFERENCES `customer` (`registry`);
ALTER TABLE `customer_fee` DROP FOREIGN KEY `customer_fee_ibfk_2`;
ALTER TABLE `customer_fee` DROP KEY `item`;
ALTER TABLE `customer_fee` ADD KEY `IDX_CUSTOMER_FEE_ITEM` (`item`);
ALTER TABLE `customer_fee` ADD CONSTRAINT `FK_CUSTOMER_FEE_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`);
ALTER TABLE `customer_fee` DROP FOREIGN KEY `customer_fee_ibfk_3`;
ALTER TABLE `customer_fee` DROP KEY `workplace`;
ALTER TABLE `customer_fee` ADD KEY `IDX_CUSTOMER_FEE_WORKPLACE` (`workplace`);
ALTER TABLE `customer_fee` ADD CONSTRAINT `FK_CUSTOMER_FEE_WORKPLACE` FOREIGN KEY (`workplace`) REFERENCES `workplace` (`id`);

ALTER TABLE `daily_tracking` DROP FOREIGN KEY `FK_DT_ACT_TYPE`;
ALTER TABLE `daily_tracking` DROP KEY `IDX_DT_ACT_TYPE`;
ALTER TABLE `daily_tracking` ADD KEY `IDX_DAILY_TRACKING_ACTIVITY_TYPE` (`activity_type`);
ALTER TABLE `daily_tracking` ADD CONSTRAINT `FK_DAILY_TRACKING_ACTIVITY_TYPE` FOREIGN KEY (`activity_type`) REFERENCES `activity_type` (`id`);
ALTER TABLE `daily_tracking` DROP FOREIGN KEY `FK_DT_JOB_TYPE`;
ALTER TABLE `daily_tracking` DROP KEY `IDX_DT_JOB_TYPE`;
ALTER TABLE `daily_tracking` ADD KEY `IDX_DAILY_TRACKING_JOB_TYPE` (`job_type`);
ALTER TABLE `daily_tracking` ADD CONSTRAINT `FK_DAILY_TRACKING_JOB_TYPE` FOREIGN KEY (`job_type`) REFERENCES `job_type` (`id`);
ALTER TABLE `daily_tracking` DROP FOREIGN KEY `FK_DT_PROJECT`;
ALTER TABLE `daily_tracking` DROP KEY `IDX_DT_PROJECT`;
ALTER TABLE `daily_tracking` ADD KEY `IDX_DAILY_TRACKING_PROJECT` (`project`);
ALTER TABLE `daily_tracking` ADD CONSTRAINT `FK_DAILY_TRACKING_PROJECT` FOREIGN KEY (`project`) REFERENCES `project` (`id`);
ALTER TABLE `daily_tracking` DROP FOREIGN KEY `FK_DT_REGISTRY`;
ALTER TABLE `daily_tracking` DROP KEY `IDX_DT_REGISTRY`;
ALTER TABLE `daily_tracking` ADD KEY `IDX_DAILY_TRACKING_REGISTRY` (`registry`);
ALTER TABLE `daily_tracking` ADD CONSTRAINT `FK_DAILY_TRACKING_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`);
ALTER TABLE `daily_tracking` DROP FOREIGN KEY `FK_DT_TASK`;
ALTER TABLE `daily_tracking` DROP KEY `IDX_DT_TASK`;
ALTER TABLE `daily_tracking` ADD KEY `IDX_DAILY_TRACKING_TASK` (`task`);
ALTER TABLE `daily_tracking` ADD CONSTRAINT `FK_DAILY_TRACKING_TASK` FOREIGN KEY (`task`) REFERENCES `task` (`id`);
ALTER TABLE `daily_tracking` DROP FOREIGN KEY `FK_DT_TASK_HOLDER`;
ALTER TABLE `daily_tracking` DROP KEY `IDX_DT_TASK_HOLDER`;
ALTER TABLE `daily_tracking` ADD KEY `IDX_DAILY_TRACKING_TASK_HOLDER` (`task_holder`);
ALTER TABLE `daily_tracking` ADD CONSTRAINT `FK_DAILY_TRACKING_TASK_HOLDER` FOREIGN KEY (`task_holder`) REFERENCES `task_holder` (`registry`);

ALTER TABLE `delivery` DROP KEY `idx_dlvy_date`;
ALTER TABLE `delivery` ADD KEY `IDX_DELIVERY_ISSUE_TIME` (`issue_time`);
ALTER TABLE `delivery` DROP FOREIGN KEY `delivery_ibfk_1`;
ALTER TABLE `delivery` DROP KEY `idx_dlvy_ctmr`;
ALTER TABLE `delivery` ADD KEY `IDX_DELIVERY_CUSTOMER` (`customer`);
ALTER TABLE `delivery` ADD CONSTRAINT `FK_DELIVERY_CUSTOMER` FOREIGN KEY (`customer`) REFERENCES `customer` (`registry`);
ALTER TABLE `delivery` DROP FOREIGN KEY `delivery_ibfk_2`;
ALTER TABLE `delivery` DROP KEY `idx_dlvy_radr`;
ALTER TABLE `delivery` ADD KEY `IDX_DELIVERY_RADDRESS` (`address`);
ALTER TABLE `delivery` ADD CONSTRAINT `FK_DELIVERY_RADDRESS` FOREIGN KEY (`address`) REFERENCES `raddress` (`id`);
ALTER TABLE `delivery` ADD KEY `IDX_DELIVERY_PAY_METHOD` (`pay_method`);
ALTER TABLE `delivery` ADD CONSTRAINT `FK_DELIVERY_PAY_METHOD` FOREIGN KEY (`pay_method`) REFERENCES `pay_method` (`id`);

ALTER TABLE `delivery_detail` DROP FOREIGN KEY `delivery_detail_ibfk_1`;
ALTER TABLE `delivery_detail` DROP KEY `idx_dlvd_dlvy`;
ALTER TABLE `delivery_detail` ADD KEY `IDX_DELIVERY_DETAIL_DELIVERY` (`delivery`);
ALTER TABLE `delivery_detail` ADD CONSTRAINT `FK_DELIVERY_DETAIL_DELIVERY` FOREIGN KEY (`delivery`) REFERENCES `delivery` (`id`);
ALTER TABLE `delivery_detail` DROP FOREIGN KEY `delivery_detail_ibfk_2`;
ALTER TABLE `delivery_detail` DROP KEY `idx_dlvd_wrhs`;
ALTER TABLE `delivery_detail` ADD KEY `IDX_DELIVERY_DETAIL_WAREHOUSE` (`warehouse`);
ALTER TABLE `delivery_detail` ADD CONSTRAINT `FK_DELIVERY_DETAIL_WAREHOUSE` FOREIGN KEY (`warehouse`) REFERENCES `warehouse` (`id`);
ALTER TABLE `delivery_detail` DROP FOREIGN KEY `delivery_detail_ibfk_3`;
ALTER TABLE `delivery_detail` DROP KEY `idx_dlvd_item`;
ALTER TABLE `delivery_detail` ADD KEY `IDX_DELIVERY_DETAIL_ITEM` (`item`);
ALTER TABLE `delivery_detail` ADD CONSTRAINT `FK_DELIVERY_DETAIL_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`);
ALTER TABLE `delivery_detail` DROP FOREIGN KEY `delivery_detail_ibfk_4`;
ALTER TABLE `delivery_detail` DROP KEY `idx_dlvd_sldt`;
ALTER TABLE `delivery_detail` ADD KEY `IDX_DELIVERY_DETAIL_SALES_DETAIL` (`sales_detail`);
ALTER TABLE `delivery_detail` ADD CONSTRAINT `FK_DELIVERY_DETAIL_SALES_DETAIL` FOREIGN KEY (`sales_detail`) REFERENCES `sales_detail` (`id`);

ALTER TABLE `favorite` DROP FOREIGN KEY `favorite_fk`;
ALTER TABLE `favorite` DROP KEY `favorite_category`;
ALTER TABLE `favorite` ADD KEY `IDX_FAVORITE_FAVORITE_CATEGORY` (`favorite_category`);
ALTER TABLE `favorite` ADD CONSTRAINT `FK_FAVORITE_FAVORITE_CATEGORY` FOREIGN KEY (`favorite_category`) REFERENCES `favorite_category` (`id`);
ALTER TABLE `favorite` DROP FOREIGN KEY `favorite_fk1`;
ALTER TABLE `favorite` DROP KEY `user`;
ALTER TABLE `favorite` ADD KEY `IDX_FAVORITE_USER` (`user_id`);
ALTER TABLE `favorite` ADD CONSTRAINT `FK_FAVORITE_USER` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`);

ALTER TABLE `favorite_category` DROP FOREIGN KEY `favorite_category_fk`;
ALTER TABLE `favorite_category` DROP KEY `user`;
ALTER TABLE `favorite_category` ADD KEY `IDX_FAVORITE_CATEGORY_USER` (`user_id`);
ALTER TABLE `favorite_category` ADD CONSTRAINT `FK_FAVORITE_CATEGORY_USER` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`);

ALTER TABLE `fbatch` DROP FOREIGN KEY `fbatch_fk_1`;
ALTER TABLE `fbatch` DROP KEY `rbank`;
ALTER TABLE `fbatch` ADD KEY `IDX_FBATCH_RBANK` (`rbank`);
ALTER TABLE `fbatch` ADD CONSTRAINT `FK_FBATCH_RBANK` FOREIGN KEY (`rbank`) REFERENCES `rbank` (`id`);

ALTER TABLE `fbatch_detail` DROP FOREIGN KEY `fbatch_detail_ibfk_1`;
ALTER TABLE `fbatch_detail` DROP KEY `finance`;
ALTER TABLE `fbatch_detail` ADD KEY `IDX_FBATCH_DETAIL_FINANCE` (`finance`);
ALTER TABLE `fbatch_detail` ADD CONSTRAINT `FK_FBATCH_DETAIL_FINANCE` FOREIGN KEY (`finance`) REFERENCES `finance` (`id`);
ALTER TABLE `fbatch_detail` DROP FOREIGN KEY `fbatch_detail_ibfk_2`;
ALTER TABLE `fbatch_detail` DROP KEY `fbatch`;
ALTER TABLE `fbatch_detail` ADD KEY `IDX_FBATCH_DETAIL_FBATCH` (`fbatch`);
ALTER TABLE `fbatch_detail` ADD CONSTRAINT `FK_FBATCH_DETAIL_FBATCH` FOREIGN KEY (`fbatch`) REFERENCES `fbatch` (`id`);

ALTER TABLE `finance` DROP KEY `idx_finc_dtty`;
ALTER TABLE `finance` ADD KEY `IDX_FINANCE_DUE_DATE` (`due_date`);
ALTER TABLE `finance` DROP FOREIGN KEY `finance_ibfk_1`;
ALTER TABLE `finance` DROP KEY `idx_finc_rgty`;
ALTER TABLE `finance` ADD KEY `IDX_FINANCE_REGISTRY` (`registry`);
ALTER TABLE `finance` ADD CONSTRAINT `FK_FINANCE_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`);
ALTER TABLE `finance` DROP FOREIGN KEY `finance_ibfk_2`;
ALTER TABLE `finance` DROP KEY `idx_finc_pymt`;
ALTER TABLE `finance` ADD KEY `IDX_FINANCE_PAY_METHOD` (`pay_method`);
ALTER TABLE `finance` ADD CONSTRAINT `FK_FINANCE_PAY_METHOD` FOREIGN KEY (`pay_method`) REFERENCES `pay_method` (`id`);
ALTER TABLE `finance` DROP FOREIGN KEY `finance_ibfk_3`;
ALTER TABLE `finance` DROP KEY `idx_finc_bank`;
ALTER TABLE `finance` ADD KEY `IDX_FINANCE_BANK` (`bank`);
ALTER TABLE `finance` ADD CONSTRAINT `FK_FINANCE_BANK` FOREIGN KEY (`bank`) REFERENCES `bank` (`id`);
ALTER TABLE `finance` DROP FOREIGN KEY `finance_ibfk_4`;
ALTER TABLE `finance` DROP KEY `invoice`;
ALTER TABLE `finance` ADD KEY `IDX_FINANCE_INVOICE` (`invoice`);
ALTER TABLE `finance` ADD CONSTRAINT `FK_FINANCE_INVOICE` FOREIGN KEY (`invoice`) REFERENCES `invoice` (`id`);

ALTER TABLE `finance_tracking` DROP FOREIGN KEY `finance_tracking_fk`;
ALTER TABLE `finance_tracking` DROP KEY `finance`;
ALTER TABLE `finance_tracking` ADD KEY `IDX_FINANCE_TRACKING_FINANCE` (`finance`);
ALTER TABLE `finance_tracking` ADD CONSTRAINT `FK_FINANCE_TRACKING_FINANCE` FOREIGN KEY (`finance`) REFERENCES `finance` (`id`);

ALTER TABLE `fs_prof_retention` DROP FOREIGN KEY `FK_FS_PROF_RET_ENTERPRISE`;
ALTER TABLE `fs_prof_retention` DROP KEY `IDX_FS_PROF_RET_ENTERPRISE`;
ALTER TABLE `fs_prof_retention` ADD KEY `IDX_FS_PROF_RETENTION_ENTERPRISE` (`enterprise`);
ALTER TABLE `fs_prof_retention` ADD CONSTRAINT `FK_FS_PROF_RETENTION_ENTERPRISE` FOREIGN KEY (`enterprise`) REFERENCES `enterprise` (`registry`);

ALTER TABLE `fs_vat_detail` DROP FOREIGN KEY `FK_FS_DETAIL_FS_VAT`;
ALTER TABLE `fs_vat_detail` DROP KEY `IDX_FS_DETAIL_FS_VAT`;
ALTER TABLE `fs_vat_detail` ADD KEY `IDX_FS_VAT_DETAIL_FS_VAT` (`fs_vat`);
ALTER TABLE `fs_vat_detail` ADD CONSTRAINT `FK_FS_VAT_DETAIL_FS_VAT` FOREIGN KEY (`fs_vat`) REFERENCES `fs_vat` (`id`);

ALTER TABLE `geotree` DROP FOREIGN KEY `geotree_fk1`;
ALTER TABLE `geotree` DROP KEY `parent`;
ALTER TABLE `geotree` ADD KEY `IDX_GEOTREE_PARENT_GEOZONE` (`parent`);
ALTER TABLE `geotree` ADD CONSTRAINT `FK_GEOTREE_PARENT_GEOZONE` FOREIGN KEY (`parent`) REFERENCES `geozone` (`id`);
ALTER TABLE `geotree` DROP FOREIGN KEY `geotree_fk2`;
ALTER TABLE `geotree` DROP KEY `child`;
ALTER TABLE `geotree` ADD KEY `IDX_GEOTREE_CHILD_GEOZONE` (`child`);
ALTER TABLE `geotree` ADD CONSTRAINT `FK_GEOTREE_CHILD_GEOZONE` FOREIGN KEY (`child`) REFERENCES `geozone` (`id`);

ALTER TABLE `geozone_irpf_handicap` DROP FOREIGN KEY `FK_GEOZONE_IRPF_PERCENT_GEOZONE_IRPF`;
ALTER TABLE `geozone_irpf_handicap` DROP KEY `IDX_GEOZONE_IRPF_PERCENT_GEOZONE_IRPF`;
ALTER TABLE `geozone_irpf_handicap` ADD KEY `IDX_GEOZONE_IRPF_HANDICAP_GEOZONE_IRPF` (`geozone_irpf`);
ALTER TABLE `geozone_irpf_handicap` ADD CONSTRAINT `FK_GEOZONE_IRPF_HANDICAP_GEOZONE_IRPF` FOREIGN KEY (`geozone_irpf`) REFERENCES `geozone_irpf` (`id`);

ALTER TABLE `iattach` DROP FOREIGN KEY `iattach_ibfk_1`;
ALTER TABLE `iattach` DROP KEY `item`;
ALTER TABLE `iattach` ADD KEY `IDX_IATTACH_ITEM` (`item`);
ALTER TABLE `iattach` ADD CONSTRAINT `FK_IATTACH_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`);

DROP TABLE `incidence_type`;

ALTER TABLE `income` DROP FOREIGN KEY `income_ibfk_1`;
ALTER TABLE `income` DROP KEY `supplier`;
ALTER TABLE `income` ADD KEY `IDX_INCOME_SUPPLIER` (`supplier`);
ALTER TABLE `income` ADD CONSTRAINT `FK_INCOME_SUPPLIER` FOREIGN KEY (`supplier`) REFERENCES `supplier` (`registry`);
ALTER TABLE `income` DROP FOREIGN KEY `income_ibfk_2`;
ALTER TABLE `income` DROP KEY `address`;
ALTER TABLE `income` ADD KEY `IDX_INCOME_RADDRESS` (`address`);
ALTER TABLE `income` ADD CONSTRAINT `FK_INCOME_RADDRESS` FOREIGN KEY (`address`) REFERENCES `raddress` (`id`);
ALTER TABLE `income` ADD KEY `IDX_INCOME_PAY_METHOD` (`pay_method`);
ALTER TABLE `income` ADD CONSTRAINT `FK_INCOME_PAY_METHOD` FOREIGN KEY (`pay_method`) REFERENCES `pay_method` (`id`);

ALTER TABLE `income_detail` DROP FOREIGN KEY `income_detail_ibfk_2`;
ALTER TABLE `income_detail` DROP KEY `item`;
ALTER TABLE `income_detail` ADD KEY `IDX_INCOME_DETAIL_ITEM` (`item`);
ALTER TABLE `income_detail` ADD CONSTRAINT `FK_INCOME_DETAIL_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`);
ALTER TABLE `income_detail` DROP FOREIGN KEY `income_detail_ibfk_3`;
ALTER TABLE `income_detail` DROP KEY `warehouse`;
ALTER TABLE `income_detail` ADD KEY `IDX_INCOME_DETAIL_WAREHOUSE` (`warehouse`);
ALTER TABLE `income_detail` ADD CONSTRAINT `FK_INCOME_DETAIL_WAREHOUSE` FOREIGN KEY (`warehouse`) REFERENCES `warehouse` (`id`);
ALTER TABLE `income_detail` DROP FOREIGN KEY `income_detail_ibfk_4`;
ALTER TABLE `income_detail` DROP KEY `purchase_detail`;
ALTER TABLE `income_detail` ADD KEY `IDX_INCOME_DETAIL_PURCHASE_DETAIL` (`purchase_detail`);
ALTER TABLE `income_detail` ADD CONSTRAINT `FK_INCOME_DETAIL_PURCHASE_DETAIL` FOREIGN KEY (`purchase_detail`) REFERENCES `purchase_detail` (`id`);
ALTER TABLE `income_detail` DROP FOREIGN KEY `income_detail_ibfk_5`;
ALTER TABLE `income_detail` DROP KEY `income`;
ALTER TABLE `income_detail` ADD KEY `IDX_INCOME_DETAIL_INCOME` (`income`);
ALTER TABLE `income_detail` ADD CONSTRAINT `FK_INCOME_DETAIL_INCOME` FOREIGN KEY (`income`) REFERENCES `income` (`id`);


UPDATE `db_version` SET `version_number` = '6.20.0';

COMMIT;
