# Database: aon_master
# Version: Actualizacion de la version 6.2.2 a la version 6.2.3.
# Created by: girazu
# Creation Date: 25/03/2011 12:58
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



BEGIN;

DROP TABLE `annual_report_detail`;

DROP TABLE `annual_report`;


UPDATE `db_version` SET `version_number` = '6.2.3';

COMMIT;
