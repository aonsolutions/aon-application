package com.code.aon.cms;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.code.aon.common.ITransferObject;

@Entity
@Table(name="banner")
public class Banner implements ITransferObject, IPositionObject {
	
	private Integer id;
	
	private String alias;
	
	private boolean active = true;
	
	private Integer position = new Integer(0);
	
	private BannerCategory bannerCategory;
	
	private Set<BannerDetail> details;

	
	@Id
	@GeneratedValue
	@Column(nullable = false)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(length=32)
	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "banner_category", nullable = false)
	public BannerCategory getBannerCategory() {
		return bannerCategory;
	}

	public void setBannerCategory(BannerCategory bannerCategory) {
		this.bannerCategory = bannerCategory;
	}

	@OneToMany(mappedBy = "banner", cascade={CascadeType.REMOVE})
	public Set<BannerDetail> getDetails() {
		return details;
	}

	public void setDetails(Set<BannerDetail> details) {
		this.details = details;
	}
}