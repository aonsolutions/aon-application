

# Database: aon_master
# Version: Actualizacion de la version 8.15.2 a la version 8.16.0.
# Created by: rtrepiana
# Creation Date: 11/02/2015 

BEGIN;

DROP PROCEDURE IF EXISTS `UPDATE_8_15_2`;

#DELIMITER $$
CREATE PROCEDURE `UPDATE_8_15_2`(IN pattern VARCHAR(100) CHARACTER SET latin1 COLLATE latin1_spanish_ci)
BEGIN	
	SET @TO_REMOVE=pattern;	/*Skips SQLFile Statement End*/

	UPDATE `payment_concept` SET `expression`=REPLACE(expression, @TO_REMOVE, "" )  
	WHERE  `expression` LIKE CONCAT('%DIAS_TRABAJADOS%',@TO_REMOVE,'%') 
	OR `expression` LIKE CONCAT('%', @TO_REMOVE,'%DIAS_TRABAJADOS%'); /*Skips SQLFile Statement End*/

	UPDATE `agreement_payment` SET `expression`=REPLACE(expression, @TO_REMOVE, "" )  
	WHERE  `expression` LIKE CONCAT('%DIAS_TRABAJADOS%',@TO_REMOVE,'%') 
	OR `expression` LIKE CONCAT('%', @TO_REMOVE,'%DIAS_TRABAJADOS%');  /*Skips SQLFile Statement End*/
	
	UPDATE `contract_payment` SET `expression`=REPLACE(expression, @TO_REMOVE, "" ) 
	WHERE  `expression` LIKE CONCAT('%DIAS_TRABAJADOS%',@TO_REMOVE,'%') 
	OR `expression` LIKE CONCAT('%', @TO_REMOVE,'%DIAS_TRABAJADOS%'); /*Skips SQLFile Statement End*/
END
#$$ DELIMITER 
; 


CALL UPDATE_8_15_2("* COEFICIENTE_PARCIALIDAD") ;
CALL UPDATE_8_15_2("*COEFICIENTE_PARCIALIDAD");
CALL UPDATE_8_15_2("* ( TIEMPO_COMPLETO ? 1 : (HORAS_SEMANA / HORAS_CONVENIO))");
CALL UPDATE_8_15_2("*( TIEMPO_COMPLETO ? 1 : (HORAS_SEMANA / HORAS_CONVENIO))");
CALL UPDATE_8_15_2("*( TIEMPO_COMPLETO ? 1 : (HORAS_SEMANA/HORAS_CONVENIO))");


DELETE FROM `system_data` WHERE name='COEFICIENTE_PARCIALIDAD';

DROP PROCEDURE IF EXISTS `UPDATE_8_15_2`;

UPDATE `db_version` SET `version_number` = '8.16.0';

COMMIT;

