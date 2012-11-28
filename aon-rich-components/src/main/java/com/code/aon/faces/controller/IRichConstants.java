package com.code.aon.faces.controller;

public interface IRichConstants {

	String BUNDLE_NAME = "richBundle";
	
	String LOG_PANEL_CONTROLLER_NAME = "logPanel";
	
	String FILE_MANAGER_FORM = "fileManager_form";
	
	String FILE_DUPLICATED_NAME = "rich_file_duplicated_name";
	
	String SEARCH_NO_RESULTS = "aon_search_no_results";

	// UIViewRoot attributes keys
	
	String ATTRIBUTE_PREFFIX = "aon.richfaces.";
	
	String EDIT_DATA_TABLE_ID = ATTRIBUTE_PREFFIX + "EditDataTable.id";
	
	String LABELS_MAP = ATTRIBUTE_PREFFIX + "OutputLabel.map";
	
	String LOOKUP_MODAL_PANEL_MAP = ATTRIBUTE_PREFFIX + "LookupButtonPopup.map";
	
	String SELECTED_MENU_ATTRIBUTE = ATTRIBUTE_PREFFIX + "selectedMenu";
	
	String CURRENT_COMPONENT_GROUP = ATTRIBUTE_PREFFIX + "ComponentGroup.current";
	
	String CURRENT_FORM = ATTRIBUTE_PREFFIX + "Form.current";
	
	String CURRENT_FORM_DATA_TABLE_MAP = CURRENT_FORM + "DataTable.map";

}
