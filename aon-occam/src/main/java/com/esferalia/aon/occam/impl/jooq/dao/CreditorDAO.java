package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Account.ACCOUNT;
import static com.esferalia.aon.jooq.tables.Creditor.CREDITOR;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;

import java.sql.Timestamp;
import java.util.Date;
import java.util.function.Function;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.Select;
import org.jooq.SelectConditionStep;
import org.jooq.SelectJoinStep;
import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter.CreditorFilter;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Properties.CreditorProperties;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.api.model.registry.CreditorFull;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.RegistryStatus;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.occam.impl.jooq.dao.AccountDAO.FullAccountFiller;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryDAO.RegistryPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.validation.CreditorAutoComplete;
import com.esferalia.aon.occam.impl.jooq.validation.CreditorValidation;
import com.esferalia.aon.watson.util.AonEnumUtils;

public class CreditorDAO {
	
	private static final CreditorPropertiesDAO CREDITOR_PROPERTIES = new CreditorPropertiesDAO();
	public static class CreditorPropertiesDAO extends RegistryPropertiesDAO implements CreditorProperties {
		
		protected Select<Record> build(SelectJoinStep<Record> select, CreditorFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}

		protected Condition[] getConditions(CreditorFilter filter) {
			if (filter == null) return new Condition[0];
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getRegistryProperty() {return new FilterDAO.PropertyDAO<Integer>(CREDITOR.REGISTRY);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<Integer>(CREDITOR.DOMAIN);}
		@Override public Property<Byte> getWithholdingProperty() {return new FilterDAO.PropertyDAO<Byte>(CREDITOR.WITHHOLDING);}
		@Override public Property<Byte> getVatAccrualPaymentProperty() {return new FilterDAO.PropertyDAO<Byte>(CREDITOR.VAT_ACCRUAL_PAYMENT);}
		@Override public Property<Byte> getTransactionProperty() {return new FilterDAO.PropertyDAO<Byte>(CREDITOR.TRANSACTION);}
		@Override public Property<Byte> getStatusProperty() {return new FilterDAO.PropertyDAO<Byte>(CREDITOR.STATUS);}
		@Override public Property<Integer> getScopeProperty() {return new FilterDAO.PropertyDAO<Integer>(CREDITOR.SCOPE);}
		@Override public Property<Integer> getAccountProperty() { return new FilterDAO.PropertyDAO<Integer>(CREDITOR.ACCOUNT);}
		@Override public Property<String> getCreationUserProperty() {return new FilterDAO.PropertyDAO<String>(CREDITOR.CREATION_USER);}
		@Override public Property<Timestamp> getCreationDateProperty() {return new FilterDAO.PropertyDAO<Timestamp>(CREDITOR.CREATION_DATE);}
		@Override public Property<String> getModificationUserProperty() {return new FilterDAO.PropertyDAO<String>(CREDITOR.MODIFICATION_USER);}
		@Override public Property<Timestamp> getModificationDateProperty() {return new FilterDAO.PropertyDAO<Timestamp>(CREDITOR.MODIFICATION_DATE);}
	}

	
	protected static class CreditorFiller implements Function<Record, Creditor> {

		@Override
		public Creditor apply(Record r) {
			return buildCreditor(r, REGISTRY);
		}
		
		public static Creditor buildCreditor(Record r, com.esferalia.aon.jooq.tables.Registry registry) {
			if(registry == null) registry = REGISTRY;
			return new Creditor()
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
					.setWithholding(r.getValue(CREDITOR.WITHHOLDING)==1)
					.setVatAccrualPayment(r.getValue(CREDITOR.VAT_ACCRUAL_PAYMENT)==1)
					.setTransaction(InvoiceTransactionType.safeValueOf( r.getValue(CREDITOR.TRANSACTION)))
					.setStatus(RegistryStatus.safeValueOf(r.getValue(CREDITOR.STATUS)))
					.setScope(r.getValue(CREDITOR.SCOPE))
					.setAccount(r.getValue(CREDITOR.ACCOUNT))
					.setCreationUser(r.getValue(CREDITOR.CREATION_USER))
					.setCreationDate(r.getValue(CREDITOR.CREATION_DATE))
					.setModificationDate(r.getValue(CREDITOR.MODIFICATION_DATE))
					.setModificationUser(r.getValue(CREDITOR.MODIFICATION_USER))
					;
		}
	}
	
