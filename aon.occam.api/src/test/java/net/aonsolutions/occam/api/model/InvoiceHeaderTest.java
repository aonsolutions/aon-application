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

import net.aonsolutions.occam.api.model.metadata.InvoiceHeaderMetadata;
import net.aonsolutions.occam.api.model.metadata.InvoiceHeaderMetadata.InvoiceHeaderMetadataVisitor;
import net.aonsolutions.occam.api.model.type.Country;
import net.aonsolutions.occam.api.model.type.DocumentType;
import net.aonsolutions.occam.api.model.type.InvoiceTransactionType;
import net.aonsolutions.occam.api.model.type.InvoiceType;
import net.aonsolutions.occam.api.model.type.RectificationType;

class InvoiceHeaderTest {

	@Test
	void testInvoice() {
		InvoiceHeader expected = AonMocker.mock(InvoiceHeader.class);
		InvoiceHeader actual = new InvoiceHeader()
			.setId(expected.getId())
			.setDomain(expected.getDomain())
			.setActivity(expected.getActivity().orElse(null))
			//.setInvestAsset(expected.getInvestAsset().orElse(null))
			.setProject(expected.getProject())
			.setSeries(expected.getSeries())
			.setNumber(expected.getNumber())
			.setReferenceCode(expected.getReferenceCode())
			.setRegistry(expected.getRegistry())
			.setRegistryDocument(expected.getRegistryDocument())
			.setRegistryDocumentType(expected.getRegistryDocumentType())
			.setRegistryDocumentCountry(expected.getRegistryDocumentCountry())
			.setRegistryName(expected.getRegistryName())
			.setRegistryAccount(expected.getRegistryAccount().orElse(null))
			//.setRaddress(expected.getRaddress())
			.setIssueDate(expected.getIssueDate())
			.setTaxDate(expected.getTaxDate())
			.setConfidential(expected.isConfidential())
			.setRecorded(expected.isRecorded())
			.setType(expected.getType())
			.setSurcharge(expected.isSurcharge())
			.setWithholding(expected.isWithholding())
			.setWithholdingFarmer(expected.isWithholdingFarmer())
			.setVatAccrualPayment(expected.isVatAccrualPayment())
			.setComments(expected.getComments())
			.setRemarks(expected.getRemarks())
			.setInvestment(expected.isInvestment())
			.setAnnulled(expected.isAnnulled())
			.setTransaction(expected.getTransaction())
			.setSigned(expected.isSigned())
			.setScope(expected.getScope())
			.setService(expected.isService())
			.setRectificationType(expected.getRectificationType())
			.setRectificationInvoiceId(expected.getRectificationInvoiceId())
			//.setAdvance(expected.isAdvance())
			//.setPosShift(expected.getPosShift())
			.setSeller(expected.getSeller().orElse(null))
			.setTaxableBase(expected.getTaxableBase())
			.setVatQuota(expected.getVatQuota())
			.setRetentionQuota(expected.getRetentionQuota())
			.setTotal(expected.getTotal())
			.setCreationUser(expected.getCreationUser())
			.setCreationDate(expected.getCreationDate())
			.setModificationUser(expected.getModificationUser())
			.setModificationDate(expected.getModificationDate())
			.setDeleted(expected.isDeleted())
		;
		AonAsserts.assertClassEquals(expected, actual);
	}

