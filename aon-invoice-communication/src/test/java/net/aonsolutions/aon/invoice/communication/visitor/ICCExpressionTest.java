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


class ICCExpressionTest extends ICCAbstractEnablingTest {

	@Test
	void test_enable_no_verifactu_test() {
		resetAndGetIcc();
		Date today = AonDateUtils.today();
		Date lastMonthFirstDay = AonDateUtils.getMonthFirstDay( AonDateUtils.addMonths( today, -1 ));
		CommunicationData cd = getCD( Administration.COMMON_TERRITORY )
				.setStartDate(lastMonthFirstDay ).setTest( true ); 
		InvoiceCommunicationConfiguration icc = ICCDAO.enableNoVerifactu( getCtx(),getDomainId(), cd);
		printIcc(icc);
		assertTrue( icc.isNoVerifactu( today ) );
		assertTrue( icc.getNoVerifactuData().isPresent() );
		CommunicationData ed = icc.getNoVerifactuData().get();
		assertEquals( EnterpriseDataNames.ICC_NO_VERIFACTU, ed.getDataName());
		assertTrue( ed.isAEAT() );
		assertTrue( ed.isTest() );
		
		assertFalse( ed.isCanarias() );
		assertFalse( ed.isBizkaia( ) );
		assertFalse( ed.isAraba( ) );
		assertFalse( ed.isNavarra( ) );

		assertFalse( icc.isVerifactu( today ) );
		assertFalse( icc.isNoSif( today ) );
		assertFalse( icc.isTbai( today ) );
		assertFalse( icc.isLroe( today ) );
		assertFalse( icc.isSii( today ) );
		
	}
	
	@Test
	void test_enable_verifactu_canarias() {
		resetAndGetIcc();
		Date today = AonDateUtils.today();
		Date lastMonthFirstDay = AonDateUtils.getMonthFirstDay( AonDateUtils.addMonths( today, -1 ));
		CommunicationData cd = getCD( Administration.CANARIAS )
				.setStartDate(lastMonthFirstDay ); 
		InvoiceCommunicationConfiguration icc = ICCDAO.enableVerifactu( getCtx(),getDomainId(), cd);
		printIcc(icc);
		assertTrue( icc.isVerifactu( today ) );
		assertTrue( icc.getVerifactuData().isPresent() );
		CommunicationData ed = icc.getVerifactuData().get();
		assertEquals( EnterpriseDataNames.ICC_VERIFACTU, ed.getDataName());
		assertTrue( ed.isCanarias() );
		assertFalse( ed.isTest() );
		
		assertFalse( ed.isAEAT() );
		assertFalse( ed.isBizkaia( ) );
		assertFalse( ed.isAraba( ) );
		assertFalse( ed.isNavarra( ) );

		assertFalse( icc.isNoVerifactu( today ) );
		assertFalse( icc.isNoSif( today ) );
		assertFalse( icc.isTbai( today ) );
		assertFalse( icc.isLroe( today ) );
		assertFalse( icc.isSii( today ) );
		
	}

	@Test
	void test_enable_verifactu_exempt() {
		resetAndGetIcc();
		Date today = AonDateUtils.today();
		Date lastMonthFirstDay = AonDateUtils.getMonthFirstDay( AonDateUtils.addMonths( today, -1 ));
		CommunicationData cd = getCD( Administration.COMMON_TERRITORY)
				.setStartDate(lastMonthFirstDay )
				.setTest( true )
				.setExemptType( ExemptType.NO_OBLIGATION); 
		InvoiceCommunicationConfiguration icc = ICCDAO.enableVerifactu( getCtx(),getDomainId(), cd);
		printIcc(icc);
		assertTrue( icc.isVerifactu( today ) );
		assertTrue( icc.getVerifactuData().isPresent() );
		CommunicationData ed = icc.getVerifactuData().get();
		assertEquals( EnterpriseDataNames.ICC_VERIFACTU, ed.getDataName());
		assertTrue( ed.getAdministration().isPresent() );
		Administration a = ed.getAdministration().get();
		assertEquals( Administration.COMMON_TERRITORY, a );
		assertTrue( ed.isAEAT() );
		assertTrue( ed.isTest() );
		assertTrue( ed.getExemptType().isPresent() );
		ExemptType et = ed.getExemptType().get();
		assertEquals( ExemptType.NO_OBLIGATION, et );
		
		assertFalse( ed.isCanarias() );
		assertFalse( ed.isBizkaia( ) );
		assertFalse( ed.isAraba( ) );
		assertFalse( ed.isNavarra( ) );

		assertFalse( icc.isNoVerifactu( today ) );
		assertFalse( icc.isNoSif( today ) );
		assertFalse( icc.isTbai( today ) );
		assertFalse( icc.isLroe( today ) );
		assertFalse( icc.isSii( today ) );
		
	
	}
}
