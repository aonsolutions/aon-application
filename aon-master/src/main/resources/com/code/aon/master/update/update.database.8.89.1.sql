# Database: aon_master
# Version: Actualizacion de la version 8.89.1 a la version 8.90.0.
# Created by: girazu
# Creation Date: 09/02/2017 14:30

BEGIN;

UPDATE `registry` SET `document_country` = 'AD' WHERE `document_country` = 'AN';
UPDATE `registry` SET `nationality` = 'AD' WHERE `nationality` = 'AN';
UPDATE `invoice` SET `rdocument_country` = 'AD' WHERE `rdocument_country` = 'AN';
UPDATE `finance` SET `rdocument_country` = 'AD' WHERE `rdocument_country` = 'AN';

UPDATE `fs_mod347_detail` SET `country` = 'AD' WHERE `country` = 'AN';
UPDATE `fs_mod349_detail` SET `country` = 'AD' WHERE `country` = 'AN';
UPDATE `fs_model184` SET `country` = 'AD' WHERE `country` = 'AN';
UPDATE `fs_model184_detail` SET `country` = 'AD' WHERE `country` = 'AN';
UPDATE `fs_model200_registry` SET `country` = 'AD' WHERE `country` = 'AN';

UPDATE `project_reservation_guest` SET `document_country` = 'AD' WHERE `document_country` = 'AN';
UPDATE `project_reservation_guest` SET `country` = 'AD' WHERE `country` = 'AN';
UPDATE `reservation_request_guest` SET `country` = 'AD' WHERE `country` = 'AN';


UPDATE `db_version` SET `version_number` = '8.90.0';

COMMIT;
