package com.esferalia.aon.occam.test.finance.invoice;


import static com.esferalia.aon.occam.test.OccamAssertions.*;

import org.junit.Test;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.impl.jooq.dao.FinanceDAO;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.AonRandom;
import com.esferalia.aon.occam.test.faker.InvoiceFaker;
import com.esferalia.aon.occam.test.faker.InvoiceFaker.InvoiceFakerParams;
import com.esferalia.aon.watson.error.AonCoreException;


public class InvoiceAnnulTest extends AbstractOccamTest {

	private static final Integer UNKNOWN_INVOICE_ID = 999999999;

	private Invoice insertRandomInvoice() {
		InvoiceFakerParams params = new InvoiceFakerParams(ctx).setIssueDate(AonRandom.today());
		return AON.insertInvoice(DOMAIN_NAME, DOMAIN_ID, USER, InvoiceFaker.getRandom(params));
	}

	private Invoice reload(Integer invoiceId) {
		return AON.getInvoice(DOMAIN_NAME, DOMAIN_ID, USER, invoiceId);
	}

	private long countInvoices() {
		return AON.getInvoiceStream(getOccam(), p -> p.getDomainProperty().eq(DOMAIN_ID)).count();
	}

	private long countInvoices(Integer invoiceId) {
		return AON.getInvoiceStream(getOccam(), p -> p.getDomainProperty().eq(DOMAIN_ID)
				.and(p.getIdProperty().eq(invoiceId))).count();
	}

	private long countFinances(Integer invoiceId) {
		return FinanceDAO.getFinanceStream(ctx, f -> f.getInvoiceProperty().eq(invoiceId)).count();
	}

	/**
	 * La anulaci\u00F3n marca la factura, que se sigue leyendo por id.
	 */
	@Test
	public void testAnnulMarksTheInvoice() {
		Invoice inserted = insertRandomInvoice();
		Invoice before = reload(inserted.getId());
		boolean annulledBefore = before.isAnnulled();
		assertFalse(annulledBefore, "La factura reci\u00E9n insertada no debe estar anulada");

		InvoiceDAO.annul(ctx, inserted.getId());

		Invoice after = reload(inserted.getId());
		assertNotNull(after, "El acceso por id debe seguir devolviendo la factura anulada");
		boolean annulledAfter = after.isAnnulled();
		assertTrue(annulledAfter, "La factura debe quedar anulada");
	}

	/**
	 * Una factura anulada desaparece de los listados, que pasan por el punto \u00FAnico del filtro.
	 */
	@Test
	public void testAnnulledInvoiceIsNotListed() {
		Invoice inserted = insertRandomInvoice();
		long before = countInvoices();

		InvoiceDAO.annul(ctx, inserted.getId());

		long after = countInvoices();
		assertEquals(before - 1, after, "El listado debe traer una factura menos");

		long found = countInvoices(inserted.getId());
		assertEquals(0, found, "La factura anulada no debe aparecer en el listado");
	}

	/**
	 * Regla de tesorer\u00EDa: al anular se borran los vencimientos.
	 */
	@Test
	public void testAnnulDeletesFinances() {
		Invoice inserted = insertRandomInvoice();

		InvoiceDAO.annul(ctx, inserted.getId());

		long finances = countFinances(inserted.getId());
		assertEquals(0, finances, "La factura anulada no debe conservar vencimientos");
	}

	@Test(expected = AonCoreException.class)
	public void testAnnulWithoutIdFails() {
		InvoiceDAO.annul(ctx, null);
	}

	@Test(expected = AonCoreException.class)
	public void testAnnulUnknownInvoiceFails() {
		InvoiceDAO.annul(ctx, UNKNOWN_INVOICE_ID);
	}

	/**
	 * Una factura rectificada no se puede anular mientras tenga su rectificativa.
	 */
	@Test
	public void testAnnulRectifiedInvoiceFails() {
		Invoice rectified = insertRandomInvoice();
		Invoice rectifier = insertRandomInvoice();
		InvoiceDAO.rectify(ctx, rectifier.getId(), rectified.getId());

		try {
			InvoiceDAO.annul(ctx, rectified.getId());
			fail("Anular una factura rectificada debe fallar");
		} catch (AonCoreException e) {
			Invoice after = reload(rectified.getId());
			boolean annulled = after.isAnnulled();
			assertFalse(annulled, "La factura rectificada no debe quedar anulada");
		}
	}

}
