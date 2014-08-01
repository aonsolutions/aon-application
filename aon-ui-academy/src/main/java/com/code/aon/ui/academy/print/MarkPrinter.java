package com.code.aon.ui.academy.print;

import static com.code.aon.ui.academy.controller.IAcademyConstants.COURSE_CONTROLLER_NAME;
import static com.code.aon.ui.common.ICommonMessages.MARK_AVERAGE;
import static com.code.aon.ui.common.ICommonMessages.MARK_AVERAGE_ABRV;
import static com.code.aon.ui.common.ICommonMessages.MARK_QUALITATIVE;
import static com.code.aon.ui.common.ICommonMessages.MARK_QUANTITATIVE;
import static com.code.aon.ui.common.ICommonMessages.MARK_QUANTITATIVE_AVERAGE;
import static com.code.aon.ui.common.ICommonMessages.MARK_QUANTITATIVE_AVERAGE_FINAL;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.code.aon.AonVersion;
import com.code.aon.academy.Absence;
import com.code.aon.academy.AcademicSkill;
import com.code.aon.academy.Course;
import com.code.aon.academy.CourseAcademicSkill;
import com.code.aon.academy.CourseAlumn;
import com.code.aon.academy.CourseSchedule;
import com.code.aon.academy.EvaluationObservation;
import com.code.aon.academy.Mark;
import com.code.aon.academy.Qualification;
import com.code.aon.academy.enumeration.CourseAlumnStatus;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.academy.controller.CourseController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class MarkPrinter implements ICollectionProvider{

	private static final Logger LOGGER = Logger.getLogger(MarkPrinter.class.getName());

	private Integer printOption;

	public Integer getPrintOption() {
		return printOption;
	}

	public void setPrintOption(Integer printOption) {
		this.printOption = printOption;
	}

	public List<SelectItem> getPrintOptions() throws ManagerBeanException {
    	List<SelectItem> printTypes = new LinkedList<SelectItem>();
        printTypes.add(new SelectItem(1, AonUtil.getMessage(MARK_QUALITATIVE)));
        printTypes.add(new SelectItem(2, AonUtil.getMessage(MARK_QUANTITATIVE)));
        printTypes.add(new SelectItem(3, AonUtil.getMessage(MARK_QUANTITATIVE_AVERAGE)));
        printTypes.add(new SelectItem(4, AonUtil.getMessage(MARK_QUANTITATIVE_AVERAGE_FINAL)));
        return printTypes;
    }

	public boolean isEncodedMarks() {
		return (printOption == 1);
	}

	public boolean withAverageMark() {
		return (printOption > 2);
	}

	public boolean withFinalMark() {
		return (printOption > 3);
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Collection getCollection() {
		List<ReportMark> reportMarkList = new LinkedList<ReportMark>();
		try{
			CourseController courseController = (CourseController)FormUtil.getController(COURSE_CONTROLLER_NAME);
			Course course = (Course)courseController.getTo();
			IManagerBean courseAlumnBean = BeanManager.getManagerBean(CourseAlumn.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(courseAlumnBean.getFieldName(IEntityAlias.COURSE_ALUMN_COURSE_ID), course.getId());
			criteria.addEqualExpression(courseAlumnBean.getFieldName(IEntityAlias.COURSE_ALUMN_STATUS), CourseAlumnStatus.ACTIVE);
			criteria.addOrder(courseAlumnBean.getFieldName(IEntityAlias.COURSE_ALUMN_CUSTOMER_REGISTRY_NAME));
			List<ITransferObject> courseAlumnList = courseAlumnBean.getList(criteria);
			Iterator<ITransferObject> courseAlumnListIter = courseAlumnList.iterator();
			while (courseAlumnListIter.hasNext()){
				CourseAlumn courseAlumn = (CourseAlumn)courseAlumnListIter.next();
				Map<Integer, ReportMarkTo> averageMarksMap = new HashMap<Integer, ReportMarkTo>(); 
				ReportMark reportMark = new ReportMark();
				reportMark.setCourseAlumn(courseAlumn);
				reportMark.setMarks(obtainMarks(courseAlumn, averageMarksMap));
				reportMark.setAverageMarks(obtainAverageMarks(averageMarksMap));
				reportMark.setFinalMark(obtainFinalMark(averageMarksMap));
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

	private Integer obtainMaxEvaluation(List<ReportMarkTo> marks) {
		int maxEval = 0; 
		Iterator<ReportMarkTo> iter = marks.iterator();
		while(iter.hasNext()){
			ReportMarkTo markTo = iter.next();
			if(markTo.getMark().getEvaluation() > maxEval){
				maxEval = markTo.getMark().getEvaluation();
			}
		}
		return new Integer(maxEval);
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Collection getCollection(boolean forceRefresh) throws ManagerBeanException {
		return getCollection();
	}
	
	private List<ReportMarkTo> obtainMarks(CourseAlumn courseAlumn, Map<Integer, ReportMarkTo> averageMarksMap){
		try {
			List<ReportMarkTo> marksLst = new ArrayList<ReportMarkTo>();
			int previousEval = -1;
			Double averageMark = 0.0;
			Double weightSum = 0.0;

			IManagerBean markBean = BeanManager.getManagerBean(Mark.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(markBean.getFieldName(IEntityAlias.MARK_ALUMN_ID), courseAlumn.getId());
			criteria.addOrder(markBean.getFieldName(IEntityAlias.MARK_EVALUATION));
			criteria.addOrder(markBean.getFieldName(IEntityAlias.MARK_SUBJECT_ID));
			Iterator<ITransferObject> iter = markBean.getList(criteria).iterator();
			while (iter.hasNext()){
				Mark mark = (Mark)iter.next();
				if (withAverageMark() && previousEval >= 0 && previousEval != mark.getEvaluation()) {
					Mark avgMark = new Mark();
					avgMark.setAlumn(courseAlumn);
					avgMark.setEvaluation(previousEval);
					avgMark.setMark(round(averageMark / weightSum, 2));
					avgMark.setSubject(getAverageSubject(courseAlumn));

					ReportMarkTo avgMarkTo = new ReportMarkTo();
					avgMarkTo.setMark(avgMark);
					averageMarksMap.put(avgMarkTo.getMark().getEvaluation(), avgMarkTo);

					averageMark = 0.0;
					weightSum = 0.0;
				}

				ReportMarkTo markTo = new ReportMarkTo();
				markTo.setMark(mark);
				marksLst.add(markTo);

				if (withAverageMark()) {
					previousEval = mark.getEvaluation();
					if (mark.getMark() != null) {
						averageMark += mark.getMark() * mark.getSubject().getWeight();
						weightSum += mark.getSubject().getWeight();
					}
					if (!iter.hasNext()) {
						Mark avgMark = new Mark();
						avgMark.setAlumn(courseAlumn);
						avgMark.setEvaluation(previousEval);
						avgMark.setMark(round(averageMark / weightSum, 2));
						avgMark.setSubject(getAverageSubject(courseAlumn));
	
						ReportMarkTo avgMarkTo = new ReportMarkTo();
						avgMarkTo.setMark(avgMark);
						averageMarksMap.put(avgMarkTo.getMark().getEvaluation(), avgMarkTo);
					}
				}
			}

			if (isEncodedMarks()) {
				marksLst = encodeMarks(marksLst);
			}

			return marksLst;
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining marks with course alumn = " + courseAlumn.getId(), e);
		}
		return null;
	}

	private double round(double value, int precision) {
		return Math.round(value * Math.pow(10, precision)) / Math.pow(10, precision);
	}

	private CourseAcademicSkill getAverageSubject(CourseAlumn alumn) {
		AcademicSkill academicSkill = new AcademicSkill();
		academicSkill.setDescription(AonUtil.getMessage(MARK_AVERAGE));
		academicSkill.setCode(AonUtil.getMessage(MARK_AVERAGE_ABRV));

		CourseAcademicSkill subject = new CourseAcademicSkill();
		subject.setAcademicSkill(academicSkill);
		subject.setCourse(alumn.getCourse());
		return subject;
	}

	private List<ReportMarkTo> encodeMarks(List<ReportMarkTo> marksLst) {
		Iterator<ReportMarkTo> iter = marksLst.iterator();
		List<ReportMarkTo> returnList = new ArrayList<ReportMarkTo>();
		while(iter.hasNext()){
			ReportMarkTo to = iter.next();
			Qualification qualification = obtainQualification(to.getMark().getMark());
			if(qualification != null){
				to.setCode(qualification.getCode());
				to.setDescription(qualification.getDescription());
			}
			returnList.add(to);
		}
		return returnList;
	}

	private List<ReportMarkTo> obtainAverageMarks(Map<Integer, ReportMarkTo> averageMarksMap) {
		List<ReportMarkTo> averageMarksLst = new ArrayList<ReportMarkTo>();
		if (withAverageMark() && averageMarksMap.size() > 0) {
			Iterator<ReportMarkTo> iterator = averageMarksMap.values().iterator();
			while (iterator.hasNext()) {
				averageMarksLst.add(iterator.next());
			}
		}
		return averageMarksLst;
	}

	private Double obtainFinalMark(Map<Integer, ReportMarkTo> averageMarksMap) {
		double finalMark = 0;
		if (withAverageMark() && averageMarksMap.size() > 0) {
			Iterator<ReportMarkTo> iterator = averageMarksMap.values().iterator();
			while (iterator.hasNext()) {
				ReportMarkTo markTo = iterator.next();
				finalMark += markTo.getMark().getMark();
			}
			finalMark = finalMark / averageMarksMap.size();
		}
		return finalMark;
	}

	private Qualification obtainQualification(Double mark) {
		if (mark != null) {
			try {
				IManagerBean qualificationBean = BeanManager.getManagerBean(Qualification.class);
				Criteria criteria = new Criteria();
				criteria.addLessThanOrEqualExpression(qualificationBean.getFieldName(IEntityAlias.QUALIFICATION_MIN_VALUE), mark);
				criteria.addGreaterThanOrEqualExpression(qualificationBean.getFieldName(IEntityAlias.QUALIFICATION_MAX_VALUE), mark);
				Iterator<ITransferObject> iter = qualificationBean.getList(criteria).iterator();
				if(iter.hasNext()){
					return ((Qualification)iter.next());
				}
			} catch (ManagerBeanException e) {
				LOGGER.log(Level.SEVERE, "Error obtaining code for mark: " + mark, e);
			}
		}
		return null;
	}

	private List<Absence> obtainAbsences(CourseAlumn courseAlumn, Integer evaluation){
		try {
			IManagerBean absenceBean = BeanManager.getManagerBean(Absence.class);
			Expression courseAlumnExp = ExpressionUtilities.getEqualExpression(absenceBean.getFieldName(IEntityAlias.ABSENCE_COURSE_ALUMN_ID), courseAlumn.getId());
			List<Absence> absencesLst = new ArrayList<Absence>();
			for(int i=1; i<=evaluation.intValue(); i++){
				Criteria criteria = new Criteria();
				criteria.addBetweenExpression(absenceBean.getFieldName(IEntityAlias.ABSENCE_ABSENCE_DATE), courseAlumn.getCourse().getStartDate(), courseAlumn.getCourse().getEndDate());
				Expression evalExp = ExpressionUtilities.getEqualExpression(absenceBean.getFieldName(IEntityAlias.ABSENCE_EVALUATION), new Integer(i));
				criteria.addExpression(ExpressionUtilities.getAndExpression(courseAlumnExp, evalExp));
				criteria.addOrder(absenceBean.getFieldName(IEntityAlias.ABSENCE_ABSENCE_DATE));
				Iterator<ITransferObject> iter = absenceBean.getList(criteria).iterator();
				
				if(absenceBean.getCount(criteria)==0){
					Absence emptyAbsence = new Absence();
					emptyAbsence.setCourseAlumn(courseAlumn);
					emptyAbsence.setEvaluation(i);
					emptyAbsence.setAbsenceDate(null);
					emptyAbsence.setComments(Integer.toString(absenceBean.getCount(criteria)));
					absencesLst.add(emptyAbsence);
				} else {
					while (iter.hasNext()){
						absencesLst.add((Absence)iter.next());
					}
				}
			}
			return absencesLst;
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining absences with course alumn = " + courseAlumn.getId(), e);
		}
		return null;
	}
	
	private List<EvaluationObservation> obtainObservations(CourseAlumn courseAlumn, Integer evaluation){
		try {
			IManagerBean observationBean = BeanManager.getManagerBean(EvaluationObservation.class);
			Expression courseAlumnExp = ExpressionUtilities.getEqualExpression(observationBean.getFieldName(IEntityAlias.EVALUATION_OBSERVATION_COURSE_ALUMN_ID), courseAlumn.getId());
			List<EvaluationObservation> observationsLst = new ArrayList<EvaluationObservation>();
			for(int i=1; i<=evaluation.intValue(); i++){
				Criteria criteria = new Criteria();
				Expression evalExp = ExpressionUtilities.getEqualExpression(observationBean.getFieldName(IEntityAlias.EVALUATION_OBSERVATION_EVALUATION), new Integer(i));
				criteria.addExpression(ExpressionUtilities.getAndExpression(courseAlumnExp, evalExp));
				Iterator<ITransferObject> iter = observationBean.getList(criteria).iterator();

				if(observationBean.getCount(criteria)==0){
					EvaluationObservation emptyObservation = new EvaluationObservation();
					emptyObservation.setCourseAlumn(courseAlumn);
					emptyObservation.setEvaluation(i);
					emptyObservation.setComments("");
					observationsLst.add(emptyObservation);
				} else {
					while (iter.hasNext()){
						observationsLst.add((EvaluationObservation)iter.next());
					}
				}
			}
			return observationsLst;
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
			criteria.addEqualExpression(courseScheduleBean.getFieldName(IEntityAlias.COURSE_SCHEDULE_COURSE_ID), courseAlumn.getCourse().getId());
			String courseScheduleStr = new String();
			Iterator<ITransferObject> iter = courseScheduleBean.getList(criteria).iterator();
			double hours = 0;
			CourseSchedule courseSchedule;
			while (iter.hasNext()){
				courseSchedule = (CourseSchedule)iter.next();
				courseScheduleStr = (courseScheduleStr.equals("")?courseScheduleStr:courseScheduleStr + "-");
				courseScheduleStr += courseSchedule.getDayOfWeek().getShortName(locale);
				hours += (courseSchedule.getEndTime().getTime() - courseSchedule.getStartTime().getTime())/1000.0/60.0/60.0;
			}
			return courseScheduleStr + " " + hours;
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining schedule with course alumn = " + courseAlumn.getId(), e);
		}
		return null;
	}

	public String getQualificationLegend() throws ManagerBeanException{
		String qualificationLegend = "";
		IManagerBean qualificationBean = BeanManager.getManagerBean(Qualification.class);
		Iterator<ITransferObject> iter = qualificationBean.getList(null).iterator();
		while (iter.hasNext()) {
			Qualification qualification = (Qualification)iter.next();
			qualificationLegend += qualification.getCode() + "-" + qualification.getDescription() + "; ";
		}
		return qualificationLegend;
	}
	
	public static class ReportMark implements ITransferObject {

		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
		
		private CourseAlumn courseAlumn;
		private List<ReportMarkTo> marks;
		private List<ReportMarkTo> averageMarks;
		private List<Absence> absences;
		private List<EvaluationObservation> observations;
		private String courseSchedule;
		private Integer evaluation;
		private Double finalMark;
		
		public CourseAlumn getCourseAlumn() {
			return courseAlumn;
		}
		public void setCourseAlumn(CourseAlumn courseAlumn) {
			this.courseAlumn = courseAlumn;
		}
		public List<ReportMarkTo> getMarks() {
			return marks;
		}
		public void setMarks(List<ReportMarkTo> marks) {
			this.marks = marks;
		}
		public List<ReportMarkTo> getAverageMarks() {
			return averageMarks;
		}
		public void setAverageMarks(List<ReportMarkTo> averageMarks) {
			this.averageMarks = averageMarks;
		}
		public List<Absence> getAbsences() {
			return absences;
		}
		public void setAbsences(List<Absence> absences) {
			this.absences = absences;
		}
		public List<EvaluationObservation> getObservations() {
			return observations;
		}
		public void setObservations(List<EvaluationObservation> observations) {
			this.observations = observations;
		}
		public String getCourseSchedule() {
			return courseSchedule;
		}
		public void setCourseSchedule(String courseSchedule) {
			this.courseSchedule = courseSchedule;
		}
		public Integer getEvaluation() {
			return evaluation;
		}
		public void setEvaluation(Integer evaluation) {
			this.evaluation = evaluation;
		}
		public Double getFinalMark() {
			return finalMark;
		}
		public void setFinalMark(Double finalMark) {
			this.finalMark = finalMark;
		}
	}
	
	public class ReportMarkTo {
		
		private Mark mark;
		private String code;
		private String description;
		
		public Mark getMark() {
			return mark;
		}
		public void setMark(Mark mark) {
			this.mark = mark;
		}
		public String getCode() {
			return code;
		}
		public void setCode(String code) {
			this.code = code;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
	}

}