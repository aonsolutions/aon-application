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
import com.code.aon.academy.Qualification;
import com.code.aon.academy.dao.IAcademyAlias;
import com.code.aon.academy.enumeration.CourseAlumnStatus;
import com.code.aon.academy.print.ReportMark;
import com.code.aon.academy.print.ReportMarkTo;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.academy.controller.CourseController;
import com.code.aon.ui.util.AonUtil;

public class MarkPrinter implements ICollectionProvider{
	
	private static final Logger LOGGER = Logger.getLogger(MarkPrinter.class.getName());
	
	private static final String COURSE_CONTROLLER_NAME = "course";
	
	private boolean encodedMarks;
	

	public boolean isEncodedMarks() {
		return encodedMarks;
	}

	public void setEncodedMarks(boolean encodedMarks) {
		this.encodedMarks = encodedMarks;
	}

	@SuppressWarnings("unchecked")
	public Collection getCollection() {
		List<ReportMark> reportMarkList = new LinkedList<ReportMark>();
		try{
			CourseController courseController = (CourseController)AonUtil.getController(COURSE_CONTROLLER_NAME);
			Course course = (Course)courseController.getTo();
			IManagerBean courseAlumnBean = BeanManager.getManagerBean(CourseAlumn.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(courseAlumnBean.getFieldName(IAcademyAlias.COURSE_ALUMN_COURSE_ID), course.getId());
			criteria.addEqualExpression(courseAlumnBean.getFieldName(IAcademyAlias.COURSE_ALUMN_STATUS), CourseAlumnStatus.ACTIVE);
			criteria.addOrder(courseAlumnBean.getFieldName(IAcademyAlias.COURSE_ALUMN_CUSTOMER_REGISTRY_SURNAME));
			criteria.addOrder(courseAlumnBean.getFieldName(IAcademyAlias.COURSE_ALUMN_CUSTOMER_REGISTRY_NAME));
			List<ITransferObject> courseAlumnList = courseAlumnBean.getList(criteria);
			Iterator<ITransferObject> courseAlumnListIter = courseAlumnList.iterator();
			while (courseAlumnListIter.hasNext()){
				CourseAlumn courseAlumn = (CourseAlumn)courseAlumnListIter.next();
				ReportMark reportMark = new ReportMark();
				reportMark.setCourseAlumn(courseAlumn);
				reportMark.setMarks(obtainMarks(courseAlumn));
				reportMark.setEvaluation(obtainMaxEvaluation(reportMark.getMarks()));
				reportMark.setAbsences(obtainAbsences(courseAlumn, reportMark.getEvaluation()));
				reportMark.setObservations(obtainObservations(courseAlumn, reportMark.getEvaluation()));
				reportMark.setCourseSchedule(obtainCourseSchedule(courseAlumn));
				reportMarkList.add(reportMark);
			}
		}catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining marks", e);
		}
		return reportMarkList;
	}

	@SuppressWarnings("unchecked")
	private Integer obtainMaxEvaluation(List<ReportMarkTo> marks) {
		int maxEval = 0; 
		Iterator iter = marks.iterator();
		while(iter.hasNext()){
			ReportMarkTo markTo = (ReportMarkTo) iter.next();
			if(markTo.getMark().getEvaluation() > maxEval){
				maxEval = markTo.getMark().getEvaluation();
			}
		}
		return new Integer(maxEval);
	}

	@SuppressWarnings("unchecked")
	public Collection getCollection(boolean forceRefresh) throws ManagerBeanException {
		return getCollection();
	}
	
