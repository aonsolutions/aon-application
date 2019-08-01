package com.esferalia.aon.gwt.template.jooq;

import static com.esferalia.aon.jooq.tables.CatalogueItem.CATALOGUE_ITEM;
import static com.esferalia.aon.jooq.tables.IncomeDetail.INCOME_DETAIL;
import static com.esferalia.aon.jooq.tables.Inventory.INVENTORY;
import static com.esferalia.aon.jooq.tables.InventoryDetail.INVENTORY_DETAIL;
import static com.esferalia.aon.jooq.tables.Item.ITEM;
import static com.esferalia.aon.jooq.tables.Product.PRODUCT;
import static com.esferalia.aon.jooq.tables.Proposal.PROPOSAL;
import static com.esferalia.aon.jooq.tables.ProposalDetail.PROPOSAL_DETAIL;
import static com.esferalia.aon.jooq.tables.Ritem.RITEM;
import static com.esferalia.aon.jooq.tables.Series.SERIES;
import static com.esferalia.aon.jooq.tables.Stock.STOCK;
import static com.esferalia.aon.jooq.tables.UserScope.USER_SCOPE;
import static com.esferalia.aon.jooq.tables.Warehouse.WAREHOUSE;
import static com.esferalia.aon.jooq.tables.WarehouseTransfer.WAREHOUSE_TRANSFER;
import static com.esferalia.aon.jooq.tables.WarehouseTransferDetail.WAREHOUSE_TRANSFER_DETAIL;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;
import static com.esferalia.aon.jooq.tables.WorkplaceDepartment.WORKPLACE_DEPARTMENT;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.Vector;

import org.jooq.CaseConditionStep;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.DeleteConditionStep;
import org.jooq.InsertValuesStep10;
import org.jooq.InsertValuesStep12;
import org.jooq.InsertValuesStep13;
import org.jooq.InsertValuesStep4;
import org.jooq.InsertValuesStep5;
import org.jooq.Record1;
import org.jooq.Record16;
import org.jooq.Record2;
import org.jooq.Record3;
import org.jooq.Record5;
import org.jooq.Record6;
import org.jooq.Record7;
import org.jooq.Record8;
import org.jooq.Record9;
import org.jooq.Result;
import org.jooq.impl.DSL;

import com.esferalia.aon.gwt.template.server.AuditInfo;
import com.esferalia.aon.gwt.template.server.ProposalInfo;
import com.esferalia.aon.gwt.template.server.StockInfo;
import com.esferalia.aon.gwt.template.server.TransferInfo;
import com.esferalia.aon.gwt.template.server.Utils;
import com.esferalia.aon.gwt.template.shared.Error;
import com.esferalia.aon.jooq.tables.records.InventoryDetailRecord;
import com.esferalia.aon.jooq.tables.records.ProposalDetailRecord;
import com.esferalia.aon.jooq.tables.records.StockRecord;
import com.esferalia.aon.jooq.tables.records.WarehouseTransferDetailRecord;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.occam.api.model.product.ProductStatus;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.api.model.warehouse.Department;
import com.esferalia.aon.occam.api.model.warehouse.Series;
import com.esferalia.aon.occam.api.model.warehouse.Warehouse;

public class DBStock {
	
	public static DBStock getInstance() {
		return new DBStock();
	}

	private CaseConditionStep<Double> caseA;
	private CaseConditionStep<Double> caseB;
	private InsertValuesStep10<InventoryDetailRecord, Double, Double, Double, Integer, Integer, Integer, Timestamp, String, Timestamp, String> insert;
	
	public Error insertStock(String domain, Integer domainId,Vector<StockInfo> stock, TransferInfo ti, Integer inventoryId, AuditInfo ai, String login){
		Error error = new Error();
		error.setError(true);
		Vector<String> verror = new Vector<String>();
		verror.add("");
		error.setTextError(verror);
		AONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domain, domainId, login);
			 
			Vector<String> v = new Vector<String>();
			caseA =null;
			caseB =null;
			insert = ctx.getDslContext().insertInto(INVENTORY_DETAIL, INVENTORY_DETAIL.ACTUAL_QUANTITY, INVENTORY_DETAIL.COST, INVENTORY_DETAIL.REAL_QUANTITY, INVENTORY_DETAIL.DOMAIN, INVENTORY_DETAIL.INVENTORY, INVENTORY_DETAIL.ITEM
					, INVENTORY_DETAIL.CREATION_DATE, INVENTORY_DETAIL.CREATION_USER, INVENTORY_DETAIL.MODIFICATION_DATE, INVENTORY_DETAIL.MODIFICATION_USER);
			LinkedList<Integer> idList = new LinkedList<>(); 
			
			AONContext sctx = ctx;
			
