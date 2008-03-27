package com.code.aon.ui.record.event;

import java.util.Date;

import javax.faces.event.AbortProcessingException;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.resources.Employee;
import com.code.aon.record.Contract;
import com.code.aon.ui.employee.util.Utils;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.record.controller.ContractController;

public class ContractControllerListener extends ControllerAdapter {
	
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		ContractController contractController = (ContractController)event.getController();
		((Contract)contractController.getTo()).setEmployee(contractController.getEmployee());
	}

	/* (non-Javadoc)
	 * @see com.code.aon.ui.form.event.ControllerAdapter#beforeBeanRemoved(com.code.aon.ui.form.event.ControllerEvent)
	 */
	@Override
	public void beforeBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		ContractController contractController = (ContractController)event.getController();
		try {
			if ( contractController.getModel().getRowCount() == 1 ) {
				Utils.addMessage( "aon_employee_first_contract_exception", true );
				throw new AbortProcessingException( "aon_employee_first_contract_exception" );
			}
		} catch (ManagerBeanException e) {
		}
	}

	/* (non-Javadoc)
	 * @see com.code.aon.ui.form.event.ControllerAdapter#afterBeanAdded(com.code.aon.ui.form.event.ControllerEvent)
	 */
	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		ContractController contractController = (ContractController)event.getController();
		contractController.getEmployee().setActive( true );
		save( contractController.getEmployee() );
	}

	/* (non-Javadoc)
	 * @see com.code.aon.ui.form.event.ControllerAdapter#afterBeanUpdated(com.code.aon.ui.form.event.ControllerEvent)
	 */
	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		ContractController contractController = (ContractController)event.getController();
		Date endingDate = ( (Contract) contractController.getTo() ).getEndingDate();
		if ( endingDate != null && endingDate.before( new Date() ) ) {
			contractController.getEmployee().setActive( false );
		} else {
			contractController.getEmployee().setActive( true );
		}
		save( contractController.getEmployee() );
	}

	/**
	 * Saves employee.
	 * 
	 * @param e
	 * @throws ControllerListenerException
	 */
	private void save(Employee e) throws ControllerListenerException {
		try {
			BeanManager.getManagerBean( Employee.class ).update( e );
		} catch (ManagerBeanException ex) {
			throw new ControllerListenerException( ex );
		}
	}
}
