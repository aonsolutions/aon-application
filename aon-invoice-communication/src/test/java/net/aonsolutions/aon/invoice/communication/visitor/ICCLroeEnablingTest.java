package net.aonsolutions.aon.invoice.communication.visitor;
 
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.model.invoice.CommunicationData;
import com.esferalia.aon.occam.api.model.invoice.CommunicationData.ExemptType;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationConfiguration;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationError;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.impl.jooq.dao.ICCDAO;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceCommunicationDAO;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;


class ICCLroeEnablingTest extends ICCAbstractEnablingTest {

	@Test
	void test_enable_lroe_from_nothing_past() {
		resetAndGetIcc();
		Date today = AonDateUtils.today();
		Date lastMonthFirstDay = AonDateUtils.getMonthFirstDay( AonDateUtils.addMonths( today, -1 ));
		InvoiceCommunicationConfiguration icc = ICCDAO.enableLroe( getCtx(),getDomainId(), lastMonthFirstDay );
		printIcc(icc);
		assertTrue( icc.isBizkaia( today ) );
		assertTrue( icc.isLroe( today ) );
		
		assertFalse( icc.isNoVerifactu( today ) );
		assertFalse( icc.isNoSif( today ) );
		assertFalse( icc.isSif( today ) );
		assertFalse( icc.isTbai( today ) );
		assertFalse( icc.isSif( today ) );
		assertFalse( icc.isSii( today ) );
		
		assertFalse( icc.isCanarias( today ) );
		assertFalse( icc.isAEAT( today ) );
		assertFalse( icc.isAraba( today ) );
		assertFalse( icc.isNavarra( today ) );
		assertFalse( icc.isUnknown( today ) );
	}

	@Test
	void test_enable_lroe_from_nothing_past_exempt() {
		resetAndGetIcc();
		Date today = AonDateUtils.today();
		Date lastMonthFirstDay = AonDateUtils.getMonthFirstDay( AonDateUtils.addMonths( today, -1 ));
		InvoiceCommunicationConfiguration icc = ICCDAO.enableLroe( getCtx(),getDomainId(), lastMonthFirstDay , ExemptType.NO_OBLIGATION );
		printIcc(icc);
		assertTrue( icc.isBizkaia( today ) );
		assertTrue( icc.isLroe( today ) );
		assertTrue( icc.getLroeData().isPresent() );
		CommunicationData data = icc.getLroeData().get();
		assertEquals( ExemptType.NO_OBLIGATION, data.getExemptType() );
		
		assertFalse( icc.isNoVerifactu( today ) );
		assertFalse( icc.isNoSif( today ) );
		assertFalse( icc.isSif( today ) );
		assertFalse( icc.isTbai( today ) );
		assertFalse( icc.isSif( today ) );
		assertFalse( icc.isSii( today ) );
		
		assertFalse( icc.isCanarias( today ) );
		assertFalse( icc.isAEAT( today ) );
		assertFalse( icc.isAraba( today ) );
		assertFalse( icc.isNavarra( today ) );
		assertFalse( icc.isUnknown( today ) );
	}

	@Test
	void test_enable_lroe_from_nothing_past_test_exempt() {
		resetAndGetIcc();
		Date today = AonDateUtils.today();
		Date lastMonthFirstDay = AonDateUtils.getMonthFirstDay( AonDateUtils.addMonths( today, -1 ));
		InvoiceCommunicationConfiguration icc = ICCDAO.enableLroeTest( getCtx(),getDomainId(), lastMonthFirstDay , ExemptType.NO_OBLIGATION );
		printIcc(icc);
		assertTrue( icc.isBizkaia( today ) );
		assertTrue( icc.isLroe( today ) );
		assertTrue( icc.getLroeData().isPresent() );
		CommunicationData data = icc.getLroeData().get();
		assertTrue( data.isTest() );
		assertEquals( ExemptType.NO_OBLIGATION, data.getExemptType() );
		
		assertFalse( icc.isNoVerifactu( today ) );
		assertFalse( icc.isNoSif( today ) );
		assertFalse( icc.isSif( today ) );
		assertFalse( icc.isTbai( today ) );
		assertFalse( icc.isSif( today ) );
		assertFalse( icc.isSii( today ) );
		
		assertFalse( icc.isCanarias( today ) );
		assertFalse( icc.isAEAT( today ) );
		assertFalse( icc.isAraba( today ) );
		assertFalse( icc.isNavarra( today ) );
		assertFalse( icc.isUnknown( today ) );
	}

