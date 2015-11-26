# Database: aon_master
# Version: Actualizacion de la version 3.4.0 a la version 3.5.0.
# Created by: girazu
# Creation Date: 08/06/2009 17:32
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



CREATE TABLE `invoice_attach` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `invoice` int(4) NOT NULL COMMENT 'Identificador de la Factura',
  `mimeType` tinyint(2) default '0' COMMENT 'Mime Type del Archivo Adjunto',
  `description` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Descripcion del Archivo Adjunto',
  `data` mediumblob COMMENT 'Archivo Adjunto en binario',
  PRIMARY KEY (`id`),
  KEY `IDX_INVOICE_ATTACH_INVOICE` (`invoice`),
  CONSTRAINT `FK_INVOICE_ATTACH_INVOICE` FOREIGN KEY (`invoice`) REFERENCES `invoice` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Archivos Adjuntos de Facturas';


UPDATE `db_version` SET `version_number` = '3.5.0';

COMMIT;
