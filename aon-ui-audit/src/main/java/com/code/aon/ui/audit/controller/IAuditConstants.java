package com.code.aon.ui.audit.controller;

public interface IAuditConstants {

	// Bundle
	String BUNDLE_NAME = "auditBundle";
	
	// Controllers	
	String AUDIT_COLLECTIONS_CONTROLLER_NAME = "auditCollections";
	String APPLICATION_OPTION_CONTROLLER_NAME = "appOption";
	String ACTION_DENIED_CONTROLLER_NAME = "actionDenied";	
	String AUDIT_CONTROLLER_NAME = "audit";
	String ACTION_MORE_USED_CONTROLLER_NAME = "actionMoreUsed";

	// Properties
	String MODULES_ENABLED = "modulesEnabled";
	String PROFILE_DENIED_ACTIONS_ENABLED = "profileDeniedActionsEnabled";
	
	// Velocity Templates
	String OPTIONS_TEMPLATE = "options.xhtml.vm";
	String MENU_ITEM_TEMPLATE = "menuItem.xhtml.vm";
	String LAST_EXECUTED_TEMPLATE = "lastExecuted.xhtml.vm";
	String WEB_MAP_TEMPLATE = "webMap.xhtml.vm";
	
	// Velocity Attributes
	String OPTIONS_VM = "options";
	String ACTIONS_VM = "actions";
	String PREFFIX_VM = "preffix";
	String CATEGORIES_VM = "categories";
	String CATEGORY_MAP_VM = "categoryMap";
	
	// Id preffixes
	String FAVORITE_PREFFIX = "favorite_";
	String MORE_USED_PREFFIX = "moreUsed_";	

}
