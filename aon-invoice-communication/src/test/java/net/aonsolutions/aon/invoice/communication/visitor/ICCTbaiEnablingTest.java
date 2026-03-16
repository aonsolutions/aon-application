package net.aonsolutions.aon.invoice.communication.visitor;
 
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.model.invoice.CommunicationData;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationConfiguration;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.impl.jooq.dao.ICCDAO;
import com.esferalia.aon.watson.server.AonDateUtils;


class ICCTbaiEnablingTest extends ICCAbstractEnablingTest {
	// ---------------------------------------------------------------
	// ------------------------------------------------ [ARABA - TBAI] 
	// ---------------------------------------------------------------
	
	@Test
	void test_enable_tbai_araba_from_nothing_past() {
		resetAndGetIcc();
		Date today = AonDateUtils.today();
		Date lastMonthFirstDay = AonDateUtils.getMonthFirstDay( AonDateUtils.addMonths( today, -1 ));
		CommunicationData toEnable = getCD(Administration.ALAVA).setStartDate(lastMonthFirstDay);
		InvoiceCommunicationConfiguration icc = ICCDAO.enableTbai( getCtx(),getDomainId(), toEnable);
		printIcc(icc);
		assertTrue( icc.isAraba( today ) );
		assertTrue( icc.isTbai( today ) );
		
		assertFalse( icc.isNoVerifactu( today ) );
		assertFalse( icc.isNoSif( today ) );
		assertFalse( icc.isSif( today ) );
		assertFalse( icc.isLroe( today ) );
		assertFalse( icc.isSii( today ) );
		
		assertFalse( icc.isCanarias( today ) );
		assertFalse( icc.isAEAT( today ) );
		assertFalse( icc.isBizkaia( today ) );
		assertFalse( icc.isNavarra( today ) );
		assertFalse( icc.isGipuzkoa( today ) );
	}
	
	@Test
	void test_enable_tbai_sii_araba_from_nothing_past() {
		resetAndGetIcc();
		Date today = AonDateUtils.today();
		Date lastMonthFirstDay = AonDateUtils.getMonthFirstDay( AonDateUtils.addMonths( today, -1 ));
		CommunicationData toEnable = getCD(Administration.ALAVA).setStartDate(lastMonthFirstDay);
		InvoiceCommunicationConfiguration icc = ICCDAO.enableTbai( getCtx(),getDomainId(), toEnable );
		printIcc(icc);
		assertTrue( icc.isAraba( today ) );
		assertTrue( icc.isTbai( today ) );
		
		assertFalse( icc.isNoVerifactu( today ) );
		assertFalse( icc.isNoSif( today ) );
		assertFalse( icc.isSif( today ) );
		assertFalse( icc.isLroe( today ) );
		assertFalse( icc.isSii( today ) );
		
		assertFalse( icc.isCanarias( today ) );
		assertFalse( icc.isAEAT( today ) );
		assertFalse( icc.isBizkaia( today ) );
		assertFalse( icc.isNavarra( today ) );
		assertFalse( icc.isGipuzkoa( today ) );
		
		CommunicationData toEnableSii = getCD(Administration.ALAVA).setStartDate(lastMonthFirstDay);
		InvoiceCommunicationConfiguration icc2 = ICCDAO.enableSii( getCtx(),getDomainId(), toEnableSii);
		printIcc(icc2);
		assertTrue( icc2.isAraba( today ) );
		assertTrue( icc2.isTbai( today ) );
		assertTrue( icc2.isSii( today ) );
		
		assertFalse( icc2.isNoVerifactu( today ) );
		assertFalse( icc2.isNoSif( today ) );
		assertFalse( icc2.isSif( today ) );
		assertFalse( icc2.isLroe( today ) );
		
		assertFalse( icc2.isCanarias( today ) );
		assertFalse( icc2.isAEAT( today ) );
		assertFalse( icc2.isBizkaia( today ) );
		assertFalse( icc2.isNavarra( today ) );
		assertFalse( icc2.isGipuzkoa( today ) );
	}
	
