package com.esferalia.aon.payroll.event;

import java.util.List;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.payroll.AgreementLevel;
import com.esferalia.aon.payroll.AgreementLevelCategory;
import com.esferalia.aon.payroll.dao.IPayrollAlias;

public class AgreementLevelVetoableBeanListener extends ManagerBeanVetoListenerAdapter {

	@Override
	public void vetoableBeanRemoved(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		AgreementLevel al = (AgreementLevel) evt.getTo();
		try {
			removeAgreementLevelCategories(al);
		} catch (ManagerBeanException e) {
			throw new  ManagerBeanVetoListenerException("Imposible borrar Categorías.");
		}
		
	}


	private void removeAgreementLevelCategories(AgreementLevel al) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(AgreementLevelCategory.class);
		Criteria c = new Criteria();
		c.addEqualExpression(bean.getFieldName(IPayrollAlias.AGREEMENT_LEVEL_CATEGORY_LEVEL_ID), al.getId());
		List<ITransferObject> list = bean.getList(c);
		for (ITransferObject to : list) {
			bean.remove(to);
		}
	}

}
