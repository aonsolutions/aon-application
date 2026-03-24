package com.esferalia.aon.occam.test.product;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import org.junit.Test;

import com.esferalia.aon.occam.api.model.catalogue.Catalogue;
import com.esferalia.aon.occam.impl.jooq.dao.CatalogueDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.AonFaker;
import com.esferalia.aon.watson.error.AonCoreException;

public class CatalogueValidationSaveEmptyStartDate extends AbstractOccamTest {

	@Test
	public void test() {
		Catalogue catalogue = AonFaker.getCatalogue(ctx);
		catalogue.setStart(null);
		AonCoreException e = assertThrows(AonCoreException.class, () -> CatalogueDAO.insert(ctx, catalogue));
		assertEquals("La fecha de incio es obligatoria", e.getMessage());
	}

}
