package net.aonsolutions.aon.invoice.communication.visitor;
 
import static com.esferalia.aon.jooq.tables.EnterpriseData.ENTERPRISE_DATA;
import static com.esferalia.aon.occam.api.model.type.Administration.ALAVA;
import static com.esferalia.aon.occam.api.model.type.Administration.BIZKAIA;
import static com.esferalia.aon.occam.api.model.type.Administration.CANARIAS;
import static com.esferalia.aon.occam.api.model.type.Administration.COMMON_TERRITORY;
import static com.esferalia.aon.occam.api.model.type.Administration.GIPUZKOA;
import static com.esferalia.aon.occam.api.model.type.Administration.NAVARRA;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.text.MessageFormat;
import java.util.Date;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.model.EnterpriseDataNames;
import com.esferalia.aon.occam.api.model.invoice.CommunicationData;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationConfiguration;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.impl.jooq.dao.ICCDAO;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceCommunicationDAO;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.aon.verifactu.AbstractVerifactuTest;
import net.aonsolutions.aon.verifactu.Environment;


class InvoiceCommunicationConfigTest extends AbstractVerifactuTest {

	@Override protected Environment getEnvironment() { return ICC_CONFIG_ENV; }
	
	private void printIcc(InvoiceCommunicationConfiguration icc) {
		System.out.println( " ------- InvoiceCommunicationConfiguration" );
		icc.dataStream()
			.forEach( ed ->
				System.out.println( " - ("
					+ AonStringUtils.rightPad( ed.getId() == null ? "" : AonNumberUtils.toString(ed.getId()), 10)
					+ ") "
					+ AonStringUtils.rightPad( AonStringUtils.defaultIfBlank(ed.getName()), 25)
					+ AonStringUtils.rightPad( AonStringUtils.defaultIfBlank(ed.getExpression()) , 20)
					+ AonStringUtils.rightPad(MessageFormat.format("{0,date,dd/MM/yyyy}", ed.getStartDate()), 15)
					+ AonStringUtils.rightPad(MessageFormat.format("{0,date,dd/MM/yyyy}", ed.getEndDate()), 15)
			));
	}
	
	private InvoiceCommunicationConfiguration resetAndGetIcc() {
		int count = getCtx().getDslContext()
			.delete(ENTERPRISE_DATA)
			.where(ENTERPRISE_DATA.DOMAIN.eq(getDomainId()))
			.and(ENTERPRISE_DATA.NAME.in( InvoiceCommunicationDAO.SUPPORTED_TYPES))
			.execute();
		getCtx().log().info("Configuración borrada para el dominio {0}: {1} registros eliminados", getDomainId(), count);
		InvoiceCommunicationConfiguration icc = new InvoiceCommunicationConfiguration();
		assertNotNull(icc);
		assertFalse( icc.hasCommunication() );
		return icc;
	}
	
	@Test
	void test_empty_config() {
		InvoiceCommunicationConfiguration icc = resetAndGetIcc();
		assertTrue( icc.getNoVerifactuData().isEmpty() );
		assertTrue( icc.getVerifactuData().isEmpty() );
		assertTrue( icc.getTbaiData().isEmpty() );
		assertTrue( icc.getSiiData().isEmpty() );
		assertTrue( icc.getSifData().isEmpty() );
		assertTrue( icc.getNoSifData().isEmpty() );
		assertTrue( icc.getLroeData().isEmpty() );
	}
	
	// ------------------------------------------------------------------
	// ------------------------------------------------------------------
	// --------------------------------------------------- [TicketBai] --
	// ------------------------------------------------------------------
	// ------------------------------------------------------------------
	
	@Test
	void test_set_tbai_alava() {
		resetAndGetIcc();
		Date yesterday = AonDateUtils.yesterday();
		Date tomorrow = AonDateUtils.tomorrow();
		Date today = AonDateUtils.today(); 
		
		CommunicationData cd = getCD( ALAVA )
			.setStartDate( today );
		InvoiceCommunicationConfiguration icc = ICCDAO.enableTbai( getCtx(),getDomainId(), cd);
		printIcc(icc);

		assertTrue( icc.getTbaiData( yesterday ).isEmpty() );
		assertTrue( icc.getTbaiData( tomorrow ).isPresent() );
		
		assertTrue( icc.getTbaiData().isPresent() );
		CommunicationData ed = icc.getTbaiData().get();
		assertEquals( EnterpriseDataNames.ICC_TBAI.name(), ed.getName());
		assertTrue( ed.getAdministration().isPresent() );
		Administration admon = ed.getAdministration().get();
		assertNotNull(admon);
		assertSame( ALAVA, admon);
		assertNull( ed.getEndDate() );
		assertTrue( AonDateUtils.isSameDay( ed.getStartDate(), today ));
		
		assertTrue( icc.getVerifactuData().isEmpty() );
		assertTrue( icc.getNoVerifactuData().isEmpty() );
		assertTrue( icc.getLroeData().isEmpty() );
		assertTrue( icc.getSiiData().isEmpty() );
		assertTrue( icc.getSifData().isEmpty() );
		assertTrue( icc.getNoSifData().isEmpty() );
	}

	@Test
	void test_set_tbai_gipuzkoa() {
		resetAndGetIcc();
		Date yesterday = AonDateUtils.yesterday();
		Date tomorrow = AonDateUtils.tomorrow();
		Date today = AonDateUtils.today();
		CommunicationData toEnable = getCD( GIPUZKOA ).setStartDate( today );
		InvoiceCommunicationConfiguration icc = ICCDAO.enableTbai( getCtx(),getDomainId(), toEnable);
		printIcc(icc);
		
		assertTrue( icc.getAdministration().isPresent() );
		Administration admon = icc.getAdministration().get();
		assertNotNull(admon);
		assertSame( GIPUZKOA, admon);
		
		assertTrue( icc.getAdministration( yesterday ).isEmpty() );
		assertTrue( icc.getAdministration( tomorrow ).isPresent() );
		
		assertTrue( icc.getTbaiData().isPresent() );
		CommunicationData ed = icc.getTbaiData().get();
		assertEquals( EnterpriseDataNames.ICC_TBAI.name(), ed.getName());
		assertTrue( ed.getAdministration().isPresent() );
		Administration admon2 = ed.getAdministration().get();
		assertNotNull(admon2);
		assertSame( GIPUZKOA, admon2);
		assertFalse( ed.isTest() );
		assertFalse( ed.getExemptType().isPresent() );
		assertNull( ed.getEndDate() );
		assertTrue( AonDateUtils.isSameDay( ed.getStartDate(), today ));
		
		assertTrue( icc.getVerifactuData().isEmpty() );
		assertTrue( icc.getNoVerifactuData().isEmpty() );
		assertTrue( icc.getLroeData().isEmpty() );
		assertTrue( icc.getSiiData().isEmpty() );
		assertTrue( icc.getSifData().isEmpty() );
		assertTrue( icc.getNoSifData().isEmpty() );
	}
	
