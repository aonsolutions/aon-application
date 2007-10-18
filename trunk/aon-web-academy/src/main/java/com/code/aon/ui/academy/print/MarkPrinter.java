package com.code.aon.ui.academy.print;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;

import com.code.aon.academy.Absence;
import com.code.aon.academy.Course;
import com.code.aon.academy.CourseAlumn;
import com.code.aon.academy.CourseSchedule;
import com.code.aon.academy.EvaluationObservation;
import com.code.aon.academy.Mark;
import com.code.aon.academy.dao.IAcademyAlias;
import com.code.aon.academy.enumeration.CourseAlumnStatus;
import com.code.aon.academy.print.ReportMark;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.academy.controller.CourseController;
import com.code.aon.ui.util.AonUtil;

public class MarkPrinter implements ICollectionProvider{

	private static final Logger LOGGER = Logger.getLogger(MarkPrinter.class.getName());
	
	private static final String COURSE_CONTROLLER_NAME = "course";
	
	public Collection getCollection() {
		List<ReportMark> reportMarkList = new LinkedList<ReportMark>();
		try{
			CourseController courseController = (CourseController)AonUtil.getController(COURSE_CONTROLLER_NAME);
			Course course = (Course)courseController.getTo();
			IManagerBean courseAlumnBean = BeanManager.getManagerBean(CourseAlumn.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(courseAlumnBean.getFieldName(IAcademyAlias.COURSE_ALUMN_COURSE_ID), course.getId());
			criteria.addEqualExpression(courseAlumnBean.getFieldName(IAcademyAlias.COURSE_ALUMN_STATUS), CourseAlumnStatus.ACTIVE);
			List<ITransferObject> courseAlumnList = courseAlumnBean.getList(criteria);
			Iterator<ITransferObject> courseAlumnListIter = courseAlumnList.iterator();
			while (courseAlumnListIter.hasNext()){
				CourseAlumn courseAlumn = (CourseAlumn)courseAlumnListIter.next();
				ReportMark reportMark = new ReportMark();
				reportMark.setCourseAlumn(courseAlumn);
				reportMark.setMarks(obtainMarks(courseAlumn));
				reportMark.setAbsences(obtainAbsences(courseAlumn));
				reportMark.setObservations(obtainObservations(courseAlumn));
				reportMark.setCourseSchedule(obtainCourseSchedule(courseAlumn));
				reportMarkList.add(reportMark);
			}
		}catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining marks", e);
		}
		return reportMarkList;
	}

	private List<Mark> obtainMarks(CourseAlumn courseAlumn){
		try {
			IManagerBean markBean = BeanManager.getManagerBean(Mark.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(markBean.getFieldName(IAcademyAlias.MARK_ALUMN_ID), courseAlumn.getId());
			List<Mark> marksLst = new ArrayList<Mark>();
			Iterator iter = markBean.getList(criteria).iterator();
			while (iter.hasNext()){
				marksLst.add((Mark)iter.next());
			}
			return marksLst;
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining marks with course alumn = " + courseAlumn.getId(), e);
		}
		return null;
	}

	private List<Absence> obtainAbsences(CourseAlumn courseAlumn){
		try {
			IManagerBean absenceBean = BeanManager.getManagerBean(Absence.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(absenceBean.getFieldName(IAcademyAlias.ABSENCE_COURSE_ALUMN_ID), courseAlumn.getId());
			List<Absence> absencesLst = new ArrayList<Absence>();
			Iterator iter = absenceBean.getList(criteria).iterator();
			while (iter.hasNext()){
				absencesLst.add((Absence)iter.next());
			}
			return absencesLst;
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining absences with course alumn = " + courseAlumn.getId(), e);
		}
		return null;
	}
	
	private List<EvaluationObservation> obtainObservations(CourseAlumn courseAlumn){
		try {
			IManagerBean evaluationObservationBean = BeanManager.getManagerBean(EvaluationObservation.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(evaluationObservationBean.getFieldName(IAcademyAlias.EVALUATION_OBSERVATION_ALUMN_ID), courseAlumn.getId());
			List<EvaluationObservation> evaluationObservationsLst = new ArrayList<EvaluationObservation>();
			Iterator iter = evaluationObservationBean.getList(criteria).iterator();
			while (iter.hasNext()){
				evaluationObservationsLst.add((EvaluationObservation)iter.next());
			}
			return evaluationObservationsLst;
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining observations with course alumn = " + courseAlumn.getId(), e);
		}
		return null;
	}
	
	private String obtainCourseSchedule(CourseAlumn courseAlumn){
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		try {
			IManagerBean courseScheduleBean = BeanManager.getManagerBean(CourseSchedule.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(courseScheduleBean.getFieldName(IAcademyAlias.COURSE_SCHEDULE_COURSE_ID), courseAlumn.getCourse().getId());
			String courseScheduleStr = new String();
			Iterator iter = courseScheduleBean.getList(criteria).iterator();
			double hours = 0;
			CourseSchedule courseSchedule;
			while (iter.hasNext()){
				courseSchedule = (CourseSchedule)iter.next();
				courseScheduleStr += courseSchedule.getDay().getShortName(locale);
				hours += (courseSchedule.getEndTime().getTime() - courseSchedule.getStartTime().getTime())/1000/60/60;
			}
			return courseScheduleStr + " " + hours;
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining schedule with course alumn = " + courseAlumn.getId(), e);
		}
		return null;
	}

}