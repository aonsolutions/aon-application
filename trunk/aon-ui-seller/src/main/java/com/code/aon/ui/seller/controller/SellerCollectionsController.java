package com.code.aon.ui.seller.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.code.aon.seller.enumeration.SellerStatus;

public class SellerCollectionsController {
	List<SelectItem> sellerStatuses;

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

}