	@Test
	void test_tbai_araba_from_no_sif() {
		resetAndGetIcc();
		Date today = AonDateUtils.today();
		Date lastMonthFirstDay = AonDateUtils.getMonthFirstDay( AonDateUtils.addMonths( today, -1 ));
		CommunicationData toEnable = getCD(Administration.COMMON_TERRITORY).setStartDate(lastMonthFirstDay);
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

		CommunicationData toEnableTbai = getCD(Administration.ALAVA).setStartDate(today);
		InvoiceCommunicationConfiguration icc2 = ICCDAO.enableTbai( getCtx(),getDomainId(), toEnableTbai);
		printIcc(icc2);
		assertTrue( icc2.isAraba( today ) );
		assertTrue( icc2.isTbai( today ) );
		assertFalse( icc2.isAEAT( today ) );
		assertFalse( icc2.isNoSif( today ) );
		
		assertTrue( icc2.isAEAT( lastMonthFirstDay ) );
		assertTrue( icc2.isNoSif( lastMonthFirstDay ) );
		
		assertFalse( icc2.isNoVerifactu( today ) );
		assertFalse( icc2.isNoSif( today ) );
		assertFalse( icc2.isSif( today ) );
		assertFalse( icc2.isLroe( today ) );
		assertFalse( icc2.isSii( today ) );
		
		assertFalse( icc2.isCanarias( today ) );
		assertFalse( icc2.isAEAT( today ) );
		assertFalse( icc2.isGipuzkoa( today ) );
		assertFalse( icc2.isBizkaia( today ) );
		assertFalse( icc2.isNavarra( today ) );
	}
	
	@Test
	void test_tbai_araba_from_verifctu() {
		resetAndGetIcc();
		Date today = AonDateUtils.today();
		Date lastMonthFirstDay = AonDateUtils.getMonthFirstDay( AonDateUtils.addMonths( today, -1 ));
		CommunicationData toEnable = getCD(Administration.COMMON_TERRITORY).setStartDate(lastMonthFirstDay);
		InvoiceCommunicationConfiguration icc = ICCDAO.enableVerifactu( getCtx(),getDomainId(), toEnable);
		printIcc(icc);
		assertTrue( icc.isAEAT( today ) );
		assertTrue( icc.isVerifactu( today ) );
		
		assertFalse( icc.isNoVerifactu( today ) );
		assertFalse( icc.isLroe( today ) );
		assertFalse( icc.isNoSif( today ) );
		assertFalse( icc.isSif( today ) );
		assertFalse( icc.isTbai( today ) );
		assertFalse( icc.isSif( today ) );
		assertFalse( icc.isSii( today ) );
		
		assertFalse( icc.isCanarias( today ) );
		assertFalse( icc.isBizkaia( today ) );
		assertFalse( icc.isAraba( today ) );
		assertFalse( icc.isNavarra( today ) );

		CommunicationData toEnableTbai = getCD(Administration.ALAVA).setStartDate(today);
		InvoiceCommunicationConfiguration icc2 = ICCDAO.enableTbai( getCtx(),getDomainId(), toEnableTbai);
		printIcc(icc2);
		assertTrue( icc2.isAraba( today ) );
		assertTrue( icc2.isTbai( today ) );
		assertFalse( icc2.isAEAT( today ) );
		assertFalse( icc2.isVerifactu( today ) );
		
		assertTrue( icc2.isAEAT( lastMonthFirstDay ) );
		assertTrue( icc2.isVerifactu( lastMonthFirstDay ) );
		
		assertFalse( icc2.isNoVerifactu( today ) );
		assertFalse( icc2.isNoSif( today ) );
		assertFalse( icc2.isSif( today ) );
		assertFalse( icc2.isLroe( today ) );
		assertFalse( icc2.isSii( today ) );
		
		assertFalse( icc2.isCanarias( today ) );
		assertFalse( icc2.isAEAT( today ) );
		assertFalse( icc2.isGipuzkoa( today ) );
		assertFalse( icc2.isBizkaia( today ) );
		assertFalse( icc2.isNavarra( today ) );
		
	}

