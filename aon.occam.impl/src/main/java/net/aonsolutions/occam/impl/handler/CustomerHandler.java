package net.aonsolutions.occam.impl.handler;

import static com.esferalia.aon.jooq.tables.Account.ACCOUNT;
import static com.esferalia.aon.jooq.tables.Customer.CUSTOMER;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.impl.DSL;

import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonEnumUtils;

import net.aonsolutions.occam.api.model.Account;
import net.aonsolutions.occam.api.model.Customer;
import net.aonsolutions.occam.api.model.CustomerFull;
import net.aonsolutions.occam.api.model.Filter.CustomerFilter;
import net.aonsolutions.occam.api.model.Filter.Property;
import net.aonsolutions.occam.api.model.Properties.CustomerProperties;
import net.aonsolutions.occam.api.model.type.Country;
import net.aonsolutions.occam.api.model.type.DocumentType;
import net.aonsolutions.occam.api.model.type.InvoiceTransactionType;
import net.aonsolutions.occam.api.model.type.RegistryStatus;
import net.aonsolutions.occam.impl.AONContext;
import net.aonsolutions.occam.impl.handler.AccountHandler.AccountFiller;
import net.aonsolutions.occam.impl.handler.RegistryHandler.RegistryPropertiesHandler;

class CustomerHandler {
	private CustomerHandler() {
	}
	
	private static final CustomerPropertiesHandler CUSTOMER_PROPERTIES = new CustomerPropertiesHandler();
	private static class CustomerPropertiesHandler extends RegistryPropertiesHandler implements CustomerProperties {
		
		protected Condition getCondition(CustomerFilter filter) {
			if (filter == null) return DSL.trueCondition();
			FilterImpl filterDAO = (FilterImpl) filter.filter(this);
			return filterDAO.getCondition();
		}
		
		@Override public Property<Integer> getIdProperty() {return new FilterImpl.PropertyDAO<>(CUSTOMER.REGISTRY);}
		@Override public Property<Integer> getDomainProperty() {return new FilterImpl.PropertyDAO<>(CUSTOMER.DOMAIN);}
		@Override public Property<Integer> getTariffProperty() {return new FilterImpl.PropertyDAO<>(CUSTOMER.TARIFF);}
		@Override public Property<Byte> getSurchargeProperty() {return new FilterImpl.PropertyDAO<>(CUSTOMER.SURCHARGE);}
		@Override public Property<Byte> getWithholdingProperty() {return new FilterImpl.PropertyDAO<>(CUSTOMER.WITHHOLDING);}
		@Override public Property<Byte> getTransactionProperty() {return new FilterImpl.PropertyDAO<>(CUSTOMER.TRANSACTION);}
		@Override public Property<Byte> getStatusProperty() {return new FilterImpl.PropertyDAO<>(CUSTOMER.STATUS);}
		@Override public Property<Integer> getScopeProperty() {return new FilterImpl.PropertyDAO<>(CUSTOMER.SCOPE);}
		@Override public Property<Byte> getEInvoiceProperty() {return new FilterImpl.PropertyDAO<>(CUSTOMER.E_INVOICE);}
		@Override public Property<Integer> getInvoicingGroupProperty() {return new FilterImpl.PropertyDAO<>(CUSTOMER.INVOICING_GROUP);}
		@Override public Property<Byte> getProjectGroupedProperty() {return new FilterImpl.PropertyDAO<>(CUSTOMER.PROJECT_GROUPED);}
		@Override public Property<Byte> getDeliveryGroupedProperty() {return new FilterImpl.PropertyDAO<>(CUSTOMER.DELIVERY_GROUPED);}
		@Override public Property<Byte> getDeliveryValuatedProperty() {return new FilterImpl.PropertyDAO<>(CUSTOMER.DELIVERY_VALUATED);}
		@Override public Property<Integer> getAccountProperty() {return new FilterImpl.PropertyDAO<>(CUSTOMER.ACCOUNT);}
		@Override public Property<String> getCreationUserProperty() {return new FilterImpl.PropertyDAO<>(CUSTOMER.CREATION_USER);}
		@Override public Property<Timestamp> getCreationDateProperty() {return new FilterImpl.PropertyDAO<>(CUSTOMER.CREATION_DATE);}
		@Override public Property<String> getModificationUserProperty() {return new FilterImpl.PropertyDAO<>(CUSTOMER.MODIFICATION_USER);}
		@Override public Property<Timestamp> getModificationDateProperty() {return new FilterImpl.PropertyDAO<>(CUSTOMER.MODIFICATION_DATE);}
	}

	
	protected static class CustomerFiller extends Filler<Customer> {

