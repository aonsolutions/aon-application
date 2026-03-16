package net.aonsolutions.aon.invoice.communication.visitor;
 
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.model.invoice.CommunicationData;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationConfiguration;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationError;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.impl.jooq.dao.ICCDAO;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;


class ICCNoVerifactuDisablingTest extends ICCAbstractEnablingTest {

	@Test
	void test_disable_no_verifactu_from_no_verifactu() {
		resetAndGetIcc();
		Date today = AonDateUtils.today();
		Date lastMonthFirstDay = AonDateUtils.getMonthFirstDay( AonDateUtils.addMonths( today, -1 ));
		CommunicationData toEnable = getCD(Administration.COMMON_TERRITORY)
			.setStartDate( lastMonthFirstDay )
			.setTest( true );
		InvoiceCommunicationConfiguration icc = ICCDAO.enableNoVerifactu( getCtx(),getDomainId(), toEnable);
		printIcc(icc);
		assertTrue( icc.isAEAT( today ) );
		assertTrue( icc.isNoVerifactu( today ) );
		
		CommunicationData toDisable = getCD(Administration.COMMON_TERRITORY)
			.setStartDate( lastMonthFirstDay )
			.setEndDate( lastMonthFirstDay )
			.setTest( true );
		AonCoreException e = assertThrows(AonCoreException.class,  () -> ICCDAO.disableNoVerifactu( getCtx(),getDomainId(), toDisable));
		e.printStackTrace();
		assertEquals( InvoiceCommunicationError.ICC_6002.getMessage(), e.getMessage() );
		
	}
}
