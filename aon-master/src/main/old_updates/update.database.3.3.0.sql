# Database: aon_master
# Version: Actualizacion de la version 3.3.0 a la version 3.4.0.
# Created by: girazu
# Creation Date: 21/05/2009 10:30
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



CREATE TABLE `balance` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `name` varchar(64) collate latin1_spanish_ci NOT NULL default '' COMMENT 'Nombre del Balance',
  `removable` tinyint(1) default '0' COMMENT 'Indica se puede ser borrado por el usuario',
  `type` tinyint(2) default '0' COMMENT 'Tipo de Balance',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Balances';

CREATE TABLE `balance_detail` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `balance` int(4) NOT NULL COMMENT 'Identificador del Balance',
  `code` varchar(16) collate latin1_spanish_ci default NULL COMMENT 'Codigo del Detalle en el Balance',
  `description` varchar(128) collate latin1_spanish_ci DEFAULT NULL COMMENT 'Descripción del detalle de balance',
  `accounts` text collate latin1_spanish_ci COMMENT 'Cuentas separadas por comas, que forman el acumulado.',
  `sortKey` int(4) default '0' COMMENT 'Orden el que aparecera en el listado.',
  `title` tinyint(1) NOT NULL default '0' COMMENT '',
  `internal_calculation` tinyint(1) NOT NULL default '0' COMMENT 'Indica si es un calculo interno, es decir si el contenido\r\n                de accounts son referencias a la columna -code- de esta tabla',
  `visible` tinyint(1) NOT NULL default '1' COMMENT 'Si aparece o no en la impresion.',
  `zeroFlag` tinyint(1) NOT NULL default '0' COMMENT 'Flag que se activa cuando la cuenta o cuentas tienen valor 0.',
  `creditNature` tinyint(1) NOT NULL default '0' COMMENT 'Si es verdadero se hace una haber menos debe de las cuentas indicadas',
  PRIMARY KEY (`id`),
  KEY `idx_balance` (`balance`),
  CONSTRAINT `fk_balance_detail_balance` FOREIGN KEY (`balance`) REFERENCES `balance` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Detalles del Balace';


UPDATE `db_version` SET `version_number` = '3.4.0';

COMMIT;
