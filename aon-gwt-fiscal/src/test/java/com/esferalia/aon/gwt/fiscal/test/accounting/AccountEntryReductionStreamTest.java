package com.esferalia.aon.gwt.fiscal.test.accounting;


import java.io.FileNotFoundException;
import java.util.Date;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.impl.jooq.dao.AccountEntryDAO;
import com.esferalia.aon.occam.impl.jooq.dao.AccountEntryDAO.AccountEntryOrder;


public class AccountEntryReductionStreamTest {

	private static AONContext ctx;
	private static String DOMAIN_NAME = "udapa.ecastellano.euk";
	private static int DOMAIN_ID = 3049;
	private static String USER = "montse";
	
	
	public static void main(String[] args) throws ClassNotFoundException, FileNotFoundException {
		
		Class.forName( org.gjt.mm.mysql.Driver.class.getName() );
		ctx = AONContext.getAONContext(DOMAIN_NAME, DOMAIN_ID, USER);
		int period = 37200;
		Date start = new Date(); 
		System.out.println( "START!" );
		AccountEntryDAO.fetchFlat(ctx,
				p -> p.getAccountPeriodProperty().eq(period) 
				,AccountEntryOrder.ORDER_PERIOD_JOURNAL
				).forEach(acc -> {
					System.out.println( acc.getEntryId() + "\t" + acc.getJournal());
				});
		Date end = new Date();
		System.out.println( "END! ---> " + (end.getTime() - start.getTime()) + "ms.");
	}

}
