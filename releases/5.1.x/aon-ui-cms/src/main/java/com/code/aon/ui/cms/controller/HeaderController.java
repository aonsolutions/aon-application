package com.code.aon.ui.cms.controller;

import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import com.code.aon.cms.Header;
import com.code.aon.cms.HeaderDetail;
import com.code.aon.cms.Image;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.cms.util.ControllerUtil;
import com.code.aon.ui.util.AonUtil;
import com.icesoft.faces.component.ext.RowSelectorEvent;


public class HeaderController extends BasicI18nController {

	private boolean cancelOnSelect = false;

	public void onInit(ActionEvent event){
		((GalleryController)AonUtil.getRegisteredBean("gallery")).onInit(event);
	}

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

	public void onAccept(ActionEvent event) {
		try {
			Header header = (Header)getTo();
			IManagerBean bean = BeanManager.getManagerBean(Header.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.HEADER_DEFAULT_), true);
			List<ITransferObject> list = (List<ITransferObject>)bean.getList(criteria);
			if (list.size() == 0) {
				header.setDefault_(true);
			}
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
		super.onAccept(event);
	}

	public void defaultChanged(ValueChangeEvent event) throws ManagerBeanException, ExpressionException {
		boolean selected = ((Boolean)event.getNewValue()).booleanValue();
		Header header = (Header) model.getRowData();
		if (selected) {
			header.setDefault_(true);
			updateDefault(header);
		}
		cancelOnSelect = true;
	}
	
	@SuppressWarnings("unchecked")
	private void updateDefault(Header defaultHeader) throws ManagerBeanException, ExpressionException {
		IManagerBean bean = BeanManager.getManagerBean(Header.class);
		List<ITransferObject> list = (List<ITransferObject>)model.getWrappedData();
		for (int i = 0; i < list.size(); i++) {
			Header header = (Header)list.get(i);
			if (defaultHeader != header)
				header.setDefault_(false);
			bean.update(header);
		}
	}

	private boolean imageSelectionVisible;
	
	public void onShowImages(ActionEvent event) {
		imageSelectionVisible = true; 
	}
	
	public void onCloseImages(ActionEvent event) {
		imageSelectionVisible = false; 
	}
	
	public boolean isImageSelectionVisible(){
		return imageSelectionVisible;
	}
	
	public void onDelImage(ActionEvent event) {
		HeaderDetail current = (HeaderDetail)getToI18n();
		current.setImage(null);
	}
	
	public void onSelectImage(ActionEvent event) throws ManagerBeanException {
		GalleryController controller = (GalleryController)AonUtil.getRegisteredBean("gallery");
		String image = ((Image)controller.getModel().getRowData()).getRelativePath();
		HeaderDetail current = (HeaderDetail)getToI18n();
		current.setImage(image);
	}

}