package com.code.aon.project;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.esferalia.aon.entity.master.ProjectDB;

@Entity
@Table(name="project")
public class Project extends ProjectDB {

	private static final long serialVersionUID = 1L;

	public Project() {
		setDate(new Date());
		setActive(true);
	}

	@Transient
	public boolean isExtended() {
		return (isCommercial() || isTas());
	}
}
