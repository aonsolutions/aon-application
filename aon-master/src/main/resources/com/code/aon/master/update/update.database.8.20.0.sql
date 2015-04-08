 # Database: aon_master
# Version: Actualizacion de la version 8.20.0 a la version 8.20.1.
# Created by: rtrepiana

BEGIN;

ALTER TABLE `payment_concept` modify `code` VARCHAR(25) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo'; 
ALTER TABLE `deduction_concept` modify `code` VARCHAR(25) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Codigo'; 

SET @PLUS_SALARIAL=(SELECT id FROM payment_concept WHERE domain= 0 AND code='PLUS_SALARIAL');

INSERT INTO payment_concept (domain, code, description, type, description_decorable, expression, irpf_expression, quote_expression) 
( SELECT domain, code, description, type, description_decorable, '/*user*/ 0.00 /**/ * DIAS_TRABAJADOS / DIAS_MES', irpf_expression, quote_expression  FROM payment_concept WHERE id=@PLUS_SALARIAL) ;

SET @EXPRESSION=(SELECT expression FROM payment_concept WHERE id=@PLUS_SALARIAL);
UPDATE  contract_payment SET expression=@EXPRESSION WHERE payment_concept=@PLUS_SALARIAL AND expression IS NULL;
UPDATE  payment_concept SET code='PLUS_EXTRA_SALARIAL', expression=NULL, description= 'PLUS EXTRA SALARIAL' WHERE id=@PLUS_SALARIAL;

UPDATE contract_payment SET expression= REPLACE(expression, 'PLUS_SALARIAL', 'PLUS_EXTRA_SALARIAL') 
WHERE expression LIKE '%PLUS_SALARIAL%' and payment_concept <> @PLUS_SALARIAL;

UPDATE agreement_payment SET expression= REPLACE(expression, 'PLUS_SALARIAL', 'PLUS_EXTRA_SALARIAL') 
WHERE expression LIKE '%PLUS_SALARIAL%' and payment_concept <> @PLUS_SALARIAL;

UPDATE `db_version` SET `version_number` = '8.20.1';

COMMIT;
