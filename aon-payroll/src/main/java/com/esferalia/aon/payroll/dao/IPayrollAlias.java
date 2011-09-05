package com.esferalia.aon.payroll.dao;

import com.code.aon.common.dao.DAOConstants;
import com.code.aon.common.dao.DAOConstantsEntry;
import com.esferalia.aon.payroll.Agreement;
import com.esferalia.aon.payroll.AgreementExtra;
import com.esferalia.aon.payroll.AgreementLevel;
import com.esferalia.aon.payroll.AgreementLevelCategory;
import com.esferalia.aon.payroll.AgreementLevelData;
import com.esferalia.aon.payroll.AgreementPayment;
import com.esferalia.aon.payroll.BonusConcept;
import com.esferalia.aon.payroll.Certifica2Batch;
import com.esferalia.aon.payroll.Certifica2BatchData;
import com.esferalia.aon.payroll.Certifica2BatchDetail;
import com.esferalia.aon.payroll.CNO;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractAttachment;
import com.esferalia.aon.payroll.ContractBatch;
import com.esferalia.aon.payroll.ContractBatchDetail;
import com.esferalia.aon.payroll.ContractBonus;
import com.esferalia.aon.payroll.ContractCalendarEvent;
import com.esferalia.aon.payroll.ContractData;
import com.esferalia.aon.payroll.ContractDeduction;
import com.esferalia.aon.payroll.ContractEmbargo;
import com.esferalia.aon.payroll.ContractLeave;
import com.esferalia.aon.payroll.ContractLeaveDetail;
import com.esferalia.aon.payroll.ContractPayment;
import com.esferalia.aon.payroll.DeductionConcept;
import com.esferalia.aon.payroll.EnterpriseCCC;
import com.esferalia.aon.payroll.EnterpriseActivity;
import com.esferalia.aon.payroll.FanBatch;
import com.esferalia.aon.payroll.FanBatchDetail;
import com.esferalia.aon.payroll.GeozoneIrpf;
import com.esferalia.aon.payroll.GeozoneIrpfDescendant;
import com.esferalia.aon.payroll.GeozoneIrpfHandicap;
import com.esferalia.aon.payroll.LeaveBatch;
import com.esferalia.aon.payroll.LeaveBatchDetail;
import com.esferalia.aon.payroll.IrpfData;
import com.esferalia.aon.payroll.IrpfDataAscendants;
import com.esferalia.aon.payroll.IrpfDataDescendients;
import com.esferalia.aon.payroll.PaymentConcept;
import com.esferalia.aon.payroll.PayrollWorkPlace;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBonus;
import com.esferalia.aon.payroll.SalaryCost;
import com.esferalia.aon.payroll.SalaryDeduction;
import com.esferalia.aon.payroll.SalaryEmbargo;
import com.esferalia.aon.payroll.SalaryPayment;
import com.esferalia.aon.payroll.SystemData;
import com.esferalia.aon.payroll.SystemDeduction;
import com.esferalia.aon.payroll.SystemPayment;

/** 
* Interface for holding entity properties constants.
*/ 
public interface IPayrollAlias {



	/** 
	* DAOConstantsEntry for Agreement entity.
	*/ 
	DAOConstantsEntry AGREEMENT_ENTRY = DAOConstants.getDAOConstant(Agreement.class);

	/** 
	* Alias value: Agreement_calendar_id
	* Hibernate value: Agreement.calendar.id
	*/
	String  AGREEMENT_CALENDAR_ID = AGREEMENT_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Agreement_description
	* Hibernate value: Agreement.description
	*/
	String  AGREEMENT_DESCRIPTION = AGREEMENT_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Agreement_id
	* Hibernate value: Agreement.id
	*/
	String  AGREEMENT_ID = AGREEMENT_ENTRY.getAliasNames()[2];



	/** 
	* DAOConstantsEntry for AgreementExtra entity.
	*/ 
	DAOConstantsEntry AGREEMENT_EXTRA_ENTRY = DAOConstants.getDAOConstant(AgreementExtra.class);

	/** 
	* Alias value: AgreementExtra_agreementPayment_id
	* Hibernate value: AgreementExtra.agreementPayment.id
	*/
	String  AGREEMENT_EXTRA_AGREEMENT_PAYMENT_ID = AGREEMENT_EXTRA_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: AgreementExtra_agreement_id
	* Hibernate value: AgreementExtra.agreement.id
	*/
	String  AGREEMENT_EXTRA_AGREEMENT_ID = AGREEMENT_EXTRA_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: AgreementExtra_endDate
	* Hibernate value: AgreementExtra.endDate
	*/
	String  AGREEMENT_EXTRA_END_DATE = AGREEMENT_EXTRA_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: AgreementExtra_id
	* Hibernate value: AgreementExtra.id
	*/
	String  AGREEMENT_EXTRA_ID = AGREEMENT_EXTRA_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: AgreementExtra_issueDate
	* Hibernate value: AgreementExtra.issueDate
	*/
	String  AGREEMENT_EXTRA_ISSUE_DATE = AGREEMENT_EXTRA_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: AgreementExtra_startDate
	* Hibernate value: AgreementExtra.startDate
	*/
	String  AGREEMENT_EXTRA_START_DATE = AGREEMENT_EXTRA_ENTRY.getAliasNames()[5];



	/** 
	* DAOConstantsEntry for AgreementLevel entity.
	*/ 
	DAOConstantsEntry AGREEMENT_LEVEL_ENTRY = DAOConstants.getDAOConstant(AgreementLevel.class);

	/** 
	* Alias value: AgreementLevel_agreement_id
	* Hibernate value: AgreementLevel.agreement.id
	*/
	String  AGREEMENT_LEVEL_AGREEMENT_ID = AGREEMENT_LEVEL_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: AgreementLevel_description
	* Hibernate value: AgreementLevel.description
	*/
	String  AGREEMENT_LEVEL_DESCRIPTION = AGREEMENT_LEVEL_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: AgreementLevel_id
	* Hibernate value: AgreementLevel.id
	*/
	String  AGREEMENT_LEVEL_ID = AGREEMENT_LEVEL_ENTRY.getAliasNames()[2];



	/** 
	* DAOConstantsEntry for AgreementLevelCategory entity.
	*/ 
	DAOConstantsEntry AGREEMENT_LEVEL_CATEGORY_ENTRY = DAOConstants.getDAOConstant(AgreementLevelCategory.class);

	/** 
	* Alias value: AgreementLevelCategory_description
	* Hibernate value: AgreementLevelCategory.description
	*/
	String  AGREEMENT_LEVEL_CATEGORY_DESCRIPTION = AGREEMENT_LEVEL_CATEGORY_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: AgreementLevelCategory_id
	* Hibernate value: AgreementLevelCategory.id
	*/
	String  AGREEMENT_LEVEL_CATEGORY_ID = AGREEMENT_LEVEL_CATEGORY_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: AgreementLevelCategory_level_id
	* Hibernate value: AgreementLevelCategory.level.id
	*/
	String  AGREEMENT_LEVEL_CATEGORY_LEVEL_ID = AGREEMENT_LEVEL_CATEGORY_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: AgreementLevelCategory_level_agreement_id
	* Hibernate value: AgreementLevelCategory.level.agreement.id
	*/
	String  AGREEMENT_LEVEL_CATEGORY_LEVEL_AGREEMENT_ID = AGREEMENT_LEVEL_CATEGORY_ENTRY.getAliasNames()[3];



	/** 
	* DAOConstantsEntry for AgreementLevelData entity.
	*/ 
	DAOConstantsEntry AGREEMENT_LEVEL_DATA_ENTRY = DAOConstants.getDAOConstant(AgreementLevelData.class);

	/** 
	* Alias value: AgreementLevelData_endDate
	* Hibernate value: AgreementLevelData.endDate
	*/
	String  AGREEMENT_LEVEL_DATA_END_DATE = AGREEMENT_LEVEL_DATA_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: AgreementLevelData_expression
	* Hibernate value: AgreementLevelData.expression
	*/
	String  AGREEMENT_LEVEL_DATA_EXPRESSION = AGREEMENT_LEVEL_DATA_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: AgreementLevelData_id
	* Hibernate value: AgreementLevelData.id
	*/
	String  AGREEMENT_LEVEL_DATA_ID = AGREEMENT_LEVEL_DATA_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: AgreementLevelData_level_id
	* Hibernate value: AgreementLevelData.level.id
	*/
	String  AGREEMENT_LEVEL_DATA_LEVEL_ID = AGREEMENT_LEVEL_DATA_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: AgreementLevelData_name
	* Hibernate value: AgreementLevelData.name
	*/
	String  AGREEMENT_LEVEL_DATA_NAME = AGREEMENT_LEVEL_DATA_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: AgreementLevelData_startDate
	* Hibernate value: AgreementLevelData.startDate
	*/
	String  AGREEMENT_LEVEL_DATA_START_DATE = AGREEMENT_LEVEL_DATA_ENTRY.getAliasNames()[5];



	/** 
	* DAOConstantsEntry for AgreementPayment entity.
	*/ 
	DAOConstantsEntry AGREEMENT_PAYMENT_ENTRY = DAOConstants.getDAOConstant(AgreementPayment.class);

	/** 
	* Alias value: AgreementPayment_agreement_id
	* Hibernate value: AgreementPayment.agreement.id
	*/
	String  AGREEMENT_PAYMENT_AGREEMENT_ID = AGREEMENT_PAYMENT_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: AgreementPayment_description
	* Hibernate value: AgreementPayment.description
	*/
	String  AGREEMENT_PAYMENT_DESCRIPTION = AGREEMENT_PAYMENT_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: AgreementPayment_descriptionDecorable
	* Hibernate value: AgreementPayment.descriptionDecorable
	*/
	String  AGREEMENT_PAYMENT_DESCRIPTION_DECORABLE = AGREEMENT_PAYMENT_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: AgreementPayment_endDate
	* Hibernate value: AgreementPayment.endDate
	*/
	String  AGREEMENT_PAYMENT_END_DATE = AGREEMENT_PAYMENT_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: AgreementPayment_expression
	* Hibernate value: AgreementPayment.expression
	*/
	String  AGREEMENT_PAYMENT_EXPRESSION = AGREEMENT_PAYMENT_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: AgreementPayment_id
	* Hibernate value: AgreementPayment.id
	*/
	String  AGREEMENT_PAYMENT_ID = AGREEMENT_PAYMENT_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: AgreementPayment_irpfExpression
	* Hibernate value: AgreementPayment.irpfExpression
	*/
	String  AGREEMENT_PAYMENT_IRPF_EXPRESSION = AGREEMENT_PAYMENT_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: AgreementPayment_month
	* Hibernate value: AgreementPayment.month
	*/
	String  AGREEMENT_PAYMENT_MONTH = AGREEMENT_PAYMENT_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: AgreementPayment_paymentConcept_id
	* Hibernate value: AgreementPayment.paymentConcept.id
	*/
	String  AGREEMENT_PAYMENT_PAYMENT_CONCEPT_ID = AGREEMENT_PAYMENT_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: AgreementPayment_quoteExpression
	* Hibernate value: AgreementPayment.quoteExpression
	*/
	String  AGREEMENT_PAYMENT_QUOTE_EXPRESSION = AGREEMENT_PAYMENT_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: AgreementPayment_salaryType
	* Hibernate value: AgreementPayment.salaryType
	*/
	String  AGREEMENT_PAYMENT_SALARY_TYPE = AGREEMENT_PAYMENT_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: AgreementPayment_startDate
	* Hibernate value: AgreementPayment.startDate
	*/
	String  AGREEMENT_PAYMENT_START_DATE = AGREEMENT_PAYMENT_ENTRY.getAliasNames()[11];

