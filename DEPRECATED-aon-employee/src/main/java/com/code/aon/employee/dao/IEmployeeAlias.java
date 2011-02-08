package com.code.aon.employee.dao;

import com.code.aon.common.dao.DAOConstants;
import com.code.aon.common.dao.DAOConstantsEntry;
import com.code.aon.employee.Contract;
import com.code.aon.employee.ContractBatch;
import com.code.aon.employee.ContractBatchDetail;
import com.code.aon.employee.ContractCalendarEvent;
import com.code.aon.employee.ContractData;
import com.code.aon.employee.ContractDeduction;
import com.code.aon.employee.ContractEvent;
import com.code.aon.employee.ContractPayment;
import com.code.aon.employee.ContractType;
import com.code.aon.employee.DeductionConcept;
import com.code.aon.employee.FunctionConstant;
import com.code.aon.employee.PaymentConcept;
import com.code.aon.employee.Salary;
import com.code.aon.employee.SalaryPayment;
import com.code.aon.employee.SalaryDeduction;

/** 
* Interface for holding entity properties constants.
*/ 
public interface IEmployeeAlias {



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
	* Alias value: ContractData_category
	* Hibernate value: ContractData.category
	*/
	String  CONTRACT_DATA_CATEGORY = CONTRACT_DATA_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: ContractData_code
	* Hibernate value: ContractData.code
	*/
	String  CONTRACT_DATA_CODE = CONTRACT_DATA_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: ContractData_conditions
	* Hibernate value: ContractData.conditions
	*/
	String  CONTRACT_DATA_CONDITIONS = CONTRACT_DATA_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: ContractData_contract_id
	* Hibernate value: ContractData.contract.id
	*/
	String  CONTRACT_DATA_CONTRACT_ID = CONTRACT_DATA_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: ContractData_description
	* Hibernate value: ContractData.description
	*/
	String  CONTRACT_DATA_DESCRIPTION = CONTRACT_DATA_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: ContractData_endDate
	* Hibernate value: ContractData.endDate
	*/
	String  CONTRACT_DATA_END_DATE = CONTRACT_DATA_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: ContractData_id
	* Hibernate value: ContractData.id
	*/
	String  CONTRACT_DATA_ID = CONTRACT_DATA_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: ContractData_quoteGroup
	* Hibernate value: ContractData.quoteGroup
	*/
	String  CONTRACT_DATA_QUOTE_GROUP = CONTRACT_DATA_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: ContractData_startDate
	* Hibernate value: ContractData.startDate
	*/
	String  CONTRACT_DATA_START_DATE = CONTRACT_DATA_ENTRY.getAliasNames()[8];



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
	* DAOConstantsEntry for ContractEvent entity.
	*/ 
	DAOConstantsEntry CONTRACT_EVENT_ENTRY = DAOConstants.getDAOConstant(ContractEvent.class);

	/** 
	* Alias value: ContractEvent_contract_id
	* Hibernate value: ContractEvent.contract.id
	*/
	String  CONTRACT_EVENT_CONTRACT_ID = CONTRACT_EVENT_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: ContractEvent_endDate
	* Hibernate value: ContractEvent.endDate
	*/
	String  CONTRACT_EVENT_END_DATE = CONTRACT_EVENT_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: ContractEvent_expression
	* Hibernate value: ContractEvent.expression
	*/
	String  CONTRACT_EVENT_EXPRESSION = CONTRACT_EVENT_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: ContractEvent_id
	* Hibernate value: ContractEvent.id
	*/
	String  CONTRACT_EVENT_ID = CONTRACT_EVENT_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: ContractEvent_name
	* Hibernate value: ContractEvent.name
	*/
	String  CONTRACT_EVENT_NAME = CONTRACT_EVENT_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: ContractEvent_startDate
	* Hibernate value: ContractEvent.startDate
	*/
	String  CONTRACT_EVENT_START_DATE = CONTRACT_EVENT_ENTRY.getAliasNames()[5];



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
	* Alias value: ContractPayment_month
	* Hibernate value: ContractPayment.month
	*/
	String  CONTRACT_PAYMENT_MONTH = CONTRACT_PAYMENT_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: ContractPayment_paymentConcept_id
	* Hibernate value: ContractPayment.paymentConcept.id
	*/
	String  CONTRACT_PAYMENT_PAYMENT_CONCEPT_ID = CONTRACT_PAYMENT_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: ContractPayment_startDate
	* Hibernate value: ContractPayment.startDate
	*/
	String  CONTRACT_PAYMENT_START_DATE = CONTRACT_PAYMENT_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: ContractPayment_type
	* Hibernate value: ContractPayment.type
	*/
	String  CONTRACT_PAYMENT_TYPE = CONTRACT_PAYMENT_ENTRY.getAliasNames()[9];



