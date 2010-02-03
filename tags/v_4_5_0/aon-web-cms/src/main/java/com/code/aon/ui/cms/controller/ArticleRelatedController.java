package com.code.aon.ui.cms.controller;

import com.code.aon.cms.Article;
import com.code.aon.ui.form.BasicController;

public class ArticleRelatedController extends BasicController {

	private Article currentArticle;

	public Article getCurrentArticle() {
		return currentArticle;
	}

	public void setCurrentArticle(Article currentArticle) {
		this.currentArticle = currentArticle;
	}
	
}
