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
@Table(name="article_document_i18n")
public class ArticleDocumentDetail implements ITransferObject {

	private Integer id;
	
	private ArticleDocument articleDocument;
	
	private Language language;
	
	private String title;
	
	private String description;
	
	private String file;

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
	@JoinColumn(name = "article_document", nullable = false)
	public ArticleDocument getArticleDocument() {
		return articleDocument;
	}

	public void setArticleDocument(ArticleDocument articleDocument) {
		this.articleDocument = articleDocument;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "language", nullable = false)
	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}

	@Column(length=128)
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Column(length=65535)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(length=255)
	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}
}