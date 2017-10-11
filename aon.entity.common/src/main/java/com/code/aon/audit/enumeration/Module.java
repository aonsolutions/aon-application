package com.code.aon.audit.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;


/**
 * Enummeration to identify the modules of an application.
 * 
 * @author esferalia Networks. Aimar Tellitu - 28-abr-2012
 * @since 1.0
 * @version 1.0
 */
public enum Module implements IResourceable {

	/** MARKETING. */
	MARKETING( "marketing" ),
    
	/** CRM. */
	CRM( "crm" ),
    
    /** MANAGEMENT. */
	MANAGEMENT( "management" ),
    
    /** TREASURY. */
	TREASURY( "treasury" ),

    /** WAREHOUSE. */
	WAREHOUSE( "warehouse" ),

    /** GROUPWARE. */
	GROUPWARE( "groupware" ),

    /** ACCOUNTING. */
	ACCOUNTING( "accounting" ),

    /** FISCAL. */
	FISCAL( "fiscal" ),

    /** PAYROLL. */
	PAYROLL( "payroll" ),

    /** DOCUMENT. */
	DOCUMENT( "document" ),
	
    /** GARAGE. */
	GARAGE( "garage" ),
	
    /** ACADEMY. */
	ACADEMY( "academy" ),

    /** HOTEL. */
	HOTEL( "hotel" ),
	
    /** INFOWEB. */
	INFOWEB( "infoweb" ),
	
    /** PAYROLL_PORTAL. */
	PAYROLL_PORTAL( "payroll_portal" ),

    /** DOCUMENT_PORTAL. */
	DOCUMENT_PORTAL( "document_portal" ),
	
    /** POS. */
	POS( "pos" ),
	
    /** CONTRAT@. */
	CONTRATA( "contrata" ),

    /** CONFIGURATION. */
	CONFIGURATION( "configuration" ),

    /** AON_ONE. */
	AON_ONE( "aonOne" ),

    /** ECOMMERCE. */
	ECOMMERCE( "eCommerce" ),
	
    /** CALL_CENTER. */
	CALL_CENTER( "call_center" );
	
    /** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_module_";
    
	private String name;
	
	private Module(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

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
	

    /**
     * Return the Module.
     * 
     * @param name Module name.
     * @return The Module.
     */
	public static Module get(String name) {
    	for( Module module : Module.values() ) {
    		if ( module.getName().equals(name) ) {
    			return module;
    		}
    	}
    	return null;
	}
    
}