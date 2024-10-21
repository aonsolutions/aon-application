package net.aonsolutions.occam.impl.handler;

import static com.esferalia.aon.jooq.tables.Account.ACCOUNT;
import static com.esferalia.aon.jooq.tables.Creditor.CREDITOR;
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
import net.aonsolutions.occam.api.model.Creditor;
import net.aonsolutions.occam.api.model.CreditorFull;
import net.aonsolutions.occam.api.model.Filter.CreditorFilter;
import net.aonsolutions.occam.api.model.Filter.Property;
import net.aonsolutions.occam.api.model.Properties.CreditorProperties;
import net.aonsolutions.occam.api.model.type.Country;
import net.aonsolutions.occam.api.model.type.DocumentType;
import net.aonsolutions.occam.api.model.type.InvoiceTransactionType;
import net.aonsolutions.occam.api.model.type.RegistryStatus;
import net.aonsolutions.occam.impl.AONContext;
import net.aonsolutions.occam.impl.handler.AccountHandler.AccountFiller;
import net.aonsolutions.occam.impl.handler.RegistryHandler.RegistryPropertiesHandler;

class CreditorHandler {
	private CreditorHandler() {
	}
	
	private static final CreditorPropertiesHandler CREDITOR_PROPERTIES = new CreditorPropertiesHandler();
	static class CreditorPropertiesHandler extends RegistryPropertiesHandler implements CreditorProperties {
		
		protected Condition getCondition(CreditorFilter filter) {
			if (filter == null) return DSL.trueCondition();
			FilterHandler filterDAO = (FilterHandler) filter.filter(this);
			return filterDAO.getCondition();
		}
		
		@Override public Property<Byte> getWithholdingProperty() {return new FilterHandler.PropertyDAO<>(CREDITOR.WITHHOLDING);}
		@Override public Property<Byte> getVatAccrualPaymentProperty() {return new FilterHandler.PropertyDAO<>(CREDITOR.VAT_ACCRUAL_PAYMENT);}
		@Override public Property<Byte> getTransactionProperty() {return new FilterHandler.PropertyDAO<>(CREDITOR.TRANSACTION);}
		@Override public Property<Byte> getStatusProperty() {return new FilterHandler.PropertyDAO<>(CREDITOR.STATUS);}
		@Override public Property<Integer> getScopeProperty() {return new FilterHandler.PropertyDAO<>(CREDITOR.SCOPE);}
		@Override public Property<Integer> getAccountProperty() { return new FilterHandler.PropertyDAO<>(CREDITOR.ACCOUNT);}
		@Override public Property<String> getCreationUserProperty() {return new FilterHandler.PropertyDAO<>(CREDITOR.CREATION_USER);}
		@Override public Property<Timestamp> getCreationDateProperty() {return new FilterHandler.PropertyDAO<>(CREDITOR.CREATION_DATE);}
		@Override public Property<String> getModificationUserProperty() {return new FilterHandler.PropertyDAO<>(CREDITOR.MODIFICATION_USER);}
		@Override public Property<Timestamp> getModificationDateProperty() {return new FilterHandler.PropertyDAO<>(CREDITOR.MODIFICATION_DATE);}
	}

	
	protected static class CreditorFiller extends Filler<Creditor> {

		@Override
		public Creditor apply(Record r) {
			return buildCreditor(r, REGISTRY);
		}
		
		static Creditor buildCreditor(Record r, com.esferalia.aon.jooq.tables.Registry registry) {
			if (isNull(r, registry.ID)) return null;
			return new Creditor()
				.setId(getValue(r, registry.ID))
				.setDomain(getValue(r, CREDITOR.DOMAIN))
				.setDocument(getValue(r, registry.DOCUMENT))
				.setDocumentType(DocumentType.value(getValue(r, registry.DOCUMENT_TYPE)).orElse(null))
				.setDocumentCountry(Country.value(getValue(r, registry.DOCUMENT_COUNTRY)).orElse(null))
				.setName(getValue(r, registry.NAME))
				.setAlias(getValue(r, registry.ALIAS))
				.setLegalPerson(AonEnumUtils.getBoolean(getValue(r, registry.TYPE)))
				.setNationality(Country.value(getValue(r, registry.NATIONALITY)).orElse(null))
				.setConfidential(getBoolean(r, registry.SECURITY_LEVEL))
				.setWithholding(getBoolean(r, CREDITOR.WITHHOLDING))
				.setVatAccrualPayment(getBoolean(r, CREDITOR.VAT_ACCRUAL_PAYMENT))
				.setTransaction(InvoiceTransactionType.value( getValue(r, CREDITOR.TRANSACTION)).orElse(null))
				.setStatus(RegistryStatus.value(getValue(r, CREDITOR.STATUS)).orElse(null))
				.setScope(getValue(r, CREDITOR.SCOPE))
				.setAccount(AccountFiller.build(r) )
				.setCreationUser(getValue(r, CREDITOR.CREATION_USER))
				.setCreationDate(getValue(r, CREDITOR.CREATION_DATE))
				.setModificationDate(getValue(r, CREDITOR.MODIFICATION_DATE))
				.setModificationUser(getValue(r, CREDITOR.MODIFICATION_USER))
			;
		}
	}
	
