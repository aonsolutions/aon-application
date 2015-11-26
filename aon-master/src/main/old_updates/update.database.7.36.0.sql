# Version: Upgrade from version 7.36.0 to 7.36.1.
# Created by: ecastellano@esferalia.com
# Creation Date: 13/06/2014 

SET FOREIGN_KEY_CHECKS=0;


DROP TABLE `enterprise_agreement`;
DROP TABLE `fs_batch_detail`;
DROP TABLE `fs_batch`;
DROP TABLE `fs_renting_detail`;
DROP TABLE `fs_renting`;
DROP TABLE `message_log`;
DROP TABLE `message_content`;


UPDATE `db_version` SET `version_number` = '7.36.1';

SET FOREIGN_KEY_CHECKS=1;
