package net.aonsolutions.occam.test.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.server.AonEnumUtils;

import net.aonsolutions.occam.api.config.User;
import net.aonsolutions.occam.dao.DAOUtils;
import net.aonsolutions.occam.dao.SecurityDAO;
import net.aonsolutions.occam.test.AbstractOccamTest;
import net.aonsolutions.occam.test.Asserts;
import net.aonsolutions.occam.test.TimingExtension;
import net.aonsolutions.occam.test.faker.AonRandom;

@ExtendWith(TimingExtension.class)	
class UserDAOTest extends AbstractOccamTest {

	@Test()
	void selectOneTest() {
		Optional<User> user = SecurityDAO.get(ctx,p -> p.withLogin().eq( USER ));
		assertTrue(user.isPresent());
	}

	@Test()
	void selectNoneTest() {
		Optional<User> user = SecurityDAO.get(ctx,p -> p.withId().eq( Integer.MIN_VALUE ));
		assertFalse(user.isPresent());
	}

	@Test()
	void selectDirtyTest() {
		Optional<User> user = SecurityDAO.get(ctx,p -> p.withLogin().eq( USER ));
		assertTrue(user.isPresent());
		assertFalse(user.get().isDirty(), "Dirty flag not set" );
	}

	@Test()
	void emptyFilterTest() {
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> SecurityDAO.get(ctx, null));
		assertEquals(DAOUtils.NULL_FILTER_MSG, e.getMessage());
	}

	@Test()
	void selectNoBuilderNoFacturyStreamTest() {
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> SecurityDAO.getStream(ctx,p -> p.withLogin().eq( USER ), null));
		assertEquals(DAOUtils.NULL_FACTORY_MSG, e.getMessage());
	}

	@Test()
	void selectNoBuilderStreamTest() {
		Stream<User> user = SecurityDAO.getStream(ctx,p -> p.withLogin().eq( USER ));
		assertTrue(user.findAny().isPresent());
	}

	@Test()
	void selectStreamTest() {
		Stream<User> user = SecurityDAO.getStream(ctx
			,p -> p.withLogin().eq( USER )
		);
		assertTrue(user.findAny().isPresent());
	}
	
	@Test
	void streamLimitTest() {
		long max = SecurityDAO.getStream(ctx, p -> p.withLogin().like("%"))
			.limit(10)
			.count();
		int rows = AonRandom.getInt(0, (int) max);
		long count = SecurityDAO.getStream(ctx, p -> p.withLogin().like("%")
			,b -> b.limit(0, rows))
		.count();
		assertEquals(count, rows, "Limit not working" );
	}
	
	@Test
	void filterTest() {
		Optional<User> optUser = SecurityDAO.get(ctx
			,p -> p.withLogin().eq( USER )
			,b -> b.full());
		assertTrue(optUser.isPresent());
		User expected = optUser.get();
		
		Optional<User> optActual = SecurityDAO.get(ctx
			,p -> p.withId().eq( expected.getId() )
				.and(p.withDomain().eq( expected.getDomain() ))
				.and(p.withName().eq( expected.getName() )) 
				.and(p.withLogin().eq( expected.getLogin() ))
				.and(p.withActive().eq( AonEnumUtils.getByte(expected.isActive())))
			,b -> b.full()
		);
		assertTrue(optActual.isPresent(),"User not found!");
		Asserts.assertEqualsUser(expected, optActual.get());
	}
	
	@Test()
	void denyReadOneTest() {
		try {
			ctx.denyRead();
			SecurityException e = assertThrows(SecurityException.class, () -> SecurityDAO.get(ctx,p -> p.withLogin().eq( USER )));
			assertEquals(AonError.READ_FORBIDDEN.getMessage(), e.getMessage());
		} finally {
			ctx.allowRead();
		}
	}
}
