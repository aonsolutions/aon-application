package com.code.aon.ui.academy.controller;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.StringTokenizer;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import com.code.aon.academy.Absence;
import com.code.aon.academy.Course;
import com.code.aon.academy.CourseAlumn;
import com.code.aon.academy.dao.IAcademyAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.BasicController;

public class AbsenceController extends BasicController {
	
	private Course course;
	
	private Customer customer;
	
	private int evaluation = 1;
	
	private int year;
	
	private int month;
	
	private String days;
	
	/**
	 * @return the course
	 */
	public Course getCourse() {
		return course;
	}

	/**
	 * @param course the course to set
	 */
	public void setCourse(Course course) {
		this.course = course;
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

	/**
	 * @return the evaluation
	 */
	public int getEvaluation() {
		return evaluation;
	}

	/**
	 * @param evaluation the evaluation to set
	 */
	public void setEvaluation(int evaluation) {
		this.evaluation = evaluation;
	}

	public void onEvaluationChanged(ValueChangeEvent event) throws ManagerBeanException{
		if(event.getNewValue() != null){
			setEvaluation(((Integer)event.getNewValue()).intValue());
		}
	}

	@SuppressWarnings("unused")
    public void onSearchAbsences(ActionEvent event) throws ManagerBeanException {
    	this.initDates();
		this.updateCriteria();
		this.onSearch(null);
    }
	
	private void updateCriteria() throws ManagerBeanException{
		Criteria criteria = new Criteria();
		CourseAlumn courseAlumn = getCourseAlumn();
		criteria.addEqualExpression(this.getFieldName(IAcademyAlias.ABSENCE_COURSE_ALUMN_ID),courseAlumn==null?new Integer(-1):courseAlumn.getId());
		criteria.addEqualExpression(this.getFieldName(IAcademyAlias.ABSENCE_EVALUATION),new Integer(evaluation));
		criteria.addOrder(this.getFieldName(IAcademyAlias.ABSENCE_ABSENCE_DATE),false);
		this.setCriteria(criteria);
	}
	
	public CourseAlumn getCourseAlumn() throws ManagerBeanException{
		if (this.customer == null || this.course == null)
			return null;
		IManagerBean courseAlumnBean = BeanManager.getManagerBean(CourseAlumn.class);
		Criteria courseAlumnCriteria = new Criteria();
		courseAlumnCriteria.addEqualExpression(courseAlumnBean.getFieldName(IAcademyAlias.COURSE_ALUMN_CUSTOMER_ID), customer.getId());
		courseAlumnCriteria.addEqualExpression(courseAlumnBean.getFieldName(IAcademyAlias.COURSE_ALUMN_COURSE_ID), course.getId());
		List<ITransferObject> listCourseAlumn = courseAlumnBean.getList(courseAlumnCriteria);
		if (listCourseAlumn.isEmpty()){
			return null;
		}
		return (CourseAlumn)listCourseAlumn.get(0);
	}

	/**
	 * @return the days
	 */
	public String getDays() {
		return days;
	}

	/**
	 * @param days the days to set
	 */
	public void setDays(String days) {
		this.days = days;
	}

	/**
	 * @return the month
	 */
	public int getMonth() {
		return month;
	}

	/**
	 * @param month the month to set
	 */
	public void setMonth(int month) {
		this.month = month;
	}

	/**
	 * @return the year
	 */
	public int getYear() {
		return year;
	}

	/**
	 * @param year the year to set
	 */
	public void setYear(int year) {
		this.year = year;
	}

	@SuppressWarnings("unused")
    public void onParseAbsences(ActionEvent event) {
		try{
			IManagerBean absenceBean = BeanManager.getManagerBean(Absence.class);
	    	Absence absence;
	    	CourseAlumn courseAlumn = getCourseAlumn();
	    	StringTokenizer tokens=new StringTokenizer(days, " ");
    		int day;
    		String value = new String();
	    	while (tokens.hasMoreElements()){
	    		try{
	    			value = tokens.nextToken();
	    			day = Integer.parseInt(value);
	    		}catch (NumberFormatException e) {
	    			throw new AbortProcessingException(value+" is not a number.", e); 
	    		}
	    		Calendar cal = new GregorianCalendar();
	    		cal.set(Calendar.DATE, day);
	    		cal.set(Calendar.MONTH, month-1);
	    		cal.set(Calendar.YEAR, year);
	    		if (cal.get(Calendar.DATE)!=day){
	    			throw new AbortProcessingException(day+" must be smaller."); 
	    		}
	    		absence = new Absence();
	    		absence.setAbsenceDate(cal.getTime());
	    		absence.setCourseAlumn(courseAlumn);
	    		absence.setEvaluation(evaluation);
	    		absenceBean.insert(absence);
	    	}
			initDates();
		}catch (Exception e) {
            addMessage(e.getMessage());
            throw new AbortProcessingException(e.getMessage(), e);
		}finally{
			this.onSearch(null);
		}
	}
	
    private void initDates(){
    	days = "";
    	GregorianCalendar gc = new GregorianCalendar();
    	gc.setTime(new Date());
    	month = gc.get(GregorianCalendar.MONTH) + 1;
    	year = gc.get(GregorianCalendar.YEAR);
    }
    
}
