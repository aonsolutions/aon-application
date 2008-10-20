package com.code.aon.ui.account.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import com.code.aon.account.Account;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;

public class AccountConverter implements Converter {

	boolean validate;
	
	@Override
	public Object getAsObject(FacesContext ctx, UIComponent c, String value) {
		if (value == null) {
			return null;
		}
		try {
			IManagerBean  bean = BeanManager.getManagerBean(Account.class);
			Account a = (Account) bean.get(value);
			if (validate && a==null) {
				throw new ConverterException("No existe una cuenta contable para el código '" + value +"'");	
			}
			if (a==null) {
				a = new Account();
			}
			return a;
		} catch (ManagerBeanException e) {
			throw new ConverterException("No se puedo obtener el Manager de Account.");
		}
	}

	@Override
	public String getAsString(FacesContext ctx, UIComponent c, Object value) {
		if (value == null) {
			return null;
		}
		if (value instanceof Account) {
			Account a = (Account) value;
			return a.getId();
		}
		throw new ConverterException("Error de conversión en la cuenta, "
				+ value.getClass().getName() + " no se puede convertir en Account!.");
	}

	public boolean isValidate() {
		return validate;
	}

	public void setValidate(boolean validate) {
		this.validate = validate;
	}

}
