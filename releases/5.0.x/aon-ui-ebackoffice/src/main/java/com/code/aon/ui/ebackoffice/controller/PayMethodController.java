package com.code.aon.ui.ebackoffice.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.PayMethod;
import com.code.aon.config.enumeration.PayMethodType;
import com.code.aon.ebackoffice.EcPaymethod;
import com.code.aon.ebackoffice.dao.IEbackofficeAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;

public class PayMethodController extends BasicController {

	public boolean isCreditPaymethod() {
		return (((PayMethod) this.getTo()).getType() == PayMethodType.CREDIT_CARD);
	}

	public boolean isDataAvailable() throws ManagerBeanException {
	
		if (((PayMethod) this.getTo()).getType() == PayMethodType.CREDIT_CARD) {
			IManagerBean ecpaymethodBean = BeanManager.getManagerBean(EcPaymethod.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(ecpaymethodBean	.getFieldName(IEbackofficeAlias.EC_PAYMETHOD_PAYMETHOD_ID),((PayMethod) this.getTo()).getId());
			List<ITransferObject> lista;
			lista = ecpaymethodBean.getList(criteria);
			if (lista.size() != 0) {
				return false;
			} 		
		}else return false;
		return true;
	
	}
	
	public String getPayMethodTypeName() {
		
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		return ((PayMethod) this.getTo()).getType().getName(locale);
	}
	
}
