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
@Table(name = "footer_banner_category")
public class FooterBannerCategory implements ITransferObject {

	private Integer id;

	private Footer footer;

	private BannerCategory bannerCategory;

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
	@JoinColumn(name = "footer", nullable = false)
	public Footer getFooter() {
		return footer;
	}

	public void setFooter(Footer footer) {
		this.footer = footer;
	}

	@ManyToOne
	@JoinColumn(name = "banner_category", nullable = false)
	public BannerCategory getBannerCategory() {
		return bannerCategory;
	}

	public void setBannerCategory(BannerCategory bannerCategory) {
		this.bannerCategory = bannerCategory;
	}

}
