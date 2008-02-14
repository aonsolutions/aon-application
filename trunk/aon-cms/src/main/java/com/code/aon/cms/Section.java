package com.code.aon.cms;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.common.ITransferObject;

@Entity
@Table(name = "section")
public class Section implements ITransferObject {

	private Integer id;

	private String alias;
	
	private Header header;
	
	private Footer footer;
	
	private Sidebar sidebar;
	
	private Menu menu;
	
	private Menu menu_alt;
	
	private boolean show_header = true;
	
	private boolean show_footer = true;
	
	private boolean show_sidebar_left = true;
	
	private boolean show_sidebar_right = true;
	
	private boolean show_menu = true;

	private boolean show_menu_alt = true;

	private boolean default_;
	
	private Section parent_;
	
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
	@JoinColumn(name = "header")
	public Header getHeader() {
		return header;
	}

	public void setHeader(Header header) {
		this.header = header;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "footer")
	public Footer getFooter() {
		return footer;
	}

	public void setFooter(Footer footer) {
		this.footer = footer;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "sidebar")
	public Sidebar getSidebar() {
		return sidebar;
	}

	public void setSidebar(Sidebar sidebar) {
		this.sidebar = sidebar;
	}

	@Column(name = "show_header")
	public boolean isShow_header() {
		return show_header;
	}

	public void setShow_header(boolean show_header) {
		this.show_header = show_header;
	}

	@Column(name = "show_footer")
	public boolean isShow_footer() {
		return show_footer;
	}

	public void setShow_footer(boolean show_footer) {
		this.show_footer = show_footer;
	}

	@Column(name = "show_sidebar_left")
	public boolean isShow_sidebar_left() {
		return show_sidebar_left;
	}

	public void setShow_sidebar_left(boolean show_sidebar_left) {
		this.show_sidebar_left = show_sidebar_left;
	}

	@Column(name = "show_sidebar_right")
	public boolean isShow_sidebar_right() {
		return show_sidebar_right;
	}

	public void setShow_sidebar_right(boolean show_sidebar_right) {
		this.show_sidebar_right = show_sidebar_right;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "menu")
	public Menu getMenu() {
		return menu;
	}

	public void setMenu(Menu menu) {
		this.menu = menu;
	}

	@Column(name = "show_menu")
	public boolean isShow_menu() {
		return show_menu;
	}

	public void setShow_menu(boolean show_menu) {
		this.show_menu = show_menu;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "menu_alt")
	public Menu getMenu_alt() {
		return menu_alt;
	}

	public void setMenu_alt(Menu menu_alt) {
		this.menu_alt = menu_alt;
	}

	@Column(name = "show_menu_alt")
	public boolean isShow_menu_alt() {
		return show_menu_alt;
	}

	public void setShow_menu_alt(boolean show_menu_alt) {
		this.show_menu_alt = show_menu_alt;
	}

	@Column(name = "default_", nullable = false)
	public boolean isDefault_() {
		return default_;
	}

	public void setDefault_(boolean default_) {
		this.default_ = default_;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "parent_")
	public Section getParent_() {
		return parent_;
	}

	public void setParent_(Section parent_) {
		this.parent_ = parent_;
	}

	@Transient
	public Header getHeaderToShow(){
		if (this.parent_ == null)
			return this.header;
		else
			return parent_.getHeaderToShow();
	}
	
	@Transient
	public Footer getFooterToShow(){
		if (this.parent_ == null)
			return this.footer;
		else
			return parent_.getFooterToShow();
	}
	
	@Transient
	public Sidebar getSidebarToShow(){
		if (this.parent_ == null)
			return this.sidebar;
		else
			return parent_.getSidebarToShow();
	}
	
	@Transient
	public Menu getMenuToShow(){
		if (this.parent_ == null)
			return this.menu;
		else
			return parent_.getMenuToShow();
	}
	
	@Transient
	public Menu getMenuAltToShow(){
		if (this.parent_ == null)
			return this.menu_alt;
		else
			return parent_.getMenuAltToShow();
	}
	
}
