package com.code.aon.ui.sales.controller;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.code.aon.AonVersion;
import com.code.aon.sales.enumeration.DocumentType;
import com.code.aon.sales.enumeration.SalesStatus;

public class SalesCollectionsController implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private List<SelectItem> salesStatuses;
	private List<SelectItem> documentTypes;

	public List<SelectItem> getSalesStatuses() {
		if (salesStatuses == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			salesStatuses = new LinkedList<>();
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
			documentTypes = new LinkedList<>();
			for (DocumentType type : DocumentType.values()) {
				String name = type.getName(locale);
				SelectItem item = new SelectItem(type, name);
				documentTypes.add(item);
			}
		}
		return documentTypes;
	}
	
	public List<SelectItem> getBasicSalesTypes() {
		List<SelectItem> list = new LinkedList<>();
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		SelectItem item = new SelectItem(DocumentType.NORMAL, DocumentType.NORMAL.getName(locale));
		list.add(item);
		item = new SelectItem(DocumentType.ITEM_RETURN, DocumentType.ITEM_RETURN.getName(locale));
		list.add(item);
		return list;
	}
}