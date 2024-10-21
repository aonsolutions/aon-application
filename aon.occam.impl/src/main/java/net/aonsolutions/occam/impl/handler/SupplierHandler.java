package net.aonsolutions.occam.impl.handler;

import static com.esferalia.aon.jooq.tables.Account.ACCOUNT;
import static com.esferalia.aon.jooq.tables.Customer.CUSTOMER;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Supplier.SUPPLIER;

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
import net.aonsolutions.occam.api.model.Filter.Property;
import net.aonsolutions.occam.api.model.Filter.SupplierFilter;
import net.aonsolutions.occam.api.model.Properties.SupplierProperties;
import net.aonsolutions.occam.api.model.Supplier;
import net.aonsolutions.occam.api.model.SupplierFull;
import net.aonsolutions.occam.api.model.type.Country;
import net.aonsolutions.occam.api.model.type.DocumentType;
import net.aonsolutions.occam.api.model.type.InvoiceTransactionType;
import net.aonsolutions.occam.api.model.type.RegistryStatus;
import net.aonsolutions.occam.impl.AONContext;
import net.aonsolutions.occam.impl.handler.AccountHandler.AccountFiller;
import net.aonsolutions.occam.impl.handler.RegistryHandler.RegistryPropertiesHandler;

class SupplierHandler extends AbsHandler {
	private SupplierHandler() {
	}
	
	private static final SupplierPropertiesHandler SUPPLIER_PROPERTIES = new SupplierPropertiesHandler();
	private static class SupplierPropertiesHandler extends RegistryPropertiesHandler implements SupplierProperties {
		
		protected Condition getCondition(SupplierFilter filter) {
			if (filter == null) return DSL.trueCondition();
			FilterHandler filterDAO = (FilterHandler) filter.filter(this);
			return filterDAO.getCondition();
		}
		
		@Override public Property<Integer> getTariffProperty(){return new FilterHandler.PropertyDAO<>(SUPPLIER.TARIFF);}
		@Override public Property<Byte> getWithholdingProperty() {return new FilterHandler.PropertyDAO<>(SUPPLIER.WITHHOLDING);}
		@Override public Property<Byte> getWithholdingFarmerProperty() {return new FilterHandler.PropertyDAO<>(SUPPLIER.WITHHOLDING_FARMER);}
		@Override public Property<Byte> getVatAccrualPaymentProperty() {return new FilterHandler.PropertyDAO<>(SUPPLIER.VAT_ACCRUAL_PAYMENT);}
		@Override public Property<Byte> getTransactionProperty() {return new FilterHandler.PropertyDAO<>(SUPPLIER.TRANSACTION);}
		@Override public Property<Byte> getStatusProperty() {return new FilterHandler.PropertyDAO<>(SUPPLIER.STATUS);}
		@Override public Property<Integer> getScopeProperty() {return new FilterHandler.PropertyDAO<>(SUPPLIER.SCOPE);}
		@Override public Property<Byte> getPurchaseValuatedProperty() {return new FilterHandler.PropertyDAO<>(SUPPLIER.PURCHASE_VALUATED);}
		@Override public Property<Integer> getAccountProperty() { return new FilterHandler.PropertyDAO<>(SUPPLIER.ACCOUNT);}
		@Override public Property<String> getCreationUserProperty() {return new FilterHandler.PropertyDAO<>(SUPPLIER.CREATION_USER);}
		@Override public Property<Timestamp> getCreationDateProperty() {return new FilterHandler.PropertyDAO<>(SUPPLIER.CREATION_DATE);}
		@Override public Property<String> getModificationUserProperty() {return new FilterHandler.PropertyDAO<>(SUPPLIER.MODIFICATION_USER);}
		@Override public Property<Timestamp> getModificationDateProperty() {return new FilterHandler.PropertyDAO<>(SUPPLIER.MODIFICATION_DATE);}
	}

	
	private static class SupplierAutoComplete {
		
		private SupplierAutoComplete() {
		}
		
		private static final BiConsumer<AONContext,Supplier> COMPLETE_TRANSACTION = (ctx,supplier) -> {
			if (supplier.getTransaction() == null) {
				ctx.log().debug("\t saving supplier: autocomplete transaction: {0}",InvoiceTransactionType.NATIONAL);
				supplier.setTransaction(InvoiceTransactionType.NATIONAL);
			}
		};

		private static final BiConsumer<AONContext,Supplier> COMPLETE_STATUS = (ctx,supplier) -> {
			if (supplier.getStatus() == null) {
				ctx.log().debug("\t saving supplier: autocomplete status: {0}",RegistryStatus.ACTIVE);
				supplier.setStatus(RegistryStatus.ACTIVE);
			}
		};
		
		private static final BiConsumer<AONContext, Supplier> COMPLETE_SCOPE = (ctx, supplier) -> {
			if(supplier.getScope() == null) {
				ctx.getDefaultScope( supplier.getDomain() )
					.ifPresent(s -> supplier.setScope(s.getId()));
			}
		};

