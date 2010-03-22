package com.code.aon.ui.employee.event;

import java.util.Date;

import javax.faces.event.AbortProcessingException;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.resources.Employee;
import com.code.aon.record.Contract;
import com.code.aon.ui.employee.controller.EmployeeController;
import com.code.aon.ui.employee.util.Utils;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.record.controller.ContractController;
import com.code.aon.ui.util.AonUtil;

/**
 * The employee listener class for receiving contract events. 
 * 
 * @author iayerbe
 *
 */
public class EmployeeContractListener extends ControllerAdapter {

	private static final long serialVersionUID = 5831837830390797362L;

	/**
	 * Before adding a contract, the current employee is set to the new instance. 
	 */
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		ContractController cc = (ContractController) event.getController();
		Contract c = (Contract) cc.getTo();
		c.setEmployee( cc.getEmployee() );
	}

	/**
	 * Checks if there are more than one contracts before removing the selected one, otherwise throws 
	 * an exception.
	 */
	@Override
	public void beforeBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		ContractController cc = (ContractController) event.getController();
		try {
			if ( cc.getModel().getRowCount() == 1 ) {
				Utils.addMessage( "aon_employee_first_contract_exception", true );
				throw new AbortProcessingException( "aon_employee_first_contract_exception" );
			}
		} catch (ManagerBeanException e) {
		}
	}

	/**
	 * Enables employee.
	 */
	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		EmployeeController ec = 
			(EmployeeController) AonUtil.getController( EmployeeController.MANAGER_BEAN_NAME );
		ec.setResourceDirty( true );
		try {
			ec.getEmployee().setActive( true );
			ContractController cc = (ContractController) event.getController();
		//	Checks if the user is creating a new Employee or adding a new contract to an existing one.
			if ( cc.getModel().getRowCount() > 0 ) {
				ec.onAccept( null );
			} else {
				BeanManager.getManagerBean( Employee.class ).update( ec.getEmployee() );
			}
		} catch (ManagerBeanException ex) {
			throw new ControllerListenerException( ex );
		}
	}

	/**
	 * Disables employee.
	 */
	@Override
	public void afterBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		EmployeeController ec = 
			(EmployeeController) AonUtil.getController( EmployeeController.MANAGER_BEAN_NAME );
		ec.setResourceDirty( true );
		ec.onRemove( null );
	}

	/**
	 * Depending on contract ending date the employee is enabled or disabled. Normally the employee should
	 * disable.
	 */
	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		EmployeeController ec = 
			(EmployeeController) AonUtil.getController( EmployeeController.MANAGER_BEAN_NAME );
		ContractController cc = (ContractController) event.getController();
		Contract c = (Contract) cc.getTo();
		Date endingDate = c.getEndingDate();
		ec.setResourceDirty( true );
		if ( endingDate != null && endingDate.before( new Date() ) ) {
			ec.onRemove( null );
		} else {
			ec.getEmployee().setActive( true );
			ec.onAccept( null );
		}
	}
	
}
