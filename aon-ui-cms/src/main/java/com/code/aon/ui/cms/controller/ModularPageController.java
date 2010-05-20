package com.code.aon.ui.cms.controller;

import java.util.List;

import javax.faces.event.ActionEvent;

import com.code.aon.cms.ModularPage;
import com.code.aon.cms.ModularPageDetail;
import com.code.aon.cms.ModularPageOption;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.cms.Constants;
import com.code.aon.ui.form.FormUtil;

public class ModularPageController extends BasicI18nController implements ICMSConstants, Constants {
	
	public void onSelectOptions(ActionEvent event) throws ManagerBeanException, ExpressionException {
		ModularPageOptionController mpc = (ModularPageOptionController)FormUtil.getController(MODULAR_PAGE_OPTION);
		IManagerBean mpBean = BeanManager.getManagerBean(ModularPageOption.class);
		ModularPage modularPage = (ModularPage) this.getTo();
		Criteria criteria = new Criteria();
		criteria.addExpression(mpBean.getFieldName(ICMSAlias.MODULAR_PAGE_OPTION_MODULAR_PAGE_ID), "" + modularPage.getId());
		criteria.addOrder(mpBean.getFieldName(ICMSAlias.MODULAR_PAGE_OPTION_POSITION));
		mpc.setCurrentModularPage(modularPage);
		mpc.setCriteria(criteria);
		mpc.onSearch(event);
	}

	public void defaultHomepageChanged(ActionEvent event) throws ManagerBeanException, ExpressionException {
		ModularPage modularPage = (ModularPage) model.getRowData();
		disableHomepage();
		modularPage.setHomepage(true);
		modularPage.setActive(true);
		IManagerBean bean = BeanManager.getManagerBean(ModularPage.class);
		bean.update(modularPage);
	}
	
	@SuppressWarnings("unchecked")
	private void disableHomepage() throws ManagerBeanException, ExpressionException {
		IManagerBean bean = BeanManager.getManagerBean(ModularPage.class);
		List<ITransferObject> list = (List<ITransferObject>)model.getWrappedData();
		for (int i = 0; i < list.size(); i++) {
			ModularPage modularPage = (ModularPage)list.get(i);
			modularPage.setHomepage(false);
			bean.update(modularPage);
		}
	}
	
	public String getI18nLabel() throws ManagerBeanException {
		String label = NO_VALUE_LABEL;
		ModularPageDetail mpd = (ModularPageDetail)getModelRowdataI18n();
		if (mpd != null) label = mpd.getLabel();
		return label;
	}

}
