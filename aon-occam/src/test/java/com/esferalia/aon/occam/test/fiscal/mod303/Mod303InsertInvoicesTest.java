package com.esferalia.aon.occam.test.fiscal.mod303;

import java.text.MessageFormat;
import java.time.LocalDate;

import org.junit.Assert;
import org.junit.Test;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.AonRandom;
import com.esferalia.aon.occam.test.faker.InvoiceFaker;
import com.esferalia.aon.occam.test.faker.InvoiceFaker.InvoiceFakerParams;

public class Mod303InsertInvoicesTest extends AbstractOccamTest {

	@Test
	public void test() {
		int year = LocalDate.now().getYear();
		int times = AonRandom.getInt(1, 50);
		for (int count = 0; count <times; count++) {
			InvoiceFakerParams params = new InvoiceFakerParams(ctx,getConfiguration()).setIssueDate( AonRandom.getRandomYearDay( year ) );
			AON.insertInvoice(getOccam(),InvoiceFaker.getRandom(params));
		}
		System.out.println(MessageFormat.format("\t\t {0} Invoices inserted ",times));
		Assert.assertTrue( AON.getInvoiceStream(getOccam(), p -> p.getDomainProperty().eq(DOMAIN_ID))
				.findFirst()
				.isPresent()
			);
	}

}
