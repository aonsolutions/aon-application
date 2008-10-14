package com.code.aon.ui.commercial.event;

import com.code.aon.commercial.Offer;
import com.code.aon.commercial.Target;
import com.code.aon.commercial.dao.ICommercialAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.customer.dao.ICustomerAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.commercial.controller.ICommercialConstants;
import com.code.aon.ui.commercial.controller.TargetController;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

public class TargetControllerListener extends ControllerAdapter implements ICommercialConstants {
	
	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		TargetController controller = (TargetController) event.getController();
		try {
			controller.refreshSegments();
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException( e.getMessage(), e );
		}	
		cancelChildControllers();
	}

	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		TargetController controller = (TargetController) event.getController();
		try {
			controller.refreshSegments();
			resetTargetTracking( (Target) controller.getTo() );
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException( e.getMessage(), e );
		}
		cancelChildControllers();
	}
	
	@Override
	public void afterBeanReset(ControllerEvent event)
			throws ControllerListenerException {
		TargetController controller = (TargetController) event.getController();
		if ( controller.getTo() != null ) {
			try {
				controller.refreshSegments();
			} catch (ManagerBeanException e) {
				throw new ControllerListenerException( e.getMessage(), e );
			}			
		}
	}

	@Override
	public void beforeBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		Target target = (Target)event.getController().getTo();
		try {
			IManagerBean customerBean = BeanManager.getManagerBean(Customer.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(customerBean.getFieldName(ICustomerAlias.CUSTOMER_REGISTRY_ID), target.getRegistry().getId());
			if (customerBean.getList(criteria).size() > 0) {
				throw new ControllerListenerException("Imposible borrar cliente potencial. Esta registrado como cliente.");
			}

			IManagerBean offerBean = BeanManager.getManagerBean(Offer.class);
			criteria = new Criteria();
			criteria.addEqualExpression(offerBean.getFieldName(ICommercialAlias.OFFER_TARGET_ID), target.getRegistry().getId());
			if (offerBean.getList(criteria).size() > 0) {
				throw new ControllerListenerException("Imposible borrar cliente potencial. Tiene presupuestos asociados.");
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}
	
	private void cancelChildControllers() {
    	AonUtil.getController(TARGET_ADDRESS_CONTROLLER_NAME).onCancel(null);
       	AonUtil.getController(TARGET_MEDIA_CONTROLLER_NAME).onCancel(null);
       	AonUtil.getController(TARGET_SEGMENT_CONTROLLER_NAME).onCancel(null);
       	AonUtil.getController(TARGET_ITEM_CONTROLLER_NAME).onCancel(null);
       	AonUtil.getController(TARGET_SELLER_CONTROLLER_NAME).onCancel(null);
	}

	private void resetTargetTracking( Target target ) throws ManagerBeanException {
		IController controller = AonUtil.getController(TARGET_TRACKING_CONTROLLER_NAME);
		controller.clearCriteria();
		Criteria criteria = controller.getCriteria();
		String field = controller.getFieldName(ICommercialAlias.COMMERCIAL_TRACKING_TARGET_ID);
		criteria.addEqualExpression( field, target.getId() );
		controller.onSearch(null);
	}
	
}