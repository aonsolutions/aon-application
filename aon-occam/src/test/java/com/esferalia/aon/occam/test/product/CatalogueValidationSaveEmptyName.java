package com.esferalia.aon.occam.test.product;

import static com.esferalia.aon.occam.test.OccamAssertions.assertEquals;
import static com.esferalia.aon.occam.test.OccamAssertions.assertThrows;

import org.junit.Test;

import com.esferalia.aon.occam.api.model.catalogue.Catalogue;
import com.esferalia.aon.occam.impl.jooq.dao.CatalogueDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.AonFaker;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;

public class CatalogueValidationSaveEmptyName extends AbstractOccamTest {

	@Test
	public void test() {
		Catalogue catalogue = AonFaker.getCatalogue(ctx);
		catalogue.setName(null);
		AonCoreException e = assertThrows(AonCoreException.class, () -> CatalogueDAO.insert(ctx, catalogue));
		assertEquals(AonError.EMPTY_NAME.getMessage(), e.getMessage());
	}

}