	@Test
	void test_enable_verifactu_from_nothing_today() {
		resetAndGetIcc();
		Date today = AonDateUtils.today();
		InvoiceCommunicationConfiguration icc = ICCDAO.enableLroe( getCtx(),getDomainId(), today );
		printIcc(icc);
		assertTrue( icc.isBizkaia( today ) );
		assertTrue( icc.isLroe( today ) );
		
		assertFalse( icc.isNoVerifactu( today ) );
		assertFalse( icc.isNoSif( today ) );
		assertFalse( icc.isSif( today ) );
		assertFalse( icc.isTbai( today ) );
		assertFalse( icc.isSif( today ) );
		assertFalse( icc.isSii( today ) );
		
		assertFalse( icc.isCanarias( today ) );
		assertFalse( icc.isAEAT( today ) );
		assertFalse( icc.isAraba( today ) );
		assertFalse( icc.isNavarra( today ) );
		assertFalse( icc.isUnknown( today ) );
	}

	@Test
	void test_lroe_from_no_sif() {
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
		
		InvoiceCommunicationConfiguration icc2 = ICCDAO.enableLroe( getCtx(),getDomainId(), today );
		printIcc(icc2);
		assertTrue( icc2.isBizkaia( today ) );
		assertTrue( icc2.isLroe( today ) );
		
		assertTrue( icc2.isAEAT( lastMonthFirstDay ) );
		assertTrue( icc2.isNoSif( lastMonthFirstDay ) );
		
		assertFalse( icc2.isNoVerifactu( today ) );
		assertFalse( icc2.isNoSif( today ) );
		assertFalse( icc2.isSif( today ) );
		assertFalse( icc2.isTbai( today ) );
		assertFalse( icc2.isSif( today ) );
		assertFalse( icc2.isSii( today ) );
		
		assertFalse( icc2.isCanarias( today ) );
		assertFalse( icc2.isAEAT( today ) );
		assertFalse( icc2.isAraba( today ) );
		assertFalse( icc2.isNavarra( today ) );
		assertFalse( icc2.isUnknown( today ) );
	}
	
	@Test
	void test_lroe_from_verifctu() {
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
		
		AonCoreException e = assertThrows(AonCoreException.class,  () -> ICCDAO.enableLroe( getCtx(),getDomainId(), today ) );
		assertEquals( InvoiceCommunicationError.ICC_5011.getMessage(), e.getMessage() );
	}

	@Test
	void test_lroe_from_no_verifctu() {
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
		
		AonCoreException e = assertThrows(AonCoreException.class,  () -> ICCDAO.enableLroe( getCtx(),getDomainId(), today ) );
		assertEquals( InvoiceCommunicationError.ICC_5012.getMessage(), e.getMessage() );
	}
	
	@Test
	void test_lroe_from_sii() {
		resetAndGetIcc();
		Date today = AonDateUtils.today();
		Date lastMonthFirstDay = AonDateUtils.getMonthFirstDay( AonDateUtils.addMonths( today, -1 ));
		InvoiceCommunicationConfiguration icc = ICCDAO.enableSii( getCtx(),getDomainId(), lastMonthFirstDay );
		printIcc(icc);
		assertTrue( icc.isAEAT( today ) );
		assertTrue( icc.isSii( today ) );
		
		assertFalse( icc.isVerifactu( today ) );
		assertFalse( icc.isNoVerifactu( today ) );
		assertFalse( icc.isLroe( today ) );
		assertFalse( icc.isNoSif( today ) );
		assertFalse( icc.isSif( today ) );
		assertFalse( icc.isTbai( today ) );
		assertFalse( icc.isSif( today ) );
		
		assertFalse( icc.isCanarias( today ) );
		assertFalse( icc.isBizkaia( today ) );
		assertFalse( icc.isAraba( today ) );
		assertFalse( icc.isNavarra( today ) );
		assertFalse( icc.isUnknown( today ) );
		
		AonCoreException e = assertThrows(AonCoreException.class,  () -> ICCDAO.enableLroe( getCtx(),getDomainId(), today ) );
		assertEquals( InvoiceCommunicationError.ICC_5014.getMessage(), e.getMessage() );
	}
	
