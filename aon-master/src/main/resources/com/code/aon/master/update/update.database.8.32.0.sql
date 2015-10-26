# Database: aon_master
# Version: Actualizacion de la version 8.32.0 a la version 8.32.1.
# Created by: amtzdelagos
# Creation Date: 22/10/2015 13:18

BEGIN;

ALTER TABLE `notice` ADD `notice` INT(4) collate latin1_spanish_ci default NULL COMMENT 'Notice al que referencia';

ALTER TABLE `tag` ADD `color` VARCHAR(24) collate latin1_spanish_ci default NULL COMMENT 'Color de la tag';

CREATE TABLE `notice_tag` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del aviso',
  `notice` int(11) NOT NULL COMMENT 'Identificador de Notice',
  `tag` int(11) NOT NULL COMMENT 'Identificador de la Tag',
  `start_date` datetime NOT NULL COMMENT 'Fecha y hora de la apertura del aviso',
  `end_date` datetime DEFAULT NULL COMMENT 'Fecha y hora de cierre del aviso',
  PRIMARY KEY (`id`),
  KEY `FK_NOTICE_TAG_NOTICE` (`notice`),
  KEY `FK_NOTICE_TAG_TAG` (`tag`),
  CONSTRAINT `FK_NOTICE_TAG_NOTICE` FOREIGN KEY (`notice`) REFERENCES `notice` (`id`),
  CONSTRAINT `FK_NOTICE_TAG_TAG` FOREIGN KEY (`tag`) REFERENCES `tag` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci
;

UPDATE `db_version` SET `version_number` = '8.32.0';

COMMIT;

