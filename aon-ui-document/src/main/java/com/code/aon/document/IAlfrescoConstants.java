package com.code.aon.document;

import static org.alfresco.webservice.util.Constants.NAMESPACE_CONTENT_MODEL;
import static org.alfresco.webservice.util.Constants.NAMESPACE_SYSTEM_MODEL;

import org.alfresco.webservice.util.Constants;


/**
 * Interface for Alfresco constants.
 * 
 * @author atellitu
 *
 */
public interface IAlfrescoConstants {

	
	String NAMESPACE_AON_MODEL = "http://aon.esferalia.com/models/catalogue/1.0";
	
	String AON_MODEL = "{" + NAMESPACE_AON_MODEL + "}";
	
	String AON_PREFFIX = "aon:";

	String SYSTEM_MODEL = "{" + NAMESPACE_SYSTEM_MODEL + "}";
	
	String SYSTEM_PREFFIX = "sys:";

	String CONTENT_MODEL = "{" + NAMESPACE_CONTENT_MODEL + "}";
	
	String CONTENT_PREFFIX = "cm:";
	
	String TYPE_CONTENT = CONTENT_PREFFIX + "content";
	
	String TYPE_CATEGORY = CONTENT_PREFFIX + "category";
	
	String CATEGORY_ROOT_SHORT = CONTENT_PREFFIX + "categoryRoot";
	
	String UUID = "node-uuid";
	
	String UUID_LONG = SYSTEM_MODEL + UUID;
	
	String UUID_SHORT = SYSTEM_PREFFIX + UUID;
	
	String PATH_LONG = CONTENT_MODEL + "path";

	String NAME = "name";
	
	String NAME_LONG = Constants.PROP_NAME;
	
	String NAME_SHORT = CONTENT_PREFFIX + NAME;

	String TITLE = "title";
	
	String TITLE_LONG = Constants.PROP_TITLE;
	
	String TITLE_SHORT = CONTENT_PREFFIX + TITLE;

	String DESCRIPTION = "description";
	
	String DESCRIPTION_LONG = Constants.PROP_DESCRIPTION;
	
	String DESCRIPTION_SHORT = CONTENT_PREFFIX + DESCRIPTION;
	
	String CREATED = "created";
	
	String CREATED_LONG = Constants.PROP_CREATED;
	
	String CREATED_SHORT = CONTENT_PREFFIX + CREATED;
	
	String MODIFIED = "modified";
	
	String MODIFIED_LONG = CONTENT_MODEL + MODIFIED;
	
	String CATEGORIES = "categories";
	
	String CATEGORIES_SHORT = CONTENT_PREFFIX + CATEGORIES;
	
	String CATEGORIES_LONG = CONTENT_MODEL + CATEGORIES;
	
	String MIME_TYPE = Constants.PROP_CONTENT + ".mimetype";
	
	String ASPECT_NAME = AON_MODEL + "enterpriseDocument";
	
	String ENTERPRISE_ID = "enterpriseId";
	
	String ENTERPRISE_ID_SHORT = AON_PREFFIX + ENTERPRISE_ID;

	String ENTERPRISE_ID_LONG = AON_MODEL + ENTERPRISE_ID;

	String PROJECT_ID = "projectId";
	
	String PROJECT_ID_SHORT = AON_PREFFIX + PROJECT_ID;

	String PROJECT_ID_LONG = AON_MODEL + PROJECT_ID;

	String REFERENCE_DATE = "referenceDate";
	
	String REFERENCE_DATE_SHORT = AON_PREFFIX + REFERENCE_DATE;

	String REFERENCE_DATE_LONG = AON_MODEL + REFERENCE_DATE;
	
	String GROUP_AUTHORITY_TYPE = "GROUP";
	
	String USER_AUTHORITY_TYPE = "USER";
	
	String ALFRESCO_ADMINISTRATORS = "ALFRESCO_ADMINISTRATORS";
	
	String ENTERPRISE_PREFFIX = "EMPRESA-";
	
	String COMPANY_HOME_PATH = "/app:company_home";

	// ************************************************************
	// MESSAGES
	// ************************************************************
	
	String REMOVE_ERROR = "document_error_remove";
	String UPDATE_ERROR = "document_error_update";
	String INSERT_ERROR = "document_error_insert";
	
	
}
