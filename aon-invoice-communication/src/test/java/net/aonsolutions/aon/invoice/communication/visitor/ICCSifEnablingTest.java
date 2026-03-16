package net.aonsolutions.aon.invoice.communication.visitor;
 
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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


class ICCSifEnablingTest extends ICCAbstractEnablingTest {
	@Test
	void test_enable_sif_aeat() {
		resetAndGetIcc();
		Date today = AonDateUtils.today();
		Date lastMonthFirstDay = AonDateUtils.getMonthFirstDay( AonDateUtils.addMonths( today, -1 ));
		CommunicationData toEnable = getCD(Administration.COMMON_TERRITORY).setStartDate(lastMonthFirstDay );
		AonCoreException e = assertThrows(AonCoreException.class, () -> ICCDAO.enableSif( getCtx(),getDomainId(), toEnable));
		assertEquals( InvoiceCommunicationError.ICC_5025.getMessage(), e.getMessage() );
	}

	@Test
	void test_enable_sif_from_nothing_past() {
		resetAndGetIcc();
		Date today = AonDateUtils.today();
		Date lastMonthFirstDay = AonDateUtils.getMonthFirstDay( AonDateUtils.addMonths( today, -1 ));
		CommunicationData toEnable = getCD(Administration.NAVARRA).setStartDate(lastMonthFirstDay );
		InvoiceCommunicationConfiguration icc = ICCDAO.enableSif( getCtx(),getDomainId(), toEnable);
		printIcc(icc);
		assertTrue( icc.isNavarra( today ) );
		assertTrue( icc.isSif( today ) );
		
		assertFalse( icc.isCanarias( today ) );
		assertFalse( icc.isBizkaia( today ) );
		assertFalse( icc.isAraba( today ) );
		assertFalse( icc.isAEAT( today ) );
		
		assertFalse( icc.isNoSif( today ) );
		assertFalse( icc.isNoVerifactu( today ) );
		assertFalse( icc.isVerifactu( today ) );
		assertFalse( icc.isTbai( today ) );
		assertFalse( icc.isLroe( today ) );
		assertFalse( icc.isSii( today ) );
	}

	@Test
	void test_enable_sif_from_nothing_today() {
		resetAndGetIcc();
		Date today = AonDateUtils.today();
		CommunicationData toEnable = getCD(Administration.NAVARRA).setStartDate(today );
		InvoiceCommunicationConfiguration icc = ICCDAO.enableSif( getCtx(),getDomainId(), toEnable);
		printIcc(icc);
		assertTrue( icc.isNavarra( today ) );
		assertTrue( icc.isSif( today ) );
		
		assertFalse( icc.isCanarias( today ) );
		assertFalse( icc.isBizkaia( today ) );
		assertFalse( icc.isAraba( today ) );
		assertFalse( icc.isAEAT( today ) );
		
		assertFalse( icc.isNoSif( today ) );
		assertFalse( icc.isNoVerifactu( today ) );
		assertFalse( icc.isVerifactu( today ) );
		assertFalse( icc.isTbai( today ) );
		assertFalse( icc.isLroe( today ) );
		assertFalse( icc.isSii( today ) );
	}
	
	@Test
	void test_enable_sif_from_sii_today() {
		resetAndGetIcc();
		Date today = AonDateUtils.today();
		CommunicationData toEnable = getCD(Administration.NAVARRA).setStartDate(today );
		InvoiceCommunicationConfiguration icc = ICCDAO.enableSii( getCtx(),getDomainId(), toEnable);
		printIcc(icc);
		
		assertTrue( icc.isNavarra( today ) );
		assertTrue( icc.isSii( today ) );
		
		assertFalse( icc.isCanarias( today ) );
		assertFalse( icc.isBizkaia( today ) );
		assertFalse( icc.isAraba( today ) );
		assertFalse( icc.isAEAT( today ) );
		
		assertFalse( icc.isNoSif( today ) );
		assertFalse( icc.isNoVerifactu( today ) );
		assertFalse( icc.isVerifactu( today ) );
		assertFalse( icc.isTbai( today ) );
		assertFalse( icc.isLroe( today ) );
		assertFalse( icc.isSif( today ) );

		CommunicationData toEnable2 = getCD(Administration.NAVARRA).setStartDate(today );
		AonCoreException e = assertThrows(AonCoreException.class,  () -> ICCDAO.enableSif( getCtx(),getDomainId(), toEnable2));
		assertEquals( InvoiceCommunicationError.ICC_5023.getMessage(), e.getMessage() );
	}
	
	/*
	@Test
	void test_enable_sif_from_no_sif_today() {
		resetAndGetIcc();
		Date today = AonDateUtils.today();
		Date lastMonthFirstDay = AonDateUtils.getMonthFirstDay( AonDateUtils.addMonths( today, -1 ));
		InvoiceCommunicationConfiguration icc = ICCDAO.enableNoSif( getCtx(),getDomainId(), Administration.NAVARRA
			, getCD().setStartDate(lastMonthFirstDay ))
		;
		printIcc(icc);
		assertTrue( icc.isNoSif( today ) );
		assertTrue( icc.isNavarra( today ) );
		
		assertFalse( icc.isCanarias( today ) );
		assertFalse( icc.isBizkaia( today ) );
		assertFalse( icc.isAraba( today ) );
		assertFalse( icc.isAEAT( today ) );
		assertFalse( icc.isUnknown( today ) );
		
		assertFalse( icc.isSif( today ) );
		assertFalse( icc.isSii( today ) );
		assertFalse( icc.isNoVerifactu( today ) );
		assertFalse( icc.isVerifactu( today ) );
		assertFalse( icc.isTbai( today ) );
		assertFalse( icc.isLroe( today ) );

		InvoiceCommunicationConfiguration icc2 = ICCDAO.enableSif( getCtx(),getDomainId(), Administration.NAVARRA
			, getCD()
				.setStartDate(today) 
		);
		printIcc(icc2);
		assertTrue( icc2.isNavarra( today ) );
		assertTrue( icc2.isSif( today ) );
		assertFalse( icc2.isNoSif( today ) );
		
		assertTrue( icc2.isNavarra( lastMonthFirstDay ) );
		assertTrue( icc2.isNoSif( lastMonthFirstDay ) );

		assertFalse( icc2.isSii( today ) );
		assertFalse( icc2.isNoVerifactu( today ) );
		assertFalse( icc2.isVerifactu( today ) );
		assertFalse( icc2.isTbai( today ) );
		assertFalse( icc2.isLroe( today ) );
		
		assertFalse( icc2.isCanarias( today ) );
		assertFalse( icc2.isBizkaia( today ) );
		assertFalse( icc2.isAraba( today ) );
		assertFalse( icc2.isAEAT( today ) );
		assertFalse( icc2.isUnknown( today ) );

	}
*/
}
