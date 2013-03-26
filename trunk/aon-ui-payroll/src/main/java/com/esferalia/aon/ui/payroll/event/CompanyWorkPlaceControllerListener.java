package com.esferalia.aon.ui.payroll.event;

import java.util.List;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.WorkPlace;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.payroll.PayrollWorkPlace;

/**
 * Listener added to the CompanyController
 * 
 */
public class CompanyWorkPlaceControllerListener extends ControllerAdapter {

	@Override
	public void afterBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		addCurrentToPayrollWorkPlace();
	}
	
	@Override
	public void beforeBeanRemoved(ControllerEvent event)
			throws ControllerListenerException {
		removeCurrentToPayrollWorkPlace();
	}
	
	private void addCurrentToPayrollWorkPlace() throws ControllerListenerException {
		WorkPlace w = (WorkPlace) this.getController().getTo();
		try {
			IManagerBean bean = BeanManager.getManagerBean(PayrollWorkPlace.class);
			PayrollWorkPlace pw = new PayrollWorkPlace();
			pw.setWorkPlace(w);
			bean.insertOrUpdate(pw);
		} catch (ManagerBeanException e) {
			String message = "Error al actualizar el centro de trabajo";
			AonUtil.addErrorMessage(message);
			throw new ControllerListenerException(message, e);
		}
	}
	
	private void removeCurrentToPayrollWorkPlace() throws ControllerListenerException {
		WorkPlace w = (WorkPlace) this.getController().getTo();
		try {
			IManagerBean bean = BeanManager.getManagerBean(PayrollWorkPlace.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PAYROLL_WORK_PLACE_WORK_PLACE_ID), w.getId());
			List<ITransferObject> list = bean.getList(criteria);
			if(!list.isEmpty()){
				PayrollWorkPlace pw = (PayrollWorkPlace) list.get(0);
				bean.remove(pw);
			}
		} catch (ManagerBeanException e) {
			String message = "Error al borrar el centro de trabajo";
			AonUtil.addErrorMessage(message);
			throw new ControllerListenerException(message, e);
		}
	}
	
}
