package com.esferalia.aon.occam.test.offer;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.model.management.Offer;
import com.esferalia.aon.occam.impl.jooq.dao.OfferDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.faker.AonFaker;

public class OfferCRUDETest extends AbstractOccamTest {

	@Test
	public void test() {
		Offer offer = AonFaker.getOffer(ctx); 
		offer = OfferDAO.insertOffer(ctx, offer);

		Integer offerId = offer.getId();
		Offer inserted = OfferDAO.getOffer(ctx, f -> f.getIdProperty().eq(offerId));
		Asserts.assertEqualsOffer(offer, inserted);
		
		offer = OfferDAO.updateOffer(ctx, offer);
		Offer updated = OfferDAO.getOffer(ctx, f -> f.getIdProperty().eq(offerId));
		Asserts.assertEqualsOffer(offer, updated);
		
//		OfferDAO.delete(ctx, offer.getId());
//		Offer deleted = OfferDAO.getOffer(ctx, f -> f.getIdProperty().eq(offerId));
//		assertNull(deleted.getId());
	}
}
