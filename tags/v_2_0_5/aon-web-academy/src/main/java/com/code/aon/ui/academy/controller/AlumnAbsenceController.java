package com.code.aon.ui.academy.controller;

import java.util.Iterator;
import java.util.List;

import javax.faces.event.ActionEvent;

import com.code.aon.academy.Absence;
import com.code.aon.academy.AcademicYear;
import com.code.aon.academy.CourseAlumn;
import com.code.aon.academy.dao.IAcademyAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.form.BasicController;

public class AlumnAbsenceController extends BasicController {
	
	private AcademicYear academicYear = new AcademicYear();

	private Customer customer;
	
	/**
	 * @return the academicYear
	 */
	public AcademicYear getAcademicYear() {
		return academicYear;
	}

	/**
	 * @param academicYear the academicYear to set
	 */
	public void setAcademicYear(AcademicYear academicYear) {
		this.academicYear = academicYear;
	}

	/**
	 * @return the customer
	 */
	public Customer getCustomer() {
		return customer;
	}

	/**
	 * @param customer the customer to set
	 */
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	@SuppressWarnings("unused")
	public void refresh(ActionEvent event) throws ManagerBeanException{
		try {
			if (customer!=null){
				loadAbsences(customer);
			}
		} catch (ExpressionException e) {
			throw new ManagerBeanException(e);
		}
	}
	
	private void loadAbsences(Customer customer) throws ManagerBeanException, ExpressionException{
		Criteria criteria = new Criteria(); 
		IManagerBean absenceBean = BeanManager.getManagerBean(Absence.class);
		
		IManagerBean courseAlumnBean = BeanManager.getManagerBean(CourseAlumn.class);
		Criteria courseAlumnCriteria = new Criteria();
		courseAlumnCriteria.addEqualExpression(courseAlumnBean.getFieldName(IAcademyAlias.COURSE_ALUMN_CUSTOMER_ID), customer.getId());
		List<ITransferObject> listCourseAlumn = courseAlumnBean.getList(courseAlumnCriteria);
		if (!listCourseAlumn.isEmpty()){
			Iterator<ITransferObject> iterCourseAlumn = listCourseAlumn.iterator();
			while (iterCourseAlumn.hasNext()){
				CourseAlumn courseAlumn = (CourseAlumn)iterCourseAlumn.next();
				criteria.addOrExpression(absenceBean.getFieldName(IAcademyAlias.ABSENCE_COURSE_ALUMN_ID),String.valueOf(courseAlumn.getId()));
			}
		}else{
			criteria.addOrExpression(absenceBean.getFieldName(IAcademyAlias.ABSENCE_COURSE_ALUMN_ID),"-1");
		}
		if (this.getAcademicYear()!=null &&
				this.getAcademicYear().getId()!=null){
			criteria.addEqualExpression("Absence.courseAlumn.course.academicYear.id", this.getAcademicYear().getId());
		}
		criteria.addOrder(absenceBean.getFieldName(IAcademyAlias.ABSENCE_ABSENCE_DATE),false);
		this.setCriteria(criteria);
		this.onSearch(null);
	}
	

}
