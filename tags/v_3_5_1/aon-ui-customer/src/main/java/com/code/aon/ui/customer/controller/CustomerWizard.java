package com.code.aon.ui.customer.controller;

import java.lang.reflect.InvocationTargetException;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.customer.dao.ICustomerAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.registry.Registry;
import com.code.aon.ui.form.wizard.AbstractWizard;

public class CustomerWizard extends AbstractWizard {

	private static String ID;
	private static String NAME;
	private static String SURNAME;
	private static String ALIAS;
	private static String DOCUMENT;
	private static String STATUS;

	private String id;
	private String name;
	private String surname;
	private String alias;
	private String document;
	private String status;

	static {
		try {
			IManagerBean customerBean = BeanManager.getManagerBean(Customer.class);
			ID = customerBean.getFieldName(ICustomerAlias.CUSTOMER_ID);
			NAME = customerBean.getFieldName(ICustomerAlias.CUSTOMER_REGISTRY_NAME);
			SURNAME = customerBean.getFieldName(ICustomerAlias.CUSTOMER_REGISTRY_SURNAME);
			DOCUMENT = customerBean.getFieldName(ICustomerAlias.CUSTOMER_REGISTRY_DOCUMENT);
			ALIAS = customerBean.getFieldName(ICustomerAlias.CUSTOMER_REGISTRY_ALIAS);
			STATUS = customerBean.getFieldName(ICustomerAlias.CUSTOMER_STATUS);
		} catch (ManagerBeanException e) {
			FacesContext context = FacesContext.getCurrentInstance();
			FacesMessage message = new FacesMessage(e.getMessage());
			context.addMessage(null, message);
		}
	}

	@Override
	protected Class<Customer> getClazz(){
		return Customer.class;
	}

	@Override
	protected void resetFields() {
		setId(null);
		setName(null);
		setSurname(null);
		setDocument(null);
		setAlias(null);
		setStatus(null);
	}
	
	@Override
	protected void fillCriteria(Criteria criteria) throws ExpressionException{
		if (!StringUtils.isBlank(id)) {
			criteria.addExpression(ExpressionUtilities.getExpression(getId(), ID));
		}
		if (!StringUtils.isBlank(name)) {
			criteria.addExpression(ExpressionUtilities.getExpression(getName(), NAME));
		}
		if (!StringUtils.isBlank(surname)) {
			criteria.addExpression(ExpressionUtilities.getExpression(getSurname(), SURNAME));
		}
		if (!StringUtils.isBlank(document)) {
			criteria.addExpression(ExpressionUtilities.getExpression(getDocument(), DOCUMENT));
		}
		if (!StringUtils.isBlank(alias)) {
			criteria.addExpression(ExpressionUtilities.getExpression(getAlias(), ALIAS));
		}
		if (!StringUtils.isBlank(status)) {
			criteria.addExpression(ExpressionUtilities.getExpression(getStatus(), STATUS));
		}
	}

	public void onSelectToRegistry(ActionEvent event)  {
		try {
			FacesContext ctx = FacesContext.getCurrentInstance();
			ELContext elctx = ctx.getELContext();
			ExpressionFactory factory = ctx.getApplication().getExpressionFactory();
			ValueExpression ve = factory.createValueExpression(elctx, getELValueExpression(),
					Registry.class);
			Customer a = (Customer) getController().getModel().getRowData();
			ve.setValue(elctx, a.getRegistry());
			assignExtendedLookupAttributes(a.getRegistry());
			reset();
		} catch (IllegalAccessException e) {
			FacesContext context = FacesContext.getCurrentInstance();
			FacesMessage message = new FacesMessage(e.getMessage());
			context.addMessage(null, message);
		} catch (InvocationTargetException e) {
			FacesContext context = FacesContext.getCurrentInstance();
			FacesMessage message = new FacesMessage(e.getMessage());
			context.addMessage(null, message);
		} catch (NoSuchMethodException e) {
			FacesContext context = FacesContext.getCurrentInstance();
			FacesMessage message = new FacesMessage(e.getMessage());
			context.addMessage(null, message);
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getDocument() {
		return document;
	}

	public void setDocument(String document) {
		this.document = document;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
