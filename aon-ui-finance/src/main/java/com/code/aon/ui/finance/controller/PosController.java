package com.code.aon.ui.finance.controller;

import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.company.WorkPlace;
import com.code.aon.config.Series;
import com.code.aon.finance.Pos;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class PosController extends BasicController {

	public void onWorkPlaceChanged(ValueChangeEvent event) {
		Pos pos = (Pos)getTo();
		pos.setWorkPlace((WorkPlace)event.getNewValue());
		pos.setSeries(null);
	}

	public List<SelectItem> getWorkPlaceSeries() throws ManagerBeanException {
		Pos pos = (Pos)getTo();
		if (pos != null) {
			List<SelectItem> series = new LinkedList<SelectItem>();
			IManagerBean seriesBean = BeanManager.getManagerBean(Series.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(seriesBean.getFieldName(IEntityAlias.SERIES_SCOPE_ID), pos.getWorkPlace().getScope().getId());
			criteria.addEqualExpression(seriesBean.getFieldName(IEntityAlias.SERIES_POS), Boolean.TRUE);
			criteria.addEqualExpression(seriesBean.getFieldName(IEntityAlias.SERIES_ACTIVE), Boolean.TRUE);
			if (!AonUtil.getRoleManager().isConfidentiality()) {
				criteria.addEqualExpression(seriesBean.getFieldName(IEntityAlias.SERIES_SECURITY_LEVEL), SecurityLevel.OFFICIAL);
			}
			criteria.addOrder(seriesBean.getFieldName(IEntityAlias.SERIES_CODE));
			for (ITransferObject ito : seriesBean.getList(criteria)) {
				Series serie = (Series)ito;
				SelectItem serieItem = new SelectItem(serie.getCode(), serie.getCode());
				series.add(serieItem);
			}
			return series;
		}
		return null;
	}

	public int getWorkPlaceSeriesCount() throws ManagerBeanException {
		Pos pos = (Pos)getTo();
		if (pos != null) {
			IManagerBean seriesBean = BeanManager.getManagerBean(Series.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(seriesBean.getFieldName(IEntityAlias.SERIES_SCOPE_ID), pos.getWorkPlace().getScope().getId());
			criteria.addEqualExpression(seriesBean.getFieldName(IEntityAlias.SERIES_POS), Boolean.TRUE);
			criteria.addEqualExpression(seriesBean.getFieldName(IEntityAlias.SERIES_ACTIVE), Boolean.TRUE);
			if (!AonUtil.getRoleManager().isConfidentiality()) {
				criteria.addEqualExpression(seriesBean.getFieldName(IEntityAlias.SERIES_SECURITY_LEVEL), SecurityLevel.OFFICIAL);
			}
			return seriesBean.getCount(criteria);
		}
		return 0;
	}

	public void onDeleteSeries(ActionEvent event) {
		Pos pos = (Pos)getTo();
		pos.setSeries(null);
	}

}