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

UPDATE `db_version` SET `version_number` = '7.32.1';

COMMIT;

SET FOREIGN_KEY_CHECKS=1;
