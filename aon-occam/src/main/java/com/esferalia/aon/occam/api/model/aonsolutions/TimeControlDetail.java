package com.esferalia.aon.occam.api.model.aonsolutions;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.task.TaskHolder;

public class TimeControlDetail implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private Integer modificatedTimeControl;
	private Domain domain;
	private TimeControlStatus status;
	private TimeControlCause cause;
	private TaskHolder taskHolder;
	private Date date;
	private String comments;
	private Location location;
	private String locationDescription;
	private Coordinates coordinates;
	
	private TimeControlReason reason;
	
	private Date creationDate;
	private String creationUser;
	private Date modificationDate;
	private String modificationUser;
	
	public TimeControlDetail() {
	
	}

	public Integer getId() {
		return id;
	}

	public TimeControlDetail setId(Integer id) {
		this.id = id;
		return this;
	}

	public Domain getDomain() {
		return domain;
	}
	
	public TimeControlDetail setDomain(Domain domain) {
		this.domain = domain;
		return this;
	}
	
	public TimeControlStatus getStatus() {
		return status;
	}

	public TimeControlDetail setStatus(TimeControlStatus status) {
		this.status = status;
		return this;
	}
	
	public TimeControlCause getCause() {
		return cause;
	}

	public TimeControlDetail setCause(TimeControlCause cause) {
		this.cause = cause;
		return this;
	}


	public Date getDate() {
		return date;
	}

	public TimeControlDetail setDate(Date date) {
		this.date = date;
		return this;
	}
	
	public Date getCreationDate() {
		return creationDate;
	}
	public TimeControlDetail setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
		return this;
	}
	
	public String getCreationUser() {
		return creationUser;
	}
	public TimeControlDetail setCreationUser(String creationUser) {
		this.creationUser = creationUser;
		return this;
	}

	public Date getModificationDate() {
		return modificationDate;
	}
	public TimeControlDetail setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
		return this;
	}
	
	public String getModificationUser() {
		return modificationUser;
	}
	public TimeControlDetail setModificationUser(String modificationUser) {
		this.modificationUser = modificationUser;
		return this;
	}

	public TaskHolder getTaskHolder() {
		return taskHolder;
	}

	public TimeControlDetail setTaskHolder(TaskHolder taskHolder) {
		this.taskHolder = taskHolder;
		return this;
	}

	public String getComments() {
		return comments;
	}

	public TimeControlDetail setComments(String comments) {
		this.comments = comments;
		return this;
	}

	public Location getLocation() {
		return location;
	}

	public TimeControlDetail setLocation(Location location) {
		this.location = location;
		return this;
	}

	public String getLocationDescription() {
		return locationDescription;
	}

	public TimeControlDetail setLocationDescription(String locationDescription) {
		this.locationDescription = locationDescription;
		return this;
	}

	public Coordinates getCoordinates() {
		return coordinates;
	}

	public TimeControlDetail setCoordinates(Coordinates coordinates) {
		this.coordinates = coordinates;
		return this;
	}
	
	public Integer getModificatedTimeControl() {
		return modificatedTimeControl;
	}

	public TimeControlDetail setModificatedTimeControl(Integer modificatedTimeControl) {
		this.modificatedTimeControl = modificatedTimeControl;
		return this;
	}
	
	public TimeControlReason getReason() {
		return reason;
	}

	public TimeControlDetail setReason(TimeControlReason reason) {
		this.reason = reason;
		return this;
	}

	
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		json.put("id", getId());
		json.put("domain", getDomain().getId());
		json.put("status", getStatus() != null ? getStatus().name().toLowerCase() : TimeControlStatus.OUT);
		json.put("cause", getCause() != null ? getCause().name().toLowerCase() : TimeControlCause.DEFAULT);
		json.put("date", getDate().getTime());
		json.put("reason", getReason() != null ? getReason().name() : null);
		json.put("reasonValue", getReason() != null ? getReason().getDescription() : null);
		json.put("comments", getComments());
		
		if(getCoordinates() != null) {
			json.put("coordinates", getCoordinates().toJSON());
		}

		if(getLocation() != null) {
			JSONObject locationJson = new JSONObject();
			locationJson.put("id", getLocation().getId());
			locationJson.put("name", getLocation().getDescription());
			json.put("location", locationJson);
		}
		
		json.put("location_description", getLocationDescription());
		
		if(getTaskHolder() != null) {
			JSONObject taskHolderJson = new JSONObject();
			taskHolderJson.put("id", getTaskHolder().getId());
			taskHolderJson.put("name", getTaskHolder().getName());
			taskHolderJson.put("active", getTaskHolder().getActiveValue());
			json.put("task_holder", taskHolderJson);
		}
		
		if(getCreationDate()!=null) 
			json.put("creation_date", getCreationDate().getTime());
		if(getCreationUser()!=null) 
			json.put("creation_user", getCreationUser());
		
		if(getModificationDate()!=null) 
			json.put("modification_date", getModificationDate().getTime());
		if(getModificationUser()!=null) 
			json.put("modification_user", getModificationUser());
		
		if(getModificatedTimeControl()!=null) 
			json.put("modificated_timecontrol", getModificatedTimeControl());
		
		return json;
	}
	
	public boolean isDirty(Object obj) {
		if (!(obj instanceof TimeControlDetail ) )
			return false;
		
		TimeControlDetail tm = (TimeControlDetail) obj;
		
		return 
			!(
				Objects.equals(status, tm.status)
				&& Objects.equals(cause, tm.cause)
				&& Objects.equals(taskHolder, tm.taskHolder)
				&& (date!=null && tm.date!=null && date.compareTo(tm.date) ==0 )
				&& Objects.equals(location, tm.location)
				&& Objects.equals(coordinates, tm.coordinates)
				&& Objects.equals(reason, tm.reason)
			);
	}
}
