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
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.apache.commons.lang.StringUtils;
import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.Record3;
import org.jooq.Record8;
import org.jooq.Result;

import com.esferalia.aon.gwt.template.shared.ConsumptionItem;
import com.esferalia.aon.jooq.tables.records.InventoryRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.warehouse.Inventory;

public class DBConsumption {
	
	public static Map<Integer, ConsumptionItem> getConsumption(Domain domain, String login
			, Integer initialId, Integer finalId, Integer warehouseId, Date initialDate, Date finalDate, String warehouseName){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login);
			
			Record1<String> result = ctx.getDslContext().select(WORKPLACE.DESCRIPTION)
			.from(WORKPLACE).join(WAREHOUSE).on(WAREHOUSE.WORKPLACE.eq(WORKPLACE.ID))
			.where(WAREHOUSE.ID.eq(warehouseId))
			.limit(1)
			.fetchOne();
			 
			String hotel = result.value1();
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
							ConsumptionItem ci = getConsumptionItem(ctx, record.value1(), warehouseName, hotel);
							ci.setInitialDate(initialDate);
							ci.setFinalDate(finalDate);
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
							ConsumptionItem ci = getConsumptionItem(ctx, record.value1(), warehouseName, hotel);
							ci.setInitialDate(initialDate);
							ci.setFinalDate(finalDate);
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
							ConsumptionItem ci = getConsumptionItem(ctx, record.value1(), warehouseName, hotel);
							ci.setInitialDate(initialDate);
							ci.setFinalDate(finalDate);
							ci.setValuePAlb(record.value2() * record.value3());
							ci.setPurchasesAlb(record.value2());
							//ci.setPurchasesValueAlb(record.value3());
							map.put(ci.getItemId(), ci);
						}
					}
					else{
						ConsumptionItem ci = map.get(record.value1());
						ci.setValuePAlb(ci.getValuePAlb() + (record.value2() * record.value3()));
						ci.setPurchasesAlb(ci.getPurchasesAlb()+record.value2());
						//ci.setPurchasesValueAlb(ci.getValuePAlb() / ci.getPurchasesAlb());
						//ci.setPurchasesValueAlb(ci.getPurchasesValueAlb()+record.value3());
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
							ConsumptionItem ci = getConsumptionItem(ctx, record.value1(), warehouseName, hotel);
							ci.setInitialDate(initialDate);
							ci.setFinalDate(finalDate);
							ci.setValuePFac(record.value2() * record.value3());
							ci.setPurchasesFac(record.value2());
							//ci.setPurchasesValueFac(record.value3());
							map.put(ci.getItemId(), ci);
						}
					}
					else{
						ConsumptionItem ci = map.get(record.value1());
						ci.setValuePFac(ci.getValuePFac() + (record.value2() * record.value3()));
						ci.setPurchasesFac(ci.getPurchasesFac()+record.value2());
						//ci.setPurchasesValueFac(ci.getValuePFac() / ci.getPurchasesFac());
						//ci.setPurchasesValueFac(ci.getPurchasesValueFac()+record.value3());
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
							ConsumptionItem ci = getConsumptionItem(ctx, record.value1(), warehouseName, hotel);
							ci.setInitialDate(initialDate);
							ci.setFinalDate(finalDate);
							ci.setValueSAlb(record.value2() * record.value3());
							ci.setSalesAlb(record.value2());
							//ci.setSalesValueAlb(record.value3());
							map.put(ci.getItemId(), ci);
						}
					}
					else{
						ConsumptionItem ci = map.get(record.value1());
						ci.setValueSAlb(ci.getValueSAlb() + (record.value2() * record.value3()));
						ci.setSalesAlb(ci.getSalesAlb()+record.value2());
						//ci.setSalesValueAlb(ci.getValueSAlb() / ci.getSalesAlb());
						//ci.setSalesValueAlb(ci.getSalesValueAlb()+ record.value3());
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
							ConsumptionItem ci = getConsumptionItem(ctx, record.value1(),  warehouseName, hotel);
							ci.setInitialDate(initialDate);
							ci.setFinalDate(finalDate);
							ci.setValueSFac(record.value2() * record.value3());
							ci.setSalesFac(record.value2());
							//ci.setSalesValueFac(record.value3());
							map.put(ci.getItemId(), ci);
						}
					}
					else{
						ConsumptionItem ci = map.get(record.value1());
						ci.setValueSFac(ci.getValueSFac() + (record.value2() * record.value3()));
						ci.setSalesFac(ci.getSalesFac()+record.value2());
						//ci.setSalesValueFac(ci.getValueSFac() / ci.getSalesFac());
						//ci.setSalesValueFac(ci.getSalesValueFac()+ record.value3());
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
							ConsumptionItem ci = getConsumptionItem(ctx, record.value1(), warehouseName, hotel);
							ci.setInitialDate(initialDate);
							ci.setFinalDate(finalDate);
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
							ConsumptionItem ci = getConsumptionItem(ctx ,record.value1(), warehouseName, hotel);
							ci.setInitialDate(initialDate);
							ci.setFinalDate(finalDate);
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

	public static ConsumptionItem getConsumptionItem(AONContext ctx, Integer itemId, String warehouseName, String hotel){
		ConsumptionItem ci = new ConsumptionItem();
		ci.setItemId(itemId);
		
		Record8<String, String, Double, String, String, String, Byte, String> data = ctx.getDslContext().select(PRODUCT.CODE, PRODUCT.NAME, ITEM.PRICE, ITEM.DETAIL, ITEM.DETAIL2, ITEM.DETAIL3
					, PRODUCT.SERIALIZABLE, ITEM.SERIAL_NUMBER)	
			.from(PRODUCT).join(ITEM).on(PRODUCT.ID.equal(ITEM.PRODUCT))
			.where(ITEM.ID.equal(itemId))
			.fetchOne();
		
		ci.setWarehouseName(warehouseName);
		ci.setHotel(hotel);
		String name = getFullName(data.getValue(PRODUCT.NAME), data.getValue(ITEM.DETAIL),
				data.getValue(ITEM.DETAIL2), data.getValue(ITEM.DETAIL3),
				data.getValue(PRODUCT.SERIALIZABLE).equals(1),data.getValue(ITEM.SERIAL_NUMBER));
		ci.setProductCode(data.getValue(PRODUCT.CODE));
		ci.setProductName(name);
		ci.setPrice(data.getValue(ITEM.PRICE));
		ci.setDetail(data.getValue(ITEM.DETAIL));
		ci.setDetail2(data.getValue(ITEM.DETAIL2));
		ci.setDetail3(data.getValue(ITEM.DETAIL3));		

		ci.setInitialQuantity(0.0);
		ci.setPurchasesAlb(0.0);
		ci.setPurchasesFac(0.0);
		ci.setSalesAlb(0.0);
		ci.setSalesFac(0.0);
		ci.setTransfersPlus(0.0);
		ci.setTransfersMinus(0.0);
		ci.setFinalQuantity(0.0);
		
		ci.setInitialValue(0.0);
		//ci.setPurchasesValueAlb(0.0);
		//ci.setPurchasesValueFac(0.0);
		//ci.setSalesValueAlb(0.0);
		//ci.setSalesValueFac(0.0);
		ci.setTransfersMinusValue(0.0);
		ci.setTransfersPlusValue(0.0);
		ci.setFinalValue(0.0);
		
		ci.setValuePAlb(0.0);
		ci.setValuePFac(0.0);
		ci.setValueSAlb(0.0);
		ci.setValueSFac(0.0);
		
		return ci;
		
	}
	
	
	public static String getWarehouseName(Domain domain, Integer warehouseId, String login){
		
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login);
			
			Record1<String> data = ctx.getDslContext().select(WAREHOUSE.NAME)
				.from(WAREHOUSE)
				.where(WAREHOUSE.ID.eq(warehouseId))
				.fetchOne();
			
			return data.value1();
		} finally{
			if (ctx != null) ctx.close();
		}
	}
	
	public static String getInventoryName(Domain domain, Integer inventoryId, String login){
		if(inventoryId == null) return "";
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login);
			
			Record1<String> data = ctx.getDslContext().select(INVENTORY.DESCRIPTION)
				.from(INVENTORY)
				.where(INVENTORY.ID.eq(inventoryId))
				.fetchOne();
			
			return (data.value1()!= null)?data.value1():"";
		} finally{
			if (ctx != null) ctx.close();
		}
	}
	
	public static ConsumptionItem getTwoLastInventory(Domain domain, Integer warehouseId, String login) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login);
			
			Result<Record3<Integer, String, Date>> data = ctx.getDslContext().select(INVENTORY.ID,INVENTORY.DESCRIPTION, INVENTORY.INVENTORY_DATE)
				.from(INVENTORY)
				.where(INVENTORY.DOMAIN.eq(domain.getId()))
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
	
	public static Inventory getInitialInventory(Domain domain, String login, Integer warehouseId, Date startDate, Date endDate){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login);
		
			return ctx.getDslContext().select().from(INVENTORY)
				.where(INVENTORY.DOMAIN.eq(domain.getId()))
				.and(INVENTORY.WAREHOUSE.eq(warehouseId))
				.and(INVENTORY.INVENTORY_DATE.greaterOrEqual(startDate))
				.and(INVENTORY.INVENTORY_DATE.lessThan(endDate))
				.orderBy(INVENTORY.INVENTORY_DATE.asc()).limit(1).fetchInto(INVENTORY)
				.stream().map(new InventoryFiller()).findFirst().orElse(new Inventory());
		} finally{
			if (ctx != null) ctx.close();
		}
	}
	
	private static class InventoryFiller implements Function<InventoryRecord, Inventory> {
		
		@Override
		public Inventory apply(InventoryRecord r) {
			return new Inventory().setId(r.getId())
					.setInventoryDate(r.getInventoryDate())
					.setDescription(r.getDescription());		
		}
	}
	
	public static Inventory getFinalInventory(Domain domain, String login, Integer warehouseId, Date startDate, Date endDate){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login);
		
			return ctx.getDslContext().select().from(INVENTORY)
				.where(INVENTORY.DOMAIN.eq(domain.getId()))
				.and(INVENTORY.WAREHOUSE.eq(warehouseId))
				.and(INVENTORY.INVENTORY_DATE.lessOrEqual(endDate))
				.and(INVENTORY.INVENTORY_DATE.greaterThan(startDate))
				.orderBy(INVENTORY.INVENTORY_DATE.desc()).limit(1).fetchInto(INVENTORY)
				.stream().map(new InventoryFiller()).findFirst().orElse(new Inventory());
		} finally{
			if (ctx != null) ctx.close();
		}
	}
	
	public static String getHotelName(String domainName, Integer domainId, String login, Integer warehouseId){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			
			Record1<String> result = ctx.getDslContext().select(WORKPLACE.DESCRIPTION)
			.from(WORKPLACE).join(WAREHOUSE).on(WAREHOUSE.WORKPLACE.eq(WORKPLACE.ID))
			.where(WAREHOUSE.ID.eq(warehouseId))
			.limit(1)
			.fetchOne();
			 
			return result.value1();
		} finally{
			if(ctx != null) ctx.close();
		}
	}
	
	private static String getFullName(String name, String detail, String detail2, String detail3,
			Boolean serializable, String serialNumber) {
		String details = getDetails(detail, detail2, detail3);
		StringBuffer sb = new StringBuffer();
		if (StringUtils.isNotEmpty(name)) {
			sb.append(name);
		}
		if (StringUtils.isNotEmpty(details)) {
			sb.append(" [" + details + "]");
		}
		if (serializable && StringUtils.isNotEmpty(serialNumber)) {
			sb.append(" #" + serialNumber);
		}
		return (sb.length() > 0) ? sb.toString() : "";
	}
	
	private static String getDetails(String detail, String detail2, String detail3) {
		StringBuffer sb = new StringBuffer();
		if (StringUtils.isNotEmpty(detail)) {
			sb.append(detail);
		}
		if (StringUtils.isNotEmpty(detail2)) {
			if (sb.length() > 0) {
				sb.append(" / ");
			}
			sb.append(detail2);
		}
		if (StringUtils.isNotEmpty(detail3)) {
			if (sb.length() > 0) {
				sb.append(" / ");
			}
			sb.append(detail3);
		}
		return (sb.length() > 0) ? sb.toString() : "";
	}
	
	
}
