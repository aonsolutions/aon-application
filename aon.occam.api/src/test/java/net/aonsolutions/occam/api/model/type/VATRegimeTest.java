package net.aonsolutions.occam.api.model.type;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.RepeatedTest;

import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.occam.api.AbstractOccamApiTest;
import net.aonsolutions.occam.api.model.AonRandom;

class VATRegimeTest extends AbstractOccamApiTest {
	
	@RepeatedTest(3)
	void valueTest() {
		VATRegime m = AonRandom.getEnum(VATRegime.class);
		assertSame(m , VATRegime.values()[m.value()]);
	}
	
	@RepeatedTest(3)
	void valueByte() {
		VATRegime m = AonRandom.getEnum(VATRegime.class);
		assertTrue(VATRegime.value( (Byte) null).isEmpty());
		assertTrue(VATRegime.value( Byte.MIN_VALUE ).isEmpty());
		assertTrue(VATRegime.value( Byte.MAX_VALUE).isEmpty());
		
		Optional<VATRegime> om = VATRegime.value( m.value() );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
	}

	@RepeatedTest(3)
	void valueInteger() {
		VATRegime m = AonRandom.getEnum(VATRegime.class);
		assertTrue(VATRegime.value( (Integer) null).isEmpty());
		assertTrue(VATRegime.value( Integer.MIN_VALUE ).isEmpty());
		assertTrue(VATRegime.value( Integer.MAX_VALUE).isEmpty());
		Optional<VATRegime> om = VATRegime.value( (int) m.value() );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
	}
	
	@RepeatedTest(3)
	void valueString() {
		VATRegime m = AonRandom.getEnum(VATRegime.class);
		assertTrue(VATRegime.value( (String) null).isEmpty());
		assertTrue(VATRegime.value( "" ).isEmpty());
		assertTrue(VATRegime.value( "12345|@#" ).isEmpty());
		String n1 = m.name();
		Optional<VATRegime> om = VATRegime.value( n1 );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
		
		String n2 = AonStringUtils.lowerCase(n1);
		om = VATRegime.value( n2 );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
		
		String n3 = AonStringUtils.upperCase(n1);
		om = VATRegime.value( n3 );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
		
	}
	
}