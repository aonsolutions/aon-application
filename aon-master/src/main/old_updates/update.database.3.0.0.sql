# Database: aon_master
# Version: Actualizacion de la version 3.0.0 a la version 3.0.1.
# Created by: girazu
# Creation Date: 29/01/2009 17:03
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



ALTER TABLE `course_academicskill` MODIFY `academic_skill` int(4) NOT NULL COMMENT 'Aptitud Academica';

ALTER TABLE `course_academicskill` ADD `weight` int(4) NOT NULL DEFAULT 0 COMMENT 'Peso de la Aptitud para calcular la Nota media';

ALTER TABLE `invoice_detail` CHANGE `delivery_detail` `source_id` int(4) default NULL COMMENT 'Identificador del Origen del Detalle de la Factura';


UPDATE `db_version` SET `version_number` = '3.0.1';

COMMIT;
