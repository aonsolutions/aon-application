package com.esferalia.aon.occam.test.registry;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.AonFaker;
import com.esferalia.aon.watson.util.AonDocumentUtil;

public class ValidationSaveLegalEntity extends AbstractOccamTest {

	@Test
	public void test() {
		Registry registry = AonFaker.getRegistry( ctx );
		boolean entity = AonDocumentUtil.isEntity( registry.getDocument() );
		registry.setDocumentCountry( Country.ES );
		registry.setLegalPerson( !entity );
		registry = RegistryDAO.insert(ctx, registry);
		String msg = "Doc: " + registry.getDocument() + " must be "  + AonDocumentUtil.isEntity( registry.getDocument() ) +  " --> " + registry.isLegalPerson(); 
		assertEquals(msg,entity, registry.isLegalPerson() );
	}

}
