package com.code.aon.ui.audit.controller;

public interface IAuditConstants {

	String AUDIT_COLLECTIONS_CONTROLLER_NAME = "auditCollections";

	String APPLICATION_OPTION_CONTROLLER_NAME = "appOption";

	String ACTION_DENIED_CONTROLLER_NAME = "actionDenied";	
	
	/** Velocity Templates **/
	String OPTIONS_TEMPLATE = "options.xhtml.vm";
	
	String MENU_ITEM_TEMPLATE = "menuItem.xhtml.vm";
	
	String LAST_EXECUTED_TEMPLATE = "lastExecuted.xhtml.vm";
	
	String WEB_MAP_TEMPLATE = "webMap.xhtml.vm";
	
	
	/** Velocity Attributes **/
	String OPTIONS_VM = "options";
	
	String ACTIONS_VM = "actions";
	
	String PREFFIX_VM = "preffix";
	
	String CATEGORIES_VM = "categories";
	
	String CATEGORY_MAP_VM = "categoryMap";
	
	
	/** Id preffixes **/
	String FAVORITE_PREFFIX = "favorite_";
	
	String MORE_USED_PREFFIX = "moreUsed_";	

}
