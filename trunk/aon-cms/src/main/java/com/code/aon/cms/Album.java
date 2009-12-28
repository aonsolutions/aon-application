package com.code.aon.cms;

import java.util.Date;
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

import com.code.aon.cms.util.IActivableObject;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;

@Entity
@Table(name="album")
public class Album implements IActivableObject, IPositionObject {

	private static final long serialVersionUID = -3501150445896926462L;

	private Integer id;
	
	private String alias;
	
	private boolean active = true;
	
	private Integer position = new Integer(0);
	
	private AlbumCategory albumCategory;
	
	private Date publishDate;
	
	private String image;
	
	private int itemsPerPage;
	
	private Set<AlbumDetail> details;

	@Id
	@GeneratedValue
	@Column(nullable=false)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(nullable=false,length=32)
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

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "album_category", nullable = false)
	public AlbumCategory getAlbumCategory() {
		return albumCategory;
	}

	public void setAlbumCategory(AlbumCategory albumCategory) {
		this.albumCategory = albumCategory;
	}

	@Column(name="publish_date")
	public Date getPublishDate() {
		return publishDate;
	}

	public void setPublishDate(Date publishDate) {
		this.publishDate = publishDate;
	}

	@Column(length=255)
	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	@Column(name="items_per_page")
	public int getItemsPerPage() {
		return itemsPerPage;
	}

	public void setItemsPerPage(int itemsPerPage) {
		this.itemsPerPage = itemsPerPage;
	}

	@OneToMany(mappedBy = "album", cascade={CascadeType.REMOVE})
	public Set<AlbumDetail> getDetails() {
		return details;
	}

	public void setDetails(Set<AlbumDetail> details) {
		this.details = details;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final Album o = (Album) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.active, o.active)
				.append(this.albumCategory, o.albumCategory)				
				.append(this.alias, o.alias)
				.append(this.image, o.image)				
				.append(this.itemsPerPage, o.itemsPerPage)
				.append(this.position, o.position)
				.append(this.publishDate, o.publishDate)				
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(active)
			.append(albumCategory)
			.append(alias)
			.append(id)	
			.append(image)
			.append(itemsPerPage)			
			.append(position)
			.append(publishDate)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}
	
}