		@Override
		public Customer apply(Record r) {
			return buildCustomer(r, REGISTRY);
		}
		
		static Customer buildCustomer(Record r, com.esferalia.aon.jooq.tables.Registry registry) {
			if (isNull(r, registry.ID)) return null;
			return new Customer()
				.setId(getValue(r, registry.ID))
				.setDomain(getValue(r, CUSTOMER.DOMAIN))
				.setDocument(getValue(r, registry.DOCUMENT))
				.setDocumentType(DocumentType.value(getValue(r, registry.DOCUMENT_TYPE)).orElse(null))
				.setDocumentCountry(Country.value(getValue(r, registry.DOCUMENT_COUNTRY)).orElse(null))
				.setName(getValue(r, registry.NAME))
				.setAlias(getValue(r, registry.ALIAS))
				.setLegalPerson(AonEnumUtils.getBoolean(getValue(r, registry.TYPE)))
				.setNationality(Country.value(getValue(r, registry.NATIONALITY)).orElse(null))
				.setConfidential(getBoolean(r, registry.SECURITY_LEVEL))
				.setTariff( getValue(r, CUSTOMER.TARIFF) )
				.setSurcharge(getBoolean(r, CUSTOMER.SURCHARGE))
				.setWithholding(getBoolean(r, CUSTOMER.WITHHOLDING))
				.setTransaction(InvoiceTransactionType.value( getValue(r, CUSTOMER.TRANSACTION)).orElse(null))
				.setStatus(RegistryStatus.value(getValue(r, CUSTOMER.STATUS)).orElse(null))
				.setScope(getValue(r, CUSTOMER.SCOPE))
				.setEInvoice(getBoolean(r, CUSTOMER.E_INVOICE))
				.setInvoicingGroup(getValue(r, CUSTOMER.INVOICING_GROUP))
				.setProjectGrouped(getBoolean(r, CUSTOMER.PROJECT_GROUPED))
				.setDeliveryGrouped(getBoolean(r, CUSTOMER.DELIVERY_GROUPED))
				.setDeliveryValuated(getBoolean(r, CUSTOMER.DELIVERY_VALUATED))
				.setAccount(AccountFiller.build(r) )
				.setCreationUser(getValue(r, CUSTOMER.CREATION_USER))
				.setCreationDate(getValue(r, CUSTOMER.CREATION_DATE))
				.setModificationDate(getValue(r, CUSTOMER.MODIFICATION_DATE))
				.setModificationUser(getValue(r, CUSTOMER.MODIFICATION_USER))
			;
		}
	}
	
	static Customer validate(AONContext ctx, Customer customer) {
		ctx.checkWrite();
		CustomerAutoComplete.autoComplete(ctx, customer);
		CustomerValidation.validate(ctx, customer);
		return customer;
	}
	
	static Customer save(AONContext ctx, Customer customer) {
		validate(ctx, customer);
		boolean nullId = (customer.getId() == null); 
		customer = RegistryHandler.save(ctx, customer);
		return nullId 
			? insert(ctx, customer)
			: update(ctx, customer);
	}
	
	
	private static class CustomerAutoComplete {
		
		private CustomerAutoComplete() {
			
		}
		private static final BiConsumer<AONContext,Customer> COMPLETE_TRANSACTION = (ctx,customer) -> {
			if (customer.getTransaction() == null) {
				ctx.log().debug("\t saving customer: autocomplete transaction: {0}",InvoiceTransactionType.NATIONAL);
				customer.setTransaction(InvoiceTransactionType.NATIONAL);
			}
		};

