package com.esferalia.aon.gwt.dump.shared;

import java.io.Serializable;
import java.util.List;

public class Progress implements Serializable {

	private Integer percent;
	private List<String> comments;
	private Integer lastId;
	private int status;
	
	public int getStatus() {
		return status;
	}

	public Progress setStatus(int status) {
		this.status = status;
		return this;
	}

	public Integer getLastId() {
		return lastId;
	}
	
	public List<String> getComments() {
		return comments;
	}
	public Integer getPercent() {
		return percent;
	}
	public Progress setPercent(Integer percent) {
		this.percent = percent;
		return this;
	}
	public Progress setComments(List<String> comments) {
		this.comments = comments;
		return this;
	}
	
	public Progress setLastId(Integer lastId) {
		this.lastId = lastId;
		return this;
	}
	

	
}