	@Test
	void test_set_tbai_test_araba() {
		resetAndGetIcc();
		Date yesterday = AonDateUtils.yesterday();
		Date tomorrow = AonDateUtils.tomorrow();
		Date today = AonDateUtils.today();
		CommunicationData toEnable = getCD( ALAVA ).setStartDate( today ).setTest( true );
		InvoiceCommunicationConfiguration icc = ICCDAO.enableTbai( getCtx(),getDomainId(), toEnable);
		printIcc(icc);
		
		assertTrue( icc.getAdministration().isPresent() );
		Administration admon = icc.getAdministration().get();
		assertNotNull(admon);
		assertSame( ALAVA, admon);
		
		assertTrue( icc.getAdministration( yesterday ).isEmpty() );
		assertTrue( icc.getAdministration( tomorrow ).isPresent() );
		
		assertTrue( icc.getTbaiData().isPresent() );
		CommunicationData ed = icc.getTbaiData().get();
		assertEquals( EnterpriseDataNames.ICC_TBAI.name(), ed.getName());
		assertTrue( ed.getAdministration().isPresent() );
		Administration admon2 = ed.getAdministration().get();
		assertNotNull(admon2);
		assertSame( ALAVA, admon2);
		assertTrue( AonStringUtils.isNotBlank( ed.getExpression()));
		assertTrue( ed.isTest() );
		assertNull( ed.getEndDate() );
		assertTrue( AonDateUtils.isSameDay( ed.getStartDate(), today ));
		
		assertTrue( icc.getVerifactuData().isEmpty() );
		assertTrue( icc.getNoVerifactuData().isEmpty() );
		assertTrue( icc.getLroeData().isEmpty() );
		assertTrue( icc.getSiiData().isEmpty() );
		assertTrue( icc.getSifData().isEmpty() );
		assertTrue( icc.getNoSifData().isEmpty() );
	}
	
	@Test
	void test_set_tbai_test_gipuzkoa() {
		resetAndGetIcc();
		Date yesterday = AonDateUtils.yesterday();
		Date tomorrow = AonDateUtils.tomorrow();
		Date today = AonDateUtils.today();
		CommunicationData toEnable = getCD( GIPUZKOA ).setStartDate( today ).setTest( true );
		InvoiceCommunicationConfiguration icc = ICCDAO.enableTbai( getCtx(),getDomainId(), toEnable);
		printIcc(icc);
		
		assertTrue( icc.getAdministration().isPresent() );
		Administration admon = icc.getAdministration().get();
		assertNotNull(admon);
		assertSame( GIPUZKOA, admon);
		
		assertTrue( icc.getAdministration( yesterday ).isEmpty() );
		assertTrue( icc.getAdministration( tomorrow ).isPresent() );
		
		assertTrue( icc.getTbaiData().isPresent() );
		CommunicationData ed = icc.getTbaiData().get();
		assertEquals( EnterpriseDataNames.ICC_TBAI.name(), ed.getName());
		assertTrue( ed.getAdministration().isPresent() );
		Administration admon2 = ed.getAdministration().get();
		assertNotNull(admon2);
		assertSame( GIPUZKOA, admon2);
		assertTrue( AonStringUtils.isNotBlank( ed.getExpression()));
		assertTrue( ed.isTest() );
		assertNull( ed.getEndDate() );
		assertTrue( AonDateUtils.isSameDay( ed.getStartDate(), today ));
		
		assertTrue( icc.getVerifactuData().isEmpty() );
		assertTrue( icc.getNoVerifactuData().isEmpty() );
		assertTrue( icc.getLroeData().isEmpty() );
		assertTrue( icc.getSiiData().isEmpty() );
		assertTrue( icc.getSifData().isEmpty() );
		assertTrue( icc.getNoSifData().isEmpty() );
	}

	// ------------------------------------------------------------------
	// ------------------------------------------------------------------
	// -------------------------------------------------------- [LROE] --
	// ------------------------------------------------------------------
	// ------------------------------------------------------------------
	@Test
	void test_set_lroe() {
		resetAndGetIcc();
		Date yesterday = AonDateUtils.yesterday();
		Date tomorrow = AonDateUtils.tomorrow();
		Date today = AonDateUtils.today();
		CommunicationData toEnable = getCD( BIZKAIA ).setStartDate( today );
		InvoiceCommunicationConfiguration icc = ICCDAO.enableLroe( getCtx(),getDomainId(), toEnable);
		printIcc(icc);
		
		assertTrue( icc.getAdministration().isPresent() );
		Administration admon = icc.getAdministration().get();
		assertNotNull(admon);
		assertSame( BIZKAIA , admon);
		
		assertTrue( icc.getAdministration( yesterday ).isEmpty() );
		assertTrue( icc.getAdministration( tomorrow ).isPresent() );
		
		assertTrue( icc.getLroeData().isPresent() );
		CommunicationData ed = icc.getLroeData().get();
		assertEquals( EnterpriseDataNames.ICC_LROE.name(), ed.getName());
		assertTrue( ed.getAdministration().isPresent() );
		Administration admon2 = ed.getAdministration().get();
		assertNotNull(admon2);
		assertSame( BIZKAIA, admon2);
		assertFalse( ed.isTest() );
		assertNull( ed.getEndDate() );
		assertTrue( AonDateUtils.isSameDay( ed.getStartDate(), today ));
		
		assertTrue( icc.getVerifactuData().isEmpty() );
		assertTrue( icc.getNoVerifactuData().isEmpty() );
		assertTrue( icc.getTbaiData().isEmpty() );
		assertTrue( icc.getSiiData().isEmpty() );
		assertTrue( icc.getSifData().isEmpty() );
		assertTrue( icc.getNoSifData().isEmpty() );
	}
	
