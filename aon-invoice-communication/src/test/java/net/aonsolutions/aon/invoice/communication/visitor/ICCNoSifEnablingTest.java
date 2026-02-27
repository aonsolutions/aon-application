package net.aonsolutions.aon.invoice.communication.visitor;
 
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationConfiguration;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.impl.jooq.dao.ICCDAO;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceCommunicationDAO;
import com.esferalia.aon.watson.server.AonDateUtils;


class ICCNoSifEnablingTest extends ICCAbstractEnablingTest {

	@Test
	void test_enable_no_sif_from_nothing_past() {
		resetAndGetIcc();
		Date today = AonDateUtils.today();
		Date lastMonthFirstDay = AonDateUtils.getMonthFirstDay( AonDateUtils.addMonths( today, -1 ));
		InvoiceCommunicationConfiguration icc = ICCDAO.enableNoSif( getCtx(),getDomainId(), Administration.COMMON_TERRITORY, lastMonthFirstDay );
		printIcc(icc);
		assertTrue( icc.isAEAT( today ) );
		assertTrue( icc.isNoSif( today ) );
		
		assertFalse( icc.isCanarias( today ) );
		assertFalse( icc.isNoVerifactu( today ) );
		assertFalse( icc.isVerifactu( today ) );
		assertFalse( icc.isTbai( today ) );
		assertFalse( icc.isLroe( today ) );
		assertFalse( icc.isSii( today ) );
	}

	@Test
	void test_enable_verifactu_from_nothing_today() {
		resetAndGetIcc();
		Date today = AonDateUtils.today();
		InvoiceCommunicationConfiguration icc = ICCDAO.enableNoSif( getCtx(),getDomainId(), Administration.COMMON_TERRITORY, today );
		printIcc(icc);
		assertTrue( icc.isAEAT( today ) );
		assertTrue( icc.isNoSif( today ) );
		
		assertFalse( icc.isCanarias( today ) );
		assertFalse( icc.isNoVerifactu( today ) );
		assertFalse( icc.isVerifactu( today ) );
		assertFalse( icc.isTbai( today ) );
		assertFalse( icc.isLroe( today ) );
		assertFalse( icc.isSii( today ) );
		
	}
	
	@Test
	void test_enable_no_sif_from_sii_today() {
		resetAndGetIcc();
		Date today = AonDateUtils.today();
		Date lastMonthFirstDay = AonDateUtils.getMonthFirstDay( AonDateUtils.addMonths( today, -1 ));
		InvoiceCommunicationConfiguration icc = ICCDAO.enableSii( getCtx(),getDomainId(), lastMonthFirstDay );
		printIcc(icc);
		assertTrue( icc.isSii( today ) );
		assertTrue( icc.isAEAT( today ) );
		
		assertFalse( icc.isCanarias( today ) );
		assertFalse( icc.isBizkaia( today ) );
		assertFalse( icc.isAraba( today ) );
		assertFalse( icc.isNavarra( today ) );
		assertFalse( icc.isUnknown( today ) );
		
		assertFalse( icc.isNoSif( today ) );
		assertFalse( icc.isSif( today ) );
		assertFalse( icc.isVerifactu( today ) );
		assertFalse( icc.isNoVerifactu( today ) );
		assertFalse( icc.isTbai( today ) );
		assertFalse( icc.isLroe( today ) );

		InvoiceCommunicationConfiguration icc2 = ICCDAO.enableNoSif( getCtx(),getDomainId(), Administration.COMMON_TERRITORY, today );
		printIcc(icc2);
		assertTrue( icc2.isAEAT( today ) );
		assertTrue( icc2.isNoSif( today ) );
		assertTrue( icc2.isSii( today ) );
		
		assertTrue( icc2.isAEAT( lastMonthFirstDay ) );
		assertTrue( icc2.isSii( lastMonthFirstDay ) );

		assertFalse( icc2.isSif( today ) );
		assertFalse( icc2.isVerifactu( today ) );
		assertFalse( icc2.isNoVerifactu( today ) );
		assertFalse( icc2.isTbai( today ) );
		assertFalse( icc2.isLroe( today ) );
		
		assertFalse( icc2.isCanarias( today ) );
		assertFalse( icc2.isBizkaia( today ) );
		assertFalse( icc2.isAraba( today ) );
		assertFalse( icc2.isNavarra( today ) );
		assertFalse( icc2.isUnknown( today ) );
	}

