package com.esferalia.aon.occam.impl.jooq.dao;


import static com.esferalia.aon.jooq.tables.Account.ACCOUNT;
import static com.esferalia.aon.jooq.tables.Company.COMPANY;
import static com.esferalia.aon.jooq.tables.Customer.CUSTOMER;
import static com.esferalia.aon.jooq.tables.CustomerFee.CUSTOMER_FEE;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Person.PERSON;
import static com.esferalia.aon.jooq.tables.Project.PROJECT;
import static com.esferalia.aon.jooq.tables.Raddinfo.RADDINFO;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Rrelationship.RRELATIONSHIP;

import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Field;
import  org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.SelectOnConditionStep;
import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter.CustomerFilter;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Properties.CustomerProperties;
import com.esferalia.aon.occam.api.model.registry.CustomerFull;
import com.esferalia.aon.occam.api.model.registry.CustomerParams;
import com.esferalia.aon.occam.api.model.registry.NoteType;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryNote;
import com.esferalia.aon.occam.api.model.registry.Target;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.tariff.Tariff;
import com.esferalia.aon.occam.api.model.type.DomainType;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.RegistryStatus;
import com.esferalia.aon.occam.impl.jooq.dao.AccountDAO.FullAccountFiller;
import com.esferalia.aon.occam.impl.jooq.dao.FillerDAO.DomainFiller;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryDAO.RegistryFiller;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryDAO.RegistryPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.validation.CustomerAutoComplete;
import com.esferalia.aon.occam.impl.jooq.validation.CustomerValidation;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonEnumUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class CustomerDAO {
	private CustomerDAO() {
		
	}
	public static final com.esferalia.aon.jooq.tables.Registry CUSTOMER_ALIAS = REGISTRY.as("registry_customer");

	private static final CustomerPropertiesDAO CUSTOMER_PROPERTIES = new CustomerPropertiesDAO();
	public static class CustomerPropertiesDAO extends RegistryPropertiesDAO implements CustomerProperties {
		
		protected Condition[] getConditions(CustomerFilter filter) {
			if (filter == null) return new Condition[0];
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getRegistryProperty() {return new FilterDAO.PropertyDAO<>(CUSTOMER.REGISTRY);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(CUSTOMER.DOMAIN);}
		@Override public Property<Integer> getTariffProperty() {return new FilterDAO.PropertyDAO<>(CUSTOMER.TARIFF);}
		@Override public Property<Byte> getSurchargeProperty() {return new FilterDAO.PropertyDAO<>(CUSTOMER.SURCHARGE);}
		@Override public Property<Byte> getWithholdingProperty() {return new FilterDAO.PropertyDAO<>(CUSTOMER.WITHHOLDING);}
		@Override public Property<Byte> getTransactionProperty() {return new FilterDAO.PropertyDAO<>(CUSTOMER.TRANSACTION);}
		@Override public Property<Byte> getStatusProperty() {return new FilterDAO.PropertyDAO<>(CUSTOMER.STATUS);}
		@Override public Property<Integer> getScopeProperty() {return new FilterDAO.PropertyDAO<>(CUSTOMER.SCOPE);}
		@Override public Property<Byte> getEInvoiceProperty() {return new FilterDAO.PropertyDAO<>(CUSTOMER.E_INVOICE);}
		@Override public Property<Integer> getInvoicingGroupProperty() {return new FilterDAO.PropertyDAO<>(CUSTOMER.INVOICING_GROUP);}
		@Override public Property<Byte> getProjectGroupedProperty() {return new FilterDAO.PropertyDAO<>(CUSTOMER.PROJECT_GROUPED);}
		@Override public Property<Byte> getDeliveryGroupedProperty() {return new FilterDAO.PropertyDAO<>(CUSTOMER.DELIVERY_GROUPED);}
		@Override public Property<Byte> getDeliveryValuatedProperty() {return new FilterDAO.PropertyDAO<>(CUSTOMER.DELIVERY_VALUATED);}
		@Override public Property<Integer> getAccountProperty() {return new FilterDAO.PropertyDAO<>(CUSTOMER.ACCOUNT);}
		@Override public Property<String> getCreationUserProperty() {return new FilterDAO.PropertyDAO<>(CUSTOMER.CREATION_USER);}
		@Override public Property<Timestamp> getCreationDateProperty() {return new FilterDAO.PropertyDAO<>(CUSTOMER.CREATION_DATE);}
		@Override public Property<String> getModificationUserProperty() {return new FilterDAO.PropertyDAO<>(CUSTOMER.MODIFICATION_USER);}
		@Override public Property<Timestamp> getModificationDateProperty() {return new FilterDAO.PropertyDAO<>(CUSTOMER.MODIFICATION_DATE);}
        @Override public Property<Integer> getProjectTypeProperty() {return new FilterDAO.PropertyDAO<>(PROJECT.PROJECT_TYPE);}	
        
        @Override public Property<Integer> getRegistryRelationProperty() {return new FilterDAO.PropertyDAO<>(RRELATIONSHIP.ID);}	
        @Override public Property<Integer> getRelatedRegistryProperty() {return new FilterDAO.PropertyDAO<>(RRELATIONSHIP.RELATED_REGISTRY); }
		
        @Override public Property<Integer> getRaddInfoDomainProperty() {return new FilterDAO.PropertyDAO<>(RADDINFO.ID); }
        
		}

	protected static class CustomerFiller extends Filler  implements Function<Record, Customer> {

		@Override
		public Customer apply(Record r) {
			return build(r);
		}
		
		public static Customer build(Record r) {
			return buildCustomer(r, REGISTRY);
		}
		
		public static Customer buildCustomer(Record r, com.esferalia.aon.jooq.tables.Registry registry) {
			Customer customer = new Customer()
					.copy(RegistryFiller.build(r, registry))
					.setAccount(getValue(r, CUSTOMER.ACCOUNT))
					.setCreationDate(getValue(r, CUSTOMER.CREATION_DATE))
					.setCreationUser(getValue(r, CUSTOMER.CREATION_USER))
					.setDeliveryGrouped(getBoolean(r, CUSTOMER.DELIVERY_GROUPED))
					.setDeliveryValuated(getBoolean(r, CUSTOMER.DELIVERY_VALUATED))
					.setEInvoice(getBoolean(r, CUSTOMER.E_INVOICE))
					.setInvoicingGroup(getValue(r, CUSTOMER.INVOICING_GROUP))
					.setModificationDate(getValue(r, CUSTOMER.MODIFICATION_DATE))
					.setModificationUser(getValue(r, CUSTOMER.MODIFICATION_USER))
					.setProjectGrouped(getBoolean(r, CUSTOMER.PROJECT_GROUPED))
					.setScope(new Scope().setId(getValue(r, CUSTOMER.SCOPE)))
					.setSurcharge(getBoolean(r, CUSTOMER.SURCHARGE))
					.setTariff(getValue(r, CUSTOMER.TARIFF))
					.setTransaction(InvoiceTransactionType.safeValueOf(getValue(r, CUSTOMER.TRANSACTION)))
					.setWithholding(getBoolean(r, CUSTOMER.WITHHOLDING))
					.setStatus(RegistryStatus.safeValueOf(getValue(r, CUSTOMER.STATUS)))
					.setRelationship(getValue(r, RRELATIONSHIP.ID)!=null || (checkField(r, hasDomain) && r.get(hasDomain)) );
			
			if(checkField(r, PERSON.REGISTRY)) {
				Registry customerReg = customer.get();
				customerReg.setPersonName(r.get(PERSON.NAME));
				customerReg.setPersonFirstsurname(r.get(PERSON.FIRST_SURNAME));
				customerReg.setPersonSecondsurname(r.get(PERSON.SECOND_SURNAME));
				customer.copy(customerReg);
			}
			
			return customer;
		}
	}
	
	private static Field<Boolean> hasDomain = DSL.exists(
		    DSL.selectOne()
		       .from(RADDINFO)
		       .where(RADDINFO.REGISTRY.eq(REGISTRY.ID))
		       .and(RADDINFO.ATTRIBUTE.like("AON_DOMAIN%"))
		).as("has_domain");
	
	private static SelectOnConditionStep<Record> select(AONContext ctx) {
		return ctx.getDslContext()
	            .selectDistinct(CUSTOMER.fields())
	            .select(REGISTRY.fields())
	            .select(PERSON.fields())
	            .select(DOMAIN.fields())
	            .select(RRELATIONSHIP.ID)
	            .select(hasDomain)
	        .from(CUSTOMER)
	        .join(REGISTRY).on(REGISTRY.ID.eq(CUSTOMER.REGISTRY))
	        .join(DOMAIN).on(CUSTOMER.DOMAIN.eq(DOMAIN.ID))
	        .leftOuterJoin(PERSON).on(PERSON.REGISTRY.eq(REGISTRY.ID))
	        .leftOuterJoin(PROJECT).on(PROJECT.REGISTRY.eq(REGISTRY.ID))
	        .leftOuterJoin(RRELATIONSHIP).on(RRELATIONSHIP.REGISTRY.eq(REGISTRY.ID).and(RRELATIONSHIP.RELATIONSHIP.eq(-1)));
		
	}
	
	private static SelectConditionStep<Record> select(AONContext ctx, CustomerFilter filter) {
		return select(ctx)
			.where(CUSTOMER_PROPERTIES.getConditions(filter));
		
	}

	public static Stream<Customer> getStream(AONContext ctx, CustomerFilter filter){
		return select(ctx,filter)
			.fetch()
			.stream()
			.map(new CustomerFiller());
	}
	
	public static Stream<Customer> getStreamSuggestion(AONContext ctx, Integer domainId, String query) {
		return select(ctx)
			.where(CUSTOMER.DOMAIN.eq(domainId))
			.and( (REGISTRY.NAME.containsIgnoreCase(query))
				.or(REGISTRY.ALIAS.containsIgnoreCase(query))
				.or(REGISTRY.DOCUMENT.containsIgnoreCase(query))
			)
		.limit(50)	
		.fetch()
		.stream()
		.map(new CustomerFiller());
	}
	
	public static Stream<Customer> getStream(AONContext ctx, CustomerFilter filter, int offset, int limit){
		return select(ctx,filter)
				.orderBy(REGISTRY.NAME)
				.offset(offset)
				.limit(limit)				
				.fetch()
				.stream()
				.map(new CustomerFiller());
	}
	
	public static Stream<Customer> getSigStream(AONContext ctx, CustomerFilter filter, int offset, int limit){
		return ctx.getDslContext()
	            .selectDistinct(CUSTOMER.fields())
	            .select(REGISTRY.fields())
	            .select(DOMAIN.fields())
	            .select(hasDomain)
		        .from(CUSTOMER)
		        .join(REGISTRY).on(REGISTRY.ID.eq(CUSTOMER.REGISTRY))
		        .join(DOMAIN).on(CUSTOMER.DOMAIN.eq(DOMAIN.ID))
		        .leftOuterJoin(PROJECT).on(PROJECT.REGISTRY.eq(REGISTRY.ID))
		        .where(CUSTOMER_PROPERTIES.getConditions(filter))
				.orderBy(REGISTRY.NAME)
				.offset(offset)
				.limit(limit)				
				.fetch()
				.stream()
				.filter(r -> r.getValue(hasDomain))
				.map(new CustomerFiller());
	}
	
	public static Stream<Customer> getSigNotLinkedStream(AONContext ctx, CustomerFilter filter, int offset, int limit){
		return ctx.getDslContext()
	            .selectDistinct(CUSTOMER.fields())
	            .select(REGISTRY.fields())
	            .select(DOMAIN.fields())
	            .select(hasDomain)
		        .from(CUSTOMER)
		        .join(REGISTRY).on(REGISTRY.ID.eq(CUSTOMER.REGISTRY))
		        .join(DOMAIN).on(CUSTOMER.DOMAIN.eq(DOMAIN.ID))
		        .leftOuterJoin(PROJECT).on(PROJECT.REGISTRY.eq(REGISTRY.ID))
		        .where(CUSTOMER_PROPERTIES.getConditions(filter))
				.orderBy(REGISTRY.NAME)
				.offset(offset)
				.limit(limit)				
				.fetch()
				.stream()
				.filter(r -> !r.getValue(hasDomain))
				.map(new CustomerFiller());
	}
	
	public static List<Customer> getList(AONContext ctx, CustomerFilter filter) {
		return getStream(ctx, filter).collect(Collectors.toList());
	}

	public static Customer get(AONContext ctx, Integer id){
		return get(ctx, p -> p.getIdProperty().eq(id));
	}
	
	public static Customer get(AONContext ctx, CustomerFilter filter){
		return getStream(ctx, filter)
			.findFirst()
			.orElse(new Customer());
	}
	
	public static List<Customer> getCustomerWithoutFee(AONContext ctx) {
		return ctx.getDslContext().select()
		.from(CUSTOMER)
		.join(REGISTRY).on(REGISTRY.ID.eq(CUSTOMER.REGISTRY))
		.leftOuterJoin(CUSTOMER_FEE).on(CUSTOMER.REGISTRY.eq(CUSTOMER_FEE.CUSTOMER)
			.and(CUSTOMER_FEE.FINAL_DATE.isNull().or(CUSTOMER_FEE.FINAL_DATE.ge(AonDateUtils.toSql(new Date()))))
		)
		.where(CUSTOMER.DOMAIN.eq(ctx.getDomainId()))
		.and(CUSTOMER.STATUS.eq(RegistryStatus.ACTIVE.value()))
		.and(CUSTOMER_FEE.ID.isNull())
		.fetch().stream().map(new CustomerFiller())
		.collect(Collectors.toList());
	}
	
	public static List<Customer> getCustomerWithoutFee(AONContext ctx, CustomerParams params) {
		Condition condition = paramsToCondition(ctx, params);
		
		SelectConditionStep<Record> select = ctx.getDslContext().select()
				.from(CUSTOMER)
				.join(REGISTRY).on(REGISTRY.ID.eq(CUSTOMER.REGISTRY))
				.leftOuterJoin(CUSTOMER_FEE).on(CUSTOMER_FEE.CUSTOMER.eq(CUSTOMER.REGISTRY))
				.where(condition)
				.and(CUSTOMER_FEE.ID.isNull());
		
		if(params.isAsc()) {
			if(AonStringUtils.equals(params.getOrderBy(), "name"))
				select.orderBy(REGISTRY.NAME);
			else if(AonStringUtils.equals(params.getOrderBy(), "document"))
				select.orderBy(REGISTRY.DOCUMENT);
			else if(AonStringUtils.equals(params.getOrderBy(), "status"))
				select.orderBy(CUSTOMER.STATUS);
		} else {
			if(AonStringUtils.equals(params.getOrderBy(), "name"))
				select.orderBy(REGISTRY.NAME.desc());
			else if(AonStringUtils.equals(params.getOrderBy(), "document"))
				select.orderBy(REGISTRY.DOCUMENT.desc());
			else if(AonStringUtils.equals(params.getOrderBy(), "status"))
				select.orderBy(CUSTOMER.STATUS.desc());
		}
		
		List<Customer> customerList = select
			.limit(params.getOffset(), params.getLimit())
			.fetch()
			.stream()
			.map(new CustomerFiller())
			.collect(Collectors.toList());
		
		System.out.println("------------ Customer DAO --> getCustomerWithoutFee: " + customerList.size());
		
		return customerList;
		
	}
	
	private static Condition paramsToCondition(AONContext ctx, CustomerParams params) {
		Condition condition = CUSTOMER.DOMAIN.eq(params.getDomain());
		
		User user = SecurityDAO.getUser(ctx);
		if(user.getDomain().getId() == ctx.getDomainId()) {
			Integer[] userScopes = SecurityDAO.getUserScopes(ctx);
			condition = condition.and(CUSTOMER.SCOPE.in(userScopes));
		}
		
		if(AonStringUtils.isNotBlank(params.getDescription())) 
			condition = condition.and(REGISTRY.NAME.like("%" + params.getDescription() + "%")
						.or(REGISTRY.ALIAS.like("%" + params.getDescription() + "%"))
						.or(REGISTRY.DOCUMENT.like("%" + params.getDescription() + "%"))
					);
		
		if(null != params.getStatus())
			condition = condition.and(CUSTOMER.STATUS.eq(params.getStatus()));
		
		if(null != params.getCustomerIds() && params.getCustomerIds().size() > 0)
			condition = condition.and(CUSTOMER.REGISTRY.in(params.getCustomerIds()));
		
		return condition;
	}
	
	public static Customer save(AONContext ctx, Customer customer) {
		ctx.checkWrite();
		CustomerAutoComplete.autoComplete(ctx, customer);
		CustomerValidation.validate(ctx, customer);
		boolean nullId = (customer.getId() == null); 
		customer = RegistryDAO.save(ctx, customer);
		return (nullId || get(ctx, customer.getId()).isEmpty())
			? insert(ctx, customer)
			: update(ctx, customer);			
	}
	
	private static Customer insert(AONContext ctx, Customer customer){
		ctx.getDslContext()
		.insertInto(CUSTOMER)
			.set(CUSTOMER.REGISTRY,customer.getId())
			.set(CUSTOMER.DOMAIN,customer.getDomain().getId())
			.set(CUSTOMER.TARIFF,customer.getTariff())
			.set(CUSTOMER.SURCHARGE,AonEnumUtils.getByte(customer.isSurcharge()))
			.set(CUSTOMER.WITHHOLDING,AonEnumUtils.getByte(customer.isWithholding()))
			.set(CUSTOMER.TRANSACTION,AonEnumUtils.getByte(customer.getTransaction()))
			.set(CUSTOMER.STATUS, customer.getStatus().value())
			.set(CUSTOMER.SCOPE, customer.getScope().getId())
			.set(CUSTOMER.E_INVOICE, AonEnumUtils.getByte(customer.isEInvoice()))
			.set(CUSTOMER.INVOICING_GROUP, customer.getInvoicingGroup())
			.set(CUSTOMER.PROJECT_GROUPED, AonEnumUtils.getByte(customer.isProjectGrouped()))
			.set(CUSTOMER.DELIVERY_GROUPED, AonEnumUtils.getByte(customer.isDeliveryGrouped()))
			.set(CUSTOMER.DELIVERY_VALUATED, AonEnumUtils.getByte(customer.isDeliveryValuated()))
			.set(CUSTOMER.ACCOUNT, customer.getAccount())
			.set(CUSTOMER.CREATION_USER, ctx.getUser())
			.set(CUSTOMER.CREATION_DATE, new Timestamp(new Date().getTime()))
			.execute();
		
		ctx.log().debug("INSERT CUSTOMER id: {0}", customer.getId());	
		
		saveTargetByCustomer(ctx, customer); // SAVE POTENTIAL CLIENT
		
		return customer;
	}
	
	public static Target saveTargetByCustomer(AONContext ctx, Customer customer) {
		Target target = TargetDAO.get(ctx, f -> f.getIdProperty().eq(customer.getId()));
	    if(target.isEmpty()) {
	        target = TargetDAO.save(ctx, 
	        		new Target()
	                .copy(customer)
	                .setScope(customer.getScope())
	                .setSurcharge(customer.isSurcharge())
	                .setTariff(customer.getTariff()!=null ? new Tariff().setId(customer.getTariff()) : null)
	                .setTransaction(customer.getTransaction())
	                .setWithholding(customer.isWithholding())
	         );
	    }
	    
	    return target;
	}

	private static Customer update(AONContext ctx, Customer customer){
		int count = ctx.getDslContext().update(CUSTOMER)
			.set(CUSTOMER.DOMAIN, customer.getDomain().getId())
			.set(CUSTOMER.TARIFF, customer.getTariff())
			.set(CUSTOMER.SURCHARGE, AonEnumUtils.getByte(customer.isSurcharge()))
			.set(CUSTOMER.WITHHOLDING, AonEnumUtils.getByte(customer.isWithholding()))
			.set(CUSTOMER.TRANSACTION, AonEnumUtils.getByte(customer.getTransaction()))
			.set(CUSTOMER.STATUS, customer.getStatus().value())
			.set(CUSTOMER.SCOPE, customer.getScope().getId())
			.set(CUSTOMER.E_INVOICE,AonEnumUtils.getByte(customer.isEInvoice()))
			.set(CUSTOMER.INVOICING_GROUP, customer.getInvoicingGroup())
			.set(CUSTOMER.PROJECT_GROUPED, AonEnumUtils.getByte(customer.isProjectGrouped()))
			.set(CUSTOMER.DELIVERY_GROUPED, AonEnumUtils.getByte(customer.isDeliveryGrouped()))
			.set(CUSTOMER.DELIVERY_VALUATED, AonEnumUtils.getByte(customer.isDeliveryValuated()))
			.set(CUSTOMER.ACCOUNT, customer.getAccount())
			.set(CUSTOMER.MODIFICATION_USER, ctx.getUser())
			.set(CUSTOMER.MODIFICATION_DATE, new Timestamp(new Date().getTime()))
			.where(CUSTOMER.REGISTRY.eq(customer.getId()))
			.execute();
		ctx.log().debug("UPDATE CUSTOMER id: {0} ({1} rows)", customer.getId(),count);		
		return customer;
	}

	public static void delete(AONContext ctx, Integer id) {
		ctx.checkWrite();
		CustomerValidation.validateDeletion(ctx, id);
		int count = ctx.getDslContext().delete(CUSTOMER)
			.where(CUSTOMER.REGISTRY.eq(id))
			.execute();
		ctx.log().debug("DELETE CUSTOMER id: {0} ({1} rows)",id,count);
	}

	public static Account getCustomerAccount(AONContext ctx, Integer customerId) {
		return ctx.getDslContext().select(ACCOUNT.fields())
			.from ( CUSTOMER )
			.join( ACCOUNT ).on(CUSTOMER.ACCOUNT.eq(ACCOUNT.ID))
			.where(CUSTOMER.REGISTRY.eq(customerId))
			.fetch()
			.stream()
			.map(new FullAccountFiller () )
			.findFirst()
			.orElse(null);
	}
	
	public static void updateCustomerAccount(AONContext ctx, Integer customerId, Integer account) {
		ctx.checkWrite();
		ctx.getDslContext().update(CUSTOMER)
			.set(CUSTOMER.ACCOUNT,account)
			.set(CUSTOMER.MODIFICATION_USER,ctx.getUser())
			.set(CUSTOMER.MODIFICATION_DATE, new Timestamp( System.currentTimeMillis()) )
			.where(CUSTOMER.REGISTRY.eq(customerId))
			.execute();
		ctx.log().debug("ACCOUNT {0} LINKED TO CUSTOMER {1}",account,customerId);
	}
	
	public static Account ensureAccount(AONContext ctx, Integer customerId) {
		Account account = getCustomerAccount(ctx, customerId);
		if (account == null) {
			Customer customer = get(ctx, customerId);
			if (customer == null || customer.getId()==null) {
				throw new AonCoreException("Cliente no encontrado");
			}
			account = new Account()
				.setDomain( customer.getDomain().getId() )
				.setCode( AccountDAO.getNextAccountCode(ctx,"4300" ) )
				.setDescription( customer.getName() )
				.setAlias( customer.getAlias() )
				.setActive( true );
			account = AccountDAO.save( ctx, account);
		}
		return account;
	}

	public static Domain getDomainLinked(AONContext ctx, Integer id) {
		ctx.checkRead();
		Customer customer = get(ctx, id);
		if (customer == null) throw new AonCoreException("Id suministrado incorrecto. Cliente no encontrado");
		if (AonStringUtils.isBlank(customer.getDocument())) throw new AonCoreException("Cliente sin NIF / CIF / DNI");
		if (customer.getDomain() == null || customer.getDomain().getId() == null) throw new AonCoreException("Cliente sin valor en el campo dominio"); 
		Domain domain = DomainDAO.getDomain(ctx, customer.getDomain().getId());
		if (domain.getDomainType() != DomainType.OFFICE) {
			throw new AonCoreException("El tipo de dominio no es \"DESPACHO\"");	
		}
		LinkedList<Domain> domains =  ctx.getDslContext()
			.select( DOMAIN.fields() )
			.from(COMPANY)
			.join(REGISTRY).on(REGISTRY.ID.eq(COMPANY.REGISTRY))
			.join(DOMAIN).on(DOMAIN.ID.eq(COMPANY.DOMAIN))
			.where(REGISTRY.DOCUMENT.eq(customer.getDocument()))
			.fetch()
			.stream()
			.map(new DomainFiller())
			.collect( Collectors.toCollection(LinkedList::new));
		if (domains != null && domains.size() > 0) {
			if (domains.size() > 1) {
				throw new AonCoreException("Mas de un dominio encontrado para el cliente");			
			}
			return domains.getFirst(); 
		}
		return null;
	}

	public static List<Domain> getDomainOfficeLinked(AONContext ctx, String document) {
		ctx.checkRead();
		return ctx.getDslContext()
		.select(DOMAIN.fields())
		.from(DOMAIN)
		.join(CUSTOMER).on(DOMAIN.ID.eq(CUSTOMER.DOMAIN))
		.join(REGISTRY).on(CUSTOMER.REGISTRY.eq(REGISTRY.ID))
		.where(DOMAIN.TYPE.eq(DomainType.OFFICE.value())
		.and(REGISTRY.DOCUMENT.eq(document)))
		.fetch().stream().map(new DomainFiller())
		.collect( Collectors.toCollection(LinkedList::new));
	}
	
	// ******************************************
	// ********** FULL CUSTOMER *****************
	// ******************************************
	public static CustomerFull getFull(AONContext ctx, Integer id){
		CustomerFull full = new CustomerFull();
		full.setRegistry(CustomerDAO.get(ctx, id));  
		RegistryDAO.fillChilds(ctx, full);
		if (full.getRegistry() != null && full.getRegistry().getAccount() != null) {
			full.setAccount(AccountDAO.get(ctx, full.getRegistry().getAccount()));	
		}
		RegistryNote observationNote = RegistryNoteDAO.get(ctx, f -> f.getRegistryProperty().eq(id).and(f.getNoteTypeProperty().eq(NoteType.OBSERVATION.value())));
		if(null != observationNote.getId())
			full.getRegistry().setObservation(observationNote.getComments());
		
		return full;
	}

	public static CustomerFull save(AONContext ctx, CustomerFull customerFull) {
		ctx.checkWrite();
		CustomerAutoComplete.autoComplete(ctx, customerFull.getRegistry());
		CustomerValidation.validate(ctx, customerFull.getRegistry());
		
		String observation = customerFull.getRegistry().getObservation();
		
		customerFull.setRegistry(CustomerDAO.save(ctx, customerFull.getRegistry()));
		
		RegistryNoteDAO.saveRegistryObservation(ctx, customerFull.getDomain(), customerFull.getRegistry().getId(), observation);
		
		RegistryDAO.saveChilds(ctx, customerFull);
		return getFull(ctx, customerFull.getId());
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