	@Test
	void test_set_lroe_test() {
		resetAndGetIcc();
		Date yesterday = AonDateUtils.yesterday();
		Date tomorrow = AonDateUtils.tomorrow();
		Date today = AonDateUtils.today();
		CommunicationData toEnable = getCD( BIZKAIA ).setStartDate( today ).setTest( true );
		InvoiceCommunicationConfiguration icc = ICCDAO.enableLroe( getCtx(),getDomainId(), toEnable);
		printIcc(icc);
		
		assertTrue( icc.getAdministration().isPresent() );
		Administration admon = icc.getAdministration().get();
		assertNotNull(admon);
		assertSame( BIZKAIA , admon);
		
		assertTrue( icc.getAdministration( yesterday ).isEmpty() );
		assertTrue( icc.getAdministration( tomorrow ).isPresent() );
		
		assertTrue( icc.getLroeData().isPresent() );
		CommunicationData ed = icc.getLroeData().get();
		assertEquals( EnterpriseDataNames.ICC_LROE.name(), ed.getName());
		assertTrue( ed.getAdministration().isPresent() );
		Administration admon2 = ed.getAdministration().get();
		assertNotNull(admon2);
		assertSame( BIZKAIA, admon2);
		assertTrue( AonStringUtils.isNotBlank( ed.getExpression()));
		assertTrue( ed.isTest() );
		assertTrue( ed.isTest() );
		assertNull( ed.getEndDate() );
		assertTrue( AonDateUtils.isSameDay( ed.getStartDate(), today ));
		
		assertTrue( icc.getVerifactuData().isEmpty() );
		assertTrue( icc.getNoVerifactuData().isEmpty() );
		assertTrue( icc.getTbaiData().isEmpty() );
		assertTrue( icc.getSiiData().isEmpty() );
		assertTrue( icc.getSifData().isEmpty() );
		assertTrue( icc.getNoSifData().isEmpty() );
	}

	// ------------------------------------------------------------------
	// ------------------------------------------------------------------
	// --------------------------------------------------- [Verifactu] --
	// ------------------------------------------------------------------
	// ------------------------------------------------------------------

	@Test
	void test_set_verifactu_common_territory() {
		resetAndGetIcc();
		Date yesterday = AonDateUtils.yesterday();
		Date tomorrow = AonDateUtils.tomorrow();	
		Date today = AonDateUtils.today();
		CommunicationData toEnable = getCD( COMMON_TERRITORY).setStartDate( today );
		InvoiceCommunicationConfiguration icc = ICCDAO.enableVerifactu( getCtx(),getDomainId(), toEnable);
		printIcc(icc);
		
		assertTrue( icc.getAdministration().isPresent() );
		Administration admon = icc.getAdministration().get();
		assertNotNull(admon);
		assertSame( COMMON_TERRITORY, admon );
		
		assertTrue( icc.getAdministration( yesterday ).isEmpty() );
		assertTrue( icc.getAdministration( tomorrow ).isPresent() );
		
		assertTrue( icc.getVerifactuData().isPresent() );
		CommunicationData ed = icc.getVerifactuData().get();
		assertEquals( EnterpriseDataNames.ICC_VERIFACTU.name(), ed.getName());
		assertTrue( ed.getAdministration().isPresent() );
		Administration admon2 = ed.getAdministration().get();
		assertNotNull(admon2);
		assertSame( COMMON_TERRITORY, admon2);
		assertFalse( ed.isTest() );
		assertNull( ed.getEndDate() );
		assertTrue( AonDateUtils.isSameDay( ed.getStartDate(), today ));
		
		assertTrue( icc.getNoVerifactuData().isEmpty() );
		assertTrue( icc.getTbaiData().isEmpty() );
		assertTrue( icc.getSiiData().isEmpty() );
		assertTrue( icc.getSifData().isEmpty() );
		assertTrue( icc.getNoSifData().isEmpty() );
		assertTrue( icc.getLroeData().isEmpty() );
	}
	
	@Test
	void test_set_verifactu_canarias() {
		resetAndGetIcc();
		Date yesterday = AonDateUtils.yesterday();
		Date tomorrow = AonDateUtils.tomorrow();
		Date today = AonDateUtils.today();
		CommunicationData toEnable = getCD(CANARIAS).setStartDate( today );
		InvoiceCommunicationConfiguration icc = ICCDAO.enableVerifactu( getCtx(),getDomainId(), toEnable );
		printIcc(icc);
		
		assertTrue( icc.getAdministration().isPresent() );
		Administration admon = icc.getAdministration().get();
		assertNotNull(admon);
		assertSame( CANARIAS, admon);
		
		assertTrue( icc.getAdministration( yesterday ).isEmpty() );
		assertTrue( icc.getAdministration( tomorrow ).isPresent() );
		
		assertTrue( icc.getVerifactuData().isPresent() );
		CommunicationData ed = icc.getVerifactuData().get();
		assertEquals( EnterpriseDataNames.ICC_VERIFACTU.name(), ed.getName());
		assertTrue( ed.getAdministration().isPresent() );
		Administration admon2 = ed.getAdministration().get();
		assertNotNull(admon2);
		assertSame( CANARIAS, admon2);
		assertFalse( ed.isTest() );
		assertNull( ed.getEndDate() );
		assertTrue( AonDateUtils.isSameDay( ed.getStartDate(), today ));
		
		assertTrue( icc.getNoVerifactuData().isEmpty() );
		assertTrue( icc.getTbaiData().isEmpty() );
		assertTrue( icc.getSiiData().isEmpty() );
		assertTrue( icc.getSifData().isEmpty() );
		assertTrue( icc.getNoSifData().isEmpty() );
		assertTrue( icc.getLroeData().isEmpty() );
	}

	@Test
	void test_set_verifactu_test_common_territory() {
		resetAndGetIcc();
		Date yesterday = AonDateUtils.yesterday();
		Date tomorrow = AonDateUtils.tomorrow();	
		Date today = AonDateUtils.today(); 
		CommunicationData toEnable = getCD( COMMON_TERRITORY ).setStartDate( today ).setTest( true );
		InvoiceCommunicationConfiguration icc = ICCDAO.enableVerifactu( getCtx(),getDomainId(), toEnable);
		printIcc(icc);
		
		assertTrue( icc.getAdministration().isPresent() );
		Administration admon = icc.getAdministration().get();
		assertNotNull(admon);
		assertSame( COMMON_TERRITORY, admon );
		
		assertTrue( icc.getAdministration( yesterday ).isEmpty() );
		assertTrue( icc.getAdministration( tomorrow ).isPresent() );
		
		assertTrue( icc.getVerifactuData().isPresent() );
		CommunicationData ed = icc.getVerifactuData().get();
		assertEquals( EnterpriseDataNames.ICC_VERIFACTU.name(), ed.getName());
		assertTrue( AonStringUtils.isNotBlank( ed.getExpression()));
		assertTrue( ed.isTest() );
		assertNull( ed.getEndDate() );
		assertTrue( AonDateUtils.isSameDay( ed.getStartDate(), today ));
		
		assertTrue( icc.getNoVerifactuData().isEmpty() );
		assertTrue( icc.getTbaiData().isEmpty() );
		assertTrue( icc.getSiiData().isEmpty() );
		assertTrue( icc.getSifData().isEmpty() );
		assertTrue( icc.getNoSifData().isEmpty() );
		assertTrue( icc.getLroeData().isEmpty() );
	}

