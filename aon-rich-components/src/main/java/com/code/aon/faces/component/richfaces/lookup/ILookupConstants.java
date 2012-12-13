package com.code.aon.faces.component.richfaces.lookup;

public interface ILookupConstants {

	// Tags
	String ACTION_TYPE = "actionType";
	String LOOKUP = "lookup";
	String WINDOW_TITLE = "windowTitle";
	String WINDOW_CLOSE_FOCUS = "windowCloseFocus";
	String SELECT_RE_RENDER = "selectReRender";
	String LOOKUP_PROPERTY = "lookupProperty";
	String LOOKUP_CHANGE_LISTENER = "lookupChangeListener";
	String CONTROLLER_LISTENER = "controllerListener";
	String ALIAS = "alias";
	String COLUMN_EXPRESSION = "columnExpression";
	String SUGGEST_ALIAS = "suggestAlias";
	String MATCH_BEGIN_ONLY = "matchBeginOnly";
	String LOOKUP_ACTION = "lookupAction";
	String OR_EXPRESSION = "orExpression";
	
	// Title default messages
	String LIST_TITLE = "#{bundle.aon_open_select_window}";
   	String NEW_TITLE = "#{bundle.aon_open_new_window}";
   	String CLEAR_TITLE = "#{bundle.aon_clear_select}";
   	
	// Action Listenr Methods
    String LIST_ACTION_LISTENER = "onShowListWindow";
	String SEARCH_ACTION_LISTENER = "onShowSearchWindow";
   	String NEW_ACTION_LISTENER = "onShowNewWindow";
   	String CLEAR_ACTION_LISTENER = "onClear";
   	
   	// Style classes
   	String LIST_STYLE_CLASS = "aon-lookupButton";
   	String LIST_DISABLED_STYLE_CLASS = "aon-lookupButton-disabled";
   	String NEW_STYLE_CLASS = "aon-lookupButton-new";
   	String NEW_DISABLED_STYLE_CLASS = "aon-lookupButton-new-disabled";
   	String CLEAR_STYLE_CLASS = "aon-lookupButton-clear";
	
}
