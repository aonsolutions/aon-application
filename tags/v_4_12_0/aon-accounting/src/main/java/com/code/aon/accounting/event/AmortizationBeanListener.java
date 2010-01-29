package com.code.aon.accounting.event;

import java.util.List;

import com.code.aon.accounting.Amortization;
import com.code.aon.accounting.AmortizationDetail;
import com.code.aon.accounting.amortization.AmortizationManager;
import com.code.aon.accounting.dao.IAccountingAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanListenerAdapter;
import com.code.aon.ql.Criteria;

public class AmortizationBeanListener extends ManagerBeanListenerAdapter  {
	
	@Override
	public void beanInserted(ManagerBeanEvent evt) throws ManagerBeanException {
    	Amortization to = (Amortization) evt.getTo();
    	AmortizationManager am = new AmortizationManager();
    	am.generateDetails(to);
	}
	
	@Override
	public void beanUpdated(ManagerBeanEvent evt) throws ManagerBeanException {
    	Amortization to = (Amortization) evt.getTo();
		IManagerBean detailBean = BeanManager.getManagerBean(AmortizationDetail.class);
		Criteria c = new Criteria();
		c.addEqualExpression(detailBean.getFieldName(IAccountingAlias.AMORTIZATION_DETAIL_AMORTIZATION_ID),to.getId());
		List<ITransferObject> details = detailBean.getList(c);
		if (details.size() == 0) {
	    	AmortizationManager am = new AmortizationManager();
	    	am.generateDetails(to);
		}
	}
}