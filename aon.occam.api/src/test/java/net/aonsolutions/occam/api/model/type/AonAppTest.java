package net.aonsolutions.occam.api.model.type;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.RepeatedTest;

import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.occam.api.AbstractOccamApiTest;
import net.aonsolutions.occam.api.model.AonRandom;

class AonAppTest extends AbstractOccamApiTest {
	
	@RepeatedTest(3)
	void valueTest() {
		AonApp m = AonRandom.getEnum( AonApp.class);
		assertSame(m , AonApp.values()[m.value()]);
	}
	
	@RepeatedTest(3)
	void valueByte() {
		AonApp m = AonRandom.getEnum( AonApp.class);
		assertTrue(AonApp.value( (Byte) null).isEmpty());
		assertTrue(AonApp.value( Byte.MIN_VALUE ).isEmpty());
		assertTrue(AonApp.value( Byte.MAX_VALUE).isEmpty());
		
		Optional<AonApp> om = AonApp.value( m.value() );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
	}

	@RepeatedTest(3)
	void valueInteger() {
		AonApp m = AonRandom.getEnum( AonApp.class);
		assertTrue(AonApp.value( (Integer) null).isEmpty());
		assertTrue(AonApp.value( Integer.MIN_VALUE ).isEmpty());
		assertTrue(AonApp.value( Integer.MAX_VALUE).isEmpty());
		Optional<AonApp> om = AonApp.value( (int) m.value() );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
	}
	
	@RepeatedTest(3)
	void valueString() {
		AonApp m = AonRandom.getEnum( AonApp.class);
		assertTrue(AonApp.value( (String) null).isEmpty());
		assertTrue(AonApp.value( "" ).isEmpty());
		assertTrue(AonApp.value( "12345|@#" ).isEmpty());
		String n1 = m.name();
		Optional<AonApp> om = AonApp.value( n1 );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
		
		String n2 = AonStringUtils.lowerCase(n1);
		om = AonApp.value( n2 );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
		
		String n3 = AonStringUtils.upperCase(n1);
		om = AonApp.value( n3 );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
	}
	
}