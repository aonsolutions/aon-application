package com.esferalia.aon.occam.test.finance.invoice;


import org.junit.Assert;
import org.junit.Test;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.test.AbstractOccamTest;


public class DeleteAllInvoicesTest extends AbstractOccamTest {

	@Test
	public void test() {
		AON.getInvoiceStream(getOccam(), p -> p.getDomainProperty().eq(DOMAIN_ID))
			.forEach(inv ->  AON.deleteInvoice(getOccam(), inv.getId()));
		Assert.assertEquals("Existen facturas después del borrado"
				, AON.getInvoiceStream(getOccam(), p -> p.getDomainProperty().eq(DOMAIN_ID))
					.count()
				, 0);
	}

}
