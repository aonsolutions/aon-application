package com.esferalia.aon.occam.test.product;

import static com.esferalia.aon.occam.test.OccamAssertions.assertEquals;
import static com.esferalia.aon.occam.test.OccamAssertions.assertThrows;

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
		assertEquals(e.getMessage(), "La fecha de incio es obligatoria");
	}

}
