package com.code.aon.ui.cms.velocity.attribute;

import java.util.List;

public class DiaryCategoriesHandler {

	private boolean categories;
	
	private String content;
	
	private List<ArticleCategoryHandler> categoriesList;
	
	public DiaryCategoriesHandler (String content, boolean categories, List<ArticleCategoryHandler> categoriesList) {
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

	public List<ArticleCategoryHandler> getCategoriesList() {
		return categoriesList;
	}
	
	
}
