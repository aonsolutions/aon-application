package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Creditor.CREDITOR;
import static com.esferalia.aon.jooq.tables.Customer.CUSTOMER;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Project.PROJECT;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Supplier.SUPPLIER;
import static com.esferalia.aon.jooq.tables.Raddress.RADDRESS;
import static com.esferalia.aon.jooq.tables.Rmedia.RMEDIA;
import static com.esferalia.aon.jooq.tables.Rrelationship.RRELATIONSHIP;

import java.util.function.Function;
import java.util.stream.Stream;

import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.SelectJoinStep;
import org.jooq.SelectOnConditionStep;
import org.jooq.SelectOrderByStep;
import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.CreditorSupplier;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter.CreditorFilter;
import com.esferalia.aon.occam.api.model.Filter.CustomerFilter;
import com.esferalia.aon.occam.api.model.Filter.SupplierFilter;
import com.esferalia.aon.occam.api.model.Order.CustomerOrder;
import com.esferalia.aon.occam.api.model.Order.SupplierCreditorOrder;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.Supplier;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.RegistryStatus;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.occam.impl.jooq.dao.CreditorDAO.CreditorPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.CustomerDAO.CustomerFiller;
import com.esferalia.aon.occam.impl.jooq.dao.CustomerDAO.CustomerPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PropertyOrdersDAO.CustomerPropertyOrdersDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PropertyOrdersDAO.SupplierCreditorPropertyOrdersDAO;
import com.esferalia.aon.occam.impl.jooq.dao.SupplierDAO.SupplierPropertiesDAO;
import com.esferalia.aon.watson.util.AonEnumUtils;

public class CreditorSupplierDAO {
	private CreditorSupplierDAO() {}
	private static final CreditorPropertiesDAO CREDITOR_PROPERTIES = new CreditorPropertiesDAO();
	private static final SupplierPropertiesDAO SUPPLIER_PROPERTIES = new SupplierPropertiesDAO();
	private static final CustomerPropertiesDAO CUSTOMER_PROPERTIES = new CustomerPropertiesDAO();
	private static final CustomerPropertyOrdersDAO CUSTOMER_PROPERTY_ORDERS = new CustomerPropertyOrdersDAO(); 
	private static final SupplierCreditorPropertyOrdersDAO SUPPLIER_CREDITOR_PROPERTY_ORDERS = new SupplierCreditorPropertyOrdersDAO(); 
	private static final Field<String> type = DSL.field(DSL.name("typeRegistry"), String.class);
	
	public static long getCustomerCount(AONContext ctx, CustomerFilter filter, String globalFilter) {
		SelectJoinStep<Record1<Integer>> i = ctx.getDslContext().select(CUSTOMER.REGISTRY).from(CUSTOMER)
				.join(REGISTRY).on(REGISTRY.ID.eq(CUSTOMER.REGISTRY))
				.leftOuterJoin(PROJECT).on(PROJECT.REGISTRY.eq(CUSTOMER.REGISTRY))
			    .leftOuterJoin(RRELATIONSHIP).on(RRELATIONSHIP.REGISTRY.eq(CUSTOMER.REGISTRY).and(RRELATIONSHIP.RELATIONSHIP.eq(-1)));
		if(globalFilter.isEmpty()) {
			i.where(CUSTOMER_PROPERTIES.getConditions(filter));
		}else {
			i.leftJoin(RMEDIA).on(CUSTOMER.REGISTRY.eq(RMEDIA.REGISTRY))
			.leftJoin(RADDRESS).on(CUSTOMER.REGISTRY.eq(RADDRESS.REGISTRY))
			.where(CUSTOMER_PROPERTIES.getConditions(filter))
			.groupBy(CUSTOMER.REGISTRY);
			;
		}
		return i.fetch().stream().count();	
	}
	
	public static Stream<Customer> getCustomerStream(AONContext ctx, CustomerFilter filter, int offset, int limit, String globalFilter, CustomerOrder order){
		return prepareQueryCustomer(ctx, filter, globalFilter)
				.orderBy(CUSTOMER_PROPERTY_ORDERS.getOrders(order))
				.limit(limit)
				.offset(limit * (offset -1))
				.fetch()
				.stream()
				.map(new CustomerFiller());
	}
	
