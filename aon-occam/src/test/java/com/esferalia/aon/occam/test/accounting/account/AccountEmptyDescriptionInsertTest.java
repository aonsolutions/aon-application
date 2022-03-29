package com.esferalia.aon.occam.test.accounting.account;

import java.io.IOException;

import org.junit.Test;

import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.AonRandom;
import com.esferalia.aon.watson.error.AonCoreException;

public class AccountEmptyDescriptionInsertTest extends AbstractOccamTest {
	
	@Test (expected = AonCoreException.class)
	public void test() throws IOException {
		
		Account existingRandomAccount = AonRandom.getAccount(ctx);
		
		Account account = new Account()
				.setDomain(existingRandomAccount.getDomain())
				.setDescription(existingRandomAccount.getDescription())
				.setCode(existingRandomAccount.getCode());
		
		ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, account);
	}	
}
