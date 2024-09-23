package com.esferalia.aon.occam.test.accounting.entry;


import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.watson.error.AonCoreException;


public class ValidationSaveEmptyDomainTest extends AbstractOccamTest {

	@Test
	public void testEmptyDomain() {
		AccountEntry accountEntry = new AccountEntry();
		assertThrows(AonCoreException.class, () ->
			ACCOUNTING.save(getOccam(), accountEntry));
	}
	
}
