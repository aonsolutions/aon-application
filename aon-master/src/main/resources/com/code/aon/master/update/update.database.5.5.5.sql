# Database: aon_master
# Version: Actualizacion de la version 5.5.5 a la version 5.6.0.
# Created by: girazu
# Creation Date: 31/10/2010 17:13
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



BEGIN;

ALTER TABLE `bank_statement` MODIFY `description` varchar(80) collate latin1_spanish_ci default NULL COMMENT 'Descripcion';

CREATE TABLE `account_entry_bank_statement` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `account_entry` int(4) NOT NULL COMMENT 'Identificador de Asiento',
  `bank_statement` int(4) NOT NULL COMMENT 'Identificador de Extracto bancario',
  PRIMARY KEY  (`id`),
  KEY `IDX_ACC_ENTRY_BANK_STATEMENT_ACC_ENTRY` (`account_entry`),
  KEY `IDX_ACC_ENTRY_BANK_STATEMENT_BANK_STATEMENT` (`bank_statement`),
  CONSTRAINT `FK_ACC_ENTRY_BANK_STATEMENT_ACC_ENTRY` FOREIGN KEY (`account_entry`) REFERENCES `account_entry` (`id`),
  CONSTRAINT `FK_ACC_ENTRY_BANK_STATEMENT_BANK_STATEMENT` FOREIGN KEY (`bank_statement`) REFERENCES `bank_statement` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Relacion entre Asientos Contables y Extractos bancarios';


UPDATE `db_version` SET `version_number` = '5.6.0';

COMMIT;
