package net.aonsolutions.occam.api.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import com.esferalia.aon.watson.util.AonCollectionUtils;

import net.aonsolutions.occam.api.model.metadata.InvoiceFiscalMetadata;
import net.aonsolutions.occam.api.model.metadata.InvoiceFiscalMetadata.InvoiceFiscalMetadataVisitor;
import net.aonsolutions.occam.api.model.type.VATTaxRegime;

class InvoiceFiscalTest {

	@Test
	void testInvoiceFiscal() {
		InvoiceFiscal expected = AonMocker.mock(InvoiceFiscal.class);
		InvoiceFiscal actual = new InvoiceFiscal()
			.setInvoice(expected.getInvoice())
			.setDomain(expected.getDomain())
			.setIssueDate(expected.getIssueDate())
			.setTaxDate(expected.getTaxDate())
			.setExpDate(expected.getExpDate())
		;
		actual.setDeleted(expected.isDeleted() );
		actual.setSelected(expected.isSelected() );
		AonCollectionUtils.stream(VATTaxRegime.values())
			.forEach( t -> actual.setVatRegime(t, expected.isVatRegimeEnabled(t)))
		;
		AonAsserts.assertClassEquals(expected, actual);
	}

	@Test
	void testInvoiceFiscalMetadataVisitor() {
		InvoiceFiscalMetadataVisitor<InvoiceFiscalMetadata> v = new InvoiceFiscalMetadataVisitor<>() {
			 @Override public InvoiceFiscalMetadata visitInvoice() {return InvoiceFiscalMetadata.INVOICE;}
			 @Override public InvoiceFiscalMetadata visitDomain() {return InvoiceFiscalMetadata.DOMAIN;}
			 @Override public InvoiceFiscalMetadata visitIssueDate() {return InvoiceFiscalMetadata.ISSUE_DATE;}
			 @Override public InvoiceFiscalMetadata visitTaxDate() {return InvoiceFiscalMetadata.TAX_DATE;}
			 @Override public InvoiceFiscalMetadata visitExpDate() {return InvoiceFiscalMetadata.EXP_DATE;}
			 @Override public InvoiceFiscalMetadata visitVatGeneral() {return InvoiceFiscalMetadata.VAT_GENERAL;}
			 @Override public InvoiceFiscalMetadata visitVatSimplified() {return InvoiceFiscalMetadata.VAT_SIMPLIFIED;}
			 @Override public InvoiceFiscalMetadata visitVatSurcharge() {return InvoiceFiscalMetadata.VAT_SURCHARGE;}
			 @Override public InvoiceFiscalMetadata visitVatAccrualPayment() {return InvoiceFiscalMetadata.VAT_ACCRUAL_PAYMENT;}
			 @Override public InvoiceFiscalMetadata visitVatRebuOperation() {return InvoiceFiscalMetadata.VAT_REBU_OPERATION;}
			 @Override public InvoiceFiscalMetadata visitVatRebuProfit() {return InvoiceFiscalMetadata.VAT_REBU_PROFIT;}
			 @Override public InvoiceFiscalMetadata visitVatTravelAgency() {return InvoiceFiscalMetadata.VAT_TRAVEL_AGENCY;}
			 @Override public InvoiceFiscalMetadata visitVatAgriculture() {return InvoiceFiscalMetadata.VAT_AGRICULTURE;}
			 @Override public InvoiceFiscalMetadata visitVatGold() {return InvoiceFiscalMetadata.VAT_GOLD;}
			 @Override public InvoiceFiscalMetadata visitVatUnionExternal() {return InvoiceFiscalMetadata.VAT_UNION_EXTERNAL;}
			 @Override public InvoiceFiscalMetadata visitVatUnion() {return InvoiceFiscalMetadata.VAT_UNION;}
			 @Override public InvoiceFiscalMetadata visitVatImportation() {return InvoiceFiscalMetadata.VAT_IMPORTATION;}
			 @Override public InvoiceFiscalMetadata visitVatExempt() {return InvoiceFiscalMetadata.VAT_EXEMPT;}
		};
		AonCollectionUtils.stream(InvoiceFiscalMetadata.values())
			.forEach(a -> assertSame(a, a.visit(v)));
	}
	
	@Test
	void testDirtyInvoice() {
		InvoiceFiscal ent = new InvoiceFiscal();
		ent.setInvoice(1);
		assertTrue( ent.isDirty(InvoiceFiscalMetadata.INVOICE) );
	}
	
	@Test
	void testDirtyDomain() {
		InvoiceFiscal ent = new InvoiceFiscal();
		ent.setDomain(1);
		assertTrue( ent.isDirty(InvoiceFiscalMetadata.DOMAIN) );
	}
	
	@Test
	void testDirtyIssueDate() {
		InvoiceFiscal ent = new InvoiceFiscal();
		ent.setIssueDate(new Date());
		assertTrue( ent.isDirty(InvoiceFiscalMetadata.ISSUE_DATE) );
	}
	
	@Test
	void testDirtyTaxDate() {
		InvoiceFiscal ent = new InvoiceFiscal();
		ent.setTaxDate(new Date());
		assertTrue( ent.isDirty(InvoiceFiscalMetadata.TAX_DATE) );
	}
	
	@Test
	void testDirtyExpDate() {
		InvoiceFiscal ent = new InvoiceFiscal();
		ent.setExpDate(new Date());
		assertTrue( ent.isDirty(InvoiceFiscalMetadata.EXP_DATE) );
	}
	