	@Test
	void testInvoiceMetadataVisitor() {
		InvoiceHeaderMetadataVisitor<InvoiceHeaderMetadata> v = new InvoiceHeaderMetadataVisitor<>() {
			 @Override public InvoiceHeaderMetadata visitId() {return InvoiceHeaderMetadata.ID;}
			 @Override public InvoiceHeaderMetadata visitDomain() {return InvoiceHeaderMetadata.DOMAIN;}
			 @Override public InvoiceHeaderMetadata visitActivity() {return InvoiceHeaderMetadata.ACTIVITY;}
			 @Override public InvoiceHeaderMetadata visitProject() {return InvoiceHeaderMetadata.PROJECT;}
			 @Override public InvoiceHeaderMetadata visitSeries() {return InvoiceHeaderMetadata.SERIES;}
			 @Override public InvoiceHeaderMetadata visitNumber() {return InvoiceHeaderMetadata.NUMBER;}
			 @Override public InvoiceHeaderMetadata visitReferenceCode() {return InvoiceHeaderMetadata.REFERENCE_CODE;}
			 @Override public InvoiceHeaderMetadata visitRegistry() {return InvoiceHeaderMetadata.REGISTRY;}
			 @Override public InvoiceHeaderMetadata visitRdocument() {return InvoiceHeaderMetadata.RDOCUMENT;}
			 @Override public InvoiceHeaderMetadata visitRdocumentType() {return InvoiceHeaderMetadata.RDOCUMENT_TYPE;}
			 @Override public InvoiceHeaderMetadata visitRdocumentCountry() {return InvoiceHeaderMetadata.RDOCUMENT_COUNTRY;}
			 @Override public InvoiceHeaderMetadata visitRname() {return InvoiceHeaderMetadata.RNAME;}
			 @Override public InvoiceHeaderMetadata visitIssueDate() {return InvoiceHeaderMetadata.ISSUE_DATE;}
			 @Override public InvoiceHeaderMetadata visitTaxDate() {return InvoiceHeaderMetadata.TAX_DATE;}
			 @Override public InvoiceHeaderMetadata visitSecurityLevel() {return InvoiceHeaderMetadata.SECURITY_LEVEL;}
			 @Override public InvoiceHeaderMetadata visitStatus() {return InvoiceHeaderMetadata.STATUS;}
			 @Override public InvoiceHeaderMetadata visitType() {return InvoiceHeaderMetadata.TYPE;}
			 @Override public InvoiceHeaderMetadata visitSurcharge() {return InvoiceHeaderMetadata.SURCHARGE;}
			 @Override public InvoiceHeaderMetadata visitWithholding() {return InvoiceHeaderMetadata.WITHHOLDING;}
			 @Override public InvoiceHeaderMetadata visitWithholdingFarmer() {return InvoiceHeaderMetadata.WITHHOLDING_FARMER;}
			 @Override public InvoiceHeaderMetadata visitVatAccrualPayment() {return InvoiceHeaderMetadata.VAT_ACCRUAL_PAYMENT;}
			 @Override public InvoiceHeaderMetadata visitComments() {return InvoiceHeaderMetadata.COMMENTS;}
			 @Override public InvoiceHeaderMetadata visitRemarks() {return InvoiceHeaderMetadata.REMARKS;}
			 @Override public InvoiceHeaderMetadata visitInvestment() {return InvoiceHeaderMetadata.INVESTMENT;}
			 @Override public InvoiceHeaderMetadata visitTransaction() {return InvoiceHeaderMetadata.TRANSACTION;}
			 @Override public InvoiceHeaderMetadata visitSigned() {return InvoiceHeaderMetadata.SIGNED;}
			 @Override public InvoiceHeaderMetadata visitScope() {return InvoiceHeaderMetadata.SCOPE;}
			 @Override public InvoiceHeaderMetadata visitService() {return InvoiceHeaderMetadata.SERVICE;}
			 @Override public InvoiceHeaderMetadata visitRectificationType() {return InvoiceHeaderMetadata.RECTIFICATION_TYPE;}
			 @Override public InvoiceHeaderMetadata visitRectificationInvoice() {return InvoiceHeaderMetadata.RECTIFICATION_INVOICE;}
			 @Override public InvoiceHeaderMetadata visitSeller() {return InvoiceHeaderMetadata.SELLER;}
			 @Override public InvoiceHeaderMetadata visitTaxableBase() {return InvoiceHeaderMetadata.TAXABLE_BASE;}
			 @Override public InvoiceHeaderMetadata visitVatQuota() {return InvoiceHeaderMetadata.VAT_QUOTA;}
			 @Override public InvoiceHeaderMetadata visitRetentionQuota() {return InvoiceHeaderMetadata.RETENTION_QUOTA;}
			 @Override public InvoiceHeaderMetadata visitTotal() {return InvoiceHeaderMetadata.TOTAL;}
			 @Override public InvoiceHeaderMetadata visitAccount() {return InvoiceHeaderMetadata.ACCOUNT;}
		};
		AonCollectionUtils.stream(InvoiceHeaderMetadata.values())
			.forEach(a -> assertSame(a, a.visit(v)));
	}
	
	@Test
	void testDirtyId() {
		InvoiceHeader ent = new InvoiceHeader();
		ent.setId(1);
		assertTrue( ent.isDirty(InvoiceHeaderMetadata.ID) );
	}
	
	@Test
	void testDirtyDomain() {
		InvoiceHeader ent = new InvoiceHeader();
		ent.setDomain(1);
		assertTrue( ent.isDirty(InvoiceHeaderMetadata.DOMAIN) );
	}
	
	@Test
	void testDirtyActivity() {
		InvoiceHeader ent = new InvoiceHeader();
		ent.setActivity( new Activity().setId(1));
		assertTrue( ent.isDirty(InvoiceHeaderMetadata.ACTIVITY) );
	}
	
