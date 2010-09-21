package com.code.aon.ui.account.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.account.Account;

public class AccountConverter implements Converter {

	
	boolean validate;
	
	@Override
	public Object getAsObject(FacesContext ctx, UIComponent comp, String value) {
		if (value == null || "".equals(value) ) {
			return null;
		}
		try {
			IManagerBean  bean = BeanManager.getManagerBean(Account.class);
			Account c = (Account) bean.get(value);
			return c;
		} catch (ManagerBeanException e) {
			throw new ConverterException(e.getMessage());
		}
	}

	@Override
	public String getAsString(FacesContext ctx, UIComponent c, Object value) {
		if (value == null) {
			return null;
		}
		if (value instanceof Account) {
			Account a = (Account) value;
			if (a.getId() == null) {
				return null;
			}
			return a.getId();
		}
		throw new ConverterException("Error de conversión la cuenta contable, "
				+ value.getClass().getName() + " no se puede convertir en Account!.");
	}

}