		private static final BiConsumer<AONContext,Customer> COMPLETE_STATUS = (ctx,customer) -> {
			if (customer.getStatus() == null) {
				ctx.log().debug("\t saving customer: autocomplete status: {0}",RegistryStatus.ACTIVE);
				customer.setStatus(RegistryStatus.ACTIVE);
			}
		};
		
		private static final BiConsumer<AONContext, Customer> COMPLETE_SCOPE = (ctx, customer) -> {
			if(customer.getScope() == null) {
				ctx.getDefaultScope( customer.getDomain() )
					.ifPresent(s -> customer.setScope(s.getId()));
			}
		};

		static void autoComplete(AONContext ctx, Customer customer) throws AonCoreException {
			COMPLETE_TRANSACTION
			.andThen(COMPLETE_STATUS)
			.andThen(COMPLETE_SCOPE)
				.accept(ctx, customer);

		}

	}
	
	private class CustomerValidation {
		
		private CustomerValidation() {
		}
		
		private static final BiConsumer<Customer,AONContext> EMPTY_SCOPE = (customer,ctx) -> {
			if (customer.getScope() == null ) 
				throw new AonCoreException(AonError.EMPTY_SCOPE.getMessage());
		};
		
		
		static void validate(AONContext ctx, Customer customer) throws AonCoreException{
			EMPTY_SCOPE
				.accept(customer, ctx);
		}
	}
	
	// **************************************************************
	// **************************************************************
	// **************************************************************
	// **************************************************************
	private static SelectConditionStep<Record> select(AONContext ctx, int domain) {
		return ctx.getDslContext().select()
			.from(CUSTOMER)
			.join(REGISTRY).on(REGISTRY.ID.eq(CUSTOMER.REGISTRY))
			.leftOuterJoin(ACCOUNT).on(ACCOUNT.ID.eq(CUSTOMER.ACCOUNT))
			.where(CUSTOMER.DOMAIN.eq(domain))
			.and(CUSTOMER.SCOPE.in(ctx.getUserScopes(domain)))
		;
	}

	static Stream<Customer> stream(AONContext ctx, int domain, CustomerFilter filter){
		return select(ctx, domain)
			.and(CUSTOMER.DOMAIN.eq(domain))
			.and(CUSTOMER_PROPERTIES.getCondition(filter))
			.fetch()
			.stream()
			.map(new CustomerFiller());
	}
	
	static Optional<Customer> get(AONContext ctx, int domain, Integer customerId){
		return select(ctx, domain)
			.and(CUSTOMER.REGISTRY.eq(customerId))
			.fetch()
			.stream()
			.map(new CustomerFiller())
			.findFirst()
		;
	}
	
	private static Customer insert(AONContext ctx, Customer customer){
		ctx.getDslContext().insertInto(CUSTOMER)
			.set(CUSTOMER.REGISTRY,customer.getId())
			.set(CUSTOMER.DOMAIN,customer.getDomain())
			.set(CUSTOMER.TARIFF,customer.getTariff())
			.set(CUSTOMER.SURCHARGE,AonEnumUtils.getByte(customer.isSurcharge()))
			.set(CUSTOMER.WITHHOLDING,AonEnumUtils.getByte(customer.isWithholding()))
			.set(CUSTOMER.TRANSACTION, InvoiceTransactionType.value(customer.getTransaction()))
			.set(CUSTOMER.STATUS, RegistryStatus.value(customer.getStatus()))
			.set(CUSTOMER.SCOPE,customer.getScope())
			.set(CUSTOMER.E_INVOICE, AonEnumUtils.getByte(customer.isEInvoice()))
			.set(CUSTOMER.INVOICING_GROUP, customer.getInvoicingGroup())
			.set(CUSTOMER.PROJECT_GROUPED, AonEnumUtils.getByte(customer.isProjectGrouped()))
			.set(CUSTOMER.DELIVERY_GROUPED, AonEnumUtils.getByte(customer.isDeliveryGrouped()))
			.set(CUSTOMER.DELIVERY_VALUATED, AonEnumUtils.getByte(customer.isDeliveryValuated()))
			.set(CUSTOMER.ACCOUNT,customer.getAccount().map( Account::getId ).orElse(null))
			.set(CUSTOMER.CREATION_USER, ctx.getUser())
			.set(CUSTOMER.CREATION_DATE, new Timestamp(new Date().getTime()))
			.execute();
		ctx.log().debug("INSERT CUSTOMER id: {0}",customer.getId());		
		return customer;
	}
	
