package com.esferalia.aon.gwt.template.jooq;

import static com.esferalia.aon.jooq.tables.CatalogueItem.CATALOGUE_ITEM;
import static com.esferalia.aon.jooq.tables.Item.ITEM;
import static com.esferalia.aon.jooq.tables.Product.PRODUCT;
import static com.esferalia.aon.jooq.tables.Proposal.PROPOSAL;
import static com.esferalia.aon.jooq.tables.ProposalDetail.PROPOSAL_DETAIL;
import static com.esferalia.aon.jooq.tables.Series.SERIES;
import static com.esferalia.aon.jooq.tables.Stock.STOCK;
import static com.esferalia.aon.jooq.tables.Warehouse.WAREHOUSE;
import static com.esferalia.aon.jooq.tables.WarehouseTransfer.WAREHOUSE_TRANSFER;
import static com.esferalia.aon.jooq.tables.WarehouseTransferDetail.WAREHOUSE_TRANSFER_DETAIL;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;
import static com.esferalia.aon.jooq.tables.WorkplaceDepartment.WORKPLACE_DEPARTMENT;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Vector;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.DeleteConditionStep;
import org.jooq.InsertValuesStep13;
import org.jooq.InsertValuesStep14;
import org.jooq.InsertValuesStep4;
import org.jooq.InsertValuesStep5;
import org.jooq.Record1;
import org.jooq.Record16;
import org.jooq.Record2;
import org.jooq.Record3;
import org.jooq.Record5;
import org.jooq.Result;
import org.jooq.impl.DSL;

