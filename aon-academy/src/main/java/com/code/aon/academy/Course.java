package com.code.aon.academy;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.code.aon.academy.enumeration.CourseStatus;
import com.esferalia.aon.entity.master.CourseDB;

/**
 * The Class Course.
 */
@Entity
@Table(name = "course")
public class Course extends CourseDB {

	private static final long serialVersionUID = 1L;

    public Course() {
    	setStatus( CourseStatus.ACTIVE );
    }

}