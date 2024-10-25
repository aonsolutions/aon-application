package net.aonsolutions.occam.api.model;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.watson.util.AonCollectionUtils;

class CompanyFullTest {
	
	@Test
	void testCompanyFull() {
		CompanyFull expected = AonMocker.mock(CompanyFull.class);
		CompanyFull actual = new CompanyFull()
			.setRegistry(expected.getRegistry());
		expected.dirStaffStream().forEach( a -> actual.addDirStaff(a));
		expected.bankStream().forEach( a -> actual.addBank(a));
		expected.addressStream().forEach( a -> actual.addAddress(a));
		expected.mediaStream().forEach( a -> actual.addMedia(a));
		AonAsserts.assertClassEquals(expected, actual);
	}

	@Test
	void testAddAddress() {
		CompanyFull cf = new CompanyFull();
		assertFalse( cf.addressStream().findFirst().isPresent() );
		RegistryAddress raddress = AonMocker.mock(RegistryAddress.class);
		cf.addAddress(raddress);
		assertTrue( cf.addressStream().findFirst().isPresent() );
		assertSame(raddress, AonCollectionUtils.getLast( cf.addressStream()).orElse(null));

		raddress = AonMocker.mock(RegistryAddress.class);
		cf.addAddress(raddress);
		assertSame(raddress, AonCollectionUtils.getLast( cf.addressStream()).orElse(null));
		
	}

	@Test
	void tesMainAddress() {
		CompanyFull cf = new CompanyFull();
		assertFalse( cf.addressStream().findFirst().isPresent() );
		RegistryAddress raddress = AonMocker.mock(RegistryAddress.class);
		raddress.setMain(false);
		cf.addAddress(raddress);
		assertTrue( cf.addressStream().findFirst().isPresent() );
		assertTrue( cf.getMainAddress().isEmpty() );
		
		raddress = AonMocker.mock(RegistryAddress.class);
		raddress.setMain(true);
		cf.addAddress(raddress);
		assertTrue( cf.getMainAddress().isPresent() );
		assertSame(raddress, AonCollectionUtils.getLast( cf.addressStream()).orElse(null));
		
	}

	@Test
	void testAddMedia() {
		CompanyFull cf = new CompanyFull();
		assertFalse( cf.mediaStream().findFirst().isPresent() );
		RegistryMedia media = AonMocker.mock(RegistryMedia.class);
		cf.addMedia(media);
		assertTrue( cf.mediaStream().findFirst().isPresent() );
		assertSame(media, AonCollectionUtils.getLast( cf.mediaStream()).orElse(null));
		
		media = AonMocker.mock(RegistryMedia.class);
		cf.addMedia(media);
		assertSame(media, AonCollectionUtils.getLast( cf.mediaStream()).orElse(null));
	}
	
	@Test
	void testAddBank() {
		CompanyFull cf = new CompanyFull();
		assertFalse( cf.bankStream().findFirst().isPresent() );
		RegistryBank bank = AonMocker.mock(RegistryBank.class);
		cf.addBank(bank);
		assertTrue( cf.bankStream().findFirst().isPresent() );
		assertSame(bank, AonCollectionUtils.getLast( cf.bankStream()).orElse(null));

		bank = AonMocker.mock(RegistryBank.class);
		cf.addBank(bank);
		assertSame(bank, AonCollectionUtils.getLast( cf.bankStream()).orElse(null));
	}
	
	@Test
	void testAddDirStaff() {
		CompanyFull cf = new CompanyFull();
		assertFalse( cf.dirStaffStream().findFirst().isPresent() );
		RegistryDirStaff dirStaff = AonMocker.mock(RegistryDirStaff.class);
		cf.addDirStaff(dirStaff);
		assertTrue( cf.dirStaffStream().findFirst().isPresent() );
		assertSame(dirStaff, AonCollectionUtils.getLast( cf.dirStaffStream()).orElse(null));
		
		dirStaff = AonMocker.mock(RegistryDirStaff.class);
		cf.addDirStaff(dirStaff);
		assertSame(dirStaff, AonCollectionUtils.getLast( cf.dirStaffStream()).orElse(null));
	}
}
