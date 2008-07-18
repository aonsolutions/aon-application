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

import com.code.aon.cms.enumeration.ContentLevel;
import com.code.aon.cms.enumeration.PageType;
import com.code.aon.common.ITransferObject;

@Entity
@Table(name = "direct_access")
public class DirectAccess implements ITransferObject, IPositionObject {

	private Integer id;

	private String alias;

	private DirectAccessGroup directAccessGroup;

	private PageType type;
	
	private ContentLevel level;

	private Integer ident;

	private boolean active = true;

	private Integer position = new Integer(0);

	private String image;

	private Set<DirectAccessDetail> details;

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
	@JoinColumn(name = "direct_access_group", nullable = false)
	public DirectAccessGroup getDirectAccessGroup() {
		return directAccessGroup;
	}

	public void setDirectAccessGroup(DirectAccessGroup directAccessGroup) {
		this.directAccessGroup = directAccessGroup;
	}

	@Column(name = "type")
	public PageType getType() {
		return this.type;
	}

	public void setType(PageType type) {
		this.type = type;
	}

	@Column(name = "level")
	public ContentLevel getLevel() {
		return this.level;
	}

	public void setLevel(ContentLevel level) {
		this.level = level;
	}

	@Column(name = "ident")
	public Integer getIdent() {
		return this.ident;
	}

	public void setIdent(Integer ident) {
		this.ident = ident;
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

	@OneToMany(mappedBy = "directAccess", cascade={CascadeType.REMOVE})
	public Set<DirectAccessDetail> getDetails() {
		return this.details;
	}

	public void setDetails( Set<DirectAccessDetail> details ) {
		this.details = details;
	}

	@Column(length=255)
	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}


}
