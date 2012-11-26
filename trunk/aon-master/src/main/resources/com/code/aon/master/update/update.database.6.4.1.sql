# Database: aon_master
# Version: Actualizacion de la version 6.4.1 a la version 6.4.2.
# Created by: girazu
# Creation Date: 27/05/2011 10:05
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



BEGIN;

ALTER TABLE `mk_action_target` ADD `comments` text collate latin1_spanish_ci COMMENT 'Comentarios';
 
ALTER TABLE `mk_action_target` ADD `user` int(4) default NULL COMMENT 'Identificador del Usuario';
 
ALTER TABLE `mk_action_target` ADD KEY `IDX_MK_ACTION_TARGET_USER` (`user`);
 
ALTER TABLE `mk_action_target` ADD CONSTRAINT `FK_MK_ACTION_TARGET_USER` FOREIGN KEY (`user`) REFERENCES `user` (`id`);


UPDATE `db_version` SET `version_number` = '6.4.2';

COMMIT;
