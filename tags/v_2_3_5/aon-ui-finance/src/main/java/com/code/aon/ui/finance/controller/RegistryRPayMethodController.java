package com.code.aon.ui.finance.controller;

import java.util.Iterator;

import javax.faces.event.ValueChangeEvent;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.PayMethod;
import com.code.aon.finance.RegistryPayMethod;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.LinesController;

public class RegistryRPayMethodController extends LinesController {

	public void onChangePayMethod(ValueChangeEvent event) throws ManagerBeanException{
		if(event.getNewValue() != null){
			IManagerBean payMethodBean = BeanManager.getManagerBean(PayMethod.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(payMethodBean.getFieldName(IFinanceAlias.PAY_METHOD_ID),event.getNewValue());
			Iterator iter = payMethodBean.getList(criteria).iterator();
			if(iter.hasNext()){
				PayMethod payMethod = (PayMethod)iter.next();
				((RegistryPayMethod)this.getTo()).setPayment(payMethod);
			}
		}
	}
}
