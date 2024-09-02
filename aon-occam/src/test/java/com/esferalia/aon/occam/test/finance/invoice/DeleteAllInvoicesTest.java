package com.esferalia.aon.occam.test.finance.invoice;


import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.test.AbstractOccamTest;


class DeleteAllInvoicesTest extends AbstractOccamTest {

	@Test
	void test() {
		System.out.print(" ");
		AON.getInvoiceStream(getOccam(), p -> p.getDomainProperty().eq(DOMAIN_ID))
			.map(inv ->  {
				System.out.print(".");
				return inv;
			})
			.forEach(inv ->  AON.deleteInvoice(getOccam(), inv.getId()));
		System.out.println();
		assertEquals(0 
			, AON.getInvoiceStream(getOccam(), p -> p.getDomainProperty().eq(DOMAIN_ID)).count()
			, "Existen facturas después del borrado");
	}

}
