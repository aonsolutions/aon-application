package com.esferalia.aon.occam.test.finance.invoice;

import static com.esferalia.aon.occam.test.OccamAssertions.assertEquals;
import static com.esferalia.aon.occam.test.OccamAssertions.assertFalse;
import static com.esferalia.aon.occam.test.OccamAssertions.assertTrue;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import com.esferalia.aon.occam.api.model.EnterpriseData;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationConfiguration;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationType;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.InvoiceType;

/**
 * Fija el comportamiento actual de InvoiceCommunicationConfiguration antes de
 * cambiar su representacion interna por una lista de CommunicationData.
 *
 * No necesita base de datos: monta la configuracion a mano, sin pasar por el DAO.
 * El test de integracion equivalente es InvoiceCommunicationConfigurationTest.
 */
public class InvoiceCommunicationConfigurationModelTest {

	private static final Date BEFORE_ALL = date(2019, 6, 15);
	private static final Date FIRST_TBAI_DAY = date(2020, 1, 1);
	private static final Date DURING_TBAI = date(2021, 6, 15);
	private static final Date LAST_TBAI_DAY = date(2022, 12, 31);
	private static final Date FIRST_COMMON_DAY = date(2023, 1, 1);
	private static final Date IN_BETWEEN = date(2023, 6, 15);
	private static final Date FIRST_VERIFACTU_DAY = date(2024, 7, 1);
	private static final Date DURING_VERIFACTU = date(2025, 6, 15);

	private static Date date(int year, int month, int day) {
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.set(year, month - 1, day);
		return calendar.getTime();
	}

	private static EnterpriseData data(String expression, Date from, Date to) {
		return new EnterpriseData()
			.setId(1)
			.setExpression(expression)
			.setStartDate(from)
			.setEndDate(to);
	}

	private static List<EnterpriseData> history(EnterpriseData... datas) {
		return Arrays.asList(datas);
	}

	/**
	 * Araba con TicketBAI de 2020 a 2022, y despues territorio comun con Verifactu desde 2024.
	 *
	 * Los tramos de un mismo dato son dias consecutivos, no comparten la fecha de corte:
	 * el rango es cerrado por los dos extremos, asi que compartirla dejaria un dia en los dos.
	 */
	private InvoiceCommunicationConfiguration configuration() {
		return new InvoiceCommunicationConfiguration()
			.setAdministrationHistory(history(
				 data(Administration.ALAVA.name(), FIRST_TBAI_DAY, LAST_TBAI_DAY)
				,data(Administration.COMMON_TERRITORY.name(), FIRST_COMMON_DAY, null)
			))
			.setTbaiDataHistory(history(
				data("test", FIRST_TBAI_DAY, LAST_TBAI_DAY)
			))
			.setVerifactuDataHistory(history(
				data("prod", FIRST_VERIFACTU_DAY, null)
			))
			;
	}

	// ------------------------------------------------------ [ALTA POR FECHA]

	@Test
	public void testTbaiOnlyInsideItsRange() {
		InvoiceCommunicationConfiguration icc = configuration();
		assertFalse(icc.isTbai(BEFORE_ALL), "antes del alta");
		assertTrue(icc.isTbai(DURING_TBAI), "dentro del rango");
		assertFalse(icc.isTbai(IN_BETWEEN), "tras la baja");
	}

	@Test
	public void testVerifactuOnlyFromItsStart() {
		InvoiceCommunicationConfiguration icc = configuration();
		assertFalse(icc.isVerifactu(DURING_TBAI));
		assertTrue(icc.isVerifactu(DURING_VERIFACTU));
	}

	@Test
	@Ignore("PENDIENTE: inRange debe cerrar el rango por los dos extremos; hoy excluye la fecha de fin.")
	public void testRangeIncludesBothEnds() {
		InvoiceCommunicationConfiguration icc = configuration();
		assertTrue(icc.isTbai(FIRST_TBAI_DAY), "el primer dia del tramo cuenta");
		assertTrue(icc.isTbai(LAST_TBAI_DAY), "y el ultimo tambien");
		assertTrue(icc.isVerifactu(FIRST_VERIFACTU_DAY), "un tramo abierto cuenta desde su primer dia");
	}

