package com.esferalia.aon.occam.api.model.doc;

import static com.esferalia.aon.occam.test.OccamAssertions.assertEquals;
import static com.esferalia.aon.occam.test.OccamAssertions.assertNotNull;
import static com.esferalia.aon.occam.test.OccamAssertions.assertTrue;

import java.util.Optional;

import org.json.JSONObject;
import org.junit.Test;

import com.esferalia.aon.occam.api.json.doc.InvoiceDocJSON;
import com.esferalia.aon.occam.api.model.AonAsserts;
import com.esferalia.aon.occam.api.model.AonMocker;
import com.esferalia.aon.occam.test.Repeat;

public class InvoiceDocJsonTest  {
	
	@Test
	public void testEmptyJSONObjects() {
		InvoiceDoc to = AonMocker.mock(InvoiceDoc.class);
		Optional<JSONObject> optJson = InvoiceDocJSON.to(to);
		assertTrue( optJson.isPresent() );
		JSONObject json = optJson.get();
		assertNotNull(json);
		AonAsserts.assertNotEmptyKeys("InvoiceDocJSON", json);
	}
	
	@Test
	public void testNullInvoiceDoc() {
		InvoiceDoc to = null;
		Optional<JSONObject> optJson = InvoiceDocJSON.to( to );
		assertTrue( optJson.isEmpty() );
	}
	
	@Test
	public void testNullJSON() {
		JSONObject to = null;
		Optional<InvoiceDoc> opt = InvoiceDocJSON.from( to );
		assertTrue( opt.isEmpty() );
	}

	@Test
	public void testEmptyJSON() {
		JSONObject to = new JSONObject();
		Optional<InvoiceDoc> opt = InvoiceDocJSON.from( to );
		assertTrue( opt.isEmpty() );
	}

	@Test
	public void testFromSupplied() {
		InvoiceDoc to = AonMocker.mock(InvoiceDoc.class);
		Optional<JSONObject> optJson = InvoiceDocJSON.to(to);
		assertTrue( optJson.isPresent() );
		assertNotNull(optJson);
		InvoiceDoc supplied = new InvoiceDoc();
		Optional<InvoiceDoc> optFrom = InvoiceDocJSON.from(optJson.get(), () -> supplied);
		assertTrue( optJson.isPresent() );
		assertEquals(supplied, optFrom.get());
		AonAsserts.assertClassEquals( to, supplied);
	}

	@Repeat( 20 )
	void testFromTo() {
		InvoiceDoc to = AonMocker.mock(InvoiceDoc.class);
		Optional<JSONObject> optJson = InvoiceDocJSON.to(to);
		assertTrue( optJson.isPresent() );
		Optional<InvoiceDoc> optFrom = InvoiceDocJSON.from(optJson.get());
		assertTrue( optFrom.isPresent() );
		AonAsserts.assertClassEquals( to, optFrom.get());
	}
}
