package com.code.aon.ui.manager.controller;

public interface IManagerConstants {
	
	// Bundle
	String BUNDLE_NAME = "managerBundle";
	
	// Controllers
	String DOMAIN_CONTROLLER_NAME = "domain";
	String APPLICATION_CONTROLLER_NAME = "aonApplication";
	String ROLE_CONTROLLER_NAME = "role";
	String PROFILE_CONTROLLER_NAME = "profile";
	String DOMAIN_DB_CONNECTION_CONTROLLER_NAME = "domainDBConnection";
	String DOMAIN_APPLICATION_CONTROLLER_NAME = "domainApplication";
	String DOMAIN_USER_CONTROLLER_NAME = "domainUser";
	
	// Messages
	String DOMAIN_INVALID_NAME = "manager_domain_invalid_name";
	String APPLICATION_INVALID_NAME = "manager_application_invalid_name";
	String APPLICATION_DUPLICATED_NAME = "manager_application_duplicated_name";
	String DB_CONNECTION_INVALID_NAME = "manager_db_connection_invalid_name";
	String DB_CONNECTION_DUPLICATED_NAME = "manager_db_connection_duplicated_name";
	String DB_DUPLICATED = "manager_db_duplicated";

}
