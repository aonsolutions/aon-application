package com.esferalia.aon.gwt.template.jooq;

import static com.esferalia.aon.jooq.tables.Customer.CUSTOMER;
import static com.esferalia.aon.jooq.tables.CustomerFee.CUSTOMER_FEE;
import static com.esferalia.aon.jooq.tables.InvoicingGroup.INVOICING_GROUP;
import static com.esferalia.aon.jooq.tables.Item.ITEM;
import static com.esferalia.aon.jooq.tables.Product.PRODUCT;
import static com.esferalia.aon.jooq.tables.Project.PROJECT;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Seller.SELLER;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;

import java.sql.Date;
import java.util.List;
import java.util.Vector;

import org.jooq.Condition;
import org.jooq.InsertValuesStep17;
import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.Record3;
import org.jooq.Record4;
import org.jooq.Record5;
import org.jooq.Result;
import org.jooq.impl.DSL;

import com.code.aon.company.WorkPlace;
import com.code.aon.customer.Customer;
import com.code.aon.customer.InvoicingGroup;
import com.code.aon.customer.enumeration.CustomerStatus;
import com.code.aon.project.Project;
import com.code.aon.registry.Registry;
import com.esferalia.aon.gwt.template.server.AuditInfo;
import com.esferalia.aon.gwt.template.server.FeeInfo;
import com.esferalia.aon.gwt.template.shared.Error;
import com.esferalia.aon.gwt.template.shared.Seller;
import com.esferalia.aon.jooq.tables.records.CustomerFeeRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Domain;

public class DBFee {

	static Integer domainIdFee;
	public static Error insertFee(String domain, Integer domainId, Vector<FeeInfo> fees, AuditInfo ai, String login){
		Error error = new Error();
		error.setError(true);
		Vector<String> verror = new Vector<String>();
		verror.add("");
		error.setTextError(verror);
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain, domainId, login);
			
			Vector<String> v = new Vector<String>();
			InsertValuesStep17<CustomerFeeRecord, Integer, Integer, Integer, Short, Integer, String, Double, Double, String, java.sql.Date, java.sql.Date, java.sql.Date, Short, Byte, Integer, Integer, Integer> customerFeeInsertQuery = ctx.getDslContext().insertInto(CUSTOMER_FEE, CUSTOMER_FEE.DOMAIN, CUSTOMER_FEE.PROJECT, CUSTOMER_FEE.CUSTOMER, CUSTOMER_FEE.LINE, CUSTOMER_FEE.ITEM, CUSTOMER_FEE.DESCRIPTION, CUSTOMER_FEE.QUANTITY, CUSTOMER_FEE.PRICE, CUSTOMER_FEE.DISCOUNT_EXPR, CUSTOMER_FEE.INITIAL_DATE, CUSTOMER_FEE.FINAL_DATE, CUSTOMER_FEE.BILLING_DATE, CUSTOMER_FEE.PERIOD, CUSTOMER_FEE.SECURITY_LEVEL, CUSTOMER_FEE.INVOICING_GROUP, CUSTOMER_FEE.SELLER, CUSTOMER_FEE.WORKPLACE);
			
