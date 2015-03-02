package com.code.aon.ui.customer.event;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.ui.customer.controller.CustomerFACeController;
import com.code.aon.ui.customer.controller.ICustomerConstants;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

public class CustomerFACeControllerListener extends ControllerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		Customer customer = ((Customer)event.getController().getTo());
		if ( customer.isEInvoice() ) {
			try {
				recover(customer);
			} catch (ManagerBeanException e) {
				throw new ControllerListenerException(e);
			}			
		}
	}
	
	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		Customer customer = ((Customer)event.getController().getTo());
		if ( customer.isEInvoice() ) {
			try {
				recover(customer);
			} catch (ManagerBeanException e) {
				throw new ControllerListenerException(e);
			}			
		}
	}
	
	@Override
	public void afterBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		Customer customer = ((Customer)event.getController().getTo());
		try {
			remove(customer);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}			
	}
	
	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		Customer customer = ((Customer)event.getController().getTo());
		try {
			update(customer);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}			
	}

	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		Customer customer = ((Customer)event.getController().getTo());
		try {
			update(customer);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}			
	}	
	
	private void recover( Customer customer ) throws ManagerBeanException {
		CustomerFACeController controller = (CustomerFACeController) AonUtil.getRegisteredBean(ICustomerConstants.CUSTOMER_FACE_CONTROLLER_NAME);
		controller.onRecover(customer);		
	}

	private void remove( Customer customer ) throws ManagerBeanException {
		CustomerFACeController controller = (CustomerFACeController) AonUtil.getRegisteredBean(ICustomerConstants.CUSTOMER_FACE_CONTROLLER_NAME);
		controller.onRemove(customer);		
	}	

	private void update( Customer customer ) throws ManagerBeanException {
		CustomerFACeController controller = (CustomerFACeController) AonUtil.getRegisteredBean(ICustomerConstants.CUSTOMER_FACE_CONTROLLER_NAME);
		controller.onUpdate(customer);		
	}	

}