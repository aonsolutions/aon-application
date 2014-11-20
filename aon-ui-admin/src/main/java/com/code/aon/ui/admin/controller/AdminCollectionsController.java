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
	
	private static final DomainType[] VALID_CHILD_DOMAIN_TYPES = {
		DomainType.ENTERPRISE, DomainType.ACADEMY, DomainType.GARAGE
	};

	private List<SelectItem> domainTypes;
	private List<SelectItem> newDomainTypes;
	private List<SelectItem> newChildDomainTypes;

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

	public List<SelectItem> getNewChildDomainTypes() {
		if ( newChildDomainTypes == null ) {
			Locale locale = AonUtil.getCurrentLocale();
			newChildDomainTypes = new LinkedList<SelectItem>();
			for (DomainType type : VALID_CHILD_DOMAIN_TYPES) {
				String name = type.getName(locale);
				SelectItem item = new SelectItem(type, name);
				newChildDomainTypes.add(item);					
			}		
		}
		return newChildDomainTypes;
	}	
	
	public Domain getDomain() {
		return null;
	}

	public void setDomain(Domain domain) {
	}
	
}