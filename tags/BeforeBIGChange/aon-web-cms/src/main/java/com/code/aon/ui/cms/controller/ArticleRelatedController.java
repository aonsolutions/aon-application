package com.code.aon.ui.cms.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.cms.Article;
import com.code.aon.ui.form.GridController;

public class ArticleRelatedController extends GridController {

	private Article currentArticle;

	public Article getCurrentArticle() {
		return currentArticle;
	}

	public void setCurrentArticle(Article currentArticle) {
		this.currentArticle = currentArticle;
	}
	
	public void onRemoveCurrent(ActionEvent event) {
		this.setRowChecked(true);
		this.onRemoveSelected(event);
	}
}
