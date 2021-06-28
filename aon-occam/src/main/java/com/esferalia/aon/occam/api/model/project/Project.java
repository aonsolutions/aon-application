package com.esferalia.aon.occam.api.model.project;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.registry.Registry;

public class Project implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private Integer domain;
	private String name;
	private String alias;
	private Registry registry;
	private Date date;
	private ProjectType projectType;
	private Boolean tas;
	private Boolean commercial;
	private Boolean reservation;
	private Boolean active;
	
	public Project() {
	
	}

	public Integer getId() {
		return id;
	}

	public Project setId(Integer id) {
		this.id = id;
		return this;
	}

	public Integer getDomain() {
		return domain;
	}

	public Project setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}

	public String getName() {
		return name;
	}

	public Project setName(String name) {
		this.name = name;
		return this;
	}

	public String getAlias() {
		return alias;
	}

	public Project setAlias(String alias) {
		this.alias = alias;
		return this;
	}

	public Registry getRegistry() {
		return registry;
	}

	public Project setRegistry(Registry registry) {
		this.registry = registry;
		return this;
	}

	public Date getDate() {
		return date;
	}

	public Project setDate(Date date) {
		this.date = date;
		return this;
	}

	public ProjectType getProjectType() {
		return projectType;
	}

	public Project setProjectType(ProjectType projectType) {
		this.projectType = projectType;
		return this;
	}

	public Boolean getTas() {
		return tas;
	}

	public Project setTas(Boolean tas) {
		this.tas = tas;
		return this;
	}

	public Boolean getCommercial() {
		return commercial;
	}

	public Project setCommercial(Boolean commercial) {
		this.commercial = commercial;
		return this;
	}

	public Boolean getReservation() {
		return reservation;
	}

	public Project setReservation(Boolean reservation) {
		this.reservation = reservation;
		return this;
	}

	public Boolean getActive() {
		return active;
	}

	public Project setActive(Boolean active) {
		this.active = active;
		return this;
	}
	
}