	@Test
	void test_lroe_from_sif() {
		resetAndGetIcc();
		Date today = AonDateUtils.today();
		Date lastMonthFirstDay = AonDateUtils.getMonthFirstDay( AonDateUtils.addMonths( today, -1 ));
		InvoiceCommunicationConfiguration icc = ICCDAO.enableSif( getCtx(),getDomainId(), Administration.NAVARRA, lastMonthFirstDay );
		printIcc(icc);
		assertTrue( icc.isNavarra( today ) );
		assertTrue( icc.isSif( today ) );
		
		assertFalse( icc.isVerifactu( today ) );
		assertFalse( icc.isNoVerifactu( today ) );
		assertFalse( icc.isLroe( today ) );
		assertFalse( icc.isNoSif( today ) );
		assertFalse( icc.isSii( today ) );
		assertFalse( icc.isTbai( today ) );
		
		assertFalse( icc.isCanarias( today ) );
		assertFalse( icc.isBizkaia( today ) );
		assertFalse( icc.isAraba( today ) );
		assertFalse( icc.isAEAT( today ) );
		assertFalse( icc.isUnknown( today ) );
		
		InvoiceCommunicationConfiguration icc2 = ICCDAO.enableLroe( getCtx(),getDomainId(), today );
		printIcc(icc2);
		assertTrue( icc2.isBizkaia( today ) );
		assertTrue( icc2.isLroe( today ) );
		
		assertTrue( icc2.isNavarra( lastMonthFirstDay ) );
		assertTrue( icc2.isSif( lastMonthFirstDay ) );
		
		assertFalse( icc2.isNoVerifactu( today ) );
		assertFalse( icc2.isNoSif( today ) );
		assertFalse( icc2.isSif( today ) );
		assertFalse( icc2.isTbai( today ) );
		assertFalse( icc2.isSii( today ) );
		
		assertFalse( icc2.isCanarias( today ) );
		assertFalse( icc2.isAEAT( today ) );
		assertFalse( icc2.isGipuzkoa( today ) );
		assertFalse( icc2.isAraba( today ) );
		assertFalse( icc2.isNavarra( today ) );
		assertFalse( icc2.isUnknown( today ) );
	}

	@Test
	void test_lroe_from_tbai() {
		resetAndGetIcc();
		Date today = AonDateUtils.today();
		Date lastMonthFirstDay = AonDateUtils.getMonthFirstDay( AonDateUtils.addMonths( today, -1 ));
		InvoiceCommunicationConfiguration icc = ICCDAO.enableTbaiAraba(getCtx(),getDomainId(), lastMonthFirstDay );
		printIcc(icc);
		assertTrue( icc.isAraba( today ) );
		assertTrue( icc.isTbai( today ) );
		
		assertFalse( icc.isVerifactu( today ) );
		assertFalse( icc.isNoVerifactu( today ) );
		assertFalse( icc.isLroe( today ) );
		assertFalse( icc.isNoSif( today ) );
		assertFalse( icc.isSii( today ) );
		assertFalse( icc.isSif( today ) );
		
		assertFalse( icc.isCanarias( today ) );
		assertFalse( icc.isBizkaia( today ) );
		assertFalse( icc.isNavarra( today ) );
		assertFalse( icc.isAEAT( today ) );
		assertFalse( icc.isUnknown( today ) );
		
		AonCoreException e = assertThrows(AonCoreException.class,  () -> ICCDAO.enableLroe( getCtx(),getDomainId(), today ) );
		assertEquals( InvoiceCommunicationError.ICC_5015.getMessage(), e.getMessage() );
	}
}
