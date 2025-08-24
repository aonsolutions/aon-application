package com.esferalia.aon.occam.api.model.project;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.Status;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.registry.Project;
import com.esferalia.aon.occam.api.model.registry.Target;
import com.esferalia.aon.occam.api.model.task.TaskHolder;

public class ProjectTas extends Project implements Serializable {

	private static final long serialVersionUID = -8788077348103177173L;
	
	private String series;
	private Integer number;
	
	private Target target;
	
	private TasItem tasItem;
	
	private Double counter;
	private TaskHolder taskHolder;
	
	private String comments;
	private Status status;
	private Date statusDate;
	
	private Workplace workplace;
	
	public ProjectTas() {
		super();
	}

	public void copyFrom(Project project) {
		setId(project.getId());
		setDomain(project.getDomain());
		setType(project.getType());
		setRegistry(project.getRegistry());
		setName(project.getName());
		setAlias(project.getAlias());
		setDate(project.getDate());
		setTas(project.isTas());
		setCommercial(project.isCommercial());
		setReservation(project.isReservation());
		setActive(project.isActive());
		setProjectHolder(project.getProjectHolder());
		setProjectHolders(project.getProjectHolders());
		setProjectActivities(project.getProjectActivities());
		setDirty(project.isDirty());
	}

	public String getSeries() {
		return series;
	}

	public ProjectTas setSeries(String series) {
		this.series = series;
		return this;
	}

	public Integer getNumber() {
		return number;
	}

	public ProjectTas setNumber(Integer number) {
		this.number = number;
		return this;
	}

	public Target getTarget() {
		return target;
	}

	public ProjectTas setTarget(Target target) {
		this.target = target;
		return this;
	}

	public TasItem getTasItem() {
		return tasItem;
	}

	public ProjectTas setTasItem(TasItem tasItem) {
		this.tasItem = tasItem;
		return this;
	}

	public Double getCounter() {
		return counter;
	}

	public ProjectTas setCounter(Double counter) {
		this.counter = counter;
		return this;
	}

	public TaskHolder getTaskHolder() {
		return taskHolder;
	}

	public ProjectTas setTaskHolder(TaskHolder taskHolder) {
		this.taskHolder = taskHolder;
		return this;
	}

	public String getComments() {
		return comments;
	}

	public ProjectTas setComments(String comments) {
		this.comments = comments;
		return this;
	}

	public Status getStatus() {
		return status;
	}

	public ProjectTas setStatus(Status status) {
		this.status = status;
		return this;
	}

	public Date getStatusDate() {
		return statusDate;
	}

	public ProjectTas setStatusDate(Date statusDate) {
		this.statusDate = statusDate;
		return this;
	}

	public Workplace getWorkplace() {
		return workplace;
	}

	public ProjectTas setWorkplace(Workplace workplace) {
		this.workplace = workplace;
		return this;
	}

}
