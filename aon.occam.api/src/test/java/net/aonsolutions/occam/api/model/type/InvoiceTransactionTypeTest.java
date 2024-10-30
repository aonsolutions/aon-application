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
import net.aonsolutions.occam.api.model.type.InvoiceTransactionType.InvoiceTransactionTypeVisitor;

class InvoiceTransactionTypeTest extends AbstractOccamApiTest {
	
	@RepeatedTest(3)
	void valueTest() {
		InvoiceTransactionType m = AonRandom.getEnum(InvoiceTransactionType.class);
		assertSame(m , InvoiceTransactionType.values()[m.value()]);
	}
	
	@RepeatedTest(3)
	void valueByte() {
		InvoiceTransactionType m = AonRandom.getEnum(InvoiceTransactionType.class);
		assertTrue(InvoiceTransactionType.value( (Byte) null).isEmpty());
		assertTrue(InvoiceTransactionType.value( Byte.MIN_VALUE ).isEmpty());
		assertTrue(InvoiceTransactionType.value( Byte.MAX_VALUE).isEmpty());
		
		Optional<InvoiceTransactionType> om = InvoiceTransactionType.value( m.value() );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
	}

	@RepeatedTest(3)
	void valueInteger() {
		InvoiceTransactionType m = AonRandom.getEnum(InvoiceTransactionType.class);
		assertTrue(InvoiceTransactionType.value( (Integer) null).isEmpty());
		assertTrue(InvoiceTransactionType.value( Integer.MIN_VALUE ).isEmpty());
		assertTrue(InvoiceTransactionType.value( Integer.MAX_VALUE).isEmpty());
		Optional<InvoiceTransactionType> om = InvoiceTransactionType.value( (int) m.value() );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
	}
	
	@RepeatedTest(3)
	void valueString() {
		InvoiceTransactionType m = AonRandom.getEnum(InvoiceTransactionType.class);
		assertTrue(InvoiceTransactionType.value( (String) null).isEmpty());
		assertTrue(InvoiceTransactionType.value( "" ).isEmpty());
		assertTrue(InvoiceTransactionType.value( "12345|@#" ).isEmpty());
		String n1 = m.name();
		Optional<InvoiceTransactionType> om = InvoiceTransactionType.value( n1 );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
		
		String n2 = AonStringUtils.lowerCase(n1);
		om = InvoiceTransactionType.value( n2 );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
		
		String n3 = AonStringUtils.upperCase(n1);
		om = InvoiceTransactionType.value( n3 );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
		
	}

	@Test
	void testVisitor() {
		InvoiceTransactionTypeVisitor<InvoiceTransactionType> visitor = new InvoiceTransactionTypeVisitor<InvoiceTransactionType>() {
			@Override public InvoiceTransactionType visitNational() {return InvoiceTransactionType.NATIONAL;}
			@Override public InvoiceTransactionType visitIntracommunity() {return InvoiceTransactionType.INTRACOMMUNITY;}
			@Override public InvoiceTransactionType visitExtracommunity() {return InvoiceTransactionType.EXTRACOMMUNITY;}
			@Override public InvoiceTransactionType visitCanCeuMel() {return InvoiceTransactionType.CAN_CEU_MEL;}
			@Override public InvoiceTransactionType visitOtherISP() {return InvoiceTransactionType.OTHER_ISP;}
		}; 
		AonCollectionUtils.stream(InvoiceTransactionType.values())
			.forEach(a -> assertSame(a, a.visit(visitor)));
	}
	
}