	@Test
	void test_set_verifactu_test_canarias() {
		resetAndGetIcc();
		Date yesterday = AonDateUtils.yesterday();
		Date tomorrow = AonDateUtils.tomorrow();	
		Date today = AonDateUtils.today();
		CommunicationData toEnable = getCD( CANARIAS ).setStartDate( today ).setTest( true );
		InvoiceCommunicationConfiguration icc = ICCDAO.enableVerifactu( getCtx(),getDomainId(), toEnable);
		printIcc(icc);
		
		assertTrue( icc.getAdministration().isPresent() );
		Administration admon = icc.getAdministration().get();
		assertNotNull(admon);
		assertSame( CANARIAS, admon );
		
		assertTrue( icc.getAdministration( yesterday ).isEmpty() );
		assertTrue( icc.getAdministration( tomorrow ).isPresent() );
		
		assertTrue( icc.getVerifactuData().isPresent() );
		CommunicationData ed = icc.getVerifactuData().get();
		assertEquals( EnterpriseDataNames.ICC_VERIFACTU.name(), ed.getName());
		assertTrue( AonStringUtils.isNotBlank( ed.getExpression()));
		assertTrue( ed.getAdministration().isPresent() );
		Administration admon2 = ed.getAdministration().get();
		assertNotNull(admon2);
		assertSame( CANARIAS, admon2);
		assertTrue( ed.isTest() );
		assertNull( ed.getEndDate() );
		assertTrue( AonDateUtils.isSameDay( ed.getStartDate(), today ));
		
		assertTrue( icc.getNoVerifactuData().isEmpty() );
		assertTrue( icc.getTbaiData().isEmpty() );
		assertTrue( icc.getSiiData().isEmpty() );
		assertTrue( icc.getSifData().isEmpty() );
		assertTrue( icc.getNoSifData().isEmpty() );
		assertTrue( icc.getLroeData().isEmpty() );
	}

	// ------------------------------------------------------------------
	// ------------------------------------------------------------------
	// ------------------------------------------------ [No Verifactu] --
	// ------------------------------------------------------------------
	// ------------------------------------------------------------------

	@Test
	void test_set_no_verifactu_common_territory() {
		resetAndGetIcc();
		Date yesterday = AonDateUtils.yesterday();
		Date tomorrow = AonDateUtils.tomorrow();
		Date today = AonDateUtils.today();
		CommunicationData toEnable = getCD( COMMON_TERRITORY ).setStartDate( today );
		InvoiceCommunicationConfiguration icc = ICCDAO.enableNoVerifactu( getCtx(),getDomainId(), toEnable);
		printIcc(icc);
		
		assertTrue( icc.getAdministration().isPresent() );
		Administration admon = icc.getAdministration().get();
		assertNotNull(admon);
		assertSame( COMMON_TERRITORY , admon);
		
		assertTrue( icc.getAdministration( yesterday ).isEmpty() );
		assertTrue( icc.getAdministration( tomorrow ).isPresent() );
		
		assertTrue( icc.getNoVerifactuData().isPresent() );
		CommunicationData ed = icc.getNoVerifactuData().get();
		assertEquals( EnterpriseDataNames.ICC_NO_VERIFACTU.name(), ed.getName());
		assertTrue( ed.getAdministration().isPresent() );
		Administration admon2 = ed.getAdministration().get();
		assertNotNull(admon2);
		assertSame( COMMON_TERRITORY, admon2);
		assertFalse( ed.isTest() );
		assertNull( ed.getEndDate() );
		assertTrue( AonDateUtils.isSameDay( ed.getStartDate(), today ));
		
		assertTrue( icc.getVerifactuData().isEmpty() );
		assertTrue( icc.getTbaiData().isEmpty() );
		assertTrue( icc.getSiiData().isEmpty() );
		assertTrue( icc.getSifData().isEmpty() );
		assertTrue( icc.getNoSifData().isEmpty() );
		assertTrue( icc.getLroeData().isEmpty() );
	}

	@Test
	void test_set_no_verifactu_test_common_territory() {
		resetAndGetIcc();
		Date yesterday = AonDateUtils.yesterday();
		Date tomorrow = AonDateUtils.tomorrow();
		Date today = AonDateUtils.today();
		CommunicationData toEnable = getCD( COMMON_TERRITORY ).setStartDate( today ).setTest( true );
		InvoiceCommunicationConfiguration icc = ICCDAO.enableNoVerifactu( getCtx(),getDomainId(), toEnable);
		printIcc(icc);
		
		assertTrue( icc.getAdministration().isPresent() );
		Administration admon = icc.getAdministration().get();
		assertNotNull(admon);
		assertSame( COMMON_TERRITORY , admon);
		
		assertTrue( icc.getAdministration( yesterday ).isEmpty() );
		assertTrue( icc.getAdministration( tomorrow ).isPresent() );
		
		assertTrue( icc.getNoVerifactuData().isPresent() );
		CommunicationData ed = icc.getNoVerifactuData().get();
		assertEquals( EnterpriseDataNames.ICC_NO_VERIFACTU.name(), ed.getName());
		assertTrue( AonStringUtils.isNotBlank( ed.getExpression()));
		assertTrue( ed.getAdministration().isPresent() );
		Administration admon2 = ed.getAdministration().get();
		assertNotNull(admon2);
		assertSame( COMMON_TERRITORY, admon2);
		assertTrue( ed.isTest() );
		assertNull( ed.getEndDate() );
		assertTrue( AonDateUtils.isSameDay( ed.getStartDate(), today ));
		
		assertTrue( icc.getVerifactuData().isEmpty() );
		assertTrue( icc.getTbaiData().isEmpty() );
		assertTrue( icc.getSiiData().isEmpty() );
		assertTrue( icc.getSifData().isEmpty() );
		assertTrue( icc.getNoSifData().isEmpty() );
		assertTrue( icc.getLroeData().isEmpty() );
	}