	@Test
	void testDirtyProject() {
		InvoiceHeader ent = new InvoiceHeader();
		ent.setProject(1);
		assertTrue( ent.isDirty(InvoiceHeaderMetadata.PROJECT) );
	}
	
	@Test
	void testDirtySeries() {
		InvoiceHeader ent = new InvoiceHeader();
		ent.setSeries("1");
		assertTrue( ent.isDirty(InvoiceHeaderMetadata.SERIES) );
	}
	
	@Test
	void testDirtyNumber() {
		InvoiceHeader ent = new InvoiceHeader();
		ent.setNumber(1);
		assertTrue( ent.isDirty(InvoiceHeaderMetadata.NUMBER) );
	}
	
	@Test
	void testDirtyReferenceCode() {
		InvoiceHeader ent = new InvoiceHeader();
		ent.setReferenceCode("1");
		assertTrue( ent.isDirty(InvoiceHeaderMetadata.REFERENCE_CODE) );
	}
	
	@Test
	void testDirtyRegistry() {
		InvoiceHeader ent = new InvoiceHeader();
		ent.setRegistry(1);
		assertTrue( ent.isDirty(InvoiceHeaderMetadata.REGISTRY) );
	}
	
	@Test
	void testDirtyRdocument() {
		InvoiceHeader ent = new InvoiceHeader();
		ent.setRegistryDocument("1");
		assertTrue( ent.isDirty(InvoiceHeaderMetadata.RDOCUMENT) );
	}
	
	@Test
	void testDirtyRdocumentType() {
		InvoiceHeader ent = new InvoiceHeader();
		ent.setRegistryDocumentType(DocumentType.CIF);
		assertTrue( ent.isDirty(InvoiceHeaderMetadata.RDOCUMENT_TYPE) );
	}
	
	@Test
	void testDirtyRdocumentCountry() {
		InvoiceHeader ent = new InvoiceHeader();
		ent.setRegistryDocumentCountry(Country.ES);
		assertTrue( ent.isDirty(InvoiceHeaderMetadata.RDOCUMENT_COUNTRY) );
	}
	
	@Test
	void testDirtyRname() {
		InvoiceHeader ent = new InvoiceHeader();
		ent.setRegistryName("1");
		assertTrue( ent.isDirty(InvoiceHeaderMetadata.RNAME) );
	}
	
	@Test
	void testDirtyIssueDate() {
		InvoiceHeader ent = new InvoiceHeader();
		ent.setIssueDate(new Date());
		assertTrue( ent.isDirty(InvoiceHeaderMetadata.ISSUE_DATE) );
	}
	
	@Test
	void testDirtyTaxDate() {
		InvoiceHeader ent = new InvoiceHeader();
		ent.setTaxDate(new Date());
		assertTrue( ent.isDirty(InvoiceHeaderMetadata.TAX_DATE) );
	}
	
	@Test
	void testDirtySecurityLevel() {
		InvoiceHeader ent = new InvoiceHeader();
		ent.setConfidential(true);
		assertTrue( ent.isDirty(InvoiceHeaderMetadata.SECURITY_LEVEL) );
	}
	
	@Test
	void testDirtyStatus() {
		InvoiceHeader ent = new InvoiceHeader();
		ent.setRecorded(true);
		assertTrue( ent.isDirty(InvoiceHeaderMetadata.STATUS) );
	}
	
	@Test
	void testDirtyType() {
		InvoiceHeader ent = new InvoiceHeader();
		ent.setType(InvoiceType.EXPENSES);
		assertTrue( ent.isDirty(InvoiceHeaderMetadata.TYPE) );
	}
	
	@Test
	void testDirtySurcharge() {
		InvoiceHeader ent = new InvoiceHeader();
		ent.setSurcharge(true);
		assertTrue( ent.isDirty(InvoiceHeaderMetadata.SURCHARGE) );
	}
	
	@Test
	void testDirtyWithholding() {
		InvoiceHeader ent = new InvoiceHeader();
		ent.setWithholding(true);
		assertTrue( ent.isDirty(InvoiceHeaderMetadata.WITHHOLDING) );
	}
	
	@Test
	void testDirtyWithholdingFarmer() {
		InvoiceHeader ent = new InvoiceHeader();
		ent.setWithholdingFarmer(true);
		assertTrue( ent.isDirty(InvoiceHeaderMetadata.WITHHOLDING_FARMER) );
	}
	
	@Test
	void testDirtyVatAccrualPayment() {
		InvoiceHeader ent = new InvoiceHeader();
		ent.setVatAccrualPayment(true);
		assertTrue( ent.isDirty(InvoiceHeaderMetadata.VAT_ACCRUAL_PAYMENT) );
	}
	
