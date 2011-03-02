# Database: aon_master
# Version: Actualizacion de la version 6.0.1 a la version 6.1.0.
# Created by: girazu
# Creation Date: 23/02/2011 12:38
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



BEGIN;

ALTER TABLE `enterprise` DROP FOREIGN KEY `FK_ENTERPRISE_AGREEMENT`;

ALTER TABLE `enterprise` DROP INDEX `IDX_ENTERPRISE_AGREEMENT`;

ALTER TABLE `enterprise` DROP COLUMN `agreement`;

CREATE TABLE `enterprise_agreement` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `enterprise` int(4) NOT NULL COMMENT 'Identificador de la Empresa',
  `agreement` int(4) NOT NULL COMMENT 'Identificador del Convenio',
  PRIMARY KEY  (`id`),
  KEY `IDX_ENTERPRISE_AGREEMENT_ENTERPRISE` (`enterprise`),
  KEY `IDX_ENTERPRISE_AGREEMENT_AGREEMENT` (`agreement`),
  CONSTRAINT `FK_ENTERPRISE_AGREEMENT_ENTERPRISE` FOREIGN KEY (`enterprise`) REFERENCES `enterprise` (`registry`),
  CONSTRAINT `FK_ENTERPRISE_AGREEMENT_AGREEMENT` FOREIGN KEY (`agreement`) REFERENCES `agreement` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Convenio de la Empresa';


UPDATE  `deduction_concept` SET description='ROUND(CGC/BASE_CGC*100)+''%''' WHERE id= 1 ; 
UPDATE  `deduction_concept` SET description='ROUND(CGP/BASE_CGP*100)+''%''' WHERE id= 2 ; 
UPDATE  `deduction_concept` SET description='ROUND(DESMP/BASE_CGP*100)+''%''' WHERE id= 3 ; 
UPDATE  `deduction_concept` SET description='ROUND(FP/BASE_CGP*100)+''%''' WHERE id= 4 ; 
UPDATE  `deduction_concept` SET description='ROUND(ESTR/BASE_ESTR*100)+''%''' WHERE id= 5 ; 
UPDATE  `deduction_concept` SET description='ROUND(IRPF/BASE_IRPF*100)+''%''' WHERE id= 6 ; 

UPDATE system_deduction SET expression = 'BASE_CGP * (INDEFINIDO ? 1.55 : 1.60 )/100' WHERE id=2;

ALTER TABLE `contract` MODIFY COLUMN `registration` int(4)   DEFAULT NULL COMMENT 'Número libro de matricula';
ALTER TABLE `contract` MODIFY COLUMN `seniority_date` date DEFAULT NULL COMMENT 'Fecha de antiguedad ';

UPDATE `db_version` SET `version_number` = '6.1.0';

COMMIT;
