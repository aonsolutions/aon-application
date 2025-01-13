package net.aonsolutions.occam.api.model.type;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.RepeatedTest;

import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.occam.api.AbstractOccamApiTest;
import net.aonsolutions.occam.api.model.AonRandom;

class ProductStatusTest extends AbstractOccamApiTest {
	
	@RepeatedTest(3)
	void valueTest() {
		ProductStatus m = AonRandom.getEnum(ProductStatus.class);
		assertSame(m , ProductStatus.values()[m.value()]);
	}
	
	@RepeatedTest(3)
	void valueByte() {
		ProductStatus m = AonRandom.getEnum(ProductStatus.class);
		assertTrue(ProductStatus.value( (Byte) null).isEmpty());
		assertTrue(ProductStatus.value( Byte.MIN_VALUE ).isEmpty());
		assertTrue(ProductStatus.value( Byte.MAX_VALUE).isEmpty());
		
		Optional<ProductStatus> om = ProductStatus.value( m.value() );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
	}

	@RepeatedTest(3)
	void valueInteger() {
		ProductStatus m = AonRandom.getEnum(ProductStatus.class);
		assertTrue(ProductStatus.value( (Integer) null).isEmpty());
		assertTrue(ProductStatus.value( Integer.MIN_VALUE ).isEmpty());
		assertTrue(ProductStatus.value( Integer.MAX_VALUE).isEmpty());
		Optional<ProductStatus> om = ProductStatus.value( (int) m.value() );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
	}
	
	@RepeatedTest(3)
	void valueString() {
		ProductStatus m = AonRandom.getEnum(ProductStatus.class);
		assertTrue(ProductStatus.value( (String) null).isEmpty());
		assertTrue(ProductStatus.value( "" ).isEmpty());
		assertTrue(ProductStatus.value( "12345|@#" ).isEmpty());
		String n1 = m.name();
		Optional<ProductStatus> om = ProductStatus.value( n1 );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
		
		String n2 = AonStringUtils.lowerCase(n1);
		om = ProductStatus.value( n2 );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
		
		String n3 = AonStringUtils.upperCase(n1);
		om = ProductStatus.value( n3 );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
		
	}
	
}