	static Creditor validate(AONContext ctx, Creditor creditor) {
		ctx.checkWrite();
		CreditorAutoComplete.autoComplete(ctx, creditor);
		CreditorValidation.validate(ctx, creditor);
		return creditor;
	}
	
	static Creditor save(AONContext ctx, Creditor creditor) {
		validate(ctx, creditor);
		boolean nullId = (creditor.getId() == null); 
		creditor = RegistryHandler.save(ctx, creditor);
		return nullId 
			? insert(ctx, creditor)
			: update(ctx, creditor);
	}
	
	
	private static class CreditorAutoComplete {
		
		private CreditorAutoComplete() {
			
		}
		private static final BiConsumer<AONContext,Creditor> COMPLETE_TRANSACTION = (ctx,creditor) -> {
			if (creditor.getTransaction() == null) {
				ctx.log().debug("\t saving creditor: autocomplete transaction: {0}",InvoiceTransactionType.NATIONAL);
				creditor.setTransaction(InvoiceTransactionType.NATIONAL);
			}
		};

		private static final BiConsumer<AONContext,Creditor> COMPLETE_STATUS = (ctx,creditor) -> {
			if (creditor.getStatus() == null) {
				ctx.log().debug("\t saving creditor: autocomplete status: {0}",RegistryStatus.ACTIVE);
				creditor.setStatus(RegistryStatus.ACTIVE);
			}
		};
		
		private static final BiConsumer<AONContext, Creditor> COMPLETE_SCOPE = (ctx, creditor) -> {
			if(creditor.getScope() == null) {
				ctx.getDefaultScope( creditor.getDomain() )
					.ifPresent(s -> creditor.setScope(s.getId()));
			}
		};

		static void autoComplete(AONContext ctx, Creditor creditor) throws AonCoreException {
			COMPLETE_TRANSACTION
			.andThen(COMPLETE_STATUS)
			.andThen(COMPLETE_SCOPE)
				.accept(ctx, creditor);

		}

	}
	
	private class CreditorValidation {
		
		private CreditorValidation() {
		}
		
		private static final BiConsumer<Creditor,AONContext> EMPTY_SCOPE = (creditor,ctx) -> {
			if (creditor.getScope() == null ) 
				throw new AonCoreException(AonError.EMPTY_SCOPE.getMessage());
		};
		
		
		static void validate(AONContext ctx, Creditor creditor) throws AonCoreException{
			EMPTY_SCOPE
				.accept(creditor, ctx);
		}
	}
	
	// **************************************************************
	// **************************************************************
	// **************************************************************
	// **************************************************************
	private static SelectConditionStep<Record> select(AONContext ctx, int domain) {
		return ctx.getDslContext().select()
			.from(CREDITOR)
			.join(REGISTRY).on(REGISTRY.ID.eq(CREDITOR.REGISTRY))
			.leftOuterJoin(ACCOUNT).on(ACCOUNT.ID.eq(CREDITOR.ACCOUNT))
			.where(CREDITOR.DOMAIN.eq(domain))
			.and(CREDITOR.SCOPE.in(ctx.getUserScopes(domain)))
		;
	}

	static Stream<Creditor> stream(AONContext ctx, int domain, CreditorFilter filter){
		return select(ctx, domain)
			.and(CREDITOR.DOMAIN.eq(domain))
			.and(CREDITOR_PROPERTIES.getCondition(filter))
			.fetch()
			.stream()
			.map(new CreditorFiller());
	}
	
	static Optional<Creditor> get(AONContext ctx, int domain, Integer creditorId){
		return select(ctx, domain)
			.and(CREDITOR.REGISTRY.eq(creditorId))
			.fetch()
			.stream()
			.map(new CreditorFiller())
			.findFirst()
		;
	}
	
