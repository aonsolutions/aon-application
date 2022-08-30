package com.esferalia.aon.occam.impl.jooq;

import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.ICategory;
import com.esferalia.aon.occam.api.model.Filter.CategoryFilter;
import com.esferalia.aon.occam.api.model.registry.Category;
import com.esferalia.aon.occam.impl.jooq.dao.CategoryDAO;

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
}
