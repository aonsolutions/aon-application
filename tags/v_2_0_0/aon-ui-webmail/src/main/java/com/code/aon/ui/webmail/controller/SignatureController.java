package com.code.aon.ui.webmail.controller;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.webmail.bean.AonConstants;
import com.code.aon.webmail.enumeration.SignatureType;
import com.icesoft.faces.component.ext.RowSelectorEvent;

public class SignatureController extends BasicController {

	@SuppressWarnings("unused")
	public void onSelect(RowSelectorEvent event) throws ManagerBeanException {
		super.onSelect(new ActionEvent(event.getComponent()));
		FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, AonConstants.NAVIGATION_SIGNATURE_FORM);
	}

	@SuppressWarnings("unchecked")
	public List<SelectItem> getActiveTypes() throws ManagerBeanException{
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		List<SelectItem> types = new LinkedList<SelectItem>();
		for(SignatureType type_ : SignatureType.values()){
			SelectItem item = new SelectItem(type_, type_.getName(locale));
			types.add(item);
		}
		return types;
	}

}
