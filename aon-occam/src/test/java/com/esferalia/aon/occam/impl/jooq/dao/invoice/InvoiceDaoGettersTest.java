package com.esferalia.aon.occam.impl.jooq.dao.invoice;



import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceMin;
import com.esferalia.aon.occam.api.model.finance.InvoiceStatus;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.RectificationType;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceOLDDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.faker.AonRandom;
import com.esferalia.aon.occam.test.faker.InvoiceFaker;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;


class InvoiceDaoGettersTest extends AbstractOccamTest {

	@Test
	void testStream() {
		AonRandom.generateRandomInvoices(ctx, getTestDate(), AonRandom.getInt(1, 10));
		assertTrue( 
			InvoiceDAO.stream(ctx, p -> p.getDomainProperty().eq(DOMAIN_ID), 0, 1)
				.findFirst()
				.isPresent()
		);
	}

	@Test
	void testGet() {
		Invoice invoice = InvoiceFaker.getRandom(ctx);
		Invoice saved = assertDoesNotThrow(() -> InvoiceDAO.saveAndGet(ctx, invoice ));
		Optional<Invoice> opt = InvoiceDAO.get(ctx, saved.getId());
		assertTrue(opt.isPresent());
		Invoice get = opt.get();
		Asserts.assertEqualsInvoice(saved, get );
	}
	
	@Test
	void testFullPreviousMethod() {
		Date dateFrom = AonDateUtils.getYearFirstDay(2020);
		Date dateTo = AonDateUtils.getYearFirstDay(2023);
		Date date = AonRandom.getRangeDate(dateFrom, dateTo);
		InvoiceDAO.getStream( ctx, p -> p.getIdProperty().ge(0)
			.and( p.getDomainProperty().eq(getOccam().getDomain()))
			.and( p.getRectificationTypeProperty().eq(RectificationType.NORMAL_RECTIFIER.value()))
			.and( p.getIssueDateProperty().ge(date)))
			.limit( 10 )
			.forEach( i -> {
				Invoice oldInvoice = InvoiceOLDDAO.getFullInvoice(ctx, i.getId() );
				Invoice newInvoice = InvoiceDAO.getFull(ctx, i.getId() ).orElse(null);
				Asserts.assertEqualsFullInvoice( oldInvoice, newInvoice );
			});
	}
	
	@Test
	void testStreamNullFilter() {
		AonCoreException e = assertThrows(AonCoreException.class, () -> InvoiceDAO.stream(ctx, null));
		assertNotNull(e);
	}
	
	@Test
	void testInvoiceMinFilter() {
		InvoiceMin invoiceMin = AonRandom.getInvoiceMin(ctx);
		if (invoiceMin == null) {
			AonRandom.generateRandomInvoices(ctx, getTestDate(), AonRandom.getInt(1, 10));
			invoiceMin = AonRandom.getInvoiceMin(ctx);
		}
		InvoiceMin im = invoiceMin;
		InvoiceDAO.stream(ctx, p -> 
			p.getIdProperty().eq(im.getId())
			.and(p.getDomainProperty().eq(im.getDomain()))
			.and(p.getActivityProperty().eq(im.getActivity()))
			.and(p.getTypeProperty().eq(InvoiceType.safeValueOf(im.getType())))
			.and(p.getSeriesProperty().eq(im.getSeries()))
			.and(p.getNumberProperty().eq(im.getNumber()))
			.and(p.getReferenceCodeProperty().eq(im.getReferenceCode()))
			.and(p.getTransactionProperty().eq(InvoiceTransactionType.safeValueOf( im.getTransaction())))
			.and(p.getIssueDateProperty().eq(im.getIssueDate()))
			.and(p.getTaxDateProperty().eq(im.getTaxDate()))
			.and(p.getRegistryProperty().eq(im.getRegistry()))
			.and(p.getRegistryDocumentProperty().eq(im.getRegistryDocument()))
			.and(p.getRegistryDocumentTypeProperty().eq(DocumentType.safeValueOf( im.getRegistryDocumentType())))
			.and(p.getRegistryDocumentCountryProperty().eq(Country.safeIso2(im.getRegistryDocumentCountry())))
			.and(p.getRegistryNameProperty().eq(im.getRegistryName()))
			.and(p.getScopeProperty().eq(im.getScope()))
			.and(p.getConfidentialProperty().eq(SecurityLevel.safeValueOf(im.isConfidential())))
			.and(p.getStatusProperty().eq(InvoiceStatus.safeValueOf(im.isRecorded())))
			.and(p.getTotalProperty().eq(im.getTotal()))
		).forEach(inv -> Asserts.assertEqualsInvoiceMin(im, inv));
	}
	
}
