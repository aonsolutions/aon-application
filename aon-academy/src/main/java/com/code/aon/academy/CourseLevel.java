package com.code.aon.academy;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.CourseLevelDB;

/**
 * The Class CourseLevel.
 */
@Entity
@Table(name="course_level")
public class CourseLevel extends CourseLevelDB {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;	

}
