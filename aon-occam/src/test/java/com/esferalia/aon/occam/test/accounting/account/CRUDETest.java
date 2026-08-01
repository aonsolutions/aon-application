package com.esferalia.aon.occam.test.accounting.account;


import static com.esferalia.aon.jooq.tables.Account.ACCOUNT;
import static com.esferalia.aon.occam.test.OccamAssertions.assertEquals;
import static com.esferalia.aon.occam.test.OccamAssertions.assertNull;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Test;

import com.esferalia.aon.jooq.tables.records.AccountRecord;
import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Repeat;
import com.esferalia.aon.occam.test.faker.AccountingFaker;
import com.github.javafaker.Faker;

public class CRUDETest extends AbstractOccamTest {

	@Repeat( 20 )
	@Test
	public void test() {
		String code = getRandomUnusedCode();
		Account randAccount = AccountingFaker.getAccount(ctx, code);
		List<Account> insertedLowerLevels = ACCOUNTING.generateLowerLevels(getOccam(), randAccount, 1);
		Account inserted = ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, randAccount);
		Account obtained = ACCOUNTING.getAccount(ctx, code);
		assertAccount(obtained, inserted);
		
		obtained.setDescription("descripcion");
		inserted = ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, obtained);
		Account updated = ACCOUNTING.getAccount(ctx, code);
		assertAccount(updated, inserted);
		
		ACCOUNTING.delete(DOMAIN_NAME, DOMAIN_ID, USER, updated);
		Account deleted = ACCOUNTING.getAccount(ctx, code);
		assertNull(deleted);
		
		insertedLowerLevels.stream().sorted((ac1, ac2) -> {
			if (ac1 != null && ac2 != null) {
				return Byte.compare(ac2.getLevel(), ac1.getLevel());
			}
			return -1;
		}).forEach(acc -> {
			String lowCode = acc.getCode();
			ACCOUNTING.delete(DOMAIN_NAME, DOMAIN_ID, USER, acc);
			Account removed = ACCOUNTING.getAccount(ctx, lowCode);
			assertNull(removed);
		});
	}
	
	private static void assertAccount(Account original, Account obtained) {
		if (original == null || obtained == null) {
			assertEquals(original, obtained);
		}
		assertEquals(original.getId(), obtained.getId());
		assertEquals(original.getDomain(), obtained.getDomain());
		assertEquals(original.getCode(), obtained.getCode());
		assertEquals(original.getDescription(), obtained.getDescription());
		assertEquals(original.getAlias(), obtained.getAlias());
		assertEquals(original.isEntryEnabled(), obtained.isEntryEnabled());
		assertEquals(original.getLevel(), obtained.getLevel());
		assertEquals(original.isActive(), obtained.isActive());
		assertEquals(original.getCostCenter(), obtained.getCostCenter());
	}
	
	private static Set<String> getAllCodes() {
		try (CloseableAONContext ctx = AONContext.getAONContext(getOccam())) {
			return ctx.getDslContext()
			.select(ACCOUNT.CODE)
			.from(ACCOUNT)
			.fetchStreamInto(ACCOUNT)
			.map(AccountRecord::getCode)
			.collect(Collectors.toSet());
		}
	}
	
	private static String getRandomUnusedCode() {
		Set<String> allCodes = getAllCodes();
		String oneDigitCode = allCodes.stream().filter(code -> code != null && code.length() == 1).findAny().get();
		String randomCode = String.valueOf(Faker.instance().number().randomNumber(8, true));
		while (allCodes.contains(randomCode)) {
			randomCode = String.valueOf(Faker.instance().number().randomNumber(8, true));
		}
		return oneDigitCode + randomCode;
	}
}
