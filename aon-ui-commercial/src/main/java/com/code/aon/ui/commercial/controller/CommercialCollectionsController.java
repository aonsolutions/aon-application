package com.code.aon.ui.commercial.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.code.aon.commercial.enumeration.OfferDetailStatus;
import com.code.aon.commercial.enumeration.OfferStatus;
import com.code.aon.commercial.enumeration.OfferType;

/**
 * Controller used to get Collections related with clasess in <code>com.code.aon.commercial</code>
 * 
 * @author Consulting & Development. Joseba Urkiri - 6-sept-2006
 */
public class CommercialCollectionsController {

	/**
	 * Gets the offer statuses.
	 * 
	 * @return the offer statuses
	 */
	public List<SelectItem> getOfferStatuses() {
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		LinkedList<SelectItem> statuses = new LinkedList<SelectItem>();
		for (OfferStatus status : OfferStatus.values()) {
			String name = status.getName(locale);
			SelectItem item = new SelectItem(status, name);
			statuses.add(item);
		}
		return statuses;
	}
	
	/**
	 * Gets the offer types.
	 * 
	 * @return the offer types
	 */
	public List<SelectItem> getOfferTypes() {
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		LinkedList<SelectItem> types = new LinkedList<SelectItem>();
		for (OfferType type : OfferType.values()) {
			String name = type.getName(locale);
			SelectItem item = new SelectItem(type, name);
			types.add(item);
		}
		return types;
	}

	/**
	 * Gets the offer detail statuses.
	 * 
	 * @return the offer detail statuses
	 */
	public List<SelectItem> getOfferDetailStatuses() {
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		LinkedList<SelectItem> statuses = new LinkedList<SelectItem>();
		for (OfferDetailStatus status : OfferDetailStatus.values()) {
			String name = status.getName(locale);
			SelectItem item = new SelectItem(status, name);
			statuses.add(item);
		}
		return statuses;
	}
}