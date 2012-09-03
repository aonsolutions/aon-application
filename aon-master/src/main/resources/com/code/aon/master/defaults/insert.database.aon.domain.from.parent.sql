
/*
 *	Para la ejecucion de este script es necesario pasar como paramentro @Domain, la
 *  forma de hacerlo desde consola es la siguiente: 
 * 
 *  	mysql -h <host> -u <user> ... <database> -e "SET @Domain=XX; source <path al script>;"
 * 
 *  siendo XX el dominio que se quiere rellenar.
 * 
 */


SET FOREIGN_KEY_CHECKS=0;

SET @parentDomain = (SELECT `parent` FROM `domain` WHERE `id`= @Domain);

INSERT INTO `account` (`domain`,`code`,`description`,`alias`,`entryEnabled`,`level`) 
	(SELECT @Domain,`code`,`description`,`alias`,`entryEnabled`,`level` FROM `account` WHERE `domain` = @parentDomain);  


INSERT INTO `app_param` (`domain`,`name`,`value`) VALUES
  (@Domain,'ACC_DEFAULT_CASH_ACC',(SELECT `id` FROM `account` WHERE `domain` = @Domain AND `code` = '570000000')),
  (@Domain,'ACC_DEFAULT_CHARGED_RET_ACC',(SELECT `id` FROM `account` WHERE `domain` = @Domain AND `code` = '475100000')),
  (@Domain,'ACC_DEFAULT_CHARGED_VAT_ACC',(SELECT `id` FROM `account` WHERE `domain` = @Domain AND `code` = '477000000')),
  (@Domain,'ACC_DEFAULT_COMPANY_SOC_INS_ACC',(SELECT `id` FROM `account` WHERE `domain` = @Domain AND `code` = '642000000')),
  (@Domain,'ACC_DEFAULT_DEBT_INTEREST_ACC',(SELECT `id` FROM `account` WHERE `domain` = @Domain AND `code` = '662000000')),
  (@Domain,'ACC_DEFAULT_FINAN_EXPENSES_ACC',(SELECT `id` FROM `account` WHERE `domain` = @Domain AND `code` = '669000000')),
  (@Domain,'ACC_DEFAULT_PAID_RET_ACC',(SELECT `id` FROM `account` WHERE `domain` = @Domain AND `code` = '473000000')),
  (@Domain,'ACC_DEFAULT_PAID_VAT_ACC',(SELECT `id` FROM `account` WHERE `domain` = @Domain AND `code` = '472000000')),
  (@Domain,'ACC_DEFAULT_PENDING_SALARY_ACC',(SELECT `id` FROM `account` WHERE `domain` = @Domain AND `code` = '465000000')),
  (@Domain,'ACC_DEFAULT_PURCHASE_ACC',(SELECT `id` FROM `account` WHERE `domain` = @Domain AND `code` = '600000000')),
  (@Domain,'ACC_DEFAULT_SALARY_ACC',(SELECT `id` FROM `account` WHERE `domain` = @Domain AND `code` = '640000000')),
  (@Domain,'ACC_DEFAULT_SALES_ACC',(SELECT `id` FROM `account` WHERE `domain` = @Domain AND `code` = '700000000')),
  (@Domain,'ACC_DEFAULT_SOCIAL_INSURANCE_ACC',(SELECT `id` FROM `account` WHERE `domain` = @Domain AND `code` = '476000000')),
  (@Domain,'APP_PRINT_HEADER_PARAM','false'),
  (@Domain,'APP_PRINT_RECORD_DATA_PARAM','false');

INSERT INTO `balance` (`domain`,`name`,`removable`,`type`) 
	(SELECT @Domain,`name`,`removable`,`type` FROM `balance` WHERE `domain` = @parentDomain);  

SET @Balance1 = (SELECT min(id) FROM `balance` WHERE `domain`= @Domain AND `type`=0);
SET @Balance2 = (SELECT min(id) FROM `balance` WHERE `domain`= @Domain AND `type`=1);
SET @Balance3 = (SELECT max(id) FROM `balance` WHERE `domain`= @Domain AND `type`=0);
SET @Balance4 = (SELECT max(id) FROM `balance` WHERE `domain`= @Domain AND `type`=1);
SET @Balance5 = (SELECT min(id) FROM `balance` WHERE `domain`= @Domain AND `type`=2);

