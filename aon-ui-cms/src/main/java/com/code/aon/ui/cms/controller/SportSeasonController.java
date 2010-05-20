package com.code.aon.ui.cms.controller;

import java.util.List;

import javax.faces.event.ActionEvent;

import com.code.aon.cms.SportSeason;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.form.BasicController;


public class SportSeasonController extends BasicController {

	public void defaultChanged(ActionEvent event) throws ManagerBeanException, ExpressionException {
		SportSeason row = (SportSeason) model.getRowData();
		updateDefault_(row);
		row.setDefault_(true);
	}
	
	@SuppressWarnings("unchecked")
	private void updateDefault_(SportSeason default_) throws ManagerBeanException, ExpressionException {

		List<ITransferObject> list = (List<ITransferObject>)model.getWrappedData();
		for (int i = 0; i < list.size(); i++) {
			SportSeason row = (SportSeason)list.get(i);
			row.setDefault_(false);
		}
		
		IManagerBean bean = BeanManager.getManagerBean(SportSeason.class);
		list = (List<ITransferObject>)bean.getList(null);
		for (int i = 0; i < list.size(); i++) {
			SportSeason row = (SportSeason)list.get(i);
			row.setDefault_(false);
			bean.update(row);
		}

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(ICMSAlias.SPORT_SEASON_ID), default_.getId());
		list = bean.getList(criteria);
		if (list.size() > 0) {
			SportSeason row = (SportSeason) list.get(0);
			row.setDefault_(true);
			bean.update(row);
		}
	}

}