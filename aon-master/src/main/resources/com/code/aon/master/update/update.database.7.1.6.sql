# Database: aon_master
# Version: Actualizacion de la version 7.1.5 a la version 7.1.6.
# Created by: ecastellano
# Creation Date: 26/07/2012 08:30
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.


BEGIN;

ALTER TABLE `domain` ADD `owner` varchar(32) collate latin1_spanish_ci NOT NULL COMMENT 'Creador del Dominio';

update system_payment set payment_concept = (select pc.id from payment_concept pc, payment_concept pc2 where pc2.id = system_payment.payment_concept and pc.code = pc2.code and pc.domain = system_payment.domain);           
update system_deduction set deduction_concept = (select dc.id from deduction_concept dc,deduction_concept dc2 where dc2.id = system_deduction.deduction_concept and dc.code = dc2.code and  dc.domain = system_deduction.domain);
           
UPDATE `db_version` SET `version_number` = '7.1.7';

COMMIT;
