# Database: aon_master
# Version: Actualizacion de la version 1.2.0 a la version 1.2.1
# Created by: girazu
# Creation Date: 15/10/2007 17:49
# Comentarios: este script unicamente realiza los cambios necesarios en la estructura de la base de datos Aon.
#              Luego depende de cada uno el ir apañando los datos de sus aplicaciones a la nueva estructura.
#	       Cambios invalidantes: daily_tracking contiene user en lugar de employee.



ALTER TABLE `daily_tracking` DROP FOREIGN KEY `daily_tracking_fk_1`;

ALTER TABLE `daily_tracking` DROP KEY `employee`;

ALTER TABLE `daily_tracking` CHANGE `employee` `user` int(4) NOT NULL COMMENT 'Identificador del Usuario que realiza el Parte';

ALTER TABLE `daily_tracking` ADD KEY `user` (`user`);

ALTER TABLE `daily_tracking` ADD CONSTRAINT `daily_tracking_fk_1` FOREIGN KEY (`user`) REFERENCES `user` (`id`);


UPDATE `db_version` SET `version_number` = '1.2.1';

COMMIT;
