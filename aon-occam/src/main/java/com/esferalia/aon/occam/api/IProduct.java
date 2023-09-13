package com.esferalia.aon.occam.api;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

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

public interface IProduct {

	// PRODUCT
	public Stream<OldProduct> getProductStream(AONContext ctx, ProductFilter filter);
	public void insert(AONContext ctx,OldProduct p);
	public OldProduct insertProduct(AONContext ctx,OldProduct p);
	public LinkedList<OldProduct> insert(AONContext ctx,Stream<OldProduct> ps);
	public void update(AONContext ctx,OldProduct p);
	public void delete(AONContext ctx,OldProduct p);
	public void delete(AONContext ctx,Stream<OldProduct> ps);
	
	// PRODUCT_TAG
	public void insertProductTag(AONContext ctx,ProductTag pt);
	public void insertProductTag(AONContext ctx,Stream<ProductTag> pts);
	public void updateProductTag(AONContext ctx,ProductTag pt);
	public void deleteProductTag(AONContext ctx,ProductTag pt);
	public void deleteProductTag(AONContext ctx,Stream<ProductTag> pts);	
		
	// ITEM
	public Stream<OldItem> getItemStream(AONContext ctx, ItemFilter filter);
	public Stream<OldItem> getFullItemStream(AONContext ctx, ItemFilter filter);
	public OldItem insertItem(AONContext ctx, OldItem i);
	public void insertItemWithId(AONContext ctx,OldItem i);
	public void insertItem(AONContext ctx, Stream<OldItem> is);
	public void insertItemWithId(AONContext ctx,Stream<OldItem> is);
	public void updateItem(AONContext ctx, OldItem i);
	public void deleteItem(AONContext ctx, OldItem i);
	public void deleteItem(AONContext ctx, Stream<OldItem> is);
//	public Item save(AONContext ctx, Item item);
	
	// ITEM COMPOSITION
	public Stream<ItemComposition> getItemCompositionStream(AONContext ctx, ItemCompositionFilter filter);
	public List<ItemComposition> getItemCompositionList(AONContext ctx, ItemCompositionFilter filter);
	
	// BRAND
	
	public Stream<Brand> getBrandStream(AONContext ctx, BrandFilter filter);
	public Brand getBrand(AONContext ctx, BrandFilter filter);
	public Brand saveBrand(AONContext ctx, Brand brand);
	public void deleteBrand(AONContext ctx, Integer id);
	
	// PRODUCT CATEGORY
	
	public Stream<ProductCategory> getProductCategoryStream(AONContext ctx, ProductCategoryFilter filter);
	public ProductCategory getProductCategory(AONContext ctx, ProductCategoryFilter filter);
	public ProductCategory saveProductCategory(AONContext ctx, ProductCategory productCategory); 
	public void deleteProductCategory(AONContext ctx, Integer id); 
	
	// ITEM ADD INFO
	
	public Stream<ItemAddInfo> getItemAddInfoStream(AONContext ctx, ItemAddInfoFilter filter);
	public void insertItemAddInfo(AONContext ctx, ItemAddInfo i);
	public void updateItemAddInfo(AONContext ctx, ItemAddInfo i);

}
