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

import net.aonsolutions.occam.api.model.metadata.FinanceMetadata;
import net.aonsolutions.occam.api.model.metadata.FinanceMetadata.FinanceMetadataVisitor;
import net.aonsolutions.occam.api.model.type.Country;
import net.aonsolutions.occam.api.model.type.DocumentType;
import net.aonsolutions.occam.api.model.type.FinanceStatus;
import net.aonsolutions.occam.api.model.type.FinanceType;

class FinanceTest {

	@Test
	void testFinance() {
		Finance expected = AonMocker.mock(Finance.class);
		Finance actual = new Finance()
			.setId(expected.getId())
			.setDomain(expected.getDomain())
			.setFinanceType(expected.getFinanceType())
			.setRegistry(expected.getRegistry())
			.setRegistryDocument(expected.getRegistryDocument())
			.setRegistryDocumentType(expected.getRegistryDocumentType())
			.setRegistryDocumentCountry(expected.getRegistryDocumentCountry())
			.setRegistryName(expected.getRegistryName())
			.setAmount(expected.getAmount())
			.setExpenses(expected.getExpenses())
			.setConcept(expected.getConcept())
			.setInvoice(expected.getInvoice().orElse(null))
			.setDueDate(expected.getDueDate())
			.setPayMethod(expected.getPayMethod().orElse(null))
			.setBankAccount(expected.getBankAccount().orElse(null))
			.setBankAlias(expected.getBankAlias())
			.setBic(expected.getBic())
			.setChequeNumber(expected.getChequeNumber())
			.setFinanceStatus(expected.getFinanceStatus())
			.setConfidential(expected.isConfidential())
			.setRemarks(expected.getRemarks())
			.setScope(expected.getScope())
			.setManual(expected.isManual())
			.setAdvance(expected.isAdvance())
			.setPayroll(expected.isPayroll())
			.setPrepayment(expected.isPrepayment())
			.setSourceId(expected.getSourceId())
			.setFinanceGroup(expected.getFinanceGroup())
			.setRegistryAccount(expected.getRegistryAccount().orElse(null))
			.setPaidDate(expected.getPaidDate().orElse(null))
			.setCreationUser(expected.getCreationUser())
			.setCreationDate(expected.getCreationDate())
			.setModificationUser(expected.getModificationUser())
			.setModificationDate(expected.getModificationDate())
		;
		AonAsserts.assertClassEquals(expected, actual);
	}

	@Test
	void testFinanceMetadataVisitor() {
		FinanceMetadataVisitor<FinanceMetadata> v = new FinanceMetadataVisitor<>() {
			 @Override public FinanceMetadata visitId() {return FinanceMetadata.ID;}
			 @Override public FinanceMetadata visitDomain() {return FinanceMetadata.DOMAIN;}
			 @Override public FinanceMetadata visitType() {return FinanceMetadata.TYPE;}
			 @Override public FinanceMetadata visitRegistry() {return FinanceMetadata.REGISTRY;}
			 @Override public FinanceMetadata visitRegistryDocument() {return FinanceMetadata.RDOCUMENT;}
			 @Override public FinanceMetadata visitRegistryDocumentType() {return FinanceMetadata.RDOCUMENT_TYPE;}
			 @Override public FinanceMetadata visitRegistryDocumentCountry() {return FinanceMetadata.RDOCUMENT_COUNTRY;}
			 @Override public FinanceMetadata visitRegistryAccount() {return FinanceMetadata.REGISTRY_ACCOUNT;}
			 @Override public FinanceMetadata visitRname() {return FinanceMetadata.RNAME;}
			 @Override public FinanceMetadata visitAmount() {return FinanceMetadata.AMOUNT;}
			 @Override public FinanceMetadata visitExpenses() {return FinanceMetadata.EXPENSES;}
			 @Override public FinanceMetadata visitConcept() {return FinanceMetadata.CONCEPT;}
			 @Override public FinanceMetadata visitInvoice() {return FinanceMetadata.INVOICE;}
			 @Override public FinanceMetadata visitDueDate() {return FinanceMetadata.DUE_DATE;}
			 @Override public FinanceMetadata visitPayMethod() {return FinanceMetadata.PAY_METHOD;}
			 @Override public FinanceMetadata visitBankAccount() {return FinanceMetadata.BANK_ACCOUNT;}
			 @Override public FinanceMetadata visitBankAlias() {return FinanceMetadata.BANK_ALIAS;}
			 @Override public FinanceMetadata visitBic() {return FinanceMetadata.BIC;}
			 @Override public FinanceMetadata visitChequeNumber() {return FinanceMetadata.CHEQUE_NUMBER;}
			 @Override public FinanceMetadata visitStatus() {return FinanceMetadata.STATUS;}
			 @Override public FinanceMetadata visitSecurityLevel() {return FinanceMetadata.SECURITY_LEVEL;}
			 @Override public FinanceMetadata visitRemarks() {return FinanceMetadata.REMARKS;}
			 @Override public FinanceMetadata visitScope() {return FinanceMetadata.SCOPE;}
			 @Override public FinanceMetadata visitManual() {return FinanceMetadata.MANUAL;}
			 @Override public FinanceMetadata visitAdvance() {return FinanceMetadata.ADVANCE;}
			 @Override public FinanceMetadata visitPayroll() {return FinanceMetadata.PAYROLL;}
			 @Override public FinanceMetadata visitPrepayment() {return FinanceMetadata.PREPAYMENT;}
			 @Override public FinanceMetadata visitSourceId() {return FinanceMetadata.SOURCE_ID;}
			 @Override public FinanceMetadata visitFinanceGroup() {return FinanceMetadata.FINANCE_GROUP;}
			 @Override public FinanceMetadata visitPaidDate() {return FinanceMetadata.PAID_DATE;}
		};
		AonCollectionUtils.stream(FinanceMetadata.values())
			.forEach(a -> assertSame(a, a.visit(v)));
	}
	
