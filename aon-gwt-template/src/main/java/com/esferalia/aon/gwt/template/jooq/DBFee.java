package com.esferalia.aon.gwt.template.jooq;

import static com.esferalia.aon.jooq.tables.Customer.CUSTOMER;
import static com.esferalia.aon.jooq.tables.CustomerFee.CUSTOMER_FEE;
import static com.esferalia.aon.jooq.tables.Item.ITEM;
import static com.esferalia.aon.jooq.tables.Product.PRODUCT;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Seller.SELLER;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;

import java.sql.Date;
import java.util.HashMap;
import java.util.LinkedList;

import org.jooq.Condition;
import org.jooq.InsertValuesStep17;
import org.jooq.Record1;
import org.jooq.Record4;
import org.jooq.Record5;
import org.jooq.Result;
import org.jooq.impl.DSL;

import com.esferalia.aon.gwt.template.server.FeeInfo;
import com.esferalia.aon.gwt.template.shared.Error;
import com.esferalia.aon.gwt.template.shared.Seller;
import com.esferalia.aon.jooq.tables.records.CustomerFeeRecord;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.finance.InvoicingGroup;
import com.esferalia.aon.occam.api.model.registry.Project;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.RegistryStatus;
import com.esferalia.aon.occam.api.model.type.SellerStatus;

public class DBFee {

	public static DBFee getInstance() {
		return new DBFee();
	}
				
