# Database: aon_master
# Version: Actualizacion de la version 8.12.1 a la version 8.12.2.
# Created by: rtrepiana@esferalia.com
# Creation Date: 13/01/2015

BEGIN;


UPDATE `salary` SET `money_irpf_base` = `irpf_base` - `inkind_irpf_base` WHERE `money_irpf_base` = 0.00;


UPDATE `db_version` SET `version_number` = '8.12.2';

COMMIT;
