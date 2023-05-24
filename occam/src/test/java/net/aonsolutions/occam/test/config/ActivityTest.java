package net.aonsolutions.occam.test.config;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.aonsolutions.occam.api.config.Activity;
import net.aonsolutions.occam.test.AbstractOccamTest;
import net.aonsolutions.occam.test.TimingExtension;
import net.aonsolutions.occam.test.faker.AonRandom;


@ExtendWith(TimingExtension.class)	
class ActivityTest extends AbstractOccamTest {

	@Test()
	void dirtyIdTest() {
		Activity a = new Activity();
		a.setId(1);
		assertTrue(a.isDirty());
	}
	
	@Test()
	void dirtyDomainTest() {
		Activity a = new Activity();
		a.setDomain( Integer.MAX_VALUE);
		assertTrue(a.isDirty());
	}

	@Test()
	void dirtyDescriptionTest() {
		Activity a = new Activity();
		a.setDescription(AonRandom.string(4));
		assertTrue(a.isDirty());
	}

	@Test()
	void dirtyEpigraphTest() {
		Activity a = new Activity();
		a.setEpigraph(AonRandom.string(4));
		assertTrue(a.isDirty());
	}
	
	@Test()
	void dirtyMarkTrueTest() {
		Activity a = new Activity();
		a.setDomain( Integer.MAX_VALUE);
		a.setId( null );
		assertTrue(a.isDirty());
	}
	
	@Test()
	void dirtyMarkFalseTest() {
		Activity a = new Activity();
		a.setId( null );
		a.setDomain( null );
		assertFalse(a.isDirty());
	}
	
	@Test()
	void selectedMarkTest() {
		Activity a = new Activity();
		a.setSelected( true );
		assertTrue(a.isSelected());
	}
}
