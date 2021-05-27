package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Account.ACCOUNT;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Supplier.SUPPLIER;

import java.sql.Timestamp;
import java.util.Date;
import java.util.function.Function;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Filter.SupplierFilter;
import com.esferalia.aon.occam.api.model.Properties.SupplierProperties;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.Supplier;
import com.esferalia.aon.occam.api.model.registry.SupplierFull;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.RegistryStatus;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.occam.impl.jooq.dao.AccountDAO.FullAccountFiller;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryDAO.RegistryPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.validation.SupplierAutoComplete;
import com.esferalia.aon.occam.impl.jooq.validation.SupplierValidation;
import com.esferalia.aon.watson.util.AonEnumUtils;

public class SupplierDAO {
	
	private static final SupplierPropertiesDAO SUPPLIER_PROPERTIES = new SupplierPropertiesDAO();
	public static class SupplierPropertiesDAO extends RegistryPropertiesDAO implements SupplierProperties {
		
		protected Condition[] getConditions(SupplierFilter filter) {
			if (filter == null) return new Condition[0];
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getRegistryProperty() {return new FilterDAO.PropertyDAO<Integer>(SUPPLIER.REGISTRY);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<Integer>(SUPPLIER.DOMAIN);}
		@Override public Property<Integer> getTariffProperty() {return new FilterDAO.PropertyDAO<Integer>(SUPPLIER.TARIFF);}
		@Override public Property<Byte> getWithholdingProperty() {return new FilterDAO.PropertyDAO<Byte>(SUPPLIER.WITHHOLDING);}
		@Override public Property<Byte> getWithholdingFarmerProperty() {return new FilterDAO.PropertyDAO<Byte>(SUPPLIER.WITHHOLDING_FARMER);}
		@Override public Property<Byte> getVatAccrualPaymentProperty() {return new FilterDAO.PropertyDAO<Byte>(SUPPLIER.VAT_ACCRUAL_PAYMENT);}
		@Override public Property<Byte> getTransactionProperty() {return new FilterDAO.PropertyDAO<Byte>(SUPPLIER.TRANSACTION);}
		@Override public Property<Byte> getStatusProperty() {return new FilterDAO.PropertyDAO<Byte>(SUPPLIER.STATUS);}
		@Override public Property<Integer> getScopeProperty() {return new FilterDAO.PropertyDAO<Integer>(SUPPLIER.SCOPE);}
		@Override public Property<Byte> getPurchaseValuatedProperty() {return new FilterDAO.PropertyDAO<Byte>(SUPPLIER.PURCHASE_VALUATED);}
		@Override public Property<Integer> getAccountProperty() { return new FilterDAO.PropertyDAO<Integer>(SUPPLIER.ACCOUNT);}
		@Override public Property<String> getCreationUserProperty() {return new FilterDAO.PropertyDAO<String>(SUPPLIER.CREATION_USER);}
		@Override public Property<Timestamp> getCreationDateProperty() {return new FilterDAO.PropertyDAO<Timestamp>(SUPPLIER.CREATION_DATE);}
		@Override public Property<String> getModificationUserProperty() {return new FilterDAO.PropertyDAO<String>(SUPPLIER.MODIFICATION_USER);}
		@Override public Property<Timestamp> getModificationDateProperty() {return new FilterDAO.PropertyDAO<Timestamp>(SUPPLIER.MODIFICATION_DATE);}
	}

	
	protected static class SupplierFiller implements Function<Record, Supplier> {

		@Override
		public Supplier apply(Record r) {
			return buildSupplier(r, REGISTRY);
		}
		
		public static Supplier buildSupplier(Record r, com.esferalia.aon.jooq.tables.Registry registry) {
			if(registry == null) registry = REGISTRY;
			return new Supplier()
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
					.setTariff(r.getValue(SUPPLIER.TARIFF))
					.setWithholding(r.getValue(SUPPLIER.WITHHOLDING)==1)
					.setWithholdingFarmer(r.getValue(SUPPLIER.WITHHOLDING_FARMER)==1)
					.setVatAccrualPayment(r.getValue(SUPPLIER.VAT_ACCRUAL_PAYMENT)==1)
					.setTransaction(InvoiceTransactionType.safeValueOf( r.getValue(SUPPLIER.TRANSACTION)))
					.setStatus(RegistryStatus.safeValueOf(r.getValue(SUPPLIER.STATUS)))
					.setScope(r.getValue(SUPPLIER.SCOPE))
					.setPurchaseValuated(r.getValue(SUPPLIER.PURCHASE_VALUATED)==1)
					.setAccount(r.getValue(SUPPLIER.ACCOUNT))
					.setCreationUser(r.getValue(SUPPLIER.CREATION_USER))
					.setCreationDate(r.getValue(SUPPLIER.CREATION_DATE))
					.setModificationDate(r.getValue(SUPPLIER.MODIFICATION_DATE))
					.setModificationUser(r.getValue(SUPPLIER.MODIFICATION_USER))
					;
		}
	}
	
