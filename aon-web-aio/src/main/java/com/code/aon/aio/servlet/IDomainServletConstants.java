package com.code.aon.aio.servlet;

public interface IDomainServletConstants {

	// DEPLOYED.XML CONSTANTS
	String DEPLOYED_XML = "/etc/tomcat6/aon.workspace/deployed.xml";
	String DEPLOYED_NAME_ATTR = "name";
	String DEPLOYED_VALUE_ATTR = "value";
	String DEPLOYED_OPTION_TAG = "option";
	String DEPLOYED_URL_PROPERTY = "hibernate.connection.url";
	String DEPLOYED_USER_PROPERTY = "hibernate.connection.username";
	String DEPLOYED_PASSWORD_PROPERTY = "hibernate.connection.password";
	String DEPLOYED_USESSL_PROPERTY = "hibernate.connection.usessl";
	String DEPLOYED_TIMEZONE_PROPERTY = "hibernate.connection.timezone";

	// DATABASE CONSTANTS
	String AON_SOLUTIONS_NET_DOMAIN = "aon.aonsolutions.net";

	String TEST_DATABASE = "test-aonsolutions-net";
	String DEMO_DATABASE = "demo-aonsolutions-net";
	String PRODUCTION_DATABASE = "pro-aonsolutions-net";

	String TEST_DATABASE_PARAM = "test";
	String DEMO_DATABASE_PARAM = "demo";
	String PRODUCTION_DATABASE_PARAM = "pro";

	String DOMAIN_SUFFIX = ".aonsolutions.net";

	// REQUEST PARAMETERS
	String USER_PARAM = "user";
	String PASSWORD_PARAM = "password";
	String DOMAIN_NAME_PARAM = "domain-name";
	String DOMAIN_DESCRIPTION_PARAM = "domain-description";
	String DOMAIN_TYPE_PARAM = "domain-type";
    String DOMAIN_USER_PARAM = "domain-user";
    String DOMAIN_PASSWORD_PARAM = "domain-password";
    String DOMAIN_MODULES = "domain-modules";
    String DOMAIN_MAX_DEFINED_USERS = "domain-max-defined-users";
    String DOMAIN_TARGET = "domain-target";

	// PYTHON SCRIPTS CONSTANTS
	String NEW_SCRIPT = "new_domain.py";
	String UPDATE_SCRIPT = "update_domain.py";
	String SP_HOST = "--host=";
	String SP_USER = "--user=";
	String SP_PASSWD = "--passwd=";
	String SP_TIMEZONE = "--tz=";
	String SP_USESSL = "--ssl=";
	String SP_DB = "--db=";
	String SP_DOMAIN_NAME = "--domain-name=";
	String SP_DOMAIN_DESCRIPTION = "--domain-description=";
	String SP_DOMAIN_TYPE = "--domain-type=";
    String SP_DOMAIN_USER = "--domain-user=";
    String SP_DOMAIN_PASSWORD = "--domain-password=";
    String SP_DOMAIN_MODULES = "--domain-modules=";
    String SP_DOMAIN_MAX_DEFINED_USERS = "--domain-max-defined-users=";
    String SP_DOMAIN_OWNER = "--domain-owner=";

    // SQL SENTENCES
    String TABLE_SCHEMA_SENTENCE = "SELECT T.TABLE_SCHEMA FROM INFORMATION_SCHEMA.TABLES as T WHERE T.TABLE_NAME = 'domain'";
}
