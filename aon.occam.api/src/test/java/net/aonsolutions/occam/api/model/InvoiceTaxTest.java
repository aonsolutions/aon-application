package net.aonsolutions.occam.api.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import com.esferalia.aon.watson.util.AonCollectionUtils;

import net.aonsolutions.occam.api.model.metadata.InvoiceTaxMetadata;
import net.aonsolutions.occam.api.model.metadata.InvoiceTaxMetadata.InvoiceTaxMetadataVisitor;
import net.aonsolutions.occam.api.model.type.TaxType;
import net.aonsolutions.occam.api.model.type.VatDeductionType;
import net.aonsolutions.occam.api.model.type.WithholdingType;

class InvoiceTaxTest {

	
	@Test
	void testInvoiceTax() {
		InvoiceTax expected = AonMocker.mock(InvoiceTax.class);
		InvoiceTax actual = new InvoiceTax()
			.setId(expected.getId())
			.setDomain(expected.getDomain())
			.setInvoiceDetail(expected.getInvoiceDetail())
			.setTaxType(expected.getTaxType())
			.setBase(expected.getBase())
			.setPercentage(expected.getPercentage())
			.setQuota(expected.getQuota())
			.setSurcharge(expected.getSurcharge())
			.setSurchargeQuota(expected.getSurchargeQuota())
			.setVatDeductionType(expected.getVatDeductionType())
			.setDeductiblePercent(expected.getDeductiblePercent())
			.setDeductibleQuota(expected.getDeductibleQuota())
			.setWithholdingType(expected.getWithholdingType())
			.setWithholdingAccount(expected.getWithholdingAccount().orElse(null))
			.setOutputAccount(expected.getOutputAccount().orElse(null))
			.setInputAccount(expected.getInputAccount().orElse(null))
			.setAdjAccount(expected.getAdjAccount().orElse(null))
			.setQuotaEdited(expected.isQuotaEdited())
			.setSurchargeQuotaEdited(expected.isSurchargeQuotaEdited())
			.setDeductibleQuotaEdited(expected.isDeductibleQuotaEdited())
			.setDeleted(expected.isDeleted())
			.setSelected(expected.isSelected())
		;
		AonAsserts.assertClassEquals(expected, actual);
	}

	@Test
	void testInvoiceTaxMetadataVisitor() {
		InvoiceTaxMetadataVisitor<InvoiceTaxMetadata> v = new InvoiceTaxMetadataVisitor<>() {
			@Override public InvoiceTaxMetadata visitId() {return InvoiceTaxMetadata.ID;}
			@Override public InvoiceTaxMetadata visitDomain() {return InvoiceTaxMetadata.DOMAIN;}
			@Override public InvoiceTaxMetadata visitInvoiceDetail() {return InvoiceTaxMetadata.INVOICE_DETAIL;}
			@Override public InvoiceTaxMetadata visitTaxType() {return InvoiceTaxMetadata.TAX_TYPE;}
			@Override public InvoiceTaxMetadata visitBase() {return InvoiceTaxMetadata.BASE;}
			@Override public InvoiceTaxMetadata visitPercentage() {return InvoiceTaxMetadata.PERCENTAGE;}
			@Override public InvoiceTaxMetadata visitSurcharge() {return InvoiceTaxMetadata.SURCHARGE;}
			@Override public InvoiceTaxMetadata visitQuota() {return InvoiceTaxMetadata.QUOTA;}
			@Override public InvoiceTaxMetadata visitSurchargeQuota() {return InvoiceTaxMetadata.SURCHARGE_QUOTA;}
			@Override public InvoiceTaxMetadata visitVatDeductionType() {return InvoiceTaxMetadata.VAT_DEDUCTION_TYPE;}
			@Override public InvoiceTaxMetadata visitWithholdingType() {return InvoiceTaxMetadata.WITHHOLDING_TYPE;}
			@Override public InvoiceTaxMetadata visitDeductiblePercent() {return InvoiceTaxMetadata.DEDUCTIBLE_PERCENT;}
			@Override public InvoiceTaxMetadata visitDeductibleQuota() {return InvoiceTaxMetadata.DEDUCTIBLE_QUOTA;}
			@Override public InvoiceTaxMetadata visitWithholdingAccount() {return InvoiceTaxMetadata.WITHHOLDING_ACCOUNT;}
			@Override public InvoiceTaxMetadata visitOutputAccount() {return InvoiceTaxMetadata.OUTPUT_ACCOUNT;}
			@Override public InvoiceTaxMetadata visitInputAccount() {return InvoiceTaxMetadata.INPUT_ACCOUNT;}
			@Override public InvoiceTaxMetadata visitAdjAccount() {return InvoiceTaxMetadata.ADJ_ACCOUNT;}
		};
		AonCollectionUtils.stream(InvoiceTaxMetadata.values())
			.forEach(a -> assertSame(a, a.visit(v)));
	}
	
