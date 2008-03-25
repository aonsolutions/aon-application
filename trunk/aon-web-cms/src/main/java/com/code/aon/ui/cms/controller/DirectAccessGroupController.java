package com.code.aon.ui.cms.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.cms.DirectAccess;
import com.code.aon.cms.DirectAccessGroup;
import com.code.aon.cms.DirectAccessGroupDetail;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.util.AonUtil;


public class DirectAccessGroupController extends BasicI18nController {

	@SuppressWarnings("unused")
	public void onSelect(ActionEvent event) {
		super.onSelect(event);
		loadCurrentLanguage();
	}

	public void onActivate(ActionEvent event) throws ManagerBeanException {
		activate(true);
	}

	public void onDeactivate(ActionEvent event) throws ManagerBeanException {
		activate(false);
	}
	
	private void activate(boolean active) throws ManagerBeanException {
		DirectAccessGroup directAccessGroup = (DirectAccessGroup)this.model.getRowData();
		directAccessGroup.setActive(active);
		getManagerBean().update(directAccessGroup);
	}
	
	public String getI18nLabel() throws ManagerBeanException {
		String label = "- NO VALUE -";
		DirectAccessGroupDetail directAccessGroupDetail = (DirectAccessGroupDetail)getModelRowdataI18n();
		if (directAccessGroupDetail != null) label = directAccessGroupDetail.getLabel();
		return label;
	}

	public void onAccept(ActionEvent event) {
		super.accept(event);
	}

	public void onSelectDirectAccesses(ActionEvent event) throws ManagerBeanException, ExpressionException {
		DirectAccessController dac = (DirectAccessController)AonUtil.getController("direct_access");
		IManagerBean moBean = BeanManager.getManagerBean(DirectAccess.class);
		DirectAccessGroup directAccessGroup = (DirectAccessGroup) this.getTo();
		Criteria criteria = new Criteria();
		criteria.addExpression(moBean.getFieldName(ICMSAlias.DIRECT_ACCESS_DIRECT_ACCESS_GROUP_ID), "" + directAccessGroup.getId());
		criteria.addOrder(moBean.getFieldName(ICMSAlias.DIRECT_ACCESS_POSITION));
		dac.setCurrentGroup(directAccessGroup);
		dac.setCriteria(criteria);
		dac.onSearch(event);
	}

}