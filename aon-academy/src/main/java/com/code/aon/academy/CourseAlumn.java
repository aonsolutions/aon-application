package com.code.aon.academy;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.code.aon.academy.enumeration.CourseAlumnStatus;
import com.esferalia.aon.entity.master.CourseAlumnDB;

/**
 * The Class CourseAlumn.
 */
@Entity
@Table(name="course_alumn")
public class CourseAlumn extends CourseAlumnDB {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

    public CourseAlumn() {
    	setStatus( CourseAlumnStatus.ACTIVE );
    }
	
}
