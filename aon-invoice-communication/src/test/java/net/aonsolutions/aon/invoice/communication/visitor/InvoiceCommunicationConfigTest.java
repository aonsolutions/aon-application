package net.aonsolutions.aon.invoice.communication.visitor;
 
import static com.esferalia.aon.jooq.tables.EnterpriseData.ENTERPRISE_DATA;
import static com.esferalia.aon.occam.api.model.EnterpriseDataNames.ICC_ADMINISTRATION;
import static com.esferalia.aon.occam.api.model.EnterpriseDataNames.ICC_SIF;
import static com.esferalia.aon.occam.api.model.type.Administration.ALAVA;
import static com.esferalia.aon.occam.api.model.type.Administration.BIZKAIA;
import static com.esferalia.aon.occam.api.model.type.Administration.CANARIAS;
import static com.esferalia.aon.occam.api.model.type.Administration.COMMON_TERRITORY;
import static com.esferalia.aon.occam.api.model.type.Administration.GIPUZKOA;
import static com.esferalia.aon.occam.api.model.type.Administration.NAVARRA;
import static org.assertj.core.api.Assertions.in;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.Date;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.EnterpriseData;
import com.esferalia.aon.occam.api.model.EnterpriseDataNames;
import com.esferalia.aon.occam.api.model.invoice.CommunicationData;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationConfiguration;
import com.esferalia.aon.occam.api.model.payroll.Enterprise;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.impl.jooq.dao.CompanyDAO;
import com.esferalia.aon.occam.impl.jooq.dao.EnterpriseDAO;
import com.esferalia.aon.occam.impl.jooq.dao.EnterpriseDataDAO;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceCommunicationDAO;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.aon.verifactu.AbstractVerifactuTest;
import net.aonsolutions.aon.verifactu.Environment;


class InvoiceCommunicationConfigTest extends AbstractVerifactuTest {

	@Override protected Environment getEnvironment() { return ICC_CONFIG_ENV; }
	
