package com.code.aon.cms.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

public enum ModularPageOptionType implements IResourceable {

	/**
	 * Generic Page
	 */
	GENERIC,

	/**
	 * Banner
	 */
	BANNER,

	/**
	 * BannerGroup
	 */
	BANNER_GROUP,

	/**
	 * Article
	 */
	ARTICLE,

	/**
	 * News
	 */
	ARTICLE_NEWS,

	/**
	 * Services
	 */
	ARTICLE_SERVICES,

	/**
	 * Events
	 */
	ARTICLE_EVENTS,

	/**
	 * Other
	 */
	ARTICLE_OTHER,

	/**
	 * Direct Access
	 */
	DIRECT_ACCESS_GROUP,
	
	/**
	 * Direct Access
	 */
	DOWNLOADS,
	
	/**
	 * Direct Access
	 */
	DIRECT_ACCESS,
	
	/**
	 * Link Category
	 */
	LINK_CATEGORY,

	/**
	 * Activity
	 */
	ACTIVITY,
	
	/**
	 * Album Category
	 */
	ALBUM_CATEGORY,

	/**
	 * Bulletin suscription
	 */
	BULLETIN_SUSCRIBE,

	/**
	 * Bulletin suscription
	 */
	NEXT_ARTICLES;

	/**
	 * Ruta base del fichero de mensajes.
	 */
	private static final String BASE_NAME = "com.code.aon.cms.i18n.enumeration";
	
    /**
     * Prefijo de la llave de mensajes. 
     */
    private static final String MSG_KEY_PREFIX = "aon_enum_modular_page_option_type_";

    private static final String VM_MODULE_KEY_PREFIX =  "aon_enum_modular_page_option_vm_";

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
		return bundle.getString(VM_MODULE_KEY_PREFIX + toString());
	}

}