	private static SelectConditionStep<Record> select(AONContext ctx, SupplierFilter filter) {
		return ctx.getDslContext().select()
				.from(SUPPLIER)
				.join(REGISTRY).on(REGISTRY.ID.eq(SUPPLIER.REGISTRY))
				.where(SUPPLIER_PROPERTIES.getConditions(filter));
		
	}

	public static Stream<Supplier> getStream(AONContext ctx, SupplierFilter filter){
		return select(ctx,filter)
			.fetch()
			.stream()
			.map(new SupplierFiller());
	}
	
	public static Stream<Supplier> getStream(AONContext ctx, SupplierFilter filter, int offset, int limit){
		return select(ctx,filter)
				.orderBy(REGISTRY.NAME)
				.offset(offset)
				.limit(limit)
				
				.fetch()
				.stream()
				.map(new SupplierFiller());
	}

	public static Supplier get(AONContext ctx, Integer id){
		return getStream(ctx, p -> p.getIdProperty().eq(id))
			.findFirst()
			.orElse(null);
	}
	
	public static Supplier save(AONContext ctx, Supplier supplier) {
		ctx.checkWrite();
		SupplierAutoComplete.autoComplete(ctx, supplier);
		SupplierValidation.validate(ctx, supplier);
		boolean nullId = (supplier.getId() == null); 
		supplier = RegistryDAO.save(ctx, supplier);
		if (nullId || get(ctx, supplier.getId()) == null ) {
			supplier = insert(ctx, supplier);
		} else {
			supplier = update(ctx, supplier);			
		}
		return supplier;
	}

	private static Supplier insert(AONContext ctx, Supplier supplier){
		ctx.getDslContext().insertInto(SUPPLIER)
			.set(SUPPLIER.REGISTRY,supplier.getId())
			.set(SUPPLIER.DOMAIN,supplier.getDomain().getId())
			.set(SUPPLIER.TARIFF,supplier.getTariff())
			.set(SUPPLIER.WITHHOLDING,AonEnumUtils.getByte(supplier.isWithholding()))
			.set(SUPPLIER.WITHHOLDING_FARMER,AonEnumUtils.getByte(supplier.isWithholdingFarmer()))
			.set(SUPPLIER.VAT_ACCRUAL_PAYMENT,AonEnumUtils.getByte(supplier.isVatAccrualPayment()))
			.set(SUPPLIER.TRANSACTION,AonEnumUtils.getByte(supplier.getTransaction()))
			.set(SUPPLIER.STATUS, supplier.getStatus().value())
			.set(SUPPLIER.SCOPE,supplier.getScope())
			.set(SUPPLIER.PURCHASE_VALUATED,AonEnumUtils.getByte(supplier.isPurchaseValuated()))
			.set(SUPPLIER.ACCOUNT,supplier.getAccount())
			.set(SUPPLIER.CREATION_USER,ctx.getUser())
			.set(SUPPLIER.CREATION_DATE,new Timestamp(new Date().getTime()))
			.execute();
		ctx.log().info("INSERT SUPPLIER id: " + supplier.getId());		
		return supplier;
	}
	
	private static Supplier update(AONContext ctx, Supplier supplier){
		ctx.checkWrite();
		SupplierAutoComplete.autoComplete(ctx, supplier);
		SupplierValidation.validate(ctx, supplier);
		supplier = RegistryDAO.save(ctx, supplier);
		int count = ctx.getDslContext().update(SUPPLIER)
			.set(SUPPLIER.DOMAIN,supplier.getDomain().getId())
			.set(SUPPLIER.TARIFF,supplier.getTariff())
			.set(SUPPLIER.WITHHOLDING,AonEnumUtils.getByte(supplier.isWithholding()))
			.set(SUPPLIER.WITHHOLDING_FARMER,AonEnumUtils.getByte(supplier.isWithholdingFarmer()))
			.set(SUPPLIER.VAT_ACCRUAL_PAYMENT,AonEnumUtils.getByte(supplier.isVatAccrualPayment()))
			.set(SUPPLIER.TRANSACTION,AonEnumUtils.getByte(supplier.getTransaction()))
			.set(SUPPLIER.STATUS, supplier.getStatus().value())
			.set(SUPPLIER.SCOPE,supplier.getScope())
			.set(SUPPLIER.PURCHASE_VALUATED,AonEnumUtils.getByte(supplier.isPurchaseValuated()))
			.set(SUPPLIER.ACCOUNT,supplier.getAccount())
			.set(SUPPLIER.MODIFICATION_USER,ctx.getUser())
			.set(SUPPLIER.MODIFICATION_DATE,new Timestamp(new Date().getTime()))
			.where(SUPPLIER.REGISTRY.eq(supplier.getId()))
			.execute();
		ctx.log().info("UPDATE SUPPLIER id: " + supplier.getId() + ". (" + count + " rows)");		
		return supplier;
	}

