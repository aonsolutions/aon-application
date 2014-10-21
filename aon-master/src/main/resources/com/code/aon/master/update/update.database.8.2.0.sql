# Version: Upgrade from version 8.0.2 to 8.2.0
# Created by: rtrepiana
# Creation Date: 09/10/2014 

BEGIN;

UPDATE system_payment SET expression='DIAS_ENFERMEDAD_COMUN_16_20 * BASE_REGULADORA * 0.60' 
WHERE domain= 0 AND expression='ECSS=(DIAS_ENFERMEDAD_COMUN_16_20 * BASE_REGULADORA * 0.60 + DIAS_ENFERMEDAD_COMUN_21 * BASE_REGULADORA * 0.75)';

INSERT INTO system_payment 
( 		 domain, type, payment_concept, description, description_decorable, irpf_expression, quote_expression, start_date, end_date, salary_type, expression ) 

( SELECT domain, type, payment_concept, description, description_decorable, irpf_expression, quote_expression, start_date, end_date, salary_type, 'DIAS_ENFERMEDAD_COMUN_21 * BASE_REGULADORA * 0.75'  
 FROM system_payment 
 WHERE domain= 0 AND expression='DIAS_ENFERMEDAD_COMUN_16_20 * BASE_REGULADORA * 0.60'
)

UPDATE `db_version` SET `version_number` = '8.4.0';

COMMIT;