	/** 
	* Alias value: AgreementPayment_type
	* Hibernate value: AgreementPayment.type
	*/
	String  AGREEMENT_PAYMENT_TYPE = AGREEMENT_PAYMENT_ENTRY.getAliasNames()[12];



	/** 
	* DAOConstantsEntry for BonusConcept entity.
	*/ 
	DAOConstantsEntry BONUS_CONCEPT_ENTRY = DAOConstants.getDAOConstant(BonusConcept.class);

	/** 
	* Alias value: BonusConcept_description
	* Hibernate value: BonusConcept.description
	*/
	String  BONUS_CONCEPT_DESCRIPTION = BONUS_CONCEPT_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: BonusConcept_expression
	* Hibernate value: BonusConcept.expression
	*/
	String  BONUS_CONCEPT_EXPRESSION = BONUS_CONCEPT_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: BonusConcept_id
	* Hibernate value: BonusConcept.id
	*/
	String  BONUS_CONCEPT_ID = BONUS_CONCEPT_ENTRY.getAliasNames()[2];



	/** 
	* DAOConstantsEntry for Certifica2Batch entity.
	*/ 
	DAOConstantsEntry CERTIFICA2BATCH_ENTRY = DAOConstants.getDAOConstant(Certifica2Batch.class);

	/** 
	* Alias value: Certifica2Batch_date
	* Hibernate value: Certifica2Batch.date
	*/
	String  CERTIFICA2BATCH_DATE = CERTIFICA2BATCH_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Certifica2Batch_enterprise_id
	* Hibernate value: Certifica2Batch.enterprise.id
	*/
	String  CERTIFICA2BATCH_ENTERPRISE_ID = CERTIFICA2BATCH_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Certifica2Batch_enterprise_registry_name
	* Hibernate value: Certifica2Batch.enterprise.registry.name
	*/
	String  CERTIFICA2BATCH_ENTERPRISE_REGISTRY_NAME = CERTIFICA2BATCH_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Certifica2Batch_id
	* Hibernate value: Certifica2Batch.id
	*/
	String  CERTIFICA2BATCH_ID = CERTIFICA2BATCH_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Certifica2Batch_sign
	* Hibernate value: Certifica2Batch.sign
	*/
	String  CERTIFICA2BATCH_SIGN = CERTIFICA2BATCH_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Certifica2Batch_status
	* Hibernate value: Certifica2Batch.status
	*/
	String  CERTIFICA2BATCH_STATUS = CERTIFICA2BATCH_ENTRY.getAliasNames()[5];



	/** 
	* DAOConstantsEntry for Certifica2BatchData entity.
	*/ 
	DAOConstantsEntry CERTIFICA2BATCH_DATA_ENTRY = DAOConstants.getDAOConstant(Certifica2BatchData.class);

	/** 
	* Alias value: Certifica2BatchData_certifica2BatchDetail_id
	* Hibernate value: Certifica2BatchData.certifica2BatchDetail.id
	*/
	String  CERTIFICA2BATCH_DATA_CERTIFICA2BATCH_DETAIL_ID = CERTIFICA2BATCH_DATA_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Certifica2BatchData_cgcContributionBase
	* Hibernate value: Certifica2BatchData.cgcContributionBase
	*/
	String  CERTIFICA2BATCH_DATA_CGC_CONTRIBUTION_BASE = CERTIFICA2BATCH_DATA_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Certifica2BatchData_comments
	* Hibernate value: Certifica2BatchData.comments
	*/
	String  CERTIFICA2BATCH_DATA_COMMENTS = CERTIFICA2BATCH_DATA_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Certifica2BatchData_contributionDays
	* Hibernate value: Certifica2BatchData.contributionDays
	*/
	String  CERTIFICA2BATCH_DATA_CONTRIBUTION_DAYS = CERTIFICA2BATCH_DATA_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Certifica2BatchData_id
	* Hibernate value: Certifica2BatchData.id
	*/
	String  CERTIFICA2BATCH_DATA_ID = CERTIFICA2BATCH_DATA_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Certifica2BatchData_month
	* Hibernate value: Certifica2BatchData.month
	*/
	String  CERTIFICA2BATCH_DATA_MONTH = CERTIFICA2BATCH_DATA_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Certifica2BatchData_unemploymentContributionBase
	* Hibernate value: Certifica2BatchData.unemploymentContributionBase
	*/
	String  CERTIFICA2BATCH_DATA_UNEMPLOYMENT_CONTRIBUTION_BASE = CERTIFICA2BATCH_DATA_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Certifica2BatchData_year
	* Hibernate value: Certifica2BatchData.year
	*/
	String  CERTIFICA2BATCH_DATA_YEAR = CERTIFICA2BATCH_DATA_ENTRY.getAliasNames()[7];



	/** 
	* DAOConstantsEntry for Certifica2BatchDetail entity.
	*/ 
	DAOConstantsEntry CERTIFICA2BATCH_DETAIL_ENTRY = DAOConstants.getDAOConstant(Certifica2BatchDetail.class);

	/** 
	* Alias value: Certifica2BatchDetail_ccc
	* Hibernate value: Certifica2BatchDetail.ccc
	*/
	String  CERTIFICA2BATCH_DETAIL_CCC = CERTIFICA2BATCH_DETAIL_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Certifica2BatchDetail_certifica2Batch_id
	* Hibernate value: Certifica2BatchDetail.certifica2Batch.id
	*/
	String  CERTIFICA2BATCH_DETAIL_CERTIFICA2BATCH_ID = CERTIFICA2BATCH_DETAIL_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Certifica2BatchDetail_contractDuration
	* Hibernate value: Certifica2BatchDetail.contractDuration
	*/
	String  CERTIFICA2BATCH_DETAIL_CONTRACT_DURATION = CERTIFICA2BATCH_DETAIL_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Certifica2BatchDetail_contractDurationIndicator
	* Hibernate value: Certifica2BatchDetail.contractDurationIndicator
	*/
	String  CERTIFICA2BATCH_DETAIL_CONTRACT_DURATION_INDICATOR = CERTIFICA2BATCH_DETAIL_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Certifica2BatchDetail_contractType
	* Hibernate value: Certifica2BatchDetail.contractType
	*/
	String  CERTIFICA2BATCH_DETAIL_CONTRACT_TYPE = CERTIFICA2BATCH_DETAIL_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Certifica2BatchDetail_contract_id
	* Hibernate value: Certifica2BatchDetail.contract.id
	*/
	String  CERTIFICA2BATCH_DETAIL_CONTRACT_ID = CERTIFICA2BATCH_DETAIL_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Certifica2BatchDetail_dedicationPercent
	* Hibernate value: Certifica2BatchDetail.dedicationPercent
	*/
	String  CERTIFICA2BATCH_DETAIL_DEDICATION_PERCENT = CERTIFICA2BATCH_DETAIL_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Certifica2BatchDetail_document
	* Hibernate value: Certifica2BatchDetail.document
	*/
	String  CERTIFICA2BATCH_DETAIL_DOCUMENT = CERTIFICA2BATCH_DETAIL_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Certifica2BatchDetail_enterpriseNif
	* Hibernate value: Certifica2BatchDetail.enterpriseNif
	*/
	String  CERTIFICA2BATCH_DETAIL_ENTERPRISE_NIF = CERTIFICA2BATCH_DETAIL_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: Certifica2BatchDetail_enterpriseStartDate
	* Hibernate value: Certifica2BatchDetail.enterpriseStartDate
	*/
	String  CERTIFICA2BATCH_DETAIL_ENTERPRISE_START_DATE = CERTIFICA2BATCH_DETAIL_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: Certifica2BatchDetail_ere
	* Hibernate value: Certifica2BatchDetail.ere
	*/
	String  CERTIFICA2BATCH_DETAIL_ERE = CERTIFICA2BATCH_DETAIL_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: Certifica2BatchDetail_ereReductionPercent
	* Hibernate value: Certifica2BatchDetail.ereReductionPercent
	*/
	String  CERTIFICA2BATCH_DETAIL_ERE_REDUCTION_PERCENT = CERTIFICA2BATCH_DETAIL_ENTRY.getAliasNames()[11];

	/** 
	* Alias value: Certifica2BatchDetail_expireDate
	* Hibernate value: Certifica2BatchDetail.expireDate
	*/
	String  CERTIFICA2BATCH_DETAIL_EXPIRE_DATE = CERTIFICA2BATCH_DETAIL_ENTRY.getAliasNames()[12];

	/** 
	* Alias value: Certifica2BatchDetail_expireEndDate
	* Hibernate value: Certifica2BatchDetail.expireEndDate
	*/
	String  CERTIFICA2BATCH_DETAIL_EXPIRE_END_DATE = CERTIFICA2BATCH_DETAIL_ENTRY.getAliasNames()[13];

	/** 
	* Alias value: Certifica2BatchDetail_firstSurname
	* Hibernate value: Certifica2BatchDetail.firstSurname
	*/
	String  CERTIFICA2BATCH_DETAIL_FIRST_SURNAME = CERTIFICA2BATCH_DETAIL_ENTRY.getAliasNames()[14];

	/** 
	* Alias value: Certifica2BatchDetail_id
	* Hibernate value: Certifica2BatchDetail.id
	*/
	String  CERTIFICA2BATCH_DETAIL_ID = CERTIFICA2BATCH_DETAIL_ENTRY.getAliasNames()[15];

	/** 
	* Alias value: Certifica2BatchDetail_name
	* Hibernate value: Certifica2BatchDetail.name
	*/
	String  CERTIFICA2BATCH_DETAIL_NAME = CERTIFICA2BATCH_DETAIL_ENTRY.getAliasNames()[16];

	/** 
	* Alias value: Certifica2BatchDetail_occupationCode
	* Hibernate value: Certifica2BatchDetail.occupationCode
	*/
	String  CERTIFICA2BATCH_DETAIL_OCCUPATION_CODE = CERTIFICA2BATCH_DETAIL_ENTRY.getAliasNames()[17];

	/** 
	* Alias value: Certifica2BatchDetail_otherReductionPercent
	* Hibernate value: Certifica2BatchDetail.otherReductionPercent
	*/
	String  CERTIFICA2BATCH_DETAIL_OTHER_REDUCTION_PERCENT = CERTIFICA2BATCH_DETAIL_ENTRY.getAliasNames()[18];

	/** 
	* Alias value: Certifica2BatchDetail_publicAssociationCharge
	* Hibernate value: Certifica2BatchDetail.publicAssociationCharge
	*/
	String  CERTIFICA2BATCH_DETAIL_PUBLIC_ASSOCIATION_CHARGE = CERTIFICA2BATCH_DETAIL_ENTRY.getAliasNames()[19];

