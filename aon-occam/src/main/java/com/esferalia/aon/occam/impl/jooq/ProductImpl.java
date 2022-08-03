package com.esferalia.aon.occam.impl.jooq;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.IProduct;
import com.esferalia.aon.occam.api.model.Filter.BrandFilter;
import com.esferalia.aon.occam.api.model.Filter.ItemAddInfoFilter;
import com.esferalia.aon.occam.api.model.Filter.ItemCompositionFilter;
import com.esferalia.aon.occam.api.model.Filter.ItemFilter;
import com.esferalia.aon.occam.api.model.Filter.ProductCategoryFilter;
import com.esferalia.aon.occam.api.model.Filter.ProductFilter;
import com.esferalia.aon.occam.api.model.product.Brand;
import com.esferalia.aon.occam.api.model.product.ItemAddInfo;
import com.esferalia.aon.occam.api.model.product.ItemComposition;
import com.esferalia.aon.occam.api.model.product.OldItem;
import com.esferalia.aon.occam.api.model.product.OldProduct;
import com.esferalia.aon.occam.api.model.product.ProductCategory;
import com.esferalia.aon.occam.api.model.product.ProductTag;
import com.esferalia.aon.occam.impl.jooq.dao.BrandDAO;
import com.esferalia.aon.occam.impl.jooq.dao.ItemCompositionDAO;
import com.esferalia.aon.occam.impl.jooq.dao.ProductCategoryDAO;
import com.esferalia.aon.occam.impl.jooq.dao.ProductOldDAO;

public class ProductImpl implements IProduct{
	
	// ------------------------------------- PRODUCT
	
	@Override
	public Stream<OldProduct> getProductStream(AONContext ctx, ProductFilter filter) {
		return ctx.getDslContext().transactionResult( configuration -> 
			ProductOldDAO.getProductStream(ctx, filter));
	}
	
	
	@Override
	public void insert(AONContext ctx, OldProduct p) {
		ctx.getDslContext().transaction(configuration -> {
			ProductOldDAO.insert(ctx, p);
		} );
	}

	@Override
	public OldProduct insertProduct(AONContext ctx, OldProduct p) {
		return ctx.getDslContext().transactionResult(configuration -> 
			ProductOldDAO.insertProduct(ctx, p));
	}

	@Override
	public void insertWithId(AONContext ctx, OldProduct p) {
		ctx.getDslContext().transaction(configuration -> {
			ProductOldDAO.insertWithId(ctx, p);
		} );
		
	}

	@Override
	public LinkedList<OldProduct> insert(AONContext ctx, Stream<OldProduct> ps) {
		return ctx.getDslContext().transactionResult(configuration -> 
			ProductOldDAO.insert(ctx, ps));		
	}

	@Override
	public void insertWithId(AONContext ctx, Stream<OldProduct> ps) {
		ctx.getDslContext().transaction(configuration -> {
			ProductOldDAO.insertWithId(ctx, ps);
		} );		
	}
	
	@Override
	public void update(AONContext ctx, OldProduct p) {
		ctx.getDslContext().transaction(configuration -> {
			ProductOldDAO.update(ctx, p);
		} );		
	}

	@Override
	public void delete(AONContext ctx, OldProduct p) {
		ctx.getDslContext().transaction(configuration -> {
			ProductOldDAO.delete(ctx, p);
		} );
	}

	@Override
	public void delete(AONContext ctx, Stream<OldProduct> ps) {
		ctx.getDslContext().transaction(configuration -> {
			ProductOldDAO.delete(ctx, ps);
		} );		
	}

	// ------------------------------------- PRODUCT_TAG
	
	@Override
	public void insertProductTag(AONContext ctx, ProductTag pt) {
		ctx.getDslContext().transaction(configuration -> {
			ProductOldDAO.insertProductTag(ctx, pt);
		} );		
	}

	@Override
	public void insertProductTag(AONContext ctx, Stream<ProductTag> pts) {
		ctx.getDslContext().transaction(configuration -> {
			ProductOldDAO.insertProductTag(ctx, pts);
		} );		
	}

	@Override
	public void updateProductTag(AONContext ctx, ProductTag pt) {
		ctx.getDslContext().transaction(configuration -> {
			ProductOldDAO.updateProductTag(ctx, pt);
		} );		
	}

	@Override
	public void deleteProductTag(AONContext ctx, ProductTag pt) {
		ctx.getDslContext().transaction(configuration -> {
			ProductOldDAO.deleteProductTag(ctx, pt);
		} );		
	}

	@Override
	public void deleteProductTag(AONContext ctx, Stream<ProductTag> pts) {
		ctx.getDslContext().transaction(configuration -> {
			ProductOldDAO.deleteProductTag(ctx, pts);
		} );		
	}

	// ------------------------------------- ITEM
	
	@Override
	public Stream<OldItem> getItemStream(AONContext ctx, ItemFilter filter){
		return ctx.getDslContext().transactionResult(configuration -> 
				ProductOldDAO.getItemStream(ctx, filter));			
	}
	
