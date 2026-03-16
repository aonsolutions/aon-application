package net.aonsolutions.aon.invoice.communication.visitor;
 
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.model.EnterpriseDataNames;
import com.esferalia.aon.occam.api.model.invoice.CommunicationData;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationConfiguration;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.impl.jooq.dao.ICCDAO;
import com.esferalia.aon.watson.server.AonDateUtils;


class ICCSiiEnablingTest extends ICCAbstractEnablingTest {

	@Test
	void test_enable_sii_aeat_from_nothing_past() {
		resetAndGetIcc();
		Date today = AonDateUtils.today();
		Date lastMonthFirstDay = AonDateUtils.getMonthFirstDay( AonDateUtils.addMonths( today, -1 ));
		CommunicationData toEnable = getCD(Administration.COMMON_TERRITORY)
			.setDataName( EnterpriseDataNames.ICC_SII)
			.setStartDate(lastMonthFirstDay );
		InvoiceCommunicationConfiguration icc = ICCDAO.enableSii( getCtx(),getDomainId(), toEnable);
		printIcc(icc);
		assertTrue( icc.isSii( today ) );
		assertTrue( icc.isAEAT( today ) );
		
		assertFalse( icc.isNoVerifactu( today ) );
		assertFalse( icc.isNoSif( today ) );
		assertFalse( icc.isSif( today ) );
		assertFalse( icc.isLroe( today ) );
		assertFalse( icc.isTbai( today ) );
		
		assertFalse( icc.isCanarias( today ) );
		assertFalse( icc.isAraba( today ) );
		assertFalse( icc.isBizkaia( today ) );
		assertFalse( icc.isNavarra( today ) );
		assertFalse( icc.isGipuzkoa( today ) );
	}
	
	@Test
	void test_enable_sii_canarias_from_nothing_past() {
		resetAndGetIcc();
		Date today = AonDateUtils.today();
		Date lastMonthFirstDay = AonDateUtils.getMonthFirstDay( AonDateUtils.addMonths( today, -1 ));
		CommunicationData toEnable = getCD(Administration.CANARIAS)
				.setDataName( EnterpriseDataNames.ICC_SII)
				.setStartDate(lastMonthFirstDay );
		InvoiceCommunicationConfiguration icc = ICCDAO.enableSii( getCtx(),getDomainId(), toEnable);
		printIcc(icc);
		assertTrue( icc.isSii( today ) );
		assertTrue( icc.isCanarias( today ) );
		
		assertFalse( icc.isNoVerifactu( today ) );
		assertFalse( icc.isNoSif( today ) );
		assertFalse( icc.isSif( today ) );
		assertFalse( icc.isLroe( today ) );
		assertFalse( icc.isTbai( today ) );
		
		assertFalse( icc.isAEAT( today ) );
		assertFalse( icc.isAraba( today ) );
		assertFalse( icc.isBizkaia( today ) );
		assertFalse( icc.isNavarra( today ) );
		assertFalse( icc.isGipuzkoa( today ) );
	}
	
	@Test
	void test_enable_sii_navarra_from_nothing_past() {
		resetAndGetIcc();
		Date today = AonDateUtils.today();
		Date lastMonthFirstDay = AonDateUtils.getMonthFirstDay( AonDateUtils.addMonths( today, -1 ));
		CommunicationData toEnable = getCD(Administration.NAVARRA)
				.setDataName( EnterpriseDataNames.ICC_SII)
				.setStartDate(lastMonthFirstDay );
		InvoiceCommunicationConfiguration icc = ICCDAO.enableSii( getCtx(),getDomainId(), toEnable);
		printIcc(icc);
		assertTrue( icc.isSii( today ) );
		assertTrue( icc.isNavarra( today ) );
		
		assertFalse( icc.isNoVerifactu( today ) );
		assertFalse( icc.isNoSif( today ) );
		assertFalse( icc.isSif( today ) );
		assertFalse( icc.isLroe( today ) );
		assertFalse( icc.isTbai( today ) );
		
		assertFalse( icc.isAEAT( today ) );
		assertFalse( icc.isAraba( today ) );
		assertFalse( icc.isBizkaia( today ) );
		assertFalse( icc.isCanarias( today ) );
		assertFalse( icc.isGipuzkoa( today ) );
	}
	
