package com.code.aon.ui.finance.converter;

import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.Bank;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.ql.Criteria;

public class BankCodeConverter implements Converter {

	boolean validate;
	
	@Override
	public Object getAsObject(FacesContext ctx, UIComponent comp, String value) {
		if (value == null) {
			return null;
		}
		try {
			IManagerBean  bean = BeanManager.getManagerBean(Bank.class);
			String codeField = bean.getFieldName( IFinanceAlias.BANK_CODE );
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(codeField, value);
			List<?> list = bean.getList(criteria);
			Bank c = (Bank) list.get(0);
			if (validate && c==null) {
				throw new ConverterException("No existe un banco para el código '" + value +"'");	
			}
			if (c==null) {
				c = new Bank();
			}
			return c;
		} catch (ManagerBeanException e) {
			throw new ConverterException("No se puedo obtener el Manager de Bank.");
		}
	}

	@Override
	public String getAsString(FacesContext ctx, UIComponent c, Object value) {
		if (value == null) {
			return null;
		}
		if (value instanceof Bank) {
			Bank a = (Bank) value;
			return a.getCode();
		}
		throw new ConverterException("Error de conversión en el banco, "
				+ value.getClass().getName() + " no se puede convertir en Bank!.");
	}

	public boolean isValidate() {
		return validate;
	}

	public void setValidate(boolean validate) {
		this.validate = validate;
	}

}
