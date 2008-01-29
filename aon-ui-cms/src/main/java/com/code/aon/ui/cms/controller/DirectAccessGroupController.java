package com.code.aon.ui.cms.controller;

import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import com.code.aon.cms.DirectAccess;
import com.code.aon.cms.DirectAccessGroup;
import com.code.aon.cms.DirectAccessGroupDetail;
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


public class DirectAccessGroupController extends BasicI18nController {

	private boolean cancelOnSelect = false;

	@SuppressWarnings("unused")
	public void onSelect(RowSelectorEvent event) throws ManagerBeanException {
		if (!cancelOnSelect) {
			super.onSelect(new ActionEvent(event.getComponent()));
			FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, "direct_access_group_form");
			loadCurrentLanguage();
		}
		cancelOnSelect = false;
	}

	public void onActivate(ActionEvent event) throws ManagerBeanException {
		cancelOnSelect = true; 
		activate(true);
	}

	public void onDeactivate(ActionEvent event) throws ManagerBeanException {
		cancelOnSelect = true; 
		activate(false);
	}
	
	private void activate(boolean active) throws ManagerBeanException {
		DirectAccessGroup directAccessGroup = (DirectAccessGroup)this.model.getRowData();
		directAccessGroup.setActive(active);
		getManagerBean().update(directAccessGroup);
	}
	
	public void onChecked(ValueChangeEvent event) throws ManagerBeanException {
		cancelOnSelect = true; 
	}

	public String getI18nLabel() throws ManagerBeanException {
		String label = "";
		DirectAccessGroupDetail directAccessGroupDetail = getCurrentDetail();
		if (directAccessGroupDetail != null) label = directAccessGroupDetail.getLabel();
		return label;
	}

	private DirectAccessGroupDetail getCurrentDetail() throws ManagerBeanException {
		DirectAccessGroupDetail directAccessGroupDetail = null;
		DirectAccessGroup directAccessGroup = (DirectAccessGroup)this.model.getRowData();
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(getManagerBeanI18n().getFieldName(ICMSAlias.DIRECT_ACCESS_GROUP_DETAIL_DIRECT_ACCESS_GROUP_ID), directAccessGroup.getId());
		criteria.addEqualExpression(getManagerBeanI18n().getFieldName(ICMSAlias.DIRECT_ACCESS_GROUP_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
		List<ITransferObject> list = (List<ITransferObject>)getManagerBeanI18n().getList(criteria);
		if (list.size() > 0) {
			directAccessGroupDetail = (DirectAccessGroupDetail)list.get(0);
		}
		return directAccessGroupDetail;
	}

	public void onAccept(ActionEvent event) {
		super.accept(event);
	}

	public void onSelectLinks(ActionEvent event) throws ManagerBeanException, ExpressionException {
		cancelOnSelect = true;
		DirectAccessController dac = (DirectAccessController)AonUtil.getController("direct_access");
		IManagerBean moBean = BeanManager.getManagerBean(DirectAccess.class);
		DirectAccessGroup directAccessGroup = (DirectAccessGroup) this.getSelectedTO();
		Criteria criteria = new Criteria();
		criteria.addExpression(moBean.getFieldName(ICMSAlias.DIRECT_ACCESS_DIRECT_ACCESS_GROUP_ID), "" + directAccessGroup.getId());
		criteria.addOrder(moBean.getFieldName(ICMSAlias.DIRECT_ACCESS_POSITION));
		dac.setCurrentGroup(directAccessGroup);
		dac.setCriteria(criteria);
		dac.onSearch(event);
		dac.onInit(event);
	}

}