	@Test
	@Ignore("PENDIENTE: inRange debe cerrar el rango por los dos extremos; hoy excluye la fecha de fin.")
	public void testConsecutiveRangesDoNotOverlap() {
		InvoiceCommunicationConfiguration icc = configuration();
		assertTrue(icc.isTbai(LAST_TBAI_DAY), "el ultimo dia sigue siendo del tramo que acaba");
		assertFalse(icc.isTbai(FIRST_COMMON_DAY), "y el dia siguiente ya no");
		assertEquals(Administration.ALAVA, icc.getAdministration(LAST_TBAI_DAY));
		assertEquals(Administration.COMMON_TERRITORY, icc.getAdministration(FIRST_COMMON_DAY));
	}

	@Test
	public void testNullDateIsNeverInRange() {
		InvoiceCommunicationConfiguration icc = configuration();
		assertFalse(icc.isTbai((Date) null), "con fecha nula inRange devuelve false");
		assertFalse(icc.isVerifactu((Date) null));
		assertTrue(icc.getTypes(InvoiceType.SALES, null).isEmpty(), "y por tanto no hay comunicacion");
	}

	@Test
	public void testNoHistoryMeansNoRegistration() {
		InvoiceCommunicationConfiguration icc = new InvoiceCommunicationConfiguration();
		assertFalse(icc.isTbai(DURING_TBAI));
		assertFalse(icc.isSii(DURING_TBAI));
		assertFalse(icc.isSif(DURING_TBAI));
		assertFalse(icc.isNoSif(DURING_TBAI));
	}

	// ------------------------------------------------------- [ADMINISTRACION]

	@Test
	public void testAdministrationChangesWithDate() {
		InvoiceCommunicationConfiguration icc = configuration();
		assertEquals(Administration.ALAVA, icc.getAdministration(DURING_TBAI));
		assertEquals(Administration.COMMON_TERRITORY, icc.getAdministration(DURING_VERIFACTU));
		assertEquals(Administration.UNKNOWN, icc.getAdministration(BEFORE_ALL), "fuera de todo rango, desconocida");
	}

	@Test
	public void testUnknownWithDateNeedsAnUnknownRecord() {
		InvoiceCommunicationConfiguration icc = new InvoiceCommunicationConfiguration();
		assertTrue(icc.isUnknown(), "sin administracion, la version sin fecha dice desconocida");
		assertFalse(icc.isUnknown(DURING_TBAI), "pero la version con fecha exige un registro en rango");
	}

	// ------------------------------------------------------- [MODO PRUEBAS]

	@Test
	public void testTestModeComesFromExpression() {
		InvoiceCommunicationConfiguration icc = configuration()
			.setTbaiData(data("test", date(2020, 1, 1), null))
			.setVerifactuData(data("prod", date(2024, 7, 1), null));
		assertTrue(icc.isTbaiTest());
		assertFalse(icc.isVerifactuTest());
	}

	@Test
	public void testTestModeNeedsCurrentDataWithId() {
		InvoiceCommunicationConfiguration icc = new InvoiceCommunicationConfiguration()
			.setTbaiData(new EnterpriseData().setExpression("test"));
		assertFalse(icc.isTbaiTest(), "sin id no cuenta");
	}

	// ------------------------------------------------------------- [TIPOS]

	@Test
	public void testSalesTypesDuringTbai() {
		InvoiceCommunicationConfiguration icc = configuration();
		List<InvoiceCommunicationType> types = icc.getTypes(InvoiceType.SALES, DURING_TBAI);
		assertEquals(1, types.size());
		assertEquals(InvoiceCommunicationType.TBAI, types.get(0));
		assertTrue(icc.hasCommunication(InvoiceType.SALES, DURING_TBAI));
	}

	@Test
	public void testSalesTypesDuringVerifactu() {
		InvoiceCommunicationConfiguration icc = configuration();
		List<InvoiceCommunicationType> types = icc.getTypes(InvoiceType.SALES, DURING_VERIFACTU);
		assertEquals(1, types.size());
		assertEquals(InvoiceCommunicationType.VERIFACTU, types.get(0));
	}

