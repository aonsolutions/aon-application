package com.esferalia.aon.occam.api.model.doc;

import org.junit.Test;

import com.esferalia.aon.occam.api.model.AonAsserts;
import com.esferalia.aon.occam.api.model.AonMocker;

public class InvoiceDocTest {
	
	@Test
	public void testInvoiceDoc() {
		InvoiceDoc expected = AonMocker.mock(InvoiceDoc.class);
		InvoiceDoc actual = new InvoiceDoc()
			.setId(expected.getId())
			.setDomain(expected.getDomain())
			.setInvoice(expected.getInvoice())
			.setAonId(expected.getAonId())
			.setAonTable(expected.getAonTable())
			.setDriveId(expected.getDriveId())
			.setExternalStorage(expected.getExternalStorage())
			.setS3Bucket(expected.getS3Bucket())
			.setS3Key(expected.getS3Key())
			.setDescription(expected.getDescription())
			.setDate(expected.getDate())
			.setMimeType(expected.getMimeType())
			.setType(expected.getType())
			.setUrl(expected.getUrl())
		;
		AonAsserts.assertClassEquals(expected, actual);
	}
	
}
