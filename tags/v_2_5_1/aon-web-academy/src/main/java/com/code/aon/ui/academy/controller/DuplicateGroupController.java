package com.code.aon.ui.academy.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import com.code.aon.academy.AcademicYear;
import com.code.aon.academy.Course;
import com.code.aon.academy.CourseAcademicSkill;
import com.code.aon.academy.CourseInstructor;
import com.code.aon.academy.CourseSchedule;
import com.code.aon.academy.dao.IAcademyAlias;
import com.code.aon.academy.enumeration.CourseStatus;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.menu.jsf.MenuEvent;
import com.code.aon.ui.menu.jsf.MenuManager;
import com.code.aon.ui.util.AonUtil;

public class DuplicateGroupController extends BasicController {
	
	private static final Logger LOGGER = Logger.getLogger(DuplicateGroupController.class.getName());
	
	private static final String COURSE_CONTROLLER_NAME = "course";

	private static final String MENU_MANAGER_NAME = "menuManager";

	private ArrayList<Course> checks = new ArrayList<Course>();
	
	private Integer academicYearId;
	
	private String dateIncrement;
	
	private Boolean instructors;
	
	private Boolean schedules;
	
	private Boolean skills;
	
	public Integer getAcademicYearId() {
		return academicYearId;
	}

	public void setAcademicYearId(Integer academicYearId) {
		this.academicYearId = academicYearId;
	}

	public String getDateIncrement() {
		return dateIncrement;
	}

	public void setDateIncrement(String dateIncrement) {
		this.dateIncrement = dateIncrement;
	}

	public Boolean getInstructors() {
		return instructors;
	}

	public void setInstructors(Boolean instructors) {
		this.instructors = instructors;
	}

	public Boolean getSchedules() {
		return schedules;
	}

	public void setSchedules(Boolean schedules) {
		this.schedules = schedules;
	}

	public Boolean getSkills() {
		return skills;
	}

	public void setSkills(Boolean skills) {
		this.skills = skills;
	}

	public void rowSelected(ValueChangeEvent event) {
		if (event.getNewValue() != null) {
			setRowChecked(((Boolean) event.getNewValue()).booleanValue());
		}
	}

	@SuppressWarnings({"unused","unchecked"})
	public void checkAll(ActionEvent event) throws ManagerBeanException{
		Iterator iter = this.getManagerBean().getList(this.getCriteria()).iterator();
		while(iter.hasNext()){
			Course detail = (Course)iter.next();
			if (!checks.contains( detail )) {
				checks.add( detail );
			}
		}
	}

	@SuppressWarnings("unused")
	public void checkNone(ActionEvent event) {
		clearCheckedCourses();
	}

	public boolean getRowChecked() {
		Course to = (Course) model.getRowData();
		return checks.contains(to);
	}

	public void setRowChecked(boolean rowChecked) {
		if (rowChecked) {
			Course to = (Course) model.getRowData();
			if (!checks.contains(to)) {
				checks.add(to);
			}
		} else {
			Course to = (Course) model.getRowData();
			if (checks.contains(to)) {
				checks.remove(to);
			}
		}
	}

	public void clearCheckedCourses() {
		checks = new ArrayList<Course>();
	}
	
	public void onEditSearch(MenuEvent event){
		this.onEditSearch((ActionEvent)event);
	}

	@Override
	public void onEditSearch(ActionEvent event) {
		initializeParams();
		clearCheckedCourses();
		super.onEditSearch(event);
		try {
			getCriteria().addOrder(getManagerBean().getFieldName(IAcademyAlias.COURSE_CODE));
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("Unable to apply criteria");
			LOGGER.log(Level.SEVERE, "Unable to apply criteria", e);
			throw new AbortProcessingException(e);
		}
	}
	
	private void initializeParams() {
		dateIncrement = "12";
		instructors = false;
		schedules = false;
		skills = false;
	}

