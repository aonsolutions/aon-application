# Database: aon_master
# Version: Actualizacion de la version 8.21.2 a la version 8.22.0.
# Created by: rtrepiana

/*!40101 SET NAMES latin1 */;

SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;

BEGIN;

SET @CONCEPT=(SELECT `id` FROM `payment_concept` WHERE `code`='PREST_IT' AND `domain`=0);

DELETE FROM `system_payment` WHERE `domain`='0' 
AND `payment_concept` = @CONCEPT;

INSERT INTO `system_payment` 
(
`domain`, 
`payment_concept`, 
`description`, 
`expression`, 
`irpf_expression`, 
`quote_expression`, 
`start_date`, 
`salary_type`) 
(
SELECT  
0,
@CONCEPT,
'PREST. POR ENFERMEDAD COMÚN',
NULL,
NULL,
'DIAS_ENFERMEDAD_COMUN_1_3 * BASE_REGULADORA',
'2010-01-01',
0 
FROM `payment_concept` 
WHERE `id` = @CONCEPT
);

INSERT INTO `system_payment` 
(
`domain`, 
`payment_concept`, 
`description`, 
`expression`, 
`irpf_expression`, 
`quote_expression`, 
`start_date`, 
`salary_type`) 
(
SELECT 
0,
@CONCEPT,
'PREST. POR ENFERMEDAD COMÚN A CARGO DE LA EMPRESA',
'DIAS_ENFERMEDAD_COMUN_4_15 * BASE_REGULADORA * 0.60',
'_P',
'DIAS_ENFERMEDAD_COMUN_4_15 * BASE_REGULADORA',
'2010-01-01',
0
FROM `payment_concept` 
WHERE `id` = @CONCEPT
);

INSERT INTO `system_payment` 
(
`domain`, 
`payment_concept`, 
`description`, 
`expression`, 
`irpf_expression`, 
`quote_expression`, 
`start_date`, 
`salary_type`) 
(
SELECT 
0,
@CONCEPT,
'PREST. POR ENFERMEDAD COMÚN A CARGO DEL INSS',
'DIAS_ENFERMEDAD_COMUN_16_20 * BASE_REGULADORA * 0.60',
'_P',
'DIAS_ENFERMEDAD_COMUN_16_20 * BASE_REGULADORA',
'2010-01-01',
0
FROM `payment_concept` 
WHERE `id` = @CONCEPT
);

INSERT INTO `system_payment` 
(
`domain`, 
`payment_concept`, 
`description`, 
`expression`, 
`irpf_expression`, 
`quote_expression`, 
`start_date`, 
`salary_type`) 
(
SELECT 
0,
@CONCEPT,
'PREST. POR ENFERMEDAD COMÚN A CARGO DEL INSS',
'DIAS_ENFERMEDAD_COMUN_21 * BASE_REGULADORA * 0.75',
'_P',
'DIAS_ENFERMEDAD_COMUN_21 * BASE_REGULADORA',
'2010-01-01',
0
FROM `payment_concept` 
WHERE `id` = @CONCEPT
);

INSERT INTO `system_payment` 
(
`domain`, 
`payment_concept`, 
`description`, 
`expression`, 
`irpf_expression`, 
`quote_expression`, 
`start_date`, 
`salary_type`) 
(
SELECT 
0,
@CONCEPT,
'PREST. POR ACCIDENTE DE TRABAJO Y/O ENFERMEDAD PROFESIONAL',
'DIAS_ENFERMEDAD_PROFESIONAL * BASE_REGULADORA * 0.75',
'_P',
'DIAS_ENFERMEDAD_PROFESIONAL * BASE_REGULADORA',
'2010-01-01',
0
FROM `payment_concept` 
WHERE `id` = @CONCEPT
);

INSERT INTO `system_payment` 
(
`domain`, 
`payment_concept`, 
`description`, 
`expression`, 
`irpf_expression`, 
`quote_expression`, 
`start_date`, 
`salary_type`) 
(
SELECT 
0,
@CONCEPT,
'PREST. POR PATERNIDAD',
'DIAS_PATERNIDAD * 0.00',
NULL,
'DIAS_PATERNIDAD * BASE_REGULADORA',
'2010-01-01',
0
FROM `payment_concept` 
WHERE `id` = @CONCEPT
);

INSERT INTO `system_payment` 
(
`domain`, 
`payment_concept`, 
`description`, 
`expression`, 
`irpf_expression`, 
`quote_expression`, 
`start_date`, 
`salary_type`) 
(
SELECT 
0,
@CONCEPT,
'PREST. POR MATERNIDAD Y/O RIESGO DURANTE EL EMBARAZO',
'DIAS_MATERNIDAD * 0.00',
NULL,
'DIAS_MATERNIDAD * BASE_REGULADORA',
'2010-01-01',
0
FROM `payment_concept` 
WHERE `id` = @CONCEPT
);

UPDATE `db_version` SET `version_number` = '8.22.0';

COMMIT;

SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
