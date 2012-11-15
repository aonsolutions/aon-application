package com.code.aon.academy;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.academy.enumeration.CourseAlumnStatus;
import com.code.aon.academy.enumeration.CourseStatus;
import com.esferalia.aon.entity.master.CourseDB;

/**
 * The Class Course.
 */
@Entity
@Table(name = "course")
public class Course extends CourseDB {

	private static final long serialVersionUID = 1L;

	private Set<CourseAlumn> alumns = new HashSet<CourseAlumn>();

    public Course() {
    	setStatus( CourseStatus.ACTIVE );
    }

	@OneToMany(mappedBy = "course", cascade={CascadeType.REMOVE})
	@OrderBy()
	public Set<CourseAlumn> getAlumns() {
		return this.alumns;
	}
	public void setAlumns(Set<CourseAlumn> alumns) {
		this.alumns = alumns;
	}
	
	@Transient
	public Course getCourse(){
		return this;
	}
	
	@Transient
	public Integer getAlumnCount(){
		int count = 0;
		for(CourseAlumn alumn: getAlumns()){
			if(alumn.getStatus()==CourseAlumnStatus.ACTIVE){
				count++;
			}
		}
		return count;
	}
    

}