package net.aonsolutions.occam.test.invoicing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.aonsolutions.occam.api.constants.InvoiceSource;
import net.aonsolutions.occam.api.invoicing.InvoiceDetail;
import net.aonsolutions.occam.test.AbstractOccamTest;
import net.aonsolutions.occam.test.TimingExtension;
import net.aonsolutions.occam.test.faker.AonFaker;
import net.aonsolutions.occam.test.faker.AonRandom;


@ExtendWith(TimingExtension.class)	
class InvoiceDetailTest extends AbstractOccamTest {

	@Test()
	void dirtyIdTest() {
		InvoiceDetail d = new InvoiceDetail();
		d.setId(1);
		assertTrue(d.isDirty());
	}
	
	@Test()
	void dirtyItemTest() {
		InvoiceDetail d = new InvoiceDetail();
		d.setItem(1);
		assertTrue(d.isDirty());
	}
	
	@Test()
	void dirtyLineTest() {
		InvoiceDetail d = new InvoiceDetail();
		d.setLine(1);
		assertTrue(d.isDirty());
	}

	@Test()
	void dirtyDescriptionTest() {
		InvoiceDetail d = new InvoiceDetail();
		d.setDescription(AonRandom.string(9));
		assertTrue(d.isDirty());
	}

	@Test()
	void dirtyQuantityTest() {
		InvoiceDetail d = new InvoiceDetail();
		d.setQuantity(Double.valueOf(1.5));
		assertTrue(d.isDirty());
	}
	
	@Test()
	void dirtyPriceTest() {
		InvoiceDetail d = new InvoiceDetail();
		d.setPrice(Double.valueOf(1.5));
		assertTrue(d.isDirty());
	}

	@Test()
	void dirtyDiscountExpressionTest() {
		InvoiceDetail d = new InvoiceDetail();
		d.setDiscountExpression(AonRandom.string(9));
		assertTrue(d.isDirty());
	}

	@Test()
	void dirtyTaxableBaseTest() {
		InvoiceDetail d = new InvoiceDetail();
		d.setTaxableBase(Double.valueOf(1.5));
		assertTrue(d.isDirty());
	}

	@Test()
	void dirtyPrepaymentTest() {
		InvoiceDetail d = new InvoiceDetail();
		d.setPrepayment( true );
		assertTrue(d.isDirty());
	}

	@Test()
	void dirtySourceTest() {
		InvoiceDetail d = new InvoiceDetail();
		d.setSource(InvoiceSource.DIRECT_EXPENSE);
		assertTrue(d.isDirty());
	}

	@Test()
	void dirtySourceIdTest() {
		InvoiceDetail d = new InvoiceDetail();
		d.setSourceId(1);
		assertTrue(d.isDirty());
	}

	@Test()
	void dirtyTaxTest() {
		InvoiceDetail d = new InvoiceDetail();
		d.addTax( AonFaker.getInvoiceTax());
		assertTrue(d.isDirty());
	}
	
	@Test()
	void dirtyMarkTrueTest() {
		InvoiceDetail d = new InvoiceDetail();
		d.setDescription( "dddddd" );
		d.setId( null );
		assertTrue(d.isDirty());
	}
	
	@Test()
	void dirtyMarkFalseTest() {
		InvoiceDetail d = new InvoiceDetail();
		d.setId( null );
		d.setDescription( null );
		assertFalse(d.isDirty());
	}
	
	@Test()
	void equalsTest() {
		InvoiceDetail d1 = new InvoiceDetail();
		InvoiceDetail d2 = null;
		assertNotEquals(d1,d2);
		assertEquals(d1,d1);
		d2 = new InvoiceDetail();
		assertEquals(d1,d2);
		d1.setId(1);
		assertNotEquals(d1,d2);
		d2.setId(1);
		assertEquals(d1,d2);
	}

}