package com.code.aon.ui.infoweb.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.model.SelectItem;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.infoweb.enumeration.WebInfoLayoutType;
import com.code.aon.infoweb.enumeration.WebInfoPageType;
import com.code.aon.ui.util.AonUtil;

public class CollectionsController {
	
	private List<SelectItem> pageTypes;
	
	private List<SelectItem> layoutTypes;

	public List<SelectItem> getPageTypes() throws ManagerBeanException {
		if ( pageTypes == null ) {
			Locale locale = AonUtil.getCurrentLocale();
			pageTypes = new LinkedList<SelectItem>();
			for (WebInfoPageType type : WebInfoPageType.values()) {
				String name = type.getName(locale);
				SelectItem item = new SelectItem(type, name);
				pageTypes.add(item);
			}
		}
		return pageTypes;
	}

	public List<SelectItem> getLayoutTypes() throws ManagerBeanException {
		if ( layoutTypes == null ) {
			Locale locale = AonUtil.getCurrentLocale();
			layoutTypes = new LinkedList<SelectItem>();
			for (WebInfoLayoutType type : WebInfoLayoutType.values()) {
				String name = type.getName(locale);
				SelectItem item = new SelectItem(type, name);
				layoutTypes.add(item);
			}
		}
		return layoutTypes;
	}

}
