package com.code.aon.ui.academy.controller;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.academy.Absence;
import com.code.aon.academy.CourseAlumn;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.Month;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.BasicController;
import com.esferalia.aon.entity.IEntityAlias;

public class AbsenceController extends BasicController {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbsenceController.class);
	
	private int evaluation;
	
	private CourseAlumn courseAlumn;
	
	private int year;
	
	private Month month;
	
	private String days;	
	
	public int getEvaluation() {
		return evaluation;
	}

	public void setEvaluation(int evaluation) {
		this.evaluation = evaluation;
	}

	public CourseAlumn getCourseAlumn() {
		return courseAlumn;
	}

	public void setCourseAlumn(CourseAlumn courseAlumn) {
		this.courseAlumn = courseAlumn;
	}

	public void refresh() throws ManagerBeanException {
		clearCriteria();
		Criteria criteria = getCriteria();
		criteria.addEqualExpression(getFieldName(IEntityAlias.ABSENCE_EVALUATION), evaluation);
		criteria.addEqualExpression(getFieldName(IEntityAlias.ABSENCE_COURSE_ALUMN_ID), courseAlumn.getId());
		initializeModel();
		initDates();
	}
	
	@Override
	public void onReset(ActionEvent event) {
		super.onReset(event);
		Absence absence = (Absence) getTo();
		absence.setEvaluation(evaluation);
		absence.setCourseAlumn(courseAlumn);
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public Month getMonth() {
		return month;
	}

	public void setMonth(Month month) {
		this.month = month;
	}

	public String getDays() {
		return days;
	}

	public void setDays(String days) {
		this.days = days;
	}

    private void initDates(){
    	setDays(null);
    	GregorianCalendar gc = new GregorianCalendar();
    	gc.setTime(new Date());
    	int monthValue = gc.get(GregorianCalendar.MONTH) + 1;
    	setMonth( Month.values()[monthValue] );
    	setYear( gc.get(GregorianCalendar.YEAR) );
    }

    public void onAddAbsences(ActionEvent event) {
		try{
			IManagerBean absenceBean = BeanManager.getManagerBean(Absence.class);
	    	for( String value : StringUtils.split(days) ) {
	    		if ( NumberUtils.isDigits(value) ) {
	    			int day = NumberUtils.toInt(value);
		    		Calendar cal = new GregorianCalendar();
		    		cal.set(Calendar.DATE, day);
		    		cal.set(Calendar.MONTH, month.ordinal());
		    		cal.set(Calendar.YEAR, year);
		    		if (cal.get(Calendar.DATE)!=day){
		    			throw new AbortProcessingException(day+" must be smaller."); 
		    		}
		    		Absence absence = new Absence();
		    		absence.setAbsenceDate(cal.getTime());
		    		absence.setCourseAlumn(courseAlumn);
		    		absence.setEvaluation(evaluation);
		    		absenceBean.insert(absence);	    			
	    		} else {
	    			throw new AbortProcessingException(value+" is not a number");
	    		}
	    	}
	    	refresh();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
            addMessage(e.getMessage());
            throw new AbortProcessingException(e.getMessage(), e);
		}
	}
    
}
