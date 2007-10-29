package com.code.aon.cms;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
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
	
	private boolean language_menu = true;
	
	private LanguageMenuType language_menu_type;
	
	private boolean defaultHeader = false;
	
	private String css;
	
	private String javascript;

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

	@Column(name = "menu")
	public Menu getMenu() {
		return menu;
	}

	public void setMenu(Menu menu) {
		this.menu = menu;
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

	@Column(name = "defaultHeader", nullable = false)
	public boolean isDefaultHeader() {
		return defaultHeader;
	}

	public void setDefaultHeader(boolean defaultHeader) {
		this.defaultHeader = defaultHeader;
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

}
