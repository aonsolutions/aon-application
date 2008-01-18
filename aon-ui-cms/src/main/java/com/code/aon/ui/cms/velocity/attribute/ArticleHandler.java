package com.code.aon.ui.cms.velocity.attribute;

import com.code.aon.cms.ArticleDetail;
import com.code.aon.cms.enumeration.Templates;

public class ArticleHandler {

	private String title;

	private String subtitle;

	private String content;
	
	private String url;

	public ArticleHandler (ArticleDetail ad) {
		this.title = ad.getTitle();
		this.subtitle = ad.getSubtitle();
		this.content = ad.getContent();
		this.url = Templates.ARTICLE.getHtmlName();
		this.url = this.url.replaceAll("%NAME%", ad.getArticle().getAlias());
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

	public String getUrl() {
		return url;
	}
	
}
