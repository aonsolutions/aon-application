package com.esferalia.aon.occam.test.registry.media;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.type.MediaType;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryMediaDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.AonFaker;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;

public class ValidationSaveInvalidMail extends AbstractOccamTest {

	@Test
	public void test() {
		RegistryMedia media = AonFaker.getRegistryMedia(ctx);
		media.setMedia(MediaType.EMAIL);
		media.setValue( "CORREO NO VALIDO");
		AonCoreException e = assertThrows(AonCoreException.class, () -> RegistryMediaDAO.insert(ctx, media) );
		assertEquals(AonError.INVALID_FORMAT.format( RegistryMediaDAO.MEDIA_EMAIL_LABEL,media.getValue()),e.getMessage());
	}

}
