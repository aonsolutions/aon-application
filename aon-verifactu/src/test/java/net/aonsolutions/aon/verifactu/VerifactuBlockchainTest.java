package net.aonsolutions.aon.verifactu;

import org.junit.jupiter.api.Test;

class VerifactuBlockchainTest {

	@Test
	void testBlockchain() {
		VerifactuBlockchain block1 = VerifactuMocker.mock( VerifactuBlockchain.class ); 
		 
		VerifactuBlockchain block2 = new VerifactuBlockchain();
		block2.setDocument( block1.getDocument() );
		block2.setReference( block1.getReference() );
		block2.setDate( block1.getDate() );
		block2.setHuella( block1.getHuella() );
		VerifactuAsserts.assertClassEquals( block1, block2);

	}
	
}
