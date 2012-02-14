package com.code.aon.fiscal.dao;

import com.code.aon.common.dao.DAOConstants;
import com.code.aon.common.dao.DAOConstantsEntry;
import com.code.aon.fiscal.Mod347;
import com.code.aon.fiscal.Mod347Detail;
import com.code.aon.fiscal.ProfessionalRetention;
import com.code.aon.fiscal.Renting;
import com.code.aon.fiscal.RentingDetail;
import com.code.aon.fiscal.VatTax;
import com.code.aon.fiscal.VatTaxDetail;
import com.code.aon.fiscal.VatTaxDeclaration;

/** 
* Interface for holding entity properties constants.
*/ 
public interface IFiscalAlias {



	/** 
	* DAOConstantsEntry for Mod347 entity.
	*/ 
	DAOConstantsEntry MOD347_ENTRY = DAOConstants.getDAOConstant(Mod347.class);

	/** 
	* Alias value: Mod347_administration
	* Hibernate value: Mod347.administration
	*/
	String  MOD347_ADMINISTRATION = MOD347_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Mod347_comments
	* Hibernate value: Mod347.comments
	*/
	String  MOD347_COMMENTS = MOD347_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Mod347_complementary
	* Hibernate value: Mod347.complementary
	*/
	String  MOD347_COMPLEMENTARY = MOD347_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Mod347_id
	* Hibernate value: Mod347.id
	*/
	String  MOD347_ID = MOD347_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Mod347_number
	* Hibernate value: Mod347.number
	*/
	String  MOD347_NUMBER = MOD347_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Mod347_replacedNumber
	* Hibernate value: Mod347.replacedNumber
	*/
	String  MOD347_REPLACED_NUMBER = MOD347_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Mod347_replacement
	* Hibernate value: Mod347.replacement
	*/
	String  MOD347_REPLACEMENT = MOD347_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Mod347_securityLevel
	* Hibernate value: Mod347.securityLevel
	*/
	String  MOD347_SECURITY_LEVEL = MOD347_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Mod347_status
	* Hibernate value: Mod347.status
	*/
	String  MOD347_STATUS = MOD347_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: Mod347_year
	* Hibernate value: Mod347.year
	*/
	String  MOD347_YEAR = MOD347_ENTRY.getAliasNames()[9];



	/** 
	* DAOConstantsEntry for Mod347Detail entity.
	*/ 
	DAOConstantsEntry MOD347DETAIL_ENTRY = DAOConstants.getDAOConstant(Mod347Detail.class);

	/** 
	* Alias value: Mod347Detail_amount
	* Hibernate value: Mod347Detail.amount
	*/
	String  MOD347DETAIL_AMOUNT = MOD347DETAIL_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Mod347Detail_country
	* Hibernate value: Mod347Detail.country
	*/
	String  MOD347DETAIL_COUNTRY = MOD347DETAIL_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Mod347Detail_document
	* Hibernate value: Mod347Detail.document
	*/
	String  MOD347DETAIL_DOCUMENT = MOD347DETAIL_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Mod347Detail_firstQuarterAmount
	* Hibernate value: Mod347Detail.firstQuarterAmount
	*/
	String  MOD347DETAIL_FIRST_QUARTER_AMOUNT = MOD347DETAIL_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Mod347Detail_fourthQuarterAmount
	* Hibernate value: Mod347Detail.fourthQuarterAmount
	*/
	String  MOD347DETAIL_FOURTH_QUARTER_AMOUNT = MOD347DETAIL_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Mod347Detail_id
	* Hibernate value: Mod347Detail.id
	*/
	String  MOD347DETAIL_ID = MOD347DETAIL_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Mod347Detail_mod347_id
	* Hibernate value: Mod347Detail.mod347.id
	*/
	String  MOD347DETAIL_MOD347_ID = MOD347DETAIL_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Mod347Detail_name
	* Hibernate value: Mod347Detail.name
	*/
	String  MOD347DETAIL_NAME = MOD347DETAIL_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Mod347Detail_province
	* Hibernate value: Mod347Detail.province
	*/
	String  MOD347DETAIL_PROVINCE = MOD347DETAIL_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: Mod347Detail_registry
	* Hibernate value: Mod347Detail.registry
	*/
	String  MOD347DETAIL_REGISTRY = MOD347DETAIL_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: Mod347Detail_secondQuarterAmount
	* Hibernate value: Mod347Detail.secondQuarterAmount
	*/
	String  MOD347DETAIL_SECOND_QUARTER_AMOUNT = MOD347DETAIL_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: Mod347Detail_thirdQuarterAmount
	* Hibernate value: Mod347Detail.thirdQuarterAmount
	*/
	String  MOD347DETAIL_THIRD_QUARTER_AMOUNT = MOD347DETAIL_ENTRY.getAliasNames()[11];

