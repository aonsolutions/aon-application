package com.code.aon.ui.academy.controller;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.ObjectUtils;

import com.code.aon.academy.Absence;
import com.code.aon.academy.AcademicYear;
import com.code.aon.academy.CourseAlumn;
import com.code.aon.academy.enumeration.CourseAlumnStatus;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.form.BasicController;
import com.esferalia.aon.entity.IEntityAlias;

public class CustomerAbsenceController extends BasicController {
	
	private AcademicYear academicYear;

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

	public void onAcademicYearChange(ValueChangeEvent event) throws ManagerBeanException, ExpressionException {
		if (! ObjectUtils.equals(this.academicYear, event.getNewValue()) ) {
			setAcademicYear( (AcademicYear) event.getNewValue() );
			loadAbsences(customer);			
		}
	}	
	
	public void refresh(ActionEvent event) throws ManagerBeanException{
		try {
			this.academicYear = null;
			if (customer!=null) {
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
		courseAlumnCriteria.addEqualExpression(courseAlumnBean.getFieldName(IEntityAlias.COURSE_ALUMN_CUSTOMER_ID), customer.getId());
		List<ITransferObject> listCourseAlumn = courseAlumnBean.getList(courseAlumnCriteria);
		if (!listCourseAlumn.isEmpty()){
			Iterator<ITransferObject> iterCourseAlumn = listCourseAlumn.iterator();
			while (iterCourseAlumn.hasNext()){
				CourseAlumn courseAlumn = (CourseAlumn)iterCourseAlumn.next();
				criteria.addOrExpression(absenceBean.getFieldName(IEntityAlias.ABSENCE_COURSE_ALUMN_ID),String.valueOf(courseAlumn.getId()));
			}
		} else {
			criteria.addOrExpression(absenceBean.getFieldName(IEntityAlias.ABSENCE_COURSE_ALUMN_ID),"-1");
		}
		if (this.getAcademicYear()!=null &&
				this.getAcademicYear().getId()!=null){
			criteria.addEqualExpression("Absence.courseAlumn.course.academicYear.id", this.getAcademicYear().getId());
		}
		criteria.addOrder(absenceBean.getFieldName(IEntityAlias.ABSENCE_ABSENCE_DATE),false);
		setCriteria(criteria);
		onSearch(null);
	}

    public List<SelectItem> getCourseAlumns() throws ManagerBeanException{
        List<SelectItem> courseAlumns = new LinkedList<SelectItem>();
        IManagerBean courseAlumnBean = BeanManager.getManagerBean(CourseAlumn.class);
        Criteria criteria = new Criteria();
        criteria.addEqualExpression(courseAlumnBean.getFieldName(IEntityAlias.COURSE_ALUMN_CUSTOMER_ID), customer.getId());
        criteria.addEqualExpression(courseAlumnBean.getFieldName(IEntityAlias.COURSE_ALUMN_STATUS), CourseAlumnStatus.ACTIVE);
        for( ITransferObject to : courseAlumnBean.getList(criteria) ) {
        	CourseAlumn courseAlumn = (CourseAlumn) to;
            SelectItem item = new SelectItem(courseAlumn, courseAlumn.getCourse().getDescription());
            courseAlumns.add(item);
        }
        return courseAlumns;
    }
	
}
