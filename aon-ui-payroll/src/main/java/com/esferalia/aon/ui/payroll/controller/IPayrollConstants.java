package com.esferalia.aon.ui.payroll.controller;

import com.esferalia.aon.file.payroll.contract.pdf.ModelOption;

public interface IPayrollConstants {
	
	String ZERO_VALUE = "0";
	String IPREM_FORMMULA = "EXCESO_IPREM";
		
	// ************************************************************
	// CONTROLLERS
	// ************************************************************
	String AGREEMENT_CONTROLLER_NAME = "agreement";
	String AGREEMENT_EXTRA_CONTROLLER_NAME = "agreementExtra";
	String AGREEMENT_LEVEL_CONTROLLER_NAME = "agreementLevel";
	String AGREEMENT_PAYMENT_CONTROLLER_NAME = "agreementPayment";
	String AGREEMENT_TREE_CONTROLLER_NAME = "agreementTree";
	String CONTRACT_CONTROLLER = "contract";
	String CONTRACT_ATTACH_CONTROLLER = "contractAttach";
	String CONTRACT_BATCH_ATTACH_CONTROLLER_NAME = "contractBatchAttach";
	String CONTRACT_BATCH_CONTROLLER_NAME = "contractBatch";
	String CONTRACT_BATCH_DETAIL_CONTROLLER_NAME = "contractBatchDetail";
	String CONTRACT_CLAUSES_CONTROLLER = "contractClauses";
	String CONTRACT_CLAUSE_CONTROLLER = "contractClause";
	String CONTRACT_LIST_CONTROLLER_NAME = "contractList";
	String CONTRACT_BONUS_CONTROLLER = "contractBonus";
	String CONTRACT_EMBARGO_CONTROLLER = "contractEmbargo";
	String CONTRACT_PAYMENT_CONTROLLER = "contractPayment";
	String CONTRACT_PDF_CONTROLLER_NAME = "contractPdf";
	String CONTRACT_DEDUCTION_CONTROLLER = "contractDeduction";
	String CONTRACT_DATA_CONTROLLER = "contractData";
	String CONTRACT_GENERATION_WIZARD_CONTROLLER = "contractGenerationWizard";
	String CONTRACT_LEAVE_CONTROLLER_NAME = "contractLeave";
	String CONTRACT_SEARCH_LISTENER_NAME = "contractSearch";
	String ENTERPRISE_CONTROLLER="enterprise";
	String ENTERPRISE_DIR_STAFF_CONTROLLER="enterpriseDirStaff";
	String ENTERPRISE_ACTIVITY_CONTROLLER = "enterpriseActivity";
	String ENTERPRISE_CCC_CONTROLLER = "enterpriseCCC";
	String ENTERPRISE_TREE_CONTROLLER = "enterpriseTree";
	String FAN_BATCH_ATTACH_CONTROLLER_NAME = "fanBatchAttach";
	String FAN_BATCH_DETAIL_CONTROLLER_NAME = "fanBatchDetail";
	String FAN_BATCH_CONTROLLER_NAME = "fanBatch";
	String FAN_LIST_CONTROLLER_NAME = "fanList";
	String IRPF_DATA_CONTROLLER_NAME = "irpfData";
	String IRPF_LAUNCHER_CONTROLLER_NAME = "irpfLauncher";
	String IRPF_REGULARIZATION_CONTROLLER_NAME = "irpfRegularization";
	String IRPF_RESULT_CONTROLLER_NAME = "irpfResult";
	String IRPF_DRAFT_CONTROLLER_NAME = "irpfDraft";
	String LEAVE_BATCH_DETAIL_CONTROLLER_NAME = "leaveBatchDetail";
	String LEAVE_BATCH_CONTROLLER_NAME = "leaveBatch";
	String LEAVE_LIST_CONTROLLER_NAME = "leaveList";
	String PAYMENT_UPDATE_CONTROLLER = "paymentUpdate";
	String PAYROLL_APP_PARAMS_CONTROLLER_NAME = "payrollAppParams";
	String PAYROLL_WORK_PLACE_CONTROLLER = "payrollWorkPlace";
	String PERSON_CONTROLLER_NAME = "person";
	String SALARY_CONTROLLER = "salary";
	String SALARY_DRAFT_BONUS_CONTROLLER = "salaryDraftBonus";
	String SALARY_DRAFT_CONTROLLER = "salaryDraft";
	String SALARY_DRAFT_DEDUCTION_CONTROLLER = "salaryDraftDeduction";
	String SALARY_DRAFT_PAYMENT_CONTROLLER = "salaryDraftPayment";
	String SALARY_EMBARGO_CONTROLLER = "salaryEmbargo";
	String SALARY_LAUNCHER_CONTROLLER = "salaryLauncher";
	String SALARY_REMOVER_CONTROLLER = "salaryRemover";
	String SALARY_TEST_LAUNCHER_NAME = "salaryTestLauncher";
	String IRPF_TEST_LAUNCHER_NAME = "irpfTestLauncher";
	String SALARY_EXPENSE_CONTROLLER_NAME = "salaryExpense";
	String TRAINING_CENTER_CONTROLLER_NAME = "trainingCenter";