	/** 
	* Alias value: Mod347Detail_type
	* Hibernate value: Mod347Detail.type
	*/
	String  MOD347DETAIL_TYPE = MOD347DETAIL_ENTRY.getAliasNames()[12];



	/** 
	* DAOConstantsEntry for ProfessionalRetention entity.
	*/ 
	DAOConstantsEntry PROFESSIONAL_RETENTION_ENTRY = DAOConstants.getDAOConstant(ProfessionalRetention.class);

	/** 
	* Alias value: ProfessionalRetention_concept
	* Hibernate value: ProfessionalRetention.concept
	*/
	String  PROFESSIONAL_RETENTION_CONCEPT = PROFESSIONAL_RETENTION_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: ProfessionalRetention_document
	* Hibernate value: ProfessionalRetention.document
	*/
	String  PROFESSIONAL_RETENTION_DOCUMENT = PROFESSIONAL_RETENTION_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: ProfessionalRetention_documentCountry
	* Hibernate value: ProfessionalRetention.documentCountry
	*/
	String  PROFESSIONAL_RETENTION_DOCUMENT_COUNTRY = PROFESSIONAL_RETENTION_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: ProfessionalRetention_documentType
	* Hibernate value: ProfessionalRetention.documentType
	*/
	String  PROFESSIONAL_RETENTION_DOCUMENT_TYPE = PROFESSIONAL_RETENTION_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: ProfessionalRetention_enterprise_id
	* Hibernate value: ProfessionalRetention.enterprise.id
	*/
	String  PROFESSIONAL_RETENTION_ENTERPRISE_ID = PROFESSIONAL_RETENTION_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: ProfessionalRetention_id
	* Hibernate value: ProfessionalRetention.id
	*/
	String  PROFESSIONAL_RETENTION_ID = PROFESSIONAL_RETENTION_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: ProfessionalRetention_inKind
	* Hibernate value: ProfessionalRetention.inKind
	*/
	String  PROFESSIONAL_RETENTION_IN_KIND = PROFESSIONAL_RETENTION_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: ProfessionalRetention_key
	* Hibernate value: ProfessionalRetention.key
	*/
	String  PROFESSIONAL_RETENTION_KEY = PROFESSIONAL_RETENTION_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: ProfessionalRetention_name
	* Hibernate value: ProfessionalRetention.name
	*/
	String  PROFESSIONAL_RETENTION_NAME = PROFESSIONAL_RETENTION_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: ProfessionalRetention_paymentDate
	* Hibernate value: ProfessionalRetention.paymentDate
	*/
	String  PROFESSIONAL_RETENTION_PAYMENT_DATE = PROFESSIONAL_RETENTION_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: ProfessionalRetention_percent
	* Hibernate value: ProfessionalRetention.percent
	*/
	String  PROFESSIONAL_RETENTION_PERCENT = PROFESSIONAL_RETENTION_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: ProfessionalRetention_quota
	* Hibernate value: ProfessionalRetention.quota
	*/
	String  PROFESSIONAL_RETENTION_QUOTA = PROFESSIONAL_RETENTION_ENTRY.getAliasNames()[11];

	/** 
	* Alias value: ProfessionalRetention_subkey
	* Hibernate value: ProfessionalRetention.subkey
	*/
	String  PROFESSIONAL_RETENTION_SUBKEY = PROFESSIONAL_RETENTION_ENTRY.getAliasNames()[12];

	/** 
	* Alias value: ProfessionalRetention_taxableBase
	* Hibernate value: ProfessionalRetention.taxableBase
	*/
	String  PROFESSIONAL_RETENTION_TAXABLE_BASE = PROFESSIONAL_RETENTION_ENTRY.getAliasNames()[13];



	/** 
	* DAOConstantsEntry for Renting entity.
	*/ 
	DAOConstantsEntry RENTING_ENTRY = DAOConstants.getDAOConstant(Renting.class);

