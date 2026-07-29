package com.esferalia.aon.occam.test.registry.seller;

import static com.esferalia.aon.occam.test.OccamAssertions.assertNull;

import org.junit.Test;

import com.esferalia.aon.occam.api.model.registry.Seller;
import com.esferalia.aon.occam.impl.jooq.dao.SellerDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.faker.AonFaker;

public class SellerTest extends AbstractOccamTest {

	@Test
	public void crudeTest() {
		Seller seller = AonFaker.getSeller( ctx ); 
		seller = SellerDAO.save(ctx, seller);
		Seller inserted = SellerDAO.get(ctx, seller.getId());

		Asserts.assertEqualsSeller(seller, inserted);
		
		seller = SellerDAO.save(ctx, seller);
		Seller updated = SellerDAO.get(ctx, seller.getId());
		Asserts.assertEqualsSeller(seller, updated);
		
		SellerDAO.delete(ctx, seller.getId());
		Seller deleted = SellerDAO.get(ctx, seller.getId());
		assertNull(deleted.getId());
	}
	
}
