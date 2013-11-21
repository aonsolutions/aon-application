# Database: aon_master
# Version: Actualizacion de la version 7.25.0 a la version 7.25.1.
# Created by: girazu
# Creation Date: 15/11/2013 12:55
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.

BEGIN;

ALTER TABLE `project_reservation` ADD KEY `IDX_PROJECT_RESERVATION_START_DATE` (`start_date`);

ALTER TABLE `finance` ADD `source_id` int(4) default NULL COMMENT 'Identificador del Origen del Vencimiento' AFTER `prepayment`; 


UPDATE `db_version` SET `version_number` = '7.25.1';

COMMIT;
