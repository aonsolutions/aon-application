# Database: aon_master
# Version: Actualizacion de la version 1.6.2 a la version 1.6.3
# Created by: girazu
# Creation Date: 03/06/2008 18:25
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



ALTER TABLE `curriculum` ADD `entrydate` date NOT NULL COMMENT 'Fecha de registro del Curriculum' AFTER `registry`;

ALTER TABLE `curriculum` ADD `postcategory` tinyint(2) COMMENT 'Rol del Curriculum dentro de la Empresa';

DROP INDEX `resource_idx` ON `resource`;

CREATE INDEX `resource_idx` ON `resource` (`employee`,`endingdate`);

ALTER TABLE `dossier` MODIFY `dossier_type` int(4) NOT NULL COMMENT 'Tipo de Expediente';

ALTER TABLE `dossier` ADD KEY `dossier_type` (`dossier_type`);

ALTER TABLE `dossier` ADD CONSTRAINT `dossier_ibfk_2` FOREIGN KEY (`dossier_type`) REFERENCES `dossier_type` (`id`);


UPDATE `db_version` SET `version_number` = '1.6.3';

COMMIT;
