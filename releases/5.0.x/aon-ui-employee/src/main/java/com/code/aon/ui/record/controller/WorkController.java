package com.code.aon.ui.record.controller;

import java.util.Iterator;
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
import com.code.aon.record.dao.IRecordAlias;
import com.code.aon.registry.Registry;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;

public class WorkController extends BasicController {
	
	public static final String MANAGER_BEAN_NAME = "work";

	private Employee employee;
	
	private Logger LOGGER = Logger.getLogger(CourseController.class.getName());
	
	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public void employeeChanged(ValueChangeEvent event) throws ManagerBeanException{
		if(event.getNewValue() != null){
			this.setEmployee(obtainEmployee(event.getNewValue()));
		}
		if(this.getEmployee().getId() != null){
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(getManagerBean().getFieldName(IRecordAlias.WORK_EMPLOYEE_ID), getEmployee().getId());
			setCriteria(criteria);
			this.onSearch(null);
		}
	}

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
	public void onContract(ActionEvent event) throws ManagerBeanException{
		ContractController contractController = 
			(ContractController) AonUtil.getController( ContractController.MANAGER_BEAN_NAME );
		contractController.setEmployee(getEmployee());
		if(getEmployee().getId() != null){
			IManagerBean contractBean = BeanManager.getManagerBean(Contract.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(contractBean.getFieldName(IRecordAlias.CONTRACT_EMPLOYEE_ID), getEmployee().getId());
			contractController.setCriteria(criteria);
			contractController.onSearch(null);
		}
	}
}