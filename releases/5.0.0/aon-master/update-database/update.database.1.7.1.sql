# Database: aon_master
# Version: Actualizacion de la version 1.7.1 a la version 1.7.2
# Created by: girazu
# Creation Date: 13/06/2008 11:34
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



ALTER TABLE `account` MODIFY `alias` varchar(32) collate latin1_spanish_ci default NULL COMMENT 'Alias de la Cuenta';

ALTER TABLE `activity_type` MODIFY `dossier_type` int(4) COMMENT 'Tipo de Dossier';


UPDATE `db_version` SET `version_number` = '1.7.2';

COMMIT;
