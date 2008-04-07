package com.code.aon.ui.cms.velocity.attribute;

import java.util.ArrayList;

import com.code.aon.cms.ArticleCategoryDetail;
import com.code.aon.cms.enumeration.ArticleType;
import com.code.aon.ui.cms.velocity.ArticleGenerator;

public class ArticleCategoryHandler {

	private String label;
	
	private String url;
	
	private ArrayList<ArticleHandler> list;

	public ArticleCategoryHandler (ArticleCategoryDetail detail, ArticleType art_type, ArrayList<ArticleHandler> list) {
		this.label = detail.getLabel();
		this.list = list;
		String url = ArticleGenerator.getTemplate(art_type.ordinal()).getHtmlName();
		url = url.replaceAll("%NAME%", art_type.getName()+"_"+detail.getArticleCategory().getAlias());
		this.url = url;
	}

	public String getLabel() {
		return label;
	}

	public ArrayList<ArticleHandler> getList() {
		return list;
	}

	public String getUrl() {
		return url;
	}

}
