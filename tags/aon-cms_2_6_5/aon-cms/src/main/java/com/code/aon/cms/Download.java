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

import com.code.aon.common.ITransferObject;

@Entity
@Table(name="download")
public class Download implements ITransferObject, IPositionObject {

	private Integer id;
	
	private String alias;
	
	private boolean active = true;
	
	private Integer position = new Integer(0);
	
	private DownloadCategory downloadCategory;
	
	private Date publishDate;
	
	private String type;
	
	private int size;
	
	private Set<DownloadDetail> details;

	@Id
	@GeneratedValue
	@Column(nullable=false)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(nullable=false, length=32)
	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "download_category", nullable = false)
	public DownloadCategory getDownloadCategory() {
		return downloadCategory;
	}

	public void setDownloadCategory(DownloadCategory downloadCategory) {
		this.downloadCategory = downloadCategory;
	}

	@Column(name="publish_date")
	public Date getPublishDate() {
		return publishDate;
	}

	public void setPublishDate(Date publishDate) {
		this.publishDate = publishDate;
	}

	@Column(length=3)
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	@OneToMany(mappedBy = "download", cascade={CascadeType.REMOVE})
	public Set<DownloadDetail> getDetails() {
		return details;
	}

	public void setDetails(Set<DownloadDetail> details) {
		this.details = details;
	}
}