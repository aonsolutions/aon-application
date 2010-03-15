package com.code.aon.cms.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

public enum PageType implements IResourceable {

	/**
	 * Generic Page
	 */
	EXTERNAL,

	/**
	 * Generic Page
	 */
	GENERIC,

	/**
	 * Menu Page
	 */
	MENU,

	/**
	 * Faq
	 */
	FAQ,

	/**
	 * Links
	 */
	LINK,

	/**
	 * Modular
	 */
	MODULAR,

	/**
	 * DIRECT_ACCESS
	 */
	DIRECT_ACCESS,
	
	/**
	 * ALBUM_IMAGES,
	 */
	ALBUM_IMAGES,
	
	/**
	 * ARTICLE_NEWS
	 */
	ARTICLE_NEWS,

	/**
	 * ARTICLE_SERVICES
	 */
	ARTICLE_SERVICES,

	/**
	 * ARTICLE_EVENTS
	 */
	ARTICLE_EVENTS,

	/**
	 * ARTICLE_OTHER
	 */
	ARTICLE_OTHER,
	
	/**
	 * DOWNLOAD_CATEGORY
	 */
	DOWNLOAD,

	/**
	 * DIARY
	 */
	DIARY,

	/**
	 * PRODUCT_CATEGORIES,
	 */
	PRODUCT_CATEGORIES,

	/**
	 * BRANDS,
	 */
	BRANDS,

	/**
	 * HIRU,
	 */
	HIRU,

	/**
	 * SPORT,
	 */
	SPORT,
	
	/**
	 * Activity
	 */
	ACTIVITY;
	
	/**
	 * Ruta base del fichero de mensajes.
	 */
	private static final String BASE_NAME = "com.code.aon.cms.i18n.enumeration";
	
    /**
     * Prefijo de la llave de mensajes. 
     */
    private static final String MSG_KEY_PREFIX = "aon_enum_page_type_";

    public String getName() {
		ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME);
		return bundle.getString(MSG_KEY_PREFIX + toString());
	}

	public String getName(Locale locale) {
		ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale);
		return bundle.getString(MSG_KEY_PREFIX + toString());
	}

}