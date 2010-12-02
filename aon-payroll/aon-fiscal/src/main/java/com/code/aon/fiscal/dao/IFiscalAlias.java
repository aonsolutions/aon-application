package com.code.aon.fiscal.dao;

import com.code.aon.common.dao.DAOConstants;
import com.code.aon.common.dao.DAOConstantsEntry;
import com.code.aon.fiscal.VatTax;
import com.code.aon.fiscal.VatTaxDetail;
import com.code.aon.fiscal.VatTaxDeclaration;

/** 
* Interface for holding entity properties constants.
*/ 
public interface IFiscalAlias {



	/** 
	* DAOConstantsEntry for VatTax entity.
	*/ 
	DAOConstantsEntry VAT_TAX_ENTRY = DAOConstants.getDAOConstant(VatTax.class);

	/** 
	* Alias value: VatTax_comments
	* Hibernate value: VatTax.comments
	*/
	String  VAT_TAX_COMMENTS = VAT_TAX_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: VatTax_complementary
	* Hibernate value: VatTax.complementary
	*/
	String  VAT_TAX_COMPLEMENTARY = VAT_TAX_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: VatTax_id
	* Hibernate value: VatTax.id
	*/
	String  VAT_TAX_ID = VAT_TAX_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: VatTax_number
	* Hibernate value: VatTax.number
	*/
	String  VAT_TAX_NUMBER = VAT_TAX_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: VatTax_period
	* Hibernate value: VatTax.period
	*/
	String  VAT_TAX_PERIOD = VAT_TAX_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: VatTax_replacement
	* Hibernate value: VatTax.replacement
	*/
	String  VAT_TAX_REPLACEMENT = VAT_TAX_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: VatTax_securityLevel
	* Hibernate value: VatTax.securityLevel
	*/
	String  VAT_TAX_SECURITY_LEVEL = VAT_TAX_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: VatTax_status
	* Hibernate value: VatTax.status
	*/
	String  VAT_TAX_STATUS = VAT_TAX_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: VatTax_taxRefundRegistry
	* Hibernate value: VatTax.taxRefundRegistry
	*/
	String  VAT_TAX_TAX_REFUND_REGISTRY = VAT_TAX_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: VatTax_year
	* Hibernate value: VatTax.year
	*/
	String  VAT_TAX_YEAR = VAT_TAX_ENTRY.getAliasNames()[9];



	/** 
	* DAOConstantsEntry for VatTaxDetail entity.
	*/ 
	DAOConstantsEntry VAT_TAX_DETAIL_ENTRY = DAOConstants.getDAOConstant(VatTaxDetail.class);

	/** 
	* Alias value: VatTaxDetail_deductibleQuota
	* Hibernate value: VatTaxDetail.deductibleQuota
	*/
	String  VAT_TAX_DETAIL_DEDUCTIBLE_QUOTA = VAT_TAX_DETAIL_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: VatTaxDetail_deductibleQuotaAdjust
	* Hibernate value: VatTaxDetail.deductibleQuotaAdjust
	*/
	String  VAT_TAX_DETAIL_DEDUCTIBLE_QUOTA_ADJUST = VAT_TAX_DETAIL_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: VatTaxDetail_id
	* Hibernate value: VatTaxDetail.id
	*/
	String  VAT_TAX_DETAIL_ID = VAT_TAX_DETAIL_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: VatTaxDetail_key
	* Hibernate value: VatTaxDetail.key
	*/
	String  VAT_TAX_DETAIL_KEY = VAT_TAX_DETAIL_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: VatTaxDetail_percent
	* Hibernate value: VatTaxDetail.percent
	*/
	String  VAT_TAX_DETAIL_PERCENT = VAT_TAX_DETAIL_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: VatTaxDetail_quota
	* Hibernate value: VatTaxDetail.quota
	*/
	String  VAT_TAX_DETAIL_QUOTA = VAT_TAX_DETAIL_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: VatTaxDetail_quotaAdjust
	* Hibernate value: VatTaxDetail.quotaAdjust
	*/
	String  VAT_TAX_DETAIL_QUOTA_ADJUST = VAT_TAX_DETAIL_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: VatTaxDetail_taxableBase
	* Hibernate value: VatTaxDetail.taxableBase
	*/
	String  VAT_TAX_DETAIL_TAXABLE_BASE = VAT_TAX_DETAIL_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: VatTaxDetail_taxableBaseAdjust
	* Hibernate value: VatTaxDetail.taxableBaseAdjust
	*/
	String  VAT_TAX_DETAIL_TAXABLE_BASE_ADJUST = VAT_TAX_DETAIL_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: VatTaxDetail_vatTax_id
	* Hibernate value: VatTaxDetail.vatTax.id
	*/
	String  VAT_TAX_DETAIL_VAT_TAX_ID = VAT_TAX_DETAIL_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: VatTaxDetail_vatTax_period
	* Hibernate value: VatTaxDetail.vatTax.period
	*/
	String  VAT_TAX_DETAIL_VAT_TAX_PERIOD = VAT_TAX_DETAIL_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: VatTaxDetail_vatTax_status
	* Hibernate value: VatTaxDetail.vatTax.status
	*/
	String  VAT_TAX_DETAIL_VAT_TAX_STATUS = VAT_TAX_DETAIL_ENTRY.getAliasNames()[11];

