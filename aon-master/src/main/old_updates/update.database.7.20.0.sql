# Database: aon_master
# Version: Actualizacion de la version 7.20.0 a la version 7.20.1.
# Created by: girazu
# Creation Date: 25/06/2013 16:45
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.

BEGIN;

ALTER TABLE `workplace` ADD `customer` int(4) DEFAULT NULL COMMENT 'Identificador del Cliente' AFTER `address`; 
ALTER TABLE `workplace` ADD KEY `IDX_WORKPLACE_CUSTOMER` (`customer`); 
ALTER TABLE `workplace` ADD CONSTRAINT `FK_WORKPLACE_CUSTOMER` FOREIGN KEY (`customer`) REFERENCES `customer`(`registry`); 
UPDATE `workplace` SET `customer` = (SELECT `customer` FROM `hotel` WHERE `workplace` = `workplace`.`id`);

ALTER TABLE `hotel` DROP FOREIGN KEY `FK_HOTEL_CUSTOMER`;
ALTER TABLE `hotel` DROP KEY `IDX_HOTEL_CUSTOMER`;
ALTER TABLE `hotel` DROP `customer`;

ALTER TABLE `pos` ADD `num_rows` int(4) DEFAULT '0' COMMENT 'Numero de filas en la vista tipo Hosteleria' AFTER `display_mode`;
ALTER TABLE `pos` ADD `num_cols` int(4) DEFAULT '0' COMMENT 'Numero de columnas en la vista tipo Hosteleria' AFTER `num_rows`;

CREATE TABLE `pos_catalogue` (
  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `pos` int(4) NOT NULL COMMENT 'Identificador del TPV',
  `catalogue` int(4) NOT NULL COMMENT 'Identificador del Catalogo',
  PRIMARY KEY (`id`),
  KEY `IDX_POS_CATALOGUE_CATALOGUE` (`catalogue`),
  KEY `IDX_POS_CATALOGUE_DOMAIN` (`domain`),
  KEY `IDX_POS_CATALOGUE_POS` (`pos`),
  CONSTRAINT `FK_POS_CATALOGUE_CATALOGUE` FOREIGN KEY (`catalogue`) REFERENCES `catalogue` (`id`),
  CONSTRAINT `FK_POS_CATALOGUE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_POS_CATALOGUE_POS` FOREIGN KEY (`pos`) REFERENCES `pos` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Catalogos asociados al TPV';

ALTER TABLE `target_item` DROP FOREIGN KEY `FK_TARGET_ITEM_TARGET`;
ALTER TABLE `target_item` DROP KEY `IDX_TARGET_ITEM_TARGET`;
ALTER TABLE `target_item` DROP FOREIGN KEY `FK_TARGET_ITEM_ITEM`;
ALTER TABLE `target_item` DROP KEY `IDX_TARGET_ITEM_ITEM`;
ALTER TABLE `target_item` DROP FOREIGN KEY `FK_TARGET_ITEM_DOMAIN`;
ALTER TABLE `target_item` DROP KEY `IDX_TARGET_ITEM_DOMAIN`;
ALTER TABLE `target_item` RENAME `ritem`;
ALTER TABLE `ritem` COMMENT 'Articulos interesados por Personas o Empresas';
ALTER TABLE `ritem` CHANGE `target` `registry` int(4) NOT NULL COMMENT 'Identificador de Persona o Empresa';
ALTER TABLE `ritem` ADD KEY `IDX_RITEM_DOMAIN` (`domain`);
ALTER TABLE `ritem` ADD CONSTRAINT `FK_RITEM_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);
ALTER TABLE `ritem` ADD KEY `IDX_RITEM_ITEM` (`item`);
ALTER TABLE `ritem` ADD CONSTRAINT `FK_RITEM_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`);
ALTER TABLE `ritem` ADD KEY `IDX_RITEM_REGISTRY` (`registry`);
ALTER TABLE `ritem` ADD CONSTRAINT `FK_RITEM_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`);

ALTER TABLE `target_profile` DROP FOREIGN KEY `FK_TARGET_PROFILE_TARGET`;
ALTER TABLE `target_profile` DROP KEY `IDX_TARGET_PROFILE_TARGET`;
ALTER TABLE `target_profile` DROP FOREIGN KEY `FK_TARGET_PROFILE_QUESTION`;
ALTER TABLE `target_profile` DROP KEY `IDX_TARGET_PROFILE_QUESTION`;
ALTER TABLE `target_profile` DROP FOREIGN KEY `FK_TARGET_PROFILE_DOMAIN`;
ALTER TABLE `target_profile` DROP KEY `IDX_TARGET_PROFILE_DOMAIN`;
ALTER TABLE `target_profile` RENAME `rprofile`;
ALTER TABLE `rprofile` COMMENT 'Perfiles de Personas o Empresas';
ALTER TABLE `rprofile` CHANGE `target` `registry` int(4) NOT NULL COMMENT 'Identificador de Persona o Empresa';
ALTER TABLE `rprofile` ADD KEY `IDX_RPROFILE_DOMAIN` (`domain`);
ALTER TABLE `rprofile` ADD CONSTRAINT `FK_RPROFILE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);
ALTER TABLE `rprofile` ADD KEY `IDX_RPROFILE_QUESTION` (`question`);
ALTER TABLE `rprofile` ADD CONSTRAINT `FK_RPROFILE_QUESTION` FOREIGN KEY (`question`) REFERENCES `question` (`id`);
ALTER TABLE `rprofile` ADD KEY `IDX_RPROFILE_REGISTRY` (`registry`);
ALTER TABLE `rprofile` ADD CONSTRAINT `FK_RPROFILE_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`);

