package com.esferalia.aon.occam.test.product;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;

import org.junit.Test;

import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.catalogue.Catalogue;
import com.esferalia.aon.occam.api.model.catalogue.CatalogueItem;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.product.Product;
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
import com.esferalia.aon.occam.impl.jooq.dao.PriceStrategyDAO.PriceStrategy;
import com.esferalia.aon.occam.impl.jooq.dao.ProductDAO;
import com.esferalia.aon.occam.impl.jooq.dao.TariffDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.AonFaker;

/**
 * Tests for {@link PriceStrategyDAO#calculatePriceStrategy}, exercising each of
 * the four price resolution branches in priority order:
 * <ol>
 * <li>Registry item (RITEM) specific price for the customer.</li>
 * <li>Catalogue item price found through the customer's tariff.</li>
 * <li>Flat discount defined on the customer's tariff.</li>
 * <li>Default: the item's own price with no discount.</li>
 * </ol>
 */
public class PriceStrategyDAOTest extends AbstractOccamTest {

	private static final double DELTA = 0.0001;
	private static final long ONE_DAY = 24L * 60L * 60L * 1000L;

	/**
	 * Branch 1: a registry item with a specific price takes precedence over any
	 * tariff or catalogue pricing.
	 */
	@Test
	public void testRegistryItemPriceWins() {
		Item item = createItem(100.0);
		Customer customer = createCustomer(null);

		RegistryItem ritem = new RegistryItem()
				.setDomain(ctx.getDomainId())
				.setRegistry(customer.getId())
				.setItem(item)
				.setType(RegistryMode.CUSTOMER)
				.setStatus(RegistryItemStatus.ACTIVE)
				.setPriority(Priority.NORMAL)
				.setPrice(50.0);
		ItemDAO.saveRItem(ctx, ritem);

		PriceStrategy strategy = PriceStrategyDAO.calculatePriceStrategy(ctx, customer.getId(), new Date(), item);

		assertNotNull(strategy);
		assertEquals(50.0, strategy.getPrice(), DELTA);
	}

	/**
	 * Branch 2: with no registry item, the price comes from the catalogue item
	 * reached through the customer's tariff.
	 */
	@Test
	public void testCatalogueItemPrice() {
		Date date = new Date();
		Item item = createItem(100.0);

		Tariff tariff = TariffDAO.insert(ctx, AonFaker.getTariff(ctx).setDiscount(0.0));
		Customer customer = createCustomer(tariff.getId());

		Catalogue catalogue = CatalogueDAO.insert(ctx, AonFaker.getCatalogue(ctx)
				.setStart(new Date(date.getTime() - ONE_DAY))
				.setEnd(new Date(date.getTime() + ONE_DAY)));

		TariffDAO.insert(ctx, new TariffCatalogue()
				.setDomain(ctx.getDomainId())
				.setTariff(tariff)
				.setCatalogue(catalogue));

		CatalogueItemDAO.insert(ctx, new CatalogueItem()
				.setDomain(ctx.getDomainId())
				.setCatalogue(catalogue.getId())
				.setProduct(item.getProduct().getId())
				.setPrice(75.0)
				.setDiscount(5.0));

		PriceStrategy strategy = PriceStrategyDAO.calculatePriceStrategy(ctx, customer.getId(), date, item);

		assertNotNull(strategy);
		assertEquals(75.0, strategy.getPrice(), DELTA);
		assertEquals(5.0, strategy.getDiscountExpression().getPercentage(), DELTA);
	}

	/**
	 * Branch 3: tariff has a flat discount but no matching catalogue item, so the
	 * item's own price is returned with the tariff discount applied.
	 */
	@Test
	public void testTariffFlatDiscount() {
		Item item = createItem(100.0);

		Tariff tariff = TariffDAO.insert(ctx, AonFaker.getTariff(ctx).setDiscount(15.0));
		Customer customer = createCustomer(tariff.getId());

		PriceStrategy strategy = PriceStrategyDAO.calculatePriceStrategy(ctx, customer.getId(), new Date(), item);

		assertNotNull(strategy);
		assertEquals(100.0, strategy.getPrice(), DELTA);
		assertEquals(15.0, strategy.getDiscountExpression().getPercentage(), DELTA);
	}

	/**
	 * Branch 4: no registry item and no tariff, so the item's own price is
	 * returned with no discount.
	 */
	@Test
	public void testDefaultItemPrice() {
		Item item = createItem(100.0);
		Customer customer = createCustomer(null);

		PriceStrategy strategy = PriceStrategyDAO.calculatePriceStrategy(ctx, customer.getId(), new Date(), item);

		assertNotNull(strategy);
		assertEquals(100.0, strategy.getPrice(), DELTA);
		assertEquals(0.0, strategy.getDiscountExpression().getPercentage(), DELTA);
	}

	/** Creates and persists a fresh product + item with the given sale price. */
	private Item createItem(double price) {
		Product product = ProductDAO.save(ctx, AonFaker.getProduct(ctx));
		Item item = new Item()
				.setDomain(new Domain().setId(ctx.getDomainId()))
				.setProduct(product)
				.setPrice(price);
		return ItemDAO.save(ctx, item);
	}

	/** Creates and persists a customer optionally linked to the given tariff. */
	private Customer createCustomer(Integer tariffId) {
		Customer customer = AonFaker.getCustomer(ctx).setTariff(tariffId);
		return CustomerDAO.save(ctx, customer);
	}

}
