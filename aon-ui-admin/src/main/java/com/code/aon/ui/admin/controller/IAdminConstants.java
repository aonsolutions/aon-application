package com.code.aon.ui.admin.controller;

public interface IAdminConstants {
	
	// Bundle
	String BUNDLE_NAME = "managerBundle";

	// Controllers
	String ADMIN_CONTROLLER_NAME = "adminMain";
	String APPLICATION_CONTROLLER_NAME = "adminApplication";
	String DOMAIN_CONTROLLER_NAME = "adminDomain";
	String DOMAIN_USER_CONTROLLER_NAME = "domainUser";	
	String DB_MANAGER_CONTROLLER_NAME = "dbManager";	
	
	// Properties
	String ADVANCED_MODE_USER = "advancedMode_user";
	String ADVANCED_MODE_PASSWORD = "advancedMode_password";
	String NOTIFICATION_EMAIL = "notification_email";
	String SHOW_DOMAIN_MANAGEMENT = "showDomainManagement";
	
	String ADMIN_USER = "admin";
	String GENERAL_SCOPE = "GENERAL";
	String DEFAULT_SUBDOMAIN_SUFFIX = "aonsolutions.es";
	
	String USER_TABLE = "user";
	String COMPANY_TABLE = "company";
	String SCOPE_TABLE = "scope";
	String WORK_GROUP_TABLE = "workgroup";
	
	// Messages
	String WRONG_MAIL_ACCOUNT = "admin_wrong_mail_account";
	String NEED_MAIL_ACCOUNT = "admin_need_mail_account";
	String DB_NOT_EXIST = "admin_db_not_exist";
	String DB_DUPLICATED = "admin_db_duplicated";
	String NEW_PASSWORD_ERROR = "admin_new_passwd_error";
	
	// Navigation
	String DOMAIN_LIST = DOMAIN_CONTROLLER_NAME + "_list";
	String DOMAIN_FORM_TEMPLATE = "/com/code/aon/ui/admin/facelet/adminDomain/form.xhtml";
	String DOMAIN_LIST_TEMPLATE = "/com/code/aon/ui/admin/facelet/adminDomain/list.xhtml";
		
}