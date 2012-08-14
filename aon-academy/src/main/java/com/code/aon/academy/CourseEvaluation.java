package com.code.aon.academy;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.CourseEvaluationDB;

/**
 * The Class CourseEvaluation.
 */
@Entity
@Table(name="course_evaluation")
public class CourseEvaluation extends CourseEvaluationDB {
	
	private static final long serialVersionUID = 1L;

}