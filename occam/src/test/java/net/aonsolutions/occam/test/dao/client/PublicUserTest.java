package net.aonsolutions.occam.test.dao.client;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.aonsolutions.occam.api.AON;
import net.aonsolutions.occam.api.config.User;
import net.aonsolutions.occam.test.AbstractOccamTest;
import net.aonsolutions.occam.test.TimingExtension;


@ExtendWith(TimingExtension.class)	
class PublicUserTest extends AbstractOccamTest {

	@Test()
	void selectOneTest() {
		Optional<User> user = AON.getUser(getOccam(), p -> p.withLogin().eq( USER ));
		assertTrue(user.isPresent());
	}

	@Test()
	void selectFullTest() {
		Optional<User> user = AON.getUser(getOccam()
			, p -> p.withLogin().eq( USER )
			, b -> b.full()
			);
		assertTrue(user.isPresent());
	}

	@Test()
	void selectStreamTest() {
		Stream<User> user = AON.getUsers(getOccam()
			,p -> p.withLogin().eq( USER )
		);
		assertTrue(user.findAny().isPresent());
	}
	
	@Test()
	void selectFullStreamTest() {
		Stream<User> user = AON.getUsers(getOccam()
			,p -> p.withLogin().eq( USER )
			,b -> b.full()
		);
		assertTrue(user.findAny().isPresent());
	}
	
}
