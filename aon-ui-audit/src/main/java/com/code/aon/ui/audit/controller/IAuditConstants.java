package com.code.aon.ui.audit.controller;

public interface IAuditConstants {

	// Controllers	
	String AUDIT_COLLECTIONS_CONTROLLER_NAME = "auditCollections";
	String ACTION_DENIED_CONTROLLER_NAME = "actionDenied";	
	String AUDIT_CONTROLLER_NAME = "audit";
	String SESSION_CONTROLLER_NAME = "auditSession";
	String ACTION_FAVORITE_CONTROLLER_NAME = "actionFavorite";

	// Properties
	String MODULES_ENABLED = "modulesEnabled";
	String PROFILE_DENIED_ACTIONS_ENABLED = "profileDeniedActionsEnabled";
	
	// Velocity Templates
	String OPTIONS_TEMPLATE = "options.xhtml.vm";
	String MENU_ITEM_TEMPLATE = "menuItem.xhtml.vm";
	String WEB_MAP_TEMPLATE = "webMap.xhtml.vm";
	String INIT_ACTION_TEMPLATE = "initAction.xhtml.vm";
	
	// Velocity Attributes
	String OPTION_VM = "option";
	String OPTIONS_VM = "options";
	String ACTIONS_VM = "actions";
	String PREFFIX_VM = "preffix";
	String CATEGORIES_VM = "categories";
	String CATEGORY_MAP_VM = "categoryMap";
	
	// Id preffixes
	String FAVORITE_PREFFIX = "favorite_";
	String MORE_USED_PREFFIX = "moreUsed_";	
	
	// Menu IDs
	String ENTERPRISE_CATEGORY = "enterprise";
	String CONFIGURATION_CATEGORY = "configuration";
	String DOCUMENT_CATEGORY = "document";
	String PAYROLL_CATEGORY = "payroll";
	
	String GROUP_CONFIG_COMPANY = "group_config_company";
	String GROUP_CONFIG_SECURITY = "group_configuration_security";
	String GROUP_ENTERPRISE_SECURITY = "group_enterprise_security";
	String GROUP_DOCUMENT = "group_documental";
	String GROUP_DOCUMENT_UTILITIES = "group_documental_utilities";
	String GROUP_PAYROLL_CONTRATA_MAIN = "group_payroll_contrata_general";
	String GROUP_CONFIGURATION_UTILITIES = "group_configuration_utilities";
	
	String MAIL_ACCOUNT_ACTION = "mailAccount_list-enterprise";	
	String SIGNATURE_ACTION = "signature_list-enterprise";
	String USER_PROFILE_ACTION = "domainUser_profile";
	String BATCH_DOCUMENT_ACTION = "batchDocument_form";

}
