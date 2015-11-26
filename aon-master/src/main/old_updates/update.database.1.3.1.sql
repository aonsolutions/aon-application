# Database: aon_master
# Version: Actualizacion de la version 1.3.1 a la version 1.3.2
# Created by: girazu
# Creation Date: 13/03/2008 09:19
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



ALTER TABLE `rattach` MODIFY `data` mediumblob COMMENT 'Archivo Adjunto en binario';


UPDATE `db_version` SET `version_number` = '1.3.2';

COMMIT;
