package com.code.aon.ui.finance.controller;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.Bank;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.wizard.AbstractWizard;

public class BankWizard extends AbstractWizard {

	private static String ID;
	private static String NAME;
	private static String CODE;

	private String id;
	private String name;
	private String code;

	static {
		try {
			IManagerBean bankBean = BeanManager.getManagerBean(Bank.class);
			ID = bankBean.getFieldName(IFinanceAlias.BANK_ID);
			NAME = bankBean.getFieldName(IFinanceAlias.BANK_NAME);
			CODE = bankBean.getFieldName(IFinanceAlias.BANK_CODE);
		} catch (ManagerBeanException e) {
			FacesContext context = FacesContext.getCurrentInstance();
			FacesMessage message = new FacesMessage(e.getMessage());
			context.addMessage(null, message);
		}
	}

	@Override
	protected Class<Bank> getClazz(){
		return Bank.class;
	}

	@Override
	protected void resetFields() {
		setId(null);
		setName(null);
		setCode(null);
	}
	
	@Override
	protected void fillCriteria(Criteria criteria) throws ExpressionException{
		if (!StringUtils.isBlank(id)) {
			criteria.addExpression(ExpressionUtilities.getExpression(getId(), ID));
		}
		if (!StringUtils.isBlank(name)) {
			criteria.addExpression(ExpressionUtilities.getExpression(getName(), NAME));
		}
		if (!StringUtils.isBlank(code)) {
			criteria.addExpression(ExpressionUtilities.getExpression(getCode(), CODE));
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

}
