#DELIMITER $$
CREATE PROCEDURE `UPDATE_8_15_2`(IN pattern VARCHAR(100) CHARACTER SET latin1 COLLATE latin1_spanish_ci)
BEGIN	
	SET @TO_REMOVE=pattern;

	UPDATE `payment_concept` SET `expression`=REPLACE(expression, @TO_REMOVE, "" )  
	WHERE  `expression` LIKE CONCAT('%DIAS_TRABAJADOS%',@TO_REMOVE,'%') 
	OR `expression` LIKE CONCAT('%', @TO_REMOVE,'%DIAS_TRABAJADOS%');

	UPDATE `agreement_payment` SET `expression`=REPLACE(expression, @TO_REMOVE, "" )  
	WHERE  `expression` LIKE CONCAT('%DIAS_TRABAJADOS%',@TO_REMOVE,'%') 
	OR `expression` LIKE CONCAT('%', @TO_REMOVE,'%DIAS_TRABAJADOS%');  
	
	UPDATE `contract_payment` SET `expression`=REPLACE(expression, @TO_REMOVE, "" ) 
	WHERE  `expression` LIKE CONCAT('%DIAS_TRABAJADOS%',@TO_REMOVE,'%') 
	OR `expression` LIKE CONCAT('%', @TO_REMOVE,'%DIAS_TRABAJADOS%'); 
END
#$$ DELIMITER 
