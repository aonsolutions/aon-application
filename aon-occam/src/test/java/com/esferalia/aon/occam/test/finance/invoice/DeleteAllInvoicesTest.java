package com.esferalia.aon.occam.test.finance.invoice;


import static com.esferalia.aon.occam.test.OccamAssertions.*;
import org.junit.Test;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.test.AbstractOccamTest;


public class DeleteAllInvoicesTest extends AbstractOccamTest {

	@Test
	public void test() {
		System.out.print(" ");
		AON.getInvoiceStream(getOccam(), p -> p.getDomainProperty().eq(DOMAIN_ID))
			.map(inv ->  {
				System.out.print(".");
				return inv;
			})
			.forEach(inv ->  AON.deleteInvoice(getOccam(), inv.getId()));
		System.out.println();
		assertEquals(AON.getInvoiceStream(getOccam(), p -> p.getDomainProperty().eq(DOMAIN_ID))
					.count(), 0, "Existen facturas después del borrado");
	}

}
