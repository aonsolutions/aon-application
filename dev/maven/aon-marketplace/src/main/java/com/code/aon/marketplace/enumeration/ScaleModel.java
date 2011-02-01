package com.code.aon.marketplace.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

/**
 * Enumeration of event category.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 21-nov-2006
 * @version 1.0
 * @since 1.0
 * 
 */
public enum ScaleModel implements IResourceable {

    NONE,
	EUROSCALE,
    DIBAL,
    BIZERBA;
    
    /**
     * Messages file base path.
     */
    private static final String BASE_NAME = "com.code.aon.marketplace.i18n.messages";

    /**
     * Messages key prefix. 
     */
    private static final String MSG_KEY_PREFIX = "aon_enum_scalemodel_";
    private static final String SELECT_CATEGORY_PREFIX = "selectcategory_";
    private static final String INSERT_CATEGORY_PREFIX = "insertcategory_";
    private static final String MAX_CATEGORY_PREFIX = "maxcategory_";
    private static final String INSERT_SUBCATEGORY_PREFIX = "insertsubcategory_";
    private static final String MAX_SUBCATEGORY_PREFIX = "maxsubcategory_";
    private static final String UPDATE_CATEGORY_PREFIX = "updatecategory_";
    private static final String UPDATE_SUBCATEGORY_PREFIX = "updatesubcategory_";
    private static final String EXISTS_CATEGORY_PREFIX = "existscategory_";
    private static final String EXISTS_SUBCATEGORY_PREFIX = "existssubcategory_";
    private static final String SELECT_ITEM_PREFIX = "selectitem_";
    private static final String INSERT_ITEM_PREFIX = "insertitem_";
    private static final String UPDATE_ITEM_PREFIX = "updateitem_";
    private static final String EXISTS_ITEM_PREFIX = "existsitem_";
    private static final String SELECT_TICKET_PREFIX = "selectticket_";
    private static final String SELECT_SCALE_PREFIX = "selectscale_";
    private static final String GET_SCALE_CATEGORY_PREFIX = "getsubcategory_";

	/**
	 * Return the <code>Scale Model</code>
	 * 
	 * @param value
	 * @return ScaleModel
	 */
    public static ScaleModel get(String value) {
    	for( ScaleModel _enum : values() ) {
			if ( value.equals(_enum.name()) ) {
				return _enum;
			}
    	}
		return null;
    }	

    /*
     * (non-Javadoc)
     * @see com.code.aon.common.enumeration.IResourceable#getName(java.util.Locale)
     */
    public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
    }

    /*
     * (non-Javadoc)
     * @see com.code.aon.common.enumeration.IResourceable#getName(java.util.Locale)
     */
    public String getSelectCategory(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + SELECT_CATEGORY_PREFIX + toString());
    }

    /*
     * (non-Javadoc)
     * @see com.code.aon.common.enumeration.IResourceable#getName(java.util.Locale)
     */
    public String getSelectItem(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + SELECT_ITEM_PREFIX + toString());
    }

    /*
     * (non-Javadoc)
     * @see com.code.aon.common.enumeration.IResourceable#getName(java.util.Locale)
     */
    public String getSelectTicket(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + SELECT_TICKET_PREFIX + toString());
    }

    /*
     * (non-Javadoc)
     * @see com.code.aon.common.enumeration.IResourceable#getName(java.util.Locale)
     */
    public String getInsertCategory(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + INSERT_CATEGORY_PREFIX + toString());
    }

    /*
     * (non-Javadoc)
     * @see com.code.aon.common.enumeration.IResourceable#getName(java.util.Locale)
     */
    public String getMaxCategory(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + MAX_CATEGORY_PREFIX + toString());
    }

    /*
     * (non-Javadoc)
     * @see com.code.aon.common.enumeration.IResourceable#getName(java.util.Locale)
     */
    public String getInsertSubcategory(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + INSERT_SUBCATEGORY_PREFIX + toString());
    }

    /*
     * (non-Javadoc)
     * @see com.code.aon.common.enumeration.IResourceable#getName(java.util.Locale)
     */
    public String getMaxSubcategory(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + MAX_SUBCATEGORY_PREFIX + toString());
    }

    /*
     * (non-Javadoc)
     * @see com.code.aon.common.enumeration.IResourceable#getName(java.util.Locale)
     */
    public String getUpdateCategory(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + UPDATE_CATEGORY_PREFIX + toString());
    }

    /*
     * (non-Javadoc)
     * @see com.code.aon.common.enumeration.IResourceable#getName(java.util.Locale)
     */
    public String getUpdateSubcategory(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + UPDATE_SUBCATEGORY_PREFIX + toString());
    }

    /*
     * (non-Javadoc)
     * @see com.code.aon.common.enumeration.IResourceable#getName(java.util.Locale)
     */
    public String existsCategory(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + EXISTS_CATEGORY_PREFIX + toString());
    }


    /*
     * (non-Javadoc)
     * @see com.code.aon.common.enumeration.IResourceable#getName(java.util.Locale)
     */
    public String existsSubcategory(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + EXISTS_SUBCATEGORY_PREFIX + toString());
    }

    /*
     * (non-Javadoc)
     * @see com.code.aon.common.enumeration.IResourceable#getName(java.util.Locale)
     */
    public String getInsertItem(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + INSERT_ITEM_PREFIX + toString());
    }

    /*
     * (non-Javadoc)
     * @see com.code.aon.common.enumeration.IResourceable#getName(java.util.Locale)
     */
    public String getUpdateItem(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + UPDATE_ITEM_PREFIX + toString());
    }

    /*
     * (non-Javadoc)
     * @see com.code.aon.common.enumeration.IResourceable#getName(java.util.Locale)
     */
    public String existsItem(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + EXISTS_ITEM_PREFIX + toString());
    }

    /*
     * (non-Javadoc)
     * @see com.code.aon.common.enumeration.IResourceable#getName(java.util.Locale)
     */
    public String getSelectScale(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + SELECT_SCALE_PREFIX + toString());
    }

    /*
     * (non-Javadoc)
     * @see com.code.aon.common.enumeration.IResourceable#getName(java.util.Locale)
     */
    public String getGetScaleCategoryDescription(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + GET_SCALE_CATEGORY_PREFIX + toString());
    }

}