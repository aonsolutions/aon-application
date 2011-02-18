# Database: aon_master
# Version: Actualizacion de la version 6.0.0 a la version 6.0.1.
# Created by: girazu
# Creation Date: 18/02/2011 10:44
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



BEGIN;

UPDATE `registry` SET `document_type` = 2 WHERE SUBSTR(`document`, 1, 1) IN ('K','L','M','X','Y','Z');

UPDATE `registry` SET `document_type` = 1 WHERE SUBSTR(`document`, 1, 1) IN ('A','B','C','D','E','F','G','H','I','J');

UPDATE `registry` SET `document_type` = 0 WHERE `document_type` IS NULL;

UPDATE `registry` SET `document_country` = 'ES';

UPDATE `registry` SET `nationality` = 'ES';


UPDATE `db_version` SET `version_number` = '6.0.1';

COMMIT;
