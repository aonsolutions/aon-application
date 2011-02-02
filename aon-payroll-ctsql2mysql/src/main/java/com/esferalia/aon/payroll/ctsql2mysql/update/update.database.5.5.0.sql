# Database: aon_master
# Version: Actualizacion de la version 5.5.0 a la version ?.?.?.
# Created by: rtrepiana
# Creation Date: 04/10/2010 


ALTER TABLE `contract` 	DROP FOREIGN KEY `FK_CONTRACT_TYPE`;
ALTER TABLE `contract`  DROP COLUMN `type`;
DROP TABLE `contract_type`;



CREATE TABLE `contract_type` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `code` int(4) NOT NULL COMMENT 'Código del contrato',
  `model` varchar(5) collate latin1_spanish_ci default NULL COMMENT 'Modelo',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion',
  `conditions` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Condiciones',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Tipos de cotrato';

CREATE TABLE `bonus` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion',
  `formula` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Fórmula',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Bonificaciones';

ALTER TABLE `contract`  ADD COLUMN `contract_type` int(4) NOT NULL COMMENT 'Tipo de contrato';
ALTER TABLE `contract` 	ADD CONSTRAINT `FK_CONTRACT_CONTRACT_TYPE` FOREIGN KEY (`contract_type`) REFERENCES `contract_type` (`id`);
ALTER TABLE `contract` 	ADD COLUMN `document` mediumblob COMMENT 'Impreso (.pdf) del comtrato.';