ALTER TABLE `target_seller` DROP FOREIGN KEY `FK_TARGET_SELLER_TARGET`;
ALTER TABLE `target_seller` DROP KEY `IDX_TARGET_SELLER_TARGET`;
ALTER TABLE `target_seller` DROP FOREIGN KEY `FK_TARGET_SELLER_SELLER`;
ALTER TABLE `target_seller` DROP KEY `IDX_TARGET_SELLER_SELLER`;
ALTER TABLE `target_seller` DROP FOREIGN KEY `FK_TARGET_SELLER_DOMAIN`;
ALTER TABLE `target_seller` DROP KEY `IDX_TARGET_SELLER_DOMAIN`;
ALTER TABLE `target_seller` RENAME `rseller`;
ALTER TABLE `rseller` COMMENT 'Comerciales relacionados con Personas o Empresas';
ALTER TABLE `rseller` CHANGE `target` `registry` int(4) NOT NULL COMMENT 'Identificador de Persona o Empresa';
ALTER TABLE `rseller` ADD KEY `IDX_RSELLER_DOMAIN` (`domain`);
ALTER TABLE `rseller` ADD CONSTRAINT `FK_RSELLER_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);
ALTER TABLE `rseller` ADD KEY `IDX_RSELLER_SELLER` (`seller`);
ALTER TABLE `rseller` ADD CONSTRAINT `FK_RSELLER_SELLER` FOREIGN KEY (`seller`) REFERENCES `seller` (`registry`);
ALTER TABLE `rseller` ADD KEY `IDX_RSELLER_REGISTRY` (`registry`);
ALTER TABLE `rseller` ADD CONSTRAINT `FK_RSELLER_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`);

ALTER TABLE `target_supplier` DROP FOREIGN KEY `FK_TARGET_SUPPLIER_TARIFF`;
ALTER TABLE `target_supplier` DROP KEY `IDX_TARGET_SUPPLIER_TARIFF`;
ALTER TABLE `target_supplier` DROP FOREIGN KEY `FK_TARGET_SUPPLIER_TARGET`;
ALTER TABLE `target_supplier` DROP KEY `IDX_TARGET_SUPPLIER_TARGET`;
ALTER TABLE `target_supplier` DROP FOREIGN KEY `FK_TARGET_SUPPLIER_SUPPLIER`;
ALTER TABLE `target_supplier` DROP KEY `IDX_TARGET_SUPPLIER_SUPPLIER`;
ALTER TABLE `target_supplier` DROP FOREIGN KEY `FK_TARGET_SUPPLIER_PAY_METHOD`;
ALTER TABLE `target_supplier` DROP KEY `IDX_TARGET_SUPPLIER_PAY_METHOD`;
ALTER TABLE `target_supplier` DROP FOREIGN KEY `FK_TARGET_SUPPLIER_DOMAIN`;
ALTER TABLE `target_supplier` DROP KEY `IDX_TARGET_SUPPLIER_DOMAIN`;
ALTER TABLE `target_supplier` DROP FOREIGN KEY `FK_TARGET_SUPPLIER_BANK`;
ALTER TABLE `target_supplier` DROP KEY `IDX_TARGET_SUPPLIER_BANK`;
ALTER TABLE `target_supplier` RENAME `rsupplier`;
ALTER TABLE `rsupplier` COMMENT 'Proveedores relacionados con Personas o Empresas';
ALTER TABLE `rsupplier` CHANGE `target` `registry` int(4) NOT NULL COMMENT 'Identificador de Persona o Empresa';
ALTER TABLE `rsupplier` ADD KEY `IDX_RSUPPLIER_BANK` (`bank`);
ALTER TABLE `rsupplier` ADD CONSTRAINT `FK_RSUPPLIER_BANK` FOREIGN KEY (`bank`) REFERENCES `bank` (`id`);
ALTER TABLE `rsupplier` ADD KEY `IDX_RSUPPLIER_DOMAIN` (`domain`);
ALTER TABLE `rsupplier` ADD CONSTRAINT `FK_RSUPPLIER_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`);
ALTER TABLE `rsupplier` ADD KEY `IDX_RSUPPLIER_PAY_METHOD` (`pay_method`);
ALTER TABLE `rsupplier` ADD CONSTRAINT `FK_RSUPPLIER_PAY_METHOD` FOREIGN KEY (`pay_method`) REFERENCES `pay_method` (`id`);
ALTER TABLE `rsupplier` ADD KEY `IDX_RSUPPLIER_REGISTRY` (`registry`);
ALTER TABLE `rsupplier` ADD CONSTRAINT `FK_RSUPPLIER_REGISTRY` FOREIGN KEY (`registry`) REFERENCES `registry` (`id`);
ALTER TABLE `rsupplier` ADD KEY `IDX_RSUPPLIER_SUPPLIER` (`supplier`);
ALTER TABLE `rsupplier` ADD CONSTRAINT `FK_RSUPPLIER_SUPPLIER` FOREIGN KEY (`supplier`) REFERENCES `supplier` (`registry`);
ALTER TABLE `rsupplier` ADD KEY `IDX_RSUPPLIER_TARIFF` (`tariff`);
ALTER TABLE `rsupplier` ADD CONSTRAINT `FK_RSUPPLIER_TARIFF` FOREIGN KEY (`tariff`) REFERENCES `tariff` (`id`);


UPDATE `db_version` SET `version_number` = '7.20.1';

COMMIT;
