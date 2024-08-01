package com.esferalia.aon.occam.test.finance.invoice;


import java.util.Date;

import org.junit.Test;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceDAO;
import com.esferalia.aon.occam.impl.jooq.dao.accounting.invoice.InvoiceTextPrinter;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.watson.mutable.MutableInt;
import com.esferalia.aon.watson.server.AonDateUtils;


public class InvoiceDAOTest extends AbstractOccamTest {

	@Test
	public void testInvoiceFullInvoices() {
		Occam occam = new Occam()
			.setDomain( 5 )
			.setDomainName( "sig.ecastellano.org" )
			.setUser( "jgarcia" );
		try (CloseableAONContext ctx =  AONContext.getAONContext(occam)){
			Date date = AonDateUtils.getYearFirstDay(2022);
			MutableInt x = new MutableInt(0);
			InvoiceDAO.getInvoiceStream( ctx, p -> p.getIdProperty().ge(0)
					.and( p.getDomainProperty().eq(occam.getDomain()))
					.and( p.getEndIssueDateProperty().ge(date))
				)
				.limit( 10 )
				.forEach( i -> {
					x.add(1);
					System.out.println(x.getValue() + " --- " + i.getId());
					if (1627205 == i.getId()) {
						System.out.println( "AQUI");
					}
					Invoice oldInvoice = InvoiceDAO.getFullInvoice(ctx, i.getId() );
					System.out.println();
					System.out.println( " --------------------------------------[OLD]");
					System.out.println();
					InvoiceTextPrinter.print( oldInvoice );
					System.out.println();
					System.out.println( " --------------------------------------[NEW]");
					System.out.println();
					Invoice newInvoice = InvoiceDAO.getFull(ctx, i.getId() ).orElse(null);
					InvoiceTextPrinter.print( newInvoice );
					Asserts.assertEqualsFullInvoice( oldInvoice, newInvoice );
				});
		}

	}

}
