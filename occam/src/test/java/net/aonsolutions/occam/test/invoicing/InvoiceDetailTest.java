package net.aonsolutions.occam.test.invoicing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.aonsolutions.occam.api.constants.InvoiceSource;
import net.aonsolutions.occam.api.invoicing.InvoiceDetail;
import net.aonsolutions.occam.api.metadata.InvoiceDetailMetadata;
import net.aonsolutions.occam.api.metadata.InvoiceDetailMetadata.InvoiceDetailMetadataVisitor;
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
		assertTrue(d.getDirtySet().contains( InvoiceDetailMetadata.ID ));
	}
	
	@Test()
	void dirtyItemTest() {
		InvoiceDetail d = new InvoiceDetail();
		d.setItem(1);
		assertTrue(d.isDirty());
		assertTrue(d.getDirtySet().contains( InvoiceDetailMetadata.ITEM ));
	}
	
	@Test()
	void dirtyLineTest() {
		InvoiceDetail d = new InvoiceDetail();
		d.setLine(1);
		assertTrue(d.isDirty());
		assertTrue(d.getDirtySet().contains( InvoiceDetailMetadata.LINE));
	}

	@Test()
	void dirtyDescriptionTest() {
		InvoiceDetail d = new InvoiceDetail();
		d.setDescription(AonRandom.string(9));
		assertTrue(d.isDirty());
		assertTrue(d.getDirtySet().contains( InvoiceDetailMetadata.DESCRIPTION));
	}

	@Test()
	void dirtyQuantityTest() {
		InvoiceDetail d = new InvoiceDetail();
		d.setQuantity(Double.valueOf(1.5));
		assertTrue(d.isDirty());
		assertTrue(d.getDirtySet().contains( InvoiceDetailMetadata.QUANTITY));
	}
	
	@Test()
	void dirtyPriceTest() {
		InvoiceDetail d = new InvoiceDetail();
		d.setPrice(Double.valueOf(1.5));
		assertTrue(d.isDirty());
		assertTrue(d.getDirtySet().contains( InvoiceDetailMetadata.PRICE));
	}

	@Test()
	void dirtyDiscountExpressionTest() {
		InvoiceDetail d = new InvoiceDetail();
		d.setDiscountExpression(AonRandom.string(9));
		assertTrue(d.isDirty());
		assertTrue(d.getDirtySet().contains( InvoiceDetailMetadata.DISCOUNT_EXPRESSION));
	}

	@Test()
	void dirtyTaxableBaseTest() {
		InvoiceDetail d = new InvoiceDetail();
		d.setTaxableBase(Double.valueOf(1.5));
		assertTrue(d.isDirty());
		assertTrue(d.getDirtySet().contains( InvoiceDetailMetadata.TAXABLE_BASE));
	}

	@Test()
	void dirtyPrepaymentTest() {
		InvoiceDetail d = new InvoiceDetail();
		d.setPrepayment( true );
		assertTrue(d.isDirty());
		assertTrue(d.getDirtySet().contains( InvoiceDetailMetadata.PREPAYMENT));
	}

	@Test()
	void dirtySourceTest() {
		InvoiceDetail d = new InvoiceDetail();
		d.setSource(InvoiceSource.DIRECT_EXPENSE);
		assertTrue(d.isDirty());
		assertTrue(d.getDirtySet().contains( InvoiceDetailMetadata.SOURCE));
	}

	@Test()
	void dirtySourceIdTest() {
		InvoiceDetail d = new InvoiceDetail();
		d.setSourceId(1);
		assertTrue(d.isDirty());
		assertTrue(d.getDirtySet().contains( InvoiceDetailMetadata.SOURCE_ID));
	}

	@Test()
	void dirtyTaxTest() {
		InvoiceDetail d = new InvoiceDetail();
		d.addTax( AonFaker.getInvoiceTax());
		assertTrue(d.isDirty());
		assertTrue(d.getDirtySet().contains( InvoiceDetailMetadata.TAXES));
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
	void metadataVisitorTest() {
		InvoiceDetailMetadataVisitor<Boolean,InvoiceDetailMetadata> visitor = new InvoiceDetailMetadataVisitor<Boolean, InvoiceDetailMetadata>() {
			@Override public Boolean visitId(InvoiceDetailMetadata t) {return t == InvoiceDetailMetadata.ID; }
			@Override public Boolean visitItem(InvoiceDetailMetadata t) {return t == InvoiceDetailMetadata.ITEM; }
			@Override public Boolean visitLine(InvoiceDetailMetadata t) {return t == InvoiceDetailMetadata.LINE; }
			@Override public Boolean visitDescription(InvoiceDetailMetadata t) {return t == InvoiceDetailMetadata.DESCRIPTION; }
			@Override public Boolean visitQuantity(InvoiceDetailMetadata t) {return t == InvoiceDetailMetadata.QUANTITY; }
			@Override public Boolean visitPrice(InvoiceDetailMetadata t) {return t == InvoiceDetailMetadata.PRICE; }
			@Override public Boolean visitDiscountExpression(InvoiceDetailMetadata t) {return t == InvoiceDetailMetadata.DISCOUNT_EXPRESSION; }
			@Override public Boolean visitTaxableBase(InvoiceDetailMetadata t) {return t == InvoiceDetailMetadata.TAXABLE_BASE; }
			@Override public Boolean visitPrepayment(InvoiceDetailMetadata t) {return t == InvoiceDetailMetadata.PREPAYMENT; }
			@Override public Boolean visitSource(InvoiceDetailMetadata t) {return t == InvoiceDetailMetadata.SOURCE; }
			@Override public Boolean visitSourceId(InvoiceDetailMetadata t) {return t == InvoiceDetailMetadata.SOURCE_ID; }
			@Override public Boolean visitTaxes(InvoiceDetailMetadata t) {return t == InvoiceDetailMetadata.TAXES; }
			@Override public Boolean visitAudit(InvoiceDetailMetadata t) {return t == InvoiceDetailMetadata.AUDIT; }
		}; 
		Arrays.stream(InvoiceDetailMetadata.values()).forEach( dt -> assertTrue(dt.visit(visitor, dt)));		
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