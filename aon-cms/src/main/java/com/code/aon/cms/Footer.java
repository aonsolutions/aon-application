package com.code.aon.cms;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.code.aon.common.ITransferObject;

@Entity
@Table(name = "footer")
public class Footer implements ITransferObject {

	private Integer id;

	private String alias;

	private Menu menu;
	
	private boolean defaultFooter = false;

	private Set<FooterDetail> details;

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
	
	@Column(name = "defaultFooter", nullable = false)
	public boolean isDefaultFooter() {
		return defaultFooter;
	}

	public void setDefaultFooter(boolean defaultFooter) {
		this.defaultFooter = defaultFooter;
	}

	@OneToMany(mappedBy = "footer", cascade={CascadeType.REMOVE})
	public Set<FooterDetail> getDetails() {
		return this.details;
	}

	public void setDetails( Set<FooterDetail> details ) {
		this.details = details;
	}

}
