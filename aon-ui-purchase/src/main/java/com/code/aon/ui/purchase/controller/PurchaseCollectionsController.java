package com.code.aon.ui.purchase.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.code.aon.purchase.enumeration.PurchaseDocumentType;
import com.code.aon.purchase.enumeration.PurchaseStatus;


/**
 * Controller used to get Collections related with clasess in 
 * <code>com.code.aon.purchase</code>
 * 
 * @author Consulting & Development. Joseba Urkiri - 21-dic-2005
 */
public class PurchaseCollectionsController {
	List<SelectItem> purchaseStatuses;
	List<SelectItem> documentTypes;

	public List<SelectItem> getPurchaseStatuses() {
		if (purchaseStatuses == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			purchaseStatuses = new LinkedList<SelectItem>();
			for (PurchaseStatus status : PurchaseStatus.values()) {
				String name = status.getName(locale);
				SelectItem item = new SelectItem(status, name);
				purchaseStatuses.add(item);
			}
		}
		return purchaseStatuses;
	}

	public List<SelectItem> getDocumentTypes() {
		if (documentTypes == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			documentTypes = new LinkedList<SelectItem>();
			for (PurchaseDocumentType type : PurchaseDocumentType.values()) {
				String name = type.getName(locale);
				SelectItem item = new SelectItem(type, name);
				documentTypes.add(item);
			}
		}
		return documentTypes;
	}

}