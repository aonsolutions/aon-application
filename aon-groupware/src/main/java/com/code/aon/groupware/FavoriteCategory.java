package com.code.aon.groupware;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.config.User;

@Entity
@Table(name="favorite_category")
public class FavoriteCategory implements ITransferObject {

	private static final long serialVersionUID = -6923645771997989214L;

	private Integer id;
	
	private String description;
	
	private User user;

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
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@ManyToOne
	@JoinColumn(name="user_id",nullable=false)
	@Fetch(FetchMode.JOIN)
	@ForeignKey(name = "FK_FAVORITE_CATEGORY_USER_ID")
	@Index(name = "IDX_FAVORITE_CATEGORY_USER_ID")			
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final FavoriteCategory o = (FavoriteCategory) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.description, o.description)
				.append(this.user, o.user)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().
			append(description).append(id).
			append(user).
			toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}
	
}