	@Test
	void test_tbai_araba_from_no_verifctu() {
		resetAndGetIcc();
		Date today = AonDateUtils.today();
		Date lastMonthFirstDay = AonDateUtils.getMonthFirstDay( AonDateUtils.addMonths( today, -1 ));
		CommunicationData toEnable = getCD(Administration.COMMON_TERRITORY).setStartDate(lastMonthFirstDay);
		InvoiceCommunicationConfiguration icc = ICCDAO.enableNoVerifactu( getCtx(),getDomainId(), toEnable);
		printIcc(icc);
		assertTrue( icc.isAEAT( today ) );
		assertTrue( icc.isNoVerifactu( today ) );
		
		assertFalse( icc.isVerifactu( today ) );
		assertFalse( icc.isLroe( today ) );
		assertFalse( icc.isNoSif( today ) );
		assertFalse( icc.isSif( today ) );
		assertFalse( icc.isTbai( today ) );
		assertFalse( icc.isSif( today ) );
		assertFalse( icc.isSii( today ) );
		
		assertFalse( icc.isCanarias( today ) );
		assertFalse( icc.isBizkaia( today ) );
		assertFalse( icc.isAraba( today ) );
		assertFalse( icc.isNavarra( today ) );
		
		CommunicationData toEnableTbai = getCD(Administration.ALAVA).setStartDate(today);
		InvoiceCommunicationConfiguration icc2 = ICCDAO.enableTbai( getCtx(),getDomainId(), toEnableTbai);
		printIcc(icc2);
		assertTrue( icc2.isAraba( today ) );
		assertTrue( icc2.isTbai( today ) );
		assertFalse( icc2.isAEAT( today ) );
		assertFalse( icc2.isNoVerifactu( today ) );
		
		assertTrue( icc2.isAEAT( lastMonthFirstDay ) );
		assertTrue( icc2.isNoVerifactu( lastMonthFirstDay ) );
		
		assertFalse( icc2.isVerifactu( today ) );
		assertFalse( icc2.isNoSif( today ) );
		assertFalse( icc2.isSif( today ) );
		assertFalse( icc2.isLroe( today ) );
		assertFalse( icc2.isSii( today ) );
		
		assertFalse( icc2.isCanarias( today ) );
		assertFalse( icc2.isAEAT( today ) );
		assertFalse( icc2.isGipuzkoa( today ) );
		assertFalse( icc2.isBizkaia( today ) );
		assertFalse( icc2.isNavarra( today ) );
	}
	
	@Test
	void test_tbai_araba_from_sif() {
		resetAndGetIcc();
		Date today = AonDateUtils.today();
		Date lastMonthFirstDay = AonDateUtils.getMonthFirstDay( AonDateUtils.addMonths( today, -1 ));
		CommunicationData toEnable = getCD(Administration.NAVARRA).setStartDate(lastMonthFirstDay);
		InvoiceCommunicationConfiguration icc = ICCDAO.enableSif( getCtx(),getDomainId(), toEnable);
		printIcc(icc);
		assertTrue( icc.isNavarra( today ) );
		assertTrue( icc.isSif( today ) );
		
		assertFalse( icc.isVerifactu( today ) );
		assertFalse( icc.isLroe( today ) );
		assertFalse( icc.isNoSif( today ) );
		assertFalse( icc.isTbai( today ) );
		assertFalse( icc.isSii( today ) );
		
		assertFalse( icc.isCanarias( today ) );
		assertFalse( icc.isBizkaia( today ) );
		assertFalse( icc.isAraba( today ) );
		assertFalse( icc.isAEAT( today ) );
		
		CommunicationData toEnableTbai = getCD(Administration.ALAVA).setStartDate(today);
		InvoiceCommunicationConfiguration icc2 = ICCDAO.enableTbai( getCtx(),getDomainId(), toEnableTbai);
		printIcc(icc2);
		assertTrue( icc2.isAraba( today ) );
		assertTrue( icc2.isTbai( today ) );
		assertFalse( icc2.isNavarra( today ) );
		assertFalse( icc2.isSif( today ) );
		
		assertTrue( icc2.isNavarra( lastMonthFirstDay ) );
		assertTrue( icc2.isSif( lastMonthFirstDay ) );

		assertFalse( icc2.isVerifactu( today ) );
		assertFalse( icc2.isLroe( today ) );
		assertFalse( icc2.isNoSif( today ) );
		assertFalse( icc2.isSii( today ) );
		
		assertFalse( icc2.isCanarias( today ) );
		assertFalse( icc2.isBizkaia( today ) );
		assertFalse( icc2.isAEAT( today ) );
		
	}
	
