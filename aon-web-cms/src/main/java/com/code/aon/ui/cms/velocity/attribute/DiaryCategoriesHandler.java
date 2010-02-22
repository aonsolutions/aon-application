package com.code.aon.ui.cms.velocity.attribute;

import java.util.ArrayList;

public class DiaryCategoriesHandler {

	private boolean categories;
	
	private String content;
	
	private ArrayList<ArticleCategoryHandler> categoriesList;
	
	public DiaryCategoriesHandler (String content, 
			boolean categories,
			ArrayList<ArticleCategoryHandler> categoriesList) {
		this.categories = categories;
		this.content = content;
		this.categoriesList = categoriesList;
	}

	public boolean isCategories() {
		return categories;
	}

	public String getContent() {
		return content;
	}

	public ArrayList<ArticleCategoryHandler> getCategoriesList() {
		return categoriesList;
	}
	
	
}
