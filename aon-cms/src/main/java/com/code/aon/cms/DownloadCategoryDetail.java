package com.code.aon.cms;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;

@Entity
@Table(name="download_category_i18n")
public class DownloadCategoryDetail implements ITransferObject{
	
	private static final long serialVersionUID = 5999818859381016003L;

	private Integer id;
	
	private DownloadCategory downloadCategory;
	
	private Language language;
	
	private String label;

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
	@JoinColumn(name = "download_category", nullable = false)
	public DownloadCategory getDownloadCategory() {
		return downloadCategory;
	}

	public void setDownloadCategory(DownloadCategory downloadCategory) {
		this.downloadCategory = downloadCategory;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "language", nullable = false)
	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}

	@Column(length=64)
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final DownloadCategoryDetail o = (DownloadCategoryDetail) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.downloadCategory, o.downloadCategory)				
				.append(this.label, o.label)
				.append(this.language, o.language)				
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(downloadCategory)
			.append(id)	
			.append(label)
			.append(language)			
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}	
	
}