	@Test
	void test_set_no_verifactu_canarias() {
		resetAndGetIcc();
		Date yesterday = AonDateUtils.yesterday();
		Date tomorrow = AonDateUtils.tomorrow();
		Date today = AonDateUtils.today();
		CommunicationData toEnable = getCD( CANARIAS ).setStartDate( today );
		InvoiceCommunicationConfiguration icc = ICCDAO.enableNoVerifactu( getCtx(),getDomainId(), toEnable);
		printIcc(icc);
		
		assertTrue( icc.getAdministration().isPresent() );
		Administration admon = icc.getAdministration().get();
		assertNotNull(admon);
		assertSame( CANARIAS, admon);
		
		assertTrue( icc.getAdministration( yesterday ).isEmpty() );
		assertTrue( icc.getAdministration( tomorrow ).isPresent() );

		assertTrue( icc.getNoVerifactuData().isPresent() );
		CommunicationData ed = icc.getNoVerifactuData().get();
		assertEquals( EnterpriseDataNames.ICC_NO_VERIFACTU.name(), ed.getName());
		assertTrue( ed.getAdministration().isPresent() );
		Administration admon2 = ed.getAdministration().get();
		assertNotNull(admon2);
		assertSame( CANARIAS, admon2);
		assertFalse( ed.isTest() );
		assertNull( ed.getEndDate() );
		assertTrue( AonDateUtils.isSameDay( ed.getStartDate(), today ));
		
		assertTrue( icc.getVerifactuData().isEmpty() );
		assertTrue( icc.getTbaiData().isEmpty() );
		assertTrue( icc.getSiiData().isEmpty() );
		assertTrue( icc.getSifData().isEmpty() );
		assertTrue( icc.getNoSifData().isEmpty() );
		assertTrue( icc.getLroeData().isEmpty() );
	}
	
	@Test
	void test_set_no_verifactu_test_canarias() {
		resetAndGetIcc();
		Date yesterday = AonDateUtils.yesterday();
		Date tomorrow = AonDateUtils.tomorrow();
		Date today = AonDateUtils.today();
		CommunicationData toEnable = getCD( CANARIAS ).setStartDate( today ).setTest( true );
		InvoiceCommunicationConfiguration icc = ICCDAO.enableNoVerifactu( getCtx(),getDomainId(), toEnable);
		printIcc(icc);
		
		assertTrue( icc.getAdministration().isPresent() );
		Administration admon = icc.getAdministration().get();
		assertNotNull(admon);
		assertSame( CANARIAS, admon);
		
		assertTrue( icc.getAdministration( yesterday ).isEmpty() );
		assertTrue( icc.getAdministration( tomorrow ).isPresent() );

		assertTrue( icc.getNoVerifactuData().isPresent() );
		CommunicationData ed = icc.getNoVerifactuData().get();
		assertEquals( EnterpriseDataNames.ICC_NO_VERIFACTU.name(), ed.getName());
		assertTrue( AonStringUtils.isNotBlank( ed.getExpression()));
		assertTrue( ed.getAdministration().isPresent() );
		Administration admon2 = ed.getAdministration().get();
		assertNotNull(admon2);
		assertSame( CANARIAS, admon2);
		assertTrue( ed.isTest() );
		assertNull( ed.getEndDate() );
		assertTrue( AonDateUtils.isSameDay( ed.getStartDate(), today ));
		
		assertTrue( icc.getVerifactuData().isEmpty() );
		assertTrue( icc.getTbaiData().isEmpty() );
		assertTrue( icc.getSiiData().isEmpty() );
		assertTrue( icc.getSifData().isEmpty() );
		assertTrue( icc.getNoSifData().isEmpty() );
		assertTrue( icc.getLroeData().isEmpty() );
	}

	// ------------------------------------------------------------------
	// ------------------------------------------------------------------
	// --------------------------------------------------------- [SII] --
	// ------------------------------------------------------------------
	// ------------------------------------------------------------------

	@Test
	void test_set_sii_aeat() {
		resetAndGetIcc();
		Date yesterday = AonDateUtils.yesterday();
		Date tomorrow = AonDateUtils.tomorrow();
		Date today = AonDateUtils.today();
		CommunicationData toEnable = getCD( COMMON_TERRITORY ).setStartDate( today );
		InvoiceCommunicationConfiguration icc = ICCDAO.enableSii( getCtx(),getDomainId(), toEnable);
		printIcc(icc);
		
		assertTrue( icc.getAdministration().isPresent() );
		Administration admon = icc.getAdministration().get();
		assertNotNull(admon);
		assertSame( COMMON_TERRITORY, admon);
		
		assertTrue( icc.getAdministration( yesterday ).isEmpty() );
		assertTrue( icc.getAdministration( tomorrow ).isPresent() );

		assertTrue( icc.getSiiData().isPresent() );
		CommunicationData ed = icc.getSiiData().get();
		assertEquals( EnterpriseDataNames.ICC_SII.name(), ed.getName());
		assertFalse( ed.isTest() );
		assertTrue( ed.getAdministration().isPresent() );
		Administration admon2 = ed.getAdministration().get();
		assertNotNull(admon2);
		assertSame( COMMON_TERRITORY, admon2);
		assertNull( ed.getEndDate() );
		assertTrue( AonDateUtils.isSameDay( ed.getStartDate(), today ));
		
		assertTrue( icc.getVerifactuData().isEmpty() );
		assertTrue( icc.getNoVerifactuData().isEmpty() );
		assertTrue( icc.getTbaiData().isEmpty() );
		assertTrue( icc.getSifData().isEmpty() );
		assertTrue( icc.getNoSifData().isEmpty() );
		assertTrue( icc.getLroeData().isEmpty() );
	}