	/** 
	* Alias value: Renting_accountDeposit
	* Hibernate value: Renting.accountDeposit
	*/
	String  RENTING_ACCOUNT_DEPOSIT = RENTING_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Renting_accountDepositAccumulated
	* Hibernate value: Renting.accountDepositAccumulated
	*/
	String  RENTING_ACCOUNT_DEPOSIT_ACCUMULATED = RENTING_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Renting_accountDepositAdjust
	* Hibernate value: Renting.accountDepositAdjust
	*/
	String  RENTING_ACCOUNT_DEPOSIT_ADJUST = RENTING_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Renting_accountDepositDeclared
	* Hibernate value: Renting.accountDepositDeclared
	*/
	String  RENTING_ACCOUNT_DEPOSIT_DECLARED = RENTING_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Renting_accountDepositResult
	* Hibernate value: Renting.accountDepositResult
	*/
	String  RENTING_ACCOUNT_DEPOSIT_RESULT = RENTING_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Renting_administration
	* Hibernate value: Renting.administration
	*/
	String  RENTING_ADMINISTRATION = RENTING_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Renting_comments
	* Hibernate value: Renting.comments
	*/
	String  RENTING_COMMENTS = RENTING_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Renting_complementary
	* Hibernate value: Renting.complementary
	*/
	String  RENTING_COMPLEMENTARY = RENTING_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Renting_delayInterest
	* Hibernate value: Renting.delayInterest
	*/
	String  RENTING_DELAY_INTEREST = RENTING_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: Renting_extraCharge
	* Hibernate value: Renting.extraCharge
	*/
	String  RENTING_EXTRA_CHARGE = RENTING_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: Renting_id
	* Hibernate value: Renting.id
	*/
	String  RENTING_ID = RENTING_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: Renting_lessorCount
	* Hibernate value: Renting.lessorCount
	*/
	String  RENTING_LESSOR_COUNT = RENTING_ENTRY.getAliasNames()[11];

	/** 
	* Alias value: Renting_lessorCountAccumulated
	* Hibernate value: Renting.lessorCountAccumulated
	*/
	String  RENTING_LESSOR_COUNT_ACCUMULATED = RENTING_ENTRY.getAliasNames()[12];

	/** 
	* Alias value: Renting_lessorCountAdjust
	* Hibernate value: Renting.lessorCountAdjust
	*/
	String  RENTING_LESSOR_COUNT_ADJUST = RENTING_ENTRY.getAliasNames()[13];

	/** 
	* Alias value: Renting_lessorCountDeclared
	* Hibernate value: Renting.lessorCountDeclared
	*/
	String  RENTING_LESSOR_COUNT_DECLARED = RENTING_ENTRY.getAliasNames()[14];

	/** 
	* Alias value: Renting_lessorCountInKind
	* Hibernate value: Renting.lessorCountInKind
	*/
	String  RENTING_LESSOR_COUNT_IN_KIND = RENTING_ENTRY.getAliasNames()[15];

	/** 
	* Alias value: Renting_lessorCountInKindAccumulated
	* Hibernate value: Renting.lessorCountInKindAccumulated
	*/
	String  RENTING_LESSOR_COUNT_IN_KIND_ACCUMULATED = RENTING_ENTRY.getAliasNames()[16];

	/** 
	* Alias value: Renting_lessorCountInKindAdjust
	* Hibernate value: Renting.lessorCountInKindAdjust
	*/
	String  RENTING_LESSOR_COUNT_IN_KIND_ADJUST = RENTING_ENTRY.getAliasNames()[17];

	/** 
	* Alias value: Renting_lessorCountInKindDeclared
	* Hibernate value: Renting.lessorCountInKindDeclared
	*/
	String  RENTING_LESSOR_COUNT_IN_KIND_DECLARED = RENTING_ENTRY.getAliasNames()[18];

	/** 
	* Alias value: Renting_lessorCountInKindResult
	* Hibernate value: Renting.lessorCountInKindResult
	*/
	String  RENTING_LESSOR_COUNT_IN_KIND_RESULT = RENTING_ENTRY.getAliasNames()[19];

	/** 
	* Alias value: Renting_lessorCountResult
	* Hibernate value: Renting.lessorCountResult
	*/
	String  RENTING_LESSOR_COUNT_RESULT = RENTING_ENTRY.getAliasNames()[20];

