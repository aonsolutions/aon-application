package com.esferalia.aon.occam.test.product;

import static com.esferalia.aon.jooq.tables.Customer.CUSTOMER;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Ritem.RITEM;
import static com.esferalia.aon.jooq.tables.Target.TARGET;
import static com.esferalia.aon.jooq.tables.Person.PERSON;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.catalogue.Catalogue;
import com.esferalia.aon.occam.api.model.catalogue.CatalogueItem;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.registry.RegistryItem;
import com.esferalia.aon.occam.api.model.registry.RegistryItemStatus;
import com.esferalia.aon.occam.api.model.registry.RegistryMode;
import com.esferalia.aon.occam.api.model.tariff.Tariff;
import com.esferalia.aon.occam.api.model.tariff.TariffCatalogue;
import com.esferalia.aon.occam.api.model.type.Priority;
import com.esferalia.aon.occam.impl.jooq.dao.CatalogueDAO;
import com.esferalia.aon.occam.impl.jooq.dao.CatalogueItemDAO;
import com.esferalia.aon.occam.impl.jooq.dao.CustomerDAO;
import com.esferalia.aon.occam.impl.jooq.dao.ItemDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PriceStrategyDAO;
import com.esferalia.aon.occam.impl.jooq.dao.TariffDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.AonFaker;

public class PriceStrategyDAOTest extends AbstractOccamTest {

	private static final double RITEM_PRICE     = 100.0;
	private static final double CATALOGUE_PRICE = 50.0;
	private static final double BASE_PRICE      = 10.0;
	private static final double DELTA           = 0.001;

	// Priority 1: customer-specific RITEM price
	@Test
	public void testGetUnitPrice_returnsRitemPrice() {
		Item item = ItemDAO.save(ctx, AonFaker.getItem(ctx).setPrice(BASE_PRICE));
		Customer customer = CustomerDAO.save(ctx, AonFaker.getCustomer(ctx).setTariff(null));
		ItemDAO.saveRItem(ctx, buildRitem(customer.getId(), item.getId(), RITEM_PRICE));
		try {
			Double price = PriceStrategyDAO.getUnitPrice(ctx, customer.getId(), item.getId());
			assertNotNull(price);
			assertEquals(RITEM_PRICE, price, DELTA);
		} finally {
			deleteCustomer(customer.getId());
			ItemDAO.delete(ctx, item.getId());
		}
	}

	// RITEM price = 0.0 is skipped; falls through to catalogue price
	@Test
	public void testGetUnitPrice_ritemPriceIsZero_fallsToCataloguePrice() {
		Item item = ItemDAO.save(ctx, AonFaker.getItem(ctx).setPrice(BASE_PRICE));
		Tariff tariff = TariffDAO.insert(ctx, AonFaker.getTariff(ctx));
		Catalogue catalogue = CatalogueDAO.insert(ctx, AonFaker.getCatalogue(ctx));
		TariffCatalogue tariffCatalogue = TariffDAO.insert(ctx, buildTariffCatalogue(tariff, catalogue));
		CatalogueItem catalogueItem = CatalogueItemDAO.insert(ctx, buildCatalogueItem(catalogue.getId(), item, CATALOGUE_PRICE));
		Customer customer = CustomerDAO.save(ctx, AonFaker.getCustomer(ctx).setTariff(tariff.getId()));
		ItemDAO.saveRItem(ctx, buildRitem(customer.getId(), item.getId(), 0.0));
		try {
			Double price = PriceStrategyDAO.getUnitPrice(ctx, customer.getId(), item.getId());
			assertNotNull(price);
			assertEquals(CATALOGUE_PRICE, price, DELTA);
		} finally {
			deleteCustomer(customer.getId());
			CatalogueItemDAO.delete(ctx, catalogueItem.getId());
			TariffDAO.deleteTariffCatalogue(ctx, tariffCatalogue.getId());
			CatalogueDAO.delete(ctx, catalogue.getId());
			TariffDAO.delete(ctx, tariff.getId());
			ItemDAO.delete(ctx, item.getId());
		}
	}

	// Priority 2: catalogue price via customer tariff
	@Test
	public void testGetUnitPrice_returnsCataloguePrice() {
		Item item = ItemDAO.save(ctx, AonFaker.getItem(ctx).setPrice(BASE_PRICE));
		Tariff tariff = TariffDAO.insert(ctx, AonFaker.getTariff(ctx));
		Catalogue catalogue = CatalogueDAO.insert(ctx, AonFaker.getCatalogue(ctx));
		TariffCatalogue tariffCatalogue = TariffDAO.insert(ctx, buildTariffCatalogue(tariff, catalogue));
		CatalogueItem catalogueItem = CatalogueItemDAO.insert(ctx, buildCatalogueItem(catalogue.getId(), item, CATALOGUE_PRICE));
		Customer customer = CustomerDAO.save(ctx, AonFaker.getCustomer(ctx).setTariff(tariff.getId()));
		try {
			Double price = PriceStrategyDAO.getUnitPrice(ctx, customer.getId(), item.getId());
			assertNotNull(price);
			assertEquals(CATALOGUE_PRICE, price, DELTA);
		} finally {
			deleteCustomer(customer.getId());
			CatalogueItemDAO.delete(ctx, catalogueItem.getId());
			TariffDAO.deleteTariffCatalogue(ctx, tariffCatalogue.getId());
			CatalogueDAO.delete(ctx, catalogue.getId());
			TariffDAO.delete(ctx, tariff.getId());
			ItemDAO.delete(ctx, item.getId());
		}
	}

