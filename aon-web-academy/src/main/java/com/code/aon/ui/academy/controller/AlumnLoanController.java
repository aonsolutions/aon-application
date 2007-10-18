package com.code.aon.ui.academy.controller;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.ValueChangeEvent;

import com.code.aon.academy.AlumnLoan;
import com.code.aon.academy.dao.IAcademyAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.BasicController;

public class AlumnLoanController extends BasicController {
	
	private static final Logger LOGGER = Logger.getLogger(AlumnLoanController.class.getName());
	
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

	public void addLoanNotReturnedExpression(ValueChangeEvent event){
		if(Boolean.parseBoolean(event.getNewValue().toString()) == true){
			try {
				IManagerBean alumnLoanBean = BeanManager.getManagerBean(AlumnLoan.class);
				getCriteria().addNullExpression(alumnLoanBean.getFieldName(IAcademyAlias.ALUMN_LOAN_END_DATE));
			} catch (ManagerBeanException e) {
				LOGGER.log(Level.SEVERE, "Error adding TO date expression", e);
			}
		}
	}
	
}
