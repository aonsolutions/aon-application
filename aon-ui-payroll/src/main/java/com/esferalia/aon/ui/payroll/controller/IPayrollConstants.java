package com.esferalia.aon.ui.payroll.controller;

public interface IPayrollConstants {
	
	// ************************************************************
	// MESSAGES
	// ************************************************************
	
	/** ResourceBundle name. */
	String BUNDLE_NAME = "employeeBundle";
	String SALARY_EMAIL_SUBJECT = "employee_email_subject";
	String SALARY_EMAIL_BODY_HEADER = "employee_email_subject_header";
	String SALARY_EMAIL_BODY_LINE = "employee_email_subject_line";
	String SALARY_EMAIL_BODY_FOOTER = "employee_email_subject_footer";
	
	// ************************************************************
	// CONTROLLERS
	// ************************************************************
	String AGREEMENT_CONTROLLER_NAME = "agreement";
	String AGREEMENT_LEVEL_CONTROLLER_NAME = "agreementLevel";
	String AGREEMENT_TREE_CONTROLLER_NAME = "agreementTree";
	String CONTRACT_CONTROLLER = "contract";
	String CONTRACT_PAYMENT_CONTROLLER = "contractPayment";
	String CONTRACT_DEDUCTION_CONTROLLER = "contractDeduction";
	String CONTRACT_DATA_CONTROLLER = "contractData";
	String ENTERPRISE_DIR_STAFF_CONTROLLER="enterpriseDirStaff";
	String ENTERPRISE_ACTIVITY_CONTROLLER = "enterpriseActivity";
	String ENTERPRISE_CCC_CONTROLLER = "enterpriseCCC";
	String SALARY_CONTROLLER = "salary";
	String SALARY_DRAFT_CONTROLLER = "salaryDraft";
	String PAYMENT_UPDATE_CONTROLLER = "paymentUpdate";
	String SALARY_LAUNCHER_NAME = "salaryLauncher";

	// ************************************************************
	// CONFIGURATION
	// ************************************************************
	String MODEL_PATH = "com/code/aon/ui/employee/contractModel/";
	String SHOW_ENTERPRISE_IN_SEARCH = "showEnterpriseInSearch";
	String SHOW_PERSON_COLUMN = "showPersonColumn";

	// ************************************************************
	// REPORT
	// ************************************************************
	String CURRENT_SALARY_REPORT = "currentSalaryReport";
	String SALARY_REPORT = "salaryReport";

	// ************************************************************
	// NAVIGATION KEYS
	// ************************************************************
	String AGREEMENT_FORM = "agreement_form";
	String AGREEMENT_LEVEL_FORM = "agreement_level_form";
}