	@Test
	public void testTbaiDoesNotCommunicatePurchases() {
		InvoiceCommunicationConfiguration icc = configuration();
		assertTrue(icc.getTypes(InvoiceType.PURCHASE, DURING_TBAI).isEmpty(), "TicketBAI es solo de emitidas");
		assertFalse(icc.hasCommunication(InvoiceType.PURCHASE, DURING_TBAI));
	}

	@Test
	public void testTbaiNeedsForalAdministration() {
		InvoiceCommunicationConfiguration icc = new InvoiceCommunicationConfiguration()
			.setAdministrationHistory(history(
				data(Administration.COMMON_TERRITORY.name(), date(2020, 1, 1), null)
			))
			.setTbaiDataHistory(history(
				data("prod", date(2020, 1, 1), null)
			));
		assertTrue(icc.isTbai(DURING_TBAI), "el alta existe");
		assertTrue(icc.getTypes(InvoiceType.SALES, DURING_TBAI).isEmpty(), "pero sin Araba ni Gipuzkoa no se comunica");
	}

	@Test
	public void testVerifactuNeedsCommonCanariasOrUnknown() {
		InvoiceCommunicationConfiguration icc = new InvoiceCommunicationConfiguration()
			.setVerifactuDataHistory(history(
				data("prod", date(2024, 7, 1), null)
			));
		assertTrue(icc.isVerifactu(DURING_VERIFACTU), "el alta existe");
		assertTrue(icc.getTypes(InvoiceType.SALES, DURING_VERIFACTU).isEmpty(),
			"sin historial de administracion, isUnknown(fecha) es falso y no se comunica");
	}

	@Test
	public void testNoSifLeavesSalesWithoutCommunication() {
		InvoiceCommunicationConfiguration icc = configuration()
			.setNoSifDataHistory(history(
				data("NO_OBLIGATION", date(2020, 1, 1), null)
			));
		assertTrue(icc.getTypes(InvoiceType.SALES, DURING_TBAI).isEmpty(), "las ventas quedan fuera");
	}

	@Test
	public void testSifCommunicatesBothDirections() {
		InvoiceCommunicationConfiguration icc = new InvoiceCommunicationConfiguration()
			.setSifDataHistory(history(
				data("prod", date(2024, 7, 1), null)
			));
		assertEquals(Arrays.asList(InvoiceCommunicationType.SIF), icc.getTypes(InvoiceType.SALES, DURING_VERIFACTU));
		assertEquals(Arrays.asList(InvoiceCommunicationType.SIF), icc.getTypes(InvoiceType.PURCHASE, DURING_VERIFACTU));
	}

	@Test
	public void testSiiCommunicatesPurchasesInForalTerritory() {
		InvoiceCommunicationConfiguration icc = new InvoiceCommunicationConfiguration()
			.setAdministrationHistory(history(
				data(Administration.ALAVA.name(), date(2020, 1, 1), null)
			))
			.setSiiDataHistory(history(
				data("prod", date(2020, 1, 1), null)
			));
		assertTrue(icc.getTypes(InvoiceType.SALES, DURING_TBAI).isEmpty(), "en ventas, Araba con SII no comunica");
		assertEquals(Arrays.asList(InvoiceCommunicationType.SII),
			icc.getTypes(InvoiceType.PURCHASE, DURING_TBAI), "en compras si");
	}

	@Test
	public void testTwoCommunicationsKeepOrder() {
		InvoiceCommunicationConfiguration icc = new InvoiceCommunicationConfiguration()
			.setAdministrationHistory(history(
				data(Administration.COMMON_TERRITORY.name(), date(2020, 1, 1), null)
			))
			.setVerifactuDataHistory(history(data("prod", date(2024, 7, 1), null)))
			.setSifDataHistory(history(data("prod", date(2024, 7, 1), null)));
		assertEquals(
			Arrays.asList(InvoiceCommunicationType.VERIFACTU, InvoiceCommunicationType.SIF),
			icc.getTypes(InvoiceType.SALES, DURING_VERIFACTU));
	}

}
