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
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.code.aon.common.ITransferObject;

@Entity
@Table(name="article_document")
public class ArticleDocument implements ITransferObject {

	private static final long serialVersionUID = -5566306597391761268L;

	private Integer id;
	
	private Article article;
	
	private String alias;
	
	private Set<ArticleDocumentDetail> details;

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
	@JoinColumn(name = "article", nullable = false)
	public Article getArticle() {
		return article;
	}

	public void setArticle(Article article) {
		this.article = article;
	}

	@Column(nullable=false, length=255)
	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	@OneToMany(mappedBy = "articleDocument", cascade={CascadeType.REMOVE})
	public Set<ArticleDocumentDetail> getDetails() {
		return details;
	}

	public void setDetails(Set<ArticleDocumentDetail> details) {
		this.details = details;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final ArticleDocument o = (ArticleDocument) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.alias, o.alias)
				.append(this.article, o.article)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(alias)
			.append(article)
			.append(id)	
			.toHashCode();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).
		append("alias", StringUtils.abbreviate(alias, 32)).
		append("article", article.getId()).
		append("id", id).
		toString();
	}	

}