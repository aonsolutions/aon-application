package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Account.ACCOUNT;

import java.util.function.Function;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;

import com.esferalia.aon.jooq.tables.records.AccountRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountFilter;
import com.esferalia.aon.occam.api.model.AccountProperties;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.impl.jooq.validation.AccountAutoComplete;
import com.esferalia.aon.occam.impl.jooq.validation.AccountValidation;
import com.esferalia.aon.watson.server.AonEnumUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class AccountDAO {
	private static final AccountPropertiesDAO ACCOUNT_PROPERTIES = new AccountPropertiesDAO();
	private static class AccountPropertiesDAO implements AccountProperties {
		private Condition[] getConditions(AccountFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<Integer>(ACCOUNT.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<Integer>(ACCOUNT.DOMAIN);}
		@Override public Property<String> getCodeProperty() {return new FilterDAO.PropertyDAO<String>(ACCOUNT.CODE);}
		@Override public Property<String> getDescriptionProperty() {return new FilterDAO.PropertyDAO<String>(ACCOUNT.DESCRIPTION);}
		@Override public Property<String> getAliasProperty() {return new FilterDAO.PropertyDAO<String>(ACCOUNT.ALIAS);}
		@Override public Property<Byte> getEntryEnabledProperty() {return new FilterDAO.PropertyDAO<Byte>(ACCOUNT.ENTRYENABLED);}
		@Override public Property<Byte> getActiveProperty() {return new FilterDAO.PropertyDAO<Byte>(ACCOUNT.ACTIVE);}
		@Override public Property<Byte> getLevelProperty() {return new FilterDAO.PropertyDAO<Byte>(ACCOUNT.LEVEL);}
		@Override public Property<String> getCostCenterProperty() {return new FilterDAO.PropertyDAO<String>(ACCOUNT.COST_CENTER);}
	}
	
	private static class FullAccountFiller  implements Function<Record,Account> {
		@Override
		public Account apply(Record record) {
			return new Account()
			.setId(record.getValue(ACCOUNT.ID))
			.setDomain(record.getValue(ACCOUNT.DOMAIN))
			.setCode(record.getValue(ACCOUNT.CODE))
			.setDescription(record.getValue(ACCOUNT.DESCRIPTION))
			.setAlias(record.getValue(ACCOUNT.ALIAS))
			.setEntryEnabled( AonEnumUtils.getBoolean(record.getValue(ACCOUNT.ENTRYENABLED)))
			.setLevel(record.getValue(ACCOUNT.LEVEL))
			.setActive(AonEnumUtils.getBoolean(record.getValue(ACCOUNT.ACTIVE)))
			.setCostCenter(record.getValue(ACCOUNT.COST_CENTER));
		}
	}

	private static Stream<AccountRecord> getAccountStream(AONContext ctx, AccountFilter filter) {
		return ctx.getDslContext()
				.selectFrom(ACCOUNT)
				.where(ACCOUNT_PROPERTIES.getConditions(filter))
				.and(ACCOUNT.ENTRYENABLED.eq((byte) 1))
				.and(ACCOUNT.DOMAIN.in(SecurityDAO.getInheritanceDomainIds(ctx)))
				.orderBy(ACCOUNT.CODE)
				.fetch()
				.stream();
	}
	public static Stream<Account> getAccounts(AONContext ctx, AccountFilter filter) {
		ctx.checkRead();
		return getAccountStream(ctx, filter)
			.map(new FullAccountFiller());			
	}
	public static Account get(AONContext ctx, Integer accountId) {
		Condition condition = ACCOUNT.ID.equal(accountId);
		return get(ctx, condition);
	}
	public static Account get(AONContext ctx, String code) {
		System.out.println( "Account Search ...: " + code);
		Condition condition = ACCOUNT.CODE.equal(code);
		return get(ctx, condition);
	}
	public static Account get(AONContext ctx, Condition condition) {
		ctx.checkRead();
		return ctx.getDslContext() 
			.selectFrom(ACCOUNT)
			.where(condition)
			.and(ACCOUNT.DOMAIN.in(SecurityDAO.getInheritanceDomainIds(ctx)))
//			.and(ACCOUNT.DOMAIN.eq(ctx.getDomainId())
//				.or(ACCOUNT.DOMAIN.eq(ctx.getDslContext()
//					.select(DOMAIN.PARENT)
//					.from(DOMAIN)
//					.where(DOMAIN.ID.eq(ctx.getDomainId()))
//					.and(DOMAIN.ENABLEHEREDITY.eq((byte)1)))))
//			.orderBy(ACCOUNT.CODE)
			.fetch()
			.stream()
			.map(new FullAccountFiller())
			.findFirst()
			.orElse(null);
	}

	public static Account insert(AONContext ctx, Account account) {
		ctx.checkWrite();
		AccountValidation.validate(ctx, account);
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
		return get(ctx, id);
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
			return new Long(Long.parseLong(last.getCode()) + 1).toString();
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
}




