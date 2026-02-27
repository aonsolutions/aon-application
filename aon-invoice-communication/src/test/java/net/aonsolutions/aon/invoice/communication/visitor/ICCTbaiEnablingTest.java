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


class ICCTbaiEnablingTest extends ICCAbstractEnablingTest {

	// ARABA - TBAI
	@Test
	void test_enable_tbai_araba_from_nothing_past() {
		resetAndGetIcc();
		Date today = AonDateUtils.today();
		Date lastMonthFirstDay = AonDateUtils.getMonthFirstDay( AonDateUtils.addMonths( today, -1 ));
		InvoiceCommunicationConfiguration icc = ICCDAO.enableTbaiAraba( getCtx(),getDomainId(), lastMonthFirstDay );
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
		assertFalse( icc.isUnknown( today ) );
		assertFalse( icc.isGipuzkoa( today ) );
	}
	
	@Test
	void test_enable_tbai_sii_araba_from_nothing_past() {
		resetAndGetIcc();
		Date today = AonDateUtils.today();
		Date lastMonthFirstDay = AonDateUtils.getMonthFirstDay( AonDateUtils.addMonths( today, -1 ));
		InvoiceCommunicationConfiguration icc = ICCDAO.enableTbaiAraba( getCtx(),getDomainId(), lastMonthFirstDay );
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
		assertFalse( icc.isUnknown( today ) );
		assertFalse( icc.isGipuzkoa( today ) );
		
		icc = ICCDAO.enableSiiAraba( getCtx(),getDomainId(), lastMonthFirstDay );
		printIcc(icc);
		assertTrue( icc.isAraba( today ) );
		assertTrue( icc.isTbai( today ) );
		assertTrue( icc.isSii( today ) );
		
		assertFalse( icc.isNoVerifactu( today ) );
		assertFalse( icc.isNoSif( today ) );
		assertFalse( icc.isSif( today ) );
		assertFalse( icc.isLroe( today ) );
		
		assertFalse( icc.isCanarias( today ) );
		assertFalse( icc.isAEAT( today ) );
		assertFalse( icc.isBizkaia( today ) );
		assertFalse( icc.isNavarra( today ) );
		assertFalse( icc.isUnknown( today ) );
		assertFalse( icc.isGipuzkoa( today ) );
	}

	@Test
	void test_tbai_araba_from_no_sif() {
		resetAndGetIcc();
		Date today = AonDateUtils.today();
		Date lastMonthFirstDay = AonDateUtils.getMonthFirstDay( AonDateUtils.addMonths( today, -1 ));
		InvoiceCommunicationConfiguration icc = ICCDAO.enableNoSif( getCtx(),getDomainId(), Administration.COMMON_TERRITORY, lastMonthFirstDay );
		printIcc(icc);
		assertTrue( icc.isAEAT( today ) );
		assertTrue( icc.isNoSif( today ) );
		
		assertFalse( icc.isCanarias( today ) );
		assertFalse( icc.isBizkaia( today ) );
		assertFalse( icc.isAraba( today ) );
		assertFalse( icc.isNavarra( today ) );
		assertFalse( icc.isUnknown( today ) );
		
		assertFalse( icc.isNoVerifactu( today ) );
		assertFalse( icc.isSif( today ) );
		assertFalse( icc.isTbai( today ) );
		assertFalse( icc.isLroe( today ) );
		assertFalse( icc.isSii( today ) );
		assertFalse( icc.isGipuzkoa( today ) );
		
		InvoiceCommunicationConfiguration icc2 = ICCDAO.enableTbaiAraba( getCtx(),getDomainId(), today );
		printIcc(icc2);
		assertTrue( icc2.isAraba( today ) );
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
		assertFalse( icc2.isGipuzkoa( today ) );
		assertFalse( icc2.isBizkaia( today ) );
		assertFalse( icc2.isNavarra( today ) );
		assertFalse( icc2.isUnknown( today ) );
	}
	
	@Test
	void test_tbai_araba_from_verifctu() {
		resetAndGetIcc();
		Date today = AonDateUtils.today();
		Date lastMonthFirstDay = AonDateUtils.getMonthFirstDay( AonDateUtils.addMonths( today, -1 ));
		InvoiceCommunicationConfiguration icc = ICCDAO.enableVerifactu( getCtx(),getDomainId(), lastMonthFirstDay );
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
		assertFalse( icc.isUnknown( today ) );
		
		AonCoreException e = assertThrows(AonCoreException.class,  () -> ICCDAO.enableTbaiAraba( getCtx(),getDomainId(), today ) );
		assertEquals( InvoiceCommunicationError.ICC_5007.getMessage(), e.getMessage() );
	}

	@Test
	void test_tbai_araba_from_no_verifctu() {
		resetAndGetIcc();
		Date today = AonDateUtils.today();
		Date lastMonthFirstDay = AonDateUtils.getMonthFirstDay( AonDateUtils.addMonths( today, -1 ));
		InvoiceCommunicationConfiguration icc = ICCDAO.enableNoVerifactu( getCtx(),getDomainId(), lastMonthFirstDay );
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
		assertFalse( icc.isUnknown( today ) );
		
		AonCoreException e = assertThrows(AonCoreException.class,  () -> ICCDAO.enableTbaiAraba( getCtx(),getDomainId(), today ) );
		assertEquals( InvoiceCommunicationError.ICC_5008.getMessage(), e.getMessage() );
	}
	
