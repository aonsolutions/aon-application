package net.aonsolutions.occam.api.model;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.watson.util.AonCollectionUtils;

import net.aonsolutions.occam.api.model.type.Administration;
import net.aonsolutions.occam.api.model.type.FiscalStatus;
import net.aonsolutions.occam.api.model.type.FiscalStatus.FiscalStatusVisitor;

class FiscalModelTest {

	@Test
	void testFiscalModel() {
		FiscalModel expected = AonMocker.mock(FiscalModel.class);
		FiscalModel actual = new FiscalModel()
			.setId(expected.getId())
			.setDomain(expected.getDomain())
			.setYear(expected.getYear())
			.setPeriod(expected.getPeriod())
			.setAdministration(expected.getAdministration())
			.setStatus(expected.getStatus())
			.setConfidential(expected.isConfidential())
			.setComplementary(expected.isComplementary())
			.setReplacement(expected.isReplacement())
			.setWithoutActivity(expected.isWithoutActivity())
			.setModel(expected.getModel())
			.setNumber(expected.getNumber())
			.setReplacedNumber(expected.getReplacedNumber())
			.setComments(expected.getComments())
			.setFinance(expected.getFinance().orElse(null))
			.setDocument(expected.getDocument())
			.setSurname(expected.getSurname())
			.setName(expected.getName())
			.setStreetInitial(expected.getStreetInitial())
			.setStreetName(expected.getStreetName())
			.setStreetNumber(expected.getStreetNumber())
			.setStreetStair(expected.getStreetStair())
			.setStreetFloor(expected.getStreetFloor())
			.setStreetDoor(expected.getStreetDoor())
			.setPhone(expected.getPhone())
			.setTown(expected.getTown())
			.setProvince(expected.getProvince())
			.setZip(expected.getZip())
			.setAdmonAeat(expected.getAdmonAeat())
			.setContactPerson(expected.getContactPerson())
			.setContactPhone(expected.getContactPhone())
			.setContactCellular(expected.getContactCellular())
			.setContactEmail(expected.getContactEmail())
			.setCreationUser(expected.getCreationUser())
			.setCreationDate(expected.getCreationDate())
			.setModificationUser(expected.getModificationUser())
			.setModificationDate(expected.getModificationDate())
			.setAccountEntry(expected.getAccountEntry())
			.setResult(expected.getResult())
			.setDeclarationResultType(expected.getDeclarationResultType())
			.setComplementaryDeclarationAvailable(expected.isComplementaryDeclarationAvailable())
			.setDeclarationResult(expected.getDeclarationResult())
			.setDomainName(expected.getDomainName())
			.setGenerateFromYearStart(expected.isGenerateFromYearStart())
			.setGenerateFromYearStartAvailable(expected.isGenerateFromYearStartAvailable())
			.setIban(expected.getIban())
			.setMap(expected.getMap())
			.setMessages(expected.getMessages())
			.setPreviousInvoicesAvailable(expected.isPreviousInvoicesAvailable())
			.setPreviousSalariesAvailable(expected.isPreviousSalariesAvailable())
			.setReplacementDeclarationAvailable(expected.isReplacementDeclarationAvailable())
		;
		AonAsserts.assertClassEquals(expected, actual);
	}
	
	@Test
	void testAdministration() {
		FiscalModel fm = new FiscalModel();
		
		fm.setAdministration(Administration.ALAVA);
		assertTrue(fm.isAraba());
		assertFalse(fm.isBizkaia());
		assertFalse(fm.isGipuzkoa());
		assertFalse(fm.isNavarra());
		assertFalse(fm.isAEAT());

		fm.setAdministration(Administration.BIZKAIA);
		assertFalse(fm.isAraba());
		assertTrue(fm.isBizkaia());
		assertFalse(fm.isGipuzkoa());
		assertFalse(fm.isNavarra());
		assertFalse(fm.isAEAT());

		fm.setAdministration(Administration.GIPUZKOA);
		assertFalse(fm.isAraba());
		assertFalse(fm.isBizkaia());
		assertTrue(fm.isGipuzkoa());
		assertFalse(fm.isNavarra());
		assertFalse(fm.isAEAT());

		fm.setAdministration(Administration.NAVARRA);
		assertFalse(fm.isAraba());
		assertFalse(fm.isBizkaia());
		assertFalse(fm.isGipuzkoa());
		assertTrue(fm.isNavarra());
		assertFalse(fm.isAEAT());

		fm.setAdministration(Administration.COMMON_TERRITORY);
		assertFalse(fm.isAraba());
		assertFalse(fm.isBizkaia());
		assertFalse(fm.isGipuzkoa());
		assertFalse(fm.isNavarra());
		assertTrue(fm.isAEAT());
	}
	
