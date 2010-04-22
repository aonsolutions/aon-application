package com.code.aon.cms;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.code.aon.common.ITransferObject;

@Entity
@Table(name = "sport_club")
public class SportClub implements ITransferObject {

	private Integer id;
	
	private SportCategory sportCategory;

	private String description;

	private String stadium;
	
	private String image;
	
	private String logo;
	
	private boolean default_;
	
	@Id
	@GeneratedValue
	@Column(name = "id", nullable = false)
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "sport_category", nullable = false)
	public SportCategory getSportCategory() {
		return sportCategory;
	}

	public void setSportCategory(SportCategory sportCategory) {
		this.sportCategory = sportCategory;
	}

	@Column(length=28)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(length=32)
	public String getStadium() {
		return stadium;
	}

	public void setStadium(String stadium) {
		this.stadium = stadium;
	}

	@Column(length=255)
	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	@Column(length=255)
	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	@Column(nullable = false)
	public boolean isDefault_() {
		return default_;
	}

	public void setDefault_(boolean default_) {
		this.default_ = default_;
	}
	
	
}
