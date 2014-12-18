package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Account.ACCOUNT;

import org.jooq.Condition;

import com.esferalia.aon.jooq.tables.records.AccountRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.watson.server.AonEnumUtils;

public class AccountDAO {
	
	public static Account fetchOne(AONContext ctx, Integer accountId) {
		Condition condition = ACCOUNT.ID.equal(accountId).and(
				ctx.getDomainInheritanceCondition(
						ctx.getDomainId(),ACCOUNT.DOMAIN));
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


}
