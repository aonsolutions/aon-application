package com.esferalia.aon.ui.payroll.event;

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
import com.esferalia.aon.ui.payroll.utils.PayrollUtils;

/**
 * Listener added to the PayrollWorkPlaceController
 * 
 */
public class PayrollWorkPlaceControllerListener extends ControllerAdapter {
	
	@Override
	public void beforeBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		updateCurrentToWorkPlace();
	}
	
	@Override
	public void beforeModelInitialized(ControllerEvent event)
			throws ControllerListenerException {
		try {
			if(this.getController().getRowCount()==0){
				IManagerBean bean = BeanManager.getManagerBean(WorkPlace.class);
				Criteria criteria = new Criteria();
				PayrollUtils utils = new PayrollUtils();
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.WORK_PLACE_ENTERPRISE_ID), utils.getCurrentDomainEnterprise().getId());
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.WORK_PLACE_ACTIVE), true);
				IManagerBean pwBean = BeanManager.getManagerBean(PayrollWorkPlace.class);
				for(ITransferObject to: bean.getList(criteria)){
					WorkPlace wp = (WorkPlace) to;
					PayrollWorkPlace pw = new PayrollWorkPlace();
					pw.setWorkPlace(wp);
					pwBean.insert(pw);
				}
			}
		} catch (ManagerBeanException e) {
			String message = "Error al iniciar los centros de trabajo";
			AonUtil.addErrorMessage(message);
			throw new ControllerListenerException(message, e);
		}
	}
	
	@Override
	public void afterBeanRemoved(ControllerEvent event)
			throws ControllerListenerException {
		removeCurrentToWorkPlace();
	}
	
	private void updateCurrentToWorkPlace() throws ControllerListenerException {
		PayrollWorkPlace pw = (PayrollWorkPlace) this.getController().getTo();
		try {
			IManagerBean bean = BeanManager.getManagerBean(WorkPlace.class);
			pw.setWorkPlace((WorkPlace) bean.update(pw.getWorkPlace()));
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
