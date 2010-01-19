package com.code.aon.ui.cms.controller;

import java.util.List;

import javax.faces.event.ActionEvent;

import com.code.aon.cms.Sidebar;
import com.code.aon.cms.SidebarOption;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;

public class SidebarController extends BasicController {

	public void onSelectOptions(ActionEvent event) throws ManagerBeanException, ExpressionException {
		SidebarOptionController soc = (SidebarOptionController)FormUtil.getController("sidebar_option");
		IManagerBean soBean = BeanManager.getManagerBean(SidebarOption.class);
		Sidebar sidebar = (Sidebar) this.getTo();
		Criteria criteria = new Criteria();
		criteria.addExpression(soBean.getFieldName(ICMSAlias.SIDEBAR_OPTION_SIDEBAR_ID), "" + sidebar.getId());
		criteria.addOrder(soBean.getFieldName(ICMSAlias.SIDEBAR_OPTION_POSITION));
		soc.setCurrentSidebar(sidebar);
		soc.setCriteria(criteria);
		soc.onSearch(event);
	}

	public void defaultChanged(ActionEvent event) throws ManagerBeanException, ExpressionException {
		Sidebar sidebar = (Sidebar) model.getRowData();
		sidebar.setDefault_(true);
		updateDefault(sidebar);
	}
	
	@SuppressWarnings("unchecked")
	private void updateDefault(Sidebar defaultSidebar) throws ManagerBeanException, ExpressionException {
		IManagerBean bean = BeanManager.getManagerBean(Sidebar.class);
		List<ITransferObject> list = (List<ITransferObject>)model.getWrappedData();
		for (int i = 0; i < list.size(); i++) {
			Sidebar sidebar = (Sidebar)list.get(i);
			if (defaultSidebar != sidebar)
				sidebar.setDefault_(false);
			bean.update(sidebar);
		}
	}

}
