# Version: Upgrade from version 7.31.1 to 7.32.0.
# Created by: rtrepiana@esferalia.com
# Creation Date: 02/06/2014 

SET FOREIGN_KEY_CHECKS=0;

# Like `description` at `contract_payment` ....
ALTER TABLE  salary_payment MODIFY `description` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Descripcion';

BEGIN;

# Be aware of new UNDEFINED function.  
UPDATE system_data SET expression=REPLACE(expression, 'new com.esferalia.aon.salary.expression.UndefinedVariablesException','UNDEFINED') ; 

INSERT INTO system_data (domain, name , expression, start_date) 
VALUES (0, 'EMBARGAR', 'def (embargo){ ( PENDIENTE = ( embargo - EMBARGADO ) ) > 0 ? MIN(MAX_EMBARGABLE(TOTAL_LIQUIDO), PENDIENTE ) : REMOVE()  }', '2000-01-01' );

INSERT INTO deduction_concept ( domain, type, description) VALUES ( 0, -1, 'EMBARGO');

INSERT INTO deduction_concept ( domain, type, description) VALUES ( 0, 7, 'ANTICIPO');
INSERT INTO deduction_concept ( domain, type, description) VALUES ( 0, 8, 'VALOR DE PRODUCTOS EN ESPECIE');

UPDATE payment_concept SET expression=REPLACE(expression,'SALARIO_ANUAL','/*user*/SALARIO_ANUAL/**/') WHERE domain=0;
UPDATE payment_concept SET expression=REPLACE(expression,'SALARIO_MENSUAL','/*user*/SALARIO_MENSUAL/**/') WHERE domain=0;
UPDATE payment_concept SET expression=REPLACE(expression,'SALARIO_DIARIO','/*user*/SALARIO_DIARIO/**/') WHERE domain=0;
UPDATE payment_concept SET expression=REPLACE(expression,'IMPORTE_PAGA_EXTRA','/*user*/IMPORTE_PAGA_EXTRA/**/') WHERE domain=0;

UPDATE payment_concept SET expression='/*read-only*/SALARIO_HORA * HORAS/**/' WHERE domain=0
 AND expression='SALARIO_HORA * HORAS';
UPDATE payment_concept SET expression='/*read-only*/IMPORTE_KM * KMS/**/' WHERE domain=0
 AND expression='IMPORTE_KM * KMS';
UPDATE payment_concept SET expression='/*read-only*/IMPORTE_PERNOCTA * DIAS_PERNOCTA/**/' WHERE domain=0 
 AND  expression='IMPORTE_PERNOCTA * DIAS_PERNOCTA';
UPDATE payment_concept SET expression='/*read-only*/IMPORTE_MANUTENCION * DIAS_MANUTENCION/**/' WHERE domain=0 
 AND  expression='IMPORTE_MANUTENCION * DIAS_MANUTENCION';
UPDATE payment_concept SET expression='/*read-only*/IMPORTE_PERNOCTA_EXTRANJERO * DIAS_PERNOCTA_EXTRANJERO/**/' 
 ,irpf_expression='EXCESO(91.35 * DIAS_PERNOCTA_EXTRANJERO)' 
 ,quote_expression='EXCESO(91.35 * DIAS_PERNOCTA_EXTRANJERO)' 
 WHERE domain=0 AND  expression='PERNOCTA_EXTRANJERO * DIAS_PERNOCTA_EXTRANJERO';
UPDATE payment_concept SET expression='/*read-only*/IMPORTE_MANUTENCION_EXTRANJERO * DIAS_MANUTENCION_EXTRANJERO/**/'
 ,irpf_expression='EXCESO(91.35 * DIAS_MANUTENCION_EXTRANJERO)' 
 ,quote_expression='EXCESO(91.35 * DIAS_MANUTENCION_EXTRANJERO)' 
 WHERE domain=0 AND  expression='MANUTENCION_EXTRANJERO * DIAS_MANUTENCION_EXTRANJERO';

UPDATE payment_concept SET expression=CONCAT('/*read-only*/',expression,'/**/') WHERE domain=0 
 AND  expression LIKE 'DIAS_INDEMNIZACION * %';


UPDATE `db_version` SET `version_number` = '7.32.1';

COMMIT;

SET FOREIGN_KEY_CHECKS=1;
