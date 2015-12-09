package com.esferalia.aon.occam.api;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.registry.Category;

public interface IRegistry {
	
	public Category getCategory(AONContext ctx, Integer categoryId);
	public LinkedList<Category> getCategoryList(AONContext ctx);

}