	private static Creditor insert(AONContext ctx, Creditor creditor){
		ctx.getDslContext().insertInto(CREDITOR)
			.set(CREDITOR.REGISTRY,creditor.getId())
			.set(CREDITOR.DOMAIN,creditor.getDomain())
			.set(CREDITOR.WITHHOLDING,AonEnumUtils.getByte(creditor.isWithholding()))
			.set(CREDITOR.VAT_ACCRUAL_PAYMENT,AonEnumUtils.getByte(creditor.isVatAccrualPayment()))
			.set(CREDITOR.TRANSACTION, InvoiceTransactionType.value(creditor.getTransaction()))
			.set(CREDITOR.STATUS, RegistryStatus.value(creditor.getStatus()))
			.set(CREDITOR.SCOPE,creditor.getScope())
			.set(CREDITOR.ACCOUNT,creditor.getAccount().map( Account::getId ).orElse(null))
			.set(CREDITOR.CREATION_USER,ctx.getUser())
			.set(CREDITOR.CREATION_DATE,new Timestamp(new Date().getTime()))
			.execute();
		ctx.log().debug("INSERT CREDITOR id: {0}",creditor.getId());		
		return creditor;
	}
	
	private static Creditor update(AONContext ctx, Creditor creditor){
		int count = ctx.getDslContext().update(CREDITOR)
			.set(CREDITOR.DOMAIN,creditor.getDomain())
			.set(CREDITOR.WITHHOLDING,AonEnumUtils.getByte(creditor.isWithholding()))
			.set(CREDITOR.VAT_ACCRUAL_PAYMENT,AonEnumUtils.getByte(creditor.isVatAccrualPayment()))
			.set(CREDITOR.TRANSACTION, InvoiceTransactionType.value(creditor.getTransaction()))
			.set(CREDITOR.STATUS, RegistryStatus.value(creditor.getStatus()))
			.set(CREDITOR.SCOPE,creditor.getScope())
			.set(CREDITOR.ACCOUNT,creditor.getAccount().map( Account::getId ).orElse(null))
			.set(CREDITOR.MODIFICATION_USER,ctx.getUser())
			.set(CREDITOR.MODIFICATION_DATE,new Timestamp(new Date().getTime()))
			.where(CREDITOR.REGISTRY.eq(creditor.getId()))
			.execute();
		ctx.log().debug("UPDATE CREDITOR id: {0}. ({1} rows)",creditor.getId(),count);		
		return creditor;
	}

	static void delete(AONContext ctx, Integer id) {
		ctx.checkWrite();
		int count = ctx.getDslContext().delete(CREDITOR)
			.where(CREDITOR.REGISTRY.eq(id))
			.execute();
		ctx.log().debug("DELETE CREDITOR id: {0} ({1} rows)",id,count);
	}
	
	// ******************************************
	// ********** FULL CREDITOR *****************
	// ******************************************
	static Optional<CreditorFull> getFull(AONContext ctx, Integer domain, Integer id){
		return CreditorHandler.get(ctx, domain, id)
			.map( c -> new CreditorFull().setRegistry(c) )
			.map( cf -> {
				RegistryHandler.fillChilds(ctx, cf);
				return cf;
			})
		;  
	}

	static CreditorFull save(AONContext ctx, CreditorFull creditorFull) {
		ctx.checkWrite();
		CreditorAutoComplete.autoComplete(ctx, creditorFull.getRegistry());
		CreditorValidation.validate(ctx, creditorFull.getRegistry());
		creditorFull.setRegistry(CreditorHandler.save(ctx, creditorFull.getRegistry()));
		RegistryHandler.saveChilds(ctx, creditorFull);
		return getFull(ctx, creditorFull.getDomain(), creditorFull.getId())
			.orElseThrow(() -> new AonCoreException("No se pudo recuperar el dato grabado."));
	}

	// *************************************************
	// ********** TEST PURPOSE METHODS *****************
	// *************************************************
	static Creditor getRandom(AONContext ctx, int domain, CreditorFilter filter) {
		return select(ctx, domain)
			.and(CREDITOR.DOMAIN.eq(domain))
			.and(CREDITOR_PROPERTIES.getCondition(filter))
			.orderBy( DSL.rand() )
			.fetch()
			.stream()
			.map(new CreditorFiller())
			.findFirst()
			.orElse(null)
		;
	}
}

 