	/** 
	* Alias value: Renting_period
	* Hibernate value: Renting.period
	*/
	String  RENTING_PERIOD = RENTING_ENTRY.getAliasNames()[21];

	/** 
	* Alias value: Renting_registryBank_id
	* Hibernate value: Renting.registryBank.id
	*/
	String  RENTING_REGISTRY_BANK_ID = RENTING_ENTRY.getAliasNames()[22];

	/** 
	* Alias value: Renting_remunerationInKind
	* Hibernate value: Renting.remunerationInKind
	*/
	String  RENTING_REMUNERATION_IN_KIND = RENTING_ENTRY.getAliasNames()[23];

	/** 
	* Alias value: Renting_remunerationInKindAccumulated
	* Hibernate value: Renting.remunerationInKindAccumulated
	*/
	String  RENTING_REMUNERATION_IN_KIND_ACCUMULATED = RENTING_ENTRY.getAliasNames()[24];

	/** 
	* Alias value: Renting_remunerationInKindAdjust
	* Hibernate value: Renting.remunerationInKindAdjust
	*/
	String  RENTING_REMUNERATION_IN_KIND_ADJUST = RENTING_ENTRY.getAliasNames()[25];

	/** 
	* Alias value: Renting_remunerationInKindDeclared
	* Hibernate value: Renting.remunerationInKindDeclared
	*/
	String  RENTING_REMUNERATION_IN_KIND_DECLARED = RENTING_ENTRY.getAliasNames()[26];

	/** 
	* Alias value: Renting_remunerationInKindResult
	* Hibernate value: Renting.remunerationInKindResult
	*/
	String  RENTING_REMUNERATION_IN_KIND_RESULT = RENTING_ENTRY.getAliasNames()[27];

	/** 
	* Alias value: Renting_rentingAmount
	* Hibernate value: Renting.rentingAmount
	*/
	String  RENTING_RENTING_AMOUNT = RENTING_ENTRY.getAliasNames()[28];

	/** 
	* Alias value: Renting_rentingAmountAccumulated
	* Hibernate value: Renting.rentingAmountAccumulated
	*/
	String  RENTING_RENTING_AMOUNT_ACCUMULATED = RENTING_ENTRY.getAliasNames()[29];

	/** 
	* Alias value: Renting_rentingAmountAdjust
	* Hibernate value: Renting.rentingAmountAdjust
	*/
	String  RENTING_RENTING_AMOUNT_ADJUST = RENTING_ENTRY.getAliasNames()[30];

	/** 
	* Alias value: Renting_rentingAmountDeclared
	* Hibernate value: Renting.rentingAmountDeclared
	*/
	String  RENTING_RENTING_AMOUNT_DECLARED = RENTING_ENTRY.getAliasNames()[31];

	/** 
	* Alias value: Renting_rentingAmountResult
	* Hibernate value: Renting.rentingAmountResult
	*/
	String  RENTING_RENTING_AMOUNT_RESULT = RENTING_ENTRY.getAliasNames()[32];

	/** 
	* Alias value: Renting_replacement
	* Hibernate value: Renting.replacement
	*/
	String  RENTING_REPLACEMENT = RENTING_ENTRY.getAliasNames()[33];

	/** 
	* Alias value: Renting_retention
	* Hibernate value: Renting.retention
	*/
	String  RENTING_RETENTION = RENTING_ENTRY.getAliasNames()[34];

	/** 
	* Alias value: Renting_retentionAccumulated
	* Hibernate value: Renting.retentionAccumulated
	*/
	String  RENTING_RETENTION_ACCUMULATED = RENTING_ENTRY.getAliasNames()[35];

	/** 
	* Alias value: Renting_retentionAdjust
	* Hibernate value: Renting.retentionAdjust
	*/
	String  RENTING_RETENTION_ADJUST = RENTING_ENTRY.getAliasNames()[36];

	/** 
	* Alias value: Renting_retentionDeclared
	* Hibernate value: Renting.retentionDeclared
	*/
	String  RENTING_RETENTION_DECLARED = RENTING_ENTRY.getAliasNames()[37];

	/** 
	* Alias value: Renting_retentionResult
	* Hibernate value: Renting.retentionResult
	*/
	String  RENTING_RETENTION_RESULT = RENTING_ENTRY.getAliasNames()[38];

	/** 
	* Alias value: Renting_securityLevel
	* Hibernate value: Renting.securityLevel
	*/
	String  RENTING_SECURITY_LEVEL = RENTING_ENTRY.getAliasNames()[39];