			Record3<Integer,Integer, Date> data3 = ctx.getDslContext().select(WAREHOUSE.ID, WAREHOUSE.WORKPLACE, INVENTORY.INVENTORY_DATE)
					.from(INVENTORY).join(WAREHOUSE).on(WAREHOUSE.ID.eq(INVENTORY.WAREHOUSE))
					.where(INVENTORY.ID.eq(inventoryId))
					.limit(1).fetchOne();
			Integer warehouseId = data3.getValue(WAREHOUSE.ID);
			Integer workplaceId = data3.getValue(WAREHOUSE.WORKPLACE); 
			java.util.Date inventoryDate = data3.getValue(INVENTORY.INVENTORY_DATE);
			ApplicationParameter ap = AON.getApplicationParameter(domain, domainId, login, AppParam.AON_PRODUCT_VALUATION_METHOD);

			
			stock.stream().filter(f -> f.getProduct() != null).forEach(s ->{
				Condition serialNumber = ITEM.SERIAL_NUMBER.eq(s.getItem().getSerialNumber());
				if(s.getItem().getSerialNumber() == null)
					serialNumber = ITEM.SERIAL_NUMBER.isNull();
				Result<Record7<Integer, Double, Double, Byte, Byte, Integer, Byte>> data = sctx.getDslContext().select(ITEM.ID, ITEM.PRICE, ITEM.PURCHASE_PRICE
						, PRODUCT.MANUFACTURED, PRODUCT.INVENTORIABLE, PRODUCT.ID, PRODUCT.LOTABLE)
					.from(ITEM).join(PRODUCT).on(PRODUCT.ID.eq(ITEM.PRODUCT))
					.where(ITEM.BARCODE.eq(s.getProduct())).and(ITEM.DOMAIN.eq(domainId))
					.and(serialNumber).fetch();
				if(data.isEmpty()){
					data = sctx.getDslContext().select(ITEM.ID, ITEM.PRICE, ITEM.PURCHASE_PRICE, PRODUCT.MANUFACTURED, PRODUCT.INVENTORIABLE, PRODUCT.ID, PRODUCT.LOTABLE)
							.from(ITEM).join(PRODUCT).on(PRODUCT.ID.eq(ITEM.PRODUCT))
							.where(PRODUCT.CODE.eq(s.getProduct()))
									.and(serialNumber)
									.and(PRODUCT.DOMAIN.eq(domainId)).fetch();
				}
				if(data.size()>1){
					Condition detail = s.getItem().getDetail() == "" ? ITEM.DETAIL.eq("").or(ITEM.DETAIL.isNull()) : ITEM.DETAIL.eq(s.getItem().getDetail());
					Condition detail2 = s.getItem().getDetail2() == "" ? ITEM.DETAIL2.eq("").or(ITEM.DETAIL2.isNull()) : ITEM.DETAIL2.eq(s.getItem().getDetail2());
					Condition detail3 = s.getItem().getDetail3() == "" ? ITEM.DETAIL3.eq("").or(ITEM.DETAIL3.isNull()) : ITEM.DETAIL3.eq(s.getItem().getDetail3());
					Condition formatTag = s.getItem().getPackFormatTag().getId() != null ? ITEM.PACK_FORMAT_TAG.eq(s.getItem().getPackFormatTag().getId()) : ITEM.PACK_FORMAT_TAG.isNull();
					Condition units = ITEM.PACK_UNITS.eq(s.getItem().getPackUnits().intValue());						
					Condition unitsTag = s.getItem().getPackUnitsTag().getId() != null ? ITEM.PACK_UNITS_TAG.eq(s.getItem().getPackUnitsTag().getId()) : ITEM.PACK_UNITS_TAG.isNull();
					Condition measurement = ITEM.PACK_MEASUREMENT.eq(s.getItem().getPackMeasurement());
					Condition measurementTag = s.getItem().getPackMeasurementTag().getId() != null ? ITEM.PACK_MEASUREMENT_TAG.eq(s.getItem().getPackMeasurementTag().getId()) : ITEM.PACK_MEASUREMENT_TAG.isNull();
					
					data = sctx.getDslContext().select(ITEM.ID, ITEM.PRICE, ITEM.PURCHASE_PRICE, PRODUCT.MANUFACTURED, PRODUCT.INVENTORIABLE, PRODUCT.ID, PRODUCT.LOTABLE)
							.from(ITEM).join(PRODUCT).on(PRODUCT.ID.eq(ITEM.PRODUCT))
							.where(PRODUCT.CODE.eq(s.getProduct()))
								.and(detail).and(detail2).and(detail3).and(serialNumber)
								.and(formatTag).and(unitsTag).and(units).and(measurementTag).and(measurement)
								.and(PRODUCT.DOMAIN.eq(domainId)).fetch();
				}
				Integer index = 0;
				if(!data.isEmpty()){
					Byte lotable = data.get(index).getValue(PRODUCT.LOTABLE);
					if((s.getItem().getSerialNumber() != null &&  lotable != 1) && s.getQuantity() > 1 ){
						v.add("*Fila " +(s.getRow()+1) + " : La cantidad no puede ser mayor que 1.");
						error.setError(false);
						error.setTextError(v);
					} else {
						Integer itemId = data.get(index).value1();
						s.setDomainId(domainId);
						s.getItem().setId(itemId);
						
						Result<Record1<Integer>> data2 = sctx.getDslContext().select(INVENTORY_DETAIL.ID).from(INVENTORY_DETAIL)
							.where(INVENTORY_DETAIL.ITEM.eq(itemId))
							.and(INVENTORY_DETAIL.INVENTORY.eq(inventoryId)).fetch();
					
						Item item = new Item();
						item.setId(itemId);
						item.setPurchasePrice(data.get(index).value3() != null ? data.get(index).value3() :0.0);
						Product product = new Product().setId(data.get(index).getValue(PRODUCT.ID))
							.setManufactured(data.get(index).getValue(PRODUCT.MANUFACTURED))
							.setInventoriable(data.get(index).getValue(PRODUCT.INVENTORIABLE));
						item.setProduct(product);
						item.setProductId(product.getId());
						item.setDomain(domainId);

						Double cost = Utils.getValCost(sctx.getDomainName(), s.getQuantity(), item, login, workplaceId, warehouseId, inventoryDate, ap);
						if(data2.isNotEmpty()){
							Boolean first = true;
							for(Integer i = 0; i < data2.size(); i++) {
								idList.add(itemId);
								caseA = caseA != null ? caseA.when(INVENTORY_DETAIL.ID.eq(data2.get(i).getValue(INVENTORY_DETAIL.ID)), first ? s.getQuantity() : 0)
									: DSL.decode().when(INVENTORY_DETAIL.ID.eq(data2.get(i).getValue(INVENTORY_DETAIL.ID)), first ? s.getQuantity() : 0);
								caseB = caseB != null ? caseB.when(INVENTORY_DETAIL.ID.eq(data2.get(i).getValue(INVENTORY_DETAIL.ID)), cost)
									: DSL.decode().when(INVENTORY_DETAIL.ID.eq(data2.get(i).getValue(INVENTORY_DETAIL.ID)), cost);
								first = false;
							}
						}else{
							insert = insert.values(0.0, cost, s.getQuantity(), s.getDomainId(), inventoryId, itemId, new Timestamp(new java.util.Date().getTime()), "system", new Timestamp(new java.util.Date().getTime()), "system");
						}
					}
				}
				else{
					v.add("*Fila " +(s.getRow()+1) + " : El producto no existe o los detalles no coinciden.");
					error.setError(false);
					error.setTextError(v);
				}
			});
			
