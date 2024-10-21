package net.aonsolutions.occam.api.model;

import static org.junit.jupiter.api.Assertions.assertSame;
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
}
