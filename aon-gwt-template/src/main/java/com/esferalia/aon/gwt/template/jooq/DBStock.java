package com.esferalia.aon.gwt.template.jooq;

import static com.esferalia.aon.jooq.tables.CatalogueItem.CATALOGUE_ITEM;
import static com.esferalia.aon.jooq.tables.IncomeDetail.INCOME_DETAIL;
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

import java.sql.Timestamp;
import java.util.Vector;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.DeleteConditionStep;
import org.jooq.InsertValuesStep12;
import org.jooq.InsertValuesStep13;
import org.jooq.InsertValuesStep4;
import org.jooq.InsertValuesStep5;
import org.jooq.Record1;
import org.jooq.Record16;
import org.jooq.Record2;
import org.jooq.Record3;
import org.jooq.Record4;
import org.jooq.Record5;
import org.jooq.Record6;
import org.jooq.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.company.Department;
import com.code.aon.company.WorkPlace;
import com.code.aon.config.Series;
import com.code.aon.product.Item;
import com.code.aon.product.Product;
import com.code.aon.product.enumeration.ProductStatus;
import com.esferalia.aon.gwt.template.server.AuditInfo;
import com.esferalia.aon.gwt.template.server.ProposalInfo;
import com.esferalia.aon.gwt.template.server.StockInfo;
import com.esferalia.aon.gwt.template.server.TransferInfo;
import com.esferalia.aon.gwt.template.shared.Error;
import com.esferalia.aon.gwt.template.shared.Warehouse;
import com.esferalia.aon.jooq.tables.records.ProposalDetailRecord;
import com.esferalia.aon.jooq.tables.records.StockRecord;
import com.esferalia.aon.jooq.tables.records.WarehouseTransferDetailRecord;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;

public class DBStock {
	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(DBStock.class.getName());
	
	static String stockquery ;
	static String inventoryquery ;
	
	Vector<String> v = new Vector<String>();
	static String itemIds;
	static TransferInfo transferInfo;
	public static Error insertStock2(String domain, Integer domainId,Vector<StockInfo> stock, TransferInfo ti, Integer inventoryId, AuditInfo ai){
		itemIds ="";
		Error error = new Error();
		error.setError(true);
		Vector<String> verror = new Vector<String>();
		verror.add("");
		error.setTextError(verror);
		AONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domain, domainId);
			 
			Vector<String> v = new Vector<String>();
			inventoryquery = "update inventory_detail set real_quantity = case ";
			transferInfo= ti;
			
			AONContext sctx = ctx;
			stock.stream().forEach(s ->{
				if(s.getProduct() != null){
					Result<Record2<Integer, Double>> data = sctx.getDslContext().select(ITEM.ID, ITEM.PRICE)
						.from(ITEM)
						.where(ITEM.BARCODE.eq(s.getProduct())).and(ITEM.DOMAIN.eq(domainId)).fetch();
					if(data.isEmpty()){

						data = sctx.getDslContext().select(ITEM.ID, ITEM.PRICE)
								.from(ITEM).join(PRODUCT).on(PRODUCT.ID.eq(ITEM.PRODUCT))
								.where(PRODUCT.CODE.eq(s.getProduct()))
										.and(PRODUCT.DOMAIN.eq(domainId)).fetch();
					}
					if(data.size()>1){
						Condition detail = ITEM.DETAIL.eq(s.getDetail());
						if(s.getDetail() == "") 
							detail = ITEM.DETAIL.eq("").or(ITEM.DETAIL.isNull());
						
						Condition detail2 = ITEM.DETAIL2.eq(s.getDetail2());
						if(s.getDetail2() == "") 
							detail2 = ITEM.DETAIL2.eq("").or(ITEM.DETAIL2.isNull());
						
						Condition detail3 = ITEM.DETAIL3.eq(s.getDetail3());
						if(s.getDetail3() == "") 
							detail3 = ITEM.DETAIL3.eq("").or(ITEM.DETAIL3.isNull());
						
						data = sctx.getDslContext().select(ITEM.ID, ITEM.PRICE)
								.from(ITEM).join(PRODUCT).on(PRODUCT.ID.eq(ITEM.PRODUCT))
								.where(PRODUCT.CODE.eq(s.getProduct()))
									.and(detail)
									.and(detail2)
									.and(detail3)
									
									
									.and(PRODUCT.DOMAIN.eq(domainId)).fetch();
					}
					
					if(!data.isEmpty()){
						Integer itemId = data.get(0).value1();
						System.out.println(itemId);
							
						s.setDomainId(domainId);
						s.setItemId(itemId);
					
						Result<Record1<Integer>> data2 = sctx.getDslContext().select(INVENTORY_DETAIL.ID).from(INVENTORY_DETAIL)
						.where(INVENTORY_DETAIL.ITEM.eq(itemId))
						.and(INVENTORY_DETAIL.INVENTORY.eq(inventoryId)).fetch();
						
						if(data2.isNotEmpty()){
							itemIds = itemIds + ","+itemId;
							inventoryquery = inventoryquery + " when item = "+ itemId+" then "+s.getQuantity();
						}
						else{
							sctx.getDslContext().insertInto(INVENTORY_DETAIL, INVENTORY_DETAIL.ACTUAL_QUANTITY, INVENTORY_DETAIL.COST, INVENTORY_DETAIL.REAL_QUANTITY, INVENTORY_DETAIL.DOMAIN, INVENTORY_DETAIL.INVENTORY, INVENTORY_DETAIL.ITEM)
									.values(0.0, data.get(0).value2(), s.getQuantity(), s.getDomainId(), inventoryId, itemId).execute() ;
						}
						
			
					}
					else{
						v.add("*Fila " +(s.getRow()+1) + " : El producto no existe o los detalles no coincide.");
						error.setError(false);
						error.setTextError(v);
					}
				}
			});
	
