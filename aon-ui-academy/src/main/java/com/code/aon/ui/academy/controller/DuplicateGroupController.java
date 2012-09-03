package com.code.aon.ui.academy.controller;

import java.io.Serializable;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.academy.AcademicYear;
import com.code.aon.academy.Course;
import com.code.aon.academy.CourseAcademicSkill;
import com.code.aon.academy.CourseInstructor;
import com.code.aon.academy.CourseSchedule;
import com.code.aon.academy.enumeration.CourseStatus;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class DuplicateGroupController extends GroupSelectionController {

	private static final Logger LOGGER = LoggerFactory.getLogger(DuplicateGroupController.class);
	
	private AcademicYear academicYear;
	
	private String dateIncrement;
	
	private Boolean instructors;
	
	private Boolean schedules;
	
	private Boolean skills;
	
	public AcademicYear getAcademicYear() {
		return academicYear;
	}

	public void setAcademicYear(AcademicYear academicYear) {
		this.academicYear = academicYear;
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

	@Override
	public void onEditSearch(ActionEvent event) {
		super.onEditSearch(event);
		initializeParams();
		try {
			setAcademicYear((AcademicYear)BeanManager.getManagerBean(AcademicYear.class).createNewTo());
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
			throw new AbortProcessingException(e);
		}
	}
	
	private void initializeParams() {
		dateIncrement = "12";
		instructors = false;
		schedules = false;
		skills = false;
	}

	public void onDuplicate(ActionEvent event){
		try {
			if( getAcademicYear().getId() != null ) {
				List<Serializable> courseIds = new LinkedList<Serializable>();
				IManagerBean courseBean = BeanManager.getManagerBean(Course.class);
				for( Serializable id : getCheckList() ) {
					Course course = (Course) courseBean.get(id);
					GregorianCalendar startCalendar = new GregorianCalendar();
					startCalendar.setTime(course.getStartDate());
					startCalendar.add(Calendar.MONTH, Integer.parseInt(dateIncrement));
					GregorianCalendar endCalendar = new GregorianCalendar();
					endCalendar.setTime(course.getEndDate());
					endCalendar.add(Calendar.MONTH, Integer.parseInt(dateIncrement));

					Course newCourse = new Course();
					newCourse.setAcademicYear(getAcademicYear());
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
					courseIds.add(newCourse.getId());

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
				updateCourseController(event, courseIds);
			}
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("Unable to assign the academic skill to selected courses");
			LOGGER.error("Unable to assign the academic skill to selected courses", e);
			throw new AbortProcessingException(e);
		}
	}

	private void createCourseInstructors(Integer courseId, Course newCourse) {
		try {
			IManagerBean courseInstructorBean = BeanManager.getManagerBean(CourseInstructor.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(courseInstructorBean.getFieldName(IEntityAlias.COURSE_INSTRUCTOR_COURSE_ID), courseId);
			for( ITransferObject to : courseInstructorBean.getList(criteria) ) {
				CourseInstructor courseInstructor = (CourseInstructor) to;
				CourseInstructor newCourseInstructor = new CourseInstructor();
				newCourseInstructor.setCourse(newCourse);
				newCourseInstructor.setEmployee(courseInstructor.getEmployee());
				newCourseInstructor.setType(courseInstructor.getType());
				courseInstructorBean.insert(newCourseInstructor);
			}
		} catch (ManagerBeanException e) {
			LOGGER.error("Error creating course instructors for course with id = " + newCourse.getId(), e);
		}
	}

	private void createCourseSchedules(Integer courseId, Course newCourse) {
		try {
			IManagerBean courseScheduleBean = BeanManager.getManagerBean(CourseSchedule.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(courseScheduleBean.getFieldName(IEntityAlias.COURSE_SCHEDULE_COURSE_ID), courseId);
			for( ITransferObject to : courseScheduleBean.getList(criteria) ) {
				CourseSchedule courseSchedule = (CourseSchedule) to;
				CourseSchedule newCourseSchedule = new CourseSchedule();
				newCourseSchedule.setCourse(newCourse);
				newCourseSchedule.setDayOfWeek(courseSchedule.getDayOfWeek());
				newCourseSchedule.setStartTime(courseSchedule.getStartTime());
				newCourseSchedule.setEndTime(courseSchedule.getEndTime());
				courseScheduleBean.insert(newCourseSchedule);
			}
		} catch (ManagerBeanException e) {
			LOGGER.error("Error creating course schedules for course with id = " + newCourse.getId(), e);
		}
	}

	private void createCourseSkills(Integer courseId, Course newCourse) {
		try {
			IManagerBean courseAcademicSkillBean = BeanManager.getManagerBean(CourseAcademicSkill.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(courseAcademicSkillBean.getFieldName(IEntityAlias.COURSE_ACADEMIC_SKILL_COURSE_ID), courseId);
			for( ITransferObject to : courseAcademicSkillBean.getList(criteria) ) {
				CourseAcademicSkill courseAcademicSkill = (CourseAcademicSkill) to;
				CourseAcademicSkill newCourseAcademicSkill = new CourseAcademicSkill();
				newCourseAcademicSkill.setCourse(newCourse);
				newCourseAcademicSkill.setAcademicSkill(courseAcademicSkill.getAcademicSkill());
				newCourseAcademicSkill.setWeight(courseAcademicSkill.getWeight());
				courseAcademicSkillBean.insert(newCourseAcademicSkill);
			}
		} catch (ManagerBeanException e) {
			LOGGER.error("Error creating course academic skills for course with id = " + newCourse.getId(), e);
		}
	}

}