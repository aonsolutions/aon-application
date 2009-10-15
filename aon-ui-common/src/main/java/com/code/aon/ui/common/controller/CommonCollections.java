package com.code.aon.ui.common.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.code.aon.common.enumeration.Month;
import com.code.aon.common.enumeration.SecurityLevel;

/**
 * Controller used to get Collections related with clasess in <code>com.code.aon.common</code>.
 * 
 */
public class CommonCollections {

	/**
     * Get year months.
     * 
     * @return the address types
     */
	public List<SelectItem> getMonths(){
		List<SelectItem> monthList = new LinkedList<SelectItem>();
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		Month[] months = Month.values();
		for (int i = 0; i < months.length; i++) {
			Month month = months[i];
			String name = month.getName(locale);
			SelectItem item = new SelectItem(month, name);
			monthList.add(item);
		}
		return monthList;
	}

	/**
	 * @return List<SelectItem>
	 */
	public List<SelectItem> getSecurityLevels() {
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		LinkedList<SelectItem> levels = new LinkedList<SelectItem>();
		for (SecurityLevel level : SecurityLevel.values()) {
			String name = level.getName(locale);
			SelectItem item = new SelectItem(level, name);
			levels.add(item);
		}
		return levels;
	}

}