	/** 
	* Alias value: Certifica2BatchDetail_quoteGroup
	* Hibernate value: Certifica2BatchDetail.quoteGroup
	*/
	String  CERTIFICA2BATCH_DETAIL_QUOTE_GROUP = CERTIFICA2BATCH_DETAIL_ENTRY.getAliasNames()[20];

	/** 
	* Alias value: Certifica2BatchDetail_reductionCauseCode
	* Hibernate value: Certifica2BatchDetail.reductionCauseCode
	*/
	String  CERTIFICA2BATCH_DETAIL_REDUCTION_CAUSE_CODE = CERTIFICA2BATCH_DETAIL_ENTRY.getAliasNames()[21];

	/** 
	* Alias value: Certifica2BatchDetail_salaryPeriodEndDate
	* Hibernate value: Certifica2BatchDetail.salaryPeriodEndDate
	*/
	String  CERTIFICA2BATCH_DETAIL_SALARY_PERIOD_END_DATE = CERTIFICA2BATCH_DETAIL_ENTRY.getAliasNames()[22];

	/** 
	* Alias value: Certifica2BatchDetail_salaryPeriodStartDate
	* Hibernate value: Certifica2BatchDetail.salaryPeriodStartDate
	*/
	String  CERTIFICA2BATCH_DETAIL_SALARY_PERIOD_START_DATE = CERTIFICA2BATCH_DETAIL_ENTRY.getAliasNames()[23];

	/** 
	* Alias value: Certifica2BatchDetail_salaryProcessingDays
	* Hibernate value: Certifica2BatchDetail.salaryProcessingDays
	*/
	String  CERTIFICA2BATCH_DETAIL_SALARY_PROCESSING_DAYS = CERTIFICA2BATCH_DETAIL_ENTRY.getAliasNames()[24];

	/** 
	* Alias value: Certifica2BatchDetail_secondSurname
	* Hibernate value: Certifica2BatchDetail.secondSurname
	*/
	String  CERTIFICA2BATCH_DETAIL_SECOND_SURNAME = CERTIFICA2BATCH_DETAIL_ENTRY.getAliasNames()[25];

	/** 
	* Alias value: Certifica2BatchDetail_ssNumber
	* Hibernate value: Certifica2BatchDetail.ssNumber
	*/
	String  CERTIFICA2BATCH_DETAIL_SS_NUMBER = CERTIFICA2BATCH_DETAIL_ENTRY.getAliasNames()[26];

	/** 
	* Alias value: Certifica2BatchDetail_suspensionCause
	* Hibernate value: Certifica2BatchDetail.suspensionCause
	*/
	String  CERTIFICA2BATCH_DETAIL_SUSPENSION_CAUSE = CERTIFICA2BATCH_DETAIL_ENTRY.getAliasNames()[27];



	/** 
	* DAOConstantsEntry for CNO entity.
	*/ 
	DAOConstantsEntry CNO_ENTRY = DAOConstants.getDAOConstant(CNO.class);

	/** 
	* Alias value: CNO_code
	* Hibernate value: CNO.code
	*/
	String  CNO_CODE = CNO_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: CNO_id
	* Hibernate value: CNO.id
	*/
	String  CNO_ID = CNO_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: CNO_title
	* Hibernate value: CNO.title
	*/
	String  CNO_TITLE = CNO_ENTRY.getAliasNames()[2];



	/** 
	* DAOConstantsEntry for Contract entity.
	*/ 
	DAOConstantsEntry CONTRACT_ENTRY = DAOConstants.getDAOConstant(Contract.class);

	/** 
	* Alias value: Contract_ccc_id
	* Hibernate value: Contract.ccc.id
	*/
	String  CONTRACT_CCC_ID = CONTRACT_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Contract_contractType_id
	* Hibernate value: Contract.contractType.id
	*/
	String  CONTRACT_CONTRACT_TYPE_ID = CONTRACT_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Contract_endDate
	* Hibernate value: Contract.endDate
	*/
	String  CONTRACT_END_DATE = CONTRACT_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Contract_id
	* Hibernate value: Contract.id
	*/
	String  CONTRACT_ID = CONTRACT_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Contract_person_id
	* Hibernate value: Contract.person.id
	*/
	String  CONTRACT_PERSON_ID = CONTRACT_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Contract_person_registry_document
	* Hibernate value: Contract.person.registry.document
	*/
	String  CONTRACT_PERSON_REGISTRY_DOCUMENT = CONTRACT_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Contract_person_registry_name
	* Hibernate value: Contract.person.registry.name
	*/
	String  CONTRACT_PERSON_REGISTRY_NAME = CONTRACT_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Contract_person_firstSurname
	* Hibernate value: Contract.person.firstSurname
	*/
	String  CONTRACT_PERSON_FIRST_SURNAME = CONTRACT_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Contract_person_secondSurname
	* Hibernate value: Contract.person.secondSurname
	*/
	String  CONTRACT_PERSON_SECOND_SURNAME = CONTRACT_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: Contract_startDate
	* Hibernate value: Contract.startDate
	*/
	String  CONTRACT_START_DATE = CONTRACT_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: Contract_status
	* Hibernate value: Contract.status
	*/
	String  CONTRACT_STATUS = CONTRACT_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: Contract_workPlace_id
	* Hibernate value: Contract.workPlace.id
	*/
	String  CONTRACT_WORK_PLACE_ID = CONTRACT_ENTRY.getAliasNames()[11];

	/** 
	* Alias value: Contract_workPlace_enterprise_id
	* Hibernate value: Contract.workPlace.enterprise.id
	*/
	String  CONTRACT_WORK_PLACE_ENTERPRISE_ID = CONTRACT_ENTRY.getAliasNames()[12];

	/** 
	* Alias value: Contract_workPlace_enterprise_registry_name
	* Hibernate value: Contract.workPlace.enterprise.registry.name
	*/
	String  CONTRACT_WORK_PLACE_ENTERPRISE_REGISTRY_NAME = CONTRACT_ENTRY.getAliasNames()[13];

	/** 
	* Alias value: Contract_registration
	* Hibernate value: Contract.registration
	*/
	String  CONTRACT_REGISTRATION = CONTRACT_ENTRY.getAliasNames()[14];

	/** 
	* Alias value: Contract_seniorityDate
	* Hibernate value: Contract.seniorityDate
	*/
	String  CONTRACT_SENIORITY_DATE = CONTRACT_ENTRY.getAliasNames()[15];



	/** 
	* DAOConstantsEntry for ContractAttachment entity.
	*/ 
	DAOConstantsEntry CONTRACT_ATTACHMENT_ENTRY = DAOConstants.getDAOConstant(ContractAttachment.class);

	/** 
	* Alias value: ContractAttachment_attachDate
	* Hibernate value: ContractAttachment.attachDate
	*/
	String  CONTRACT_ATTACHMENT_ATTACH_DATE = CONTRACT_ATTACHMENT_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: ContractAttachment_attachmentType
	* Hibernate value: ContractAttachment.attachmentType
	*/
	String  CONTRACT_ATTACHMENT_ATTACHMENT_TYPE = CONTRACT_ATTACHMENT_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: ContractAttachment_contract_id
	* Hibernate value: ContractAttachment.contract.id
	*/
	String  CONTRACT_ATTACHMENT_CONTRACT_ID = CONTRACT_ATTACHMENT_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: ContractAttachment_data
	* Hibernate value: ContractAttachment.data
	*/
	String  CONTRACT_ATTACHMENT_DATA = CONTRACT_ATTACHMENT_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: ContractAttachment_description
	* Hibernate value: ContractAttachment.description
	*/
	String  CONTRACT_ATTACHMENT_DESCRIPTION = CONTRACT_ATTACHMENT_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: ContractAttachment_id
	* Hibernate value: ContractAttachment.id
	*/
	String  CONTRACT_ATTACHMENT_ID = CONTRACT_ATTACHMENT_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: ContractAttachment_mimeType
	* Hibernate value: ContractAttachment.mimeType
	*/
	String  CONTRACT_ATTACHMENT_MIME_TYPE = CONTRACT_ATTACHMENT_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: ContractAttachment_scope_id
	* Hibernate value: ContractAttachment.scope.id
	*/
	String  CONTRACT_ATTACHMENT_SCOPE_ID = CONTRACT_ATTACHMENT_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: ContractAttachment_securityLevel
	* Hibernate value: ContractAttachment.securityLevel
	*/
	String  CONTRACT_ATTACHMENT_SECURITY_LEVEL = CONTRACT_ATTACHMENT_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: ContractAttachment_size
	* Hibernate value: ContractAttachment.size
	*/
	String  CONTRACT_ATTACHMENT_SIZE = CONTRACT_ATTACHMENT_ENTRY.getAliasNames()[9];



	/** 
	* DAOConstantsEntry for ContractBatch entity.
	*/ 
	DAOConstantsEntry CONTRACT_BATCH_ENTRY = DAOConstants.getDAOConstant(ContractBatch.class);

	/** 
	* Alias value: ContractBatch_date
	* Hibernate value: ContractBatch.date
	*/
	String  CONTRACT_BATCH_DATE = CONTRACT_BATCH_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: ContractBatch_id
	* Hibernate value: ContractBatch.id
	*/
	String  CONTRACT_BATCH_ID = CONTRACT_BATCH_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: ContractBatch_redNotifyDate
	* Hibernate value: ContractBatch.redNotifyDate
	*/
	String  CONTRACT_BATCH_RED_NOTIFY_DATE = CONTRACT_BATCH_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: ContractBatch_redNotifyId
	* Hibernate value: ContractBatch.redNotifyId
	*/
	String  CONTRACT_BATCH_RED_NOTIFY_ID = CONTRACT_BATCH_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: ContractBatch_redResponseDate
	* Hibernate value: ContractBatch.redResponseDate
	*/
	String  CONTRACT_BATCH_RED_RESPONSE_DATE = CONTRACT_BATCH_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: ContractBatch_redResponseId
	* Hibernate value: ContractBatch.redResponseId
	*/
	String  CONTRACT_BATCH_RED_RESPONSE_ID = CONTRACT_BATCH_ENTRY.getAliasNames()[5];



	/** 
	* DAOConstantsEntry for ContractBatchDetail entity.
	*/ 
	DAOConstantsEntry CONTRACT_BATCH_DETAIL_ENTRY = DAOConstants.getDAOConstant(ContractBatchDetail.class);

	/** 
	* Alias value: ContractBatchDetail_contractBatch_id
	* Hibernate value: ContractBatchDetail.contractBatch.id
	*/
	String  CONTRACT_BATCH_DETAIL_CONTRACT_BATCH_ID = CONTRACT_BATCH_DETAIL_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: ContractBatchDetail_contract_id
	* Hibernate value: ContractBatchDetail.contract.id
	*/
	String  CONTRACT_BATCH_DETAIL_CONTRACT_ID = CONTRACT_BATCH_DETAIL_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: ContractBatchDetail_id
	* Hibernate value: ContractBatchDetail.id
	*/
	String  CONTRACT_BATCH_DETAIL_ID = CONTRACT_BATCH_DETAIL_ENTRY.getAliasNames()[2];



