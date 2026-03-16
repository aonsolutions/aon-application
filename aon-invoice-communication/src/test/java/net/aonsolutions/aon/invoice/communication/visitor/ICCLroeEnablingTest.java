package net.aonsolutions.aon.invoice.communication.visitor;
 
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.model.EnterpriseDataNames;
import com.esferalia.aon.occam.api.model.invoice.CommunicationData;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationConfiguration;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.ExemptType;
import com.esferalia.aon.occam.impl.jooq.dao.ICCDAO;
import com.esferalia.aon.watson.server.AonDateUtils;


class ICCLroeEnablingTest extends ICCAbstractEnablingTest {
	
	@Test
	void test_enable_lroe_from_nothing_past() {
		resetAndGetIcc();
		Date today = AonDateUtils.today();
		Date lastMonthFirstDay = AonDateUtils.getMonthFirstDay( AonDateUtils.addMonths( today, -1 ));
		CommunicationData toEnable = getCD( Administration.BIZKAIA)
				.setDataName( EnterpriseDataNames.ICC_LROE )
				.setStartDate(lastMonthFirstDay);
		InvoiceCommunicationConfiguration icc = ICCDAO.enableLroe( getCtx(),getDomainId(), toEnable );
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
	}
	

	@Test
	void test_enable_lroe_from_nothing_past_exempt() {
		resetAndGetIcc();
		Date today = AonDateUtils.today();
		Date lastMonthFirstDay = AonDateUtils.getMonthFirstDay( AonDateUtils.addMonths( today, -1 ));
		CommunicationData toEnable = getCD( Administration.BIZKAIA)
				.setDataName( EnterpriseDataNames.ICC_LROE )
				.setStartDate(lastMonthFirstDay)
				.setExemptType(ExemptType.NO_OBLIGATION);
		InvoiceCommunicationConfiguration icc = ICCDAO.enableLroe( getCtx(),getDomainId(), toEnable);
		printIcc(icc);
		assertTrue( icc.isBizkaia( today ) );
		assertTrue( icc.isLroe( today ) );
		assertTrue( icc.getLroeData().isPresent() );
		CommunicationData data = icc.getLroeData().get();
		assertTrue( data.getExemptType().isPresent() );
		assertEquals( ExemptType.NO_OBLIGATION, data.getExemptType().get() );
		
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
	}

	@Test
	void test_enable_lroe_from_nothing_past_test_exempt() {
		resetAndGetIcc();
		Date today = AonDateUtils.today();
		Date lastMonthFirstDay = AonDateUtils.getMonthFirstDay( AonDateUtils.addMonths( today, -1 ));
		CommunicationData toEnable = getCD( Administration.BIZKAIA)
				.setStartDate(lastMonthFirstDay)
				.setDataName( EnterpriseDataNames.ICC_LROE )
				.setExemptType(ExemptType.NO_OBLIGATION)
				.setTest(true);
		InvoiceCommunicationConfiguration icc = ICCDAO.enableLroe( getCtx() , getDomainId(), toEnable);
		printIcc(icc);
		assertTrue( icc.isBizkaia( today ) );
		assertTrue( icc.isLroe( today ) );
		assertTrue( icc.getLroeData().isPresent() );
		CommunicationData data = icc.getLroeData().get();
		assertTrue( data.isTest() );
		assertTrue( data.getExemptType().isPresent()  );
		assertEquals( ExemptType.NO_OBLIGATION, data.getExemptType().get() );
		
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
	}

	@Test
	void test_enable_verifactu_from_nothing_today() {
		resetAndGetIcc();
		Date today = AonDateUtils.today();
		CommunicationData toEnable = getCD( Administration.BIZKAIA)
				.setDataName( EnterpriseDataNames.ICC_LROE )
				.setStartDate(today);
		InvoiceCommunicationConfiguration icc = ICCDAO.enableLroe( getCtx(),getDomainId(), toEnable);
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
	}

	@Test
	void test_lroe_from_no_sif() {
		resetAndGetIcc();
		Date today = AonDateUtils.today();
		Date lastMonthFirstDay = AonDateUtils.getMonthFirstDay( AonDateUtils.addMonths( today, -1 ));
		CommunicationData toEnable = getCD( Administration.COMMON_TERRITORY)
				.setDataName( EnterpriseDataNames.ICC_NO_SIF)
				.setStartDate(lastMonthFirstDay);
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
		
		CommunicationData toEnable2 = getCD( Administration.BIZKAIA)
				.setDataName( EnterpriseDataNames.ICC_LROE )
				.setStartDate(today);		
		InvoiceCommunicationConfiguration icc2 = ICCDAO.enableLroe( getCtx(),getDomainId(), toEnable2);
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
	}
	
	@Test
	void test_lroe_from_verifctu() {
		resetAndGetIcc();
		Date today = AonDateUtils.today();
		Date yesterday = AonDateUtils.yesterday();
		Date lastMonthFirstDay = AonDateUtils.getMonthFirstDay( AonDateUtils.addMonths( today, -1 ));
		CommunicationData toEnable = getCD( Administration.COMMON_TERRITORY)
				.setDataName( EnterpriseDataNames.ICC_VERIFACTU )
				.setStartDate(lastMonthFirstDay);
		InvoiceCommunicationConfiguration icc = ICCDAO.enableVerifactu( getCtx(),getDomainId(), toEnable);
		printIcc(icc);
		assertTrue( icc.isAEAT( today ) );
		assertTrue( icc.isVerifactu( today ) );
		
		assertFalse( icc.isNoVerifactu( today ) );
		assertFalse( icc.isLroe( today ) );
		assertFalse( icc.isNoSif( today ) );
		assertFalse( icc.isTbai( today ) );
		assertFalse( icc.isSif( today ) );
		assertFalse( icc.isSii( today ) );
		
		assertFalse( icc.isCanarias( today ) );
		assertFalse( icc.isBizkaia( today ) );
		assertFalse( icc.isAraba( today ) );
		assertFalse( icc.isNavarra( today ) );
		
		CommunicationData toEnable2 = getCD( Administration.BIZKAIA)
				.setDataName( EnterpriseDataNames.ICC_LROE )
				.setStartDate(today);
		InvoiceCommunicationConfiguration icc2 = ICCDAO.enableLroe( getCtx(),getDomainId(), toEnable2);
		printIcc(icc2);
		assertTrue( icc2.isBizkaia(today ) );
		assertTrue( icc2.isLroe( today ) );
		assertFalse( icc2.isAEAT( today) );
		assertFalse( icc2.isVerifactu( today ) );
		
		assertFalse( icc2.isBizkaia(yesterday ) );
		assertFalse( icc2.isLroe( yesterday ) );
		
		assertFalse( icc2.isNoVerifactu( today ) );
		assertFalse( icc2.isNoSif( today ) );
		assertFalse( icc2.isSif( today ) );
		assertFalse( icc2.isTbai( today ) );
		assertFalse( icc2.isSii( today ) );
		
		assertFalse( icc2.isCanarias( today ) );
		assertFalse( icc2.isAEAT( today ) );
		assertFalse( icc2.isAraba( today ) );
		assertFalse( icc2.isNavarra( today ) );
		assertFalse( icc2.isGipuzkoa( today ) );
		
	}

}