	@Test
	void test_set_sii_test_aeat() {
		resetAndGetIcc();
		Date yesterday = AonDateUtils.yesterday();
		Date tomorrow = AonDateUtils.tomorrow();
		Date today = AonDateUtils.today();
		CommunicationData toEnable = getCD( COMMON_TERRITORY ).setStartDate( today ).setTest( true );
		InvoiceCommunicationConfiguration icc = ICCDAO.enableSii( getCtx(),getDomainId(), toEnable);
		printIcc(icc);
		
		assertTrue( icc.getAdministration().isPresent() );
		Administration admon = icc.getAdministration().get();
		assertNotNull(admon);
		assertSame( COMMON_TERRITORY, admon);
		
		assertTrue( icc.getAdministration( yesterday ).isEmpty() );
		assertTrue( icc.getAdministration( tomorrow ).isPresent() );

		assertTrue( icc.getSiiData().isPresent() );
		CommunicationData ed = icc.getSiiData().get();
		assertEquals( EnterpriseDataNames.ICC_SII.name(), ed.getName());
		assertTrue( AonStringUtils.isNotBlank( ed.getExpression()));
		assertTrue( ed.getAdministration().isPresent() );
		Administration admon2 = ed.getAdministration().get();
		assertNotNull(admon2);
		assertSame( COMMON_TERRITORY, admon2);
		assertTrue( ed.isTest() );
		assertNull( ed.getEndDate() );
		assertTrue( AonDateUtils.isSameDay( ed.getStartDate(), today ));
		
		assertTrue( icc.getVerifactuData().isEmpty() );
		assertTrue( icc.getNoVerifactuData().isEmpty() );
		assertTrue( icc.getTbaiData().isEmpty() );
		assertTrue( icc.getSifData().isEmpty() );
		assertTrue( icc.getNoSifData().isEmpty() );
		assertTrue( icc.getLroeData().isEmpty() );
	}

	@Test
	void test_set_sii_canarias() {
		resetAndGetIcc();
		Date yesterday = AonDateUtils.yesterday();
		Date tomorrow = AonDateUtils.tomorrow();
		Date today = AonDateUtils.today();
		CommunicationData toEnable = getCD( CANARIAS ).setStartDate( today );
		InvoiceCommunicationConfiguration icc = ICCDAO.enableSii( getCtx(),getDomainId(), toEnable);
		printIcc(icc);
		
		assertTrue( icc.getAdministration().isPresent() );
		Administration admon = icc.getAdministration().get();
		assertNotNull(admon);
		assertSame( CANARIAS, admon);
		
		assertTrue( icc.getAdministration( yesterday ).isEmpty() );
		assertTrue( icc.getAdministration( tomorrow ).isPresent() );

		assertTrue( icc.getSiiData().isPresent() );
		CommunicationData ed = icc.getSiiData().get();
		assertEquals( EnterpriseDataNames.ICC_SII.name(), ed.getName());
		assertTrue( ed.getAdministration().isPresent() );
		Administration admon2 = ed.getAdministration().get();
		assertNotNull(admon2);
		assertSame( CANARIAS, admon2);
		assertFalse( ed.isTest() );
		assertNull( ed.getEndDate() );
		assertTrue( AonDateUtils.isSameDay( ed.getStartDate(), today ));
		
		assertTrue( icc.getVerifactuData().isEmpty() );
		assertTrue( icc.getNoVerifactuData().isEmpty() );
		assertTrue( icc.getTbaiData().isEmpty() );
		assertTrue( icc.getSifData().isEmpty() );
		assertTrue( icc.getNoSifData().isEmpty() );
		assertTrue( icc.getLroeData().isEmpty() );
	}

	@Test
	void test_set_sii_test_canarias() {
		resetAndGetIcc();
		Date yesterday = AonDateUtils.yesterday();
		Date tomorrow = AonDateUtils.tomorrow();
		Date today = AonDateUtils.today();
		CommunicationData toEnable = getCD( CANARIAS ).setStartDate( today ).setTest( true );
		InvoiceCommunicationConfiguration icc = ICCDAO.enableSii( getCtx(),getDomainId(), toEnable);
		printIcc(icc);
		
		assertTrue( icc.getAdministration().isPresent() );
		Administration admon = icc.getAdministration().get();
		assertNotNull(admon);
		assertSame( CANARIAS, admon);
		
		assertTrue( icc.getAdministration( yesterday ).isEmpty() );
		assertTrue( icc.getAdministration( tomorrow ).isPresent() );

		assertTrue( icc.getSiiData().isPresent() );
		CommunicationData ed = icc.getSiiData().get();
		assertEquals( EnterpriseDataNames.ICC_SII.name(), ed.getName());
		assertTrue( ed.getAdministration().isPresent() );
		Administration admon2 = ed.getAdministration().get();
		assertNotNull(admon2);
		assertSame( CANARIAS, admon2);
		assertTrue( ed.isTest() );
		assertNull( ed.getEndDate() );
		assertTrue( AonDateUtils.isSameDay( ed.getStartDate(), today ));
		
		assertTrue( icc.getVerifactuData().isEmpty() );
		assertTrue( icc.getNoVerifactuData().isEmpty() );
		assertTrue( icc.getTbaiData().isEmpty() );
		assertTrue( icc.getSifData().isEmpty() );
		assertTrue( icc.getNoSifData().isEmpty() );
		assertTrue( icc.getLroeData().isEmpty() );
	}

	@Test
	void test_set_sii_araba() {
		resetAndGetIcc();
		Date yesterday = AonDateUtils.yesterday();
		Date tomorrow = AonDateUtils.tomorrow();
		Date today = AonDateUtils.today();
		CommunicationData toEnable = getCD( ALAVA ).setStartDate( today );
		ICCDAO.enableTbai( getCtx(),getDomainId(), toEnable);
		CommunicationData toEnableSii = getCD( ALAVA ).setStartDate( today );
		InvoiceCommunicationConfiguration icc = ICCDAO.enableSii( getCtx(),getDomainId(), toEnableSii);
		printIcc(icc);
		
		assertTrue( icc.getAdministration().isPresent() );
		Administration admon = icc.getAdministration().get();
		assertNotNull(admon);
		assertSame( ALAVA, admon);
		
		assertTrue( icc.getAdministration( yesterday ).isEmpty() );
		assertTrue( icc.getAdministration( tomorrow ).isPresent() );

		assertTrue( icc.getTbaiData().isPresent() );
		CommunicationData tbai = icc.getTbaiData().get();
		assertEquals( EnterpriseDataNames.ICC_TBAI.name(), tbai.getName());
		assertTrue( tbai.getAdministration().isPresent() );
		Administration admon2 = tbai.getAdministration().get();
		assertNotNull(admon2);
		assertSame( ALAVA, admon2);
		assertFalse( tbai.isTest() );
		assertNull( tbai.getEndDate() );
		assertTrue( AonDateUtils.isSameDay( tbai.getStartDate(), today ));

		assertTrue( icc.getSiiData().isPresent() );
		CommunicationData sii = icc.getSiiData().get();
		assertEquals( EnterpriseDataNames.ICC_SII.name(), sii.getName());
		assertTrue( sii.getAdministration().isPresent() );
		Administration admon3 = sii.getAdministration().get();
		assertNotNull(admon3);
		assertSame( ALAVA, admon3);
		assertFalse( sii.isTest() );
		assertNull( sii.getEndDate() );
		assertTrue( AonDateUtils.isSameDay( sii.getStartDate(), today ));
		
		assertTrue( icc.getVerifactuData().isEmpty() );
		assertTrue( icc.getNoVerifactuData().isEmpty() );
		assertTrue( icc.getSifData().isEmpty() );
		assertTrue( icc.getNoSifData().isEmpty() );
		assertTrue( icc.getLroeData().isEmpty() );
	}

