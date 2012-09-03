package com.code.aon.academy;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.CourseAcademicSkillDB;

/**
 * The Class CourseInstructor.
 */
@Entity
@Table(name="course_academicskill")
public class CourseAcademicSkill extends CourseAcademicSkillDB {
	
	private static final long serialVersionUID = 1L;

}

