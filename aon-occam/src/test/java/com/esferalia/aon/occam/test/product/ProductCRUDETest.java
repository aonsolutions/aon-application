package com.esferalia.aon.occam.test.product;

import static com.esferalia.aon.occam.test.OccamAssertions.assertNull;

import org.junit.Test;

import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.occam.impl.jooq.dao.ProductDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.faker.AonFaker;

public class ProductCRUDETest extends AbstractOccamTest {

	@Test
	public void test() {
		Product product = AonFaker.getProduct( ctx ); 
		product = ProductDAO.insert(ctx, product);

		Integer productId = product.getId();
		Product inserted = ProductDAO.get(ctx, f -> f.getIdProperty().eq(productId));
		Asserts.assertEqualsProduct(product, inserted);
		
		product = ProductDAO.update(ctx, product);
		Product updated = ProductDAO.get(ctx, f -> f.getIdProperty().eq(productId));
		Asserts.assertEqualsProduct(product, updated);
		
		ProductDAO.delete(ctx, product.getId());
		Product deleted = ProductDAO.get(ctx, f -> f.getIdProperty().eq(productId));
		
		assertNull(deleted.getId());
	}

}
