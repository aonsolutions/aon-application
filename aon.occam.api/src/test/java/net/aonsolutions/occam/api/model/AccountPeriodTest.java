package net.aonsolutions.occam.api.model;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;

import org.junit.jupiter.api.Test;
import com.esferalia.aon.watson.util.AonCollectionUtils;

import net.aonsolutions.occam.api.model.metadata.AccountPeriodMetadata;
import net.aonsolutions.occam.api.model.metadata.AccountPeriodMetadata.AccountPeriodMetadataVisitor;
import net.aonsolutions.occam.api.model.type.AccountPeriodStatus;

class AccountPeriodTest {

	@Test
	void testAccountPeriod() {
		AccountPeriod expected = AonMocker.mock(AccountPeriod.class);
		AccountPeriod actual = new AccountPeriod()
			.setId(expected.getId())
			.setDomain(expected.getDomain())
			.setName(expected.getName())
			.setInitiationDate(expected.getInitiationDate())
			.setDeadline(expected.getDeadline())
			.setStatus(expected.getStatus())
			.setCreationUser(expected.getCreationUser())
			.setCreationDate(expected.getCreationDate())
			.setModificationUser(expected.getModificationUser())
			.setModificationDate(expected.getModificationDate())
			.setDefaultPeriod(expected.isDefaultPeriod());
		;
		AonAsserts.assertClassEquals(expected, actual);
	}

	@Test
	void testAccountPeriodMetadataVisitor() {
		AccountPeriodMetadataVisitor<AccountPeriodMetadata> v = new AccountPeriodMetadataVisitor<>() {
			 @Override public AccountPeriodMetadata visitId() {return AccountPeriodMetadata.ID;}
			 @Override public AccountPeriodMetadata visitDomain() {return AccountPeriodMetadata.DOMAIN;}
			 @Override public AccountPeriodMetadata visitName() {return AccountPeriodMetadata.NAME;}
			 @Override public AccountPeriodMetadata visitInitiationDate() {return AccountPeriodMetadata.INITIATION_DATE;}
			 @Override public AccountPeriodMetadata visitDeadline() {return AccountPeriodMetadata.DEADLINE;}
			 @Override public AccountPeriodMetadata visitStatus() {return AccountPeriodMetadata.STATUS;}
		};
		AonCollectionUtils.stream(AccountPeriodMetadata.values())
			.forEach(a -> assertSame(a, a.visit(v)));
	}
	
	@Test
	void testDirtyId() {
		AccountPeriod ent = new AccountPeriod();
		ent.setId(1);
		assertTrue( ent.isDirty(AccountPeriodMetadata.ID) );
	}
	
	@Test
	void testDirtyDomain() {
		AccountPeriod ent = new AccountPeriod();
		ent.setDomain(1);
		assertTrue( ent.isDirty(AccountPeriodMetadata.DOMAIN) );
	}
	
	@Test
	void testDirtyName() {
		AccountPeriod ent = new AccountPeriod();
		ent.setName("1");
		assertTrue( ent.isDirty(AccountPeriodMetadata.NAME) );
	}
	
	@Test
	void testDirtyInitiationDate() {
		AccountPeriod ent = new AccountPeriod();
		ent.setInitiationDate(new Date());
		assertTrue( ent.isDirty(AccountPeriodMetadata.INITIATION_DATE) );
	}
	
	@Test
	void testDirtyDeadline() {
		AccountPeriod ent = new AccountPeriod();
		ent.setDeadline(new Date());
		assertTrue( ent.isDirty(AccountPeriodMetadata.DEADLINE) );
	}
	
	@Test
	void testDirtyStatus() {
		AccountPeriod ent = new AccountPeriod();
		ent.setStatus( AccountPeriodStatus.ACTIVE );
		assertTrue( ent.isDirty(AccountPeriodMetadata.STATUS) );
	}
}
