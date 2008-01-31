package com.code.aon.cms.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

public enum SidebarType implements IResourceable {

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
	 * Menu Page
	 */
	MENU,

	/**
	 * Article
	 */
	ARTICLE,

	/**
	 * Links
	 */
	LINK,

	/**
	 * DirectAccessGroup
	 */
	DIRECT_ACCESS;

	/**
	 * Ruta base del fichero de mensajes.
	 */
	private static final String BASE_NAME = "com.code.aon.cms.i18n.enumeration";
	
    /**
     * Prefijo de la llave de mensajes. 
     */
    private static final String MSG_KEY_PREFIX = "aon_enum_sidebar_type_";

    private static final String VM_SIDEBAR_KEY_PREFIX = "aon_enum_sidebar_vm_";

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
		return bundle.getString(VM_SIDEBAR_KEY_PREFIX + toString());
	}

}