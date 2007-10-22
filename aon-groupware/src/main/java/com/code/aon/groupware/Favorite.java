package com.code.aon.groupware;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.code.aon.common.ITransferObject;
import com.code.aon.config.User;

@Entity
@Table(name="favorite")
public class Favorite implements ITransferObject {

	private Integer id;
	
	private FavoriteCategory favoriteCategory;
	
	private String description;
	
	private String url;
	
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

	@ManyToOne
	@JoinColumn(name="favorite_category", nullable=false)
	public FavoriteCategory getFavoriteCategory() {
		return favoriteCategory;
	}

	public void setFavoriteCategory(FavoriteCategory favoriteCategory) {
		this.favoriteCategory = favoriteCategory;
	}

	@Column(length=128)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(length=128, nullable=false)
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@ManyToOne
	@JoinColumn(name="user")
	@Fetch(FetchMode.JOIN)
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}