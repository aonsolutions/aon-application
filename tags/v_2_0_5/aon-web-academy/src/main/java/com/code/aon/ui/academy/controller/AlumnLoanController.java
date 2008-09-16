package com.code.aon.ui.academy.controller;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import com.code.aon.academy.AlumnLoan;
import com.code.aon.academy.dao.IAcademyAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.menu.jsf.MenuEvent;

public class AlumnLoanController extends BasicController {
	
	private boolean notReturned;
	
	private static final Logger LOGGER = Logger.getLogger(AlumnLoanController.class.getName());

	public boolean isNotReturned() {
		return notReturned;
	}

	public void setNotReturned(boolean notReturned) {
		this.notReturned = notReturned;
	}

	public void onEditSearch(MenuEvent event){
		this.onEditSearch((ActionEvent)event);
	}
	
	@Override
	public void onEditSearch(ActionEvent event) {
		setNotReturned(true);
		super.onEditSearch(event);
	}

	@Override
	public void onSearch(ActionEvent event){
		try {
			if(notReturned){
				IManagerBean alumnLoanBean = BeanManager.getManagerBean(AlumnLoan.class);
				getCriteria().addNullExpression(alumnLoanBean.getFieldName(IAcademyAlias.ALUMN_LOAN_END_DATE));		
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error adding notReturnedExpression", e);
		}
		super.onSearch(event);
	}
	public void addLoanDateFromExpression(ValueChangeEvent event){
		if(event.getNewValue() != null){
			try {
				IManagerBean alumnLoanBean = BeanManager.getManagerBean(AlumnLoan.class);
				getCriteria().addGreaterThanOrEqualExpression(alumnLoanBean.getFieldName(IAcademyAlias.ALUMN_LOAN_LOAN_DATE), event.getNewValue());
			} catch (ManagerBeanException e) {
				LOGGER.log(Level.SEVERE, "Error adding FROM date expression", e);
			}
		}
	}
	
	public void addLoanDateToExpression(ValueChangeEvent event){
		if(event.getNewValue() != null){
			try {
				IManagerBean alumnLoanBean = BeanManager.getManagerBean(AlumnLoan.class);
				getCriteria().addLessThanOrEqualExpression(alumnLoanBean.getFieldName(IAcademyAlias.ALUMN_LOAN_LOAN_DATE), event.getNewValue());
			} catch (ManagerBeanException e) {
				LOGGER.log(Level.SEVERE, "Error adding TO date expression", e);
			}
		}
	}

	public void addEndDateFromExpression(ValueChangeEvent event){
		if(event.getNewValue() != null){
			try {
				IManagerBean alumnLoanBean = BeanManager.getManagerBean(AlumnLoan.class);
				getCriteria().addGreaterThanOrEqualExpression(alumnLoanBean.getFieldName(IAcademyAlias.ALUMN_LOAN_END_DATE), event.getNewValue());
			} catch (ManagerBeanException e) {
				LOGGER.log(Level.SEVERE, "Error adding FROM date expression", e);
			}
		}
	}
	
	public void addEndDateToExpression(ValueChangeEvent event){
		if(event.getNewValue() != null){
			try {
				IManagerBean alumnLoanBean = BeanManager.getManagerBean(AlumnLoan.class);
				getCriteria().addLessThanOrEqualExpression(alumnLoanBean.getFieldName(IAcademyAlias.ALUMN_LOAN_END_DATE), event.getNewValue());
			} catch (ManagerBeanException e) {
				LOGGER.log(Level.SEVERE, "Error adding TO date expression", e);
			}
		}
	}
}