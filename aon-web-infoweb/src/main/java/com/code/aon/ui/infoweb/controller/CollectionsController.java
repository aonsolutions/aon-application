package com.code.aon.ui.infoweb.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.infoweb.enumeration.WebInfoLayoutType;
import com.code.aon.infoweb.enumeration.WebInfoPageType;

public class CollectionsController {

	public List<SelectItem> getPageTypes() throws ManagerBeanException {
		List<SelectItem> types = new LinkedList<SelectItem>();
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		SelectItem item;;
		for (WebInfoPageType type : WebInfoPageType.values()) {
			String name = type.getName(locale);
			item = new SelectItem(type, name);
			types.add(item);
		}
		return types;
	}

	public List<SelectItem> getLayoutTypes() throws ManagerBeanException {
		List<SelectItem> types = new LinkedList<SelectItem>();
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		SelectItem item;;
		for (WebInfoLayoutType type : WebInfoLayoutType.values()) {
			String name = type.getName(locale);
			item = new SelectItem(type, name);
			types.add(item);
		}
		return types;
	}

}
