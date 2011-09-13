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
import com.esferalia.aon.payroll.enumeration.QuoteGroup;

public class PayrollVariablesCollectionsController {

	
	
	public List<?> getTc2List() {
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		List<SelectItemGroup> list = new LinkedList<SelectItemGroup>();
		for( ContractType p : ContractType.values() ) {
			List<SelectItem> subList = new ArrayList<SelectItem>();
			for( ContractCode c : p.getCodes() ) {
				String name = c.getName(locale);
				SelectItem item = new SelectItem(c, name);
				subList.add(item);			
			}
			SelectItemGroup group = new SelectItemGroup(p.getName(locale), p.getName(locale), false, subList.toArray(new SelectItem[0]));
			list.add(group);
		}
		return list;
	}
	
	public List<SelectItem> getCnoList() {
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		List<SelectItem> list = new LinkedList<SelectItem>();
		for( CNO p : CNO.values() ) {
			String name = p.getName(locale);
			SelectItem item = new SelectItem(p, name);
			list.add(item);			
		}
		return list;
	}
	
	public List<SelectItem> getCategoryList() {
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
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
			SelectItem item = new SelectItem(p, name);
			list.add(item);			
		}
		return list;
	}
	
		
}