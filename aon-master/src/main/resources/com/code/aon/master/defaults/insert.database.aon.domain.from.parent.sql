
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
  (@Domain,'ACC_DEFAULT_PREPAYMENT_ACC',(SELECT `id` FROM `account` WHERE `domain` = @Domain AND `code` = '555900000')),
  (@Domain,'ACC_VAT_NEGATIVE_ADJUST_ACC',(SELECT `id` FROM `account` WHERE `domain` = @Domain AND `code` = '634100001')),
  (@Domain,'APP_PRINT_HEADER_PARAM','false'),
  (@Domain,'APP_PRINT_RECORD_DATA_PARAM','false');

COMMIT;

SET FOREIGN_KEY_CHECKS=1;
