package com.esferalia.aon.occam.test.fiscal.mod115;

import java.text.MessageFormat;
import java.util.stream.IntStream;

import org.junit.Assert;
import org.junit.Test;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.AonRandom;

public class Mod115InsertInvoicesTest extends AbstractOccamTest {

	@Test
	public void test() {
		System.out.println(MessageFormat.format("\t\t {0} Invoices inserted "
			,IntStream.range(0 , AonRandom.getInt(1, 50))
			.mapToObj(i -> AonRandom.getRandomWithholdingType() )
			.map(wt -> AonRandom.generateRandomRetentionInvoices(ctx,getOccam(),getConfiguration(),wt))
			.count()));
		Assert.assertTrue( AON.getInvoiceStream(getOccam(), p -> p.getDomainProperty().eq(DOMAIN_ID))
			.findFirst()
			.isPresent());
	}

}
