package com.code.aon.ui.cms.controller;

import java.util.List;

import javax.faces.event.ActionEvent;

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


public class HeaderController extends BasicI18nController implements ICMSConstants {

	@SuppressWarnings("unused")
	public void onSelect(ActionEvent event) {
		super.onSelect(event);
		loadCurrentLanguage();
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

	public void defaultChanged(ActionEvent event) throws ManagerBeanException, ExpressionException {
		Header header = (Header) model.getRowData();
		header.setDefault_(true);
		updateDefault(header);
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

	public void onDelImage(ActionEvent event) {
		HeaderDetail current = (HeaderDetail)getToI18n();
		current.setImage(null);
	}
	
	public void onSelectImage(ActionEvent event) throws ManagerBeanException {
		GalleryController controller = (GalleryController)AonUtil.getRegisteredBean(GALLERY);
		String image = ((Image)controller.getModel().getRowData()).getRelativePath();
		HeaderDetail current = (HeaderDetail)getToI18n();
		current.setImage(image);
	}
	
}