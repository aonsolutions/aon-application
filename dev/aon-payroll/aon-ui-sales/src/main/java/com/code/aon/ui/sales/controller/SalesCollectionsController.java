package com.code.aon.ui.sales.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.code.aon.sales.enumeration.DocumentType;
import com.code.aon.sales.enumeration.SalesStatus;
import com.code.aon.seller.enumeration.SellerStatus;

/**
 * Controller used to get Collections related with clasess in
 * <code>com.code.aon.sales</code>
 * 
 * @author Consulting & Development. igayarre - 22-jun-2006
 */
public class SalesCollectionsController {
	List<SelectItem> sellerStatuses;
	List<SelectItem> salesStatuses;
	List<SelectItem> documentTypes;

	public List<SelectItem> getSellerStatuses() {
		if (sellerStatuses == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			sellerStatuses = new LinkedList<SelectItem>();
			for (SellerStatus status : SellerStatus.values()) {
				String name = status.getName(locale);
				SelectItem item = new SelectItem(status, name);
				sellerStatuses.add(item);
			}
		}
		return sellerStatuses;
	}

	public List<SelectItem> getSalesStatuses() {
		if (salesStatuses == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			salesStatuses = new LinkedList<SelectItem>();
			for (SalesStatus status : SalesStatus.values()) {
				String name = status.getName(locale);
				SelectItem item = new SelectItem(status, name);
				salesStatuses.add(item);
			}
		}
		return salesStatuses;
	}

	public List<SelectItem> getDocumentTypes() {
		if (documentTypes == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			documentTypes = new LinkedList<SelectItem>();
			for (DocumentType type : DocumentType.values()) {
				String name = type.getName(locale);
				SelectItem item = new SelectItem(type, name);
				documentTypes.add(item);
			}
		}
		return documentTypes;
	}

}