	@Test
	void test_tbai_from_lroe() {
		resetAndGetIcc();
		Date today = AonDateUtils.today();
		Date lastMonthFirstDay = AonDateUtils.getMonthFirstDay( AonDateUtils.addMonths( today, -1 ));
		CommunicationData toEnable = getCD(Administration.BIZKAIA).setStartDate(lastMonthFirstDay);
		InvoiceCommunicationConfiguration icc = ICCDAO.enableLroe(getCtx(),getDomainId(), toEnable);
		printIcc(icc);
		assertTrue( icc.isBizkaia(today ) );
		assertTrue( icc.isLroe( today ) );
		
		assertFalse( icc.isVerifactu( today ) );
		assertFalse( icc.isNoVerifactu( today ) );
		assertFalse( icc.isTbai( today ) );
		assertFalse( icc.isNoSif( today ) );
		assertFalse( icc.isSii( today ) );
		assertFalse( icc.isSif( today ) );
		
		assertFalse( icc.isCanarias( today ) );
		assertFalse( icc.isAraba( today ) );
		assertFalse( icc.isNavarra( today ) );
		assertFalse( icc.isAEAT( today ) );
		
		CommunicationData toEnableTbai = getCD(Administration.ALAVA).setStartDate(today);
		InvoiceCommunicationConfiguration icc2 = ICCDAO.enableTbai( getCtx(),getDomainId(), toEnableTbai);
		printIcc(icc2);
		assertTrue( icc2.isAraba( today ) );
		assertTrue( icc2.isTbai( today ) );
		assertFalse( icc2.isBizkaia( today ) );
		assertFalse( icc2.isLroe( today ) );
		
		assertTrue( icc2.isBizkaia( lastMonthFirstDay ) );
		assertTrue( icc2.isLroe( lastMonthFirstDay ) );

		assertFalse( icc2.isVerifactu( today ) );
		assertFalse( icc2.isSif( today ) );
		assertFalse( icc2.isNoSif( today ) );
		assertFalse( icc2.isSii( today ) );
		
		assertFalse( icc2.isCanarias( today ) );
		assertFalse( icc2.isNavarra( today ) );
		assertFalse( icc2.isAEAT( today ) );
	}
	
	// ---------------------------------------------------------------
	// --------------------------------------------- [GIPUZKOA - TBAI]
	// ---------------------------------------------------------------
	
	@Test
	void test_enable_tbai_gipuzkoa_from_nothing_past() {
		resetAndGetIcc();
		Date today = AonDateUtils.today();
		Date lastMonthFirstDay = AonDateUtils.getMonthFirstDay( AonDateUtils.addMonths( today, -1 ));
		CommunicationData toEnable = getCD(Administration.GIPUZKOA).setStartDate(lastMonthFirstDay);
		InvoiceCommunicationConfiguration icc = ICCDAO.enableTbai( getCtx(),getDomainId(), toEnable);
		printIcc(icc);
		assertTrue( icc.isGipuzkoa( today ) );
		assertTrue( icc.isTbai( today ) );
		
		assertFalse( icc.isNoVerifactu( today ) );
		assertFalse( icc.isNoSif( today ) );
		assertFalse( icc.isSif( today ) );
		assertFalse( icc.isLroe( today ) );
		assertFalse( icc.isSif( today ) );
		assertFalse( icc.isSii( today ) );
		
		assertFalse( icc.isCanarias( today ) );
		assertFalse( icc.isAEAT( today ) );
		assertFalse( icc.isBizkaia( today ) );
		assertFalse( icc.isNavarra( today ) );
		assertFalse( icc.isAraba( today ) );
	}
	