	public Error insertFee(Domain domain, String login, LinkedList<FeeInfo> fees ){
		Error error = new Error();
		error.setError(true);
		LinkedList<String> verror = new LinkedList<String>();
		verror.add("");
		error.setTextError(verror);
		AONContext ctx = null;
		
		try {
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login);
			
			LinkedList<String> v = new LinkedList<String>();
			InsertValuesStep17<CustomerFeeRecord, Integer, Integer, Integer, Short, Integer, String, Double, Double, String, java.sql.Date, java.sql.Date, java.sql.Date, Short, Byte, Integer, Integer, Integer> customerFeeInsertQuery = ctx.getDslContext().insertInto(CUSTOMER_FEE, CUSTOMER_FEE.DOMAIN, CUSTOMER_FEE.PROJECT, CUSTOMER_FEE.CUSTOMER, CUSTOMER_FEE.LINE, CUSTOMER_FEE.ITEM, CUSTOMER_FEE.DESCRIPTION, CUSTOMER_FEE.QUANTITY, CUSTOMER_FEE.PRICE, CUSTOMER_FEE.DISCOUNT_EXPR, CUSTOMER_FEE.INITIAL_DATE, CUSTOMER_FEE.FINAL_DATE, CUSTOMER_FEE.BILLING_DATE, CUSTOMER_FEE.PERIOD, CUSTOMER_FEE.SECURITY_LEVEL, CUSTOMER_FEE.INVOICING_GROUP, CUSTOMER_FEE.SELLER, CUSTOMER_FEE.WORKPLACE);
			
			AONContext sctx = ctx;
			HashMap<Integer, Short> lineMap = new HashMap<>();
			fees.stream().forEach(s ->{	
				if(s.getProduct() != null){
					Result<Record1< Integer>> data = sctx.getDslContext().select(ITEM.ID)
						.from(ITEM)
						.where(ITEM.BARCODE.eq(s.getProduct())).and(ITEM.DOMAIN.eq(domain.getId())).fetch();
					if(data.isEmpty()){

						data = sctx.getDslContext().select(ITEM.ID)
								.from(ITEM).join(PRODUCT).on(PRODUCT.ID.eq(ITEM.PRODUCT))
								.where(PRODUCT.CODE.eq(s.getProduct()))
										.and(PRODUCT.DOMAIN.eq(domain.getId())).fetch();
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
									.and(PRODUCT.DOMAIN.eq(domain.getId())).fetch();
					}
					if(!data.isEmpty()){
						Integer confidential = s.getConfidential() ? 1 : 0;	
						Integer itemId = data.get(0).value1();	
						if(s.getEndDate() != null ) new java.sql.Date(s.getEndDate().getTime());
						
						Short line = 0;
						if(s.getLine() == null){
							if(lineMap.containsKey(s.getClientId())) {
								line = (short) (lineMap.get(s.getClientId()) + 1);
							}else {
								Result<Record1<Short>> n = sctx.getDslContext().select(DSL.max(CUSTOMER_FEE.LINE))
										.from(CUSTOMER_FEE)
										.where(CUSTOMER_FEE.DOMAIN.eq(domain.getId()).and(CUSTOMER_FEE.CUSTOMER.eq(s.getClientId()))).fetch();
								line = (n.isEmpty() || n.get(0).value1()==null) ? (short) 1 :  (short) (n.get(0).value1() + 1);
							}
							lineMap.put(s.getClientId(), line);
						} else{
							line = s.getLine().shortValue();
							sctx.getDslContext().update(CUSTOMER_FEE).set(CUSTOMER_FEE.LINE, CUSTOMER_FEE.LINE.add(1))
									.where(CUSTOMER_FEE.DOMAIN.eq(domain.getId())).and(CUSTOMER_FEE.CUSTOMER.eq(s.getClientId()))
									.and(CUSTOMER_FEE.LINE.greaterThan(line));
							
						}
						Integer w = sctx.getDslContext().select(WORKPLACE.ID)
								.from(WORKPLACE)
								.where(WORKPLACE.DOMAIN.eq(domain.getId())).limit(1).fetchOne().value1();
						if(s.getWorkplaceId() == null) s.setWorkplaceId(w);
						Short period = s.getPeriod().shortValue();
						
						Date endDate = null;
						if(s.getEndDate() != null) endDate =  new java.sql.Date(s.getEndDate().getTime());
						
						customerFeeInsertQuery.values(domain.getId(), s.getProjectId(), s.getClientId(),line, itemId, s.getDescription(), s.getQuantity(), s.getPrice(), s.getDiscount().toString(), new java.sql.Date(s.getStartDate().getTime()), endDate, new java.sql.Date(s.getBillingDate().getTime()),period, confidential.byteValue(), s.getBillingGroup(), s.getSellerId(),s.getWorkplaceId());
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
				customerFeeInsertQuery.execute();
				ctx.activateForeignKeys();
			}
			return error;
			
		} finally {
				if (ctx != null) ctx.close();
		}
	}
	
	public Customer getCustomer(Domain domain, String login, String client, Boolean ignoreInactiveClient){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login);
			Result<Record5<Integer, String, String, String, Byte>> result = null;
			if(ignoreInactiveClient){
				result = ctx.getDslContext().select(REGISTRY.ID, REGISTRY.ALIAS,
						REGISTRY.DOCUMENT, REGISTRY.NAME,
						CUSTOMER.STATUS)
								.from(REGISTRY).join(CUSTOMER).on(REGISTRY.ID.eq(CUSTOMER.REGISTRY))
								.where(REGISTRY.DOCUMENT.eq(client))
								.and(REGISTRY.DOMAIN.eq(domain.getId()))
								.and(CUSTOMER.STATUS.eq((byte) 0))
								.fetch();
				if(result.isEmpty()){
					result = ctx.getDslContext().select(REGISTRY.ID, REGISTRY.ALIAS,
							REGISTRY.DOCUMENT, REGISTRY.NAME,
							CUSTOMER.STATUS)
									.from(REGISTRY).join(CUSTOMER).on(REGISTRY.ID.eq(CUSTOMER.REGISTRY))
									.where(REGISTRY.NAME.eq(client))
									.and(REGISTRY.DOMAIN.eq(domain.getId()))
									.and(CUSTOMER.STATUS.eq((byte) 0))
									.fetch();
				}
			}
			else{
				result = ctx.getDslContext().select(REGISTRY.ID, REGISTRY.ALIAS,
						REGISTRY.DOCUMENT, REGISTRY.NAME,
						CUSTOMER.STATUS)
								.from(REGISTRY).join(CUSTOMER).on(REGISTRY.ID.eq(CUSTOMER.REGISTRY))
								.where(REGISTRY.DOCUMENT.eq(client))
								.and(REGISTRY.DOMAIN.eq(domain.getId()))
								.fetch();
				if(result.isEmpty()){
					result = ctx.getDslContext().select(REGISTRY.ID, REGISTRY.ALIAS,
							REGISTRY.DOCUMENT, REGISTRY.NAME,
							CUSTOMER.STATUS)
									.from(REGISTRY).join(CUSTOMER).on(REGISTRY.ID.eq(CUSTOMER.REGISTRY))
									.where(REGISTRY.NAME.eq(client))
									.and(REGISTRY.DOMAIN.eq(domain.getId()))
									.fetch();
				}
			}

			if(result.isNotEmpty()){
				Customer customer = new Customer();
				Integer index = 0;
				if(result.size() > 1){
					while(index < result.size()-1 && result.get(index).value5() != 0){
						index++;
					}
				}
				
				customer.setId(result.get(index).getValue(REGISTRY.ID));
				Registry registry = new Registry()
						.setId(result.get(index).getValue(REGISTRY.ID))
						.setAlias(result.get(index).getValue(REGISTRY.ALIAS))
						.setDocument(result.get(index).getValue(REGISTRY.DOCUMENT))
						.setName(result.get(index).getValue(REGISTRY.NAME));
				customer.copy(registry);
				customer.setStatus(RegistryStatus.values()[result.get(index).value5()]);
			
				return customer;
			}
			return null;
		}finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public LinkedList<Customer> getCustomers(Domain domain, String login) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login);

