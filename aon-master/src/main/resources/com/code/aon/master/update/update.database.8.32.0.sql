# Database: aon_master
# Version: Actualizacion de la version 8.32.0 a la version 8.32.1.
# Created by: amtzdelagos
# Creation Date: 22/10/2015 13:18

BEGIN;

ALTER TABLE `notice` ADD `notice` INT(4) collate latin1_spanish_ci default NULL COMMENT 'Aviso al que referencia';

ALTER TABLE `tag` ADD `color` VARCHAR(24) collate latin1_spanish_ci default NULL COMMENT 'Color de la Etiqueta';

CREATE TABLE `notice_tag` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `notice` int(4) NOT NULL COMMENT 'Identificador del Aviso',
  `tag` int(4) NOT NULL COMMENT 'Identificador de la Etiqueta',
  `start_date` datetime NOT NULL COMMENT 'Fecha y hora de la apertura del Aviso',
  `end_date` datetime DEFAULT NULL COMMENT 'Fecha y hora de cierre del Aviso',
  PRIMARY KEY (`id`),
  KEY `IDX_NOTICE_TAG_NOTICE` (`notice`),
  KEY `IDX_NOTICE_TAG_TAG` (`tag`),
  CONSTRAINT `FK_NOTICE_TAG_NOTICE` FOREIGN KEY (`notice`) REFERENCES `notice` (`id`),
  CONSTRAINT `FK_NOTICE_TAG_TAG` FOREIGN KEY (`tag`) REFERENCES `tag` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Relacion entre Avisos y Etiquetas';

ALTER TABLE `app_param` MODIFY `value` varchar(96) collate latin1_spanish_ci default NULL COMMENT 'Valor del Parametro';


UPDATE `db_version` SET `version_number` = '8.32.1';

COMMIT;

