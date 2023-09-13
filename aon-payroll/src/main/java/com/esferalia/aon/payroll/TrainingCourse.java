package com.esferalia.aon.payroll;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.code.aon.common.annotations.Heritable;
import com.esferalia.aon.entity.master.TrainingCourseDB;

@Entity
@Table(name="training_course")
@Heritable
public class TrainingCourse extends TrainingCourseDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
		
}