	@Test
	void testDirtyVatGeneral() {
		InvoiceFiscal ent = new InvoiceFiscal();
		ent.setVatRegime(VATTaxRegime.VAT_GENERAL, true);
		assertTrue( ent.isDirty(InvoiceFiscalMetadata.VAT_GENERAL) );
	}
	
	@Test
	void testDirtyVatSimplified() {
		InvoiceFiscal ent = new InvoiceFiscal();
		ent.setVatRegime(VATTaxRegime.VAT_SIMPLIFIED, true);
		assertTrue( ent.isDirty(InvoiceFiscalMetadata.VAT_SIMPLIFIED) );
	}
	
	@Test
	void testDirtyVatSurcharge() {
		InvoiceFiscal ent = new InvoiceFiscal();
		ent.setVatRegime(VATTaxRegime.VAT_SURCHARGE, true);
		assertTrue( ent.isDirty(InvoiceFiscalMetadata.VAT_SURCHARGE) );
	}
	
	@Test
	void testDirtyVatAccrualPayment() {
		InvoiceFiscal ent = new InvoiceFiscal();
		ent.setVatRegime(VATTaxRegime.VAT_ACCRUAL_PAYMENT, true);
		assertTrue( ent.isDirty(InvoiceFiscalMetadata.VAT_ACCRUAL_PAYMENT) );
	}
	
	@Test
	void testDirtyVatRebuOperation() {
		InvoiceFiscal ent = new InvoiceFiscal();
		ent.setVatRegime(VATTaxRegime.VAT_REBU_OPERATION, true);
		assertTrue( ent.isDirty(InvoiceFiscalMetadata.VAT_REBU_OPERATION) );
	}
	
	@Test
	void testDirtyVatRebuProfit() {
		InvoiceFiscal ent = new InvoiceFiscal();
		ent.setVatRegime(VATTaxRegime.VAT_REBU_PROFIT, true);
		assertTrue( ent.isDirty(InvoiceFiscalMetadata.VAT_REBU_PROFIT) );
	}
	
	@Test
	void testDirtyVatTravelAgency() {
		InvoiceFiscal ent = new InvoiceFiscal();
		ent.setVatRegime(VATTaxRegime.VAT_TRAVEL_AGENCY, true);
		assertTrue( ent.isDirty(InvoiceFiscalMetadata.VAT_TRAVEL_AGENCY) );
	}
	
	@Test
	void testDirtyVatAgriculture() {
		InvoiceFiscal ent = new InvoiceFiscal();
		ent.setVatRegime(VATTaxRegime.VAT_AGRICULTURE, true);
		assertTrue( ent.isDirty(InvoiceFiscalMetadata.VAT_AGRICULTURE) );
	}
	
	@Test
	void testDirtyVatGold() {
		InvoiceFiscal ent = new InvoiceFiscal();
		ent.setVatRegime(VATTaxRegime.VAT_GOLD, true);
		assertTrue( ent.isDirty(InvoiceFiscalMetadata.VAT_GOLD) );
	}
	
	@Test
	void testDirtyVatUnionExternal() {
		InvoiceFiscal ent = new InvoiceFiscal();
		ent.setVatRegime(VATTaxRegime.VAT_UNION_EXTERNAL, true);
		assertTrue( ent.isDirty(InvoiceFiscalMetadata.VAT_UNION_EXTERNAL) );
	}
	
	@Test
	void testDirtyVatUnion() {
		InvoiceFiscal ent = new InvoiceFiscal();
		ent.setVatRegime(VATTaxRegime.VAT_UNION, true);
		assertTrue( ent.isDirty(InvoiceFiscalMetadata.VAT_UNION) );
	}
	
	@Test
	void testDirtyVatImportation() {
		InvoiceFiscal ent = new InvoiceFiscal();
		ent.setVatRegime(VATTaxRegime.VAT_IMPORTATION, true);
		assertTrue( ent.isDirty(InvoiceFiscalMetadata.VAT_IMPORTATION) );
	}
	
	@Test
	void testDirtyVatExempt() {
		InvoiceFiscal ent = new InvoiceFiscal();
		ent.setVatRegime(VATTaxRegime.VAT_EXEMPT, true);
		assertTrue( ent.isDirty(InvoiceFiscalMetadata.VAT_EXEMPT) );
	}
	
	@Test
	void testInvoiceFiscalEquals() {
		InvoiceFiscal a1 = new InvoiceFiscal().setInvoice(1);
		assertEquals(a1,a1);
		assertNotEquals(a1,null);
		assertNotEquals(null,a1);
		assertNotEquals(a1,new Object());
		assertNotEquals(a1,new Account());

		InvoiceFiscal a2 = new InvoiceFiscal().setInvoice(1);
		assertEquals(a1,a2);
		InvoiceFiscal a3 = new InvoiceFiscal().setInvoice(3);
		assertNotEquals(a1,a3);
	}

	@Test
	void testHashcode() {
		List<InvoiceFiscal> objects = new ArrayList<>();
		for (int i = 0; i < 1000; i++) {
			objects.add(new InvoiceFiscal().setInvoice(i));
		}
		Set<Integer> hashCodes = new HashSet<>();
		for (InvoiceFiscal obj : objects) {
			hashCodes.add(obj.hashCode());
		}
		assertEquals(objects.size(), hashCodes.size(), 10);
	}
}
