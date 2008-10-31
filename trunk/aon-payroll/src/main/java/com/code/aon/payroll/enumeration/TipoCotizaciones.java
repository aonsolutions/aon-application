package com.code.aon.payroll.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;
import com.code.aon.common.enumeration.IStringEnum;

/**
 * The Enum EnumCotizaciones.
 */
public enum TipoCotizaciones implements IResourceable, IStringEnum {

	COT1("1"),
	COT2("2"),
	COT3("3"),
	COT4("4"),
	COT5("5"),
	COT6("6"),
	COT7("7");
	
	private static final String BASE_NAME = "com.code.aon.payroll.i18n.messages";

	/** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_cotizaciones_";
	
    private String value;
    
    TipoCotizaciones( String value ) {
      	this.value = value;
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
    
    @Override
	public String getValue() {
		return value;
	}
}