		static void autoComplete(AONContext ctx, Supplier supplier) throws AonCoreException {
			COMPLETE_TRANSACTION
			.andThen(COMPLETE_STATUS)
			.andThen(COMPLETE_SCOPE)
				.accept(ctx, supplier);
		}
	}
	
	private class SupplierValidation {
		
		private SupplierValidation() {
		}
		
		private static final BiConsumer<Supplier,AONContext> EMPTY_SCOPE = (supplier,ctx) -> {
			if (supplier.getScope() == null ) 
				throw new AonCoreException(AonError.EMPTY_SCOPE.getMessage());
		};
		
		static void validate(AONContext ctx, Supplier supplier) throws AonCoreException{
			EMPTY_SCOPE
				.accept(supplier, ctx);
		}
	}
	
	static class SupplierFiller extends Filler<Supplier> {

		@Override
		public Supplier apply(Record r) {
			return buildSupplier(r, REGISTRY);
		}
		
		static Supplier buildSupplier(Record r, com.esferalia.aon.jooq.tables.Registry registry) {
			if(registry == null) registry = REGISTRY;
			return new Supplier()
				.setId(getValue(r, registry.ID))
				.setDomain(getValue(r, SUPPLIER.DOMAIN))
				.setDocument(getValue(r, registry.DOCUMENT))
				.setDocumentType(DocumentType.value(getValue(r, registry.DOCUMENT_TYPE)).orElse(null))
				.setDocumentCountry(Country.value(getValue(r, registry.DOCUMENT_COUNTRY)).orElse(null))
				.setName(getValue(r, registry.NAME))
				.setAlias(getValue(r, registry.ALIAS))
				.setLegalPerson(AonEnumUtils.getBoolean(getValue(r, registry.TYPE)))
				.setNationality(Country.value(getValue(r, registry.NATIONALITY)).orElse(null))
				.setConfidential(getBoolean(r, registry.SECURITY_LEVEL))
				.setTariff(getValue(r, SUPPLIER.TARIFF))
				.setWithholding(getBoolean(r, SUPPLIER.WITHHOLDING))
				.setWithholdingFarmer(getBoolean(r, SUPPLIER.WITHHOLDING_FARMER))
				.setVatAccrualPayment(getBoolean(r, SUPPLIER.VAT_ACCRUAL_PAYMENT))
				.setTransaction(InvoiceTransactionType.value( getValue(r, SUPPLIER.TRANSACTION)).orElse(null))
				.setStatus(RegistryStatus.value(getValue(r, SUPPLIER.STATUS)).orElse(null))
				.setScope(getValue(r, SUPPLIER.SCOPE))
				.setPurchaseValuated(getBoolean(r, SUPPLIER.PURCHASE_VALUATED))
				.setAccount(AccountFiller.build(r) )
				.setCreationUser(getValue(r, SUPPLIER.CREATION_USER))
				.setCreationDate(getValue(r, SUPPLIER.CREATION_DATE))
				.setModificationDate(getValue(r, SUPPLIER.MODIFICATION_DATE))
				.setModificationUser(getValue(r, SUPPLIER.MODIFICATION_USER))
			;
		}
	}
	
	// -------------------------------------------------------- [PROTECTED]
	static Stream<Supplier> stream(AONContext ctx, int domain, SupplierFilter filter){
		return select(ctx, domain)
			.and(SUPPLIER.DOMAIN.eq(domain))
			.and(SUPPLIER_PROPERTIES.getCondition(filter))
			.fetch()
			.stream()
			.map(new SupplierFiller());
	}
	
	static Optional<Supplier> get(AONContext ctx, int domain, Integer supplierId){
		return select(ctx, domain)
			.and(SUPPLIER.REGISTRY.eq(supplierId))
			.fetch()
			.stream()
			.map(new SupplierFiller())
			.findFirst()
		;
	}

	static Supplier validate(AONContext ctx, Supplier supplier) {
		ctx.checkWrite();
		SupplierAutoComplete.autoComplete(ctx, supplier);
		SupplierValidation.validate(ctx, supplier);
		return supplier;
	}
	
	static Supplier save(AONContext ctx, Supplier supplier) {
		validate(ctx, supplier);
		boolean nullId = (supplier.getId() == null); 
		supplier = RegistryHandler.save(ctx, supplier);
		return nullId 
			? insert(ctx, supplier)
			: update(ctx, supplier);
	}
	
	static void delete(AONContext ctx, Integer id) {
		ctx.checkWrite();
		int count = ctx.getDslContext().delete(SUPPLIER)
			.where(SUPPLIER.REGISTRY.eq(id))
			.execute();
		ctx.log().debug("DELETE SUPPLIER id: {0} ({1} rows)",id,count);
	}
	
	
	// **************************************************************
	// **************************************************************
	// **************************************************************
	// **************************************************************
	private static SelectConditionStep<Record> select(AONContext ctx, int domain) {
		return ctx.getDslContext().select()
			.from(SUPPLIER)
			.join(REGISTRY).on(REGISTRY.ID.eq(SUPPLIER.REGISTRY))
			.leftOuterJoin(ACCOUNT).on(ACCOUNT.ID.eq(SUPPLIER.ACCOUNT))
			.where(SUPPLIER.DOMAIN.eq(domain))
			.and(SUPPLIER.SCOPE.in(ctx.getUserScopes(domain)))
		;
	}

