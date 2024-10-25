package net.aonsolutions.occam.api.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.watson.util.AonCollectionUtils;

import net.aonsolutions.occam.api.model.metadata.AccountEntryDetailMetadata;
import net.aonsolutions.occam.api.model.metadata.AccountEntryDetailMetadata.AccountEntryDetailMetadataVisitor;

class AccountEntryDetailTest {

	@Test
	void testAccountEntryDetail() {
		AccountEntryDetail expected = AonMocker.mock(AccountEntryDetail.class);
		AccountEntryDetail actual = new AccountEntryDetail()
			.setId(expected.getId())
			.setDomain(expected.getDomain())
			.setAccountEntry(expected.getAccountEntry())
			.setLine(expected.getLine())
			.setAccount(expected.getAccount().orElse(null))
			.setConcept(expected.getConcept())
			.setBalancingAccount(expected.getBalancingAccount().orElse(null))
			.setDebit(expected.getDebit())
			.setCredit(expected.getCredit())
			.setDocumentNumber(expected.getDocumentNumber())
			.setCreationUser(expected.getCreationUser())
			.setCreationDate(expected.getCreationDate())
			.setModificationUser(expected.getModificationUser())
			.setModificationDate(expected.getModificationDate())
			.setDeleted(expected.isDeleted())
			.setSelected(expected.isSelected())
			.markAsClean()
		;
		AonAsserts.assertClassEquals(expected, actual);
	}

	@Test
	void testAccountEntryDetailMetadataVisitor() {
		AccountEntryDetailMetadataVisitor<AccountEntryDetailMetadata> v = new AccountEntryDetailMetadataVisitor<>() {
			@Override public AccountEntryDetailMetadata visitId() {return AccountEntryDetailMetadata.ID;}
			@Override public AccountEntryDetailMetadata visitDomain() {return AccountEntryDetailMetadata.DOMAIN;}
			@Override public AccountEntryDetailMetadata visitAccountEntry() {return AccountEntryDetailMetadata.ACCOUNT_ENTRY;}
			@Override public AccountEntryDetailMetadata visitLine() {return AccountEntryDetailMetadata.LINE;}
			@Override public AccountEntryDetailMetadata visitAccount() {return AccountEntryDetailMetadata.ACCOUNT;}
			@Override public AccountEntryDetailMetadata visitConcept() {return AccountEntryDetailMetadata.CONCEPT;}
			@Override public AccountEntryDetailMetadata visitBalancingAccount() {return AccountEntryDetailMetadata.BALANCING_ACCOUNT;}
			@Override public AccountEntryDetailMetadata visitDebit() {return AccountEntryDetailMetadata.DEBIT;}
			@Override public AccountEntryDetailMetadata visitCredit() {return AccountEntryDetailMetadata.CREDIT;}
			@Override public AccountEntryDetailMetadata visitDocumentNumber() {return AccountEntryDetailMetadata.DOCUMENT_NUMBER;}
		};
		AonCollectionUtils.stream(AccountEntryDetailMetadata.values())
			.forEach(a -> assertSame(a, a.visit(v)));
	}

	@Test
	void testDirtyId() {
		AccountEntryDetail ent = new AccountEntryDetail();
		ent.setId(1);
		assertTrue( ent.isDirty(AccountEntryDetailMetadata.ID) );
	}
	
	@Test
	void testDirtyDomain() {
		AccountEntryDetail ent = new AccountEntryDetail();
		ent.setDomain(1);
		assertTrue( ent.isDirty(AccountEntryDetailMetadata.DOMAIN) );
	}
	
	@Test
	void testDirtyAccountEntry() {
		AccountEntryDetail ent = new AccountEntryDetail();
		ent.setAccountEntry(1);
		assertTrue( ent.isDirty(AccountEntryDetailMetadata.ACCOUNT_ENTRY) );
	}
	
	@Test
	void testDirtyLine() {
		AccountEntryDetail ent = new AccountEntryDetail();
		ent.setLine(1);
		assertTrue( ent.isDirty(AccountEntryDetailMetadata.LINE) );
	}
	
	@Test
	void testDirtyAccount() {
		AccountEntryDetail ent = new AccountEntryDetail();
		ent.setAccount( new Account().setId(1));
		assertTrue( ent.isDirty(AccountEntryDetailMetadata.ACCOUNT) );
	}
	
	@Test
	void testDirtyConcept() {
		AccountEntryDetail ent = new AccountEntryDetail();
		ent.setConcept("1");
		assertTrue( ent.isDirty(AccountEntryDetailMetadata.CONCEPT) );
	}
	
	@Test
	void testDirtyBalancingAccount() {
		AccountEntryDetail ent = new AccountEntryDetail();
		ent.setBalancingAccount( new Account().setId(1));
		assertTrue( ent.isDirty(AccountEntryDetailMetadata.BALANCING_ACCOUNT) );
	}
	
