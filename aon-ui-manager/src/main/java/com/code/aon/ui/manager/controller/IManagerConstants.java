package com.code.aon.ui.manager.controller;

public interface IManagerConstants {
	
	// Bundle
	String BUNDLE_NAME = "managerBundle";
	
	// Controllers
	String MANAGER_CONTROLLER_NAME = "manager";
	String DOMAIN_CONTROLLER_NAME = "domain";
	String APPLICATION_CONTROLLER_NAME = "aonApplication";
	String ROLE_CONTROLLER_NAME = "role";
	String PROFILE_CONTROLLER_NAME = "profile";
	String DOMAIN_PROFILE_CONTROLLER_NAME = "domainProfile";
	String DOMAIN_DB_CONNECTION_CONTROLLER_NAME = "domainDBConnection";
	String DOMAIN_APPLICATION_CONTROLLER_NAME = "domainApplication";
	String DOMAIN_USER_CONTROLLER_NAME = "domainUser";
	String DOMAIN_APPLICATION_USER_CONTROLLER_NAME = "appUser";
	String WORK_GROUP_CONTROLLER_NAME = "workGroup";
	String SCOPE_CONTROLLER_NAME = "scope";
	String USER_WORK_GROUP_CONTROLLER_NAME = "userWorkgroup";
	String USER_SCOPE_CONTROLLER_NAME = "userScope";
	String COMPANY_BASIC_CONTROLLER_NAME = "companyBasic";
	
	// Applications
	String AON_DESKTOP = "aon-desktop";
	String AON_WEBMAIL = "aon-webmail";
	String AON_MANAGER = "aon-manager";
	String AON_CMS = "aon-cms";
	String AON_PUBLISHER = "aon-publisher";
	
	String USUARIO_PROFILE = "Usuario";
	String ADMINISTRADOR_PROFILE = "Administrador";
	String ADMIN_USER = "admin";
	String GENERAL_SCOPE = "GENERAL";
	
	// Messages
	String DOMAIN_INVALID_NAME = "manager_domain_invalid_name";
	String DOMAIN_DUPLICATED_NAME = "manager_domain_duplicated_name";
	String APPLICATION_INVALID_NAME = "manager_application_invalid_name";
	String APPLICATION_DUPLICATED_NAME = "manager_application_duplicated_name";
	String DB_DUPLICATED = "manager_db_duplicated";
	String DB_NOT_EXIST = "manager_db_not_exist";
	String NEW_PASSWORD_ERROR = "manager_new_passwd_error";
	String NEED_MAIL_ACCOUNT = "manager_need_mail_account";
	String WRONG_MAIL_ACCOUNT = "manager_wrong_mail_account";

	// Properties
	
	String ADVANCED_MODE_USER = "advancedMode_user";
	String ADVANCED_MODE_PASSWORD = "advancedMode_password";
	String NOTIFICATION_EMAIL = "notification_email";
	String NEW_DOMAIN_APPLICATION_URL = "newDomainApplicationURL";
	String MAIL_ACCOUNT_CREATE_SCRIPT = "MailAccount_create_script";
	String MAIL_ACCOUNT_DELETE_SCRIPT = "MailAccount_delete_script";
	
}

