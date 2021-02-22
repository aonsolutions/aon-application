package com.esferalia.aon.occam.impl.jooq.dao;


import static com.esferalia.aon.jooq.tables.Customer.CUSTOMER;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;

import java.sql.Timestamp;
import java.util.Date;
import java.util.function.Function;
import java.util.stream.Stream;

import  org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter.CustomerFilter;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.RegistryStatus;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.CustomerPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.validation.CustomerAutoComplete;
import com.esferalia.aon.occam.impl.jooq.validation.CustomerValidation;
import com.esferalia.aon.watson.util.AonEnumUtils;

public class CustomerDAO {
	
	private static final CustomerPropertiesDAO CUSTOMER_PROPERTIES = new CustomerPropertiesDAO();

	protected static class CustomerFiller  implements Function<Record, Customer> {

		@Override
		public Customer apply(Record r) {
			return buildCustomer(r, REGISTRY);
		}
		
		public static Customer buildCustomer(Record r, com.esferalia.aon.jooq.tables.Registry registry) {
			if(registry == null) 
				registry = REGISTRY;
			return new Customer()
					.copy( new Registry() 
						.setId(r.getValue(registry.ID))
						.setDomain(new Domain().setId(r.getValue(CUSTOMER.DOMAIN)))
						.setDocument(r.getValue(registry.DOCUMENT))
						.setDocumentType(DocumentType.safeValueOf(r.getValue(registry.DOCUMENT_TYPE)))
						.setDocumentCountry(Country.safeValueOf(r.getValue(registry.DOCUMENT_COUNTRY)) )
						.setName(r.getValue(registry.NAME))
						.setAlias(r.getValue(registry.ALIAS))
						.setLegalPerson(AonEnumUtils.getBoolean(r.getValue(registry.TYPE)))
						.setNationality(Country.safeValueOf(r.getValue(registry.NATIONALITY)) )
						.setSecurityLevel(SecurityLevel.safeValueOf(r.getValue(registry.SECURITY_LEVEL))))
					.setAccount(r.getValue(CUSTOMER.ACCOUNT))
					.setCreationDate(r.getValue(CUSTOMER.CREATION_DATE))
					.setCreationUser(r.getValue(CUSTOMER.CREATION_USER))
					.setDeliveryGrouped(r.getValue(CUSTOMER.DELIVERY_GROUPED) == 1)
					.setDeliveryValuated(r.getValue(CUSTOMER.DELIVERY_VALUATED) == 1)
					.setEInvoice(r.getValue(CUSTOMER.E_INVOICE) == 1)
					.setInvoicingGroup(r.getValue(CUSTOMER.INVOICING_GROUP))
					.setModificationDate(r.getValue(CUSTOMER.MODIFICATION_DATE))
					.setModificationUser(r.getValue(CUSTOMER.MODIFICATION_USER))
					.setProjectGrouped(r.getValue(CUSTOMER.PROJECT_GROUPED) == 1)
					.setScope(r.getValue(CUSTOMER.SCOPE))
					.setSurcharge(r.getValue(CUSTOMER.SURCHARGE)==1)
					.setTariff(r.getValue(CUSTOMER.TARIFF))
					.setTransaction(InvoiceTransactionType.safeValueOf( r.getValue(CUSTOMER.TRANSACTION)))
					.setWithholding(r.getValue(CUSTOMER.WITHHOLDING)==1)
					.setStatus(RegistryStatus.safeValueOf(r.getValue(CUSTOMER.STATUS)));
		}
	}
	
	private static SelectConditionStep<Record> select(AONContext ctx, CustomerFilter filter) {
		return ctx.getDslContext().select()
				.from(CUSTOMER)
				.join(REGISTRY).on(REGISTRY.ID.eq(CUSTOMER.REGISTRY))
				.where(CUSTOMER_PROPERTIES.getConditions(filter));
		
	}
	
	public static Stream<Customer> getStream(AONContext ctx, CustomerFilter filter){
		return select(ctx,filter)
			.fetch()
			.stream()
			.map(new CustomerFiller());
	}
	
