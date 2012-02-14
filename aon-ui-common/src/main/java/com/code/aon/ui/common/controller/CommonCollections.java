package com.code.aon.ui.common.controller;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.enumeration.Country;
import com.code.aon.common.enumeration.Month;
import com.code.aon.common.enumeration.Province;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.ui.util.AonUtil;

/**
 * Controller used to get Collections related with clasess in <code>com.code.aon.common</code>.
 * 
 */
public class CommonCollections {
	
	private Map<Locale,List<SelectItem>> countries = new HashMap<Locale,List<SelectItem>>();  
	private Map<Locale,List<SelectItem>> countryCodes = new HashMap<Locale,List<SelectItem>>();
	private Map<Locale,List<SelectItem>> provinces = new HashMap<Locale,List<SelectItem>>();
	private Map<Locale,List<SelectItem>> months = new HashMap<Locale,List<SelectItem>>();  
	private Map<Locale,List<SelectItem>> levels = new HashMap<Locale,List<SelectItem>>();
	private Map<Locale,List<SelectItem>> confidentialValues = new HashMap<Locale,List<SelectItem>>();
	
	/**
     * Get year months.
     * 
     * @return the address types
     */
	public List<SelectItem> getMonths(){
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		if (months.get(locale) == null) {
			List<SelectItem> monthList = new LinkedList<SelectItem>();
			Month[] m = Month.values();
			for (int i = 0; i < m.length; i++) {
				Month month = m[i];
				String name = month.getName(locale);
				SelectItem item = new SelectItem(month, name);
				monthList.add(item);
			}
			months.put(locale,monthList);			
		}
		return months.get(locale);
	}

	/**
	 * @return s
	 */
	public List<SelectItem> getCountries(){
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		if (countries.get(locale) == null) {
			List<SelectItem> countryList = new LinkedList<SelectItem>();
			Country[] m = Country.values();
			for (int i = 0; i < m.length; i++) {
				Country country = m[i];
				String name = country.getName(locale);
				SelectItem item = new SelectItem(country, StringUtils.abbreviate(name,25));
				countryList.add(item);
			}
			countries.put(locale,countryList);			
		}
		return countries.get(locale);
	}

	/**
	 * @return s
	 */
	public List<SelectItem> getCountryCodes(){
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		if (countryCodes.get(locale) == null) {
			List<SelectItem> countryList = new LinkedList<SelectItem>();
			Country[] m = Country.values();
			for (int i = 0; i < m.length; i++) {
				Country country = m[i];
				SelectItem item = new SelectItem(country, country.getValue());
				countryList.add(item);
			}
			countryCodes.put(locale,countryList);			
		}
		return countryCodes.get(locale);
	}

	/**
	 * @return s
	 */
	public List<SelectItem> getProvinces(){
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		if (provinces.get(locale) == null) {
			List<SelectItem> provinceList = new LinkedList<SelectItem>();
			Province[] m = Province.values();
			for (int i = 0; i < m.length; i++) {
				Province province = m[i];
				String name = province.getName(locale);
				SelectItem item = new SelectItem(province, StringUtils.abbreviate(name,25));
				provinceList.add(item);
			}
			provinces.put(locale,provinceList);			
		}
		return provinces.get(locale);
	}

	/**
	 * @return List<SelectItem>
	 */
	public List<SelectItem> getSecurityLevels() {
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		if (levels.get(locale) == null) {
			List<SelectItem> levelList = new LinkedList<SelectItem>();
			for (SecurityLevel level : SecurityLevel.values()) {
				String name = level.getName(locale);
				SelectItem item = new SelectItem(level, name);
				levelList.add(item);
			}
			levels.put(locale,levelList);			
		}
		return levels.get(locale);
	}

	/**
	 * @return List<SelectItem>
	 */
	public List<SelectItem> getConfidentialValues() {
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		if (confidentialValues.get(locale) == null) {
			List<SelectItem> confidentialList = new LinkedList<SelectItem>();
			SelectItem item = new SelectItem(SecurityLevel.CONFIDENTIAL, AonUtil.getMessage("aon_yes"));
			confidentialList.add(item);
			item = new SelectItem(SecurityLevel.OFFICIAL, AonUtil.getMessage("aon_no"));
			confidentialList.add(item);
			confidentialValues.put(locale,confidentialList);			
		}
		return confidentialValues.get(locale);
	}
}
