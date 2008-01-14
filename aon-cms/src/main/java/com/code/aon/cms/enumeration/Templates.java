package com.code.aon.cms.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

public enum Templates implements IResourceable {

	/**
	 * Index Page with principal structure
	 */
	INDEX,

	/**
	 * Index Page for popups or no menus page
	 */
	INNER,

	/**
	 * Language Page (index.php)
	 */
	LANGUAGE,

	/**
	 * Home Page
	 */
	HOME,

	/**
	 * Generic Page
	 */
	GENERIC,

	/**
	 * Menu Page
	 */
	MENU,

	/**
	 * FAQS
	 */
	FAQ,
	
	/**
	 * FAQS
	 */
	LINK,

	/**
	 * MODULAR
	 */
	MODULAR,

	/**
	 * ARTICLE
	 */
	ARTICLE,

	/**
	 * ARTICLE
	 */
	DIRECT_ACCESS,
	
	/**
	 * ALBUM_IMAGES
	 */
	ALBUM_IMAGES;
	
	/**
	 * Ruta base del fichero de mensajes.
	 */
	private static final String BASE_NAME = "com.code.aon.cms.i18n.templates";
	
    /**
     * Prefijo de la llave de mensajes. 
     */
    private static final String MSG_KEY_PREFIX = "aon_enum_template_";

    private static final String VM_KEY_PREFIX = "aon_enum_template_vm_";

    private static final String HTML_KEY_PREFIX = "aon_enum_template_html_";

    public String getName() {
		ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME);
		return bundle.getString(MSG_KEY_PREFIX + toString());
	}

	public String getName(Locale locale) {
		ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale);
		return bundle.getString(MSG_KEY_PREFIX + toString());
	}

	public String getTemplateName() {
		ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME);
		return bundle.getString(VM_KEY_PREFIX + toString());
	}

	public String getHtmlName() {
		ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME);
		return bundle.getString(HTML_KEY_PREFIX + toString());
	}

}