	/** 
	* DAOConstantsEntry for ContractBonus entity.
	*/ 
	DAOConstantsEntry CONTRACT_BONUS_ENTRY = DAOConstants.getDAOConstant(ContractBonus.class);

	/** 
	* Alias value: ContractBonus_bonusConcept_id
	* Hibernate value: ContractBonus.bonusConcept.id
	*/
	String  CONTRACT_BONUS_BONUS_CONCEPT_ID = CONTRACT_BONUS_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: ContractBonus_contract_id
	* Hibernate value: ContractBonus.contract.id
	*/
	String  CONTRACT_BONUS_CONTRACT_ID = CONTRACT_BONUS_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: ContractBonus_description
	* Hibernate value: ContractBonus.description
	*/
	String  CONTRACT_BONUS_DESCRIPTION = CONTRACT_BONUS_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: ContractBonus_endDate
	* Hibernate value: ContractBonus.endDate
	*/
	String  CONTRACT_BONUS_END_DATE = CONTRACT_BONUS_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: ContractBonus_expression
	* Hibernate value: ContractBonus.expression
	*/
	String  CONTRACT_BONUS_EXPRESSION = CONTRACT_BONUS_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: ContractBonus_id
	* Hibernate value: ContractBonus.id
	*/
	String  CONTRACT_BONUS_ID = CONTRACT_BONUS_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: ContractBonus_startDate
	* Hibernate value: ContractBonus.startDate
	*/
	String  CONTRACT_BONUS_START_DATE = CONTRACT_BONUS_ENTRY.getAliasNames()[6];



	/** 
	* DAOConstantsEntry for ContractCalendarEvent entity.
	*/ 
	DAOConstantsEntry CONTRACT_CALENDAR_EVENT_ENTRY = DAOConstants.getDAOConstant(ContractCalendarEvent.class);

	/** 
	* Alias value: ContractCalendarEvent_contract_id
	* Hibernate value: ContractCalendarEvent.contract.id
	*/
	String  CONTRACT_CALENDAR_EVENT_CONTRACT_ID = CONTRACT_CALENDAR_EVENT_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: ContractCalendarEvent_date
	* Hibernate value: ContractCalendarEvent.date
	*/
	String  CONTRACT_CALENDAR_EVENT_DATE = CONTRACT_CALENDAR_EVENT_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: ContractCalendarEvent_duration
	* Hibernate value: ContractCalendarEvent.duration
	*/
	String  CONTRACT_CALENDAR_EVENT_DURATION = CONTRACT_CALENDAR_EVENT_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: ContractCalendarEvent_id
	* Hibernate value: ContractCalendarEvent.id
	*/
	String  CONTRACT_CALENDAR_EVENT_ID = CONTRACT_CALENDAR_EVENT_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: ContractCalendarEvent_type
	* Hibernate value: ContractCalendarEvent.type
	*/
	String  CONTRACT_CALENDAR_EVENT_TYPE = CONTRACT_CALENDAR_EVENT_ENTRY.getAliasNames()[4];



	/** 
	* DAOConstantsEntry for ContractData entity.
	*/ 
	DAOConstantsEntry CONTRACT_DATA_ENTRY = DAOConstants.getDAOConstant(ContractData.class);

	/** 
	* Alias value: ContractData_contract_id
	* Hibernate value: ContractData.contract.id
	*/
	String  CONTRACT_DATA_CONTRACT_ID = CONTRACT_DATA_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: ContractData_endDate
	* Hibernate value: ContractData.endDate
	*/
	String  CONTRACT_DATA_END_DATE = CONTRACT_DATA_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: ContractData_expression
	* Hibernate value: ContractData.expression
	*/
	String  CONTRACT_DATA_EXPRESSION = CONTRACT_DATA_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: ContractData_id
	* Hibernate value: ContractData.id
	*/
	String  CONTRACT_DATA_ID = CONTRACT_DATA_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: ContractData_name
	* Hibernate value: ContractData.name
	*/
	String  CONTRACT_DATA_NAME = CONTRACT_DATA_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: ContractData_startDate
	* Hibernate value: ContractData.startDate
	*/
	String  CONTRACT_DATA_START_DATE = CONTRACT_DATA_ENTRY.getAliasNames()[5];



	/** 
	* DAOConstantsEntry for ContractDeduction entity.
	*/ 
	DAOConstantsEntry CONTRACT_DEDUCTION_ENTRY = DAOConstants.getDAOConstant(ContractDeduction.class);

	/** 
	* Alias value: ContractDeduction_contract_id
	* Hibernate value: ContractDeduction.contract.id
	*/
	String  CONTRACT_DEDUCTION_CONTRACT_ID = CONTRACT_DEDUCTION_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: ContractDeduction_deductionConcept_id
	* Hibernate value: ContractDeduction.deductionConcept.id
	*/
	String  CONTRACT_DEDUCTION_DEDUCTION_CONCEPT_ID = CONTRACT_DEDUCTION_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: ContractDeduction_description
	* Hibernate value: ContractDeduction.description
	*/
	String  CONTRACT_DEDUCTION_DESCRIPTION = CONTRACT_DEDUCTION_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: ContractDeduction_descriptionDecorable
	* Hibernate value: ContractDeduction.descriptionDecorable
	*/
	String  CONTRACT_DEDUCTION_DESCRIPTION_DECORABLE = CONTRACT_DEDUCTION_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: ContractDeduction_endDate
	* Hibernate value: ContractDeduction.endDate
	*/
	String  CONTRACT_DEDUCTION_END_DATE = CONTRACT_DEDUCTION_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: ContractDeduction_expression
	* Hibernate value: ContractDeduction.expression
	*/
	String  CONTRACT_DEDUCTION_EXPRESSION = CONTRACT_DEDUCTION_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: ContractDeduction_id
	* Hibernate value: ContractDeduction.id
	*/
	String  CONTRACT_DEDUCTION_ID = CONTRACT_DEDUCTION_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: ContractDeduction_month
	* Hibernate value: ContractDeduction.month
	*/
	String  CONTRACT_DEDUCTION_MONTH = CONTRACT_DEDUCTION_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: ContractDeduction_startDate
	* Hibernate value: ContractDeduction.startDate
	*/
	String  CONTRACT_DEDUCTION_START_DATE = CONTRACT_DEDUCTION_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: ContractDeduction_type
	* Hibernate value: ContractDeduction.type
	*/
	String  CONTRACT_DEDUCTION_TYPE = CONTRACT_DEDUCTION_ENTRY.getAliasNames()[9];



	/** 
	* DAOConstantsEntry for ContractEmbargo entity.
	*/ 
	DAOConstantsEntry CONTRACT_EMBARGO_ENTRY = DAOConstants.getDAOConstant(ContractEmbargo.class);

	/** 
	* Alias value: ContractEmbargo_amount
	* Hibernate value: ContractEmbargo.amount
	*/
	String  CONTRACT_EMBARGO_AMOUNT = CONTRACT_EMBARGO_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: ContractEmbargo_contract_id
	* Hibernate value: ContractEmbargo.contract.id
	*/
	String  CONTRACT_EMBARGO_CONTRACT_ID = CONTRACT_EMBARGO_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: ContractEmbargo_description
	* Hibernate value: ContractEmbargo.description
	*/
	String  CONTRACT_EMBARGO_DESCRIPTION = CONTRACT_EMBARGO_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: ContractEmbargo_endDate
	* Hibernate value: ContractEmbargo.endDate
	*/
	String  CONTRACT_EMBARGO_END_DATE = CONTRACT_EMBARGO_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: ContractEmbargo_expression
	* Hibernate value: ContractEmbargo.expression
	*/
	String  CONTRACT_EMBARGO_EXPRESSION = CONTRACT_EMBARGO_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: ContractEmbargo_id
	* Hibernate value: ContractEmbargo.id
	*/
	String  CONTRACT_EMBARGO_ID = CONTRACT_EMBARGO_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: ContractEmbargo_startDate
	* Hibernate value: ContractEmbargo.startDate
	*/
	String  CONTRACT_EMBARGO_START_DATE = CONTRACT_EMBARGO_ENTRY.getAliasNames()[6];



	/** 
	* DAOConstantsEntry for ContractLeave entity.
	*/ 
	DAOConstantsEntry CONTRACT_LEAVE_ENTRY = DAOConstants.getDAOConstant(ContractLeave.class);

	/** 
	* Alias value: ContractLeave_contract_id
	* Hibernate value: ContractLeave.contract.id
	*/
	String  CONTRACT_LEAVE_CONTRACT_ID = CONTRACT_LEAVE_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: ContractLeave_dailyCgcBase
	* Hibernate value: ContractLeave.dailyCgcBase
	*/
	String  CONTRACT_LEAVE_DAILY_CGC_BASE = CONTRACT_LEAVE_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: ContractLeave_dailyCgpBase
	* Hibernate value: ContractLeave.dailyCgpBase
	*/
	String  CONTRACT_LEAVE_DAILY_CGP_BASE = CONTRACT_LEAVE_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: ContractLeave_dailyRegBase
	* Hibernate value: ContractLeave.dailyRegBase
	*/
	String  CONTRACT_LEAVE_DAILY_REG_BASE = CONTRACT_LEAVE_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: ContractLeave_description
	* Hibernate value: ContractLeave.description
	*/
	String  CONTRACT_LEAVE_DESCRIPTION = CONTRACT_LEAVE_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: ContractLeave_endDate
	* Hibernate value: ContractLeave.endDate
	*/
	String  CONTRACT_LEAVE_END_DATE = CONTRACT_LEAVE_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: ContractLeave_id
	* Hibernate value: ContractLeave.id
	*/
	String  CONTRACT_LEAVE_ID = CONTRACT_LEAVE_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: ContractLeave_parent_id
	* Hibernate value: ContractLeave.parent.id
	*/
	String  CONTRACT_LEAVE_PARENT_ID = CONTRACT_LEAVE_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: ContractLeave_startDate
	* Hibernate value: ContractLeave.startDate
	*/
	String  CONTRACT_LEAVE_START_DATE = CONTRACT_LEAVE_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: ContractLeave_type
	* Hibernate value: ContractLeave.type
	*/
	String  CONTRACT_LEAVE_TYPE = CONTRACT_LEAVE_ENTRY.getAliasNames()[9];



	/** 
	* DAOConstantsEntry for ContractLeaveDetail entity.
	*/ 
	DAOConstantsEntry CONTRACT_LEAVE_DETAIL_ENTRY = DAOConstants.getDAOConstant(ContractLeaveDetail.class);