	@Override
	public Stream<OldItem> getFullItemStream(AONContext ctx, ItemFilter filter){
		return ctx.getDslContext().transactionResult(configuration -> 
				ProductOldDAO.getFullItemStream(ctx, filter));			
	}
	
	@Override
	public OldItem insertItem(AONContext ctx, OldItem i) {
		return ctx.getDslContext().transactionResult(configuration -> 
			ProductOldDAO.insertItemResult(ctx, i));
	}

	@Override
	public void insertItem(AONContext ctx, Stream<OldItem> is) {
		ctx.getDslContext().transaction(configuration -> {
			ProductOldDAO.insertItem(ctx, is);
		} );
	}

	@Override
	public void updateItem(AONContext ctx, OldItem i) {
		ctx.getDslContext().transaction(configuration -> {
			ProductOldDAO.updateItem(ctx, i);
		} );
	}

	@Override
	public void deleteItem(AONContext ctx, OldItem i) {
		ctx.getDslContext().transaction(configuration -> {
			ProductOldDAO.deleteItem(ctx, i);
		} );
	}

	@Override
	public void deleteItem(AONContext ctx, Stream<OldItem> is) {
		ctx.getDslContext().transaction(configuration -> {
			ProductOldDAO.deleteItem(ctx, is);
		} );
	}

	@Override
	public void insertItemWithId(AONContext ctx, OldItem i) {
		ctx.getDslContext().transaction(configuration -> {
			ProductOldDAO.insertItemWithId(ctx, i);
		} );
	}

	@Override
	public void insertItemWithId(AONContext ctx, Stream<OldItem> is) {
		ctx.getDslContext().transaction(configuration -> {
			ProductOldDAO.insertItemWithId(ctx, is);
		} );
	}
	
	@Override
	public Stream<ItemComposition> getItemCompositionStream(AONContext ctx, ItemCompositionFilter filter) {
		return ctx.getDslContext().transactionResult(configuration -> 
			ItemCompositionDAO.getStream(ctx, filter));	
	}
	
	@Override
	public List<ItemComposition> getItemCompositionList(AONContext ctx, ItemCompositionFilter filter) {
		return ctx.getDslContext().transactionResult(configuration -> 
			ItemCompositionDAO.getList(ctx, filter));
	}
	
//	@Override
//	public Item save(AONContext ctx, Item item) {
//		return ctx.getDslContext().transactionResult(configuration -> 
//			ItemDAO.save(ctx, item));
//	}
	
	// ------------------------------------- BRAND
	
	@Override
	public Stream<Brand> getBrandStream(AONContext ctx, BrandFilter filter){
		return ctx.getDslContext().transactionResult(configuration -> 
			BrandDAO.getStream(ctx, filter));
	}
	
	@Override
	public Brand getBrand(AONContext ctx, BrandFilter filter){
		return ctx.getDslContext().transactionResult(configuration -> 
			BrandDAO.get(ctx, filter));
	}
	
	@Override
	public Brand saveBrand(AONContext ctx, Brand brand){
		return ctx.getDslContext().transactionResult(configuration -> 
			BrandDAO.save(ctx, brand));
	}

	@Override
	public void deleteBrand(AONContext ctx, Integer id){
		ctx.getDslContext().transaction(configuration -> 
			BrandDAO.delete(ctx, id));
	}
	
	// ------------------------------------- PRODUCT CATEGORY
	
	@Override
	public Stream<ProductCategory> getProductCategoryStream(AONContext ctx, ProductCategoryFilter filter){
		return ctx.getDslContext().transactionResult(configuration ->
			ProductCategoryDAO.getStream(ctx, filter));
	}
	
	@Override
	public ProductCategory getProductCategory(AONContext ctx, ProductCategoryFilter filter){
		return ctx.getDslContext().transactionResult(configuration ->
			ProductCategoryDAO.get(ctx, filter));
	}
	
	@Override
	public ProductCategory saveProductCategory(AONContext ctx, ProductCategory productCategory){
		return ctx.getDslContext().transactionResult(configuration ->
			ProductCategoryDAO.save(ctx, productCategory));
	}

	@Override
	public void deleteProductCategory(AONContext ctx, Integer id){
		ctx.getDslContext().transaction(configuration ->
			ProductCategoryDAO.delete(ctx, id));
	}

	// ------------------------------------- ITEM ADD INFO
	
	@Override
	public Stream<ItemAddInfo> getItemAddInfoStream(AONContext ctx, ItemAddInfoFilter filter) {
		return ctx.getDslContext().transactionResult(configuration ->
			ProductOldDAO.getItemAddInfoStream(ctx, filter));
	}

	@Override
	public void insertItemAddInfo(AONContext ctx, ItemAddInfo i) {
		ctx.getDslContext().transaction(configuration -> {
			ProductOldDAO.insertItemAddInfo(ctx, i);
		} );		
	}
	
	@Override
	public void updateItemAddInfo(AONContext ctx, ItemAddInfo i) {
		ctx.getDslContext().transaction(configuration -> {
			ProductOldDAO.updateItemAddInfo(ctx, i);
		} );		
	}

}
