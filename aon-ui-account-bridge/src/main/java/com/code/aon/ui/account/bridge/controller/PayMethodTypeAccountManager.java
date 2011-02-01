package com.code.aon.ui.account.bridge.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.code.aon.config.enumeration.PayMethodType;
import com.code.aon.ui.form.LinesController;

public class PayMethodTypeAccountManager extends LinesController {

	private List<SelectItem> payMethodTypes;

	public List<SelectItem> getPayMethodTypes() {
		if ( payMethodTypes == null ) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			payMethodTypes = new LinkedList<SelectItem>();
			String name = PayMethodType.CASH_BASIS.getName(locale);
			SelectItem item = new SelectItem(PayMethodType.CASH_BASIS, name);
			payMethodTypes.add(item);
			name = PayMethodType.OTHER.getName(locale);
			item = new SelectItem(PayMethodType.OTHER, name);
			payMethodTypes.add(item);
		}
		return payMethodTypes;
	}

}