	@Test
	void test_enable_tbai_sii_gipuzkoa_from_nothing_past() {
		resetAndGetIcc();
		Date today = AonDateUtils.today();
		Date lastMonthFirstDay = AonDateUtils.getMonthFirstDay( AonDateUtils.addMonths( today, -1 ));
		CommunicationData toEnable = getCD(Administration.GIPUZKOA).setStartDate(lastMonthFirstDay);
		InvoiceCommunicationConfiguration icc = ICCDAO.enableTbai( getCtx(),getDomainId(), toEnable);
		printIcc(icc);
		assertTrue( icc.isGipuzkoa( today ) );
		assertTrue( icc.isTbai( today ) );
		
		assertFalse( icc.isNoVerifactu( today ) );
		assertFalse( icc.isNoSif( today ) );
		assertFalse( icc.isSif( today ) );
		assertFalse( icc.isLroe( today ) );
		assertFalse( icc.isSii( today ) );
		
		assertFalse( icc.isCanarias( today ) );
		assertFalse( icc.isAEAT( today ) );
		assertFalse( icc.isBizkaia( today ) );
		assertFalse( icc.isNavarra( today ) );
		assertFalse( icc.isAraba( today ) );
		
		CommunicationData toEnableSii = getCD(Administration.GIPUZKOA).setStartDate(lastMonthFirstDay);
		InvoiceCommunicationConfiguration icc2 = ICCDAO.enableSii( getCtx(),getDomainId(), toEnableSii);
		printIcc(icc2);
		assertTrue( icc2.isGipuzkoa( today ) );
		assertTrue( icc2.isTbai( today ) );
		assertTrue( icc2.isSii( today ) );
		
		assertFalse( icc2.isNoVerifactu( today ) );
		assertFalse( icc2.isNoSif( today ) );
		assertFalse( icc2.isSif( today ) );
		assertFalse( icc2.isLroe( today ) );
		
		assertFalse( icc2.isCanarias( today ) );
		assertFalse( icc2.isAEAT( today ) );
		assertFalse( icc2.isBizkaia( today ) );
		assertFalse( icc2.isNavarra( today ) );
		assertFalse( icc2.isAraba( today ) );
	}
	
	@Test
	void test_tbai_gipuzkoa_from_no_sif() {
		resetAndGetIcc();
		Date today = AonDateUtils.today();
		Date lastMonthFirstDay = AonDateUtils.getMonthFirstDay( AonDateUtils.addMonths( today, -1 ));
		CommunicationData toEnable = getCD(Administration.COMMON_TERRITORY).setStartDate(lastMonthFirstDay);
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
		
		
		CommunicationData toEnableTbai = getCD(Administration.GIPUZKOA).setStartDate(today);
		InvoiceCommunicationConfiguration icc2 = ICCDAO.enableTbai( getCtx(),getDomainId(), toEnableTbai);
		printIcc(icc2);
		assertTrue( icc2.isGipuzkoa( today ) );
		assertTrue( icc2.isTbai( today ) );
		
		assertTrue( icc2.isAEAT( lastMonthFirstDay ) );
		assertTrue( icc2.isNoSif( lastMonthFirstDay ) );
		
		assertFalse( icc2.isNoVerifactu( today ) );
		assertFalse( icc2.isNoSif( today ) );
		assertFalse( icc2.isSif( today ) );
		assertFalse( icc2.isLroe( today ) );
		assertFalse( icc2.isSii( today ) );
		
		assertFalse( icc2.isCanarias( today ) );
		assertFalse( icc2.isAEAT( today ) );
		assertFalse( icc2.isAraba( today ) );
		assertFalse( icc2.isBizkaia( today ) );
		assertFalse( icc2.isNavarra( today ) );
	}
}