	public static SelectOrderByStep<Record> prepareQueryCustomer(AONContext ctx, CustomerFilter filter, String value ){
		SelectOnConditionStep<Record> customers = ctx.getDslContext()
	        .selectDistinct(CUSTOMER.fields())
	        .select(REGISTRY.fields())
	        .select(DOMAIN.fields())
	        .select(RRELATIONSHIP.ID)
		    .from(CUSTOMER)
		    .join(REGISTRY).on(REGISTRY.ID.eq(CUSTOMER.REGISTRY))
		    .join(DOMAIN).on(CUSTOMER.DOMAIN.eq(DOMAIN.ID))
		    .leftOuterJoin(PROJECT).on(PROJECT.REGISTRY.eq(REGISTRY.ID))
		    .leftOuterJoin(RRELATIONSHIP).on(RRELATIONSHIP.REGISTRY.eq(REGISTRY.ID).and(RRELATIONSHIP.RELATIONSHIP.eq(-1)));
		if(value.isEmpty()) {
			customers.where(CUSTOMER_PROPERTIES.getConditions(filter));
		} else {
			customers.leftJoin(RMEDIA).on(REGISTRY.ID.eq(RMEDIA.REGISTRY))
			.leftJoin(RADDRESS).on(REGISTRY.ID.eq(RADDRESS.REGISTRY))
			.where(CUSTOMER_PROPERTIES.getConditions(filter))
			.groupBy(CUSTOMER.REGISTRY);
			;
		}
		return customers.groupBy(REGISTRY.ID);
	}

	public static long getSupplierCreditorCount(AONContext ctx, CreditorFilter filter, SupplierFilter filter2, String globalFilter) {
	    SelectJoinStep<Record1<Integer>> creditors = ctx.getDslContext().select(CREDITOR.REGISTRY).from(CREDITOR).join(REGISTRY).on(REGISTRY.ID.eq(CREDITOR.REGISTRY));
	    SelectJoinStep<Record1<Integer>> suppliers = ctx.getDslContext().select(SUPPLIER.REGISTRY).from(SUPPLIER).join(REGISTRY).on(REGISTRY.ID.eq(SUPPLIER.REGISTRY));
	
	    if (globalFilter.isEmpty()) {
	        creditors.where(CREDITOR_PROPERTIES.getConditions(filter));
	        suppliers.where(SUPPLIER_PROPERTIES.getConditions(filter2));
	    } else {
	        creditors.leftJoin(RMEDIA).on(CREDITOR.REGISTRY.eq(RMEDIA.REGISTRY))
	                .leftJoin(RADDRESS).on(CREDITOR.REGISTRY.eq(RADDRESS.REGISTRY))
	                .where(CREDITOR_PROPERTIES.getConditions(filter))
	                .groupBy(CREDITOR.REGISTRY);
	        
	        suppliers.leftJoin(RMEDIA).on(SUPPLIER.REGISTRY.eq(RMEDIA.REGISTRY))
	                .leftJoin(RADDRESS).on(SUPPLIER.REGISTRY.eq(RADDRESS.REGISTRY))
	                .where(SUPPLIER_PROPERTIES.getConditions(filter2))
	                .groupBy(SUPPLIER.REGISTRY);
	    } 	    
	    return creditors.union(suppliers).fetch().stream().count();
	}
	
	public static Stream<CreditorSupplier> getSupplierCreditorStream(AONContext ctx, CreditorFilter filter, SupplierFilter filter2, int offset, int limit, String globalFilter, SupplierCreditorOrder order){
		return ctx.getDslContext()
			.select(DSL.asterisk())
			.from(prepareQuery(ctx, filter, filter2, globalFilter).asTable(REGISTRY))
			.groupBy(REGISTRY.ID)
			.orderBy(SUPPLIER_CREDITOR_PROPERTY_ORDERS.getOrders(order))
			.limit(limit)
			.offset(limit * (offset -1))
			.fetch()
			.stream()
			.map(new CreditorSupplierFiller());
	}
	
