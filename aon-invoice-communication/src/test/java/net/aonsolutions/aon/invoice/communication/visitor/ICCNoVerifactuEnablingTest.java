package net.aonsolutions.aon.invoice.communication.visitor;
 
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationConfiguration;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationError;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.impl.jooq.dao.ICCDAO;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceCommunicationDAO;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;


class ICCNoVerifactuEnablingTest extends ICCAbstractEnablingTest {

	@Test
	void test_enable_no_verifactu_from_nothing_past() {
		resetAndGetIcc();
		Date today = AonDateUtils.today();
		Date lastMonthFirstDay = AonDateUtils.getMonthFirstDay( AonDateUtils.addMonths( today, -1 ));
		InvoiceCommunicationConfiguration icc = ICCDAO.enableNoVerifactu( getCtx(),getDomainId(), lastMonthFirstDay );
		printIcc(icc);
		assertTrue( icc.isAEAT( today ) );
		assertTrue( icc.isNoVerifactu( today ) );
		
		assertFalse( icc.isCanarias( today ) );
		assertFalse( icc.isBizkaia( today ) );
		assertFalse( icc.isAraba( today ) );
		assertFalse( icc.isNavarra( today ) );
		assertFalse( icc.isUnknown( today ) );

		assertFalse( icc.isVerifactu( today ) );
		assertFalse( icc.isNoSif( today ) );
		assertFalse( icc.isTbai( today ) );
		assertFalse( icc.isLroe( today ) );
		assertFalse( icc.isSii( today ) );
		
	}
	
	@Test
	void test_enable_no_verifactu_canarias_from_nothing_past() {
		resetAndGetIcc();
		Date today = AonDateUtils.today();
		Date lastMonthFirstDay = AonDateUtils.getMonthFirstDay( AonDateUtils.addMonths( today, -1 ));
		InvoiceCommunicationConfiguration icc = ICCDAO.enableNoVerifactuCanarias( getCtx(),getDomainId(), lastMonthFirstDay );
		printIcc(icc);
		assertTrue( icc.isCanarias( today ) );
		assertTrue( icc.isNoVerifactu( today ) );
		
		assertFalse( icc.isAEAT( today ) );
		assertFalse( icc.isBizkaia( today ) );
		assertFalse( icc.isAraba( today ) );
		assertFalse( icc.isNavarra( today ) );
		assertFalse( icc.isUnknown( today ) );

		assertFalse( icc.isVerifactu( today ) );
		assertFalse( icc.isNoSif( today ) );
		assertFalse( icc.isTbai( today ) );
		assertFalse( icc.isLroe( today ) );
		assertFalse( icc.isSii( today ) );
		
	}

	@Test
	void test_enable_no_verifactu_from_nothing_today() {
		resetAndGetIcc();
		Date today = AonDateUtils.today();
		InvoiceCommunicationConfiguration icc = ICCDAO.enableNoVerifactu( getCtx(),getDomainId(), today );
		printIcc(icc);
		assertTrue( icc.isAEAT( today ) );
		assertTrue( icc.isNoVerifactu( today ) );
		
		assertFalse( icc.isCanarias( today ) );
		assertFalse( icc.isBizkaia( today ) );
		assertFalse( icc.isAraba( today ) );
		assertFalse( icc.isNavarra( today ) );
		assertFalse( icc.isUnknown( today ) );

		assertFalse( icc.isVerifactu( today ) );
		assertFalse( icc.isNoSif( today ) );
		assertFalse( icc.isTbai( today ) );
		assertFalse( icc.isLroe( today ) );
		assertFalse( icc.isSii( today ) );
		
	}
	
