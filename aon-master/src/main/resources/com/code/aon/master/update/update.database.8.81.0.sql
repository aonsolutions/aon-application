# Database: aon_master
# Version: Actualizacion de la version 8.80.0 a la version 8.81.1.
# Created by: ecastellano
# Creation Date: 22/12/2016 12:30

BEGIN;

ALTER TABLE `fs_model184_detail` ADD `penalization` double(15,3) DEFAULT 0 COMMENT 'Penalizaciones';
ALTER TABLE `fs_model193_detail` ADD `declarant_nature` tinyint(1) DEFAULT 0 COMMENT 'Naturaleza del declarante';

ALTER TABLE `fs_model190_detail` ADD `perception_il` double(15,3) DEFAULT 0 COMMENT 'Percepción Integra/valoracion derivada de incapacidad laboral';
ALTER TABLE `fs_model190_detail` ADD `retention_il` double(15,3) DEFAULT 0 COMMENT 'Retenciones practicadas/ingresos a cuenta efectuados derivadas de incapacidad laboral';
ALTER TABLE `fs_model190_detail` ADD `output_retention_il` double(15,3) DEFAULT 0 COMMENT 'Ingresos a cuenta repercutidos derivados de incapacidad laboral';

UPDATE `db_version` SET `version_number` = '8.81.1';

COMMIT;
