package net.aonsolutions.occam.api.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

class InvoiceTest {
	
	@RepeatedTest(10)
	void testInvoice() {
		Invoice expected = AonMocker.mock(Invoice.class);
		Invoice actual = new Invoice()
			.setDeleted(expected.isDeleted())
			.setSelected(expected.isSelected())
			.setHeader( expected.getHeader() )
			.setRectificationInvoice( expected.getRectificationInvoice().orElse(null))
			.setInvoiceAddress( expected.getInvoiceAddress().orElse(null) )
			.setFiscal(expected.getFiscal().orElse(null)) 
			.setAttach(expected.getAttach().orElse(null))
			.setRawdocId(expected.getRawdocId())
		;
		expected.messageStream().forEach( m -> actual.addMessage(m));
		expected.detailStream().forEach( d -> actual.addDetail(d));
		expected.financeStream().forEach( d -> actual.addFinance(d));
		AonAsserts.assertClassEquals(expected, actual);
	}
	
	@Test
	void testInvoiceEquals() {
		Invoice a1 = new Invoice();
		a1.setId(1);
		assertEquals(a1,a1);
		assertNotEquals(a1,null);
		assertNotEquals(null,a1);
		assertNotEquals(a1,new Object());
		assertNotEquals(a1,new Account());
		
		Invoice a2 = new Invoice();
		a2.setId(1);
		assertEquals(a1,a2);
		
		Invoice a3 = new Invoice();
		a3.setId(3);
		assertNotEquals(a1,a3);
	}

	
	@Test
	void testHashcode() {
	    List<Invoice> objects = new ArrayList<>();
	    for (int i = 0; i < 1000; i++) {
	    	Invoice inv = new Invoice();
	    	inv.setId(i);
	        objects.add(inv);
	    }
	    Set<Integer> hashCodes = new HashSet<>();
	    for (Invoice obj : objects) {
	        hashCodes.add(obj.hashCode());
	    }
	    assertEquals(objects.size(), hashCodes.size(), 10);
	}	
	
	@Test
	void testRawdoc() {
		Invoice inv = new Invoice();
		assertFalse(inv.isFromRawdoc());
		inv.setRawdocId(12);
		assertTrue(inv.isFromRawdoc());
	}
	
	@Test
	void testAddDetails() {
		Invoice inv = new Invoice();
		InvoiceDetail d1 = AonMocker.mock(InvoiceDetail.class).setId(1).setDeleted(false);
		inv.addDetail( d1 );
		inv.addDetail( d1 );
		inv.addDetail( d1 );
		assertEquals( 1, inv.getDetailsSize() );
	}

	@Test
	void testDeleteDetails() {
		Invoice inv = new Invoice();
		InvoiceDetail d1 = AonMocker.mock(InvoiceDetail.class).setId(1).setDeleted(false);
		inv.addDetail( d1 );
		InvoiceDetail d2 = AonMocker.mock(InvoiceDetail.class).setId(2).setDeleted(false);
		inv.addDetail( d2 );
		InvoiceDetail d3 = AonMocker.mock(InvoiceDetail.class).setId(3).setDeleted(false);
		inv.addDetail( d3 );
		InvoiceDetail d4 = AonMocker.mock(InvoiceDetail.class).setId(4).setDeleted(false);
		inv.addDetail( d4 );
		InvoiceDetail d5 = AonMocker.mock(InvoiceDetail.class).setId(5).setDeleted(false);
		inv.addDetail( d5 );
		InvoiceDetail d6 = AonMocker.mock(InvoiceDetail.class).setId(6).setDeleted(false);
		inv.addDetail( d6 );
		assertEquals( 6, inv.getDetailsSize() );
		
		d3.setDeleted(true);
		assertEquals( 5, inv.getDetailsSize() );
		
		Optional<InvoiceDetail> deleted = inv.deleteDetail( d3 );
		assertTrue(deleted.isPresent());
		assertEquals( 5, inv.getDetailsSize() );
		
		deleted = inv.deleteDetail( d2 );
		assertTrue(deleted.isPresent());
		assertEquals( 4, inv.getDetailsSize() );
		
		inv.deleteDetails();
		assertEquals( 0, inv.getDetailsSize() );
	}
	
	
}
