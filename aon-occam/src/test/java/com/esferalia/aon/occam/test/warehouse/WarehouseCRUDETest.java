package com.esferalia.aon.occam.test.warehouse;

import static com.esferalia.aon.occam.test.OccamAssertions.assertNull;

import org.junit.Test;

import com.esferalia.aon.occam.api.model.warehouse.Warehouse;
import com.esferalia.aon.occam.impl.jooq.dao.WarehouseDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.faker.AonFaker;

public class WarehouseCRUDETest extends AbstractOccamTest {

	@Test
	public void test() {
		Warehouse warehouse = AonFaker.getWarehouse( ctx ); 
		warehouse = WarehouseDAO.save(ctx, warehouse);
		Integer warehouseId = warehouse.getId();
		Warehouse inserted = WarehouseDAO.get(ctx, f -> f.getIdProperty().eq(warehouseId));
		Asserts.assertEqualsWarehouse(warehouse, inserted);
		
		warehouse.setName("UPDATE TEST WAREHOUSE");
		warehouse = WarehouseDAO.save(ctx, warehouse);
		Warehouse updated = WarehouseDAO.get(ctx, f -> f.getIdProperty().eq(warehouseId));
		Asserts.assertEqualsWarehouse(warehouse, updated);
		
		WarehouseDAO.delete(ctx, warehouse.getId());
		Warehouse deleted = WarehouseDAO.get(ctx, f -> f.getIdProperty().eq(warehouseId));
		
		assertNull(deleted.getId());
	}

}
