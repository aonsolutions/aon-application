package com.esferalia.aon.occam.api.model.aonsolutions;

import java.util.Date;
import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.task.TaskHolder;

public class TimeControl {
	
	private Date startDate;
	private Date endDate;
	private TimeControlGroup group;
	
	private Long time;
	private TimeControlStatus status;
	private TaskHolder taskHolder;
	private Date inDate;
	private LinkedList<TimeControlDetail> detail;
	private Date lastDate;
	private Location lastLocation;
	private Coordinates lastCoordinates;
	
	public TimeControl() {
		this.time = 0L;
	}
	
	public Long getTime() {
		return time;
	}

	public TimeControl setTime(Long time) {
		this.time = time;
		return this;
	}

	public TimeControlStatus getStatus() {
		return status;
	}

	public TimeControl setStatus(TimeControlStatus status) {
		this.status = status;
		return this;
	}

	public Date getInDate() {
		return inDate;
	}

	public TimeControl setInDate(Date inDate) {
		this.inDate = inDate;
		return this;
	}

	public TaskHolder getTaskHolder() {
		return taskHolder;
	}

	public TimeControl setTaskHolder(TaskHolder taskHolder) {
		this.taskHolder = taskHolder;
		return this;
	}
	
	public LinkedList<TimeControlDetail> getDetail() {
		if(detail == null) {
			detail = new LinkedList<>();
		}
		return detail;
	}

	public TimeControl setDetail(LinkedList<TimeControlDetail> detail) {
		this.detail = detail;
		return this;
	}
	
	public Date getLastDate() {
		return lastDate;
	}
	
	public TimeControl setLastDate(Date lastDate) {
		this.lastDate = lastDate;
		return this;
	}
	
	public Location getLastLocation() {
		return lastLocation;
	}
	
	public TimeControl setLastLocation(Location lastLocation) {
		this.lastLocation = lastLocation;
		return this;
	}
	
	public Coordinates getLastCoordinates() {
		return lastCoordinates;
	}
	
	public void setLastCoordinates(Coordinates lastCoordinates) {
		this.lastCoordinates = lastCoordinates;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public TimeControlGroup getGroup() {
		return group;
	}

	public void setGroup(TimeControlGroup group) {
		this.group = group;
	}

	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		json.put("start_date", getStartDate() != null ? getStartDate().getTime() : null);
		json.put("end_date", getEndDate() != null ? getEndDate().getTime() : null);
		json.put("group", getGroup() != null ? getGroup().name() : null);
		
		json.put("time", getTime());
		json.put("in_date", getInDate() != null ? getInDate().getTime() : null);
		json.put("status", getStatus() != null ? getStatus().name().toLowerCase() : TimeControlStatus.OUT);
		json.put("last_date", getLastDate() != null ? getLastDate().getTime() : null);

		if(getLastLocation() != null) {
			JSONObject locationJson = new JSONObject();
			locationJson.put("id", getLastLocation().getId());
			locationJson.put("name", getLastLocation().getDescription());
			json.put("last_location", locationJson);
		}
		
		if(getLastLocation() != null) {
			json.put("coordinates", getLastCoordinates().toJSON());
		}
		
		if(getTaskHolder() != null) {
			JSONObject taskHolderJson = new JSONObject();
			taskHolderJson.put("id", getTaskHolder().getId());
			taskHolderJson.put("name", getTaskHolder().getName());
			json.put("task_holder", taskHolderJson);
		}
		JSONArray detail = new JSONArray();
		getDetail().stream().forEach(r -> detail.put(r.toJSON()));
		json.put("detail", detail);
		return json;
	}
}
