package com.esferalia.aon.occam.impl.jooq;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.IProduct2;
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
import com.esferalia.aon.occam.impl.jooq.dao.CatalogueDAO;
import com.esferalia.aon.occam.impl.jooq.dao.InvestAssetDAO;
import com.esferalia.aon.occam.impl.jooq.dao.ItemAddInfoDAO;
import com.esferalia.aon.occam.impl.jooq.dao.ItemDAO;
import com.esferalia.aon.occam.impl.jooq.dao.ItemTariffDAO;
import com.esferalia.aon.occam.impl.jooq.dao.ProductDAO;
import com.esferalia.aon.occam.impl.jooq.dao.ProductOldDAO;
import com.esferalia.aon.occam.impl.jooq.dao.TargetItemDAO;
import com.esferalia.aon.occam.impl.jooq.dao.TariffDAO;

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
	public LinkedList<Product> getProductList(AONContext ctx, ProductParams params) {
		return ctx.getDslContext().transactionResult( configuration -> 
			ProductDAO.getList(ctx, params));
	}
	
	@Override
	public Product saveProduct(AONContext ctx, Product product) {
		return ctx.getDslContext().transactionResult( configuration -> 
			ProductDAO.save(ctx, product));
	}
	
	@Override
	public Product createProduct(AONContext ctx, Product product, List<ProductTag> productTags, Item item) {
		return ctx.getDslContext().transactionResult( configuration -> {
			// Product
			Product newProduct = ProductDAO.save(ctx, product);
			
			// Product Tags
			productTags.forEach(productTag -> productTag.setProduct(newProduct.getId()));
			ProductOldDAO.insertProductTag(ctx, productTags.stream());
			
			// Item
			item.setProduct(newProduct);
			ItemDAO.save(ctx, item);
			
			return newProduct;
		});
	}
	
	@Override
	public void deleteProduct(AONContext ctx, Integer id) {
		ctx.getDslContext().transaction( configuration -> 
			ProductDAO.delete(ctx, id));
	}

	// ------------------------------------- ITEM
	
	@Override
	public Item getItem(AONContext ctx, ItemFilter filter, Options...options) {
		return ctx.getDslContext().transactionResult( configuration -> 
			ItemDAO.get(ctx, filter, options));
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
	public void updateRItemQuantity(AONContext ctx, String quantity, RegistryItemFilter filter) {
		ctx.getDslContext().transaction( configuration -> 
		ItemDAO.updateRItemQuantity(ctx, quantity, filter));
	}

	@Override
	public void deleteItem(AONContext ctx, Integer id) {
		ctx.getDslContext().transaction( configuration -> 
			ItemDAO.delete(ctx, id));
	}
	
	@Override
	public void updateAllTargetItem(AONContext ctx, InvoiceFilter filter, boolean disable) {
		ctx.getDslContext().transaction( configuration -> 
			TargetItemDAO.updateAllTargetItem(ctx, filter, disable));
	}
	
	// ------------------------------------- ITEM ADD INFO

	@Override
	public Stream<ItemAddInfo> getItemAddInfoStream(CloseableAONContext ctx, ItemAddInfoFilter filter) {
		return ctx.getDslContext().transactionResult(configuration -> ItemAddInfoDAO.getStream(ctx, filter));
	}

	@Override
	public void saveItemAddInfo(CloseableAONContext ctx, ItemAddInfo itemAddInfo) {
		ctx.getDslContext().transaction( configuration -> ItemAddInfoDAO.save(ctx, itemAddInfo));
	}

	@Override
	public void deleteItemAddInfo(CloseableAONContext ctx, Integer id) {
		ctx.getDslContext().transaction( configuration -> ItemAddInfoDAO.delete(ctx, id));
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

	@Override
	public void assignInvestAsset2Invoice(AONContext ctx, Integer investAssetId, Invoice invoice) {
		// TODO Auto-generated method stub
		ctx.getDslContext().transaction( configuration -> 
		InvestAssetDAO.assignInvestAsset2Invoice(ctx, investAssetId, invoice));
	}

	@Override
	public List<InvestAsset> getInvestAssetList(CloseableAONContext ctx, InvestAssetParams params) {
		return ctx.getDslContext().transactionResult(configuration -> 
		InvestAssetDAO.getInvestAssetList(ctx, params));
	}

	@Override
	public InvestAsset getInvestAsset(CloseableAONContext ctx, Integer id) {
		return ctx.getDslContext().transactionResult(configuration -> 
		InvestAssetDAO.getInvestAsset(ctx, id));
	}
	
	// ------------------------------------- TARIFF / ITEM TARIFF

	@Override
	public Stream<Tariff> getTariffStream(CloseableAONContext ctx, TariffFilter filter) {
		return ctx.getDslContext().transactionResult(configuration -> TariffDAO.getStream(ctx, filter));
	}

	@Override
	public List<Tariff> getTariffList(CloseableAONContext ctx, TariffParams params) {
		return ctx.getDslContext().transactionResult(configuration -> TariffDAO.getTariffList(ctx, params));
	}

	@Override
	public void deleteTariff(CloseableAONContext ctx, Integer id) {
		ctx.getDslContext().transaction( configuration -> TariffDAO.delete(ctx, id));
	}

	@Override
	public Tariff saveTariff(CloseableAONContext ctx, Tariff tariff) {
		return ctx.getDslContext().transactionResult(configuration -> TariffDAO.save(ctx, tariff));
	}

	@Override
	public Stream<ItemTariff> getItemTariffStream(CloseableAONContext ctx, ItemTariffFilter filter) {
		return ctx.getDslContext().transactionResult(configuration -> ItemTariffDAO.getStream(ctx, filter));
	}

	@Override
	public void deleteItemTariff(CloseableAONContext ctx, Integer id) {
		ctx.getDslContext().transaction( configuration -> ItemTariffDAO.delete(ctx, id));
	}

	@Override
	public ItemTariff saveItemTariff(CloseableAONContext ctx, ItemTariff itemTariff) {
		return ctx.getDslContext().transactionResult(configuration -> ItemTariffDAO.save(ctx, itemTariff));
	}

	@Override
	public List<TariffAddInfo> getTariffAddInfoList(CloseableAONContext ctx, Integer tariffId) {
		return ctx.getDslContext().transactionResult(configuration -> TariffDAO.getTariffAddInfoList(ctx, tariffId));
	}

	@Override
	public TariffAddInfo saveTariffAddInfo(CloseableAONContext ctx, TariffAddInfo tariffAddInfo) {
		return ctx.getDslContext().transactionResult(configuration -> TariffDAO.saveTariffAddInfo(ctx, tariffAddInfo));
	}

	@Override
	public void deleteTariffAddInfo(CloseableAONContext ctx, Integer id) {
		ctx.getDslContext().transaction( configuration -> TariffDAO.deleteTariffAddInfo(ctx, id));
	}

	@Override
	public List<TariffCatalogue> getTariffCatalgueList(CloseableAONContext ctx, Integer tariffId) {
		return ctx.getDslContext().transactionResult(configuration -> TariffDAO.getTariffCatalgueList(ctx, tariffId));
	}

	@Override
	public TariffCatalogue saveTariffCatalogue(CloseableAONContext ctx, TariffCatalogue tariffCatalogue) {
		return ctx.getDslContext().transactionResult(configuration -> TariffDAO.saveTariffCatalogue(ctx, tariffCatalogue));
	}

	@Override
	public void deleteTariffCatalogue(CloseableAONContext ctx, Integer id) {
		ctx.getDslContext().transaction( configuration -> TariffDAO.deleteTariffCatalogue(ctx, id));
	}

	@Override
	public List<Catalogue> getCatalogueList(CloseableAONContext ctx, CatalogueFilter filter) {
		return ctx.getDslContext().transactionResult(configuration -> CatalogueDAO.getStream(ctx, filter).collect(Collectors.toList()));
	}

}
