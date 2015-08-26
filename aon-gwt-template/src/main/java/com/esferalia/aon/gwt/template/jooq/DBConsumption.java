package com.esferalia.aon.gwt.template.jooq;


import static com.esferalia.aon.jooq.tables.Delivery.DELIVERY;
import static com.esferalia.aon.jooq.tables.DeliveryDetail.DELIVERY_DETAIL;
import static com.esferalia.aon.jooq.tables.Income.INCOME;
import static com.esferalia.aon.jooq.tables.IncomeDetail.INCOME_DETAIL;
import static com.esferalia.aon.jooq.tables.Inventory.INVENTORY;
import static com.esferalia.aon.jooq.tables.InventoryDetail.INVENTORY_DETAIL;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.InvoiceDetail.INVOICE_DETAIL;
import static com.esferalia.aon.jooq.tables.Item.ITEM;
import static com.esferalia.aon.jooq.tables.Product.PRODUCT;
import static com.esferalia.aon.jooq.tables.Warehouse.WAREHOUSE;
import static com.esferalia.aon.jooq.tables.WarehouseTransfer.WAREHOUSE_TRANSFER;
import static com.esferalia.aon.jooq.tables.WarehouseTransferDetail.WAREHOUSE_TRANSFER_DETAIL;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.Record3;
import org.jooq.Record6;
import org.jooq.Result;

import com.esferalia.aon.gwt.template.shared.ConsumptionItem;
import com.esferalia.aon.occam.api.AONContext;

public class DBConsumption {
	
	public static Map<Integer, ConsumptionItem> getConsumption(String domain, Integer domainId
			, Integer initialId, Integer finalId, Integer warehouseId, Date initialDate, Date finalDate){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain, domainId);
			
			// INITIAL INVENTORY
			
			Result<Record3<Integer, Double, Double>> data = ctx.getDslContext().select(INVENTORY_DETAIL.ITEM,INVENTORY_DETAIL.REAL_QUANTITY, INVENTORY_DETAIL.COST)
						.from(INVENTORY_DETAIL).join(INVENTORY).on(INVENTORY.ID.eq(INVENTORY_DETAIL.INVENTORY))
						.where(INVENTORY.ID.equal(initialId))
						.fetch();
			
			Map<Integer, ConsumptionItem> map = new HashMap<Integer, ConsumptionItem>();
			
			for (Record3<Integer, Double, Double> record : data) {
				if(record.value1() != null){
					 if(!map.containsKey(record.value1())){
						 if(record.value2() != 0){
							ConsumptionItem ci = getConsumptionItem(ctx, record.value1());
						 	ci.setInitialQuantity(record.value2());
						 	ci.setInitialValue(record.value3());
						 	map.put(ci.getItemId(), ci);
						 }
					 }
				}
			}
		
		
			// FINAL INVENTORY

			Result<Record3<Integer, Double, Double>> data2 = ctx.getDslContext()
					.select(INVENTORY_DETAIL.ITEM,INVENTORY_DETAIL.REAL_QUANTITY, INVENTORY_DETAIL.COST)
					.from(INVENTORY_DETAIL).join(INVENTORY)
					.on(INVENTORY.ID.eq(INVENTORY_DETAIL.INVENTORY))
					.where(INVENTORY.ID.equal(finalId)).fetch();

			for (Record3<Integer, Double, Double> record : data2) {
				if (record.value1() != null) {
					if(!map.containsKey(record.value1())) {
						if(record.value2() != 0){
							ConsumptionItem ci = getConsumptionItem(ctx, record.value1());
							ci.setFinalQuantity(record.value2());
							ci.setFinalValue(record.value3());
							map.put(ci.getItemId(), ci);
						}
					}
					else{
						ConsumptionItem ci = map.get(record.value1());
						ci.setFinalQuantity(record.value2());
						ci.setFinalValue(record.value3());
						map.replace(ci.getItemId(), ci);
					}
				}
			}
			
			// COMPRAS (ALBARANES)
			