	/** 
	* Alias value: ContractLeaveDetail_cias
	* Hibernate value: ContractLeaveDetail.cias
	*/
	String  CONTRACT_LEAVE_DETAIL_CIAS = CONTRACT_LEAVE_DETAIL_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: ContractLeaveDetail_collegeNumber
	* Hibernate value: ContractLeaveDetail.collegeNumber
	*/
	String  CONTRACT_LEAVE_DETAIL_COLLEGE_NUMBER = CONTRACT_LEAVE_DETAIL_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: ContractLeaveDetail_confirmOrder
	* Hibernate value: ContractLeaveDetail.confirmOrder
	*/
	String  CONTRACT_LEAVE_DETAIL_CONFIRM_ORDER = CONTRACT_LEAVE_DETAIL_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: ContractLeaveDetail_contractLeave_id
	* Hibernate value: ContractLeaveDetail.contractLeave.id
	*/
	String  CONTRACT_LEAVE_DETAIL_CONTRACT_LEAVE_ID = CONTRACT_LEAVE_DETAIL_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: ContractLeaveDetail_date
	* Hibernate value: ContractLeaveDetail.date
	*/
	String  CONTRACT_LEAVE_DETAIL_DATE = CONTRACT_LEAVE_DETAIL_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: ContractLeaveDetail_id
	* Hibernate value: ContractLeaveDetail.id
	*/
	String  CONTRACT_LEAVE_DETAIL_ID = CONTRACT_LEAVE_DETAIL_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: ContractLeaveDetail_processed
	* Hibernate value: ContractLeaveDetail.processed
	*/
	String  CONTRACT_LEAVE_DETAIL_PROCESSED = CONTRACT_LEAVE_DETAIL_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: ContractLeaveDetail_type
	* Hibernate value: ContractLeaveDetail.type
	*/
	String  CONTRACT_LEAVE_DETAIL_TYPE = CONTRACT_LEAVE_DETAIL_ENTRY.getAliasNames()[7];



	/** 
	* DAOConstantsEntry for ContractPayment entity.
	*/ 
	DAOConstantsEntry CONTRACT_PAYMENT_ENTRY = DAOConstants.getDAOConstant(ContractPayment.class);

	/** 
	* Alias value: ContractPayment_contract_id
	* Hibernate value: ContractPayment.contract.id
	*/
	String  CONTRACT_PAYMENT_CONTRACT_ID = CONTRACT_PAYMENT_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: ContractPayment_description
	* Hibernate value: ContractPayment.description
	*/
	String  CONTRACT_PAYMENT_DESCRIPTION = CONTRACT_PAYMENT_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: ContractPayment_descriptionDecorable
	* Hibernate value: ContractPayment.descriptionDecorable
	*/
	String  CONTRACT_PAYMENT_DESCRIPTION_DECORABLE = CONTRACT_PAYMENT_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: ContractPayment_endDate
	* Hibernate value: ContractPayment.endDate
	*/
	String  CONTRACT_PAYMENT_END_DATE = CONTRACT_PAYMENT_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: ContractPayment_expression
	* Hibernate value: ContractPayment.expression
	*/
	String  CONTRACT_PAYMENT_EXPRESSION = CONTRACT_PAYMENT_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: ContractPayment_id
	* Hibernate value: ContractPayment.id
	*/
	String  CONTRACT_PAYMENT_ID = CONTRACT_PAYMENT_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: ContractPayment_irpfExpression
	* Hibernate value: ContractPayment.irpfExpression
	*/
	String  CONTRACT_PAYMENT_IRPF_EXPRESSION = CONTRACT_PAYMENT_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: ContractPayment_month
	* Hibernate value: ContractPayment.month
	*/
	String  CONTRACT_PAYMENT_MONTH = CONTRACT_PAYMENT_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: ContractPayment_paymentConcept_id
	* Hibernate value: ContractPayment.paymentConcept.id
	*/
	String  CONTRACT_PAYMENT_PAYMENT_CONCEPT_ID = CONTRACT_PAYMENT_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: ContractPayment_quoteExpression
	* Hibernate value: ContractPayment.quoteExpression
	*/
	String  CONTRACT_PAYMENT_QUOTE_EXPRESSION = CONTRACT_PAYMENT_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: ContractPayment_salaryType
	* Hibernate value: ContractPayment.salaryType
	*/
	String  CONTRACT_PAYMENT_SALARY_TYPE = CONTRACT_PAYMENT_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: ContractPayment_startDate
	* Hibernate value: ContractPayment.startDate
	*/
	String  CONTRACT_PAYMENT_START_DATE = CONTRACT_PAYMENT_ENTRY.getAliasNames()[11];

	/** 
	* Alias value: ContractPayment_type
	* Hibernate value: ContractPayment.type
	*/
	String  CONTRACT_PAYMENT_TYPE = CONTRACT_PAYMENT_ENTRY.getAliasNames()[12];



	/** 
	* DAOConstantsEntry for DeductionConcept entity.
	*/ 
	DAOConstantsEntry DEDUCTION_CONCEPT_ENTRY = DAOConstants.getDAOConstant(DeductionConcept.class);

	/** 
	* Alias value: DeductionConcept_code
	* Hibernate value: DeductionConcept.code
	*/
	String  DEDUCTION_CONCEPT_CODE = DEDUCTION_CONCEPT_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: DeductionConcept_description
	* Hibernate value: DeductionConcept.description
	*/
	String  DEDUCTION_CONCEPT_DESCRIPTION = DEDUCTION_CONCEPT_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: DeductionConcept_descriptionDecorable
	* Hibernate value: DeductionConcept.descriptionDecorable
	*/
	String  DEDUCTION_CONCEPT_DESCRIPTION_DECORABLE = DEDUCTION_CONCEPT_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: DeductionConcept_expression
	* Hibernate value: DeductionConcept.expression
	*/
	String  DEDUCTION_CONCEPT_EXPRESSION = DEDUCTION_CONCEPT_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: DeductionConcept_id
	* Hibernate value: DeductionConcept.id
	*/
	String  DEDUCTION_CONCEPT_ID = DEDUCTION_CONCEPT_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: DeductionConcept_type
	* Hibernate value: DeductionConcept.type
	*/
	String  DEDUCTION_CONCEPT_TYPE = DEDUCTION_CONCEPT_ENTRY.getAliasNames()[5];



	/** 
	* DAOConstantsEntry for EnterpriseCCC entity.
	*/ 
	DAOConstantsEntry ENTERPRISE_CCC_ENTRY = DAOConstants.getDAOConstant(EnterpriseCCC.class);

	/** 
	* Alias value: EnterpriseCCC_activity_id
	* Hibernate value: EnterpriseCCC.activity.id
	*/
	String  ENTERPRISE_CCC_ACTIVITY_ID = ENTERPRISE_CCC_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: EnterpriseCCC_ccc
	* Hibernate value: EnterpriseCCC.ccc
	*/
	String  ENTERPRISE_CCC_CCC = ENTERPRISE_CCC_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: EnterpriseCCC_geozone_id
	* Hibernate value: EnterpriseCCC.geozone.id
	*/
	String  ENTERPRISE_CCC_GEOZONE_ID = ENTERPRISE_CCC_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: EnterpriseCCC_id
	* Hibernate value: EnterpriseCCC.id
	*/
	String  ENTERPRISE_CCC_ID = ENTERPRISE_CCC_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: EnterpriseCCC_type
	* Hibernate value: EnterpriseCCC.type
	*/
	String  ENTERPRISE_CCC_TYPE = ENTERPRISE_CCC_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: EnterpriseCCC_activity_enterprise_id
	* Hibernate value: EnterpriseCCC.activity.enterprise.id
	*/
	String  ENTERPRISE_CCC_ACTIVITY_ENTERPRISE_ID = ENTERPRISE_CCC_ENTRY.getAliasNames()[5];



	/** 
	* DAOConstantsEntry for EnterpriseActivity entity.
	*/ 
	DAOConstantsEntry ENTERPRISE_ACTIVITY_ENTRY = DAOConstants.getDAOConstant(EnterpriseActivity.class);

	/** 
	* Alias value: EnterpriseActivity_cnae2009_id
	* Hibernate value: EnterpriseActivity.cnae2009.id
	*/
	String  ENTERPRISE_ACTIVITY_CNAE2009_ID = ENTERPRISE_ACTIVITY_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: EnterpriseActivity_description
	* Hibernate value: EnterpriseActivity.description
	*/
	String  ENTERPRISE_ACTIVITY_DESCRIPTION = ENTERPRISE_ACTIVITY_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: EnterpriseActivity_enterprise_id
	* Hibernate value: EnterpriseActivity.enterprise.id
	*/
	String  ENTERPRISE_ACTIVITY_ENTERPRISE_ID = ENTERPRISE_ACTIVITY_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: EnterpriseActivity_id
	* Hibernate value: EnterpriseActivity.id
	*/
	String  ENTERPRISE_ACTIVITY_ID = ENTERPRISE_ACTIVITY_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: EnterpriseActivity_type
	* Hibernate value: EnterpriseActivity.type
	*/
	String  ENTERPRISE_ACTIVITY_TYPE = ENTERPRISE_ACTIVITY_ENTRY.getAliasNames()[4];



	/** 
	* DAOConstantsEntry for FanBatch entity.
	*/ 
	DAOConstantsEntry FAN_BATCH_ENTRY = DAOConstants.getDAOConstant(FanBatch.class);

	/** 
	* Alias value: FanBatch_date
	* Hibernate value: FanBatch.date
	*/
	String  FAN_BATCH_DATE = FAN_BATCH_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: FanBatch_id
	* Hibernate value: FanBatch.id
	*/
	String  FAN_BATCH_ID = FAN_BATCH_ENTRY.getAliasNames()[1];



	/** 
	* DAOConstantsEntry for FanBatchDetail entity.
	*/ 
	DAOConstantsEntry FAN_BATCH_DETAIL_ENTRY = DAOConstants.getDAOConstant(FanBatchDetail.class);

	/** 
	* Alias value: FanBatchDetail_enterprise_id
	* Hibernate value: FanBatchDetail.enterprise.id
	*/
	String  FAN_BATCH_DETAIL_ENTERPRISE_ID = FAN_BATCH_DETAIL_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: FanBatchDetail_fanBatch_id
	* Hibernate value: FanBatchDetail.fanBatch.id
	*/
	String  FAN_BATCH_DETAIL_FAN_BATCH_ID = FAN_BATCH_DETAIL_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: FanBatchDetail_id
	* Hibernate value: FanBatchDetail.id
	*/
	String  FAN_BATCH_DETAIL_ID = FAN_BATCH_DETAIL_ENTRY.getAliasNames()[2];



	/** 
	* DAOConstantsEntry for GeozoneIrpf entity.
	*/ 
	DAOConstantsEntry GEOZONE_IRPF_ENTRY = DAOConstants.getDAOConstant(GeozoneIrpf.class);

	/** 
	* Alias value: GeozoneIrpf_amount
	* Hibernate value: GeozoneIrpf.amount
	*/
	String  GEOZONE_IRPF_AMOUNT = GEOZONE_IRPF_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: GeozoneIrpf_endDate
	* Hibernate value: GeozoneIrpf.endDate
	*/
	String  GEOZONE_IRPF_END_DATE = GEOZONE_IRPF_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: GeozoneIrpf_geozone_id
	* Hibernate value: GeozoneIrpf.geozone.id
	*/
	String  GEOZONE_IRPF_GEOZONE_ID = GEOZONE_IRPF_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: GeozoneIrpf_id
	* Hibernate value: GeozoneIrpf.id
	*/
	String  GEOZONE_IRPF_ID = GEOZONE_IRPF_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: GeozoneIrpf_startDate
	* Hibernate value: GeozoneIrpf.startDate
	*/
	String  GEOZONE_IRPF_START_DATE = GEOZONE_IRPF_ENTRY.getAliasNames()[4];