	@Test
	void testDirtyId() {
		Finance ent = new Finance();
		ent.setId(1);
		assertTrue( ent.isDirty(FinanceMetadata.ID) );
	}
	
	@Test
	void testDirtyDomain() {
		Finance ent = new Finance();
		ent.setDomain(1);
		assertTrue( ent.isDirty(FinanceMetadata.DOMAIN) );
	}
	
	@Test
	void testDirtyPayment() {
		Finance ent = new Finance();
		ent.setFinanceType( FinanceType.COLLECTION );
		assertTrue( ent.isDirty(FinanceMetadata.TYPE) );
	}
	
	@Test
	void testDirtyRegistry() {
		Finance ent = new Finance();
		ent.setRegistry(1);
		assertTrue( ent.isDirty(FinanceMetadata.REGISTRY) );
	}
	
	@Test
	void testDirtyRdocument() {
		Finance ent = new Finance();
		ent.setRegistryDocument("1");
		assertTrue( ent.isDirty(FinanceMetadata.RDOCUMENT) );
	}
	
	@Test
	void testDirtyRdocumentType() {
		Finance ent = new Finance();
		ent.setRegistryDocumentType(DocumentType.COMMUNITY_CARD);
		assertTrue( ent.isDirty(FinanceMetadata.RDOCUMENT_TYPE) );
	}
	
	@Test
	void testDirtyRdocumentCountry() {
		Finance ent = new Finance();
		ent.setRegistryDocumentCountry(Country.AE);
		assertTrue( ent.isDirty(FinanceMetadata.RDOCUMENT_COUNTRY) );
	}
	
	@Test
	void testDirtyRname() {
		Finance ent = new Finance();
		ent.setRegistryName("1");
		assertTrue( ent.isDirty(FinanceMetadata.RNAME) );
	}
	
	@Test
	void testDirtyAmount() {
		Finance ent = new Finance();
		ent.setAmount(1);
		assertTrue( ent.isDirty(FinanceMetadata.AMOUNT) );
	}
	
	@Test
	void testDirtyExpenses() {
		Finance ent = new Finance();
		ent.setExpenses(1);
		assertTrue( ent.isDirty(FinanceMetadata.EXPENSES) );
	}
	
	@Test
	void testDirtyConcept() {
		Finance ent = new Finance();
		ent.setConcept("1");
		assertTrue( ent.isDirty(FinanceMetadata.CONCEPT) );
	}
	
	@Test
	void testDirtyInvoice() {
		Finance ent = new Finance();
		ent.setInvoice( new InvoiceHeader().setId(1));
		assertTrue( ent.isDirty(FinanceMetadata.INVOICE) );
	}
	
	@Test
	void testDirtyDueDate() {
		Finance ent = new Finance();
		ent.setDueDate(new Date());
		assertTrue( ent.isDirty(FinanceMetadata.DUE_DATE) );
	}
	
	@Test
	void testDirtyPayMethod() {
		Finance ent = new Finance();
		ent.setPayMethod( new PayMethod().setId(1));
		assertTrue( ent.isDirty(FinanceMetadata.PAY_METHOD) );
	}
	