	private static Supplier insert(AONContext ctx, Supplier supplier){
		ctx.getDslContext().insertInto(SUPPLIER)
			.set(SUPPLIER.REGISTRY,supplier.getId())
			.set(SUPPLIER.DOMAIN,supplier.getDomain())
			.set(SUPPLIER.TARIFF,supplier.getTariff())
			.set(SUPPLIER.WITHHOLDING,AonEnumUtils.getByte(supplier.isWithholding()))
			.set(SUPPLIER.WITHHOLDING_FARMER,AonEnumUtils.getByte(supplier.isWithholdingFarmer()))
			.set(SUPPLIER.VAT_ACCRUAL_PAYMENT,AonEnumUtils.getByte(supplier.isVatAccrualPayment()))
			.set(SUPPLIER.TRANSACTION, InvoiceTransactionType.value(supplier.getTransaction()))
			.set(SUPPLIER.STATUS, supplier.getStatus().value())
			.set(SUPPLIER.SCOPE,supplier.getScope())
			.set(SUPPLIER.PURCHASE_VALUATED,AonEnumUtils.getByte(supplier.isPurchaseValuated()))
			.set(SUPPLIER.ACCOUNT,supplier.getAccount().map( Account::getId ).orElse(null))
			.set(SUPPLIER.CREATION_USER,ctx.getUser())
			.set(SUPPLIER.CREATION_DATE,new Timestamp(new Date().getTime()))
			.execute();
		ctx.log().debug("INSERT SUPPLIER id: {0}",supplier.getId());		
		return supplier;
	}
	
	private static Supplier update(AONContext ctx, Supplier supplier){
		int count = ctx.getDslContext().update(SUPPLIER)
			.set(SUPPLIER.DOMAIN,supplier.getDomain())
			.set(SUPPLIER.TARIFF,supplier.getTariff())
			.set(SUPPLIER.WITHHOLDING,AonEnumUtils.getByte(supplier.isWithholding()))
			.set(SUPPLIER.WITHHOLDING_FARMER,AonEnumUtils.getByte(supplier.isWithholdingFarmer()))
			.set(SUPPLIER.VAT_ACCRUAL_PAYMENT,AonEnumUtils.getByte(supplier.isVatAccrualPayment()))
			.set(SUPPLIER.TRANSACTION, InvoiceTransactionType.value(supplier.getTransaction()))
			.set(SUPPLIER.STATUS, supplier.getStatus().value())
			.set(SUPPLIER.SCOPE,supplier.getScope())
			.set(SUPPLIER.PURCHASE_VALUATED,AonEnumUtils.getByte(supplier.isPurchaseValuated()))
			.set(SUPPLIER.ACCOUNT,supplier.getAccount().map( Account::getId ).orElse(null))
			.set(SUPPLIER.MODIFICATION_USER,ctx.getUser())
			.set(SUPPLIER.MODIFICATION_DATE,new Timestamp(new Date().getTime()))
			.where(SUPPLIER.REGISTRY.eq(supplier.getId()))
			.execute();
		ctx.log().debug("UPDATE SUPPLIER id: {0}. ({1} rows)",supplier.getId(),count);		
		return supplier;
	}
	
	// ******************************************
	// ********** FULL SUPPLIER *****************
	// ******************************************
	static Optional<SupplierFull> getFull(AONContext ctx, Integer domain, Integer id){
		return SupplierHandler.get(ctx, domain, id)
			.map( c -> new SupplierFull().setRegistry(c) )
			.map( cf -> {
				RegistryHandler.fillChilds(ctx, cf);
				return cf;
			})
		;  
	}

	static SupplierFull save(AONContext ctx, SupplierFull supplierFull) {
		ctx.checkWrite();
		SupplierAutoComplete.autoComplete(ctx, supplierFull.getRegistry());
		SupplierValidation.validate(ctx, supplierFull.getRegistry());
		supplierFull.setRegistry(SupplierHandler.save(ctx, supplierFull.getRegistry()));
		RegistryHandler.saveChilds(ctx, supplierFull);
		return getFull(ctx, supplierFull.getDomain(), supplierFull.getId())
			.orElseThrow(() -> new AonCoreException("No se pudo recuperar el dato grabado."));
	}

	// *************************************************
	// ********** TEST PURPOSE METHODS *****************
	// *************************************************
	static Supplier getRandom(AONContext ctx, int domain, SupplierFilter filter) {
		return select(ctx, domain)
			.and(SUPPLIER.DOMAIN.eq(domain))
			.and(SUPPLIER_PROPERTIES.getCondition(filter))
			.orderBy( DSL.rand() )
			.fetch()
			.stream()
			.map(new SupplierFiller())
			.findFirst()
			.orElse(null)
		;
	}
}

