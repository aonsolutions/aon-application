package net.aonsolutions.occam.api.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.watson.util.AonCollectionUtils;

import net.aonsolutions.occam.api.model.metadata.ItemMetadata;
import net.aonsolutions.occam.api.model.metadata.ItemMetadata.ItemMetadataVisitor;
import net.aonsolutions.occam.api.model.type.ProductStatus;

class ItemTest {

	@Test
	void testItem() {
		Item expected = AonMocker.mock(Item.class);
		Item actual = new Item()
			.setId(expected.getId())
			.setDomain(expected.getDomain())
			.setProduct(expected.getProduct())
			.setDetail(expected.getDetail())
			.setDetail2(expected.getDetail2())
			.setDetail3(expected.getDetail3())
			.setDescription(expected.getDescription())
			.setSerialNumber(expected.getSerialNumber())
			.setSerialDate(expected.getSerialDate())
			.setExpireDate(expected.getExpireDate())
			.setPrice(expected.getPrice())
			.setStatus(expected.getStatus())
			.setExpensesPercent(expected.getExpensesPercent())
			.setExpensesFixed(expected.getExpensesFixed())
			.setProfitPercent(expected.getProfitPercent())
			.setPurchasePrice(expected.getPurchasePrice())
			.setInternet(expected.isInternet())
			.setBarcode(expected.getBarcode())
			.setPackFormatTag(expected.getPackFormatTag().orElse(null))
			.setPackUnits(expected.getPackUnits())
			.setPackUnitsTag(expected.getPackUnitsTag().orElse(null))
			.setPackMeasurement(expected.getPackMeasurement())
			.setPackMeasurementTag(expected.getPackMeasurementTag().orElse(null))
			.setStockUnitTag(expected.getStockUnitTag().orElse(null))
			.setCreationUser(expected.getCreationUser())
			.setCreationDate(expected.getCreationDate())
			.setModificationUser(expected.getModificationUser())
			.setModificationDate(expected.getModificationDate())
			.setDeleted( expected.isDeleted() )
		;
		AonAsserts.assertClassEquals(expected, actual);
	}

	@Test
	void testItemMetadataVisitor() {
		ItemMetadataVisitor<ItemMetadata> v = new ItemMetadataVisitor<>() {
			 @Override public ItemMetadata visitId() {return ItemMetadata.ID;}
			 @Override public ItemMetadata visitDomain() {return ItemMetadata.DOMAIN;}
			 @Override public ItemMetadata visitProduct() {return ItemMetadata.PRODUCT;}
			 @Override public ItemMetadata visitDetail() {return ItemMetadata.DETAIL;}
			 @Override public ItemMetadata visitDetail2() {return ItemMetadata.DETAIL2;}
			 @Override public ItemMetadata visitDetail3() {return ItemMetadata.DETAIL3;}
			 @Override public ItemMetadata visitDescription() {return ItemMetadata.DESCRIPTION;}
			 @Override public ItemMetadata visitSerialNumber() {return ItemMetadata.SERIAL_NUMBER;}
			 @Override public ItemMetadata visitSerialDate() {return ItemMetadata.SERIAL_DATE;}
			 @Override public ItemMetadata visitExpireDate() {return ItemMetadata.EXPIRE_DATE;}
			 @Override public ItemMetadata visitPrice() {return ItemMetadata.PRICE;}
			 @Override public ItemMetadata visitStatus() {return ItemMetadata.STATUS;}
			 @Override public ItemMetadata visitExpensesPercent() {return ItemMetadata.EXPENSES_PERCENT;}
			 @Override public ItemMetadata visitExpensesFixed() {return ItemMetadata.EXPENSES_FIXED;}
			 @Override public ItemMetadata visitProfitPercent() {return ItemMetadata.PROFIT_PERCENT;}
			 @Override public ItemMetadata visitPurchasePrice() {return ItemMetadata.PURCHASE_PRICE;}
			 @Override public ItemMetadata visitInternet() {return ItemMetadata.INTERNET;}
			 @Override public ItemMetadata visitBarcode() {return ItemMetadata.BARCODE;}
			 @Override public ItemMetadata visitPackFormatTag() {return ItemMetadata.PACK_FORMAT_TAG;}
			 @Override public ItemMetadata visitPackUnits() {return ItemMetadata.PACK_UNITS;}
			 @Override public ItemMetadata visitPackUnitsTag() {return ItemMetadata.PACK_UNITS_TAG;}
			 @Override public ItemMetadata visitPackMeasurement() {return ItemMetadata.PACK_MEASUREMENT;}
			 @Override public ItemMetadata visitPackMeasurementTag() {return ItemMetadata.PACK_MEASUREMENT_TAG;}
			 @Override public ItemMetadata visitStockUnitTag() {return ItemMetadata.STOCK_UNIT_TAG;}
		};
		AonCollectionUtils.stream(ItemMetadata.values())
			.forEach(a -> assertSame(a, a.visit(v)));
	}
	
	@Test
	void testDirtyId() {
		Item ent = new Item();
		ent.setId(1);
		assertTrue( ent.isDirty(ItemMetadata.ID) );
	}
	
	@Test
	void testDirtyDomain() {
		Item ent = new Item();
		ent.setDomain(1);
		assertTrue( ent.isDirty(ItemMetadata.DOMAIN) );
	}
	
	@Test
	void testDirtyProduct() {
		Item ent = new Item();
		ent.setProduct(new Product());
		assertTrue( ent.isDirty(ItemMetadata.PRODUCT) );
	}
	
