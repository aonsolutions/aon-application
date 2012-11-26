package com.code.aon.ui.admin.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.model.SelectItem;

import com.code.aon.config.Domain;
import com.code.aon.config.enumeration.DomainType;
import com.code.aon.ui.util.AonUtil;

public class AdminCollectionsController {

	private List<SelectItem> domainTypes;
	

	public List<SelectItem> getDomainTypes() {
		if ( domainTypes == null ) {
			Locale locale = AonUtil.getCurrentLocale();
			domainTypes = new LinkedList<SelectItem>();
			for (DomainType type : DomainType.values()) {
				String name = type.getName(locale);
				SelectItem item = new SelectItem(type, name);
				domainTypes.add(item);
			}		
		}
		return domainTypes;
	}	

	public Domain getDomain() {
		return null;
	}

	public void setDomain(Domain domain) {
	}
	
}