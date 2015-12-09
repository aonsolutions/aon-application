package com.esferalia.aon.occam.impl.jooq;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.IRegistry;
import com.esferalia.aon.occam.api.model.registry.Category;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryDAO;

public class RegistryImpl implements IRegistry{
	
	// ------------------------------------- CATEGORY
	
	@Override
	public Category getCategory(AONContext ctx, Integer categoryId) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> RegistryDAO.getCategory(ctx, categoryId));
	}
	
	@Override
	public LinkedList<Category> getCategoryList(AONContext ctx) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> RegistryDAO.getCategoryList(ctx));
	}
	
}
