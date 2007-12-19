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
@Table(name = "section")
public class Section implements ITransferObject {

	private Integer id;

	private String alias;
	
	private Header header;
	
	private Footer footer;
	
	private Sidebar sidebar;
	
	private boolean show_header = true;
	
	private boolean show_footer = true;
	
	private boolean show_sidebar = true;
	
	private Menu menu;
	
	private boolean show_menu = true;
	
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
	@JoinColumn(name = "header", nullable = false)
	public Header getHeader() {
		return header;
	}

	public void setHeader(Header header) {
		this.header = header;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "footer", nullable = false)
	public Footer getFooter() {
		return footer;
	}

	public void setFooter(Footer footer) {
		this.footer = footer;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "sidebar", nullable = false)
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

	@Column(name = "show_sidebar")
	public boolean isShow_sidebar() {
		return show_sidebar;
	}

	public void setShow_sidebar(boolean show_sidebar) {
		this.show_sidebar = show_sidebar;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "menu", nullable = false)
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

}
