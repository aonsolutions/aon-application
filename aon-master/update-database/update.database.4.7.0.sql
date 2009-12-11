# Database: aon_master
# Version: Actualizacion de la version 4.7.0 a la version 4.8.0.
# Created by: girazu
# Creation Date: 26/11/2009 12:41
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



ALTER TABLE `item` DROP FOREIGN KEY `FK_ALTERNATIVE_ITEM`;

ALTER TABLE `item` DROP KEY `IDX_ALTERNATIVE_ITEM`;

ALTER TABLE `item` DROP `alternative_item`;

CREATE TABLE `item_alternative` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `item` int(4) NOT NULL default '0' COMMENT 'Identificador de Articulo',
  `alternative_item` int(4) NOT NULL default '0' COMMENT 'Identificador de Articulo Alternativo',
  `priority` tinyint(2) default '0' COMMENT 'Prioridad del Articulo Alternativo',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `IDX_UNQ_ITEM_ALTERNATIVE` (`item`,`alternative_item`),
  KEY `IDX_ITEM_ALTERNATIVE_ITEM` (`item`),
  KEY `IDX_ITEM_ALTERNATIVE_ALTERNATIVE` (`alternative_item`),
  CONSTRAINT `FK_ITEM_ALTERNATIVE_ITEM` FOREIGN KEY (`item`) REFERENCES `item` (`id`),
  CONSTRAINT `FK_ITEM_ALTERNATIVE_ALTERNATIVE` FOREIGN KEY (`alternative_item`) REFERENCES `item` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Articulo Alternativos';

DELETE FROM `web_info_page_resource` WHERE `web_info_page` IS NULL OR `rattach` IS NULL;

ALTER TABLE `web_info_page_resource` MODIFY `web_info_page` int(4) NOT NULL COMMENT 'Codigo de la Pagina';

ALTER TABLE `web_info_page_resource` MODIFY `rattach` int(4) NOT NULL COMMENT 'Identificador del Archivo Adjunto calificado como Recurso';

ALTER TABLE `ec_paymethod` MODIFY `user_name` varchar(64) collate latin1_spanish_ci default 'Null' COMMENT 'Nombre de Usuario';

ALTER TABLE `ec_paymethod` MODIFY `signature` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Identificador unico de la empresa para pasarela';


UPDATE `db_version` SET `version_number` = '4.8.0';

COMMIT;
