package com.code.aon.ui.admin.controller;

public interface IAdminConstants {
	
	// Bundle
	String BUNDLE_NAME = "adminBundle";

	// Controllers
	String ADMIN_CONTROLLER_NAME = "adminMain";
	String APPLICATION_CONTROLLER_NAME = "adminApplication";
	String DOMAIN_CONTROLLER_NAME = "adminDomain";
	String APPLICATION_PROFILE_CONTROLLER_NAME = "applicationProfile";
	String DOMAIN_APPLICATION_PROFILE_CONTROLLER_NAME = "domainApplicationProfile";
	String DOMAIN_APPLICATION_CONTROLLER_NAME = "domainApplication";
	String DOMAIN_USER_CONTROLLER_NAME = "domainUser";	
	String APPLICATION_USER_CONTROLLER_NAME = "appUser";
	String USER_SCOPE_EX_CONTROLLER_NAME = "userScopeEx";
	String USER_WORK_GROUP_EX_CONTROLLER_NAME = "userWorkGroupEx";
	String DOMAIN_APPLICATION_MODULE_CONTROLLER_NAME = "domainApplicationModule";
	
	// Properties
	String ADVANCED_MODE_USER = "advancedMode_user";
	String ADVANCED_MODE_PASSWORD = "advancedMode_password";
	String NOTIFICATION_EMAIL = "notification_email";
	String SHOW_DOMAIN_MANAGEMENT = "showDomainManagement";
	
	String ADMIN_USER = "admin";
	String GENERAL_SCOPE = "GENERAL";
	String DEFAULT_SUBDOMAIN_SUFFIX = "aonsolutions.es";
	String AON_AIO_APPLICATION = "aon-aio";
	
	String USER_TABLE = "user";
	String COMPANY_TABLE = "company";
	String SCOPE_TABLE = "scope";
	String WORK_GROUP_TABLE = "workgroup";
	
	// Messages
	String WRONG_MAIL_ACCOUNT = "admin_wrong_mail_account";
	String NEED_MAIL_ACCOUNT = "admin_need_mail_account";
	String NEW_PASSWORD_ERROR = "admin_new_passwd_error";
	String USER_DUPLICATED = "admin_user_duplicated_login";
	
	// Navigation
	String DOMAIN_LIST = DOMAIN_CONTROLLER_NAME + "_list";
	String DOMAIN_FORM_TEMPLATE = "/com/code/aon/ui/admin/facelet/adminDomain/form.xhtml";
	String DOMAIN_LIST_TEMPLATE = "/com/code/aon/ui/admin/facelet/adminDomain/list.xhtml";
		
}