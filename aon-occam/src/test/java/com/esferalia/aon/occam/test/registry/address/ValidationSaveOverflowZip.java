package com.esferalia.aon.occam.test.registry.address;

import static com.esferalia.aon.jooq.tables.Raddress.RADDRESS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryAddressDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.AonFaker;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonStringUtils;

public class ValidationSaveOverflowZip extends AbstractOccamTest {

	@Test
	public void test() {
		RegistryAddress address = AonFaker.getRegistryAddress(ctx);
		address.setZip( AonStringUtils.repeat('X', RADDRESS.ZIP.getDataType().length() + 1) );
		AonCoreException e = assertThrows(AonCoreException.class, () -> RegistryAddressDAO.save(ctx, address) );
		assertEquals(AonError.INVALID_LENGTH.format( RegistryAddressDAO.ADDRESS_ZIP_LABEL, RADDRESS.ZIP.getDataType().length() ),e.getMessage());
	}

}
