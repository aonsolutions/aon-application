package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Account.ACCOUNT;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;

import java.util.function.Function;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;

import com.esferalia.aon.jooq.tables.records.AccountRecord;
import com.esferalia.aon.jooq.tables.records.DomainRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountFilter;
import com.esferalia.aon.occam.api.model.AccountProperties;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.watson.server.AonEnumUtils;

public class AccountDAO {
	private static final AccountPropertiesDAO ACCOUNT_PROPERTIES = new AccountPropertiesDAO();
	private static class AccountPropertiesDAO implements AccountProperties {

		private Condition[] getConditions(AccountFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null)
				return new Condition[0];

			return new Condition[] { filterDAO.getCondition() };
		}

		@Override
		public Property<Integer> getIdProperty() {
			return new FilterDAO.PropertyDAO<Integer>(ACCOUNT.ID);
		}

		@Override
		public Property<Integer> getDomainProperty() {
			return new FilterDAO.PropertyDAO<Integer>(ACCOUNT.DOMAIN);
		}

		@Override
		public Property<String> getCodeProperty() {
			return new FilterDAO.PropertyDAO<String>(ACCOUNT.CODE);
		}

		@Override
		public Property<String> getDescriptionProperty() {
			return new FilterDAO.PropertyDAO<String>(ACCOUNT.DESCRIPTION);
		}
		
		@Override
		public Property<String> getAliasProperty() {
			return new FilterDAO.PropertyDAO<String>(ACCOUNT.ALIAS);
		}

		@Override
		public Property<Byte> getEntryEnabledProperty() {
			return new FilterDAO.PropertyDAO<Byte>(ACCOUNT.ENTRYENABLED);
		}

		@Override
		public Property<Byte> getActiveProperty() {
			return new FilterDAO.PropertyDAO<Byte>(ACCOUNT.ACTIVE);
		}
		
		@Override
		public Property<Byte> getLevelProperty() {
			return new FilterDAO.PropertyDAO<Byte>(ACCOUNT.LEVEL);
		}
		
		@Override
		public Property<String> getCostCenterProperty() {
			return new FilterDAO.PropertyDAO<String>(ACCOUNT.COST_CENTER);
		}
	}
	
	
	public static Stream<Account> getAccounts(AONContext ctx, AccountFilter filter) {
		DomainRecord record = ctx.getDslContext().fetchOne(DOMAIN,DOMAIN.ID.equal(ctx.getDomainId()));
		Condition domainCondition;
		if (AonEnumUtils.getBoolean(record.getValue(DOMAIN.ENABLEHEREDITY))) {
			Integer parentDomain = record.getValue(DOMAIN.PARENT);
			domainCondition = ACCOUNT.DOMAIN.in(ctx.getDomainId(),parentDomain);
		} else {
			domainCondition = ACCOUNT.DOMAIN.eq(ctx.getDomainId());
		}
		return ctx.getDslContext()
			.select(ACCOUNT.ID,ACCOUNT.DOMAIN,ACCOUNT.CODE,ACCOUNT.DESCRIPTION
				,ACCOUNT.ALIAS,ACCOUNT.ENTRYENABLED,ACCOUNT.ACTIVE
				,ACCOUNT.LEVEL,ACCOUNT.COST_CENTER)
			.from(ACCOUNT)
			.where(ACCOUNT_PROPERTIES.getConditions(filter))
			.and(ACCOUNT.ENTRYENABLED.eq((byte) 1))
			.and(domainCondition)
			.orderBy(ACCOUNT.CODE)
			.fetch()
			.stream()
			.map(new FullAccountFiller());			
	}
	
	public static Account get(AONContext ctx, Integer accountId) {
		Condition condition = ACCOUNT.ID.equal(accountId).and(
				ctx.getDomainInheritanceCondition(
						ctx.getDomainId(),ACCOUNT.DOMAIN));
		ctx.checkRead();
		return populateRecord(ctx.getDslContext().fetchOne(ACCOUNT,condition));
	}

	public static Account get(AONContext ctx, String code) {
		Condition condition = ACCOUNT.CODE.equal(code)
			.and(ctx.getDomainInheritanceCondition(ctx.getDomainId(),ACCOUNT.DOMAIN));
		ctx.checkRead();
		return populateRecord(ctx.getDslContext().fetchOne(ACCOUNT,condition));
	}

	private static Account populateRecord(AccountRecord record) {
		if (record == null) return null;
		Account account = new Account();
		return populateRecord(record, account);
	}

	private static Account populateRecord(AccountRecord record, Account account) {
		account.setId(record.getId());
		account.setDomain(record.getDomain());
		account.setCode(record.getCode());
		account.setDescription(record.getDescription());
		account.setAlias(record.getAlias());
		account.setEntryEnabled( AonEnumUtils.getBoolean(record.getEntryenabled()));
		account.setLevel(record.getLevel());
		account.setActive(AonEnumUtils.getBoolean(record.getActive()));
		account.setCostCenter(record.getCostCenter());
		return account;
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

}




