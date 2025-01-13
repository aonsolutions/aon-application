package net.aonsolutions.occam.api.model.type;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.RepeatedTest;

import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.occam.api.AbstractOccamApiTest;
import net.aonsolutions.occam.api.model.AonRandom;

class ProductKindTest extends AbstractOccamApiTest {
	
	@RepeatedTest(3)
	void valueTest() {
		ProductKind m = AonRandom.getEnum(ProductKind.class);
		assertSame(m , ProductKind.values()[m.value()]);
	}
	
	@RepeatedTest(3)
	void valueByte() {
		ProductKind m = AonRandom.getEnum(ProductKind.class);
		assertTrue(ProductKind.value( (Byte) null).isEmpty());
		assertTrue(ProductKind.value( Byte.MIN_VALUE ).isEmpty());
		assertTrue(ProductKind.value( Byte.MAX_VALUE).isEmpty());
		
		Optional<ProductKind> om = ProductKind.value( m.value() );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
	}

	@RepeatedTest(3)
	void valueInteger() {
		ProductKind m = AonRandom.getEnum(ProductKind.class);
		assertTrue(ProductKind.value( (Integer) null).isEmpty());
		assertTrue(ProductKind.value( Integer.MIN_VALUE ).isEmpty());
		assertTrue(ProductKind.value( Integer.MAX_VALUE).isEmpty());
		Optional<ProductKind> om = ProductKind.value( (int) m.value() );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
	}
	
	@RepeatedTest(3)
	void valueString() {
		ProductKind m = AonRandom.getEnum(ProductKind.class);
		assertTrue(ProductKind.value( (String) null).isEmpty());
		assertTrue(ProductKind.value( "" ).isEmpty());
		assertTrue(ProductKind.value( "12345|@#" ).isEmpty());
		String n1 = m.name();
		Optional<ProductKind> om = ProductKind.value( n1 );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
		
		String n2 = AonStringUtils.lowerCase(n1);
		om = ProductKind.value( n2 );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
		
		String n3 = AonStringUtils.upperCase(n1);
		om = ProductKind.value( n3 );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
		
	}
	
}