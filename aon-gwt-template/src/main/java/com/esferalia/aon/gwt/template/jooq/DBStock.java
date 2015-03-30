package com.esferalia.aon.gwt.template.jooq;

import static com.esferalia.aon.jooq.tables.Item.ITEM;
import static com.esferalia.aon.jooq.tables.Product.PRODUCT;
import static com.esferalia.aon.jooq.tables.Series.SERIES;
import static com.esferalia.aon.jooq.tables.Stock.STOCK;
import static com.esferalia.aon.jooq.tables.Warehouse.WAREHOUSE;
import static com.esferalia.aon.jooq.tables.WarehouseTransfer.WAREHOUSE_TRANSFER;
import static com.esferalia.aon.jooq.tables.WarehouseTransferDetail.WAREHOUSE_TRANSFER_DETAIL;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Vector;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.InsertValuesStep4;
import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.Record3;
import org.jooq.Record5;
import org.jooq.Result;
import org.jooq.impl.DSL;

import com.code.aon.config.Series;
import com.code.aon.google.apis.DatabaseSync;
import com.code.aon.google.apis.jooq.JooqSettings;
import com.code.aon.product.Item;
import com.code.aon.product.Product;
import com.esferalia.aon.gwt.template.server.StockInfo;
import com.esferalia.aon.gwt.template.server.TransferInfo;
import com.esferalia.aon.gwt.template.shared.Error;
import com.esferalia.aon.gwt.template.shared.Warehouse;
import com.esferalia.aon.jooq.tables.records.WarehouseTransferDetailRecord;

public class DBStock {

	
	static InsertValuesStep4<WarehouseTransferDetailRecord, Integer, Integer, Integer, Double> transferInsert;
	static String stockquery ;
	public class ImportStockThread extends Thread{
		private final String domain;
		private final Vector<StockInfo> stock;
		private final Integer warehouse;
		Integer domainId;
		public ImportStockThread(String domain, Vector<StockInfo> stock, Integer warehouse) {
			this.stock = stock;
			this.domain = domain;
			this.warehouse = warehouse;
		}
	
		@Override
		public void run() {
			long startAll2= System.currentTimeMillis();

			Connection connection2 = null;
			try{
				connection2 = DatabaseSync.getConnection(domain);
				DSLContext dslContext = DSL.using(connection2,
						JooqSettings.getDefaultSettings());
				transferInsert = dslContext.insertInto(WAREHOUSE_TRANSFER_DETAIL, WAREHOUSE_TRANSFER_DETAIL.DOMAIN, WAREHOUSE_TRANSFER_DETAIL.ITEM, WAREHOUSE_TRANSFER_DETAIL.WAREHOUSE_TRANSFER, WAREHOUSE_TRANSFER_DETAIL.QUANTITY);
				//for(StockInfo stockInfo :stock){
				stockquery = "update stock set quantity = case ";
				stock.stream().forEach(stockInfo -> {
					domainId = stockInfo.getDomainId();
					if(stockInfo.getProduct() != null){
						transferInsert.values(stockInfo.getDomainId(),stockInfo.getItemId(),stockInfo.getTransferId(),stockInfo.getQuantityDifference());	
						
						if(stockInfo.getQuantityDifference() != 0.0)
							stockquery = stockquery + " when item = "+ stockInfo.getItemId()+" then "+ stockInfo.getQuantity(); //+ ";";
					}
				});
				stockquery = stockquery + " else " + 0.0 + " end where domain = "+ domainId +";";
				dslContext.query(stockquery).execute();
				transferInsert.execute();
				//}
			} catch (SQLException e) {
				e.printStackTrace();
			}finally {
				if (connection2 != null)
					try {
						connection2.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
			}
			long timeAll2 = System.currentTimeMillis() - startAll2;
			
			System.out.println("TIME RUN    " + (timeAll2/1000d));
		}
	}
	
