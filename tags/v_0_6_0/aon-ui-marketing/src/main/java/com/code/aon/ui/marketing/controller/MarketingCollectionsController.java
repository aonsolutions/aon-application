package com.code.aon.ui.marketing.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.code.aon.marketing.enumeration.QuestionType;

public class MarketingCollectionsController {

	private List<SelectItem> questionTypes;
	
	/**
	 * Gets the audit levels.
	 * 
	 * @return the audit levels
	 */
	public List<SelectItem> getQuestionTypes() {
		if ( questionTypes == null ) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			questionTypes = new LinkedList<SelectItem>();
			for (QuestionType auditLevel : QuestionType.values()) {
				String name = auditLevel.getName(locale);
				SelectItem item = new SelectItem(auditLevel, name);
				questionTypes.add(item);
			}
		}
		return questionTypes;
	}	
		
}
