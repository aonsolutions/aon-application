# Database: aon_master
# Version: Actualizacion de la version 7.32.1 a la version 7.35.0.
# Created by: girazu
# Creation Date: 20/03/2014 16:30

BEGIN;

ALTER TABLE `ritem` ADD `type` tinyint(2) default '0' COMMENT 'Tipo de relacion' AFTER `item`;
ALTER TABLE `ritem` ADD `code` varchar(15) collate latin1_spanish_ci default NULL COMMENT 'Codigo del Producto' AFTER `type`;
ALTER TABLE `ritem` ADD `price` double default '0' COMMENT 'Precio del Producto' AFTER `code`;
ALTER TABLE `ritem` ADD `discount_expr` varchar(32) collate latin1_spanish_ci default '0.0' COMMENT 'Descuentos del Producto' AFTER `price`;
ALTER TABLE `ritem` ADD `priority` tinyint(2) default '0' COMMENT 'Prioridad del Producto' AFTER `discount_expr`;
ALTER TABLE `ritem` ADD `workplace` int(4) default NULL COMMENT 'Identificador del Centro de Trabajo' AFTER `priority`;
ALTER TABLE `ritem` ADD KEY `IDX_RITEM_WORKPLACE` (`workplace`);
ALTER TABLE `ritem` ADD CONSTRAINT `FK_RITEM_WORKPLACE` FOREIGN KEY (`workplace`) REFERENCES `workplace` (`id`);

INSERT INTO `ritem` (`domain`,`registry`,`item`,`type`,`code`,`price`,`discount_expr`,`priority`,`workplace`,`status`) 
	SELECT `domain`,`supplier`,`item`,2,`code`,`price`,'0',`priority`,`workplace`,0 FROM `item_supplier`;

DROP TABLE `item_supplier`;

ALTER TABLE `offer` DROP FOREIGN KEY `FK_OFFER_TARIFF`;
ALTER TABLE `offer` DROP KEY `IDX_OFFER_TARIFF`;
ALTER TABLE `offer` DROP `tariff`;


UPDATE `db_version` SET `version_number` = '7.35.0';

COMMIT;
