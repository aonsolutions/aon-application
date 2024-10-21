package net.aonsolutions.occam.api.model;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;

import org.junit.jupiter.api.Test;
import com.esferalia.aon.watson.util.AonCollectionUtils;

import net.aonsolutions.occam.api.model.metadata.AccountEntryMetadata;
import net.aonsolutions.occam.api.model.metadata.AccountEntryMetadata.AccountEntryMetadataVisitor;
import net.aonsolutions.occam.api.model.type.AccountEntryType;

class AccountEntryTest {

	@Test
	void testAccountEntry() {
		AccountEntry expected = AonMocker.mock(AccountEntry.class);
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
			.setDetails(expected.getDetails())
		;
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
}
