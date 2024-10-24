package net.aonsolutions.occam.impl.handler;


import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;
import java.util.stream.IntStream;

import org.assertj.core.api.Assertions;
import org.json.JSONObject;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonEnumUtils;

import net.aonsolutions.occam.api.json.CreditorJSON;
import net.aonsolutions.occam.api.model.Account;
import net.aonsolutions.occam.api.model.AonAsserts;
import net.aonsolutions.occam.api.model.AonRandom;
import net.aonsolutions.occam.api.model.Creditor;
import net.aonsolutions.occam.api.model.CreditorFull;
import net.aonsolutions.occam.api.model.Filter;
import net.aonsolutions.occam.api.model.Filter.Property;
import net.aonsolutions.occam.api.model.Scope;
import net.aonsolutions.occam.api.model.type.Country;
import net.aonsolutions.occam.api.model.type.DocumentType;
import net.aonsolutions.occam.api.model.type.InvoiceTransactionType;
import net.aonsolutions.occam.api.model.type.RegistryStatus;
import net.aonsolutions.occam.api.model.type.SecurityLevel;
import net.aonsolutions.occam.impl.AbstractOccamImplTest;

class CreditorHandlerTest extends AbstractOccamImplTest {

	@Test
	void testValidateEmptyScope() {
		ctx.transaction( c -> {
			Optional<Scope> scope = ctx.getDefaultScope( DOMAIN_ID );
			if (scope.isEmpty()) {
				Creditor creditor = AonFaker.getCreditor( ctx, DOMAIN_ID  );
				creditor.setScope(null);
				AonCoreException e = assertThrows(AonCoreException.class, () -> CreditorHandler.save(ctx, creditor) );
				Assertions.assertThat(AonError.EMPTY_SCOPE.getMessage()).isEqualTo(e.getMessage());
			}
		});
	}

	@Test
	void testValidateEmptyTransaction() {
		ctx.transaction( c -> {
			Creditor registry = AonFaker.getCreditor( ctx, DOMAIN_ID  );
			registry.setTransaction(null);
			registry = CreditorHandler.save(ctx, registry);
			Assertions.assertThat(registry.getTransaction()).isSameAs(InvoiceTransactionType.NATIONAL);
		});
	}
	
	@Test
	void testCompleteStatus() {
		ctx.transaction( c -> {
			Creditor registry = AonFaker.getCreditor( ctx, DOMAIN_ID  );
			registry.setStatus(null);
			registry = CreditorHandler.save(ctx, registry);
			Assertions.assertThat(registry.getStatus()).isSameAs(RegistryStatus.ACTIVE);
		});
	}

	@Test
	void testCRUDE() {
		ctx.transaction( c -> {
			Creditor creditor = AonFaker.getCreditor( ctx, DOMAIN_ID );
			creditor = CreditorHandler.save(ctx, creditor);
			Optional<Creditor> inserted = CreditorHandler.get(ctx, DOMAIN_ID, creditor.getId());
			Assertions.assertThat(inserted).isPresent();
			
			AonAsserts.assertClassEquals(creditor, inserted.get());
			
			creditor = CreditorHandler.save(ctx, creditor);
			Optional<Creditor> updated = CreditorHandler.get(ctx, DOMAIN_ID, creditor.getId());
			Assertions.assertThat(updated).isPresent();
			AonAsserts.assertClassEquals(creditor, updated.get());
			
			CreditorHandler.delete(ctx, creditor.getId());
			Optional<Creditor> deleted = CreditorHandler.get(ctx, DOMAIN_ID, creditor.getId());
			Assertions.assertThat(deleted).isEmpty();
		});
	}
		
	
	@RepeatedTest(10)
	void test() {
		ctx.transaction( c -> {
			CreditorFull full = new CreditorFull()
					.setRegistry(AonFaker.getCreditor(ctx, DOMAIN_ID));
				
			IntStream.range(0, AonRandom.integer(0, 10))
				.mapToObj( i -> AonFaker.getRegistryMedia( full.getRegistry() ))
				.forEach(media -> full.addMedia( media ));

			IntStream.range(0, AonRandom.integer(0, 10))
				.mapToObj( i -> AonFaker.getRegistryAddress(ctx, full.getRegistry() ))
				.forEach(address -> full.addAddress( address ));
			
			IntStream.range(0, AonRandom.integer(0, 10))
				.mapToObj( i -> AonFaker.getRegistryBank(ctx, full.getRegistry() ))
				.forEach(bank -> full.addBank( bank ));
				
			CreditorFull inserted = CreditorHandler.save(ctx, full);
			Optional<CreditorFull> afterInserted = CreditorHandler.getFull(ctx, DOMAIN_ID, full.getId());
			Assertions.assertThat(afterInserted).isPresent();
			AonAsserts.assertClassEquals(inserted, afterInserted.get());
				
			inserted.addressStream()
				.forEach( a -> {
					a.setDeleted(AonRandom.gt(90));
					a.setAddress(AonRandom.gt(90)?"MODIFICADO":a.getAddress());
			});
			if (AonRandom.gt(10)) {
				full.addAddress( AonFaker.getRegistryAddress(ctx, full.getRegistry()) );
			}
			
			inserted.mediaStream()
				.forEach( m -> {
					m.setDeleted(AonRandom.gt(90));
					m.setComment(AonRandom.gt(90)?"MODIFICADO":m.getComment());
			});
			if (AonRandom.gt(10)) {
				full.addMedia( AonFaker.getRegistryMedia(full.getRegistry()));
			}
				
			inserted.bankStream()
			.forEach( m -> {
				m.setDeleted(AonRandom.gt(90));
				m.setAlias(AonRandom.gt(90)?"MODIFICADO":m.getAlias());
			});
			if (AonRandom.gt(10)) {
				full.addMedia( AonFaker.getRegistryMedia(full.getRegistry()));
			}
	
			CreditorFull updated = CreditorHandler.save(ctx, full);
			Optional<CreditorFull> afterUpdated = CreditorHandler.getFull(ctx, DOMAIN_ID, full.getId());
			Assertions.assertThat(afterUpdated).isPresent();
			AonAsserts.assertClassEquals(updated, afterUpdated.get());
				
			CreditorHandler.delete(ctx, full.getId());
			Optional<CreditorFull> afterDeleted = CreditorHandler.getFull(ctx, DOMAIN_ID, full.getId());
			Assertions.assertThat(afterDeleted).isEmpty();
		});
	}

