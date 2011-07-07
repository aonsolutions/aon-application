package com.esferalia.aon.ui.payroll.controller;

public interface IPayrollConstants {
	
	// ************************************************************
	// MESSAGES
	// ************************************************************
	
	/** ResourceBundle name. */
	String BUNDLE_NAME = "payrollBundle";
	String SALARY_EMAIL_SUBJECT = "employee_email_subject";
	String SALARY_EMAIL_BODY_HEADER = "employee_email_subject_header";
	String SALARY_EMAIL_BODY_LINE = "employee_email_subject_line";
	String SALARY_EMAIL_BODY_FOOTER = "employee_email_subject_footer";
	String PAYROLL_DOCUMENTS= "payroll_documents";
	String PAYROLL_ECONOMIC_DATA = "payroll_economic_data";
	String PAYROLL_SALARY_PAYMENTS = "payroll_salary_payments_tab";
	String PAYROLL_SALARY_DEDUCTIONS = "payroll_salary_deductions_tab";
	String PAYROLL_SALARY_BONUS = "payroll_salary_bonus_tab";
	String PAYROLL_SALARY_EMBARGOS = "payroll_salary_embargos_tab";
	String PAYROLL_SALARY = "payroll_salary";
	String PAYROLL_SALARY_DRAFT = "payroll_salary_draft";
	
	// ************************************************************
	// CONTROLLERS
	// ************************************************************
	String AGREEMENT_CONTROLLER_NAME = "agreement";
	String AGREEMENT_LEVEL_CONTROLLER_NAME = "agreementLevel";
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
	String ENTERPRISE_CONTROLLER="enterprise";
	String ENTERPRISE_DIR_STAFF_CONTROLLER="enterpriseDirStaff";
	String ENTERPRISE_ACTIVITY_CONTROLLER = "enterpriseActivity";
	String ENTERPRISE_CCC_CONTROLLER = "enterpriseCCC";
	String ENTERPRISE_TREE_CONTROLLER = "enterpriseTree";
	String SALARY_CONTROLLER = "salary";
	String SALARY_DRAFT_BONUS_CONTROLLER = "salaryDraftBonus";
	String SALARY_DRAFT_CONTROLLER = "salaryDraft";
	String SALARY_DRAFT_DEDUCTION_CONTROLLER = "salaryDraftDeduction";
	String SALARY_DRAFT_PAYMENT_CONTROLLER = "salaryDraftPayment";
	String SALARY_EMBARGO_CONTROLLER = "salaryEmbargo";
	String SALARY_TEST_LAUNCHER_NAME = "salaryTestLauncher";
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
	String SALARY_TESTER_LAUNCHER_FORM = "salaryTestLauncher_form";
	
		
}
