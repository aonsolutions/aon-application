# Database: aon_master
# Version: Actualizacion de la version 7.23.0 a la version 7.24.0.
# Created by: girazu
# Creation Date: 09/10/2013 17:20
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.

BEGIN;

CREATE TABLE `prepayment` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
  `creditor` int(4) NOT NULL COMMENT 'Identificador del Acreedor',
  `customer` int(4) NOT NULL COMMENT 'Identificador del Cliente',
  `finance` int(4) NOT NULL COMMENT 'Identificador del Vencimiento',
  `collect` tinyint(2) default '0' COMMENT 'Localizacion del cobro del Suplido',
  `collect_id` int(4) default NULL COMMENT 'Identificador del cobro del Suplido',
  PRIMARY KEY  (`id`),
  KEY `IDX_PREPAYMENT_DOMAIN` (`domain`),
  KEY `IDX_PREPAYMENT_CREDITOR` (`creditor`),
  KEY `IDX_PREPAYMENT_CUSTOMER` (`customer`),
  KEY `IDX_PREPAYMENT_FINANCE` (`finance`),
  CONSTRAINT `FK_PREPAYMENT_CREDITOR` FOREIGN KEY (`creditor`) REFERENCES `creditor` (`registry`),
  CONSTRAINT `FK_PREPAYMENT_CUSTOMER` FOREIGN KEY (`customer`) REFERENCES `customer` (`registry`),
  CONSTRAINT `FK_PREPAYMENT_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
  CONSTRAINT `FK_PREPAYMENT_FINANCE` FOREIGN KEY (`finance`) REFERENCES `finance` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Suplidos';

ALTER TABLE `invoice_detail` ADD `prepayment` tinyint(1) default '0' COMMENT 'Indica si el Detalle de Factura es un Suplido' AFTER `taxes`;

ALTER TABLE `finance` ADD `prepayment` tinyint(1) default '0' COMMENT 'Indica si el Vencimiento es un Suplido' AFTER `payroll`;

ALTER TABLE `rdir_staff` ADD `charge_description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion del cargo de Directivo'; 

ALTER TABLE `domain` ADD UNIQUE KEY `IDX_UNQ_DOMAIN_NAME` (`name`); 

ALTER TABLE `company` ADD UNIQUE KEY `IDX_UNQ_COMPANY_DOMAIN` (`domain`); 

ALTER TABLE `user` ADD UNIQUE KEY `IDX_UNQ_USER_DOMAIN_LOGIN` (`domain`, `login`);

ALTER TABLE `domain_application` ADD UNIQUE KEY `IDX_UNQ_DOMAIN_APPLICATION` (`domain`, `application`); 

INSERT IGNORE INTO `account` (`domain`, `code`, `description`, `entryEnabled`, `level`, `active`) 
	SELECT `domain`, '5559', 'Suplidos y pagos a cuenta.', 0, 4, 1 FROM `account` WHERE `code` = '555';
INSERT IGNORE INTO `account` (`domain`, `code`, `description`, `entryEnabled`, `level`, `active`) 
	SELECT `domain`, '555900000', `description`, 1, 5, 1 FROM `account` WHERE `code` = '5559';
INSERT IGNORE INTO `app_param` (`domain`, `name`, `value`) SELECT `domain`, 'ACC_DEFAULT_PREPAYMENT_ACC', id FROM `account` WHERE `code` = '555900000';


UPDATE `db_version` SET `version_number` = '7.24.0';

COMMIT;
