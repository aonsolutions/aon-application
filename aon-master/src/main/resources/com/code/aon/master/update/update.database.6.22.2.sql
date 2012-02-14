# Database: aon_master
# Version: Actualizacion de la version 6.22.2 a la version 6.24.0.
# Created by: girazu
# Creation Date: 14/02/2012 10:40
# Comentarios: Ninguno


BEGIN;

ALTER TABLE `fs_mod347_detail` ADD `first_quarter_amount` double(15,3) default '0.000' COMMENT 'Importe de las operaciones primer trimestre';
ALTER TABLE `fs_mod347_detail` ADD `second_quarter_amount` double(15,3) default '0.000' COMMENT 'Importe de las operaciones segundo trimestre';
ALTER TABLE `fs_mod347_detail` ADD `third_quarter_amount` double(15,3) default '0.000' COMMENT 'Importe de las operaciones tercer trimestre';
ALTER TABLE `fs_mod347_detail` ADD `fourth_quarter_amount` double(15,3) default '0.000' COMMENT 'Importe de las operaciones cuarto trimestre'; 


UPDATE `db_version` SET `version_number` = '6.24.0';

COMMIT;
