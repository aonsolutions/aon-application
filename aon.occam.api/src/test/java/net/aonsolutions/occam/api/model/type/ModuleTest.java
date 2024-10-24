package net.aonsolutions.occam.api.model.type;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.RepeatedTest;

import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.occam.api.AbstractOccamApiTest;
import net.aonsolutions.occam.api.model.AonRandom;

class ModuleTest extends AbstractOccamApiTest {
	
	@RepeatedTest(3)
	void valueTest() {
		Module m = AonRandom.getEnum( Module.class );
		assertSame(m , Module.values()[m.value()]);
	}
	
	@RepeatedTest(3)
	void valueByte() {
		Module m = AonRandom.getEnum( Module.class );
		assertTrue(Module.value( (Byte) null).isEmpty());
		assertTrue(Module.value( Byte.MIN_VALUE ).isEmpty());
		assertTrue(Module.value( Byte.MAX_VALUE).isEmpty());
		
		Optional<Module> om = Module.value( m.value() );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
	}

	@RepeatedTest(3)
	void valueInteger() {
		Module m = AonRandom.getEnum( Module.class );
		assertTrue(Module.value( (Integer) null).isEmpty());
		assertTrue(Module.value( Integer.MIN_VALUE ).isEmpty());
		assertTrue(Module.value( Integer.MAX_VALUE).isEmpty());
		Optional<Module> om = Module.value( (int) m.value() );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
	}
	
	@RepeatedTest(3)
	void valueString() {
		Module m = AonRandom.getEnum( Module.class );
		assertTrue(Module.value( (String) null).isEmpty());
		assertTrue(Module.value( "" ).isEmpty());
		assertTrue(Module.value( "12345|@#" ).isEmpty());
		String n1 = m.name();
		Optional<Module> om = Module.value( n1 );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
		
		String n2 = AonStringUtils.lowerCase(n1);
		om = Module.value( n2 );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
		
		String n3 = AonStringUtils.upperCase(n1);
		om = Module.value( n3 );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
		
		// El nombre de Module.AON_ONE no conicide con su name()
		n1 = Module.AON_ONE.getName();
		String n4 = AonStringUtils.lowerCase(n1);
		om = Module.value( n4 );
		assertTrue(om.isPresent());
		assertSame(Module.AON_ONE, om.get());
		
		String n5 = AonStringUtils.upperCase(n1);
		om = Module.value( n5 );
		assertTrue(om.isPresent());
		assertSame(Module.AON_ONE, om.get());
	}
	
}