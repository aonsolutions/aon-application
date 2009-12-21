package com.code.aon.ui.cms.controller;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import com.code.aon.cms.Article;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.GridController;
import com.icesoft.faces.component.ext.RowSelectorEvent;

public class ArticleRelatedController extends GridController {

	private Article currentArticle;

	private boolean cancelOnSelect = false;
	
	@SuppressWarnings("unused")
	public void onSelect(RowSelectorEvent event) throws ManagerBeanException {
		if (!cancelOnSelect) {
			super.onSelect(new ActionEvent(event.getComponent()));
		}
		cancelOnSelect = false;
	}

	public void onChecked(ValueChangeEvent event) throws ManagerBeanException {
		cancelOnSelect = true;
	}

	public Article getCurrentArticle() {
		return currentArticle;
	}

	public void setCurrentArticle(Article currentArticle) {
		this.currentArticle = currentArticle;
	}

	
	
}
