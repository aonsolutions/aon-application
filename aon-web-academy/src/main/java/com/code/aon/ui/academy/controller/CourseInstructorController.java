package com.code.aon.ui.academy.controller;

import java.util.List;

import javax.faces.event.ValueChangeEvent;

import com.code.aon.academy.CourseInstructor;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.dao.ICompanyAlias;
import com.code.aon.company.resources.Employee;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.LinesController;

public class CourseInstructorController extends LinesController {

	
	public void onInstructorChanged(ValueChangeEvent event) throws ManagerBeanException{
		if(event.getNewValue() != null){
			IManagerBean employeeBean = BeanManager.getManagerBean(Employee.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(employeeBean.getFieldName(ICompanyAlias.EMPLOYEE_ID), event.getNewValue());
		}
	}
	
	public CourseInstructor getFirstInstructor(){
		List<ITransferObject> list = getWrappedList();
		if (list.isEmpty()){
			return null;
		}
		return (CourseInstructor)list.get(0);
	}
	
}
