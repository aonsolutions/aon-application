# Database: aon_master
# Version: Actualizacion de la version 6.18.1 a la version 6.18.2.
# Created by: rtrepiana
# Creation Date: 03/10/2011 07:00
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



BEGIN;

ALTER TABLE `irpf_data` 
	ADD `request_irpf` double(15,2) default NULL COMMENT 'Tipo de retención solicitado';

ALTER TABLE `irpf_data` 
	ADD `contract_type` tinyint(2)  NOT NULL default 0 COMMENT 'Contrato o relación';

ALTER TABLE `irpf_data` 
	ADD `ceuta_melilla` tinyint(1)  NOT NULL default 0 COMMENT 'Los datos anteriores corresponden a rendimientos obtenidos en Ceuta o Melilla';

UPDATE `db_version` SET `version_number` = '6.18.2';

COMMIT;
