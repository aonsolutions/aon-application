package com.code.aon.ui.common.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.enumeration.Country;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.enumeration.Month;
import com.code.aon.common.enumeration.Province;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.ui.util.AonUtil;

/**
 * Controller used to get Collections related with clasess in <code>com.code.aon.common</code>.
 * 
 */
public class CommonCollections {
	
	private List<SelectItem> countries;  
	private List<SelectItem> countryCodes;
	private List<SelectItem> provinces;
	private List<SelectItem> months;  
	private List<SelectItem> levels;
	private List<SelectItem> confidentialValues;
	private List<SelectItem> mimeTypes;
	
	/**
     * Get year months.
     * 
     * @return the address types
     */
	public List<SelectItem> getMonths() {
    	if ( months == null ) {
    		Locale locale = AonUtil.getCurrentLocale();
    		months = new LinkedList<SelectItem>();
    		for( Month month : Month.values() ) {
	            String name = month.getName(locale); 
	            SelectItem item = new SelectItem(month, name);
	            months.add( item );
    		}
        }
        return months;
	}

	/**
	 * @return s
	 */
	public List<SelectItem> getCountries(){
    	if ( countries == null ) {
    		Locale locale = AonUtil.getCurrentLocale();
    		countries = new LinkedList<SelectItem>();
    		for( Country country : Country.values() ) {
	            String name = country.getName(locale); 
	            SelectItem item = new SelectItem(country, StringUtils.abbreviate(name,25));
	            countries.add( item );
    		}
        }
        return countries;
	}

	/**
	 * @return s
	 */
	public List<SelectItem> getCountryCodes() {
    	if ( countryCodes == null ) {
    		countryCodes = new LinkedList<SelectItem>();
    		for( Country country : Country.values() ) {
	            SelectItem item = new SelectItem(country, country.getValue());
	            countryCodes.add( item );
    		}
        }
        return countryCodes;
	}

	/**
	 * @return s
	 */
	public List<SelectItem> getProvinces(){
    	if ( provinces == null ) {
    		Locale locale = AonUtil.getCurrentLocale();
    		provinces = new LinkedList<SelectItem>();
    		for( Province province : Province.values() ) {
	            String name = province.getName(locale); 
	            SelectItem item = new SelectItem(province, StringUtils.abbreviate(name,25));
	            provinces.add( item );
    		}
        }
        return provinces;
	}

	/**
	 * @return List<SelectItem>
	 */
	public List<SelectItem> getSecurityLevels() {
    	if ( levels == null ) {
    		Locale locale = AonUtil.getCurrentLocale();
    		levels = new LinkedList<SelectItem>();
    		for( SecurityLevel level : SecurityLevel.values() ) {
	            String name = level.getName(locale); 
	            SelectItem item = new SelectItem(level, name);
	            levels.add( item );
    		}
        }
        return levels;
	}

	/**
	 * @return List<SelectItem>
	 */
	public List<SelectItem> getConfidentialValues() {
    	if ( confidentialValues == null ) {
			confidentialValues = new LinkedList<SelectItem>();
			SelectItem item = new SelectItem(SecurityLevel.CONFIDENTIAL, AonUtil.getMessage("aon_yes"));
			confidentialValues.add(item);
			item = new SelectItem(SecurityLevel.OFFICIAL, AonUtil.getMessage("aon_no"));
			confidentialValues.add(item);		
        }
        return confidentialValues;
	}
	
	public List<SelectItem> getMimeTypes() {
		if ( mimeTypes == null ) {
			Locale locale = AonUtil.getCurrentLocale();
			mimeTypes = new LinkedList<SelectItem>();
			for( MimeType mimeType : MimeType.values() ) {
				String name = mimeType.getName(locale);
				SelectItem item = new SelectItem(mimeType, name);
				mimeTypes.add(item);			
			}
		}
		return mimeTypes;
	}	
	
}
