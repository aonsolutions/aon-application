package com.esferalia.aon.gwt.template.jooq;



import static com.esferalia.aon.jooq.tables.Inventory.INVENTORY;
import static com.esferalia.aon.jooq.tables.InventoryDetail.INVENTORY_DETAIL;
import static com.esferalia.aon.jooq.tables.Item.ITEM;
import static com.esferalia.aon.jooq.tables.Product.PRODUCT;

import java.util.Vector;

import org.jooq.Record1;
import org.jooq.Record9;
import org.jooq.Result;

import com.esferalia.aon.gwt.template.server.InventoryInfo;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Domain;

public class DBInventory {
	
	public static Vector<InventoryInfo> getInventory(Domain domain, Integer inventoryId, Boolean close, String login){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login);
			
			// INITIAL INVENTORY
			Result<Record9<Double, Double, String, String, String, String, String, Integer, String>> data ;
			if(close){
				 data = ctx.getDslContext()
						.select(INVENTORY_DETAIL.REAL_QUANTITY,INVENTORY_DETAIL.COST
								, ITEM.DETAIL, ITEM.DETAIL2, ITEM.DETAIL3
								, PRODUCT.CODE, PRODUCT.NAME, PRODUCT.CATEGORY, ITEM.SERIAL_NUMBER)
						.from(INVENTORY_DETAIL).join(ITEM).on(ITEM.ID.equal(INVENTORY_DETAIL.ITEM))
						.join(PRODUCT).on(ITEM.PRODUCT.equal(PRODUCT.ID))
						.where(INVENTORY_DETAIL.INVENTORY.equal(inventoryId))
						.fetch();
			}
			else{
				data = ctx.getDslContext()
						.select(INVENTORY_DETAIL.REAL_QUANTITY,INVENTORY_DETAIL.COST
								, ITEM.DETAIL, ITEM.DETAIL2, ITEM.DETAIL3
								, PRODUCT.CODE, PRODUCT.NAME, PRODUCT.CATEGORY, ITEM.SERIAL_NUMBER)
						.from(INVENTORY_DETAIL).join(ITEM).on(ITEM.ID.equal(INVENTORY_DETAIL.ITEM))
						.join(PRODUCT).on(ITEM.PRODUCT.equal(PRODUCT.ID))
						.where(INVENTORY_DETAIL.INVENTORY.equal(inventoryId))
						.and(INVENTORY_DETAIL.REAL_QUANTITY.notEqual(0.0))
						.fetch();
			}
			Vector<InventoryInfo> v = new Vector<InventoryInfo>();
			
			for (Record9<Double, Double, String, String, String, String, String, Integer, String> record : data) {
				InventoryInfo ii = new InventoryInfo();
				if(record.value1() != null) ii.setInventory(record.value1());
				if(!close){	
					if(record.value2() != null) ii.setCost(record.value2());
					ii.setTotal(ii.getCost()*ii.getInventory());
				}
				if(record.value3() != null) ii.setDetail(record.value3());
				if(record.value4() != null) ii.setDetail2(record.value4());
				if(record.value5() != null) ii.setDetail3(record.value5());
				if(record.value6() != null) ii.setProductCode(record.value6());
				if(record.value7() != null) ii.setProductName(record.value7());
				if(record.value8() != null) ii.setProductCategory(DBProduct.getCategory(domain.getName(), domain.getId(), record.value8(), login).getName());
				if(record.getValue(ITEM.SERIAL_NUMBER) != null) ii.setSerialNumber(record.getValue(ITEM.SERIAL_NUMBER));
				v.add(ii);
			} 

			return v;
		
		} finally{
			if (ctx != null) ctx.close();
		}
	}

	public static String getInventoryName(Domain domain, Integer inventoryId, String login){
		
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login);
			
			Record1<String> data = ctx.getDslContext().select(INVENTORY.DESCRIPTION)
				.from(INVENTORY)
				.where(INVENTORY.ID.eq(inventoryId))
				.fetchOne();
			
			return data.value1();
		} finally{
			if (ctx != null) ctx.close();
		}
	}
	
}
