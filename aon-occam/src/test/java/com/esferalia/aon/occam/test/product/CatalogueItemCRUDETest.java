package com.esferalia.aon.occam.test.product;

import static com.esferalia.aon.occam.test.OccamAssertions.assertNull;

import org.junit.Test;

import com.esferalia.aon.occam.api.model.catalogue.CatalogueItem;
import com.esferalia.aon.occam.impl.jooq.dao.CatalogueItemDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.faker.AonFaker;

public class CatalogueItemCRUDETest extends AbstractOccamTest {

	@Test
	public void test() {
		CatalogueItem catalogueItem = AonFaker.getCatalogueItem(ctx);
		catalogueItem = CatalogueItemDAO.insert(ctx, catalogueItem);
		CatalogueItem inserted = CatalogueItemDAO.get(ctx, catalogueItem.getId());
		Asserts.assertEqualsCatalogueItem(catalogueItem, inserted);

		catalogueItem.setPrice(99.99).setDiscount(10.0).setQuantity(5.0);
		catalogueItem = CatalogueItemDAO.update(ctx, catalogueItem);
		CatalogueItem updated = CatalogueItemDAO.get(ctx, catalogueItem.getId());
		Asserts.assertEqualsCatalogueItem(catalogueItem, updated);

		CatalogueItemDAO.delete(ctx, catalogueItem.getId());
		CatalogueItem deleted = CatalogueItemDAO.get(ctx, catalogueItem.getId());
		assertNull(deleted);
	}

}
