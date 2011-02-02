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

import com.code.aon.cms.util.IActivableObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;

@Entity
@Table(name="album_image")
public class AlbumImage implements IActivableObject, IPositionObject {

	private static final long serialVersionUID = -3962124093163080184L;

	private Integer id;
	
	private Album album;
	
	private boolean active = true;
	
	private Integer position = new Integer(0);
	
	private String image;
	
	private String thumbnail;
	
	private Set<AlbumImageDetail> details;

	@Id
	@GeneratedValue
	@Column(nullable=false)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "album", nullable = false)
	public Album getAlbum() {
		return album;
	}

	public void setAlbum(Album album) {
		this.album = album;
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

	@Column(nullable=false, length=255)
	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	@Column(length=255)
	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	@OneToMany(mappedBy = "albumImage", cascade={CascadeType.REMOVE})
	public Set<AlbumImageDetail> getDetails() {
		return details;
	}

	public void setDetails(Set<AlbumImageDetail> details) {
		this.details = details;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final AlbumImage o = (AlbumImage) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.active, o.active)
				.append(this.album, o.album)
				.append(this.image, o.image)
				.append(this.position, o.position)
				.append(this.thumbnail, o.thumbnail)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(active)
			.append(album)
			.append(id)	
			.append(image)
			.append(position)
			.append(thumbnail)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}	
	
}