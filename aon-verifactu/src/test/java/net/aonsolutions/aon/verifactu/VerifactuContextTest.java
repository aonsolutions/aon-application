package net.aonsolutions.aon.verifactu;

import org.junit.jupiter.api.Test;

class VerifactuContextTest {

	@Test
	void testContext() {
		
		VerifactuContext vc1 = VerifactuMocker.mock( VerifactuContext.class );
		
		VerifactuContext vc2 = new VerifactuContext( vc1.getInvoiceCommunicatorContext(), vc1.getEnablerData());
		
		VerifactuAsserts.assertClassEquals( vc1.getInvoiceCommunicatorContext(), vc2.getInvoiceCommunicatorContext());
		VerifactuAsserts.assertClassEquals( vc1.getEnablerData(), vc2.getEnablerData());
		
		vc2.setActivities(vc1.getActivities());
		vc2.setBlockchain(vc1.getBlockchain());
		vc2.setRequest(vc1.getRequest());
		vc2.setQueryRequest(vc1.getQueryRequest());
		vc2.setRequestBytes(vc1.getRequestBytes());
		vc2.setResponse(vc1.getResponse());
		vc2.setOperation(vc1.getOperation());
		
		VerifactuAsserts.assertClassEquals( vc1, vc2);
		
	}
	
}
