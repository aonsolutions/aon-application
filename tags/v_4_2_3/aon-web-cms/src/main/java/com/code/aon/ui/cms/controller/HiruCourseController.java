package com.code.aon.ui.cms.controller;

import java.util.Date;

import javax.faces.event.ActionEvent;

import com.code.aon.cms.HiruCourse;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.common.ManagerBeanException;


public class HiruCourseController extends BasicI18nController {

	private Date initDateFrom;

	private Date initDateTo;

	private Date endDateFrom;

	private Date endDateTo;

	private String activeTo = "true";
	
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
		HiruCourse f = (HiruCourse)this.model.getRowData();
		f.setActive(active);
		getManagerBean().update(f);
	}
	
	@SuppressWarnings("unused")
	public void onBuildFile(ActionEvent event) {
		
	}

	public void completeCriteria() throws ManagerBeanException {
		if (getInitDateFrom() != null) {
			String alias = getFieldName(ICMSAlias.HIRU_COURSE_INIT_DATE); 
			getCriteria().addGreaterThanOrEqualExpression(alias, getInitDateFrom());
		}
		if (getInitDateTo() != null) {
			String alias = getFieldName(ICMSAlias.HIRU_COURSE_INIT_DATE);
			getCriteria().addLessThanOrEqualExpression(alias, getInitDateTo());
		}
		if (getEndDateFrom() != null) {
			String alias = getFieldName(ICMSAlias.HIRU_COURSE_END_DATE); 
			getCriteria().addGreaterThanOrEqualExpression(alias, getEndDateFrom());
		}
		if (getEndDateTo() != null) {
			String alias = getFieldName(ICMSAlias.HIRU_COURSE_END_DATE);
			getCriteria().addLessThanOrEqualExpression(alias, getEndDateTo());
		}
		if (getActiveTo() != null && !getActiveTo().equals("")) {
			String alias = getFieldName(ICMSAlias.HIRU_COURSE_ACTIVE);
			if (getActiveTo().equals("true")) {
				getCriteria().addEqualExpression(alias, true);
			}else{
				getCriteria().addEqualExpression(alias, false);
			}
		}
	}
	
	// -------------------------------------------------
	// Getters y setters para los campos de la búsqueda.
	// -------------------------------------------------

	public Date getInitDateFrom() {
		return initDateFrom;
	}

	public void setInitDateFrom(Date initDateFrom) {
		this.initDateFrom = initDateFrom;
	}

	public Date getInitDateTo() {
		return initDateTo;
	}

	public void setInitDateTo(Date initDateTo) {
		this.initDateTo = initDateTo;
	}

	public Date getEndDateFrom() {
		return endDateFrom;
	}

	public void setEndDateFrom(Date endDateFrom) {
		this.endDateFrom = endDateFrom;
	}

	public Date getEndDateTo() {
		return endDateTo;
	}

	public void setEndDateTo(Date endDateTo) {
		this.endDateTo = endDateTo;
	}

	public String getActiveTo() {
		return activeTo;
	}

	public void setActiveTo(String activeTo) {
		this.activeTo = activeTo;
	}
	
}