package net.aonsolutions.occam.api.model.type;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Optional;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.occam.api.AbstractOccamApiTest;
import net.aonsolutions.occam.api.model.AonRandom;

class CountryTest extends AbstractOccamApiTest {
	
	@Test
	void testRepeated() {
		AonCollectionUtils.stream(Country.values())
			.filter( c ->  AonCollectionUtils.stream(Country.values()) 
				.filter(c1 -> AonStringUtils.equals(c.getIso2(),c1.getIso2()))
				.count()>1
		).findAny()
		.ifPresent( c -> fail( "ISO CODE \"" + c.getIso2() + "\" repeated. Fix it!"));
	}
	
	@RepeatedTest(3)
	void valueTest() {
		Country c = AonRandom.getEnum(Country.class);
		Optional<Country> om = Country.value(c.value());
		assertTrue(om.isPresent());
		assertSame(c, om.get());
	}
	
	@RepeatedTest(3)
	void valueStringTest() {
		String nullString = Country.value( (Country) null);
		assertNull(nullString);
		
		Country c = AonRandom.getEnum(Country.class);
		String cIso = Country.value(c);
		Optional<Country> om = Country.value(cIso);
		assertTrue(om.isPresent());
		assertSame(c, om.get());
	}
	
	@RepeatedTest(3)
	void valueNameString() {
		Country m = AonRandom.getEnum(Country.class);
		assertTrue(Country.value( (String) null).isEmpty());
		assertTrue(Country.value( "" ).isEmpty());
		assertTrue(Country.value( "12345|@#" ).isEmpty());
		String name = m.name();
		Optional<Country> om = Country.value( name );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
		
		String n2 = AonStringUtils.lowerCase(name);
		om = Country.value( n2 );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
		
		String n3 = AonStringUtils.upperCase(name);
		om = Country.value( n3 );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
	}
	
	@RepeatedTest(50)
	void valueGetNameString() {
		Country m = AonRandom.getEnum(Country.class);
		assertTrue(Country.value( (String) null).isEmpty());
		assertTrue(Country.value( "" ).isEmpty());
		assertTrue(Country.value( "12345|@#" ).isEmpty());
		String name = m.getName();
		Optional<Country> om = Country.value( name );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
		
		String n2 = AonStringUtils.lowerCase(name);
		om = Country.value( n2 );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
		
		String n3 = AonStringUtils.upperCase(name);
		om = Country.value( n3 );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
	}
	
	@RepeatedTest(3)
	void valueISO2String() {
		Country m = AonRandom.getEnum(Country.class);
		assertTrue(Country.value( (String) null).isEmpty());
		assertTrue(Country.value( "" ).isEmpty());
		assertTrue(Country.value( "12345|@#" ).isEmpty());
		String iso2 = m.getIso2();
		Optional<Country> om = Country.value( iso2 );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
		
		String n2 = AonStringUtils.lowerCase(iso2);
		om = Country.value( n2 );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
		
		String n3 = AonStringUtils.upperCase(iso2);
		om = Country.value( n3 );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
	}
	
	@RepeatedTest(3)
	void valueISOCodeString() {
		Country m = AonRandom.getEnum(Country.class);
		assertTrue(Country.valueOfIsoCode( null ).isEmpty());
		
		int isoCode = m.getIsoCode();
		Optional<Country> om = Country.valueOfIsoCode( isoCode );
		assertTrue(om.isPresent());
		if ( m == Country.LU || m == Country.XG) {
			assertThat(om.get()).isIn(Country.LU,Country.XG);
		} else if ( m == Country.GB || m == Country.XI) {
			assertThat(om.get()).isIn(Country.XI,Country.GB);
		} else {
			assertSame(m, om.get());
		}
		
	}
}