	@Test
	void testDirtyComments() {
		InvoiceHeader ent = new InvoiceHeader();
		ent.setComments("1");
		assertTrue( ent.isDirty(InvoiceHeaderMetadata.COMMENTS) );
	}
	
	@Test
	void testDirtyRemarks() {
		InvoiceHeader ent = new InvoiceHeader();
		ent.setRemarks("1");
		assertTrue( ent.isDirty(InvoiceHeaderMetadata.REMARKS) );
	}
	
	@Test
	void testDirtyInvestment() {
		InvoiceHeader ent = new InvoiceHeader();
		ent.setInvestment(true);
		assertTrue( ent.isDirty(InvoiceHeaderMetadata.INVESTMENT) );
	}
	
	@Test
	void testDirtyTransaction() {
		InvoiceHeader ent = new InvoiceHeader();
		ent.setTransaction(InvoiceTransactionType.CAN_CEU_MEL);
		assertTrue( ent.isDirty(InvoiceHeaderMetadata.TRANSACTION) );
	}
	
	@Test
	void testDirtySigned() {
		InvoiceHeader ent = new InvoiceHeader();
		ent.setSigned(true);
		assertTrue( ent.isDirty(InvoiceHeaderMetadata.SIGNED) );
	}
	
	@Test
	void testDirtyScope() {
		InvoiceHeader ent = new InvoiceHeader();
		ent.setScope(1);
		assertTrue( ent.isDirty(InvoiceHeaderMetadata.SCOPE) );
	}
	
	@Test
	void testDirtyService() {
		InvoiceHeader ent = new InvoiceHeader();
		ent.setService(true);
		assertTrue( ent.isDirty(InvoiceHeaderMetadata.SERVICE) );
	}
	
	@Test
	void testDirtyRectificationType() {
		InvoiceHeader ent = new InvoiceHeader();
		ent.setRectificationType(RectificationType.NORMAL_RECTIFIER);
		assertTrue( ent.isDirty(InvoiceHeaderMetadata.RECTIFICATION_TYPE) );
	}
	
	@Test
	void testDirtyRectificationInvoice() {
		InvoiceHeader ent = new InvoiceHeader();
		ent.setRectificationInvoiceId(1);
		assertTrue( ent.isDirty(InvoiceHeaderMetadata.RECTIFICATION_INVOICE) );
	}
	
	@Test
	void testDirtySeller() {
		InvoiceHeader ent = new InvoiceHeader();
		ent.setSeller(new Seller().setId(1));
		assertTrue( ent.isDirty(InvoiceHeaderMetadata.SELLER) );
	}
	
	@Test
	void testDirtyTaxableBase() {
		InvoiceHeader ent = new InvoiceHeader();
		ent.setTaxableBase(1);
		assertTrue( ent.isDirty(InvoiceHeaderMetadata.TAXABLE_BASE) );
	}
	
	@Test
	void testDirtyVatQuota() {
		InvoiceHeader ent = new InvoiceHeader();
		ent.setVatQuota(1);
		assertTrue( ent.isDirty(InvoiceHeaderMetadata.VAT_QUOTA) );
	}
	
	@Test
	void testDirtyRetentionQuota() {
		InvoiceHeader ent = new InvoiceHeader();
		ent.setRetentionQuota(1);
		assertTrue( ent.isDirty(InvoiceHeaderMetadata.RETENTION_QUOTA) );
	}
	
	@Test
	void testDirtyTotal() {
		InvoiceHeader ent = new InvoiceHeader();
		ent.setTotal(1);
		assertTrue( ent.isDirty(InvoiceHeaderMetadata.TOTAL) );
	}
	
	@Test
	void testAccountEquals() {
		InvoiceHeader a1 = new InvoiceHeader().setId(1);
		assertEquals(a1,a1);
		assertNotEquals(a1,null);
		assertNotEquals(null,a1);
		assertNotEquals(a1,new Object());
		assertNotEquals(a1,new Account());
		
		InvoiceHeader a2 = new InvoiceHeader().setId(1);
		assertEquals(a1,a2);
		
		InvoiceHeader a3 = new InvoiceHeader().setId(3);
		assertNotEquals(a1,a3);
	}

	
	@Test
	void testHashcode() {
	    List<InvoiceHeader> objects = new ArrayList<>();
	    for (int i = 0; i < 1000; i++) {
	        objects.add(new InvoiceHeader().setId(i));
	    }
	    Set<Integer> hashCodes = new HashSet<>();
	    for (InvoiceHeader obj : objects) {
	        hashCodes.add(obj.hashCode());
	    }
	    assertEquals(objects.size(), hashCodes.size(), 10);
	}	
	
