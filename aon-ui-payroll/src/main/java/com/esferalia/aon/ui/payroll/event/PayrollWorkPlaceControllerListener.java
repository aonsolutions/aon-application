package com.esferalia.aon.ui.payroll.event;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.WorkPlace;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.PayrollWorkPlace;

/**
 * Listener added to the PayrollWorkPlaceController
 * 
 */
public class PayrollWorkPlaceControllerListener extends ControllerAdapter {
	
	@Override
	public void beforeBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		insertOrUpdateCurrentToWorkPlace();
	}
	
	@Override
	public void beforeBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		insertOrUpdateCurrentToWorkPlace();
	}
	
	@Override
	public void afterBeanRemoved(ControllerEvent event)
			throws ControllerListenerException {
		removeCurrentToWorkPlace();
	}
	
	private void insertOrUpdateCurrentToWorkPlace() throws ControllerListenerException {
		PayrollWorkPlace pw = (PayrollWorkPlace) this.getController().getTo();
		try {
			IManagerBean bean = BeanManager.getManagerBean(WorkPlace.class);
			pw.setWorkPlace((WorkPlace) bean.insertOrUpdate(pw.getWorkPlace()));
		} catch (ManagerBeanException e) {
			String message = "Error al actualizar el centro de trabajo";
			AonUtil.addErrorMessage(message);
			throw new ControllerListenerException(message, e);
		}
	}
	
	private void removeCurrentToWorkPlace() throws ControllerListenerException {
		PayrollWorkPlace pw = (PayrollWorkPlace) this.getController().getTo();
		try {
			IManagerBean bean = BeanManager.getManagerBean(WorkPlace.class);
			bean.remove(pw.getWorkPlace());
		} catch (ManagerBeanException e) {
			String message = "Error al borrar el centro de trabajo";
			AonUtil.addErrorMessage(message);
			throw new ControllerListenerException(message, e);
		}
	}
	
}
