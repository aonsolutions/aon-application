package com.code.aon.ui.tas.controller;

import javax.faces.event.ValueChangeEvent;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.util.SeriesNumberUtil;
import com.code.aon.tas.ProjectTas;
import com.code.aon.ui.form.BasicController;

public class ProjectTasController extends BasicController {

	public void onSeriesChanged(ValueChangeEvent event) throws ManagerBeanException {
		int number = obtainMaxNumber((String)event.getNewValue());
		if (getTo() != null) {
			((ProjectTas)getTo()).setNumber(number);
		}
	}

	private int obtainMaxNumber(String seriesId) throws ManagerBeanException {
    	return SeriesNumberUtil.obtainNumber(seriesId, StringUtils.capitalize(this.getBeanName()));
	}

/*
	public void targetData(LookupChangeEvent event) throws ManagerBeanException {
		if (event.getNewValue() != null && !event.getNewValue().equals("")) {
			Target target = (Target)event.getNewValue();
			getOffer().setTarget(target);
		}
	}
*/
}
