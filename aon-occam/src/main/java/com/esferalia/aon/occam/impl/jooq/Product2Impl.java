package com.esferalia.aon.occam.impl.jooq;

import java.util.LinkedList;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.IProduct2;
import com.esferalia.aon.occam.api.model.Filter.InvestAssetFilter;
import com.esferalia.aon.occam.api.model.Filter.ItemFilter;
import com.esferalia.aon.occam.api.model.Filter.ProductFilter;
import com.esferalia.aon.occam.api.model.Filter.RegistryItemFilter;
import com.esferalia.aon.occam.api.model.InvestAsset;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.occam.api.model.registry.RegistryItem;
import com.esferalia.aon.occam.impl.jooq.dao.InvestAssetDAO;
import com.esferalia.aon.occam.impl.jooq.dao.ItemDAO;
import com.esferalia.aon.occam.impl.jooq.dao.ProductDAO;

public class Product2Impl implements IProduct2{
	// ------------------------------------- PRODUCT

	@Override
	public Product getProduct(AONContext ctx, ProductFilter filter) {
		return ctx.getDslContext().transactionResult( configuration -> 
			ProductDAO.get(ctx, filter));
	}
	
	@Override
	public Stream<Product> getProductStream(AONContext ctx, ProductFilter filter) {
		return ctx.getDslContext().transactionResult( configuration -> 
			ProductDAO.getStream(ctx, filter));
	}
	
	@Override
	public Stream<Product> getProductStream(AONContext ctx, ProductFilter filter, Integer page, Integer perPage) {
		return ctx.getDslContext().transactionResult( configuration -> 
			ProductDAO.getStream(ctx, filter, page, perPage));
	}
	
	
	@Override
	public LinkedList<Product> getProductList(AONContext ctx, ProductFilter filter) {
		return ctx.getDslContext().transactionResult( configuration -> 
			ProductDAO.getList(ctx, filter));
	}
	
	@Override
	public Product saveProduct(AONContext ctx, Product product) {
		return ctx.getDslContext().transactionResult( configuration -> 
			ProductDAO.save(ctx, product));
	}
	
	@Override
	public void deleteProduct(AONContext ctx, Integer id) {
		ctx.getDslContext().transaction( configuration -> 
			ProductDAO.delete(ctx, id));
	}

	// ------------------------------------- ITEM
	
	@Override
	public Item getItem(AONContext ctx, ItemFilter filter) {
		return ctx.getDslContext().transactionResult( configuration -> 
			ItemDAO.get(ctx, filter));
	}
	
	@Override
	public Stream<Item> getItemStream(AONContext ctx, ItemFilter filter) {
		return ctx.getDslContext().transactionResult( configuration -> 
			ItemDAO.getStream(ctx, filter));
	}
	
	@Override
	public Stream<Item> getItemStream(AONContext ctx, ItemFilter filter, Integer page, Integer perPage) {
		return ctx.getDslContext().transactionResult( configuration -> 
			ItemDAO.getStream(ctx, filter, page, perPage));
	}
	
	
	@Override
	public LinkedList<Item> getItemList(AONContext ctx, ItemFilter filter) {
		return ctx.getDslContext().transactionResult( configuration -> 
			ItemDAO.getList(ctx, filter));
	}
	
	@Override
	public Stream<Item> getRItemStream(AONContext ctx, ItemFilter filter) {
		return ctx.getDslContext().transactionResult( configuration -> 
			ItemDAO.getRItemStream(ctx, filter));
	}
	
	@Override
	public Item saveItem(AONContext ctx, Item item) {
		return ctx.getDslContext().transactionResult( configuration -> 
			ItemDAO.save(ctx, item));
	}
	
	@Override
	public RegistryItem[] saveRItem(AONContext ctx, RegistryItem ...ritems) {
		return ctx.getDslContext().transactionResult(configuration -> ItemDAO.saveRItem(ctx, ritems));
	}
	
	@Override
	public void deleteRItem(AONContext ctx, RegistryItemFilter filter) {
		ctx.getDslContext().transaction( configuration -> 
		ItemDAO.deleteRItem(ctx, filter));
	}

	@Override
	public void deleteItem(AONContext ctx, Integer id) {
		ctx.getDslContext().transaction( configuration -> 
			ItemDAO.delete(ctx, id));
	}

	// ------------------------------------- INVEST ASSET
	
	@Override
	public InvestAsset getInvestAsset(AONContext ctx, InvestAssetFilter filter) {
		return ctx.getDslContext().transactionResult( configuration -> 
			InvestAssetDAO.get(ctx, filter));	
	}

	@Override
	public Stream<InvestAsset> getInvestAssetStream(AONContext ctx, InvestAssetFilter filter) {
		return ctx.getDslContext().transactionResult(configuration -> 
			InvestAssetDAO.getStream(ctx, filter));	
	}

	@Override
	public InvestAsset saveInvestAsset(AONContext ctx, InvestAsset investAsset) {
		return ctx.getDslContext().transactionResult(configuration -> 
			InvestAssetDAO.save(ctx, investAsset));
	}

	@Override
	public void deleteInvestAsset(AONContext ctx, Integer id) {
		ctx.getDslContext().transaction( configuration -> 
			InvestAssetDAO.delete(ctx, id));
	}

}
