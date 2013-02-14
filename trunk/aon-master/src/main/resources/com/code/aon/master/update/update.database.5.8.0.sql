# Database: aon_master
# Version: Actualizacion de la version 5.8.0 a la version 5.8.1.
# Created by: girazu
# Creation Date: 12/01/2011 11:15
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



BEGIN;

CREATE TABLE `cashflow_forecast` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `payment` tinyint(1) NOT NULL default '0' COMMENT 'Indica si es un pago o un cobro',
  `description` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Descripcion',
  `start_date` date NOT NULL COMMENT 'Fecha de inicio de aplicacion',
  `due_date` date default NULL COMMENT 'Fecha final de aplicacion',
  `rbank` int(4) default NULL COMMENT 'Identificador de Banco de la Compañia',
  `amount` double(15,2) NOT NULL default '0.00' COMMENT 'Importe',
  `payment_day` double(15,2) NOT NULL default '1.00' COMMENT 'Dia de pago',
  `january` tinyint(1) NOT NULL default '0' COMMENT 'Aplicable en enero',
  `february` tinyint(1) NOT NULL default '0' COMMENT 'Aplicable en febrero',
  `march` tinyint(1) NOT NULL default '0' COMMENT 'Aplicable en marzo',
  `april` tinyint(1) NOT NULL default '0' COMMENT 'Aplicable en abril',
  `may` tinyint(1) NOT NULL default '0' COMMENT 'Aplicable en mayo',
  `june` tinyint(1) NOT NULL default '0' COMMENT 'Aplicable en junio',
  `july` tinyint(1) NOT NULL default '0' COMMENT 'Aplicable en julio',
  `august` tinyint(1) NOT NULL default '0' COMMENT 'Aplicable en agosto',
  `september` tinyint(1) NOT NULL default '0' COMMENT 'Aplicable en septiembre',
  `october` tinyint(1) NOT NULL default '0' COMMENT 'Aplicable en octubre',
  `november` tinyint(1) NOT NULL default '0' COMMENT 'Aplicable en noviembre',
  `december` tinyint(1) NOT NULL default '0' COMMENT 'Aplicable en diciembre',
  PRIMARY KEY  (`id`),
  KEY `IDX_CASHFLOW_FORECAST_RBANK` (`rbank`),
  CONSTRAINT `FK_CASHFLOW_FORECAST_RBANK` FOREIGN KEY (`rbank`) REFERENCES `rbank` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Prevision de tesoreria';

DROP FUNCTION IF EXISTS `facturasCategoriaFecha`;

DROP FUNCTION IF EXISTS `facturasGrupoCategoriaFecha`;

DROP FUNCTION IF EXISTS `inventarioCategoriaFecha`;

DROP FUNCTION IF EXISTS `inventarioGrupoCategoriaFecha`;

UPDATE `finance` SET `concept` = 
	(SELECT CONCAT(ELT((`type` + 1),"R","E","R","G"),"-",IF(`series` IS NULL OR `series` = "","",CONCAT(`series`,"/")),LPAD(""+`number`,6,"0")) 
		FROM `invoice` WHERE `invoice`.`id` = `finance`.`invoice`)
	WHERE `invoice` IS NOT NULL
	AND concept <> 
		(SELECT CONCAT(ELT((`type` + 1),"R","E","R","G"),"-",IF(`series` IS NULL OR `series` = "","",CONCAT(`series`,"/")),LPAD(""+`number`,6,"0")) 
			FROM `invoice` WHERE `invoice`.`id` = `finance`.`invoice`);


UPDATE `db_version` SET `version_number` = '5.8.1';

COMMIT;
