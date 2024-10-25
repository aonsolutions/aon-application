package net.aonsolutions.occam.api.model.type;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.occam.api.AbstractOccamApiTest;
import net.aonsolutions.occam.api.model.AonRandom;
import net.aonsolutions.occam.api.model.type.RegistryStatus.RegistryStatusVisitor;

class RegistryStatusTest extends AbstractOccamApiTest {
	
	@RepeatedTest(3)
	void valueTest() {
		RegistryStatus m = AonRandom.getEnum(RegistryStatus.class);
		assertSame(m , RegistryStatus.values()[m.value()]);
	}
	
	@RepeatedTest(3)
	void valueByte() {
		RegistryStatus m = AonRandom.getEnum(RegistryStatus.class);
		assertTrue(RegistryStatus.value( (Byte) null).isEmpty());
		assertTrue(RegistryStatus.value( Byte.MIN_VALUE ).isEmpty());
		assertTrue(RegistryStatus.value( Byte.MAX_VALUE).isEmpty());
		
		Optional<RegistryStatus> om = RegistryStatus.value( m.value() );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
	}

	@RepeatedTest(3)
	void valueInteger() {
		RegistryStatus m = AonRandom.getEnum(RegistryStatus.class);
		assertTrue(RegistryStatus.value( (Integer) null).isEmpty());
		assertTrue(RegistryStatus.value( Integer.MIN_VALUE ).isEmpty());
		assertTrue(RegistryStatus.value( Integer.MAX_VALUE).isEmpty());
		Optional<RegistryStatus> om = RegistryStatus.value( (int) m.value() );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
	}
	
	@RepeatedTest(3)
	void valueString() {
		RegistryStatus m = AonRandom.getEnum(RegistryStatus.class);
		assertTrue(RegistryStatus.value( (String) null).isEmpty());
		assertTrue(RegistryStatus.value( "" ).isEmpty());
		assertTrue(RegistryStatus.value( "12345|@#" ).isEmpty());
		String n1 = m.name();
		Optional<RegistryStatus> om = RegistryStatus.value( n1 );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
		
		String n2 = AonStringUtils.lowerCase(n1);
		om = RegistryStatus.value( n2 );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
		
		String n3 = AonStringUtils.upperCase(n1);
		om = RegistryStatus.value( n3 );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
		
	}
	
	@Test
	void testVisitor() {
		RegistryStatusVisitor<RegistryStatus> visitor = new RegistryStatusVisitor<RegistryStatus>() {
			@Override public RegistryStatus visitActive() { return RegistryStatus.ACTIVE;}
			@Override public RegistryStatus visitInactive() {return RegistryStatus.INACTIVE;}
			@Override public RegistryStatus visitBlocked() {return RegistryStatus.BLOCKED;}
		}; 
		AonCollectionUtils.stream(RegistryStatus.values())
			.forEach(a -> assertSame(a, a.visit(visitor)));
	}
	
}