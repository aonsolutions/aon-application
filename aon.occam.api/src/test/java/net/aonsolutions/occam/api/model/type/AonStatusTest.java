package net.aonsolutions.occam.api.model.type;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.RepeatedTest;

import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.occam.api.AbstractOccamApiTest;
import net.aonsolutions.occam.api.model.AonRandom;

class AonStatusTest extends AbstractOccamApiTest {
	
	@RepeatedTest(3)
	void valueTest() {
		AonStatus m = AonRandom.getEnum(AonStatus.class);
		assertSame(m , AonStatus.values()[m.value()]);
	}
	
	@RepeatedTest(3)
	void valueByte() {
		AonStatus m = AonRandom.getEnum(AonStatus.class);
		assertTrue(AonStatus.value( (Byte) null).isEmpty());
		assertTrue(AonStatus.value( Byte.MIN_VALUE ).isEmpty());
		assertTrue(AonStatus.value( Byte.MAX_VALUE).isEmpty());
		
		Optional<AonStatus> om = AonStatus.value( m.value() );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
	}

	@RepeatedTest(3)
	void valueInteger() {
		AonStatus m = AonRandom.getEnum(AonStatus.class);
		assertTrue(AonStatus.value( (Integer) null).isEmpty());
		assertTrue(AonStatus.value( Integer.MIN_VALUE ).isEmpty());
		assertTrue(AonStatus.value( Integer.MAX_VALUE).isEmpty());
		Optional<AonStatus> om = AonStatus.value( (int) m.value() );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
	}
	
	@RepeatedTest(3)
	void valueString() {
		AonStatus m = AonRandom.getEnum(AonStatus.class);
		assertTrue(AonStatus.value( (String) null).isEmpty());
		assertTrue(AonStatus.value( "" ).isEmpty());
		assertTrue(AonStatus.value( "12345|@#" ).isEmpty());
		String n1 = m.name();
		Optional<AonStatus> om = AonStatus.value( n1 );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
		
		String n2 = AonStringUtils.lowerCase(n1);
		om = AonStatus.value( n2 );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
		
		String n3 = AonStringUtils.upperCase(n1);
		om = AonStatus.value( n3 );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
		
	}
	
}