	@Test
	void testFiscalStatus() {
		final FiscalModel fm = new FiscalModel();
		FiscalStatusVisitor<FiscalStatus> visitor = new FiscalStatusVisitor<FiscalStatus>() {
			
			@Override
			public FiscalStatus visitSent() {
				assertTrue( fm.isSent() );
				assertFalse( fm.isPending() );
				assertFalse( fm.isFinished() );
				assertFalse( fm.isCustomerRejected() );
				assertFalse( fm.isCustomerCheck() );
				assertFalse( fm.isCustomerAccepted() );
				assertFalse( fm.isBlocked() );
				return FiscalStatus.SENT;
			}
			
			@Override
			public FiscalStatus visitPending() {
				assertTrue( fm.isPending() );
				assertFalse( fm.isSent() );
				assertFalse( fm.isFinished() );
				assertFalse( fm.isCustomerRejected() );
				assertFalse( fm.isCustomerCheck() );
				assertFalse( fm.isCustomerAccepted() );
				assertFalse( fm.isBlocked() );
				return FiscalStatus.PENDING;
			}
			
			@Override
			public FiscalStatus visitMissing() {
				
				return FiscalStatus.MISSING;
			}
			
			@Override
			public FiscalStatus visitFinished() {
				assertTrue( fm.isFinished() );
				assertFalse( fm.isSent() );
				assertFalse( fm.isPending() );
				assertFalse( fm.isCustomerRejected() );
				assertFalse( fm.isCustomerCheck() );
				assertFalse( fm.isCustomerAccepted() );
				assertFalse( fm.isBlocked() );
				return FiscalStatus.FINISHED;
			}
			
			@Override
			public FiscalStatus visitCustomerRejected() {
				assertTrue( fm.isCustomerRejected() );
				assertFalse( fm.isSent() );
				assertFalse( fm.isPending() );
				assertFalse( fm.isFinished() );
				assertFalse( fm.isCustomerCheck() );
				assertFalse( fm.isCustomerAccepted() );
				assertFalse( fm.isBlocked() );
				return FiscalStatus.CUSTOMER_REJECTED;
			}
			
			@Override
			public FiscalStatus visitCustomerCheck() {
				assertTrue( fm.isCustomerCheck() );
				assertFalse( fm.isSent() );
				assertFalse( fm.isPending() );
				assertFalse( fm.isFinished() );
				assertFalse( fm.isCustomerRejected() );
				assertFalse( fm.isCustomerAccepted() );
				assertFalse( fm.isBlocked() );
				return FiscalStatus.CUSTOMER_CHECK;
			}
			
			@Override
			public FiscalStatus visitCustomerAccepted() {
				assertFalse( fm.isSent() );
				assertFalse( fm.isPending() );
				assertFalse( fm.isFinished() );
				assertFalse( fm.isCustomerRejected() );
				assertFalse( fm.isCustomerCheck() );
				assertFalse( fm.isBlocked() );
				assertTrue( fm.isCustomerAccepted() );
				return FiscalStatus.CUSTOMER_ACCEPTED;
			}
			
			@Override
			public FiscalStatus visitBlocked() {
				assertFalse( fm.isSent() );
				assertFalse( fm.isPending() );
				assertFalse( fm.isFinished() );
				assertFalse( fm.isCustomerRejected() );
				assertFalse( fm.isCustomerCheck() );
				assertFalse( fm.isCustomerAccepted() );
				assertTrue( fm.isBlocked() );
				return FiscalStatus.BLOCKED;
			}
			
			@Override
			public FiscalStatus visitBatched() {
				return FiscalStatus.BATCHED;
			}
		};  		
		AonCollectionUtils.stream(FiscalStatus.values())
			.map(s -> fm.setStatus(s))
			.forEach(f -> assertSame(f.getStatus(), f.getStatus().visit(visitor)));
	}
	
}