SET @BalanceParent1 = (SELECT min(id) FROM `balance` WHERE `domain`= @parentDomain AND `type`=0);
SET @BalanceParent2 = (SELECT min(id) FROM `balance` WHERE `domain`= @parentDomain AND `type`=1);
SET @BalanceParent3 = (SELECT max(id) FROM `balance` WHERE `domain`= @parentDomain AND `type`=0);
SET @BalanceParent4 = (SELECT max(id) FROM `balance` WHERE `domain`= @parentDomain AND `type`=1);
SET @BalanceParent5 = (SELECT min(id) FROM `balance` WHERE `domain`= @parentDomain AND `type`=2);

INSERT INTO `balance_detail` (`domain`,`balance`,`code`,`description`,`accounts`,`sortKey`,`title`,`internal_calculation`,`visible`,`zeroFlag`,`creditNature`) 
	(SELECT @Domain,@Balance1,`code`,`description`,`accounts`,`sortKey`,`title`,`internal_calculation`,`visible`,`zeroFlag`,`creditNature`
		FROM `balance_detail` WHERE `domain` = @parentDomain AND `balance` = @BalanceParent1);    

INSERT INTO `balance_detail` (`domain`,`balance`,`code`,`description`,`accounts`,`sortKey`,`title`,`internal_calculation`,`visible`,`zeroFlag`,`creditNature`) 
	(SELECT @Domain,@Balance2,`code`,`description`,`accounts`,`sortKey`,`title`,`internal_calculation`,`visible`,`zeroFlag`,`creditNature`
		FROM `balance_detail` WHERE `domain` = @parentDomain AND `balance` = @BalanceParent2);    

INSERT INTO `balance_detail` (`domain`,`balance`,`code`,`description`,`accounts`,`sortKey`,`title`,`internal_calculation`,`visible`,`zeroFlag`,`creditNature`) 
	(SELECT @Domain,@Balance3,`code`,`description`,`accounts`,`sortKey`,`title`,`internal_calculation`,`visible`,`zeroFlag`,`creditNature`
		FROM `balance_detail` WHERE `domain` = @parentDomain AND `balance` = @BalanceParent3);    

INSERT INTO `balance_detail` (`domain`,`balance`,`code`,`description`,`accounts`,`sortKey`,`title`,`internal_calculation`,`visible`,`zeroFlag`,`creditNature`) 
	(SELECT @Domain,@Balance4,`code`,`description`,`accounts`,`sortKey`,`title`,`internal_calculation`,`visible`,`zeroFlag`,`creditNature`
		FROM `balance_detail` WHERE `domain` = @parentDomain AND `balance` = @BalanceParent4);

INSERT INTO `balance_detail` (`domain`,`balance`,`code`,`description`,`accounts`,`sortKey`,`title`,`internal_calculation`,`visible`,`zeroFlag`,`creditNature`) 
	(SELECT @Domain,@Balance5,`code`,`description`,`accounts`,`sortKey`,`title`,`internal_calculation`,`visible`,`zeroFlag`,`creditNature`
		FROM `balance_detail` WHERE `domain` = @parentDomain AND `balance` = @BalanceParent5);
	
INSERT INTO `bank` (`domain`,`name`,`code`)  
	(SELECT @Domain,`name`,`code` FROM `bank` WHERE `domain` = @parentDomain);  

INSERT INTO `deduction_concept` (`domain`,`code`,`description`,`type`,`description_decorable`,`expression`)  
	(SELECT @Domain,`code`,`description`,`type`,`description_decorable`,`expression` FROM `deduction_concept` WHERE `domain` = @parentDomain);  

INSERT INTO `geozone` (`domain`,`name`,`code`,`system`)  
	(SELECT @Domain,`name`,`code`,`system` FROM `geozone` WHERE `domain` = @parentDomain);  