	/** 
	* DAOConstantsEntry for GeozoneIrpfDescendant entity.
	*/ 
	DAOConstantsEntry GEOZONE_IRPF_DESCENDANT_ENTRY = DAOConstants.getDAOConstant(GeozoneIrpfDescendant.class);

	/** 
	* Alias value: GeozoneIrpfDescendant_descendant
	* Hibernate value: GeozoneIrpfDescendant.descendant
	*/
	String  GEOZONE_IRPF_DESCENDANT_DESCENDANT = GEOZONE_IRPF_DESCENDANT_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: GeozoneIrpfDescendant_geozoneIrpf_amount
	* Hibernate value: GeozoneIrpfDescendant.geozoneIrpf.amount
	*/
	String  GEOZONE_IRPF_DESCENDANT_GEOZONE_IRPF_AMOUNT = GEOZONE_IRPF_DESCENDANT_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: GeozoneIrpfDescendant_geozoneIrpf_id
	* Hibernate value: GeozoneIrpfDescendant.geozoneIrpf.id
	*/
	String  GEOZONE_IRPF_DESCENDANT_GEOZONE_IRPF_ID = GEOZONE_IRPF_DESCENDANT_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: GeozoneIrpfDescendant_geozoneIrpf_geozone_id
	* Hibernate value: GeozoneIrpfDescendant.geozoneIrpf.geozone.id
	*/
	String  GEOZONE_IRPF_DESCENDANT_GEOZONE_IRPF_GEOZONE_ID = GEOZONE_IRPF_DESCENDANT_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: GeozoneIrpfDescendant_id
	* Hibernate value: GeozoneIrpfDescendant.id
	*/
	String  GEOZONE_IRPF_DESCENDANT_ID = GEOZONE_IRPF_DESCENDANT_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: GeozoneIrpfDescendant_percent
	* Hibernate value: GeozoneIrpfDescendant.percent
	*/
	String  GEOZONE_IRPF_DESCENDANT_PERCENT = GEOZONE_IRPF_DESCENDANT_ENTRY.getAliasNames()[5];



	/** 
	* DAOConstantsEntry for GeozoneIrpfHandicap entity.
	*/ 
	DAOConstantsEntry GEOZONE_IRPF_HANDICAP_ENTRY = DAOConstants.getDAOConstant(GeozoneIrpfHandicap.class);

	/** 
	* Alias value: GeozoneIrpfHandicap_geozoneIrpf_amount
	* Hibernate value: GeozoneIrpfHandicap.geozoneIrpf.amount
	*/
	String  GEOZONE_IRPF_HANDICAP_GEOZONE_IRPF_AMOUNT = GEOZONE_IRPF_HANDICAP_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: GeozoneIrpfHandicap_geozoneIrpf_id
	* Hibernate value: GeozoneIrpfHandicap.geozoneIrpf.id
	*/
	String  GEOZONE_IRPF_HANDICAP_GEOZONE_IRPF_ID = GEOZONE_IRPF_HANDICAP_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: GeozoneIrpfHandicap_geozoneIrpf_geozone_id
	* Hibernate value: GeozoneIrpfHandicap.geozoneIrpf.geozone.id
	*/
	String  GEOZONE_IRPF_HANDICAP_GEOZONE_IRPF_GEOZONE_ID = GEOZONE_IRPF_HANDICAP_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: GeozoneIrpfHandicap_handicap
	* Hibernate value: GeozoneIrpfHandicap.handicap
	*/
	String  GEOZONE_IRPF_HANDICAP_HANDICAP = GEOZONE_IRPF_HANDICAP_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: GeozoneIrpfHandicap_id
	* Hibernate value: GeozoneIrpfHandicap.id
	*/
	String  GEOZONE_IRPF_HANDICAP_ID = GEOZONE_IRPF_HANDICAP_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: GeozoneIrpfHandicap_percent
	* Hibernate value: GeozoneIrpfHandicap.percent
	*/
	String  GEOZONE_IRPF_HANDICAP_PERCENT = GEOZONE_IRPF_HANDICAP_ENTRY.getAliasNames()[5];



	/** 
	* DAOConstantsEntry for LeaveBatch entity.
	*/ 
	DAOConstantsEntry LEAVE_BATCH_ENTRY = DAOConstants.getDAOConstant(LeaveBatch.class);

	/** 
	* Alias value: LeaveBatch_date
	* Hibernate value: LeaveBatch.date
	*/
	String  LEAVE_BATCH_DATE = LEAVE_BATCH_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: LeaveBatch_id
	* Hibernate value: LeaveBatch.id
	*/
	String  LEAVE_BATCH_ID = LEAVE_BATCH_ENTRY.getAliasNames()[1];



	/** 
	* DAOConstantsEntry for LeaveBatchDetail entity.
	*/ 
	DAOConstantsEntry LEAVE_BATCH_DETAIL_ENTRY = DAOConstants.getDAOConstant(LeaveBatchDetail.class);

	/** 
	* Alias value: LeaveBatchDetail_contractLeaveDetail_id
	* Hibernate value: LeaveBatchDetail.contractLeaveDetail.id
	*/
	String  LEAVE_BATCH_DETAIL_CONTRACT_LEAVE_DETAIL_ID = LEAVE_BATCH_DETAIL_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: LeaveBatchDetail_id
	* Hibernate value: LeaveBatchDetail.id
	*/
	String  LEAVE_BATCH_DETAIL_ID = LEAVE_BATCH_DETAIL_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: LeaveBatchDetail_leaveBatch_id
	* Hibernate value: LeaveBatchDetail.leaveBatch.id
	*/
	String  LEAVE_BATCH_DETAIL_LEAVE_BATCH_ID = LEAVE_BATCH_DETAIL_ENTRY.getAliasNames()[2];



	/** 
	* DAOConstantsEntry for IrpfData entity.
	*/ 
	DAOConstantsEntry IRPF_DATA_ENTRY = DAOConstants.getDAOConstant(IrpfData.class);

	/** 
	* Alias value: IrpfData_contract_id
	* Hibernate value: IrpfData.contract.id
	*/
	String  IRPF_DATA_CONTRACT_ID = IRPF_DATA_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: IrpfData_dependence
	* Hibernate value: IrpfData.dependence
	*/
	String  IRPF_DATA_DEPENDENCE = IRPF_DATA_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: IrpfData_descendientCount
	* Hibernate value: IrpfData.descendientCount
	*/
	String  IRPF_DATA_DESCENDIENT_COUNT = IRPF_DATA_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: IrpfData_disabilityLevel
	* Hibernate value: IrpfData.disabilityLevel
	*/
	String  IRPF_DATA_DISABILITY_LEVEL = IRPF_DATA_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: IrpfData_endDate
	* Hibernate value: IrpfData.endDate
	*/
	String  IRPF_DATA_END_DATE = IRPF_DATA_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: IrpfData_familySituation
	* Hibernate value: IrpfData.familySituation
	*/
	String  IRPF_DATA_FAMILY_SITUATION = IRPF_DATA_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: IrpfData_fiscalExclusion
	* Hibernate value: IrpfData.fiscalExclusion
	*/
	String  IRPF_DATA_FISCAL_EXCLUSION = IRPF_DATA_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: IrpfData_id
	* Hibernate value: IrpfData.id
	*/
	String  IRPF_DATA_ID = IRPF_DATA_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: IrpfData_labourProlongation
	* Hibernate value: IrpfData.labourProlongation
	*/
	String  IRPF_DATA_LABOUR_PROLONGATION = IRPF_DATA_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: IrpfData_movingDate
	* Hibernate value: IrpfData.movingDate
	*/
	String  IRPF_DATA_MOVING_DATE = IRPF_DATA_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: IrpfData_spouseDocument
	* Hibernate value: IrpfData.spouseDocument
	*/
	String  IRPF_DATA_SPOUSE_DOCUMENT = IRPF_DATA_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: IrpfData_startDate
	* Hibernate value: IrpfData.startDate
	*/
	String  IRPF_DATA_START_DATE = IRPF_DATA_ENTRY.getAliasNames()[11];



	/** 
	* DAOConstantsEntry for IrpfDataAscendants entity.
	*/ 
	DAOConstantsEntry IRPF_DATA_ASCENDANTS_ENTRY = DAOConstants.getDAOConstant(IrpfDataAscendants.class);

	/** 
	* Alias value: IrpfDataAscendants_anotherDescendient
	* Hibernate value: IrpfDataAscendants.anotherDescendient
	*/
	String  IRPF_DATA_ASCENDANTS_ANOTHER_DESCENDIENT = IRPF_DATA_ASCENDANTS_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: IrpfDataAscendants_birthYear
	* Hibernate value: IrpfDataAscendants.birthYear
	*/
	String  IRPF_DATA_ASCENDANTS_BIRTH_YEAR = IRPF_DATA_ASCENDANTS_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: IrpfDataAscendants_dependence
	* Hibernate value: IrpfDataAscendants.dependence
	*/
	String  IRPF_DATA_ASCENDANTS_DEPENDENCE = IRPF_DATA_ASCENDANTS_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: IrpfDataAscendants_disabilityLevel
	* Hibernate value: IrpfDataAscendants.disabilityLevel
	*/
	String  IRPF_DATA_ASCENDANTS_DISABILITY_LEVEL = IRPF_DATA_ASCENDANTS_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: IrpfDataAscendants_id
	* Hibernate value: IrpfDataAscendants.id
	*/
	String  IRPF_DATA_ASCENDANTS_ID = IRPF_DATA_ASCENDANTS_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: IrpfDataAscendants_irpfData_id
	* Hibernate value: IrpfDataAscendants.irpfData.id
	*/
	String  IRPF_DATA_ASCENDANTS_IRPF_DATA_ID = IRPF_DATA_ASCENDANTS_ENTRY.getAliasNames()[5];



	/** 
	* DAOConstantsEntry for IrpfDataDescendients entity.
	*/ 
	DAOConstantsEntry IRPF_DATA_DESCENDIENTS_ENTRY = DAOConstants.getDAOConstant(IrpfDataDescendients.class);

