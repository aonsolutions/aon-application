package net.aonsolutions.occam.test.config;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.aonsolutions.occam.api.config.Scope;
import net.aonsolutions.occam.test.AbstractOccamTest;
import net.aonsolutions.occam.test.TimingExtension;
import net.aonsolutions.occam.test.faker.AonRandom;


@ExtendWith(TimingExtension.class)	
class ScopeTest extends AbstractOccamTest {

	@Test()
	void dirtyIdTest() {
		Scope u = new Scope();
		u.setId(1);
		assertTrue(u.isDirty());
	}
	
	@Test()
	void dirtyDomainTest() {
		Scope u = new Scope();
		u.setDomain(1);
		assertTrue(u.isDirty());
	}

	@Test()
	void dirtyDescriptionTest() {
		Scope u = new Scope();
		u.setDescription(AonRandom.string(10));
		assertTrue(u.isDirty());
	}
	
	@Test()
	void dirtyMarkTrueTest() {
		Scope d = new Scope();
		d.setDescription( "dddddd" );
		d.setId( null );
		assertTrue(d.isDirty());
	}
	
	@Test()
	void dirtyMarkFalseTest() {
		Scope d = new Scope();
		d.setId( null );
		d.setDescription( null );
		assertFalse(d.isDirty());
	}
	
}
