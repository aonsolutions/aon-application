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
@Table(name = "article_related")
public class ArticleRelated implements ITransferObject {

	private static final long serialVersionUID = -502369353259523322L;

	private Integer id;

	private Article articleParent;

	private Article articleRelated;

	@Id
	@GeneratedValue
	@Column(name = "id", nullable = false)
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "article_parent", nullable = false)
	public Article getArticleParent() {
		return articleParent;
	}

	public void setArticleParent(Article articleParent) {
		this.articleParent = articleParent;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "article_related", nullable = false)
	public Article getArticleRelated() {
		return articleRelated;
	}

	public void setArticleRelated(Article articleRelated) {
		this.articleRelated = articleRelated;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final ArticleRelated o = (ArticleRelated) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.articleParent, o.articleParent)
				.append(this.articleRelated, o.articleRelated)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(articleParent)
			.append(articleRelated)
			.append(id)	
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}	
	
}
