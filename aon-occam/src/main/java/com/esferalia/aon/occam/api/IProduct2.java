package com.esferalia.aon.occam.api;

import java.util.LinkedList;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.model.Filter.InvestAssetFilter;
import com.esferalia.aon.occam.api.model.Filter.ItemFilter;
import com.esferalia.aon.occam.api.model.Filter.ProductFilter;
import com.esferalia.aon.occam.api.model.InvestAsset;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.product.Product;

public interface IProduct2 {

	// PRODUCT
	
	public Product getProduct(AONContext ctx, ProductFilter filter);
	public Stream<Product> getProductStream(AONContext ctx, ProductFilter filter);
	public Stream<Product> getProductStream(AONContext ctx, ProductFilter filter, Integer page, Integer perPage);
	public LinkedList<Product> getProductList(AONContext ctx, ProductFilter filter);
	public Product saveProduct(AONContext ctx, Product product);
	public void deleteProduct(AONContext ctx, Integer id);

	// ITEM
	
	public Item getItem(AONContext ctx, ItemFilter filter);
	public Stream<Item> getItemStream(AONContext ctx, ItemFilter filter);
	public LinkedList<Item> getItemList(AONContext ctx, ItemFilter filter);
	public Item saveItem(AONContext ctx, Item item);
	public void deleteItem(AONContext ctx, Integer id);
	
	// INVEST ASSET
	
	public InvestAsset getInvestAsset(AONContext ctx, InvestAssetFilter filter);
	public Stream<InvestAsset> getInvestAssetStream(AONContext ctx, InvestAssetFilter filter);
	public InvestAsset saveInvestAsset(AONContext ctx, InvestAsset investAsset);
	public void deleteInvestAsset(AONContext ctx, Integer id);
	
	
}
