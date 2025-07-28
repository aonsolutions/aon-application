package net.aonsolutions.aon.verifactu;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Occam;

class VerifactuContextTest {

	@Test
	void testContext() {
		VerifactuContext vc1 = VerifactuMocker.mock( VerifactuContext.class ); 
		 
		VerifactuContext vc2 = new VerifactuContext();
		vc2.setConfig(vc1.getConfig());
		vc2.setCompany(vc1.getCompany());
		vc2.setActivities(vc1.getActivities());
		vc2.setInvoices(vc1.getInvoices());
		vc2.setBlockchain(vc1.getBlockchain());
		vc2.setUser(vc1.getUser());
		vc2.setRequest(vc1.getRequest());
		vc2.setResponse(vc1.getResponse());
		
		VerifactuAsserts.assertClassEquals( vc1, vc2);
		
		Domain d = vc1.getDomain();
		VerifactuAsserts.assertClassEquals( vc1.getCompany().getDomain(), d);
		
		Occam occam = vc2.getOccam();
		assertNotNull( occam );
		int i1 = occam.getDomain();
		int i2 = d.getId();
		assertEquals( i1, i2 );
		assertEquals( occam.getDomainName(), d.getName() );
		assertEquals( occam.getUser(), vc1.getUser() );

	}
	
}