	private static SelectOrderByStep<Record> prepareQuery(AONContext ctx, CreditorFilter filter, SupplierFilter filter2, String value) {
		SelectOnConditionStep<Record> creditors = ctx.getDslContext().select(REGISTRY.asterisk())
				.select(CREDITOR.TRANSACTION)
				.select(CREDITOR.STATUS)
				.select(DSL.inline("creditor").as(type))
				.from(CREDITOR)
				.join(REGISTRY).on(REGISTRY.ID.eq(CREDITOR.REGISTRY))
				.join(DOMAIN).on(CREDITOR.DOMAIN.eq(DOMAIN.ID));
		SelectOnConditionStep<Record> suppliers = ctx.getDslContext().select(REGISTRY.asterisk())
				.select(SUPPLIER.TRANSACTION)
				.select(SUPPLIER.STATUS)
				.select(DSL.inline("supplier").as(type))
				.from(SUPPLIER)
				.join(REGISTRY).on(REGISTRY.ID.eq(SUPPLIER.REGISTRY))
				.join(DOMAIN).on(SUPPLIER.DOMAIN.eq(DOMAIN.ID));
		if(value.isEmpty()) {
			creditors.where(CREDITOR_PROPERTIES.getConditions(filter));
			suppliers.where(SUPPLIER_PROPERTIES.getConditions(filter2));
		} else {
			creditors.leftJoin(RMEDIA).on(REGISTRY.ID.eq(RMEDIA.REGISTRY))
			.leftJoin(RADDRESS).on(REGISTRY.ID.eq(RADDRESS.REGISTRY))
			.where(CREDITOR_PROPERTIES.getConditions(filter))
			.groupBy(REGISTRY.ID)
			;
			suppliers.leftJoin(RMEDIA).on(REGISTRY.ID.eq(RMEDIA.REGISTRY))
			.leftJoin(RADDRESS).on(REGISTRY.ID.eq(RADDRESS.REGISTRY))
			.where(SUPPLIER_PROPERTIES.getConditions(filter2))
			.groupBy(REGISTRY.ID)
			;
		}
		return creditors.union(suppliers);
	}
	
	protected static class CreditorSupplierFiller implements Function<Record, CreditorSupplier> {

		@Override
		public CreditorSupplier apply(Record r) {
			return buildCreditorSupplier(r, REGISTRY);
		}
		
		public static CreditorSupplier buildCreditorSupplier(Record r, com.esferalia.aon.jooq.tables.Registry registry) {
			if(registry == null) registry = REGISTRY;
			if(r.getValue(type).equals("creditor")) {
				Creditor c = new Creditor()
						.copy( new Registry() 
							.setId(r.getValue(registry.ID))
							.setDomain(new Domain().setId(r.getValue(CREDITOR.DOMAIN)))
							.setDocument(r.getValue(registry.DOCUMENT))
							.setDocumentType(DocumentType.safeValueOf(r.getValue(registry.DOCUMENT_TYPE)))
							.setDocumentCountry(Country.safeValueOf(r.getValue(registry.DOCUMENT_COUNTRY)) )
							.setName(r.getValue(registry.NAME))
							.setAlias(r.getValue(registry.ALIAS))
							.setLegalPerson(AonEnumUtils.getBoolean(r.getValue(registry.TYPE)))
							.setNationality(Country.safeValueOf(r.getValue(registry.NATIONALITY)) )
							.setSecurityLevel(SecurityLevel.safeValueOf(r.getValue(registry.SECURITY_LEVEL))))
						.setTransaction(InvoiceTransactionType.safeValueOf( r.getValue(CREDITOR.TRANSACTION)))
						.setStatus(RegistryStatus.safeValueOf(r.getValue(CREDITOR.STATUS)))
						;
				return new CreditorSupplier(c, new Supplier(), r.getValue(type));
			} else if(r.getValue(type).equals("supplier")) {
				Supplier s = new Supplier()
						.copy( new Registry() 
							.setId(r.getValue(registry.ID))
							.setDomain(new Domain().setId(r.getValue(SUPPLIER.DOMAIN)))
							.setDocument(r.getValue(registry.DOCUMENT))
							.setDocumentType(DocumentType.safeValueOf(r.getValue(registry.DOCUMENT_TYPE)))
							.setDocumentCountry(Country.safeValueOf(r.getValue(registry.DOCUMENT_COUNTRY)) )
							.setName(r.getValue(registry.NAME))
							.setAlias(r.getValue(registry.ALIAS))
							.setLegalPerson(AonEnumUtils.getBoolean(r.getValue(registry.TYPE)))
							.setNationality(Country.safeValueOf(r.getValue(registry.NATIONALITY)) )
							.setSecurityLevel(SecurityLevel.safeValueOf(r.getValue(registry.SECURITY_LEVEL))))
						.setTransaction(InvoiceTransactionType.safeValueOf( r.getValue(SUPPLIER.TRANSACTION)))
						.setStatus(RegistryStatus.safeValueOf(r.getValue(SUPPLIER.STATUS)))
						;
				return new CreditorSupplier(new Creditor(), s, r.getValue(type));
			}
			return null;
		}
	}
}
