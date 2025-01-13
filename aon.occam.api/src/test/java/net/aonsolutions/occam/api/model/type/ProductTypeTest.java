package net.aonsolutions.occam.api.model.type;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.RepeatedTest;

import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.occam.api.AbstractOccamApiTest;
import net.aonsolutions.occam.api.model.AonRandom;

class ProductTypeTest extends AbstractOccamApiTest {
	
	@RepeatedTest(3)
	void valueTest() {
		ProductType m = AonRandom.getEnum(ProductType.class);
		assertSame(m , ProductType.values()[m.value()]);
	}
	
	@RepeatedTest(3)
	void valueByte() {
		ProductType m = AonRandom.getEnum(ProductType.class);
		assertTrue(ProductType.value( (Byte) null).isEmpty());
		assertTrue(ProductType.value( Byte.MIN_VALUE ).isEmpty());
		assertTrue(ProductType.value( Byte.MAX_VALUE).isEmpty());
		
		Optional<ProductType> om = ProductType.value( m.value() );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
	}

	@RepeatedTest(3)
	void valueInteger() {
		ProductType m = AonRandom.getEnum(ProductType.class);
		assertTrue(ProductType.value( (Integer) null).isEmpty());
		assertTrue(ProductType.value( Integer.MIN_VALUE ).isEmpty());
		assertTrue(ProductType.value( Integer.MAX_VALUE).isEmpty());
		Optional<ProductType> om = ProductType.value( (int) m.value() );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
	}
	
	@RepeatedTest(3)
	void valueString() {
		ProductType m = AonRandom.getEnum(ProductType.class);
		assertTrue(ProductType.value( (String) null).isEmpty());
		assertTrue(ProductType.value( "" ).isEmpty());
		assertTrue(ProductType.value( "12345|@#" ).isEmpty());
		String n1 = m.name();
		Optional<ProductType> om = ProductType.value( n1 );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
		
		String n2 = AonStringUtils.lowerCase(n1);
		om = ProductType.value( n2 );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
		
		String n3 = AonStringUtils.upperCase(n1);
		om = ProductType.value( n3 );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
		
	}
	
}