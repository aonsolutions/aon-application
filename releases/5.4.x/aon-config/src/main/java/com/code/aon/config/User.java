package com.code.aon.config;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.code.aon.common.ITransferObject;

@Entity
@Table(name="user")
public class User implements ITransferObject{
	
	private static final long serialVersionUID = -151638379810317997L;

	private Integer id;
	
	private String name;
	
	private String login;

	private boolean available;

	private boolean validate;
	
	private String aon_key;
	
	private Integer status;


    @Id
    @GeneratedValue
	@Column(nullable=false)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(length=64, nullable=false)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(length=16,nullable=false)
	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}
	
	@Column(nullable=false)
	public boolean getAvailable() {
		return available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}

	@Column(nullable=false)
	public boolean getValidate() {
		return validate;
	}

	public void setValidate(boolean validate) {
		this.validate = validate;
	}

	@Column(length = 128)
	public String getAon_key() {
		return aon_key;
	}

	public void setAon_key(String aon_key) {
		this.aon_key = aon_key;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final User o = (User) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.aon_key, o.aon_key)			
				.append(this.available, o.available)
				.append(this.login, o.login)				
				.append(this.name, o.name)			
				.append(this.status, o.status)
				.append(this.validate, o.validate)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(aon_key)		
			.append(available)			
			.append(id)		
			.append(login)		
			.append(name)		
			.append(status)
			.append(validate)			
			.toHashCode();
	}	

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
}