	private static SelectConditionStep<Record> select(AONContext ctx, CreditorFilter filter) {
		return ctx.getDslContext().select()
				.from(CREDITOR)
				.join(REGISTRY).on(REGISTRY.ID.eq(CREDITOR.REGISTRY))
				.where(CREDITOR_PROPERTIES.getConditions(filter));
		
	}

	public static Stream<Creditor> getStream(AONContext ctx, CreditorFilter filter){
		return select(ctx,filter)
			.fetch()
			.stream()
			.map(new CreditorFiller());
	}
	public static Stream<Creditor> getStream(AONContext ctx, CreditorFilter filter, int offset, int limit){
		return select(ctx,filter)
				.orderBy(REGISTRY.NAME)
				.offset(offset)
				.limit(limit)
				
				.fetch()
				.stream()
				.map(new CreditorFiller());
	}
	
	
	public static Creditor get(AONContext ctx, Integer id){
		return getStream(ctx, p -> p.getIdProperty().eq(id))
			.findFirst()
			.orElse(null);
	}
	
	public static Creditor save(AONContext ctx, Creditor creditor) {
		ctx.checkWrite();
		CreditorAutoComplete.autoComplete(ctx, creditor);
		CreditorValidation.validate(ctx, creditor);
		boolean nullId = (creditor.getId() == null); 
		creditor = RegistryDAO.save(ctx, creditor);
		if (nullId || get(ctx, creditor.getId()) == null ) {
			creditor = insert(ctx, creditor);
		} else {
			creditor = update(ctx, creditor);			
		}
		return creditor;
	}

	private static Creditor insert(AONContext ctx, Creditor creditor){
		ctx.getDslContext().insertInto(CREDITOR)
			.set(CREDITOR.REGISTRY,creditor.getId())
			.set(CREDITOR.DOMAIN,creditor.getDomain().getId())
			.set(CREDITOR.WITHHOLDING,AonEnumUtils.getByte(creditor.isWithholding()))
			.set(CREDITOR.VAT_ACCRUAL_PAYMENT,AonEnumUtils.getByte(creditor.isVatAccrualPayment()))
			.set(CREDITOR.TRANSACTION,AonEnumUtils.getByte(creditor.getTransaction()))
			.set(CREDITOR.STATUS, creditor.getStatus().value())
			.set(CREDITOR.SCOPE,creditor.getScope())
			.set(CREDITOR.ACCOUNT,creditor.getAccount())
			.set(CREDITOR.CREATION_USER,ctx.getUser())
			.set(CREDITOR.CREATION_DATE,new Timestamp(new Date().getTime()))
			.execute();
		ctx.log().info("INSERT CREDITOR id: " + creditor.getId());		
		return creditor;
	}
	
	private static Creditor update(AONContext ctx, Creditor creditor){
		ctx.checkWrite();
		CreditorAutoComplete.autoComplete(ctx, creditor);
		CreditorValidation.validate(ctx, creditor);
		creditor = RegistryDAO.save(ctx, creditor);
		int count = ctx.getDslContext().update(CREDITOR)
			.set(CREDITOR.DOMAIN,creditor.getDomain().getId())
			.set(CREDITOR.WITHHOLDING,AonEnumUtils.getByte(creditor.isWithholding()))
			.set(CREDITOR.VAT_ACCRUAL_PAYMENT,AonEnumUtils.getByte(creditor.isVatAccrualPayment()))
			.set(CREDITOR.TRANSACTION,AonEnumUtils.getByte(creditor.getTransaction()))
			.set(CREDITOR.STATUS, creditor.getStatus().value())
			.set(CREDITOR.SCOPE,creditor.getScope())
			.set(CREDITOR.ACCOUNT,creditor.getAccount())
			.set(CREDITOR.MODIFICATION_USER,ctx.getUser())
			.set(CREDITOR.MODIFICATION_DATE,new Timestamp(new Date().getTime()))
			.where(CREDITOR.REGISTRY.eq(creditor.getId()))
			.execute();
		ctx.log().info("UPDATE CREDITOR id: " + creditor.getId() + ". (" + count + " rows)");		
		return creditor;
	}