	/** 
	* Alias value: Renting_status
	* Hibernate value: Renting.status
	*/
	String  RENTING_STATUS = RENTING_ENTRY.getAliasNames()[40];

	/** 
	* Alias value: Renting_totalTaxDebt
	* Hibernate value: Renting.totalTaxDebt
	*/
	String  RENTING_TOTAL_TAX_DEBT = RENTING_ENTRY.getAliasNames()[41];

	/** 
	* Alias value: Renting_year
	* Hibernate value: Renting.year
	*/
	String  RENTING_YEAR = RENTING_ENTRY.getAliasNames()[42];



	/** 
	* DAOConstantsEntry for RentingDetail entity.
	*/ 
	DAOConstantsEntry RENTING_DETAIL_ENTRY = DAOConstants.getDAOConstant(RentingDetail.class);

	/** 
	* Alias value: RentingDetail_accountDeposit
	* Hibernate value: RentingDetail.accountDeposit
	*/
	String  RENTING_DETAIL_ACCOUNT_DEPOSIT = RENTING_DETAIL_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: RentingDetail_accrualPeriod
	* Hibernate value: RentingDetail.accrualPeriod
	*/
	String  RENTING_DETAIL_ACCRUAL_PERIOD = RENTING_DETAIL_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: RentingDetail_address
	* Hibernate value: RentingDetail.address
	*/
	String  RENTING_DETAIL_ADDRESS = RENTING_DETAIL_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: RentingDetail_city
	* Hibernate value: RentingDetail.city
	*/
	String  RENTING_DETAIL_CITY = RENTING_DETAIL_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: RentingDetail_document
	* Hibernate value: RentingDetail.document
	*/
	String  RENTING_DETAIL_DOCUMENT = RENTING_DETAIL_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: RentingDetail_id
	* Hibernate value: RentingDetail.id
	*/
	String  RENTING_DETAIL_ID = RENTING_DETAIL_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: RentingDetail_name
	* Hibernate value: RentingDetail.name
	*/
	String  RENTING_DETAIL_NAME = RENTING_DETAIL_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: RentingDetail_paidReturns
	* Hibernate value: RentingDetail.paidReturns
	*/
	String  RENTING_DETAIL_PAID_RETURNS = RENTING_DETAIL_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: RentingDetail_percent
	* Hibernate value: RentingDetail.percent
	*/
	String  RENTING_DETAIL_PERCENT = RENTING_DETAIL_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: RentingDetail_province
	* Hibernate value: RentingDetail.province
	*/
	String  RENTING_DETAIL_PROVINCE = RENTING_DETAIL_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: RentingDetail_renting_id
	* Hibernate value: RentingDetail.renting.id
	*/
	String  RENTING_DETAIL_RENTING_ID = RENTING_DETAIL_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: RentingDetail_type
	* Hibernate value: RentingDetail.type
	*/
	String  RENTING_DETAIL_TYPE = RENTING_DETAIL_ENTRY.getAliasNames()[11];



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
	* Alias value: VatTax_prorata
	* Hibernate value: VatTax.prorata
	*/
	String  VAT_TAX_PRORATA = VAT_TAX_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: VatTax_replacement
	* Hibernate value: VatTax.replacement
	*/
	String  VAT_TAX_REPLACEMENT = VAT_TAX_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: VatTax_securityLevel
	* Hibernate value: VatTax.securityLevel
	*/
	String  VAT_TAX_SECURITY_LEVEL = VAT_TAX_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: VatTax_status
	* Hibernate value: VatTax.status
	*/
	String  VAT_TAX_STATUS = VAT_TAX_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: VatTax_taxRefundRegistry
	* Hibernate value: VatTax.taxRefundRegistry
	*/
	String  VAT_TAX_TAX_REFUND_REGISTRY = VAT_TAX_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: VatTax_year
	* Hibernate value: VatTax.year
	*/
	String  VAT_TAX_YEAR = VAT_TAX_ENTRY.getAliasNames()[10];



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
	* Alias value: VatTaxDetail_vatTax_securityLevel
	* Hibernate value: VatTaxDetail.vatTax.securityLevel
	*/
	String  VAT_TAX_DETAIL_VAT_TAX_SECURITY_LEVEL = VAT_TAX_DETAIL_ENTRY.getAliasNames()[14];



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