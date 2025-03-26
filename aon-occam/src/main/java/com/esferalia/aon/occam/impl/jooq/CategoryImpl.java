package com.esferalia.aon.occam.impl.jooq;

import java.util.Optional;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.ICategory;
import com.esferalia.aon.occam.api.model.S3Category;
import com.esferalia.aon.occam.api.model.Filter.CategoryFilter;
import com.esferalia.aon.occam.api.model.Filter.S3CategoryFilter;
import com.esferalia.aon.occam.api.model.registry.Category;
import com.esferalia.aon.occam.impl.jooq.dao.CategoryDAO;
import com.esferalia.aon.occam.impl.jooq.dao.S3CategoryDAO;

public class CategoryImpl implements ICategory {

	@Override
	public Category getCategory(AONContext ctx, CategoryFilter filter) {
		return ctx.getDslContext().transactionResult(configuration -> CategoryDAO.get(ctx, filter));
	}

	@Override
	public Stream<Category> getCategoryStream(AONContext ctx, CategoryFilter filter) {
		return ctx.getDslContext().transactionResult(configuration -> CategoryDAO.getStream(ctx, filter));
	}
	
	@Override
	public Stream<Category> getCategoryStream(AONContext ctx, CategoryFilter filter, Integer page, Integer perPage) {
		return ctx.getDslContext().transactionResult(configuration -> CategoryDAO.getStream(ctx, filter, page, perPage));
	}

	@Override
	public Category saveCategory(AONContext ctx, Category category) {
		return ctx.getDslContext().transactionResult(configuration -> CategoryDAO.save(ctx, category));
	}

	@Override
	public void deleteCategory(AONContext ctx, Integer id) {
		ctx.getDslContext().transaction(configuration -> CategoryDAO.delete(ctx, id));
	}
	
	// NUEVAS CATEGORIAS PARA EL NUEVO DOCUMENTAL
	
	public Stream<S3Category> getS3CategoryStream(AONContext ctx, S3CategoryFilter filter, Optional<Integer> page, Optional<Integer> perPage){
		return ctx.getDslContext().transactionResult(configuration -> S3CategoryDAO.getStream(ctx, filter, page, perPage));
	}
	
	public long countS3Category(AONContext ctx, S3CategoryFilter filter) {
		return ctx.getDslContext().transactionResult(configuration -> S3CategoryDAO.count(ctx, filter));	
	}
	
	public S3Category updateS3Category(AONContext ctx, S3Category category) {
		return ctx.getDslContext().transactionResult(configuration -> S3CategoryDAO.update(ctx, category));
	}
	
	public S3Category insertS3Category(AONContext ctx, S3Category category) {
		return ctx.getDslContext().transactionResult(configuration -> S3CategoryDAO.insert(ctx, category));
	}
	
	public void deleteS3Category(AONContext ctx, S3CategoryFilter filter) {
		ctx.getDslContext().transaction(configuration -> S3CategoryDAO.delete(ctx, filter));
	}
	
}