	@Test
	void testDirtyBankAccount() {
		Finance ent = new Finance();
		ent.setBankAccount(new BankAccount("DDDDD"));
		assertTrue( ent.isDirty(FinanceMetadata.BANK_ACCOUNT) );
	}
	
	@Test
	void testDirtyBankAlias() {
		Finance ent = new Finance();
		ent.setBankAlias("1");
		assertTrue( ent.isDirty(FinanceMetadata.BANK_ALIAS) );
	}
	
	@Test
	void testDirtyBic() {
		Finance ent = new Finance();
		ent.setBic("1");
		assertTrue( ent.isDirty(FinanceMetadata.BIC) );
	}
	
	@Test
	void testDirtyChequeNumber() {
		Finance ent = new Finance();
		ent.setChequeNumber("1");
		assertTrue( ent.isDirty(FinanceMetadata.CHEQUE_NUMBER) );
	}
	
	@Test
	void testDirtyStatus() {
		Finance ent = new Finance();
		ent.setFinanceStatus(FinanceStatus.BATCHED);
		assertTrue( ent.isDirty(FinanceMetadata.STATUS) );
	}
	
	@Test
	void testDirtySecurityLevel() {
		Finance ent = new Finance();
		ent.setConfidential( true );
		assertTrue( ent.isDirty(FinanceMetadata.SECURITY_LEVEL) );
	}
	
	@Test
	void testDirtyRemarks() {
		Finance ent = new Finance();
		ent.setRemarks("1");
		assertTrue( ent.isDirty(FinanceMetadata.REMARKS) );
	}
	
	@Test
	void testDirtyScope() {
		Finance ent = new Finance();
		ent.setScope(1);
		assertTrue( ent.isDirty(FinanceMetadata.SCOPE) );
	}
	
	@Test
	void testDirtyManual() {
		Finance ent = new Finance();
		ent.setManual(true);
		assertTrue( ent.isDirty(FinanceMetadata.MANUAL) );
	}
	
	@Test
	void testDirtyAdvance() {
		Finance ent = new Finance();
		ent.setAdvance(true);
		assertTrue( ent.isDirty(FinanceMetadata.ADVANCE) );
	}
	
	@Test
	void testDirtyPayroll() {
		Finance ent = new Finance();
		ent.setPayroll(true);
		assertTrue( ent.isDirty(FinanceMetadata.PAYROLL) );
	}
	
	@Test
	void testDirtyPrepayment() {
		Finance ent = new Finance();
		ent.setPrepayment(true);
		assertTrue( ent.isDirty(FinanceMetadata.PREPAYMENT) );
	}
	
	@Test
	void testDirtySourceId() {
		Finance ent = new Finance();
		ent.setSourceId(1);
		assertTrue( ent.isDirty(FinanceMetadata.SOURCE_ID) );
	}
	
	@Test
	void testDirtyFinanceGroup() {
		Finance ent = new Finance();
		ent.setFinanceGroup(1);
		assertTrue( ent.isDirty(FinanceMetadata.FINANCE_GROUP) );
	}
	
	@Test
	void testDirtyRegistryAccount() {
		Finance ent = new Finance();
		ent.setRegistryAccount( new Account().setId(1));
		assertTrue( ent.isDirty(FinanceMetadata.REGISTRY_ACCOUNT) );
	}

	@Test
	void testDirtyPaidDate() {
		Finance ent = new Finance();
		ent.setPaidDate( new Date());
		assertTrue( ent.isDirty(FinanceMetadata.PAID_DATE) );
	}

	@Test
	void testAccountEquals() {
		Finance a1 = new Finance().setId(1);
		assertEquals(a1,a1);
		assertNotEquals(a1,null);
		assertNotEquals(null,a1);
		assertNotEquals(a1,new Object());
		assertNotEquals(a1,new Finance());
		
		Finance a2 = new Finance().setId(1);
		assertEquals(a1,a2);
		
		Finance a3 = new Finance().setId(3);
		assertNotEquals(a1,a3);
	}

	
	@Test
	void testHashcode() {
	    List<Finance> objects = new ArrayList<>();
	    for (int i = 0; i < 1000; i++) {
	        objects.add(new Finance().setId(i));
	    }
	    Set<Integer> hashCodes = new HashSet<>();
	    for (Finance obj : objects) {
	        hashCodes.add(obj.hashCode());
	    }
	    assertEquals(objects.size(), hashCodes.size(), 10);
	}	
}