	@Test
	void test_enable_no_verifactu_canarias_from_nothing_today() {
		resetAndGetIcc();
		Date today = AonDateUtils.today();
		InvoiceCommunicationConfiguration icc = ICCDAO.enableNoVerifactuCanarias( getCtx(),getDomainId(), today );
		printIcc(icc);
		assertTrue( icc.isCanarias( today ) );
		assertTrue( icc.isNoVerifactu( today ) );
		
		assertFalse( icc.isAEAT( today ) );
		assertFalse( icc.isBizkaia( today ) );
		assertFalse( icc.isAraba( today ) );
		assertFalse( icc.isNavarra( today ) );
		assertFalse( icc.isUnknown( today ) );

		assertFalse( icc.isVerifactu( today ) );
		assertFalse( icc.isNoSif( today ) );
		assertFalse( icc.isTbai( today ) );
		assertFalse( icc.isLroe( today ) );
		assertFalse( icc.isSii( today ) );
	}
	
	
	@Test
	void test_enable_no_verifactu_from_no_verifactu_canarias() {
		resetAndGetIcc();
		Date today = AonDateUtils.today();
		Date lastMonthFirstDay = AonDateUtils.getMonthFirstDay( AonDateUtils.addMonths( today, -1 ));
		InvoiceCommunicationConfiguration icc = ICCDAO.enableNoVerifactuCanarias( getCtx(),getDomainId(), lastMonthFirstDay );
		printIcc(icc);
		assertTrue( icc.isCanarias( today ) );
		assertTrue( icc.isNoVerifactu( today ) );
		
		assertFalse( icc.isAEAT( today ) );
		assertFalse( icc.isBizkaia( today ) );
		assertFalse( icc.isAraba( today ) );
		assertFalse( icc.isNavarra( today ) );
		assertFalse( icc.isUnknown( today ) );

		assertFalse( icc.isVerifactu( today ) );
		assertFalse( icc.isNoSif( today ) );
		assertFalse( icc.isTbai( today ) );
		assertFalse( icc.isLroe( today ) );
		assertFalse( icc.isSii( today ) );
		
		InvoiceCommunicationConfiguration icc2 = ICCDAO.enableNoVerifactu( getCtx(),getDomainId(), today );
		printIcc(icc2);
		assertTrue( icc2.isAEAT( today ) );
		assertTrue( icc2.isCanarias( lastMonthFirstDay ) );
		assertTrue( icc2.isNoVerifactu( today ) );
		
		assertFalse( icc2.isCanarias( today ) );
		assertFalse( icc2.isBizkaia( today ) );
		assertFalse( icc2.isAraba( today ) );
		assertFalse( icc2.isNavarra( today ) );
		assertFalse( icc2.isUnknown( today ) );

		assertFalse( icc2.isVerifactu( today ) );
		assertFalse( icc2.isNoSif( today ) );
		assertFalse( icc2.isTbai( today ) );
		assertFalse( icc2.isLroe( today ) );
		assertFalse( icc2.isSii( today ) );
		
	}

	@Test
	void test_enable_no_verifactu_canarias_from_no_verifactu() {
		resetAndGetIcc();
		Date today = AonDateUtils.today();
		Date lastMonthFirstDay = AonDateUtils.getMonthFirstDay( AonDateUtils.addMonths( today, -1 ));
		InvoiceCommunicationConfiguration icc = ICCDAO.enableNoVerifactu( getCtx(),getDomainId(), lastMonthFirstDay );
		printIcc(icc);
		assertTrue( icc.isAEAT( today ) );
		assertTrue( icc.isNoVerifactu( today ) );
		
		assertFalse( icc.isCanarias( today ) );
		assertFalse( icc.isBizkaia( today ) );
		assertFalse( icc.isAraba( today ) );
		assertFalse( icc.isNavarra( today ) );
		assertFalse( icc.isUnknown( today ) );

		assertFalse( icc.isVerifactu( today ) );
		assertFalse( icc.isNoSif( today ) );
		assertFalse( icc.isTbai( today ) );
		assertFalse( icc.isLroe( today ) );
		assertFalse( icc.isSii( today ) );
		
		InvoiceCommunicationConfiguration icc2 = ICCDAO.enableNoVerifactuCanarias( getCtx(),getDomainId(), today );
		printIcc(icc2);
		assertTrue( icc2.isCanarias( today ) );
		assertTrue( icc2.isAEAT( lastMonthFirstDay ) );
		assertTrue( icc2.isNoVerifactu( today ) );
		
		assertFalse( icc2.isAEAT( today ) );
		assertFalse( icc2.isBizkaia( today ) );
		assertFalse( icc2.isAraba( today ) );
		assertFalse( icc2.isNavarra( today ) );
		assertFalse( icc2.isUnknown( today ) );

		assertFalse( icc2.isVerifactu( today ) );
		assertFalse( icc2.isNoSif( today ) );
		assertFalse( icc2.isTbai( today ) );
		assertFalse( icc2.isLroe( today ) );
		assertFalse( icc2.isSii( today ) );
		
	}