	private Integer enterpriseId = null;

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
			.and(ENTERPRISE_DATA.NAME.like( InvoiceCommunicationDAO.ICC_PREFIX))
			.execute();
		getCtx().log().info("Configuración borrada para el dominio {0}: {1} registros eliminados", getDomainId(), count);
		InvoiceCommunicationConfiguration icc = new InvoiceCommunicationConfiguration();
		assertNotNull(icc);
		assertFalse( icc.hasCommunication() );
		return icc;
	}
	
	private void insertFake(Administration admon, Date date) {
		insertFake( ICC_ADMINISTRATION, admon.name(), date );
	}
	private void insertFake(EnterpriseDataNames name, Date date) {
		insertFake( name, null, date );
	}
	private void insertFake(EnterpriseDataNames name, String expr, Date date) {
		getCtx().getDslContext().insertInto(ENTERPRISE_DATA)
			.set(ENTERPRISE_DATA.DOMAIN, getDomainId())
			.set(ENTERPRISE_DATA.ENTERPRISE, getEnterpriseId() )
			.set(ENTERPRISE_DATA.NAME, name.name())
			.set(ENTERPRISE_DATA.START_DATE, AonDateUtils.toSql( date) )
			.set(ENTERPRISE_DATA.CREATION_USER, getUser()) 
			.set(ENTERPRISE_DATA.CREATION_DATE, new Timestamp( System.currentTimeMillis()))
			.set(ENTERPRISE_DATA.MODIFICATION_USER, getUser()) 
			.set(ENTERPRISE_DATA.MODIFICATION_DATE, new Timestamp( System.currentTimeMillis()))
		.execute();
	}
	
	private Integer getEnterpriseId() {
		if (enterpriseId == null) {
			Company company = CompanyDAO.getByDomain(getCtx(), getDomainId());
			if (company != null) {
				Enterprise enterprise = EnterpriseDAO.get(getCtx(), f -> 
					f.getDomainProperty().eq(getDomainId())
					.and(f.getIdProperty().eq(company.getId())));
				if (enterprise != null) {
					enterpriseId = enterprise.getId();
				} else {
					throw new AonCoreException("No enterprise found for domain " + getDomainId());
				}
			}
		}
		return enterpriseId;
	}

	@Test
	void test_empty_config() {
		InvoiceCommunicationConfiguration icc = resetAndGetIcc();
		assertTrue( icc.getAdministration().isEmpty() );
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
		InvoiceCommunicationConfiguration icc = InvoiceCommunicationDAO.enableTbaiAraba( getCtx(),getDomainId(), today );
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
		assertTrue( AonStringUtils.isBlank( ed.getExpression()));
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
		InvoiceCommunicationConfiguration icc = InvoiceCommunicationDAO.enableTbaiGipuzkoa( getCtx(),getDomainId(), today );
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
		assertTrue( AonStringUtils.isBlank( ed.getExpression()));
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
		InvoiceCommunicationConfiguration icc = InvoiceCommunicationDAO.enableTbaiArabaTest( getCtx(),getDomainId(), today );
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
		assertTrue( AonStringUtils.isNotBlank( ed.getExpression()));
		assertTrue( ed.isTest() );
		assertTrue( icc.isTbaiTest() );
		assertFalse( icc.isLroeTest() );
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
		InvoiceCommunicationConfiguration icc = InvoiceCommunicationDAO.enableTbaiGipuzkoaTest( getCtx(),getDomainId(), today );
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
		assertTrue( AonStringUtils.isNotBlank( ed.getExpression()));
		assertTrue( ed.isTest() );
		assertTrue( icc.isTbaiTest() );
		assertFalse( icc.isLroeTest() );
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
		InvoiceCommunicationConfiguration icc = InvoiceCommunicationDAO.enableLroe( getCtx(),getDomainId(), today);
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
		assertTrue( AonStringUtils.isBlank( ed.getExpression()));
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
		InvoiceCommunicationConfiguration icc = InvoiceCommunicationDAO.enableLroeTest( getCtx(),getDomainId(), today);
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
		assertTrue( AonStringUtils.isNotBlank( ed.getExpression()));
		assertTrue( ed.isTest() );
		assertTrue( icc.isLroeTest() );
		assertFalse( icc.isTbaiTest() );
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
		InvoiceCommunicationConfiguration icc = InvoiceCommunicationDAO.enableVerifactu( getCtx(),getDomainId(), today);
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
		assertTrue( AonStringUtils.isBlank( ed.getExpression()));
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
		InvoiceCommunicationConfiguration icc = InvoiceCommunicationDAO.enableVerifactuCanarias( getCtx(),getDomainId(), today );
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
		assertTrue( AonStringUtils.isBlank( ed.getExpression()));
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
		InvoiceCommunicationConfiguration icc = InvoiceCommunicationDAO.enableVerifactuTest( getCtx(),getDomainId(), today );
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
		assertFalse( icc.isLroeTest() );
		assertFalse( icc.isTbaiTest() );
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
		InvoiceCommunicationConfiguration icc = InvoiceCommunicationDAO.enableVerifactuCanariasTest( getCtx(),getDomainId(), today );
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
		assertTrue( ed.isTest() );
		assertFalse( icc.isLroeTest() );
		assertFalse( icc.isTbaiTest() );
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
		InvoiceCommunicationConfiguration icc = InvoiceCommunicationDAO.enableNoVerifactu( getCtx(),getDomainId(), today );
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
		assertTrue( AonStringUtils.isBlank( ed.getExpression()));
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
		InvoiceCommunicationConfiguration icc = InvoiceCommunicationDAO.enableNoVerifactuTest( getCtx(),getDomainId(), today );
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
		assertTrue( ed.isTest() );
		assertFalse( icc.isLroeTest() );
		assertFalse( icc.isTbaiTest() );
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
		InvoiceCommunicationConfiguration icc = InvoiceCommunicationDAO.enableNoVerifactuCanarias( getCtx(),getDomainId(), today );
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
		assertTrue( AonStringUtils.isBlank( ed.getExpression()));
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
		InvoiceCommunicationConfiguration icc = InvoiceCommunicationDAO.enableNoVerifactuCanariasTest( getCtx(),getDomainId(), today );
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
		assertTrue( ed.isTest() );
		assertFalse( icc.isLroeTest() );
		assertFalse( icc.isTbaiTest() );
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
		InvoiceCommunicationConfiguration icc = InvoiceCommunicationDAO.enableSii( getCtx(),getDomainId(), today );
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
		assertTrue( AonStringUtils.isBlank( ed.getExpression()));
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
		InvoiceCommunicationConfiguration icc = InvoiceCommunicationDAO.enableSiiTest( getCtx(),getDomainId(), today );
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
		assertTrue( ed.isTest() );
		assertFalse( icc.isLroeTest() );
		assertFalse( icc.isTbaiTest() );
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
		InvoiceCommunicationConfiguration icc = InvoiceCommunicationDAO.enableSiiCanarias( getCtx(),getDomainId(), today );
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
		assertTrue( AonStringUtils.isBlank( ed.getExpression()));
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
		InvoiceCommunicationConfiguration icc = InvoiceCommunicationDAO.enableSiiCanariasTest( getCtx(),getDomainId(), today );
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
		assertTrue( AonStringUtils.isNotBlank( ed.getExpression()));
		assertTrue( ed.isTest() );
		assertFalse( icc.isLroeTest() );
		assertFalse( icc.isTbaiTest() );
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
		InvoiceCommunicationDAO.enableTbaiAraba( getCtx(),getDomainId(), today );
		InvoiceCommunicationConfiguration icc = InvoiceCommunicationDAO.enableSiiAraba( getCtx(),getDomainId(), today );
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
		assertTrue( AonStringUtils.isBlank( tbai.getExpression()));
		assertNull( tbai.getEndDate() );
		assertTrue( AonDateUtils.isSameDay( tbai.getStartDate(), today ));

		assertTrue( icc.getSiiData().isPresent() );
		CommunicationData sii = icc.getSiiData().get();
		assertEquals( EnterpriseDataNames.ICC_SII.name(), sii.getName());
		assertTrue( AonStringUtils.isBlank( sii.getExpression()));
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
		InvoiceCommunicationDAO.enableTbaiArabaTest( getCtx(),getDomainId(), today );
		InvoiceCommunicationConfiguration icc = InvoiceCommunicationDAO.enableSiiArabaTest( getCtx(),getDomainId(), today );
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
		assertTrue( AonStringUtils.isNotBlank( ed0.getExpression()));
		assertTrue( ed0.isTest() );
		assertTrue( icc.isTbaiTest() );

		assertTrue( icc.getSiiData().isPresent() );
		CommunicationData ed = icc.getSiiData().get();
		assertEquals( EnterpriseDataNames.ICC_SII.name(), ed.getName());
		assertTrue( AonStringUtils.isNotBlank( ed.getExpression()));
		assertTrue( ed.isTest() );
		assertFalse( icc.isLroeTest() );
		assertTrue( icc.isTbaiTest() );
		assertTrue( icc.isSiiTest() );
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
		InvoiceCommunicationDAO.enableTbaiGipuzkoa( getCtx(),getDomainId(), today );
		InvoiceCommunicationConfiguration icc = InvoiceCommunicationDAO.enableSiiGipuzkoa( getCtx(),getDomainId(), today );
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
		assertTrue( AonStringUtils.isBlank( tbai.getExpression()));
		assertNull( tbai.getEndDate() );
		assertTrue( AonDateUtils.isSameDay( tbai.getStartDate(), today ));

		assertTrue( icc.getSiiData().isPresent() );
		CommunicationData sii = icc.getSiiData().get();
		assertEquals( EnterpriseDataNames.ICC_SII.name(), sii.getName());
		assertTrue( AonStringUtils.isBlank( sii.getExpression()));
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
		InvoiceCommunicationConfiguration icc = InvoiceCommunicationDAO.enableSiiGipuzkoaTest( getCtx(),getDomainId(), today );
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
		assertTrue( AonStringUtils.isNotBlank( ed.getExpression()));
		assertTrue( ed.isTest() );
		assertFalse( icc.isLroeTest() );
		assertFalse( icc.isTbaiTest() );
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
		InvoiceCommunicationConfiguration icc = InvoiceCommunicationDAO.enableSiiNavarra( getCtx(),getDomainId(), today );
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
		assertTrue( AonStringUtils.isBlank( sii.getExpression()));
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
		InvoiceCommunicationConfiguration icc = InvoiceCommunicationDAO.enableSiiNavarraTest( getCtx(),getDomainId(), today );
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
		assertTrue( AonStringUtils.isNotBlank( ed.getExpression()));
		assertTrue( ed.isTest() );
		assertFalse( icc.isLroeTest() );
		assertFalse( icc.isTbaiTest() );
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
		InvoiceCommunicationConfiguration icc = InvoiceCommunicationDAO.enableSif( getCtx(),getDomainId(), NAVARRA, today );
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
		assertTrue( AonStringUtils.isBlank( ed.getExpression()));
		assertNull( ed.getEndDate() );
		assertTrue( AonDateUtils.isSameDay( ed.getStartDate(), today ));
		
		assertTrue( icc.getVerifactuData().isEmpty() );
		assertTrue( icc.getNoVerifactuData().isEmpty() );
		assertTrue( icc.getLroeData().isEmpty() );
		assertTrue( icc.getSiiData().isEmpty() );
		assertTrue( icc.getTbaiData().isEmpty() );
		assertTrue( icc.getNoSifData().isEmpty() );
	}
	
	@Test
	void test_set_sif_navarra_test() {
		resetAndGetIcc();
		Date yesterday = AonDateUtils.yesterday();
		Date tomorrow = AonDateUtils.tomorrow();
		Date today = AonDateUtils.today(); 
		InvoiceCommunicationConfiguration icc = InvoiceCommunicationDAO.enableSifTest( getCtx(),getDomainId(), NAVARRA, today );
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
		assertTrue( AonStringUtils.isNotBlank( ed.getExpression()));
		assertTrue( ed.isTest() );
		assertTrue( icc.isSifTest() );
		assertNull( ed.getEndDate() );
		assertTrue( AonDateUtils.isSameDay( ed.getStartDate(), today ));
		
		assertTrue( icc.getVerifactuData().isEmpty() );
		assertTrue( icc.getNoVerifactuData().isEmpty() );
		assertTrue( icc.getLroeData().isEmpty() );
		assertTrue( icc.getSiiData().isEmpty() );
		assertTrue( icc.getTbaiData().isEmpty() );
		assertTrue( icc.getNoSifData().isEmpty() );
	}

	// Consistency
	@Test
	void test_consistency() {
		resetAndGetIcc();
		Date today = AonDateUtils.today();
		InvoiceCommunicationConfiguration icc = InvoiceCommunicationDAO.enableTbaiAraba(getCtx(),getDomainId(), today );
		insertFake( ALAVA, today );
		insertFake( ICC_SIF, today );
		printIcc(icc);
		icc = InvoiceCommunicationDAO.get(getCtx(),getDomainId() );
		printIcc(icc);
	}

	
}
