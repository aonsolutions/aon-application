package com.esferalia.aon.occam.test.fiscal.invoice;

import java.text.MessageFormat;
import java.time.LocalDate;

import org.junit.Assert;
import org.junit.Test;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.AonRandom;
import com.esferalia.aon.occam.test.faker.InvoiceFaker;
import com.esferalia.aon.occam.test.faker.InvoiceFaker.InvoiceFakerParams;

public class InsertRandomInvoicesTest extends AbstractOccamTest {

	@Test
	public void test() {
		int year = LocalDate.now().getYear();
		int times = AonRandom.getInt(1, 50);
		int mod = AonRandom.getInt(0, times);
		for (int count = 0; count < times; count++) {
			InvoiceFakerParams params = new InvoiceFakerParams(ctx,getConfiguration()).setIssueDate( AonRandom.getRandomYearDay( year ) );
			AON.insertInvoice(getOccam(),InvoiceFaker.getRandom(params));
			if (mod != 0 && times % mod == 0) {
				AonRandom.generateRandomRetentionInvoice(ctx,getOccam(),getConfiguration(),AonRandom.getRandomWithholdingType());
				count++;
			}
		}
		System.out.println(MessageFormat.format("\t\t {0} Invoices inserted ",times));
		Assert.assertTrue( 
			InvoiceDAO.getInvoiceHeaders(ctx, p -> p.getDomainProperty().eq(DOMAIN_ID), 0, 1)
				.findFirst()
				.isPresent()
		);
	}

}
