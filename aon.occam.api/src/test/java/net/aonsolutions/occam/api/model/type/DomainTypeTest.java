package net.aonsolutions.occam.api.model.type;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.RepeatedTest;

import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.occam.api.AbstractOccamApiTest;
import net.aonsolutions.occam.api.model.AonRandom;

class DomainTypeTest extends AbstractOccamApiTest {
	
	@RepeatedTest(3)
	void valueTest() {
		DomainType m = AonRandom.getEnum(DomainType.class);
		assertSame(m , DomainType.values()[m.value()]);
	}
	
	@RepeatedTest(3)
	void valueByte() {
		DomainType m = AonRandom.getEnum(DomainType.class);
		assertTrue(DomainType.value( (Byte) null).isEmpty());
		assertTrue(DomainType.value( Byte.MIN_VALUE ).isEmpty());
		assertTrue(DomainType.value( Byte.MAX_VALUE).isEmpty());
		
		Optional<DomainType> om = DomainType.value( m.value() );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
	}

	@RepeatedTest(3)
	void valueInteger() {
		DomainType m = AonRandom.getEnum(DomainType.class);
		assertTrue(DomainType.value( (Integer) null).isEmpty());
		assertTrue(DomainType.value( Integer.MIN_VALUE ).isEmpty());
		assertTrue(DomainType.value( Integer.MAX_VALUE).isEmpty());
		Optional<DomainType> om = DomainType.value( (int) m.value() );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
	}
	
	@RepeatedTest(3)
	void valueString() {
		DomainType m = AonRandom.getEnum(DomainType.class);
		assertTrue(DomainType.value( (String) null).isEmpty());
		assertTrue(DomainType.value( "" ).isEmpty());
		assertTrue(DomainType.value( "12345|@#" ).isEmpty());
		String n1 = m.name();
		Optional<DomainType> om = DomainType.value( n1 );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
		
		String n2 = AonStringUtils.lowerCase(n1);
		om = DomainType.value( n2 );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
		
		String n3 = AonStringUtils.upperCase(n1);
		om = DomainType.value( n3 );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
		
	}
	
}