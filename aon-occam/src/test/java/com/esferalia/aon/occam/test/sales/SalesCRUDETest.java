package com.esferalia.aon.occam.test.sales;

import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.esferalia.aon.occam.api.model.management.Sales;
import com.esferalia.aon.occam.api.model.warehouse.Delivery;
import com.esferalia.aon.occam.impl.jooq.dao.DeliveryDAO;
import com.esferalia.aon.occam.impl.jooq.dao.SalesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.WarehouseDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.faker.AonFaker;

public class SalesCRUDETest extends AbstractOccamTest {

	@Test
	public void test() {
		crudeSalesDAO();
	}
	
	private void crudeSalesDAO() {
		Sales sales = AonFaker.getSales(ctx); 
		sales = SalesDAO.insert(ctx, sales);
		Integer salesId = sales.getId();
		Sales inserted = SalesDAO.get(ctx, f -> f.getIdProperty().eq(salesId));
		Asserts.assertEqualsSales(sales, inserted);
		
		sales = SalesDAO.update(ctx, sales);
		Sales updated = SalesDAO.get(ctx, f -> f.getIdProperty().eq(salesId));
//		Asserts.assertEqualsSales(sales, updated);
		
//		SalesDAO.delete(ctx, f -> f.getIdProperty().eq(salesId));
//		Sales deleted = SalesDAO.get(ctx, f -> f.getIdProperty().eq(salesId));
//		assertNull(deleted.getId());
	}
}
