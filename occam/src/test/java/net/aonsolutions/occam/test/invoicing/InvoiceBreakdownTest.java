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
import net.aonsolutions.occam.api.invoicing.InvoiceBreakdown;
import net.aonsolutions.occam.api.metadata.InvoiceBreakdownMetadata;
import net.aonsolutions.occam.api.metadata.InvoiceBreakdownMetadata.InvoiceBreakdownMetadataVisitor;
import net.aonsolutions.occam.test.AbstractOccamTest;
import net.aonsolutions.occam.test.TimingExtension;


@ExtendWith(TimingExtension.class)	
class InvoiceBreakdownTest extends AbstractOccamTest {

	@Test()
	void dirtyIdTest() {
		InvoiceBreakdown d = new InvoiceBreakdown();
		d.setId(1);
		assertTrue(d.isDirty());
		assertTrue(d.getDirtySet().contains( InvoiceBreakdownMetadata.ID ));
	}
	
	@Test()
	void dirtyTaxTypeTest() {
		InvoiceBreakdown d = new InvoiceBreakdown();
		d.setTaxType(TaxType.RETENTION);
		assertTrue(d.isDirty());
		assertTrue(d.getDirtySet().contains( InvoiceBreakdownMetadata.TAX_TYPE ));
	}
	
	@Test()
	void dirtyBaseTest() {
		InvoiceBreakdown d = new InvoiceBreakdown();
		d.setBase(Double.valueOf(1.5));
		assertTrue(d.isDirty());
		assertTrue(d.getDirtySet().contains( InvoiceBreakdownMetadata.BASE ));
	}

	@Test()
	void dirtyPercentTest() {
		InvoiceBreakdown d = new InvoiceBreakdown();
		d.setPercent(Double.valueOf(1.5));
		assertTrue(d.isDirty());
		assertTrue(d.getDirtySet().contains( InvoiceBreakdownMetadata.PERCENT ));
	}

	@Test()
	void dirtyQuotaTest() {
		InvoiceBreakdown d = new InvoiceBreakdown();
		d.setQuota(Double.valueOf(1.5));
		assertTrue(d.isDirty());
		assertTrue(d.getDirtySet().contains( InvoiceBreakdownMetadata.QUOTA ));
	}

	@Test()
	void dirtySurchargePercentTest() {
		InvoiceBreakdown d = new InvoiceBreakdown();
		d.setSurchargePercent(Double.valueOf(1.5));
		assertTrue(d.isDirty());
		assertTrue(d.getDirtySet().contains( InvoiceBreakdownMetadata.SURCHARGE_PERCENT ));
	}

	@Test()
	void dirtySurchargeQuotaTest() {
		InvoiceBreakdown d = new InvoiceBreakdown();
		d.setSurchargeQuota(Double.valueOf(1.5));
		assertTrue(d.isDirty());
		assertTrue(d.getDirtySet().contains( InvoiceBreakdownMetadata.SURCHARGE_QUOTA ));
	}

	@Test()
	void dirtyDeductiblePercentTest() {
		InvoiceBreakdown d = new InvoiceBreakdown();
		d.setDeductiblePercent(Double.valueOf(1.5));
		assertTrue(d.isDirty());
		assertTrue(d.getDirtySet().contains( InvoiceBreakdownMetadata.DEDUCTIBLE_PERCENT ));
	}

	@Test()
	void dirtyDeductibleQuotaTest() {
		InvoiceBreakdown d = new InvoiceBreakdown();
		d.setDeductibleQuota(Double.valueOf(1.5));
		assertTrue(d.isDirty());
		assertTrue(d.getDirtySet().contains( InvoiceBreakdownMetadata.DEDUCTIBLE_QUOTA ));
	}

	@Test()
	void dirtyVatDeductionTypeTest() {
		InvoiceBreakdown d = new InvoiceBreakdown();
		d.setVatDeductionType(VatDeductionType.NON_TAXABLE);
		assertTrue(d.isDirty());
		assertTrue(d.getDirtySet().contains( InvoiceBreakdownMetadata.VAT_DEDUCTION_TYPE ));
	}