	@Test
	void testDirtyDebit() {
		AccountEntryDetail ent = new AccountEntryDetail();
		ent.setDebit(1.0);
		assertTrue( ent.isDirty(AccountEntryDetailMetadata.DEBIT) );
	}
	
	@Test
	void testDirtyCredit() {
		AccountEntryDetail ent = new AccountEntryDetail();
		ent.setCredit(1.0);
		assertTrue( ent.isDirty(AccountEntryDetailMetadata.CREDIT) );
	}
	
	@Test
	void testDirtyDocumentNumber() {
		AccountEntryDetail ent = new AccountEntryDetail();
		ent.setDocumentNumber("1");
		assertTrue( ent.isDirty(AccountEntryDetailMetadata.DOCUMENT_NUMBER) );
	}

	@Test
	void testAccountEntryEquals() {
		AccountEntryDetail a1 = new AccountEntryDetail().setId(1);
		assertEquals(a1,a1);
		assertNotEquals(a1,null);
		assertNotEquals(null,a1);
		assertNotEquals(a1,new Object());
		assertNotEquals(a1,new AccountEntry());
		
		AccountEntryDetail a2 = new AccountEntryDetail().setId(1);
		assertEquals(a1,a2);
		
		AccountEntryDetail a3 = new AccountEntryDetail().setId(3);
		assertNotEquals(a1,a3);
	}

	
	@Test
	void testHashcode() {
	    List<AccountEntryDetail> objects = new ArrayList<>();
	    for (int i = 0; i < 1000; i++) {
	        objects.add(new AccountEntryDetail().setId(i));
	    }
	    Set<Integer> hashCodes = new HashSet<>();
	    for (AccountEntryDetail obj : objects) {
	        hashCodes.add(obj.hashCode());
	    }
	    assertEquals(objects.size(), hashCodes.size(), 10);
	}
	
	@Test
	void testClone() {
		AccountEntryDetail expected = AonMocker.mock(AccountEntryDetail.class);
		AccountEntryDetail actual = AccountEntryDetail.clone(expected);
		AonAsserts.assertClassEquals(expected, actual);
	}
	
	@Test
	void testAccount() {
		AccountEntryDetail aed = new AccountEntryDetail();
		assertNull(aed.getAccountCode());
		assertNull(aed.getAccountDescription());
		Account account = new Account().setId(1).setCode("CODE").setDescription("DESCRIPTION"); 
		aed.setAccount( account );
		assertEquals(account.getCode(), aed.getAccountCode());
		assertEquals(account.getDescription(), aed.getAccountDescription());
	}

	@Test
	void testBalancingAccount() {
		AccountEntryDetail aed = new AccountEntryDetail();
		assertNull(aed.getBalancingAccountCode());
		assertNull(aed.getBalancingAccountDescription());
		Account account = new Account().setId(1).setCode("CODE").setDescription("DESCRIPTION"); 
		aed.setBalancingAccount( account );
		assertEquals(account.getCode(), aed.getBalancingAccountCode());
		assertEquals(account.getDescription(), aed.getBalancingAccountDescription());
	}
	
	@Test
	void testSetDebitCredit() {
		AccountEntryDetail aed = new AccountEntryDetail();
		assertEquals(0.0,aed.getDebit());
		assertEquals(0.0,aed.getCredit());
		
		aed.setDebit(1.0);
		assertEquals(1.0,aed.getDebit());
		assertEquals(0.0,aed.getCredit());
		
		aed.setCredit(1.0);
		assertEquals(0.0,aed.getDebit());
		assertEquals(1.0,aed.getCredit());
		
		aed.setDebit(-1.0);
		assertEquals(0.0,aed.getDebit());
		assertEquals(1.0,aed.getCredit());
		
		aed.setCredit(-1.0);
		assertEquals(1.0,aed.getDebit());
		assertEquals(0.0,aed.getCredit());
		
	}
	
	@Test
	void testAddDebitCredit() {
		AccountEntryDetail aed = new AccountEntryDetail();
		assertEquals(0.0,aed.getDebit());
		assertEquals(0.0,aed.getCredit());
		
		aed.setDebit(1.0);
		assertEquals(1.0,aed.getDebit());
		assertEquals(0.0,aed.getCredit());
		
		aed.addDebit(10.0);
		assertEquals(11.0,aed.getDebit());
		assertEquals(0.0,aed.getCredit());
		
		aed.addCredit(6.0);
		assertEquals(5.0,aed.getDebit());
		assertEquals(0.0,aed.getCredit());
		
		aed.addCredit(10.0);
		assertEquals(0.0,aed.getDebit());
		assertEquals(5.0,aed.getCredit());
		
		aed.addDebit(5.0);
		assertEquals(0.0,aed.getDebit());
		assertEquals(0.0,aed.getCredit());
		
		aed.addDebit(10.0);
		assertEquals(10.0,aed.getDebit());
		assertEquals(0.0,aed.getCredit());
		
		aed.addCredit(10.0);
		assertEquals(0.0,aed.getDebit());
		assertEquals(0.0,aed.getCredit());
		
	}	
	
}