	@Test
	void testDirtyDetail() {
		Item ent = new Item();
		ent.setDetail("1");
		assertTrue( ent.isDirty(ItemMetadata.DETAIL) );
	}
	
	@Test
	void testDirtyDetail2() {
		Item ent = new Item();
		ent.setDetail2("1");
		assertTrue( ent.isDirty(ItemMetadata.DETAIL2) );
	}
	
	@Test
	void testDirtyDetail3() {
		Item ent = new Item();
		ent.setDetail3("1");
		assertTrue( ent.isDirty(ItemMetadata.DETAIL3) );
	}
	
	@Test
	void testDirtyDescription() {
		Item ent = new Item();
		ent.setDescription("1");
		assertTrue( ent.isDirty(ItemMetadata.DESCRIPTION) );
	}
	
	@Test
	void testDirtySerialNumber() {
		Item ent = new Item();
		ent.setSerialNumber("1");
		assertTrue( ent.isDirty(ItemMetadata.SERIAL_NUMBER) );
	}
	
	@Test
	void testDirtySerialDate() {
		Item ent = new Item();
		ent.setSerialDate(new Date());
		assertTrue( ent.isDirty(ItemMetadata.SERIAL_DATE) );
	}
	
	@Test
	void testDirtyExpireDate() {
		Item ent = new Item();
		ent.setExpireDate(new Date());
		assertTrue( ent.isDirty(ItemMetadata.EXPIRE_DATE) );
	}
	
	@Test
	void testDirtyPrice() {
		Item ent = new Item();
		ent.setPrice(1);
		assertTrue( ent.isDirty(ItemMetadata.PRICE) );
	}
	
	@Test
	void testDirtyStatus() {
		Item ent = new Item();
		ent.setStatus(ProductStatus.ACTIVE);
		assertTrue( ent.isDirty(ItemMetadata.STATUS) );
	}
	
	@Test
	void testDirtyExpensesPercent() {
		Item ent = new Item();
		ent.setExpensesPercent(1);
		assertTrue( ent.isDirty(ItemMetadata.EXPENSES_PERCENT) );
	}
	
	@Test
	void testDirtyExpensesFixed() {
		Item ent = new Item();
		ent.setExpensesFixed(1);
		assertTrue( ent.isDirty(ItemMetadata.EXPENSES_FIXED) );
	}
	
	@Test
	void testDirtyProfitPercent() {
		Item ent = new Item();
		ent.setProfitPercent(1);
		assertTrue( ent.isDirty(ItemMetadata.PROFIT_PERCENT) );
	}
	
	@Test
	void testDirtyPurchasePrice() {
		Item ent = new Item();
		ent.setPurchasePrice(1);
		assertTrue( ent.isDirty(ItemMetadata.PURCHASE_PRICE) );
	}
	
	@Test
	void testDirtyInternet() {
		Item ent = new Item();
		ent.setInternet(true);
		assertTrue( ent.isDirty(ItemMetadata.INTERNET) );
	}
	
	@Test
	void testDirtyBarcode() {
		Item ent = new Item();
		ent.setBarcode("1");
		assertTrue( ent.isDirty(ItemMetadata.BARCODE) );
	}
	
	@Test
	void testDirtyPackFormatTag() {
		Item ent = new Item();
		ent.setPackFormatTag(new Tag());
		assertTrue( ent.isDirty(ItemMetadata.PACK_FORMAT_TAG) );
	}
	
	@Test
	void testDirtyPackUnits() {
		Item ent = new Item();
		ent.setPackUnits(1);
		assertTrue( ent.isDirty(ItemMetadata.PACK_UNITS) );
	}
	
	@Test
	void testDirtyPackUnitsTag() {
		Item ent = new Item();
		ent.setPackUnitsTag(new Tag());
		assertTrue( ent.isDirty(ItemMetadata.PACK_UNITS_TAG) );
	}
	
	@Test
	void testDirtyPackMeasurement() {
		Item ent = new Item();
		ent.setPackMeasurement(1);
		assertTrue( ent.isDirty(ItemMetadata.PACK_MEASUREMENT) );
	}
	
	@Test
	void testDirtyPackMeasurementTag() {
		Item ent = new Item();
		ent.setPackMeasurementTag(new Tag());
		assertTrue( ent.isDirty(ItemMetadata.PACK_MEASUREMENT_TAG) );
	}
	
	@Test
	void testDirtyStockUnitTag() {
		Item ent = new Item();
		ent.setStockUnitTag(new Tag());
		assertTrue( ent.isDirty(ItemMetadata.STOCK_UNIT_TAG) );
	}
	@Test
	void testItemEquals() {
		Item a1 = new Item().setId(1);
		assertEquals(a1,a1);
		assertNotEquals(a1, (Item) null);
		assertNotEquals(null,a1);
		assertNotEquals(a1,new Object());
		assertNotEquals(a1,new Account());

		Item a2 = new Item().setId(1);
		assertEquals(a1,a2);
		Item a3 = new Item().setId(3);
		assertNotEquals(a1,a3);
	}

	@Test
	void testHashcode() {
		List<Item> objects = new ArrayList<>();
		for (int i = 0; i < 1000; i++) {
			objects.add(new Item().setId(i));
		}
		Set<Integer> hashCodes = new HashSet<>();
		for (Item obj : objects) {
			hashCodes.add(obj.hashCode());
		}
		assertEquals(objects.size(), hashCodes.size(), 10);
	}
}
