package net.aonsolutions.aon.verifactu;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.json.JSONObject;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

class VerifactuBlockchainJSONTest {

	@Test
	public void testEmptyJSONObjects() {
		VerifactuBlockchain to = VerifactuMocker.mock(VerifactuBlockchain.class);
		Optional<JSONObject> optJson = VerifactuBlockchainJSON.toJSON(to);
		assertTrue( optJson.isPresent() );
		JSONObject json = optJson.get();
		assertNotNull(json);
		VerifactuAsserts.assertNotEmptyKeys("VerifactuBlockchainJSON", json);
	}
	
	@Test
	public void testNullVerifactuBlockchain() {
		VerifactuBlockchain to = null;
		Optional<JSONObject> optJson = VerifactuBlockchainJSON.toJSON(to );
		assertTrue( optJson.isEmpty() );
	}
	
	@Test
	public void testNullJSON() {
		JSONObject to = null;
		Optional<VerifactuBlockchain> opt = VerifactuBlockchainJSON.fromJSON( to );
		assertTrue( opt.isEmpty() );
	}

	@Test
	public void testEmptyJSON() {
		JSONObject to = new JSONObject();
		Optional<VerifactuBlockchain> opt = VerifactuBlockchainJSON.fromJSON( to );
		assertTrue( opt.isEmpty() );
	}

	@Test
	public void testFromEmptyString() {
		String nullString = null;
		Optional<VerifactuBlockchain> opt1 = VerifactuBlockchainJSON.fromJSON( nullString );
		assertTrue( opt1.isEmpty() );
		
		String blankString = "";
		Optional<VerifactuBlockchain> opt2 = VerifactuBlockchainJSON.fromJSON( blankString );
		assertTrue( opt2.isEmpty() );
		
		String spaceString = " ";
		Optional<VerifactuBlockchain> opt3 = VerifactuBlockchainJSON.fromJSON( spaceString );
		assertTrue( opt3.isEmpty() );
	}
	
	@Test
	public void testFromString() {
		VerifactuBlockchain to = VerifactuMocker.mock(VerifactuBlockchain.class);
		Optional<JSONObject> optJson = VerifactuBlockchainJSON.toJSON( to );
		assertTrue( optJson.isPresent() );
		assertNotNull(optJson);
		JSONObject from = optJson.get(); 
		String jsonJSON = from.toString(); 
		Optional<VerifactuBlockchain> optFrom = VerifactuBlockchainJSON.fromJSON(jsonJSON);
		assertTrue( optJson.isPresent() );
		VerifactuAsserts.assertClassEquals( to, optFrom.get());
	}

	@Test
	public void testFromSupplied() {
		VerifactuBlockchain to = VerifactuMocker.mock(VerifactuBlockchain.class);
		Optional<JSONObject> optJson = VerifactuBlockchainJSON.toJSON( to );
		assertTrue( optJson.isPresent() );
		assertNotNull(optJson);
		VerifactuBlockchain supplied = new VerifactuBlockchain();
		Optional<VerifactuBlockchain> optFrom = VerifactuBlockchainJSON.fromJSON(optJson.get(), () -> supplied);
		assertTrue( optJson.isPresent() );
		assertEquals(supplied, optFrom.get());
		VerifactuAsserts.assertClassEquals( to, supplied);
	}

	@RepeatedTest( 20 )
	void testFromTo() {
		VerifactuBlockchain to = VerifactuMocker.mock(VerifactuBlockchain.class);
		Optional<JSONObject> optJson = VerifactuBlockchainJSON.toJSON(to);
		assertTrue( optJson.isPresent() );
		Optional<VerifactuBlockchain> optFrom = VerifactuBlockchainJSON.fromJSON(optJson.get());
		assertTrue( optFrom.isPresent() );
		VerifactuAsserts.assertClassEquals( to, optFrom.get());
	}
	
}
