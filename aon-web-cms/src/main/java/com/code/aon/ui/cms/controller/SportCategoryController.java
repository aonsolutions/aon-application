package com.code.aon.ui.cms.controller;

import java.util.List;

import javax.faces.event.ActionEvent;

import com.code.aon.cms.SportCategory;
import com.code.aon.cms.SportCategoryDetail;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.cms.Constants;


public class SportCategoryController extends BasicI18nController implements Constants {

	@SuppressWarnings("unused")
	public void onSelect(ActionEvent event) {
		super.onSelect(event);
		loadCurrentLanguage();
	}

	public String getI18nDescription() throws ManagerBeanException {
		String description = NO_VALUE_LABEL;
		SportCategoryDetail detail = (SportCategoryDetail)getModelRowdataI18n();
		if (detail != null) description = detail.getDescription();
		return description;
	}

	public void defaultChanged(ActionEvent event) throws ManagerBeanException, ExpressionException {
		SportCategory row = (SportCategory) model.getRowData();
		updateDefault_(row);
		row.setDefault_(true);
	}
	
	@SuppressWarnings("unchecked")
	private void updateDefault_(SportCategory default_) throws ManagerBeanException, ExpressionException {

		List<ITransferObject> list = (List<ITransferObject>)model.getWrappedData();
		for (int i = 0; i < list.size(); i++) {
			SportCategory row = (SportCategory)list.get(i);
			row.setDefault_(false);
		}
		
		IManagerBean bean = BeanManager.getManagerBean(SportCategory.class);
		list = (List<ITransferObject>)bean.getList(null);
		for (int i = 0; i < list.size(); i++) {
			SportCategory row = (SportCategory)list.get(i);
			row.setDefault_(false);
			bean.update(row);
		}

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(ICMSAlias.SPORT_CATEGORY_ID), default_.getId());
		list = bean.getList(criteria);
		if (list.size() > 0) {
			SportCategory row = (SportCategory) list.get(0);
			row.setDefault_(true);
			bean.update(row);
		}
	}

}