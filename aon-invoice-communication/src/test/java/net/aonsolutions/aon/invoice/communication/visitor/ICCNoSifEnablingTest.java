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


class ICCNoSifEnablingTest extends ICCAbstractEnablingTest {
	@Test
	void test_enable_no_sif_from_nothing_past() {
		resetAndGetIcc();
		Date today = AonDateUtils.today();
		Date lastMonthFirstDay = AonDateUtils.getMonthFirstDay( AonDateUtils.addMonths( today, -1 ));
		CommunicationData toEnable = getCD(Administration.COMMON_TERRITORY)
			.setDataName(EnterpriseDataNames.ICC_NO_SIF)
			.setStartDate(lastMonthFirstDay);
		InvoiceCommunicationConfiguration icc = ICCDAO.enableNoSif( getCtx(),getDomainId(), toEnable);
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
		CommunicationData toEnable = getCD(Administration.COMMON_TERRITORY)
				.setDataName(EnterpriseDataNames.ICC_NO_SIF)
				.setStartDate(today);
		InvoiceCommunicationConfiguration icc = ICCDAO.enableNoSif( getCtx(),getDomainId(), toEnable);
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
		CommunicationData toEnable = getCD(Administration.COMMON_TERRITORY)
				.setDataName(EnterpriseDataNames.ICC_SII)
				.setStartDate(lastMonthFirstDay);
		InvoiceCommunicationConfiguration icc = ICCDAO.enableSii( getCtx(),getDomainId(), toEnable);
		printIcc(icc);
		assertTrue( icc.isSii( today ) );
		assertTrue( icc.isAEAT( today ) );
		
		assertFalse( icc.isCanarias( today ) );
		assertFalse( icc.isBizkaia( today ) );
		assertFalse( icc.isAraba( today ) );
		assertFalse( icc.isNavarra( today ) );
		
		assertFalse( icc.isNoSif( today ) );
		assertFalse( icc.isSif( today ) );
		assertFalse( icc.isVerifactu( today ) );
		assertFalse( icc.isNoVerifactu( today ) );
		assertFalse( icc.isTbai( today ) );
		assertFalse( icc.isLroe( today ) );

		CommunicationData toEnable2 = getCD(Administration.COMMON_TERRITORY)
				.setDataName(EnterpriseDataNames.ICC_NO_SIF)
				.setStartDate(today);
		InvoiceCommunicationConfiguration icc2 = ICCDAO.enableNoSif( getCtx(),getDomainId(), toEnable2);
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
	}

	@Test
	void test_enable_no_sif_from_verifactu_today() {
		resetAndGetIcc();
		Date today = AonDateUtils.today();
		Date lastMonthFirstDay = AonDateUtils.getMonthFirstDay( AonDateUtils.addMonths( today, -1 ));
		CommunicationData toEnable = getCD(Administration.COMMON_TERRITORY)
				.setDataName(EnterpriseDataNames.ICC_VERIFACTU)
				.setStartDate(lastMonthFirstDay);
		InvoiceCommunicationConfiguration icc = ICCDAO.enableVerifactu( getCtx(),getDomainId(), toEnable);
		printIcc(icc);
		assertTrue( icc.isVerifactu( today ) );
		assertTrue( icc.isAEAT( today ) );
		
		assertFalse( icc.isCanarias( today ) );
		assertFalse( icc.isBizkaia( today ) );
		assertFalse( icc.isAraba( today ) );
		assertFalse( icc.isNavarra( today ) );
		
		assertFalse( icc.isNoSif( today ) );
		assertFalse( icc.isSif( today ) );
		assertFalse( icc.isSii( today ) );
		assertFalse( icc.isNoVerifactu( today ) );
		assertFalse( icc.isTbai( today ) );
		assertFalse( icc.isLroe( today ) );

		CommunicationData toEnable2 = getCD(Administration.COMMON_TERRITORY)
				.setDataName(EnterpriseDataNames.ICC_NO_SIF)
				.setStartDate(today);
		InvoiceCommunicationConfiguration icc2 = ICCDAO.enableNoSif( getCtx(),getDomainId(), toEnable2);
		printIcc(icc2);
		assertTrue( icc2.isAEAT( today ) );
		assertTrue( icc2.isNoSif( today ) );
		assertFalse( icc2.isVerifactu( today ) );
		
		assertFalse( icc2.isSii( today ) );
		assertFalse( icc2.isSif( today ) );
		assertFalse( icc2.isNoVerifactu( today ) );
		assertFalse( icc2.isTbai( today ) );
		assertFalse( icc2.isLroe( today ) );
		
		assertFalse( icc2.isCanarias( today ) );
		assertFalse( icc2.isBizkaia( today ) );
		assertFalse( icc2.isAraba( today ) );
		assertFalse( icc2.isNavarra( today ) );
	}

