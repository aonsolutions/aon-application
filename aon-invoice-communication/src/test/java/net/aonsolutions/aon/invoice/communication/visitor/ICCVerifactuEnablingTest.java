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


class ICCVerifactuEnablingTest extends ICCAbstractEnablingTest {
	@Test
	void test_enable_verifactu_from_nothing_past() {
		resetAndGetIcc();
		Date today = AonDateUtils.today();
		Date lastMonthFirstDay = AonDateUtils.getMonthFirstDay( AonDateUtils.addMonths( today, -1 ));
		CommunicationData toEnable = getCD(Administration.COMMON_TERRITORY).setStartDate(lastMonthFirstDay);
		InvoiceCommunicationConfiguration icc = ICCDAO.enableVerifactu( getCtx(),getDomainId(), toEnable);
		printIcc(icc);
		assertTrue( icc.isAEAT( today ) );
		assertTrue( icc.isVerifactu( today ) );
		
		assertFalse( icc.isCanarias( today ) );
		assertFalse( icc.isNoVerifactu( today ) );
		assertFalse( icc.isNoSif( today ) );
		assertFalse( icc.isTbai( today ) );
		assertFalse( icc.isLroe( today ) );
		assertFalse( icc.isSii( today ) );
		
	}

	@Test
	void test_enable_verifactu_from_nothing_today() {
		resetAndGetIcc();
		Date today = AonDateUtils.today();
		CommunicationData toEnable = getCD(Administration.COMMON_TERRITORY).setStartDate(today);
		InvoiceCommunicationConfiguration icc = ICCDAO.enableVerifactu( getCtx(),getDomainId(), toEnable);
		printIcc(icc);
		assertTrue( icc.isAEAT( today ) );
		assertTrue( icc.isVerifactu( today ) );
		
		assertFalse( icc.isCanarias( today ) );
		assertFalse( icc.isNoVerifactu( today ) );
		assertFalse( icc.isNoSif( today ) );
		assertFalse( icc.isTbai( today ) );
		assertFalse( icc.isLroe( today ) );
		assertFalse( icc.isSii( today ) );
		
	}

	@Test
	void test_verifactu_common_territory_to_canarias() {
		resetAndGetIcc();
		Date today = AonDateUtils.today();
		Date lastMonthFirstDay = AonDateUtils.getMonthFirstDay( AonDateUtils.addMonths( today, -1 ));
		CommunicationData toEnable = getCD(Administration.COMMON_TERRITORY).setStartDate(lastMonthFirstDay);
		InvoiceCommunicationConfiguration icc = ICCDAO.enableVerifactu( getCtx(),getDomainId(), toEnable);
		printIcc(icc);
		assertTrue( icc.isAEAT( today ) );
		assertTrue( icc.isVerifactu( today ) );
		
		assertFalse( icc.isCanarias( today ) );
		assertFalse( icc.isNoVerifactu( today ) );
		assertFalse( icc.isNoSif( today ) );
		assertFalse( icc.isTbai( today ) );
		assertFalse( icc.isLroe( today ) );
		assertFalse( icc.isSii( today ) );
		
		CommunicationData toEnableCanarias = getCD(Administration.CANARIAS).setStartDate(today);
		InvoiceCommunicationConfiguration icc2 = ICCDAO.enableVerifactu( getCtx(),getDomainId(), toEnableCanarias );
		printIcc(icc2);
		assertTrue( icc2.isCanarias( today ) );
		assertTrue( icc2.isVerifactu( today ) );

		assertFalse( icc2.isAEAT( today ) );
		assertFalse( icc2.isNoVerifactu( today ) );
		assertFalse( icc2.isNoSif( today ) );
		assertFalse( icc2.isTbai( today ) );
		assertFalse( icc2.isLroe( today ) );
		assertFalse( icc2.isSii( today ) );
	}
	
	@Test
	void test_enable_verifactu_from_sii_today() {
		resetAndGetIcc();
		Date today = AonDateUtils.today();
		Date lastMonthFirstDay = AonDateUtils.getMonthFirstDay( AonDateUtils.addMonths( today, -1 ));
		CommunicationData toEnable = getCD(Administration.COMMON_TERRITORY).setStartDate(lastMonthFirstDay);
		InvoiceCommunicationConfiguration icc = ICCDAO.enableSii( getCtx(),getDomainId(), toEnable);
		printIcc(icc);
		assertTrue( icc.isAEAT( today ) );
		assertTrue( icc.isSii( today ) );
		
		assertFalse( icc.isVerifactu( today ) );
		assertFalse( icc.isCanarias( today ) );
		assertFalse( icc.isNoVerifactu( today ) );
		assertFalse( icc.isNoSif( today ) );
		assertFalse( icc.isTbai( today ) );
		assertFalse( icc.isLroe( today ) );
		
		CommunicationData toEnableVerifactu = getCD(Administration.COMMON_TERRITORY).setStartDate(today);
		AonCoreException e = assertThrows(AonCoreException.class,  () -> ICCDAO.enableVerifactu( getCtx(),getDomainId(), toEnableVerifactu ) );
		assertEquals( InvoiceCommunicationError.ICC_6000.getMessage(), e.getMessage() );
	}
	
