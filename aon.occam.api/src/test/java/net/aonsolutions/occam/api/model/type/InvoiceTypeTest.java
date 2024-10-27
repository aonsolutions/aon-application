package net.aonsolutions.occam.api.model.type;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.occam.api.AbstractOccamApiTest;
import net.aonsolutions.occam.api.model.AonRandom;
import net.aonsolutions.occam.api.model.type.InvoiceType.InvoiceTypeVisitor;

class InvoiceTypeTest extends AbstractOccamApiTest {
	
	@RepeatedTest(3)
	void valueTest() {
		InvoiceType m = AonRandom.getEnum(InvoiceType.class);
		assertSame(m , InvoiceType.values()[m.value()]);
	}
	
	@RepeatedTest(3)
	void valueByte() {
		InvoiceType m = AonRandom.getEnum(InvoiceType.class);
		assertTrue(InvoiceType.value( (Byte) null).isEmpty());
		assertTrue(InvoiceType.value( Byte.MIN_VALUE ).isEmpty());
		assertTrue(InvoiceType.value( Byte.MAX_VALUE).isEmpty());
		
		Optional<InvoiceType> om = InvoiceType.value( m.value() );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
	}

	@RepeatedTest(3)
	void valueInteger() {
		InvoiceType m = AonRandom.getEnum(InvoiceType.class);
		assertTrue(InvoiceType.value( (Integer) null).isEmpty());
		assertTrue(InvoiceType.value( Integer.MIN_VALUE ).isEmpty());
		assertTrue(InvoiceType.value( Integer.MAX_VALUE).isEmpty());
		Optional<InvoiceType> om = InvoiceType.value( (int) m.value() );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
	}
	
	@RepeatedTest(3)
	void valueString() {
		InvoiceType m = AonRandom.getEnum(InvoiceType.class);
		assertTrue(InvoiceType.value( (String) null).isEmpty());
		assertTrue(InvoiceType.value( "" ).isEmpty());
		assertTrue(InvoiceType.value( "12345|@#" ).isEmpty());
		String n1 = m.name();
		Optional<InvoiceType> om = InvoiceType.value( n1 );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
		
		String n2 = AonStringUtils.lowerCase(n1);
		om = InvoiceType.value( n2 );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
		
		String n3 = AonStringUtils.upperCase(n1);
		om = InvoiceType.value( n3 );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
		
	}
	
	@Test
	void testVisitor() {
		InvoiceTypeVisitor<InvoiceType> visitor = new InvoiceTypeVisitor<InvoiceType>() {
			@Override public InvoiceType visitSales() { return InvoiceType.SALES;}
			@Override public InvoiceType visitPurchase() {return InvoiceType.PURCHASE;}
			@Override public InvoiceType visitExpenses() {return InvoiceType.EXPENSES;}
			@Override public InvoiceType visitUndeductible() {return InvoiceType.UNDEDUCTIBLE;}
		}; 
		AonCollectionUtils.stream(InvoiceType.values())
			.forEach(a -> assertSame(a, a.visit(visitor)));
	}
	
}