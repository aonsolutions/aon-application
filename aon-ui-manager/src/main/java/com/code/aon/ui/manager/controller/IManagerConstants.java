package com.code.aon.ui.manager.controller;

public interface IManagerConstants {
	
	// Bundle
	String BUNDLE_NAME = "managerBundle";
	
	// Controllers
	String MANAGER_CONTROLLER_NAME = "manager";
	String DB_MANAGER_CONTROLLER_NAME = "dbManager";
	String DOMAIN_CONTROLLER_NAME = "domain";
	String APPLICATION_CONTROLLER_NAME = "aonApplication";
	String ROLE_CONTROLLER_NAME = "role";
	String PROFILE_CONTROLLER_NAME = "profile";
	String DOMAIN_PROFILE_CONTROLLER_NAME = "domainProfile";
	String DOMAIN_DB_CONNECTION_CONTROLLER_NAME = "domainDBConnection";
	String DOMAIN_APPLICATION_CONTROLLER_NAME = "domainApplication";
	String DOMAIN_USER_CONTROLLER_NAME = "domainUser";
	String DOMAIN_APPLICATION_USER_CONTROLLER_NAME = "appUser";
	String USER_WORK_GROUP_CONTROLLER_NAME = "userWorkGroupEx";
	String USER_SCOPE_CONTROLLER_NAME = "userScopeEx";
	String COMPANY_BASIC_CONTROLLER_NAME = "companyBasic";
	String ALIAS_CONTROLLER_NAME = "alias";
	String CONFIG_CONTROLLER_NAME = "aonConfig";
	String BEAN_CONTACT = "contact";
	String BEAN_SIGNATURE = "signature";
	String BEAN_MAIL_ACCOUNT = "mailAccount";	
	
	// Applications
	String AON_DESKTOP = "aon-desktop";
	String AON_WEBMAIL = "aon-webmail";
	String AON_MANAGER = "aon-manager";
	String AON_CMS = "aon-cms";
	String AON_PUBLISHER = "aon-publisher";
	String DEFAULT_SUBDOMAIN_SUFFIX = "aonsolutions.es";
	
	String USUARIO_PROFILE = "Usuario";
	String ADMINISTRADOR_PROFILE = "Administrador";
	String INVITADO_PROFILE = "Invitado";
	String ADMIN_USER = "admin";
	String GENERAL_SCOPE = "GENERAL";
	
	String USER_TABLE = "user";
	String COMPANY_TABLE = "company";
	String SCOPE_TABLE = "scope";
	String WORK_GROUP_TABLE = "workgroup";
	
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
	String WRONG_EMAIL = "manager_wrong_email";
	
	// Properties
	
	String ADVANCED_MODE_USER = "advancedMode_user";
	String ADVANCED_MODE_PASSWORD = "advancedMode_password";
	String NOTIFICATION_EMAIL = "notification_email";
	String MAIL_ACCOUNT_CREATE_SCRIPT = "MailAccount_create_script";
	String MAIL_ACCOUNT_DELETE_SCRIPT = "MailAccount_delete_script";
	String LOGGER_SCRIPT = "logger_script";
	String SHOW_DOMAIN_MANAGEMENT = "showDomainManagement";
	
	String DOMAIN_LIST = "domain_list";
	String DOMAIN_FORM_TEMPLATE = "/com/code/aon/ui/manager/facelet/domain/form.xhtml";
	String DOMAIN_LIST_TEMPLATE = "/com/code/aon/ui/manager/facelet/domain/list.xhtml";
	
}