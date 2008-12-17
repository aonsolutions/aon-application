package com.code.aon.ui.consultant.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.code.aon.common.enumeration.Month;

public class ConsultantCollectionsController {

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
}