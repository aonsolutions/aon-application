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
	private byte type;
	private String name;
	private String color;
	private Date startDate;
	private Date endDate;
	private User user;

	public Tag() {
     // TODO document why this constructor is empty
    }

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

	public Tag setType(byte type) {
		this.type = type;
		return this;
	}
	
	public Tag setTagType(TagType tagType) {
		if(tagType!=null) this.type = tagType.value();
		return this;
	}
	
	public TagType getTagType() {
		return TagType.safeValueOf(type);
	}

	public byte getType() {
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
	
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	public Date getStartDate() {
		return startDate;
	}
	
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	public Date getEndDate() {
		return endDate;
	}
	
	public void setUser(User user) {
		this.user = user;
	}
	
	public User getUser() {
		return user;
	}

	public boolean isTagType() {
		return this.type == TagType.OFFICE_TYPE.value();
	}
	
	public boolean isTagPriority() {
		return this.type == TagType.OFFICE_PRIORITY.value();
	}
	
	public boolean isTagStatus() {
		return this.type == TagType.OFFICE_STATUS.value();
	}	
}
