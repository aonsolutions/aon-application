package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Account.ACCOUNT;
import static com.esferalia.aon.jooq.tables.Creditor.CREDITOR;
import static com.esferalia.aon.jooq.tables.Customer.CUSTOMER;
import static com.esferalia.aon.jooq.tables.Supplier.SUPPLIER;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.SelectSeekStep1;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.AccountRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountFilter;
import com.esferalia.aon.occam.api.model.AccountParams;
import com.esferalia.aon.occam.api.model.AccountProperties;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.impl.jooq.validation.AccountAutoComplete;
import com.esferalia.aon.occam.impl.jooq.validation.AccountValidation;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonEnumUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class AccountDAO {
	
	private AccountDAO() {
	}
	
	private static final AccountPropertiesDAO ACCOUNT_PROPERTIES = new AccountPropertiesDAO();
	private static class AccountPropertiesDAO implements AccountProperties {
		private Condition[] getConditions(AccountFilter filter) {
			if (filter==null) return new Condition[0];
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(ACCOUNT.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(ACCOUNT.DOMAIN);}
		@Override public Property<String> getCodeProperty() {return new FilterDAO.PropertyDAO<>(ACCOUNT.CODE);}
		@Override public Property<String> getDescriptionProperty() {return new FilterDAO.PropertyDAO<>(ACCOUNT.DESCRIPTION);}
		@Override public Property<String> getAliasProperty() {return new FilterDAO.PropertyDAO<>(ACCOUNT.ALIAS);}
		@Override public Property<Byte> getEntryEnabledProperty() {return new FilterDAO.PropertyDAO<>(ACCOUNT.ENTRYENABLED);}
		@Override public Property<Byte> getActiveProperty() {return new FilterDAO.PropertyDAO<>(ACCOUNT.ACTIVE);}
		@Override public Property<Byte> getLevelProperty() {return new FilterDAO.PropertyDAO<>(ACCOUNT.LEVEL);}
		@Override public Property<String> getCostCenterProperty() {return new FilterDAO.PropertyDAO<>(ACCOUNT.COST_CENTER);}
	}
	
	public static class FullAccountFiller extends Filler implements Function<Record,Account> {
		@Override
		public Account apply(Record r) {
			return build(r);
		}
		
		public static Account build(Record r) {
			return build(r, ACCOUNT);
		}

		public static Account build(Record r, com.esferalia.aon.jooq.tables.Account alias) {
			return new Account()
				.setId(r.getValue(alias.ID))
				.setDomain(r.getValue(alias.DOMAIN))
				.setCode(r.getValue(alias.CODE))
				.setDescription(r.getValue(alias.DESCRIPTION))
				.setAlias(r.getValue(alias.ALIAS))
				.setEntryEnabled( AonEnumUtils.getBoolean(r.getValue(alias.ENTRYENABLED)))
				.setLevel(getByte(r, alias.LEVEL))
				.setActive(AonEnumUtils.getBoolean(r.getValue(alias.ACTIVE)))
				.setCostCenter(r.getValue(alias.COST_CENTER))
				.setHasRegistry((checkField(r, CUSTOMER.REGISTRY) || checkField(r, SUPPLIER.REGISTRY) || checkField(r, CREDITOR.REGISTRY)) && (null != r.getValue(CUSTOMER.REGISTRY) || null != r.getValue(SUPPLIER.REGISTRY) || null != r.getValue(CREDITOR.REGISTRY)));
		}
	}
	private static SelectConditionStep<AccountRecord> select(AONContext ctx, AccountFilter filter) {
		return ctx.getDslContext()
				.selectFrom(ACCOUNT)
				.where(ACCOUNT_PROPERTIES.getConditions(filter))
				.and(ACCOUNT.DOMAIN.in(SecurityDAO.getInheritanceDomainIds(ctx)));
	}
	private static SelectSeekStep1<AccountRecord,String> getAccountSelect(AONContext ctx, AccountFilter filter) {
		return select(ctx,filter)
			.orderBy(ACCOUNT.CODE);
	}

	private static Stream<AccountRecord> getAccountStream(AONContext ctx, AccountFilter filter) {
		ctx.checkRead();
		return getAccountSelect(ctx, filter) 
			.fetch()
			.stream();
	}
	public static Stream<Account> getAccounts(AONContext ctx, AccountFilter filter) {
		ctx.checkRead();
		return getAccountStream(ctx, filter)
			.map(new FullAccountFiller());			
	}
	public static Stream<Account> getAccounts(AONContext ctx, AccountFilter filter, int offset, int limit) {
		ctx.checkRead();
		return getAccountSelect(ctx, filter)
			.limit(limit)
			.fetch()
			.stream()
			.map(new FullAccountFiller());			
	}
	public static List<Account> getAccountsList(AONContext ctx, AccountParams params) {
		ctx.checkRead();
		Condition condition = getFilter( params );
		Integer[] domains = SecurityDAO.getInheritanceDomainIds(ctx);
		List<Account> accounts = ctx.getDslContext() 
			.select().from(ACCOUNT)
			.leftJoin(CUSTOMER)
			.on(ACCOUNT.ID.eq(CUSTOMER.ACCOUNT).and(CUSTOMER.DOMAIN.in(domains)))
			.leftJoin(SUPPLIER)
			.on(ACCOUNT.ID.eq(SUPPLIER.ACCOUNT).and(SUPPLIER.DOMAIN.in(domains)))
			.leftJoin(CREDITOR)
			.on(ACCOUNT.ID.eq(CREDITOR.ACCOUNT).and(CREDITOR.DOMAIN.in(domains)))
			.where(condition)
			.and(ACCOUNT.DOMAIN.in(domains))
			.orderBy(ACCOUNT.CODE)
			.offset(params.getOffset())
			.limit(params.getLimit())
			.fetch()
			.stream()
			.map(new FullAccountFiller())
			.collect(Collectors.toList());
		
		return accounts;
	}
	
	public static Stream<Account> getAccounts(AONContext ctx, AccountParams params) {
		ctx.checkRead();
		Condition condition = getFilter( params );
		return ctx.getDslContext() 
			.select().from(ACCOUNT)
			.where(condition)
			.and(ACCOUNT.DOMAIN.in(SecurityDAO.getInheritanceDomainIds(ctx)))
			.orderBy(ACCOUNT.CODE)
			.offset(params.getOffset())
			.limit(params.getLimit())
			.fetch()
			.stream()
			.map(new FullAccountFiller());
	}

	public static Account get(AONContext ctx, Integer accountId) {
		ctx.checkRead();
		Condition condition = ACCOUNT.ID.equal(accountId);
		return get(ctx, condition);
	}
	public static Account get(AONContext ctx, String code) {
		ctx.checkRead();
		Condition condition = ACCOUNT.CODE.equal(code);
		return get(ctx, condition);
	}
	public static Account get(AONContext ctx, Condition condition) {
		ctx.checkRead();
		return ctx.getDslContext() 
			.selectFrom(ACCOUNT)
			.where(condition)
			.and(ACCOUNT.DOMAIN.in(SecurityDAO.getInheritanceDomainIds(ctx)))
			.fetch()
			.stream()
			.map(new FullAccountFiller())
			.findFirst()
			.orElse(null);
	}
	public static Stream<Account> getAccounts(AONContext ctx, Condition condition) {
		ctx.checkRead();
		return ctx.getDslContext() 
			.selectFrom(ACCOUNT)
			.where(condition)
			.and(ACCOUNT.DOMAIN.in(SecurityDAO.getInheritanceDomainIds(ctx)))
			.orderBy(ACCOUNT.CODE)
			.fetch()
			.stream()
			.map(new FullAccountFiller());
	}
	public static Account save(AONContext ctx, Account account) {
		if (account.getId() == null) {
			return insert(ctx, account);
		} 
		return update(ctx, account);
	}
	
	public static Account insert(AONContext ctx, Account account) {
		return insert(ctx, account,false);
	}
	
	protected static Account insert(AONContext ctx, Account account, boolean skipValidation) {
		ctx.checkWrite();
		if (!skipValidation) {
			AccountValidation.validate(ctx, account);
		}
		AccountAutoComplete.complete(ctx, account);
		Integer id = ctx.getDslContext()
			.insertInto(ACCOUNT)
			.set(ACCOUNT.DOMAIN,account.getDomain())
			.set(ACCOUNT.CODE,account.getCode())
			.set(ACCOUNT.DESCRIPTION,account.getDescription())
			.set(ACCOUNT.ALIAS,account.getAlias())
			.set(ACCOUNT.ENTRYENABLED, AonEnumUtils.getByte(account.isEntryEnabled()))
			.set(ACCOUNT.LEVEL,account.getLevel())
			.set(ACCOUNT.ACTIVE, AonEnumUtils.getByte(account.isActive()))
			.set(ACCOUNT.COST_CENTER,account.getCostCenter())
			.returning(ACCOUNT.ID)
			.fetchOne()
			.getValue(ACCOUNT.ID);
		ctx.log().debug("INSERT ACCOUNT id: {0} code: {1}",account.getId(),account.getCode());
		return get(ctx, id);
	}
	
	/**
	 * 
	 * @param ctx AONContext
	 * @param account Cuenta contable
	 * @param minLevel Nivel mínimo de cuenta contable que DEBE existir en base de datos
	 * @param skipValidation Saltarse las validaciones
	 * @return La lista de las cuentas insertadas en base de datos
	 */
	public static List<Account> generateLowerLevels(AONContext ctx, Account account, int minLevel) {
		int level = (byte) ((account.getCode().length() > 4) ? 5 : account.getCode().length());
		int lowestLevel = findLowestLevel(ctx, account);
		if (minLevel > lowestLevel) {
			throw new AonCoreException(AonError.ACCOUNT_LOW_LEVEL_TOO_LOW.format(minLevel));
		}
		int lowerLevel = lowestLevel + 1;
		List<Account> insertedLoweLevelAccounts = new LinkedList<>();
		while (lowerLevel < level) {
			Account lowerAccount = account.clone();
			String lowerCode = AonStringUtils.substring(account.getCode(),0, lowerLevel);
			lowerAccount.setCode(lowerCode)
				.setDescription("AUTOGENERADA: " + String.valueOf(lowerCode))
				.setAlias("autogenerada");
			insertedLoweLevelAccounts.add(insert(ctx, lowerAccount));
			lowerLevel++;
		}
		return insertedLoweLevelAccounts;
	}
	
	public static int findLowestLevel(AONContext ctx, Account account) {
		int level = (byte) ((account.getCode().length() > 4) ? 5 : account.getCode().length());
		int parentLevel = level - 1;
		Account a = null;
		while (parentLevel > 1) {
			String parentCode = AonStringUtils.substring(account.getCode(),0, parentLevel);
			a = AccountDAO.get(ctx, ACCOUNT.CODE.equal(parentCode));
			if (a != null) {
				return parentLevel;
			}
			parentLevel--;
		}
		return parentLevel;
	}
	
	
	public static Account update(AONContext ctx, Account account) {
		ctx.checkWrite();
		AccountValidation.validate(ctx, account);
		AccountAutoComplete.complete(ctx, account);
		ctx.getDslContext()
			.update(ACCOUNT)
			.set(ACCOUNT.DOMAIN,account.getDomain())
			.set(ACCOUNT.CODE,account.getCode())
			.set(ACCOUNT.DESCRIPTION,account.getDescription())
			.set(ACCOUNT.ALIAS,account.getAlias())
			.set(ACCOUNT.ENTRYENABLED, AonEnumUtils.getByte(account.isEntryEnabled()))
			.set(ACCOUNT.LEVEL,account.getLevel())
			.set(ACCOUNT.ACTIVE, AonEnumUtils.getByte(account.isActive()))
			.set(ACCOUNT.COST_CENTER,account.getCostCenter())
			.where(ACCOUNT.ID.eq(account.getId()))
			.execute();
		ctx.log().debug("UPDATE ACCOUNT id: {0} code: {1}",account.getId(),account.getCode());
		return account;
	}

	public static Account delete(AONContext ctx, Account account) {
		ctx.checkWrite();
		AccountValidation.validateDeletion(ctx, account);
		ctx.getDslContext()
			.delete(ACCOUNT)
			.where(ACCOUNT.ID.eq(account.getId()))
			.execute();
		ctx.log().debug("DELETE ACCOUNT id: {0} code: {1}",account.getId(),account.getCode());
		return account;
	}
	
	public static String getNextAccountCode(AONContext ctx, String prefix) {
		Account last = ctx.getDslContext()
			.selectFrom(ACCOUNT)
			.where(ACCOUNT.DOMAIN.in(SecurityDAO.getInheritanceDomainIds(ctx)))
			.and(ACCOUNT.ENTRYENABLED.eq((byte) 1))
			.and(ACCOUNT.CODE.like(fillprefix(prefix)))
			.orderBy(ACCOUNT.CODE.desc())
			.limit(1)
			.fetch()
			.stream()
			.map(new FullAccountFiller())
			.findFirst()
			.orElse(null);
		if (last != null){
			return Long.valueOf(Long.parseLong(last.getCode()) + 1).toString();
		}
		return zerofill(prefix); 
	}
	
	private static String fillprefix(String prefix) {
		String string = AonStringUtils.EMPTY;
		for(int i=0; i< 9 - (prefix.length());i++){
			string = string + AonStringUtils.UNDERSCORE;
		}
		return prefix + string;
	}

	private static String zerofill(String prefix) {
		String string = AonStringUtils.ONE;
		for(int i=0;i< 9 - (prefix.length() + 1); i++){
			string = AonStringUtils.ZERO + string;
		}
		return prefix + string;
	}
	private static String toSQLLike( String value) {
		value = AonStringUtils.trim(value);
		if ( AonStringUtils.isBlank(value) ) return value;
		if (AonStringUtils.startsWith(value, AonStringUtils.ASTERISK)  
		 || AonStringUtils.endsWith(value, AonStringUtils.ASTERISK)) {
			if (AonStringUtils.startsWith(value, AonStringUtils.ASTERISK) ) {
				value = AonStringUtils.replaceOnce(value, AonStringUtils.ASTERISK, AonStringUtils.PERCENT);
			}
			if (AonStringUtils.endsWith(value, AonStringUtils.ASTERISK) ) {
				value = AonStringUtils.substring(value, 0, value.length()-1) + AonStringUtils.PERCENT;
			}
		} else {
			value = AonStringUtils.SQLlike(value);
		}
		return value;
	}
	
	private static Condition getFilter(AccountParams params) {
		if ( params == null) {
			return DSL.trueCondition(); 
		}
		Condition c = null;
		if ( params.getId() != null) {
			c = ACCOUNT.ID.eq(params.getId());
		}
		if ( AonStringUtils.isNotBlank(params.getCode())) {
			Condition a = ACCOUNT.CODE.like( toSQLLike(params.getCode()));	
			c = c==null?a:c.and(a);
		}
		if ( AonStringUtils.isNotBlank(params.getDescription())) {
			Condition a = ACCOUNT.DESCRIPTION.like( toSQLLike(params.getDescription()));	
			c = c==null?a:c.and(a);
		}
		if ( AonStringUtils.isNotBlank(params.getAlias())) {
			Condition a = ACCOUNT.ALIAS.like( toSQLLike(params.getAlias()));	
			c = c==null?a:c.and(a);
		}
		if ( params.getLevel() != null ) {
			Condition a = ACCOUNT.LEVEL.eq( params.getLevel() );	
			c = c==null?a:c.and(a);
		}
		if ( params.getActive() != null ) {
			Condition a = ACCOUNT.ACTIVE.eq( (byte) (params.getActive()?1:0));	
			c = c==null?a:c.and(a);
		}
		if ( AonStringUtils.isNotBlank(params.getCostCenter())) {
			Condition a = ACCOUNT.COST_CENTER.like( toSQLLike(params.getCostCenter()));	
			c = c==null?a:c.and(a);
		}
		return c!=null?c:DSL.trueCondition();
	}

	// *************************************************
	// ********** TEST PURPOSE METHODS *****************
	// *************************************************
	public static Account getRandom(AONContext ctx, AccountFilter filter) {
		return select(ctx,filter)
			.orderBy( DSL.rand() )
			.fetch()
			.stream()
			.map(new FullAccountFiller())
			.findFirst()
			.orElse(null);
	}
}




