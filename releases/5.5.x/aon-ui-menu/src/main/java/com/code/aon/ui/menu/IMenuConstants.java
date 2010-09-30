package com.code.aon.ui.menu;

/**
 * The class that defines the constants that are used in <code>aon-ui-menu</code>.
 * 
 * @author Consulting & Development. Eugenio Castellano - 08-mar-2005
 * @since 1.0
 * 
 */
public interface IMenuConstants {
	/**
	 * DIGESTER rules file name.
	 */
	static final String RULES = "menu_digester.xml";

	/**
	 * Parameter that defines the path of the XML file that holds the menu.
	 */
	static final String MENU_PARAM = "aon.ui.menu.file";

	/**
	 * Parameter that defines the identifier of the menu.
	 */
	static final String MENU_ID_PARAM = "aon_ui_menu_id";

	/**
	 * Parameter that defines the path of the XSL style sheet.
	 */
	static final String XSL_PARAM = "aon.ui.xsl.file";

	/**
	 * Parameter that defines the parth of the i18n file.
	 */
	static final String BASENAME_PARAM = "aon.ui.basename";

	/**
	 * The name of the attribute in which it is stored the base name of the i18n file.
	 */
	static final String BASENAME_ATTR = "aon.ui.basename.attr";

	/**
	 * The name of the attribute in which it is stored the locale of the i18n file. 
	 */
	static final String LOCALE_ATTR = "aon.ui.locale.attr";

	/**
	 * Name of the attribute that will be used to store the {@link IMenu}
	 * object in some context of the container.
	 */
	static final String MENU_ATTR = "aon.ui.menu.attr";

	/**
	 * XML Content Type (application/xml)
	 */
	static final String XML_CONTENT_TYPE = "application/xml";

	/**
	 * XML Content Type (application/xml)
	 */
	static final String HTML_CONTENT_TYPE = "text/html";

	/**
	 * Identifier of the root tag.
	 */
	static final String ROOT = "root";

	/**
	 * Identifier of the key attribute.
	 */
	static final String KEY = "key";

	/**
	 * Identifier of the descriptionKey attribute. 
	 */
	static final String DESCRIPTION_KEY = "descriptionKey";

	/**
	 * Identifier of the label attribute. 
	 */
	static final String LABEL = "label";

	/**
	 * Identifier of the description attribute. 
	 */
	static final String DESCRIPTION = "description";
	
	/**
	 * Identifier of the icon attribute.
	 */
	static final String ICON = "icon";

	/**
	 * Identifier of the external attribute.
	 */
	static final String EXTERNAL = "external";

	/**
	 * Identifier of the context relative attribute. 
	 */
	static final String CONTEXT_RELATIVE = "contextRelative";

	/**
	 * Identifier of the target attribute. 
	 */
	static final String TARGET = "target";

	/**
	 * Identifier of the menu tag. 
	 */
	static final String MENU = "menu";

	/**
	 * Identifier of the id attribute.
	 */
	static final String ID = "id";

	/**
	 * Identifier of the reference attribute. 
	 */
	static final String REF = "ref";

	/**
	 * Identifier of the role attribute. 
	 */
	static final String ROLE = "role";

	/**
	 * Identifier of the option tag. 
	 */
	static final String OPTION = "option";

	/**
	 * Identifier of the action listener tag. 
	 */
	static final String ACTION_LISTENER = "actionListener";

	/**
	 * Identifier of the action attribute.
	 */
	static final String ACTION = "action";

	/**
	 * Identifier of the type attribute.
	 */
	static final String TYPE = "type";

	/**
	 * Identifier of the root type for the tree2 facet. 
	 */
	static final String ROOT_TYPE = "root";

	/**
	 * Identifier of the menu type for the tree2 facet. 
	 */
	static final String MENU_TYPE = "menu";

	/**
	 * Identifier of the option type for the tree2 facet. 
	 */
	static final String OPTION_TYPE = "option";
}