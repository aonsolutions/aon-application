package com.esferalia.aon.occam.test.offer;

import static com.esferalia.aon.occam.test.OccamAssertions.assertNull;

import org.junit.Test;

import com.esferalia.aon.occam.api.model.management.OfferDetail;
import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.occam.impl.jooq.dao.ProductDAO;
import com.esferalia.aon.occam.impl.jooq.dao.offer.OfferDetailDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.faker.AonFaker;

public class OfferDetailCRUDETest extends AbstractOccamTest {

	@Test
	public void test() {
		OfferDetail detail = AonFaker.getOfferDetail(ctx); 
		detail = OfferDetailDAO.insert(ctx, detail);

		Integer detailId = detail.getId();
		OfferDetail inserted = OfferDetailDAO.get(ctx, f -> f.getIdProperty().eq(detailId));
		Asserts.assertEqualsOfferDetail(detail, inserted);
		
		detail = OfferDetailDAO.update(ctx, detail);
		OfferDetail updated = OfferDetailDAO.get(ctx, f -> f.getIdProperty().eq(detailId));
		Asserts.assertEqualsOfferDetail(detail, updated);
		
		ProductDAO.delete(ctx, detail.getId());
		Product deleted = ProductDAO.get(ctx, f -> f.getIdProperty().eq(detailId));
		
		assertNull(deleted.getId());
	}
}
