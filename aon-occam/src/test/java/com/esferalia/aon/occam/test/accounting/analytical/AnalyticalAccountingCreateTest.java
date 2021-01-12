package com.esferalia.aon.occam.test.accounting.analytical;

import java.io.InputStream;
import java.io.InputStreamReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.Test;

import com.esferalia.aon.occam.api.model.accounting.analytical.Analytical;
import com.esferalia.aon.occam.impl.jooq.dao.AnalyticalAccountingDAO;
import com.esferalia.aon.occam.impl.jooq.dao.accounting.analytical.ANALYTICAL;
import com.esferalia.aon.occam.impl.jooq.dao.accounting.analytical.JAXBAnalytical;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.watson.error.AonCoreException;


public class AnalyticalAccountingCreateTest extends AbstractOccamTest {

	private static final String ANALYTICAL_CONFIG = "/com/esferalia/aon/occam/test/accounting/analytical/config.xml";

	@Test
	public void testSave() {
		InputStream in = AnalyticalAccountingCreateTest.class.getResourceAsStream( ANALYTICAL_CONFIG );
		InputStreamReader reader = new InputStreamReader( in );
		Analytical analytical = deserialize(reader);
		AnalyticalAccountingDAO.save(ctx, analytical);
	}

	private Analytical deserialize(InputStreamReader reader) {
		try {
			JAXBContext context = JAXBContext.newInstance(JAXBAnalytical.class);
			Unmarshaller um = context.createUnmarshaller();
			JAXBAnalytical jaxbAnalytical = (JAXBAnalytical) um.unmarshal(reader);
			return ANALYTICAL.getAnalytical(jaxbAnalytical);
		} catch (JAXBException e) {
			e.printStackTrace();
			throw new AonCoreException("Error en serializacion XML",e);
		}				
	}
	
}
