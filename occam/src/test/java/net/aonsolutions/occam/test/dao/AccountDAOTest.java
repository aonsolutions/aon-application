package net.aonsolutions.occam.test.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.aonsolutions.occam.api.AonError;
import net.aonsolutions.occam.api.accounting.Account;
import net.aonsolutions.occam.dao.AccountDAO;
import net.aonsolutions.occam.dao.DAOUtils;
import net.aonsolutions.occam.test.AbstractOccamTest;
import net.aonsolutions.occam.test.Asserts;
import net.aonsolutions.occam.test.TimingExtension;
import net.aonsolutions.occam.test.faker.AonRandom;
import net.aonsolutions.watson.server.AonEnumUtils;


@ExtendWith(TimingExtension.class)	
class AccountDAOTest extends AbstractOccamTest {
	
	private static final String ACCOUNT_CODE = "477000000";

	@Test()
	void selectOneTest() {
		Optional<Account> account = AccountDAO.get(ctx,p -> p.withCode().eq( ACCOUNT_CODE ), b -> b);
		assertTrue(account.isPresent());
	}

	@Test()
	void selectNoneTest() {
		Optional<Account> account = AccountDAO.get(ctx,p -> p.withId().eq( Integer.MIN_VALUE ), b -> b);
		assertFalse(account.isPresent());
	}

	@Test()
	void selectDirtyTest() {
		Optional<Account> account = AccountDAO.get(ctx,p -> p.withCode().eq( ACCOUNT_CODE ), b -> b);
		assertTrue(account.isPresent());
		assertFalse(account.get().isDirty(), "Dirty flag not set" );
	}

	@Test()
	void emptyFilterTest() {
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> AccountDAO.get(ctx, null, b -> b));
		assertEquals(DAOUtils.NULL_FILTER_MSG, e.getMessage());
	}

	@Test()
	void selectNoBuilderNoFacturyStreamTest() {
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> AccountDAO.getStream(ctx,p -> p.withCode().eq( ACCOUNT_CODE ), null));
		assertEquals(DAOUtils.NULL_FACTORY_MSG, e.getMessage());
	}

	@Test()
	void selectStreamTest() {
		Stream<Account> domain = AccountDAO.getStream(ctx
			,p -> p.withCode().eq( ACCOUNT_CODE )
			,b -> b
		);
		assertTrue(domain.findAny().isPresent());
	}
	
	@Test
	void streamLimitTest() {
		long max = AccountDAO.getStream(ctx, p -> p.withDescription().like("%a%"), b -> b)
			.limit(10)
			.count();
		int rows = AonRandom.getInt(0, (int) max);
		long count = AccountDAO.getStream(ctx, p -> p.withDescription().like("%a%")
			,b -> b.limit(0, rows))
		.count();
		assertEquals(count, rows, "Limit not working" );
	}
	
	@Test
	void filterTest() {
		Optional<Account> optAccount = AccountDAO.get(ctx
			,p -> p.withCode().eq( ACCOUNT_CODE )
			,b -> b);
		assertTrue(optAccount.isPresent());
		Account expected = optAccount.get();
		Optional<Account> optActual = AccountDAO.get(ctx
			,p -> p.withId().eq( expected.getId() )
				.and(p.withDomain().eq( expected.getDomain() ))	
				.and(p.withCode().eq( expected.getCode() ))
				.and(p.withDescription().eq( expected.getDescription() ))
				.and(p.withAlias().eq( expected.getAlias() ))
				.and(p.withActive().eq( AonEnumUtils.getByte(expected.isActive())))
			,b -> b
		);
		assertTrue(optActual.isPresent(),"Account not found!");
		Asserts.assertEqualsAccount(expected, optActual.get());
	}
	
	@Test()
	void denyReadOneTest() {
		try {
			ctx.denyRead();
			SecurityException e = assertThrows(SecurityException.class, () -> AccountDAO.get(ctx,p -> p.withCode().eq( ACCOUNT_CODE ), b -> b));
			assertEquals(AonError.READ_FORBIDDEN.getMessage(), e.getMessage());
		} finally {
			ctx.allowRead();
		}
	}
	
//	@Test
//	void saveDirtyFlagTes() {
//		AonLogger originalLogger = ctx.getLoggerForTests();
//		Level originalLevel = ctx.getLoggerLevelForTests();
//		try {
//			StringBuilder debugMessage = new StringBuilder(); 
//			Logger thisLogger = Logger.getLogger(this.getClass().getName());
//			setLoggerLevel(Level.FINEST);
//			ctx.setLoggerForTests( new AonLogger(thisLogger, DOMAIN_NAME) {
//				@Override
//				public void debug(String msg) {
//					super.debug(msg);
//					debugMessage.append(msg);
//				}
//			});
//			Account account = new Account();
//			AccountDAO.save(ctx, account);
//			assertEquals(AonError.NOT_DIRTY.format("Account",account.getId()), debugMessage.toString());
//		} finally {
//			ctx.setLoggerForTests( originalLogger );
//			setLoggerLevel(originalLevel);
//		}
//	}
//	@Test
//	void saveTest() {
//		Domain insertDomain = AonFaker.getDomain(true);
//		assertDoesNotThrow(() -> AccountDAO.save(ctx, insertDomain));
//
//		Domain updateDomain = AonFaker.getDomain(true);
//		updateDomain.setId(1);
//		assertDoesNotThrow(() -> AccountDAO.save(ctx, updateDomain));
//	}
	
}
