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
import com.esferalia.aon.calendar.Calendar;
import com.esferalia.aon.payroll.Agreement;
import com.esferalia.aon.payroll.PayrollWorkPlace;
import com.esferalia.aon.payroll.dao.IPayrollAlias;
import com.esferalia.aon.ui.payroll.controller.enterprise.PayrollWorkPlaceController;

/**
 * Listener added to the PayrollWorkPlaceController
 * 
 */
public class PayrollWorkPlaceControllerListener extends ControllerAdapter {
	
	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		selectPayrollWorkPlace();
	}

	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		try {
			createNewPayrollWorkPlace();
		} catch (ManagerBeanException e) {
			// NADA, se inicializa a nulo
		}
	}
	
	private void createNewPayrollWorkPlace() throws ManagerBeanException {
		PayrollWorkPlaceController controller = (PayrollWorkPlaceController) this.getController();
		IManagerBean cBean = BeanManager.getManagerBean(Calendar.class);
		IManagerBean aBean = BeanManager.getManagerBean(Agreement.class);
		PayrollWorkPlace pw = new PayrollWorkPlace();
		pw.setCalendar((Calendar) cBean.createNewTo());
		pw.setAgreement((Agreement) aBean.createNewTo());
		controller.setPayrollWorkPlace(pw);
	}

	private void selectPayrollWorkPlace() {
		PayrollWorkPlaceController controller = (PayrollWorkPlaceController) this.getController();
		WorkPlace workPlace = (WorkPlace) controller.getTo();
		try {
			IManagerBean bean = BeanManager.getManagerBean(PayrollWorkPlace.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IPayrollAlias.PAYROLL_WORK_PLACE_WORK_PLACE_ID), workPlace.getId());
			List<ITransferObject> list = bean.getList(criteria);
			if(list.isEmpty()){
				createNewPayrollWorkPlace();
			} else {
				controller.setPayrollWorkPlace((PayrollWorkPlace) list.get(0));
			}
		} catch (ManagerBeanException e) {
			// NADA, se inicializa a nulo
			controller.setPayrollWorkPlace(null);
		}
	}

	@Override
	public void afterBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		PayrollWorkPlace pw = ((PayrollWorkPlaceController)this.getController()).getPayrollWorkPlace();
		if((pw.getAgreement()!=null && pw.getAgreement().getId()!=null) 
				|| (pw.getCalendar()!=null && pw.getCalendar().getId()!=null )
				|| (pw.getEnterpriseActivity()!=null && pw.getEnterpriseActivity().getId()!=null)){
			insertOrUpdateChild();
		}
	}
	
	@Override
	public void afterBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		PayrollWorkPlace pw = ((PayrollWorkPlaceController)this.getController()).getPayrollWorkPlace();
		if((pw.getAgreement()!=null && pw.getAgreement().getId()!=null) 
				|| (pw.getCalendar()!=null && pw.getCalendar().getId()!=null )
				|| (pw.getEnterpriseActivity()!=null && pw.getEnterpriseActivity().getId()!=null)){
			insertOrUpdateChild();
		}
	}
	
	@Override
	public void beforeBeanRemoved(ControllerEvent event)
			throws ControllerListenerException {
		removeChild();
	}
	
	private void insertOrUpdateChild() throws ControllerListenerException {
		PayrollWorkPlaceController controller = (PayrollWorkPlaceController) this.getController();
		if(controller.getPayrollWorkPlace().getId()==null){
			controller.getPayrollWorkPlace().setWorkPlace((WorkPlace) controller.getTo());
		}
		try {
			IManagerBean bean = BeanManager.getManagerBean(PayrollWorkPlace.class);
			controller.setPayrollWorkPlace((PayrollWorkPlace) bean.insertOrUpdate(controller.getPayrollWorkPlace()));
		} catch (ManagerBeanException e) {
			String message = "Error al actualizar el centro de trabajo";
			AonUtil.addErrorMessage(message);
			throw new ControllerListenerException(message, e);
		}
	}
	private void removeChild() throws ControllerListenerException {
		PayrollWorkPlaceController controller = (PayrollWorkPlaceController) this.getController();
		if(controller.getPayrollWorkPlace().getId()!=null){
			try {
				IManagerBean bean = BeanManager.getManagerBean(PayrollWorkPlace.class);
				bean.remove(controller.getPayrollWorkPlace());
			} catch (ManagerBeanException e) {
				String message = "Error al borrar el centro de trabajo";
				AonUtil.addErrorMessage(message);
				throw new ControllerListenerException(message, e);
			}
		}
	}
	
	
}
