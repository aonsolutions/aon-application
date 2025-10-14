package com.esferalia.aon.ui.payroll.event;

import com.code.aon.AonVersion;
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
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		super.afterBeanReset(event);
		PayrollWorkPlace pw = (PayrollWorkPlace) event.getController().getTo();
		WorkPlace w = new WorkPlace();
		PayrollUtils utils = PayrollUtils.getInstance();
		w.setEnterprise( utils.getCurrentDomainEnterprise() );
		pw.setWorkPlace(w);
		
	}
	
	@Override
	public void beforeModelInitialized(ControllerEvent event) throws ControllerListenerException {
		try {
			PayrollUtils utils = PayrollUtils.getInstance();
			Integer currentEnterprise = utils.getCurrentDomainEnterprise().getId();
			
			IManagerBean wBean = BeanManager.getManagerBean(WorkPlace.class);
			IManagerBean pwBean = BeanManager.getManagerBean(PayrollWorkPlace.class);
			
			Criteria wCriteria = new Criteria();
			wCriteria.addEqualExpression(wBean.getFieldName(IEntityAlias.WORK_PLACE_ENTERPRISE_ID), currentEnterprise);
			for (ITransferObject to: wBean.getList(wCriteria)){
				WorkPlace w = (WorkPlace) to;
				Criteria pwCriteria = new Criteria();
				pwCriteria.addEqualExpression(pwBean.getFieldName(IEntityAlias.PAYROLL_WORK_PLACE_WORK_PLACE_ID), w.getId());
				if (pwBean.getList(pwCriteria).isEmpty()){
					PayrollWorkPlace pw = new PayrollWorkPlace();
					pw.setWorkPlace(w);
					pwBean.insert(pw);
					System.out.println( "Inserted PayrollWorkPlace: " + pw.getId() + " for WorkPlace ---> ID:" +  pw.getWorkPlace().getId() + " Description: " + pw.getWorkPlace().getDescription() );
				}
			}
				
		} catch (ManagerBeanException e) {
			String message = "Error al iniciar los centros de trabajo";
			AonUtil.addErrorMessage(message);
			throw new ControllerListenerException(message, e);
		}
	}
	
	
}
