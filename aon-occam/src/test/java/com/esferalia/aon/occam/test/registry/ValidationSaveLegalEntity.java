package com.esferalia.aon.occam.test.registry;

import static com.esferalia.aon.occam.test.OccamAssertions.assertEquals;

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
		registry = RegistryDAO.save(ctx, registry);
		String msg = "Doc: " + registry.getDocument() + " must be "  + AonDocumentUtil.isEntity( registry.getDocument() ) +  " --> " + registry.isLegalPerson(); 
		assertEquals(entity, registry.isLegalPerson(), msg);
	}

}