	@Test
	void test_enable_verifactu_from_sii_next_year() {
		resetAndGetIcc();
		Date today = AonDateUtils.today();
		Date lastMonthFirstDay = AonDateUtils.getMonthFirstDay( AonDateUtils.addMonths( today, -1 ));
		Date nextYearFirstDay = AonDateUtils.getYearFirstDay(AonDateUtils.getCurrentYear() + 1);
		CommunicationData toEnable = getCD(Administration.COMMON_TERRITORY).setStartDate(lastMonthFirstDay);
		InvoiceCommunicationConfiguration icc = ICCDAO.enableSii( getCtx(),getDomainId(), toEnable);
		printIcc(icc);
		assertTrue( icc.isAEAT( today ) );
		assertTrue( icc.isSii( today ) );
		
		assertFalse( icc.isVerifactu( today ) );
		assertFalse( icc.isCanarias( today ) );
		assertFalse( icc.isNoVerifactu( today ) );
		assertFalse( icc.isNoSif( today ) );
		assertFalse( icc.isTbai( today ) );
		assertFalse( icc.isLroe( today ) );

		CommunicationData toEnableVerifactu = getCD(Administration.COMMON_TERRITORY).setStartDate(nextYearFirstDay);
		InvoiceCommunicationConfiguration icc2 = ICCDAO.enableVerifactu( getCtx(),getDomainId(), toEnableVerifactu);
		printIcc(icc2);
		assertTrue( icc2.isAEAT( nextYearFirstDay ) );
		assertTrue( icc2.isVerifactu( nextYearFirstDay ) );
		assertTrue( icc2.isSii( today ) );
		assertFalse( icc2.isSii( nextYearFirstDay ) );

		assertFalse( icc2.isNoVerifactu( nextYearFirstDay ) );
		assertFalse( icc2.isNoSif( nextYearFirstDay ) );
		assertFalse( icc2.isTbai( nextYearFirstDay ) );
		assertFalse( icc2.isLroe( nextYearFirstDay ) );
	}

	@Test
	void test_enable_verifactu_from_no_verifactu_today() {
		resetAndGetIcc();
		Date today = AonDateUtils.today();
		Date lastMonthFirstDay = AonDateUtils.getMonthFirstDay( AonDateUtils.addMonths( today, -1 ));
		CommunicationData toEnable = getCD(Administration.COMMON_TERRITORY).setStartDate(lastMonthFirstDay);
		InvoiceCommunicationConfiguration icc = ICCDAO.enableNoVerifactu( getCtx(),getDomainId(), toEnable);
		printIcc(icc);
		assertTrue( icc.isAEAT( today ) );
		assertTrue( icc.isNoVerifactu( today ) );
		
		assertFalse( icc.isSii( today ) );
		assertFalse( icc.isVerifactu( today ) );
		assertFalse( icc.isCanarias( today ) );
		assertFalse( icc.isNoSif( today ) );
		assertFalse( icc.isTbai( today ) );
		assertFalse( icc.isLroe( today ) );
		
		CommunicationData toEnableVerifactu = getCD(Administration.COMMON_TERRITORY).setStartDate(today);
		InvoiceCommunicationConfiguration icc2 = ICCDAO.enableVerifactu( getCtx(),getDomainId(), toEnableVerifactu);
		printIcc(icc2);
		assertTrue( icc2.isAEAT( today ) );
		assertTrue( icc2.isVerifactu( today ) );
		assertTrue( icc2.isNoVerifactu( lastMonthFirstDay ) );
		assertFalse( icc2.isNoVerifactu( today ) );

		assertFalse( icc2.isSii( today ) );
		assertFalse( icc2.isNoSif( today ) );
		assertFalse( icc2.isTbai( today ) );
		assertFalse( icc2.isLroe( today ) );
	}
	