	public static Customer get(AONContext ctx, Integer id){
		return getStream(ctx, p -> p.getIdProperty().eq(id))
			.findFirst()
			.orElse(null);
	}
	
	
	public static Customer insert(AONContext ctx, Customer customer){
		ctx.checkWrite();
		CustomerAutoComplete.autoComplete(ctx, customer);
		CustomerValidation.validate(ctx, customer);
		customer = (Customer) RegistryDAO.save(ctx, customer);
		ctx.getDslContext().insertInto(CUSTOMER)
			.set(CUSTOMER.REGISTRY,customer.getId())
			.set(CUSTOMER.DOMAIN,customer.getDomain().getId())
			.set(CUSTOMER.TARIFF,customer.getTariff())
			.set(CUSTOMER.SURCHARGE,AonEnumUtils.getByte(customer.isSurcharge()))
			.set(CUSTOMER.WITHHOLDING,AonEnumUtils.getByte(customer.isWithholding()))
			.set(CUSTOMER.TRANSACTION,AonEnumUtils.getByte(customer.getTransaction()))
			.set(CUSTOMER.STATUS, customer.getStatus().value())
			.set(CUSTOMER.SCOPE,customer.getScope())
			.set(CUSTOMER.E_INVOICE,AonEnumUtils.getByte(customer.isEInvoice()))
			.set(CUSTOMER.INVOICING_GROUP,customer.getInvoicingGroup())
			.set(CUSTOMER.PROJECT_GROUPED,AonEnumUtils.getByte(customer.isProjectGrouped()))
			.set(CUSTOMER.DELIVERY_GROUPED,AonEnumUtils.getByte(customer.isDeliveryGrouped()))
			.set(CUSTOMER.DELIVERY_VALUATED,AonEnumUtils.getByte(customer.isDeliveryValuated()))
			.set(CUSTOMER.ACCOUNT,customer.getAccount())
			.set(CUSTOMER.CREATION_USER,ctx.getUser())
			.set(CUSTOMER.CREATION_DATE,new Timestamp(new Date().getTime()))
			.execute();
		ctx.log().info("INSERT CUSTOMER id: " + customer.getId());		
		return customer;
	}

	public static Customer update(AONContext ctx, Customer customer){
		ctx.checkWrite();
		CustomerAutoComplete.autoComplete(ctx, customer);
		CustomerValidation.validate(ctx, customer);
		customer = (Customer) RegistryDAO.save(ctx, customer);
		int count = ctx.getDslContext().update(CUSTOMER)
			.set(CUSTOMER.DOMAIN,customer.getDomain().getId())
			.set(CUSTOMER.TARIFF,customer.getTariff())
			.set(CUSTOMER.SURCHARGE,AonEnumUtils.getByte(customer.isSurcharge()))
			.set(CUSTOMER.WITHHOLDING,AonEnumUtils.getByte(customer.isWithholding()))
			.set(CUSTOMER.TRANSACTION,AonEnumUtils.getByte(customer.getTransaction()))
			.set(CUSTOMER.STATUS, customer.getStatus().value())
			.set(CUSTOMER.SCOPE,customer.getScope())
			.set(CUSTOMER.E_INVOICE,AonEnumUtils.getByte(customer.isEInvoice()))
			.set(CUSTOMER.INVOICING_GROUP,customer.getInvoicingGroup())
			.set(CUSTOMER.PROJECT_GROUPED,AonEnumUtils.getByte(customer.isProjectGrouped()))
			.set(CUSTOMER.DELIVERY_GROUPED,AonEnumUtils.getByte(customer.isDeliveryGrouped()))
			.set(CUSTOMER.DELIVERY_VALUATED,AonEnumUtils.getByte(customer.isDeliveryValuated()))
			.set(CUSTOMER.ACCOUNT,customer.getAccount())
			.set(CUSTOMER.MODIFICATION_USER,ctx.getUser())
			.set(CUSTOMER.MODIFICATION_DATE,new Timestamp(new Date().getTime()))
			.where(CUSTOMER.REGISTRY.eq(customer.getId()))
			.execute();
		ctx.log().info("UPDATE CUSTOMER id: " + customer.getId() + ". (" + count + " rows)");		
		return customer;
	}

	public static void delete(AONContext ctx, Integer id) {
		ctx.checkWrite();
		CustomerValidation.validateDeletion(ctx, id);
		int count = ctx.getDslContext().delete(CUSTOMER)
			.where(CUSTOMER.REGISTRY.eq(id))
			.execute();
		ctx.log().info("DELETE CUSTOMER id:" + id + " ("+count+" rows)");
	}

	// *************************************************
	// ********** TEST PURPOSE METHODS *****************
	// *************************************************
	public static Customer getRandom(AONContext ctx, CustomerFilter filter) {
		return select(ctx,filter)
			.orderBy( DSL.rand() )
			.fetch()
			.stream()
			.map(new CustomerFiller())
			.findFirst()
			.orElse(null);
	}
	

}
