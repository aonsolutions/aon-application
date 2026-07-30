package com.esferalia.aon.occam.api.model.doc;

import static com.esferalia.aon.jooq.tables.InvoiceDoc.INVOICE_DOC;
import static com.esferalia.aon.occam.test.OccamAssertions.assertEquals;
import static com.esferalia.aon.occam.test.OccamAssertions.assertNotNull;
import static com.esferalia.aon.occam.test.OccamAssertions.assertNull;
import static com.esferalia.aon.occam.test.OccamAssertions.assertThrows;
import static com.esferalia.aon.occam.test.OccamAssertions.assertTrue;

import java.io.IOException;
import java.util.Date;
import java.util.Optional;

import org.junit.Test;

import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.InvoiceAttachmentType;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.impl.jooq.dao.AttachmentDAO;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceDAO;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceDocDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.AonRandom;
import com.esferalia.aon.occam.test.faker.InvoiceFaker;
import com.esferalia.aon.occam.test.faker.InvoiceFaker.InvoiceFakerParams;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class InvoiceDocDAOTest extends AbstractOccamTest {
	private static final String S3BUCKET ="aon-junit-tests";
	
	private static final String[] INVOICES = new String[] {
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
	
	@Test
	public void testEmptyDomain() {
		InvoiceDoc doc = new InvoiceDoc();
		AonCoreException e = assertThrows(AonCoreException.class, () -> InvoiceDocDAO.save(ctx, doc));
		assertEquals( AonError.EMPTY_DOMAIN.getMessage(), e.getMessage());
	}

	@Test
	public void testEmptyMimeType() {
		InvoiceDoc doc = new InvoiceDoc()
			.setDomain(DOMAIN_ID);
		AonCoreException e = assertThrows(AonCoreException.class, () -> InvoiceDocDAO.save(ctx, doc));
		assertEquals( AonError.EMPTY_DATA.format( IJsonNames.MIME_TYPE ), e.getMessage());
	}
	
	@Test
	public void testEmptyType() {
		InvoiceDoc doc = new InvoiceDoc()
			.setDomain(DOMAIN_ID)
			.setMimeType(MimeType.PDF);
		AonCoreException e = assertThrows(AonCoreException.class, () -> InvoiceDocDAO.save(ctx, doc));
		assertEquals( AonError.EMPTY_DATA.format( IJsonNames.TYPE ), e.getMessage());
	}
		
	@Test
	public void testEmptyInvoice() {
		InvoiceDoc doc = new InvoiceDoc()
			.setDomain(DOMAIN_ID)
			.setMimeType(MimeType.PDF)
			.setType( InvoiceAttachmentType.INVOICE );
		AonCoreException e = assertThrows(AonCoreException.class, () -> InvoiceDocDAO.save(ctx, doc));
		assertEquals( AonError.EMPTY_DATA.format( IJsonNames.INVOICE ), e.getMessage());
	}

	@Test
	public void testEmptyExternalStorage() {
		InvoiceDoc doc = new InvoiceDoc()
			.setDomain(DOMAIN_ID)
			.setMimeType(MimeType.PDF)
			.setType( InvoiceAttachmentType.INVOICE )
			.setInvoice(1);
		AonCoreException e = assertThrows(AonCoreException.class, () -> InvoiceDocDAO.save(ctx, doc));
		assertEquals( AonError.EMPTY_DATA.format( IJsonNames.EXTERNAL_STORAGE ), e.getMessage());
	}

	@Test
	public void testValidExternalStorageAon() {
		InvoiceDoc doc = new InvoiceDoc()
			.setDomain(DOMAIN_ID)
			.setMimeType(MimeType.PDF)
			.setType( InvoiceAttachmentType.INVOICE )
			.setInvoice(1)
			.setExternalStorage( ExternalStorage.AON )
			;
		AonCoreException e = assertThrows(AonCoreException.class, () -> InvoiceDocDAO.save(ctx, doc));
		assertEquals( AonError.EMPTY_DATA.format( IJsonNames.AON_ID ), e.getMessage());
	}

	@Test
	public void testValidExternalStorageDrive() {
		InvoiceDoc doc = new InvoiceDoc()
			.setDomain(DOMAIN_ID)
			.setMimeType(MimeType.PDF)
			.setType( InvoiceAttachmentType.INVOICE )
			.setInvoice(1)
			.setExternalStorage( ExternalStorage.DRIVE )
			;
		AonCoreException e = assertThrows(AonCoreException.class, () -> InvoiceDocDAO.save(ctx, doc));
		assertEquals( AonError.EMPTY_DATA.format( IJsonNames.DRIVE_ID ), e.getMessage());
	}

	@Test
	public void testValidExternalStorageAWSS3Bucket() {
		InvoiceDoc doc = new InvoiceDoc()
			.setDomain(DOMAIN_ID)
			.setMimeType(MimeType.PDF)
			.setType( InvoiceAttachmentType.INVOICE )
			.setInvoice(1)
			.setExternalStorage( ExternalStorage.AWS )
			;
		AonCoreException e = assertThrows(AonCoreException.class, () -> InvoiceDocDAO.save(ctx, doc));
		assertEquals( AonError.EMPTY_DATA.format( IJsonNames.S3_BUCKET ), e.getMessage());
	}

	@Test
	public void testValidExternalStorageAWSS3Key() {
		InvoiceDoc doc = new InvoiceDoc()
			.setDomain(DOMAIN_ID)
			.setMimeType(MimeType.PDF)
			.setType( InvoiceAttachmentType.INVOICE )
			.setInvoice(1)
			.setExternalStorage( ExternalStorage.AWS )
			.setS3Bucket("S3Bucket")
			;
		AonCoreException e = assertThrows(AonCoreException.class, () -> InvoiceDocDAO.save(ctx, doc));
		assertEquals( AonError.EMPTY_DATA.format( IJsonNames.S3_KEY  ), e.getMessage());
	}

	@Test
	public void testValidExternalStorageSCALEWAYS3Bucket() {
		InvoiceDoc doc = new InvoiceDoc()
			.setDomain(DOMAIN_ID)
			.setMimeType(MimeType.PDF)
			.setType( InvoiceAttachmentType.INVOICE )
			.setInvoice(1)
			.setExternalStorage( ExternalStorage.SCALEWAY )
			;
		AonCoreException e = assertThrows(AonCoreException.class, () -> InvoiceDocDAO.save(ctx, doc));
		assertEquals( AonError.EMPTY_DATA.format( IJsonNames.S3_BUCKET ), e.getMessage());
	}

	@Test
	public void testValidExternalStorageSCALEWAYS3Key() {
		InvoiceDoc doc = new InvoiceDoc()
			.setDomain(DOMAIN_ID)
			.setMimeType(MimeType.PDF)
			.setType( InvoiceAttachmentType.INVOICE )
			.setInvoice(1)
			.setExternalStorage( ExternalStorage.SCALEWAY )
			.setS3Bucket("S3Bucket")
			;
		AonCoreException e = assertThrows(AonCoreException.class, () -> InvoiceDocDAO.save(ctx, doc));
		assertEquals( AonError.EMPTY_DATA.format( IJsonNames.S3_KEY  ), e.getMessage());
	}
	
	@Test
	public void testOverflowDescription() {
		int columnSize = INVOICE_DOC.DESCRIPTION.getDataType().length();
		InvoiceDoc doc = new InvoiceDoc()
			.setDomain(DOMAIN_ID)
			.setMimeType(MimeType.PDF)
			.setType( InvoiceAttachmentType.INVOICE )
			.setInvoice(1)
			.setExternalStorage( ExternalStorage.AWS)
			.setS3Bucket("S3Bucket")
			.setS3Key("S3Key")
			.setDescription( AonStringUtils.repeat("*", columnSize + 1))
		;
		AonCoreException e = assertThrows(AonCoreException.class, () -> InvoiceDocDAO.save(ctx, doc));
		assertEquals( AonError.INVALID_LENGTH.format(IJsonNames.DESCRIPTION,columnSize), e.getMessage());
	}
	

	@Test
	public void testAONInsert() throws IOException {
		// New Invoice
		int year = AonDateUtils.getYear( new Date() );
		Date issueDate = AonRandom.getRandomYearDay( year );
		InvoiceFakerParams params = new InvoiceFakerParams(ctx).setIssueDate( issueDate );
		Invoice invoice = InvoiceFaker.getRandomNotSales(params);
		InvoiceDAO.insert(ctx, invoice);
		
		// New Invoice Attach
		Attach attach = new Attach();
		attach.setDomain(new Domain().setId(DOMAIN_ID));
		attach.setAttachModule(invoice.getId());
		attach.setAttachType( AttachType.INVOICE );
		attach.setType( InvoiceAttachmentType.INVOICE.value() );
		attach.setDate(invoice.getIssueDate());
		attach.setMimeType(MimeType.PDF);
		attach.setDescription("Factura");
		int i = AonRandom.number(0, INVOICES.length - 1);
		attach.setData( InvoiceDocDAOTest.class.getResourceAsStream( INVOICES[i] ).readAllBytes() );
		Integer attachId = AttachmentDAO.insertInvoiceAttach(ctx, attach );
		
		// Simulate InvoiceDoc
		InvoiceDoc doc = new 	InvoiceDoc()
			.setDomain(DOMAIN_ID)
			.setMimeType(MimeType.PDF)
			.setType( InvoiceAttachmentType.INVOICE )
			.setInvoice( invoice.getId())
			.setExternalStorage( ExternalStorage.AON )
			.setAonId( attachId )
		;
		
		// Real InvoiceDoc
		Optional<InvoiceDoc> optInserted = InvoiceDocDAO.get(ctx, DOMAIN_ID, doc.getInvoice());
		assertNotNull( optInserted );
		assertTrue( optInserted.isPresent() );
		InvoiceDoc inserted =  optInserted.get();
		
		// Compare
		assertEquals(invoice.getId(), inserted.getInvoice());
		assertEquals(inserted.getAonTable(), "invoice_doc");
		assertEquals(ExternalStorage.AON, inserted.getExternalStorage());
		assertNull(inserted.getS3Bucket());
		assertNull(inserted.getS3Key());
		assertNull(inserted.getDriveId());
		assertEquals(attachId, inserted.getAonId());
		assertEquals(invoice.getIssueDate(), inserted.getDate());
		assertEquals(inserted.getDescription(), "Factura");
		assertTrue( AonStringUtils.startsWith(inserted.getUrl(), "https://"+DOMAIN_NAME));

	}
	
	@Test
	public void testAWSInsert() {
		// New Invoice
		int year = AonDateUtils.getYear( new Date() );
		Date issueDate = AonRandom.getRandomYearDay( year );
		InvoiceFakerParams params = new InvoiceFakerParams(ctx).setIssueDate( issueDate );
		Invoice invoice = InvoiceFaker.getRandomNotSales(params);
		InvoiceDAO.insert(ctx, invoice);
		
		// New Invoice Doc
		String invoicePath = INVOICES[0];
		String s3Key = AonStringUtils.substringAfterLast(INVOICES[0], "/");
		InvoiceDoc doc = new 	InvoiceDoc()
			.setDomain(DOMAIN_ID)
			.setMimeType(MimeType.PDF)
			.setType( InvoiceAttachmentType.INVOICE )
			.setDate(issueDate) 
			.setInvoice( invoice.getId())
			.setDescription( invoicePath )
			.setExternalStorage( ExternalStorage.AWS )
			.setS3Bucket(S3BUCKET)
			.setS3Key(s3Key)
		;
		InvoiceDocDAO.save(ctx, doc);
		
		// Read InvoiceDoc
		Optional<InvoiceDoc> optInserted = InvoiceDocDAO.get(ctx, DOMAIN_ID, doc.getInvoice());
		assertNotNull( optInserted );
		assertTrue( optInserted.isPresent() );
		InvoiceDoc inserted =  optInserted.get();
		
		// Compare
		assertEquals(invoice.getId(), inserted.getInvoice());
		assertEquals(inserted.getAonTable(), "invoice_doc");
		assertEquals(ExternalStorage.AWS, inserted.getExternalStorage());
		assertEquals(S3BUCKET,inserted.getS3Bucket());
		assertEquals(s3Key,inserted.getS3Key());
		assertNull(inserted.getDriveId());
		assertNull(inserted.getAonId());
		assertEquals(invoice.getIssueDate(), inserted.getDate());
		assertEquals(invoicePath, inserted.getDescription());
		assertTrue( AonStringUtils.startsWith(inserted.getUrl(), "https://"+DOMAIN_NAME));

	}
	
	@Test
	public void testCRUDE() {
		// New Invoice
		int year = AonDateUtils.getYear( new Date() );
		Date issueDate = AonRandom.getRandomYearDay( year );
		InvoiceFakerParams params = new InvoiceFakerParams(ctx).setIssueDate( issueDate );
		Invoice invoice = InvoiceFaker.getRandomNotSales(params);
		InvoiceDAO.insert(ctx, invoice);
		
		// New Invoice Doc
		String invoicePath = INVOICES[2];
		String s3Key = AonStringUtils.substringAfterLast(invoicePath, "/");
		InvoiceDoc doc = new 	InvoiceDoc()
			.setDomain(DOMAIN_ID)
			.setMimeType(MimeType.PDF)
			.setType( InvoiceAttachmentType.INVOICE )
			.setDate(issueDate) 
			.setInvoice( invoice.getId())
			.setDescription( invoicePath )
			.setExternalStorage( ExternalStorage.AWS )
			.setS3Bucket(S3BUCKET)
			.setS3Key(s3Key)
		;
		InvoiceDocDAO.save(ctx, doc);
		
		// Read InvoiceDoc
		Optional<InvoiceDoc> optInserted = InvoiceDocDAO.get(ctx, DOMAIN_ID, doc.getInvoice());
		assertNotNull( optInserted );
		assertTrue( optInserted.isPresent() );
		InvoiceDoc inserted =  optInserted.get();
		
		// Compare
		assertEquals(invoice.getId(), inserted.getInvoice());
		assertEquals(inserted.getAonTable(), "invoice_doc");
		assertEquals(ExternalStorage.AWS, inserted.getExternalStorage());
		assertEquals(S3BUCKET,inserted.getS3Bucket());
		assertEquals(s3Key,inserted.getS3Key());
		assertNull(inserted.getDriveId());
		assertNull(inserted.getAonId());
		assertEquals(invoice.getIssueDate(), inserted.getDate());
		assertEquals(invoicePath, inserted.getDescription());
		assertTrue( AonStringUtils.startsWith(inserted.getUrl(), "https://"+DOMAIN_NAME));
		
		InvoiceDAO.delete(ctx, invoice.getId());
		Optional<InvoiceDoc> optDeleted = InvoiceDocDAO.get(ctx, DOMAIN_ID, doc.getInvoice());
		assertNotNull( optDeleted );
		assertTrue( optDeleted.isEmpty() );
	}
	
	@Test
	public void testInsertExisting() {
		// New Invoice
		int year = AonDateUtils.getYear( new Date() );
		Date issueDate = AonRandom.getRandomYearDay( year );
		InvoiceFakerParams params = new InvoiceFakerParams(ctx).setIssueDate( issueDate );
		Invoice invoice = InvoiceFaker.getRandomNotSales(params);
		InvoiceDAO.insert(ctx, invoice);
		
		// New Invoice Doc
		String invoicePath = INVOICES[2];
		String s3Key = AonStringUtils.substringAfterLast(invoicePath, "/");
		InvoiceDoc doc = new 	InvoiceDoc()
			.setDomain(DOMAIN_ID)
			.setMimeType(MimeType.PDF)
			.setType( InvoiceAttachmentType.INVOICE )
			.setDate(issueDate) 
			.setInvoice( invoice.getId())
			.setDescription( invoicePath )
			.setExternalStorage( ExternalStorage.AWS )
			.setS3Bucket(S3BUCKET)
			.setS3Key(s3Key)
		;
		InvoiceDocDAO.save(ctx, doc);
		
		// Read InvoiceDoc
		Optional<InvoiceDoc> optInserted = InvoiceDocDAO.get(ctx, DOMAIN_ID, doc.getInvoice());
		assertNotNull( optInserted );
		assertTrue( optInserted.isPresent() );
		InvoiceDoc inserted =  optInserted.get();
		
		// Compare
		assertEquals(invoice.getId(), inserted.getInvoice());
		assertEquals(inserted.getAonTable(), "invoice_doc");
		assertEquals(ExternalStorage.AWS, inserted.getExternalStorage());
		assertEquals(S3BUCKET,inserted.getS3Bucket());
		assertEquals(s3Key,inserted.getS3Key());
		assertNull(inserted.getDriveId());
		assertNull(inserted.getAonId());
		assertEquals(invoice.getIssueDate(), inserted.getDate());
		assertEquals(invoicePath, inserted.getDescription());
		assertTrue( AonStringUtils.startsWith(inserted.getUrl(), "https://"+DOMAIN_NAME));
		

		// New Invoice Doc
		String otherInvoicePath = INVOICES[3];
		String otherS3Key = AonStringUtils.substringAfterLast(otherInvoicePath, "/");
		InvoiceDoc otherDoc = new 	InvoiceDoc()
			.setDomain(DOMAIN_ID)
			.setMimeType(MimeType.PDF)
			.setType( InvoiceAttachmentType.INVOICE )
			.setDate(issueDate) 
			.setInvoice( invoice.getId())
			.setDescription( otherInvoicePath )
			.setExternalStorage( ExternalStorage.AWS )
			.setS3Bucket(S3BUCKET)
			.setS3Key(otherS3Key)
		;
		InvoiceDocDAO.save(ctx, otherDoc);
		
		// Read InvoiceDoc
		Optional<InvoiceDoc> optOtherInserted = InvoiceDocDAO.get(ctx, DOMAIN_ID, doc.getInvoice());
		assertNotNull( optOtherInserted );
		assertTrue( optOtherInserted.isPresent() );
		InvoiceDoc otherInserted =  optOtherInserted.get();
		
		// Compare
		assertEquals(invoice.getId(), otherInserted.getInvoice());
		assertEquals(otherInserted.getAonTable(), "invoice_doc");
		assertEquals(ExternalStorage.AWS, otherInserted.getExternalStorage());
		assertEquals(S3BUCKET,otherInserted.getS3Bucket());
		assertEquals(otherS3Key,otherInserted.getS3Key());
		assertNull(otherInserted.getDriveId());
		assertNull(otherInserted.getAonId());
		assertEquals(invoice.getIssueDate(), otherInserted.getDate());
		assertEquals(otherInvoicePath, otherInserted.getDescription());
		assertTrue( AonStringUtils.startsWith(otherInserted.getUrl(), "https://"+DOMAIN_NAME));
		
		assertEquals(inserted.getId(), otherInserted.getId());
		
	}
	
	@Test
	public void testGetFullInvoice() {
		// New Invoice
		int year = AonDateUtils.getYear( new Date() );
		Date issueDate = AonRandom.getRandomYearDay( year );
		InvoiceFakerParams params = new InvoiceFakerParams(ctx).setIssueDate( issueDate );
		Invoice invoice = InvoiceFaker.getRandomNotSales(params);
		InvoiceDAO.insert(ctx, invoice);
		
		// New Invoice Doc
		String invoicePath = INVOICES[2];
		String s3Key = AonStringUtils.substringAfterLast(invoicePath, "/");
		InvoiceDoc doc = new 	InvoiceDoc()
			.setDomain(DOMAIN_ID)
			.setMimeType(MimeType.PDF)
			.setType( InvoiceAttachmentType.INVOICE )
			.setDate(issueDate) 
			.setInvoice( invoice.getId())
			.setDescription( invoicePath )
			.setExternalStorage( ExternalStorage.AWS )
			.setS3Bucket(S3BUCKET)
			.setS3Key(s3Key)
		;
		InvoiceDocDAO.save(ctx, doc);
		
		Invoice full = InvoiceDAO.getFullInvoice(ctx, doc.getInvoice());
		assertNotNull( full );
		assertTrue( full.getDoc().isPresent() );
		InvoiceDoc inserted =  full.getDoc().get();
		
		// Compare
		assertEquals(invoice.getId(), inserted.getInvoice());
		assertEquals(inserted.getAonTable(), "invoice_doc");
		assertEquals(ExternalStorage.AWS, inserted.getExternalStorage());
		assertEquals(S3BUCKET,inserted.getS3Bucket());
		assertEquals(s3Key,inserted.getS3Key());
		assertNull(inserted.getDriveId());
		assertNull(inserted.getAonId());
		assertEquals(invoice.getIssueDate(), inserted.getDate());
		assertEquals(invoicePath, inserted.getDescription());
		assertTrue( AonStringUtils.startsWith(inserted.getUrl(), "https://"+DOMAIN_NAME));
		
	}
	
}
