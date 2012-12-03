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
import com.esferalia.aon.payroll.enumeration.ContractModel;
import com.esferalia.aon.payroll.enumeration.ContractModelCode;
import com.esferalia.aon.payroll.enumeration.ContractType;
import com.esferalia.aon.payroll.enumeration.OccupationType;
import com.esferalia.aon.payroll.enumeration.QuoteGroup;

public class PayrollVariablesCollectionsController {

	private final int NAME_LENGHT_80 = 80;	
	private final int NAME_LENGHT_100 = 100;	
	
	public List<?> getTc2List() {
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		List<SelectItemGroup> list = new LinkedList<SelectItemGroup>();
		for( ContractType type : ContractType.values() ) {
			if(type.getModel()==ContractModel.PE151
					|| type.getModel()==ContractModel.PE170
					|| type.getModel()==ContractModel.PE176
					|| type.getModel()==ContractModel.PE177
					|| type.getModel()==ContractModel.PE179
					|| type.getModel()==ContractModel.PE183
					|| type.getModel()==ContractModel.PE187
					|| type.getModel()==ContractModel.PE226){
				List<SelectItem> subList = new ArrayList<SelectItem>();
				for( ContractModelCode o : ContractModelCode.values() ) {
					if ( o.getModel() == type.getModel() ) {
						String name = getAbbreviatedSelectItemLabel(o.getCode().getName(locale), NAME_LENGHT_80);
						SelectItem item = new SelectItem(o, name);
						subList.add(item);
					}
				}
				SelectItemGroup group = new SelectItemGroup(getAbbreviatedSelectItemLabel(type.getName(locale), NAME_LENGHT_100), type.getName(locale), false, subList.toArray(new SelectItem[0]));
				group.setValue(type);
				list.add(group);
			}
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

	private String getAbbreviatedSelectItemLabel(String name, int lenght) {
		if(name.length()>lenght){
			return name.substring(0, lenght)+"...";
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
			SelectItem item = new SelectItem(p, getAbbreviatedSelectItemLabel(name, NAME_LENGHT_80));
			list.add(item);			
		}
		return list;
	}

	public List<SelectItem> getOccupationList() {
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		List<SelectItem> list = new LinkedList<SelectItem>();
		for( OccupationType p : OccupationType.values() ) {
			String name = p.getName(locale);
			SelectItem item = new SelectItem(p, getAbbreviatedSelectItemLabel(name, NAME_LENGHT_80));
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