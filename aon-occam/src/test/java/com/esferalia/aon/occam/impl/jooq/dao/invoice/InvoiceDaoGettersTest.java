package com.esferalia.aon.occam.impl.jooq.dao.invoice;



import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.type.RectificationType;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceOLDDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.faker.AonRandom;
import com.esferalia.aon.occam.test.faker.InvoiceFaker;
import com.esferalia.aon.watson.server.AonDateUtils;


class InvoiceDaoGettersTest extends AbstractOccamTest {
	
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
}
