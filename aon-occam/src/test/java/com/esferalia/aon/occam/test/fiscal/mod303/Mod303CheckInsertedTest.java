package com.esferalia.aon.occam.test.fiscal.mod303;

import static com.esferalia.aon.jooq.tables.Alcatraz.ALCATRAZ;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.InvoiceDetail.INVOICE_DETAIL;
import static com.esferalia.aon.jooq.tables.InvoiceDua.INVOICE_DUA;
import static com.esferalia.aon.jooq.tables.InvoiceTax.INVOICE_TAX;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.jooq.impl.DSL;
import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.type.TaxType;

class Mod303CheckInsertedTest extends Mod303AbstractTest{

	@Test
	void checkInserted() {
		int year = LocalDate.now().getYear();
		Set<Integer> invoices = ctx.getDslContext().select(INVOICE.ID)
			.from(INVOICE)
			.innerJoin(INVOICE_DETAIL).on(INVOICE_DETAIL.INVOICE.eq(INVOICE.ID))
			.innerJoin(INVOICE_TAX).on(INVOICE_TAX.INVOICE_DETAIL.eq(INVOICE_DETAIL.ID))
			.leftAntiJoin(ALCATRAZ).on(ALCATRAZ.INVOICE.eq(INVOICE.ID))
			.where( DSL.year(INVOICE.TAX_DATE).eq(year))
			.and(INVOICE_TAX.TAX_TYPE.eq(TaxType.VAT.value()))
			.stream()
			.map(rec -> rec.getValue(INVOICE.ID))
			.filter( id -> isNotImportationWithoutDUA(id))
			.collect(Collectors.toCollection(HashSet::new));

		invoices.stream().forEach( i -> System.out.println( "\tFra. sin tener en cuenta ...: " + i));
		assertEquals(0, invoices.size(),"Existen facturas que no se han tenido en cuenta");
	}

	private boolean isNotImportationWithoutDUA(Integer id) {
		Invoice inv = AON.getInvoice(getOccam(), id);
		if (inv.isPurchase() 
			&& (inv.isExtracommunity() || inv.isCanCeuMel())
			&& !inv.isVatImportation() ) {
			
			boolean hasDua = ctx.getDslContext().select(INVOICE_DUA.ID)
				.from(INVOICE_DUA)
				.where( INVOICE_DUA.INVOICE_IMPORT.eq(inv.getId()))
				.stream()
				.findFirst()
				.isPresent();
			
			return hasDua;
		}
		return true;
	}

}
