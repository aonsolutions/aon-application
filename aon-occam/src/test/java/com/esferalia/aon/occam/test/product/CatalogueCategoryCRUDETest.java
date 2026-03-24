package com.esferalia.aon.occam.test.product;

import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.esferalia.aon.occam.api.model.catalogue.CatalogueCategory;
import com.esferalia.aon.occam.impl.jooq.dao.CatalogueCategoryDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.faker.AonFaker;

public class CatalogueCategoryCRUDETest extends AbstractOccamTest {

	@Test
	public void test() {
		CatalogueCategory catalogueCategory = AonFaker.getCatalogueCategory(ctx);
		catalogueCategory = CatalogueCategoryDAO.insert(ctx, catalogueCategory);
		CatalogueCategory inserted = CatalogueCategoryDAO.get(ctx, catalogueCategory.getId());
		Asserts.assertEqualsCatalogueCategory(catalogueCategory, inserted);

		catalogueCategory.setDiscount(15.0).setQuantity(3.0);
		catalogueCategory = CatalogueCategoryDAO.update(ctx, catalogueCategory);
		CatalogueCategory updated = CatalogueCategoryDAO.get(ctx, catalogueCategory.getId());
		Asserts.assertEqualsCatalogueCategory(catalogueCategory, updated);

		CatalogueCategoryDAO.delete(ctx, catalogueCategory.getId());
		CatalogueCategory deleted = CatalogueCategoryDAO.get(ctx, catalogueCategory.getId());
		assertNull(deleted);
	}

}
