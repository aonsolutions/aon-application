package net.aonsolutions.occam.api.model.type;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.RepeatedTest;

import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.occam.api.AbstractOccamApiTest;
import net.aonsolutions.occam.api.model.AonRandom;

class IRPFRegimeTest extends AbstractOccamApiTest {
	
	@RepeatedTest(3)
	void valueTest() {
		IRPFRegime m = AonRandom.getEnum(IRPFRegime.class);
		assertSame(m , IRPFRegime.values()[m.value()]);
	}
	
	@RepeatedTest(3)
	void valueByte() {
		IRPFRegime m = AonRandom.getEnum(IRPFRegime.class);
		assertTrue(IRPFRegime.value( (Byte) null).isEmpty());
		assertTrue(IRPFRegime.value( Byte.MIN_VALUE ).isEmpty());
		assertTrue(IRPFRegime.value( Byte.MAX_VALUE).isEmpty());
		
		Optional<IRPFRegime> om = IRPFRegime.value( m.value() );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
	}

	@RepeatedTest(3)
	void valueInteger() {
		IRPFRegime m = AonRandom.getEnum(IRPFRegime.class);
		assertTrue(IRPFRegime.value( (Integer) null).isEmpty());
		assertTrue(IRPFRegime.value( Integer.MIN_VALUE ).isEmpty());
		assertTrue(IRPFRegime.value( Integer.MAX_VALUE).isEmpty());
		Optional<IRPFRegime> om = IRPFRegime.value( (int) m.value() );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
	}
	
	@RepeatedTest(3)
	void valueString() {
		IRPFRegime m = AonRandom.getEnum(IRPFRegime.class);
		assertTrue(IRPFRegime.value( (String) null).isEmpty());
		assertTrue(IRPFRegime.value( "" ).isEmpty());
		assertTrue(IRPFRegime.value( "12345|@#" ).isEmpty());
		String n1 = m.name();
		Optional<IRPFRegime> om = IRPFRegime.value( n1 );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
		
		String n2 = AonStringUtils.lowerCase(n1);
		om = IRPFRegime.value( n2 );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
		
		String n3 = AonStringUtils.upperCase(n1);
		om = IRPFRegime.value( n3 );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
		
	}
	
}