package com.esferalia.aon.occam.api;

import java.util.stream.Stream;

import com.esferalia.aon.occam.api.model.Filter.ItemFilter;
import com.esferalia.aon.occam.api.model.Filter.ProductFilter;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.product.Product;

public interface IProduct2 {

	// PRODUCT
	public Stream<Product> getProductStream(AONContext ctx, ProductFilter filter);

	// ITEM
	public Stream<Item> getItemStream(AONContext ctx, ItemFilter filter);
}
