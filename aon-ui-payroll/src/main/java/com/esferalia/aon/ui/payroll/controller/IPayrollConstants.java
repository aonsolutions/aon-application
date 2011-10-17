package com.esferalia.aon.ui.payroll.controller;

public interface IPayrollConstants {
	
	// ************************************************************
	// MESSAGES
	// ************************************************************
	
	/** ResourceBundle name. */
	String BUNDLE_BASE_NAME = "com.esferalia.aon.ui.payroll.i18n.messages";
	String BUNDLE_NAME = "payrollBundle";
	String SALARY_EMAIL_SUBJECT = "payroll_email_subject";
	String SALARY_EMAIL_BODY_HEADER = "payroll_email_subject_header";
	String SALARY_EMAIL_BODY_LINE = "payroll_email_subject_line";
	String SALARY_EMAIL_BODY_FOOTER = "payroll_email_subject_footer";
	String PAYROLL_DOCUMENTS = "payroll_documents";
	String PAYROLL_ECONOMIC_DATA = "payroll_economic_data";
	String PAYROLL_SALARY_PAYMENTS = "payroll_salary_payments_tab";
	String PAYROLL_SALARY_DEDUCTIONS = "payroll_salary_deductions_tab";
	String PAYROLL_SALARY_BONUS = "payroll_salary_bonus_tab";
	String PAYROLL_SALARY_EMBARGOS = "payroll_salary_embargos_tab";
	String PAYROLL_SALARY = "payroll_salary";
	String PAYROLL_SALARY_DRAFT = "payroll_salary_draft";
	String PAYROLL_SETTLE_NOTICE_DAY_AMOUNT = "payroll_settle_noticeDayAmount";
	String PAYROLL_SETTLE_VACATION_AMOUNT = "payroll_settle_vacationAmount";
	String PAYROLL_SETTLE_COMPENSATION = "payroll_settle_compensation";
	String PAYROLL_IRPF = "payroll_irpf";
	
	
	// ************************************************************
	// CONTROLLERS
	// ************************************************************
	String AGREEMENT_CONTROLLER_NAME = "agreement";
	String AGREEMENT_EXTRA_CONTROLLER_NAME = "agreementExtra";
	String AGREEMENT_LEVEL_CONTROLLER_NAME = "agreementLevel";
	String AGREEMENT_PAYMENT_CONTROLLER_NAME = "agreementPayment";
	String AGREEMENT_TREE_CONTROLLER_NAME = "agreementTree";
	String CERTIFICATE_FILE_WIZARD = "certifica2FileWizard";
	String CONTRACT_CONTROLLER = "contract";
	String CONTRACT_ATTACH_CONTROLLER = "contractAttach";
	String CONTRACT_BONUS_CONTROLLER = "contractBonus";
	String CONTRACT_EMBARGO_CONTROLLER = "contractEmbargo";
	String CONTRACT_PAYMENT_CONTROLLER = "contractPayment";
	String CONTRACT_DEDUCTION_CONTROLLER = "contractDeduction";
	String CONTRACT_DATA_CONTROLLER = "contractData";
	String CONTRACT_GENERATION_WIZARD_CONTROLLER = "contractGenerationWizard";
	String CONTRACT_LEAVE_CONTROLLER_NAME = "contractLeave";
	String ENTERPRISE_CONTROLLER="enterprise";
	String ENTERPRISE_DIR_STAFF_CONTROLLER="enterpriseDirStaff";
	String ENTERPRISE_ACTIVITY_CONTROLLER = "enterpriseActivity";
	String ENTERPRISE_CCC_CONTROLLER = "enterpriseCCC";
	String ENTERPRISE_TREE_CONTROLLER = "enterpriseTree";
	String IRPF_DATA_CONTROLLER_NAME = "irpfData";
	String IRPF_LAUNCHER_CONTROLLER_NAME = "irpfLauncher";
	String IRPF_REGULARIZATION_CONTROLLER_NAME = "irpfRegularization";
	String IRPF_RESULT_CONTROLLER_NAME = "irpfResult";
	String LEAVE_BATCH_DETAIL_CONTROLLER_NAME = "leaveBatchDetail";
	String LEAVE_BATCH_CONTROLLER_NAME = "leaveBatch";
	String LEAVE_LIST_CONTROLLER_NAME = "leaveList";
	String SALARY_CONTROLLER = "salary";
	String SALARY_DRAFT_BONUS_CONTROLLER = "salaryDraftBonus";
	String SALARY_DRAFT_CONTROLLER = "salaryDraft";
	String SALARY_DRAFT_DEDUCTION_CONTROLLER = "salaryDraftDeduction";
	String SALARY_DRAFT_PAYMENT_CONTROLLER = "salaryDraftPayment";
	String SALARY_EMBARGO_CONTROLLER = "salaryEmbargo";
	String SALARY_LAUNCHER_CONTROLLER = "salaryLauncher";
	String SALARY_REMOVER_CONTROLLER = "salaryRemover";
	String SALARY_TEST_LAUNCHER_NAME = "salaryTestLauncher";
	String SETTLE_CONTROLLER_NAME = "settle";
	String PAYMENT_UPDATE_CONTROLLER = "paymentUpdate";
	String PAYROLL_WORK_PLACE_CONTROLLER = "payrollWorkPlace";

	// ************************************************************
	// CONFIGURATION
	// ************************************************************
	String MODEL_PATH = "com/esferalia/aon/ui/payroll/contractModel/";
	String SHOW_ENTERPRISE_IN_SEARCH = "showEnterpriseInSearch";
	String SHOW_PERSON_COLUMN = "showPersonColumn";

	// ************************************************************
	// REPORT
	// ************************************************************
	String SALARY_REPORT = "salary";
	String SALARY_LIST_REPORT = "salaryList";
	String DEFAULT_SALARY_TEMPLATE = "salary";
	String DEFAULT_SALARY_DRAFT_TEMPLATE = "salaryDraft";

	// ************************************************************
	// NAVIGATION KEYS
	// ************************************************************
	String AGREEMENT_FORM = "agreement_form";
	String AGREEMENT_LEVEL_FORM = "agreement_level_form";
	String CONTRACT_FORM_TREE = "contract_formTree";
	String ENTERPRISE_FORM_TREE = "enterprise_formTree";
	String SALARY_TESTER_LAUNCHER_FORM = "salaryTestLauncher_form";
	
		
}
