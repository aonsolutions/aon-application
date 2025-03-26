package com.esferalia.aon.occam.api;

import java.util.Optional;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.model.Filter.CategoryFilter;
import com.esferalia.aon.occam.api.model.Filter.S3CategoryFilter;
import com.esferalia.aon.occam.api.model.S3Category;
import com.esferalia.aon.occam.api.model.registry.Category;

public interface ICategory {
	
	public Category getCategory(AONContext ctx, CategoryFilter filter);
	
	public Stream<Category> getCategoryStream(AONContext ctx, CategoryFilter filter);	
	
	public Stream<Category> getCategoryStream(AONContext ctx, CategoryFilter filter, Integer page, Integer perPage);	
	
	public Category saveCategory(AONContext ctx, Category category);
	
	public void deleteCategory(AONContext ctx, Integer id);
	
	// NUEVAS FUNCIONES PARA LAS NUEVAS CATEGORYS DEL NUEVO DOCUMENTAL
	
	public Stream<S3Category> getS3CategoryStream(AONContext ctx, S3CategoryFilter filter, Optional<Integer> page, Optional<Integer> perPage);
	public long countS3Category(AONContext ctx, S3CategoryFilter filter);
	public S3Category updateS3Category(AONContext ctx, S3Category category);
	public S3Category insertS3Category(AONContext ctx, S3Category category);
	public void deleteS3Category(AONContext ctx, S3CategoryFilter filter);
}
