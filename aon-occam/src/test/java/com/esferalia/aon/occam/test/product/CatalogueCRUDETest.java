package com.esferalia.aon.occam.test.product;

import static com.esferalia.aon.occam.test.OccamAssertions.assertNull;

import org.junit.Test;

import com.esferalia.aon.occam.api.model.catalogue.Catalogue;
import com.esferalia.aon.occam.impl.jooq.dao.CatalogueDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.faker.AonFaker;

public class CatalogueCRUDETest extends AbstractOccamTest {

	@Test
	public void test() {
		Catalogue catalogue = AonFaker.getCatalogue(ctx);
		catalogue = CatalogueDAO.insert(ctx, catalogue);
		Catalogue inserted = CatalogueDAO.get(ctx, catalogue.getId());
		Asserts.assertEqualsCatalogue(catalogue, inserted);

		catalogue.setName("Updated Name").setPurchase(!catalogue.isPurchase());
		catalogue = CatalogueDAO.update(ctx, catalogue);
		Catalogue updated = CatalogueDAO.get(ctx, catalogue.getId());
		Asserts.assertEqualsCatalogue(catalogue, updated);

		CatalogueDAO.delete(ctx, catalogue.getId());
		Catalogue deleted = CatalogueDAO.get(ctx, catalogue.getId());
		assertNull(deleted);
	}

}