			Result<Record3<Integer, Double, Double>> data3 = ctx.getDslContext()
					.select(INCOME_DETAIL.ITEM, INCOME_DETAIL.QUANTITY, INCOME_DETAIL.PRICE)
					.from(INCOME).join(INCOME_DETAIL).on(INCOME.ID.equal(INCOME_DETAIL.INCOME))
					.where(INCOME_DETAIL.WAREHOUSE.equal(warehouseId))
					.and(INCOME.ISSUE_TIME.greaterOrEqual(initialDate))
					.and(INCOME.ISSUE_TIME.lessOrEqual(finalDate))
					.fetch();

			for (Record3<Integer, Double, Double> record : data3) {
				if (record.value1() != null) {
					if(!map.containsKey(record.value1())) {
						if(record.value2() != 0){
							ConsumptionItem ci = getConsumptionItem(ctx, record.value1());
							ci.setPurchases(record.value2());
							ci.setPurchasesValue(record.value3());
							map.put(ci.getItemId(), ci);
						}
					}
					else{
						ConsumptionItem ci = map.get(record.value1());
						ci.setPurchases(ci.getPurchases()+record.value2());
						ci.setPurchasesValue(ci.getPurchasesValue()+record.value3());
						map.replace(ci.getItemId(), ci);
					}
				}
			}
			
			// COMPRAS (Facturas)
			
			Result<Record3<Integer, Double, Double>> data4 = ctx.getDslContext()
					.select(INVOICE_DETAIL.ITEM, INVOICE_DETAIL.QUANTITY, INVOICE_DETAIL.TAXABLE_BASE)
					.from(INVOICE).join(INVOICE_DETAIL).on(INVOICE.ID.equal(INVOICE_DETAIL.INVOICE))
					.where(INVOICE_DETAIL.WAREHOUSE.equal(warehouseId))
					.and(INVOICE.TYPE.equal((byte)0))
					.and(INVOICE_DETAIL.SOURCE.notEqual((byte)3))
					.and(INVOICE.ISSUE_DATE.greaterOrEqual(initialDate))
					.and(INVOICE.ISSUE_DATE.lessOrEqual(finalDate))
					.fetch();

			for (Record3<Integer, Double, Double> record : data4) {
				if (record.value1() != null) {
					if(!map.containsKey(record.value1())) {
						if(record.value2() != 0){
							ConsumptionItem ci = getConsumptionItem(ctx, record.value1());
							ci.setPurchases(record.value2());
							ci.setPurchasesValue(record.value3());
							map.put(ci.getItemId(), ci);
						}
					}
					else{
						ConsumptionItem ci = map.get(record.value1());
						ci.setPurchases(ci.getPurchases()+record.value2());
						ci.setPurchases(ci.getPurchasesValue()+record.value3());
						map.replace(ci.getItemId(), ci);
					}
				}
			}
			
			// VENTAS (ALBARANES)

			Result<Record3<Integer, Double, Double>> data5 = ctx.getDslContext()
					.select(DELIVERY_DETAIL.ITEM, DELIVERY_DETAIL.QUANTITY, DELIVERY_DETAIL.PRICE)
					.from(DELIVERY).join(DELIVERY_DETAIL).on(DELIVERY.ID.equal(DELIVERY_DETAIL.DELIVERY))
					.where(DELIVERY_DETAIL.WAREHOUSE.equal(warehouseId))
					.and(DELIVERY.ISSUE_TIME.greaterOrEqual(new Timestamp(initialDate.getTime())))
					.and(DELIVERY.ISSUE_TIME.lessOrEqual(new Timestamp(finalDate.getTime())))
					.fetch();

			for (Record3<Integer, Double, Double> record : data5) {
				if (record.value1() != null) {
					if(!map.containsKey(record.value1())) {
						if(record.value2() != 0){
							ConsumptionItem ci = getConsumptionItem(ctx, record.value1());
							ci.setSales(record.value2());
							ci.setSalesValue(record.value3());
							map.put(ci.getItemId(), ci);
						}
					}
					else{
						ConsumptionItem ci = map.get(record.value1());
						ci.setSales(ci.getSales()+record.value2());
						ci.setSalesValue(ci.getSalesValue()+ record.value3());
						map.replace(ci.getItemId(), ci);
					}
				}
			}
			
			// VENTAS (FACTURAS)
			