	@Test
	void test_enable_no_verifactu_from_verifactu_today() {
		resetAndGetIcc();
		Date today = AonDateUtils.today();
		Date lastMonthFirstDay = AonDateUtils.getMonthFirstDay( AonDateUtils.addMonths( today, -1 ));
		InvoiceCommunicationConfiguration icc = ICCDAO.enableVerifactu( getCtx(),getDomainId(), lastMonthFirstDay );
		printIcc(icc);
		assertTrue( icc.isAEAT( today ) );
		assertTrue( icc.isVerifactu( today ) );
		
		assertFalse( icc.isCanarias( today ) );
		assertFalse( icc.isBizkaia( today ) );
		assertFalse( icc.isAraba( today ) );
		assertFalse( icc.isNavarra( today ) );
		assertFalse( icc.isUnknown( today ) );

		assertFalse( icc.isNoVerifactu( today ) );
		assertFalse( icc.isNoSif( today ) );
		assertFalse( icc.isTbai( today ) );
		assertFalse( icc.isLroe( today ) );
		assertFalse( icc.isSii( today ) );
		
		AonCoreException e = assertThrows(AonCoreException.class,  () -> ICCDAO.enableNoVerifactu( getCtx(),getDomainId(), today ) );
		assertEquals( InvoiceCommunicationError.ICC_6001.getMessage(), e.getMessage() );
		
	}
	
	@Test
	void test_enable_no_verifactu_from_verifactu_next_year() {
		resetAndGetIcc();
		Date today = AonDateUtils.today();
		Date lastMonthFirstDay = AonDateUtils.getMonthFirstDay( AonDateUtils.addMonths( today, -1 ));
		Date nextYearFirstDay = AonDateUtils.getYearFirstDay(AonDateUtils.getCurrentYear() + 1);
		InvoiceCommunicationConfiguration icc = ICCDAO.enableVerifactu( getCtx(),getDomainId(), lastMonthFirstDay );
		printIcc(icc);
		assertTrue( icc.isAEAT( today ) );
		assertTrue( icc.isVerifactu( today ) );
		
		assertFalse( icc.isCanarias( today ) );
		assertFalse( icc.isBizkaia( today ) );
		assertFalse( icc.isAraba( today ) );
		assertFalse( icc.isNavarra( today ) );
		assertFalse( icc.isUnknown( today ) );

		assertFalse( icc.isNoVerifactu( today ) );
		assertFalse( icc.isNoSif( today ) );
		assertFalse( icc.isTbai( today ) );
		assertFalse( icc.isLroe( today ) );
		assertFalse( icc.isSii( today ) );
		
		InvoiceCommunicationConfiguration icc2 = ICCDAO.enableNoVerifactu( getCtx(),getDomainId(), nextYearFirstDay );
		printIcc(icc2);
		assertTrue( icc2.isAEAT( today ) );
		assertTrue( icc2.isVerifactu( today ) );
		assertTrue( icc2.isNoVerifactu( nextYearFirstDay ) );
		assertFalse( icc2.isVerifactu( nextYearFirstDay ) );
		
		assertFalse( icc2.isCanarias( nextYearFirstDay ) );
		assertFalse( icc2.isBizkaia( nextYearFirstDay ) );
		assertFalse( icc2.isAraba( nextYearFirstDay ) );
		assertFalse( icc2.isNavarra( nextYearFirstDay ) );
		assertFalse( icc2.isUnknown( nextYearFirstDay ) );

		assertFalse( icc2.isNoSif( nextYearFirstDay ) );
		assertFalse( icc2.isTbai( nextYearFirstDay ) );
		assertFalse( icc2.isLroe( nextYearFirstDay ) );
		assertFalse( icc2.isSii( nextYearFirstDay ) );
		
		
	}

