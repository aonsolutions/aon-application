package com.code.aon.ui.common.controller;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.code.aon.common.enumeration.Month;
import com.code.aon.common.enumeration.SecurityLevel;

/**
 * Controller used to get Collections related with clasess in <code>com.code.aon.common</code>.
 * 
 */
public class CommonCollections {
	Map<Locale,List<SelectItem>> months = new HashMap<Locale,List<SelectItem>>();  
	Map<Locale,List<SelectItem>> levels = new HashMap<Locale,List<SelectItem>>();
	
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

}