	// ************************************************************
	// CONFIGURATION
	// ************************************************************
	String SHOW_ENTERPRISE_IN_SEARCH = "showEnterpriseInSearch";
	String SHOW_PERSON_COLUMN = "showPersonColumn";

	// ************************************************************
	// REPORT
	// ************************************************************
	String SALARY_REPORT = "salary";
	String SALARY_LIST_REPORT = "salaryList";
	String DEFAULT_SETTLEMENT_TEMPLATE = "settlement";
	String DEFAULT_SALARY_TEMPLATE = "salary";
	String DEFAULT_SALARY_DRAFT_TEMPLATE = "salaryDraft";
	String COST_REPORT = "salaryExpenseReport";
	String TRAINING_DIRECT_DEBIT_REPORT_KEY = "trainingDirectDebit";
	String ADDITIONAL_CLAUSES_REPORT_KEY = "additionalClauses";

	// ************************************************************
	// NAVIGATION KEYS
	// ************************************************************
	String PERSON_FORM = "person_form";
	String AGREEMENT_FORM = "agreement_form";
	String AGREEMENT_LEVEL_FORM = "agreement_level_form";
	String CONTRACT_FORM_TREE = "contract_formTree";
	String ENTERPRISE_FORM_TREE = "enterprise_formTree";
	String SALARY_DRAFT_FORM = "salaryDraft_form";
	String SALARY_TESTER_LAUNCHER_FORM = "salaryTestLauncher_form";
	String IRPF_TESTER_LAUNCHER_FORM = "irpfTestLauncher_form";
	
	// ************************************************************
	// OTHER
	// ************************************************************
	public static final ModelOption[] AVAILABLE_CONTRACT_MODEL_OPTIONS = {
		ModelOption.LEARNING_OPT1,
//		ModelOption.LEARNING_OPT2,
//		ModelOption.LEARNING_OPT3,
//		ModelOption.LEARNING_OPT4,
//		ModelOption.PRACTICE_OPT1,
//		ModelOption.PRACTICE_OPT2,
//		ModelOption.PRACTICE_OPT3,
//		ModelOption.PRACTICE_OPT4,
//		ModelOption.PRACTICE_OPT5,
		ModelOption.TEMPORARY_OPT1,
		ModelOption.TEMPORARY_OPT2,
		ModelOption.TEMPORARY_OPT3,
//		ModelOption.TEMPORARY_OPT4,
//		ModelOption.TEMPORARY_OPT5,
//		ModelOption.TEMPORARY_OPT6,
//		ModelOption.TEMPORARY_OPT7,
		ModelOption.TEMPORARY_OPT8,
//		ModelOption.TEMPORARY_OPT9,
//		ModelOption.TEMPORARY_OPT10,
		ModelOption.TEMPORARY_OPT11,
		ModelOption.TEMPORARY_OPT12,
//		ModelOption.TEMPORARY_OPT13,
		ModelOption.TEMPORARY_OPT14,
//		ModelOption.TEMPORARY_OPT15,
//		ModelOption.TEMPORARY_OPT16,
//		ModelOption.TEMPORARY_OPT17,
//		ModelOption.TEMPORARY_OPT18,
		ModelOption.INDEFINITE_OPT1,
//		ModelOption.INDEFINITE_OPT2,
//		ModelOption.INDEFINITE_OPT3,
//		ModelOption.INDEFINITE_OPT4,
		ModelOption.INDEFINITE_OPT5,
		ModelOption.INDEFINITE_OPT6,
		ModelOption.INDEFINITE_OPT7,
//		ModelOption.INDEFINITE_OPT8,
//		ModelOption.INDEFINITE_OPT9,
//		ModelOption.INDEFINITE_OPT10,
//		ModelOption.INDEFINITE_OPT11,
//		ModelOption.INDEFINITE_OPT12,
//		ModelOption.INDEFINITE_OPT13,
//		ModelOption.INDEFINITE_OPT14,
//		ModelOption.INDEFINITE_OPT15,
//		ModelOption.INDEFINITE_OPT16,
//		ModelOption.INDEFINITE_OPT17, // TRANSFORMATION CODES
	};
	
}