	@Test
	void test_enable_no_sif_from_no_verifactu_today() {
		resetAndGetIcc();
		Date today = AonDateUtils.today();
		Date lastMonthFirstDay = AonDateUtils.getMonthFirstDay( AonDateUtils.addMonths( today, -1 ));
		CommunicationData toEnable = getCD(Administration.COMMON_TERRITORY)
				.setDataName(EnterpriseDataNames.ICC_NO_VERIFACTU)
				.setStartDate(lastMonthFirstDay);
		InvoiceCommunicationConfiguration icc = ICCDAO.enableNoVerifactu( getCtx(),getDomainId(), toEnable);
		printIcc(icc);
		assertTrue( icc.isNoVerifactu( today ) );
		assertTrue( icc.isAEAT( today ) );
		
		assertFalse( icc.isCanarias( today ) );
		assertFalse( icc.isBizkaia( today ) );
		assertFalse( icc.isAraba( today ) );
		assertFalse( icc.isNavarra( today ) );
		
		assertFalse( icc.isNoSif( today ) );
		assertFalse( icc.isSif( today ) );
		assertFalse( icc.isSii( today ) );
		assertFalse( icc.isVerifactu( today ) );
		assertFalse( icc.isTbai( today ) );
		assertFalse( icc.isLroe( today ) );

		CommunicationData toEnable2 = getCD(Administration.COMMON_TERRITORY)
				.setDataName(EnterpriseDataNames.ICC_NO_SIF)
				.setStartDate(today);
		InvoiceCommunicationConfiguration icc2 = ICCDAO.enableNoSif( getCtx(),getDomainId(), toEnable2);
		printIcc(icc2);
		assertTrue( icc2.isAEAT( today ) );
		assertTrue( icc2.isNoSif( today ) );
		assertFalse( icc2.isNoVerifactu( today ) );
		
		assertFalse( icc2.isSii( today ) );
		assertFalse( icc2.isSif( today ) );
		assertFalse( icc2.isVerifactu( today ) );
		assertFalse( icc2.isTbai( today ) );
		assertFalse( icc2.isLroe( today ) );
		
		assertFalse( icc2.isCanarias( today ) );
		assertFalse( icc2.isBizkaia( today ) );
		assertFalse( icc2.isAraba( today ) );
		assertFalse( icc2.isNavarra( today ) );

	}

	@Test
	void test_enable_no_sif_from_sif_today() {
		resetAndGetIcc();
		Date today = AonDateUtils.today();
		Date lastMonthFirstDay = AonDateUtils.getMonthFirstDay( AonDateUtils.addMonths( today, -1 ));
		CommunicationData toEnable = getCD(Administration.NAVARRA)
				.setDataName(EnterpriseDataNames.ICC_SIF)
				.setStartDate(lastMonthFirstDay);
		InvoiceCommunicationConfiguration icc = ICCDAO.enableSif( getCtx(),getDomainId(), toEnable);
		printIcc(icc);
		assertTrue( icc.isSif( today ) );
		assertTrue( icc.isNavarra( today ) );
		
		assertFalse( icc.isCanarias( today ) );
		assertFalse( icc.isBizkaia( today ) );
		assertFalse( icc.isAraba( today ) );
		assertFalse( icc.isAEAT( today ) );
		
		assertFalse( icc.isNoSif( today ) );
		assertFalse( icc.isSii( today ) );
		assertFalse( icc.isNoVerifactu( today ) );
		assertFalse( icc.isVerifactu( today ) );
		assertFalse( icc.isTbai( today ) );
		assertFalse( icc.isLroe( today ) );

		CommunicationData toEnable2 = getCD(Administration.COMMON_TERRITORY)
				.setDataName(EnterpriseDataNames.ICC_NO_SIF)
				.setStartDate(today);
		InvoiceCommunicationConfiguration icc2 = ICCDAO.enableNoSif( getCtx(),getDomainId(), toEnable2);
		printIcc(icc2);
		assertTrue( icc2.isAEAT( today ) );
		assertTrue( icc2.isNoSif( today ) );
		assertFalse( icc2.isSif( today ) );
		
		assertFalse( icc2.isSii( today ) );
		assertFalse( icc2.isNoVerifactu( today ) );
		assertFalse( icc2.isVerifactu( today ) );
		assertFalse( icc2.isTbai( today ) );
		assertFalse( icc2.isLroe( today ) );
		
		assertFalse( icc2.isCanarias( today ) );
		assertFalse( icc2.isBizkaia( today ) );
		assertFalse( icc2.isAraba( today ) );
		assertFalse( icc2.isNavarra( today ) );
	}

