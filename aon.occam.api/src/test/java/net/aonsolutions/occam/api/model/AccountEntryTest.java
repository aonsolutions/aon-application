package net.aonsolutions.occam.api.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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

import net.aonsolutions.occam.api.model.metadata.AccountEntryMetadata;
import net.aonsolutions.occam.api.model.metadata.AccountEntryMetadata.AccountEntryMetadataVisitor;
import net.aonsolutions.occam.api.model.type.AccountEntryType;

class AccountEntryTest {

	@Test
	void testAccountEntry() {
		AccountEntry expected = AonMocker.mock(AccountEntry.class);
		if (AonRandom.gt( 10) ) {
			AonCollectionUtils.stream( AonRandom.integer(0, 20) )
				.forEach( i -> expected.addDetail( AonMocker.mock(AccountEntryDetail.class)) );
		}
		AccountEntry actual = new AccountEntry()
			.setId(expected.getId())
			.setDomain(expected.getDomain())
			.setPeriod(expected.getPeriod().orElse(null))
			.setActivity(expected.getActivity().orElse(null))
			.setEntryDate(expected.getEntryDate())
			.setEntryType(expected.getEntryType())
			.setJournal(expected.getJournal())
			.setConfidential(expected.isConfidential())
			.setComments(expected.getComments())
			.setCreationUser(expected.getCreationUser())
			.setCreationDate(expected.getCreationDate())
			.setModificationUser(expected.getModificationUser())
			.setModificationDate(expected.getModificationDate())
			.setDeleted(expected.isDeleted())
			.setSelected(expected.isSelected())
		;
		expected.detailStream().forEach( actual::addDetail);
		expected.dirtySetStream().forEach(actual::markAsDirty); 
		AonAsserts.assertClassEquals(expected, actual);
	}

	@Test
	void testAccountEntryMetadataVisitor() {
		AccountEntryMetadataVisitor<AccountEntryMetadata> v = new AccountEntryMetadataVisitor<>() {
			 @Override public AccountEntryMetadata visitId() {return AccountEntryMetadata.ID;}
			 @Override public AccountEntryMetadata visitDomain() {return AccountEntryMetadata.DOMAIN;}
			 @Override public AccountEntryMetadata visitAccountPeriod() {return AccountEntryMetadata.ACCOUNT_PERIOD;}
			 @Override public AccountEntryMetadata visitActivity() {return AccountEntryMetadata.ACTIVITY;}
			 @Override public AccountEntryMetadata visitEntryDate() {return AccountEntryMetadata.ENTRY_DATE;}
			 @Override public AccountEntryMetadata visitEntryType() {return AccountEntryMetadata.ENTRY_TYPE;}
			 @Override public AccountEntryMetadata visitJournal() {return AccountEntryMetadata.JOURNAL;}
			 @Override public AccountEntryMetadata visitSecurityLevel() {return AccountEntryMetadata.SECURITY_LEVEL;}
			 @Override public AccountEntryMetadata visitComments() {return AccountEntryMetadata.COMMENTS;}
			 @Override public AccountEntryMetadata visitDetails() {return AccountEntryMetadata.DETAILS;}
		};
		AonCollectionUtils.stream(AccountEntryMetadata.values())
			.forEach(a -> assertSame(a, a.visit(v)));
	}
	
	@Test
	void testDirtyId() {
		AccountEntry ent = new AccountEntry();
		ent.setId(1);
		assertTrue( ent.isDirty() );
		assertTrue( ent.isDirty(AccountEntryMetadata.ID) );
	}
	
	@Test
	void testDirtyDomain() {
		AccountEntry ent = new AccountEntry();
		ent.setDomain(1);
		assertTrue( ent.isDirty() );
		assertTrue( ent.isDirty(AccountEntryMetadata.DOMAIN) );
	}
	
	@Test
	void testDirtyAccountPeriod() {
		AccountEntry ent = new AccountEntry();
		ent.setPeriod( new AccountPeriod().setId(10));
		assertTrue( ent.isDirty() );
		assertTrue( ent.isDirty(AccountEntryMetadata.ACCOUNT_PERIOD) );
		
		ent.markAsClean();
		ent.setPeriod( new AccountPeriod().setId(10));
		assertFalse( ent.isDirty() );
		assertFalse( ent.isDirty(AccountEntryMetadata.ACCOUNT_PERIOD) );
		
		ent.markAsClean();
		ent.setPeriod( new AccountPeriod().setId(100));
		assertTrue( ent.isDirty() );
		assertTrue( ent.isDirty(AccountEntryMetadata.ACCOUNT_PERIOD) );
	}
	
	@Test
	void testDirtyActivity() {
		AccountEntry ent = new AccountEntry();
		ent.setActivity(new Activity().setId(10));
		assertTrue( ent.isDirty() );
		assertTrue( ent.isDirty(AccountEntryMetadata.ACTIVITY) );
		
		ent.markAsClean();
		ent.setActivity(new Activity().setId(10));
		assertFalse( ent.isDirty() );
		assertFalse( ent.isDirty(AccountEntryMetadata.ACTIVITY) );
		
		ent.markAsClean();
		ent.setActivity(new Activity().setId(100));
		assertTrue( ent.isDirty() );
		assertTrue( ent.isDirty(AccountEntryMetadata.ACTIVITY) );
	}
	
