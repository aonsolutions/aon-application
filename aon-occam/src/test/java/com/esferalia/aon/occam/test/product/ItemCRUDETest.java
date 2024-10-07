package com.esferalia.aon.occam.test.product;

import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.impl.jooq.dao.ItemDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.Repeat;
import com.esferalia.aon.occam.test.faker.AonFaker;

public class ItemCRUDETest extends AbstractOccamTest {

	@Test
	@Repeat(20)
	public void test() {
		Item item = AonFaker.getItem( ctx ); 
		item = ItemDAO.insert(ctx, item);

		Integer itemId = item.getId();
		Item inserted = ItemDAO.get(ctx, f -> f.getIdProperty().eq(itemId));
		Asserts.assertEqualsItem(item, inserted);
		
		item = ItemDAO.update(ctx, item);
		Item updated = ItemDAO.get(ctx, f -> f.getIdProperty().eq(itemId));
		Asserts.assertEqualsItem(item, updated);
		
		ItemDAO.delete(ctx, item.getId());
		Item deleted = ItemDAO.get(ctx, f -> f.getIdProperty().eq(itemId));
		
		assertNull(deleted.getId());
	}

}
