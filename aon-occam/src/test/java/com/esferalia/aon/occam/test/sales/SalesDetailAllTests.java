package com.esferalia.aon.occam.test.sales;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.esferalia.aon.occam.api.model.management.SalesDetail;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.registry.Carrier;
import com.esferalia.aon.occam.impl.jooq.dao.SalesDetailDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.faker.AonFaker;

public class SalesDetailAllTests extends AbstractOccamTest {

	@Test
	public void test() {
		crudeSalesDetailDAO();
		getItemTest();
		getDiscountExpressionTest();
		getCarrierTest();
	}
	
	private void crudeSalesDetailDAO() {
		SalesDetail salesDetail = AonFaker.getSalesDetail(ctx); 
		salesDetail = SalesDetailDAO.insert(ctx, salesDetail);
		Integer salesDetailId = salesDetail.getId();
		SalesDetail inserted = SalesDetailDAO.get(ctx, f -> f.getIdProperty().eq(salesDetailId));
		Asserts.assertEqualsSalesDetail(salesDetail, inserted);
		
		salesDetail = SalesDetailDAO.update(ctx, salesDetail);
		SalesDetail updated = SalesDetailDAO.get(ctx, f -> f.getIdProperty().eq(salesDetailId));
		Asserts.assertEqualsSalesDetail(salesDetail, updated);
		
		SalesDetailDAO.delete(ctx, f -> f.getIdProperty().eq(salesDetailId));
		SalesDetail deleted = SalesDetailDAO.get(ctx, f -> f.getIdProperty().eq(salesDetailId));
		assertNull(deleted.getId());
	}
	
	private void getItemTest() {
		SalesDetail sales = AonFaker.getSalesDetail(ctx);
		Item item = AonFaker.getItem(ctx);

		sales.setItem(item);

		Asserts.assertEqualsItem(item, sales.getItem());

		sales.setItem(null);
		assertTrue(sales.getItem().isEmpty());
	}
	
	private void getDiscountExpressionTest() {
		SalesDetail sales = AonFaker.getSalesDetail(ctx);
		String discount = null;
		sales.setDiscountExpression(discount);

		assertEquals("0.0", sales.getDiscountExpression().getDiscountExpr());
	}
	
	private void getCarrierTest() {
	    SalesDetail sales = AonFaker.getSalesDetail(ctx);
	    Carrier carrier = AonFaker.getCarrier(ctx);

	    sales.setCarrier(carrier);

	    Asserts.assertEqualsCarrier(carrier, sales.getCarrier());

	    sales.setCarrier(null);
	    assertTrue(sales.getCarrier().isEmpty());
	}
}