	@Test
	void test_set_sii_test_araba() {
		resetAndGetIcc();
		Date yesterday = AonDateUtils.yesterday();
		Date tomorrow = AonDateUtils.tomorrow();
		Date today = AonDateUtils.today();
		CommunicationData toEnable = getCD( ALAVA ).setStartDate( today ).setTest( true );
		ICCDAO.enableTbai( getCtx(),getDomainId(), toEnable);
		CommunicationData toEnableSii = getCD( ALAVA ).setStartDate( today ).setTest( true );
		InvoiceCommunicationConfiguration icc = ICCDAO.enableSii( getCtx(),getDomainId(), toEnableSii);
		printIcc(icc);
		
		assertTrue( icc.getAdministration().isPresent() );
		Administration admon = icc.getAdministration().get();
		assertNotNull(admon);
		assertSame( ALAVA, admon);
		
		assertTrue( icc.getAdministration( yesterday ).isEmpty() );
		assertTrue( icc.getAdministration( tomorrow ).isPresent() );

		assertTrue( icc.getTbaiData().isPresent() );
		CommunicationData ed0 = icc.getTbaiData().get();
		assertEquals( EnterpriseDataNames.ICC_TBAI.name(), ed0.getName());
		assertTrue( ed0.getAdministration().isPresent() );
		Administration admon2 = ed0.getAdministration().get();
		assertNotNull(admon2);
		assertSame( ALAVA, admon2);
		assertTrue( ed0.isTest() );

		assertTrue( icc.getSiiData().isPresent() );
		CommunicationData ed = icc.getSiiData().get();
		assertEquals( EnterpriseDataNames.ICC_SII.name(), ed.getName());
		assertTrue( AonStringUtils.isNotBlank( ed.getExpression()));
		assertTrue( ed.getAdministration().isPresent() );
		Administration admon3 = ed.getAdministration().get();
		assertNotNull(admon3);
		assertSame( ALAVA, admon3);
		assertTrue( ed.isTest() );
		assertNull( ed.getEndDate() );
		assertTrue( AonDateUtils.isSameDay( ed.getStartDate(), today ));
		
		assertTrue( icc.getVerifactuData().isEmpty() );
		assertTrue( icc.getNoVerifactuData().isEmpty() );
		assertFalse( icc.getTbaiData().isEmpty() );
		assertTrue( icc.getSifData().isEmpty() );
		assertTrue( icc.getNoSifData().isEmpty() );
		assertTrue( icc.getLroeData().isEmpty() );
	}

	@Test
	void test_set_sii_gipuzkoa() {
		resetAndGetIcc();
		Date yesterday = AonDateUtils.yesterday();
		Date tomorrow = AonDateUtils.tomorrow();
		Date today = AonDateUtils.today();
		CommunicationData toEnable = getCD( GIPUZKOA ).setStartDate( today );
		ICCDAO.enableTbai( getCtx(),getDomainId(), toEnable);
		CommunicationData toEnableSii = getCD( GIPUZKOA ).setStartDate( today );
		InvoiceCommunicationConfiguration icc = ICCDAO.enableSii( getCtx(),getDomainId(), toEnableSii);
		printIcc(icc);
		
		assertTrue( icc.getAdministration().isPresent() );
		Administration admon = icc.getAdministration().get();
		assertNotNull(admon);
		assertSame( GIPUZKOA, admon);
		
		assertTrue( icc.getAdministration( yesterday ).isEmpty() );
		assertTrue( icc.getAdministration( tomorrow ).isPresent() );

		assertTrue( icc.getTbaiData().isPresent() );
		CommunicationData tbai = icc.getTbaiData().get();
		assertEquals( EnterpriseDataNames.ICC_TBAI.name(), tbai.getName());
		assertTrue( tbai.getAdministration().isPresent() );
		Administration admon2 = tbai.getAdministration().get();
		assertNotNull(admon2);
		assertSame( GIPUZKOA, admon2);
		assertFalse( tbai.isTest() );
		assertNull( tbai.getEndDate() );
		assertTrue( AonDateUtils.isSameDay( tbai.getStartDate(), today ));

		assertTrue( icc.getSiiData().isPresent() );
		CommunicationData sii = icc.getSiiData().get();
		assertEquals( EnterpriseDataNames.ICC_SII.name(), sii.getName());
		assertTrue( sii.getAdministration().isPresent() );
		Administration admon3 = sii.getAdministration().get();
		assertNotNull(admon3);
		assertSame( GIPUZKOA, admon3);
		assertFalse( sii.isTest() );
		assertNull( sii.getEndDate() );
		assertTrue( AonDateUtils.isSameDay( sii.getStartDate(), today ));
		
		assertTrue( icc.getVerifactuData().isEmpty() );
		assertTrue( icc.getNoVerifactuData().isEmpty() );
		assertTrue( icc.getSifData().isEmpty() );
		assertTrue( icc.getNoSifData().isEmpty() );
		assertTrue( icc.getLroeData().isEmpty() );
	}
	
