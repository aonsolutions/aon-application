package com.code.aon.ui.record.controller;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ValueChangeEvent;

import org.apache.myfaces.custom.tabbedpane.TabChangeEvent;
import org.apache.myfaces.custom.tabbedpane.TabChangeListener;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.menu.jsf.MenuEvent;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.employee.controller.EmployeeController;

public class RecordController implements TabChangeListener {
	
	public static final String MANAGER_BEAN_NAME = "record";

	@SuppressWarnings("unused")
	public void onReset(MenuEvent event) throws ManagerBeanException{
		EmployeeController ec = 
			(EmployeeController) AonUtil.getController( EmployeeController.MANAGER_BEAN_NAME );
		ec.onReset( event );
		CourseController cc = (CourseController) AonUtil.getController(CourseController.MANAGER_BEAN_NAME);
        cc.onReset( null );
        WorkController wc = (WorkController) AonUtil.getController(WorkController.MANAGER_BEAN_NAME);
        wc.onReset( null );
        PositionController pc = (PositionController) AonUtil.getController(PositionController.MANAGER_BEAN_NAME);
        pc.onReset( null );
        ContractController contractController = (ContractController) AonUtil.getController(ContractController.MANAGER_BEAN_NAME);
        contractController.onReset( null );
	}

	public void employeeChanged(ValueChangeEvent event) throws ManagerBeanException{
		if( event.getNewValue() != null ) {
			CourseController cc = (CourseController) AonUtil.getController(CourseController.MANAGER_BEAN_NAME);
	        cc.onReset(null);
	        cc.employeeChanged(event);
	        WorkController wc = (WorkController) AonUtil.getController(WorkController.MANAGER_BEAN_NAME);
	        wc.onReset(null);
	        wc.employeeChanged(event);
	        PositionController pc = (PositionController) AonUtil.getController(PositionController.MANAGER_BEAN_NAME);
	        pc.onReset(null);
	        pc.employeeChanged(event);
	        ContractController contractController = (ContractController) AonUtil.getController(ContractController.MANAGER_BEAN_NAME);
	        contractController.onReset(null);
	        contractController.employeeChanged(event);
		}
	}

	public void processTabChange(TabChangeEvent arg0) throws AbortProcessingException {
		// TODO Auto-generated method stub
	}

}