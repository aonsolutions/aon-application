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

import com.code.aon.cms.enumeration.LanguageMenuType;
import com.code.aon.common.ITransferObject;

@Entity
@Table(name = "header")
public class Header implements ITransferObject {

	private Integer id;

	private String alias;

	private Menu menu;
	
	private BannerCategory bannerCategory;
	
	private boolean language_menu = true;
	
	private LanguageMenuType language_menu_type;
	
	private String css;
	
	private String javascript;

	private boolean default_;

	private Set<HeaderDetail> details;

	@Id
	@GeneratedValue
	@Column(name = "id", nullable = false)
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "alias", nullable = false, length = 32)
	public String getAlias() {
		return this.alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "menu", nullable = true)
	public Menu getMenu() {
		return menu;
	}

	public void setMenu(Menu menu) {
		this.menu = menu;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "banner_category", nullable = true)
	public BannerCategory getBannerCategory() {
		return bannerCategory;
	}

	public void setBannerCategory(BannerCategory bannerCategory) {
		this.bannerCategory = bannerCategory;
	}

	@Column(name = "language_menu", nullable = false)
	public boolean isLanguage_menu() {
		return language_menu;
	}

	public void setLanguage_menu(boolean language_menu) {
		this.language_menu = language_menu;
	}

	@Column(name = "language_menu_type", nullable = false)
	public LanguageMenuType getLanguage_menu_type() {
		return language_menu_type;
	}

	public void setLanguage_menu_type(LanguageMenuType language_menu_type) {
		this.language_menu_type = language_menu_type;
	}

	@Column(name = "css")
	public String getCss() {
		return css;
	}

	public void setCss(String css) {
		this.css = css;
	}

	@Column(name = "javascript")
	public String getJavascript() {
		return javascript;
	}

	public void setJavascript(String javascript) {
		this.javascript = javascript;
	}

	@OneToMany(mappedBy = "header", cascade={CascadeType.REMOVE})
	public Set<HeaderDetail> getDetails() {
		return this.details;
	}

	public void setDetails( Set<HeaderDetail> details ) {
		this.details = details;
	}

	@Column(name = "default_", nullable = false)
	public boolean isDefault_() {
		return default_;
	}

	public void setDefault_(boolean default_) {
		this.default_ = default_;
	}


}
