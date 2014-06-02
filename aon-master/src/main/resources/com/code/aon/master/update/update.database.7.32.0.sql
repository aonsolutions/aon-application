# Version: Upgrade from version 7.31.1 to 7.32.0.
# Created by: rtrepiana@esferalia.com
# Creation Date: 02/06/2014 


# Like `description` at `contract_payment` ....
ALTER TABLE  salary_payment MODIFY `description` varchar(128) collate latin1_spanish_ci default NULL COMMENT 'Descripcion';

BEGIN;

# Be aware of new UNDEFINED function.  
UPDATE system_data SET expression=REPLACE(expression, 'new com.esferalia.aon.salary.expression.UndefinedVariablesException','UNDEFINED') ; 


UPDATE `db_version` SET `version_number` = '7.32.1';

COMMIT;