			Result<Record4<Integer, String, String, String>> data = ctx.getDslContext().select(REGISTRY.ID, REGISTRY.ALIAS, REGISTRY.DOCUMENT, REGISTRY.NAME)
					.from(REGISTRY).join(CUSTOMER).on(REGISTRY.ID.eq(CUSTOMER.REGISTRY))
					.where(CUSTOMER.DOMAIN.eq(domain.getId()))
					.fetch();
			
	

			LinkedList<Customer> v = new LinkedList<Customer>();

			for (Record4<Integer, String,String,String> r : data) {
				Customer customer = new Customer();
				Registry registry = new Registry();
				registry.setId(r.value1());
				registry.setAlias(r.value2());
				registry.setDocument(r.value3());
				registry.setName(r.value4());
				customer.copy(registry);
				v.add(customer);
			}

			return v;

		} finally {
			if (ctx != null) ctx.close();
		}
	}
	
	
	
	public LinkedList<Seller> getSellers(Domain domain,  String login){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login);

			Result<Record4<Integer, String, String, String>> data = ctx.getDslContext().select(REGISTRY.ID,REGISTRY.ALIAS,REGISTRY.NAME, REGISTRY.DOCUMENT)
					.from(REGISTRY).join(SELLER).on(REGISTRY.ID.eq(SELLER.REGISTRY))
					.where(SELLER.DOMAIN.eq(domain.getId()))
					.and(SELLER.STATUS.eq(SellerStatus.ACTIVE.value()))
					.orderBy(REGISTRY.NAME)
					.fetch();
			LinkedList<Seller> v = new LinkedList<Seller>();
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
			if (ctx != null) ctx.close();
		}
	}

	public LinkedList<Project> getProjectList(Domain domain, String login){
		return AON.getProjectList(domain.getName(), domain.getId(), login,
				filter -> filter.getDomainProperty().eq(domain.getId()));
	}
	
	public Project getProject(Domain domain,String str, Integer customer, String login){
		return AON.getProject(domain.getName(), domain.getId(), login,
				filter -> filter.getDomainProperty().eq(domain.getId())
				.and(filter.getRegistryProperty().eq(customer))
				.and(filter.getAliasProperty().eq(str).or(filter.getNameProperty().eq(str))));
	}
	
	public Integer insertProject(Domain domain, User user, String name, Integer customer){
		Project project = getProjectDefault()
				.setDomain(domain.getId())
				.setName(name)
				.setRegistryId(customer);
		return AON.insertProject(domain.getName(), domain.getId(), user.getLogin(), project);
	}
	
	public LinkedList<Workplace> getWorkplaceList(Domain domain, User user){
		return AON.getWorkplaceList(domain.getName(), domain.getId(), user.getLogin(),
				filter -> filter.getDomainProperty().eq(domain.getId()));
	}
	
	public LinkedList<InvoicingGroup> getInvoicingGroupList(Domain domain, User user){
		return AON.getInvoicingGroupList(domain.getName(), domain.getId(), user.getLogin(),
				filter -> filter.getDomainProperty().eq(domain.getId()));
	}
	
	
	private Project getProjectDefault() {
		return new Project()
				.setActive(true)
				.setAlias("")
				.setCommercial(false)
				.setDate(new java.util.Date())
				.setDomain(0)
				.setName("")
				.setRegistryId(0)
				.setReservation(false)
				.setTas(false);
	}
}
