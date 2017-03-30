package com.esferalia.aon.occam.api;

import java.util.LinkedList;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.model.Filter.BrandFilter;
import com.esferalia.aon.occam.api.model.Filter.ItemFilter;
import com.esferalia.aon.occam.api.model.Filter.ProductCategoryFilter;
import com.esferalia.aon.occam.api.model.Filter.ProductFilter;
import com.esferalia.aon.occam.api.model.product.Brand;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.occam.api.model.product.ProductCategory;
import com.esferalia.aon.occam.api.model.product.ProductTag;

public interface IProduct {

	// PRODUCT
	public Stream<Product> getProductStream(AONContext ctx, ProductFilter filter);
	public void insert(AONContext ctx,Product p);
	public void insertWithId(AONContext ctx,Product p);
	public LinkedList<Product> insert(AONContext ctx,Stream<Product> ps);
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
	public Stream<Item> getItemStream(AONContext ctx, ItemFilter filter);
	public Stream<Item> getFullItemStream(AONContext ctx, ProductFilter filter);
	public Item insertItem(AONContext ctx, Item i);
	public void insertItemWithId(AONContext ctx,Item i);
	public void insertItem(AONContext ctx, Stream<Item> is);
	public void insertItemWithId(AONContext ctx,Stream<Item> is);
	public void updateItem(AONContext ctx, Item i);
	public void deleteItem(AONContext ctx, Item i);
	public void deleteItem(AONContext ctx, Stream<Item> is);
	
	// BRAND
	public Stream<Brand> getBrandStream(AONContext ctx, BrandFilter filter);
	public Brand getBrand(AONContext ctx, BrandFilter filter);
	public Brand insertBrand(AONContext ctx, Brand brand); 
	
	// PRODUCT CATEGORY
	public Stream<ProductCategory> getProductCategoryStream(AONContext ctx, ProductCategoryFilter filter);
	public ProductCategory insertProductCategory(AONContext ctx, ProductCategory productCategory); 
}
