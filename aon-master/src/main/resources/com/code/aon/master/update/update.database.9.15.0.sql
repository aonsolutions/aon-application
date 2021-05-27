# Database: aon_master
# Version: Actualizacion de la version 9.15.0 a la version 9.15.1
# Created by: ecastellano
# Creation Date: 20/12/2017 10:30

BEGIN;

ALTER TABLE `fs_model190_detail` ADD `in_kind_perception_il` double(15,3) NOT NULL DEFAULT '0.000';
ALTER TABLE `fs_model190_detail` ADD `in_kind_deposit_il` double(15,3) NOT NULL DEFAULT '0.000';
ALTER TABLE `fs_model190_detail` ADD `in_kind_output_deposit_il` double(15,3) NOT NULL DEFAULT '0.000';

UPDATE `db_version` SET `version_number` = '9.15.1';

COMMIT;
