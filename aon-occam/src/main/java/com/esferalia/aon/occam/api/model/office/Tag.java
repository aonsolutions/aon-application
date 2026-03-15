package com.esferalia.aon.occam.api.model.office;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.HasId;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.TagType;

public class Tag implements Serializable, HasId {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private Integer domain;
	private TagType type;
	private String name;
	private String color;
	private Date startDate;
	private Date endDate;
	private User user;

	public Tag setId(Integer id) {
		this.id = id;
		return this;
	}

	@Override
	public Integer getId() {
		return id;
	}

	public Tag setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}

	public Integer getDomain() {
		return this.domain;
	}

	public Tag setType(TagType type) {
		this.type = type;
		return this;
	}
	
	public TagType getType() {
		return type;
	}

	public Tag setName(String name) {
		this.name = name;
		return this;
	}

	public String getName() {
		return this.name;
	}

	public Tag setColor(String color) {
		this.color = color;
		return this;
	}

	public String getColor() {
		return color;
	}
	
	public Tag setStartDate(Date startDate) {
		this.startDate = startDate;
		return this;
	}
	
	public Date getStartDate() {
		return startDate;
	}
	
	public Tag setEndDate(Date endDate) {
		this.endDate = endDate;
		return this;
	}
	
	public Date getEndDate() {
		return endDate;
	}
	
	public Tag setUser(User user) {
		this.user = user;
		return this;
	}
	
	public User getUser() {
		return user;
	}

	public boolean isTagType() {
		return TagType.OFFICE_TYPE.equals(getType());
	}
	
	public boolean isTagPriority() {
		return TagType.OFFICE_PRIORITY.equals(getType());
	}
	
	public boolean isTagStatus() {
		return TagType.OFFICE_STATUS.equals(getType());
	}	
	
	public boolean isEmpty() {
		return getId() == null && getDomain() == null 
			&& getName() == null && getColor() == null;
	}
}