			domainIdFee = domainId;
			AONContext sctx = ctx;
			fees.stream().forEach(s ->{	
				if(s.getProduct() != null){
					Result<Record1< Integer>> data = sctx.getDslContext().select(ITEM.ID)
						.from(ITEM)
						.where(ITEM.BARCODE.eq(s.getProduct())).and(ITEM.DOMAIN.eq(domainIdFee)).fetch();
					if(data.isEmpty()){

						data = sctx.getDslContext().select(ITEM.ID)
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
						
						data = sctx.getDslContext().select(ITEM.ID)
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
						
						
						if(s.getEndDate() != null ) new java.sql.Date(s.getEndDate().getTime());
						
						Short line;
						if(s.getLine() == null){
							Result<Record1<Short>> n = sctx.getDslContext().select(DSL.max(CUSTOMER_FEE.LINE))
								.from(CUSTOMER_FEE)
								.where(CUSTOMER_FEE.DOMAIN.eq(domainId).and(CUSTOMER_FEE.CUSTOMER.eq(s.getClientId()))).fetch();
							
							if(n.isEmpty() || n.get(0).value1()==null) line = 1;
							else line = n.get(0).value1(); //get max number (domain, customer)
							line++;
						}
						else{

							line = s.getLine().shortValue();
							sctx.getDslContext().update(CUSTOMER_FEE).set(CUSTOMER_FEE.LINE, CUSTOMER_FEE.LINE.add(1))
									.where(CUSTOMER_FEE.DOMAIN.eq(domainId)).and(CUSTOMER_FEE.CUSTOMER.eq(s.getClientId()))
									.and(CUSTOMER_FEE.LINE.greaterThan(line));
							
						}
						Integer w = sctx.getDslContext().select(WORKPLACE.ID)
								.from(WORKPLACE)
								.where(WORKPLACE.DOMAIN.eq(domainId)).limit(1).fetchOne().value1();
						if(s.getWorkplaceId() == null) s.setWorkplaceId(w);
						Short period = s.getPeriod().shortValue();
						
						Date endDate = null;
						if(s.getEndDate() != null) endDate =  new java.sql.Date(s.getEndDate().getTime());
						
						customerFeeInsertQuery.values(domainIdFee, s.getProjectId(), s.getClientId(),line, itemId, s.getDescription(), s.getQuantity(), s.getPrice(), s.getDiscount().toString(), new java.sql.Date(s.getStartDate().getTime()), endDate, new java.sql.Date(s.getBillingDate().getTime()),period, confidential.byteValue(), s.getBillingGroup(), s.getSellerId(),s.getWorkplaceId());
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
	
	public static Customer getCustomer(Domain domain, String login, String client, Boolean ignoreInactiveClient){
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
	
				customer.setId(result.get(index).value1());
				Registry registry = new Registry();
				registry.setAlias(result.get(index).value2());
				registry.setDocument(result.get(index).value3());
				registry.setName(result.get(index).value4());
				customer.setRegistry(registry);
				customer.setStatus(CustomerStatus.values()[result.get(index).value5()]);
			
				return customer;
			}
			return null;
		}finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public static Vector<Customer> getCustomers(String domain,Integer domainId, String login) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain, domainId, login);

			Result<Record4<Integer, String, String, String>> data = ctx.getDslContext().select(REGISTRY.ID, REGISTRY.ALIAS, REGISTRY.DOCUMENT, REGISTRY.NAME)
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
			if (ctx != null) ctx.close();
		}
	}
	
	
	
	public static List<Seller> getSellers(Domain domain,  String login){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login);

			Result<Record4<Integer, String, String, String>> data = ctx.getDslContext().select(REGISTRY.ID,REGISTRY.ALIAS,REGISTRY.NAME, REGISTRY.DOCUMENT)
					.from(REGISTRY).join(SELLER).on(REGISTRY.ID.eq(SELLER.REGISTRY))
					.where(SELLER.DOMAIN.eq(domain.getId()))
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
			if (ctx != null) ctx.close();
		}
	}

	public static Vector<Project> getProjects(String domain,Integer domainId, String login){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain, domainId, login);

			Result<Record3<Integer, String, String>> data = ctx.getDslContext().select(PROJECT.ID,PROJECT.ALIAS,PROJECT.NAME)
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
			if (ctx != null) ctx.close();
		}
	}

	
	public static Project getProject(Domain domain,String str, Integer customer, String login){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login);

			Result<Record3<Integer, String, String>> result = ctx.getDslContext().select(PROJECT.ID,PROJECT.ALIAS,PROJECT.NAME)
					.from(PROJECT)
					.where(PROJECT.DOMAIN.eq(domain.getId()))
					.and(PROJECT.REGISTRY.eq(customer))
					.and(PROJECT.ALIAS.eq(str).or(PROJECT.NAME.eq(str)))
					.fetch();
			
			if(result.isNotEmpty()){
				Project project = new Project();
				Integer index = 0;
				project.setId(result.get(index).value1());
				project.setAlias(result.get(index).value2());
				project.setName(result.get(index).value3());
			
				return project;
			}
			return null;
		} finally {
			if (ctx != null) ctx.close();
		}
	}

	public static Vector<WorkPlace> getWorkplaces(Domain domain, String login){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login);

			Result<Record2<Integer, String>> data = ctx.getDslContext().select(WORKPLACE.ID,WORKPLACE.DESCRIPTION)
					.from(WORKPLACE)
					.where(WORKPLACE.DOMAIN.eq(domain.getId()))
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
			if (ctx != null) ctx.close();
		}
	}
	
	public static Vector<InvoicingGroup> getInvoicingGroups(Domain domain, String login) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login);

			Result<Record2<Integer, String>> data = ctx.getDslContext().select(INVOICING_GROUP.ID,INVOICING_GROUP.DESCRIPTION)
					.from(INVOICING_GROUP)
					.where(INVOICING_GROUP.DOMAIN.eq(domain.getId()))
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
			if (ctx != null) ctx.close();
		}
	}
}
