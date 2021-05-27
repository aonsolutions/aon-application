package com.esferalia.aon.occam.test.registry.media;

import static com.esferalia.aon.jooq.tables.Rmedia.RMEDIA;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import org.junit.Test;

import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryMediaDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.AonFaker;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonStringUtils;

public class ValidationSaveOverflowValue extends AbstractOccamTest {

	@Test
	public void test() {
		RegistryMedia media = AonFaker.getRegistryMedia(ctx);
		media.setValue( AonStringUtils.repeat('X', RMEDIA.VALUE.getDataType().length() + 1) );
		AonCoreException e = assertThrows(AonCoreException.class, () -> RegistryMediaDAO.insert(ctx, media) );
		assertEquals(AonError.INVALID_LENGTH.format( RegistryMediaDAO.MEDIA_VALUE_LABEL, RMEDIA.VALUE.getDataType().length() ),e.getMessage());
	}

}
