package net.aonsolutions.occam.test.dao;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.aonsolutions.occam.api.AonError;
import net.aonsolutions.occam.api.config.Scope;
import net.aonsolutions.occam.api.config.User;
import net.aonsolutions.occam.dao.DAOUtils;
import net.aonsolutions.occam.dao.SecurityDAO;
import net.aonsolutions.occam.test.AbstractOccamTest;
import net.aonsolutions.occam.test.Asserts;
import net.aonsolutions.occam.test.TimingExtension;
import net.aonsolutions.occam.test.faker.AonRandom;
import net.aonsolutions.watson.server.AonEnumUtils;

@ExtendWith(TimingExtension.class)	
class SecurityDAOTest extends AbstractOccamTest {

	@Test()
	void selectOneTest() {
		Optional<User> user = SecurityDAO.getUser(ctx,p -> p.withLogin().eq( USER ));
		assertTrue(user.isPresent());
	}

	@Test()
	void selectNoneTest() {
		Optional<User> user = SecurityDAO.getUser(ctx,p -> p.withId().eq( Integer.MIN_VALUE ));
		assertFalse(user.isPresent());
	}

	@Test()
	void selectDirtyTest() {
		Optional<User> user = SecurityDAO.getUser(ctx,p -> p.withLogin().eq( USER ));
		assertTrue(user.isPresent());
		assertFalse(user.get().isDirty(), "Dirty flag not set" );
	}

	@Test()
	void emptyFilterTest() {
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> SecurityDAO.getUser(ctx, null));
		assertEquals(DAOUtils.NULL_FILTER_MSG, e.getMessage());
	}

	@Test()
	void selectNoBuilderNoFacturyStreamTest() {
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> SecurityDAO.getUserStream(ctx,p -> p.withLogin().eq( USER ), null));
		assertEquals(DAOUtils.NULL_FACTORY_MSG, e.getMessage());
	}

	@Test()
	void selectNoBuilderStreamTest() {
		Stream<User> user = SecurityDAO.getUserStream(ctx,p -> p.withLogin().eq( USER ));
		assertTrue(user.findAny().isPresent());
	}

	@Test()
	void selectStreamTest() {
		Stream<User> user = SecurityDAO.getUserStream(ctx
			,p -> p.withLogin().eq( USER )
		);
		assertTrue(user.findAny().isPresent());
	}
	
	@Test
	void streamLimitTest() {
		long max = SecurityDAO.getUserStream(ctx, p -> p.withLogin().like("%"))
			.limit(10)
			.count();
		int rows = AonRandom.number(0, (int) max);
		long count = SecurityDAO.getUserStream(ctx, p -> p.withLogin().like("%")
			,b -> b.limit(0, rows))
		.count();
		assertEquals(count, rows, "Limit not working" );
	}
	
	@Test
	void filterTest() {
		Optional<User> optUser = SecurityDAO.getUser(ctx,p -> p.withLogin().eq( USER ));
		assertTrue(optUser.isPresent());
		User expected = optUser.get();
		
		Optional<User> optActual = SecurityDAO.getUser(ctx
			,p -> p.withId().eq( expected.getId() )
				.and(p.withDomain().eq( expected.getDomain() ))
				.and(p.withName().eq( expected.getName() )) 
				.and(p.withLogin().eq( expected.getLogin() ))
				.and(p.withActive().eq( AonEnumUtils.getByte(expected.isActive())))
		);
		assertTrue(optActual.isPresent(),"User not found!");
		Asserts.assertEqualsUser(expected, optActual.get());
	}
	
	@Test()
	void denyReadOneTest() {
		try {
			ctx.denyRead();
			SecurityException e = assertThrows(SecurityException.class, () -> SecurityDAO.getUser(ctx,p -> p.withLogin().eq( USER )));
			assertEquals(AonError.READ_FORBIDDEN.getMessage(), e.getMessage());
		} finally {
			ctx.allowRead();
		}
	}
	
	@Test()
	void userScopesConditionTest() {
		
		assertNotNull(SecurityDAO.getUserScopesCondition(ctx, DOMAIN.SCOPE));
		
		Condition conditionByName = SecurityDAO.getUserScopesCondition(ctx, DOMAIN.SCOPE, USER);
		assertNotNull(conditionByName);
		
		Condition falseCondition = SecurityDAO.getUserScopesCondition(ctx, DOMAIN.SCOPE, "INVALID USER");
		assertEquals(falseCondition, DSL.falseCondition());
		
		Optional<User> userOpt = SecurityDAO.getUser(ctx,p -> p.withLogin().eq( USER ));
		assertTrue(userOpt.isPresent());
		User user = userOpt.get();
		
		Condition conditionById = SecurityDAO.getUserScopesCondition(ctx, DOMAIN.SCOPE, user.getId());
		assertNotNull(conditionById);
		
		assertEquals(conditionById.toString(),conditionByName.toString());
		
		falseCondition = SecurityDAO.getUserScopesCondition(ctx, DOMAIN.SCOPE, Integer.MIN_VALUE);
		assertEquals(falseCondition, DSL.falseCondition());
		
		Collection<Scope> scopes = SecurityDAO.getUserScopes (ctx, user)
				.collect(Collectors.toCollection(LinkedList::new));
		Asserts.assertNotEmpty(scopes ,"Empty collection");
		
		IllegalAccessError e = assertThrows( IllegalAccessError.class, () -> SecurityDAO.getUserScopes (ctx, null));
		assertEquals(AonError.USER_INVALID.getMessage(), e.getMessage());
		
	}
	
}