	@Test
	void testEmptyDocumentNumber() {
		InvoiceHeader ih = new InvoiceHeader();
		assertEquals("??????", ih.getDocumentNumber());
		ih.setSeries("");
		assertEquals("??????", ih.getDocumentNumber()); 
		ih.setSeries(" ");
		assertEquals("??????", ih.getDocumentNumber());
		
		ih.setSeries(null);
		ih.setNumber(0);
		assertEquals("000000", ih.getDocumentNumber());
		ih.setSeries("");
		assertEquals("000000", ih.getDocumentNumber());
		ih.setSeries(" ");
		assertEquals("000000", ih.getDocumentNumber());
		
		ih.setSeries(null);
		ih.setNumber(10);
		assertEquals("000010", ih.getDocumentNumber());
		ih.setSeries("");
		assertEquals("000010", ih.getDocumentNumber());
		ih.setSeries(" ");
		assertEquals("000010", ih.getDocumentNumber()); 
		
	}	
	
	@Test
	void testProformaDocumentNumber() {
		InvoiceHeader ih = new InvoiceHeader();
		ih.setSeries(null);
		ih.setNumber(-10);
		assertEquals("PROFORMA", ih.getDocumentNumber());
		ih.setSeries("");
		assertEquals("PROFORMA", ih.getDocumentNumber());
		ih.setSeries(" ");
		assertEquals("PROFORMA", ih.getDocumentNumber());
	}	
	
	@Test
	void testPurchaseDocumentNumber() {
		InvoiceHeader ih = new InvoiceHeader();
		ih.setType(InvoiceType.PURCHASE);
		ih.setSeries("24");
		ih.setNumber(null);
		assertEquals("R-24/??????"  , ih.getDocumentNumber());
		ih.setNumber(0);
		assertEquals("R-24/000000"  , ih.getDocumentNumber());
		ih.setNumber(10);
		assertEquals("R-24/000010"  , ih.getDocumentNumber());
		ih.setNumber(-10);
		assertEquals("R-24/PROFORMA", ih.getDocumentNumber());
		ih.setNumber(8888888);
		assertEquals("R-24/8888888" , ih.getDocumentNumber());
	}	
	
	@Test
	void testExpenseDocumentNumber() {
		InvoiceHeader ih = new InvoiceHeader();
		ih.setType(InvoiceType.EXPENSES);
		ih.setSeries("24");
		ih.setNumber(null);
		assertEquals("R-24/??????"  , ih.getDocumentNumber());
		ih.setNumber(0);
		assertEquals("R-24/000000"  , ih.getDocumentNumber());
		ih.setNumber(10);
		assertEquals("R-24/000010"  , ih.getDocumentNumber());
		ih.setNumber(-10);
		assertEquals("R-24/PROFORMA", ih.getDocumentNumber());
		ih.setNumber(8888888);
		assertEquals("R-24/8888888" , ih.getDocumentNumber());
	}	
	
	@Test
	void testSalesDocumentNumber() {
		InvoiceHeader ih = new InvoiceHeader();
		ih.setType(InvoiceType.SALES);
		ih.setSeries("24");
		ih.setNumber(null);
		assertEquals("E-24/??????"  , ih.getDocumentNumber());
		ih.setNumber(0);
		assertEquals("E-24/000000"  , ih.getDocumentNumber());
		ih.setNumber(10);
		assertEquals("E-24/000010"  , ih.getDocumentNumber());
		ih.setNumber(-10);
		assertEquals("E-24/PROFORMA", ih.getDocumentNumber());
		ih.setNumber(8888888);
		assertEquals("E-24/8888888" , ih.getDocumentNumber());
	}	
	
	@Test
	void testUndeductibleDocumentNumber() {
		InvoiceHeader ih = new InvoiceHeader();
		ih.setType(InvoiceType.UNDEDUCTIBLE);
		ih.setSeries("24");
		ih.setNumber(null);
		assertEquals("G-24/??????"  , ih.getDocumentNumber());
		ih.setNumber(0);
		assertEquals("G-24/000000"  , ih.getDocumentNumber());
		ih.setNumber(10);
		assertEquals("G-24/000010"  , ih.getDocumentNumber());
		ih.setNumber(-10);
		assertEquals("G-24/PROFORMA", ih.getDocumentNumber());
		ih.setNumber(8888888);
		assertEquals("G-24/8888888" , ih.getDocumentNumber());
		
	}
}