	public static Error insertFee(){
		//TODO
		return null;
	}
	
	
	Vector<String> v = new Vector<String>();
	static String itemIds;
	public static Error insertStock2(String domain, Integer domainId,Vector<StockInfo> stock, TransferInfo ti) throws SQLException {
		itemIds ="";
		Error error = new Error();
		error.setError(true);
		Vector<String> verror = new Vector<String>();
		verror.add("");
		error.setTextError(verror);
		Connection connection = null;
		try {
			connection = DatabaseSync.getConnection(domain);
			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
			 
			
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
			Result<Record1<Integer>> n = dslContext.select(DSL.max(WAREHOUSE_TRANSFER.NUMBER))
				.from(WAREHOUSE_TRANSFER)
				.where(WAREHOUSE_TRANSFER.DOMAIN.eq(domainId).and(series)).fetch();
			
			Integer max;
			if(n.isEmpty() || n.get(0).value1()==null) max = 0;
			else max = n.get(0).value1(); //get max number (domain, serie)
			Integer next = max+1;
			
			Integer transferId = dslContext.insertInto(WAREHOUSE_TRANSFER,WAREHOUSE_TRANSFER.DOMAIN, WAREHOUSE_TRANSFER.SERIES,WAREHOUSE_TRANSFER.NUMBER, WAREHOUSE_TRANSFER.COMMENTS, WAREHOUSE_TRANSFER.ISSUE_TIME, WAREHOUSE_TRANSFER.SOURCE_WAREHOUSE, WAREHOUSE_TRANSFER.TARGET_WAREHOUSE)
					.values(domainId,scode,next,ti.getComments(),t,null,ti.getTargetWarehouse().getId()).returning(WAREHOUSE_TRANSFER.ID).fetchOne().getId();
			Vector<String> v = new Vector<String>();
			transferInsert = dslContext.insertInto(WAREHOUSE_TRANSFER_DETAIL, WAREHOUSE_TRANSFER_DETAIL.DOMAIN, WAREHOUSE_TRANSFER_DETAIL.ITEM, WAREHOUSE_TRANSFER_DETAIL.WAREHOUSE_TRANSFER, WAREHOUSE_TRANSFER_DETAIL.QUANTITY);
			stockquery = "update stock set quantity = case ";
			stock.stream().forEach(s ->{
				if(s.getProduct() != null){
					Result<Record1< Integer>> data = dslContext.select(ITEM.ID)
						.from(ITEM)
						.where(ITEM.BARCODE.eq(s.getProduct())).and(ITEM.DOMAIN.eq(domainId)).fetch();
					if(data.isEmpty()){

						data = dslContext.select(ITEM.ID)
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
						
						data = dslContext.select(ITEM.ID)
								.from(ITEM).join(PRODUCT).on(PRODUCT.ID.eq(ITEM.PRODUCT))
								.where(PRODUCT.CODE.eq(s.getProduct()))
									.and(detail)
									.and(detail2)
									.and(detail3)
									.and(PRODUCT.DOMAIN.eq(domainId)).fetch();
					}
					if(!data.isEmpty()){
						Integer itemId = data.get(0).value1();
						Result<Record1<Double>> data2 = dslContext.select(STOCK.QUANTITY)
							.from(STOCK)
							.where(STOCK.ITEM.eq(itemId).and(STOCK.WAREHOUSE.eq(ti.getTargetWarehouse().getId()))).fetch();
						Double quantity;
						if(!data2.isEmpty()){
							 
							quantity = data2.get(0).value1();
							//System.out.println(" New Quantity: "+ s.getQuantity() +" ; code:  "+s.getProduct());
							//System.out.println(" Old Quantity: "+ quantity + " or " + Math.abs(quantity));

							Double quantityTransfer = s.getQuantity()-quantity;
							
							s.setDomainId(domainId);
							s.setItemId(itemId);
							s.setTransferId(transferId);
							s.setQuantityDifference(quantityTransfer);
							
							transferInsert.values(s.getDomainId(),s.getItemId(),s.getTransferId(),s.getQuantityDifference());	

							if(quantityTransfer != 0.0){
								itemIds = itemIds + ","+s.getItemId();
								stockquery = stockquery + " when item = "+ s.getItemId()+" then "+ s.getQuantity();
							
							}
							/*if(s.getQuantityDifference() != 0.0)
								dslContext.update(STOCK)
									.set(STOCK.QUANTITY,s.getQuantity())
									.where(STOCK.ITEM.eq(s.getItemId()).and(STOCK.WAREHOUSE.eq(ti.getTargetWarehouse().getId()))).execute();
							*/
						}
						else{
							
							v.add("*Fila " +s.getRow() + " : El producto no está en stock.");
							error.setError(false);
							error.setTextError(v);
							//return error;
						}
						
					}
					else{
						v.add("*Fila " +s.getRow() + " : El producto no existe o los detalles no coincide.");
						error.setError(false);
						error.setTextError(v);
						
						//return error;
					}
				}
			});
	
			if(error.getError()){
				if(!stockquery.equals("update stock set quantity = case ")){
					stockquery = stockquery + " else " + 0.0 + " end where domain = "+ domainId +" and item in ("+ itemIds.substring(1) +");";
					dslContext.query(stockquery).execute();
				}
				transferInsert.execute();
				/*DBConsults outer = new DBConsults();
				ImportStockThread thread = outer.new ImportStockThread(domain, stock, ti.getTargetWarehouse().getId());
				thread.start();*/
			}
			return error;
			
		}finally {
			if (connection != null)
				connection.close();
		}
	}
	