	/** 
	* Alias value: IrpfDataDescendients_adoptionYear
	* Hibernate value: IrpfDataDescendients.adoptionYear
	*/
	String  IRPF_DATA_DESCENDIENTS_ADOPTION_YEAR = IRPF_DATA_DESCENDIENTS_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: IrpfDataDescendients_birthYear
	* Hibernate value: IrpfDataDescendients.birthYear
	*/
	String  IRPF_DATA_DESCENDIENTS_BIRTH_YEAR = IRPF_DATA_DESCENDIENTS_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: IrpfDataDescendients_dependence
	* Hibernate value: IrpfDataDescendients.dependence
	*/
	String  IRPF_DATA_DESCENDIENTS_DEPENDENCE = IRPF_DATA_DESCENDIENTS_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: IrpfDataDescendients_disabilityLevel
	* Hibernate value: IrpfDataDescendients.disabilityLevel
	*/
	String  IRPF_DATA_DESCENDIENTS_DISABILITY_LEVEL = IRPF_DATA_DESCENDIENTS_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: IrpfDataDescendients_id
	* Hibernate value: IrpfDataDescendients.id
	*/
	String  IRPF_DATA_DESCENDIENTS_ID = IRPF_DATA_DESCENDIENTS_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: IrpfDataDescendients_irpfData_id
	* Hibernate value: IrpfDataDescendients.irpfData.id
	*/
	String  IRPF_DATA_DESCENDIENTS_IRPF_DATA_ID = IRPF_DATA_DESCENDIENTS_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: IrpfDataDescendients_uniqueParent
	* Hibernate value: IrpfDataDescendients.uniqueParent
	*/
	String  IRPF_DATA_DESCENDIENTS_UNIQUE_PARENT = IRPF_DATA_DESCENDIENTS_ENTRY.getAliasNames()[6];



	/** 
	* DAOConstantsEntry for PaymentConcept entity.
	*/ 
	DAOConstantsEntry PAYMENT_CONCEPT_ENTRY = DAOConstants.getDAOConstant(PaymentConcept.class);

	/** 
	* Alias value: PaymentConcept_code
	* Hibernate value: PaymentConcept.code
	*/
	String  PAYMENT_CONCEPT_CODE = PAYMENT_CONCEPT_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: PaymentConcept_description
	* Hibernate value: PaymentConcept.description
	*/
	String  PAYMENT_CONCEPT_DESCRIPTION = PAYMENT_CONCEPT_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: PaymentConcept_descriptionDecorable
	* Hibernate value: PaymentConcept.descriptionDecorable
	*/
	String  PAYMENT_CONCEPT_DESCRIPTION_DECORABLE = PAYMENT_CONCEPT_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: PaymentConcept_expression
	* Hibernate value: PaymentConcept.expression
	*/
	String  PAYMENT_CONCEPT_EXPRESSION = PAYMENT_CONCEPT_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: PaymentConcept_id
	* Hibernate value: PaymentConcept.id
	*/
	String  PAYMENT_CONCEPT_ID = PAYMENT_CONCEPT_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: PaymentConcept_irpfExpression
	* Hibernate value: PaymentConcept.irpfExpression
	*/
	String  PAYMENT_CONCEPT_IRPF_EXPRESSION = PAYMENT_CONCEPT_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: PaymentConcept_quoteExpression
	* Hibernate value: PaymentConcept.quoteExpression
	*/
	String  PAYMENT_CONCEPT_QUOTE_EXPRESSION = PAYMENT_CONCEPT_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: PaymentConcept_type
	* Hibernate value: PaymentConcept.type
	*/
	String  PAYMENT_CONCEPT_TYPE = PAYMENT_CONCEPT_ENTRY.getAliasNames()[7];



	/** 
	* DAOConstantsEntry for PayrollWorkPlace entity.
	*/ 
	DAOConstantsEntry PAYROLL_WORK_PLACE_ENTRY = DAOConstants.getDAOConstant(PayrollWorkPlace.class);

	/** 
	* Alias value: PayrollWorkPlace_id
	* Hibernate value: PayrollWorkPlace.id
	*/
	String  PAYROLL_WORK_PLACE_ID = PAYROLL_WORK_PLACE_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: PayrollWorkPlace_agreement_id
	* Hibernate value: PayrollWorkPlace.agreement.id
	*/
	String  PAYROLL_WORK_PLACE_AGREEMENT_ID = PAYROLL_WORK_PLACE_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: PayrollWorkPlace_calendar_id
	* Hibernate value: PayrollWorkPlace.calendar.id
	*/
	String  PAYROLL_WORK_PLACE_CALENDAR_ID = PAYROLL_WORK_PLACE_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: PayrollWorkPlace_enterpriseActivity_id
	* Hibernate value: PayrollWorkPlace.enterpriseActivity.id
	*/
	String  PAYROLL_WORK_PLACE_ENTERPRISE_ACTIVITY_ID = PAYROLL_WORK_PLACE_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: PayrollWorkPlace_workPlace_id
	* Hibernate value: PayrollWorkPlace.workPlace.id
	*/
	String  PAYROLL_WORK_PLACE_WORK_PLACE_ID = PAYROLL_WORK_PLACE_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: PayrollWorkPlace_workPlace_enterprise_id
	* Hibernate value: PayrollWorkPlace.workPlace.enterprise.id
	*/
	String  PAYROLL_WORK_PLACE_WORK_PLACE_ENTERPRISE_ID = PAYROLL_WORK_PLACE_ENTRY.getAliasNames()[5];



	/** 
	* DAOConstantsEntry for Salary entity.
	*/ 
	DAOConstantsEntry SALARY_ENTRY = DAOConstants.getDAOConstant(Salary.class);

	/** 
	* Alias value: Salary_id
	* Hibernate value: Salary.id
	*/
	String  SALARY_ID = SALARY_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Salary_address
	* Hibernate value: Salary.address
	*/
	String  SALARY_ADDRESS = SALARY_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Salary_broadcastDate
	* Hibernate value: Salary.broadcastDate
	*/
	String  SALARY_BROADCAST_DATE = SALARY_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Salary_category
	* Hibernate value: Salary.category
	*/
	String  SALARY_CATEGORY = SALARY_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Salary_commonBase
	* Hibernate value: Salary.commonBase
	*/
	String  SALARY_COMMON_BASE = SALARY_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Salary_contract_id
	* Hibernate value: Salary.contract.id
	*/
	String  SALARY_CONTRACT_ID = SALARY_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Salary_contract_person_id
	* Hibernate value: Salary.contract.person.id
	*/
	String  SALARY_CONTRACT_PERSON_ID = SALARY_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Salary_contract_workPlace_id
	* Hibernate value: Salary.contract.workPlace.id
	*/
	String  SALARY_CONTRACT_WORK_PLACE_ID = SALARY_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Salary_contract_workPlace_enterprise_id
	* Hibernate value: Salary.contract.workPlace.enterprise.id
	*/
	String  SALARY_CONTRACT_WORK_PLACE_ENTERPRISE_ID = SALARY_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: Salary_employeeName
	* Hibernate value: Salary.employeeName
	*/
	String  SALARY_EMPLOYEE_NAME = SALARY_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: Salary_endDate
	* Hibernate value: Salary.endDate
	*/
	String  SALARY_END_DATE = SALARY_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: Salary_extraPayProration
	* Hibernate value: Salary.extraPayProration
	*/
	String  SALARY_EXTRA_PAY_PRORATION = SALARY_ENTRY.getAliasNames()[11];

	/** 
	* Alias value: Salary_irpfBase
	* Hibernate value: Salary.irpfBase
	*/
	String  SALARY_IRPF_BASE = SALARY_ENTRY.getAliasNames()[12];

	/** 
	* Alias value: Salary_type
	* Hibernate value: Salary.type
	*/
	String  SALARY_TYPE = SALARY_ENTRY.getAliasNames()[13];

	/** 
	* Alias value: Salary_issueDate
	* Hibernate value: Salary.issueDate
	*/
	String  SALARY_ISSUE_DATE = SALARY_ENTRY.getAliasNames()[14];

	/** 
	* Alias value: Salary_overtimeBase
	* Hibernate value: Salary.overtimeBase
	*/
	String  SALARY_OVERTIME_BASE = SALARY_ENTRY.getAliasNames()[15];

	/** 
	* Alias value: Salary_professionalBase
	* Hibernate value: Salary.professionalBase
	*/
	String  SALARY_PROFESSIONAL_BASE = SALARY_ENTRY.getAliasNames()[16];

	/** 
	* Alias value: Salary_registration
	* Hibernate value: Salary.registration
	*/
	String  SALARY_REGISTRATION = SALARY_ENTRY.getAliasNames()[17];

	/** 
	* Alias value: Salary_remuneration
	* Hibernate value: Salary.remuneration
	*/
	String  SALARY_REMUNERATION = SALARY_ENTRY.getAliasNames()[18];

	/** 
	* Alias value: Salary_startDate
	* Hibernate value: Salary.startDate
	*/
	String  SALARY_START_DATE = SALARY_ENTRY.getAliasNames()[19];

	/** 
	* Alias value: Salary_totalDeduction
	* Hibernate value: Salary.totalDeduction
	*/
	String  SALARY_TOTAL_DEDUCTION = SALARY_ENTRY.getAliasNames()[20];

	/** 
	* Alias value: Salary_totalDaysHours
	* Hibernate value: Salary.totalDaysHours
	*/
	String  SALARY_TOTAL_DAYS_HOURS = SALARY_ENTRY.getAliasNames()[21];

	/** 
	* Alias value: Salary_totalLiquid
	* Hibernate value: Salary.totalLiquid
	*/
	String  SALARY_TOTAL_LIQUID = SALARY_ENTRY.getAliasNames()[22];

	/** 
	* Alias value: Salary_totalPayment
	* Hibernate value: Salary.totalPayment
	*/
	String  SALARY_TOTAL_PAYMENT = SALARY_ENTRY.getAliasNames()[23];

	/** 
	* Alias value: Salary_total
	* Hibernate value: Salary.total
	*/
	String  SALARY_TOTAL = SALARY_ENTRY.getAliasNames()[24];



	/** 
	* DAOConstantsEntry for SalaryBonus entity.
	*/ 
	DAOConstantsEntry SALARY_BONUS_ENTRY = DAOConstants.getDAOConstant(SalaryBonus.class);

	/** 
	* Alias value: SalaryBonus_amount
	* Hibernate value: SalaryBonus.amount
	*/
	String  SALARY_BONUS_AMOUNT = SALARY_BONUS_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: SalaryBonus_bonusConcept
	* Hibernate value: SalaryBonus.bonusConcept
	*/
	String  SALARY_BONUS_BONUS_CONCEPT = SALARY_BONUS_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: SalaryBonus_description
	* Hibernate value: SalaryBonus.description
	*/
	String  SALARY_BONUS_DESCRIPTION = SALARY_BONUS_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: SalaryBonus_id
	* Hibernate value: SalaryBonus.id
	*/
	String  SALARY_BONUS_ID = SALARY_BONUS_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: SalaryBonus_salary_id
	* Hibernate value: SalaryBonus.salary.id
	*/
	String  SALARY_BONUS_SALARY_ID = SALARY_BONUS_ENTRY.getAliasNames()[4];



	/** 
	* DAOConstantsEntry for SalaryCost entity.
	*/ 
	DAOConstantsEntry SALARY_COST_ENTRY = DAOConstants.getDAOConstant(SalaryCost.class);