	@Test
	void test_tbai_araba_from_sif() {
		resetAndGetIcc();
		Date today = AonDateUtils.today();
		Date lastMonthFirstDay = AonDateUtils.getMonthFirstDay( AonDateUtils.addMonths( today, -1 ));
		InvoiceCommunicationConfiguration icc = ICCDAO.enableSif( getCtx(),getDomainId(), Administration.NAVARRA, lastMonthFirstDay );
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
		assertFalse( icc.isUnknown( today ) );
		
		InvoiceCommunicationConfiguration icc2 = ICCDAO.enableTbaiAraba( getCtx(),getDomainId(), today );
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
		assertFalse( icc2.isUnknown( today ) );
		
	}
	
	@Test
	void test_tbai_from_lroe() {
		resetAndGetIcc();
		Date today = AonDateUtils.today();
		Date lastMonthFirstDay = AonDateUtils.getMonthFirstDay( AonDateUtils.addMonths( today, -1 ));
		InvoiceCommunicationConfiguration icc = ICCDAO.enableLroe(getCtx(),getDomainId(), lastMonthFirstDay );
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
		assertFalse( icc.isUnknown( today ) );
		
		AonCoreException e = assertThrows(AonCoreException.class,  () -> ICCDAO.enableTbaiAraba( getCtx(),getDomainId(), today ) );
		assertEquals( InvoiceCommunicationError.ICC_5010.getMessage(), e.getMessage() );
	}
	
	// GIPUZKOA - TBAI
	@Test
	void test_enable_tbai_gipuzkoa_from_nothing_past() {
		resetAndGetIcc();
		Date today = AonDateUtils.today();
		Date lastMonthFirstDay = AonDateUtils.getMonthFirstDay( AonDateUtils.addMonths( today, -1 ));
		InvoiceCommunicationConfiguration icc = ICCDAO.enableTbaiGipuzkoa( getCtx(),getDomainId(), lastMonthFirstDay );
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
		assertFalse( icc.isUnknown( today ) );
		assertFalse( icc.isAraba( today ) );
	}
	
	@Test
	void test_enable_tbai_sii_gipuzkoa_from_nothing_past() {
		resetAndGetIcc();
		Date today = AonDateUtils.today();
		Date lastMonthFirstDay = AonDateUtils.getMonthFirstDay( AonDateUtils.addMonths( today, -1 ));
		InvoiceCommunicationConfiguration icc = ICCDAO.enableTbaiGipuzkoa( getCtx(),getDomainId(), lastMonthFirstDay );
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
		assertFalse( icc.isUnknown( today ) );
		assertFalse( icc.isAraba( today ) );
		
		icc = ICCDAO.enableSiiGipuzkoa( getCtx(),getDomainId(), lastMonthFirstDay );
		printIcc(icc);
		assertTrue( icc.isGipuzkoa( today ) );
		assertTrue( icc.isTbai( today ) );
		assertTrue( icc.isSii( today ) );
		
		assertFalse( icc.isNoVerifactu( today ) );
		assertFalse( icc.isNoSif( today ) );
		assertFalse( icc.isSif( today ) );
		assertFalse( icc.isLroe( today ) );
		
		assertFalse( icc.isCanarias( today ) );
		assertFalse( icc.isAEAT( today ) );
		assertFalse( icc.isBizkaia( today ) );
		assertFalse( icc.isNavarra( today ) );
		assertFalse( icc.isUnknown( today ) );
		assertFalse( icc.isAraba( today ) );
	}
	
	@Test
	void test_tbai_gipuzkoa_from_no_sif() {
		resetAndGetIcc();
		Date today = AonDateUtils.today();
		Date lastMonthFirstDay = AonDateUtils.getMonthFirstDay( AonDateUtils.addMonths( today, -1 ));
		InvoiceCommunicationConfiguration icc = ICCDAO.enableNoSif( getCtx(),getDomainId(), Administration.COMMON_TERRITORY, lastMonthFirstDay );
		printIcc(icc);
		assertTrue( icc.isAEAT( today ) );
		assertTrue( icc.isNoSif( today ) );
		
		assertFalse( icc.isCanarias( today ) );
		assertFalse( icc.isBizkaia( today ) );
		assertFalse( icc.isAraba( today ) );
		assertFalse( icc.isNavarra( today ) );
		assertFalse( icc.isUnknown( today ) );
		
		assertFalse( icc.isNoVerifactu( today ) );
		assertFalse( icc.isSif( today ) );
		assertFalse( icc.isTbai( today ) );
		assertFalse( icc.isLroe( today ) );
		assertFalse( icc.isSii( today ) );
		assertFalse( icc.isGipuzkoa( today ) );
		
		InvoiceCommunicationConfiguration icc2 = ICCDAO.enableTbaiGipuzkoa( getCtx(),getDomainId(), today );
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
		assertFalse( icc2.isUnknown( today ) );
	}
	
}
