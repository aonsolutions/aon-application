package com.esferalia.aon.gwt.template.jooq;

import static com.esferalia.aon.jooq.tables.Customer.CUSTOMER;
import static com.esferalia.aon.jooq.tables.CustomerFee.CUSTOMER_FEE;
import static com.esferalia.aon.jooq.tables.Item.ITEM;
import static com.esferalia.aon.jooq.tables.Product.PRODUCT;
import static com.esferalia.aon.jooq.tables.Project.PROJECT;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Seller.SELLER;
import static com.esferalia.aon.jooq.tables.WarehouseTransfer.WAREHOUSE_TRANSFER;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;
import static com.esferalia.aon.jooq.tables.InvoicingGroup.INVOICING_GROUP;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.InsertValuesStep17;
import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.Record3;
import org.jooq.Record4;
import org.jooq.Result;
import org.jooq.impl.DSL;

import com.code.aon.company.WorkPlace;
import com.code.aon.customer.Customer;
import com.code.aon.customer.InvoicingGroup;
import com.code.aon.google.apis.DatabaseSync;
import com.code.aon.google.apis.jooq.JooqSettings;
import com.code.aon.project.Project;
import com.code.aon.registry.Registry;
import com.esferalia.aon.gwt.template.server.FeeInfo;
import com.esferalia.aon.gwt.template.shared.Error;
import com.esferalia.aon.jooq.tables.records.CustomerFeeRecord;
import com.esferalia.aon.occam.api.model.registry.Seller;