	/** 
	* DAOConstantsEntry for ContractType entity.
	*/ 
	DAOConstantsEntry CONTRACT_TYPE_ENTRY = DAOConstants.getDAOConstant(ContractType.class);

	/** 
	* Alias value: ContractType_CCCType
	* Hibernate value: ContractType.CCCType
	*/
	String  CONTRACT_TYPE_CCCTYPE = CONTRACT_TYPE_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: ContractType_description
	* Hibernate value: ContractType.description
	*/
	String  CONTRACT_TYPE_DESCRIPTION = CONTRACT_TYPE_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: ContractType_duration
	* Hibernate value: ContractType.duration
	*/
	String  CONTRACT_TYPE_DURATION = CONTRACT_TYPE_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: ContractType_id
	* Hibernate value: ContractType.id
	*/
	String  CONTRACT_TYPE_ID = CONTRACT_TYPE_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: ContractType_workingDay
	* Hibernate value: ContractType.workingDay
	*/
	String  CONTRACT_TYPE_WORKING_DAY = CONTRACT_TYPE_ENTRY.getAliasNames()[4];



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
	* Alias value: DeductionConcept_id
	* Hibernate value: DeductionConcept.id
	*/
	String  DEDUCTION_CONCEPT_ID = DEDUCTION_CONCEPT_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: DeductionConcept_type
	* Hibernate value: DeductionConcept.type
	*/
	String  DEDUCTION_CONCEPT_TYPE = DEDUCTION_CONCEPT_ENTRY.getAliasNames()[3];



	/** 
	* DAOConstantsEntry for FunctionConstant entity.
	*/ 
	DAOConstantsEntry FUNCTION_CONSTANT_ENTRY = DAOConstants.getDAOConstant(FunctionConstant.class);

	/** 
	* Alias value: FunctionConstant_comments
	* Hibernate value: FunctionConstant.comments
	*/
	String  FUNCTION_CONSTANT_COMMENTS = FUNCTION_CONSTANT_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: FunctionConstant_endDate
	* Hibernate value: FunctionConstant.endDate
	*/
	String  FUNCTION_CONSTANT_END_DATE = FUNCTION_CONSTANT_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: FunctionConstant_expression
	* Hibernate value: FunctionConstant.expression
	*/
	String  FUNCTION_CONSTANT_EXPRESSION = FUNCTION_CONSTANT_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: FunctionConstant_id
	* Hibernate value: FunctionConstant.id
	*/
	String  FUNCTION_CONSTANT_ID = FUNCTION_CONSTANT_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: FunctionConstant_name
	* Hibernate value: FunctionConstant.name
	*/
	String  FUNCTION_CONSTANT_NAME = FUNCTION_CONSTANT_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: FunctionConstant_readOnly
	* Hibernate value: FunctionConstant.readOnly
	*/
	String  FUNCTION_CONSTANT_READ_ONLY = FUNCTION_CONSTANT_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: FunctionConstant_startDate
	* Hibernate value: FunctionConstant.startDate
	*/
	String  FUNCTION_CONSTANT_START_DATE = FUNCTION_CONSTANT_ENTRY.getAliasNames()[6];



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
	* Alias value: PaymentConcept_id
	* Hibernate value: PaymentConcept.id
	*/
	String  PAYMENT_CONCEPT_ID = PAYMENT_CONCEPT_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: PaymentConcept_type
	* Hibernate value: PaymentConcept.type
	*/
	String  PAYMENT_CONCEPT_TYPE = PAYMENT_CONCEPT_ENTRY.getAliasNames()[3];



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
	* DAOConstantsEntry for SalaryDeduction entity.
	*/ 
	DAOConstantsEntry SALARY_DEDUCTION_ENTRY = DAOConstants.getDAOConstant(SalaryDeduction.class);

	/** 
	* Alias value: SalaryDeduction_amount
	* Hibernate value: SalaryDeduction.amount
	*/
	String  SALARY_DEDUCTION_AMOUNT = SALARY_DEDUCTION_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: SalaryDeduction_description
	* Hibernate value: SalaryDeduction.description
	*/
	String  SALARY_DEDUCTION_DESCRIPTION = SALARY_DEDUCTION_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: SalaryDeduction_expression
	* Hibernate value: SalaryDeduction.expression
	*/
	String  SALARY_DEDUCTION_EXPRESSION = SALARY_DEDUCTION_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: SalaryDeduction_id
	* Hibernate value: SalaryDeduction.id
	*/
	String  SALARY_DEDUCTION_ID = SALARY_DEDUCTION_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: SalaryDeduction_salary_id
	* Hibernate value: SalaryDeduction.salary.id
	*/
	String  SALARY_DEDUCTION_SALARY_ID = SALARY_DEDUCTION_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: SalaryDeduction_type
	* Hibernate value: SalaryDeduction.type
	*/
	String  SALARY_DEDUCTION_TYPE = SALARY_DEDUCTION_ENTRY.getAliasNames()[5];


}