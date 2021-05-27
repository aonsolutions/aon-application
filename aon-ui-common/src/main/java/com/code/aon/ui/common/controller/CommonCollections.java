package com.code.aon.ui.common.controller;

import static com.code.aon.ui.common.ICommonMessages.NO;
import static com.code.aon.ui.common.ICommonMessages.YES;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.enumeration.CSSUnit;
import com.code.aon.common.enumeration.Country;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.enumeration.Month;
import com.code.aon.common.enumeration.Province;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.common.enumeration.WeekDay;
import com.code.aon.ui.util.AonUtil;

/**
 * Controller used to get Collections related with clasess in <code>com.code.aon.common</code>.
 * 
 */
public class CommonCollections implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private List<SelectItem> countries;  
	private List<SelectItem> europeanUnionCountries;
	private List<SelectItem> countryCodes;
	private List<SelectItem> provinces;
	private List<SelectItem> months;  
	private List<SelectItem> levels;
	private List<SelectItem> confidentialValues;
	private List<SelectItem> mimeTypes;
	private List<SelectItem> weekDays;
	private List<SelectItem> pageLimits;
	private List<SelectItem> cssUnits;
	
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

	public List<SelectItem> getEuropeanUnionCountries(){
    	if ( europeanUnionCountries == null ) {
    		Locale locale = AonUtil.getCurrentLocale();
    		europeanUnionCountries = new LinkedList<SelectItem>();
    		Country[] europeanUnion = new Country[] {
    				Country.DE,Country.AT,Country.BE,Country.BG,Country.HR,
    				Country.CY,Country.DK,Country.SI,Country.EE,Country.FI,
    				Country.FR,Country.GR,Country.GB,Country.NL,Country.HU,
    				Country.IT,Country.IE,Country.LV,Country.LT,Country.LU,
    				Country.MT,Country.PL,Country.PT,Country.CZ,Country.SK,
    				Country.RO,Country.SE
    		};
    		for( Country country : europeanUnion ) {
	            String name = country.getName(locale); 
	            SelectItem item = new SelectItem(country, StringUtils.abbreviate(name,25));
	            europeanUnionCountries.add( item );
    		}
        }
        return europeanUnionCountries;
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
			SelectItem item = new SelectItem(SecurityLevel.CONFIDENTIAL, AonUtil.getMessage(YES));
			confidentialValues.add(item);
			item = new SelectItem(SecurityLevel.OFFICIAL, AonUtil.getMessage(NO));
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

	public List<SelectItem> getWeekDays() {
		if ( weekDays == null ) {
			Locale locale = AonUtil.getCurrentLocale();
			weekDays = new LinkedList<SelectItem>();
			for( WeekDay weekDay : WeekDay.values() ) {
				String name = weekDay.getName(locale);
				SelectItem item = new SelectItem(weekDay, name);
				weekDays.add(item);			
			}
		}
		return weekDays;
	}	

	/**
     * Get page limits.
     * 
     * @return the page limits
     */
	public List<SelectItem> getPageLimits() {
    	if ( pageLimits == null ) {
    		pageLimits = new LinkedList<SelectItem>();
   			pageLimits.add( new SelectItem(10, "10") );
   			pageLimits.add( new SelectItem(15, "15") );
   			pageLimits.add( new SelectItem(20, "20") );
   			pageLimits.add( new SelectItem(25, "25") );
   			pageLimits.add( new SelectItem(50, "50") );
   			pageLimits.add( new SelectItem(100, "100") );
        }
        return pageLimits;
	}

	public List<SelectItem> getCssUnits() {
		if ( cssUnits == null ) {
			Locale locale = AonUtil.getCurrentLocale();
			cssUnits = new LinkedList<SelectItem>();
			for( CSSUnit cssUnit : CSSUnit.values() ) {
				String name = cssUnit.getName(locale);
				SelectItem item = new SelectItem(cssUnit, name);
				cssUnits.add(item);			
			}
		}
		return cssUnits;
	}	
	
}
