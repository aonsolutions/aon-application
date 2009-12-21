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

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;

@Entity
@Table(name = "link")
public class Link implements ITransferObject , IPositionObject {

	private static final long serialVersionUID = -3902178054750217031L;

	private Integer id;

	private String alias;

	private String url;

	private boolean active = true;

	private Integer position = new Integer(0);
	
	private LinkCategory linkCategory;

	private Set<LinkDetail> details;

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
	
	@Column(name = "url", nullable = false)
	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
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


	@OneToMany(mappedBy = "link", cascade={CascadeType.REMOVE})
	public Set<LinkDetail> getDetails() {
		return this.details;
	}

	public void setDetails( Set<LinkDetail> details ) {
		this.details = details;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "link_category", nullable = false)
	public LinkCategory getLinkCategory() {
		return linkCategory;
	}

	public void setLinkCategory(LinkCategory linkCategory) {
		this.linkCategory = linkCategory;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final Link o = (Link) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.active, o.active)
				.append(this.alias, o.alias)
				.append(this.linkCategory, o.linkCategory)
				.append(this.position, o.position)
				.append(this.url, o.url)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(active)
			.append(alias)
			.append(id)	
			.append(linkCategory)
			.append(position)
			.append(url)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();		
	}	
	
}
