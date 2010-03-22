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

import com.code.aon.cms.enumeration.ArticleType;
import com.code.aon.cms.util.IActivableObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;

@Entity
@Table(name = "article")
public class Article implements IActivableObject, IPositionObject {

	private static final long serialVersionUID = -6401510044712437861L;

	private Integer id;

	private String alias;

	private boolean active = true;

	private Integer position = new Integer(0);
	
	private Date publishDate = new Date();
	
	private Date expireDate;

	private Date initDate;

	private Date endDate;

	private String image;
	
	private String thumbnail;

	private ArticleCategory articleCategory;

	private ArticleType articleType;

	private Set<ArticleDetail> details;

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


	@OneToMany(mappedBy = "article", cascade={CascadeType.REMOVE})
	public Set<ArticleDetail> getDetails() {
		return this.details;
	}

	public void setDetails( Set<ArticleDetail> details ) {
		this.details = details;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "article_category", nullable = false)
	public ArticleCategory getArticleCategory() {
		return articleCategory;
	}

	public void setArticleCategory(ArticleCategory articleCategory) {
		this.articleCategory = articleCategory;
	}

	@Column(name = "publish_date", nullable = false)
	public Date getPublishDate() {
		return publishDate;
	}

	public void setPublishDate(Date publishDate) {
		this.publishDate = publishDate;
	}

	@Column(name = "expire_date")
	public Date getExpireDate() {
		return expireDate;
	}

	public void setExpireDate(Date expireDate) {
		this.expireDate = expireDate;
	}

	@Column(name = "type_", nullable = false)
	public ArticleType getArticleType() {
		return articleType;
	}

	public void setArticleType(ArticleType articleType) {
		this.articleType = articleType;
	}

	@Column(length=255)
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

	@Column(name = "init_date")
	public Date getInitDate() {
		return initDate;
	}

	public void setInitDate(Date initDate) {
		this.initDate = initDate;
	}

	@Column(name = "end_date")
	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final Article o = (Article) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.active, o.active)
				.append(this.alias, o.alias)
				.append(this.articleCategory, o.articleCategory)
				.append(this.articleType, o.articleType)
				.append(this.endDate, o.endDate)
				.append(this.expireDate, o.expireDate)				
				.append(this.image, o.image)
				.append(this.initDate, o.initDate)
				.append(this.position, o.position)
				.append(this.publishDate, o.publishDate)				
				.append(this.thumbnail, o.thumbnail)				
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(active)
			.append(alias)
			.append(articleCategory)
			.append(articleType)
			.append(endDate)
			.append(expireDate)
			.append(id)	
			.append(image)			
			.append(initDate)
			.append(position)
			.append(publishDate)
			.append(thumbnail)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}	

}
