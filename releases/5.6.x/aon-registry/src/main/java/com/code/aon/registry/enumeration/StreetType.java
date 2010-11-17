package com.code.aon.registry.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

/**
 * Enummeration to identify the different street types.
 * 
 * @author Consulting & Development. Eugenio Castellano - 28-ene-2005
 * @version 1.0
 * @since 1.0
 */
public enum StreetType implements IResourceable {

    /** UNKNOWN. */
    UNKNOWN,

    /** AVENUE. */
    AVENUE,

    /** NEIGHBOURHOOD. */
    NEIGHBOURHOOD,

    /** DISTRICT. */
    DISTRICT,

    /** BLOCK. */
    BLOCK,

    /** STREET. */
    STREET,

    /** PATH. */
    PATH,

    /** COLONY. */
    COLONY,

    /** ROUTE. */
    ROUTE,

    /** BUILDING. */
    BUILDING,

    /** ROUNDABOUT. */
    ROUNDABOUT,

    /** PASSAGE. */
    PASSAGE,

    /** INDUSTRIAL PARK. */
    INDUSTRIAL_PARK,

    /** PARK. */
    PARK,

    /** WALK. */
    WALK,

    /** PLAZA. */
    PLAZA,

    /** BOULEVARD. */
    BOULEVARD,

    /** RESIDENTIAL. */
    RESIDENTIAL,

    /** ROAD. */
    ROAD,

    /** ESTATE. */
    ESTATE;

    /** Message file base path. */
    private static final String BASE_NAME = "com.code.aon.registry.i18n.messages";

    /** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_streettype_";

    /**
     * Returns a <code>String</code> with the transalation <code>Locale</code>
     * for the locale.
     * 
     * @param locale Required Locale.
     * 
     * @return String a <code>String</code>.
     */
    public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
    }
}