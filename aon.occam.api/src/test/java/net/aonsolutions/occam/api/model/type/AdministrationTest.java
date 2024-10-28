package net.aonsolutions.occam.api.model.type;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.occam.api.AbstractOccamApiTest;
import net.aonsolutions.occam.api.model.AonRandom;
import net.aonsolutions.occam.api.model.type.Administration.AdministrationVisitor;

class AdministrationTest extends AbstractOccamApiTest {
	
	@RepeatedTest(3)
	void valueTest() {
		Administration m = AonRandom.getEnum(Administration.class);
		assertSame(m , Administration.values()[m.value()]);
	}
	
	@RepeatedTest(3)
	void valueByte() {
		Administration m = AonRandom.getEnum(Administration.class);
		assertTrue(Administration.value( (Byte) null).isEmpty());
		assertTrue(Administration.value( Byte.MIN_VALUE ).isEmpty());
		assertTrue(Administration.value( Byte.MAX_VALUE).isEmpty());
		
		Optional<Administration> om = Administration.value( m.value() );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
	}

	@RepeatedTest(3)
	void valueInteger() {
		Administration m = AonRandom.getEnum(Administration.class);
		assertTrue(Administration.value( (Integer) null).isEmpty());
		assertTrue(Administration.value( Integer.MIN_VALUE ).isEmpty());
		assertTrue(Administration.value( Integer.MAX_VALUE).isEmpty());
		Optional<Administration> om = Administration.value( (int) m.value() );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
	}
	
	@RepeatedTest(3)
	void valueString() {
		Administration m = AonRandom.getEnum(Administration.class);
		assertTrue(Administration.value( (String) null).isEmpty());
		assertTrue(Administration.value( "" ).isEmpty());
		assertTrue(Administration.value( "12345|@#" ).isEmpty());
		String n1 = m.name();
		Optional<Administration> om = Administration.value( n1 );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
		
		String n2 = AonStringUtils.lowerCase(n1);
		om = Administration.value( n2 );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
		
		String n3 = AonStringUtils.upperCase(n1);
		om = Administration.value( n3 );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
		
	}
	
	@Test
	void isMethodsTest() {
		assertTrue(Administration.ALAVA.isAraba());
		assertFalse(Administration.BIZKAIA.isAraba());
		assertFalse(Administration.GIPUZKOA.isAraba()); 
		assertFalse(Administration.NAVARRA.isAraba());
		assertFalse(Administration.COMMON_TERRITORY.isAraba());
		assertFalse(Administration.UNKNOWN.isAraba());
		 
		assertFalse(Administration.ALAVA.isBizkaia());
		assertTrue(Administration.BIZKAIA.isBizkaia());
		assertFalse(Administration.GIPUZKOA.isBizkaia());
		assertFalse(Administration.NAVARRA.isBizkaia());
		assertFalse(Administration.COMMON_TERRITORY.isBizkaia());
		assertFalse(Administration.UNKNOWN.isBizkaia());
		
		assertFalse(Administration.ALAVA.isGipuzkoa());
		assertFalse(Administration.BIZKAIA.isGipuzkoa());
		assertTrue(Administration.GIPUZKOA.isGipuzkoa());
		assertFalse(Administration.NAVARRA.isGipuzkoa());
		assertFalse(Administration.COMMON_TERRITORY.isGipuzkoa());
		assertFalse(Administration.UNKNOWN.isGipuzkoa());
		
		assertFalse(Administration.ALAVA.isNavarra());
		assertFalse(Administration.BIZKAIA.isNavarra());
		assertFalse(Administration.GIPUZKOA.isNavarra());
		assertTrue(Administration.NAVARRA.isNavarra());
		assertFalse(Administration.COMMON_TERRITORY.isNavarra());
		assertFalse(Administration.UNKNOWN.isNavarra());
		
		assertFalse(Administration.ALAVA.isAEAT());
		assertFalse(Administration.BIZKAIA.isAEAT());
		assertFalse(Administration.GIPUZKOA.isAEAT());
		assertFalse(Administration.NAVARRA.isAEAT());
		assertTrue(Administration.COMMON_TERRITORY.isAEAT());
		assertFalse(Administration.UNKNOWN.isAEAT());
	}
	
	@Test
	void testVisitor() {
		AdministrationVisitor<Administration> visitor = new AdministrationVisitor<Administration>() {
			@Override public Administration visitUnknown() { return Administration.UNKNOWN;}
			@Override public Administration visitNavarra() {return Administration.NAVARRA;}
			@Override public Administration visitGipuzkoa() {return Administration.GIPUZKOA;}
			@Override public Administration visitCommonTerritory() {return Administration.COMMON_TERRITORY;}
			@Override public Administration visitBizkaia() {return Administration.BIZKAIA;}
			@Override public Administration visitAlava() {return Administration.ALAVA;}
		}; 
		AonCollectionUtils.stream(Administration.values())
			.forEach(a -> assertSame(a, a.visit(visitor)));
	}
	
}