			if(error.getError()){
				if(caseA != null) {
					ctx.getDslContext().update(INVENTORY_DETAIL).set(INVENTORY_DETAIL.REAL_QUANTITY, caseA.otherwise(0.0))
					.where(INVENTORY_DETAIL.INVENTORY.eq(inventoryId)).and(INVENTORY_DETAIL.DOMAIN.eq(domainId))
					.and(INVENTORY_DETAIL.ITEM.in(idList.toArray(new Integer[idList.size()]))).execute();
				}
				if(caseB != null) {
					ctx.getDslContext().update(INVENTORY_DETAIL).set(INVENTORY_DETAIL.COST, caseB.otherwise(0.0))
					.where(INVENTORY_DETAIL.INVENTORY.eq(inventoryId)).and(INVENTORY_DETAIL.DOMAIN.eq(domainId))
					.and(INVENTORY_DETAIL.ITEM.in(idList.toArray(new Integer[idList.size()]))).execute();
				}
				ctx.getDslContext().update(INVENTORY_DETAIL)
				.set(INVENTORY_DETAIL.MODIFICATION_DATE, new Timestamp(new java.util.Date().getTime()))
				.set(INVENTORY_DETAIL.MODIFICATION_USER, "system")
				.where(INVENTORY_DETAIL.INVENTORY.eq(inventoryId)).and(INVENTORY_DETAIL.DOMAIN.eq(domainId))
				.and(INVENTORY_DETAIL.ITEM.in(idList.toArray(new Integer[idList.size()]))).execute();
				insert.execute();
			}
			ctx.getDslContext()
				.update(ITEM)
				.set(ITEM.STATUS, (byte) 0)
				.where(ITEM.DOMAIN.eq(domainId)
					.and(ITEM.ID.in(ctx.getDslContext()
						.select(STOCK.ITEM)
						.from(STOCK)
						.where(STOCK.DOMAIN.eq(domainId).and(STOCK.QUANTITY.gt(0.0))))));
			return error;
		}finally {
			if (ctx != null) ctx.close();
		}
	}
	
	private Double[] getSupplier(AONContext ctx, Integer workplace, Integer domainId, Integer itemId){
		Record2<Integer,Double> a = ctx.getDslContext().select(RITEM.REGISTRY, RITEM.PRICE)
		.from(RITEM)
		.where(RITEM.WORKPLACE.eq(workplace))
		.and(RITEM.DOMAIN.eq(domainId)).and(RITEM.TYPE.eq((byte)2))
		.and(RITEM.ITEM.eq(itemId))
		.orderBy(RITEM.PRIORITY).limit(1)
		.fetchOne();
		
		if(a == null){
			Record2<Integer,Double> b = ctx.getDslContext().select(RITEM.REGISTRY, RITEM.PRICE)
					.from(RITEM)
					.where(RITEM.DOMAIN.eq(domainId)).and(RITEM.WORKPLACE.isNull()).and(RITEM.TYPE.eq((byte)2))
					.and(RITEM.ITEM.eq(itemId))
					.orderBy(RITEM.PRIORITY).limit(1)
					.fetchOne();
			if(b == null)
				return new Double[]{-1.0,-1.0};
			return new Double[]{b.value1().doubleValue(), b.value2()};
		}
		return new Double[]{a.value1().doubleValue(), a.value2()};	
	}
	
	public Error insertProposal(Domain domain,Vector<StockInfo> stock,Integer proposal, AuditInfo ai, Integer workplace, String login){
		Error error = new Error();
		error.setError(true);
		Vector<String> verror = new Vector<String>();
		verror.add("");
		error.setTextError(verror);
		Vector<String> v = new Vector<String>();
		AONContext ctx = null;
		try{
			System.out.println("GWT TEMPLATES - (Solicitud de compra) - dentro de la funcion de insertar!!");

			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login);
			InsertValuesStep13<ProposalDetailRecord, Integer, Integer, Integer, String, Double, Double, String, Byte, Integer, String, Timestamp, String, Timestamp> proposalInsertQuery = ctx.getDslContext().insertInto(PROPOSAL_DETAIL, PROPOSAL_DETAIL.DOMAIN, PROPOSAL_DETAIL.PROPOSAL, PROPOSAL_DETAIL.ITEM, PROPOSAL_DETAIL.DESCRIPTION,  PROPOSAL_DETAIL.QUANTITY, PROPOSAL_DETAIL.PRICE, PROPOSAL_DETAIL.DISCOUNT_EXPR, PROPOSAL_DETAIL.STATUS, PROPOSAL_DETAIL.SUPPLIER, PROPOSAL_DETAIL.CREATION_USER, PROPOSAL_DETAIL.CREATION_DATE, PROPOSAL_DETAIL.MODIFICATION_USER, PROPOSAL_DETAIL.MODIFICATION_DATE);
			 InsertValuesStep12<ProposalDetailRecord, Integer, Integer, Integer, Integer, String, Double, Double, String, Byte, Integer, String, Timestamp> proposalUpdateQuery = ctx.getDslContext().insertInto(PROPOSAL_DETAIL, PROPOSAL_DETAIL.ID, PROPOSAL_DETAIL.DOMAIN, PROPOSAL_DETAIL.PROPOSAL, PROPOSAL_DETAIL.ITEM, PROPOSAL_DETAIL.DESCRIPTION,  PROPOSAL_DETAIL.QUANTITY, PROPOSAL_DETAIL.PRICE, PROPOSAL_DETAIL.DISCOUNT_EXPR, PROPOSAL_DETAIL.STATUS, PROPOSAL_DETAIL.SUPPLIER, PROPOSAL_DETAIL.MODIFICATION_USER, PROPOSAL_DETAIL.MODIFICATION_DATE);
			
			AONContext sctx = ctx;
			Vector<Integer> updateIds = new Vector<Integer>();
			
			stock.stream().forEach(s ->{
				String code = s.getProduct();
				
				if(code != null){
					Condition serialNumber = ITEM.SERIAL_NUMBER.eq(s.getItem().getSerialNumber());
					if(s.getItem().getSerialNumber() == null)
						serialNumber = ITEM.SERIAL_NUMBER.isNull();
					Result<Record3<Integer, Double, Integer>> data = sctx.getDslContext().select(ITEM.ID, ITEM.PRICE, ITEM.PRODUCT)
						.from(ITEM)
						.where(ITEM.BARCODE.eq(s.getProduct())).and(ITEM.DOMAIN.eq(domain.getId()))
						.and(serialNumber).fetch();
					if(data.isEmpty()){

						data = sctx.getDslContext().select(ITEM.ID, ITEM.PRICE, ITEM.PRODUCT)
								.from(ITEM).join(PRODUCT).on(PRODUCT.ID.eq(ITEM.PRODUCT))
								.where(PRODUCT.CODE.eq(s.getProduct()))
										.and(PRODUCT.DOMAIN.eq(domain.getId()))
										.and(serialNumber).fetch();
					}
					if(data.size()>1){
						Condition detail = ITEM.DETAIL.eq(s.getItem().getDetail());
						if(s.getItem().getDetail() == "") 
							detail = ITEM.DETAIL.eq("").or(ITEM.DETAIL.isNull());
						
						Condition detail2 = ITEM.DETAIL2.eq(s.getItem().getDetail2());
						if(s.getItem().getDetail2() == "") 
							detail2 = ITEM.DETAIL2.eq("").or(ITEM.DETAIL2.isNull());
						
						Condition detail3 = ITEM.DETAIL3.eq(s.getItem().getDetail3());
						if(s.getItem().getDetail3() == "") 
							detail3 = ITEM.DETAIL3.eq("").or(ITEM.DETAIL3.isNull());
						
						Condition formatTag = ITEM.PACK_FORMAT_TAG.isNull();
						if(s.getItem().getPackFormatTag().getId() != null)
							formatTag = ITEM.PACK_FORMAT_TAG.eq(s.getItem().getPackFormatTag().getId());
																
						Condition units = ITEM.PACK_UNITS.eq(s.getItem().getPackUnits().intValue());
												
						Condition unitsTag = ITEM.PACK_UNITS_TAG.isNull();
						if(s.getItem().getPackUnitsTag().getId() != null)
							unitsTag = ITEM.PACK_UNITS_TAG.eq(s.getItem().getPackUnitsTag().getId());
																
						Condition measurement = ITEM.PACK_MEASUREMENT.eq(s.getItem().getPackMeasurement());
																
						Condition measurementTag = ITEM.PACK_MEASUREMENT_TAG.isNull();
						if(s.getItem().getPackMeasurementTag().getId() != null)
							measurementTag = ITEM.PACK_MEASUREMENT_TAG.eq(s.getItem().getPackMeasurementTag().getId());
						
						data = sctx.getDslContext().select(ITEM.ID, ITEM.PRICE, ITEM.PRODUCT)
								.from(ITEM).join(PRODUCT).on(PRODUCT.ID.eq(ITEM.PRODUCT))
								.where(PRODUCT.CODE.eq(s.getProduct()))
									.and(detail).and(detail2).and(detail3).and(serialNumber)
									.and(formatTag).and(unitsTag).and(units).and(measurementTag).and(measurement)
									.and(PRODUCT.DOMAIN.eq(domain.getId())).fetch();
					}
				/*	Record1<Integer> data = sctx.getDslContext().select(PRODUCT.ID)
						.from(PRODUCT)
						.where(PRODUCT.CODE.eq(code).and(PRODUCT.DOMAIN.eq(domainId))).fetchOne();
			
					Record1<Integer> record = sctx.getDslContext().select(ITEM.ID)
						.from(ITEM)
						.where(ITEM.PRODUCT.eq(data.value1()))
						.fetchOne();
				*/
					// TODO update
					if(!data.isEmpty()){
						System.out.println("GWT TEMPLATES - (Solicitud de compra) - entre media de la f insertar");

						Integer productId = data.get(0).getValue(ITEM.PRODUCT);
						Byte lotable = sctx.getDslContext().select(PRODUCT.LOTABLE).from(PRODUCT)
								.where(PRODUCT.ID.eq(productId)).limit(1).fetch().get(0).getValue(PRODUCT.LOTABLE); 
						if((s.getItem().getSerialNumber() != null && lotable != 1) && s.getQuantity() > 1 ){
							v.add("*Fila " +(s.getRow()+1) + " : La cantidad no puede ser mayor que 1.");
							error.setError(false);
							error.setTextError(v);
						} else {
							ProposalInfo pi = new ProposalInfo();
							pi.setQuantity(s.getQuantity());
							pi.setDomain(domain.getId());
							pi.setProposal(proposal);
							pi.setItem(data.get(0).value1());
							pi.setStatus((byte) 0); 
							pi.setDescription("");
							pi.setDiscount((double) 0);
							Double[] supplier = getSupplier(sctx, workplace, domain.getId(),pi.getItem());
				
							if (supplier[0] != -1){
								pi.setPrice(supplier[1]);
								if(isCatalogue(sctx,pi)){
									Timestamp t = new Timestamp(ai.getDate().getTime());
									if(isProposal(sctx,pi)){
										pi = getProposal(sctx,pi);
										updateIds.add(pi.getId());
										proposalUpdateQuery.values(pi.getId(), pi.getDomain(), pi.getProposal(), pi.getItem(), pi.getDescription(), pi.getQuantity(), pi.getPrice(), pi.getDiscount().toString(), pi.getStatus(), supplier[0].intValue(), ai.getUsername(), t);
									}
									else
										proposalInsertQuery.values(pi.getDomain(), pi.getProposal(), pi.getItem(), pi.getDescription(), pi.getQuantity(), pi.getPrice(), pi.getDiscount().toString(), pi.getStatus(), supplier[0].intValue(), ai.getUsername(), t, ai.getUsername(), t);
								}
								else{
									v.add("*Fila " +(s.getRow()+1) + " : El producto no existe o los detalles no coincide.");
									System.out.println("GWT TEMPLATES - (Solicitud de compra) - "+ "*Fila " +(s.getRow()+1) + " : El producto no existe o los detalles no coincide.");

									error.setError(false);
									error.setTextError(v);
								}
							}
							else{
								v.add("*Fila " +(s.getRow()+1) + " : El producto no dispone de un proveedor asignable.");
								System.out.println("GWT TEMPLATES - (Solicitud de compra) - "+ "*Fila " +(s.getRow()+1) + " : El producto no dispone de un proveedor asignable.");

								error.setError(false);
								error.setTextError(v);
							}
						}
					}else{
						v.add("*Fila " +(s.getRow()+1) + " : El producto no existe o los detalles no coincide.");
						System.out.println("GWT TEMPLATES - (Solicitud de compra) - "+ "*Fila " +(s.getRow()+1) + " : El producto no existe o los detalles no coincide.");

						error.setError(false);
						error.setTextError(v);
					}
				}
			});
			if(error.getError()){
				System.out.println("GWT TEMPLATES - (Solicitud de compra) - EERRRROORRR");
				if(updateIds.size()>0){
					ctx.getDslContext().delete(PROPOSAL_DETAIL).where(PROPOSAL_DETAIL.ID.in(updateIds)).execute();
					proposalUpdateQuery.execute();
				}
				proposalInsertQuery.execute();
	
			}
			return error;
		} finally{
			if (ctx != null) ctx.close();	
		}
		
	}
	
	private Boolean isCatalogue(AONContext ctx, ProposalInfo pi){
		Result<Record1<Integer>> data = ctx.getDslContext().select(WORKPLACE_DEPARTMENT.CATALOGUE)
					.from(PROPOSAL).join(WORKPLACE_DEPARTMENT).on(PROPOSAL.WORKPLACE.eq(WORKPLACE_DEPARTMENT.WORKPLACE).and(PROPOSAL.DEPARTMENT.eq(WORKPLACE_DEPARTMENT.DEPARTMENT)))
					.where(PROPOSAL.ID.eq(pi.getProposal()))
					.fetch();
		
		for (Record1<Integer> record : data) {
			record.value1();
			Result<Record1<Integer>> data2 = ctx.getDslContext().select(CATALOGUE_ITEM.ID)
							.from(CATALOGUE_ITEM)
							.where(CATALOGUE_ITEM.CATALOGUE.eq(record.value1()))
							.and(CATALOGUE_ITEM.ITEM.eq(pi.getItem()))
							.fetch();
			if(data2.isNotEmpty())
				return true;
		}
		
		
		return false;
	}
	
	private Boolean isProposal(AONContext ctx, ProposalInfo pi){
		
		Result<Record1<Integer>> data = ctx.getDslContext().select(PROPOSAL_DETAIL.ID)
					.from(PROPOSAL_DETAIL)
					.where(PROPOSAL_DETAIL.PROPOSAL.eq(pi.getProposal()))
					.and(PROPOSAL_DETAIL.ITEM.eq(pi.getItem()))
					.fetch();
		
		return data.isNotEmpty();
	}
	
	private ProposalInfo getProposal(AONContext ctx, ProposalInfo pi){
		
		Result<Record2<Integer, Double>> data = ctx.getDslContext().select(PROPOSAL_DETAIL.ID, PROPOSAL_DETAIL.QUANTITY)
					.from(PROPOSAL_DETAIL)
					.where(PROPOSAL_DETAIL.PROPOSAL.eq(pi.getProposal()))
					.and(PROPOSAL_DETAIL.ITEM.eq(pi.getItem()))
					.fetch();
		
		pi.setId(data.get(0).value1());
		pi.setQuantity(pi.getQuantity()+data.get(0).value2());
		return pi;
	}
	
	
	
	public Error insertTransferStock(Domain domain,Vector<StockInfo> stock, TransferInfo ti,AuditInfo ai, String login){
		Error error = new Error();
		error.setError(true);
		Vector<String> verror = new Vector<String>();
		verror.add("");
		error.setTextError(verror);
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login);

			DeleteConditionStep<StockRecord> stockDeleteQuery;
			DeleteConditionStep<WarehouseTransferDetailRecord> transferDeleteQuery;
			InsertValuesStep4<StockRecord, Integer, Integer, Double, Integer> stockInsertQuery = ctx.getDslContext().insertInto(STOCK, STOCK.DOMAIN, STOCK.ITEM, STOCK.QUANTITY, STOCK.WAREHOUSE);
			InsertValuesStep5<StockRecord, Integer, Integer, Integer, Double, Integer> stockUpdateQuery = ctx.getDslContext().insertInto(STOCK, STOCK.ID, STOCK.DOMAIN, STOCK.ITEM, STOCK.QUANTITY, STOCK.WAREHOUSE);
			InsertValuesStep4<WarehouseTransferDetailRecord, Integer, Integer, Integer, Double> transferInsert = ctx.getDslContext().insertInto(WAREHOUSE_TRANSFER_DETAIL, WAREHOUSE_TRANSFER_DETAIL.DOMAIN, WAREHOUSE_TRANSFER_DETAIL.ITEM, WAREHOUSE_TRANSFER_DETAIL.WAREHOUSE_TRANSFER, WAREHOUSE_TRANSFER_DETAIL.QUANTITY);
			InsertValuesStep5<WarehouseTransferDetailRecord, Integer, Integer, Integer, Integer, Double> transferUpdate = ctx.getDslContext().insertInto(WAREHOUSE_TRANSFER_DETAIL,WAREHOUSE_TRANSFER_DETAIL.ID, WAREHOUSE_TRANSFER_DETAIL.DOMAIN, WAREHOUSE_TRANSFER_DETAIL.ITEM, WAREHOUSE_TRANSFER_DETAIL.WAREHOUSE_TRANSFER, WAREHOUSE_TRANSFER_DETAIL.QUANTITY);
			
			Vector<Integer> stockDeleteIds = new Vector<Integer>();
			Vector<Integer> transferDeleteIds = new Vector<Integer>();
			LinkedList<StockAux> updateStockList = new LinkedList<StockAux>();
			Integer transferId;
			if(ti.getSeries().getCode() != null){
				transferId = ctx.getDslContext().select(WAREHOUSE_TRANSFER.ID)
										.from(WAREHOUSE_TRANSFER)
										.where(WAREHOUSE_TRANSFER.DOMAIN.eq(domain.getId()))
										.and(WAREHOUSE_TRANSFER.SERIES.eq(ti.getSeries().getCode()))
										.and(WAREHOUSE_TRANSFER.NUMBER.eq(ti.getNumber()))
										.fetchOne().value1();
			} else{
				transferId = ctx.getDslContext().select(WAREHOUSE_TRANSFER.ID)
						.from(WAREHOUSE_TRANSFER)
						.where(WAREHOUSE_TRANSFER.DOMAIN.eq(domain.getId()))
						.and(WAREHOUSE_TRANSFER.SERIES.isNull())
						.and(WAREHOUSE_TRANSFER.NUMBER.eq(ti.getNumber()))
						.fetchOne().value1();
			}
			Vector<String> v = new Vector<String>();
			AONContext sctx = ctx;
			stock.stream().forEach(s ->{
				if(s.getProduct() != null){
					Condition serialNumber = ITEM.SERIAL_NUMBER.eq(s.getItem().getSerialNumber());
					if(s.getItem().getSerialNumber() == null)
						serialNumber = ITEM.SERIAL_NUMBER.isNull();
					
					Result<Record2< Integer, Integer>> data = sctx.getDslContext().select(ITEM.ID, ITEM.PRODUCT)
						.from(ITEM)
						.where(ITEM.BARCODE.eq(s.getProduct())).and(ITEM.DOMAIN.eq(domain.getId()))
						.and(serialNumber).fetch();
					if(data.isEmpty()){

						data = sctx.getDslContext().select(ITEM.ID, ITEM.PRODUCT)
								.from(ITEM).join(PRODUCT).on(PRODUCT.ID.eq(ITEM.PRODUCT))
								.where(PRODUCT.CODE.eq(s.getProduct()))
										.and(PRODUCT.DOMAIN.eq(domain.getId()))
										.and(serialNumber).fetch();
					}
					if(data.size()>1){
						Condition detail = ITEM.DETAIL.eq(s.getItem().getDetail());
						if(s.getItem().getDetail() == "") 
							detail = ITEM.DETAIL.eq("").or(ITEM.DETAIL.isNull());
						
						Condition detail2 = ITEM.DETAIL2.eq(s.getItem().getDetail2());
						if(s.getItem().getDetail2() == "") 
							detail2 = ITEM.DETAIL2.eq("").or(ITEM.DETAIL2.isNull());
						
						Condition detail3 = ITEM.DETAIL3.eq(s.getItem().getDetail3());
						if(s.getItem().getDetail3() == "") 
							detail3 = ITEM.DETAIL3.eq("").or(ITEM.DETAIL3.isNull());

						Condition formatTag = ITEM.PACK_FORMAT_TAG.isNull();
						if(s.getItem().getPackFormatTag().getId() != null)
							formatTag = ITEM.PACK_FORMAT_TAG.eq(s.getItem().getPackFormatTag().getId());
																
						Condition units = ITEM.PACK_UNITS.eq(s.getItem().getPackUnits().intValue());
												
						Condition unitsTag = ITEM.PACK_UNITS_TAG.isNull();
						if(s.getItem().getPackUnitsTag().getId() != null)
							unitsTag = ITEM.PACK_UNITS_TAG.eq(s.getItem().getPackUnitsTag().getId());
																
						Condition measurement = ITEM.PACK_MEASUREMENT.eq(s.getItem().getPackMeasurement());
																
						Condition measurementTag = ITEM.PACK_MEASUREMENT_TAG.isNull();
						if(s.getItem().getPackMeasurementTag().getId() != null)
							measurementTag = ITEM.PACK_MEASUREMENT_TAG.eq(s.getItem().getPackMeasurementTag().getId());
						
						data = sctx.getDslContext().select(ITEM.ID, ITEM.PRODUCT)
								.from(ITEM).join(PRODUCT).on(PRODUCT.ID.eq(ITEM.PRODUCT))
								.where(PRODUCT.CODE.eq(s.getProduct()))
									.and(detail).and(detail2).and(detail3).and(serialNumber)
									.and(formatTag).and(unitsTag).and(units).and(measurementTag).and(measurement)
									.and(PRODUCT.DOMAIN.eq(domain.getId())).fetch();
					}
					if(!data.isEmpty()){
						Integer productId = data.get(0).getValue(ITEM.PRODUCT);
						Byte lotable = sctx.getDslContext().select(PRODUCT.LOTABLE).from(PRODUCT)
								.where(PRODUCT.ID.eq(productId)).limit(1).fetch().get(0).getValue(PRODUCT.LOTABLE); 
						if((s.getItem().getSerialNumber() != null && lotable != 1) && s.getQuantity() > 1 ){
							v.add("*Fila " +(s.getRow()+1) + " : La cantidad no puede ser mayor que 1.");
							error.setError(false);
							error.setTextError(v);
						} else{
							Integer itemId = data.get(0).value1();
							Result<Record2<Double, Integer>> data2 = null;
							if(ti.getTargetWarehouse() != null && ti.getTargetWarehouse().getId() != null)
								data2 = sctx.getDslContext().select(STOCK.QUANTITY, STOCK.ID)
									.from(STOCK)
									.where(STOCK.ITEM.eq(itemId).and(STOCK.WAREHOUSE.eq(ti.getTargetWarehouse().getId()))).fetch();
							
							Result<Record2<Double, Integer>> data3 = null; 
							if(ti.getSourceWarehouse() != null && ti.getSourceWarehouse().getId() != null)
							data3= sctx.getDslContext().select(STOCK.QUANTITY, STOCK.ID)
								.from(STOCK)
								.where(STOCK.ITEM.eq(itemId).and(STOCK.WAREHOUSE.eq(ti.getSourceWarehouse().getId()))).fetch();
						
							Record2<Integer,Double> data4 = sctx.getDslContext().select(WAREHOUSE_TRANSFER_DETAIL.ID, WAREHOUSE_TRANSFER_DETAIL.QUANTITY)
											.from(WAREHOUSE_TRANSFER_DETAIL)
											.where(WAREHOUSE_TRANSFER_DETAIL.WAREHOUSE_TRANSFER.eq(transferId))
											.and(WAREHOUSE_TRANSFER_DETAIL.ITEM.eq(itemId))
											.fetchOne();
							if(data4 != null){
								transferDeleteIds.add(data4.value1());
								transferUpdate.values(data4.value1(),domain.getId(), itemId, transferId,s.getQuantity()+data4.value2());
							}
							else {
								transferInsert.values(domain.getId(), itemId, transferId,s.getQuantity());
							}
							Double quantity, quantity2;
							Integer stockId, stockId2;
							if((ti.getSourceWarehouse() != null && ti.getSourceWarehouse().getId() != null) 
								&& (ti.getTargetWarehouse() != null && ti.getTargetWarehouse().getId() != null)){
							//	LOGGER.debug("Source warehouse is not null & target warehouse is not null");
								if(data2.isNotEmpty() && data3.isNotEmpty()){
									quantity = data2.get(0).value1();
									quantity2 = data3.get(0).value1();
									stockId = data2.get(0).value2();
									stockId2 = data3.get(0).value2();
								
									//UPDATE source & target
									if((s.getItem().getSerialNumber() != null && lotable != 1) && (quantity+s.getQuantity() > 1 || quantity2-s.getQuantity() < -1)){
										v.add("*Fila " +(s.getRow()+1) + " : La cantidad no puede ser mayor que 1.");
										error.setError(false);
										error.setTextError(v);
									} else if(error.getError()){
										updateStockList.add(new StockAux().setStockId(stockId).setItemId(itemId).setQuantity((quantity+s.getQuantity()))
												.setWarehouseId(ti.getTargetWarehouse().getId()));
										updateStockList.add(new StockAux().setStockId(stockId2).setItemId(itemId).setQuantity((quantity2-s.getQuantity()))
												.setWarehouseId(ti.getSourceWarehouse().getId()));
									}
								}
								else if(data2.isEmpty() && data3.isNotEmpty()){
									quantity2 = data3.get(0).value1();
									stockId2 = data3.get(0).value2(); 
								
									if((s.getItem().getSerialNumber() != null && lotable != 1) && quantity2+s.getQuantity() < -1 ){
										v.add("*Fila " +(s.getRow()+1) + " : La cantidad no puede ser mayor que 1.");
										error.setError(false);
										error.setTextError(v);
									} else if(error.getError()){
										//UPDATE source
										updateStockList.add(new StockAux().setStockId(stockId2).setItemId(itemId).setQuantity((quantity2-s.getQuantity()))
												.setWarehouseId(ti.getSourceWarehouse().getId()));
										//INSERT target
										stockInsertQuery.values(domain.getId(), itemId, s.getQuantity(), ti.getTargetWarehouse().getId());
									}
								}
							}
							else if((ti.getSourceWarehouse() != null && ti.getSourceWarehouse().getId() != null) 
								&& (ti.getTargetWarehouse() == null || ti.getTargetWarehouse().getId() == null)){
								//LOGGER.debug("Source warehouse is not null & target warehouse is null");
								if(data3.isNotEmpty()){
									quantity2 = data3.get(0).value1();
									stockId2 = data3.get(0).value2();
								
									//UPDATE source
									if((s.getItem().getSerialNumber() != null && lotable != 1) && quantity2+s.getQuantity() < -1 ){
										v.add("*Fila " +(s.getRow()+1) + " : La cantidad no puede ser mayor que 1.");
										error.setError(false);
										error.setTextError(v);
									} else if(error.getError()) 
										updateStockList.add(new StockAux().setStockId(stockId2).setItemId(itemId).setQuantity((quantity2-s.getQuantity()))
												.setWarehouseId(ti.getSourceWarehouse().getId()));
								}

							}
							else if((ti.getSourceWarehouse() == null || ti.getSourceWarehouse().getId() == null) 
								&& (ti.getTargetWarehouse() != null && ti.getTargetWarehouse().getId() != null)){
								//LOGGER.debug("Source warehouse is null & target warehouse is not null");
								if(data2.isNotEmpty()){
									quantity = data2.get(0).value1();
									stockId = data2.get(0).value2();
								
									//UPDATE target
									if((s.getItem().getSerialNumber() != null && lotable != 1) && quantity+s.getQuantity() > 1 ){
										v.add("*Fila " +(s.getRow()+1) + " : La cantidad no puede ser mayor que 1.");
										error.setError(false);
										error.setTextError(v);
									} else if(error.getError())
										updateStockList.add(new StockAux().setStockId(stockId).setItemId(itemId).setQuantity((quantity+s.getQuantity()))
												.setWarehouseId(ti.getTargetWarehouse().getId()));
								}
								else{
									//INSERT target
									stockInsertQuery.values(domain.getId(), itemId, s.getQuantity(), ti.getTargetWarehouse().getId());
								}
							} else {
								v.add("Error en la selección de almacenes.");
								error.setError(false);
								error.setTextError(v);
							}
						}
					}
					else{
						v.add("*Fila " +(s.getRow()+1) + " : El producto no existe o los detalles no coincide.");
						//LOGGER.error("Error: *Fila " +(s.getRow()+1) + " : El producto no existe o los detalles no coincide.");
						error.setError(false);
						error.setTextError(v);
					}
				}
			});
	
			if(error.getError()){
				for (StockAux sa : updateStockList) {
					updateStock(ctx, sa.stockId, sa.getItemId(), sa.getQuantity(), sa.getWarehouseId());
				}
				stockInsertQuery.execute();
				
				if(transferDeleteIds.size() > 0){
					transferDeleteQuery = ctx.getDslContext().delete(WAREHOUSE_TRANSFER_DETAIL).where(WAREHOUSE_TRANSFER_DETAIL.ID.in(transferDeleteIds));
					transferDeleteQuery.execute();
					transferUpdate.execute();
				}
				transferInsert.execute();
			}
			return error;
			
		}finally {
			if (ctx != null) ctx.close();	
		}
	}
	
	public void updateStock(AONContext ctx, Integer stockId, Integer itemId, Double quantity, Integer warehouseId){
		ctx.getDslContext().update(STOCK)
			.set(STOCK.ITEM, itemId)
			.set(STOCK.QUANTITY, quantity)
			.set(STOCK.WAREHOUSE, warehouseId)
			.where(STOCK.ID.eq(stockId)).execute();
	}

	public Vector<StockInfo> getStocks(Domain domain,Integer wid, Condition c, boolean onlyNonCero, String login) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login);
			
			Result<Record6<Integer, Integer, Integer, Double,Integer, String>> data ;
			if(wid != null)
				data = ctx.getDslContext().select(STOCK.ID, STOCK.ITEM, STOCK.WAREHOUSE, STOCK.QUANTITY,ITEM.PRODUCT, ITEM.SERIAL_NUMBER)
				.from(STOCK).join(ITEM).on(ITEM.ID.eq(STOCK.ITEM)).join(PRODUCT).on(ITEM.PRODUCT.eq(PRODUCT.ID))
				.where(STOCK.DOMAIN.eq(domain.getId())).and(STOCK.WAREHOUSE.eq(wid))
				.and(c)
				.orderBy(PRODUCT.NAME)
				.fetch();
			else
				data = ctx.getDslContext().select(STOCK.ID, STOCK.ITEM, STOCK.WAREHOUSE, STOCK.QUANTITY,ITEM.PRODUCT, ITEM.SERIAL_NUMBER)
				.from(STOCK).join(ITEM).on(ITEM.ID.eq(STOCK.ITEM)).join(PRODUCT).on(ITEM.PRODUCT.eq(PRODUCT.ID))
				.where(STOCK.DOMAIN.eq(domain.getId()))
				.and(c)
				.orderBy(PRODUCT.NAME)
				.fetch();
				
			Vector<StockInfo> v = new Vector<StockInfo>();
			for (Record6<Integer, Integer, Integer, Double, Integer, String> d : data) {
				Item i = getItem(domain, login, d.value2());
				if ( (d.value4() == 0) && ((i.getStatus() == ProductStatus.DISCONTINUED.value()) || (!i.getProduct().isInventoriable()) ) ) {
					ctx.getDslContext().delete(STOCK).where(STOCK.ID.equal(d.value1()));
				} else if ( !onlyNonCero || (d.value4() != 0) ) {
					StockInfo si = new StockInfo();
					si.setItem(i.setSerialNumber(d.getValue(ITEM.SERIAL_NUMBER)));
					if(i.getBarcode()!= null) si.setProduct(i.getBarcode());
					else {
						si.setProduct(i.getProduct().getCode());
					}
					
					si.setQuantity(d.value4());
					si.setProductId(d.value5());
					si.setProductName(i.getProduct().getName());
					v.add(si);					
				}
			}
			return v;
		} finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public Vector<StockInfo> getInventoryClosed(Domain domain, Integer inventoryId, Condition c, String login) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login);
			
			Result<Record9<Double, String, String, String, String, String, String, Integer, Integer>> data = 
				ctx.getDslContext().select(INVENTORY_DETAIL.REAL_QUANTITY,ITEM.DETAIL,
						ITEM.DETAIL2, ITEM.DETAIL3, PRODUCT.CODE, PRODUCT.NAME, ITEM.SERIAL_NUMBER, ITEM.ID, PRODUCT.ID)
					.from(INVENTORY_DETAIL).join(ITEM).on(ITEM.ID.eq(INVENTORY_DETAIL.ITEM))
					.join(PRODUCT).on(ITEM.PRODUCT.eq(PRODUCT.ID))
					.where(INVENTORY_DETAIL.DOMAIN.eq(domain.getId()))
					.and(INVENTORY_DETAIL.INVENTORY.eq(inventoryId))
					.and(c)
					.orderBy(PRODUCT.NAME)
					.fetch();
			
				
			Vector<StockInfo> v = new Vector<StockInfo>();
			for (Record9<Double, String, String, String, String, String, String, Integer, Integer> d : data) {
				StockInfo si = new StockInfo();
				Item i = getItem(domain, login, d.getValue(ITEM.ID));
				si.setItem(i.setSerialNumber(d.getValue(ITEM.SERIAL_NUMBER)));
				si.setProduct(d.getValue(PRODUCT.CODE));
				si.setQuantity(d.getValue(INVENTORY_DETAIL.REAL_QUANTITY));
				si.setProductId(d.getValue(PRODUCT.ID));
				si.setProductName(d.getValue(PRODUCT.NAME));
				v.add(si);
			}
			return v;
		} finally {
			if (ctx != null) ctx.close();
		}
	}
	public String[] getWarehouseComments(DSLContext dslContext, Integer id ){
		
		Result<Record2<String, String>> data = dslContext.select(WAREHOUSE_TRANSFER.COMMENTS,WAREHOUSE_TRANSFER.SERIES)
			.from(WAREHOUSE_TRANSFER)
			.where(WAREHOUSE_TRANSFER.TARGET_WAREHOUSE.eq(id)).fetch();
		String[] s = new String[2];
		
		if(data.isNotEmpty() && data.get(0).value1()!=null) s[0] = data.get(0).value1();
		else s[0] = "";
		if(data.isNotEmpty() && data.get(0).value2()!=null) s[1] = data.get(0).value2();
		else s[1] = "";
			
		return s;
		
	}
	
	public Tag getTag(Domain domain, String login, Integer id){
		return AON.getTag(domain.getName(), domain.getId(), login, id);
	}

	public Item getItem(Domain domain, String login, Integer id ){
		return AON.getItem(domain.getName(), domain.getId(), login, id);
	}

	public Boolean checkSeries(Domain domain, Series s, Warehouse w, Warehouse w2, String login){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login);
			Result<Record1< Integer>> data = null; 
			if(w != null)
				data = ctx.getDslContext().select(SERIES.ID)
					.from(WAREHOUSE)
						.join(WORKPLACE).on(WAREHOUSE.WORKPLACE.eq(WORKPLACE.ID))
						.join(SERIES).on(SERIES.SCOPE.eq(WORKPLACE.SCOPE))
					.where(SERIES.ID.eq(s.getId()).and(WAREHOUSE.ID.eq(w.getId()))).fetch();
			
			Result<Record1< Integer>> data2 = null;
			if(w2 != null)
				data2 = ctx.getDslContext().select(SERIES.ID)
					.from(WAREHOUSE)
						.join(WORKPLACE).on(WAREHOUSE.WORKPLACE.eq(WORKPLACE.ID))
						.join(SERIES).on(SERIES.SCOPE.eq(WORKPLACE.SCOPE))
					.where(SERIES.ID.eq(s.getId()).and(WAREHOUSE.ID.eq(w2.getId()))).fetch();
			
			return (w != null && data.isNotEmpty()) || (w2!= null && data2.isNotEmpty());

		} finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public Series getSeries(Domain domain, String serie, String login){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login);
			
			Result<Record3< Integer, String,String>> data = ctx.getDslContext().select(SERIES.ID,SERIES.CODE,SERIES.DESCRIPTION)
				.from(SERIES)
				.where(SERIES.DOMAIN.eq(domain.getId()))
					.and(SERIES.CODE.eq(serie)).fetch();
			
			Series s = new Series();
			for(Record3<Integer, String,String> r : data){
				
				s.setId(r.value1());
				s.setCode(r.value2());
				s.setDescription(r.value3());
	
			}
			return s;

		} finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public Vector<Series> getSeries(Domain domain, Warehouse warehouse, Workplace workplace, String login){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login);
			
			Result<Record3< Integer, String,String>> data = ctx.getDslContext().select(SERIES.ID,SERIES.CODE,SERIES.DESCRIPTION)
				.from(SERIES)
				.where(SERIES.DOMAIN.eq(domain.getId()))
					.and(SERIES.ACTIVE.eq((byte)1))
					.and(SERIES.SCOPE.eq(workplace.getScope()))
					.and(SERIES.DELIVERY.eq((byte) 1))
					.fetch();
			
			Vector<Series> v = new Vector<Series>();
			for(Record3<Integer, String,String> r : data){
				Series s = new Series();
				s.setId(r.value1());
				s.setCode(r.value2());
				s.setDescription(r.value3());
				v.add(s);
			}
			return v;

		} finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public Vector<Series> getSeries(Domain domain, String login){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(),  login);
			
			Result<Record3< Integer, String,String>> data = ctx.getDslContext().select(SERIES.ID,SERIES.CODE,SERIES.DESCRIPTION)
				.from(SERIES)
				.where(SERIES.DOMAIN.eq(domain.getId()))
				.and(SERIES.ACTIVE.eq((byte)1))
				.and(SERIES.DELIVERY.eq((byte)1))
				.fetch();
			
			Vector<Series> v = new Vector<Series>();
			
			for(Record3<Integer, String,String> r : data){
				Series s = new Series();
				s.setId(r.value1());
				s.setCode(r.value2());
				s.setDescription(r.value3());
				v.add(s);
			}
			return v;

		} finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public Warehouse getWarehouse(Domain domain, User user, String warehouse){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), user.getLogin());
			
			Result<Record3< Integer, String,Integer>> data ;
			if(DBCatalogue.isParentUser(domain, user)){
				data = ctx.getDslContext().select(WAREHOUSE.ID,WAREHOUSE.NAME,WAREHOUSE.WORKPLACE)
						.from(WAREHOUSE).join(WORKPLACE).on(WAREHOUSE.WORKPLACE.eq(WORKPLACE.ID))
						.where(WAREHOUSE.NAME.eq(warehouse)).and(WAREHOUSE.DOMAIN.eq(domain.getId()))
						.fetch();	
			}
			else{
			data = ctx.getDslContext().select(WAREHOUSE.ID,WAREHOUSE.NAME,WAREHOUSE.WORKPLACE)
				.from(WAREHOUSE).join(WORKPLACE).on(WAREHOUSE.WORKPLACE.eq(WORKPLACE.ID))
				.join(USER_SCOPE).on(USER_SCOPE.SCOPE.eq(WORKPLACE.SCOPE))
				.where(WAREHOUSE.NAME.eq(warehouse)).and(WAREHOUSE.DOMAIN.eq(domain.getId()))
				.and(USER_SCOPE.USER_ID.eq(user.getId()))
				.fetch();
			}
			Warehouse w = new Warehouse();
			
			for(Record3<Integer, String,Integer> r : data){
				
				w.setDomain(domain.getId());
				w.setId(r.value1());
				w.setName(r.value2());
				w.setWorkplace(r.value3());	
			}
			return w;

		} finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public Vector<Warehouse> getWarehouse(Domain domain, User user){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), user.getLogin());
			Result<Record3< Integer, String,Integer>> data ;
			if(DBCatalogue.isParentUser(domain, user)){
				data = ctx.getDslContext().select(WAREHOUSE.ID,WAREHOUSE.NAME,WAREHOUSE.WORKPLACE)
						.from(WAREHOUSE).leftOuterJoin(WORKPLACE).on(WAREHOUSE.WORKPLACE.eq(WORKPLACE.ID))
						.where(WAREHOUSE.DOMAIN.eq(domain.getId()))
						.orderBy(WAREHOUSE.NAME)
						.fetch();
			}
			else{
			data = ctx.getDslContext().select(WAREHOUSE.ID,WAREHOUSE.NAME,WAREHOUSE.WORKPLACE)
				.from(WAREHOUSE).leftOuterJoin(WORKPLACE).on(WAREHOUSE.WORKPLACE.eq(WORKPLACE.ID))
				.leftOuterJoin(USER_SCOPE).on(USER_SCOPE.SCOPE.eq(WORKPLACE.SCOPE))
				.where(WAREHOUSE.DOMAIN.eq(domain.getId()))
				.and(USER_SCOPE.USER_ID.eq(user.getId()))
				.orderBy(WAREHOUSE.NAME)
				.fetch();
			}
			
			Vector<Warehouse> v = new Vector<Warehouse>();
			for(Record3<Integer, String,Integer> r : data){
				Warehouse w = new Warehouse();
				w.setDomain(domain.getId());
				w.setId(r.value1());
				w.setName(r.value2());
				w.setWorkplace(0);//
				v.add(w);
			}
			return v;

		} finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public Vector<Warehouse> getWarehouse(Domain domain, User user, Integer workplaceId){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), user.getLogin());
			Result<Record3< Integer, String,Integer>> data ;
			if(DBCatalogue.isParentUser(domain, user)){
				data = ctx.getDslContext().select(WAREHOUSE.ID,WAREHOUSE.NAME,WAREHOUSE.WORKPLACE)
						.from(WAREHOUSE).join(WORKPLACE).on(WAREHOUSE.WORKPLACE.eq(WORKPLACE.ID))
						.where(WAREHOUSE.DOMAIN.eq(domain.getId()))
						.and(WAREHOUSE.WORKPLACE.eq(workplaceId))
						.and(WAREHOUSE.ACTIVE.eq((byte) 1))
						.orderBy(WAREHOUSE.NAME)
						.fetch();
			}
			else{
			data = ctx.getDslContext().select(WAREHOUSE.ID,WAREHOUSE.NAME,WAREHOUSE.WORKPLACE)
				.from(WAREHOUSE).join(WORKPLACE).on(WAREHOUSE.WORKPLACE.eq(WORKPLACE.ID))
				.join(USER_SCOPE).on(USER_SCOPE.SCOPE.eq(WORKPLACE.SCOPE))
				.where(WAREHOUSE.DOMAIN.eq(domain.getId()))
				.and(USER_SCOPE.USER_ID.eq(user.getId()))
				.and(WAREHOUSE.WORKPLACE.eq(workplaceId))
				.and(WAREHOUSE.ACTIVE.eq((byte) 1))
				.orderBy(WAREHOUSE.NAME)
				.fetch();
			}
			Vector<Warehouse> v = new Vector<Warehouse>();
			
			for(Record3<Integer, String,Integer> r : data){
				Warehouse w = new Warehouse();
				w.setDomain(domain.getId());
				w.setId(r.value1());
				w.setName(r.value2());
				w.setWorkplace(0);//
				v.add(w);
			}
			return v;

		} finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public Vector<StockInfo> getProposal(Domain domain, Integer proposalId, String login){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login);
			
			Result<Record16<Integer, Integer, Integer, Integer, Integer, Integer, String, Double, Double, String, Byte, Integer, String, Timestamp, String, Timestamp>> data = ctx.getDslContext().select( PROPOSAL.WORKPLACE, PROPOSAL.DEPARTMENT, PROPOSAL_DETAIL.ID, PROPOSAL_DETAIL.DOMAIN, PROPOSAL_DETAIL.PROPOSAL, PROPOSAL_DETAIL.ITEM, PROPOSAL_DETAIL.DESCRIPTION,  PROPOSAL_DETAIL.QUANTITY, PROPOSAL_DETAIL.PRICE, PROPOSAL_DETAIL.DISCOUNT_EXPR, PROPOSAL_DETAIL.STATUS, PROPOSAL_DETAIL.SUPPLIER, PROPOSAL_DETAIL.CREATION_USER, PROPOSAL_DETAIL.CREATION_DATE, PROPOSAL_DETAIL.MODIFICATION_USER, PROPOSAL_DETAIL.MODIFICATION_DATE)
								.from(PROPOSAL_DETAIL).join(PROPOSAL).on(PROPOSAL.ID.eq(PROPOSAL_DETAIL.PROPOSAL))
								.join(ITEM).on(PROPOSAL_DETAIL.ITEM.eq(ITEM.ID)).join(PRODUCT).on(PRODUCT.ID.eq(ITEM.PRODUCT))
								.where(PROPOSAL_DETAIL.PROPOSAL.eq(proposalId))
								.and(PROPOSAL_DETAIL.DOMAIN.eq(domain.getId()))
								
								.orderBy(PRODUCT.NAME)
								.fetch();
			
			Vector<StockInfo> v = new Vector<StockInfo>();
			
			for(Record16<Integer, Integer, Integer, Integer, Integer, Integer, String, Double, Double, String, Byte, Integer, String, Timestamp, String, Timestamp> r : data){
				StockInfo si  = new StockInfo();
				Workplace wp = DBCatalogue.getWorkplace(domain, new User().setLogin(login), r.value1());
				Department d = DBCatalogue.getDepartment(domain, wp, r.value2(), login);
				Record5<Integer, String, String, String, String> rd = ctx.getDslContext().select(ITEM.PRODUCT,ITEM.DETAIL,ITEM.DETAIL2, ITEM.DETAIL3, ITEM.SERIAL_NUMBER).from(ITEM).where(ITEM.ID.eq(r.value6())).fetchOne();
				Integer productId = rd.getValue(ITEM.PRODUCT);
				com.esferalia.aon.occam.api.model.product.Product p = AON.getProduct(domain.getName(), domain.getId(), login,
						f -> f.getIdProperty().eq(productId));
				si.setDepartmentStr(d.getName());
				si.setWorkplaceStr(wp.getDescription());
				si.setDomainId(domain.getId());
				si.setItem(new Item().setId(r.getValue(PROPOSAL_DETAIL.ITEM))
						.setDetail(rd.getValue(ITEM.DETAIL))
						.setDetail2(rd.getValue(ITEM.DETAIL2))
						.setDetail3(rd.getValue(ITEM.DETAIL3))
						.setSerialNumber(rd.getValue(ITEM.SERIAL_NUMBER)));
				si.setQuantity(r.value8());
				si.setProductName(p.getName());
				si.setProduct(p.getCode());
				v.add(si);
			}
			return v;
		} finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public Vector<StockInfo> getIncome(Domain domain, Integer incomeId, String login){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login);
			
			Result<Record8<Double, String, String, String, String, String, String, Integer>> data = ctx.getDslContext().select(INCOME_DETAIL.QUANTITY,ITEM.DETAIL, ITEM.DETAIL2, ITEM.DETAIL3, PRODUCT.NAME, PRODUCT.CODE, ITEM.SERIAL_NUMBER, ITEM.ID)
								.from(INCOME_DETAIL)
								.join(ITEM).on(INCOME_DETAIL.ITEM.eq(ITEM.ID)).join(PRODUCT).on(PRODUCT.ID.eq(ITEM.PRODUCT))
								.where(INCOME_DETAIL.INCOME.eq(incomeId))
								.and(INCOME_DETAIL.DOMAIN.eq(domain.getId()))
								.orderBy(PRODUCT.NAME)
								.fetch();
			
			Vector<StockInfo> v = new Vector<StockInfo>();
			
			for(Record8<Double, String, String, String, String, String, String,Integer> r : data){
				StockInfo si  = new StockInfo();
				si.setDomainId(domain.getId());
				
				si.setItem(new Item().setId(r.getValue(ITEM.ID))
						.setDetail(r.getValue(ITEM.DETAIL))
						.setDetail2(r.getValue(ITEM.DETAIL2))
						.setDetail3(r.getValue(ITEM.DETAIL3))
						.setSerialNumber(r.getValue(ITEM.SERIAL_NUMBER)));				
				si.setQuantity(r.value1());
				si.setProductName(r.value5());
				si.setProduct(r.value6());
				v.add(si);
			}
			return v;
		} finally {
			if (ctx != null) ctx.close();
		}
	}
	
	
	static class StockAux {
		Integer domainId;
		Integer stockId;
		Integer itemId;
		Double quantity;
		Integer warehouseId;
		
		public StockAux() {
		}
		
		public Integer getDomainId() {
			return domainId;
		}
		public StockAux setDomainId(Integer domainId) {
			this.domainId = domainId;
			return this;
		}
		public Integer getStockId() {
			return stockId;
		}
		public StockAux setStockId(Integer stockId) {
			this.stockId = stockId;
			return this;
		}
		public Integer getItemId() {
			return itemId;
		}
		public StockAux setItemId(Integer itemId) {
			this.itemId = itemId;
			return this;
		}
		public Double getQuantity() {
			return quantity;
		}
		public StockAux setQuantity(Double quantity) {
			this.quantity = quantity;
			return this;
		}
		public Integer getWarehouseId() {
			return warehouseId;
		}
		public StockAux setWarehouseId(Integer warehouseId) {
			this.warehouseId = warehouseId;
			return this;
		}
		
	}
	
}
