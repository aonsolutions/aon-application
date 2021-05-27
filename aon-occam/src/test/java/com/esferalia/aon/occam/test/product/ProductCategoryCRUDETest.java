package com.esferalia.aon.occam.test.product;

import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.esferalia.aon.occam.api.model.product.ProductCategory;
import com.esferalia.aon.occam.impl.jooq.dao.ProductCategoryDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.faker.AonFaker;

public class ProductCategoryCRUDETest extends AbstractOccamTest {

	@Test
	public void test() {
		ProductCategory productCategory = AonFaker.getProductCategory(ctx); 
		productCategory = ProductCategoryDAO.insert(ctx, productCategory);
		Integer productCategoryId = productCategory.getId();
		ProductCategory inserted = ProductCategoryDAO.get(ctx, f -> f.getIdProperty().eq(productCategoryId));
		Asserts.assertEqualsProductCategory(productCategory, inserted);
		
		ProductCategory updateProductCategory = AonFaker.getProductCategory(ctx);
		updateProductCategory.setId(productCategoryId);
		productCategory = ProductCategoryDAO.update(ctx, updateProductCategory);
		ProductCategory updated = ProductCategoryDAO.get(ctx, f -> f.getIdProperty().eq(productCategoryId));
		Asserts.assertEqualsProductCategory(productCategory, updated);
		
		ProductCategoryDAO.delete(ctx, productCategory.getId());
		ProductCategory deleted = ProductCategoryDAO.get(ctx, f -> f.getIdProperty().eq(productCategoryId));
		
		assertNull(deleted.getId());
	}

}
