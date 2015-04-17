package com.esferalia.aon.occam.api;

import java.util.stream.Stream;

import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.occam.api.model.product.ProductTag;

public interface IProduct {

	// PRODUCT
	public Product getProduct(AONContext ctx, Integer productId);
	public void insert(AONContext ctx,Product p);
	public void insertWithId(AONContext ctx,Product p);
	public void insert(AONContext ctx,Stream<Product> ps);
	public void insertWithId(AONContext ctx,Stream<Product> ps);
	public void update(AONContext ctx,Product p);
	public void delete(AONContext ctx,Product p);
	public void delete(AONContext ctx,Stream<Product> ps);
	
	// PRODUCT_TAG
	public void insertProductTag(AONContext ctx,ProductTag pt);
	public void insertProductTag(AONContext ctx,Stream<ProductTag> pts);
	public void updateProductTag(AONContext ctx,ProductTag pt);
	public void deleteProductTag(AONContext ctx,ProductTag pt);
	public void deleteProductTag(AONContext ctx,Stream<ProductTag> pts);	
	
	// ITEM
	public void insertItem(AONContext ctx, Item i);
	public void insertItemWithId(AONContext ctx,Item i);
	public void insertItem(AONContext ctx, Stream<Item> is);
	public void insertItemWithId(AONContext ctx,Stream<Item> is);
	public void updateItem(AONContext ctx, Item i);
	public void deleteItem(AONContext ctx, Item i);
	public void deleteItem(AONContext ctx, Stream<Item> is);
}
