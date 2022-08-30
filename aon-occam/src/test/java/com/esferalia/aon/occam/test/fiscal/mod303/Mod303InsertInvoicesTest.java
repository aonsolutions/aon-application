package com.esferalia.aon.occam.test.fiscal.mod303;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.stream.IntStream;

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
		System.out.println(MessageFormat.format("\t\t {0} Invoices inserted "
				,IntStream.range(0 , AonRandom.getInt(1, 50))
				.mapToObj( i -> new InvoiceFakerParams(ctx,getConfiguration())
						.setIssueDate( AonRandom.getRandomYearDay( year ) ))
				.map( params -> InvoiceFaker.getRandom(params))
				.map(inv -> AON.insertInvoice(getOccam(),inv))
				.count()));
		Assert.assertTrue( AON.getInvoiceStream(getOccam(), p -> p.getDomainProperty().eq(DOMAIN_ID))
				.findFirst()
				.isPresent()
			);
	}

}
