package com.esferalia.aon.occam.test.rawdoc;


import java.io.IOException;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Rawdoc;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.api.model.type.RawdocNature;
import com.esferalia.aon.occam.api.model.type.RawdocStatus;
import com.esferalia.aon.occam.api.model.type.RawdocType;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.AonRandom;
import com.esferalia.aon.watson.error.AonCoreException;


public class RawdocFaker extends AbstractOccamTest {
	
	private static String[] INVOICES = new String[] {
		 "/com/esferalia/aon/occam/test/faker/INVOICE-01.pdf"
		,"/com/esferalia/aon/occam/test/faker/INVOICE-02.pdf"
		,"/com/esferalia/aon/occam/test/faker/INVOICE-03.pdf"
		,"/com/esferalia/aon/occam/test/faker/INVOICE-04.pdf"
		,"/com/esferalia/aon/occam/test/faker/INVOICE-05.pdf"
		,"/com/esferalia/aon/occam/test/faker/INVOICE-06.pdf"
		,"/com/esferalia/aon/occam/test/faker/INVOICE-07.pdf"
		,"/com/esferalia/aon/occam/test/faker/INVOICE-08.pdf"
		,"/com/esferalia/aon/occam/test/faker/INVOICE-09.pdf"
		,"/com/esferalia/aon/occam/test/faker/INVOICE-10.pdf"
		,"/com/esferalia/aon/occam/test/faker/INVOICE-11.pdf"
		,"/com/esferalia/aon/occam/test/faker/INVOICE-12.pdf"
		,"/com/esferalia/aon/occam/test/faker/INVOICE-13.pdf"};
	
	static Rawdoc getRawdoc(AONContext ctx) {
		try {
			Rawdoc rawdoc = new Rawdoc();
			rawdoc.setDomain( DOMAIN_ID );
			rawdoc.setNature( RawdocNature.INVOICE );
			rawdoc.setType( RawdocType.INPUT);
			rawdoc.setStatus( RawdocStatus.INBOX );
			rawdoc.setJson( null );
			rawdoc.setTediInvoice( null );
			rawdoc.setInvoice( null );
			rawdoc.setLog( null );
			rawdoc.setMimeType( MimeType.PDF );
			rawdoc.setData( getRandomInvoice() );
			rawdoc.setS3Key( null );
			return rawdoc;
		} catch (IOException e) {
			throw new AonCoreException(e);
		}
	}
	
	static Rawdoc getS3Rawdoc(AONContext ctx) {
		Rawdoc rawdoc = new Rawdoc();
		rawdoc.setDomain( DOMAIN_ID );
		rawdoc.setNature( RawdocNature.INVOICE );
		rawdoc.setType( RawdocType.INPUT);
		rawdoc.setStatus( RawdocStatus.INBOX );
		rawdoc.setJson( null );
		rawdoc.setTediInvoice( null );
		rawdoc.setInvoice( null );
		rawdoc.setLog( null );
		rawdoc.setMimeType( MimeType.PDF );
		rawdoc.setS3Key( INVOICES[AonRandom.number(0, INVOICES.length - 1)] );
		return rawdoc;
	}

	private static byte[] getRandomInvoice() throws IOException {
		int i = AonRandom.number(0, INVOICES.length - 1);
		return RawdocFaker.class.getResourceAsStream( INVOICES[i] ).readAllBytes();
	}
}
