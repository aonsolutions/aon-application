package com.esferalia.aon.occam.impl.jooq;

import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.IProduct2;
import com.esferalia.aon.occam.api.model.Filter.ItemFilter;
import com.esferalia.aon.occam.api.model.Filter.ProductFilter;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.occam.impl.jooq.dao.ItemDAO;
import com.esferalia.aon.occam.impl.jooq.dao.ProductDAO;

public class Product2Impl implements IProduct2{
	// ------------------------------------- PRODUCT
	
	@Override
	public Stream<Product> getProductStream(AONContext ctx, ProductFilter filter) {
		return ctx.getDslContext().transactionResult( configuration -> 
			ProductDAO.getStream(ctx, filter));
	}
	

	// ------------------------------------- ITEM
	
	@Override
	public Stream<Item> getItemStream(AONContext ctx, ItemFilter filter) {
		return ctx.getDslContext().transactionResult( configuration -> 
			ItemDAO.getStream(ctx, filter));
	}

}
