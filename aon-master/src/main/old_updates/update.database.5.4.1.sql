# Database: aon_master
# Version: Actualizacion de la version 5.4.1 a la version 5.4.2.
# Created by: girazu
# Creation Date: 14/09/2010 17:13
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



BEGIN;

ALTER TABLE `account_entry_detail` ADD `document_number` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Numero de documento asociado';

CREATE TABLE `pm_type_detail` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `type` tinyint(2) NOT NULL default '0' COMMENT 'Tipo de Forma de Pago',
  `description` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Descripcion del detalle',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalles por Tipo de Forma de Pago';

CREATE TABLE `pm_type_detail_account` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `pm_type_detail` int(4) NOT NULL default '0' COMMENT 'Identificador del Detalle por Tipo de Forma de Pago',
  `account` char(12) collate latin1_spanish_ci NOT NULL COMMENT 'Identificador de la Cuenta Contable',
  PRIMARY KEY  (`id`),
  KEY IDX_PM_TYPE_DETAIL_ACCOUNT_DETAIL (`pm_type_detail`),
  KEY IDX_PM_TYPE_DETAIL_ACCOUNT_ACCOUNT (`account`),
  CONSTRAINT FK_PM_TYPE_DETAIL_ACCOUNT_DETAIL FOREIGN KEY (`pm_type_detail`) REFERENCES `pm_type_detail` (`id`),
  CONSTRAINT FK_PM_TYPE_DETAIL_ACCOUNT_ACCOUNT FOREIGN KEY (`account`) REFERENCES `account` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Cuentas Contables de Entidades Bancarias';

ALTER TABLE `finance_tracking` ADD `rbank` int(4) default NULL COMMENT 'Identificador de la Cuenta Bancaria de la Compañia'  AFTER `description`;

ALTER TABLE `finance_tracking` ADD KEY IDX_FINANCE_TRACKING_RBANK (`rbank`);

ALTER TABLE `finance_tracking` ADD CONSTRAINT FK_FINANCE_TRACKING_RBANK FOREIGN KEY (`rbank`) REFERENCES `rbank` (`id`);

ALTER TABLE `finance_tracking` ADD `pm_type_detail` int(4) default NULL COMMENT 'Identificador del Detalle por Tipo de Forma de Pago' AFTER `description`;

ALTER TABLE `finance_tracking` ADD KEY IDX_FINANCE_TRACKING_PM_TYPE_DETAIL (`pm_type_detail`);

ALTER TABLE `finance_tracking` ADD CONSTRAINT FK_FINANCE_TRACKING_PM_TYPE_DETAIL FOREIGN KEY (`pm_type_detail`) REFERENCES `pm_type_detail` (`id`);

ALTER TABLE `finance_tracking` ADD `recorded` tinyint(1) NOT NULL default '0' COMMENT 'Indica si esta contabilizado o no';

UPDATE `pay_method` SET `type` = 7 WHERE `type` = 0;

UPDATE `pay_method` SET `type` = 0 WHERE `type` = 1;

UPDATE `pay_method` SET `type` = 1 WHERE `type` = 2;

UPDATE `pay_method` SET `type` = 2 WHERE `type` = 3;

UPDATE `pay_method` SET `type` = 3 WHERE `type` = 4;

UPDATE `pay_method` SET `type` = 4 WHERE `type` = 5;

UPDATE `pay_method` SET `type` = 5 WHERE `type` = 6;

UPDATE `pay_method` SET `type` = 6 WHERE `type` = 7;

UPDATE `finance_tracking` SET `type` = 1 WHERE `type` = 2;

UPDATE `finance_tracking` SET `type` = 2 WHERE `type` = 3;

UPDATE `finance_tracking` SET `type` = 3 WHERE `type` = 5;

UPDATE `finance_tracking` SET `type` = 4 WHERE `type` = 6;

UPDATE `finance_tracking` SET `recorded` = 1 where `type` = 1 or `type` = 2;

UPDATE `finance_tracking` SET `description` = REPLACE(`description`, "Remesado en ", "");

UPDATE `finance_tracking` SET `description` = REPLACE(`description`, "Contabilizado en ", "");

UPDATE `finance_tracking` SET `rbank` = 
	(SELECT `rbank` FROM `rbank_account` WHERE `account` = 
    	(SELECT `account` FROM `account_entry_detail` WHERE `account` like '5%' AND `account_entry` = 
        	(SELECT `account_entry` FROM `account_entry_finance_tracking` WHERE `finance_tracking` = `finance_tracking`.`id`
        	)
        )
    );

UPDATE `finance_tracking` SET `rbank` = 
	(SELECT MAX(`rbank`) FROM `fbatch` WHERE `id` IN
    	(SELECT `fbatch` FROM `fbatch_detail` WHERE `finance` = `finance_tracking`.`finance`
        )
    ) 
	WHERE `recorded` = 1 AND `rbank` IS NULL;

UPDATE `account_entry_detail` SET `document_number` = 
	(SELECT CONCAT(ELT((`type` + 1),"R","E","R","G"),"-",`series`,"/",LPAD(""+`number`,6,"0")) FROM `invoice` WHERE `id` = 
		(SELECT MAX(`invoice`) FROM `account_entry_invoice` WHERE `account_entry` = `account_entry_detail`.`account_entry`
		)
	);

UPDATE `account_entry_detail` SET `document_number` = CONCAT("E-",TRIM(SUBSTRING_INDEX(SUBSTRING_INDEX(`concept`, ":", -1), "/", 1)),"/",LPAD(SUBSTRING_INDEX(SUBSTRING_INDEX(`concept`, ":", -1), "/", -1),6,"0"))
	WHERE `account_entry` IN
		(SELECT `id` FROM `account_entry` WHERE `entry_type` in (14,21)
	    )
    AND `concept` like '%Fra:%'
    AND `concept` not like '%(%';

UPDATE `account_entry_detail` SET `document_number` = CONCAT("E-",TRIM(SUBSTRING_INDEX(SUBSTRING_INDEX(`concept`, ":", -2), "/", 1)),"/",LPAD(TRIM(SUBSTRING_INDEX(SUBSTRING_INDEX(`concept`, "(", 1), "/", -1)),6,"0"))
	WHERE `account_entry` IN
		(SELECT `id` FROM `account_entry` WHERE `entry_type` in (14,21)
	    )
    AND `concept` like '%Fra:%'
    AND `concept` like '%(R%';

UPDATE `account_entry_detail` SET `document_number` = 
	(SELECT MAX(CONCAT(ELT((`type` + 1),"R","E","R","G"),"-",`series`,"/",LPAD(""+`number`,6,"0"))) 
    	FROM `invoice` WHERE `type` != 1 AND `reference_code` = TRIM(SUBSTRING_INDEX(`account_entry_detail`.`concept`, ":", -1)))
	WHERE `account_entry` IN
		(SELECT `id` FROM `account_entry` WHERE `entry_type` in (13,20)
	    )
    AND `concept` like '%Fra:%'
    AND `concept` not like '%(%';


UPDATE `db_version` SET `version_number` = '5.4.2';

COMMIT;
