# Database: aon_master
# Version: Actualizacion de la version 5.7.1 a la version 5.8.0.
# Created by: girazu
# Creation Date: 17/12/2010 13:38
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



BEGIN;

ALTER TABLE `bank_statement_link` ADD `source_date` date default NULL COMMENT 'Fecha del origen' AFTER `source_id`;

ALTER TABLE `finance_tracking` ADD `bank_statement_link` int(4) default NULL COMMENT 'Identificador de la Linea del Extracto bancario' AFTER `rbank`;

ALTER TABLE `finance_tracking` ADD KEY `IDX_FINANCE_TRACKING_BANK_STATEMENT_LINK` (`bank_statement_link`);

ALTER TABLE `finance_tracking` ADD CONSTRAINT `FK_FINANCE_TRACKING_BANK_STATEMENT_LINK` FOREIGN KEY (`bank_statement_link`) REFERENCES `bank_statement_link` (`id`);

ALTER TABLE `fbatch` ADD `bank_statement_link` int(4) default NULL COMMENT 'Identificador de la Linea del Extracto bancario' AFTER `rbank`;

ALTER TABLE `fbatch` ADD KEY `IDX_FBATCH_BANK_STATEMENT_LINK` (`bank_statement_link`);

ALTER TABLE `fbatch` ADD CONSTRAINT `FK_FBATCH_BANK_STATEMENT_LINK` FOREIGN KEY (`bank_statement_link`) REFERENCES `bank_statement_link` (`id`);


UPDATE `db_version` SET `version_number` = '5.8.0';

COMMIT;