	@Test
	void test_enable_no_sif_from_verifactu_today() {
		resetAndGetIcc();
		Date today = AonDateUtils.today();
		Date lastMonthFirstDay = AonDateUtils.getMonthFirstDay( AonDateUtils.addMonths( today, -1 ));
		InvoiceCommunicationConfiguration icc = ICCDAO.enableVerifactu( getCtx(),getDomainId(), lastMonthFirstDay );
		printIcc(icc);
		assertTrue( icc.isVerifactu( today ) );
		assertTrue( icc.isAEAT( today ) );
		
		assertFalse( icc.isCanarias( today ) );
		assertFalse( icc.isBizkaia( today ) );
		assertFalse( icc.isAraba( today ) );
		assertFalse( icc.isNavarra( today ) );
		assertFalse( icc.isUnknown( today ) );
		
		assertFalse( icc.isNoSif( today ) );
		assertFalse( icc.isSif( today ) );
		assertFalse( icc.isSii( today ) );
		assertFalse( icc.isNoVerifactu( today ) );
		assertFalse( icc.isTbai( today ) );
		assertFalse( icc.isLroe( today ) );

		InvoiceCommunicationConfiguration icc2 = ICCDAO.enableNoSif( getCtx(),getDomainId(), Administration.COMMON_TERRITORY, today );
		printIcc(icc2);
		assertTrue( icc2.isAEAT( today ) );
		assertTrue( icc2.isNoSif( today ) );
		assertFalse( icc2.isVerifactu( today ) );
		
		assertTrue( icc2.isAEAT( lastMonthFirstDay ) );
		assertTrue( icc2.isVerifactu( lastMonthFirstDay ) );

		assertFalse( icc2.isSii( today ) );
		assertFalse( icc2.isSif( today ) );
		assertFalse( icc2.isNoVerifactu( today ) );
		assertFalse( icc2.isTbai( today ) );
		assertFalse( icc2.isLroe( today ) );
		
		assertFalse( icc2.isCanarias( today ) );
		assertFalse( icc2.isBizkaia( today ) );
		assertFalse( icc2.isAraba( today ) );
		assertFalse( icc2.isNavarra( today ) );
		assertFalse( icc2.isUnknown( today ) );
	}

	@Test
	void test_enable_no_sif_from_no_verifactu_today() {
		resetAndGetIcc();
		Date today = AonDateUtils.today();
		Date lastMonthFirstDay = AonDateUtils.getMonthFirstDay( AonDateUtils.addMonths( today, -1 ));
		InvoiceCommunicationConfiguration icc = ICCDAO.enableNoVerifactu( getCtx(),getDomainId(), lastMonthFirstDay );
		printIcc(icc);
		assertTrue( icc.isNoVerifactu( today ) );
		assertTrue( icc.isAEAT( today ) );
		
		assertFalse( icc.isCanarias( today ) );
		assertFalse( icc.isBizkaia( today ) );
		assertFalse( icc.isAraba( today ) );
		assertFalse( icc.isNavarra( today ) );
		assertFalse( icc.isUnknown( today ) );
		
		assertFalse( icc.isNoSif( today ) );
		assertFalse( icc.isSif( today ) );
		assertFalse( icc.isSii( today ) );
		assertFalse( icc.isVerifactu( today ) );
		assertFalse( icc.isTbai( today ) );
		assertFalse( icc.isLroe( today ) );

		InvoiceCommunicationConfiguration icc2 = ICCDAO.enableNoSif( getCtx(),getDomainId(), Administration.COMMON_TERRITORY, today );
		printIcc(icc2);
		assertTrue( icc2.isAEAT( today ) );
		assertTrue( icc2.isNoSif( today ) );
		assertFalse( icc2.isNoVerifactu( today ) );
		
		assertTrue( icc2.isAEAT( lastMonthFirstDay ) );
		assertTrue( icc2.isNoVerifactu( lastMonthFirstDay ) );

		assertFalse( icc2.isSii( today ) );
		assertFalse( icc2.isSif( today ) );
		assertFalse( icc2.isVerifactu( today ) );
		assertFalse( icc2.isTbai( today ) );
		assertFalse( icc2.isLroe( today ) );
		
		assertFalse( icc2.isCanarias( today ) );
		assertFalse( icc2.isBizkaia( today ) );
		assertFalse( icc2.isAraba( today ) );
		assertFalse( icc2.isNavarra( today ) );
		assertFalse( icc2.isUnknown( today ) );

	}

