package com.code.aon.ui.cms.velocity.attribute;

import java.util.ArrayList;

import com.code.aon.cms.ArticleCategoryDetail;
import com.code.aon.cms.enumeration.Templates;

public class ArticleCategoryHandler {

	private String label;

	private ArrayList<ArticleHandler> list;

	public ArticleCategoryHandler (ArticleCategoryDetail detail, ArrayList<ArticleHandler> list) {
		this.label = detail.getLabel();
		this.list = list;
	}

	public String getLabel() {
		return label;
	}
	
	public ArrayList<ArticleHandler> getList() {
		return list;
	}

}
