package com.esferalia.aon.occam.impl.jooq.dao.invoice;



import org.junit.jupiter.api.RepeatedTest;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.AonRandom;
import com.esferalia.aon.occam.test.faker.InvoiceFaker;
import com.esferalia.aon.occam.test.faker.InvoiceFaker.InvoiceFakerParams;
import com.esferalia.aon.watson.server.AonDateUtils;


class InvoiceDaoSettersTest extends AbstractOccamTest {
	
	
	@RepeatedTest( 10 )
	void testInsertInvoice() {
		int year = AonDateUtils.getYear( getTestDate() );
		Invoice invoice = null;
		if (AonRandom.gt(90)) {
			invoice = AonRandom.generateRandomRetentionInvoice(ctx,getOccam(),AonRandom.getRandomWithholdingType());
		} else {
			InvoiceFakerParams params = new InvoiceFakerParams(ctx).setIssueDate( AonRandom.getRandomYearDay( year ) );
			invoice = InvoiceFaker.getRandom(params);
		}
		invoice = AON.insertInvoice(getOccam(),invoice);
		
	}
	

}
