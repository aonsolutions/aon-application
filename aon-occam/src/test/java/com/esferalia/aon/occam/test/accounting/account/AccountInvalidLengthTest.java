package com.esferalia.aon.occam.test.accounting.account;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.watson.error.AonCoreException;

public class AccountInvalidLengthTest extends AbstractOccamTest {
	
	@Test 
	public void test() throws IOException {
		Account account = new Account()
				.setDomain(DOMAIN_ID)
				.setDescription("ERROR")
				.setCode("n123")
				;
		assertThrows(AonCoreException.class, () ->
			ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, account));
	}	
}