			if(error.getError()){
				if(!inventoryquery.equals("update inventory_detail set real_quantity = case ")){
					inventoryquery = inventoryquery + " else " + 0.0 + " end where inventory = "+ inventoryId +" and domain = "+ domainId +" and item in ("+ itemIds.substring(1) +");";
					ctx.getDslContext().query(inventoryquery).execute();
				}
			}
			return error;
			
		}finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public static Double[] getSupplier(AONContext ctx, Integer workplace, Integer domainId, Integer itemId){
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
	
	public static Error insertProposal(String domain, Integer domainId,Vector<StockInfo> stock,Integer proposal, AuditInfo ai, Integer workplace){
		Error error = new Error();
		error.setError(true);
		Vector<String> verror = new Vector<String>();
		verror.add("");
		error.setTextError(verror);
		Vector<String> v = new Vector<String>();
		AONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domain, domainId);
			InsertValuesStep13<ProposalDetailRecord, Integer, Integer, Integer, String, Double, Double, String, Byte, Integer, String, Timestamp, String, Timestamp> proposalInsertQuery = ctx.getDslContext().insertInto(PROPOSAL_DETAIL, PROPOSAL_DETAIL.DOMAIN, PROPOSAL_DETAIL.PROPOSAL, PROPOSAL_DETAIL.ITEM, PROPOSAL_DETAIL.DESCRIPTION,  PROPOSAL_DETAIL.QUANTITY, PROPOSAL_DETAIL.PRICE, PROPOSAL_DETAIL.DISCOUNT_EXPR, PROPOSAL_DETAIL.STATUS, PROPOSAL_DETAIL.SUPPLIER, PROPOSAL_DETAIL.CREATION_USER, PROPOSAL_DETAIL.CREATION_DATE, PROPOSAL_DETAIL.MODIFICATION_USER, PROPOSAL_DETAIL.MODIFICATION_DATE);
			InsertValuesStep12<ProposalDetailRecord, Integer, Integer, Integer, Integer, String, Double, Double, String, Byte, Integer, String, Timestamp> proposalUpdateQuery = ctx.getDslContext().insertInto(PROPOSAL_DETAIL, PROPOSAL_DETAIL.ID, PROPOSAL_DETAIL.DOMAIN, PROPOSAL_DETAIL.PROPOSAL, PROPOSAL_DETAIL.ITEM, PROPOSAL_DETAIL.DESCRIPTION,  PROPOSAL_DETAIL.QUANTITY, PROPOSAL_DETAIL.PRICE, PROPOSAL_DETAIL.DISCOUNT_EXPR, PROPOSAL_DETAIL.STATUS, PROPOSAL_DETAIL.SUPPLIER, PROPOSAL_DETAIL.MODIFICATION_USER, PROPOSAL_DETAIL.MODIFICATION_DATE);
			
			AONContext sctx = ctx;
			Vector<Integer> updateIds = new Vector<Integer>();
			
			stock.stream().forEach(s ->{
				String code = s.getProduct();
				
				if(code != null){
					Result<Record2<Integer, Double>> data = sctx.getDslContext().select(ITEM.ID, ITEM.PRICE)
						.from(ITEM)
						.where(ITEM.BARCODE.eq(s.getProduct())).and(ITEM.DOMAIN.eq(domainId)).fetch();
					if(data.isEmpty()){

						data = sctx.getDslContext().select(ITEM.ID, ITEM.PRICE)
								.from(ITEM).join(PRODUCT).on(PRODUCT.ID.eq(ITEM.PRODUCT))
								.where(PRODUCT.CODE.eq(s.getProduct()))
										.and(PRODUCT.DOMAIN.eq(domainId)).fetch();
					}
					if(data.size()>1){
						Condition detail = ITEM.DETAIL.eq(s.getDetail());
						if(s.getDetail() == "") 
							detail = ITEM.DETAIL.eq("").or(ITEM.DETAIL.isNull());
						
						Condition detail2 = ITEM.DETAIL2.eq(s.getDetail2());
						if(s.getDetail2() == "") 
							detail2 = ITEM.DETAIL2.eq("").or(ITEM.DETAIL2.isNull());
						
						Condition detail3 = ITEM.DETAIL3.eq(s.getDetail3());
						if(s.getDetail3() == "") 
							detail3 = ITEM.DETAIL3.eq("").or(ITEM.DETAIL3.isNull());
						
						data = sctx.getDslContext().select(ITEM.ID, ITEM.PRICE)
								.from(ITEM).join(PRODUCT).on(PRODUCT.ID.eq(ITEM.PRODUCT))
								.where(PRODUCT.CODE.eq(s.getProduct()))
									.and(detail)
									.and(detail2)
									.and(detail3)
									.and(PRODUCT.DOMAIN.eq(domainId)).fetch();
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
						ProposalInfo pi = new ProposalInfo();
						pi.setQuantity(s.getQuantity());
						pi.setDomain(domainId);
						pi.setProposal(proposal);
						pi.setItem(data.get(0).value1());
						pi.setStatus((byte) 0); 
						pi.setDescription("");
						pi.setDiscount((double) 0);
						Double[] supplier = getSupplier(sctx, workplace, domainId,pi.getItem());
				
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
								error.setError(false);
								error.setTextError(v);
							}
						}
						else{
							v.add("*Fila " +(s.getRow()+1) + " : El producto no dispone de un proveedor asignable.");
							error.setError(false);
							error.setTextError(v);
						}
					}else{
						v.add("*Fila " +(s.getRow()+1) + " : El producto no existe o los detalles no coincide.");
						error.setError(false);
						error.setTextError(v);
					}
				}
			});
			if(error.getError()){

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
	
	private static Boolean isCatalogue(AONContext ctx, ProposalInfo pi){
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
	
	private static Boolean isProposal(AONContext ctx, ProposalInfo pi){
		
		Result<Record1<Integer>> data = ctx.getDslContext().select(PROPOSAL_DETAIL.ID)
					.from(PROPOSAL_DETAIL)
					.where(PROPOSAL_DETAIL.PROPOSAL.eq(pi.getProposal()))
					.and(PROPOSAL_DETAIL.ITEM.eq(pi.getItem()))
					.fetch();
		
		return data.isNotEmpty();
	}
	
	private static ProposalInfo getProposal(AONContext ctx, ProposalInfo pi){
		
		Result<Record2<Integer, Double>> data = ctx.getDslContext().select(PROPOSAL_DETAIL.ID, PROPOSAL_DETAIL.QUANTITY)
					.from(PROPOSAL_DETAIL)
					.where(PROPOSAL_DETAIL.PROPOSAL.eq(pi.getProposal()))
					.and(PROPOSAL_DETAIL.ITEM.eq(pi.getItem()))
					.fetch();
		
		pi.setId(data.get(0).value1());
		pi.setQuantity(pi.getQuantity()+data.get(0).value2());
		return pi;
	}
	
	public static Error insertTransferStock(String domain, Integer domainId,Vector<StockInfo> stock, TransferInfo ti,AuditInfo ai){
		Error error = new Error();
		error.setError(true);
		Vector<String> verror = new Vector<String>();
		verror.add("");
		error.setTextError(verror);
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain, domainId);
			
			 
			DeleteConditionStep<StockRecord> stockDeleteQuery;
			DeleteConditionStep<WarehouseTransferDetailRecord> transferDeleteQuery;
			InsertValuesStep4<StockRecord, Integer, Integer, Double, Integer> stockInsertQuery = ctx.getDslContext().insertInto(STOCK, STOCK.DOMAIN, STOCK.ITEM, STOCK.QUANTITY, STOCK.WAREHOUSE);
			InsertValuesStep5<StockRecord, Integer, Integer, Integer, Double, Integer> stockUpdateQuery = ctx.getDslContext().insertInto(STOCK, STOCK.ID, STOCK.DOMAIN, STOCK.ITEM, STOCK.QUANTITY, STOCK.WAREHOUSE);
			InsertValuesStep4<WarehouseTransferDetailRecord, Integer, Integer, Integer, Double> transferInsert = ctx.getDslContext().insertInto(WAREHOUSE_TRANSFER_DETAIL, WAREHOUSE_TRANSFER_DETAIL.DOMAIN, WAREHOUSE_TRANSFER_DETAIL.ITEM, WAREHOUSE_TRANSFER_DETAIL.WAREHOUSE_TRANSFER, WAREHOUSE_TRANSFER_DETAIL.QUANTITY);
			InsertValuesStep5<WarehouseTransferDetailRecord, Integer, Integer, Integer, Integer, Double> transferUpdate = ctx.getDslContext().insertInto(WAREHOUSE_TRANSFER_DETAIL,WAREHOUSE_TRANSFER_DETAIL.ID, WAREHOUSE_TRANSFER_DETAIL.DOMAIN, WAREHOUSE_TRANSFER_DETAIL.ITEM, WAREHOUSE_TRANSFER_DETAIL.WAREHOUSE_TRANSFER, WAREHOUSE_TRANSFER_DETAIL.QUANTITY);

			Vector<Integer> stockDeleteIds = new Vector<Integer>();
			Vector<Integer> transferDeleteIds = new Vector<Integer>();
			
			/*Date d = new Date();
			Timestamp t = new Timestamp(d.getTime());
			
			Condition series;
			String scode;
			
			if(ti.getSeries() == null || ti.getSeries().getCode() == "-") {
				series = WAREHOUSE_TRANSFER.SERIES.isNull();
				scode = null;
			}
			else {
				series = WAREHOUSE_TRANSFER.SERIES.eq(ti.getSeries().getCode());
				scode = ti.getSeries().getCode();
			}
			Result<Record1<Integer>> n = ctx.getDslContext().select(DSL.max(WAREHOUSE_TRANSFER.NUMBER))
				.from(WAREHOUSE_TRANSFER)
				.where(WAREHOUSE_TRANSFER.DOMAIN.eq(domainId).and(series)).fetch();
			
			Integer max;
			if(n.isEmpty() || n.get(0).value1()==null) max = 0;
			else max = n.get(0).value1(); //get max number (domain, serie)
			Integer next = max+1;
			
			Integer source = null, target = null;
			if(ti.getSourceWarehouse() != null) source = ti.getSourceWarehouse().getId();
			if(ti.getTargetWarehouse() != null) target = ti.getTargetWarehouse().getId();
			
			Integer transferId = ctx.getDslContext().insertInto(WAREHOUSE_TRANSFER,WAREHOUSE_TRANSFER.DOMAIN, WAREHOUSE_TRANSFER.SERIES,WAREHOUSE_TRANSFER.NUMBER, WAREHOUSE_TRANSFER.COMMENTS, WAREHOUSE_TRANSFER.ISSUE_TIME, WAREHOUSE_TRANSFER.SOURCE_WAREHOUSE, WAREHOUSE_TRANSFER.TARGET_WAREHOUSE)
					.values(domainId,scode,next,ti.getComments(),t,source,target).returning(WAREHOUSE_TRANSFER.ID).fetchOne().getId();
			*/
			Integer transferId;
			if(ti.getSeries().getCode() != null){
				transferId = ctx.getDslContext().select(WAREHOUSE_TRANSFER.ID)
										.from(WAREHOUSE_TRANSFER)
										.where(WAREHOUSE_TRANSFER.DOMAIN.eq(domainId))
										.and(WAREHOUSE_TRANSFER.SERIES.eq(ti.getSeries().getCode()))
										.and(WAREHOUSE_TRANSFER.NUMBER.eq(ti.getNumber()))
										.fetchOne().value1();
			} else{
				transferId = ctx.getDslContext().select(WAREHOUSE_TRANSFER.ID)
						.from(WAREHOUSE_TRANSFER)
						.where(WAREHOUSE_TRANSFER.DOMAIN.eq(domainId))
						.and(WAREHOUSE_TRANSFER.SERIES.isNull())
						.and(WAREHOUSE_TRANSFER.NUMBER.eq(ti.getNumber()))
						.fetchOne().value1();
			}
			Vector<String> v = new Vector<String>();
			AONContext sctx = ctx;
			stock.stream().forEach(s ->{
				if(s.getProduct() != null){
					Result<Record1< Integer>> data = sctx.getDslContext().select(ITEM.ID)
						.from(ITEM)
						.where(ITEM.BARCODE.eq(s.getProduct())).and(ITEM.DOMAIN.eq(domainId)).fetch();
					if(data.isEmpty()){

						data = sctx.getDslContext().select(ITEM.ID)
								.from(ITEM).join(PRODUCT).on(PRODUCT.ID.eq(ITEM.PRODUCT))
								.where(PRODUCT.CODE.eq(s.getProduct()))
										.and(PRODUCT.DOMAIN.eq(domainId)).fetch();
					}
					if(data.size()>1){
						Condition detail = ITEM.DETAIL.eq(s.getDetail());
						if(s.getDetail() == "") 
							detail = ITEM.DETAIL.eq("").or(ITEM.DETAIL.isNull());
						
						Condition detail2 = ITEM.DETAIL2.eq(s.getDetail2());
						if(s.getDetail2() == "") 
							detail2 = ITEM.DETAIL2.eq("").or(ITEM.DETAIL2.isNull());
						
						Condition detail3 = ITEM.DETAIL3.eq(s.getDetail3());
						if(s.getDetail3() == "") 
							detail3 = ITEM.DETAIL3.eq("").or(ITEM.DETAIL3.isNull());
						
						data = sctx.getDslContext().select(ITEM.ID)
								.from(ITEM).join(PRODUCT).on(PRODUCT.ID.eq(ITEM.PRODUCT))
								.where(PRODUCT.CODE.eq(s.getProduct()))
									.and(detail)
									.and(detail2)
									.and(detail3)
									.and(PRODUCT.DOMAIN.eq(domainId)).fetch();
					}
					if(!data.isEmpty()){
						Integer itemId = data.get(0).value1();
						ti.getSourceWarehouse();
						ti.getTargetWarehouse();
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
							transferUpdate.values(data4.value1(),domainId, itemId, transferId,s.getQuantity()+data4.value2());
						}
						else {
							transferInsert.values(domainId, itemId, transferId,s.getQuantity());
						}
						Double quantity, quantity2;
						Integer stockId, stockId2;
						if((ti.getSourceWarehouse() != null && ti.getSourceWarehouse().getId() != null) 
								&& (ti.getTargetWarehouse() != null && ti.getTargetWarehouse().getId() != null)){
							LOGGER.debug("Source warehouse is not null & target warehouse is not null");
							if(data2.isNotEmpty() && data3.isNotEmpty()){
								quantity = data2.get(0).value1();
								quantity2 = data3.get(0).value1();
								stockId = data2.get(0).value2();
								stockId2 = data3.get(0).value2();
								
								//UPDATE source & target
								stockDeleteIds.add(stockId);
								stockDeleteIds.add(stockId2);
								stockUpdateQuery.values(stockId, domainId, itemId, (quantity+s.getQuantity()), ti.getTargetWarehouse().getId());
								stockUpdateQuery.values(stockId2, domainId, itemId, (quantity2-s.getQuantity()), ti.getSourceWarehouse().getId());
							}
							else if(data2.isEmpty() && data3.isNotEmpty()){
								quantity2 = data3.get(0).value1();
								stockId2 = data3.get(0).value2(); 
								
								//UPDATE source
								stockDeleteIds.add(stockId2);
								stockUpdateQuery.values(stockId2, domainId, itemId, (quantity2-s.getQuantity()), ti.getSourceWarehouse().getId());
								
								//INSERT target
								stockInsertQuery.values(domainId, itemId, s.getQuantity(), ti.getTargetWarehouse().getId());
							}
						}
						else if((ti.getSourceWarehouse() != null && ti.getSourceWarehouse().getId() != null) 
								&& (ti.getTargetWarehouse() == null || ti.getTargetWarehouse().getId() == null)){
							LOGGER.debug("Source warehouse is not null & target warehouse is null");
							if(data3.isNotEmpty()){
								quantity2 = data3.get(0).value1();
								stockId2 = data3.get(0).value2();
								
								//UPDATE source
								stockDeleteIds.add(stockId2);
								stockUpdateQuery.values(stockId2, domainId, itemId, (quantity2-s.getQuantity()), ti.getSourceWarehouse().getId());
							}

						}
						else if((ti.getSourceWarehouse() == null || ti.getSourceWarehouse().getId() == null) 
								&& (ti.getTargetWarehouse() != null && ti.getTargetWarehouse().getId() != null)){
							LOGGER.debug("Source warehouse is null & target warehouse is not null");
							if(data2.isNotEmpty()){
								quantity = data2.get(0).value1();
								stockId = data2.get(0).value2();
								
								//UPDATE target
								stockDeleteIds.add(stockId);
								stockUpdateQuery.values(stockId, domainId, itemId, (quantity+s.getQuantity()), ti.getTargetWarehouse().getId());
							}
							else{
								//INSERT target
								stockInsertQuery.values(domainId, itemId, s.getQuantity(), ti.getTargetWarehouse().getId());
							}
						}
					}
					else{
						v.add("*Fila " +(s.getRow()+1) + " : El producto no existe o los detalles no coincide.");
						LOGGER.error("Error: *Fila " +(s.getRow()+1) + " : El producto no existe o los detalles no coincide.");
						error.setError(false);
						error.setTextError(v);
					}
				}
			});
	
			if(error.getError()){
				ctx.deactivateForeignKeys();
				if(stockDeleteIds.size() > 0){
					stockDeleteQuery = ctx.getDslContext().delete(STOCK).where(STOCK.ID.in(stockDeleteIds));
					stockDeleteQuery.execute();
					stockUpdateQuery.execute();
				}	
				
				stockInsertQuery.execute();
				
				if(transferDeleteIds.size() > 0){
					transferDeleteQuery = ctx.getDslContext().delete(WAREHOUSE_TRANSFER_DETAIL).where(WAREHOUSE_TRANSFER_DETAIL.ID.in(transferDeleteIds));
					transferDeleteQuery.execute();
					transferUpdate.execute();
				}
				transferInsert.execute();
				
				ctx.activateForeignKeys();
			}
			return error;
			
		}finally {
			if (ctx != null) ctx.close();	
		}
	}

	public static Vector<StockInfo> getStocks(String domain, Integer domainId,Integer wid, Condition c, boolean onlyNonCero) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain, domainId);
			
			Result<Record5<Integer, Integer, Integer, Double,Integer>> data ;
			if(wid != null)
				data = ctx.getDslContext().select(STOCK.ID, STOCK.ITEM, STOCK.WAREHOUSE, STOCK.QUANTITY,ITEM.PRODUCT)
				.from(STOCK).join(ITEM).on(ITEM.ID.eq(STOCK.ITEM)).join(PRODUCT).on(ITEM.PRODUCT.eq(PRODUCT.ID))
				.where(STOCK.DOMAIN.eq(domainId)).and(STOCK.WAREHOUSE.eq(wid))
				.and(c)
				.orderBy(PRODUCT.NAME)
				.fetch();
			else
				data = ctx.getDslContext().select(STOCK.ID, STOCK.ITEM, STOCK.WAREHOUSE, STOCK.QUANTITY,ITEM.PRODUCT)
				.from(STOCK).join(ITEM).on(ITEM.ID.eq(STOCK.ITEM)).join(PRODUCT).on(ITEM.PRODUCT.eq(PRODUCT.ID))
				.where(STOCK.DOMAIN.eq(domainId))
				.and(c)
				.orderBy(PRODUCT.NAME)
				.fetch();
				
			Vector<StockInfo> v = new Vector<StockInfo>();
			for (Record5<Integer, Integer, Integer, Double, Integer> d : data) {
				Item i = getItem(ctx.getDslContext(),domain,d.value2());
				if ( (d.value4() == 0) && ((i.getStatus() == ProductStatus.DISCONTINUED) || (!i.getProduct().isInventoriable()) ) ) {
					ctx.getDslContext().delete(STOCK).where(STOCK.ID.equal(d.value1()));
				} else if ( !onlyNonCero || (d.value4() != 0) ) {
					StockInfo si = new StockInfo();
					si.setDetail(i.getDetail());
					si.setDetail2(i.getDetail2());
					si.setDetail3(i.getDetail3());
					if(i.getBarcode()!= null) si.setProduct(i.getBarcode());
					else {
						si.setProduct(i.getProduct().getCode());
					}
					
					si.setQuantity(d.value4());
					String[] s = getWarehouseComments(ctx.getDslContext(), d.value3());
					Series ss = new Series();ss.setCode(s[1]);
					//si.setSeries(ss);
					//si.setComments(s[0]);
					//si.setTargetWarehouse(getWarehouse(dslContext, d.value3()));
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
	
	public static Vector<StockInfo> getInventoryClosed(String domain, Integer domainId,Integer inventoryId, Condition c) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain, domainId);
			
			Result<Record6<Double, String, String, String, String, String>> data = 
				ctx.getDslContext().select(INVENTORY_DETAIL.REAL_QUANTITY,ITEM.DETAIL,
						ITEM.DETAIL2, ITEM.DETAIL3, PRODUCT.CODE, PRODUCT.NAME)
					.from(INVENTORY_DETAIL).join(ITEM).on(ITEM.ID.eq(INVENTORY_DETAIL.ITEM))
					.join(PRODUCT).on(ITEM.PRODUCT.eq(PRODUCT.ID))
					.where(INVENTORY_DETAIL.DOMAIN.eq(domainId))
					.and(INVENTORY_DETAIL.INVENTORY.eq(inventoryId))
					.and(c)
					.orderBy(PRODUCT.NAME)
					.fetch();
			
				
			Vector<StockInfo> v = new Vector<StockInfo>();
			for (Record6<Double, String, String, String, String, String> d : data) {
				StockInfo si = new StockInfo();
				si.setDetail(d.value2());
				si.setDetail2(d.value3());
				si.setDetail3(d.value4());
				si.setProduct(d.value5());
				si.setQuantity(d.value1());
				si.setProductName(d.value6());
				v.add(si);
			}
			return v;
		} finally {
			if (ctx != null) ctx.close();
		}
	}
	public static String[] getWarehouseComments(DSLContext dslContext, Integer id ){
		
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
	
	public static Item getItem(DSLContext dslContext, String domain, Integer id ){
		
		Result<Record6<String, String, String, String, Integer, Byte>> data = dslContext.select(ITEM.BARCODE, ITEM.DETAIL, ITEM.DETAIL2, ITEM.DETAIL3, ITEM.PRODUCT, ITEM.STATUS)
			.from(ITEM)
			.where(ITEM.ID.eq(id)).fetch();
		
		Item i = new Item();
		if(data.get(0).value1() != null)i.setBarcode(data.get(0).value1());
		if(data.get(0).value2() != null) i.setDetail(data.get(0).value2());
		else i.setDetail("");
		if(data.get(0).value3() != null) i.setDetail2(data.get(0).value3());
		else i.setDetail2("");
		if(data.get(0).value4() != null) i.setDetail3(data.get(0).value4());
		else i.setDetail3("");
		Product p = getProduct(dslContext, data.get(0).value5());
		i.setProduct(p);
		if(data.get(0).value6() != null) i.setStatus( ProductStatus.values()[data.get(0).value6()] );
		
		return i;
		
	}
	

	public static Product getProduct(DSLContext dslContext, Integer id ){
		
		Result<Record3<String,String,Byte>> data = dslContext.select(PRODUCT.CODE,PRODUCT.NAME,PRODUCT.INVENTORIABLE)
			.from(PRODUCT)
			.where(PRODUCT.ID.eq(id)).fetch();
		
		Product p = new Product();
		p.setId(id);
		p.setCode(data.get(0).value1());
		p.setName(data.get(0).value2());
		p.setInventoriable(data.get(0).value3()==1);
		return p;
		
	}
	
	public static Boolean checkSeries(String domain, Integer domainId,Series s, Warehouse w, Warehouse w2){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain, domainId);
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
	
	public static Series getSeries(String domain,Integer domainId, String serie){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain, domainId);
			
			Result<Record3< Integer, String,String>> data = ctx.getDslContext().select(SERIES.ID,SERIES.CODE,SERIES.DESCRIPTION)
				.from(SERIES)
				.where(SERIES.DOMAIN.eq(domainId))
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
	
	public static Vector<Series> getSeries(Warehouse warehouse, WorkPlace workplace, String domain,Integer domainId){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain, domainId);
			
			Result<Record3< Integer, String,String>> data = ctx.getDslContext().select(SERIES.ID,SERIES.CODE,SERIES.DESCRIPTION)
				.from(SERIES)
				.where(SERIES.DOMAIN.eq(domainId))
					.and(SERIES.ACTIVE.eq((byte)1))
					.and(SERIES.SCOPE.eq(workplace.getScope().getId()))
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
	
	public static Vector<Series> getSeries(String domain,Integer domainId){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain, domainId);
			
			Result<Record3< Integer, String,String>> data = ctx.getDslContext().select(SERIES.ID,SERIES.CODE,SERIES.DESCRIPTION)
				.from(SERIES)
				.where(SERIES.DOMAIN.eq(domainId))
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
	
	public static Warehouse getWarehouse(String warehouse, Integer domainId,String domain, Integer userId){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain, domainId);
			
			Result<Record3< Integer, String,Integer>> data ;
			if(DBCatalogue.isParentUser(ctx, userId, domainId)){
				data = ctx.getDslContext().select(WAREHOUSE.ID,WAREHOUSE.NAME,WAREHOUSE.WORKPLACE)
						.from(WAREHOUSE).join(WORKPLACE).on(WAREHOUSE.WORKPLACE.eq(WORKPLACE.ID))
						.where(WAREHOUSE.NAME.eq(warehouse)).and(WAREHOUSE.DOMAIN.eq(domainId))
						.fetch();	
			}
			else{
			data = ctx.getDslContext().select(WAREHOUSE.ID,WAREHOUSE.NAME,WAREHOUSE.WORKPLACE)
				.from(WAREHOUSE).join(WORKPLACE).on(WAREHOUSE.WORKPLACE.eq(WORKPLACE.ID))
				.join(USER_SCOPE).on(USER_SCOPE.SCOPE.eq(WORKPLACE.SCOPE))
				.where(WAREHOUSE.NAME.eq(warehouse)).and(WAREHOUSE.DOMAIN.eq(domainId))
				.and(USER_SCOPE.USER_ID.eq(userId))
				.fetch();
			}
			Warehouse w = new Warehouse();
			
			for(Record3<Integer, String,Integer> r : data){
				
				w.setDomainId(domainId);
				w.setId(r.value1());
				w.setName(r.value2());
				w.setWorkplace(r.value3());	
			}
			return w;

		} finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public static Vector<Warehouse> getWarehouse(String domain,Integer domainId, Integer userId){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain, domainId);
			Result<Record3< Integer, String,Integer>> data ;
			if(DBCatalogue.isParentUser(ctx, userId, domainId)){
				data = ctx.getDslContext().select(WAREHOUSE.ID,WAREHOUSE.NAME,WAREHOUSE.WORKPLACE)
						.from(WAREHOUSE).join(WORKPLACE).on(WAREHOUSE.WORKPLACE.eq(WORKPLACE.ID))
						.where(WAREHOUSE.DOMAIN.eq(domainId))
						.orderBy(WAREHOUSE.NAME)
						.fetch();
			}
			else{
			data = ctx.getDslContext().select(WAREHOUSE.ID,WAREHOUSE.NAME,WAREHOUSE.WORKPLACE)
				.from(WAREHOUSE).join(WORKPLACE).on(WAREHOUSE.WORKPLACE.eq(WORKPLACE.ID))
				.join(USER_SCOPE).on(USER_SCOPE.SCOPE.eq(WORKPLACE.SCOPE))
				.where(WAREHOUSE.DOMAIN.eq(domainId))
				.and(USER_SCOPE.USER_ID.eq(userId))
				.orderBy(WAREHOUSE.NAME)
				.fetch();
			}
			Vector<Warehouse> v = new Vector<Warehouse>();
			
			for(Record3<Integer, String,Integer> r : data){
				Warehouse w = new Warehouse();
				w.setDomainId(domainId);
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
	
	public static Vector<Warehouse> getWarehouse(String domain,Integer domainId, Integer userId, Integer workplaceId){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain, domainId);
			Result<Record3< Integer, String,Integer>> data ;
			if(DBCatalogue.isParentUser(ctx, userId, domainId)){
				data = ctx.getDslContext().select(WAREHOUSE.ID,WAREHOUSE.NAME,WAREHOUSE.WORKPLACE)
						.from(WAREHOUSE).join(WORKPLACE).on(WAREHOUSE.WORKPLACE.eq(WORKPLACE.ID))
						.where(WAREHOUSE.DOMAIN.eq(domainId))
						.and(WAREHOUSE.WORKPLACE.eq(workplaceId))
						.and(WAREHOUSE.ACTIVE.eq((byte) 1))
						.orderBy(WAREHOUSE.NAME)
						.fetch();
			}
			else{
			data = ctx.getDslContext().select(WAREHOUSE.ID,WAREHOUSE.NAME,WAREHOUSE.WORKPLACE)
				.from(WAREHOUSE).join(WORKPLACE).on(WAREHOUSE.WORKPLACE.eq(WORKPLACE.ID))
				.join(USER_SCOPE).on(USER_SCOPE.SCOPE.eq(WORKPLACE.SCOPE))
				.where(WAREHOUSE.DOMAIN.eq(domainId))
				.and(USER_SCOPE.USER_ID.eq(userId))
				.and(WAREHOUSE.WORKPLACE.eq(workplaceId))
				.and(WAREHOUSE.ACTIVE.eq((byte) 1))
				.orderBy(WAREHOUSE.NAME)
				.fetch();
			}
			Vector<Warehouse> v = new Vector<Warehouse>();
			
			for(Record3<Integer, String,Integer> r : data){
				Warehouse w = new Warehouse();
				w.setDomainId(domainId);
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
	
	public static Vector<StockInfo> getProposal(String domain, Integer domainId, Integer proposalId){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain, domainId);
			
			Result<Record16<Integer, Integer, Integer, Integer, Integer, Integer, String, Double, Double, String, Byte, Integer, String, Timestamp, String, Timestamp>> data = ctx.getDslContext().select( PROPOSAL.WORKPLACE, PROPOSAL.DEPARTMENT, PROPOSAL_DETAIL.ID, PROPOSAL_DETAIL.DOMAIN, PROPOSAL_DETAIL.PROPOSAL, PROPOSAL_DETAIL.ITEM, PROPOSAL_DETAIL.DESCRIPTION,  PROPOSAL_DETAIL.QUANTITY, PROPOSAL_DETAIL.PRICE, PROPOSAL_DETAIL.DISCOUNT_EXPR, PROPOSAL_DETAIL.STATUS, PROPOSAL_DETAIL.SUPPLIER, PROPOSAL_DETAIL.CREATION_USER, PROPOSAL_DETAIL.CREATION_DATE, PROPOSAL_DETAIL.MODIFICATION_USER, PROPOSAL_DETAIL.MODIFICATION_DATE)
								.from(PROPOSAL_DETAIL).join(PROPOSAL).on(PROPOSAL.ID.eq(PROPOSAL_DETAIL.PROPOSAL))
								.join(ITEM).on(PROPOSAL_DETAIL.ITEM.eq(ITEM.ID)).join(PRODUCT).on(PRODUCT.ID.eq(ITEM.PRODUCT))
								.where(PROPOSAL_DETAIL.PROPOSAL.eq(proposalId))
								.and(PROPOSAL_DETAIL.DOMAIN.eq(domainId))
								
								.orderBy(PRODUCT.NAME)
								.fetch();
			
			Vector<StockInfo> v = new Vector<StockInfo>();
			
			for(Record16<Integer, Integer, Integer, Integer, Integer, Integer, String, Double, Double, String, Byte, Integer, String, Timestamp, String, Timestamp> r : data){
				StockInfo si  = new StockInfo();
				WorkPlace wp = DBCatalogue.getWorkplace(r.value1(), domainId, domain);
				Department d = DBCatalogue.getDepartment(wp, r.value2(), domainId, domain);
				Record4<Integer, String, String, String> rd = ctx.getDslContext().select(ITEM.PRODUCT,ITEM.DETAIL,ITEM.DETAIL2, ITEM.DETAIL3).from(ITEM).where(ITEM.ID.eq(r.value6())).fetchOne();
				Integer productId = rd.value1();
				com.esferalia.aon.occam.api.model.product.Product p = AON.getProduct(ctx, productId);
				si.setDepartmentStr(d.getName());
				si.setWorkplaceStr(wp.getDescription());
				si.setDomainId(domainId);
				if(rd.value2() != null )si.setDetail(rd.value2()); 
				else si.setDetail("");
				if(rd.value3() != null )si.setDetail2(rd.value3()); 
				else si.setDetail2("");
				if(rd.value4() != null )si.setDetail3(rd.value4()); 
				else si.setDetail3("");
				si.setItemId(r.value6());
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
	
	public static Vector<StockInfo> getIncome(String domain, Integer domainId, Integer incomeId){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain, domainId);
			
			Result<Record6<Double, String, String, String, String, String>> data = ctx.getDslContext().select(INCOME_DETAIL.QUANTITY,ITEM.DETAIL, ITEM.DETAIL2, ITEM.DETAIL3, PRODUCT.NAME, PRODUCT.CODE)
								.from(INCOME_DETAIL)
								.join(ITEM).on(INCOME_DETAIL.ITEM.eq(ITEM.ID)).join(PRODUCT).on(PRODUCT.ID.eq(ITEM.PRODUCT))
								.where(INCOME_DETAIL.INCOME.eq(incomeId))
								.and(INCOME_DETAIL.DOMAIN.eq(domainId))
								.orderBy(PRODUCT.NAME)
								.fetch();
			
			Vector<StockInfo> v = new Vector<StockInfo>();
			
			for(Record6<Double, String, String, String, String, String> r : data){
				StockInfo si  = new StockInfo();
				si.setDomainId(domainId);
				if(r.value2() != null )si.setDetail(r.value2()); 
				else si.setDetail("");
				if(r.value3() != null )si.setDetail2(r.value3()); 
				else si.setDetail2("");
				if(r.value4() != null )si.setDetail3(r.value4()); 
				else si.setDetail3("");
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
}
