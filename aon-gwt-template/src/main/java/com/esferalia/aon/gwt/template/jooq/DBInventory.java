package com.esferalia.aon.gwt.template.jooq;



import static com.esferalia.aon.jooq.tables.Category.CATEGORY;
import static com.esferalia.aon.jooq.tables.Inventory.INVENTORY;
import static com.esferalia.aon.jooq.tables.InventoryDetail.INVENTORY_DETAIL;
import static com.esferalia.aon.jooq.tables.Item.ITEM;
import static com.esferalia.aon.jooq.tables.Product.PRODUCT;

import java.util.Vector;

import org.jooq.Record1;
import org.jooq.Record7;
import org.jooq.Record8;
import org.jooq.Result;

import com.esferalia.aon.gwt.template.server.InventoryInfo;
import com.esferalia.aon.occam.api.AONContext;

public class DBInventory {
	
	public static Vector<InventoryInfo> getInventory(String domain, Integer domainId, Integer inventoryId, Boolean close){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain, domainId);
			
			// INITIAL INVENTORY
			Result<Record8<Double, Double, String, String, String, String, String, Integer>> data ;
			if(close){
				 data = ctx.getDslContext()
						.select(INVENTORY_DETAIL.REAL_QUANTITY,INVENTORY_DETAIL.COST
								, ITEM.DETAIL, ITEM.DETAIL2, ITEM.DETAIL3
								, PRODUCT.CODE, PRODUCT.NAME, PRODUCT.CATEGORY)
						.from(INVENTORY_DETAIL).join(ITEM).on(ITEM.ID.equal(INVENTORY_DETAIL.ITEM))
						.join(PRODUCT).on(ITEM.PRODUCT.equal(PRODUCT.ID))
						.where(INVENTORY_DETAIL.INVENTORY.equal(inventoryId))
						.fetch();
			}
			else{
				data = ctx.getDslContext()
						.select(INVENTORY_DETAIL.REAL_QUANTITY,INVENTORY_DETAIL.COST
								, ITEM.DETAIL, ITEM.DETAIL2, ITEM.DETAIL3
								, PRODUCT.CODE, PRODUCT.NAME, PRODUCT.CATEGORY)
						.from(INVENTORY_DETAIL).join(ITEM).on(ITEM.ID.equal(INVENTORY_DETAIL.ITEM))
						.join(PRODUCT).on(ITEM.PRODUCT.equal(PRODUCT.ID))
						.where(INVENTORY_DETAIL.INVENTORY.equal(inventoryId))
						.and(INVENTORY_DETAIL.REAL_QUANTITY.notEqual(0.0))
						.fetch();
			}
			Vector<InventoryInfo> v = new Vector<InventoryInfo>();
			
			for (Record8<Double, Double, String, String, String, String, String, Integer> record : data) {
				InventoryInfo ii = new InventoryInfo();
				if(record.value1() != null) ii.setInventory(record.value1());
				if(!close){
					
					if(record.value2() != null) ii.setCost(record.value2());
					ii.setTotal(ii.getCost()*ii.getInventory());
				}
				else{
					
				}
				if(record.value3() != null) ii.setDetail(record.value3());
				if(record.value4() != null) ii.setDetail2(record.value4());
				if(record.value5() != null) ii.setDetail3(record.value5());
				if(record.value6() != null) ii.setProductCode(record.value6());
				if(record.value7() != null) ii.setProductName(record.value7());
				if(record.value8() != null) ii.setProductCategory(DBProduct.getCategory(domain, domainId, record.value8()).getName());
				
				v.add(ii);
			} 

			return v;
		
		} finally{
			if (ctx != null) ctx.close();
		}
	}

	public static String getInventoryName(String domain, Integer domainId, Integer inventoryId){
		
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain, domainId);
			
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