	@Test
	void test_enable_no_verifactu_from_sii_today() {
		resetAndGetIcc();
		Date today = AonDateUtils.today();
		Date lastMonthFirstDay = AonDateUtils.getMonthFirstDay( AonDateUtils.addMonths( today, -1 ));
		InvoiceCommunicationConfiguration icc = ICCDAO.enableSii( getCtx(),getDomainId(), lastMonthFirstDay );
		printIcc(icc);
		assertTrue( icc.isAEAT( today ) );
		assertTrue( icc.isSii( today ) );
		
		assertFalse( icc.isCanarias( today ) );
		assertFalse( icc.isBizkaia( today ) );
		assertFalse( icc.isAraba( today ) );
		assertFalse( icc.isNavarra( today ) );
		assertFalse( icc.isUnknown( today ) );

		assertFalse( icc.isNoVerifactu( today ) );
		assertFalse( icc.isNoSif( today ) );
		assertFalse( icc.isTbai( today ) );
		assertFalse( icc.isLroe( today ) );
		assertFalse( icc.isVerifactu( today ) );
		
		AonCoreException e = assertThrows(AonCoreException.class,  () -> ICCDAO.enableNoVerifactu( getCtx(),getDomainId(), today ) );
		assertEquals( InvoiceCommunicationError.ICC_6001.getMessage(), e.getMessage() );
		
	}

	
	@Test
	void test_enable_no_verifactu_from_sii_next_year() {
		resetAndGetIcc();
		Date today = AonDateUtils.today();
		Date lastMonthFirstDay = AonDateUtils.getMonthFirstDay( AonDateUtils.addMonths( today, -1 ));
		Date nextYearFirstDay = AonDateUtils.getYearFirstDay(AonDateUtils.getCurrentYear() + 1);
		InvoiceCommunicationConfiguration icc = ICCDAO.enableSii( getCtx(),getDomainId(), lastMonthFirstDay );
		printIcc(icc);
		assertTrue( icc.isAEAT( today ) );
		assertTrue( icc.isSii( today ) );
		
		assertFalse( icc.isCanarias( today ) );
		assertFalse( icc.isBizkaia( today ) );
		assertFalse( icc.isAraba( today ) );
		assertFalse( icc.isNavarra( today ) );
		assertFalse( icc.isUnknown( today ) );

		assertFalse( icc.isNoVerifactu( today ) );
		assertFalse( icc.isNoSif( today ) );
		assertFalse( icc.isTbai( today ) );
		assertFalse( icc.isLroe( today ) );
		assertFalse( icc.isVerifactu( today ) );
		
		InvoiceCommunicationConfiguration icc2 = ICCDAO.enableNoVerifactu( getCtx(),getDomainId(), nextYearFirstDay );
		printIcc(icc2);
		assertTrue( icc2.isAEAT( today ) );
		assertTrue( icc2.isSii( today ) );
		assertTrue( icc2.isNoVerifactu( nextYearFirstDay ) );
		assertFalse( icc2.isSii( nextYearFirstDay ) );
		
		assertFalse( icc2.isCanarias( nextYearFirstDay ) );
		assertFalse( icc2.isBizkaia( nextYearFirstDay ) );
		assertFalse( icc2.isAraba( nextYearFirstDay ) );
		assertFalse( icc2.isNavarra( nextYearFirstDay ) );
		assertFalse( icc2.isUnknown( nextYearFirstDay ) );

		assertFalse( icc2.isNoSif( nextYearFirstDay ) );
		assertFalse( icc2.isTbai( nextYearFirstDay ) );
		assertFalse( icc2.isLroe( nextYearFirstDay ) );
		assertFalse( icc2.isVerifactu( nextYearFirstDay ) );

	}
	
	@Test
	void test_enable_no_verifactu_from_lroe() {
		resetAndGetIcc();
		Date today = AonDateUtils.today();
		Date lastMonthFirstDay = AonDateUtils.getMonthFirstDay( AonDateUtils.addMonths( today, -1 ));
		InvoiceCommunicationConfiguration icc = ICCDAO.enableLroe( getCtx(),getDomainId(), lastMonthFirstDay );
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
		assertFalse( icc.isUnknown( today ) ); 
		assertFalse( icc.isNoVerifactu( today ) );
		assertFalse( icc.isNoSif( today ) );
		assertFalse( icc.isTbai( today ) );
		
		AonCoreException e = assertThrows(AonCoreException.class,  () -> ICCDAO.enableNoVerifactu( getCtx(),getDomainId(), today ) );
		assertEquals( InvoiceCommunicationError.ICC_5012.getMessage(), e.getMessage() );
	}
	

	@Test
	void test_enable_no_verifactu_from_tbai() {
		resetAndGetIcc();
		Date today = AonDateUtils.today();
		Date lastMonthFirstDay = AonDateUtils.getMonthFirstDay( AonDateUtils.addMonths( today, -1 ));
		InvoiceCommunicationConfiguration icc = ICCDAO.enableTbaiAraba( getCtx(),getDomainId(), lastMonthFirstDay );
		printIcc(icc);
		assertTrue( icc.isTbai( today ) );
		assertTrue( icc.isAraba( today ) );
		
		assertFalse( icc.isCanarias( today ) );
		assertFalse( icc.isAEAT( today ) );
		assertFalse( icc.isBizkaia( today ) );
		assertFalse( icc.isGipuzkoa( today ) );
		assertFalse( icc.isNavarra( today ) );
		assertFalse( icc.isUnknown( today ) );
		
		assertFalse( icc.isSii( today ) );
		assertFalse( icc.isVerifactu( today ) );
		assertFalse( icc.isNoVerifactu( today ) );
		assertFalse( icc.isNoSif( today ) );
		assertFalse( icc.isLroe( today ) );
		
		AonCoreException e = assertThrows(AonCoreException.class,  () -> ICCDAO.enableNoVerifactu( getCtx(),getDomainId(), today ) );
		assertEquals( InvoiceCommunicationError.ICC_5008.getMessage(), e.getMessage() );
	}
	
