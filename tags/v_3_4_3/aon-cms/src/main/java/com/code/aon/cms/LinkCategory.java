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
@Table(name = "link_category")
public class LinkCategory implements ITransferObject, IPositionObject {

	private Integer id;

	private String alias;

	private boolean active = true;

	private Integer position = new Integer(0);

	private Section section;
	
	private Set<LinkCategoryDetail> details;

	private Set<Link> links;

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
	
	@Column(name = "active", nullable = false)
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Column(name = "position", nullable = false)
	public Integer getPosition() {
		return this.position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	@OneToMany(mappedBy = "linkCategory", cascade={CascadeType.REMOVE})
	public Set<LinkCategoryDetail> getDetails() {
		return this.details;
	}

	public void setDetails( Set<LinkCategoryDetail> details ) {
		this.details = details;
	}

	@OneToMany(mappedBy = "linkCategory", cascade={CascadeType.REMOVE})
	public Set<Link> getLinks() {
		return this.links;
	}

	public void setLinks( Set<Link> links) {
		this.links = links;
	}
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "section")
	public Section getSection() {
		return section;
	}

	public void setSection(Section section) {
		this.section = section;
	}

}