	@RepeatedTest(10)
	void testDBJSON() {
		Creditor creditor = CreditorHandler.getRandom(ctx, DOMAIN_ID, null);
		assertNotNull(creditor);
		JSONObject json = CreditorJSON.toJSON(creditor);
		AonAsserts.assertNotEmptyKeys("CreditorJSON", json);
	}
	
	@RepeatedTest(10)
	void testFilter() {
		Creditor c1 = CreditorHandler.getRandom(ctx, DOMAIN_ID, null);
		if (c1 != null) {
			Optional<Creditor> c2 = CreditorHandler.stream(ctx, DOMAIN_ID, f ->
				getProp(f.getIdProperty(), c1.getId() ) 
				.and(getProp(f.getDomainProperty(), c1.getDomain()))
				.and(getProp(f.getDocumentProperty(), c1.getDocument()))
				.and(getProp(f.getDocumentTypeProperty(), DocumentType.value( c1.getDocumentType())))
				.and(getProp(f.getDocumentCountryProperty(), Country.value( c1.getDocumentCountry())))
				.and(getProp(f.getNameProperty(), c1.getName()))
				.and(getProp(f.getAliasProperty(), c1.getAlias()))
				.and(getProp(f.getTypeProperty(), AonEnumUtils.getByte( c1.isLegalPerson())))
				.and(getProp(f.getNationalityProperty(), Country.value( c1.getNationality())))
				.and(getProp(f.getSecurityLevelProperty(), SecurityLevel.value(c1.isConfidential())))
				.and(getProp(f.getDomainProperty(), c1.getDomain()))
				.and(getProp(f.getWithholdingProperty(), AonEnumUtils.getByte( c1.isWithholding())))
				.and(getProp(f.getVatAccrualPaymentProperty(), AonEnumUtils.getByte( c1.isVatAccrualPayment())))
				.and(getProp(f.getTransactionProperty(), InvoiceTransactionType.value(c1.getTransaction())))
				.and(getProp(f.getStatusProperty(), RegistryStatus.value(c1.getStatus())))
				.and(getProp(f.getScopeProperty(), c1.getScope()))
				.and(getProp(f.getAccountProperty(), c1.getAccount().map(Account::getId).orElse(null)))
				.and(getProp(f.getCreationUserProperty(), c1.getCreationUser()))
				.and(getProp(f.getCreationDateProperty(), c1.getCreationDate()))
				.and(getProp(f.getModificationUserProperty(), c1.getModificationUser()))
				.and(getProp(f.getModificationDateProperty(), c1.getModificationDate()))
			)
			.findFirst();
			Assertions.assertThat(c2).isPresent();
			AonAsserts.assertClassEquals(c1, c2.get());
		}
	}

	private <T> Filter getProp(Property<T> property, T value) {
		return value == null?property.isNull():property.eq(value);
	}
	
}
