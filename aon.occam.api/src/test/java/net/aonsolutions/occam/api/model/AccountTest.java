package net.aonsolutions.occam.api.model;

import static org.junit.jupiter.api.Assertions.assertSame;

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
	
	
}