	public static void delete(AONContext ctx, Integer id) {
		ctx.checkWrite();
		SupplierValidation.validateDeletion(ctx, id);
		int count = ctx.getDslContext().delete(SUPPLIER)
			.where(SUPPLIER.REGISTRY.eq(id))
			.execute();
		ctx.log().info("DELETE SUPPLIER id:" + id + " ("+count+" rows)");
	}

	// *************************************************
	// ************ SUPPLIER ACCOUNT *******************
	// *************************************************
	public static Account getSupplierAccount(AONContext ctx, Integer registry) {
		return ctx.getDslContext().select(ACCOUNT.fields())
			.from ( SUPPLIER )
			.join( ACCOUNT ).on(SUPPLIER.ACCOUNT.eq(ACCOUNT.ID))
			.where(SUPPLIER.REGISTRY.eq(registry))
			.fetch()
			.stream()
			.map(new FullAccountFiller() )
			.findFirst()
			.orElse(null);
	}
	public static void updateSupplierAccount(AONContext ctx, Integer registry, Integer account) {
		ctx.checkWrite();
		ctx.getDslContext().update(SUPPLIER)
			.set(SUPPLIER.ACCOUNT,account)
			.set(SUPPLIER.MODIFICATION_USER,ctx.getUser())
			.set(SUPPLIER.MODIFICATION_DATE, new Timestamp( System.currentTimeMillis()) )
			.where(SUPPLIER.REGISTRY.eq(registry))
			.execute();
		ctx.log().info("ACCOUNT " + account + " LINKED TO SUPPLIER " + registry);
	}

	// ******************************************
	// ********** FULL CREDITOR *****************
	// ******************************************
	public static SupplierFull getFull(AONContext ctx, Integer id){
		SupplierFull full = new SupplierFull();
		full.setRegistry(SupplierDAO.get(ctx, id));  
		RegistryDAO.fillChilds(ctx, full);
		if (full.getRegistry() != null && full.getRegistry().getAccount() != null) {
			full.setAccount(AccountDAO.get(ctx, full.getRegistry().getAccount()));	
		}
		return full;
	}
	
	public static SupplierFull save(AONContext ctx, SupplierFull supplierFull) {
		ctx.checkWrite();
		SupplierAutoComplete.autoComplete(ctx, supplierFull.getRegistry());
		SupplierValidation.validate(ctx, supplierFull.getRegistry());
		supplierFull.setRegistry(SupplierDAO.save(ctx, supplierFull.getRegistry()));
		RegistryDAO.saveChilds(ctx, supplierFull);
		return getFull(ctx, supplierFull.getId());
	}

	// *************************************************
	// ********** TEST PURPOSE METHODS *****************
	// *************************************************
	public static Supplier getRandom(AONContext ctx, SupplierFilter filter) {
		return select(ctx,filter)
			.orderBy( DSL.rand() )
			.fetch()
			.stream()
			.map(new SupplierFiller())
			.findFirst()
			.orElse(null);
	}

	// ***********************************************************
	// ***********************************************************
	// ***********************************************************
	public static Stream<Supplier> getBasicSuppliers(AONContext ctx, SupplierFilter filter) {
		ctx.checkRead();
		return select(ctx, filter)
			.and( SUPPLIER.DOMAIN.eq(ctx.getDomainId()) )
			.and(SecurityDAO.getSecurityLevelCondition(ctx, ctx.getUser(), REGISTRY.SECURITY_LEVEL))
			.and(SecurityDAO.getUserScopesCondition(ctx,ctx.getUser(),SUPPLIER.SCOPE))
			.orderBy(REGISTRY.NAME)
			.fetch()
			.stream()
			.map(new SupplierFiller());			
	}
	// ***********************************************************
	// ***********************************************************
	// ***********************************************************
	
}

