package net.aonsolutions.occam.test.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.aonsolutions.occam.api.config.Scope;
import net.aonsolutions.occam.api.metadata.ScopeMetadata;
import net.aonsolutions.occam.api.metadata.ScopeMetadata.ScopeMetadataVisitor;
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
	
	@Test()
	void selectedMarkTest() {
		Scope d = new Scope();
		d.setSelected( true );
		assertTrue(d.isSelected());
	}
	
	@Test()
	void equalsTest() {
		Scope d1 = new Scope();
		Scope d2 = null;
		assertNotEquals(d1,d2);
		assertEquals(d1,d1);
		d2 = new Scope();
		assertEquals(d1,d2);
		d1.setId(1);
		assertNotEquals(d1,d2);
		d2.setId(1);
		assertEquals(d1,d2);
	}
	
	@Test()
	void metadataVisitorTest() {
		ScopeMetadataVisitor<ScopeMetadata> visitor = new ScopeMetadataVisitor<>() {
			@Override public ScopeMetadata visitId() { return ScopeMetadata.ID; }
			@Override public ScopeMetadata visitDomain() { return  ScopeMetadata.DOMAIN; }
			@Override public ScopeMetadata visitDescription() {return  ScopeMetadata.DESCRIPTION; }
		};
		
		Arrays.stream(ScopeMetadata.values()).forEach( dm -> {
			assertEquals( dm, dm.visit(visitor));
		});		
	}
}
