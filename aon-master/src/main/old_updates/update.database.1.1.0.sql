# Database: aon_master
# Version: Actualizacion de la version 1.1.0 a la version 1.2.0
# Created by: girazu
# Creation Date: 15/10/2007 10:07
# Comentarios: este script unicamente realiza los cambios necesarios en la estructura de la base de datos Aon.
#              Luego depende de cada uno el ir apañando los datos de sus aplicaciones a la nueva estructura.
#	       Cambios invalidantes: user ha dejado de ser un registry. Por lo tanto, se ha eliminado employee_workgroup y 
#	       se ha creado user_workgroup.



DROP TABLE `employee_workgroup`;

ALTER TABLE `task` DROP FOREIGN KEY `task_fk_1`;

ALTER TABLE `task` DROP FOREIGN KEY `task_fk_4`;

ALTER TABLE `alarm` DROP FOREIGN KEY `alarm_fk`;

ALTER TABLE `mail_account` DROP FOREIGN KEY `mail_account_fk`;

ALTER TABLE `favorite_category` DROP FOREIGN KEY `favorite_category_fk`;

ALTER TABLE `favorite` DROP FOREIGN KEY `favorite_fk1`;

ALTER TABLE `note` DROP FOREIGN KEY `note_fk`;

ALTER TABLE `notice` DROP FOREIGN KEY `notice_fk`;

ALTER TABLE `notice` DROP FOREIGN KEY `notice_fk1`;

ALTER TABLE `periodical_task` DROP FOREIGN KEY `periodical_task_fk1`;

ALTER TABLE `user_scope` DROP FOREIGN KEY `user_scope_fk`;

DROP TABLE `user`;

CREATE TABLE `user` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `name` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Nombre del Usuario',
  `login` varchar(16) collate latin1_spanish_ci NOT NULL COMMENT 'Login del Usuario',
  `available` tinyint(1) NOT NULL COMMENT 'Indica si el Usuario esta disponible o no',
  `validate` tinyint(1) NOT NULL default '0' COMMENT 'Indica si el Usuario requiere validacion o no de la clave hardware',
  `aon_key` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Campo alfanumerico donde se guarda la ultima clave hardware generada',
  `status` tinyint(2) default '0' COMMENT 'Estado del Usuario con respecto a su primera validacion de la clave hardware',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Usuarios';

ALTER TABLE `task` ADD CONSTRAINT `task_fk_1` FOREIGN KEY (`user`) REFERENCES `user` (`id`);

ALTER TABLE `task` ADD CONSTRAINT `task_fk_4` FOREIGN KEY (`sender`) REFERENCES `user` (`id`);

ALTER TABLE `contact` ADD CONSTRAINT `contact_fk` FOREIGN KEY (`user`) REFERENCES `user` (`id`);

ALTER TABLE `alarm` ADD CONSTRAINT `alarm_fk` FOREIGN KEY (`user`) REFERENCES `user` (`id`);

ALTER TABLE `mail_account` ADD CONSTRAINT `mail_account_fk` FOREIGN KEY (`user`) REFERENCES `user` (`id`);

ALTER TABLE `favorite_category` ADD CONSTRAINT `favorite_category_fk` FOREIGN KEY (`user`) REFERENCES `user` (`id`);

ALTER TABLE `favorite` ADD CONSTRAINT `favorite_fk1` FOREIGN KEY (`user`) REFERENCES `user` (`id`);

ALTER TABLE `note` ADD CONSTRAINT `note_fk` FOREIGN KEY (`owner`) REFERENCES `user` (`id`);

ALTER TABLE `notice` ADD CONSTRAINT `notice_fk` FOREIGN KEY (`sender`) REFERENCES `user` (`id`);

ALTER TABLE `notice` ADD CONSTRAINT `notice_fk1` FOREIGN KEY (`recipient`) REFERENCES `user` (`id`);

ALTER TABLE `periodical_task` ADD CONSTRAINT `periodical_task_fk1` FOREIGN KEY (`owner`) REFERENCES `user` (`id`);

ALTER TABLE `user_scope` ADD CONSTRAINT `user_scope_fk` FOREIGN KEY (`user`) REFERENCES `user` (`id`);

CREATE TABLE `user_workgroup` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `user` int(4) NOT NULL COMMENT 'Identificado del Usuario',
  `workgroup` int(4) NOT NULL COMMENT 'Identificador del Grupo de Trabajo',
  PRIMARY KEY  (`id`),
  KEY `user` (`user`),
  KEY `workgroup` (`workgroup`),
  CONSTRAINT `user_workgroup_fk_1` FOREIGN KEY (`user`) REFERENCES `user` (`id`),
  CONSTRAINT `user_workgroup_fk_2` FOREIGN KEY (`workgroup`) REFERENCES `workgroup` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Relacion entre Usuarios y Grupos de Trabajo';


UPDATE `db_version` SET `version_number` = '1.2.0';

COMMIT;