			Result<Record3<Integer, Double, Double>> data6 = ctx.getDslContext()
					.select(INVOICE_DETAIL.ITEM, INVOICE_DETAIL.QUANTITY, INVOICE.TAXABLE_BASE)
					.from(INVOICE).join(INVOICE_DETAIL).on(INVOICE.ID.equal(INVOICE_DETAIL.INVOICE))
					.where(INVOICE_DETAIL.WAREHOUSE.equal(warehouseId))
					.and(INVOICE.TYPE.equal((byte)1))
					.and(INVOICE_DETAIL.SOURCE.notEqual((byte)3))
					.and(INVOICE.ISSUE_DATE.greaterOrEqual(initialDate))
					.and(INVOICE.ISSUE_DATE.lessOrEqual(finalDate))
					.fetch();

			for (Record3<Integer, Double, Double> record : data6) {
				if (record.value1() != null) {
					if(!map.containsKey(record.value1())) {
						if(record.value2() != 0){
							ConsumptionItem ci = getConsumptionItem(ctx, record.value1());
							ci.setSales(record.value2());
							ci.setSalesValue(record.value3());
							map.put(ci.getItemId(), ci);
						}
					}
					else{
						ConsumptionItem ci = map.get(record.value1());
						ci.setSales(ci.getSales()+record.value2());
						ci.setSalesValue(ci.getSalesValue()+ record.value3());
						map.replace(ci.getItemId(), ci);
					}
				}
			}
			
			// TRANSFERS salidas
			
			Result<Record2<Integer, Double>> data7 = ctx.getDslContext()
					.select(WAREHOUSE_TRANSFER_DETAIL.ITEM, WAREHOUSE_TRANSFER_DETAIL.QUANTITY)
					.from(WAREHOUSE_TRANSFER).join(WAREHOUSE_TRANSFER_DETAIL).on(WAREHOUSE_TRANSFER.ID.equal(WAREHOUSE_TRANSFER_DETAIL.WAREHOUSE_TRANSFER))
					.where(WAREHOUSE_TRANSFER.SOURCE_WAREHOUSE.equal(warehouseId))
					.and(WAREHOUSE_TRANSFER.ISSUE_TIME.greaterOrEqual(new Timestamp(initialDate.getTime())))
					.and(WAREHOUSE_TRANSFER.ISSUE_TIME.lessOrEqual(new Timestamp(finalDate.getTime())))
					.and(WAREHOUSE_TRANSFER.INVENTORY.isNull())
					.fetch();

			for (Record2<Integer, Double> record : data7) {
				if (record.value1() != null) {
					if(!map.containsKey(record.value1())) {
						if(record.value2() != 0){
							ConsumptionItem ci = getConsumptionItem(ctx, record.value1());
							ci.setTransfersMinus(record.value2());
							map.put(ci.getItemId(), ci);
						}
					}
					else{
						ConsumptionItem ci = map.get(record.value1());
						ci.setTransfersMinus(ci.getTransfersMinus()+record.value2());
						map.replace(ci.getItemId(), ci);
					}
				}
			}
			
			// TRANSFERS entradas
			
			Result<Record2<Integer, Double>> data8 = ctx.getDslContext()
					.select(WAREHOUSE_TRANSFER_DETAIL.ITEM, WAREHOUSE_TRANSFER_DETAIL.QUANTITY)
					.from(WAREHOUSE_TRANSFER).join(WAREHOUSE_TRANSFER_DETAIL).on(WAREHOUSE_TRANSFER.ID.equal(WAREHOUSE_TRANSFER_DETAIL.WAREHOUSE_TRANSFER))
					.where(WAREHOUSE_TRANSFER.TARGET_WAREHOUSE.equal(warehouseId))
					.and(WAREHOUSE_TRANSFER.ISSUE_TIME.greaterOrEqual(new Timestamp(initialDate.getTime())))
					.and(WAREHOUSE_TRANSFER.ISSUE_TIME.lessOrEqual(new Timestamp(finalDate.getTime())))
					.and(WAREHOUSE_TRANSFER.INVENTORY.isNull())
					.fetch();

