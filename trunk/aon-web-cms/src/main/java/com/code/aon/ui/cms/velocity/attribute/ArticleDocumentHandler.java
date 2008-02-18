package com.code.aon.ui.cms.velocity.attribute;

import com.code.aon.cms.ArticleDocumentDetail;

public class ArticleDocumentHandler {

	private String title;

	private String description;

	private String url;
	
	public ArticleDocumentHandler (ArticleDocumentDetail obj) {
		this.title = obj.getTitle();
		this.description = obj.getDescription();
		this.url = obj.getFile();
	}

	public String getTitle() {
		return title;
	}
	
	public String getDescription() {
		return description;
	}

	public String getUrl() {
		return url;
	}
	
}
