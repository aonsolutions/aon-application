package com.esferalia.aon.occam.test.sales;

import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.esferalia.aon.occam.api.model.management.SalesDetail;
import com.esferalia.aon.occam.impl.jooq.dao.SalesDetailDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.faker.AonFaker;

public class SalesDetailCRUDETest extends AbstractOccamTest {

	@Test
	public void test() {
		crudeSalesDetailDAO();
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
}
