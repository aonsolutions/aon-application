package com.code.aon.cms;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.code.aon.common.ITransferObject;

@Entity
@Table(name = "article_related")
public class ArticleRelated implements ITransferObject {

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

}
