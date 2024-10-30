package net.aonsolutions.occam.api.model.type;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.occam.api.AbstractOccamApiTest;
import net.aonsolutions.occam.api.model.AonRandom;

class AppParamTest extends AbstractOccamApiTest {
	
	@RepeatedTest(3)
	void valueTest() {
		AppParam m = AonRandom.getEnum( AppParam.class );
		assertSame(m , AppParam.values()[m.ordinal()]);
	}
	
	@RepeatedTest(3)
	void valueNameTest() {
		AppParam m = AonRandom.getEnum(AppParam.class);
		assertEquals( m.name() , m.value());
		String value = AppParam.value( m );
		assertEquals( m.name() , value );
	}

	@Test
	void valueNullTest() {
		assertNull( AppParam.value( (AppParam) null) );
	}

	@RepeatedTest(3)
	void valueString() {
		AppParam m = AonRandom.getEnum( AppParam.class );
		assertTrue(AppParam.value( (String) null).isEmpty());
		assertTrue(AppParam.value( "" ).isEmpty());
		assertTrue(AppParam.value( "12345|@#" ).isEmpty());
		String n1 = m.name();
		Optional<AppParam> om = AppParam.value( n1 );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
		
		String n2 = AonStringUtils.lowerCase(n1);
		om = AppParam.value( n2 );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
		
		String n3 = AonStringUtils.upperCase(n1);
		om = AppParam.value( n3 );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
	}
	
}
