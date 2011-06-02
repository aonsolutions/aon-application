# Database: aon_master
# Version: Actualizacion de la version 6.4.2 a la version 6.4.3.
# Created by: girazu
# Creation Date: 02/06/2011 11:28
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



BEGIN;

CREATE TABLE `fs_prof_retention` (
  `id` int(4) NOT NULL auto_increment COMMENT 'Identificador unico',
  `enterprise` int(4) NOT NULL default '0' COMMENT 'Identificador de Empresa',
  `payment_date` date NOT NULL COMMENT 'Fecha de Pago',
  `document` varchar(16) collate latin1_spanish_ci NOT NULL COMMENT 'Numero de Documento del Profesional',
  `document_type` tinyint(2) NOT NULL default '0' COMMENT 'Tipo de documento (NIF, CIF...) del Profesional',
  `document_country` varchar(2) collate latin1_spanish_ci NOT NULL default 'ES' COMMENT 'Pais del documento del Profesional',
  `name` varchar(64) collate latin1_spanish_ci NOT NULL COMMENT 'Nombre completo del Profesional',
  `concept` varchar(64) collate latin1_spanish_ci default NULL COMMENT 'Concepto',
  `taxable_base` double default '0' COMMENT 'Base Imponible',
  `percent` double(15,3) default '0.000' COMMENT 'Porcentaje de  retencion',
  `quota` double default '0' COMMENT 'Cuota de retencion',
  `in_kind` tinyint(1) default '0' COMMENT 'Indica si el importe es en especie (1) o dinerario (0)',
  `key` varchar(2) collate latin1_spanish_ci NOT NULL COMMENT 'Clave de retencion',
  `subkey` varchar(3) collate latin1_spanish_ci default NULL COMMENT 'Subclave de retencion',
  PRIMARY KEY  (`id`),
  KEY `IDX_FS_PROF_RET_ENTERPRISE` (`enterprise`),
  CONSTRAINT `FK_FS_PROF_RET_ENTERPRISE` FOREIGN KEY (`enterprise`) REFERENCES `enterprise` (`registry`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Retenciones de profesionales'; 


UPDATE `db_version` SET `version_number` = '6.4.3';

COMMIT;