import com.code.aon.company.Department;
import com.code.aon.company.WorkPlace;
import com.code.aon.config.Series;
import com.code.aon.product.Item;
import com.code.aon.product.Product;
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

	static String stockquery ;
	static String inventoryquery ;
	
	Vector<String> v = new Vector<String>();
	static String itemIds;
	static TransferInfo transferInfo;
	public static Error insertStock2(String domain, Integer domainId,Vector<StockInfo> stock, TransferInfo ti, Integer inventoryId){
		itemIds ="";
		Error error = new Error();
		error.setError(true);
		Vector<String> verror = new Vector<String>();
		verror.add("");
		error.setTextError(verror);
		AONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domain, domainId);
			 
			
			Date d = new Date();
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
			
			Integer transferId = ctx.getDslContext().insertInto(WAREHOUSE_TRANSFER,WAREHOUSE_TRANSFER.DOMAIN, WAREHOUSE_TRANSFER.SERIES,WAREHOUSE_TRANSFER.NUMBER, WAREHOUSE_TRANSFER.COMMENTS, WAREHOUSE_TRANSFER.ISSUE_TIME, WAREHOUSE_TRANSFER.SOURCE_WAREHOUSE, WAREHOUSE_TRANSFER.TARGET_WAREHOUSE)
					.values(domainId,scode,next,ti.getComments(),t,null,ti.getTargetWarehouse().getId()).returning(WAREHOUSE_TRANSFER.ID).fetchOne().getId();
			Vector<String> v = new Vector<String>();
			InsertValuesStep4<WarehouseTransferDetailRecord, Integer, Integer, Integer, Double> transferInsert = ctx.getDslContext().insertInto(WAREHOUSE_TRANSFER_DETAIL, WAREHOUSE_TRANSFER_DETAIL.DOMAIN, WAREHOUSE_TRANSFER_DETAIL.ITEM, WAREHOUSE_TRANSFER_DETAIL.WAREHOUSE_TRANSFER, WAREHOUSE_TRANSFER_DETAIL.QUANTITY);
			stockquery = "update stock set quantity = case ";
			inventoryquery = "update inventory_detail set real_quantity = case ";
			transferInfo= ti;
			
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
						Result<Record1<Double>> data2 = sctx.getDslContext().select(STOCK.QUANTITY)
							.from(STOCK)
							.where(STOCK.ITEM.eq(itemId).and(STOCK.WAREHOUSE.eq(transferInfo.getTargetWarehouse().getId()))).fetch();
						Double quantity;
						if(!data2.isEmpty()){
							 
							quantity = data2.get(0).value1();
							//System.out.println(" New Quantity: "+ s.getQuantity() +" ; code:  "+s.getProduct());
							//System.out.println(" Old Quantity: "+ quantity + " or " + Math.abs(quantity));
							System.out.println(s.getProduct());
							Double quantityTransfer = s.getQuantity()-quantity;
							
							s.setDomainId(domainId);
							s.setItemId(itemId);
							s.setTransferId(transferId);
							s.setQuantityDifference(quantityTransfer);
							
							transferInsert.values(s.getDomainId(),s.getItemId(),s.getTransferId(),s.getQuantityDifference());	

							if(quantityTransfer != 0.0){
								itemIds = itemIds + ","+s.getItemId();
								stockquery = stockquery + " when item = "+ s.getItemId()+" then "+ s.getQuantity();
								inventoryquery = inventoryquery + " when item = "+ s.getItemId()+" then "+s.getQuantity();
							}
							/*if(s.getQuantityDifference() != 0.0)
								dslContext.update(STOCK)
									.set(STOCK.QUANTITY,s.getQuantity())
									.where(STOCK.ITEM.eq(s.getItemId()).and(STOCK.WAREHOUSE.eq(ti.getTargetWarehouse().getId()))).execute();
							*/
						}
						else{
							
							v.add("*Fila " +(s.getRow()+1) + " : El producto no está en stock.");
							error.setError(false);
							error.setTextError(v);
							//return error;
						}
						
					}
					else{
						v.add("*Fila " +(s.getRow()+1) + " : El producto no existe o los detalles no coincide.");
						error.setError(false);
						error.setTextError(v);
						
						//return error;
					}
				}
			});
	
			if(error.getError()){
				if(!stockquery.equals("update stock set quantity = case ")){
					stockquery = stockquery + " else " + 0.0 + " end where domain = "+ domainId +" and item in ("+ itemIds.substring(1) +");";
					ctx.getDslContext().query(stockquery).execute();
				}
				if(!inventoryquery.equals("update inventory_detail set real_quantity = case ")){
					inventoryquery = inventoryquery + " else " + 0.0 + " end where inventory = "+ inventoryId +" and domain = "+ domainId +" and item in ("+ itemIds.substring(1) +");";
					ctx.getDslContext().query(inventoryquery).execute();
				}
				transferInsert.execute();
				/*DBConsults outer = new DBConsults();
				ImportStockThread thread = outer.new ImportStockThread(domain, stock, ti.getTargetWarehouse().getId());
				thread.start();*/
			}
			return error;
			
		}finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public static Error insertProposal(String domain, Integer domainId,Vector<StockInfo> stock,Integer proposal){
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
			InsertValuesStep14<ProposalDetailRecord, Integer, Integer, Integer, Integer, String, Double, Double, String, Byte, Integer, String, Timestamp, String, Timestamp> proposalUpdateQuery = ctx.getDslContext().insertInto(PROPOSAL_DETAIL, PROPOSAL_DETAIL.ID, PROPOSAL_DETAIL.DOMAIN, PROPOSAL_DETAIL.PROPOSAL, PROPOSAL_DETAIL.ITEM, PROPOSAL_DETAIL.DESCRIPTION,  PROPOSAL_DETAIL.QUANTITY, PROPOSAL_DETAIL.PRICE, PROPOSAL_DETAIL.DISCOUNT_EXPR, PROPOSAL_DETAIL.STATUS, PROPOSAL_DETAIL.SUPPLIER, PROPOSAL_DETAIL.CREATION_USER, PROPOSAL_DETAIL.CREATION_DATE, PROPOSAL_DETAIL.MODIFICATION_USER, PROPOSAL_DETAIL.MODIFICATION_DATE);

			AONContext sctx = ctx;
			Vector<Integer> updateIds = new Vector<Integer>();
			stock.stream().forEach(s ->{
				
				String code = s.getProduct();
				
				Record1<Integer> data = sctx.getDslContext().select(PRODUCT.ID)
						.from(PRODUCT)
						.where(PRODUCT.CODE.eq(code).and(PRODUCT.DOMAIN.eq(domainId))).fetchOne();
				
				Record2<Integer, Double> record = sctx.getDslContext().select(ITEM.ID, ITEM.PRICE)
						.from(ITEM)
						.where(ITEM.PRODUCT.eq(data.value1()))
						.fetchOne();
				
				// TODO update
				
				ProposalInfo pi = new ProposalInfo();
				pi.setQuantity(s.getQuantity());
				pi.setDomain(domainId);
				pi.setProposal(proposal);
				pi.setItem(record.value1());
				pi.setPrice(record.value2());
				pi.setStatus((byte) 0); 
				pi.setDescription("");
				pi.setDiscount((double) 0);
				if(isCatalogue(sctx,pi)){
					if(isProposal(sctx,pi)){
						pi = getProposal(sctx,pi);
						updateIds.add(pi.getId());
						proposalUpdateQuery.values(pi.getId(), pi.getDomain(), pi.getProposal(), pi.getItem(), pi.getDescription(), pi.getQuantity(), pi.getPrice(), pi.getDiscount().toString(), pi.getStatus(), null, null, null, null, null);
					}
					else
						proposalInsertQuery.values(pi.getDomain(), pi.getProposal(), pi.getItem(), pi.getDescription(), pi.getQuantity(), pi.getPrice(), pi.getDiscount().toString(), pi.getStatus(), null, null, null, null, null);
				}
				else{
					v.add("*Fila " +(s.getRow()+1) + " : El producto no existe o los detalles no coincide.");
					error.setError(false);
					error.setTextError(v);
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
	
	public static Error insertTransferStock(String domain, Integer domainId,Vector<StockInfo> stock, TransferInfo ti){
		Error error = new Error();
		error.setError(true);
		Vector<String> verror = new Vector<String>();
		verror.add("");
		error.setTextError(verror);
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain, domainId);
			
			 
			DeleteConditionStep<StockRecord> stockDeleteQuery;
			InsertValuesStep4<StockRecord, Integer, Integer, Double, Integer> stockInsertQuery = ctx.getDslContext().insertInto(STOCK, STOCK.DOMAIN, STOCK.ITEM, STOCK.QUANTITY, STOCK.WAREHOUSE);
			InsertValuesStep5<StockRecord, Integer, Integer, Integer, Double, Integer> stockUpdateQuery = ctx.getDslContext().insertInto(STOCK, STOCK.ID, STOCK.DOMAIN, STOCK.ITEM, STOCK.QUANTITY, STOCK.WAREHOUSE);
			InsertValuesStep4<WarehouseTransferDetailRecord, Integer, Integer, Integer, Double> transferInsert = ctx.getDslContext().insertInto(WAREHOUSE_TRANSFER_DETAIL, WAREHOUSE_TRANSFER_DETAIL.DOMAIN, WAREHOUSE_TRANSFER_DETAIL.ITEM, WAREHOUSE_TRANSFER_DETAIL.WAREHOUSE_TRANSFER, WAREHOUSE_TRANSFER_DETAIL.QUANTITY);

			Vector<Integer> stockDeleteIds = new Vector<Integer>();

			Date d = new Date();
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
						if(ti.getTargetWarehouse() != null) 
							data2 = sctx.getDslContext().select(STOCK.QUANTITY, STOCK.ID)
								.from(STOCK)
								.where(STOCK.ITEM.eq(itemId).and(STOCK.WAREHOUSE.eq(ti.getTargetWarehouse().getId()))).fetch();
							
						Result<Record2<Double, Integer>> data3 = null; 
						if(ti.getSourceWarehouse() != null) 
							data3= sctx.getDslContext().select(STOCK.QUANTITY, STOCK.ID)
								.from(STOCK)
								.where(STOCK.ITEM.eq(itemId).and(STOCK.WAREHOUSE.eq(ti.getSourceWarehouse().getId()))).fetch();
						
						transferInsert.values(domainId, itemId, transferId,s.getQuantity());
						Double quantity, quantity2;
						Integer stockId, stockId2;
						if(ti.getSourceWarehouse() != null && ti.getTargetWarehouse() != null ){

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
						else if(ti.getSourceWarehouse() != null && ti.getTargetWarehouse() == null){
							if(data3.isNotEmpty()){
								quantity2 = data3.get(0).value1();
								stockId2 = data3.get(0).value2();
								
								//UPDATE source
								stockDeleteIds.add(stockId2);
								stockUpdateQuery.values(stockId2, domainId, itemId, (quantity2-s.getQuantity()), ti.getSourceWarehouse().getId());
							}

						}
						else if(ti.getSourceWarehouse() == null && ti.getTargetWarehouse() != null){
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
				
				transferInsert.execute();
				
				ctx.activateForeignKeys();
			}
			return error;
			
		}finally {
			if (ctx != null) ctx.close();	
		}
	}
	
	public static Vector<StockInfo> getStocks(String domain, Integer domainId,Integer wid) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain, domainId);
			
			Result<Record5<Integer, Integer, Integer, Double,Integer>> data ;
			if(wid != null)
				data = ctx.getDslContext().select(STOCK.ID, STOCK.ITEM, STOCK.WAREHOUSE, STOCK.QUANTITY,ITEM.PRODUCT)
				.from(STOCK).join(ITEM).on(ITEM.ID.eq(STOCK.ITEM))
				.where(STOCK.DOMAIN.eq(domainId)).and(STOCK.WAREHOUSE.eq(wid)).fetch();
			else
				data = ctx.getDslContext().select(STOCK.ID, STOCK.ITEM, STOCK.WAREHOUSE, STOCK.QUANTITY,ITEM.PRODUCT)
				.from(STOCK).join(ITEM).on(ITEM.ID.eq(STOCK.ITEM))
				.where(STOCK.DOMAIN.eq(domainId)).fetch();
				
			Vector<StockInfo> v = new Vector<StockInfo>();
			for (Record5<Integer, Integer, Integer, Double, Integer> d : data) {
				StockInfo si = new StockInfo();
				Item i = getItem(ctx.getDslContext(),domain,d.value2());
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

		if(data.get(0).value1()!=null) s[0] = data.get(0).value1();
		else s[0] = "";
		if(data.get(0).value2()!=null) s[1] = data.get(0).value2();
		else s[1] = "";
			
		return s;
		
	}
	
	public static Item getItem(DSLContext dslContext, String domain, Integer id ){
		
		Result<Record5<String, String, String, String, Integer>> data = dslContext.select(ITEM.BARCODE, ITEM.DETAIL, ITEM.DETAIL2, ITEM.DETAIL3,ITEM.PRODUCT)
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
		
		return i;
		
	}
	

	public static Product getProduct(DSLContext dslContext, Integer id ){
		
		Result<Record2<String,String>> data = dslContext.select(PRODUCT.CODE,PRODUCT.NAME)
			.from(PRODUCT)
			.where(PRODUCT.ID.eq(id)).fetch();
		
		Product p = new Product();
		p.setId(id);
		p.setCode(data.get(0).value1());
		p.setName(data.get(0).value2());
		return p;
		
	}
	
	public static Boolean checkSeries(String domain, Integer domainId,Series s, Warehouse w, Warehouse w2){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain, domainId);
			
			Result<Record1< Integer>> data = ctx.getDslContext().select(SERIES.ID)
				.from(WAREHOUSE)
					.join(WORKPLACE).on(WAREHOUSE.WORKPLACE.eq(WORKPLACE.ID))
					.join(SERIES).on(SERIES.SCOPE.eq(WORKPLACE.SCOPE))
				.where(SERIES.ID.eq(s.getId()).and(WAREHOUSE.ID.eq(w.getId()))).fetch();
			
			Result<Record1< Integer>> data2 = ctx.getDslContext().select(SERIES.ID)
					.from(WAREHOUSE)
						.join(WORKPLACE).on(WAREHOUSE.WORKPLACE.eq(WORKPLACE.ID))
						.join(SERIES).on(SERIES.SCOPE.eq(WORKPLACE.SCOPE))
					.where(SERIES.ID.eq(s.getId()).and(WAREHOUSE.ID.eq(w2.getId()))).fetch();
			
			return data.isNotEmpty() || data2.isNotEmpty();

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
	
	public static Warehouse getWarehouse(String warehouse, Integer domainId,String domain){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain, domainId);
			
			Result<Record3< Integer, String,Integer>> data = ctx.getDslContext().select(WAREHOUSE.ID,WAREHOUSE.NAME,WAREHOUSE.WORKPLACE)
				.from(WAREHOUSE)
				.where(WAREHOUSE.NAME.eq(warehouse)).and(WAREHOUSE.DOMAIN.eq(domainId)).fetch();
			
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
	
	public static Vector<Warehouse> getWarehouse(String domain,Integer domainId){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain, domainId);
			
			Result<Record3< Integer, String,Integer>> data = ctx.getDslContext().select(WAREHOUSE.ID,WAREHOUSE.NAME,WAREHOUSE.WORKPLACE)
				.from(WAREHOUSE)
				.where(WAREHOUSE.DOMAIN.eq(domainId)).fetch();
			
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
								.where(PROPOSAL_DETAIL.PROPOSAL.eq(proposalId))
								.and(PROPOSAL_DETAIL.DOMAIN.eq(domainId))
								.fetch();
			
			Vector<StockInfo> v = new Vector<StockInfo>();
			
			for(Record16<Integer, Integer, Integer, Integer, Integer, Integer, String, Double, Double, String, Byte, Integer, String, Timestamp, String, Timestamp> r : data){
				StockInfo si  = new StockInfo();
				WorkPlace wp = DBCatalogue.getWorkplace(r.value1(), domainId, domain);
				Department d = DBCatalogue.getDepartment(wp, r.value2(), domainId, domain);
				Integer productId = ctx.getDslContext().select(ITEM.PRODUCT).from(ITEM).where(ITEM.ID.eq(r.value6())).fetchOne().value1();
				com.esferalia.aon.occam.api.model.product.Product p = AON.getProduct(ctx, productId);
				si.setDepartmentStr(d.getName());
				si.setWorkplaceStr(wp.getDescription());
				si.setDomainId(domainId);
				si.setDetail("");
				si.setDetail2("");
				si.setDetail3("");
				si.setItemId(r.value6());
				si.setQuantity(r.value8());
				si.setProductName(p.getName());
				si.setProduct(p.getCode());
				System.out.println(si.getProductName());
				v.add(si);
			}
			return v;
		} finally {
			if (ctx != null) ctx.close();
		}
	}
}
