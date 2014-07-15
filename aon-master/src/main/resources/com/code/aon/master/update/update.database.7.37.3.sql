# Version: Upgrade from version 7.37.3 to 7.37.4
# Created by: rtrepiana
# Creation Date: 14/07/2014 

SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;

BEGIN;

INSERT INTO system_payment 
( domain, type, payment_concept, irpf_expression, quote_expression, start_date, month, end_date, salary_type , expression, description ) 
( SELECT domain, type, payment_concept, irpf_expression, quote_expression, start_date, month, end_date, salary_type, REPLACE(expression, '== FIN', '== FIN_OBRA'), 'INDEMNIZACION POR FIN DE OBRA'
 FROM system_payment WHERE description='INDEMNIZACION POR CESE');

INSERT INTO system_payment 
( domain, type, payment_concept, irpf_expression, quote_expression, start_date, month, end_date, salary_type , expression, description ) 
( SELECT domain, type, payment_concept, irpf_expression, quote_expression, start_date, month, end_date, salary_type, 
REPLACE(expression, '== FIN', '== FIN_TEMPORAL'), 'INDEMNIZACION POR FIN DE CONTRATO TEMPORAL'
 FROM system_payment WHERE description='INDEMNIZACION POR CESE');

INSERT INTO system_payment 
( domain, type, payment_concept, irpf_expression, quote_expression, start_date, month, end_date, salary_type , expression, description ) 
( SELECT domain, type, payment_concept, irpf_expression, quote_expression, start_date, month, end_date, salary_type, 
REPLACE(expression, '== PROCEDENTE', '== CAMBIO_CONDICIONES'), 'INDEMNIZACION POR BAJA VOLUNTARIA'
 FROM system_payment WHERE description='INDEMNIZACION POR DESPIDO POR CAUSAS OBJETIVAS');


UPDATE `db_version` SET `version_number` = '7.37.4';


COMMIT;

SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=1;
