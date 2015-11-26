# Database: aon_master
# Version: Actualizacion de la version 6.0.0 a la version 6.0.1.
# Created by: girazu
# Creation Date: 18/02/2011 10:44
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



BEGIN;

ALTER TABLE `registry` MODIFY `document_type` tinyint(2) default '0' COMMENT 'Tipo de documento (NIF, CIF...)';

ALTER TABLE `registry` MODIFY `document_country` varchar(2) collate latin1_spanish_ci default 'ES' COMMENT 'Pais del documento';

ALTER TABLE `registry` MODIFY `nationality` varchar(2) collate latin1_spanish_ci default 'ES' COMMENT 'Nacionalidad';

UPDATE `registry` SET `document_type` = 2 WHERE SUBSTR(`document`, 1, 1) IN ('X','Y','Z');

UPDATE `registry` SET `document_type` = 1 WHERE SUBSTR(`document`, 1, 1) BETWEEN 'A' AND 'J' OR SUBSTR(`document`, 1, 1) BETWEEN 'N' AND 'W';

UPDATE `registry` SET `document_type` = 0 WHERE `document_type` IS NULL;

UPDATE `registry` SET `document_country` = 'ES';

UPDATE `registry` SET `nationality` = 'ES';
 
UPDATE `registry` SET `type` = 0 WHERE `document_type` != 1; 

UPDATE `registry` SET `type` = 1 WHERE `document_type` = 1; 

ALTER TABLE `contract_leave` DROP COLUMN `relapse`;

ALTER TABLE `contract_leave` ADD COLUMN `parent` int(4) DEFAULT NULL COMMENT 'Baja origen, si es recaida';

ALTER TABLE `contract_leave` ADD KEY `IDX_CONTRACT_LEAVE_CONTRACT_LEAVE` (`parent`);

ALTER TABLE `contract_leave` ADD CONSTRAINT `FK_CONTRACT_LEAVE_CONTRACT_LEAVE` FOREIGN KEY (`parent`) REFERENCES `contract_leave` (`id`);


UPDATE `db_version` SET `version_number` = '6.0.1';

COMMIT;
