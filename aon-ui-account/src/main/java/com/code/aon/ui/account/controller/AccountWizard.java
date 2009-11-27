package com.code.aon.ui.account.controller;

import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;

import com.code.aon.account.Account;
import com.code.aon.account.dao.IAccountAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.wizard.AbstractWizard;
import com.code.aon.ui.util.AonUtil;

public class AccountWizard extends AbstractWizard {

	private final static String ASTERISK = "*";
	private final static String PERCENT = "%";

	private static String ID;
	private static String DESCRIPTION;
	private static String ENTRY_ENABLED;
	private static String ALIAS;

	private String id;
	private String description;
	private String alias;
	private boolean orderById = false;
	private boolean orderByDescription  = true;
	private boolean orderByAlias = false;
	

	static {
		try {
			IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
			ID = accountBean.getFieldName(IAccountAlias.ACCOUNT_ID);
			ENTRY_ENABLED = accountBean.getFieldName(IAccountAlias.ACCOUNT_ENTRY_ENABLED);
			DESCRIPTION = accountBean.getFieldName(IAccountAlias.ACCOUNT_DESCRIPTION);
			ALIAS = accountBean.getFieldName(IAccountAlias.ACCOUNT_ALIAS);
		} catch (ManagerBeanException e) {
			FacesContext context = FacesContext.getCurrentInstance();
			FacesMessage message = new FacesMessage(e.getMessage());
			context.addMessage(null, message);
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	@SuppressWarnings("unchecked")
	public List autocompleteId(Object suggest) {
		try {
			String condition = (String) suggest;
			condition = condition.replace(ASTERISK, PERCENT);
			Criteria criteria = new Criteria();
			criteria.addExpression(ExpressionUtilities.getLikeExpression(ID, condition));
			criteria.addExpression(ExpressionUtilities.getEqualExpression(ENTRY_ENABLED, true));
			IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
			return accountBean.getList(criteria);
		} catch (ManagerBeanException e) {
			FacesContext context = FacesContext.getCurrentInstance();
			FacesMessage message = new FacesMessage(e.getMessage());
			context.addMessage(null, message);
			return null;
		}
	}

	@Override
	protected void fillCriteria(Criteria criteria) throws ExpressionException {
		if (!StringUtils.isBlank(id)) {
			criteria.addExpression(ExpressionUtilities.getExpression(getId(), ID));
		}
		if (!StringUtils.isBlank(description)) {
			criteria
					.addExpression(ExpressionUtilities.getExpression(getDescription(), DESCRIPTION));
		}
		if (!StringUtils.isBlank(alias)) {
			criteria.addExpression(ExpressionUtilities.getExpression(getAlias(), ALIAS));
		}
		criteria.addEqualExpression(ENTRY_ENABLED, true);
		if (isOrderById()) {
			criteria.addOrder(ID);	
		} else if (isOrderByDescription()) {
			criteria.addOrder(DESCRIPTION);	
		} else if (isOrderByAlias()) {
			criteria.addOrder(ALIAS);	
		}
	}

	@Override
	protected Class<Account> getClazz(){
		return Account.class;
	}

	@Override
	protected void resetFields() {
		setId(null);
		setDescription(null);
		setAlias(null);
	}

	public boolean isOrderById() {
		return orderById;
	}

	public void setOrderById(boolean orderById) {
		this.orderById = orderById;
	}

	public boolean isOrderByDescription() {
		return orderByDescription;
	}

	public void setOrderByDescription(boolean orderByDescription) {
		this.orderByDescription = orderByDescription;
	}

	public boolean isOrderByAlias() {
		return orderByAlias;
	}

	public void setOrderByAlias(boolean orderByAlias) {
		this.orderByAlias = orderByAlias;
	}

	public void onAccept(ActionEvent event) {
		if (StringUtils.isEmpty(getId())) {
			AonUtil.addErrorMessage("El código de cuenta contable es requerido.");
		} else if (StringUtils.isEmpty(getDescription())) {
			AonUtil.addErrorMessage("La descripción de la cuenta contable es requerida.");
		} else {
			try {
				IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
				Account account = new Account();
				account.setId(getId());
				account.setDescription(getDescription());
				account.setAlias(getAlias());
				account = (Account) accountBean.insert(account);
				onSearch(event);
			} catch (ManagerBeanException e) {
				FacesContext context = FacesContext.getCurrentInstance();
				FacesMessage message = new FacesMessage(e.getMessage());
				context.addMessage(null, message);
			}
		}
	}
}
