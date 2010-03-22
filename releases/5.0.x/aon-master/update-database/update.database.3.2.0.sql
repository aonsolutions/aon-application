# Database: aon_master
# Version: Actualizacion de la version 3.2.0 a la version 3.2.1.
# Created by: girazu
# Creation Date: 04/05/2009 16:35
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



ALTER TABLE `rmedia` DROP FOREIGN KEY `rmedia_ibfk_2`;

ALTER TABLE `rmedia` DROP INDEX `raddress`;

ALTER TABLE `rmedia` DROP `raddress`;

ALTER TABLE `rpaymethod` MODIFY `registry` int(4) NOT NULL COMMENT 'Identificador del Registro de la Persona o Empresa';

ALTER TABLE `rpaymethod` MODIFY `pay_method` int(4) NOT NULL COMMENT 'Identificador de la Forma de Pago';

ALTER TABLE `user_scope` DROP FOREIGN KEY `user_scope_fk`;

ALTER TABLE `user_scope` DROP INDEX `user`;

ALTER TABLE `user_scope` CHANGE `user` `user_id` int(4) NOT NULL COMMENT 'Identificador del Usuario';

ALTER TABLE `user_scope` ADD CONSTRAINT `user_scope_fk` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`);

CREATE INDEX `user` ON `user_scope` (`user_id`);

ALTER TABLE `user_workgroup` DROP FOREIGN KEY `user_workgroup_fk_1`;

ALTER TABLE `user_workgroup` DROP INDEX `user`;

ALTER TABLE `user_workgroup` CHANGE `user` `user_id` int(4) NOT NULL COMMENT 'Identificador del Usuario';

ALTER TABLE `user_workgroup` ADD CONSTRAINT `user_workgroup_fk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`);

CREATE INDEX `user` ON `user_workgroup` (`user_id`);

ALTER TABLE `fbatch_detail` MODIFY `fbatch` int(4) NOT NULL COMMENT 'Identificador de la Remesa';

ALTER TABLE `fbatch_detail` MODIFY `finance` int(4) NOT NULL COMMENT 'Identificador del Vencimiento';

ALTER TABLE `account` MODIFY `level` tinyint(2) NOT NULL default '0' COMMENT 'Nivel de la Cuenta';

ALTER TABLE `alarm` DROP FOREIGN KEY `alarm_fk`;

ALTER TABLE `alarm` DROP INDEX `user`;

ALTER TABLE `alarm` CHANGE `user` `user_id` INTEGER(4) DEFAULT NULL COMMENT 'Identificador del Usuario asociado a la Alarma';

ALTER TABLE `alarm` ADD CONSTRAINT `alarm_fk` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`);

CREATE INDEX `user` ON `alarm` (`user_id`);

ALTER TABLE `favorite` DROP FOREIGN KEY `favorite_fk1`;

ALTER TABLE `favorite` DROP INDEX `user`;

ALTER TABLE `favorite` CHANGE `user` `user_id` INTEGER(4) NOT NULL COMMENT 'Usuario al que pertenece el Favorito';

ALTER TABLE `favorite` ADD CONSTRAINT `favorite_fk1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`);

CREATE INDEX `user` ON `favorite` (`user_id`);

ALTER TABLE `favorite_category` DROP FOREIGN KEY `favorite_category_fk`;

ALTER TABLE `favorite_category` DROP INDEX `user`;

ALTER TABLE `favorite_category` CHANGE `user` `user_id` INTEGER(4) NOT NULL COMMENT 'Usuario al que pertenece la Categoria';

ALTER TABLE `favorite_category` ADD CONSTRAINT `favorite_category_fk` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`);

CREATE INDEX `user` ON `favorite_category` (`user_id`);

ALTER TABLE `notice` MODIFY `date` DATETIME NOT NULL COMMENT 'Fecha y hora en la que se produjo el Aviso';

ALTER TABLE `activity` MODIFY `workgroup` INTEGER(4) NOT NULL COMMENT 'Identificador del Grupo de Trabajo';

ALTER TABLE `daily_tracking` DROP FOREIGN KEY `daily_tracking_fk_1`;

ALTER TABLE `daily_tracking` DROP INDEX `user`;

ALTER TABLE `daily_tracking` CHANGE `user` `user_id` INTEGER(4) NOT NULL COMMENT 'Identificador del Usuario que realiza el Parte';

ALTER TABLE `daily_tracking` ADD CONSTRAINT `daily_tracking_fk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`);

CREATE INDEX `user` ON `daily_tracking` (`user_id`);

ALTER TABLE `task` DROP FOREIGN KEY `task_fk_1`;

ALTER TABLE `task` DROP INDEX `user`;

ALTER TABLE `task` CHANGE `user` `user_id` INTEGER(4) DEFAULT NULL COMMENT 'Identificador del Usuario asociado a la Tarea';

ALTER TABLE `task` ADD CONSTRAINT `task_fk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`);

CREATE INDEX `user` ON `task` (`user_id`);


UPDATE `db_version` SET `version_number` = '3.2.1';

COMMIT;
