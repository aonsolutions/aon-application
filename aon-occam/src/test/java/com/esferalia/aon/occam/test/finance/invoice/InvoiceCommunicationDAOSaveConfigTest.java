package com.esferalia.aon.occam.test.finance.invoice;

import static com.esferalia.aon.jooq.tables.EnterpriseData.ENTERPRISE_DATA;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.esferalia.aon.occam.api.model.invoice.CommunicationData;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationConfiguration;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.ExemptType;
import com.esferalia.aon.occam.impl.jooq.dao.ICCDAO;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceCommunicationDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.watson.server.AonDateUtils;

public class InvoiceCommunicationDAOSaveConfigTest extends AbstractOccamTest {

	@Before
	public void resetIcc() {
		ctx.getDslContext()
			.delete(ENTERPRISE_DATA)
			.where(ENTERPRISE_DATA.DOMAIN.eq(DOMAIN_ID))
			.and(ENTERPRISE_DATA.NAME.in(InvoiceCommunicationDAO.SUPPORTED_TYPES))
			.execute();
	}

	@Test
	public void save_siiConfig_returnsSavedConfig() {
		Date today = AonDateUtils.today();
		Date startDate = AonDateUtils.getMonthFirstDay(AonDateUtils.addMonths(today, -1));
		CommunicationData data = new CommunicationData()
			.setDomain(DOMAIN_ID)
			.setAdministration(Administration.COMMON_TERRITORY)
			.setStartDate(startDate);

		InvoiceCommunicationConfiguration result = ICCDAO.enableSii(ctx, DOMAIN_ID, data);

		assertNotNull(result);
		assertTrue(result.isSii(today));
		assertFalse(result.isVerifactu(today));
	}

	@Test
	public void save_verifactuConfig_returnsSavedConfig() {
		Date today = AonDateUtils.today();
		Date startDate = AonDateUtils.getMonthFirstDay(AonDateUtils.addMonths(today, -1));
		CommunicationData data = new CommunicationData()
			.setDomain(DOMAIN_ID)
			.setAdministration(Administration.COMMON_TERRITORY)
			.setStartDate(startDate);

		InvoiceCommunicationConfiguration result = ICCDAO.enableVerifactu(ctx, DOMAIN_ID, data);

		assertNotNull(result);
		assertTrue(result.isVerifactu(today));
		assertFalse(result.isSii(today));
	}

	@Test
	public void save_lroeConfig_returnsSavedConfig() {
		Date today = AonDateUtils.today();
		Date startDate = AonDateUtils.getMonthFirstDay(AonDateUtils.addMonths(today, -1));
		CommunicationData data = new CommunicationData()
			.setDomain(DOMAIN_ID)
			.setAdministration(Administration.BIZKAIA)
			.setStartDate(startDate);

		InvoiceCommunicationConfiguration result = ICCDAO.enableLroe(ctx, DOMAIN_ID, data);

		assertNotNull(result);
		assertTrue(result.isLroe(today));
		assertFalse(result.isSii(today));
	}

	@Test
	public void save_noVerifactuWithExemptType_persistsExemptType() {
		Date today = AonDateUtils.today();
		Date startDate = AonDateUtils.getMonthFirstDay(AonDateUtils.addMonths(today, -1));
		CommunicationData data = new CommunicationData()
			.setDomain(DOMAIN_ID)
			.setAdministration(Administration.COMMON_TERRITORY)
			.setStartDate(startDate)
			.setExemptType(ExemptType.NO_SOFTWARE);

		InvoiceCommunicationConfiguration result = ICCDAO.enableNoVerifactu(ctx, DOMAIN_ID, data);

		assertNotNull(result);
		assertTrue(result.isNoVerifactu(today));
		assertTrue(result.getNoVerifactuData().isPresent());
		assertTrue(result.getNoVerifactuData().get().getExemptType().isPresent());
	}

	@Test
	public void save_noVerifactuTestMode_persistsTestFlag() {
		Date today = AonDateUtils.today();
		Date startDate = AonDateUtils.getMonthFirstDay(AonDateUtils.addMonths(today, -1));
		CommunicationData data = new CommunicationData()
			.setDomain(DOMAIN_ID)
			.setAdministration(Administration.COMMON_TERRITORY)
			.setStartDate(startDate)
			.setTest(true);

		InvoiceCommunicationConfiguration result = ICCDAO.enableNoVerifactu(ctx, DOMAIN_ID, data);

		assertNotNull(result);
		assertTrue(result.isNoVerifactu(today));
		assertTrue(result.getNoVerifactuData().isPresent());
		assertTrue(result.getNoVerifactuData().get().isTest());
	}

	@Test
	public void save_sifConfig_returnsSavedConfig() {
		Date today = AonDateUtils.today();
		Date startDate = AonDateUtils.getMonthFirstDay(AonDateUtils.addMonths(today, -1));
		CommunicationData data = new CommunicationData()
			.setDomain(DOMAIN_ID)
			.setAdministration(Administration.COMMON_TERRITORY)
			.setStartDate(startDate);

		InvoiceCommunicationConfiguration result = ICCDAO.enableSif(ctx, DOMAIN_ID, data);

		assertNotNull(result);
		assertTrue(result.isSif(today));
	}

	@Test
	public void save_noSifConfig_returnsSavedConfig() {
		Date today = AonDateUtils.today();
		Date startDate = AonDateUtils.getMonthFirstDay(AonDateUtils.addMonths(today, -1));
		CommunicationData data = new CommunicationData()
			.setDomain(DOMAIN_ID)
			.setAdministration(Administration.COMMON_TERRITORY)
			.setStartDate(startDate);

		InvoiceCommunicationConfiguration result = ICCDAO.enableNoSif(ctx, DOMAIN_ID, data);

		assertNotNull(result);
		assertTrue(result.isNoSif(today));
		assertFalse(result.isSif(today));
	}
}