INSERT INTO `geotree` (`domain`,`parent`,`child`) 
	SELECT @Domain
		,(SELECT id FROM `geozone` WHERE `domain`= @Domain AND `code`=(SELECT `code` FROM `geozone` WHERE `id` = `parent`))
		,(SELECT id FROM `geozone` WHERE `domain`= @Domain AND `code`=(SELECT `code` FROM `geozone` WHERE `id` = `child`))
	 FROM `geotree` WHERE `domain` = @parentDomain;

  
INSERT INTO `pay_method` (`domain`,`name`,`type`)  
	(SELECT @Domain,`name`,`type` FROM `pay_method` WHERE `domain` = @parentDomain);  

INSERT INTO `payment_concept` (`domain`,`code`,`description`,`type`,`description_decorable`,`expression`,`irpf_expression`,`quote_expression`)  
	(SELECT @Domain,`code`,`description`,`type`,`description_decorable`,`expression`,`irpf_expression`,`quote_expression` FROM `payment_concept` WHERE `domain` = @parentDomain);  

INSERT INTO `pcategory` (`domain`,`name`,`detail_pattern`,`pcategory_group`)  
	(SELECT @Domain,`name`,`detail_pattern`,`pcategory_group` FROM `pcategory` WHERE `domain` = @parentDomain);  

INSERT INTO `scope` (`domain`,`description`)  
	(SELECT @Domain,`description` FROM `scope` WHERE `domain` = @parentDomain);  

INSERT INTO `system_cost` (`domain`,`start_date`,`end_date`,`description`,`expression`,`type`,`code`) 
	(SELECT @Domain,`start_date`,`end_date`,`description`,`expression`,`type`,`code` FROM `system_cost` WHERE `domain` = @parentDomain);  

INSERT INTO `system_data` (`domain`,`name`,`expression`,`start_date`,`end_date`,`read_only`,`comments`) 
	(SELECT @Domain,`name`,`expression`,`start_date`,`end_date`,`read_only`,`comments` FROM `system_data` WHERE `domain` = @parentDomain);  

INSERT INTO `system_deduction` (`domain`,`type`,`deduction_concept`,`description`,`description_decorable`,`expression`,`start_date`,`end_date`,`month`)  
	(SELECT @Domain,`type`,`deduction_concept`,`description`,`description_decorable`,`expression`,`start_date`,`end_date`,`month` FROM `system_deduction` WHERE `domain` = @parentDomain);  

INSERT INTO `system_payment` (`domain`,`type`,`payment_concept`,`description`,`description_decorable`,`expression`,`irpf_expression`,`quote_expression`,`start_date`,`month`,`end_date`,`salary_type`)  
	(SELECT @Domain,`type`,`payment_concept`,`description`,`description_decorable`,`expression`,`irpf_expression`,`quote_expression`,`start_date`,`month`,`end_date`,`salary_type` FROM `system_payment` WHERE `domain` = @parentDomain);  

INSERT INTO `tax` (`domain`,`name`,`tax_type`,`percentage`,`surcharge`,`start_date`,`vat_deduction_type`,`withholding_type`)  
	(SELECT @Domain,`name`,`tax_type`,`percentage`,`surcharge`,`start_date`,`vat_deduction_type`,`withholding_type` FROM `tax` WHERE `domain` = @parentDomain);  

INSERT INTO `tax_detail` (`domain`,`tax`,`start_date`,`end_date`,`value`,`surcharge`) 
	(SELECT @Domain,(SELECT id FROM `tax` WHERE `domain`= @Domain AND `name`= (SELECT name FROM `tax` WHERE `id`= `tax`)),`start_date`,`end_date`,`value`,`surcharge` FROM `tax_detail` WHERE `domain` = @parentDomain);  

INSERT INTO `warehouse` (`domain`,`name`,`workplace`) 
	(SELECT @Domain,`name`,`workplace` FROM `warehouse` WHERE `domain` = @parentDomain AND `workplace` IS NULL);  

COMMIT;

SET FOREIGN_KEY_CHECKS=1;