	@Test
	void test_enable_no_sif_bizkaia_from_lroe_today() {
		resetAndGetIcc();
		Date today = AonDateUtils.today();
		Date lastMonthFirstDay = AonDateUtils.getMonthFirstDay( AonDateUtils.addMonths( today, -1 ));
		CommunicationData toEnable = getCD(Administration.BIZKAIA)
				.setDataName(EnterpriseDataNames.ICC_LROE)
				.setStartDate(lastMonthFirstDay);
		InvoiceCommunicationConfiguration icc = ICCDAO.enableLroe( getCtx(),getDomainId(), toEnable);
		printIcc(icc);
		assertTrue( icc.isLroe( today ) );
		assertTrue( icc.isBizkaia( today ) );
		
		assertFalse( icc.isCanarias( today ) );
		assertFalse( icc.isAEAT( today ) );
		assertFalse( icc.isAraba( today ) );
		assertFalse( icc.isNavarra( today ) );
		
		assertFalse( icc.isNoSif( today ) );
		assertFalse( icc.isSii( today ) );
		assertFalse( icc.isNoVerifactu( today ) );
		assertFalse( icc.isVerifactu( today ) );
		assertFalse( icc.isTbai( today ) );
		assertFalse( icc.isSif( today ) );

		CommunicationData toEnable2 = getCD(Administration.BIZKAIA)
				.setDataName(EnterpriseDataNames.ICC_NO_SIF)
				.setStartDate(today);
		InvoiceCommunicationConfiguration icc2 = ICCDAO.enableNoSif( getCtx(),getDomainId(), toEnable2);
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
	}

	@Test
	void test_enable_no_alava_sif_from_lroe_today() {
		resetAndGetIcc();
		Date today = AonDateUtils.today();
		Date lastMonthFirstDay = AonDateUtils.getMonthFirstDay( AonDateUtils.addMonths( today, -1 ));
		CommunicationData toEnable = getCD(Administration.BIZKAIA)
				.setDataName(EnterpriseDataNames.ICC_LROE)
				.setStartDate(lastMonthFirstDay);
		InvoiceCommunicationConfiguration icc = ICCDAO.enableLroe( getCtx(),getDomainId(), toEnable);
		printIcc(icc);
		assertTrue( icc.isLroe( today ) );
		assertTrue( icc.isBizkaia( today ) );
		
		assertFalse( icc.isCanarias( today ) );
		assertFalse( icc.isAEAT( today ) );
		assertFalse( icc.isAraba( today ) );
		assertFalse( icc.isNavarra( today ) );
		
		assertFalse( icc.isNoSif( today ) );
		assertFalse( icc.isSii( today ) );
		assertFalse( icc.isNoVerifactu( today ) );
		assertFalse( icc.isVerifactu( today ) );
		assertFalse( icc.isTbai( today ) );
		assertFalse( icc.isSif( today ) );

		CommunicationData toEnable2 = getCD(Administration.ALAVA)
				.setDataName(EnterpriseDataNames.ICC_NO_SIF)
				.setStartDate(today);
		InvoiceCommunicationConfiguration icc2 = ICCDAO.enableNoSif( getCtx(),getDomainId(), toEnable2);
		printIcc(icc2);
		assertTrue( icc2.isAraba( today ) );
		assertTrue( icc2.isNoSif( today ) );
		assertFalse( icc2.isLroe( today ) );
		
		assertFalse( icc2.isSii( today ) );
		assertFalse( icc2.isNoVerifactu( today ) );
		assertFalse( icc2.isVerifactu( today ) );
		assertFalse( icc2.isTbai( today ) );
		assertFalse( icc2.isSif( today ) );
		
		assertFalse( icc2.isCanarias( today ) );
		assertFalse( icc2.isBizkaia( today ) );
		assertFalse( icc2.isNavarra( today ) );
	}
	
	@Test
	void test_enable_no_sif_from_tbai_araba_today() {
		resetAndGetIcc();
		Date today = AonDateUtils.today();
		Date lastMonthFirstDay = AonDateUtils.getMonthFirstDay( AonDateUtils.addMonths( today, -1 ));
		CommunicationData toEnable = getCD(Administration.ALAVA)
				.setDataName(EnterpriseDataNames.ICC_TBAI)
				.setStartDate(lastMonthFirstDay);
		InvoiceCommunicationConfiguration icc = ICCDAO.enableTbai( getCtx(),getDomainId(), toEnable);
		printIcc(icc);
		assertTrue( icc.isTbai( today ) );
		assertTrue( icc.isAraba( today ) );
		
		assertFalse( icc.isCanarias( today ) );
		assertFalse( icc.isAEAT( today ) );
		assertFalse( icc.isBizkaia( today ) );
		assertFalse( icc.isNavarra( today ) );
		
		assertFalse( icc.isNoSif( today ) );
		assertFalse( icc.isSii( today ) );
		assertFalse( icc.isNoVerifactu( today ) );
		assertFalse( icc.isVerifactu( today ) );
		assertFalse( icc.isLroe( today ) );
		assertFalse( icc.isSif( today ) );

		CommunicationData toEnable2 = getCD(Administration.COMMON_TERRITORY)
				.setDataName(EnterpriseDataNames.ICC_NO_SIF)
				.setStartDate(today);
		InvoiceCommunicationConfiguration icc2 = ICCDAO.enableNoSif( getCtx(),getDomainId(), toEnable2);
		printIcc(icc2);
		assertTrue( icc2.isAEAT( today ) );
		assertTrue( icc2.isNoSif( today ) );
		assertFalse( icc2.isTbai( today ) );
		
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
	}
}