	/** 
	* Alias value: SalaryCost_amount
	* Hibernate value: SalaryCost.amount
	*/
	String  SALARY_COST_AMOUNT = SALARY_COST_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: SalaryCost_costConcept
	* Hibernate value: SalaryCost.costConcept
	*/
	String  SALARY_COST_COST_CONCEPT = SALARY_COST_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: SalaryCost_description
	* Hibernate value: SalaryCost.description
	*/
	String  SALARY_COST_DESCRIPTION = SALARY_COST_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: SalaryCost_id
	* Hibernate value: SalaryCost.id
	*/
	String  SALARY_COST_ID = SALARY_COST_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: SalaryCost_salary_id
	* Hibernate value: SalaryCost.salary.id
	*/
	String  SALARY_COST_SALARY_ID = SALARY_COST_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: SalaryCost_type
	* Hibernate value: SalaryCost.type
	*/
	String  SALARY_COST_TYPE = SALARY_COST_ENTRY.getAliasNames()[5];



	/** 
	* DAOConstantsEntry for SalaryDeduction entity.
	*/ 
	DAOConstantsEntry SALARY_DEDUCTION_ENTRY = DAOConstants.getDAOConstant(SalaryDeduction.class);

	/** 
	* Alias value: SalaryDeduction_amount
	* Hibernate value: SalaryDeduction.amount
	*/
	String  SALARY_DEDUCTION_AMOUNT = SALARY_DEDUCTION_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: SalaryDeduction_deductionConcept
	* Hibernate value: SalaryDeduction.deductionConcept
	*/
	String  SALARY_DEDUCTION_DEDUCTION_CONCEPT = SALARY_DEDUCTION_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: SalaryDeduction_description
	* Hibernate value: SalaryDeduction.description
	*/
	String  SALARY_DEDUCTION_DESCRIPTION = SALARY_DEDUCTION_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: SalaryDeduction_expression
	* Hibernate value: SalaryDeduction.expression
	*/
	String  SALARY_DEDUCTION_EXPRESSION = SALARY_DEDUCTION_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: SalaryDeduction_id
	* Hibernate value: SalaryDeduction.id
	*/
	String  SALARY_DEDUCTION_ID = SALARY_DEDUCTION_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: SalaryDeduction_salary_id
	* Hibernate value: SalaryDeduction.salary.id
	*/
	String  SALARY_DEDUCTION_SALARY_ID = SALARY_DEDUCTION_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: SalaryDeduction_type
	* Hibernate value: SalaryDeduction.type
	*/
	String  SALARY_DEDUCTION_TYPE = SALARY_DEDUCTION_ENTRY.getAliasNames()[6];



	/** 
	* DAOConstantsEntry for SalaryEmbargo entity.
	*/ 
	DAOConstantsEntry SALARY_EMBARGO_ENTRY = DAOConstants.getDAOConstant(SalaryEmbargo.class);

	/** 
	* Alias value: SalaryEmbargo_amount
	* Hibernate value: SalaryEmbargo.amount
	*/
	String  SALARY_EMBARGO_AMOUNT = SALARY_EMBARGO_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: SalaryEmbargo_contractEmbargo_id
	* Hibernate value: SalaryEmbargo.contractEmbargo.id
	*/
	String  SALARY_EMBARGO_CONTRACT_EMBARGO_ID = SALARY_EMBARGO_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: SalaryEmbargo_description
	* Hibernate value: SalaryEmbargo.description
	*/
	String  SALARY_EMBARGO_DESCRIPTION = SALARY_EMBARGO_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: SalaryEmbargo_id
	* Hibernate value: SalaryEmbargo.id
	*/
	String  SALARY_EMBARGO_ID = SALARY_EMBARGO_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: SalaryEmbargo_salary_id
	* Hibernate value: SalaryEmbargo.salary.id
	*/
	String  SALARY_EMBARGO_SALARY_ID = SALARY_EMBARGO_ENTRY.getAliasNames()[4];



	/** 
	* DAOConstantsEntry for SalaryPayment entity.
	*/ 
	DAOConstantsEntry SALARY_PAYMENT_ENTRY = DAOConstants.getDAOConstant(SalaryPayment.class);

	/** 
	* Alias value: SalaryPayment_amount
	* Hibernate value: SalaryPayment.amount
	*/
	String  SALARY_PAYMENT_AMOUNT = SALARY_PAYMENT_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: SalaryPayment_description
	* Hibernate value: SalaryPayment.description
	*/
	String  SALARY_PAYMENT_DESCRIPTION = SALARY_PAYMENT_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: SalaryPayment_expression
	* Hibernate value: SalaryPayment.expression
	*/
	String  SALARY_PAYMENT_EXPRESSION = SALARY_PAYMENT_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: SalaryPayment_id
	* Hibernate value: SalaryPayment.id
	*/
	String  SALARY_PAYMENT_ID = SALARY_PAYMENT_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: SalaryPayment_paymentConcept
	* Hibernate value: SalaryPayment.paymentConcept
	*/
	String  SALARY_PAYMENT_PAYMENT_CONCEPT = SALARY_PAYMENT_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: SalaryPayment_salary_id
	* Hibernate value: SalaryPayment.salary.id
	*/
	String  SALARY_PAYMENT_SALARY_ID = SALARY_PAYMENT_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: SalaryPayment_type
	* Hibernate value: SalaryPayment.type
	*/
	String  SALARY_PAYMENT_TYPE = SALARY_PAYMENT_ENTRY.getAliasNames()[6];



	/** 
	* DAOConstantsEntry for SystemData entity.
	*/ 
	DAOConstantsEntry SYSTEM_DATA_ENTRY = DAOConstants.getDAOConstant(SystemData.class);

	/** 
	* Alias value: SystemData_comments
	* Hibernate value: SystemData.comments
	*/
	String  SYSTEM_DATA_COMMENTS = SYSTEM_DATA_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: SystemData_endDate
	* Hibernate value: SystemData.endDate
	*/
	String  SYSTEM_DATA_END_DATE = SYSTEM_DATA_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: SystemData_expression
	* Hibernate value: SystemData.expression
	*/
	String  SYSTEM_DATA_EXPRESSION = SYSTEM_DATA_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: SystemData_id
	* Hibernate value: SystemData.id
	*/
	String  SYSTEM_DATA_ID = SYSTEM_DATA_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: SystemData_name
	* Hibernate value: SystemData.name
	*/
	String  SYSTEM_DATA_NAME = SYSTEM_DATA_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: SystemData_readOnly
	* Hibernate value: SystemData.readOnly
	*/
	String  SYSTEM_DATA_READ_ONLY = SYSTEM_DATA_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: SystemData_startDate
	* Hibernate value: SystemData.startDate
	*/
	String  SYSTEM_DATA_START_DATE = SYSTEM_DATA_ENTRY.getAliasNames()[6];



	/** 
	* DAOConstantsEntry for SystemDeduction entity.
	*/ 
	DAOConstantsEntry SYSTEM_DEDUCTION_ENTRY = DAOConstants.getDAOConstant(SystemDeduction.class);

	/** 
	* Alias value: SystemDeduction_deductionConcept_id
	* Hibernate value: SystemDeduction.deductionConcept.id
	*/
	String  SYSTEM_DEDUCTION_DEDUCTION_CONCEPT_ID = SYSTEM_DEDUCTION_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: SystemDeduction_description
	* Hibernate value: SystemDeduction.description
	*/
	String  SYSTEM_DEDUCTION_DESCRIPTION = SYSTEM_DEDUCTION_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: SystemDeduction_descriptionDecorable
	* Hibernate value: SystemDeduction.descriptionDecorable
	*/
	String  SYSTEM_DEDUCTION_DESCRIPTION_DECORABLE = SYSTEM_DEDUCTION_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: SystemDeduction_endDate
	* Hibernate value: SystemDeduction.endDate
	*/
	String  SYSTEM_DEDUCTION_END_DATE = SYSTEM_DEDUCTION_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: SystemDeduction_expression
	* Hibernate value: SystemDeduction.expression
	*/
	String  SYSTEM_DEDUCTION_EXPRESSION = SYSTEM_DEDUCTION_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: SystemDeduction_id
	* Hibernate value: SystemDeduction.id
	*/
	String  SYSTEM_DEDUCTION_ID = SYSTEM_DEDUCTION_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: SystemDeduction_month
	* Hibernate value: SystemDeduction.month
	*/
	String  SYSTEM_DEDUCTION_MONTH = SYSTEM_DEDUCTION_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: SystemDeduction_startDate
	* Hibernate value: SystemDeduction.startDate
	*/
	String  SYSTEM_DEDUCTION_START_DATE = SYSTEM_DEDUCTION_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: SystemDeduction_type
	* Hibernate value: SystemDeduction.type
	*/
	String  SYSTEM_DEDUCTION_TYPE = SYSTEM_DEDUCTION_ENTRY.getAliasNames()[8];



	/** 
	* DAOConstantsEntry for SystemPayment entity.
	*/ 
	DAOConstantsEntry SYSTEM_PAYMENT_ENTRY = DAOConstants.getDAOConstant(SystemPayment.class);

	/** 
	* Alias value: SystemPayment_description
	* Hibernate value: SystemPayment.description
	*/
	String  SYSTEM_PAYMENT_DESCRIPTION = SYSTEM_PAYMENT_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: SystemPayment_descriptionDecorable
	* Hibernate value: SystemPayment.descriptionDecorable
	*/
	String  SYSTEM_PAYMENT_DESCRIPTION_DECORABLE = SYSTEM_PAYMENT_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: SystemPayment_endDate
	* Hibernate value: SystemPayment.endDate
	*/
	String  SYSTEM_PAYMENT_END_DATE = SYSTEM_PAYMENT_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: SystemPayment_expression
	* Hibernate value: SystemPayment.expression
	*/
	String  SYSTEM_PAYMENT_EXPRESSION = SYSTEM_PAYMENT_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: SystemPayment_id
	* Hibernate value: SystemPayment.id
	*/
	String  SYSTEM_PAYMENT_ID = SYSTEM_PAYMENT_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: SystemPayment_irpfExpression
	* Hibernate value: SystemPayment.irpfExpression
	*/
	String  SYSTEM_PAYMENT_IRPF_EXPRESSION = SYSTEM_PAYMENT_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: SystemPayment_month
	* Hibernate value: SystemPayment.month
	*/
	String  SYSTEM_PAYMENT_MONTH = SYSTEM_PAYMENT_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: SystemPayment_paymentConcept_id
	* Hibernate value: SystemPayment.paymentConcept.id
	*/
	String  SYSTEM_PAYMENT_PAYMENT_CONCEPT_ID = SYSTEM_PAYMENT_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: SystemPayment_quoteExpression
	* Hibernate value: SystemPayment.quoteExpression
	*/
	String  SYSTEM_PAYMENT_QUOTE_EXPRESSION = SYSTEM_PAYMENT_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: SystemPayment_salaryType
	* Hibernate value: SystemPayment.salaryType
	*/
	String  SYSTEM_PAYMENT_SALARY_TYPE = SYSTEM_PAYMENT_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: SystemPayment_startDate
	* Hibernate value: SystemPayment.startDate
	*/
	String  SYSTEM_PAYMENT_START_DATE = SYSTEM_PAYMENT_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: SystemPayment_type
	* Hibernate value: SystemPayment.type
	*/
	String  SYSTEM_PAYMENT_TYPE = SYSTEM_PAYMENT_ENTRY.getAliasNames()[11];


}