	@Test
	void test_enable_verifactu_from_no_verifactu_next_year() {
		resetAndGetIcc();
		Date today = AonDateUtils.today();
		Date lastMonthFirstDay = AonDateUtils.getMonthFirstDay( AonDateUtils.addMonths( today, -1 ));
		Date nextYearFirstDay = AonDateUtils.getYearFirstDay(AonDateUtils.getCurrentYear() + 1);
		CommunicationData toEnable = getCD(Administration.COMMON_TERRITORY).setStartDate(lastMonthFirstDay);
		InvoiceCommunicationConfiguration icc = ICCDAO.enableNoVerifactu( getCtx(),getDomainId(), toEnable);
		printIcc(icc);
		assertTrue( icc.isAEAT( today ) );
		assertTrue( icc.isNoVerifactu( today ) );
		
		assertFalse( icc.isSii( today ) );
		assertFalse( icc.isVerifactu( today ) );
		assertFalse( icc.isCanarias( today ) );
		assertFalse( icc.isNoSif( today ) );
		assertFalse( icc.isTbai( today ) );
		assertFalse( icc.isLroe( today ) );
		
		CommunicationData toEnableVerifactu = getCD(Administration.COMMON_TERRITORY).setStartDate(nextYearFirstDay);
		InvoiceCommunicationConfiguration icc2 = ICCDAO.enableVerifactu( getCtx(),getDomainId(), toEnableVerifactu);
		printIcc(icc2);
		assertTrue( icc2.isAEAT( nextYearFirstDay ) );
		assertTrue( icc2.isVerifactu( nextYearFirstDay ) );
		assertTrue( icc2.isNoVerifactu( today ) );

		assertFalse( icc2.isNoVerifactu( nextYearFirstDay ) );
		assertFalse( icc2.isSii( nextYearFirstDay ) );
		assertFalse( icc2.isNoSif( nextYearFirstDay ) );
		assertFalse( icc2.isTbai( nextYearFirstDay ) );
		assertFalse( icc2.isLroe( nextYearFirstDay ) );
		
		assertFalse( icc2.isCanarias( nextYearFirstDay ) );
		assertFalse( icc2.isBizkaia( nextYearFirstDay ) );
		assertFalse( icc2.isAraba( nextYearFirstDay ) );
		assertFalse( icc2.isNavarra( nextYearFirstDay ) );
	}

	@Test
	void test_enable_verifactu_from_lroe() {
		resetAndGetIcc();
		Date today = AonDateUtils.today();
		Date yesterday = AonDateUtils.yesterday();
		Date lastMonthFirstDay = AonDateUtils.getMonthFirstDay( AonDateUtils.addMonths( today, -1 ));
		CommunicationData toEnable = getCD(Administration.BIZKAIA).setStartDate(lastMonthFirstDay);
		InvoiceCommunicationConfiguration icc = ICCDAO.enableLroe( getCtx(),getDomainId(), toEnable);
		printIcc(icc);
		assertTrue( icc.isLroe( today ) );
		assertTrue( icc.isBizkaia( today ) );
		assertFalse( icc.isAEAT( today ) );
		
		assertFalse( icc.isCanarias( today ) );
		assertFalse( icc.isAEAT( today ) );
		assertFalse( icc.isAraba( today ) );
		assertFalse( icc.isGipuzkoa( today ) );
		assertFalse( icc.isNavarra( today ) );
		
		assertFalse( icc.isSii( today ) );
		assertFalse( icc.isVerifactu( today ) );
		assertFalse( icc.isNoVerifactu( today ) );
		assertFalse( icc.isNoSif( today ) );
		assertFalse( icc.isTbai( today ) );
		
		CommunicationData toEnableVerifactu = getCD(Administration.COMMON_TERRITORY).setStartDate(today);
		InvoiceCommunicationConfiguration icc2 = ICCDAO.enableVerifactu( getCtx(),getDomainId(), toEnableVerifactu);
		assertFalse( icc2.isLroe( today ) );
		assertFalse( icc2.isBizkaia( today ) );
		assertTrue( icc2.isAEAT( today ) );
		assertTrue( icc2.isVerifactu( today ) );
		assertTrue( icc2.isLroe( yesterday ) );
		assertTrue( icc2.isBizkaia( yesterday ) );
		
		assertFalse( icc2.isCanarias( today ) );
		assertFalse( icc2.isAraba( today ) );
		assertFalse( icc2.isGipuzkoa( today ) );
		assertFalse( icc2.isNavarra( today ) );
		
		assertFalse( icc2.isSii( today ) );
		assertFalse( icc2.isNoVerifactu( today ) );
		assertFalse( icc2.isNoSif( today ) );
		assertFalse( icc2.isTbai( today ) );
		
	}
	
