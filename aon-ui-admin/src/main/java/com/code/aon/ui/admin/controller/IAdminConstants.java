package com.code.aon.ui.admin.controller;

public interface IAdminConstants {
	
	// Controllers
	String ADMIN_CONTROLLER_NAME = "adminMain";
	String DOMAIN_CONTROLLER_NAME = "adminDomain";
	String SERVICE_ACCOUNT_CONTROLLER_NAME = "serviceAccount";
	String APPLICATION_PROFILE_CONTROLLER_NAME = "applicationProfile";
	String DOMAIN_APPLICATION_PROFILE_CONTROLLER_NAME = "domainApplicationProfile";
	String DOMAIN_APPLICATION_CONTROLLER_NAME = "domainApplication";
	String DOMAIN_USER_CONTROLLER_NAME = "domainUser";	
	String APPLICATION_USER_CONTROLLER_NAME = "appUser";
	String USER_SCOPE_EX_CONTROLLER_NAME = "userScopeEx";
	String USER_WORK_GROUP_EX_CONTROLLER_NAME = "userWorkGroupEx";
	String DOMAIN_APPLICATION_MODULE_CONTROLLER_NAME = "domainApplicationModule";
	String PROFILE_ACTION_DENIED_CONTROLLER_NAME = "profileActionDenied";
	String DOMAINS_CONTROLLER_NAME = "domains";
	String DOMAINS_SEARCH_CONTROLLER_NAME = "domainSearch";
	String NEW_DOMAIN_CONTROLLER_NAME = "adminNewDomain";
	String REMOVE_DOMAIN_CONTROLLER_NAME = "adminRemoveDomain";
	String GLOBAL_CONFIG_CONTROLLER_NAME = "globalConfig";
	String PORTAL_ACCESS_CONTROLLER_NAME = "portalAccess";
	String DOMAIN_BOOKING_CONTROLLER_NAME = "domainBooking";
	String DOMAIN_PRINT_CONTROLLER_NAME = "domainPrint";
	
	// Properties
	String ADVANCED_MODE_USER = "advancedMode_user";
	String ADVANCED_MODE_PASSWORD = "advancedMode_password";
	String NOTIFICATION_EMAIL = "notification_email";
	
	String ADMIN_USER = "admin";
	String GENERAL_SCOPE = "GENERAL";
	String DEFAULT_SUBDOMAIN_SUFFIX = "aonsolutions.es";
	String AON_EMPLOYEE_APPLICATION = "aon-employee";
	
	String USER_TABLE = "user";
	String COMPANY_TABLE = "company";
	String SCOPE_TABLE = "scope";
	String WORK_GROUP_TABLE = "workgroup";

	int PAYROLL_INFO_PORTAL = 1;
	int FISCAL_INFO_PORTAL = 2;
	int DOCUMENTAL_INFO_PORTAL = 4;
	int PAYROLL_PORTAL = 8;
	int ACCOUNTING_PORTAL = 16;
	int ACTIVE_PORTAL = 32;
	int INACTIVE_PORTAL = 64;
	int DOCUMENTAL_MANAGEMENT_PORTAL = 128;
	int FINANCE_MANAGEMENT_PORTAL = 256;
	
}