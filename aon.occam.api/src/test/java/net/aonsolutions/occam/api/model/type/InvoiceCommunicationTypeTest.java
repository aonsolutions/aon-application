package net.aonsolutions.occam.api.model.type;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.RepeatedTest;

import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.occam.api.AbstractOccamApiTest;
import net.aonsolutions.occam.api.model.AonRandom;

class InvoiceCommunicationTypeTest extends AbstractOccamApiTest {
	
	@RepeatedTest(3)
	void valueTest() {
		InvoiceCommunicationType m = AonRandom.getEnum(InvoiceCommunicationType.class);
		assertSame(m , InvoiceCommunicationType.values()[m.value()]);
	}
	
	@RepeatedTest(3)
	void valueByte() {
		InvoiceCommunicationType m = AonRandom.getEnum(InvoiceCommunicationType.class);
		assertTrue(InvoiceCommunicationType.value( (Byte) null).isEmpty());
		assertTrue(InvoiceCommunicationType.value( Byte.MIN_VALUE ).isEmpty());
		assertTrue(InvoiceCommunicationType.value( Byte.MAX_VALUE).isEmpty());
		
		Optional<InvoiceCommunicationType> om = InvoiceCommunicationType.value( m.value() );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
	}

	@RepeatedTest(3)
	void valueInteger() {
		InvoiceCommunicationType m = AonRandom.getEnum(InvoiceCommunicationType.class);
		assertTrue(InvoiceCommunicationType.value( (Integer) null).isEmpty());
		assertTrue(InvoiceCommunicationType.value( Integer.MIN_VALUE ).isEmpty());
		assertTrue(InvoiceCommunicationType.value( Integer.MAX_VALUE).isEmpty());
		Optional<InvoiceCommunicationType> om = InvoiceCommunicationType.value( (int) m.value() );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
	}
	
	@RepeatedTest(3)
	void valueString() {
		InvoiceCommunicationType m = AonRandom.getEnum(InvoiceCommunicationType.class);
		assertTrue(InvoiceCommunicationType.value( (String) null).isEmpty());
		assertTrue(InvoiceCommunicationType.value( "" ).isEmpty());
		assertTrue(InvoiceCommunicationType.value( "12345|@#" ).isEmpty());
		String n1 = m.name();
		Optional<InvoiceCommunicationType> om = InvoiceCommunicationType.value( n1 );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
		
		String n2 = AonStringUtils.lowerCase(n1);
		om = InvoiceCommunicationType.value( n2 );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
		
		String n3 = AonStringUtils.upperCase(n1);
		om = InvoiceCommunicationType.value( n3 );
		assertTrue(om.isPresent());
		assertSame(m, om.get());
		
	}
	
}