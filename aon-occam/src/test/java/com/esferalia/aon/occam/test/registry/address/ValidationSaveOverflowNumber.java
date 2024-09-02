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

public class ValidationSaveOverflowNumber extends AbstractOccamTest {

	@Test
	public void test() {
		RegistryAddress address = AonFaker.getRegistryAddress(ctx);
		address.setNumber( AonStringUtils.repeat('X', RADDRESS.NUMBER.getDataType().length() + 1) );
		AonCoreException e = assertThrows(AonCoreException.class, () -> RegistryAddressDAO.save(ctx, address) );
		assertEquals(AonError.INVALID_LENGTH.format( RegistryAddressDAO.ADDRESS_NUMBER_LABEL, RADDRESS.NUMBER.getDataType().length() ),e.getMessage());
	}

}