	@Test
	void test_enable_sii_araba_from_nothing_past() {
		resetAndGetIcc();
		Date today = AonDateUtils.today();
		Date lastMonthFirstDay = AonDateUtils.getMonthFirstDay( AonDateUtils.addMonths( today, -1 ));
		CommunicationData toEnable = getCD(Administration.ALAVA)
				.setDataName( EnterpriseDataNames.ICC_SII)
				.setStartDate(lastMonthFirstDay );
		InvoiceCommunicationConfiguration icc = ICCDAO.enableSii( getCtx(),getDomainId(), toEnable);
		printIcc(icc);
		assertTrue( icc.isSii( today ) );
		assertTrue( icc.isAraba( today ) );
		
		assertFalse( icc.isNoVerifactu( today ) );
		assertFalse( icc.isNoSif( today ) );
		assertFalse( icc.isSif( today ) );
		assertFalse( icc.isLroe( today ) );
		assertFalse( icc.isTbai( today ) );
		
		assertFalse( icc.isAEAT( today ) );
		assertFalse( icc.isNavarra( today ) );
		assertFalse( icc.isBizkaia( today ) );
		assertFalse( icc.isCanarias( today ) );
		assertFalse( icc.isGipuzkoa( today ) );
	}

	@Test
	void test_enable_sii_gipuzkoa_from_nothing_past() {
		resetAndGetIcc();
		Date today = AonDateUtils.today();
		Date lastMonthFirstDay = AonDateUtils.getMonthFirstDay( AonDateUtils.addMonths( today, -1 ));
		CommunicationData toEnable = getCD(Administration.GIPUZKOA)
				.setDataName( EnterpriseDataNames.ICC_SII)
				.setStartDate(lastMonthFirstDay );
		InvoiceCommunicationConfiguration icc = ICCDAO.enableSii( getCtx(),getDomainId(), toEnable);
		printIcc(icc);
		assertTrue( icc.isSii( today ) );
		assertTrue( icc.isGipuzkoa( today ) );
		
		assertFalse( icc.isNoVerifactu( today ) );
		assertFalse( icc.isNoSif( today ) );
		assertFalse( icc.isSif( today ) );
		assertFalse( icc.isLroe( today ) );
		assertFalse( icc.isTbai( today ) );
		
		assertFalse( icc.isAEAT( today ) );
		assertFalse( icc.isNavarra( today ) );
		assertFalse( icc.isBizkaia( today ) );
		assertFalse( icc.isCanarias( today ) );
		assertFalse( icc.isAraba( today ) );
	}
	
	@Test
	void test_sii_araba_from_no_sif() {
		resetAndGetIcc();
		Date today = AonDateUtils.today();
		Date lastMonthFirstDay = AonDateUtils.getMonthFirstDay( AonDateUtils.addMonths( today, -1 ));
		CommunicationData toEnable = getCD(Administration.COMMON_TERRITORY)
				.setDataName( EnterpriseDataNames.ICC_NO_SIF)
				.setStartDate(lastMonthFirstDay );
		InvoiceCommunicationConfiguration icc = ICCDAO.enableNoSif( getCtx(),getDomainId(), toEnable);
		printIcc(icc);
		assertTrue( icc.isAEAT( today ) );
		assertTrue( icc.isNoSif( today ) );
		
		assertFalse( icc.isCanarias( today ) );
		assertFalse( icc.isBizkaia( today ) );
		assertFalse( icc.isAraba( today ) );
		assertFalse( icc.isNavarra( today ) );
		
		assertFalse( icc.isNoVerifactu( today ) );
		assertFalse( icc.isSif( today ) );
		assertFalse( icc.isTbai( today ) );
		assertFalse( icc.isLroe( today ) );
		assertFalse( icc.isSii( today ) );
		assertFalse( icc.isGipuzkoa( today ) );

		CommunicationData toEnable2 = getCD(Administration.ALAVA)
				.setDataName( EnterpriseDataNames.ICC_SII)
				.setStartDate(today );
		InvoiceCommunicationConfiguration icc2 = ICCDAO.enableSii( getCtx(),getDomainId(), toEnable2);
		printIcc(icc2);
		assertTrue( icc2.isAraba( today ) );
		assertTrue( icc2.isSii( today ) );
		
		assertTrue( icc2.isAEAT( lastMonthFirstDay ) );
		assertTrue( icc2.isNoSif( lastMonthFirstDay ) );
		
		assertFalse( icc2.isNoVerifactu( today ) );
		assertFalse( icc2.isNoSif( today ) );
		assertFalse( icc2.isSif( today ) );
		assertFalse( icc2.isLroe( today ) );
		assertFalse( icc2.isTbai( today ) );
		
		assertFalse( icc2.isCanarias( today ) );
		assertFalse( icc2.isAEAT( today ) );
		assertFalse( icc2.isGipuzkoa( today ) );
		assertFalse( icc2.isBizkaia( today ) );
		assertFalse( icc2.isNavarra( today ) );
	}

}
