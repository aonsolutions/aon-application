# Database: aon_master
# Version: Actualizacion de la version 6.19.2 a la version 6.19.3.
# Created by: girazu
# Creation Date: 06/12/2011 21:40
# Comentarios: SE HABIA OLVIDADO ESTA ACTUALIZACION EN LA UPDATE A LA VERSION 6.19.0


BEGIN;

UPDATE `bank_statement_link` SET `source_id` = (SELECT `id` FROM `account` WHERE `code` = `bank_statement_link`.`source_id`) WHERE `source` = 3;


UPDATE `db_version` SET `version_number` = '6.19.3';

COMMIT;
