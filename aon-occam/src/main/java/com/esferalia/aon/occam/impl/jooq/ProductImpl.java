package com.esferalia.aon.occam.impl.jooq;

import java.util.LinkedList;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.IProduct;
import com.esferalia.aon.occam.api.model.Filter.BrandFilter;
import com.esferalia.aon.occam.api.model.Filter.ItemFilter;
import com.esferalia.aon.occam.api.model.Filter.ProductCategoryFilter;
import com.esferalia.aon.occam.api.model.Filter.ProductFilter;
import com.esferalia.aon.occam.api.model.product.Brand;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.occam.api.model.product.ProductCategory;
import com.esferalia.aon.occam.api.model.product.ProductTag;
import com.esferalia.aon.occam.impl.jooq.dao.ProductDAO;

public class ProductImpl implements IProduct{
	
	// ------------------------------------- PRODUCT
	
	@Override
	public Stream<Product> getProductStream(AONContext ctx, ProductFilter filter) {
		return ctx.getDslContext().transactionResult( configuration -> 
			ProductDAO.getProductStream(ctx, filter));
	}
	
	
	@Override
	public void insert(AONContext ctx, Product p) {
		ctx.getDslContext().transaction(configuration -> {
			ProductDAO.insert(ctx, p);
		} );
	}

	@Override
	public void insertWithId(AONContext ctx, Product p) {
		ctx.getDslContext().transaction(configuration -> {
			ProductDAO.insertWithId(ctx, p);
		} );
		
	}

	@Override
	public LinkedList<Product> insert(AONContext ctx, Stream<Product> ps) {
		return ctx.getDslContext().transactionResult(configuration -> 
			ProductDAO.insert(ctx, ps));		
	}

	@Override
	public void insertWithId(AONContext ctx, Stream<Product> ps) {
		ctx.getDslContext().transaction(configuration -> {
			ProductDAO.insertWithId(ctx, ps);
		} );		
	}
	
	@Override
	public void update(AONContext ctx, Product p) {
		ctx.getDslContext().transaction(configuration -> {
			ProductDAO.update(ctx, p);
		} );		
	}

	@Override
	public void delete(AONContext ctx, Product p) {
		ctx.getDslContext().transaction(configuration -> {
			ProductDAO.delete(ctx, p);
		} );
	}

	@Override
	public void delete(AONContext ctx, Stream<Product> ps) {
		ctx.getDslContext().transaction(configuration -> {
			ProductDAO.delete(ctx, ps);
		} );		
	}

	// ------------------------------------- PRODUCT_TAG
	
	@Override
	public void insertProductTag(AONContext ctx, ProductTag pt) {
		ctx.getDslContext().transaction(configuration -> {
			ProductDAO.insertProductTag(ctx, pt);
		} );		
	}

	@Override
	public void insertProductTag(AONContext ctx, Stream<ProductTag> pts) {
		ctx.getDslContext().transaction(configuration -> {
			ProductDAO.insertProductTag(ctx, pts);
		} );		
	}

	@Override
	public void updateProductTag(AONContext ctx, ProductTag pt) {
		ctx.getDslContext().transaction(configuration -> {
			ProductDAO.updateProductTag(ctx, pt);
		} );		
	}

	@Override
	public void deleteProductTag(AONContext ctx, ProductTag pt) {
		ctx.getDslContext().transaction(configuration -> {
			ProductDAO.deleteProductTag(ctx, pt);
		} );		
	}

	@Override
	public void deleteProductTag(AONContext ctx, Stream<ProductTag> pts) {
		ctx.getDslContext().transaction(configuration -> {
			ProductDAO.deleteProductTag(ctx, pts);
		} );		
	}

	// ------------------------------------- ITEM
	
	@Override
	public Item getItem(AONContext ctx, Integer itemId){
		return ctx.getDslContext().transactionResult(configuration -> 
				ProductDAO.getItem(ctx, itemId));			
	}
	
	@Override
	public Item getItem(AONContext ctx, ItemFilter filter){
		return ctx.getDslContext().transactionResult(configuration -> 
				ProductDAO.getItem(ctx, filter));			
	}
	
	@Override
	public LinkedList<Item> getItemList(AONContext ctx, ItemFilter filter){
		return ctx.getDslContext().transactionResult(configuration -> 
				ProductDAO.getItemList(ctx, filter));			
	}
	
	@Override
	public void insertItem(AONContext ctx, Item i) {
		ctx.getDslContext().transaction(configuration -> {
			ProductDAO.insertItem(ctx, i);
		} );
	}

	@Override
	public void insertItem(AONContext ctx, Stream<Item> is) {
		ctx.getDslContext().transaction(configuration -> {
			ProductDAO.insertItem(ctx, is);
		} );
	}

	@Override
	public void updateItem(AONContext ctx, Item i) {
		ctx.getDslContext().transaction(configuration -> {
			ProductDAO.updateItem(ctx, i);
		} );
	}

	@Override
	public void deleteItem(AONContext ctx, Item i) {
		ctx.getDslContext().transaction(configuration -> {
			ProductDAO.deleteItem(ctx, i);
		} );
	}

	@Override
	public void deleteItem(AONContext ctx, Stream<Item> is) {
		ctx.getDslContext().transaction(configuration -> {
			ProductDAO.deleteItem(ctx, is);
		} );
	}

	@Override
	public void insertItemWithId(AONContext ctx, Item i) {
		ctx.getDslContext().transaction(configuration -> {
			ProductDAO.insertItemWithId(ctx, i);
		} );
	}

	@Override
	public void insertItemWithId(AONContext ctx, Stream<Item> is) {
		ctx.getDslContext().transaction(configuration -> {
			ProductDAO.insertItemWithId(ctx, is);
		} );
	}
	
	// ------------------------------------- BRAND
	@Override
	public Brand getBrand(AONContext ctx, BrandFilter filter){
		return ctx.getDslContext().transactionResult(configuration -> 
			ProductDAO.getBrand(ctx, filter));
	}
	
	@Override
	public Brand insertBrand(AONContext ctx, Brand brand){
		return ctx.getDslContext().transactionResult(configuration -> 
			ProductDAO.insertBrand(ctx, brand));
	}

	// ------------------------------------- PRODUCT CATEGORY
	
	@Override
	public ProductCategory getProductCategory(AONContext ctx, ProductCategoryFilter filter){
		return ctx.getDslContext().transactionResult(configuration ->
			ProductDAO.getProductCategory(ctx, filter));
	}
	
	@Override
	public ProductCategory insertProductCategory(AONContext ctx, ProductCategory productCategory){
		return ctx.getDslContext().transactionResult(configuration ->
			ProductDAO.insertProductCategory(ctx, productCategory));
	}

}
