# Database: aon_master
# Version: Actualizacion de la version 6.18.8 a la version 6.18.9
# Created by: rtrepiana
# Creation Date: 10/11/2011 
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.


BEGIN;



ALTER TABLE `salary` ADD `charge_date` date NULL  COMMENT 'Fecha de cobro' ;

UPDATE `salary` SET `charge_date`=`end_date` ;


ALTER TABLE `salary` MODIFY `charge_date` date NOT NULL  COMMENT 'Fecha de cobro' ;

UPDATE `db_version` SET `version_number` = '6.18.9';

COMMIT;
