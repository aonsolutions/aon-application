package net.aonsolutions.occam.test.invoicing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.aonsolutions.occam.api.constants.TaxType;
import net.aonsolutions.occam.api.constants.VatDeductionType;
import net.aonsolutions.occam.api.constants.WithholdingType;
import net.aonsolutions.occam.api.invoicing.InvoiceTax;
import net.aonsolutions.occam.api.metadata.InvoiceTaxMetadata;
import net.aonsolutions.occam.api.metadata.InvoiceTaxMetadata.InvoiceTaxMetadataVisitor;
import net.aonsolutions.occam.test.AbstractOccamTest;
import net.aonsolutions.occam.test.TimingExtension;


@ExtendWith(TimingExtension.class)	
class InvoiceTaxTest extends AbstractOccamTest {

	@Test()
	void dirtyIdTest() {
		InvoiceTax d = new InvoiceTax();
		d.setId(1);
		assertTrue(d.isDirty());
		assertTrue(d.getDirtySet().contains( InvoiceTaxMetadata.ID ));
	}
	
	@Test()
	void dirtyTaxTypeTest() {
		InvoiceTax d = new InvoiceTax();
		d.setTaxType(TaxType.RETENTION);
		assertTrue(d.isDirty());
		assertTrue(d.getDirtySet().contains( InvoiceTaxMetadata.TAX_TYPE ));
	}
	
	@Test()
	void dirtyPercentTest() {
		InvoiceTax d = new InvoiceTax();
		d.setPercent(Double.valueOf(1.5));
		assertTrue(d.isDirty());
		assertTrue(d.getDirtySet().contains( InvoiceTaxMetadata.PERCENT ));
	}

	@Test()
	void dirtySurchargePercentTest() {
		InvoiceTax d = new InvoiceTax();
		d.setSurchargePercent(Double.valueOf(1.5));
		assertTrue(d.isDirty());
		assertTrue(d.getDirtySet().contains( InvoiceTaxMetadata.SURCHARGE_PERCENT ));
	}

	@Test()
	void dirtyDeductiblePercentTest() {
		InvoiceTax d = new InvoiceTax();
		d.setDeductiblePercent(Double.valueOf(1.5));
		assertTrue(d.isDirty());
		assertTrue(d.getDirtySet().contains( InvoiceTaxMetadata.DEDUCTIBLE_PERCENT ));
	}

	@Test()
	void dirtyVatDeductionTypeTest() {
		InvoiceTax d = new InvoiceTax();
		d.setVatDeductionType(VatDeductionType.NON_TAXABLE);
		assertTrue(d.isDirty());
		assertTrue(d.getDirtySet().contains( InvoiceTaxMetadata.VAT_DEDUCTION_TYPE ));
	}

	@Test()
	void dirtyWithholdingTypeTest() {
		InvoiceTax d = new InvoiceTax();
		d.setWithholdingType(WithholdingType.M190_F_02_1);
		assertTrue(d.isDirty());
		assertTrue(d.getDirtySet().contains( InvoiceTaxMetadata.WITHHOLDING_TYPE ));
	}

	@Test()
	void dirtyMarkTrueTest() {
		InvoiceTax d = new InvoiceTax();
		d.setPercent( Double.valueOf(7.5) );
		d.setId( null );
		assertTrue(d.isDirty());
	}
	
	@Test()
	void dirtyMarkFalseTest() {
		InvoiceTax d = new InvoiceTax();
		d.setId( null );
		d.setPercent( null );
		assertFalse(d.isDirty());
	}
	
	@Test()
	void selectedMarkTest() {
		InvoiceTax d = new InvoiceTax();
		d.setSelected( true );
		assertTrue(d.isSelected());
	}
	
	@Test()
	void metadataVisitorTest() {
		InvoiceTaxMetadataVisitor<Boolean,InvoiceTaxMetadata> visitor = new InvoiceTaxMetadataVisitor<Boolean, InvoiceTaxMetadata>() {
			@Override public Boolean visitId(InvoiceTaxMetadata t) {return t == InvoiceTaxMetadata.ID; }
			@Override public Boolean visitTaxType(InvoiceTaxMetadata t) {return t == InvoiceTaxMetadata.TAX_TYPE; }
			@Override public Boolean visitPercent(InvoiceTaxMetadata t) {return t == InvoiceTaxMetadata.PERCENT; }
			@Override public Boolean visitSurchargePercent(InvoiceTaxMetadata t) {return t == InvoiceTaxMetadata.SURCHARGE_PERCENT; }
			@Override public Boolean visitDeductiblePercent(InvoiceTaxMetadata t) {return t == InvoiceTaxMetadata.DEDUCTIBLE_PERCENT; }
			@Override public Boolean visitVatDeductionType(InvoiceTaxMetadata t) {return t == InvoiceTaxMetadata.VAT_DEDUCTION_TYPE; }
			@Override public Boolean visitWithholdingType(InvoiceTaxMetadata t) {return t == InvoiceTaxMetadata.WITHHOLDING_TYPE; }
		}; 
		Arrays.stream(InvoiceTaxMetadata.values()).forEach( dt -> assertTrue(dt.visit(visitor, dt)));		
	}

	@Test()
	void equalsTest() {
		InvoiceTax d1 = new InvoiceTax();
		InvoiceTax d2 = null;
		assertNotEquals(d1,d2);
		assertEquals(d1,d1);
		d2 = new InvoiceTax();
		assertEquals(d1,d2);
		d1.setId(1);
		assertNotEquals(d1,d2);
		d2.setId(1);
		assertEquals(d1,d2);
	}

}