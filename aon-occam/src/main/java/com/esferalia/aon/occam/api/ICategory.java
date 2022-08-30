package com.esferalia.aon.occam.api;

import java.util.stream.Stream;

import com.esferalia.aon.occam.api.model.Filter.CategoryFilter;
import com.esferalia.aon.occam.api.model.registry.Category;

public interface ICategory {
	
	public Category getCategory(AONContext ctx, CategoryFilter filter);
	
	public Stream<Category> getCategoryStream(AONContext ctx, CategoryFilter filter);	
	
	public Stream<Category> getCategoryStream(AONContext ctx, CategoryFilter filter, Integer page, Integer perPage);	
	
	public Category saveCategory(AONContext ctx, Category category);
	
	public void deleteCategory(AONContext ctx, Integer id);
}
