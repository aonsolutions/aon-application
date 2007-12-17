package com.code.aon.ui.cms.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import com.code.aon.cms.Header;
import com.code.aon.cms.HeaderDetail;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.cms.enumeration.ArticleType;
import com.code.aon.cms.enumeration.LanguageMenuType;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.cms.util.ControllerUtil;
import com.icesoft.faces.component.ext.RowSelectorEvent;


public class HeaderController extends BasicI18nController {

	private boolean cancelOnSelect = false;

	@SuppressWarnings("unused")
	public void onSelect(RowSelectorEvent event) throws ManagerBeanException {
		if (!cancelOnSelect) {
			super.onSelect(new ActionEvent(event.getComponent()));
			FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, "header_form");
			loadCurrentLanguage();
		}
		cancelOnSelect = false;
	}
	
	public void onChecked(ValueChangeEvent event) throws ManagerBeanException {
		cancelOnSelect = true; 
	}

	private HeaderDetail getHeaderDetail() throws ManagerBeanException{
		Header h = (Header)this.model.getRowData();
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(getManagerBeanI18n().getFieldName(ICMSAlias.HEADER_DETAIL_HEADER_ID), h.getId());
		criteria.addEqualExpression(getManagerBeanI18n().getFieldName(ICMSAlias.HEADER_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
		List<ITransferObject> list = (List<ITransferObject>)getManagerBeanI18n().getList(criteria);
		if (list.size() > 0) {
			return (HeaderDetail)list.get(0);
		}
		return null;
	}
	
	public String getI18nSitename() throws ManagerBeanException {
		HeaderDetail hd = getHeaderDetail();
		if (hd != null) {
			return hd.getSitename();
		}
		return "";
	}
	
	public String getI18nImage() throws ManagerBeanException {
		HeaderDetail hd = getHeaderDetail();
		if (hd != null) {
			return hd.getImage();
		}
		return "";
	}

	public String getI18nContent() throws ManagerBeanException {
		HeaderDetail hd = getHeaderDetail();
		if (hd != null) {
			return hd.getContent();
		}
		return "";
	}
	
	public List<SelectItem> getLanguageTypes() throws ManagerBeanException {
		List<SelectItem> types = new LinkedList<SelectItem>();
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		SelectItem item;;
		for (LanguageMenuType type : LanguageMenuType.values()) {
			String name = type.getName(locale);
			item = new SelectItem(type, name);
			types.add(item);
		}
		return types;
	}

}