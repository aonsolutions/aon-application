# Database: aon_master
# Version: Actualizacion de la version 1.7.0 a la version 1.7.1
# Created by: girazu
# Creation Date: 11/06/2008 17:07
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



UPDATE `invoice_detail` SET `quantity` = 1, `price` = `taxable_base` WHERE `source` = 6 AND `quantity` = 0 AND `price` = 0;


UPDATE `db_version` SET `version_number` = '1.7.1';

COMMIT;
