package com.code.aon.academy;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.CourseEvaluationDB;

/**
 * The Class CourseEvaluation.
 */
@Entity
@Table(name="course_evaluation")
public class CourseEvaluation extends CourseEvaluationDB {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

}