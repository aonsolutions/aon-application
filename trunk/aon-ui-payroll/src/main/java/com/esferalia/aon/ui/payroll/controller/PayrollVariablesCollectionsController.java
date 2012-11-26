package com.esferalia.aon.ui.payroll.controller;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;

import com.esferalia.aon.payroll.enumeration.CNO;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.esferalia.aon.payroll.enumeration.ContractType;
import com.esferalia.aon.payroll.enumeration.OccupationType;
import com.esferalia.aon.payroll.enumeration.QuoteGroup;

public class PayrollVariablesCollectionsController {

	private final int NAME_LENGHT_80 = 80;	
	
	public List<?> getTc2List() {
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		List<SelectItemGroup> list = new LinkedList<SelectItemGroup>();
		for( ContractType p : ContractType.values() ) {
			List<SelectItem> subList = new ArrayList<SelectItem>();
			for( ContractCode c : p.getCodes() ) {
				String name = getAbbreviatedSelectItemLabel(c.getName(locale));
				SelectItem item = new SelectItem(c, name);
				subList.add(item);			
			}
			SelectItemGroup group = new SelectItemGroup(getAbbreviatedSelectItemLabel(p.getName(locale)), p.getName(locale), false, subList.toArray(new SelectItem[0]));
			list.add(group);
		}
		return list;
	}
	
	public List<SelectItem> getCnoList() {
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		List<SelectItem> list = new LinkedList<SelectItem>();
		for( CNO p : CNO.values() ) {
			String name = p.getName(locale);
//			SelectItem item = new SelectItem(p, getFormattedSelectItemLabel(name), null, false, false);
			SelectItem item = new SelectItem(p, name);
			list.add(item);			
		}
		return list;
	}
	
	private String getFormattedSelectItemLabel(String name) {
		if(name.length()>20){
//			return name.substring(0, 80)+"\r\n &#13; "+name.substring(80, name.length());
			return name.substring(0, 20)+" &#13;&#10; "+name.substring(20, name.length());
		}
		return name;
	}

	private String getAbbreviatedSelectItemLabel(String name) {
		if(name.length()>NAME_LENGHT_80){
			return name.substring(0, NAME_LENGHT_80)+"...";
		}
		return name;
	}
	
	public List<SelectItem> getCategoryList() {
//		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		List<SelectItem> list = new LinkedList<SelectItem>();
//		for( DisabilityLevel p : DisabilityLevel.values() ) {
//			String name = p.getName(locale);
//			SelectItem item = new SelectItem(p, name);
//			list.add(item);			
//		}
		return list;
	}
	
	public List<SelectItem> getQuoteGroupList() {
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		List<SelectItem> list = new LinkedList<SelectItem>();
		for( QuoteGroup p : QuoteGroup.values() ) {
			String name = p.getName(locale);
//			SelectItem item = new SelectItem(p, getFormattedSelectItemLabel(name), null, false, false);
			SelectItem item = new SelectItem(p, getAbbreviatedSelectItemLabel(name));
			list.add(item);			
		}
		return list;
	}

	public List<SelectItem> getOccupationList() {
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		List<SelectItem> list = new LinkedList<SelectItem>();
		for( OccupationType p : OccupationType.values() ) {
			String name = p.getName(locale);
			SelectItem item = new SelectItem(p, getAbbreviatedSelectItemLabel(name));
			list.add(item);			
		}
		return list;
	}

	public List<SelectItem> getQuoteItList() {
//		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		List<SelectItem> list = new LinkedList<SelectItem>();
//		for( DisabilityLevel p : DisabilityLevel.values() ) {
//			String name = p.getName(locale);
//			SelectItem item = new SelectItem(p, name);
//			list.add(item);			
//		}
		return list;
	}
	
		
}