	@Test()
	void dirtyWithholdingTypeTest() {
		InvoiceBreakdown d = new InvoiceBreakdown();
		d.setWithholdingType(WithholdingType.M190_F_02_1);
		assertTrue(d.isDirty());
		assertTrue(d.getDirtySet().contains( InvoiceBreakdownMetadata.WITHHOLDING_TYPE ));
	}

	@Test()
	void dirtyMarkTrueTest() {
		InvoiceBreakdown d = new InvoiceBreakdown();
		d.setTaxType( TaxType.RETENTION );
		d.setId( null );
		assertTrue(d.isDirty());
	}
	
	@Test()
	void dirtyMarkFalseTest() {
		InvoiceBreakdown d = new InvoiceBreakdown();
		d.setId( null );
		d.setTaxType( null );
		assertFalse(d.isDirty());
	}
	
	@Test()
	void selectedMarkTest() {
		InvoiceBreakdown d = new InvoiceBreakdown();
		d.setSelected( true );
		assertTrue(d.isSelected());
	}
	
	@Test()
	void metadataVisitorTest() {
		InvoiceBreakdownMetadataVisitor<Boolean,InvoiceBreakdownMetadata> visitor = new InvoiceBreakdownMetadataVisitor<Boolean, InvoiceBreakdownMetadata>() {
			@Override public Boolean visitId(InvoiceBreakdownMetadata t) {return t == InvoiceBreakdownMetadata.ID; }
			@Override public Boolean visitTaxType(InvoiceBreakdownMetadata t) {return t == InvoiceBreakdownMetadata.TAX_TYPE; }
			@Override public Boolean visitBase(InvoiceBreakdownMetadata t) {return t == InvoiceBreakdownMetadata.BASE; }
			@Override public Boolean visitPercent(InvoiceBreakdownMetadata t) {return t == InvoiceBreakdownMetadata.PERCENT; }
			@Override public Boolean visitQuota(InvoiceBreakdownMetadata t) {return t == InvoiceBreakdownMetadata.QUOTA; }
			@Override public Boolean visitSurchargePercent(InvoiceBreakdownMetadata t) {return t == InvoiceBreakdownMetadata.SURCHARGE_PERCENT; }
			@Override public Boolean visitSurchargeQuota(InvoiceBreakdownMetadata t) {return t == InvoiceBreakdownMetadata.SURCHARGE_QUOTA; }
			@Override public Boolean visitDeductiblePercent(InvoiceBreakdownMetadata t) {return t == InvoiceBreakdownMetadata.DEDUCTIBLE_PERCENT; }
			@Override public Boolean visitDeductibleQuota(InvoiceBreakdownMetadata t) {return t == InvoiceBreakdownMetadata.DEDUCTIBLE_QUOTA; }
			@Override public Boolean visitVatDeductionType(InvoiceBreakdownMetadata t) {return t == InvoiceBreakdownMetadata.VAT_DEDUCTION_TYPE; }
			@Override public Boolean visitWithholdingType(InvoiceBreakdownMetadata t) {return t == InvoiceBreakdownMetadata.WITHHOLDING_TYPE; }
		}; 
		Arrays.stream(InvoiceBreakdownMetadata.values()).forEach( dt -> assertTrue(dt.visit(visitor, dt)));		
	}
	
	@Test()
	void equalsTest() {
		InvoiceBreakdown d1 = new InvoiceBreakdown();
		InvoiceBreakdown d2 = null;
		assertNotEquals(d1,d2);
		assertEquals(d1,d1);
		d2 = new InvoiceBreakdown();
		assertEquals(d1,d2);
		d1.setId(1);
		assertNotEquals(d1,d2);
		d2.setId(1);
		assertEquals(d1,d2);
	}

}