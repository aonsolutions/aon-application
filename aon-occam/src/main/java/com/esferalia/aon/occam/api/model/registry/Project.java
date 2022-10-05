package com.esferalia.aon.occam.api.model.registry;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.project.ProjectHolder;
import com.esferalia.aon.occam.api.model.project.ProjectType;

public class Project implements Serializable {

	private static final long serialVersionUID = -5945149996376642583L;
	
	private Integer id;
	private Domain domain;
	private ProjectType type;
	
	private Registry registry;
	private String name;
	private String alias;
	private Date date;
	private boolean tas;
	private boolean commercial;
	private boolean reservation;
	private boolean active;
	
	private ProjectHolder projectHolder;

	private boolean dirty;
	
	public Integer getId() {
		return id;
	}
	
	public Project setId(Integer id) {
		this.id = id;
		return this;
	}
	
	public Domain getDomain() {
		if(this.domain == null) {
			this.domain = new Domain();
		}
		return domain;
	}
	
	public Project setDomain(Domain domain) {
		this.domain = domain;
		return this;
	}
	
	@Deprecated
	public Integer getProjectTypeId() {
		return getType().getId();
	}

	@Deprecated
	public Project setProjectTypeId(Integer projectTypeId) {
		getType().setId(projectTypeId);
		return this;
	}
	
	@Deprecated
	public String getProjectTypeName() {
		return getType().getDescription();
	}
	
	@Deprecated
	public Project setProjectTypeName(String projectTypeName) {
		getType().setDescription(projectTypeName);
		return this;
	}
	
	public String getName() {
		return name;
	}
	
	public Project setName(String name) {
		setDirty(true);
		this.name = name;
		return this;
	}
	
	public String getAlias() {
		return alias;
	}
	
	public Project setAlias(String alias) {
		setDirty(true);
		this.alias = alias;
		return this;
	}
	
	public Date getDate() {
		return date;
	}
	
	public Project setDate(Date date) {
		setDirty(true);
		this.date = date;
		return this;
	}
	
	public boolean isTas() {
		return tas;
	}
	
	public Project setTas(boolean tas) {
		setDirty(true);
		this.tas = tas;
		return this;
	}
	
	public boolean isCommercial() {
		return commercial;
	}
	
	public Project setCommercial(boolean commercial) {
		setDirty(true);
		this.commercial = commercial;
		return this;
	}
	
	public boolean isReservation() {
		return reservation;
	}
	
	public Project setReservation(boolean reservation) {
		setDirty(true);
		this.reservation = reservation;
		return this;
	}
	
	public boolean isActive() {
		return active;
	}
	
	public Project setActive(boolean active) {
		setDirty(true);
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
		setDirty(true);
		this.registry = registry;
		return this;
	}
	
	public ProjectType getType() {
		if(type == null) {
			type = new ProjectType();
		}
		return type;
	}
	
	public Project setType(ProjectType type) {
		setDirty(true);
		this.type = type;
		return this;
	}
	
	public ProjectHolder getProjectHolder() {
		if(this.projectHolder == null) {
			this.projectHolder = new ProjectHolder();
		}
		return projectHolder;
 	}
	
	public Project setProjectHolder(ProjectHolder projectHolder) {
		setDirty(true);
		this.projectHolder = projectHolder;
		return this;
	}
	
	public boolean isDirty() {
		return dirty;
	}
	
	public Project setDirty(boolean dirty) {
		this.dirty = dirty;
		return this;
	}
	
	protected <T extends Project> T copy(Project project, T child) {
		if (registry != null) {
			child.setId(project.getId());
			child.setDomain(project.getDomain());
			child.setName(project.getName());
			child.setAlias(project.getAlias());
			child.setRegistry(project.getRegistry());
			child.setDate(project.getDate());
			child.setType(project.getType());
			child.setActive(project.isActive());
			child.setCommercial(project.isCommercial());
			child.setReservation(project.isReservation());
			child.setTas(project.isTas());
			child.setDirty(project.isDirty());
		}
		return child;
	}
	
	public void setValues(Project project) {
		setId(project.getId());
		setDomain(project.getDomain());
		setName(project.getName());
		setAlias(project.getAlias());
		setRegistry(project.getRegistry());
		setDate(project.getDate());
		setType(project.getType());
		setActive(project.isActive());
		setCommercial(project.isCommercial());
		setReservation(project.isReservation());
		setTas(project.isTas());
		setDirty(project.isDirty());
		setProjectHolder(project.getProjectHolder());
	}
	
	
	public boolean isEmpty() {
		return getId() == null && getDomain().getId() == null
			&& getType().isEmpty() && getRegistry().isEmpty()
			&& getName() == null && getAlias() == null
			&& getDate() == null;
	}
	
}
