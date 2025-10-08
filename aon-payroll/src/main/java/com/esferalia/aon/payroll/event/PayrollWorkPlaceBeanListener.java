package com.esferalia.aon.payroll.event;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanListenerAdapter;
import com.code.aon.company.WorkPlace;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.payroll.PayrollWorkPlace;

public class PayrollWorkPlaceBeanListener extends ManagerBeanListenerAdapter {
	
	private static final long serialVersionUID = -5356032262632618892L;

	@Override
	public void beanRemoved(ManagerBeanEvent evt) throws ManagerBeanException {
		PayrollWorkPlace pw = (PayrollWorkPlace) evt.getTo();
		try {
			IManagerBean pwBean = BeanManager.getManagerBean(PayrollWorkPlace.class);
			Criteria crit = new Criteria();
			crit.addNotEqualExpression(pwBean.getFieldName(IEntityAlias.PAYROLL_WORK_PLACE_ID), pw.getId());
			crit.addEqualExpression(pwBean.getFieldName(IEntityAlias.PAYROLL_WORK_PLACE_WORK_PLACE_ID), pw.getWorkPlace().getId());
			if (pwBean.getCount(crit) == 0) {
				IManagerBean bean = BeanManager.getManagerBean(WorkPlace.class);
				bean.remove(pw.getWorkPlace());
			}
		} catch (Exception e) {
			String message = "Error al borrar el centro de trabajo";
			throw new ManagerBeanException(message, e);
		}
	}
}