	@SuppressWarnings("unchecked")
	public void onDuplicate(ActionEvent event){
		try {
			CourseController courseController = (CourseController)AonUtil.getController(COURSE_CONTROLLER_NAME);
			Criteria criteria = new Criteria();
			if(academicYearId != null && checks.size() > 0){
				IManagerBean courseBean = BeanManager.getManagerBean(Course.class);
				AcademicYear academicYear = obtainAcademicYear();
				Iterator iter = checks.iterator();
				while(iter.hasNext()){
					Course course = (Course)iter.next();
					GregorianCalendar startCalendar = new GregorianCalendar();
					startCalendar.setTime(course.getStartDate());
					startCalendar.add(Calendar.MONTH, Integer.parseInt(dateIncrement));
					GregorianCalendar endCalendar = new GregorianCalendar();
					endCalendar.setTime(course.getEndDate());
					endCalendar.add(Calendar.MONTH, Integer.parseInt(dateIncrement));

					Course newCourse = new Course();
					newCourse.setAcademicYear(academicYear);
					newCourse.setStartDate(startCalendar.getTime());
					newCourse.setEndDate(endCalendar.getTime());
					newCourse.setStatus(CourseStatus.ACTIVE);
					newCourse.setCode(course.getCode());
					newCourse.setCourseSubject(course.getCourseSubject());
					newCourse.setCourseLevel(course.getCourseLevel());
					newCourse.setWorkPlace(course.getWorkPlace());
					newCourse.setAlumnLimit(course.getAlumnLimit());
					newCourse.setComments(course.getComments());
					newCourse = (Course)courseBean.insert(newCourse);
					criteria.addOrExpression(courseController.getFieldName(IAcademyAlias.COURSE_ID), newCourse.getId().toString());

					if (instructors) {
						createCourseInstructors(course.getId(), newCourse);
					}
					if (schedules) {
						createCourseSchedules(course.getId(), newCourse);
					}
					if (skills) {
						createCourseSkills(course.getId(), newCourse);
					}
				}
			}
			courseController.setCriteria(criteria);
			courseController.onSearch(null);
			updateBreadCrumb();
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("Unable to assign the academic skill to selected courses");
			LOGGER.log(Level.SEVERE, "Unable to assign the academic skill to selected courses", e);
			throw new AbortProcessingException(e);
		} catch (ExpressionException e) {
			AonUtil.addErrorMessage("Unable to assign the academic skill to selected courses");
			LOGGER.log(Level.SEVERE, "Unable to assign the academic skill to selected courses", e);
			throw new AbortProcessingException(e);
		}
	}

	private void updateBreadCrumb() {
		MenuManager menuManager = (MenuManager)AonUtil.getRegisteredBean(MENU_MANAGER_NAME);
        menuManager.setCurrentMenu("AON_APP");
        menuManager.getCurrentMenuModel().setSelectedNode("root.aon_course");	
    }

	@SuppressWarnings("unchecked")
	private AcademicYear obtainAcademicYear() {
		try {
			IManagerBean academicYearBean = BeanManager.getManagerBean(AcademicYear.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(academicYearBean.getFieldName(IAcademyAlias.ACADEMIC_YEAR_ID), academicYearId);
			Iterator iter = academicYearBean.getList(criteria, 0, 1).iterator();
			if(iter.hasNext()){
				return (AcademicYear)iter.next();
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining academic year with id = " + academicYearId, e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private void createCourseInstructors(Integer courseId, Course newCourse) {
		try {
			IManagerBean courseInstructorBean = BeanManager.getManagerBean(CourseInstructor.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(courseInstructorBean.getFieldName(IAcademyAlias.COURSE_INSTRUCTOR_COURSE_ID), courseId);
			Iterator iter = courseInstructorBean.getList(criteria).iterator();
			while (iter.hasNext()) {
				CourseInstructor courseInstructor = (CourseInstructor)iter.next();
				CourseInstructor newCourseInstructor = new CourseInstructor();
				newCourseInstructor.setCourse(newCourse);
				newCourseInstructor.setEmployee(courseInstructor.getEmployee());
				newCourseInstructor.setType(courseInstructor.getType());
				courseInstructorBean.insert(newCourseInstructor);
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error creating course instructors for course with id = " + newCourse.getId(), e);
		}
	}

	@SuppressWarnings("unchecked")
	private void createCourseSchedules(Integer courseId, Course newCourse) {
		try {
			IManagerBean courseScheduleBean = BeanManager.getManagerBean(CourseSchedule.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(courseScheduleBean.getFieldName(IAcademyAlias.COURSE_SCHEDULE_COURSE_ID), courseId);
			Iterator iter = courseScheduleBean.getList(criteria).iterator();
			while (iter.hasNext()) {
				CourseSchedule courseSchedule = (CourseSchedule)iter.next();
				CourseSchedule newCourseSchedule = new CourseSchedule();
				newCourseSchedule.setCourse(newCourse);
				newCourseSchedule.setDay(courseSchedule.getDay());
				newCourseSchedule.setStartTime(courseSchedule.getStartTime());
				newCourseSchedule.setEndTime(courseSchedule.getEndTime());
				courseScheduleBean.insert(newCourseSchedule);
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error creating course schedules for course with id = " + newCourse.getId(), e);
		}
	}

	@SuppressWarnings("unchecked")
	private void createCourseSkills(Integer courseId, Course newCourse) {
		try {
			IManagerBean courseAcademicSkillBean = BeanManager.getManagerBean(CourseAcademicSkill.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(courseAcademicSkillBean.getFieldName(IAcademyAlias.COURSE_ACADEMIC_SKILL_COURSE_ID), courseId);
			Iterator iter = courseAcademicSkillBean.getList(criteria).iterator();
			while (iter.hasNext()) {
				CourseAcademicSkill courseAcademicSkill = (CourseAcademicSkill)iter.next();
				CourseAcademicSkill newCourseAcademicSkill = new CourseAcademicSkill();
				newCourseAcademicSkill.setCourse(newCourse);
				newCourseAcademicSkill.setAcademicSkill(courseAcademicSkill.getAcademicSkill());
				courseAcademicSkillBean.insert(newCourseAcademicSkill);
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error creating course academic skills for course with id = " + newCourse.getId(), e);
		}
	}

}