package com.code.aon.ui.academy.controller;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.model.ListDataModel;

import com.code.aon.academy.CourseInstructor;
import com.code.aon.academy.CourseSchedule;
import com.code.aon.academy.dao.IAcademyAlias;
import com.code.aon.academy.enumeration.CourseStatus;
import com.code.aon.academy.print.ReportCourseInstructor;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.enumeration.WeekDay;
import com.code.aon.company.resources.Employee;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.academy.model.InstructorWeeklyHours;
import com.code.aon.ui.employee.controller.EmployeeController;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.report.OutputFormat;
import com.code.aon.ui.report.ReportException;
import com.code.aon.ui.report.controller.ReportManager;
import com.code.aon.ui.util.AonUtil;

public class InstructorCourseController extends LinesController {

	private static final String INSTRUCTOR_CONTROLLER_NAME = "employee";

	private static final Logger LOGGER = Logger.getLogger(InstructorCourseController.class.getName());

	public String getCourseSchedule() throws ManagerBeanException{
		CourseInstructor courseInstructor = (CourseInstructor)this.getModel().getRowData();
		return getCourseSchedule(courseInstructor.getCourse().getId());
	}

	@SuppressWarnings("unchecked")
	private String getCourseSchedule(Integer courseId) throws ManagerBeanException  {
		String schedule = new String();
		IManagerBean courseScheduleBean = BeanManager.getManagerBean(CourseSchedule.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(courseScheduleBean.getFieldName(IAcademyAlias.COURSE_SCHEDULE_COURSE_ID), courseId);
		Iterator iter = courseScheduleBean.getList(criteria).iterator();
		while(iter.hasNext()){
			CourseSchedule courseSchedule = (CourseSchedule)iter.next();
			schedule = schedule.concat(courseSchedule.getDay().getShortName(AonUtil.getCurrentLocale()) + " ");
		}
		return schedule;
		
	}

	public String getCourseHours() throws ManagerBeanException {
		NumberFormat formatter = new DecimalFormat("#,##0.00"); 
		CourseInstructor courseInstructor = (CourseInstructor)this.getModel().getRowData();
		return formatter.format(getCourseHours(courseInstructor.getCourse().getId()));
	}

	@SuppressWarnings("unchecked")
	private double getCourseHours(Integer courseId) throws ManagerBeanException {
		long sumMiliseconds = 0;
		IManagerBean courseScheduleBean = BeanManager.getManagerBean(CourseSchedule.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(courseScheduleBean.getFieldName(IAcademyAlias.COURSE_SCHEDULE_COURSE_ID), courseId);
		Iterator iter = courseScheduleBean.getList(criteria).iterator();
		while(iter.hasNext()){
			CourseSchedule courseSchedule = (CourseSchedule)iter.next();
			sumMiliseconds += courseSchedule.getEndTime().getTime() - courseSchedule.getStartTime().getTime();
		}
		return (double)sumMiliseconds / (1000 * 60 * 60);
	}

	@SuppressWarnings("unchecked")
	public String getTotalCourseHours() throws ManagerBeanException {
		NumberFormat formatter = new DecimalFormat("#,##0.00"); 
		EmployeeController instructorController = (EmployeeController)AonUtil.getController(INSTRUCTOR_CONTROLLER_NAME);
		Integer instructorId = ((Employee)instructorController.getTo()).getId();
		double totalHours = 0;
		try {
			IManagerBean courseInstructorBean = BeanManager.getManagerBean(CourseInstructor.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(courseInstructorBean.getFieldName(IAcademyAlias.COURSE_INSTRUCTOR_EMPLOYEE_ID), instructorId);
			criteria.addEqualExpression(courseInstructorBean.getFieldName(IAcademyAlias.COURSE_INSTRUCTOR_COURSE_STATUS), CourseStatus.ACTIVE);
			Iterator iterator = courseInstructorBean.getList(criteria).iterator();
			while (iterator.hasNext()) {
				CourseInstructor courseInstructor = (CourseInstructor)iterator.next();
				totalHours += getCourseHours(courseInstructor.getCourse().getId());
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error getting courseInstructor list", e);
		}
		return formatter.format(totalHours);
	}

	@SuppressWarnings("unchecked")
	public ListDataModel getTotalWeeklyHours() {
		EmployeeController instructorController = (EmployeeController)AonUtil.getController(INSTRUCTOR_CONTROLLER_NAME);
		InstructorWeeklyHours weeklyHours = new InstructorWeeklyHours();
		weeklyHours.setInstructor((Employee)instructorController.getTo());
		weeklyHours.setValues(new Double[7]);
		for (int i=0; i<weeklyHours.getValues().length; weeklyHours.getValues()[i] = new Double(0), i++);

		try {
			IManagerBean courseInstructorBean = BeanManager.getManagerBean(CourseInstructor.class);
			IManagerBean courseScheduleBean = BeanManager.getManagerBean(CourseSchedule.class);

			Criteria criteria = new Criteria();
			criteria.addEqualExpression(courseInstructorBean.getFieldName(IAcademyAlias.COURSE_INSTRUCTOR_EMPLOYEE_ID), weeklyHours.getInstructor().getId());
			criteria.addEqualExpression(courseInstructorBean.getFieldName(IAcademyAlias.COURSE_INSTRUCTOR_COURSE_STATUS), CourseStatus.ACTIVE);
			Iterator iterator = courseInstructorBean.getList(criteria).iterator();
			while (iterator.hasNext()) {
				CourseInstructor courseInstructor = (CourseInstructor)iterator.next();

				criteria = new Criteria();
				criteria.addEqualExpression(courseScheduleBean.getFieldName(IAcademyAlias.COURSE_SCHEDULE_COURSE_ID), courseInstructor.getCourse().getId());
				Iterator iter = courseScheduleBean.getList(criteria).iterator();
				while(iter.hasNext()){
					CourseSchedule courseSchedule = (CourseSchedule)iter.next();
					long sumMiliseconds = courseSchedule.getEndTime().getTime() - courseSchedule.getStartTime().getTime();
					double hours = weeklyHours.getValues()[courseSchedule.getDay().ordinal()].doubleValue();
					hours += (double)sumMiliseconds / (1000 * 60 * 60);
					weeklyHours.getValues()[courseSchedule.getDay().ordinal()] = new Double(hours);
				}
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error getting courseInstructor list", e);
		}

    	List<InstructorWeeklyHours> instructorWeeklyHoursList = new LinkedList<InstructorWeeklyHours>();
    	instructorWeeklyHoursList.add(weeklyHours);
		return new ListDataModel(instructorWeeklyHoursList);
	}

	public List<InstructorWeeklyHoursHeader> getInstructorWeeklyHoursHeader() {
		List<InstructorWeeklyHoursHeader> list = new LinkedList<InstructorWeeklyHoursHeader>();
    	for (int i=0; i<WeekDay.values().length; i++){
        	InstructorWeeklyHoursHeader instructorWeeklyHoursHeader = new InstructorWeeklyHoursHeader();
        	instructorWeeklyHoursHeader.setPosition(i);
        	instructorWeeklyHoursHeader.setWeekDay(WeekDay.values()[i].getName(AonUtil.getCurrentLocale()));
        	list.add(i, instructorWeeklyHoursHeader);
    	}
		return list;
	}

	public String onExecute() throws ReportException, DAOException{
		ReportManager manager = (ReportManager)AonUtil.getRegisteredBean("report");
        manager.setReportKey("instructorCourse");
        manager.setOutputFormat(OutputFormat.PDF);
        String outcome = manager.onExecute();
        return outcome;
	}

	@SuppressWarnings("unchecked")
	public Collection getCollection() {
		List<ReportCourseInstructor> reportCourseInstructorList = new LinkedList<ReportCourseInstructor>();
		try{
			Iterator iterator = getManagerBean().getList(getCriteria()).iterator();
			while (iterator.hasNext()) {
				ReportCourseInstructor reportCourseInstructor = new ReportCourseInstructor();
				CourseInstructor courseInstructor = (CourseInstructor)iterator.next();
				reportCourseInstructor.setCourseInstructor(courseInstructor);
				reportCourseInstructor.setCourseSchedule(getCourseSchedule(courseInstructor.getCourse().getId()));
				reportCourseInstructor.setCourseHours(getCourseHours(courseInstructor.getCourse().getId()));

				reportCourseInstructorList.add(reportCourseInstructor);
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining marks", e);
		}
		return reportCourseInstructorList;
	}

	@SuppressWarnings("unchecked")
	public Collection getWeeklyHoursCollection() {
		return (Collection)getTotalWeeklyHours().getWrappedData();
	}

	public class InstructorWeeklyHoursHeader{
    	
    	private int position; 
    	
    	private String weekDay;
    	
		public int getPosition() {
			return position;
		}

		public void setPosition(int position) {
			this.position = position;
		}
		
		public String getWeekDay() {
			return weekDay;
		}

		public void setWeekDay(String weekDay) {
			this.weekDay = weekDay;
		}
    }
}
