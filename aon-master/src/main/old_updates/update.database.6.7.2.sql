# Database: aon_master
# Version: Actualizacion de la version 6.7.2 a la version 6.9.0.
# Created by: girazu
# Creation Date: 22/07/2011 14:08
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



BEGIN;

ALTER TABLE `commercial_tracking` MODIFY `comments` text collate latin1_spanish_ci default NULL COMMENT 'Comentarios del Seguimiento Comercial';

ALTER TABLE `question` ADD `alias`  varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Alias de la Pregunta';

CREATE TABLE `mk_template` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `name` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Nombre de la Plantilla',
  `data` mediumtext NOT NULL COMMENT 'Contenido de la Plantilla',
  `active` tinyint(1) NOT NULL COMMENT 'Indica si la Plantilla esta activa o no',
  `creationDate` datetime NOT NULL COMMENT 'Fecha de la creacion en el sistema de la Plantilla',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Plantilla de Marketing';

ALTER TABLE `mk_action` ADD `template` int(4) default NULL COMMENT 'Identificador de la Plantilla';

ALTER TABLE `mk_action` ADD KEY `IDX_MK_ACTION_MK_TEMPLATE` (`template`);

ALTER TABLE `mk_action` ADD CONSTRAINT `FK_MK_ACTION_MK_TEMPLATE` FOREIGN KEY (`template`) REFERENCES `mk_template` (`id`);


UPDATE `db_version` SET `version_number` = '6.9.0';

COMMIT;
