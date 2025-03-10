package com.esferalia.aon.occam.api;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Filter.CatalogueFilter;
import com.esferalia.aon.occam.api.model.Filter.InvestAssetFilter;
import com.esferalia.aon.occam.api.model.Filter.ItemAddInfoFilter;
import com.esferalia.aon.occam.api.model.Filter.ItemFilter;
import com.esferalia.aon.occam.api.model.Filter.ItemTariffFilter;
import com.esferalia.aon.occam.api.model.Filter.ProductFilter;
import com.esferalia.aon.occam.api.model.Filter.RegistryItemFilter;
import com.esferalia.aon.occam.api.model.Filter.TariffFilter;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceFilter;
import com.esferalia.aon.occam.api.model.InvestAsset;
import com.esferalia.aon.occam.api.model.InvestAssetParams;
import com.esferalia.aon.occam.api.model.Options;
import com.esferalia.aon.occam.api.model.catalogue.Catalogue;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.product.ItemAddInfo;
import com.esferalia.aon.occam.api.model.product.ItemTariff;
import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.occam.api.model.product.ProductParams;
import com.esferalia.aon.occam.api.model.product.ProductTag;
import com.esferalia.aon.occam.api.model.registry.RegistryItem;
import com.esferalia.aon.occam.api.model.tariff.Tariff;
import com.esferalia.aon.occam.api.model.tariff.TariffAddInfo;
import com.esferalia.aon.occam.api.model.tariff.TariffCatalogue;
import com.esferalia.aon.occam.api.model.tariff.TariffParams;

public interface IProduct2 {

	// PRODUCT
	
	public Product getProduct(AONContext ctx, ProductFilter filter);
	public Stream<Product> getProductStream(AONContext ctx, ProductFilter filter);
	public Stream<Product> getProductStream(AONContext ctx, ProductFilter filter, Integer page, Integer perPage);
	public LinkedList<Product> getProductList(AONContext ctx, ProductFilter filter);
	public LinkedList<Product> getProductList(AONContext ctx, ProductParams params);
	public Product saveProduct(AONContext ctx, Product product);
	public Product createProduct(AONContext ctx, Product product, List<ProductTag> productTags, Item item);
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
	public void updateAllTargetItem(AONContext ctx, InvoiceFilter filter, boolean disable);
	
	// ITEM ADD INFO
	
	public Stream<ItemAddInfo> getItemAddInfoStream(CloseableAONContext ctx, ItemAddInfoFilter filter);
	public void saveItemAddInfo(CloseableAONContext ctx, ItemAddInfo itemAddInfo);
	public void deleteItemAddInfo(CloseableAONContext ctx, Integer id);
	
	// INVEST ASSET
	
	public InvestAsset getInvestAsset(AONContext ctx, InvestAssetFilter filter);
	public Stream<InvestAsset> getInvestAssetStream(AONContext ctx, InvestAssetFilter filter);
	public InvestAsset saveInvestAsset(AONContext ctx, InvestAsset investAsset);
	public void deleteInvestAsset(AONContext ctx, Integer id);
	public void assignInvestAsset2Invoice(AONContext ctx, Integer investAssetId, Invoice invoice);
	public List<InvestAsset> getInvestAssetList(CloseableAONContext ctx, InvestAssetParams params);
	public InvestAsset getInvestAsset(CloseableAONContext ctx, Integer id);
	
	// TARIFF / ITEM TARIFF
	
	public Stream<Tariff> getTariffStream(CloseableAONContext ctx, TariffFilter filter);
	public List<Tariff> getTariffList(CloseableAONContext ctx, TariffParams params);
	public void deleteTariff(CloseableAONContext ctx, Integer id);
	public Tariff saveTariff(CloseableAONContext ctx, Tariff tariff);
	public Stream<ItemTariff> getItemTariffStream(CloseableAONContext ctx, ItemTariffFilter filter);
	public void deleteItemTariff(CloseableAONContext ctx, Integer id);
	public ItemTariff saveItemTariff(CloseableAONContext ctx, ItemTariff itemTariff);
	
	public List<TariffAddInfo> getTariffAddInfoList(CloseableAONContext ctx, Integer tariffId);
	public TariffAddInfo saveTariffAddInfo(CloseableAONContext ctx, TariffAddInfo tariffAddInfo);
	public void deleteTariffAddInfo(CloseableAONContext ctx, Integer id);
	
	public List<TariffCatalogue> getTariffCatalgueList(CloseableAONContext ctx, Integer tariffId);
	public TariffCatalogue saveTariffCatalogue(CloseableAONContext ctx, TariffCatalogue tariffCatalogue);
	public void deleteTariffCatalogue(CloseableAONContext ctx, Integer id);
	
	public List<Catalogue> getCatalogueList(CloseableAONContext ctx, CatalogueFilter filter);
	
}
