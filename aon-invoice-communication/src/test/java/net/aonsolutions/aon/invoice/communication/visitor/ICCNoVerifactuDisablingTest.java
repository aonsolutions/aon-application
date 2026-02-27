package net.aonsolutions.aon.invoice.communication.visitor;
 
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationConfiguration;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationError;
import com.esferalia.aon.occam.impl.jooq.dao.ICCDAO;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;


class ICCNoVerifactuDisablingTest extends ICCAbstractEnablingTest {

	@Test
	void test_disable_no_verifactu_from_no_verifactu() {
		resetAndGetIcc();
		Date today = AonDateUtils.today();
		Date lastMonthFirstDay = AonDateUtils.getMonthFirstDay( AonDateUtils.addMonths( today, -1 ));
		InvoiceCommunicationConfiguration icc = ICCDAO.enableNoVerifactu( getCtx(),getDomainId(), lastMonthFirstDay );
		printIcc(icc);
		assertTrue( icc.isAEAT( today ) );
		assertTrue( icc.isNoVerifactu( today ) );
		
		AonCoreException e = assertThrows(AonCoreException.class,  () -> ICCDAO.disableNoVerifactu( getCtx(),getDomainId(), today, today));
		System.out.println( "\t [EXCEPTION OK] - " + e.getMessage() );
		assertEquals( InvoiceCommunicationError.ICC_6002.getMessage(), e.getMessage() );
		
	}
	
}
