package com.esferalia.aon.occam.api;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Filter.InvestAssetFilter;
import com.esferalia.aon.occam.api.model.Filter.ItemFilter;
import com.esferalia.aon.occam.api.model.Filter.ProductFilter;
import com.esferalia.aon.occam.api.model.Filter.RegistryItemFilter;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceFilterOLD;
import com.esferalia.aon.occam.api.model.InvestAsset;
import com.esferalia.aon.occam.api.model.InvestAssetParams;
import com.esferalia.aon.occam.api.model.Options;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.occam.api.model.registry.RegistryItem;

public interface IProduct2 {

	// PRODUCT
	
	public Product getProduct(AONContext ctx, ProductFilter filter);
	public Stream<Product> getProductStream(AONContext ctx, ProductFilter filter);
	public Stream<Product> getProductStream(AONContext ctx, ProductFilter filter, Integer page, Integer perPage);
	public LinkedList<Product> getProductList(AONContext ctx, ProductFilter filter);
	public Product saveProduct(AONContext ctx, Product product);
	public void deleteProduct(AONContext ctx, Integer id);

	// ITEM
	
	public Item getItem(AONContext ctx, ItemFilter filter, Options...options);
	public Stream<Item> getItemStream(AONContext ctx, ItemFilter filter);
	public Stream<Item> getItemStream(AONContext ctx, ItemFilter filter, Integer page, Integer perPage);
	public Stream<Item> getRItemStream(AONContext ctx, ItemFilter filter);
	public LinkedList<Item> getItemList(AONContext ctx, ItemFilter filter);
	public Item saveItem(AONContext ctx, Item item);
	public void deleteItem(AONContext ctx, Integer id);
	public RegistryItem[] saveRItem(AONContext ctx, RegistryItem ...ritems);
	public void deleteRItem(AONContext ctx, RegistryItemFilter filter);
	public void updateRItemQuantity(AONContext ctx, String quantity, RegistryItemFilter filter);
	public void updateAllTargetItem(AONContext ctx, InvoiceFilterOLD filter, boolean disable);
	
	// INVEST ASSET
	
	public InvestAsset getInvestAsset(AONContext ctx, InvestAssetFilter filter);
	public Stream<InvestAsset> getInvestAssetStream(AONContext ctx, InvestAssetFilter filter);
	public InvestAsset saveInvestAsset(AONContext ctx, InvestAsset investAsset);
	public void deleteInvestAsset(AONContext ctx, Integer id);
	public void assignInvestAsset2Invoice(AONContext ctx, Integer investAssetId, Invoice invoice);
	public List<InvestAsset> getInvestAssetList(CloseableAONContext ctx, InvestAssetParams params);
	public InvestAsset getInvestAsset(CloseableAONContext ctx, Integer id);

	
}