	@Test
	void testDirtyId() {
		InvoiceTax ent = new InvoiceTax();
		ent.setId(1);
		assertTrue( ent.isDirty(InvoiceTaxMetadata.ID) );
	}
	
	@Test
	void testDirtyDomain() {
		InvoiceTax ent = new InvoiceTax();
		ent.setDomain(1);
		assertTrue( ent.isDirty(InvoiceTaxMetadata.DOMAIN) );
	}
	
	@Test
	void testDirtyInvoiceDetail() {
		InvoiceTax ent = new InvoiceTax();
		ent.setInvoiceDetail(1);
		assertTrue( ent.isDirty(InvoiceTaxMetadata.INVOICE_DETAIL) );
	}
	
	@Test
	void testDirtyTaxType() {
		InvoiceTax ent = new InvoiceTax();
		ent.setTaxType(TaxType.RETENTION);
		assertTrue( ent.isDirty(InvoiceTaxMetadata.TAX_TYPE) );
	}
	
	@Test
	void testDirtyBase() {
		InvoiceTax ent = new InvoiceTax();
		ent.setBase(1);
		assertTrue( ent.isDirty(InvoiceTaxMetadata.BASE) );
	}
	
	@Test
	void testDirtyPercentage() {
		InvoiceTax ent = new InvoiceTax();
		ent.setPercentage(1);
		assertTrue( ent.isDirty(InvoiceTaxMetadata.PERCENTAGE) );
	}
	
	@Test
	void testDirtySurcharge() {
		InvoiceTax ent = new InvoiceTax();
		ent.setSurcharge(1);
		assertTrue( ent.isDirty(InvoiceTaxMetadata.SURCHARGE) );
	}
	
	@Test
	void testDirtyQuota() {
		InvoiceTax ent = new InvoiceTax();
		ent.setQuota(1);
		assertTrue( ent.isDirty(InvoiceTaxMetadata.QUOTA) );
	}
	
	@Test
	void testDirtySurchargeQuota() {
		InvoiceTax ent = new InvoiceTax();
		ent.setSurchargeQuota(1);
		assertTrue( ent.isDirty(InvoiceTaxMetadata.SURCHARGE_QUOTA) );
	}
	
	@Test
	void testDirtyVatDeductionType() {
		InvoiceTax ent = new InvoiceTax();
		ent.setVatDeductionType(VatDeductionType.WITH_RIGHT);
		assertTrue( ent.isDirty(InvoiceTaxMetadata.VAT_DEDUCTION_TYPE) );
	}
	
	@Test
	void testDirtyWithholdingType() {
		InvoiceTax ent = new InvoiceTax();
		ent.setWithholdingType(WithholdingType.M190_I_02);
		assertTrue( ent.isDirty(InvoiceTaxMetadata.WITHHOLDING_TYPE) );
	}
	
	@Test
	void testDirtyDeductiblePercent() {
		InvoiceTax ent = new InvoiceTax();
		ent.setDeductiblePercent(1);
		assertTrue( ent.isDirty(InvoiceTaxMetadata.DEDUCTIBLE_PERCENT) );
	}
	
	@Test
	void testDirtyDeductibleQuota() {
		InvoiceTax ent = new InvoiceTax();
		ent.setDeductibleQuota(1);
		assertTrue( ent.isDirty(InvoiceTaxMetadata.DEDUCTIBLE_QUOTA) );
	}
	@Test
	void testInvoiceTaxEquals() {
		InvoiceTax a1 = new InvoiceTax().setId(1);
		assertEquals(a1,a1);
		assertNotEquals(a1,null);
		assertNotEquals(null,a1);
		assertNotEquals(a1,new Object());
		assertNotEquals(a1,new Account());

		InvoiceTax a2 = new InvoiceTax().setId(1);
		assertEquals(a1,a2);
		InvoiceTax a3 = new InvoiceTax().setId(3);
		assertNotEquals(a1,a3);
	}

	@Test
	void testHashcode() {
		List<InvoiceTax> objects = new ArrayList<>();
		for (int i = 0; i < 1000; i++) {
			objects.add(new InvoiceTax().setId(i));
		}
		Set<Integer> hashCodes = new HashSet<>();
		for (InvoiceTax obj : objects) {
			hashCodes.add(obj.hashCode());
		}
		assertEquals(objects.size(), hashCodes.size(), 10);
	}
}
