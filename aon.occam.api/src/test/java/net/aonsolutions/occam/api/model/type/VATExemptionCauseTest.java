package net.aonsolutions.occam.api.model.type;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.RepeatedTest;

import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.occam.api.AbstractOccamApiTest;
import net.aonsolutions.occam.api.model.AonRandom;

class VATExemptionCauseTest extends AbstractOccamApiTest {
	
	@RepeatedTest(3)
	void valueTest() {
		VATExemptionCause m = AonRandom.getEnum(VATExemptionCause.class);
		assertSame(m , VATExemptionCause.values()[m.value()]);
	}
	
	@RepeatedTest(3)
	void valueByte() {
		VATExemptionCause m = AonRandom.getEnum(VATExemptionCause.class);
		assertTrue(VATExemptionCause.value( (Byte) null).isEmpty());
		assertTrue(VATExemptionCause.value( Byte.MIN_VALUE ).isEmpty());
		assertTrue(VATExemptionCause.value( Byte.MAX_VALUE).isEmpty());
		
		Optional<VATExemptionCause> om = VATExemptionCause.value( m.value() );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
	}

	@RepeatedTest(3)
	void valueInteger() {
		VATExemptionCause m = AonRandom.getEnum(VATExemptionCause.class);
		assertTrue(VATExemptionCause.value( (Integer) null).isEmpty());
		assertTrue(VATExemptionCause.value( Integer.MIN_VALUE ).isEmpty());
		assertTrue(VATExemptionCause.value( Integer.MAX_VALUE).isEmpty());
		Optional<VATExemptionCause> om = VATExemptionCause.value( (int) m.value() );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
	}
	
	@RepeatedTest(3)
	void valueString() {
		VATExemptionCause m = AonRandom.getEnum(VATExemptionCause.class);
		assertTrue(VATExemptionCause.value( (String) null).isEmpty());
		assertTrue(VATExemptionCause.value( "" ).isEmpty());
		assertTrue(VATExemptionCause.value( "12345|@#" ).isEmpty());
		String n1 = m.name();
		Optional<VATExemptionCause> om = VATExemptionCause.value( n1 );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
		
		String n2 = AonStringUtils.lowerCase(n1);
		om = VATExemptionCause.value( n2 );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
		
		String n3 = AonStringUtils.upperCase(n1);
		om = VATExemptionCause.value( n3 );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
		
	}
	
}