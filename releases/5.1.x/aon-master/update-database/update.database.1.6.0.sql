# Database: aon_master
# Version: Actualizacion de la version 1.6.0 a la version 1.6.1
# Created by: girazu
# Creation Date: 26/05/2008 11:17
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



DELETE FROM `customer_fee` WHERE `billing_date` > 'final_date';


UPDATE `db_version` SET `version_number` = '1.6.1';

COMMIT;