	public static void delete(AONContext ctx, Integer id) {
		ctx.checkWrite();
		CreditorValidation.validateDeletion(ctx, id);
		int count = ctx.getDslContext().delete(CREDITOR)
			.where(CREDITOR.REGISTRY.eq(id))
			.execute();
		ctx.log().info("DELETE CREDITOR id:" + id + " ("+count+" rows)");
	}

	// *************************************************
	// ************ CREDITOR ACCOUNT *******************
	// *************************************************
	public static Account getCreditorAccount(AONContext ctx, Integer registry) {
		return ctx.getDslContext().select(ACCOUNT.fields())
			.from ( CREDITOR )
			.join( ACCOUNT ).on(CREDITOR.ACCOUNT.eq(ACCOUNT.ID))
			.where(CREDITOR.REGISTRY.eq(registry))
			.fetch()
			.stream()
			.map(new FullAccountFiller() )
			.findFirst()
			.orElse(null);
	}
	public static void updateCreditorAccount(AONContext ctx, Integer registry, Integer account) {
		ctx.checkWrite();
		ctx.getDslContext().update(CREDITOR)
			.set(CREDITOR.ACCOUNT,account)
			.set(CREDITOR.MODIFICATION_USER,ctx.getUser())
			.set(CREDITOR.MODIFICATION_DATE, new Timestamp( System.currentTimeMillis()) )
			.where(CREDITOR.REGISTRY.eq(registry))
			.execute();
		ctx.log().info("ACCOUNT " + account + " LINKED TO CREDITOR " + registry);
	}

	// ******************************************
	// ********** FULL CREDITOR *****************
	// ******************************************
	public static CreditorFull getFull(AONContext ctx, Integer id){
		CreditorFull full = new CreditorFull();
		full.setRegistry(CreditorDAO.get(ctx, id));  
		RegistryDAO.fillChilds(ctx, full);
		if (full.getRegistry() != null && full.getRegistry().getAccount() != null) {
			full.setAccount(AccountDAO.get(ctx, full.getRegistry().getAccount()));	
		}
		return full;
	}

	public static CreditorFull save(AONContext ctx, CreditorFull creditorFull) {
		ctx.checkWrite();
		CreditorAutoComplete.autoComplete(ctx, creditorFull.getRegistry());
		CreditorValidation.validate(ctx, creditorFull.getRegistry());
		creditorFull.setRegistry(CreditorDAO.save(ctx, creditorFull.getRegistry()));
		RegistryDAO.saveChilds(ctx, creditorFull);
		creditorFull = getFull(ctx, creditorFull.getId());
		return creditorFull;
	}
	// *************************************************
	// ********** TEST PURPOSE METHODS *****************
	// *************************************************
	public static Creditor getRandom(AONContext ctx, CreditorFilter filter) {
		return select(ctx,filter)
			.orderBy( DSL.rand() )
			.fetch()
			.stream()
			.map(new CreditorFiller())
			.findFirst()
			.orElse(null);
	}

	// ***********************************************************
	// ***********************************************************
	// ***********************************************************
	public static Stream<Creditor> getBasicCreditors(AONContext ctx, CreditorFilter filter) {
		ctx.checkRead();
		return select(ctx, filter)
			.and( CREDITOR.DOMAIN.eq(ctx.getDomainId()) )
			.and(SecurityDAO.getSecurityLevelCondition(ctx, ctx.getUser(), REGISTRY.SECURITY_LEVEL))
			.and(SecurityDAO.getUserScopesCondition(ctx,ctx.getUser(),CREDITOR.SCOPE))
			.orderBy(REGISTRY.NAME)
			.fetch()
			.stream()
			.map(new CreditorFiller());			
	}
	// ***********************************************************
	// ***********************************************************
	// ***********************************************************
	
}

