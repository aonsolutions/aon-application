package net.aonsolutions.occam.api.model.type;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.RepeatedTest;

import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.occam.api.AbstractOccamApiTest;
import net.aonsolutions.occam.api.model.AonRandom;

class SecurityLevelTest extends AbstractOccamApiTest {
	
	@RepeatedTest(3)
	void valueTest() {
		SecurityLevel m = AonRandom.getEnum(SecurityLevel.class);
		assertSame(m , SecurityLevel.values()[m.value()]);
	}
	
	@RepeatedTest(3)
	void valueByte() {
		SecurityLevel m = AonRandom.getEnum(SecurityLevel.class);
		assertTrue(SecurityLevel.value( (Byte) null).isEmpty());
		assertTrue(SecurityLevel.value( Byte.MIN_VALUE ).isEmpty());
		assertTrue(SecurityLevel.value( Byte.MAX_VALUE).isEmpty());
		
		Optional<SecurityLevel> om = SecurityLevel.value( m.value() );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
	}

	@RepeatedTest(3)
	void valueInteger() {
		SecurityLevel m = AonRandom.getEnum(SecurityLevel.class);
		assertTrue(SecurityLevel.value( (Integer) null).isEmpty());
		assertTrue(SecurityLevel.value( Integer.MIN_VALUE ).isEmpty());
		assertTrue(SecurityLevel.value( Integer.MAX_VALUE).isEmpty());
		Optional<SecurityLevel> om = SecurityLevel.value( (int) m.value() );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
	}
	
	@RepeatedTest(3)
	void valueString() {
		SecurityLevel m = AonRandom.getEnum(SecurityLevel.class);
		assertTrue(SecurityLevel.value( (String) null).isEmpty());
		assertTrue(SecurityLevel.value( "" ).isEmpty());
		assertTrue(SecurityLevel.value( "12345|@#" ).isEmpty());
		String n1 = m.name();
		Optional<SecurityLevel> om = SecurityLevel.value( n1 );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
		
		String n2 = AonStringUtils.lowerCase(n1);
		om = SecurityLevel.value( n2 );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
		
		String n3 = AonStringUtils.upperCase(n1);
		om = SecurityLevel.value( n3 );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
		
	}
	
}