	@Test
	void test_enable_no_sif_from_sif_today() {
		resetAndGetIcc();
		Date today = AonDateUtils.today();
		Date lastMonthFirstDay = AonDateUtils.getMonthFirstDay( AonDateUtils.addMonths( today, -1 ));
		InvoiceCommunicationConfiguration icc = ICCDAO.enableSif( getCtx(),getDomainId(), Administration.NAVARRA, lastMonthFirstDay );
		printIcc(icc);
		assertTrue( icc.isSif( today ) );
		assertTrue( icc.isNavarra( today ) );
		
		assertFalse( icc.isCanarias( today ) );
		assertFalse( icc.isBizkaia( today ) );
		assertFalse( icc.isAraba( today ) );
		assertFalse( icc.isAEAT( today ) );
		assertFalse( icc.isUnknown( today ) );
		
		assertFalse( icc.isNoSif( today ) );
		assertFalse( icc.isSii( today ) );
		assertFalse( icc.isNoVerifactu( today ) );
		assertFalse( icc.isVerifactu( today ) );
		assertFalse( icc.isTbai( today ) );
		assertFalse( icc.isLroe( today ) );

		InvoiceCommunicationConfiguration icc2 = ICCDAO.enableNoSif( getCtx(),getDomainId(), Administration.COMMON_TERRITORY, today );
		printIcc(icc2);
		assertTrue( icc2.isAEAT( today ) );
		assertTrue( icc2.isNoSif( today ) );
		assertFalse( icc2.isSif( today ) );
		
		assertTrue( icc2.isNavarra( lastMonthFirstDay ) );
		assertTrue( icc2.isSif( lastMonthFirstDay ) );

		assertFalse( icc2.isSii( today ) );
		assertFalse( icc2.isNoVerifactu( today ) );
		assertFalse( icc2.isVerifactu( today ) );
		assertFalse( icc2.isTbai( today ) );
		assertFalse( icc2.isLroe( today ) );
		
		assertFalse( icc2.isCanarias( today ) );
		assertFalse( icc2.isBizkaia( today ) );
		assertFalse( icc2.isAraba( today ) );
		assertFalse( icc2.isNavarra( today ) );
		assertFalse( icc2.isUnknown( today ) );
	}

	@Test
	void test_enable_no_sif_from_lroe_today() {
		resetAndGetIcc();
		Date today = AonDateUtils.today();
		Date lastMonthFirstDay = AonDateUtils.getMonthFirstDay( AonDateUtils.addMonths( today, -1 ));
		InvoiceCommunicationConfiguration icc = ICCDAO.enableLroe( getCtx(),getDomainId(), lastMonthFirstDay );
		printIcc(icc);
		assertTrue( icc.isLroe( today ) );
		assertTrue( icc.isBizkaia( today ) );
		
		assertFalse( icc.isCanarias( today ) );
		assertFalse( icc.isAEAT( today ) );
		assertFalse( icc.isAraba( today ) );
		assertFalse( icc.isNavarra( today ) );
		assertFalse( icc.isUnknown( today ) );
		
		assertFalse( icc.isNoSif( today ) );
		assertFalse( icc.isSii( today ) );
		assertFalse( icc.isNoVerifactu( today ) );
		assertFalse( icc.isVerifactu( today ) );
		assertFalse( icc.isTbai( today ) );
		assertFalse( icc.isSif( today ) );

		InvoiceCommunicationConfiguration icc2 = ICCDAO.enableNoSif( getCtx(),getDomainId(), Administration.BIZKAIA, today );
		printIcc(icc2);
		assertTrue( icc2.isBizkaia( today ) );
		assertTrue( icc2.isNoSif( today ) );
		assertTrue( icc2.isLroe( today ) );
		
		assertTrue( icc2.isBizkaia( lastMonthFirstDay ) );
		assertTrue( icc2.isLroe( lastMonthFirstDay ) );

		assertFalse( icc2.isSii( today ) );
		assertFalse( icc2.isNoVerifactu( today ) );
		assertFalse( icc2.isVerifactu( today ) );
		assertFalse( icc2.isTbai( today ) );
		assertFalse( icc2.isSif( today ) );
		
		assertFalse( icc2.isCanarias( today ) );
		assertFalse( icc2.isAraba( today ) );
		assertFalse( icc2.isNavarra( today ) );
		assertFalse( icc2.isUnknown( today ) );
	}

	@Test
	void test_enable_no_sif_from_tbai_araba_today() {
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
		assertFalse( icc.isNavarra( today ) );
		assertFalse( icc.isUnknown( today ) );
		
		assertFalse( icc.isNoSif( today ) );
		assertFalse( icc.isSii( today ) );
		assertFalse( icc.isNoVerifactu( today ) );
		assertFalse( icc.isVerifactu( today ) );
		assertFalse( icc.isLroe( today ) );
		assertFalse( icc.isSif( today ) );

		InvoiceCommunicationConfiguration icc2 = ICCDAO.enableNoSif( getCtx(),getDomainId(), Administration.COMMON_TERRITORY, today );
		printIcc(icc2);
		assertTrue( icc2.isAEAT( today ) );
		assertTrue( icc2.isNoSif( today ) );
		assertFalse( icc2.isTbai( today ) );
		
		assertTrue( icc2.isAraba( lastMonthFirstDay ) );
		assertTrue( icc2.isTbai( lastMonthFirstDay ) );

		assertFalse( icc2.isSii( today ) );
		assertFalse( icc2.isNoVerifactu( today ) );
		assertFalse( icc2.isVerifactu( today ) );
		assertFalse( icc2.isTbai( today ) );
		assertFalse( icc2.isSif( today ) );
		assertFalse( icc2.isLroe( today ) );
		
		assertFalse( icc2.isCanarias( today ) );
		assertFalse( icc2.isBizkaia( today ) );
		assertFalse( icc2.isAraba( today ) );
		assertFalse( icc2.isNavarra( today ) );
		assertFalse( icc2.isUnknown( today ) );
	}
}
