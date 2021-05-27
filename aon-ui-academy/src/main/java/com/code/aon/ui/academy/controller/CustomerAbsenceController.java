package com.code.aon.ui.academy.controller;

import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.ObjectUtils;

import com.code.aon.academy.AcademicYear;
import com.code.aon.academy.Course;
import com.code.aon.academy.CourseAlumn;
import com.code.aon.academy.enumeration.CourseAlumnStatus;
import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.form.BasicController;
import com.esferalia.aon.entity.IEntityAlias;

public class CustomerAbsenceController extends BasicController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private AcademicYear academicYear;

	private Customer customer;
	
	private List<CourseAlumn> courseAlumns;
	
	private boolean showNewWindow;
	
	public boolean isShowNewWindow() {
		return showNewWindow;
	}

	public void setShowNewWindow(boolean showNewWindow) {
		this.showNewWindow = showNewWindow;
	}

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
				updateCourseAlumns();
				loadAbsences(customer);
			}
		} catch (ExpressionException e) {
			throw new ManagerBeanException(e);
		}
	}
	
	public boolean isShowTab() throws ManagerBeanException {
		Criteria criteria = getCriteria(getManagerBean());
		return getManagerBean().getCount(criteria) > 0;
	}
	
	private Criteria getCriteria( IManagerBean bean ) throws ManagerBeanException {
		Criteria criteria = new Criteria(); 
		if (! courseAlumns.isEmpty()) {
			List<Integer> ids = new LinkedList<Integer>();
			for( CourseAlumn courseAlumn :courseAlumns ) {
				ids.add(courseAlumn.getId());
			}
			criteria.addInExpression(bean.getFieldName(IEntityAlias.ABSENCE_COURSE_ALUMN_ID), ids);
		} else {
			criteria.addNullExpression(bean.getFieldName(IEntityAlias.ABSENCE_COURSE_ALUMN_ID));
		}
		criteria.addOrder(bean.getFieldName(IEntityAlias.ABSENCE_ABSENCE_DATE), false);
		return criteria;
	}
	
	private void loadAbsences(Customer customer) throws ManagerBeanException, ExpressionException{
		Criteria criteria = getCriteria(getManagerBean());
		if ((getAcademicYear()!=null) && (getAcademicYear().getId()!=null)) {
			criteria.addEqualExpression("Absence.courseAlumn.course.academicYear.id", getAcademicYear().getId());
		}
		setCriteria(criteria);
		onSearch(null);
	}

    public List<SelectItem> getCourseAlumns() throws ManagerBeanException{
        List<SelectItem> courseAlumns = new LinkedList<SelectItem>();
        for( CourseAlumn courseAlumn : this.courseAlumns ) {
        	if ( courseAlumn.getStatus() == CourseAlumnStatus.ACTIVE ) {
        		Course course = courseAlumn.getCourse();
                SelectItem item = new SelectItem(courseAlumn, course.getCode() + " " + course.getDescription());
                courseAlumns.add(item);        		
        	}
        }
        return courseAlumns;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
	public void updateCourseAlumns() throws ManagerBeanException{
        IManagerBean courseAlumnBean = BeanManager.getManagerBean(CourseAlumn.class);
        Criteria criteria = new Criteria();
        criteria.addEqualExpression(courseAlumnBean.getFieldName(IEntityAlias.COURSE_ALUMN_CUSTOMER_ID), customer.getId());
        criteria.addOrder(courseAlumnBean.getFieldName(IEntityAlias.COURSE_ALUMN_COURSE_CODE));
        this.courseAlumns = (List) courseAlumnBean.getList(criteria);
    }
    
}