	// Catalogue exists but has no entry for this item; falls to base item price
	@Test
	public void testGetUnitPrice_noCatalogueItemPrice_fallsToBaseItemPrice() {
		Item item = ItemDAO.save(ctx, AonFaker.getItem(ctx).setPrice(BASE_PRICE));
		Tariff tariff = TariffDAO.insert(ctx, AonFaker.getTariff(ctx));
		Catalogue catalogue = CatalogueDAO.insert(ctx, AonFaker.getCatalogue(ctx));
		TariffCatalogue tariffCatalogue = TariffDAO.insert(ctx, buildTariffCatalogue(tariff, catalogue));
		Customer customer = CustomerDAO.save(ctx, AonFaker.getCustomer(ctx).setTariff(tariff.getId()));
		try {
			Double price = PriceStrategyDAO.getUnitPrice(ctx, customer.getId(), item.getId());
			assertNotNull(price);
			assertEquals(BASE_PRICE, price, DELTA);
		} finally {
			deleteCustomer(customer.getId());
			TariffDAO.deleteTariffCatalogue(ctx, tariffCatalogue.getId());
			CatalogueDAO.delete(ctx, catalogue.getId());
			TariffDAO.delete(ctx, tariff.getId());
			ItemDAO.delete(ctx, item.getId());
		}
	}

	// Priority 3: base item price when customer has no tariff
	@Test
	public void testGetUnitPrice_customerHasNoTariff_fallsToBaseItemPrice() {
		Item item = ItemDAO.save(ctx, AonFaker.getItem(ctx).setPrice(BASE_PRICE));
		Customer customer = CustomerDAO.save(ctx, AonFaker.getCustomer(ctx).setTariff(null));
		try {
			Double price = PriceStrategyDAO.getUnitPrice(ctx, customer.getId(), item.getId());
			assertNotNull(price);
			assertEquals(BASE_PRICE, price, DELTA);
		} finally {
			deleteCustomer(customer.getId());
			ItemDAO.delete(ctx, item.getId());
		}
	}

	// Tariff exists but has no catalogue linked; falls to base item price
	@Test
	public void testGetUnitPrice_tariffHasNoCatalogue_fallsToBaseItemPrice() {
		Item item = ItemDAO.save(ctx, AonFaker.getItem(ctx).setPrice(BASE_PRICE));
		Tariff tariff = TariffDAO.insert(ctx, AonFaker.getTariff(ctx));
		Customer customer = CustomerDAO.save(ctx, AonFaker.getCustomer(ctx).setTariff(tariff.getId()));
		try {
			Double price = PriceStrategyDAO.getUnitPrice(ctx, customer.getId(), item.getId());
			assertNotNull(price);
			assertEquals(BASE_PRICE, price, DELTA);
		} finally {
			deleteCustomer(customer.getId());
			TariffDAO.delete(ctx, tariff.getId());
			ItemDAO.delete(ctx, item.getId());
		}
	}

	// No RITEM, no tariff, item not in ITEM table → null
	@Test
	public void testGetUnitPrice_itemNotFound_returnsNull() {
		Customer customer = CustomerDAO.save(ctx, AonFaker.getCustomer(ctx).setTariff(null));
		Integer nonExistentItemId = Integer.MAX_VALUE;
		try {
			Double price = PriceStrategyDAO.getUnitPrice(ctx, customer.getId(), nonExistentItemId);
			assertNull(price);
		} finally {
			deleteCustomer(customer.getId());
		}
	}

	// ---- helpers ----

	private static RegistryItem buildRitem(Integer customerId, Integer itemId, double price) {
		return new RegistryItem()
			.setDomain(ctx.getDomainId())
			.setRegistry(customerId)
			.setItem(new Item().setId(itemId))
			.setType(RegistryMode.CUSTOMER)
			.setStatus(RegistryItemStatus.ACTIVE)
			.setPriority(Priority.NORMAL)
			.setPrice(price);
	}

	private static TariffCatalogue buildTariffCatalogue(Tariff tariff, Catalogue catalogue) {
		return new TariffCatalogue()
			.setDomain(ctx.getDomainId())
			.setTariff(tariff)
			.setCatalogue(catalogue);
	}

	private static CatalogueItem buildCatalogueItem(Integer catalogueId, Item item, double price) {
		return new CatalogueItem()
			.setDomain(ctx.getDomainId())
			.setCatalogue(catalogueId)
			.setProduct(item.getProduct().getId())
			.setItem(item.getId())
			.setPrice(price);
	}

	private static void deleteCustomer(Integer customerId) {
		ctx.getDslContext().delete(RITEM).where(RITEM.REGISTRY.eq(customerId)).execute();
		ctx.getDslContext().delete(CUSTOMER).where(CUSTOMER.REGISTRY.eq(customerId)).execute();
		ctx.getDslContext().delete(TARGET).where(TARGET.REGISTRY.eq(customerId)).execute();
		ctx.getDslContext().delete(PERSON).where(PERSON.REGISTRY.eq(customerId)).execute();
		ctx.getDslContext().delete(REGISTRY).where(REGISTRY.ID.eq(customerId)).execute();
	}
}