			for (Record2<Integer, Double> record : data8) {
				if (record.value1() != null) {
					if(!map.containsKey(record.value1())) {
						if(record.value2() != 0){
							ConsumptionItem ci = getConsumptionItem(ctx ,record.value1());
							ci.setTransfersPlus(record.value2());
							map.put(ci.getItemId(), ci);
						}
					}
					else{
						ConsumptionItem ci = map.get(record.value1());
						ci.setTransfersPlus(ci.getTransfersPlus()+record.value2());
						map.replace(ci.getItemId(), ci);
					}
				}
			}
			
			return map;
		
		} finally{
			if (ctx != null) ctx.close();
		}
	}

	public static ConsumptionItem getConsumptionItem(AONContext ctx, Integer itemId){
		ConsumptionItem ci = new ConsumptionItem();
		ci.setItemId(itemId);
		
		Record6<String, String, Double, String, String, String> data = ctx.getDslContext().select(PRODUCT.CODE, PRODUCT.NAME, ITEM.PRICE, ITEM.DETAIL, ITEM.DETAIL2, ITEM.DETAIL3)	
			.from(PRODUCT).join(ITEM).on(PRODUCT.ID.equal(ITEM.PRODUCT))
			.where(ITEM.ID.equal(itemId))
			.fetchOne();
		
		ci.setProductCode(data.value1());
		ci.setProductName(data.value2());
		ci.setPrice(data.value3());
		ci.setDetail(data.value4());
		ci.setDetail2(data.value5());
		ci.setDetail3(data.value6());		

		ci.setInitialQuantity(0.0);
		ci.setPurchases(0.0);
		ci.setSales(0.0);
		ci.setTransfersPlus(0.0);
		ci.setTransfersMinus(0.0);
		ci.setFinalQuantity(0.0);
		
		ci.setInitialValue(0.0);
		ci.setPurchasesValue(0.0);
		ci.setSalesValue(0.0);
		ci.setTransfersMinusValue(0.0);
		ci.setTransfersPlusValue(0.0);
		ci.setFinalValue(0.0);
		
		return ci;
		
	}
	
	
	public static String getWarehouseName(String domain, Integer domainId, Integer warehouseId){
		
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain, domainId);
			
			Record1<String> data = ctx.getDslContext().select(WAREHOUSE.NAME)
				.from(WAREHOUSE)
				.where(WAREHOUSE.ID.eq(warehouseId))
				.fetchOne();
			
			return data.value1();
		} finally{
			if (ctx != null) ctx.close();
		}
	}
	
	public static String getInventoryName(String domain, Integer domainId, Integer inventoryId){
		if(inventoryId == null) return "";
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain, domainId);
			
			Record1<String> data = ctx.getDslContext().select(INVENTORY.DESCRIPTION)
				.from(INVENTORY)
				.where(INVENTORY.ID.eq(inventoryId))
				.fetchOne();
			
			return (data.value1()!= null)?data.value1():"";
		} finally{
			if (ctx != null) ctx.close();
		}
	}
	
	public static ConsumptionItem getTwoLastInventory(String domain, Integer domainId, Integer warehouseId) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain, domainId);
			
			Result<Record3<Integer, String, Date>> data = ctx.getDslContext().select(INVENTORY.ID,INVENTORY.DESCRIPTION, INVENTORY.INVENTORY_DATE)
				.from(INVENTORY)
				.where(INVENTORY.DOMAIN.eq(domainId))
				.and(INVENTORY.WAREHOUSE.eq(warehouseId))
				.orderBy(INVENTORY.INVENTORY_DATE.desc()).limit(2)
				.fetch();
			
			Integer cont = 0;
			ConsumptionItem ci = new ConsumptionItem();
			for (Record3<Integer, String, Date> i : data) {
				if(i.value1() != null && cont == 0) ci.setFinalId(i.value1());
				if(i.value2() != null && cont == 0) ci.setFinalDate(i.value3());
				if(i.value1() != null && cont == 1) ci.setInitialId(i.value1());
				if(i.value2() != null && cont == 1) ci.setInitialDate(i.value3());
				cont++;
			}
			
			return ci;
		} finally{
			if (ctx != null) ctx.close();
		}
	}
	
}