	@Test
	void test_enable_verifactu_from_no_sif_gipuzkoa() {
		resetAndGetIcc();
		Date today = AonDateUtils.today();
		Date lastMonthFirstDay = AonDateUtils.getMonthFirstDay( AonDateUtils.addMonths( today, -1 ));
		InvoiceCommunicationConfiguration icc = ICCDAO.enableNoSif( getCtx(), getDomainId(), Administration.GIPUZKOA, lastMonthFirstDay );
		printIcc(icc);
		assertTrue( icc.isNoSif( today ) );
		assertTrue( icc.isGipuzkoa( today ) );
		
		assertFalse( icc.isCanarias( today ) );
		assertFalse( icc.isAEAT( today ) );
		assertFalse( icc.isBizkaia( today ) );
		assertFalse( icc.isAraba( today ) );
		assertFalse( icc.isNavarra( today ) );
		assertFalse( icc.isUnknown( today ) );
		
		assertFalse( icc.isSii( today ) );
		assertFalse( icc.isSif( today ) );
		assertFalse( icc.isVerifactu( today ) );
		assertFalse( icc.isNoVerifactu( today ) );
		assertFalse( icc.isTbai( today ) );
		assertFalse( icc.isLroe( today ) );

		InvoiceCommunicationConfiguration icc2 = ICCDAO.enableNoVerifactu( getCtx(),getDomainId(), today );
		printIcc(icc2);
		assertTrue( icc2.isAEAT( today ) );
		assertTrue( icc2.isNoVerifactu( today ) );
		assertFalse( icc2.isNoSif( today ) );
		
		assertTrue( icc2.isGipuzkoa( lastMonthFirstDay ) );
		assertTrue( icc2.isNoSif( lastMonthFirstDay ) );

		assertFalse( icc2.isVerifactu( today ) );
		assertFalse( icc2.isSii( today ) );
		assertFalse( icc2.isTbai( today ) );
		assertFalse( icc2.isLroe( today ) );
		
		assertFalse( icc2.isCanarias( today ) );
		assertFalse( icc2.isBizkaia( today ) );
		assertFalse( icc2.isAraba( today ) );
		assertFalse( icc2.isNavarra( today ) );
		assertFalse( icc2.isUnknown( today ) );
		
	}
	
	@Test
	void test_enable_verifactu_from_sif_navarra() {
		resetAndGetIcc();
		Date today = AonDateUtils.today();
		Date lastMonthFirstDay = AonDateUtils.getMonthFirstDay( AonDateUtils.addMonths( today, -1 ));
		InvoiceCommunicationConfiguration icc = ICCDAO.enableSif( getCtx(), getDomainId(), Administration.NAVARRA, lastMonthFirstDay );
		printIcc(icc);
		assertTrue( icc.isSif( today ) );
		assertTrue( icc.isNavarra(today ) );
		
		assertFalse( icc.isCanarias( today ) );
		assertFalse( icc.isAEAT( today ) );
		assertFalse( icc.isBizkaia( today ) );
		assertFalse( icc.isAraba( today ) );
		assertFalse( icc.isGipuzkoa( today ) );
		assertFalse( icc.isUnknown( today ) );
		
		assertFalse( icc.isNoSif( today ) );
		assertFalse( icc.isSii( today ) );
		assertFalse( icc.isVerifactu( today ) );
		assertFalse( icc.isNoVerifactu( today ) );
		assertFalse( icc.isTbai( today ) );
		assertFalse( icc.isLroe( today ) );

		InvoiceCommunicationConfiguration icc2 = ICCDAO.enableNoVerifactu( getCtx(),getDomainId(), today );
		printIcc(icc2);
		assertTrue( icc2.isAEAT( today ) );
		assertTrue( icc2.isNoVerifactu( today ) );
		assertFalse( icc2.isSif( today ) );
		
		assertTrue( icc2.isNavarra( lastMonthFirstDay ) );
		assertTrue( icc2.isSif( lastMonthFirstDay ) );

		assertFalse( icc2.isVerifactu( today ) );
		assertFalse( icc2.isSii( today ) );
		assertFalse( icc2.isTbai( today ) );
		assertFalse( icc2.isLroe( today ) );
		
		assertFalse( icc2.isCanarias( today ) );
		assertFalse( icc2.isBizkaia( today ) );
		assertFalse( icc2.isAraba( today ) );
		assertFalse( icc2.isNavarra( today ) );
		assertFalse( icc2.isUnknown( today ) );
		
	}
}