	@SuppressWarnings("unchecked")
	private List<ReportMarkTo> obtainMarks(CourseAlumn courseAlumn){
		try {
			IManagerBean markBean = BeanManager.getManagerBean(Mark.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(markBean.getFieldName(IAcademyAlias.MARK_ALUMN_ID), courseAlumn.getId());
			criteria.addOrder(markBean.getFieldName(IAcademyAlias.MARK_EVALUATION));
			criteria.addOrder(markBean.getFieldName(IAcademyAlias.MARK_SUBJECT_ID));
			List<ReportMarkTo> marksLst = new ArrayList<ReportMarkTo>();
			Iterator iter = markBean.getList(criteria).iterator();
			while (iter.hasNext()){
				Mark mark = (Mark)iter.next();
				ReportMarkTo markTo = new ReportMarkTo();
				markTo.setMark(mark);
				marksLst.add(markTo);
			}
			if(isEncodedMarks()){
				marksLst = encodeMarks(marksLst);
			}
			return marksLst;
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining marks with course alumn = " + courseAlumn.getId(), e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private List<ReportMarkTo> encodeMarks(List<ReportMarkTo> marksLst) {
		Iterator iter = marksLst.iterator();
		List<ReportMarkTo> returnList = new ArrayList<ReportMarkTo>();
		while(iter.hasNext()){
			ReportMarkTo to = (ReportMarkTo)iter.next();
			Qualification qualification = obtainQualification(to.getMark().getMark());
			if(qualification != null){
				to.setCode(qualification.getCode());
				to.setDescription(qualification.getDescription());
			}
			returnList.add(to);
		}
		return returnList;
	}

	@SuppressWarnings("unchecked")
	private Qualification obtainQualification(double mark) {
		try {
			IManagerBean qualificationBean = BeanManager.getManagerBean(Qualification.class);
			Criteria criteria = new Criteria();
			criteria.addLessThanOrEqualExpression(qualificationBean.getFieldName(IAcademyAlias.QUALIFICATION_MIN_VALUE), mark);
			criteria.addGreaterThanOrEqualExpression(qualificationBean.getFieldName(IAcademyAlias.QUALIFICATION_MAX_VALUE), mark);
			Iterator iter = qualificationBean.getList(criteria).iterator();
			if(iter.hasNext()){
				return ((Qualification)iter.next());
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining code for mark: " + mark, e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private List<Absence> obtainAbsences(CourseAlumn courseAlumn, Integer evaluation){
		try {
			IManagerBean absenceBean = BeanManager.getManagerBean(Absence.class);
			Expression courseAlumnExp = ExpressionUtilities.getEqualExpression(absenceBean.getFieldName(IAcademyAlias.ABSENCE_COURSE_ALUMN_ID), courseAlumn.getId());
			List<Absence> absencesLst = new ArrayList<Absence>();
			for(int i=1; i<=evaluation.intValue(); i++){
				Criteria criteria = new Criteria();
				Expression evalExp = ExpressionUtilities.getEqualExpression(absenceBean.getFieldName(IAcademyAlias.ABSENCE_EVALUATION), new Integer(i));
				criteria.addExpression(ExpressionUtilities.getAndExpression(courseAlumnExp, evalExp));
				Iterator iter = absenceBean.getList(criteria).iterator();

				Absence emptyAbsence = new Absence();
				emptyAbsence.setCourseAlumn(courseAlumn);
				emptyAbsence.setEvaluation(i);
				emptyAbsence.setComments(Integer.toString(absenceBean.getCount(criteria)));
				absencesLst.add(emptyAbsence);
				while (iter.hasNext()){
					absencesLst.add((Absence)iter.next());
				}
			}
			return absencesLst;
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining absences with course alumn = " + courseAlumn.getId(), e);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private List<EvaluationObservation> obtainObservations(CourseAlumn courseAlumn, Integer evaluation){
		try {
			IManagerBean observationBean = BeanManager.getManagerBean(EvaluationObservation.class);
			Expression courseAlumnExp = ExpressionUtilities.getEqualExpression(observationBean.getFieldName(IAcademyAlias.EVALUATION_OBSERVATION_ALUMN_ID), courseAlumn.getId());
			List<EvaluationObservation> observationsLst = new ArrayList<EvaluationObservation>();
			for(int i=1; i<=evaluation.intValue(); i++){
				Criteria criteria = new Criteria();
				Expression evalExp = ExpressionUtilities.getEqualExpression(observationBean.getFieldName(IAcademyAlias.EVALUATION_OBSERVATION_EVALUATION), new Integer(i));
				criteria.addExpression(ExpressionUtilities.getAndExpression(courseAlumnExp, evalExp));
				Iterator iter = observationBean.getList(criteria).iterator();

				EvaluationObservation emptyObservation = new EvaluationObservation();
				emptyObservation.setAlumn(courseAlumn);
				emptyObservation.setEvaluation(i);
				emptyObservation.setComments("");
				observationsLst.add(emptyObservation);
				while (iter.hasNext()){
					observationsLst.add((EvaluationObservation)iter.next());
				}
			}
			return observationsLst;
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining observations with course alumn = " + courseAlumn.getId(), e);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
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
				courseScheduleStr = (courseScheduleStr.equals("")?courseScheduleStr:courseScheduleStr + "-");
				courseScheduleStr += courseSchedule.getDay().getShortName(locale);
				hours += (courseSchedule.getEndTime().getTime() - courseSchedule.getStartTime().getTime())/1000.0/60.0/60.0;
			}
			return courseScheduleStr + " " + hours;
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining schedule with course alumn = " + courseAlumn.getId(), e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public String getQualificationLegend() throws ManagerBeanException{
		String qualificationLegend = "";
		IManagerBean qualificationBean = BeanManager.getManagerBean(Qualification.class);
		Iterator iter = qualificationBean.getList(null).iterator();
		while (iter.hasNext()) {
			Qualification qualification = (Qualification)iter.next();
			qualificationLegend += qualification.getCode() + "-" + qualification.getDescription() + "; ";
		}
		return qualificationLegend;
	}
}