	/** 
	* Alias value: VatTaxDetail_vatTax_type
	* Hibernate value: VatTaxDetail.vatTax.type
	*/
	String  VAT_TAX_DETAIL_VAT_TAX_TYPE = VAT_TAX_DETAIL_ENTRY.getAliasNames()[12];

	/** 
	* Alias value: VatTaxDetail_vatTax_year
	* Hibernate value: VatTaxDetail.vatTax.year
	*/
	String  VAT_TAX_DETAIL_VAT_TAX_YEAR = VAT_TAX_DETAIL_ENTRY.getAliasNames()[13];



	/** 
	* DAOConstantsEntry for VatTaxDeclaration entity.
	*/ 
	DAOConstantsEntry VAT_TAX_DECLARATION_ENTRY = DAOConstants.getDAOConstant(VatTaxDeclaration.class);

	/** 
	* Alias value: VatTaxDeclaration_administration
	* Hibernate value: VatTaxDeclaration.administration
	*/
	String  VAT_TAX_DECLARATION_ADMINISTRATION = VAT_TAX_DECLARATION_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: VatTaxDeclaration_compensable
	* Hibernate value: VatTaxDeclaration.compensable
	*/
	String  VAT_TAX_DECLARATION_COMPENSABLE = VAT_TAX_DECLARATION_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: VatTaxDeclaration_compensate
	* Hibernate value: VatTaxDeclaration.compensate
	*/
	String  VAT_TAX_DECLARATION_COMPENSATE = VAT_TAX_DECLARATION_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: VatTaxDeclaration_delayInterest
	* Hibernate value: VatTaxDeclaration.delayInterest
	*/
	String  VAT_TAX_DECLARATION_DELAY_INTEREST = VAT_TAX_DECLARATION_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: VatTaxDeclaration_deposit
	* Hibernate value: VatTaxDeclaration.deposit
	*/
	String  VAT_TAX_DECLARATION_DEPOSIT = VAT_TAX_DECLARATION_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: VatTaxDeclaration_doneDeposits
	* Hibernate value: VatTaxDeclaration.doneDeposits
	*/
	String  VAT_TAX_DECLARATION_DONE_DEPOSITS = VAT_TAX_DECLARATION_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: VatTaxDeclaration_doneRefunds
	* Hibernate value: VatTaxDeclaration.doneRefunds
	*/
	String  VAT_TAX_DECLARATION_DONE_REFUNDS = VAT_TAX_DECLARATION_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: VatTaxDeclaration_extraCharge
	* Hibernate value: VatTaxDeclaration.extraCharge
	*/
	String  VAT_TAX_DECLARATION_EXTRA_CHARGE = VAT_TAX_DECLARATION_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: VatTaxDeclaration_id
	* Hibernate value: VatTaxDeclaration.id
	*/
	String  VAT_TAX_DECLARATION_ID = VAT_TAX_DECLARATION_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: VatTaxDeclaration_operationsVolume
	* Hibernate value: VatTaxDeclaration.operationsVolume
	*/
	String  VAT_TAX_DECLARATION_OPERATIONS_VOLUME = VAT_TAX_DECLARATION_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: VatTaxDeclaration_payBack
	* Hibernate value: VatTaxDeclaration.payBack
	*/
	String  VAT_TAX_DECLARATION_PAY_BACK = VAT_TAX_DECLARATION_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: VatTaxDeclaration_percent
	* Hibernate value: VatTaxDeclaration.percent
	*/
	String  VAT_TAX_DECLARATION_PERCENT = VAT_TAX_DECLARATION_ENTRY.getAliasNames()[11];

	/** 
	* Alias value: VatTaxDeclaration_previousDeposit
	* Hibernate value: VatTaxDeclaration.previousDeposit
	*/
	String  VAT_TAX_DECLARATION_PREVIOUS_DEPOSIT = VAT_TAX_DECLARATION_ENTRY.getAliasNames()[12];

	/** 
	* Alias value: VatTaxDeclaration_previousPayBack
	* Hibernate value: VatTaxDeclaration.previousPayBack
	*/
	String  VAT_TAX_DECLARATION_PREVIOUS_PAY_BACK = VAT_TAX_DECLARATION_ENTRY.getAliasNames()[13];