	private static Customer update(AONContext ctx, Customer customer){
		int count = ctx.getDslContext().update(CUSTOMER)
			.set(CUSTOMER.DOMAIN,customer.getDomain())
			.set(CUSTOMER.TARIFF,customer.getTariff())
			.set(CUSTOMER.SURCHARGE,AonEnumUtils.getByte(customer.isSurcharge()))
			.set(CUSTOMER.WITHHOLDING,AonEnumUtils.getByte(customer.isWithholding()))
			.set(CUSTOMER.TRANSACTION, InvoiceTransactionType.value(customer.getTransaction()))
			.set(CUSTOMER.STATUS, RegistryStatus.value(customer.getStatus()))
			.set(CUSTOMER.SCOPE,customer.getScope())
			.set(CUSTOMER.E_INVOICE, AonEnumUtils.getByte(customer.isEInvoice()))
			.set(CUSTOMER.INVOICING_GROUP, customer.getInvoicingGroup())
			.set(CUSTOMER.PROJECT_GROUPED, AonEnumUtils.getByte(customer.isProjectGrouped()))
			.set(CUSTOMER.DELIVERY_GROUPED, AonEnumUtils.getByte(customer.isDeliveryGrouped()))
			.set(CUSTOMER.DELIVERY_VALUATED, AonEnumUtils.getByte(customer.isDeliveryValuated()))
			.set(CUSTOMER.ACCOUNT,customer.getAccount().map( Account::getId ).orElse(null))
			.set(CUSTOMER.MODIFICATION_USER,ctx.getUser())
			.set(CUSTOMER.MODIFICATION_DATE,new Timestamp(new Date().getTime()))
			.where(CUSTOMER.REGISTRY.eq(customer.getId()))
			.execute();
		ctx.log().debug("UPDATE CUSTOMER id: {0}. ({1} rows)",customer.getId(),count);		
		return customer;
	}

	static void delete(AONContext ctx, Integer id) {
		ctx.checkWrite();
		int count = ctx.getDslContext().delete(CUSTOMER)
			.where(CUSTOMER.REGISTRY.eq(id))
			.execute();
		ctx.log().debug("DELETE CUSTOMER id: {0} ({1} rows)",id,count);
	}
	
	// ******************************************
	// ********** FULL CUSTOMER *****************
	// ******************************************
	static Optional<CustomerFull> getFull(AONContext ctx, Integer domain, Integer id){
		return CustomerHandler.get(ctx, domain, id)
			.map( c -> new CustomerFull().setRegistry(c) )
			.map( cf -> {
				RegistryHandler.fillChilds(ctx, cf);
				return cf;
			})
		;  
	}

	static CustomerFull save(AONContext ctx, CustomerFull customerFull) {
		ctx.checkWrite();
		CustomerAutoComplete.autoComplete(ctx, customerFull.getRegistry());
		CustomerValidation.validate(ctx, customerFull.getRegistry());
		customerFull.setRegistry(CustomerHandler.save(ctx, customerFull.getRegistry()));
		RegistryHandler.saveChilds(ctx, customerFull);
		return getFull(ctx, customerFull.getDomain(), customerFull.getId())
			.orElseThrow(() -> new AonCoreException("No se pudo recuperar el dato grabado."));
	}

	// *************************************************
	// ********** TEST PURPOSE METHODS *****************
	// *************************************************
	static Customer getRandom(AONContext ctx, int domain, CustomerFilter filter) {
		return select(ctx, domain)
			.and(CUSTOMER.DOMAIN.eq(domain))
			.and(CUSTOMER_PROPERTIES.getCondition(filter))
			.orderBy( DSL.rand() )
			.fetch()
			.stream()
			.map(new CustomerFiller())
			.findFirst()
			.orElse(null)
		;
	}
}

 