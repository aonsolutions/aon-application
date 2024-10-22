package net.aonsolutions.occam.api.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.watson.util.AonCollectionUtils;

import net.aonsolutions.occam.api.model.metadata.AccountMetadata;
import net.aonsolutions.occam.api.model.metadata.AccountMetadata.AccountMetadataVisitor;

class AccountTest {
	
	@Test
	void testAccount() {
		Account expected = AonMocker.mock(Account.class);
		Account actual = new Account()
			.setId(expected.getId())
			.setDomain(expected.getDomain())
			.setCode(expected.getCode())
			.setDescription(expected.getDescription())
			.setAlias(expected.getAlias())
			.setEntryEnabled(expected.isEntryEnabled())
			.setLevel(expected.getLevel())
			.setActive(expected.isActive())
			.setCostCenter(expected.getCostCenter())
			.setSelected(expected.isSelected())
			.setDeleted(expected.isDeleted())
		;
		AonAsserts.assertClassEquals(expected, actual);
	}

	@Test
	void testAccountMetadataVisitor() {
		AccountMetadataVisitor< AccountMetadata> v = new AccountMetadataVisitor<>() {
			@Override public AccountMetadata visitId() {return AccountMetadata.ID;}
			@Override public AccountMetadata visitDomain() {return AccountMetadata.DOMAIN;}
			@Override public AccountMetadata visitCode() {return AccountMetadata.CODE;}
			@Override public AccountMetadata visitDescription() {return AccountMetadata.DESCRIPTION;}
			@Override public AccountMetadata visitAlias() {return AccountMetadata.ALIAS;}
			@Override public AccountMetadata visitEntryEnabled() {return AccountMetadata.ENTRY_ENABLED;}
			@Override public AccountMetadata visitLevel() {return AccountMetadata.LEVEL;}
			@Override public AccountMetadata visitActive() {return AccountMetadata.ACTIVE;}
			@Override public AccountMetadata visitCostCenter() {return AccountMetadata.COST_CENTER;}
		};
		AonCollectionUtils.stream(AccountMetadata.values())
			.forEach(a -> assertSame(a, a.visit(v)));
	}
	
	@Test
	void testAccountEquals() {
		Account a1 = new Account().setId(1);
		assertEquals(a1,a1);
		assertNotEquals(a1,null);
		assertNotEquals(null,a1);
		assertNotEquals(a1,new Object());
		assertNotEquals(a1,new Account());
		
		Account a2 = new Account().setId(1);
		assertEquals(a1,a2);
		
		Account a3 = new Account().setId(3);
		assertNotEquals(a1,a3);
	}

	
	@Test
	void testHashcode() {
	    List<Account> objects = new ArrayList<>();
	    for (int i = 0; i < 1000; i++) {
	        objects.add(new Account().setId(i));
	    }
	    Set<Integer> hashCodes = new HashSet<>();
	    for (Account obj : objects) {
	        hashCodes.add(obj.hashCode());
	    }
	    assertEquals(objects.size(), hashCodes.size(), 10);
	}	
	
}
