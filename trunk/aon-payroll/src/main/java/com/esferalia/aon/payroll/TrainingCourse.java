package com.esferalia.aon.payroll;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.code.aon.common.annotations.Heritable;
import com.esferalia.aon.entity.master.TrainingCourseDB;

@Entity
@Table(name="training_course")
@Heritable
public class TrainingCourse extends TrainingCourseDB {

	private static final long serialVersionUID = 1L;
	
		
}
