/**
 * 
 */
package com.code.aon.ui.employee.event;

import java.util.Date;

import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.resources.Employee;
import com.code.aon.record.Position;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.employee.controller.EmployeeController;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 06/09/2007
 *
 */
public class EmployeePositionListener extends ControllerAdapter {

	/* (non-Javadoc)
	 * @see com.code.aon.ui.form.event.ControllerAdapter#afterBeanAdded(com.code.aon.ui.form.event.ControllerEvent)
	 */
	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		EmployeeController ec = (EmployeeController) event.getController();
		Employee employee = (Employee) ec.getTo();
		IManagerBean bean = AonUtil.getManagerBean( Position.class );
		Position position = new Position();
		position.setEmployee( employee );
		position.setStartingDate( new Date() );
		position.setWorkPlace( ec.getResource().getWorkPlace() );
		position.setWorkActivity( ec.getResource().getWorkActivity() );
		try {
			bean.insert( position );
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException("Error addding Employee Position", e);
		}
	}

}