	@Test
	void test_set_sii_test_gipuzkoa() {
		resetAndGetIcc();
		Date yesterday = AonDateUtils.yesterday();
		Date tomorrow = AonDateUtils.tomorrow();
		Date today = AonDateUtils.today();
		CommunicationData toEnable = getCD( GIPUZKOA ).setStartDate( today ).setTest( true );
		InvoiceCommunicationConfiguration icc = ICCDAO.enableSii( getCtx(),getDomainId(), toEnable);
		printIcc(icc);
		
		assertTrue( icc.getAdministration().isPresent() );
		Administration admon = icc.getAdministration().get();
		assertNotNull(admon);
		assertSame( GIPUZKOA, admon);
		
		assertTrue( icc.getAdministration( yesterday ).isEmpty() );
		assertTrue( icc.getAdministration( tomorrow ).isPresent() );

		assertTrue( icc.getSiiData().isPresent() );
		CommunicationData ed = icc.getSiiData().get();
		assertEquals( EnterpriseDataNames.ICC_SII.name(), ed.getName());
		assertTrue( ed.getAdministration().isPresent() );
		Administration admon2 = ed.getAdministration().get();
		assertNotNull(admon2);
		assertSame( GIPUZKOA, admon2);
		assertTrue( ed.isTest() );
		assertNull( ed.getEndDate() );
		assertTrue( AonDateUtils.isSameDay( ed.getStartDate(), today ));
		
		assertTrue( icc.getVerifactuData().isEmpty() );
		assertTrue( icc.getNoVerifactuData().isEmpty() );
		assertTrue( icc.getTbaiData().isEmpty() );
		assertTrue( icc.getSifData().isEmpty() );
		assertTrue( icc.getNoSifData().isEmpty() );
		assertTrue( icc.getLroeData().isEmpty() );
	}

	@Test
	void test_set_sii_navarra() {
		resetAndGetIcc();
		Date yesterday = AonDateUtils.yesterday();
		Date tomorrow = AonDateUtils.tomorrow();
		Date today = AonDateUtils.today();
		CommunicationData toEnable = getCD( NAVARRA ).setStartDate( today );
		InvoiceCommunicationConfiguration icc = ICCDAO.enableSii( getCtx(),getDomainId(), toEnable);
		printIcc(icc);
		
		assertTrue( icc.getAdministration().isPresent() );
		Administration admon = icc.getAdministration().get();
		assertNotNull(admon);
		assertSame( NAVARRA, admon);
		
		assertTrue( icc.getAdministration( yesterday ).isEmpty() );
		assertTrue( icc.getAdministration( tomorrow ).isPresent() );

		assertTrue( icc.getSiiData().isPresent() );
		CommunicationData sii = icc.getSiiData().get();
		assertEquals( EnterpriseDataNames.ICC_SII.name(), sii.getName());
		assertTrue( sii.getAdministration().isPresent() );
		Administration admon2 = sii.getAdministration().get();
		assertNotNull(admon2);
		assertSame( NAVARRA, admon2);
		assertFalse( sii.isTest() );
		assertNull( sii.getEndDate() );
		assertTrue( AonDateUtils.isSameDay( sii.getStartDate(), today ));
		
		assertTrue( icc.getVerifactuData().isEmpty() );
		assertTrue( icc.getNoVerifactuData().isEmpty() );
		assertTrue( icc.getSifData().isEmpty() );
		assertTrue( icc.getNoSifData().isEmpty() );
		assertTrue( icc.getLroeData().isEmpty() );
	}
	
	@Test
	void test_set_sii_test_navarra() {
		resetAndGetIcc();
		Date yesterday = AonDateUtils.yesterday();
		Date tomorrow = AonDateUtils.tomorrow();
		Date today = AonDateUtils.today();
		CommunicationData toEnable = getCD( NAVARRA ).setStartDate( today ).setTest( true );
		InvoiceCommunicationConfiguration icc = ICCDAO.enableSii( getCtx(),getDomainId(), toEnable);
		printIcc(icc);
		
		assertTrue( icc.getAdministration().isPresent() );
		Administration admon = icc.getAdministration().get();
		assertNotNull(admon);
		assertSame( NAVARRA, admon);
		
		assertTrue( icc.getAdministration( yesterday ).isEmpty() );
		assertTrue( icc.getAdministration( tomorrow ).isPresent() );

		assertTrue( icc.getSiiData().isPresent() );
		CommunicationData ed = icc.getSiiData().get();
		assertEquals( EnterpriseDataNames.ICC_SII.name(), ed.getName());
		assertTrue( ed.getAdministration().isPresent() );
		Administration admon2 = ed.getAdministration().get();
		assertNotNull(admon2);
		assertSame( NAVARRA, admon2);
		assertTrue( ed.isTest() );
		assertNull( ed.getEndDate() );
		assertTrue( AonDateUtils.isSameDay( ed.getStartDate(), today ));
		
		assertTrue( icc.getVerifactuData().isEmpty() );
		assertTrue( icc.getNoVerifactuData().isEmpty() );
		assertTrue( icc.getTbaiData().isEmpty() );
		assertTrue( icc.getSifData().isEmpty() );
		assertTrue( icc.getNoSifData().isEmpty() );
		assertTrue( icc.getLroeData().isEmpty() );
	}

	// ------------------------------------------------------------------
	// ------------------------------------------------------------------
	// --------------------------------------------------------- [SII] --
	// ------------------------------------------------------------------
	// ------------------------------------------------------------------

	@Test
	void test_set_sif_navarra() {
		resetAndGetIcc();
		Date yesterday = AonDateUtils.yesterday();
		Date tomorrow = AonDateUtils.tomorrow();
		Date today = AonDateUtils.today();
		CommunicationData toEnable = getCD( NAVARRA ).setStartDate( today );
		InvoiceCommunicationConfiguration icc = ICCDAO.enableSif( getCtx(),getDomainId(), toEnable);
		printIcc(icc);
		
		assertTrue( icc.getAdministration().isPresent() );
		Administration admon = icc.getAdministration().get();
		assertNotNull(admon);
		assertSame( NAVARRA, admon);
		
		assertTrue( icc.getAdministration( yesterday ).isEmpty() );
		assertTrue( icc.getAdministration( tomorrow ).isPresent() );
		
		assertTrue( icc.getSifData().isPresent() );
		CommunicationData ed = icc.getSifData().get();
		assertEquals( EnterpriseDataNames.ICC_SIF.name(), ed.getName());
		assertTrue( ed.getAdministration().isPresent() );
		Administration admon2 = ed.getAdministration().get();
		assertNotNull(admon2);
		assertSame( NAVARRA, admon2);
		assertFalse( ed.isTest() );
		assertNull( ed.getEndDate() );
		assertTrue( AonDateUtils.isSameDay( ed.getStartDate(), today ));
		
		assertTrue( icc.getVerifactuData().isEmpty() );
		assertTrue( icc.getNoVerifactuData().isEmpty() );
		assertTrue( icc.getLroeData().isEmpty() );
		assertTrue( icc.getSiiData().isEmpty() );
		assertTrue( icc.getTbaiData().isEmpty() );
		assertTrue( icc.getNoSifData().isEmpty() );
	}
	
}