	/** 
	* Alias value: VatTaxDeclaration_previousYearCompensateQuota
	* Hibernate value: VatTaxDeclaration.previousYearCompensateQuota
	*/
	String  VAT_TAX_DECLARATION_PREVIOUS_YEAR_COMPENSATE_QUOTA = VAT_TAX_DECLARATION_ENTRY.getAliasNames()[14];

	/** 
	* Alias value: VatTaxDeclaration_quota
	* Hibernate value: VatTaxDeclaration.quota
	*/
	String  VAT_TAX_DECLARATION_QUOTA = VAT_TAX_DECLARATION_ENTRY.getAliasNames()[15];

	/** 
	* Alias value: VatTaxDeclaration_registryBank_id
	* Hibernate value: VatTaxDeclaration.registryBank.id
	*/
	String  VAT_TAX_DECLARATION_REGISTRY_BANK_ID = VAT_TAX_DECLARATION_ENTRY.getAliasNames()[16];

	/** 
	* Alias value: VatTaxDeclaration_status
	* Hibernate value: VatTaxDeclaration.status
	*/
	String  VAT_TAX_DECLARATION_STATUS = VAT_TAX_DECLARATION_ENTRY.getAliasNames()[17];

	/** 
	* Alias value: VatTaxDeclaration_totalTaxDebt
	* Hibernate value: VatTaxDeclaration.totalTaxDebt
	*/
	String  VAT_TAX_DECLARATION_TOTAL_TAX_DEBT = VAT_TAX_DECLARATION_ENTRY.getAliasNames()[18];

	/** 
	* Alias value: VatTaxDeclaration_vatTax_id
	* Hibernate value: VatTaxDeclaration.vatTax.id
	*/
	String  VAT_TAX_DECLARATION_VAT_TAX_ID = VAT_TAX_DECLARATION_ENTRY.getAliasNames()[19];

	/** 
	* Alias value: VatTaxDeclaration_vatTax_complementary
	* Hibernate value: VatTaxDeclaration.vatTax.complementary
	*/
	String  VAT_TAX_DECLARATION_VAT_TAX_COMPLEMENTARY = VAT_TAX_DECLARATION_ENTRY.getAliasNames()[20];

	/** 
	* Alias value: VatTaxDeclaration_vatTax_number
	* Hibernate value: VatTaxDeclaration.vatTax.number
	*/
	String  VAT_TAX_DECLARATION_VAT_TAX_NUMBER = VAT_TAX_DECLARATION_ENTRY.getAliasNames()[21];

	/** 
	* Alias value: VatTaxDeclaration_vatTax_period
	* Hibernate value: VatTaxDeclaration.vatTax.period
	*/
	String  VAT_TAX_DECLARATION_VAT_TAX_PERIOD = VAT_TAX_DECLARATION_ENTRY.getAliasNames()[22];

	/** 
	* Alias value: VatTaxDeclaration_vatTax_replacement
	* Hibernate value: VatTaxDeclaration.vatTax.replacement
	*/
	String  VAT_TAX_DECLARATION_VAT_TAX_REPLACEMENT = VAT_TAX_DECLARATION_ENTRY.getAliasNames()[23];

	/** 
	* Alias value: VatTaxDeclaration_vatTax_securityLevel
	* Hibernate value: VatTaxDeclaration.vatTax.securityLevel
	*/
	String  VAT_TAX_DECLARATION_VAT_TAX_SECURITY_LEVEL = VAT_TAX_DECLARATION_ENTRY.getAliasNames()[24];

	/** 
	* Alias value: VatTaxDeclaration_vatTax_status
	* Hibernate value: VatTaxDeclaration.vatTax.status
	*/
	String  VAT_TAX_DECLARATION_VAT_TAX_STATUS = VAT_TAX_DECLARATION_ENTRY.getAliasNames()[25];

	/** 
	* Alias value: VatTaxDeclaration_vatTax_taxRefundRegistry
	* Hibernate value: VatTaxDeclaration.vatTax.taxRefundRegistry
	*/
	String  VAT_TAX_DECLARATION_VAT_TAX_TAX_REFUND_REGISTRY = VAT_TAX_DECLARATION_ENTRY.getAliasNames()[26];

	/** 
	* Alias value: VatTaxDeclaration_vatTax_year
	* Hibernate value: VatTaxDeclaration.vatTax.year
	*/
	String  VAT_TAX_DECLARATION_VAT_TAX_YEAR = VAT_TAX_DECLARATION_ENTRY.getAliasNames()[27];

	/** 
	* Alias value: VatTaxDeclaration_withoutActivity
	* Hibernate value: VatTaxDeclaration.withoutActivity
	*/
	String  VAT_TAX_DECLARATION_WITHOUT_ACTIVITY = VAT_TAX_DECLARATION_ENTRY.getAliasNames()[28];


}