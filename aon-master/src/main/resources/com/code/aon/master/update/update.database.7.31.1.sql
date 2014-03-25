# Version: Upgrade from version 7.31.1 to 7.32.0.
# Created by: rtrepiana@esferalia.com
# Creation Date: 25/03/2014 



BEGIN;


# Adds amount that this payment adds to the I.R.P.F Base.
#
ALTER TABLE `salary_payment` ADD `irpf` double(15,3) default '0.000' COMMENT 'Importe I.R.P.F';
# Adds amount that this payment adds to the Quote Base. This amount isn't limited.
#
ALTER TABLE `salary_payment` ADD `quote` double(15,3) default '0.000' COMMENT 'Importe Cotizable';

# It taxes the whole amount, except for payments, that are expenses related with studies ( 0035 ), 
# locomotion or diets ( 0042..0050 ) and the compensations ( 0051..0056).
#
UPDATE `salary_payment` SET irpf=amount , quote=amount   WHERE type <> 35 AND type >= 42 ;


UPDATE `db_version` SET `version_number` = '7.32.0';

COMMIT;