	@Test
	void testDirtyEntryDate() {
		AccountEntry ent = new AccountEntry();
		ent.setEntryDate(new Date());
		assertTrue( ent.isDirty() );
		assertTrue( ent.isDirty(AccountEntryMetadata.ENTRY_DATE) );
	}
	
	@Test
	void testDirtyEntryType() {
		AccountEntry ent = new AccountEntry();
		ent.setEntryType( AccountEntryType.OPENING );
		assertTrue( ent.isDirty() );
		assertTrue( ent.isDirty(AccountEntryMetadata.ENTRY_TYPE) );
	}
	
	@Test
	void testDirtyJournal() {
		AccountEntry ent = new AccountEntry();
		ent.setJournal(1);
		assertTrue( ent.isDirty() );
		assertTrue( ent.isDirty(AccountEntryMetadata.JOURNAL) );
	}
	
	@Test
	void testDirtyConfidential() {
		AccountEntry ent = new AccountEntry();
		ent.setConfidential( true );
		assertTrue( ent.isDirty() );
		assertTrue( ent.isDirty(AccountEntryMetadata.SECURITY_LEVEL) );
	}
	
	@Test
	void testDirtyComments() {
		AccountEntry ent = new AccountEntry();
		ent.setComments("1");
		assertTrue( ent.isDirty() );
		assertTrue( ent.isDirty(AccountEntryMetadata.COMMENTS) );
	}

	@Test
	void testDirtyDetails() {
		AccountEntry ent = new AccountEntry();
		ent.addDetail(new AccountEntryDetail().setId(1));
		assertTrue( ent.isDirty() );
		assertTrue( ent.isDirty(AccountEntryMetadata.DETAILS) );
	}

	@Test
	void testAccountEntryEquals() {
		AccountEntry a1 = new AccountEntry().setId(1);
		assertEquals(a1,a1);
		assertNotEquals(a1,null);
		assertNotEquals(null,a1);
		assertNotEquals(a1,new Object());
		assertNotEquals(a1,new AccountEntry());
		
		AccountEntry a2 = new AccountEntry().setId(1);
		assertEquals(a1,a2);
		
		AccountEntry a3 = new AccountEntry().setId(3);
		assertNotEquals(a1,a3);
	}

	
	@Test
	void testHashcode() {
	    List<AccountEntry> objects = new ArrayList<>();
	    for (int i = 0; i < 1000; i++) {
	        objects.add(new AccountEntry().setId(i));
	    }
	    Set<Integer> hashCodes = new HashSet<>();
	    for (AccountEntry obj : objects) {
	        hashCodes.add(obj.hashCode());
	    }
	    assertEquals(objects.size(), hashCodes.size(), 10);
	}
	
	@Test
	void testClone() {
		AccountEntry expected = AonMocker.mock(AccountEntry.class);
		AccountEntry actual = AccountEntry.clone(expected);
		AonAsserts.assertClassEquals(expected, actual);
	}
	
	@Test
	void testDetailsSize() {
		AccountEntry expected = AonMocker.mock(AccountEntry.class);
		assertThat(expected.getDetailsSize()).isZero();
		
		AccountEntryDetail aed1 = AonMocker.mock(AccountEntryDetail.class).setDeleted(false);
		AccountEntryDetail aed2 = AonMocker.mock(AccountEntryDetail.class).setDeleted(false);
		AccountEntryDetail aed3 = AonMocker.mock(AccountEntryDetail.class).setDeleted(false);
		expected.addDetail( aed1 );
		expected.addDetail( aed2 );
		expected.addDetail( aed3 );
		assertEquals(3, expected.getDetailsSize());
		
		aed2.setDeleted( true );
		assertEquals(2, expected.getDetailsSize());
		
	}
	
	
	@Test
	void testLastDetail() {
		AccountEntry expected = AonMocker.mock(AccountEntry.class);
		AccountEntryDetail aed1 = AonMocker.mock(AccountEntryDetail.class).setDeleted(false);
		AccountEntryDetail aed2 = AonMocker.mock(AccountEntryDetail.class).setDeleted(false);
		AccountEntryDetail aed3 = AonMocker.mock(AccountEntryDetail.class).setDeleted(false);
		expected.addDetail( aed1 );
		expected.addDetail( aed2 );
		expected.addDetail( aed3 );
		
		assertTrue( expected.getLastDetail().isPresent() );
		assertSame(aed3, expected.getLastDetail().get());
		
		aed3.setDeleted( true );
		assertTrue( expected.getLastDetail().isPresent() );
		assertSame(aed2, expected.getLastDetail().get());
		
		aed2.setDeleted( true );
		aed1.setDeleted( true );
		assertFalse( expected.getLastDetail().isPresent() );
	}
	
}
