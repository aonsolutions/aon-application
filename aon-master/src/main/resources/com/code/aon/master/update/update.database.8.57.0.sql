# Database: aon_master
# Version: Actualizacion de la version 8.57.0 a la version 8.57.1.
# Created by: ecastellano

BEGIN;

ALTER TABLE fs_model200 add `nrs_anexoIII` varchar(30) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'NRS anexo III';
ALTER TABLE fs_model200 add `just_canarias` varchar(30) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Numero de justificante Canarias';
ALTER TABLE fs_model200 add `nrs_anexoIV` varchar(30) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'NRS anexo IV';
ALTER TABLE fs_model200 add `nrs_anexoV` varchar(30) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'NRS anexo V';
ALTER TABLE fs_model200 add `bic` char(11) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'BIC - Codigo Identificador del Banco';

ALTER TABLE fs_model200_detail MODIFY `key` varchar(7) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Clave Casilla';
ALTER TABLE fs_model200_registry add `cc_value` double(15,3) DEFAULT NULL COMMENT 'Eliminacion del deterioro contable';

UPDATE `db_version` SET `version_number` = '8.57.1';

COMMIT;
