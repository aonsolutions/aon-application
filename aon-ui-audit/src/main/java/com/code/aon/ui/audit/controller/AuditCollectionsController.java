package com.code.aon.ui.audit.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.code.aon.audit.enumeration.AuditLevel;

public class AuditCollectionsController {

	private List<SelectItem> auditLevels;
	
	/**
	 * Gets the audit levels.
	 * 
	 * @return the audit levels
	 */
	public List<SelectItem> getAuditLevels() {
		if ( auditLevels == null ) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			auditLevels = new LinkedList<SelectItem>();
			for (AuditLevel auditLevel : AuditLevel.values()) {
				String name = auditLevel.getName(locale);
				SelectItem item = new SelectItem(auditLevel, name);
				auditLevels.add(item);
			}
		}
		return auditLevels;
	}	
	
}
