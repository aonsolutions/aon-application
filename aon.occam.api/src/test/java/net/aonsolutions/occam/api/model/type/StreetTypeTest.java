package net.aonsolutions.occam.api.model.type;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.occam.api.AbstractOccamApiTest;
import net.aonsolutions.occam.api.model.AonRandom;

class StreetTypeTest extends AbstractOccamApiTest {
	
	@Test
	void enumValueTest() {
		Optional<String> error = AonCollectionUtils.stream( StreetType.values() )
			.filter( s ->  AonCollectionUtils.stream( StreetType.values() )
				.filter( s1 -> s != s1 && AonStringUtils.equals(s1.getIneCode(), s.getIneCode()) )
				.findAny()
				.isPresent())
			.map( s -> "Valores repetidos en el Enums " + s)
			.findFirst();
		assertTrue( error.isEmpty() , error.orElse("") );
	}

	@RepeatedTest(3)
	void valueTest() {
		StreetType m = AonRandom.getEnum(StreetType.class);
		assertSame(m , StreetType.values()[m.ordinal()]);
	}
	
	@RepeatedTest(3)
	void valueString() {
		StreetType m = AonRandom.getEnum(StreetType.class);
		assertTrue(StreetType.value( (String) null).isEmpty());
		assertTrue(StreetType.value( "" ).isEmpty());
		assertTrue(StreetType.value( "12345|@#" ).isEmpty());
		String n1 = m.name();
		Optional<StreetType> om = StreetType.value( n1 );
		assertTrue(om.isPresent());
		assertEquals(m.value(), om.get().value());
		
		String n2 = AonStringUtils.lowerCase(n1);
		om = StreetType.value( n2 );
		assertTrue(om.isPresent());
		assertEquals(m.value(), om.get().value());
		
		String n3 = AonStringUtils.upperCase(n1);
		om = StreetType.value( n3 );
		assertTrue(om.isPresent());
		assertEquals(m.value(), om.get().value());
		

		String INE1 = m.getIneCode();
		Optional<StreetType> INEom = StreetType.valueOfIneCode( INE1 );
		assertTrue(INEom.isPresent());
		assertSame(m, INEom.get());
		
		String INE2 = AonStringUtils.lowerCase(INE1);
		INEom = StreetType.valueOfIneCode( INE2  );
		assertTrue(INEom.isPresent());
		assertSame(m, INEom.get());
		
		String INE3 = AonStringUtils.upperCase(INE1);
		INEom = StreetType.valueOfIneCode( INE3 );
		assertTrue(INEom.isPresent());
		assertSame(m, INEom.get());
	}
	
}