	public static Vector<StockInfo> getStocks(String domain, Integer domainId,Integer wid) throws SQLException{
		Connection connection = null;
		try {
			connection = DatabaseSync.getConnection(domain);
			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
			Result<Record5<Integer, Integer, Integer, Double,Integer>> data ;
			if(wid != null)
				data = dslContext.select(STOCK.ID, STOCK.ITEM, STOCK.WAREHOUSE, STOCK.QUANTITY,ITEM.PRODUCT)
				.from(STOCK).join(ITEM).on(ITEM.ID.eq(STOCK.ITEM))
				.where(STOCK.DOMAIN.eq(domainId)).and(STOCK.WAREHOUSE.eq(wid)).fetch();
			else
				data = dslContext.select(STOCK.ID, STOCK.ITEM, STOCK.WAREHOUSE, STOCK.QUANTITY,ITEM.PRODUCT)
				.from(STOCK).join(ITEM).on(ITEM.ID.eq(STOCK.ITEM))
				.where(STOCK.DOMAIN.eq(domainId)).fetch();
				
			Vector<StockInfo> v = new Vector<StockInfo>();
			for (Record5<Integer, Integer, Integer, Double, Integer> d : data) {
				StockInfo si = new StockInfo();
				Item i = getItem(dslContext,domain,d.value2());
				si.setDetail(i.getDetail());
				si.setDetail2(i.getDetail2());
				si.setDetail3(i.getDetail3());
				if(i.getBarcode()!= null) si.setProduct(i.getBarcode());
				else {
					si.setProduct(i.getProduct().getCode());
				}
				si.setQuantity(d.value4());
				String[] s = getWarehouseComments(dslContext, d.value3());
				Series ss = new Series();ss.setCode(s[1]);
				//si.setSeries(ss);
				//si.setComments(s[0]);
				//si.setTargetWarehouse(getWarehouse(dslContext, d.value3()));
				si.setProductId(d.value5());
				v.add(si);
			}
			return v;
		} finally {
			if (connection != null)
				connection.close();
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
		
		Result<Record1<String>> data = dslContext.select(PRODUCT.CODE)
			.from(PRODUCT)
			.where(PRODUCT.ID.eq(id)).fetch();
		
		Product p = new Product();
		p.setId(id);
		p.setCode(data.get(0).value1());
		return p;
		
	}
	
	public static Boolean checkSeries(String domain, Integer domainId,Series s, Warehouse w) throws SQLException{
		Connection connection = null;
		try {
			connection = DatabaseSync.getConnection(domain);
			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
			
			Result<Record1< Integer>> data = dslContext.select(SERIES.ID)
				.from(WAREHOUSE)
					.join(WORKPLACE).on(WAREHOUSE.WORKPLACE.eq(WORKPLACE.ID))
					.join(SERIES).on(SERIES.SCOPE.eq(WORKPLACE.SCOPE))
				.where(SERIES.ID.eq(s.getId()).and(WAREHOUSE.ID.eq(w.getId()))).fetch();
			
			return !data.isEmpty();

		} finally {
		if (connection != null)
			connection.close();
		}
	}
	
	public static Series getSeries(String domain,Integer domainId, String serie) throws SQLException{
		Connection connection = null;
		try {
			connection = DatabaseSync.getConnection(domain);
			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
			
			Result<Record3< Integer, String,String>> data = dslContext.select(SERIES.ID,SERIES.CODE,SERIES.DESCRIPTION)
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
		if (connection != null)
			connection.close();
		}
	}
	
	public static Vector<Series> getSeries(String domain,Integer domainId) throws SQLException{
		Connection connection = null;
		try {
			connection = DatabaseSync.getConnection(domain);
			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
			
			Result<Record3< Integer, String,String>> data = dslContext.select(SERIES.ID,SERIES.CODE,SERIES.DESCRIPTION)
				.from(SERIES)
				.where(SERIES.DOMAIN.eq(domainId)).fetch();
			
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
		if (connection != null)
			connection.close();
		}
	}
	
	public static Warehouse getWarehouse(String warehouse, Integer domainId,String domain) throws SQLException{
		Connection connection = null;
		try {
			connection = DatabaseSync.getConnection(domain);
			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
			
			Result<Record3< Integer, String,Integer>> data = dslContext.select(WAREHOUSE.ID,WAREHOUSE.NAME,WAREHOUSE.WORKPLACE)
				.from(WAREHOUSE)
				.where(WAREHOUSE.NAME.eq(warehouse)).and(WAREHOUSE.DOMAIN.eq(domainId)).fetch();
			
			Warehouse w = new Warehouse();
			
			for(Record3<Integer, String,Integer> r : data){
				
				w.setDomainId(domainId);
				w.setId(r.value1());
				w.setName(r.value2());
				w.setWorkplace(0);//
		
			}
			return w;

		} finally {
		if (connection != null)
			connection.close();
		}
	}
	
	public static Vector<Warehouse> getWarehouse(String domain,Integer domainId) throws SQLException{
		Connection connection = null;
		try {
			connection = DatabaseSync.getConnection(domain);
			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
			
			Result<Record3< Integer, String,Integer>> data = dslContext.select(WAREHOUSE.ID,WAREHOUSE.NAME,WAREHOUSE.WORKPLACE)
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
		if (connection != null)
			connection.close();
		}
	}
}
