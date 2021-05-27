package com.esferalia.aon.occam.api.model.project;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.registry.Project;

public class ProjectCommercial extends Project implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer target;
	private Integer seller;
	private String comments;
	private Byte source;
	private Byte sourceId;
	private Byte status;
	private Date statusDate;
	private Integer probability;
	
	public ProjectCommercial() {
	
	}

	public Integer getTarget() {
		return target;
	}

	public ProjectCommercial setTarget(Integer target) {
		this.target = target;
		return this;
	}

	public Integer getSeller() {
		return seller;
	}

	public ProjectCommercial setSeller(Integer seller) {
		this.seller = seller;
		return this;
	}

	public String getComments() {
		return comments;
	}

	public ProjectCommercial setComments(String comments) {
		this.comments = comments;
		return this;
	}

	public Byte getSource() {
		return source;
	}

	public ProjectCommercial setSource(Byte source) {
		this.source = source;
		return this;
	}

	public Byte getSourceId() {
		return sourceId;
	}

	public ProjectCommercial setSourceId(Byte sourceId) {
		this.sourceId = sourceId;
		return this;
	}

	public Byte getStatus() {
		return status;
	}

	public ProjectCommercial setStatus(Byte status) {
		this.status = status;
		return this;
	}

	public Date getStatusDate() {
		return statusDate;
	}

	public ProjectCommercial setStatusDate(Date statusDate) {
		this.statusDate = statusDate;
		return this;
	}

	public Integer getProbability() {
		return probability;
	}

	public ProjectCommercial setProbability(Integer probability) {
		this.probability = probability;
		return this;
	}

}
