package com.code.aon.ui.customer.event;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.ui.customer.controller.CustomerEdiSupportController;
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
		try {
			recoverFACe(customer);
			recoverEdi(customer);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}			
	}
	
	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		Customer customer = ((Customer)event.getController().getTo());
		try {
			recoverFACe(customer);
			recoverEdi(customer);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}			
	}
	
	@Override
	public void afterBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		Customer customer = ((Customer)event.getController().getTo());
		try {
			removeFACe(customer);
			removeEdi(customer);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}			
	}
	
	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		Customer customer = ((Customer)event.getController().getTo());
		try {
			updateFACe(customer);
			updateEdi(customer);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}			
	}

	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		Customer customer = ((Customer)event.getController().getTo());
		try {
			updateFACe(customer);
			updateEdi(customer);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}			
	}	
	
	private void recoverFACe( Customer customer ) throws ManagerBeanException {
		CustomerFACeController controller = (CustomerFACeController) AonUtil.getRegisteredBean(ICustomerConstants.CUSTOMER_FACE_CONTROLLER_NAME);
		controller.onRecover(customer);		
	}

	private void removeFACe( Customer customer ) throws ManagerBeanException {
		CustomerFACeController controller = (CustomerFACeController) AonUtil.getRegisteredBean(ICustomerConstants.CUSTOMER_FACE_CONTROLLER_NAME);
		controller.onRemove(customer);		
	}	

	private void updateFACe( Customer customer ) throws ManagerBeanException {
		CustomerFACeController controller = (CustomerFACeController) AonUtil.getRegisteredBean(ICustomerConstants.CUSTOMER_FACE_CONTROLLER_NAME);
		controller.onUpdate(customer);		
	}	
	
	private void recoverEdi( Customer customer ) throws ManagerBeanException {
		CustomerEdiSupportController controller = (CustomerEdiSupportController) AonUtil.getRegisteredBean(ICustomerConstants.CUSTOMER_EDI_SUPPORT_CONTROLLER_NAME);
		controller.onRecover(customer);		
	}
	
	private void removeEdi( Customer customer ) throws ManagerBeanException {
		CustomerEdiSupportController controller = (CustomerEdiSupportController) AonUtil.getRegisteredBean(ICustomerConstants.CUSTOMER_EDI_SUPPORT_CONTROLLER_NAME);
		controller.onRemove(customer);		
	}	
	
	private void updateEdi( Customer customer ) throws ManagerBeanException {
		CustomerEdiSupportController controller = (CustomerEdiSupportController) AonUtil.getRegisteredBean(ICustomerConstants.CUSTOMER_EDI_SUPPORT_CONTROLLER_NAME);
		controller.onUpdate(customer);		
	}	

}