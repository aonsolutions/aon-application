# Database: aon_master
# Version: Actualizacion de la version 6.19.0 a la version 6.19.1.
# Created by: rtrepiana
# Comentarios: .


BEGIN;


ALTER TABLE `bonus_concept` MODIFY `expression` varchar(512) collate latin1_spanish_ci default NULL COMMENT 'Importe';
ALTER TABLE `bonus_concept` MODIFY `description` varchar(256) collate latin1_spanish_ci default NULL COMMENT 'Descripcion';

UPDATE `db_version` SET `version_number` = '6.19.1';

COMMIT;
