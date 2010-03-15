package com.code.aon.ui.record.controller;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.dao.ICompanyAlias;
import com.code.aon.company.resources.Employee;
import com.code.aon.ql.Criteria;
import com.code.aon.record.Contract;
import com.code.aon.record.LHCourse;
import com.code.aon.record.Position;
import com.code.aon.record.Work;
import com.code.aon.record.dao.IRecordAlias;
import com.code.aon.registry.Registry;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;

/**
 * This class manages employee contracts.
 * 
 * @author iayerbe
 *
 */
public class ContractController extends BasicController {
	
	private static final long serialVersionUID = -6879321358994217799L;

	public static final String MANAGER_BEAN_NAME = "contract";

	private Logger LOGGER = Logger.getLogger(ContractController.class.getName());
	
	private Employee employee;
	/** Tells if the application user can add a new contract. */
	private boolean newContractAllowed;
	
	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public boolean isNewContractAllowed() {
		return newContractAllowed;
	}

	@SuppressWarnings("unchecked")
	public void employeeChanged(ValueChangeEvent event) throws ManagerBeanException{
		if(event != null && event.getNewValue() != null){
			this.setEmployee(obtainEmployee(event.getNewValue()));
		}
		if(this.getEmployee().getId() != null){
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(getManagerBean().getFieldName(IRecordAlias.CONTRACT_EMPLOYEE_ID), getEmployee().getId());
			setCriteria(criteria);
			onSearch(null);
			List l = (List) getModel().getWrappedData();
			if ( l.size() > 0 ) {
				Contract c = (Contract) l.get( getModel().getRowCount() - 1 );
				newContractAllowed = ( c.getEndingDate() != null )? true: false;
			} else {
				newContractAllowed = true;
			}
		}
	}

	@SuppressWarnings("unchecked")
	private Employee obtainEmployee(Object value) {
		Employee employee;
		try {
			IManagerBean employeeBean = BeanManager.getManagerBean(Employee.class);
			Criteria criteria = new Criteria();
			String identifier = employeeBean.getFieldName( ICompanyAlias.EMPLOYEE_ID );
			criteria.addEqualExpression( identifier, value );
			Iterator iter = employeeBean.getList(criteria).iterator();
			if(iter.hasNext()){
				employee = (Employee)iter.next();
				return employee;
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining employee with id= " + value, e);
		}
		employee = new Employee();
		employee.setRegistry(new Registry());
		return employee;
	}
	
	@SuppressWarnings("unused")
	public void onWork(ActionEvent event) throws ManagerBeanException{
		WorkController workController = 
			(WorkController) AonUtil.getController( WorkController.MANAGER_BEAN_NAME );
		workController.setEmployee(getEmployee());
		if(getEmployee().getId() != null){
			IManagerBean workBean = BeanManager.getManagerBean(Work.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(workBean.getFieldName(IRecordAlias.WORK_EMPLOYEE_ID), getEmployee().getId());
			workController.setCriteria(criteria);
			workController.onSearch(null);
		}
	}
	
	@SuppressWarnings("unused")
	public void onPosition(ActionEvent event) throws ManagerBeanException{
		PositionController positionController = 
			(PositionController) AonUtil.getController( PositionController.MANAGER_BEAN_NAME );
		positionController.setEmployee(getEmployee());
		if(getEmployee().getId() != null){
			IManagerBean positionBean = BeanManager.getManagerBean(Position.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(positionBean.getFieldName(IRecordAlias.POSITION_EMPLOYEE_ID), getEmployee().getId());
			positionController.setCriteria(criteria);
			positionController.onSearch(null);
		}
	}
	
	@SuppressWarnings("unused")
	public void onCourse(ActionEvent event) throws ManagerBeanException{
		CourseController courseController = 
			(CourseController) AonUtil.getController( CourseController.MANAGER_BEAN_NAME );
		courseController.setEmployee(getEmployee());
		if(getEmployee().getId() != null){
			IManagerBean courseBean = BeanManager.getManagerBean(LHCourse.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(courseBean.getFieldName(IRecordAlias.LHCOURSE_EMPLOYEE_ID), getEmployee().getId());
			courseController.setCriteria(criteria);
			courseController.onSearch(null);
		}
	}
}
