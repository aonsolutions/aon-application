package com.code.aon.ui.admin.controller;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.model.SelectItem;

import com.code.aon.AonVersion;
import com.code.aon.config.Domain;
import com.code.aon.config.enumeration.DomainType;
import com.code.aon.ui.util.AonUtil;

public class AdminCollectionsController implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private List<SelectItem> domainTypes;
	private List<SelectItem> newDomainTypes;

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

	public List<SelectItem> getNewDomainTypes() {
		if ( newDomainTypes == null ) {
			Locale locale = AonUtil.getCurrentLocale();
			newDomainTypes = new LinkedList<SelectItem>();
			for (DomainType type : DomainType.values()) {
				if ( type != DomainType.ADMIN ) {
					String name = type.getName(locale);
					SelectItem item = new SelectItem(type, name);
					newDomainTypes.add(item);					
				}
			}		
		}
		return newDomainTypes;
	}	
	
	public Domain getDomain() {
		return null;
	}

	public void setDomain(Domain domain) {
	}
	
}