	@Test
	void test_enable_verifactu_from_tbai_araba() {
		resetAndGetIcc();
		Date today = AonDateUtils.today();
		Date yesterday = AonDateUtils.yesterday();
		Date lastMonthFirstDay = AonDateUtils.getMonthFirstDay( AonDateUtils.addMonths( today, -1 ));
		CommunicationData toEnable = getCD(Administration.ALAVA).setStartDate(lastMonthFirstDay);
		InvoiceCommunicationConfiguration icc = ICCDAO.enableTbai( getCtx(),getDomainId(), toEnable);
		printIcc(icc);
		assertTrue( icc.isTbai( today ) );
		assertTrue( icc.isAraba( today ) );
		
		assertFalse( icc.isCanarias( today ) );
		assertFalse( icc.isAEAT( today ) );
		assertFalse( icc.isBizkaia( today ) );
		assertFalse( icc.isGipuzkoa( today ) );
		assertFalse( icc.isNavarra( today ) );
		
		assertFalse( icc.isSii( today ) );
		assertFalse( icc.isVerifactu( today ) );
		assertFalse( icc.isNoVerifactu( today ) );
		assertFalse( icc.isNoSif( today ) );
		assertFalse( icc.isLroe( today ) );
		
		CommunicationData toEnableVerifactu = getCD(Administration.COMMON_TERRITORY).setStartDate(today);
		InvoiceCommunicationConfiguration icc2 = ICCDAO.enableVerifactu( getCtx(),getDomainId(), toEnableVerifactu);
		assertFalse( icc2.isTbai( today ) );
		assertFalse( icc2.isAraba( today ) );
		assertTrue( icc2.isAEAT( today ) );
		assertTrue( icc2.isVerifactu( today ) );
		assertTrue( icc2.isTbai( yesterday ) );
		assertTrue( icc2.isAraba( yesterday ) );
		
		assertFalse( icc2.isCanarias( today ) );
		assertFalse( icc2.isBizkaia( today ) );
		assertFalse( icc2.isGipuzkoa( today ) );
		assertFalse( icc2.isNavarra( today ) );
		
		assertFalse( icc2.isSii( today ) );
		assertFalse( icc2.isNoVerifactu( today ) );
		assertFalse( icc2.isNoSif( today ) );
		assertFalse( icc2.isLroe( today ) );
	}
	
	@Test
	void test_enable_verifactu_from_tbai_gipuzkoa() {
		resetAndGetIcc();
		Date today = AonDateUtils.today();
		Date yesterday = AonDateUtils.yesterday();
		Date lastMonthFirstDay = AonDateUtils.getMonthFirstDay( AonDateUtils.addMonths( today, -1 ));
		CommunicationData toEnable = getCD(Administration.GIPUZKOA).setStartDate(lastMonthFirstDay);
		InvoiceCommunicationConfiguration icc = ICCDAO.enableTbai( getCtx(),getDomainId(), toEnable);
		printIcc(icc);
		assertTrue( icc.isTbai( today ) );
		assertTrue( icc.isGipuzkoa( today ) );
		
		assertFalse( icc.isCanarias( today ) );
		assertFalse( icc.isAEAT( today ) );
		assertFalse( icc.isBizkaia( today ) );
		assertFalse( icc.isAraba( today ) );
		assertFalse( icc.isNavarra( today ) );
		
		assertFalse( icc.isSii( today ) );
		assertFalse( icc.isVerifactu( today ) );
		assertFalse( icc.isNoVerifactu( today ) );
		assertFalse( icc.isNoSif( today ) );
		assertFalse( icc.isLroe( today ) );
		
		CommunicationData toEnableVerifactu = getCD(Administration.COMMON_TERRITORY).setStartDate(today);
		InvoiceCommunicationConfiguration icc2 = ICCDAO.enableVerifactu( getCtx(),getDomainId(), toEnableVerifactu);
		assertFalse( icc2.isTbai( today ) );
		assertFalse( icc2.isGipuzkoa( today ) );
		assertTrue( icc2.isAEAT( today ) );
		assertTrue( icc2.isVerifactu( today ) );
		assertTrue( icc2.isTbai( yesterday ) );
		assertTrue( icc2.isGipuzkoa( yesterday ) );
		
		assertFalse( icc2.isCanarias( today ) );
		assertFalse( icc2.isBizkaia( today ) );
		assertFalse( icc2.isAraba( today ) );
		assertFalse( icc2.isNavarra( today ) );
		
		assertFalse( icc2.isSii( today ) );
		assertFalse( icc2.isNoVerifactu( today ) );
		assertFalse( icc2.isNoSif( today ) );
		assertFalse( icc2.isLroe( today ) );
	}
}
