package com.esferalia.aon.occam.test.finance.invoice;

import static com.esferalia.aon.jooq.tables.EnterpriseData.ENTERPRISE_DATA;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.esferalia.aon.occam.api.model.EnterpriseDataNames;
import com.esferalia.aon.occam.api.model.invoice.CommunicationData;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationConfiguration;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.impl.jooq.dao.ICCDAO;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceCommunicationDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.watson.server.AonDateUtils;

public class InvoiceCommunicationDAOGetConfigTest extends AbstractOccamTest {

	@Before
	public void resetIcc() {
		ctx.getDslContext()
			.delete(ENTERPRISE_DATA)
			.where(ENTERPRISE_DATA.DOMAIN.eq(DOMAIN_ID))
			.and(ENTERPRISE_DATA.NAME.in(InvoiceCommunicationDAO.SUPPORTED_TYPES))
			.execute();
	}

	@Test
	public void get_validDomain_returnsNonNullConfig() {
		InvoiceCommunicationConfiguration config = InvoiceCommunicationDAO.get(ctx, DOMAIN_ID);
		assertNotNull(config);
	}

	@Test
	public void get_afterReset_noActiveCommunications() {
		InvoiceCommunicationConfiguration config = InvoiceCommunicationDAO.get(ctx, DOMAIN_ID);
		assertFalse(config.hasCommunication());
	}

	@Test
	public void get_afterReset_dataListIsEmpty() {
		InvoiceCommunicationConfiguration config = InvoiceCommunicationDAO.get(ctx, DOMAIN_ID);
		assertTrue(config.getDataList().isEmpty());
	}

	@Test
	public void get_afterEnableSii_reflectsSii() {
		Date today = AonDateUtils.today();
		Date startDate = AonDateUtils.getMonthFirstDay(AonDateUtils.addMonths(today, -1));
		CommunicationData data = new CommunicationData()
			.setDomain(DOMAIN_ID)
			.setDataName(EnterpriseDataNames.ICC_SII)
			.setAdministration(Administration.COMMON_TERRITORY)
			.setStartDate(startDate);
		ICCDAO.enableSii(ctx, DOMAIN_ID, data);

		InvoiceCommunicationConfiguration config = InvoiceCommunicationDAO.get(ctx, DOMAIN_ID);

		assertNotNull(config);
		assertTrue(config.isSii(today));
		assertTrue(config.isAEAT(today));
		assertFalse(config.isVerifactu(today));
		assertFalse(config.isTbai(today));
	}

	@Test
	public void get_afterEnableVerifactu_reflectsVerifactu() {
		Date today = AonDateUtils.today();
		Date startDate = AonDateUtils.getMonthFirstDay(AonDateUtils.addMonths(today, -1));
		CommunicationData data = new CommunicationData()
			.setDataName(EnterpriseDataNames.ICC_VERIFACTU)
			.setDomain(DOMAIN_ID)
			.setAdministration(Administration.COMMON_TERRITORY)
			.setStartDate(startDate);
		ICCDAO.enableVerifactu(ctx, DOMAIN_ID, data);

		InvoiceCommunicationConfiguration config = InvoiceCommunicationDAO.get(ctx, DOMAIN_ID);

		assertNotNull(config);
		assertTrue(config.isVerifactu(today));
		assertFalse(config.isSii(today));
		assertFalse(config.isLroe(today));
	}

	@Test
	public void get_afterEnableLroe_reflectsLroe() {
		Date today = AonDateUtils.today();
		Date startDate = AonDateUtils.getMonthFirstDay(AonDateUtils.addMonths(today, -1));
		CommunicationData data = new CommunicationData()
			.setDomain(DOMAIN_ID)
			.setDataName(EnterpriseDataNames.ICC_LROE)
			.setAdministration(Administration.BIZKAIA)
			.setStartDate(startDate);
		ICCDAO.enableLroe(ctx, DOMAIN_ID, data);

		InvoiceCommunicationConfiguration config = InvoiceCommunicationDAO.get(ctx, DOMAIN_ID);

		assertNotNull(config);
		assertTrue(config.isLroe(today));
		assertTrue(config.isBizkaia(today));
		assertFalse(config.isSii(today));
	}

	@Test
	public void get_afterEnableTbai_reflectsTbai() {
		Date today = AonDateUtils.today();
		Date startDate = AonDateUtils.getMonthFirstDay(AonDateUtils.addMonths(today, -1));
		CommunicationData data = new CommunicationData()
			.setDomain(DOMAIN_ID)
			.setDataName(EnterpriseDataNames.ICC_TBAI)
			.setAdministration(Administration.GIPUZKOA)
			.setStartDate(startDate);
		ICCDAO.enableTbai(ctx, DOMAIN_ID, data);

		InvoiceCommunicationConfiguration config = InvoiceCommunicationDAO.get(ctx, DOMAIN_ID);

		assertNotNull(config);
		assertTrue(config.isTbai(today));
		assertTrue(config.isGipuzkoa(today));
		assertFalse(config.isSii(today));
	}
}
