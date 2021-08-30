package com.esferalia.aon.occam.api.model.registry;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.project.ProjectType;

public class Project implements Serializable {

	private static final long serialVersionUID = -5945149996376642583L;
	
	private Integer id;
	private int domain;
	private ProjectType type;
	private Integer projectTypeId;
	private String projectTypeName;
	
	private Registry registry;
	private String name;
	private String alias;
	private Date date;
	private boolean tas;
	private boolean commercial;
	private boolean reservation;
	private boolean active;
	
	public Integer getId() {
		return id;
	}
	
	public Project setId(Integer id) {
		this.id = id;
		return this;
	}
	
	public int getDomain() {
		return domain;
	}
	
	public Project setDomain(int domain) {
		this.domain = domain;
		return this;
	}
	
	public Integer getProjectTypeId() {
		return projectTypeId;
	}
	
	public Project setProjectTypeId(Integer projectTypeId) {
		this.projectTypeId = projectTypeId;
		return this;
	}
	
	public String getProjectTypeName() {
		return projectTypeName;
	}
	
	public Project setProjectTypeName(String projectTypeName) {
		this.projectTypeName = projectTypeName;
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
	
	public Date getDate() {
		return date;
	}
	
	public Project setDate(Date date) {
		this.date = date;
		return this;
	}
	
	public boolean isTas() {
		return tas;
	}
	
	public Project setTas(boolean tas) {
		this.tas = tas;
		return this;
	}
	
	public boolean isCommercial() {
		return commercial;
	}
	
	public Project setCommercial(boolean commercial) {
		this.commercial = commercial;
		return this;
	}
	
	public boolean isReservation() {
		return reservation;
	}
	
	public Project setReservation(boolean reservation) {
		this.reservation = reservation;
		return this;
	}
	
	public boolean isActive() {
		return active;
	}
	
	public Project setActive(boolean active) {
		this.active = active;
		return this;
	}
	
	public Registry getRegistry() {
		if(registry == null) {
			registry = new Registry();
		}
		return registry;
	}
	
	public Project setRegistry(Registry registry) {
		this.registry = registry;
		return this;
	}
	
	public ProjectType getType() {
		return type;
	}
	
	public Project setType(ProjectType type) {
		this.type = type;
		return this;
	}
}
