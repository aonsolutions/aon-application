package net.aonsolutions.occam.api.model.type;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.RepeatedTest;

import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.occam.api.AbstractOccamApiTest;
import net.aonsolutions.occam.api.model.AonRandom;

class RectificationTypeTest extends AbstractOccamApiTest {
	
	@RepeatedTest(3)
	void valueTest() {
		RectificationType m = AonRandom.getEnum(RectificationType.class);
		assertSame(m , RectificationType.values()[m.value()]);
	}
	
	@RepeatedTest(3)
	void valueByte() {
		RectificationType m = AonRandom.getEnum(RectificationType.class);
		assertTrue(RectificationType.value( (Byte) null).isEmpty());
		assertTrue(RectificationType.value( Byte.MIN_VALUE ).isEmpty());
		assertTrue(RectificationType.value( Byte.MAX_VALUE).isEmpty());
		
		Optional<RectificationType> om = RectificationType.value( m.value() );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
	}

	@RepeatedTest(3)
	void valueInteger() {
		RectificationType m = AonRandom.getEnum(RectificationType.class);
		assertTrue(RectificationType.value( (Integer) null).isEmpty());
		assertTrue(RectificationType.value( Integer.MIN_VALUE ).isEmpty());
		assertTrue(RectificationType.value( Integer.MAX_VALUE).isEmpty());
		Optional<RectificationType> om = RectificationType.value( (int) m.value() );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
	}
	
	@RepeatedTest(3)
	void valueString() {
		RectificationType m = AonRandom.getEnum(RectificationType.class);
		assertTrue(RectificationType.value( (String) null).isEmpty());
		assertTrue(RectificationType.value( "" ).isEmpty());
		assertTrue(RectificationType.value( "12345|@#" ).isEmpty());
		String n1 = m.name();
		Optional<RectificationType> om = RectificationType.value( n1 );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
		
		String n2 = AonStringUtils.lowerCase(n1);
		om = RectificationType.value( n2 );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
		
		String n3 = AonStringUtils.upperCase(n1);
		om = RectificationType.value( n3 );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
		
	}
	
}