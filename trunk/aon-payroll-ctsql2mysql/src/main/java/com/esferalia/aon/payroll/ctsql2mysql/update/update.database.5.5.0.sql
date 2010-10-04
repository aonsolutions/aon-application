# Database: aon_master
# Version: Actualizacion de la version 5.5.0 a la version ?.?.?.
# Created by: rtrepiana
# Creation Date: 04/10/2010 



CREATE TABLE `contract_model` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `code` varchar(6) collate latin1_spanish_ci NOT NULL COMMENT 'Codigo del modelo',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion del modelo',
  `document` mediumblob COMMENT 'Impreso (.pdf) del modelo.',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Modalidades de cotrato';



ALTER TABLE `contract_type` DROP `duration`   ;
ALTER TABLE `contract_type` DROP `working_day`   ;
ALTER TABLE `contract_type` DROP `ccc_type`   ;

ALTER TABLE `contract_type` ADD `contract_model` int(4) NOT NULL COMMENT 'Modalidad de contrato'  ;
ALTER TABLE `contract_type` ADD CONSTRAINT FK_CONTRACT_TYPE_CONTRACT_MODEL FOREIGN KEY (`contract_model`) REFERENCES `contract_model` (`id`);






ROLLBACK;