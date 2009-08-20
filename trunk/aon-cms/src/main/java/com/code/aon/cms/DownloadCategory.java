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
@Table(name="download_category")
public class DownloadCategory implements ITransferObject, IPositionObject {

	private static final long serialVersionUID = -4197866947961276581L;

	private Integer id;
	
	private String alias;
	
	private boolean active = true;
	
	private Integer position = new Integer(0);
	
	private String image;
	
	private Section section;
	
	private int itemsPerPage;
	
	private Set<DownloadCategoryDetail> details;

	@Id
	@GeneratedValue
	@Column(nullable=false)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(length=32, nullable=false)
	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	@Column(nullable=false)
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Column(nullable=false)
	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	@Column(length=255)
	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	@OneToMany(mappedBy = "downloadCategory", cascade={CascadeType.REMOVE})
	public Set<DownloadCategoryDetail> getDetails() {
		return details;
	}

	public void setDetails(Set<DownloadCategoryDetail> details) {
		this.details = details;
	}
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "section")
	public Section getSection() {
		return section;
	}

	public void setSection(Section section) {
		this.section = section;
	}

	@Column(name="items_per_page")
	public int getItemsPerPage() {
		return itemsPerPage;
	}

	public void setItemsPerPage(int itemsPerPage) {
		this.itemsPerPage = itemsPerPage;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final DownloadCategory o = (DownloadCategory) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.active, o.active)
				.append(this.alias, o.alias)
				.append(this.image, o.image)
				.append(this.itemsPerPage, o.itemsPerPage)
				.append(this.position, o.position)
				.append(this.section, o.section)				
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
			.append(image)	
			.append(itemsPerPage)
			.append(position)
			.append(section)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}	
	
}