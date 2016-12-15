# Database: aon_master
# Version: Actualizacion de la version 8.78.2 a la version 8.79.0.
# Created by: aibanez
# Creation Date: 15/12/2016 12:30
# Añadir los roles CallCenter y CallCenterManager.

BEGIN;

INSERT INTO role VALUES (23,'CallCenter');
INSERT INTO role VALUES (24,'CallCenterManager');
INSERT INTO application_role (application, role) VALUES (28, 23);
INSERT INTO application_role (application, role) VALUES (28, 24);

UPDATE `db_version` SET `version_number` = '8.79.0';

COMMIT;
