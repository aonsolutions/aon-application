# Database: aon_master
# Version: Actualizacion de la version 8.48.0 a la version 8.50.0
# Created by: rtrepiana

SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;

BEGIN;

SET @ANTIGUEDAD_ID=(SELECT `id` FROM `payment_concept` WHERE `code`='ANTIGUEDAD' AND `domain`=0);
SET @ANTIGUEDAD_EXP=(SELECT `expression` FROM `payment_concept` WHERE `id`=@ANTIGUEDAD_ID);

UPDATE `agreement_payment` 
SET `expression`=@ANTIGUEDAD_EXP
WHERE `payment_concept`=@ANTIGUEDAD_ID AND `expression` IS NULL;

UPDATE `contract_payment` 
SET `expression`=@ANTIGUEDAD_EXP
WHERE `payment_concept`=@ANTIGUEDAD_ID AND `expression` IS NULL;

SET @PAGA_EXTRA_ID=(SELECT `id` FROM `payment_concept` WHERE `code`='PAGA_EXTRA' AND `domain`=0);
SET @PAGA_EXTRA_EXP=(SELECT `expression` FROM `payment_concept` WHERE `id`=@PAGA_EXTRA_ID);

UPDATE `agreement_payment` 
SET `expression`=@PAGA_EXTRA_EXP
WHERE `payment_concept`=@PAGA_EXTRA_ID AND `expression` IS NULL;

UPDATE `contract_payment` 
SET `expression`=@PAGA_EXTRA_EXP
WHERE `payment_concept`=@PAGA_EXTRA_ID AND `expression` IS NULL;

ALTER TABLE `payment_concept` MODIFY `expression` varchar(1024) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Importe';

DELIMITER $$
UPDATE `payment_concept` 
SET `expression`='INPUT("/*user*//**/","Por favor, introduzca la cuant&iacute;a del complemento personal de antig&uuml;edad, ejemplos:
<ul style=\'margin-left:1em;\'>
<li>	
	<a style=\'color:orange;\' >ANTIGÜEDAD(SALARIO_BASE * 0.04, CUATRIENIO)</a>&nbsp;
	<span style=\'font-weight: lighter;\'>4 por 100 del salario base por cuatrienio</span>
</li>
<li>
	<a style=\'color:orange;\'>ANTIGÜEDAD(SALARIO_BASE * 0.03, 6, 4, 4, 4)</a>
	<span style=\'font-weight: lighter;\'>primer sexenio el 3% del salario y tres cuatrienios del 3% cada uno</span>
</li>
<li>	
	<a style=\'color:orange;\'>MAX(ANTIGÜEDAD(SALARIO_BASE * 0.05, TRIENIO), SALARIO_BASE * 0.40)</a>&nbsp;
	<span style=\'font-weight: lighter;\'>5% del salario por trienio, no podr&aacute; en ning&uacute;n caso, suponer
m&aacute;s del 40%</span>
</li>
</ul>")'
,
`description`='COMPLEMENTO PERSONAL DE ANTIGÜEDAD'
WHERE `id`=@ANTIGUEDAD_ID 
$$

UPDATE `payment_concept` 
SET `expression`='INPUT("/*user*//**/","Por favor, introduzca la cuant&iacute;a de la paga (bono), ejemplos:
<ul style=\'margin-left:1em;\'>
<li>
	<a style=\'color:orange;\'>SALARIO_BASE + ANTIGUEDAD + PLUS_SALARIAL</a>&nbsp;
	<span style=\'font-weight: lighter;\'>una mensualidad de salario base, plus convenio y antig&uuml;edad</span>
</li>
<li>
	<a style=\'color:orange;\'>(SALARIO_BASE + ANTIGUEDAD + PLUS_SALARIAL)/12</a>&nbsp;
	<span style=\'font-weight: lighter;\'>la paga arriba indicada pero en este caso prorrateada</span>
</li>
<li>
	<a style=\'color:orange;\'>2000.00 * DIAS_TRABAJADOS / DIAS_MES</a>&nbsp;
	<span style=\'font-weight: lighter;\'>2000&euro;</sppan>
</li>
</ul>")'
WHERE `id`=@PAGA_EXTRA_ID
$$

DELIMITER ;
UPDATE `db_version` SET `version_number` = '8.50.0';




COMMIT;

SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
