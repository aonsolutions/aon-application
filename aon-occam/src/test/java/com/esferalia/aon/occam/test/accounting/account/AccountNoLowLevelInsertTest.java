package com.esferalia.aon.occam.test.accounting.account;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.watson.error.AonCoreException;

public class AccountNoLowLevelInsertTest extends AbstractOccamTest {
	
	@Test 
	public void testLowLevel() throws IOException {
		Account lowerLevel = ACCOUNTING.getAccount(ctx, "8880");
		if (lowerLevel != null) {
			ACCOUNTING.delete(DOMAIN_NAME, DOMAIN_ID, USER, lowerLevel);
		}
		
		Account account = new Account()
				.setDomain(DOMAIN_ID)
				.setCode("888000000")
				.setDescription("ERROR");
		assertThrows(AonCoreException.class, () ->	
			ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, account));
	}	
}
