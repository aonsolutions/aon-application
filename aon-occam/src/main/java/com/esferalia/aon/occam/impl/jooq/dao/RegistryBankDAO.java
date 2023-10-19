package com.esferalia.aon.occam.impl.jooq.dao;


import static com.esferalia.aon.jooq.tables.Account.ACCOUNT;
import static com.esferalia.aon.jooq.tables.Rbank.RBANK;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.Select;
import org.jooq.SelectConditionStep;
import org.jooq.SelectJoinStep;
import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Filter.RegistryBankFilter;
import com.esferalia.aon.occam.api.model.Properties.RegistryBankProperties;
import com.esferalia.aon.occam.api.model.finance.BankAccount;
import com.esferalia.aon.occam.api.model.registry.RegistryBank;
import com.esferalia.aon.occam.impl.jooq.dao.AccountDAO.FullAccountFiller;
import com.esferalia.aon.occam.impl.jooq.validation.RegistryBankValidation;

public class RegistryBankDAO {

	private RegistryBankDAO() {
		
	}
	
	private static final RBankPropertiesDAO RBANK_PROPERTIES = new RBankPropertiesDAO();
	
	protected static class RBankPropertiesDAO implements RegistryBankProperties {
		protected Select<Record> build(SelectJoinStep<Record> select,RegistryBankFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		protected Condition[] getConditions(RegistryBankFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null){
				return new Condition[0];
			}
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(RBANK.ID);} 
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(RBANK.DOMAIN);}
		@Override public Property<Integer> getRegistryProperty() {return new FilterDAO.PropertyDAO<>(RBANK.REGISTRY);}
		@Override public Property<String> getBankAccountProperty() {return new FilterDAO.PropertyDAO<>(RBANK.BANK_ACCOUNT);}
		@Override public Property<String> getBicProperty() {return new FilterDAO.PropertyDAO<>(RBANK.BIC);}
		@Override public Property<String> getSufixProperty() {return new FilterDAO.PropertyDAO<>(RBANK.SUFIX);}
		@Override public Property<String> getAliasProperty() {return new FilterDAO.PropertyDAO<>(RBANK.ALIAS);}
		@Override public Property<Byte> getActiveProperty() {return new FilterDAO.PropertyDAO<>(RBANK.ACTIVE);}
		@Override public Property<Integer> getAccountProperty() {return new FilterDAO.PropertyDAO<>(RBANK.REGISTRY);}
		@Override public Property<String> getRequisitionProperty() {return new FilterDAO.PropertyDAO<>(RBANK.REQUISITION);}
		@Override public Property<String> getSepaMandateRefProperty() {return new FilterDAO.PropertyDAO<>(RBANK.SEPA_MANDATE_REF);}
	}
	
	public static class RegistryBankFiller extends Filler implements Function<Record, RegistryBank> {

		@Override
		public RegistryBank apply(Record r) {
			return build(r);
		}
		
		public static RegistryBank build(Record r) {
			return new RegistryBank()
					.setId(r.getValue(RBANK.ID))
					.setRegistry(r.getValue(RBANK.REGISTRY))
					.setDomain(r.getValue(RBANK.DOMAIN))
					.setAccount(checkField(r, ACCOUNT.ID)
					        ? FullAccountFiller.build(r)
					        : new Account().setId(getValue(r, RBANK.ID)))
					.setActive(getBoolean(r, RBANK.ACTIVE))
					.setAlias(r.getValue(RBANK.ALIAS))
					.setBankAccount(new BankAccount(r.getValue(RBANK.BANK_ACCOUNT)))
					.setRequisition(r.getValue(RBANK.REQUISITION))
					.setSepaMandateRef(r.getValue(RBANK.SEPA_MANDATE_REF))
					.setBic(r.getValue(RBANK.BIC))
					.setSuffix(r.getValue(RBANK.SUFIX))
					.setDirty(false)
					.setRemoved(false);
		}
		
	}
	
	private static SelectConditionStep<Record> select(AONContext ctx, RegistryBankFilter filter) {
		return ctx.getDslContext().select()
				.from(RBANK)
				.leftOuterJoin(ACCOUNT).on(ACCOUNT.ID.eq(RBANK.ACCOUNT))
				.where(RBANK_PROPERTIES.getConditions(filter));
	}

	public static RegistryBank get(AONContext ctx, RegistryBankFilter filter){
		return select(ctx,filter).limit(1)
				.fetch().stream().map(new RegistryBankFiller())
				.findFirst().orElse(new RegistryBank());
	}
	
	/**
	 * Get the RegistryBank object with the given id
	 * @param ctx context
	 * @param id RegistryBank's id
	 * @return RegistryBank
	 */
	public static RegistryBank get(AONContext ctx, Integer id) {
		return RegistryBankDAO.get(ctx, f -> f.getIdProperty().eq(id));
	}
	
	/**
	 * Get a collection of RegistryBank with the given domain
	 * @param ctx context
	 * @param domain RegistryBank's domain
	 * @return Collection of RegistryBank
	 */
	public static Stream<RegistryBank> getByDomain(AONContext ctx, Integer domain) {
		return RegistryBankDAO.getStream(ctx, f -> f.getDomainProperty().eq(domain));
	}
	
	/**
	 * Get a collection of RegistryBank with the given registry
	 * @param ctx context
	 * @param registry RegistryBank's registry
	 * @return Collection of RegistryBank
	 */
	public static Stream<RegistryBank> getByRegistry(AONContext ctx, Integer registry) {
		return RegistryBankDAO.getStream(ctx, f -> f.getRegistryProperty().eq(registry));
	}
	
	/**
	 * Get a collection of RegistryBank with the given bic
	 * @param ctx context
	 * @param bic RegistryBank's bic
	 * @return Collection of RegistryBank
	 */
	public static Stream<RegistryBank> getByBic(AONContext ctx, String bic) {
		return RegistryBankDAO.getStream(ctx, f -> f.getBicProperty().eq(bic));
	}
	
	/**
	 * Get a collection of RegistryBank with the given suffix
	 * @param ctx context
	 * @param suffix RegistryBank's suffix
	 * @return Collection of RegistryBank
	 */
	public static Stream<RegistryBank> getBySuffix(AONContext ctx, String suffix) {
		return RegistryBankDAO.getStream(ctx, f -> f.getSufixProperty().eq(suffix));
	}
	
	/**
	 * Get a collection of RegistryBank with the given alias
	 * @param ctx context
	 * @param alias RegistryBank's alias
	 * @return Collection of RegistryBank
	 */
	public static Stream<RegistryBank> getByAlias(AONContext ctx, String alias) {
		return RegistryBankDAO.getStream(ctx, f -> f.getAliasProperty().eq(alias));
	}
	
	/**
	 * Get a collection of RegistryBank with that the value of active
	 * @param ctx context
	 * @param active RegistryBank's active
	 * @return Collection of RegistryBank
	 */
	public static Stream<RegistryBank> getByActive(AONContext ctx, Byte active) {
		return RegistryBankDAO.getStream(ctx, f -> f.getActiveProperty().eq(active));
	}
	
	/**
	 * Get a collection of RegistryBank with the given account
	 * @param ctx context
	 * @param account RegistryBank's account
	 * @return Collection of RegistryBank
	 */
	public static Stream<RegistryBank> getByAccount(AONContext ctx, Integer account) {
		return RegistryBankDAO.getStream(ctx, f -> f.getAccountProperty().eq(account));
	}
	
	/**
	 * Get a collection of RegistryBank with the given requisition
	 * @param ctx context
	 * @param requisition RegistryBank's requisition
	 * @return Collection of RegistryBank
	 */
	public static Stream<RegistryBank> getByRequisition(AONContext ctx, String requisition) {
		return RegistryBankDAO.getStream(ctx, f -> f.getRequisitionProperty().eq(requisition));
	}
	
	/**
	 * Get a collection of RegistryBank with the given sepaMandateRef
	 * @param ctx context
	 * @param sepaMandateRef RegistryBank's sepaMandateRef
	 * @return Collection of RegistryBank
	 */
	public static Stream<RegistryBank> getBySepaMandateRef(AONContext ctx, String sepaMandateRef) {
		return RegistryBankDAO.getStream(ctx, f -> f.getSepaMandateRefProperty().eq(sepaMandateRef));
	}
	
	/**
	 * Get a collection of RegistryBank with the given BankAccount
	 * @param ctx context
	 * @param bankAccount RegistryBank's bank account
	 * @return Collection of RegistryBank
	 */
	public static Stream<RegistryBank> getByBankAccount(AONContext ctx, String bankAccount) {
		return RegistryBankDAO.getStream(ctx, f -> f.getBankAccountProperty().eq(bankAccount));
	}
	
	/**
	 * Get a stream of RegistryBank with the given filter
	 * @param ctx context
	 * @param filter the filter
	 * @return Stream of RegistryBank
	 */
	public static Stream<RegistryBank> getStream(AONContext ctx, RegistryBankFilter filter) {
		return select(ctx,filter)
			.fetch()
			.stream()
			.map(new RegistryBankFiller());
	}
	
	public static RegistryBank save(AONContext ctx, RegistryBank rbank) {
		RegistryBankValidation.validate(ctx, rbank);
		ctx.checkWrite();
		if(rbank.getId() != null && rbank.isRemoved()) { 
			delete(ctx, rbank.getId());
			return rbank;
		}
		if(!rbank.isDirty()) return rbank;
		return (rbank.getId() == null)
				? insert(ctx, rbank)
				: update(ctx, rbank);
	}
	
	private static RegistryBank insert(AONContext ctx, RegistryBank rbank){
		Integer id = ctx.getDslContext().insertInto(RBANK)
			.set(RBANK.DOMAIN,rbank.getDomain())
			.set(RBANK.REGISTRY,rbank.getRegistry())
			.set(RBANK.BANK_ACCOUNT, rbank.getBankAccount().getIban())
			.set(RBANK.BIC, rbank.getBic())
			.set(RBANK.SUFIX, rbank.getSuffix())
			.set(RBANK.ALIAS, rbank.getAlias())
			.set(RBANK.ACTIVE, rbank.getActive())
			.set(RBANK.ACCOUNT, rbank.getAccount().getId())
			.set(RBANK.REQUISITION, rbank.getRequisition())
			.set(RBANK.SEPA_MANDATE_REF, rbank.getSepaMandateRef())
			.returning(RBANK.ID)
			.fetchOne()
			.getValue(RBANK.ID);
		rbank.setId(id).setDirty(false);
		ctx.log().debug("INSERT REGISTRY BANK ( registry: {0}) id: {1}",rbank.getRegistry(),rbank.getId());
		return rbank;
	}
	private static RegistryBank update(AONContext ctx, RegistryBank rbank){
		int count = ctx.getDslContext().update(RBANK)
				.set(RBANK.DOMAIN,rbank.getDomain())
				.set(RBANK.REGISTRY,rbank.getRegistry())
				.set(RBANK.BANK_ACCOUNT, rbank.getBankAccount().getIban())
				.set(RBANK.BIC, rbank.getBic())
				.set(RBANK.SUFIX, rbank.getSuffix())
				.set(RBANK.ALIAS, rbank.getAlias())
				.set(RBANK.REQUISITION, rbank.getRequisition())
				.set(RBANK.SEPA_MANDATE_REF, rbank.getSepaMandateRef())
				.set(RBANK.ACTIVE, rbank.getActive())
				.set(RBANK.ACCOUNT, rbank.getAccount().getId())
			.where(RBANK.ID.eq(rbank.getId()))
			.execute();
		ctx.log().debug("UPDATE REGISTRY BANK ( registry: {0}) id: {1}. ({2} rows)", rbank.getRegistry(), rbank.getId(),count);
		rbank.setDirty(false);
		return rbank;
	}
	
	public static void delete(AONContext ctx, Integer id){
		ctx.checkWrite();
		int count = ctx.getDslContext().delete(RBANK)
			.where(RBANK.ID.eq(id))
			.execute();
		ctx.log().debug("DELETE REGISTRY BANK id: {0} ({1} rows)", id, count);
	}
	
	public static int deleteByRegistry(AONContext ctx, Integer registry){
		ctx.checkWrite();
		int count = ctx.getDslContext().delete(RBANK)
			.where(RBANK.REGISTRY.eq(registry))
			.execute();
		ctx.log().debug("DELETE REGISTRY BANK registry: {0} ({1} rows)", registry, count);
		return count;
	}
	
	// *************************************************
	// ********** TEST PURPOSE METHODS *****************
	// *************************************************
	public static RegistryBank getRandom(AONContext ctx, RegistryBankFilter filter) {
		return select(ctx,filter)
			.orderBy( DSL.rand() )
			.fetch()
			.stream()
			.map(new RegistryBankFiller())
			.findFirst()
			.orElse(null);
	}
	

}