public class DBFee {
	private static final String SET_FOREIGN_KEY_CHECKS_0 = "SET FOREIGN_KEY_CHECKS=0;";
	private static final String SET_FOREIGN_KEY_CHECKS_1 = "SET FOREIGN_KEY_CHECKS=1;";

	
	static Integer domainIdFee;
	public static Error insertFee(String domain, Integer domainId, Vector<FeeInfo> fees)throws SQLException{
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
			Vector<String> v = new Vector<String>();
			InsertValuesStep17<CustomerFeeRecord, Integer, Integer, Integer, Short, Integer, String, Double, Double, String, java.sql.Date, java.sql.Date, java.sql.Date, Short, Byte, Integer, Integer, Integer> customerFeeInsertQuery = dslContext.insertInto(CUSTOMER_FEE, CUSTOMER_FEE.DOMAIN, CUSTOMER_FEE.PROJECT, CUSTOMER_FEE.CUSTOMER, CUSTOMER_FEE.LINE, CUSTOMER_FEE.ITEM, CUSTOMER_FEE.DESCRIPTION, CUSTOMER_FEE.QUANTITY, CUSTOMER_FEE.PRICE, CUSTOMER_FEE.DISCOUNT_EXPR, CUSTOMER_FEE.INITIAL_DATE, CUSTOMER_FEE.FINAL_DATE, CUSTOMER_FEE.BILLING_DATE, CUSTOMER_FEE.PERIOD, CUSTOMER_FEE.SECURITY_LEVEL, CUSTOMER_FEE.INVOICING_GROUP, CUSTOMER_FEE.SELLER, CUSTOMER_FEE.WORKPLACE);
			
			domainIdFee = domainId;
			fees.stream().forEach(s ->{	
				if(s.getProduct() != null){
					Result<Record1< Integer>> data = dslContext.select(ITEM.ID)
						.from(ITEM)
						.where(ITEM.BARCODE.eq(s.getProduct())).and(ITEM.DOMAIN.eq(domainIdFee)).fetch();
					if(data.isEmpty()){

						data = dslContext.select(ITEM.ID)
								.from(ITEM).join(PRODUCT).on(PRODUCT.ID.eq(ITEM.PRODUCT))
								.where(PRODUCT.CODE.eq(s.getProduct()))
										.and(PRODUCT.DOMAIN.eq(domainIdFee)).fetch();
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
									.and(PRODUCT.DOMAIN.eq(domainIdFee)).fetch();
					}


					if(!data.isEmpty()){

						Integer confidential;
						if (s.getConfidential()) confidential = 1;
						else confidential = 0;
						
	
						Integer itemId = data.get(0).value1();
						
						java.sql.Date t = null;
						if(s.getEndDate() != null ) new java.sql.Date(s.getEndDate().getTime());
						
						Short line;
						if(s.getLine() == null){
							Result<Record1<Short>> n = dslContext.select(DSL.max(CUSTOMER_FEE.LINE))
								.from(CUSTOMER_FEE)
								.where(CUSTOMER_FEE.DOMAIN.eq(domainId).and(CUSTOMER_FEE.CUSTOMER.eq(s.getClientId()))).fetch();
							
							if(n.isEmpty() || n.get(0).value1()==null) line = 1;
							else line = n.get(0).value1(); //get max number (domain, customer)
							line++;
						}
						else{

							line = s.getLine().shortValue();
							dslContext.update(CUSTOMER_FEE).set(CUSTOMER_FEE.LINE, CUSTOMER_FEE.LINE.add(1))
									.where(CUSTOMER_FEE.DOMAIN.eq(domainId)).and(CUSTOMER_FEE.CUSTOMER.eq(s.getClientId()))
									.and(CUSTOMER_FEE.LINE.greaterThan(line));
							
						}
						
	
						Short period = s.getPeriod().shortValue();
						customerFeeInsertQuery.values(domainIdFee, s.getProjectId(), s.getClientId(),line, itemId, s.getDescription(), s.getQuantity(), s.getPrice(), s.getDiscount().toString(), new java.sql.Date(s.getStartDate().getTime()), t , new java.sql.Date(s.getBillingDate().getTime()),period, confidential.byteValue(), s.getBillingGroup(), s.getSellerId(),s.getWorkplaceId());
					}
					else{
						v.add("*Fila " +(s.getRow()+1) + " : El producto no existe o los detalles no coincide.");
						error.setError(false);
						error.setTextError(v);
					}
				}
			});
	
			if(error.getError()){
				Statement sOpen = connection.createStatement();
				sOpen.execute(SET_FOREIGN_KEY_CHECKS_0);
				System.out.println("Claves referenciales desactivadas");
				
				customerFeeInsertQuery.execute();
				
				Statement sClose = connection.createStatement();
				sClose.execute(SET_FOREIGN_KEY_CHECKS_1);
				System.out.println("Claves referenciales activadas");
			}
			return error;
			
		}finally {
			if (connection != null)
				connection.close();
		}
	}
	
	public static Vector<Customer> getCustomers(String domain,Integer domainId) throws SQLException{
		Connection connection = null;
		try {
			connection = DatabaseSync.getConnection(domain);
			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());

			Result<Record4<Integer, String, String, String>> data = dslContext.select(REGISTRY.ID, REGISTRY.ALIAS, REGISTRY.DOCUMENT, REGISTRY.NAME)
					.from(REGISTRY).join(CUSTOMER).on(REGISTRY.ID.eq(CUSTOMER.REGISTRY))
					.where(CUSTOMER.DOMAIN.eq(domainId))
					.fetch();
			
	

			Vector<Customer> v = new Vector<Customer>();

			for (Record4<Integer, String,String,String> r : data) {
				Customer customer = new Customer();
				customer.setId(r.value1());
				Registry registry = new Registry();
				registry.setAlias(r.value2());
				registry.setDocument(r.value3());
				registry.setName(r.value4());
				customer.setRegistry(registry);

				v.add(customer);
			}

			return v;

		} finally {
			if (connection != null)
				connection.close();
		}
	}
	
	
	
	public static Vector<Seller> getSellers(String domain, Integer domainId)
			throws SQLException {
		Connection connection = null;
		try {
			connection = DatabaseSync.getConnection(domain);
			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());

			Result<Record4<Integer, String, String, String>> data = dslContext.select(REGISTRY.ID,REGISTRY.ALIAS,REGISTRY.NAME, REGISTRY.DOCUMENT)
					.from(REGISTRY).join(SELLER).on(REGISTRY.ID.eq(SELLER.REGISTRY))
					.where(SELLER.DOMAIN.eq(domainId))
					.fetch();
			
	

			Vector<Seller> v = new Vector<Seller>();

			for (Record4<Integer, String, String, String> r : data) {
				Seller seller = new Seller();
				seller.setId(r.value1());
				seller.setRegistryAlias(r.value2());
				seller.setRegistryName(r.value3());
				seller.setRegistryDocument(r.value4());
				v.add(seller);
			}

			return v;

		} finally {
			if (connection != null)
				connection.close();
		}
	}

	public static Vector<Project> getProjects(String domain,Integer domainId) throws SQLException{
		Connection connection = null;
		try {
			connection = DatabaseSync.getConnection(domain);
			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());

			Result<Record3<Integer, String, String>> data = dslContext.select(PROJECT.ID,PROJECT.ALIAS,PROJECT.NAME)
					.from(PROJECT)
					.where(PROJECT.DOMAIN.eq(domainId))
					.fetch();
			
	

			Vector<Project> v = new Vector<Project>();

			for (Record3<Integer, String, String> r : data) {
				Project project = new Project();
				project.setId(r.value1());
				project.setAlias(r.value2());
				project.setName(r.value3());
				v.add(project);
			}

			return v;

		} finally {
			if (connection != null)
				connection.close();
		}
	}


	public static Vector<WorkPlace> getWorkplaces(String domain,Integer domainId) throws SQLException{
		Connection connection = null;
		try {
			connection = DatabaseSync.getConnection(domain);
			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());

			Result<Record2<Integer, String>> data = dslContext.select(WORKPLACE.ID,WORKPLACE.DESCRIPTION)
					.from(WORKPLACE)
					.where(WORKPLACE.DOMAIN.eq(domainId))
					.fetch();
			
	

			Vector<WorkPlace> v = new Vector<WorkPlace>();

			for (Record2<Integer, String> r : data) {
				WorkPlace workplace = new WorkPlace();
				workplace.setId(r.value1());
				workplace.setDescription(r.value2());

				v.add(workplace);
			}

			return v;

		} finally {
			if (connection != null)
				connection.close();
		}
	}
	
	public static Vector<InvoicingGroup> getInvoicingGroups(String domain,Integer domainId) throws SQLException{
		Connection connection = null;
		try {
			connection = DatabaseSync.getConnection(domain);
			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());

			Result<Record2<Integer, String>> data = dslContext.select(INVOICING_GROUP.ID,INVOICING_GROUP.DESCRIPTION)
					.from(INVOICING_GROUP)
					.where(INVOICING_GROUP.DOMAIN.eq(domainId))
					.fetch();
			
	

			Vector<InvoicingGroup> v = new Vector<InvoicingGroup>();

			for (Record2<Integer, String> r : data) {
				InvoicingGroup ig = new InvoicingGroup();
				ig.setId(r.value1());
				ig.setDescription(r.value2());

				v.add(ig);
			}

			return v;

		} finally {
			if (connection != null)
				connection.close();
		}
	}
}
