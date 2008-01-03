package com.code.aon.ui.cms.velocity.attribute;

import com.code.aon.cms.ArticleDetail;

public class ArticleHandler {

	private String title;

	private String subtitle;

	private String content;

	public ArticleHandler (ArticleDetail ad) {
		this.title = ad.getTitle();
		this.subtitle = ad.getSubtitle();
		this.content = ad.getContent();
	}

	public String getTitle() {
		return title;
	}

	public String getSubtitle() {
		return subtitle;
	}

	public String getContent() {
		return content;
	}

}
