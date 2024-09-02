package com.esferalia.aon.in.payroll.tgss.idc;

import static com.esferalia.aon.in.payroll.tgss.idc.PEC.isBonus;
import static com.esferalia.aon.in.payroll.tgss.idc.PEC.isCost;
import static com.esferalia.aon.in.payroll.tgss.idc.PEC.isDeduction;
import static com.esferalia.aon.jooq.tables.DeductionConcept.DEDUCTION_CONCEPT;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.SystemDeduction.SYSTEM_DEDUCTION;
import static com.esferalia.aon.jooq.tables.SystemPayment.SYSTEM_PAYMENT;
import static com.esferalia.aon.occam.api.model.type.DeductionType.BONUS;
import static com.esferalia.aon.occam.api.model.type.DeductionType.JOB_TRAINING;
import static com.esferalia.aon.occam.api.model.type.DeductionType.UNEMPLOYMENT;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.COMMON_DISEASE_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.LEAVE_FACTOR;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.OCCUPATIONAL_DISEASE_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.PREST_IT;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.QUOTE_DAYS;
import static com.esferalia.aon.watson.util.AonDateUtils.get;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfYear;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.OCTOBER;
import static java.util.Calendar.SEPTEMBER;
import static java.util.Calendar.YEAR;
import static net.aonsolutions.core.tgss.creta.jaxb.Utils.marshal;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.esferalia.aon.in.payroll.SistemaRED2AON;
import com.esferalia.aon.in.payroll.pdf.UnknownPDFException;
import com.esferalia.aon.jooq.tables.records.ContractCostRecord;
import com.esferalia.aon.jooq.tables.records.ContractDeductionRecord;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.jooq.tables.records.DeductionConceptRecord;
import com.esferalia.aon.jooq.tables.records.DomainRecord;
import com.esferalia.aon.jooq.tables.records.EnterpriseActivityRecord;
import com.esferalia.aon.jooq.tables.records.EnterpriseCccRecord;
import com.esferalia.aon.jooq.tables.records.PaymentConceptRecord;
import com.esferalia.aon.jooq.tables.records.RegistryRecord;
import com.esferalia.aon.jooq.tables.records.ScopeRecord;
import com.esferalia.aon.jooq.tables.records.WorkplaceRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.PAYROLL;
import com.esferalia.aon.occam.api.model.Bonus;
import com.esferalia.aon.occam.api.model.Cost;
import com.esferalia.aon.occam.api.model.Deduction;
import com.esferalia.aon.occam.api.model.EmployeeIT;
import com.esferalia.aon.occam.api.model.payroll.ContractData;
import com.esferalia.aon.occam.api.model.type.ContractLeaveType;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBonus;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.SalaryCost;
import com.esferalia.aon.payroll.SalaryData;
import com.esferalia.aon.payroll.SalaryDeduction;
import com.esferalia.aon.payroll.SalaryPayment;
import com.esferalia.aon.payroll.calculator.GenericContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.IContractBonus;
import com.esferalia.aon.payroll.calculator.RoundSalaryBuilder;
import com.esferalia.aon.payroll.calculator.SmartContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.sql.AbstractSQLTestCase;
import com.esferalia.aon.payroll.calculator.sql.ISQLContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.enumeration.CCCType;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.LeaveType;
import com.esferalia.aon.payroll.enumeration.SSRegimeType;
import com.esferalia.aon.payroll.tgss.creta.IndentXMLStreamWriter;
import com.esferalia.aon.salary.ISalaryBuilder;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.bonus.IBonus;
import com.esferalia.aon.salary.deduction.IDeduction;
import com.esferalia.aon.salary.enumeration.DeductionType;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ITimedVariable;
import com.esferalia.aon.watson.util.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.mchange.util.AssertException;

import net.aonsolutions.core.tgss.creta.jaxb.Utils;
import net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.DatoSolicitado;
import net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.Liquidacion;
import net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.LiquidacionMes;
import net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.Trabajador;
import net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.Trabajadores;
import net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.TrabajadoresTramos;
import net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.Tramo;

public class IdcTest extends AbstractSQLTestCase {

	private static final double DELTA = 0.001;

	public static class Data {
		String name;
		Date endDate;
		Date startDate;
		String expression;
	}

	@Test
	public void testIdcplccc() throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException {
		try (InputStream is = IdcTest.class.getResourceAsStream("idcplccc.pdf")) {
			IdcplcccParser.parse(is, new IdcParserListener() {

				@Override
				public void onPeriod(Date date) {
					assertEquals(2020, get(date, YEAR),"PERIODO DE LIQUIDACIÓN:");
					assertEquals(OCTOBER, get(date, MONTH),"PERIODO DE LIQUIDACIÓN:");
				}

				@Override
				public void onEnterprise(String socialReason, String ccc, String nif, String economicActivityCode,
						String economicActivityDescription, String regime, String fullCCC) {

					assertEquals("AON SOLUTIONS S.L.", socialReason,"RAZÓN SOCIAL:");
					assertEquals("01105360062", ccc,"C.C.C:");
					assertEquals("B01487271", nif,"DNI/NIE/CIF:");
					assertEquals("6209", economicActivityCode,"ACT ECONÓMICA:");

				}

				@Override
				public void onEmployee(String nss, String name) {
					Map<String, String> NSS_NAME_MAP = new HashMap<String, String>();
					NSS_NAME_MAP.put("010019805355", "ANA DIAZ PEREZ");
					NSS_NAME_MAP.put("011001022503", "EUGENIO CASTELLANO HURTADO");
					NSS_NAME_MAP.put("011005185924", "RAUL TREPIANA ZARATE");
					NSS_NAME_MAP.put("011006256964", "SHEILA RUESGAS GARCIA");
					NSS_NAME_MAP.put("011007308507", "PATRICIA COCA FUENTES");
					NSS_NAME_MAP.put("011011187190", "ANDER IBAÑEZ DE GAUNA NAVAZO");
					NSS_NAME_MAP.put("281468615302", "SERGIO VALDEPEÑAS DEL POZO");
					NSS_NAME_MAP.put("291136796369", "RAY DE JESUS VASQUEZ BEAUPERTHUY");

					assertEquals(NSS_NAME_MAP.get(nss), name,"NSS:");
				}

				@Override
				public void onEmployeePerido(String nss, String ccc, Date startDate, Date endDate) {

					Date _1October2020 = getDate(1, Calendar.OCTOBER, 2020);
					Date _31October2020 = getDate(31, Calendar.OCTOBER, 2020);

					assertEquals(_1October2020, startDate,"FECHA DESDE");
					assertEquals(_31October2020, endDate,"FECHA HASTA");

					// System.out.println(nss + " " + startDate + " " + endDate );

				}

				@Override
				public void onEmployeeQuotePEC(String nss, String ccc, String pec, String description, String portTipo,
						String quota, Date startDate, Date endDate) {
					assertEquals("291136796369", nss,"NSS:");
					assertEquals("01105360062", ccc,"C.C.C:");
					assertEquals("04", pec,"TIPO DE PECULIARIDAD");

					Map<String, Double> QUOTA_POR_TIPO_MAP = new HashMap<String, Double>();
					QUOTA_POR_TIPO_MAP.put("05", 0.05);
					QUOTA_POR_TIPO_MAP.put("02", 1.20);

					NumberFormat numberFormat = DecimalFormat.getNumberInstance(new Locale("es", "ES"));
					try {
						assertEquals(QUOTA_POR_TIPO_MAP.get(quota), numberFormat.parse(portTipo),"POR/TIPO");
					} catch (ParseException e) {
						fail(e.getMessage());
					}

					Date _1October2020 = getDate(1, Calendar.OCTOBER, 2020);
					Date _31October2020 = getDate(31, Calendar.OCTOBER, 2020);

					assertEquals(_1October2020, startDate,"FECHA DESDE");
					assertEquals(_31October2020, endDate,"FECHA HASTA");
				}

			});
		}
	}

	@Test
	public void testIdcplcccI() throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException {
		try (InputStream is = IdcTest.class.getResourceAsStream("idcplcccI.pdf")) {
			IdcplcccParser.parse(is, new IdcParserListener() {

				@Override
				public void onPeriod(Date date) {
					assertEquals(2020, get(date, YEAR),"PERIODO DE LIQUIDACIÓN:");
					assertEquals(OCTOBER, get(date, MONTH),"PERIODO DE LIQUIDACIÓN:");
				}

				@Override
				public void onEnterprise(String socialReason, String ccc, String nif, String economicActivityCode,
						String economicActivityDescription, String regime, String fullCCC) {

					assertEquals("AON SOLUTIONS S.L.", socialReason,"RAZÓN SOCIAL:");
					assertEquals("01105577910", ccc,"C.C.C:");
					assertEquals("B01487271", nif,"DNI/NIE/CIF:");
					assertEquals("6209", economicActivityCode,"ACT ECONÓMICA:");

				}

				@Override
				public void onEmployee(String nss, String name) {
					Map<String, String> NSS_NAME_MAP = new HashMap<String, String>();
					NSS_NAME_MAP.put("011006286569", "ESTHER ARANDA MARTIN");
					NSS_NAME_MAP.put("011017250195", "IKER GONZALEZ DIAZ");
					NSS_NAME_MAP.put("011021493543", "AKETZA EGUSQUIZA VAZQUEZ");
					NSS_NAME_MAP.put("141026133260", "MARIA LUISA BLASCO DE PORRES");

					assertEquals(NSS_NAME_MAP.get(nss), name,"NSS:");
				}

				@Override
				public void onEmployeePerido(String nss, String ccc, Date startDate, Date endDate) {

					Date _1October2020 = getDate(1, Calendar.OCTOBER, 2020);
					Date _31October2020 = getDate(31, Calendar.OCTOBER, 2020);

					assertEquals(_1October2020, startDate,"FECHA DESDE");
					assertEquals(_31October2020, endDate,"FECHA HASTA");
				}

				@Override
				public void onEmployeeQuotePEC(String nss, String ccc, String pec, String description, String portTipo,
						String quota, Date startDate, Date endDate) {

					Map<String, String> PEC_QUOTA_MAP = new HashMap<String, String>();
					PEC_QUOTA_MAP.put("01", "68");
					PEC_QUOTA_MAP.put("09", "53");

					assertEquals(PEC_QUOTA_MAP.get(pec), quota,"TIPO DE PECULIARIDAD FRACCIÓN DE CUOTA:");

					NumberFormat numberFormat = DecimalFormat.getNumberInstance(new Locale("es", "ES"));
					try {
						assertEquals(100.00, numberFormat.parse(portTipo).doubleValue(), 0.00,"POR/TIPO");
					} catch (ParseException e) {
						fail(e.getMessage());
					}

					Date _1October2020 = getDate(1, Calendar.OCTOBER, 2020);
					Date _31October2020 = getDate(31, Calendar.OCTOBER, 2020);

					assertEquals(_1October2020, startDate,"FECHA DESDE");
					assertEquals(_31October2020, endDate,"FECHA HASTA");
				}

			});
		}
	}

	@Test
	@Disabled
	public void testIdcplnss() throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException {
		try (InputStream is = IdcTest.class.getResourceAsStream("idcplnss.pdf")) {
			IdcplnssParser.parse(is, new IdcParserListener() {

				@Override
				public void onPeriod(Date date) {
					assertEquals(2020, get(date, YEAR),"PERIODO SOLICITADO:");
					assertEquals(SEPTEMBER, get(date, MONTH),"PERIODO SOLICITADO:");
				}

				@Override
				public void onEnterprise(String socialReason, String ccc, String nif, String economicActivityCode,
						String economicActivityDescription, String regime, String fullCCC) {
					assertEquals("37106820136", ccc,"C.C.C:");
					assertEquals("EDUCAMOS SALAMANCA, S.L.", socialReason,"RAZÓN SOCIAL:");
				}

				@Override
				public void onEmployee(String nss, String name) {
					assertEquals("371013120530", nss,"CCC SOLICITADO:");
					assertEquals("JOANA VAQUERO GARCIA", name,"NOMBRE Y APELLIDOS:");
				}

				@Override
				public void onEmployeePerido(String nss, String ccc, Date startDate, Date endDate) {

					Date _3July2020 = getDate(3, Calendar.JULY, 2020);
					Date _14September2020 = getDate(14, Calendar.SEPTEMBER, 2020);
					Date _15September2020 = getDate(15, Calendar.SEPTEMBER, 2020);
					Date _30September2020 = getDate(30, Calendar.SEPTEMBER, 2020);

					if (startDate.equals(_3July2020))
						assertEquals(_14September2020, endDate, "FECHA HASTA");
					else if (startDate.equals(_15September2020))
						assertEquals(_30September2020, endDate,"FECHA HASTA");
					else
						fail();

				}

				@Override
				public void onEmployeeQuotePEC(String nss, String ccc, String pec, String description, String portTipo,
						String quota, Date startDate, Date endDate) {
					assertEquals("NSS:", "371013120530", nss);
					assertEquals("C.C.C:", "37106820136", ccc);

					ArrayList<IdcPEC> PECS = new ArrayList<IdcPEC>(6);
					PECS.add(new IdcPEC("23", getDate(1, Calendar.SEPTEMBER, 2020),
							getDate(7, Calendar.SEPTEMBER, 2020), 100.00, "57"));

					PECS.add(new IdcPEC("37", getDate(3, Calendar.JULY, 2020), getDate(31, Calendar.JULY, 2020), 60.00,
							"01"));
					PECS.add(new IdcPEC("15", getDate(3, Calendar.JULY, 2020), getDate(31, Calendar.JULY, 2020), 35.00,
							"57"));
					PECS.add(new IdcPEC("18", getDate(3, Calendar.JULY, 2020), getDate(31, Calendar.JULY, 2020), 100.00,
							"08"));

					PECS.add(new IdcPEC("37", getDate(1, Calendar.AUGUST, 2020), getDate(31, Calendar.AUGUST, 2020),
							60.00, "01"));
					PECS.add(new IdcPEC("15", getDate(1, Calendar.AUGUST, 2020), getDate(31, Calendar.AUGUST, 2020),
							35.00, "57"));
					PECS.add(new IdcPEC("18", getDate(1, Calendar.AUGUST, 2020), getDate(31, Calendar.AUGUST, 2020),
							100.00, "08"));

					PECS.add(new IdcPEC("37", getDate(1, Calendar.SEPTEMBER, 2020),
							getDate(14, Calendar.SEPTEMBER, 2020), 60.00, "01"));
					PECS.add(new IdcPEC("15", getDate(1, Calendar.SEPTEMBER, 2020),
							getDate(14, Calendar.SEPTEMBER, 2020), 35.00, "57"));
					PECS.add(new IdcPEC("18", getDate(1, Calendar.SEPTEMBER, 2020),
							getDate(14, Calendar.SEPTEMBER, 2020), 100.00, "08"));

					PECS.add(new IdcPEC("37", getDate(15, Calendar.SEPTEMBER, 2020),
							getDate(30, Calendar.SEPTEMBER, 2020), 60.00, "01"));

					try {
						IdcPEC actual = new IdcPEC(pec, startDate, endDate,
								DecimalFormat.getNumberInstance(new Locale("es", "ES")).parse(portTipo).doubleValue(),
								quota);
						assertNotNull(PECS.get(PECS.indexOf(actual)));
						return;
					} catch (ParseException e) {
						fail(e.getMessage());
					}

					fail("Unknow PEC");

				}

			});
		}
	}

	@Test
	public void testIdc() throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException {

		try (InputStream is = IdcTest.class.getResourceAsStream("idc.pdf")) {

			AtomicBoolean onEmployeeQuoteTypes = new AtomicBoolean(false);

			IdcParser.parse(is, new IdcParserListener() {

				@Override
				public void onPeriod(Date date) {
					assertEquals(getDate(14, Calendar.MAY, 2020), date,"PERIODO:");
				}

				@Override
				public void onEnterprise(String socialReason, String ccc, String cif, String economicActivityCode,
						String economicActivityDescription, String regime, String fullCCC) {

					assertEquals("RAZÓN SOCIAL:", "SOUTHWEST GOLF S.L.", socialReason);
					assertEquals("C.C.C:", "11112501771", ccc);
					assertEquals("DNI/NIE/CIF:", "B85729648", cif);
					assertEquals("ACT ECONÓMICA:", "9311", economicActivityCode);
					assertEquals("C.C.C", "011111112501771", fullCCC);

				}

				@Override
				public void onEmployee(String nss, String name) {
					assertEquals("NSS:", "081053913352", nss);
					assertEquals("NOMBRE Y APELLIDOS:", "MARC JOVE JOVE", name);
				}

				@Override
				public void onEmployeePerido(String nss, String ccc, Date startDate, Date endDate) {
					assertEquals("NSS:", "081053913352", nss);
					assertEquals("C.C.C:", "11112501771", ccc);

				}

				@Override
				public void onEmployeeQuotePEC(String nss, String ccc, String pec, String description, String portTipo,
						String quota, Date startDate, Date endDate) {
					assertEquals("NSS:", "081053913352", nss);
					assertEquals("C.C.C:", "11112501771", ccc);
					if (startDate.equals(getDate(14, Calendar.MAY, 2020)))
						assertEquals(getDate(31, Calendar.MAY, 2020), endDate);
					else if (startDate.equals(getDate(1, Calendar.JUNE, 2020)))
						assertEquals(getDate(30, Calendar.JUNE, 2020), endDate);
					else
						fail();

					ArrayList<IdcPEC> PECS = new ArrayList<IdcPEC>(6);
					PECS.add(new IdcPEC("37", getDate(14, Calendar.MAY, 2020), getDate(31, Calendar.MAY, 2020), 85.00,
							"01"));
					PECS.add(new IdcPEC("15", getDate(14, Calendar.MAY, 2020), getDate(31, Calendar.MAY, 2020), 60.00,
							"57"));
					PECS.add(new IdcPEC("18", getDate(14, Calendar.MAY, 2020), getDate(31, Calendar.MAY, 2020), 100.00,
							"08"));

					PECS.add(new IdcPEC("37", getDate(1, Calendar.JUNE, 2020), getDate(30, Calendar.JUNE, 2020), 70.00,
							"01"));
					PECS.add(new IdcPEC("15", getDate(1, Calendar.JUNE, 2020), getDate(30, Calendar.JUNE, 2020), 45.00,
							"57"));
					PECS.add(new IdcPEC("18", getDate(1, Calendar.JUNE, 2020), getDate(30, Calendar.JUNE, 2020), 100.00,
							"08"));

					try {
						IdcPEC actual = new IdcPEC(pec, startDate, endDate,
								DecimalFormat.getNumberInstance(new Locale("es", "ES")).parse(portTipo).doubleValue(),
								quota);
						assertNotNull(PECS.get(PECS.indexOf(actual)));
						return;
					} catch (ParseException e) {
						fail(e.getMessage());
					}

					fail("Unknow PEC");

				}

				@Override
				public void onEmployeeQuoteTypes(Double it, Double ims, Double unemployment) {
					assertEquals(it, 1.70, 0.00);
					assertEquals(ims, 1.30, 0.00);
					assertEquals(unemployment, 7.05, 0.00);
					onEmployeeQuoteTypes.set(true);
					System.out.println("IT:" + it + " I.M.S:" + ims + " DESEMPLEO:" + unemployment);
				}

			});

			assertTrue(onEmployeeQuoteTypes.get());
		}
	}

	@Test
	public void testIdcII() throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException {
		try (InputStream is = IdcTest.class.getResourceAsStream("idcII.pdf")) {
			IdcParser.parse(is, new IdcParserListener() {

				@Override
				public void onEnterprise(String socialReason, String ccc, String cif, String economicActivityCode,
						String economicActivityDescription, String regime, String fullCCC) {

					assertEquals("RAZÓN SOCIAL:", "AON SOLUTIONS S.L.", socialReason);
					assertEquals("C.C.C:", "01105360062", ccc);
					assertEquals("DNI/NIE/CIF:", "B01487271", cif);

					assertEquals("ACT ECONÓMICA:", "6209", economicActivityCode);

				}

				@Override
				public void onEmployee(String nss, String name) {
					assertEquals("NSS:", "011005185924", nss);
					assertEquals("NOMBRE Y APELLIDOS:", "RAUL TREPIANA ZARATE", name);
				}

				@Override
				public void onEmployeePerido(String nss, String ccc, Date startDate, Date endDate) {
				}

				@Override
				public void onEmployeeQuotePEC(String nss, String ccc, String pec, String description, String portTipo,
						String quota, Date startDate, Date endDate) {
					fail("Unknow PEC");
				}

				@Override
				public void onEmployeeQuoteTypes(Double it, Double ims, Double unemployment) {
					assertEquals(it, 0.80, 0.00);
					assertEquals(ims, 0.70, 0.00);
					assertEquals(unemployment, 7.05, 0.00);
				}
				
				@Override
				public void onContractOcupation(String ocupation) {
					assertEquals("OCUPACION", "A", ocupation);
				}

			});
		}
	}

	@Test
	public void testIdcplcccBonus0() throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException {
		try (InputStream is = IdcTest.class.getResourceAsStream("idcplccc.pdf")) {
			Collection<PEC> ssBonuses = Idcplccc.getSSBonuses(is);
			assertEquals(0, ssBonuses.size());
		}
	}

	@Test
	public void testIdcplcccBonus() throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException {
		try (InputStream is = IdcTest.class.getResourceAsStream("idcplcccI.pdf")) {
			Collection<PEC> ssPecs = Idcplccc.getSSBonuses(is);
			assertEquals(4 + 4 * (2 + 3) + 4, ssPecs.size());
			assertEquals(4, ssPecs.stream().filter(pec -> isBonus(pec)).count());

			assertEquals(4 * 2 + 4, ssPecs.stream().filter(pec -> isDeduction(pec)).count());
			assertEquals(4 * 3, ssPecs.stream().filter(pec -> isCost(pec)).count());

			ssPecs.forEach(b -> System.out.println(b));
		}
	}

	@Test
	public void testIdcplnssIVBonusI() throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException,
			ExpressionException, SQLException, SalaryException {

		try (InputStream is = IdcTest.class.getResourceAsStream("idcplnssIV.pdf")) {
			Collection<PEC> ssBonuses = Idcplnss.getSSBonuses(is);
			assertEquals(1, ssBonuses.size());
			// EXONE.ERE.F.MAY.COMP (100,00%) 01-12-2020 10-12-2020

			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			calendar.set(Calendar.MONTH, Calendar.DECEMBER);
			calendar.set(Calendar.YEAR, 2020);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			Date _01122020 = calendar.getTime();

			calendar.set(Calendar.DAY_OF_MONTH, 10);
			Date _10122020 = calendar.getTime();

			calendar.set(Calendar.DAY_OF_MONTH, 11);
			Date _11122020 = calendar.getTime();

			calendar.set(Calendar.DAY_OF_MONTH, 31);
			Date _31122020 = calendar.getTime();

			Data ereFactor = new Data() {
				{
					expression = "1.0";
					endDate = _10122020;
					startDate = _01122020;
					name = ContextVariable.ERE_FACTOR_FORCE_OFF.getName();
				}
			};
			Salary salary = calculate(ssBonuses, Collections.singleton(ereFactor));

			for (ContextVariable var : new ContextVariable[] { ContextVariable.CGC_BASE, ContextVariable.CGP_BASE, }) {
				SalaryData[] salaryData = salary.getSalaryDatas().stream()
						.filter(d -> AonStringUtils.equals(d.getName(), var.getName()))
						.sorted((d1, d2) -> d1.getStartDate().compareTo(d2.getStartDate())).toArray(SalaryData[]::new);

				assertEquals(1, salaryData.length,var.getName());
				assertEquals(_11122020, salaryData[0].getStartDate(),var.getName());
				assertEquals(_31122020, salaryData[0].getEndDate(),var.getName());
			}

			for (ContextVariable var : new ContextVariable[] { ContextVariable.CGC_BASE_ENTERPRISE,
					ContextVariable.CGP_BASE_ENTERPRISE, }) {
				SalaryData[] salaryData = salary.getSalaryDatas().stream()
						.filter(d -> AonStringUtils.equals(d.getName(), var.getName()))
						.sorted((d1, d2) -> d1.getStartDate().compareTo(d2.getStartDate())).toArray(SalaryData[]::new);

				assertEquals(2, salaryData.length,var.getName());
				assertEquals(_01122020, salaryData[0].getStartDate(),var.getName());
				assertEquals(_10122020, salaryData[0].getEndDate(),var.getName());

				assertEquals(_11122020, salaryData[1].getStartDate(),var.getName());
				assertEquals(_31122020, salaryData[1].getEndDate(),var.getName());
			}

			for (SalaryPayment payment : salary.getSalaryPayments()) {
				System.out.println(payment.getDescription() + ": " + payment.getAmount() + ", " + payment.getQuote());
			}

			double totalCost = 0.00;
			for (SalaryCost cost : salary.getSalaryCosts()) {
				totalCost += cost.getAmount();
				System.out.println(cost.getName() + ": " + cost.getAmount());
			}

			assertEquals(1, salary.getSalaryBonus().size());
			double totalBonus = 0.00;
			for (SalaryBonus bonus : salary.getSalaryBonus()) {
				totalBonus += bonus.getAmount();
				System.out.println(bonus.getDescription() + ": " + bonus.getAmount());
			}

			System.out.println("CUOTA EMPRESARIAL :" + totalCost);

			assertEquals(totalCost * 9 / 30, totalBonus, DELTA);

			assertEquals(totalCost * 21 / 30, salary.getTotalEnterprise(), DELTA);
		}
	}

	@Test
	public void testIdcplnssIVBonusII() throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException,
			ExpressionException, SQLException, SalaryException {

		try (InputStream is = IdcTest.class.getResourceAsStream("idcplnssIV.pdf")) {
			Collection<PEC> ssBonuses = Idcplnss.getSSBonuses(is);
			assertEquals(1, ssBonuses.size());
			// EXONE.ERE.F.MAY.COMP (100,00%) 01-12-2020 10-12-2020

			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			calendar.set(Calendar.MONTH, Calendar.DECEMBER);
			calendar.set(Calendar.YEAR, 2020);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			Date _01122020 = calendar.getTime();

			calendar.set(Calendar.DAY_OF_MONTH, 10);
			Date _10122020 = calendar.getTime();

			calendar.set(Calendar.DAY_OF_MONTH, 11);
			Date _11122020 = calendar.getTime();

			calendar.set(Calendar.DAY_OF_MONTH, 31);
			Date _31122020 = calendar.getTime();

			Salary salary = calculate(ssBonuses);

			for (ContextVariable var : new ContextVariable[] { ContextVariable.CGC_BASE, ContextVariable.CGP_BASE,

//					ContextVariable.CGC_EMPLOYEE,
//					ContextVariable.CGC_ENTERPRISE,
//					ContextVariable.FP_EMPLOYEE,
//					ContextVariable.FP_ENTERPRISE,
//					
//					ContextVariable.EMPLOYEE_QUOTA,
//					ContextVariable.ENTERPRISE_QUOTA,
			}) {
				SalaryData[] salaryData = salary.getSalaryDatas().stream()
						.filter(d -> AonStringUtils.equals(d.getName(), var.getName()))
						.sorted((d1, d2) -> d1.getStartDate().compareTo(d2.getStartDate())).toArray(SalaryData[]::new);

				assertEquals(2, salaryData.length,var.getName());
				assertEquals(_01122020, salaryData[0].getStartDate(),var.getName());
				assertEquals(_10122020, salaryData[0].getEndDate(),var.getName());
				assertEquals(_11122020, salaryData[1].getStartDate(),var.getName());
				assertEquals(_31122020, salaryData[1].getEndDate(),var.getName());
			}

			double totalCost = 0.00;
			for (SalaryCost cost : salary.getSalaryCosts()) {
				totalCost += cost.getAmount();
				System.out.println(cost.getName() + ": " + cost.getAmount());
			}

			assertEquals(1, salary.getSalaryBonus().size());
			double totalBonus = 0.00;
			for (SalaryBonus bonus : salary.getSalaryBonus()) {
				totalBonus += bonus.getAmount();
				System.out.println(bonus.getDescription() + ": " + bonus.getAmount());
			}

			System.out.println("CUOTA EMPRESARIAL :" + totalCost);

			assertEquals(totalCost * 10 / 31, totalBonus, DELTA);

			assertEquals(totalCost * 21 / 31, salary.getTotalEnterprise(), DELTA);
		}
	}

	@Test
	@Disabled
	public void testIdcplnssVBonusI() throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException,
			ExpressionException, SQLException, SalaryException {
		testIdcplnssVBonus(ContextVariable.ERE_FACTOR);
	}

	@Test
	@Disabled
	public void testIdcplnssVBonusII() throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException,
			ExpressionException, SQLException, SalaryException {
		testIdcplnssVBonus(ContextVariable.ERE_FACTOR_FORCE);
	}

	@Test
	public void testIdcplnssVBonusIII() throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException,
			ExpressionException, SQLException, SalaryException {
		testIdcplnssVBonus(ContextVariable.ERE_FACTOR_FORCE_OFF);
	}

	public void testIdcplnssVBonus(ContextVariable ereFactorVariable)
			throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException, ExpressionException, SQLException,
			SalaryException {

		try (InputStream is = IdcTest.class.getResourceAsStream("idcplnssV.pdf")) {
			Collection<PEC> ssBonuses = Idcplnss.getSSBonuses(is);
			assertEquals(2, ssBonuses.size());
			// EXONE.ERE.F.MAY.COMP (100,00%) 01-12-2020 10-12-2020
			// EXONE.ERE.F.MAY.PARC ( 55,00%)

			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			calendar.set(Calendar.MONTH, Calendar.DECEMBER);
			calendar.set(Calendar.YEAR, 2020);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			Date _01122020 = calendar.getTime();

			calendar.set(Calendar.DAY_OF_MONTH, 10);
			Date _10122020 = calendar.getTime();

			calendar.set(Calendar.DAY_OF_MONTH, 11);
			Date _11122020 = calendar.getTime();

			calendar.set(Calendar.DAY_OF_MONTH, 31);
			Date _31122020 = calendar.getTime();

			Collection<Data> ereFactors = new ArrayList<Data>();
			ereFactors.add(new Data() {
				{
					expression = "1.00";
					endDate = _10122020;
					startDate = _01122020;
					name = ereFactorVariable.getName();
				}
			});
			ereFactors.add(new Data() {
				{
					expression = "0.55";
					endDate = _31122020;
					startDate = _11122020;
					name = ereFactorVariable.getName();
				}
			});

			Salary salary = calculate(ssBonuses, ereFactors);

			for (ContextVariable var : new ContextVariable[] { ContextVariable.CGC_BASE, ContextVariable.CGP_BASE, }) {
				SalaryData[] salaryData = salary.getSalaryDatas().stream()
						.filter(d -> AonStringUtils.equals(d.getName(), var.getName()))
						.sorted((d1, d2) -> d1.getStartDate().compareTo(d2.getStartDate())).toArray(SalaryData[]::new);

				assertEquals(1, salaryData.length,var.getName());
				assertEquals(_11122020, salaryData[0].getStartDate(),var.getName());
				assertEquals(_31122020, salaryData[0].getEndDate(),var.getName());
			}

			for (ContextVariable var : new ContextVariable[] { ContextVariable.CGC_BASE_ENTERPRISE,
					ContextVariable.CGP_BASE_ENTERPRISE, }) {
				SalaryData[] salaryData = salary.getSalaryDatas().stream()
						.filter(d -> AonStringUtils.equals(d.getName(), var.getName()))
						.sorted((d1, d2) -> d1.getStartDate().compareTo(d2.getStartDate())).toArray(SalaryData[]::new);

				assertEquals(2, salaryData.length,var.getName());
				assertEquals(_01122020, salaryData[0].getStartDate(),var.getName());
				assertEquals(_10122020, salaryData[0].getEndDate(),var.getName());
				assertEquals(_11122020, salaryData[1].getStartDate(),var.getName());
				assertEquals(_31122020, salaryData[1].getEndDate(),var.getName());
			}

			for (SalaryPayment payment : salary.getSalaryPayments()) {
				System.out.println(payment.getDescription() + ": " + payment.getAmount() + ", " + payment.getQuote());
			}

			double totalCost = 0.00;
			for (SalaryCost cost : salary.getSalaryCosts()) {
				totalCost += cost.getAmount();
				System.out.println(cost.getName() + ": " + cost.getAmount());
			}

			assertEquals(2, salary.getSalaryBonus().size());
			double totalBonus = 0.00;
			for (SalaryBonus bonus : salary.getSalaryBonus()) {
				totalBonus += bonus.getAmount();
				System.out.println(bonus.getDescription() + ": " + bonus.getAmount());
			}

			System.out.println("CUOTA EMPRESARIAL :" + totalCost);

			assertEquals(totalCost * 9 / 30 + totalCost * 21 / 30 * 0.55, totalBonus, DELTA);

			assertEquals(totalCost * 21 / 30 * 0.45, salary.getTotalEnterprise(), DELTA);
		}
	}

	protected Salary calculate(Collection<PEC> ssBonuses) throws ExpressionException, SQLException, SalaryException {
		return calculate(ssBonuses, Collections.emptyList());
	}

	protected Salary calculate(Collection<PEC> ssBonuses, Collection<Data> datas)
			throws ExpressionException, SQLException, SalaryException {
		Date bonusDate = ssBonuses.stream().map(b -> b.getStartDate()).sorted().findFirst().orElseThrow();
		return calculate(ssBonuses, datas, bonusDate);
	}

	protected Salary calculate(Collection<PEC> ssPECs, Collection<Data> datas, Date date)
			throws ExpressionException, SQLException, SalaryException {
		return calculate(ssPECs, datas, date, new SalaryBuilder());
	}

	protected Salary calculate(Collection<PEC> ssPECs, Collection<Data> datas, String[] payments, Date startDate,
			Date endDate) throws ExpressionException, SQLException, SalaryException {
		return calculate(ssPECs, datas, payments, startDate, endDate,
				new RoundSalaryBuilder<Salary>(new SalaryBuilder(), d -> d.setScale(2, RoundingMode.HALF_UP)));
	}

	protected Salary calculate(Collection<PEC> ssPECs, Collection<Data> datas, Date date,
			ISalaryBuilder<Salary> salaryBuilder) throws ExpressionException, SQLException, SalaryException {
		return calculate(ssPECs, datas, date, salaryBuilder, null);
	}

	protected Salary calculate(Collection<PEC> ssPECs, Collection<Data> datas, Date date,
		ISalaryBuilder<Salary> salaryBuilder, GenericContractSalaryCalculator.IListener listener) throws ExpressionException, SQLException, SalaryException {
	    java.sql.Date startDate = toSQL(AonDateUtils.getFirstDayOfMonth(date));
	    java.sql.Date endDate = toSQL(AonDateUtils.getLastDayOfMonth(date));

	    Connection connection = getConnection();
	    AONContext aonContext = new AONContext(connection);
	    ContractRecord contract = newContract(aonContext, toSQL(startDate), ssPECs, datas);

	    ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, startDate, endDate,
		    endDate, contract);
	    SmartContractSalaryCalculator<Salary> builder = new SmartContractSalaryCalculator<Salary>(salaryBuilder);
	    builder.setListener(listener);
	    Salary salary = builder.calculate(ctx);
	    return salary;
	}

	protected Salary calculate(Collection<PEC> ssPECs, Collection<Data> datas, String[] payments, Date startDate,
			Date endDate, ISalaryBuilder<Salary> salaryBuilder)
			throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		ContractRecord contract = newContract(aonContext, toSQL(startDate), ssPECs, datas, payments);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, toSQL(startDate),
				toSQL(endDate), toSQL(endDate), contract);
		SmartContractSalaryCalculator<Salary> builder = new SmartContractSalaryCalculator<Salary>(salaryBuilder);
		Salary salary = builder.calculate(ctx);
		return salary;
	}

	@Test
	public void testIdcIIIBonus() throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException,
			ExpressionException, SalaryException, SQLException {

		try (InputStream is = IdcTest.class.getResourceAsStream("idcIII.pdf")) {
			Collection<PEC> ssBonuses = Idc.getSSPECs(is);
			assertEquals(4, ssBonuses.size());

			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);

			calendar.set(Calendar.YEAR, 2020);
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			calendar.set(Calendar.MONTH, Calendar.MARCH);

			Date march = calendar.getTime();

			Salary salary = calculate(ssBonuses, Collections.emptyList(), march);
			double totalCost = salary.getSalaryCosts().stream().collect(Collectors.summingDouble(c -> c.getAmount()));
			assertEquals(totalCost / 31.00 * 14, salary.getTotalEnterprise(), DELTA);

			calendar.set(Calendar.MONTH, Calendar.APRIL);
			Date april = calendar.getTime();
			salary = calculate(ssBonuses, Collections.emptyList(), april);
			assertEquals(0.00, salary.getTotalEnterprise(), DELTA);

			calendar.set(Calendar.MONTH, Calendar.MAY);
			Date may = calendar.getTime();
			salary = calculate(ssBonuses, Collections.emptyList(), may);
			totalCost = salary.getSalaryCosts().stream().collect(Collectors.summingDouble(c -> c.getAmount()));
			assertEquals(totalCost / 31.00 * 19.00 * 0.40, salary.getTotalEnterprise(), DELTA);

			calendar.set(Calendar.MONTH, Calendar.JUNE);
			Date june = calendar.getTime();
			salary = calculate(ssBonuses, Collections.emptyList(), june);
			totalCost = salary.getSalaryCosts().stream().collect(Collectors.summingDouble(c -> c.getAmount()));
			assertEquals(totalCost * 0.55, salary.getTotalEnterprise(), DELTA);
		}
	}

	@Test
	public void testIdcIVBonus() throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException,
			ExpressionException, SalaryException, SQLException {

		try (InputStream is = IdcTest.class.getResourceAsStream("idcIV.pdf")) {
			Collection<PEC> ssBonuses = Idc.getSSPECs(is);
			assertEquals(0, ssBonuses.size());
			// ***SIN SITUACIONES***
		}
	}

	@Test
	public void testIdcVBonus() throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException,
			ExpressionException, SalaryException, SQLException {

		try (InputStream is = IdcTest.class.getResourceAsStream("idcV.pdf")) {
			Collection<PEC> ssBonuses = Idc.getSSPECs(is);
			assertEquals(1, ssBonuses.size());

			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);

			calendar.set(Calendar.YEAR, 2021);
			calendar.set(Calendar.DAY_OF_MONTH, 18);
			calendar.set(Calendar.MONTH, Calendar.JANUARY);

			Date january18 = calendar.getTime();
			PEC bonus = ssBonuses.stream().findFirst().get();

			assertEquals(january18, bonus.getStartDate());
			assertNull(bonus.getEndDate());

			calendar.set(Calendar.DAY_OF_MONTH, 31);
			Date january31 = calendar.getTime();

			Salary salary = calculate(ssBonuses, Collections.emptyList(),
					new String[] { "710.47 * DIAS_TRABAJADOS / DIAS_MES" }, january18, january31);
			double totalCost = salary.getSalaryCosts().stream().collect(Collectors.summingDouble(c -> c.getAmount()));

			assertEquals(0.00, salary.getTotalEnterprise(), DELTA);

			double totalBonus = salary.getSalaryBonus().stream().collect(Collectors.summingDouble(c -> c.getAmount()));

			assertEquals(Math.min(10.72 * 14.00, totalCost), totalBonus, DELTA);

		}
	}

	@Test
	public void testIdcVBonusI() throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException,
			ExpressionException, SalaryException, SQLException {

		try (InputStream is = IdcTest.class.getResourceAsStream("idcV.pdf")) {
			Collection<PEC> ssBonuses = Idc.getSSPECs(is);
			assertEquals(1, ssBonuses.size());

			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);

			calendar.set(Calendar.YEAR, 2021);
			calendar.set(Calendar.DAY_OF_MONTH, 18);
			calendar.set(Calendar.MONTH, Calendar.JANUARY);

			Date january18 = calendar.getTime();
			PEC bonus = ssBonuses.stream().findFirst().get();

			assertEquals(january18, bonus.getStartDate());
			assertNull(bonus.getEndDate());

			calendar.set(Calendar.DAY_OF_MONTH, 31);
			Date january31 = calendar.getTime();

			Salary salary = calculate(ssBonuses, Collections.emptyList(),
					new String[] { "2500.00* DIAS_TRABAJADOS / DIAS_MES" }, january18, january31);
			Double totalCost = salary.getSalaryCosts().stream().collect(Collectors.summingDouble(c -> c.getAmount()));

			assertEquals(totalCost - (10.72 * 14), salary.getTotalEnterprise(), DELTA);

			Double totalBonus = salary.getSalaryBonus().stream().collect(Collectors.summingDouble(c -> c.getAmount()));

			assertEquals(10.72 * 14.00, totalBonus, DELTA);
		}
	}

	@Test
	public void testIdcVBonusIII() throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException,
			ExpressionException, SalaryException, SQLException {

		try (InputStream is = IdcTest.class.getResourceAsStream("idcV.pdf")) {
			Collection<PEC> ssBonuses = Idc.getSSPECs(is);
			assertEquals(1, ssBonuses.size());

			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);

			calendar.set(Calendar.YEAR, 2022);
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			calendar.set(Calendar.MONTH, Calendar.FEBRUARY);

			Date february1 = calendar.getTime();

			calendar.set(Calendar.DAY_OF_MONTH, 28);
			Date february31 = calendar.getTime();

			Salary salary = calculate(ssBonuses, Collections.emptyList(),
					new String[] { "2500.00* DIAS_TRABAJADOS / DIAS_MES" }, february1, february31);
			Double totalCost = salary.getSalaryCosts().stream().collect(Collectors.summingDouble(c -> c.getAmount()));

			assertEquals(totalCost - 321.50, salary.getTotalEnterprise(), DELTA);

			Double totalBonus = salary.getSalaryBonus().stream().collect(Collectors.summingDouble(c -> c.getAmount()));

			assertEquals(321.50, totalBonus, DELTA);
		}
	}

	@Test
	public void testIdcVIBonus() throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException,
			ExpressionException, SalaryException, SQLException {

		try (InputStream is = IdcTest.class.getResourceAsStream("idcVI.pdf")) {
			Collection<PEC> ssPECs = Idc.getSSPECs(is);
			assertEquals(1 + 2 + 3 + 1 + 2 , ssPECs.size());

			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);

			calendar.set(Calendar.YEAR, 2020);
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			calendar.set(Calendar.MONTH, Calendar.OCTOBER);

			Date october10 = calendar.getTime();
			PEC bonus = ssPECs.stream().findFirst().get();

			assertEquals(october10, bonus.getStartDate());
			assertNull(bonus.getEndDate());

			calendar.set(Calendar.DAY_OF_MONTH, 1);
			Date october = calendar.getTime();

			Salary salary = calculate(ssPECs, Collections.emptyList(), october);

			double totalCost = salary.getSalaryCosts().stream()
					.peek(c -> assertNotEquals(c.getType(), DeductionType.FOGASA))
					.peek(c -> assertNotEquals(c.getType(), DeductionType.UNEMPLOYMENT))
					.peek(c -> assertNotEquals(c.getType(), DeductionType.JOB_TRAINING))
					// .peek(c -> System.out.println(c.getCostConcept() +" : " + c.getAmount() +", "
					// + c.getType()) )
					.filter(c -> c.getType() != DeductionType.COMMON_CONTINGENCY)
					.filter(c -> c.getType() != DeductionType.PROFESSIONAL_CONTINGENCY)
					.collect(Collectors.summingDouble(c -> c.getAmount()));

			double totalDeduction = salary.getSalaryDeductions().stream()
					.peek(c -> assertNotEquals(c.getType(), DeductionType.FOGASA))
					.peek(c -> assertNotEquals(c.getType(), DeductionType.UNEMPLOYMENT))
					.peek(c -> assertNotEquals(c.getType(), DeductionType.JOB_TRAINING))
					.peek(c -> System.out.println(c.getDeductionConcept() + " : " + c.getAmount() + ", " + c.getType()))
					// .filter( c -> c.getType() != DeductionType.COMMON_CONTINGENCY )
					.filter(c -> c.getType() != DeductionType.PROFESSIONAL_CONTINGENCY)
					.collect(Collectors.summingDouble(c -> c.getAmount()));

			assertEquals(totalCost, salary.getTotalEnterprise(), DELTA);
			assertEquals(totalDeduction, salary.getSocialSecurityContributions(), DELTA);

		}
	}

	@Test
	public void testIdcVIIBonus() throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException,
			ExpressionException, SalaryException, SQLException {

		try (InputStream is = IdcTest.class.getResourceAsStream("idcVII.pdf")) {
			Collection<PEC> ssBonuses = Idc.getSSPECs(is);
			assertEquals(1, ssBonuses.size());

			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);

			calendar.set(Calendar.YEAR, 2021);
			calendar.set(Calendar.DAY_OF_MONTH, 28);
			calendar.set(Calendar.MONTH, Calendar.MAY);

			Date may28 = calendar.getTime();
			PEC bonus = ssBonuses.stream().findFirst().get();

			assertEquals(may28, bonus.getStartDate());
			assertNull(bonus.getEndDate());

			calendar.set(Calendar.DAY_OF_MONTH, 1);
			Date may = calendar.getTime();

			Salary salary = calculate(ssBonuses, Collections.emptyList(), may);

			double totalCost = salary.getSalaryCosts().stream().collect(Collectors.summingDouble(c -> c.getAmount()));

			double totalDeduction = salary.getSalaryDeductions().stream()
					.filter(d -> d.getType() != DeductionType.BONUS)
					.filter(d -> !AonStringUtils.equals(d.getDeductionConcept(), "CGC"))
					.collect(Collectors.summingDouble(c -> c.getAmount()));

			salary.getSalaryDeductions().forEach(
					d -> System.out.println(d.getDeductionConcept() + " : " + d.getAmount() + ", " + d.getType()));

			assertEquals(totalCost / 31 * 27, salary.getTotalEnterprise(), DELTA);
			// assertEquals(totalDeduction, salary.getSocialSecurityContributions(), DELTA);

		}
	}

	@Test
	@Disabled
	public void testIdcVBonusII() throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException,
			ExpressionException, SalaryException, SQLException {

		try (InputStream is = IdcTest.class.getResourceAsStream("idcV.pdf")) {
			Collection<PEC> ssBonuses = Idc.getSSPECs(is);
			assertEquals(1, ssBonuses.size());

			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);

			calendar.set(Calendar.YEAR, 2021);
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			calendar.set(Calendar.MONTH, Calendar.FEBRUARY);
			Date febreruary = calendar.getTime();

			java.sql.Date startDate = toSQL(AonDateUtils.getFirstDayOfMonth(febreruary));
			java.sql.Date endDate = toSQL(AonDateUtils.getLastDayOfMonth(febreruary));

			Connection connection = getConnection();
			AONContext aonContext = new AONContext(connection);
			ContractRecord contract = newContract(aonContext, toSQL(startDate), ssBonuses);

			ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, startDate, endDate,
					endDate, contract);
			SmartContractSalaryCalculator<Salary> builder = new SmartContractSalaryCalculator<Salary>(
					new SalaryBuilder());
			Salary salary = builder.calculate(ctx);

			salary.getSalaryCosts().forEach(p -> System.out.println(p.getCostConcept() + " = " + p.getAmount()));

			double totalCost = salary.getSalaryCosts().stream().collect(Collectors.summingDouble(c -> c.getAmount()));

			assertEquals(totalCost - 321.50, salary.getTotalEnterprise(), DELTA);

			java.sql.Date startItDate = toSQL(AonDateUtils.add(startDate, Calendar.DAY_OF_MONTH, 9));
			java.sql.Date endItDate = toSQL(AonDateUtils.add(startDate, Calendar.DAY_OF_MONTH, 14));
			addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startItDate, endItDate, null);

			ctx = getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);
			builder = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder());
			salary = builder.calculate(ctx);

			// salary.getSalaryDatas().forEach(s -> System.out.println(s.getName() +" = " +
			// s.getExpression() ));

			totalCost = salary.getSalaryCosts().stream().collect(Collectors.summingDouble(c -> c.getAmount()));
			assertEquals(totalCost - 321.50, salary.getTotalEnterprise(), DELTA);
		}
	}

	@Test
	public void testIdcplnssIIIBonus() throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException,
			ExpressionException, SalaryException, SQLException {

		try (InputStream is = IdcTest.class.getResourceAsStream("idcplnssIII.pdf")) {
			Collection<PEC> ssBonuses = Idcplnss.getSSBonuses(is);
			assertEquals(1, ssBonuses.size());
			// EXONE.ERE.F.MAY.COMP (100,00%)

			Salary salary = calculate(ssBonuses);
			assertEquals(0.00, salary.getTotalEnterprise(), DELTA);

			double totalCost = 0.00;
			for (SalaryCost cost : salary.getSalaryCosts()) {
				totalCost += cost.getAmount();
				System.out.println(cost.getName() + ": " + cost.getAmount());
			}

			assertEquals(1, salary.getSalaryBonus().size());
			double totalBonus = 0.00;
			for (SalaryBonus bonus : salary.getSalaryBonus()) {
				totalBonus += bonus.getAmount();
				System.out.println(bonus.getDescription() + ": " + bonus.getAmount());
			}

			assertEquals(totalCost, totalBonus, DELTA);

		}
	}

	@Test
	public void testIdcplnssIBonus() throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException {
		try (InputStream is = IdcTest.class.getResourceAsStream("idcplnssI.pdf")) {
			Collection<PEC> ssBonuses = Idcplnss.getSSBonuses(is);
			assertEquals(0, ssBonuses.size());
		}
	}

	@Test
	public void testIdcplnssIIBonus() throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException,
			ExpressionException, SalaryException, SQLException {
		try (InputStream is = IdcTest.class.getResourceAsStream("idcplnssII.pdf")) {
			Collection<PEC> ssBonuses = Idcplnss.getSSBonuses(is);
			assertEquals(1 + 2 + 3 + 1, ssBonuses.size());
			assertEquals(1, ssBonuses.stream().filter(pec -> isBonus(pec)).count());
			assertEquals(2 + 1, ssBonuses.stream().filter(pec -> isDeduction(pec)).count());
			assertEquals(3, ssBonuses.stream().filter(pec -> isCost(pec)).count());

			Salary salary = calculate(ssBonuses);

			System.out.println(salary.getTotalEnterprise());

		}
	}

	@Test
	public void testIdcplcccTrabajadoresTramos()
			throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException, JAXBException {
		try (InputStream is = IdcTest.class.getResourceAsStream("idcplccc.pdf")) {
			TrabajadoresTramos trabajadoresTramos = Idcplccc.geTrabajadoresTramos(is, new TrabajadoresTramosCallback() {
			});
			
			marshal(trabajadoresTramos, System.out);

			assertEquals(trabajadoresTramos.getAutorizado(), "00228115");

			Liquidacion liquidacion = trabajadoresTramos.getLiquidacion();
			
			assertBonificacionFormacionContinua763(liquidacion);

			assertEquals("0111", liquidacion.getCcc().getRegimen());
			assertEquals("01", liquidacion.getCcc().getProvincia());
			assertEquals("105360062", liquidacion.getCcc().getNumero());

			assertEquals("10", liquidacion.getPeriodoDesde().getMes());
			assertEquals("2020", liquidacion.getPeriodoDesde().getAnho());
			assertEquals("10", liquidacion.getPeriodoHasta().getMes());
			assertEquals("2020", liquidacion.getPeriodoHasta().getAnho());

			assertEquals(1, liquidacion.getLiquidacionMes().size());

			LiquidacionMes liquidacionesMes = liquidacion.getLiquidacionMes().get(0);
			assertEquals("10", liquidacionesMes.getMesLiquidativo().getMes());
			assertEquals("2020", liquidacionesMes.getMesLiquidativo().getAnho());

			Trabajadores trabajadores = liquidacionesMes.getTrabajadores();
			assertEquals(8, trabajadores.getTrabajador().size());

			for (Trabajador trabajador : trabajadores.getTrabajador()) {

				assertEquals(1, trabajador.getTramos().getTramo().size());
				for (Tramo tramo : trabajador.getTramos().getTramo()) {
					assertEquals("01", tramo.getFechaDesde().getDia());
					assertEquals("10", tramo.getFechaDesde().getMes());
					assertEquals("2020", tramo.getFechaDesde().getAnho());
					assertEquals("31", tramo.getFechaHasta().getDia());
					assertEquals("10", tramo.getFechaHasta().getMes());
					assertEquals("2020", tramo.getFechaHasta().getAnho());

					int datosSolicatodos = assertTramoActivoNormal(tramo);
					assertDatosSolicitadosCount(datosSolicatodos, tramo);
				}
			}

		}
	}

	private void assertBonificacionFormacionContinua763(Liquidacion liquidacion) {
		DatoSolicitado datoSolicitado = 
		liquidacion.getDatosLiquidacion().getDatoSolicitado().get(0);
		Objects.equals(datoSolicitado.getCodigo(), "763");
		Objects.equals(datoSolicitado.getTipoDato(), "C");
		Objects.equals(datoSolicitado.getIndicadorObligatoriedad(), "P");
	}

	@Test
	public void testIdcplcccTrabajadoresTramosI()
			throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException, JAXBException {
		try (InputStream is = IdcTest.class.getResourceAsStream("idcplcccI.pdf")) {
			TrabajadoresTramos trabajadoresTramos = Idcplccc.geTrabajadoresTramos(is, new TrabajadoresTramosCallback() {
				@Override
				public boolean isScholarEmployee(String ssNum, String ccc, Date start, Date end) {
					return true;
				}
			});

			marshall(trabajadoresTramos, System.out);

			assertEquals(trabajadoresTramos.getAutorizado(), "00228115");

			Liquidacion liquidacion = trabajadoresTramos.getLiquidacion();

			assertBonificacionFormacionContinua763(liquidacion);

			assertEquals("0111", liquidacion.getCcc().getRegimen());
			assertEquals("01", liquidacion.getCcc().getProvincia());
			assertEquals("105577910", liquidacion.getCcc().getNumero());

			assertEquals("10", liquidacion.getPeriodoDesde().getMes());
			assertEquals("2020", liquidacion.getPeriodoDesde().getAnho());
			assertEquals("10", liquidacion.getPeriodoHasta().getMes());
			assertEquals("2020", liquidacion.getPeriodoHasta().getAnho());

			assertEquals(1, liquidacion.getLiquidacionMes().size());

			LiquidacionMes liquidacionesMes = liquidacion.getLiquidacionMes().get(0);
			assertEquals("10", liquidacionesMes.getMesLiquidativo().getMes());
			assertEquals("2020", liquidacionesMes.getMesLiquidativo().getAnho());

			Trabajadores trabajadores = liquidacionesMes.getTrabajadores();
			assertEquals(4, trabajadores.getTrabajador().size());

			for (Trabajador trabajador : trabajadores.getTrabajador()) {
				assertEquals(1, trabajador.getTramos().getTramo().size());
				for (Tramo tramo : trabajador.getTramos().getTramo()) {
					assertEquals(0, tramo.getDatosTramo().getDato().size());
				}

			}

		}
	}

	@Test
	public void testIdcContractData() throws IOException, UnknownPDFException {
		try (InputStream is = IdcTest.class.getResourceAsStream("idc.pdf")) {
			Map<ContextVariable, Object> contractData = Idc.getContractData(is);
			assertEquals(contractData.get(ContextVariable.TC2), "100");
			assertEquals(contractData.get(ContextVariable.QUOTE_GROUP), "08");
			assertEquals(contractData.get(ContextVariable.IT_PERCENT), 1.70);
			assertEquals(contractData.get(ContextVariable.IMS_PERCENT), 1.30);
			assertEquals(contractData.get(ContextVariable.UNEMPLOY_EMPLOYEE_PERCENT), 1.55);
			assertEquals(contractData.get(ContextVariable.UNEMPLOY_ENTERPRISE_PERCENT), 5.50);
		}
	}

	@Test
	public void testIdcContractDataI() throws IOException, UnknownPDFException {
		try (InputStream is = IdcTest.class.getResourceAsStream("idcI.pdf")) {
			Map<ContextVariable, Object> contractData = Idc.getContractData(is);
			assertEquals(contractData.get(ContextVariable.TC2), "189");
			assertEquals(contractData.get(ContextVariable.QUOTE_GROUP), "10");
			assertEquals(contractData.get(ContextVariable.IT_PERCENT), 1.70);
			assertEquals(contractData.get(ContextVariable.IMS_PERCENT), 1.30);
			assertEquals(contractData.get(ContextVariable.UNEMPLOY_EMPLOYEE_PERCENT), 1.55);
			assertEquals(contractData.get(ContextVariable.UNEMPLOY_ENTERPRISE_PERCENT), 5.50);
			assertFalse(contractData.containsKey(ContextVariable.OCCUPATION));
			
		}
	}

	@Test
	public void testIdcContractDataII() throws IOException, UnknownPDFException {
		try (InputStream is = IdcTest.class.getResourceAsStream("idcII.pdf")) {
			Map<ContextVariable, Object> contractData = Idc.getContractData(is);
			assertEquals(contractData.get(ContextVariable.TC2), "100");
			assertEquals(contractData.get(ContextVariable.QUOTE_GROUP), "01");
			assertEquals(contractData.get(ContextVariable.OCCUPATION), "a");
		}
	}

	@Test
	public void testIdcContractDataIII() throws IOException, UnknownPDFException {
		try (InputStream is = IdcTest.class.getResourceAsStream("idcIII.pdf")) {
			Map<ContextVariable, Object> contractData = Idc.getContractData(is);
			assertEquals(contractData.get(ContextVariable.TC2), "289");
			assertEquals(contractData.get(ContextVariable.QUOTE_GROUP), "07");
			assertEquals(contractData.get(ContextVariable.PARTIAL_FACTOR), 0.750);
			assertEquals(contractData.get(ContextVariable.IT_PERCENT), 1.70);
			assertEquals(contractData.get(ContextVariable.IMS_PERCENT), 1.30);
			assertEquals(contractData.get(ContextVariable.UNEMPLOY_EMPLOYEE_PERCENT), 1.55);
			assertEquals(contractData.get(ContextVariable.UNEMPLOY_ENTERPRISE_PERCENT), 5.50);
			assertFalse(contractData.containsKey(ContextVariable.OCCUPATION));
		}
	}

	@Test
	public void testIdcContractDataIV() throws IOException, UnknownPDFException {
		try (InputStream is = IdcTest.class.getResourceAsStream("idcIV.pdf")) {
			Map<ContextVariable, Object> contractData = Idc.getContractData(is);
			assertEquals(contractData.get(ContextVariable.TC2), "100");
			assertEquals(contractData.get(ContextVariable.QUOTE_GROUP), "02");
			assertEquals(contractData.get(ContextVariable.IT_PERCENT), 0.80);
			assertEquals(contractData.get(ContextVariable.IMS_PERCENT), 0.70);
			assertEquals(contractData.get(ContextVariable.UNEMPLOY_EMPLOYEE_PERCENT), 1.55);
			assertEquals(contractData.get(ContextVariable.UNEMPLOY_ENTERPRISE_PERCENT), 5.50);
			assertEquals(contractData.get(ContextVariable.OCCUPATION), "a");
		}
	}

	@Test
	public void testIdcContractDataXII() throws IOException, UnknownPDFException {
		try (InputStream is = IdcTest.class.getResourceAsStream("idcXII.pdf")) {
			Map<ContextVariable, Object> contractData = Idc.getContractData(is);
			assertEquals(contractData.get(ContextVariable.TC2), "189");
			assertEquals(contractData.get(ContextVariable.QUOTE_GROUP), "09");
			assertEquals(contractData.get(ContextVariable.IT_PERCENT), 3.35);
			assertEquals(contractData.get(ContextVariable.IMS_PERCENT), 3.35);
			assertEquals(contractData.get(ContextVariable.UNEMPLOY_EMPLOYEE_PERCENT), 1.55);
			assertEquals(contractData.get(ContextVariable.UNEMPLOY_ENTERPRISE_PERCENT), 5.50);
			assertFalse(contractData.containsKey(ContextVariable.OCCUPATION));
		}
	}

	@Test
	public void testIdcIXContractData() throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException,
			ExpressionException, SalaryException, SQLException {

		try (InputStream is = IdcTest.class.getResourceAsStream("idcIX.pdf")) {
			Map<ContextVariable, Object> contractData = Idc.getContractData(is);
			assertFalse(contractData.containsKey(ContextVariable.OCCUPATION));
		}
	}


	@Test
	public void testIdcplcccTrabajadoresTramosII()
			throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException, JAXBException {
		try (InputStream is = IdcTest.class.getResourceAsStream("idcplcccII.pdf")) {
			TrabajadoresTramos trabajadoresTramos = Idcplccc.geTrabajadoresTramos(is, new TrabajadoresTramosCallback() {
				@Override
				public boolean isScholarEmployee(String ssNum, String ccc, Date start, Date end) {
					return true;
				}
			});

			marshall(trabajadoresTramos, System.out);

			assertEquals(trabajadoresTramos.getAutorizado(), "00228115");

			Liquidacion liquidacion = trabajadoresTramos.getLiquidacion();

			assertEquals("0111", liquidacion.getCcc().getRegimen());
			assertEquals("01", liquidacion.getCcc().getProvincia());
			assertEquals("105577910", liquidacion.getCcc().getNumero());

			assertEquals("10", liquidacion.getPeriodoDesde().getMes());
			assertEquals("2020", liquidacion.getPeriodoDesde().getAnho());
			assertEquals("10", liquidacion.getPeriodoHasta().getMes());
			assertEquals("2020", liquidacion.getPeriodoHasta().getAnho());

			assertEquals(1, liquidacion.getLiquidacionMes().size());

			LiquidacionMes liquidacionesMes = liquidacion.getLiquidacionMes().get(0);
			assertEquals("10", liquidacionesMes.getMesLiquidativo().getMes());
			assertEquals("2020", liquidacionesMes.getMesLiquidativo().getAnho());

			Trabajadores trabajadores = liquidacionesMes.getTrabajadores();
			assertEquals(4, trabajadores.getTrabajador().size());

			for (Trabajador trabajador : trabajadores.getTrabajador()) {
				if ("141026133260".equals(trabajador.getNaf()))
					continue;
				assertEquals(1, trabajador.getTramos().getTramo().size());
				for (Tramo tramo : trabajador.getTramos().getTramo()) {
					assertEquals(0, tramo.getDatosTramo().getDato().size());
				}
			}

			for (Trabajador trabajador : trabajadores.getTrabajador()) {
				if (!"141026133260".equals(trabajador.getNaf()))
					continue;
				assertEquals(3, trabajador.getTramos().getTramo().size());
				assertTramoITPagoDelegadoBecarios(trabajador.getTramos().getTramo().get(1));
			}

		}
	}

	@Test
	public void testIdcplcccTrabajadoresTramosIV()
			throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException, JAXBException {
		
		Set<String> ssNums = new HashSet<>();
		ssNums.add("411045821384");
		ssNums.add("411094672204");
		ssNums.add("411099101565");
		ssNums.add("411099573532");
		ssNums.add("411105285519");
		ssNums.add("411106613207");
		//ssNums.add("411106613207");
		
		try (InputStream is = IdcTest.class.getResourceAsStream("idcplcccIV.pdf")) {
			TrabajadoresTramos trabajadoresTramos = Idcplccc.geTrabajadoresTramos(is, new TrabajadoresTramosCallback() {
				
				@Override
				public boolean isPartTimeEmployee(String ssNum, String ccc, Date start, Date end) {
					return ssNums.contains(ssNum);
				}
				
			});

			marshall(trabajadoresTramos, System.out);

			assertEquals(trabajadoresTramos.getAutorizado(), "00088233");

			Liquidacion liquidacion = trabajadoresTramos.getLiquidacion();

			assertEquals("0111", liquidacion.getCcc().getRegimen());
			assertEquals("41", liquidacion.getCcc().getProvincia());
			assertEquals("124028555", liquidacion.getCcc().getNumero());

			assertEquals("10", liquidacion.getPeriodoDesde().getMes());
			assertEquals("2022", liquidacion.getPeriodoDesde().getAnho());
			assertEquals("10", liquidacion.getPeriodoHasta().getMes());
			assertEquals("2022", liquidacion.getPeriodoHasta().getAnho());

			assertEquals(1, liquidacion.getLiquidacionMes().size());

			LiquidacionMes liquidacionesMes = liquidacion.getLiquidacionMes().get(0);
			assertEquals("10", liquidacionesMes.getMesLiquidativo().getMes());
			assertEquals("2022", liquidacionesMes.getMesLiquidativo().getAnho());

			Trabajadores trabajadores = liquidacionesMes.getTrabajadores();
			assertEquals(22, trabajadores.getTrabajador().size());

			for (Trabajador trabajador : trabajadores.getTrabajador()) {
				if (ssNums.contains(trabajador.getNaf())) {
					marshal(trabajador, System.out);
					assertEquals(1, trabajador.getTramos().getTramo().size());
					assertTramoTiempoParcial(trabajador.getTramos().getTramo().get(0));
				}
			}


		}
	}

	@Test
	public void testIdcplnssVIBonus() throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException,
			ExpressionException, SQLException, SalaryException {

		try (InputStream is = IdcTest.class.getResourceAsStream("idcplnssVI.pdf")) {
			Collection<PEC> ssBonuses = Idcplnss.getSSBonuses(is);
			assertEquals(1, ssBonuses.size());

			// EXONE.ERE.F.MAY.COMP (100,00%) 01-12-2020 10-12-2020

			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			calendar.set(Calendar.MONTH, Calendar.JANUARY);
			calendar.set(Calendar.YEAR, 2021);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			Date _01012021 = calendar.getTime();

			calendar.set(Calendar.DAY_OF_MONTH, 5);
			Date _05012021 = calendar.getTime();

			calendar.set(Calendar.DAY_OF_MONTH, 6);
			Date _06012021 = calendar.getTime();

			calendar.set(Calendar.DAY_OF_MONTH, 22);
			Date _22012021 = calendar.getTime();

			calendar.set(Calendar.DAY_OF_MONTH, 23);
			Date _23012021 = calendar.getTime();

			calendar.set(Calendar.DAY_OF_MONTH, 31);
			Date _31012021 = calendar.getTime();

			ssBonuses.stream().forEach(b -> {
				assertEquals(_06012021, b.getStartDate(),b.getDescription());
				assertEquals(_22012021, b.getEndDate(),b.getDescription());
			});

			Salary salary = calculate(ssBonuses, Collections.emptyList());

			for (ContextVariable var : new ContextVariable[] { ContextVariable.CGC_BASE, ContextVariable.CGP_BASE,
//					ContextVariable.ENTERPRISE_QUOTA,
//					ContextVariable.CGP_BASE_ENTERPRISE,
			}) {
				SalaryData[] salaryData = salary.getSalaryDatas().stream()
						.filter(d -> AonStringUtils.equals(d.getName(), var.getName()))
						.sorted((d1, d2) -> d1.getStartDate().compareTo(d2.getStartDate())).toArray(SalaryData[]::new);

				assertEquals(3, salaryData.length,var.getName());
				assertEquals(_01012021, salaryData[0].getStartDate(),var.getName());
				assertEquals(_05012021, salaryData[0].getEndDate(),var.getName());
				assertEquals(_06012021, salaryData[1].getStartDate(),var.getName());
				assertEquals(_22012021, salaryData[1].getEndDate(),var.getName());
				assertEquals(_23012021, salaryData[2].getStartDate(),var.getName());
				assertEquals(_31012021, salaryData[2].getEndDate(),var.getName());
			}

			double totalCost = 0.00;
			for (SalaryCost cost : salary.getSalaryCosts()) {
				totalCost += cost.getAmount();
				System.out.println(cost.getName() + ": " + cost.getAmount());
			}

			assertEquals(1, salary.getSalaryBonus().size());
			double totalBonus = 0.00;
			for (SalaryBonus bonus : salary.getSalaryBonus()) {
				totalBonus += bonus.getAmount();
				System.out.println(bonus.getDescription() + ": " + bonus.getAmount());
			}

			System.out.println("CUOTA EMPRESARIAL :" + totalCost);

			assertEquals(totalCost * 17 / 31, totalBonus, DELTA);
			assertEquals(totalCost * 14 / 31, salary.getTotalEnterprise(), DELTA);

		}
	}

	@Test
	public void testIdcplnssVIIBonus() throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException,
			ExpressionException, SQLException, SalaryException {

		try (InputStream is = IdcTest.class.getResourceAsStream("idcplnssVII.pdf")) {
			Collection<PEC> ssBonuses = Idcplnss.getSSBonuses(is);
			assertEquals(1, ssBonuses.size());

			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			calendar.set(Calendar.MONTH, Calendar.FEBRUARY);
			calendar.set(Calendar.YEAR, 2021);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			Date _01022021 = calendar.getTime();

			calendar.set(Calendar.DAY_OF_MONTH, 28);
			Date _28022021 = calendar.getTime();

			ssBonuses.stream().forEach(b -> {
				assertEquals(_01022021, b.getStartDate(),b.getDescription());
				assertEquals(_28022021, b.getEndDate(),b.getDescription());
			});

			Salary salary = calculate(ssBonuses, Collections.emptyList());

			for (ContextVariable var : new ContextVariable[] { ContextVariable.CGC_BASE, ContextVariable.CGP_BASE, }) {
				SalaryData[] salaryData = salary.getSalaryDatas().stream()
						.filter(d -> AonStringUtils.equals(d.getName(), var.getName()))
						.sorted((d1, d2) -> d1.getStartDate().compareTo(d2.getStartDate())).toArray(SalaryData[]::new);

				assertEquals(1, salaryData.length,var.getName());

				assertEquals(_01022021, salaryData[0].getStartDate(),var.getName());
				assertEquals(_28022021, salaryData[0].getEndDate(),var.getName());

			}

			double totalCost = 0.00;
			for (SalaryCost cost : salary.getSalaryCosts()) {
				totalCost += cost.getAmount();
				System.out.println(cost.getName() + ": " + cost.getAmount());
			}

			assertEquals(1, salary.getSalaryBonus().size());
			double totalBonus = 0.00;
			for (SalaryBonus bonus : salary.getSalaryBonus()) {
				totalBonus += bonus.getAmount();
				System.out.println(bonus.getDescription() + ": " + bonus.getAmount());
			}

			System.out.println("CUOTA EMPRESARIAL :" + totalCost);

			assertEquals(341.66, totalBonus, DELTA);

			assertEquals(totalCost - 341.66, salary.getTotalEnterprise(), DELTA);
		}
	}

	@Test
	public void testIdcplnssVIIBonusWithATEPI() throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException,
			IOException, ExpressionException, SQLException, SalaryException {

		try (InputStream is = IdcTest.class.getResourceAsStream("idcplnssVII.pdf")) {
			Collection<PEC> ssBonuses = Idcplnss.getSSBonuses(is);
			assertEquals(1, ssBonuses.size());

			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			calendar.set(Calendar.MONTH, Calendar.FEBRUARY);
			calendar.set(Calendar.YEAR, 2021);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			Date _01022021 = calendar.getTime();

			calendar.set(Calendar.DAY_OF_MONTH, 28);
			Date _28022021 = calendar.getTime();

			ssBonuses.stream().forEach(b -> {
				assertEquals(_01022021, b.getStartDate(),b.getDescription());
				assertEquals(_28022021, b.getEndDate(),b.getDescription());
			});

			ssBonuses.stream().forEach(b -> {
				System.out.println(b.getFormula());
			});

			java.sql.Date startDate = toSQL(AonDateUtils.getFirstDayOfMonth(_01022021));
			java.sql.Date endDate = toSQL(AonDateUtils.getLastDayOfMonth(_01022021));

			Connection connection = getConnection();
			AONContext aonContext = new AONContext(connection);
			ContractRecord contract = newContract(aonContext, toSQL(startDate), ssBonuses, Collections.emptyList());
			PaymentConceptRecord prestIT = addConcept(aonContext, PREST_IT, PaymentType.CRA_0000);
			addPayment(aonContext, contract, prestIT,
					String.format("BASE_REGULADORA * 0.75 * %s * (isdef %s ? %s : 1.00)", OCCUPATIONAL_DISEASE_DAYS,
							LEAVE_FACTOR, LEAVE_FACTOR),
					String.format("BASE_REGULADORA * %s * (isdef %s ? %s : 1.00)", QUOTE_DAYS, LEAVE_FACTOR,
							LEAVE_FACTOR));
			ContractCostRecord atepCost = addCost(aonContext, contract, startDate, endDate,
					"-1 * DIAS_ENFERMEDAD_PROFESIONAL * BASE_REGULADORA * 0.75", "PREST. IT A CARGO DEL INSS",
					"ATEP_E");
			atepCost.setType((byte) 8);
			atepCost.update();

			java.sql.Date startItDate = add(startDate, Calendar.DAY_OF_MONTH, 5);
			java.sql.Date endItDate = add(startItDate, Calendar.DAY_OF_MONTH, 14);
			addIT(aonContext, contract, LeaveType.OCCUPATIONAL_DISEASE, startItDate, endItDate, 1500.00 / 30.00);

			ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, startDate, endDate,
					endDate, contract);
			RoundSalaryBuilder<Salary> salaryBuilder = new RoundSalaryBuilder<Salary>(new SalaryBuilder(),
					d -> d.setScale(2, RoundingMode.HALF_UP));

			SmartContractSalaryCalculator<Salary> builder = new SmartContractSalaryCalculator<Salary>(salaryBuilder);
			Salary salary = builder.calculate(ctx);

			// salary.getSalaryPayments().forEach( p -> System.out.println( "PAYMENT :" +
			// p.getName() +" : " + p.getAmount()));

			// salary.getSalaryCosts().forEach( c -> System.out.println( "COST :" +
			// c.getName() +" : " + c.getAmount()));

			for (ContextVariable var : new ContextVariable[] { ContextVariable.CGC_BASE, ContextVariable.CGP_BASE, }) {
				SalaryData[] salaryData = salary.getSalaryDatas().stream()
						.filter(d -> AonStringUtils.equals(d.getName(), var.getName()))
						.sorted((d1, d2) -> d1.getStartDate().compareTo(d2.getStartDate())).toArray(SalaryData[]::new);

				assertEquals(3, salaryData.length,var.getName());

				assertEquals(startDate, salaryData[0].getStartDate(),var.getName());

				assertEquals(startItDate, salaryData[1].getStartDate(),var.getName());

				assertEquals(endDate, salaryData[2].getEndDate(),var.getName());

			}

			double totalCost = 0.00;
			for (SalaryCost cost : salary.getSalaryCosts()) {
				totalCost += cost.getAmount();
				System.out.println(cost.getName() + ": " + cost.getAmount());
			}

			assertEquals(3, salary.getSalaryBonus().size());
			double totalBonus = 0.00;
			for (SalaryBonus bonus : salary.getSalaryBonus()) {
				totalBonus += bonus.getAmount();
				System.out.println(bonus.getDescription() + ": " + bonus.getAmount());
			}

			System.out.println("CUOTA EMPRESARIAL :" + totalCost);

			assertEquals(341.66, totalBonus, DELTA);

			assertEquals(totalCost - 341.66, salary.getTotalEnterprise(), DELTA);
		}
	}

	@Test
	public void testIdcplnssVIIBonusWithATEPII() throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException,
			IOException, ExpressionException, SQLException, SalaryException {

		try (InputStream is = IdcTest.class.getResourceAsStream("idcplnssVII.pdf")) {
			Collection<PEC> ssBonuses = Idcplnss.getSSBonuses(is);
			assertEquals(1, ssBonuses.size());

			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			calendar.set(Calendar.MONTH, Calendar.FEBRUARY);
			calendar.set(Calendar.YEAR, 2021);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			Date _01022021 = calendar.getTime();

			calendar.set(Calendar.DAY_OF_MONTH, 28);
			Date _28022021 = calendar.getTime();

			ssBonuses.stream().forEach(b -> {
				assertEquals(_01022021, b.getStartDate(),b.getDescription());
				assertEquals(_28022021, b.getEndDate(),b.getDescription());
			});

			ssBonuses.stream().forEach(b -> {
				System.out.println(b.getFormula());
			});

			java.sql.Date startDate = toSQL(AonDateUtils.getFirstDayOfMonth(_01022021));
			java.sql.Date endDate = toSQL(AonDateUtils.getLastDayOfMonth(_01022021));

			Connection connection = getConnection();
			AONContext aonContext = new AONContext(connection);
			ContractRecord contract = newContract(aonContext, startDate, ssBonuses, Collections.emptyList(),
					new String[] { "1000.00 * DIAS_TRABAJADOS / DIAS_MES", });
			PaymentConceptRecord prestIT = addConcept(aonContext, PREST_IT, PaymentType.CRA_0000);
			addPayment(aonContext, contract, prestIT,
					String.format("BASE_REGULADORA * 0.75 * %s * (isdef %s ? %s : 1.00)", OCCUPATIONAL_DISEASE_DAYS,
							LEAVE_FACTOR, LEAVE_FACTOR),
					String.format("BASE_REGULADORA * %s * (isdef %s ? %s : 1.00)", QUOTE_DAYS, LEAVE_FACTOR,
							LEAVE_FACTOR));
			ContractCostRecord atepCost = addCost(aonContext, contract, startDate, endDate,
					"-1 * DIAS_ENFERMEDAD_PROFESIONAL * BASE_REGULADORA * 0.75", "PREST. IT A CARGO DEL INSS",
					"ATEP_E");
			atepCost.setType((byte) 8);
			atepCost.update();

			java.sql.Date startItDate = add(startDate, Calendar.DAY_OF_MONTH, 5);
			java.sql.Date endItDate = add(startItDate, Calendar.DAY_OF_MONTH, 14);
			addIT(aonContext, contract, LeaveType.OCCUPATIONAL_DISEASE, startItDate, endItDate, 1000.00 / 30.00);

			ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, startDate, endDate,
					endDate, contract);
			RoundSalaryBuilder<Salary> salaryBuilder = new RoundSalaryBuilder<Salary>(new SalaryBuilder(),
					d -> d.setScale(2, RoundingMode.HALF_UP));

			SmartContractSalaryCalculator<Salary> builder = new SmartContractSalaryCalculator<Salary>(salaryBuilder);
			Salary salary = builder.calculate(ctx);

			// salary.getSalaryPayments().forEach( p -> System.out.println( "PAYMENT :" +
			// p.getName() +" : " + p.getAmount()));

			// salary.getSalaryCosts().forEach( c -> System.out.println( "COST :" +
			// c.getName() +" : " + c.getAmount()));

			for (ContextVariable var : new ContextVariable[] { ContextVariable.CGC_BASE, ContextVariable.CGP_BASE, }) {
				SalaryData[] salaryData = salary.getSalaryDatas().stream()
						.filter(d -> AonStringUtils.equals(d.getName(), var.getName()))
						.sorted((d1, d2) -> d1.getStartDate().compareTo(d2.getStartDate())).toArray(SalaryData[]::new);

				assertEquals(3, salaryData.length,var.getName());

				assertEquals(startDate, salaryData[0].getStartDate(),var.getName());

				assertEquals(startItDate, salaryData[1].getStartDate(),var.getName());

				assertEquals(endDate, salaryData[2].getEndDate(),var.getName());

			}

			double atep = 0.00;
			double totalCost = 0.00;
			for (SalaryCost cost : salary.getSalaryCosts()) {
				if ("ATEP_E".equalsIgnoreCase(cost.getName()))
					atep += cost.getAmount();
				else
					totalCost += cost.getAmount();

				System.out.println(cost.getName() + ": " + cost.getAmount());
			}

			assertEquals(3, salary.getSalaryBonus().size());
			double totalBonus = 0.00;
			for (SalaryBonus bonus : salary.getSalaryBonus()) {
				totalBonus += bonus.getAmount();
				System.out.println(bonus.getDescription() + ": " + bonus.getAmount());
			}

			System.out.println("CUOTA EMPRESARIAL :" + totalCost);

			assertEquals(totalCost, totalBonus, DELTA);

			assertEquals(-1 * 1000.00 / 30 * 0.75 * 15, salary.getTotalEnterprise(), DELTA);
		}
	}

	@Test
	public void testIdcplnssVIIIBonus() throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException,
			ExpressionException, SQLException, SalaryException {

		try (InputStream is = IdcTest.class.getResourceAsStream("idcplnssVIII.pdf")) {
			Collection<PEC> ssBonuses = Idcplnss.getSSBonuses(is);
			assertEquals(1, ssBonuses.size());

			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			calendar.set(Calendar.MONTH, Calendar.FEBRUARY);
			calendar.set(Calendar.YEAR, 2021);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			Date _01022021 = calendar.getTime();

			calendar.set(Calendar.DAY_OF_MONTH, 28);
			Date _28022021 = calendar.getTime();

			ssBonuses.stream().forEach(b -> {
				assertEquals(_01022021, b.getStartDate(),b.getDescription());
				assertEquals(_28022021, b.getEndDate(),b.getDescription());
			});

			Salary salary = calculate(ssBonuses, Collections.emptyList());

			for (ContextVariable var : new ContextVariable[] { ContextVariable.CGC_BASE, ContextVariable.CGP_BASE, }) {
				SalaryData[] salaryData = salary.getSalaryDatas().stream()
						.filter(d -> AonStringUtils.equals(d.getName(), var.getName()))
						.sorted((d1, d2) -> d1.getStartDate().compareTo(d2.getStartDate())).toArray(SalaryData[]::new);

				assertEquals(1, salaryData.length,var.getName());

				assertEquals(_01022021, salaryData[0].getStartDate(),var.getName());
				assertEquals(_28022021, salaryData[0].getEndDate(),var.getName());

			}

			double totalCost = 0.00;
			for (SalaryCost cost : salary.getSalaryCosts()) {
				totalCost += cost.getAmount();
				System.out.println(cost.getName() + ": " + cost.getAmount());
			}

			assertEquals(1, salary.getSalaryBonus().size());
			double totalBonus = 0.00;
			for (SalaryBonus bonus : salary.getSalaryBonus()) {
				totalBonus += bonus.getAmount();
				System.out.println(bonus.getDescription() + ": " + bonus.getAmount());
			}

			double totalCgcE = 0.00;
			for (SalaryCost cost : salary.getSalaryCosts()) {
				if (cost.getType() != DeductionType.COMMON_CONTINGENCY)
					continue;
				totalCgcE += cost.getAmount();
				System.out.println(cost.getDescription() + ": " + cost.getAmount());
			}

			System.out.println("CUOTA EMPRESARIAL :" + totalCost);

			assertEquals(totalCgcE * 0.40, totalBonus, DELTA);

//			assertEquals(totalCost - 341.66, salary.getTotalEnterprise(), DELTA);
		}
	}

	@Test
	public void testIdcplnssIXBonus() throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException,
			ExpressionException, SQLException, SalaryException {

		try (InputStream is = IdcTest.class.getResourceAsStream("idcplnssIX.pdf")) {
			Collection<PEC> ssBonuses = Idcplnss.getSSBonuses(is);
			assertEquals(1, ssBonuses.size());

			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			calendar.set(Calendar.MONTH, Calendar.APRIL);
			calendar.set(Calendar.YEAR, 2021);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			Date _01042021 = calendar.getTime();

			calendar.set(Calendar.DAY_OF_MONTH, 30);
			Date _30042021 = calendar.getTime();

			ssBonuses.stream().forEach(b -> {
				assertEquals(_01042021, b.getStartDate(),b.getDescription());
				assertEquals(_30042021, b.getEndDate(),b.getDescription());
			});

			Salary salary = calculate(ssBonuses, Collections.emptyList());

			for (ContextVariable var : new ContextVariable[] { ContextVariable.CGC_BASE, ContextVariable.CGP_BASE, }) {
				SalaryData[] salaryData = salary.getSalaryDatas().stream()
						.filter(d -> AonStringUtils.equals(d.getName(), var.getName()))
						.sorted((d1, d2) -> d1.getStartDate().compareTo(d2.getStartDate())).toArray(SalaryData[]::new);

				assertEquals(1, salaryData.length,var.getName());

				assertEquals(_01042021, salaryData[0].getStartDate(),var.getName());
				assertEquals(_30042021, salaryData[0].getEndDate(),var.getName());

			}

			double totalCost = 0.00;
			for (SalaryCost cost : salary.getSalaryCosts()) {
				totalCost += cost.getAmount();
				System.out.println(cost.getName() + ": " + cost.getAmount());
			}

			assertEquals(1, salary.getSalaryBonus().size());
			double totalBonus = 0.00;
			for (SalaryBonus bonus : salary.getSalaryBonus()) {
				totalBonus += bonus.getAmount();
				System.out.println(bonus.getDescription() + ": " + bonus.getAmount());
			}

			double totalCgcE = 0.00;
			for (SalaryCost cost : salary.getSalaryCosts()) {
				if (cost.getType() != DeductionType.COMMON_CONTINGENCY)
					continue;
				totalCgcE += cost.getAmount();
				System.out.println(cost.getDescription() + ": " + cost.getAmount());
			}

			System.out.println("CUOTA EMPRESARIAL :" + totalCost);

			assertEquals(totalCgcE, totalBonus, DELTA);

//			assertEquals(totalCost - 341.66, salary.getTotalEnterprise(), DELTA);
		}
	}

	@Test
	public void testIdcVIIIBonus() throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException,
			ExpressionException, SalaryException, SQLException {

		try (InputStream is = IdcTest.class.getResourceAsStream("idcVIII.pdf")) {
			Collection<PEC> ssBonuses = Idc.getSSPECs(is);
			assertEquals(1, ssBonuses.size());

			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);

			calendar.set(Calendar.YEAR, 2020);
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			calendar.set(Calendar.MONTH, Calendar.OCTOBER);

			Date october2020 = calendar.getTime();

			calendar.set(Calendar.YEAR, 2022);
			calendar.set(Calendar.DAY_OF_MONTH, 9);
			calendar.set(Calendar.MONTH, Calendar.DECEMBER);

			Date _9december2022 = calendar.getTime();

			PEC bonus = ssBonuses.stream().findFirst().get();

			assertEquals(october2020, bonus.getStartDate());
			assertEquals(_9december2022, bonus.getEndDate());

			calendar.set(Calendar.DAY_OF_MONTH, 1);
			Date december2022 = calendar.getTime();

			double totalCosts[] = { 0.00, 0.00 };
			Salary salary = calculate(ssBonuses, Collections.emptyList(), december2022, new SalaryBuilder() {
				@Override
				public void addCost(Double amount, String description, Date start, Date end, IDeduction cost,
						Map<String, ITimedVariable<?>> context) {
					if (start.equals(december2022))
						totalCosts[0] += amount;
					else
						totalCosts[1] += amount;
					super.addCost(amount, description, start, end, cost, context);
				}

				@Override
				public void addBonus(Double amount, String description, Date startDate, Date endDate, IBonus bonus,
						Map<String, ITimedVariable<?>> context) {
					super.addBonus(amount, description, startDate, endDate, bonus, context);
				}

			});

			assertEquals(Math.max(totalCosts[0] - 150.00 / 30.00 * 9, 0.00) + totalCosts[1], salary.getTotalEnterprise(), DELTA);

		}
	}

	@Test
	public void testIdcIXBonus() throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException,
			ExpressionException, SalaryException, SQLException {

		try (InputStream is = IdcTest.class.getResourceAsStream("idcIX.pdf")) {
			Collection<PEC> ssPecs = Idc.getSSPECs(is);
			assertEquals(3, ssPecs.size());

			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);

			calendar.set(Calendar.YEAR, 2020);
			calendar.set(Calendar.DAY_OF_MONTH, 8);
			calendar.set(Calendar.MONTH, Calendar.DECEMBER);

			Date december82020 = calendar.getTime();

			calendar.set(Calendar.YEAR, 2024);
			calendar.set(Calendar.DAY_OF_MONTH, 7);
			calendar.set(Calendar.MONTH, Calendar.DECEMBER);

			Date december2024 = calendar.getTime();

			ssPecs.stream().findFirst().ifPresent(p -> {
				assertEquals(december82020, p.getStartDate());
				assertEquals(december2024, p.getEndDate());
			});
			ssPecs.stream().skip(1).forEach(p -> {
				assertEquals(december82020, p.getStartDate());
				assertNull(p.getEndDate());
			});

			calendar.set(Calendar.DAY_OF_MONTH, 1);
			Date december = calendar.getTime();

			Salary salary = calculate(ssPecs, Collections.emptyList(), december);

			double totalCost = salary.getSalaryCosts().stream().collect(Collectors.summingDouble(c -> c.getAmount()));

			// salary.getSalaryDeductions().forEach(d ->
			// System.out.println(d.getDeductionConcept() +" : " + d.getAmount() +", " +
			// d.getType()));

			// salary.getSalaryCosts().forEach(d -> System.out.println(d.getCostConcept() +"
			// : " + d.getAmount() +", " + d.getType()));

			salary.getSalaryBonus().forEach(d -> System.out.println(
					d.getBonusConcept() + " : " + d.getAmount() + ", " + d.getType() + "," + d.getDescription()));

			assertEquals(totalCost - 1.67 * 7.00, salary.getTotalEnterprise(), DELTA);

			salary.getSalaryCosts().stream().filter(c -> c.getType() == DeductionType.FOGASA)
					.forEach(c -> fail(c.getType().name()));

			salary.getSalaryCosts().stream().filter(c -> c.getType() == DeductionType.JOB_TRAINING)
					.forEach(c -> fail(c.getType().name()));

		}
	}

	@Test
	@Disabled
	public void testIdcXBonus() throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException,
			ExpressionException, SalaryException, SQLException {

		try (InputStream is = IdcTest.class.getResourceAsStream("idcX.pdf")) {
			Collection<PEC> ssBonuses = Idc.getSSPECs(is);
			assertEquals(1 + 3 * 2, ssBonuses.size());

			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);

			calendar.set(Calendar.YEAR, 2020);
			calendar.set(Calendar.DAY_OF_MONTH, 8);
			calendar.set(Calendar.MONTH, Calendar.DECEMBER);

			Date december82020 = calendar.getTime();

			calendar.set(Calendar.YEAR, 2024);
			calendar.set(Calendar.DAY_OF_MONTH, 7);
			calendar.set(Calendar.MONTH, Calendar.DECEMBER);

			Date december2024 = calendar.getTime();

			PEC bonus = ssBonuses.stream().findFirst().get();

			assertEquals(december82020, bonus.getStartDate());
			assertEquals(december2024, bonus.getEndDate());

			calendar.set(Calendar.DAY_OF_MONTH, 1);
			Date may = calendar.getTime();

			Salary salary = calculate(ssBonuses, Collections.emptyList(), may);

			double totalCost = salary.getSalaryCosts().stream().collect(Collectors.summingDouble(c -> c.getAmount()));

			double totalDeduction = salary.getSalaryDeductions().stream()
					.filter(d -> d.getType() != DeductionType.BONUS)
					.filter(d -> !AonStringUtils.equals(d.getDeductionConcept(), "CGC"))
					.collect(Collectors.summingDouble(c -> c.getAmount()));

			salary.getSalaryDeductions().forEach(
					d -> System.out.println(d.getDeductionConcept() + " : " + d.getAmount() + ", " + d.getType()));

			assertEquals(totalCost - 50.00, salary.getTotalEnterprise(), DELTA);

		}
	}

	@Test
	public void testIdcXIBonus() throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException,
			ExpressionException, SalaryException, SQLException {

		try (InputStream is = IdcTest.class.getResourceAsStream("idcXI.pdf")) {
			Collection<PEC> ssPecs = Idc.getSSPECs(is);
			assertTrue(ssPecs.size() > 1);

			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);

			calendar.set(Calendar.YEAR, 2021);
			calendar.set(Calendar.DAY_OF_MONTH, 20);
			calendar.set(Calendar.MONTH, Calendar.MAY);

			Date may202021 = calendar.getTime();

			ssPecs.stream().forEach(pec -> assertEquals(may202021, pec.getStartDate()));
			ssPecs.stream().forEach(pec -> assertNull(pec.getEndDate()));

			ssPecs.forEach(pec -> System.out.println("[" + pec.getName() + "] " + pec.getDescription() + " = "
					+ pec.getFormula() + ", " + pec.getStartDate()));

			calendar.set(Calendar.MONTH, Calendar.JUNE);
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			Date june = calendar.getTime();

			Salary salary = calculate(ssPecs, Collections.emptyList(), june);

			salary.getSalaryDeductions().forEach(
					d -> System.out.println(d.getDeductionConcept() + " : " + d.getAmount() + ", " + d.getType()));
			assertEquals(1 + 3 /*CGC + DESMPL + FP*/, salary.getSalaryDeductions().size());

			salary.getSalaryCosts()
			.forEach(c -> System.out.println(c.getName() + " : " + c.getAmount() + ", " + c.getType()));
			assertEquals(1 + 5 /*DESMPL + FP + IT + IMS + FOGASA*/, salary.getSalaryCosts().size());

			assertEquals(0.00, salary.getTotalEnterprise(), DELTA);
			assertEquals(0.00, salary.getSocialSecurityContributions(), DELTA);

		}
	}

	@Test
	public void testIdcXVBonus() throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException,
			ExpressionException, SalaryException, SQLException {

		try (InputStream is = IdcTest.class.getResourceAsStream("idcXV.pdf")) {
			Collection<PEC> ssPecs = Idc.getSSPECs(is);
			assertTrue(ssPecs.size() > 1);

			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);

			calendar.set(Calendar.YEAR, 2020);
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			calendar.set(Calendar.MONTH, Calendar.APRIL);

			Date april012020 = calendar.getTime();

			ssPecs.stream().forEach(pec -> assertEquals(april012020, pec.getStartDate()));
			ssPecs.stream().forEach(pec -> assertNull(pec.getEndDate()));

			ssPecs.forEach(pec -> System.out.println("[" + pec.getName() + "] " + pec.getDescription() + " = "
					+ pec.getFormula() + ", " + pec.getStartDate()));

			calendar.set(Calendar.MONTH, Calendar.JUNE);
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			Date june = calendar.getTime();

			Salary salary = calculate(ssPecs, Collections.emptyList(), june);

			salary.getSalaryCosts().forEach(
					c -> System.out.println("COST :" + c.getName() + " : " + c.getAmount() + ", " + c.getType()));
			salary.getSalaryDeductions().forEach(d -> System.out
					.println("DEDUCTION :" + d.getDeductionConcept() + " : " + d.getAmount() + ", " + d.getType()));
			salary.getSalaryBonus().forEach(d -> System.out
					.println("BONUS :" + d.getBonusConcept() + " : " + d.getAmount() + ", " + d.getType()));

			assertEquals(0.00, salary.getTotalEnterprise(), DELTA);
			assertEquals(0.00, salary.getSocialSecurityContributions(), DELTA);

		}
	}

	@Test
	public void testIdcXVIBonus() throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException,
			ExpressionException, SalaryException, SQLException {

		try (InputStream is = IdcTest.class.getResourceAsStream("idcXVI.pdf")) {
			Collection<PEC> ssPecs = Idc.getSSPECs(is);
			assertTrue(ssPecs.size() == 1);

			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);

			calendar.set(Calendar.YEAR, 2021);
			calendar.set(Calendar.DAY_OF_MONTH, 20);
			calendar.set(Calendar.MONTH, Calendar.OCTOBER);

			Date october2020201 = calendar.getTime();

			ssPecs.stream().forEach(pec -> assertEquals(october2020201, pec.getStartDate()));
			ssPecs.stream().forEach(pec -> assertNull(pec.getEndDate()));

			ssPecs.forEach(pec -> System.out.println("[" + pec.getName() + "] " + pec.getDescription() + " = "
					+ pec.getFormula() + ", " + pec.getStartDate()));

			calendar.set(Calendar.MONTH, Calendar.NOVEMBER);
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			Date november = calendar.getTime();

			Salary salary = calculate(ssPecs, Collections.emptyList(), november);

			salary.getSalaryCosts().forEach(
					c -> System.out.println("COST :" + c.getName() + " : " + c.getAmount() + ", " + c.getType()));
			salary.getSalaryDeductions().forEach(d -> System.out
					.println("DEDUCTION :" + d.getDeductionConcept() + " : " + d.getAmount() + ", " + d.getType()));
			salary.getSalaryBonus().forEach(d -> System.out
					.println("BONUS :" + d.getBonusConcept() + " : " + d.getAmount() + ", " + d.getType()));

			assertEquals(0.00, salary.getTotalEnterprise(), DELTA);
			// assertEquals(0.00, salary.getSocialSecurityContributions(), DELTA);

		}
	}

	@Test
	public void testIdcXVIIBonus() throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException,
			ExpressionException, SalaryException, SQLException {

		try (InputStream is = IdcTest.class.getResourceAsStream("idcXVII.pdf")) {
			Collection<PEC> ssPecs = Idc.getSSPECs(is);
			assertTrue(ssPecs.size() == 1);

			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);

			calendar.set(Calendar.YEAR, 2021);
			calendar.set(Calendar.DAY_OF_MONTH, 21);
			calendar.set(Calendar.MONTH, Calendar.OCTOBER);

			Date october2120201 = calendar.getTime();

			ssPecs.stream().forEach(pec -> assertEquals(october2120201, pec.getStartDate()));

			calendar.set(Calendar.YEAR, 2024);
			calendar.set(Calendar.DAY_OF_MONTH, 20);
			calendar.set(Calendar.MONTH, Calendar.OCTOBER);

			Date october2020204 = calendar.getTime();

			ssPecs.stream().forEach(pec -> assertEquals(october2020204, pec.getEndDate()));

			ssPecs.forEach(pec -> System.out.println("[" + pec.getName() + "] " + pec.getDescription() + " = "
					+ pec.getFormula() + ", " + pec.getStartDate()));

			calendar.set(Calendar.YEAR, 2022);
			calendar.set(Calendar.MONTH, Calendar.NOVEMBER);
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			Date november = calendar.getTime();

			Salary salary = calculate(ssPecs, Collections.emptyList(), november);

			salary.getSalaryCosts().forEach(
					c -> System.out.println("COST :" + c.getName() + " : " + c.getAmount() + ", " + c.getType()));
			salary.getSalaryDeductions().forEach(d -> System.out
					.println("DEDUCTION :" + d.getDeductionConcept() + " : " + d.getAmount() + ", " + d.getType()));
			salary.getSalaryBonus().forEach(d -> System.out
					.println("BONUS :" + d.getBonusConcept() + " : " + d.getAmount() + ", " + d.getType()));

			salary.getSalaryBonus().forEach(d -> assertEquals(43.75, d.getAmount(), DELTA));

			// assertEquals(0.00, salary.getTotalEnterprise(), DELTA);
			// assertEquals(0.00, salary.getSocialSecurityContributions(), DELTA);

		}
	}

	@Test
	public void testIdcXXIBonus() throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException,
			ExpressionException, SalaryException, SQLException {

		try (InputStream is = IdcTest.class.getResourceAsStream("idcXXI.pdf")) {
			Collection<PEC> ssPecs = Idc.getSSPECs(is);
			// assertTrue(ssPecs.size() == 1);

			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);

			calendar.set(Calendar.YEAR, 2022);
			calendar.set(Calendar.DAY_OF_MONTH, 11);
			calendar.set(Calendar.MONTH, Calendar.MAY);

			Date may112022 = calendar.getTime();

			ssPecs.stream().forEach(pec -> assertEquals(may112022, pec.getStartDate()));

			ssPecs.stream().forEach(pec -> assertNull(pec.getEndDate()));

			ssPecs.forEach(pec -> System.out.println("[" + pec.getName() + "] " + pec.getDescription() + " = "
					+ pec.getFormula() + ", " + pec.getStartDate()));

			calendar.set(Calendar.YEAR, 2022);
			calendar.set(Calendar.MONTH, Calendar.NOVEMBER);
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			Date november = calendar.getTime();

			Salary salary = calculate(ssPecs, Collections.emptyList(), november);

			salary.getSalaryCosts().forEach(
					c -> System.out.println("COST :" + c.getName() + " : " + c.getAmount() + ", " + c.getType()));
			salary.getSalaryDeductions().forEach(d -> System.out
					.println("DEDUCTION :" + d.getDeductionConcept() + " : " + d.getAmount() + ", " + d.getType()));
			salary.getSalaryBonus().forEach(d -> System.out
					.println("BONUS :" + d.getBonusConcept() + " : " + d.getAmount() + ", " + d.getType()));

			assertEquals(0.00, salary.getTotalEnterprise(), DELTA);
			assertEquals(0.00, salary.getSocialSecurityContributions(), DELTA);

		}
	}

	@Test
	public void testIdcXXIIBonus() throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException,
			ExpressionException, SalaryException, SQLException {

		try (InputStream is = IdcTest.class.getResourceAsStream("idcXXII.pdf")) {
			Collection<PEC> ssPecs = Idc.getSSPECs(is);
			// assertTrue(ssPecs.size() == 1);

			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);

			calendar.set(Calendar.YEAR, 2022);
			calendar.set(Calendar.DAY_OF_MONTH, 21);
			calendar.set(Calendar.MONTH, Calendar.FEBRUARY);

			Date february21 = calendar.getTime();

			ssPecs.stream().forEach(pec -> assertEquals(february21, pec.getStartDate()));

			ssPecs.stream().forEach(pec -> assertNull(pec.getEndDate()));

			ssPecs.forEach(pec -> System.out.println("[" + pec.getName() + "] " + pec.getDescription() + " = "
					+ pec.getFormula() + ", " + pec.getStartDate()));

			calendar.set(Calendar.YEAR, 2022);
			calendar.set(Calendar.DAY_OF_MONTH, 28);
			Date february28 = calendar.getTime();

			Collection<Data> datas = new ArrayList<>();
			datas.add(new Data() {
				{
					expression = "0.80";
					startDate = february21;
					name = "PORCENTAJE_IT";
				}
			});
			datas.add(new Data() {
				{
					expression = "0.70";
					startDate = february21;
					name = "PORCENTAJE_IMS";
				}
			});
			datas.add(new Data() {
				{
					expression = "23.60";
					startDate = february21;
					name = "PORCENTAJE_CGC_E";
				}
			});
			datas.add(new Data() {
				{
					expression = "0.60";
					startDate = february21;
					name = "PORCENTAJE_FP_E";
				}
			});
			datas.add(new Data() {
				{
					expression = "0.20";
					startDate = february21;
					name = "PORCENTAJE_FOGASA";
				}
			});
			datas.add(new Data() {
				{
					expression = "5.50";
					startDate = february21;
					name = "PORCENTAJE_DESMPL_E";
				}
			});

			Salary salary = calculate(ssPecs, datas, new String[] { "320.43" }, february21, february28);

			salary.getSalaryCosts().forEach(
					c -> System.out.println("COST :" + c.getName() + " : " + c.getAmount() + ", " + c.getType()));

			double totalBonus = salary.getSalaryBonus().stream().collect(Collectors.summingDouble(b -> b.getAmount()));

			assertEquals(91.12, totalBonus, DELTA);

			assertEquals(9.48, salary.getTotalEnterprise(), DELTA);

		}
	}

	@Test
	public void testIdcXXIIBonusI() throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException,
			ExpressionException, SalaryException, SQLException {

		try (InputStream is = IdcTest.class.getResourceAsStream("idcXXII.pdf")) {
			Collection<PEC> ssPecs = Idc.getSSPECs(is);
			// assertTrue(ssPecs.size() == 1);

			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);

			calendar.set(Calendar.YEAR, 2022);
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			calendar.set(Calendar.MONTH, Calendar.MARCH);

			Date march1 = calendar.getTime();

			calendar.set(Calendar.DAY_OF_MONTH, 31);
			Date march31 = calendar.getTime();

			Collection<Data> datas = new ArrayList<>();
			datas.add(new Data() {
				{
					expression = "0.80";
					startDate = march1;
					name = "PORCENTAJE_IT";
				}
			});
			datas.add(new Data() {
				{
					expression = "0.70";
					startDate = march1;
					name = "PORCENTAJE_IMS";
				}
			});
			datas.add(new Data() {
				{
					expression = "23.60";
					startDate = march1;
					name = "PORCENTAJE_CGC_E";
				}
			});
			datas.add(new Data() {
				{
					expression = "0.60";
					startDate = march1;
					name = "PORCENTAJE_FP_E";
				}
			});
			datas.add(new Data() {
				{
					expression = "0.20";
					startDate = march1;
					name = "PORCENTAJE_FOGASA";
				}
			});
			datas.add(new Data() {
				{
					expression = "5.50";
					startDate = march1;
					name = "PORCENTAJE_DESMPL_E";
				}
			});

			Salary salary = calculate(ssPecs, datas, new String[] { "1201.61" }, march1, march31);

			double totalBonus = salary.getSalaryBonus().stream().collect(Collectors.summingDouble(b -> b.getAmount()));

			assertEquals(341.66, totalBonus, DELTA);
			assertEquals(377.30 - 341.66, salary.getTotalEnterprise(), DELTA);

			calendar.set(Calendar.MONTH, Calendar.APRIL);
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			Date april1 = calendar.getTime();

			calendar.set(Calendar.DAY_OF_MONTH, 30);
			Date april30 = calendar.getTime();

			salary = calculate(ssPecs, datas, new String[] { "1201.61" }, april1, april30);

			totalBonus = salary.getSalaryBonus().stream().collect(Collectors.summingDouble(b -> b.getAmount()));

			assertEquals(341.66, totalBonus, DELTA);
			assertEquals(377.30 - 341.66, salary.getTotalEnterprise(), DELTA);

		}
	}

	@Test
	public void testIdcXXIIIBonus() throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException,
			ExpressionException, SalaryException, SQLException {

		try (InputStream is = IdcTest.class.getResourceAsStream("idcXXIII.pdf")) {
			Collection<PEC> ssPecs = Idc.getSSPECs(is);
			// assertTrue(ssPecs.size() == 1);

			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);

			calendar.set(Calendar.YEAR, 2022);
			calendar.set(Calendar.DAY_OF_MONTH, 11);
			calendar.set(Calendar.MONTH, Calendar.MARCH);

			Date march11 = calendar.getTime();

			ssPecs.stream().forEach(pec -> assertEquals(march11, pec.getStartDate()));

			ssPecs.stream().forEach(pec -> assertNull(pec.getEndDate()));

		}
	}

	@Test
	public void testIdcXIPECs() throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException,
			ExpressionException, SalaryException, SQLException {

		try (InputStream is = IdcTest.class.getResourceAsStream("idcXI.pdf")) {
			Collection<PEC> ssPecs = Idc.getSSPECs(is);
			assertTrue(ssPecs.size() > 1);

			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);

			calendar.set(Calendar.YEAR, 2021);
			calendar.set(Calendar.DAY_OF_MONTH, 20);
			calendar.set(Calendar.MONTH, Calendar.MAY);

			Date may202021 = calendar.getTime();

			Connection connection = getConnection();
			AONContext aonContext = new AONContext(connection);

			String ccc = "11120603901"; // Long.toString(System.currentTimeMillis()).substring(0, 11);
			String naf = "111035757934"; // Long.toString(System.currentTimeMillis()).substring(0, 12);
			String doc = "052418812H"; // Long.toString(System.currentTimeMillis()).substring(0, 9);

			DomainRecord domain = newDomain(aonContext);
			ScopeRecord scope = newScope(aonContext, domain.getId());
			EnterpriseActivityRecord enterpriseActivity = newEnterpriseActivity(aonContext, domain.getId(),
					scope.getId(), SSRegimeType.GENERAL);
			EnterpriseCccRecord enterpriseCcc = newEnterpriseCcc(aonContext, domain.getId(), scope.getId(),
					enterpriseActivity.getId(), CCCType.TRAINING, ccc);
			WorkplaceRecord workplace = newWorkplace(aonContext, domain.getId(), scope.getId(),
					enterpriseActivity.getEnterprise());
			RegistryRecord person = newPerson(aonContext, domain.getId(), doc, naf);

			ContractRecord contract = newContract(aonContext, SSRegimeType.GENERAL, CCCType.TRAINING,
					toSQL(getFirstDayOfYear(may202021)), null, Collections.emptyMap(), new String[] {}, new String[] {},
					null, domain.getId(), // domainId,
					person.getId(), // personId,
					workplace.getId(), // workplaceId,
					enterpriseCcc.getId(), // enterpriseCccId,
					enterpriseActivity.getId() // enterpriseActivityId
			);

			SistemaRED2AON.addPECs(ssPecs, "login", domain.getName(), domain.getId(), may202021, null, ccc, naf);
			Cost[] costsI = PAYROLL.getCosts(domain.getName(), domain.getId(), "login", contract.getId());
			Bonus[] bonusI = PAYROLL.getBonuses(domain.getName(), domain.getId(), "login", contract.getId());
			Deduction[] deductionsI = PAYROLL.getDeductions(domain.getName(), domain.getId(), "login",
					contract.getId());

			SistemaRED2AON.addPECs(ssPecs, "login", domain.getName(), domain.getId(), may202021, null, ccc, naf);
			Cost[] costsII = PAYROLL.getCosts(domain.getName(), domain.getId(), "login", contract.getId());
			Bonus[] bonusII = PAYROLL.getBonuses(domain.getName(), domain.getId(), "login", contract.getId());
			Deduction[] deductionsII = PAYROLL.getDeductions(domain.getName(), domain.getId(), "login",
					contract.getId());

			assertEquals(bonusI.length, bonusII.length);
			assertEquals(costsI.length, costsII.length);
			assertEquals(deductionsI.length, deductionsII.length);
		}
	}

	@Test
	public void testIdcplnssTrabajadoresTramosI()
			throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException, JAXBException {
		try (InputStream is = IdcTest.class.getResourceAsStream("idcplnssI.pdf")) {
			TrabajadoresTramos trabajadoresTramos = Idcplnss.getTrabajadoresTramos(is,
					new TrabajadoresTramosCallback() {
					});

			marshal(trabajadoresTramos, System.out);

			Liquidacion liquidacion = trabajadoresTramos.getLiquidacion();

			assertEquals("0111", liquidacion.getCcc().getRegimen());
			assertEquals("01", liquidacion.getCcc().getProvincia());
			assertEquals("105360062", liquidacion.getCcc().getNumero());

			assertEquals("12", liquidacion.getPeriodoDesde().getMes());
			assertEquals("2020", liquidacion.getPeriodoDesde().getAnho());
			assertEquals("12", liquidacion.getPeriodoHasta().getMes());
			assertEquals("2020", liquidacion.getPeriodoHasta().getAnho());

			assertEquals(1, liquidacion.getLiquidacionMes().size());

			LiquidacionMes liquidacionesMes = liquidacion.getLiquidacionMes().get(0);
			assertEquals("12", liquidacionesMes.getMesLiquidativo().getMes());
			assertEquals("2020", liquidacionesMes.getMesLiquidativo().getAnho());

			Trabajadores trabajadores = liquidacionesMes.getTrabajadores();
			assertEquals(1, trabajadores.getTrabajador().size());

			for (Trabajador trabajador : trabajadores.getTrabajador()) {

				assertEquals("011005185924", trabajador.getNaf());

				assertEquals(1, trabajador.getTramos().getTramo().size());
				for (Tramo tramo : trabajador.getTramos().getTramo()) {

					assertEquals("01", tramo.getInformacionAfiliacion().getGrupoCotizacion());

					assertEquals("01", tramo.getFechaDesde().getDia());
					assertEquals("12", tramo.getFechaDesde().getMes());
					assertEquals("2020", tramo.getFechaDesde().getAnho());
					assertEquals("31", tramo.getFechaHasta().getDia());
					assertEquals("12", tramo.getFechaHasta().getMes());
					assertEquals("2020", tramo.getFechaHasta().getAnho());

					int datosSolicatodos = assertTramoActivoNormal(tramo);
					assertDatosSolicitadosCount(datosSolicatodos, tramo);
				}
			}

		}
	}

	@Test
	public void testIdcplnssTrabajadoresTramosII()
			throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException, JAXBException {
		try (InputStream is = IdcTest.class.getResourceAsStream("idcplnssII.pdf")) {
			TrabajadoresTramos trabajadoresTramos = Idcplnss.getTrabajadoresTramos(is,
					new TrabajadoresTramosCallback() {
					});

			marshal(trabajadoresTramos, System.out);

			Liquidacion liquidacion = trabajadoresTramos.getLiquidacion();

			assertEquals("0111", liquidacion.getCcc().getRegimen());
			assertEquals("01", liquidacion.getCcc().getProvincia());
			assertEquals("105577910", liquidacion.getCcc().getNumero());

			assertEquals("12", liquidacion.getPeriodoDesde().getMes());
			assertEquals("2020", liquidacion.getPeriodoDesde().getAnho());
			assertEquals("12", liquidacion.getPeriodoHasta().getMes());
			assertEquals("2020", liquidacion.getPeriodoHasta().getAnho());

			assertEquals(1, liquidacion.getLiquidacionMes().size());

			LiquidacionMes liquidacionesMes = liquidacion.getLiquidacionMes().get(0);
			assertEquals("12", liquidacionesMes.getMesLiquidativo().getMes());
			assertEquals("2020", liquidacionesMes.getMesLiquidativo().getAnho());

			Trabajadores trabajadores = liquidacionesMes.getTrabajadores();
			assertEquals(1, trabajadores.getTrabajador().size());

			for (Trabajador trabajador : trabajadores.getTrabajador()) {

				assertEquals("011006286569", trabajador.getNaf());

				assertEquals(1, trabajador.getTramos().getTramo().size());
				for (Tramo tramo : trabajador.getTramos().getTramo()) {

					assertEquals("07", tramo.getInformacionAfiliacion().getGrupoCotizacion());

					assertEquals("01", tramo.getFechaDesde().getDia());
					assertEquals("12", tramo.getFechaDesde().getMes());
					assertEquals("2020", tramo.getFechaDesde().getAnho());
					assertEquals("31", tramo.getFechaHasta().getDia());
					assertEquals("12", tramo.getFechaHasta().getMes());
					assertEquals("2020", tramo.getFechaHasta().getAnho());

					int datosSolicatodos = assertTramoActivoNormal(tramo);
					assertDatosSolicitadosCount(datosSolicatodos, tramo);
				}
			}

		}
	}

	@Test
	public void testIdcplnssTrabajadoresTramosIV()
			throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException, JAXBException {
		try (InputStream is = IdcTest.class.getResourceAsStream("idcplnssIV.pdf")) {
			TrabajadoresTramos trabajadoresTramos = Idcplnss.getTrabajadoresTramos(is,
					new TrabajadoresTramosCallback() {
					});

			marshal(trabajadoresTramos, System.out);

			Liquidacion liquidacion = trabajadoresTramos.getLiquidacion();

			assertEquals("0111", liquidacion.getCcc().getRegimen());
			assertEquals("09", liquidacion.getCcc().getProvincia());
			assertEquals("106571578", liquidacion.getCcc().getNumero());

			assertEquals("12", liquidacion.getPeriodoDesde().getMes());
			assertEquals("2020", liquidacion.getPeriodoDesde().getAnho());
			assertEquals("12", liquidacion.getPeriodoHasta().getMes());
			assertEquals("2020", liquidacion.getPeriodoHasta().getAnho());

			assertEquals(1, liquidacion.getLiquidacionMes().size());

			LiquidacionMes liquidacionesMes = liquidacion.getLiquidacionMes().get(0);
			assertEquals("12", liquidacionesMes.getMesLiquidativo().getMes());
			assertEquals("2020", liquidacionesMes.getMesLiquidativo().getAnho());

			Trabajadores trabajadores = liquidacionesMes.getTrabajadores();
			assertEquals(1, trabajadores.getTrabajador().size());

			for (Trabajador trabajador : trabajadores.getTrabajador()) {

				assertEquals("091019740438", trabajador.getNaf());

				assertEquals(2, trabajador.getTramos().getTramo().size());
				Tramo tramo = trabajador.getTramos().getTramo().get(0);
				assertEquals("08", tramo.getInformacionAfiliacion().getGrupoCotizacion());
				assertEquals("01", tramo.getFechaDesde().getDia());
				assertEquals("12", tramo.getFechaDesde().getMes());
				assertEquals("2020", tramo.getFechaDesde().getAnho());
				assertEquals("10", tramo.getFechaHasta().getDia());
				assertEquals("12", tramo.getFechaHasta().getMes());
				assertEquals("2020", tramo.getFechaHasta().getAnho());
				int datosSolictados = assertTramoERETotal(tramo);
				assertDatosSolicitado(tramo, "I", "51", "P");
				assertDatosSolicitadosCount(datosSolictados + 1, tramo);

				tramo = trabajador.getTramos().getTramo().get(1);
				assertEquals("08", tramo.getInformacionAfiliacion().getGrupoCotizacion());
				assertEquals("11", tramo.getFechaDesde().getDia());
				assertEquals("12", tramo.getFechaDesde().getMes());
				assertEquals("2020", tramo.getFechaDesde().getAnho());
				assertEquals("31", tramo.getFechaHasta().getDia());
				assertEquals("12", tramo.getFechaHasta().getMes());
				assertEquals("2020", tramo.getFechaHasta().getAnho());
				assertDatosSolicitado(tramo, "I", "51", "P");
			}

		}
	}

	@Test
	public void testIdcplnssTrabajadoresTramosVI()
			throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException, JAXBException {
		try (InputStream is = IdcTest.class.getResourceAsStream("idcplnssVI.pdf")) {
			TrabajadoresTramos trabajadoresTramos = Idcplnss.getTrabajadoresTramos(is,
					new TrabajadoresTramosCallback() {
					});

			marshal(trabajadoresTramos, System.out);

			Liquidacion liquidacion = trabajadoresTramos.getLiquidacion();

			assertEquals("0111", liquidacion.getCcc().getRegimen());
			assertEquals("11", liquidacion.getCcc().getProvincia());
			assertEquals("122534302", liquidacion.getCcc().getNumero());

			assertEquals("01", liquidacion.getPeriodoDesde().getMes());
			assertEquals("2021", liquidacion.getPeriodoDesde().getAnho());
			assertEquals("01", liquidacion.getPeriodoHasta().getMes());
			assertEquals("2021", liquidacion.getPeriodoHasta().getAnho());

			assertEquals(1, liquidacion.getLiquidacionMes().size());

			LiquidacionMes liquidacionesMes = liquidacion.getLiquidacionMes().get(0);
			assertEquals("01", liquidacionesMes.getMesLiquidativo().getMes());
			assertEquals("2021", liquidacionesMes.getMesLiquidativo().getAnho());

			Trabajadores trabajadores = liquidacionesMes.getTrabajadores();
			assertEquals(1, trabajadores.getTrabajador().size());

			for (Trabajador trabajador : trabajadores.getTrabajador()) {

				assertEquals("111016467058", trabajador.getNaf());

				assertEquals(3, trabajador.getTramos().getTramo().size());

				Tramo tramo = trabajador.getTramos().getTramo().get(0);
				assertEquals("07", tramo.getInformacionAfiliacion().getGrupoCotizacion());
				assertEquals("01", tramo.getFechaDesde().getDia());
				assertEquals("01", tramo.getFechaDesde().getMes());
				assertEquals("2021", tramo.getFechaDesde().getAnho());
				assertEquals("05", tramo.getFechaHasta().getDia());
				assertEquals("01", tramo.getFechaHasta().getMes());
				assertEquals("2021", tramo.getFechaHasta().getAnho());
				int datosSolicatodos = assertTramoActivoNormal(tramo);
				assertDatosSolicitadosCount(datosSolicatodos, tramo);

				tramo = trabajador.getTramos().getTramo().get(1);
				assertEquals("07", tramo.getInformacionAfiliacion().getGrupoCotizacion());
				assertEquals("06", tramo.getFechaDesde().getDia());
				assertEquals("01", tramo.getFechaDesde().getMes());
				assertEquals("2021", tramo.getFechaDesde().getAnho());
				assertEquals("22", tramo.getFechaHasta().getDia());
				assertEquals("01", tramo.getFechaHasta().getMes());
				assertEquals("2021", tramo.getFechaHasta().getAnho());

				tramo = trabajador.getTramos().getTramo().get(2);
				assertEquals("07", tramo.getInformacionAfiliacion().getGrupoCotizacion());
				assertEquals("25", tramo.getFechaDesde().getDia());
				assertEquals("01", tramo.getFechaDesde().getMes());
				assertEquals("2021", tramo.getFechaDesde().getAnho());
				assertEquals("31", tramo.getFechaHasta().getDia());
				assertEquals("01", tramo.getFechaHasta().getMes());
				assertEquals("2021", tramo.getFechaHasta().getAnho());
			}

		}
	}

	@Test
	public void testIdcplnssTrabajadoresTramosXI()
			throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException, JAXBException {
		try (InputStream is = IdcTest.class.getResourceAsStream("idcplnssXI.pdf")) {
			TrabajadoresTramos trabajadoresTramos = Idcplnss.getTrabajadoresTramos(is,
					new TrabajadoresTramosCallback() {
					});

			marshal(trabajadoresTramos, System.out);

			Liquidacion liquidacion = trabajadoresTramos.getLiquidacion();

			assertEquals("0111", liquidacion.getCcc().getRegimen());
			assertEquals("04", liquidacion.getCcc().getProvincia());
			assertEquals("118744682", liquidacion.getCcc().getNumero());

			assertEquals("10", liquidacion.getPeriodoDesde().getMes());
			assertEquals("2021", liquidacion.getPeriodoDesde().getAnho());
			assertEquals("10", liquidacion.getPeriodoHasta().getMes());
			assertEquals("2021", liquidacion.getPeriodoHasta().getAnho());

			assertEquals(1, liquidacion.getLiquidacionMes().size());

			LiquidacionMes liquidacionesMes = liquidacion.getLiquidacionMes().get(0);
			assertEquals("10", liquidacionesMes.getMesLiquidativo().getMes());
			assertEquals("2021", liquidacionesMes.getMesLiquidativo().getAnho());

			Trabajadores trabajadores = liquidacionesMes.getTrabajadores();
			assertEquals(1, trabajadores.getTrabajador().size());

			for (Trabajador trabajador : trabajadores.getTrabajador()) {

				assertEquals("411022261502", trabajador.getNaf());

				assertEquals(2, trabajador.getTramos().getTramo().size());

				Tramo tramo = trabajador.getTramos().getTramo().get(0);
				assertEquals("05", tramo.getInformacionAfiliacion().getGrupoCotizacion());
				assertEquals("01", tramo.getFechaDesde().getDia());
				assertEquals("10", tramo.getFechaDesde().getMes());
				assertEquals("2021", tramo.getFechaDesde().getAnho());
				assertEquals("28", tramo.getFechaHasta().getDia());
				assertEquals("10", tramo.getFechaHasta().getMes());
				assertEquals("2021", tramo.getFechaHasta().getAnho());
				int datosSolicatodos = assertTramoActivoNormal(tramo);
				assertDatosSolicitadosCount(datosSolicatodos, tramo);

				tramo = trabajador.getTramos().getTramo().get(1);
				assertEquals("05", tramo.getInformacionAfiliacion().getGrupoCotizacion());
				assertEquals("29", tramo.getFechaDesde().getDia());
				assertEquals("10", tramo.getFechaDesde().getMes());
				assertEquals("2021", tramo.getFechaDesde().getAnho());
				assertEquals("31", tramo.getFechaHasta().getDia());
				assertEquals("10", tramo.getFechaHasta().getMes());
				assertEquals("2021", tramo.getFechaHasta().getAnho());
				datosSolicatodos = assertTramoITPagoDirecto(tramo);
				assertDatosSolicitadosCount(datosSolicatodos, tramo);

			}

		}
	}

	@Test
	public void testIdcplnssTrabajadoresTramosXII()
			throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException, JAXBException {
		try (InputStream is = IdcTest.class.getResourceAsStream("idcplnssXII.pdf")) {
			TrabajadoresTramos trabajadoresTramos = Idcplnss.getTrabajadoresTramos(is,
					new TrabajadoresTramosCallback() {
					});

			marshal(trabajadoresTramos, System.out);

			Liquidacion liquidacion = trabajadoresTramos.getLiquidacion();

			assertEquals("0111", liquidacion.getCcc().getRegimen());
			assertEquals("01", liquidacion.getCcc().getProvincia());
			assertEquals("105360062", liquidacion.getCcc().getNumero());

			assertEquals("12", liquidacion.getPeriodoDesde().getMes());
			assertEquals("2021", liquidacion.getPeriodoDesde().getAnho());
			assertEquals("12", liquidacion.getPeriodoHasta().getMes());
			assertEquals("2021", liquidacion.getPeriodoHasta().getAnho());

			assertEquals(1, liquidacion.getLiquidacionMes().size());

			LiquidacionMes liquidacionesMes = liquidacion.getLiquidacionMes().get(0);
			assertEquals("12", liquidacionesMes.getMesLiquidativo().getMes());
			assertEquals("2021", liquidacionesMes.getMesLiquidativo().getAnho());

			Trabajadores trabajadores = liquidacionesMes.getTrabajadores();
			assertEquals(1, trabajadores.getTrabajador().size());

			for (Trabajador trabajador : trabajadores.getTrabajador()) {

				assertEquals("011011187190", trabajador.getNaf());

				assertEquals(2, trabajador.getTramos().getTramo().size());

				Tramo tramo = trabajador.getTramos().getTramo().get(0);
				assertEquals("02", tramo.getInformacionAfiliacion().getGrupoCotizacion());
				assertEquals("01", tramo.getFechaDesde().getDia());
				assertEquals("12", tramo.getFechaDesde().getMes());
				assertEquals("2021", tramo.getFechaDesde().getAnho());
				assertEquals("23", tramo.getFechaHasta().getDia());
				assertEquals("12", tramo.getFechaHasta().getMes());
				assertEquals("2021", tramo.getFechaHasta().getAnho());
				int datosSolicitados = assertTramoActivoNormal(tramo);
				assertDatosSolicitadosCount(datosSolicitados, tramo);

				tramo = trabajador.getTramos().getTramo().get(1);
				assertEquals("02", tramo.getInformacionAfiliacion().getGrupoCotizacion());
				assertEquals("24", tramo.getFechaDesde().getDia());
				assertEquals("12", tramo.getFechaDesde().getMes());
				assertEquals("2021", tramo.getFechaDesde().getAnho());
				assertEquals("31", tramo.getFechaHasta().getDia());
				assertEquals("12", tramo.getFechaHasta().getMes());
				assertEquals("2021", tramo.getFechaHasta().getAnho());

				datosSolicitados = assertTramoITATEPPagoDelegado(tramo);
				assertDatosSolicitadosCount(datosSolicitados, tramo);

			}

		}
	}

	@Test
	public void testIdcplnssTrabajadoresTramosXIII()
			throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException, JAXBException {
		try (InputStream is = IdcTest.class.getResourceAsStream("idcplnssXIII.pdf")) {
			TrabajadoresTramos trabajadoresTramos = Idcplnss.getTrabajadoresTramos(is,
					new TrabajadoresTramosCallback() {
					});

			marshal(trabajadoresTramos, System.out);

			Liquidacion liquidacion = trabajadoresTramos.getLiquidacion();

			assertEquals("0111", liquidacion.getCcc().getRegimen());
			assertEquals("11", liquidacion.getCcc().getProvincia());
			assertEquals("122534302", liquidacion.getCcc().getNumero());

			assertEquals("12", liquidacion.getPeriodoDesde().getMes());
			assertEquals("2021", liquidacion.getPeriodoDesde().getAnho());
			assertEquals("12", liquidacion.getPeriodoHasta().getMes());
			assertEquals("2021", liquidacion.getPeriodoHasta().getAnho());

			assertEquals(1, liquidacion.getLiquidacionMes().size());

			LiquidacionMes liquidacionesMes = liquidacion.getLiquidacionMes().get(0);
			assertEquals("12", liquidacionesMes.getMesLiquidativo().getMes());
			assertEquals("2021", liquidacionesMes.getMesLiquidativo().getAnho());

			Trabajadores trabajadores = liquidacionesMes.getTrabajadores();
			assertEquals(1, trabajadores.getTrabajador().size());

			for (Trabajador trabajador : trabajadores.getTrabajador()) {

				assertEquals("231021198539", trabajador.getNaf());

				assertEquals(2, trabajador.getTramos().getTramo().size());

				Tramo tramo = trabajador.getTramos().getTramo().get(0);
				assertEquals("03", tramo.getInformacionAfiliacion().getGrupoCotizacion());
				assertEquals("01", tramo.getFechaDesde().getDia());
				assertEquals("12", tramo.getFechaDesde().getMes());
				assertEquals("2021", tramo.getFechaDesde().getAnho());
				assertEquals("22", tramo.getFechaHasta().getDia());
				assertEquals("12", tramo.getFechaHasta().getMes());
				assertEquals("2021", tramo.getFechaHasta().getAnho());
				int datosSolicitados = assertTramoActivoNormal(tramo);
				assertDatosSolicitadosCount(datosSolicitados, tramo);

				tramo = trabajador.getTramos().getTramo().get(1);
				assertEquals("03", tramo.getInformacionAfiliacion().getGrupoCotizacion());
				assertEquals("23", tramo.getFechaDesde().getDia());
				assertEquals("12", tramo.getFechaDesde().getMes());
				assertEquals("2021", tramo.getFechaDesde().getAnho());
				assertEquals("31", tramo.getFechaHasta().getDia());
				assertEquals("12", tramo.getFechaHasta().getMes());
				assertEquals("2021", tramo.getFechaHasta().getAnho());
				datosSolicitados = assertTramoIT15PrimerosDias(tramo);
				assertDatosSolicitadosCount(datosSolicitados, tramo);

			}

		}
	}

	@Test
	public void testIdcplnssTrabajadoresTramosXIV()
			throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException, JAXBException {
		try (InputStream is = IdcTest.class.getResourceAsStream("idcplnssXIV.pdf")) {
			TrabajadoresTramos trabajadoresTramos = Idcplnss.getTrabajadoresTramos(is,
					new TrabajadoresTramosCallback() {
					});

			marshal(trabajadoresTramos, System.out);

			Liquidacion liquidacion = trabajadoresTramos.getLiquidacion();

			assertEquals("0111", liquidacion.getCcc().getRegimen());
			assertEquals("11", liquidacion.getCcc().getProvincia());
			assertEquals("122534302", liquidacion.getCcc().getNumero());

			assertEquals("01", liquidacion.getPeriodoDesde().getMes());
			assertEquals("2022", liquidacion.getPeriodoDesde().getAnho());
			assertEquals("01", liquidacion.getPeriodoHasta().getMes());
			assertEquals("2022", liquidacion.getPeriodoHasta().getAnho());

			assertEquals(1, liquidacion.getLiquidacionMes().size());

			LiquidacionMes liquidacionesMes = liquidacion.getLiquidacionMes().get(0);
			assertEquals("01", liquidacionesMes.getMesLiquidativo().getMes());
			assertEquals("2022", liquidacionesMes.getMesLiquidativo().getAnho());

			Trabajadores trabajadores = liquidacionesMes.getTrabajadores();
			assertEquals(1, trabajadores.getTrabajador().size());

			for (Trabajador trabajador : trabajadores.getTrabajador()) {

				assertEquals("231021198539", trabajador.getNaf());

				assertEquals(3, trabajador.getTramos().getTramo().size());

				Tramo tramo = trabajador.getTramos().getTramo().get(0);
				assertEquals("03", tramo.getInformacionAfiliacion().getGrupoCotizacion());
				assertEquals("01", tramo.getFechaDesde().getDia());
				assertEquals("01", tramo.getFechaDesde().getMes());
				assertEquals("2022", tramo.getFechaDesde().getAnho());
				assertEquals("06", tramo.getFechaHasta().getDia());
				assertEquals("01", tramo.getFechaHasta().getMes());
				assertEquals("2022", tramo.getFechaHasta().getAnho());
				int datosSolicitados = assertTramoIT15PrimerosDias(tramo);
				assertDatosSolicitadosCount(datosSolicitados, tramo);

				tramo = trabajador.getTramos().getTramo().get(1);
				assertEquals("03", tramo.getInformacionAfiliacion().getGrupoCotizacion());
				assertEquals("07", tramo.getFechaDesde().getDia());
				assertEquals("01", tramo.getFechaDesde().getMes());
				assertEquals("2022", tramo.getFechaDesde().getAnho());
				assertEquals("11", tramo.getFechaHasta().getDia());
				assertEquals("01", tramo.getFechaHasta().getMes());
				assertEquals("2022", tramo.getFechaHasta().getAnho());
				datosSolicitados = assertTramoITPagoDelegado(tramo);
				assertDatosSolicitadosCount(datosSolicitados, tramo);

				tramo = trabajador.getTramos().getTramo().get(2);
				assertEquals("03", tramo.getInformacionAfiliacion().getGrupoCotizacion());
				assertEquals("12", tramo.getFechaDesde().getDia());
				assertEquals("01", tramo.getFechaDesde().getMes());
				assertEquals("2022", tramo.getFechaDesde().getAnho());
				assertEquals("31", tramo.getFechaHasta().getDia());
				assertEquals("01", tramo.getFechaHasta().getMes());
				assertEquals("2022", tramo.getFechaHasta().getAnho());
				datosSolicitados = assertTramoITPagoDelegado(tramo);
				assertDatosSolicitadosCount(datosSolicitados, tramo);

			}

		}
	}

	@Test
	public void testIdcplnssTrabajadoresTramosXV()
			throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException, JAXBException {
		try (InputStream is = IdcTest.class.getResourceAsStream("idcplnssXV.pdf")) {
			TrabajadoresTramos trabajadoresTramos = Idcplnss.getTrabajadoresTramos(is,
					new TrabajadoresTramosCallback() {
					});

			marshal(trabajadoresTramos, System.out);

			Liquidacion liquidacion = trabajadoresTramos.getLiquidacion();

			assertEquals("0111", liquidacion.getCcc().getRegimen());
			assertEquals("11", liquidacion.getCcc().getProvincia());
			assertEquals("122534302", liquidacion.getCcc().getNumero());

			assertEquals("12", liquidacion.getPeriodoDesde().getMes());
			assertEquals("2021", liquidacion.getPeriodoDesde().getAnho());
			assertEquals("12", liquidacion.getPeriodoHasta().getMes());
			assertEquals("2021", liquidacion.getPeriodoHasta().getAnho());

			assertEquals(1, liquidacion.getLiquidacionMes().size());

			LiquidacionMes liquidacionesMes = liquidacion.getLiquidacionMes().get(0);
			assertEquals("12", liquidacionesMes.getMesLiquidativo().getMes());
			assertEquals("2021", liquidacionesMes.getMesLiquidativo().getAnho());

			Trabajadores trabajadores = liquidacionesMes.getTrabajadores();
			assertEquals(1, trabajadores.getTrabajador().size());

			for (Trabajador trabajador : trabajadores.getTrabajador()) {

				assertEquals("111024858669", trabajador.getNaf());

				assertEquals(2, trabajador.getTramos().getTramo().size());

				Tramo tramo = trabajador.getTramos().getTramo().get(0);
				assertEquals("03", tramo.getInformacionAfiliacion().getGrupoCotizacion());
				assertEquals("01", tramo.getFechaDesde().getDia());
				assertEquals("12", tramo.getFechaDesde().getMes());
				assertEquals("2021", tramo.getFechaDesde().getAnho());
				assertEquals("22", tramo.getFechaHasta().getDia());
				assertEquals("12", tramo.getFechaHasta().getMes());
				assertEquals("2021", tramo.getFechaHasta().getAnho());
				int datosSolicitados = assertTramoMaternidadTiempoCompleto(tramo);
				assertDatosSolicitadosCount(datosSolicitados, tramo);

				tramo = trabajador.getTramos().getTramo().get(1);
				assertEquals("03", tramo.getInformacionAfiliacion().getGrupoCotizacion());
				assertEquals("23", tramo.getFechaDesde().getDia());
				assertEquals("12", tramo.getFechaDesde().getMes());
				assertEquals("2021", tramo.getFechaDesde().getAnho());
				assertEquals("31", tramo.getFechaHasta().getDia());
				assertEquals("12", tramo.getFechaHasta().getMes());
				assertEquals("2021", tramo.getFechaHasta().getAnho());
				datosSolicitados = assertTramoActivoNormal(tramo);
				assertDatosSolicitadosCount(datosSolicitados, tramo);

			}

		}
	}

	@Test
	public void testIdcplnssTrabajadoresTramosXVI()
			throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException, JAXBException {
		try (InputStream is = IdcTest.class.getResourceAsStream("idcplnssXVI.pdf")) {
			TrabajadoresTramos trabajadoresTramos = Idcplnss.getTrabajadoresTramos(is,
					new TrabajadoresTramosCallback() {
					});

			marshal(trabajadoresTramos, System.out);

			Liquidacion liquidacion = trabajadoresTramos.getLiquidacion();

			assertEquals("0163", liquidacion.getCcc().getRegimen());
			assertEquals("11", liquidacion.getCcc().getProvincia());
			assertEquals("123761552", liquidacion.getCcc().getNumero());

			assertEquals("03", liquidacion.getPeriodoDesde().getMes());
			assertEquals("2022", liquidacion.getPeriodoDesde().getAnho());
			assertEquals("03", liquidacion.getPeriodoHasta().getMes());
			assertEquals("2022", liquidacion.getPeriodoHasta().getAnho());

			assertEquals(1, liquidacion.getLiquidacionMes().size());

			LiquidacionMes liquidacionesMes = liquidacion.getLiquidacionMes().get(0);
			assertEquals("03", liquidacionesMes.getMesLiquidativo().getMes());
			assertEquals("2022", liquidacionesMes.getMesLiquidativo().getAnho());

			Trabajadores trabajadores = liquidacionesMes.getTrabajadores();
			assertEquals(1, trabajadores.getTrabajador().size());

			for (Trabajador trabajador : trabajadores.getTrabajador()) {

				assertEquals("291117165286", trabajador.getNaf());

				assertEquals(1, trabajador.getTramos().getTramo().size());

				Tramo tramo = trabajador.getTramos().getTramo().get(0);
				assertEquals("09", tramo.getInformacionAfiliacion().getGrupoCotizacion());
				assertEquals("07", tramo.getFechaDesde().getDia());
				assertEquals("03", tramo.getFechaDesde().getMes());
				assertEquals("2022", tramo.getFechaDesde().getAnho());
				assertEquals("31", tramo.getFechaHasta().getDia());
				assertEquals("03", tramo.getFechaHasta().getMes());
				assertEquals("2022", tramo.getFechaHasta().getAnho());
				assertTramoActivoNormal(tramo);
				assertNoDatosSolicitado(tramo, "I", "51");

			}

		}
	}

	@Test
	public void testIdcplcccTrabajadoresTramosXIX()
			throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException, JAXBException {
		try (InputStream is = IdcTest.class.getResourceAsStream("idcplcccXIX.pdf")) {
			TrabajadoresTramos trabajadoresTramos = Idcplccc.geTrabajadoresTramos(is,
					new TrabajadoresTramosCallback() {
					});

			marshal(trabajadoresTramos, System.out);
			// PEC 17 Expedientes de Regulación de Empleo Total
			Liquidacion liquidacion = trabajadoresTramos.getLiquidacion();

			assertEquals("0111", liquidacion.getCcc().getRegimen());
			assertEquals("08", liquidacion.getCcc().getProvincia());
			assertEquals("209971175", liquidacion.getCcc().getNumero());

			assertEquals("11", liquidacion.getPeriodoDesde().getMes());
			assertEquals("2022", liquidacion.getPeriodoDesde().getAnho());
			assertEquals("11", liquidacion.getPeriodoHasta().getMes());
			assertEquals("2022", liquidacion.getPeriodoHasta().getAnho());

			assertEquals(1, liquidacion.getLiquidacionMes().size());

			LiquidacionMes liquidacionesMes = liquidacion.getLiquidacionMes().get(0);
			assertEquals("11", liquidacionesMes.getMesLiquidativo().getMes());
			assertEquals("2022", liquidacionesMes.getMesLiquidativo().getAnho());

			Trabajadores trabajadores = liquidacionesMes.getTrabajadores();
			assertEquals(1, trabajadores.getTrabajador().size());

			for (Trabajador trabajador : trabajadores.getTrabajador()) {

				assertEquals("081179728618", trabajador.getNaf());

				assertEquals(1, trabajador.getTramos().getTramo().size());

				Tramo tramo = trabajador.getTramos().getTramo().get(0);
				assertEquals("07", tramo.getInformacionAfiliacion().getGrupoCotizacion());
				assertEquals("01", tramo.getFechaDesde().getDia());
				assertEquals("11", tramo.getFechaDesde().getMes());
				assertEquals("2022", tramo.getFechaDesde().getAnho());
				assertEquals("30", tramo.getFechaHasta().getDia());
				assertEquals("11", tramo.getFechaHasta().getMes());
				assertEquals("2022", tramo.getFechaHasta().getAnho());
				assertTramoERETotal(tramo);
				assertNoDatosSolicitado(tramo, "I", "51");

			}

		}
	}

	@Test
	public void testIdcplcccTrabajadoresTramosXX()
			throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException, JAXBException {
		try (InputStream is = IdcTest.class.getResourceAsStream("idcplcccXX.pdf")) {
			TrabajadoresTramos trabajadoresTramos = Idcplccc.geTrabajadoresTramos(is,
					new TrabajadoresTramosCallback() {
					});

			marshal(trabajadoresTramos, System.out);
			// PEC 17 Expedientes de Regulación de Empleo Total
			Liquidacion liquidacion = trabajadoresTramos.getLiquidacion();

			assertEquals("0111", liquidacion.getCcc().getRegimen());
			assertEquals("41", liquidacion.getCcc().getProvincia());
			assertEquals("134362691", liquidacion.getCcc().getNumero());

			assertEquals("11", liquidacion.getPeriodoDesde().getMes());
			assertEquals("2022", liquidacion.getPeriodoDesde().getAnho());
			assertEquals("11", liquidacion.getPeriodoHasta().getMes());
			assertEquals("2022", liquidacion.getPeriodoHasta().getAnho());

			assertEquals(1, liquidacion.getLiquidacionMes().size());

			LiquidacionMes liquidacionesMes = liquidacion.getLiquidacionMes().get(0);
			assertEquals("11", liquidacionesMes.getMesLiquidativo().getMes());
			assertEquals("2022", liquidacionesMes.getMesLiquidativo().getAnho());

			Trabajadores trabajadores = liquidacionesMes.getTrabajadores();
			assertEquals(10, trabajadores.getTrabajador().size());

			for (Trabajador trabajador : trabajadores.getTrabajador()) {
			    	
			    	if ("410161125294".equals(trabajador.getNaf()) ) {
        				assertEquals(1, trabajador.getTramos().getTramo().size());
        				Tramo tramo = trabajador.getTramos().getTramo().get(0);
        				assertEquals("09", tramo.getInformacionAfiliacion().getGrupoCotizacion());
        				assertEquals("01", tramo.getFechaDesde().getDia());
        				assertEquals("11", tramo.getFechaDesde().getMes());
        				assertEquals("2022", tramo.getFechaDesde().getAnho());
        				assertEquals("30", tramo.getFechaHasta().getDia());
        				assertEquals("11", tramo.getFechaHasta().getMes());
        				assertEquals("2022", tramo.getFechaHasta().getAnho());
        				assertTramoActivoNormal(tramo);
        				assertDatosSolicitado(tramo, "I", "51", "P");
			    	} else if ("410200359572".equals(trabajador.getNaf()) ) {
        				assertEquals(1, trabajador.getTramos().getTramo().size());
        				Tramo tramo = trabajador.getTramos().getTramo().get(0);
        				assertEquals("07", tramo.getInformacionAfiliacion().getGrupoCotizacion());
        				assertEquals("01", tramo.getFechaDesde().getDia());
        				assertEquals("11", tramo.getFechaDesde().getMes());
        				assertEquals("2022", tramo.getFechaDesde().getAnho());
        				assertEquals("30", tramo.getFechaHasta().getDia());
        				assertEquals("11", tramo.getFechaHasta().getMes());
        				assertEquals("2022", tramo.getFechaHasta().getAnho());
        				assertTramoActivoNormal(tramo);
        				assertNoDatosSolicitado(tramo, "I", "51");
			    	} else if ("411014285169".equals(trabajador.getNaf()) ) {
        				assertEquals(1, trabajador.getTramos().getTramo().size());
        				Tramo tramo = trabajador.getTramos().getTramo().get(0);
        				assertEquals("10", tramo.getInformacionAfiliacion().getGrupoCotizacion());
        				assertEquals("01", tramo.getFechaDesde().getDia());
        				assertEquals("11", tramo.getFechaDesde().getMes());
        				assertEquals("2022", tramo.getFechaDesde().getAnho());
        				assertEquals("30", tramo.getFechaHasta().getDia());
        				assertEquals("11", tramo.getFechaHasta().getMes());
        				assertEquals("2022", tramo.getFechaHasta().getAnho());
        				assertTramoActivoNormal(tramo);
        				assertDatosSolicitado(tramo, "I", "51", "P");
			    	} else if ( "411024557267".equals(trabajador.getNaf()) ) {
			    	    	assertEquals("411024557267", trabajador.getNaf());
        				assertEquals(1, trabajador.getTramos().getTramo().size());
        				Tramo tramo = trabajador.getTramos().getTramo().get(0);
        				assertEquals("09", tramo.getInformacionAfiliacion().getGrupoCotizacion());
        				assertEquals("02", tramo.getFechaDesde().getDia());
        				assertEquals("11", tramo.getFechaDesde().getMes());
        				assertEquals("2022", tramo.getFechaDesde().getAnho());
        				assertEquals("25", tramo.getFechaHasta().getDia());
        				assertEquals("11", tramo.getFechaHasta().getMes());
        				assertEquals("2022", tramo.getFechaHasta().getAnho());
        				assertTramoActivoNormal(tramo);
        				assertDatosSolicitado(tramo, "I", "51", "P");
			    	} else if ("411025206157".equals(trabajador.getNaf()) ) {
        				assertEquals(1, trabajador.getTramos().getTramo().size());
        				Tramo tramo = trabajador.getTramos().getTramo().get(0);
        				assertEquals("09", tramo.getInformacionAfiliacion().getGrupoCotizacion());
        				assertEquals("01", tramo.getFechaDesde().getDia());
        				assertEquals("11", tramo.getFechaDesde().getMes());
        				assertEquals("2022", tramo.getFechaDesde().getAnho());
        				assertEquals("18", tramo.getFechaHasta().getDia());
        				assertEquals("11", tramo.getFechaHasta().getMes());
        				assertEquals("2022", tramo.getFechaHasta().getAnho());
        				assertTramoActivoNormal(tramo);
        				assertDatosSolicitado(tramo, "I", "51", "P");
			    	} else if ("411033325461".equals(trabajador.getNaf()) ) {
			    	    	assertEquals("411033325461", trabajador.getNaf());
        				assertEquals(1, trabajador.getTramos().getTramo().size());
        				Tramo tramo = trabajador.getTramos().getTramo().get(0);
        				assertEquals("10", tramo.getInformacionAfiliacion().getGrupoCotizacion());
        				assertEquals("01", tramo.getFechaDesde().getDia());
        				assertEquals("11", tramo.getFechaDesde().getMes());
        				assertEquals("2022", tramo.getFechaDesde().getAnho());
        				assertEquals("30", tramo.getFechaHasta().getDia());
        				assertEquals("11", tramo.getFechaHasta().getMes());
        				assertEquals("2022", tramo.getFechaHasta().getAnho());
        				assertTramoActivoNormal(tramo);
        				assertDatosSolicitado(tramo, "I", "51", "P");
			    	    
			    	} else if ("411046423895".equals(trabajador.getNaf()) ) {
			    	    	assertEquals("411046423895", trabajador.getNaf());
        				assertEquals(1, trabajador.getTramos().getTramo().size());
        				Tramo tramo = trabajador.getTramos().getTramo().get(0);
        				assertEquals("09", tramo.getInformacionAfiliacion().getGrupoCotizacion());
        				assertEquals("02", tramo.getFechaDesde().getDia());
        				assertEquals("11", tramo.getFechaDesde().getMes());
        				assertEquals("2022", tramo.getFechaDesde().getAnho());
        				assertEquals("30", tramo.getFechaHasta().getDia());
        				assertEquals("11", tramo.getFechaHasta().getMes());
        				assertEquals("2022", tramo.getFechaHasta().getAnho());
        				assertTramoActivoNormal(tramo);
        				assertDatosSolicitado(tramo, "I", "51", "P");
			    	} else if ("411055684365".equals(trabajador.getNaf()) ) {
        				assertEquals(1, trabajador.getTramos().getTramo().size());
        				Tramo tramo = trabajador.getTramos().getTramo().get(0);
        				assertEquals("08", tramo.getInformacionAfiliacion().getGrupoCotizacion());
        				assertEquals("08", tramo.getFechaDesde().getDia());
        				assertEquals("11", tramo.getFechaDesde().getMes());
        				assertEquals("2022", tramo.getFechaDesde().getAnho());
        				assertEquals("30", tramo.getFechaHasta().getDia());
        				assertEquals("11", tramo.getFechaHasta().getMes());
        				assertEquals("2022", tramo.getFechaHasta().getAnho());
        				assertTramoActivoNormal(tramo);
        				assertDatosSolicitado(tramo, "I", "51", "P");
			    	} else if ("411083997049".equals(trabajador.getNaf()) ) {
        				assertEquals(1, trabajador.getTramos().getTramo().size());
        				Tramo tramo = trabajador.getTramos().getTramo().get(0);
        				assertEquals("10", tramo.getInformacionAfiliacion().getGrupoCotizacion());
        				assertEquals("01", tramo.getFechaDesde().getDia());
        				assertEquals("11", tramo.getFechaDesde().getMes());
        				assertEquals("2022", tramo.getFechaDesde().getAnho());
        				assertEquals("30", tramo.getFechaHasta().getDia());
        				assertEquals("11", tramo.getFechaHasta().getMes());
        				assertEquals("2022", tramo.getFechaHasta().getAnho());
        				assertTramoActivoNormal(tramo);
        				assertDatosSolicitado(tramo, "I", "51", "P");
			    	} else if ("411087630408".equals(trabajador.getNaf()) ) {
        				assertEquals(1, trabajador.getTramos().getTramo().size());
        				Tramo tramo = trabajador.getTramos().getTramo().get(0);
        				assertEquals("10", tramo.getInformacionAfiliacion().getGrupoCotizacion());
        				assertEquals("01", tramo.getFechaDesde().getDia());
        				assertEquals("11", tramo.getFechaDesde().getMes());
        				assertEquals("2022", tramo.getFechaDesde().getAnho());
        				assertEquals("30", tramo.getFechaHasta().getDia());
        				assertEquals("11", tramo.getFechaHasta().getMes());
        				assertEquals("2022", tramo.getFechaHasta().getAnho());
        				assertTramoActivoNormal(tramo);
        				assertDatosSolicitado(tramo, "I", "51", "P");
			    	} else {
			    	    fail("Unknown Trabajador");
			    	}

			}

		}
	}

	@Test
	public void testIdcplcccTrabajadoresTramosXXI()
			throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException, JAXBException {
		try (InputStream is = IdcTest.class.getResourceAsStream("idcplcccXXI.pdf")) {
			TrabajadoresTramos trabajadoresTramos = Idcplccc.geTrabajadoresTramos(is,
					new TrabajadoresTramosCallback() {
					});

			marshal(trabajadoresTramos, System.out);
			// PEC 17 Expedientes de Regulación de Empleo Total
			Liquidacion liquidacion = trabajadoresTramos.getLiquidacion();

			assertEquals("0111", liquidacion.getCcc().getRegimen());
			assertEquals("07", liquidacion.getCcc().getProvincia());
			assertEquals("129512528", liquidacion.getCcc().getNumero());

			assertEquals("01", liquidacion.getPeriodoDesde().getMes());
			assertEquals("2023", liquidacion.getPeriodoDesde().getAnho());
			assertEquals("01", liquidacion.getPeriodoHasta().getMes());
			assertEquals("2023", liquidacion.getPeriodoHasta().getAnho());

			assertEquals(1, liquidacion.getLiquidacionMes().size());

			LiquidacionMes liquidacionesMes = liquidacion.getLiquidacionMes().get(0);
			assertEquals("01", liquidacionesMes.getMesLiquidativo().getMes());
			assertEquals("2023", liquidacionesMes.getMesLiquidativo().getAnho());

			Trabajadores trabajadores = liquidacionesMes.getTrabajadores();
			assertEquals(6, trabajadores.getTrabajador().size());

			for (Trabajador trabajador : trabajadores.getTrabajador()) {
			    	
			    	if ("071020348439".equals(trabajador.getNaf()) ) {
        				assertEquals(1, trabajador.getTramos().getTramo().size());
        				Tramo tramo = trabajador.getTramos().getTramo().get(0);
        				assertEquals("08", tramo.getInformacionAfiliacion().getGrupoCotizacion());
        				assertEquals("01", tramo.getFechaDesde().getDia());
        				assertEquals("01", tramo.getFechaDesde().getMes());
        				assertEquals("2023", tramo.getFechaDesde().getAnho());
        				assertEquals("31", tramo.getFechaHasta().getDia());
        				assertEquals("01", tramo.getFechaHasta().getMes());
        				assertEquals("2023", tramo.getFechaHasta().getAnho());
        				assertTramoActivoNormal(tramo);
        				assertDatosSolicitado(tramo, "I", "51", "P");
			    	} else if ("071024494177".equals(trabajador.getNaf()) ) {
        				assertEquals(1, trabajador.getTramos().getTramo().size());
        				Tramo tramo = trabajador.getTramos().getTramo().get(0);
        				assertEquals("08", tramo.getInformacionAfiliacion().getGrupoCotizacion());
        				assertEquals("01", tramo.getFechaDesde().getDia());
        				assertEquals("01", tramo.getFechaDesde().getMes());
        				assertEquals("2023", tramo.getFechaDesde().getAnho());
        				assertEquals("31", tramo.getFechaHasta().getDia());
        				assertEquals("01", tramo.getFechaHasta().getMes());
        				assertEquals("2023", tramo.getFechaHasta().getAnho());
        				assertTramoActivoNormal(tramo);
        				assertDatosSolicitado(tramo, "I", "51", "P");
			    	} else if ("071044120412".equals(trabajador.getNaf()) ) {
        				assertEquals(1, trabajador.getTramos().getTramo().size());
        				Tramo tramo = trabajador.getTramos().getTramo().get(0);
        				assertEquals("08", tramo.getInformacionAfiliacion().getGrupoCotizacion());
        				assertEquals("01", tramo.getFechaDesde().getDia());
        				assertEquals("01", tramo.getFechaDesde().getMes());
        				assertEquals("2023", tramo.getFechaDesde().getAnho());
        				assertEquals("31", tramo.getFechaHasta().getDia());
        				assertEquals("01", tramo.getFechaHasta().getMes());
        				assertEquals("2023", tramo.getFechaHasta().getAnho());
        				assertTramoActivoNormal(tramo);
        				assertDatosSolicitado(tramo, "I", "51", "P");
			    	} else if ( "071053502332".equals(trabajador.getNaf()) ) {
        				assertEquals(1, trabajador.getTramos().getTramo().size());
        				Tramo tramo = trabajador.getTramos().getTramo().get(0);
        				assertEquals("09", tramo.getInformacionAfiliacion().getGrupoCotizacion());
        				assertEquals("01", tramo.getFechaDesde().getDia());
        				assertEquals("01", tramo.getFechaDesde().getMes());
        				assertEquals("2023", tramo.getFechaDesde().getAnho());
        				assertEquals("04", tramo.getFechaHasta().getDia());
        				assertEquals("01", tramo.getFechaHasta().getMes());
        				assertEquals("2023", tramo.getFechaHasta().getAnho());
        				assertTramoActivoNormal(tramo);
        				assertDatosSolicitado(tramo, "I", "51", "P");
			    	} else if ("071108307332".equals(trabajador.getNaf()) ) {
        				Tramo tramo = trabajador.getTramos().getTramo().get(0);
        				assertEquals("08", tramo.getInformacionAfiliacion().getGrupoCotizacion());
        				assertEquals("01", tramo.getFechaDesde().getDia());
        				assertEquals("01", tramo.getFechaDesde().getMes());
        				assertEquals("2023", tramo.getFechaDesde().getAnho());
        				assertEquals("31", tramo.getFechaHasta().getDia());
        				assertEquals("01", tramo.getFechaHasta().getMes());
        				assertEquals("2023", tramo.getFechaHasta().getAnho());
        				assertTramoActivoNormal(tramo);
        				assertDatosSolicitado(tramo, "I", "51", "P");
			    	} else if ("081103036980".equals(trabajador.getNaf()) ) {
        				Tramo tramo = trabajador.getTramos().getTramo().get(0);
        				assertEquals("08", tramo.getInformacionAfiliacion().getGrupoCotizacion());
        				assertEquals("01", tramo.getFechaDesde().getDia());
        				assertEquals("01", tramo.getFechaDesde().getMes());
        				assertEquals("2023", tramo.getFechaDesde().getAnho());
        				assertEquals("28", tramo.getFechaHasta().getDia());
        				assertEquals("01", tramo.getFechaHasta().getMes());
        				assertEquals("2023", tramo.getFechaHasta().getAnho());
        				assertTramoActivoNormal(tramo);
        				assertDatosSolicitado(tramo, "I", "51", "P");
			    	} else {
			    	    fail("Unknown Trabajador");
			    	}

			}

		}
	}

	@Test
	public void testIdcplcccTrabajadoresTramosXXII()
			throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException, JAXBException {
		try (InputStream is = IdcTest.class.getResourceAsStream("idcplcccXXII.pdf")) {
			TrabajadoresTramos trabajadoresTramos = Idcplccc.geTrabajadoresTramos(is,
				new TrabajadoresTramosCallback() {
			    		@Override
			    		public boolean isPartTimeEmployee(String ssNum, String ccc, Date start, Date end) {
			    		    return true;
			    		}
				});
	
			marshal(trabajadoresTramos, System.out);
	
			Liquidacion liquidacion = trabajadoresTramos.getLiquidacion();
	
			assertEquals("0111", liquidacion.getCcc().getRegimen());
			assertEquals("09", liquidacion.getCcc().getProvincia());
			assertEquals("106684847", liquidacion.getCcc().getNumero());
	
			assertEquals("02", liquidacion.getPeriodoDesde().getMes());
			assertEquals("2023", liquidacion.getPeriodoDesde().getAnho());
			assertEquals("02", liquidacion.getPeriodoHasta().getMes());
			assertEquals("2023", liquidacion.getPeriodoHasta().getAnho());
	
			assertEquals(1, liquidacion.getLiquidacionMes().size());
	
			LiquidacionMes liquidacionesMes = liquidacion.getLiquidacionMes().get(0);
			assertEquals("02", liquidacionesMes.getMesLiquidativo().getMes());
			assertEquals("2023", liquidacionesMes.getMesLiquidativo().getAnho());
	
			Trabajadores trabajadores = liquidacionesMes.getTrabajadores();
			assertEquals(5, trabajadores.getTrabajador().size());
	
			for (Trabajador trabajador : trabajadores.getTrabajador()) {
			    	if (trabajador.getNaf().equals("091011246369") ) {
	
				assertEquals(2, trabajador.getTramos().getTramo().size());
	
				Tramo tramo = trabajador.getTramos().getTramo().get(0);
				assertEquals("06", tramo.getInformacionAfiliacion().getGrupoCotizacion());
				assertEquals("01", tramo.getFechaDesde().getDia());
				assertEquals("02", tramo.getFechaDesde().getMes());
				assertEquals("2023", tramo.getFechaDesde().getAnho());
				assertEquals("24", tramo.getFechaHasta().getDia());
				assertEquals("02", tramo.getFechaHasta().getMes());
				assertEquals("2023", tramo.getFechaHasta().getAnho());
				assertTramoTiempoParcial(tramo);	

				tramo = trabajador.getTramos().getTramo().get(1);
				assertEquals("06", tramo.getInformacionAfiliacion().getGrupoCotizacion());
				assertEquals("25", tramo.getFechaDesde().getDia());
				assertEquals("02", tramo.getFechaDesde().getMes());
				assertEquals("2023", tramo.getFechaDesde().getAnho());
				assertEquals("28", tramo.getFechaHasta().getDia());
				assertEquals("02", tramo.getFechaHasta().getMes());
				assertEquals("2023", tramo.getFechaHasta().getAnho());
				assertTramoIT15PrimerosDias(tramo);	
			    }
			}
	
		}
	}

	@Test
	public void testIdcplcccTrabajadoresTramosXXIII()
			throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException, JAXBException {
		try (InputStream is = IdcTest.class.getResourceAsStream("idcplcccXXIII.pdf")) {
			TrabajadoresTramos trabajadoresTramos = Idcplccc.geTrabajadoresTramos(is,
				new TrabajadoresTramosCallback() {
			    		@Override
			    		public boolean isPartTimeEmployee(String ssNum, String ccc, Date start, Date end) {
			    		    return true;
			    		}
				});
	
			marshal(trabajadoresTramos, System.out);
	
			Liquidacion liquidacion = trabajadoresTramos.getLiquidacion();
	
			assertEquals("0111", liquidacion.getCcc().getRegimen());
			assertEquals("41", liquidacion.getCcc().getProvincia());
			assertEquals("111822622", liquidacion.getCcc().getNumero());
	
			assertEquals("02", liquidacion.getPeriodoDesde().getMes());
			assertEquals("2023", liquidacion.getPeriodoDesde().getAnho());
			assertEquals("02", liquidacion.getPeriodoHasta().getMes());
			assertEquals("2023", liquidacion.getPeriodoHasta().getAnho());
	
			assertEquals(1, liquidacion.getLiquidacionMes().size());
	
			LiquidacionMes liquidacionesMes = liquidacion.getLiquidacionMes().get(0);
			assertEquals("02", liquidacionesMes.getMesLiquidativo().getMes());
			assertEquals("2023", liquidacionesMes.getMesLiquidativo().getAnho());
	
			Trabajadores trabajadores = liquidacionesMes.getTrabajadores();
			assertEquals(1, trabajadores.getTrabajador().size());
	
			for (Trabajador trabajador : trabajadores.getTrabajador()) {
			    	assertEquals("411003115722", trabajador.getNaf());
				assertEquals(2, trabajador.getTramos().getTramo().size());
	
				Tramo tramo = trabajador.getTramos().getTramo().get(0);
				assertEquals("08", tramo.getInformacionAfiliacion().getGrupoCotizacion());
				assertEquals("01", tramo.getFechaDesde().getDia());
				assertEquals("02", tramo.getFechaDesde().getMes());
				assertEquals("2023", tramo.getFechaDesde().getAnho());
				assertEquals("19", tramo.getFechaHasta().getDia());
				assertEquals("02", tramo.getFechaHasta().getMes());
				assertEquals("2023", tramo.getFechaHasta().getAnho());
				assertTramoTiempoParcial(tramo);	

				tramo = trabajador.getTramos().getTramo().get(1);
				assertEquals("08", tramo.getInformacionAfiliacion().getGrupoCotizacion());
				assertEquals("20", tramo.getFechaDesde().getDia());
				assertEquals("02", tramo.getFechaDesde().getMes());
				assertEquals("2023", tramo.getFechaDesde().getAnho());
				assertEquals("28", tramo.getFechaHasta().getDia());
				assertEquals("02", tramo.getFechaHasta().getMes());
				assertEquals("2023", tramo.getFechaHasta().getAnho());
				assertTramoTiempoParcial(tramo);	
			}
	
		}
	}

	@Test
	public void testIdcplcccTrabajadoresTramosXIV()
			throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException, JAXBException {
		try (InputStream is = IdcTest.class.getResourceAsStream("idcplcccXIV.pdf")) {
			TrabajadoresTramos trabajadoresTramos = Idcplccc.geTrabajadoresTramos(is,
				new TrabajadoresTramosCallback() {
			    		@Override
			    		public boolean isPartTimeEmployee(String ssNum, String ccc, Date start, Date end) {
			    		    return false;
			    		}
				});
	
			marshal(trabajadoresTramos, System.out);
	
			Liquidacion liquidacion = trabajadoresTramos.getLiquidacion();
	
			assertEquals("0163", liquidacion.getCcc().getRegimen());
			assertEquals("06", liquidacion.getCcc().getProvincia());
			assertEquals("115046920", liquidacion.getCcc().getNumero());
	
			assertEquals("02", liquidacion.getPeriodoDesde().getMes());
			assertEquals("2023", liquidacion.getPeriodoDesde().getAnho());
			assertEquals("02", liquidacion.getPeriodoHasta().getMes());
			assertEquals("2023", liquidacion.getPeriodoHasta().getAnho());
	
			assertEquals(1, liquidacion.getLiquidacionMes().size());
	
			LiquidacionMes liquidacionesMes = liquidacion.getLiquidacionMes().get(0);
			assertEquals("02", liquidacionesMes.getMesLiquidativo().getMes());
			assertEquals("2023", liquidacionesMes.getMesLiquidativo().getAnho());
	
			Trabajadores trabajadores = liquidacionesMes.getTrabajadores();
			assertEquals(1, trabajadores.getTrabajador().size());
	
			for (Trabajador trabajador : trabajadores.getTrabajador()) {
			    	if (trabajador.getNaf().equals("280382731678") ) {
	
				assertEquals(1, trabajador.getTramos().getTramo().size());
	
				Tramo tramo = trabajador.getTramos().getTramo().get(0);
				assertEquals("08", tramo.getInformacionAfiliacion().getGrupoCotizacion());
				assertEquals("01", tramo.getFechaDesde().getDia());
				assertEquals("02", tramo.getFechaDesde().getMes());
				assertEquals("2023", tramo.getFechaDesde().getAnho());
				assertEquals("28", tramo.getFechaHasta().getDia());
				assertEquals("02", tramo.getFechaHasta().getMes());
				assertEquals("2023", tramo.getFechaHasta().getAnho());
				assertTramoActivoNormal(tramo);	
				assertNoDatosSolicitado(tramo, "I", "51");
				assertNoDatosSolicitado(tramo, "H", "01");

			    }
			}
	
		}
	}


	
	@Test
	public void testIdcSyncI() throws IOException, UnknownPDFException {
		try (InputStream is = IdcTest.class.getResourceAsStream("idcI.pdf")) {
			byte data[] = is.readAllBytes();

			AONContext aonContext = new AONContext(getConnection());

			String ccc = "11112501771";
			String naf = "081053913352";
			Date contractDate = getDate(01, Calendar.FEBRUARY, 2020);
			String domainName = java.util.UUID.randomUUID().toString();
			ContractRecord contract = newContract(aonContext, domainName, contractDate, ccc, naf);

			Date idcDate = getDate(14, Calendar.MAY, 2020);
			SistemaRED2AON.syncWithIdc(data, "login", domainName, contract.getDomain(), idcDate, ccc, naf);

			Map<String, ContractData> contractDatas = PAYROLL
					.getContractDataStream(domainName, contract.getDomain(), "login",
							p -> p.getContractProperty().eq(contract.getId()))
					.collect(Collectors.toMap(d -> d.getName(), d -> d));

			ContractData tc2Data = contractDatas.get(ContextVariable.TC2.getName());
			assertNull(tc2Data.getEndDate());
			assertEquals(idcDate, tc2Data.getStartDate());
			assertEquals("\"189\"", tc2Data.getExpression());

			ContractData quoteGroupData = contractDatas.get(ContextVariable.QUOTE_GROUP.getName());
			assertNull(quoteGroupData.getEndDate());
			assertEquals(idcDate, quoteGroupData.getStartDate());
			assertEquals("\"10\"", quoteGroupData.getExpression());

			ContractData itData = contractDatas.get(ContextVariable.IT_PERCENT.getName());
			assertNull(itData.getEndDate());
			assertEquals(idcDate, itData.getStartDate());
			assertEquals(1.70, Double.parseDouble(itData.getExpression()), 0.00);

			ContractData imsData = contractDatas.get(ContextVariable.IMS_PERCENT.getName());
			assertNull(imsData.getEndDate());
			assertEquals(idcDate, imsData.getStartDate());
			assertEquals(1.30, Double.parseDouble(imsData.getExpression()), 0.00);

			ContractData unemployEmployeePercentData = contractDatas
					.get(ContextVariable.UNEMPLOY_EMPLOYEE_PERCENT.getName());
			assertNull(unemployEmployeePercentData.getEndDate());
			assertEquals(idcDate, unemployEmployeePercentData.getStartDate());
			assertEquals(1.55, Double.parseDouble(unemployEmployeePercentData.getExpression()), 0.00);

			ContractData unemployEnterprisePercentData = contractDatas
					.get(ContextVariable.UNEMPLOY_ENTERPRISE_PERCENT.getName());
			assertNull(unemployEnterprisePercentData.getEndDate());
			assertEquals(idcDate, unemployEnterprisePercentData.getStartDate());
			assertEquals(5.50, Double.parseDouble(unemployEnterprisePercentData.getExpression()), 0.00);

			ContractData partialFactorData = contractDatas.get(ContextVariable.PARTIAL_FACTOR.getName());
			assertNull(partialFactorData);

		} finally {

		}

	}

	@Test
	public void testIdcSyncXIIIAndXIV() throws IOException, UnknownPDFException {

		AONContext aonContext = new AONContext(getConnection());

		String ccc = "01105360062";
		String naf = "011005185924";
		Date contractDate = getDate(01, Calendar.AUGUST, 2020);
		String domainName = java.util.UUID.randomUUID().toString();
		ContractRecord contract = newContract(aonContext, domainName, contractDate, ccc, naf);

		try (InputStream is = IdcTest.class.getResourceAsStream("idcXIII.pdf")) {
			byte data[] = is.readAllBytes();

			Date idcStartDate = contractDate;
			SistemaRED2AON.syncWithIdc(data, "login", domainName, contract.getDomain(), idcStartDate, ccc, naf);

			Map<String, ContractData> contractDatas = PAYROLL
					.getContractDataStream(domainName, contract.getDomain(), "login",
							p -> p.getContractProperty().eq(contract.getId()))
					.collect(Collectors.toMap(d -> d.getName(), d -> d));

			Date idcEndDate = getDate(31, Calendar.JULY, 2021);

			ContractData tc2Data = contractDatas.get(ContextVariable.TC2.getName());
			assertEquals(idcEndDate, tc2Data.getEndDate());
			assertEquals(idcStartDate, tc2Data.getStartDate());
			assertEquals("\"100\"", tc2Data.getExpression());

			ContractData quoteGroupData = contractDatas.get(ContextVariable.QUOTE_GROUP.getName());
			assertEquals(idcEndDate, quoteGroupData.getEndDate());
			assertEquals(idcStartDate, quoteGroupData.getStartDate());
			assertEquals("\"01\"", quoteGroupData.getExpression());

			ContractData itData = contractDatas.get(ContextVariable.IT_PERCENT.getName());
			assertEquals(idcEndDate, itData.getEndDate());
			assertEquals(idcStartDate, itData.getStartDate());
			assertEquals(0.80, Double.parseDouble(itData.getExpression()), 0.00);

			ContractData imsData = contractDatas.get(ContextVariable.IMS_PERCENT.getName());
			assertEquals(idcEndDate, imsData.getEndDate());
			assertEquals(idcStartDate, imsData.getStartDate());
			assertEquals(0.70, Double.parseDouble(imsData.getExpression()), 0.00);

			ContractData unemployEmployeePercentData = contractDatas
					.get(ContextVariable.UNEMPLOY_EMPLOYEE_PERCENT.getName());
			assertEquals(idcEndDate, unemployEmployeePercentData.getEndDate());
			assertEquals(idcStartDate, unemployEmployeePercentData.getStartDate());
			assertEquals(1.55, Double.parseDouble(unemployEmployeePercentData.getExpression()), 0.00);

			ContractData unemployEnterprisePercentData = contractDatas
					.get(ContextVariable.UNEMPLOY_ENTERPRISE_PERCENT.getName());
			assertEquals(idcEndDate, unemployEnterprisePercentData.getEndDate());
			assertEquals(idcStartDate, unemployEnterprisePercentData.getStartDate());
			assertEquals(5.50, Double.parseDouble(unemployEnterprisePercentData.getExpression()), 0.00);

		} finally {

		}

		try (InputStream is = IdcTest.class.getResourceAsStream("idcXIV.pdf")) {
			byte data[] = is.readAllBytes();

			Date idcStartDate = getDate(01, Calendar.AUGUST, 2021);
			SistemaRED2AON.syncWithIdc(data, "login", domainName, contract.getDomain(), idcStartDate, ccc, naf);

			Map<String, List<ContractData>> contractDatas = PAYROLL
					.getContractDataStream(domainName, contract.getDomain(), "login",
							p -> p.getContractProperty().eq(contract.getId()))
					.collect(Collectors.groupingBy(d -> d.getName()));

			List<ContractData> tc2Datas = contractDatas.get(ContextVariable.TC2.getName());
			Collections.sort(tc2Datas, (d1, d2) -> d2.getStartDate().compareTo(d1.getStartDate()));
			ContractData tc2Data = tc2Datas.get(0);
			assertNull(tc2Data.getEndDate());
			assertEquals(idcStartDate, tc2Data.getStartDate());
			assertEquals("\"100\"", tc2Data.getExpression());

			List<ContractData> quoteGroupDatas = contractDatas.get(ContextVariable.QUOTE_GROUP.getName());
			Collections.sort(quoteGroupDatas, (d1, d2) -> d2.getStartDate().compareTo(d1.getStartDate()));
			ContractData quoteGroupData = quoteGroupDatas.get(0);
			assertNull(quoteGroupData.getEndDate());
			assertEquals(idcStartDate, quoteGroupData.getStartDate());
			assertEquals("\"01\"", quoteGroupData.getExpression());

			List<ContractData> itDatas = contractDatas.get(ContextVariable.IT_PERCENT.getName());
			Collections.sort(itDatas, (d1, d2) -> d2.getStartDate().compareTo(d1.getStartDate()));
			ContractData itData = itDatas.get(0);
			assertNull(itData.getEndDate());
			assertEquals(idcStartDate, itData.getStartDate());
			assertEquals(0.80, Double.parseDouble(itData.getExpression()), 0.00);

			List<ContractData> imsDatas = contractDatas.get(ContextVariable.IMS_PERCENT.getName());
			Collections.sort(imsDatas, (d1, d2) -> d2.getStartDate().compareTo(d1.getStartDate()));
			ContractData imsData = imsDatas.get(0);
			assertNull(imsData.getEndDate());
			assertEquals(idcStartDate, imsData.getStartDate());
			assertEquals(0.70, Double.parseDouble(imsData.getExpression()), 0.00);

			List<ContractData> unemployEmployeePercentDatas = contractDatas
					.get(ContextVariable.UNEMPLOY_EMPLOYEE_PERCENT.getName());
			Collections.sort(unemployEmployeePercentDatas, (d1, d2) -> d2.getStartDate().compareTo(d1.getStartDate()));
			ContractData unemployEmployeePercentData = unemployEmployeePercentDatas.get(0);
			assertNull(unemployEmployeePercentData.getEndDate());
			assertEquals(idcStartDate, unemployEmployeePercentData.getStartDate());
			assertEquals(1.55, Double.parseDouble(unemployEmployeePercentData.getExpression()), 0.00);

			List<ContractData> unemployEnterprisePercentDatas = contractDatas
					.get(ContextVariable.UNEMPLOY_ENTERPRISE_PERCENT.getName());
			Collections.sort(unemployEnterprisePercentDatas,
					(d1, d2) -> d2.getStartDate().compareTo(d1.getStartDate()));
			ContractData unemployEnterprisePercentData = unemployEnterprisePercentDatas.get(0);
			assertNull(unemployEnterprisePercentData.getEndDate());
			assertEquals(idcStartDate, unemployEnterprisePercentData.getStartDate());
			assertEquals(5.50, Double.parseDouble(unemployEnterprisePercentData.getExpression()), 0.00);

		} finally {

		}
	}

	@Test
	public void testIdcSyncXIIIAndXIV2() throws IOException, UnknownPDFException {

		AONContext aonContext = new AONContext(getConnection());

		String ccc = "01105360062";
		String naf = "011005185924";
		Date contractDate = getDate(01, Calendar.AUGUST, 2020);
		String domainName = java.util.UUID.randomUUID().toString();
		ContractRecord contract = newContract(aonContext, domainName, contractDate, ccc, naf);

		addData(aonContext, contract, contract.getStartDate(), contract.getEndDate(), ContextVariable.TC2, "\"189\"");
		addData(aonContext, contract, contract.getStartDate(), contract.getEndDate(), ContextVariable.QUOTE_GROUP,
				"\"05\"");
		addData(aonContext, contract, contract.getStartDate(), contract.getEndDate(), ContextVariable.IT_PERCENT, "5.00");
		addData(aonContext, contract, contract.getStartDate(), contract.getEndDate(), ContextVariable.IMS_PERCENT,
				"15.00");
		addData(aonContext, contract, contract.getStartDate(), contract.getEndDate(),
				ContextVariable.UNEMPLOY_EMPLOYEE_PERCENT, "25.00");
		addData(aonContext, contract, contract.getStartDate(), contract.getEndDate(),
				ContextVariable.UNEMPLOY_ENTERPRISE_PERCENT, "25.00");

		for (int i = 0; i < 2; i++) {
			try (InputStream is = IdcTest.class.getResourceAsStream("idcXIII.pdf")) {

				byte data[] = is.readAllBytes();

				Date idcStartDate = contractDate;
				SistemaRED2AON.syncWithIdc(data, "login", domainName, contract.getDomain(), idcStartDate, ccc, naf);

				Map<String, ContractData> contractDatas = PAYROLL
						.getContractDataStream(domainName, contract.getDomain(), "login",
								p -> p.getContractProperty().eq(contract.getId()))
						.filter(d -> d.getStartDate().equals(idcStartDate))
						.collect(Collectors.toMap(d -> d.getName(), d -> d));

				Date idcEndDate = getDate(31, Calendar.JULY, 2021);

				ContractData tc2Data = contractDatas.get(ContextVariable.TC2.getName());
				assertEquals(idcEndDate, tc2Data.getEndDate());
				assertEquals(idcStartDate, tc2Data.getStartDate());
				assertEquals("\"100\"", tc2Data.getExpression());

				ContractData quoteGroupData = contractDatas.get(ContextVariable.QUOTE_GROUP.getName());
				assertEquals(idcEndDate, quoteGroupData.getEndDate());
				assertEquals(idcStartDate, quoteGroupData.getStartDate());
				assertEquals("\"01\"", quoteGroupData.getExpression());

				ContractData itData = contractDatas.get(ContextVariable.IT_PERCENT.getName());
				assertEquals(idcEndDate, itData.getEndDate());
				assertEquals(idcStartDate, itData.getStartDate());
				assertEquals(0.80, Double.parseDouble(itData.getExpression()), 0.00);

				ContractData imsData = contractDatas.get(ContextVariable.IMS_PERCENT.getName());
				assertEquals(idcEndDate, imsData.getEndDate());
				assertEquals(idcStartDate, imsData.getStartDate());
				assertEquals(0.70, Double.parseDouble(imsData.getExpression()), 0.00);

				ContractData unemployEmployeePercentData = contractDatas
						.get(ContextVariable.UNEMPLOY_EMPLOYEE_PERCENT.getName());
				assertEquals(idcEndDate, unemployEmployeePercentData.getEndDate());
				assertEquals(idcStartDate, unemployEmployeePercentData.getStartDate());
				assertEquals(1.55, Double.parseDouble(unemployEmployeePercentData.getExpression()), 0.00);

				ContractData unemployEnterprisePercentData = contractDatas
						.get(ContextVariable.UNEMPLOY_ENTERPRISE_PERCENT.getName());
				assertEquals(idcEndDate, unemployEnterprisePercentData.getEndDate());
				assertEquals(idcStartDate, unemployEnterprisePercentData.getStartDate());
				assertEquals(5.50, Double.parseDouble(unemployEnterprisePercentData.getExpression()), 0.00);

			} finally {

			}

			for (int j = 0; j < 2; j++) {
				try (InputStream is = IdcTest.class.getResourceAsStream("idcXIV.pdf")) {
					byte data[] = is.readAllBytes();

					Date idcStartDate = getDate(01, Calendar.AUGUST, 2021);
					SistemaRED2AON.syncWithIdc(data, "login", domainName, contract.getDomain(), idcStartDate, ccc, naf);

					Map<String, List<ContractData>> contractDatas = PAYROLL
							.getContractDataStream(domainName, contract.getDomain(), "login",
									p -> p.getContractProperty().eq(contract.getId()))
							.collect(Collectors.groupingBy(d -> d.getName()));

					List<ContractData> tc2Datas = contractDatas.get(ContextVariable.TC2.getName());
					Collections.sort(tc2Datas, (d1, d2) -> d2.getStartDate().compareTo(d1.getStartDate()));
					ContractData tc2Data = tc2Datas.get(0);
					assertNull(tc2Data.getEndDate());
					assertEquals(idcStartDate, tc2Data.getStartDate());
					assertEquals("\"100\"", tc2Data.getExpression());

					List<ContractData> quoteGroupDatas = contractDatas.get(ContextVariable.QUOTE_GROUP.getName());
					Collections.sort(quoteGroupDatas, (d1, d2) -> d2.getStartDate().compareTo(d1.getStartDate()));
					ContractData quoteGroupData = quoteGroupDatas.get(0);
					assertNull(quoteGroupData.getEndDate());
					assertEquals(idcStartDate, quoteGroupData.getStartDate());
					assertEquals("\"01\"", quoteGroupData.getExpression());

					List<ContractData> itDatas = contractDatas.get(ContextVariable.IT_PERCENT.getName());
					Collections.sort(itDatas, (d1, d2) -> d2.getStartDate().compareTo(d1.getStartDate()));
					ContractData itData = itDatas.get(0);
					assertNull(itData.getEndDate());
					assertEquals(idcStartDate, itData.getStartDate());
					assertEquals(0.80, Double.parseDouble(itData.getExpression()), 0.00);

					List<ContractData> imsDatas = contractDatas.get(ContextVariable.IMS_PERCENT.getName());
					Collections.sort(imsDatas, (d1, d2) -> d2.getStartDate().compareTo(d1.getStartDate()));
					ContractData imsData = imsDatas.get(0);
					assertNull(imsData.getEndDate());
					assertEquals(idcStartDate, imsData.getStartDate());
					assertEquals(0.70, Double.parseDouble(imsData.getExpression()), 0.00);

					List<ContractData> unemployEmployeePercentDatas = contractDatas
							.get(ContextVariable.UNEMPLOY_EMPLOYEE_PERCENT.getName());
					Collections.sort(unemployEmployeePercentDatas,
							(d1, d2) -> d2.getStartDate().compareTo(d1.getStartDate()));
					ContractData unemployEmployeePercentData = unemployEmployeePercentDatas.get(0);
					assertNull(unemployEmployeePercentData.getEndDate());
					assertEquals(idcStartDate, unemployEmployeePercentData.getStartDate());
					assertEquals(1.55, Double.parseDouble(unemployEmployeePercentData.getExpression()), 0.00);

					List<ContractData> unemployEnterprisePercentDatas = contractDatas
							.get(ContextVariable.UNEMPLOY_ENTERPRISE_PERCENT.getName());
					Collections.sort(unemployEnterprisePercentDatas,
							(d1, d2) -> d2.getStartDate().compareTo(d1.getStartDate()));
					ContractData unemployEnterprisePercentData = unemployEnterprisePercentDatas.get(0);
					assertNull(unemployEnterprisePercentData.getEndDate());
					assertEquals(idcStartDate, unemployEnterprisePercentData.getStartDate());
					assertEquals(5.50, Double.parseDouble(unemployEnterprisePercentData.getExpression()), 0.00);

				} finally {

				}
			}
		}
	}

	@Test
	public void testIdcSyncXV() throws IOException, UnknownPDFException {
		try (InputStream is = IdcTest.class.getResourceAsStream("idcXV.pdf")) {
			byte data[] = is.readAllBytes();

			AONContext aonContext = new AONContext(getConnection());

			String ccc = "29136287700";
			String naf = "111060977833";
			Date contractDate = getDate(18, Calendar.MARCH, 2019);
			String domainName = java.util.UUID.randomUUID().toString();
			ContractRecord contract = newContract(aonContext, domainName, contractDate, ccc, naf);

			Date idcDate = getDate(01, Calendar.APRIL, 2020);
			SistemaRED2AON.syncWithIdc(data, "login", domainName, contract.getDomain(), idcDate, ccc, naf);

			Map<String, ContractData> contractDatas = PAYROLL
					.getContractDataStream(domainName, contract.getDomain(), "login",
							p -> p.getContractProperty().eq(contract.getId()))
					.collect(Collectors.toMap(d -> d.getName(), d -> d));

			ContractData tc2Data = contractDatas.get(ContextVariable.TC2.getName());
			assertNull(tc2Data.getEndDate());
			assertEquals(idcDate, tc2Data.getStartDate());
			assertEquals("\"421\"", tc2Data.getExpression());

			ContractData quoteGroupData = contractDatas.get(ContextVariable.QUOTE_GROUP.getName());
			assertNull(quoteGroupData.getEndDate());
			assertEquals(idcDate, quoteGroupData.getStartDate());
			assertEquals("\"10\"", quoteGroupData.getExpression());

			ContractData unemployEmployeePercentData = contractDatas
					.get(ContextVariable.UNEMPLOY_EMPLOYEE_PERCENT.getName());
			assertNull(unemployEmployeePercentData.getEndDate());
			assertEquals(idcDate, unemployEmployeePercentData.getStartDate());
			assertEquals(1.55, Double.parseDouble(unemployEmployeePercentData.getExpression()), 0.00);

			ContractData unemployEnterprisePercentData = contractDatas
					.get(ContextVariable.UNEMPLOY_ENTERPRISE_PERCENT.getName());
			assertNull(unemployEnterprisePercentData.getEndDate());
			assertEquals(idcDate, unemployEnterprisePercentData.getStartDate());
			assertEquals(5.50, Double.parseDouble(unemployEnterprisePercentData.getExpression()), 0.00);

			ContractData partialFactorData = contractDatas.get(ContextVariable.PARTIAL_FACTOR.getName());
			assertNull(partialFactorData);

			ContractData itData = contractDatas.get(ContextVariable.IT_PERCENT.getName());
			assertNull(itData);

			ContractData imsData = contractDatas.get(ContextVariable.IMS_PERCENT.getName());
			assertNull(imsData);

		} finally {

		}

	}

	@Test
	public void testIdcSyncVI() throws IOException, UnknownPDFException {
		try (InputStream is = IdcTest.class.getResourceAsStream("idcVI.pdf")) {
			byte data[] = is.readAllBytes();

			AONContext aonContext = new AONContext(getConnection());

			String ccc = "01105577910";
			String naf = "141026133260";
			Date contractDate = getDate(01, Calendar.JANUARY, 2020);
			String domainName = java.util.UUID.randomUUID().toString();
			ContractRecord contract = newContract(aonContext, domainName, contractDate, ccc, naf);

			Date idcDate = getDate(01, Calendar.OCTOBER, 2020);
			SistemaRED2AON.syncWithIdc(data, "login", domainName, contract.getDomain(), idcDate, ccc, naf);

			Map<String, ContractData> contractDatas = PAYROLL
					.getContractDataStream(domainName, contract.getDomain(), "login",
							p -> p.getContractProperty().eq(contract.getId()))
					.collect(Collectors.toMap(d -> d.getName(), d -> d));

			ContractData tc2Data = contractDatas.get(ContextVariable.TC2.getName());
			assertNull(tc2Data);

			ContractData quoteGroupData = contractDatas.get(ContextVariable.QUOTE_GROUP.getName());
			assertNull(quoteGroupData.getEndDate());
			assertEquals(idcDate, quoteGroupData.getStartDate());
			assertEquals("\"07\"", quoteGroupData.getExpression());

			ContractData unemployEmployeePercentData = contractDatas
					.get(ContextVariable.UNEMPLOY_EMPLOYEE_PERCENT.getName());
			assertNull(unemployEmployeePercentData);

			ContractData unemployEnterprisePercentData = contractDatas
					.get(ContextVariable.UNEMPLOY_ENTERPRISE_PERCENT.getName());
			assertNull(unemployEnterprisePercentData);

			ContractData partialFactorData = contractDatas.get(ContextVariable.PARTIAL_FACTOR.getName());
			assertNull(partialFactorData);

			ContractData itData = contractDatas.get(ContextVariable.IT_PERCENT.getName());
			assertNull(itData);

			ContractData imsData = contractDatas.get(ContextVariable.IMS_PERCENT.getName());
			assertNull(imsData);

			Deduction[] deductions = PAYROLL.getDeductions(domainName, contract.getDomain(), "login", contract.getId());
			assertEquals(4, deductions.length);
			for (Deduction deduction : deductions) {
				if (deduction.getType() == UNEMPLOYMENT) {
					assertTrue(AonStringUtils.containsIgnoreCase(deduction.getExpression(), "REMOVE"));
				} else if (deduction.getType() == JOB_TRAINING) {
					assertTrue(AonStringUtils.containsIgnoreCase(deduction.getExpression(), "REMOVE"));
				} else if (deduction.getType() == BONUS) {

				} else {
					fail("Unknow " + deduction.getType());
				}
			}

		} finally {

		}

	}

	@Test
	public void testIdcAsimilados() throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException,
			ExpressionException, SalaryException, SQLException {

		try (InputStream is = IdcTest.class.getResourceAsStream("idcAsimilados.pdf")) {
			Collection<PEC> ssPecs = Idc.getSSPECs(is);
			// assertTrue(ssPecs.size() == 1);

			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);

			calendar.set(Calendar.YEAR, 2020);
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			calendar.set(Calendar.MONTH, Calendar.MAY);

			Date may012020 = calendar.getTime();

			ssPecs.stream().forEach(pec -> assertEquals(may012020, pec.getStartDate()));
			ssPecs.stream().forEach(pec -> assertNull(pec.getEndDate()));

			ssPecs.forEach(pec -> System.out.println("[" + pec.getName() + "] " + pec.getDescription() + " = "
					+ pec.getFormula() + ", " + pec.getStartDate()));

			calendar.set(Calendar.YEAR, 2022);
			calendar.set(Calendar.MONTH, Calendar.NOVEMBER);
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			Date november = calendar.getTime();

			Salary salary = calculate(ssPecs, Collections.emptyList(), november);

			salary.getSalaryCosts().forEach(
					c -> System.out.println("COST :" + c.getName() + " : " + c.getAmount() + ", " + c.getType()));
			salary.getSalaryDeductions().forEach(
					c -> System.out.println("DEDUCTION :" + c.getName() + " : " + c.getAmount() + ", " + c.getType()));

			salary.getSalaryCosts().forEach(c -> {
				if (AonStringUtils.equalsIgnoreCase("DESMPL_E", c.getName()))
					fail("DESEMPLEO must be excluded");
				if (AonStringUtils.equalsIgnoreCase("FOGASA_E", c.getName()))
					fail("FOGASA must be excluded");
			});

			salary.getSalaryDeductions().forEach(c -> {
				if (AonStringUtils.equalsIgnoreCase("DESMPL", c.getName()))
					fail("DESEMPLEO must be excluded");
			});

			// assertEquals(0.00, salary.getTotalEnterprise(), DELTA);
			// assertEquals(0.00, salary.getSocialSecurityContributions(), DELTA);

		}
	}

	@Test
	public void testSyncIdcXXIVBonus() throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException,
			ExpressionException, SalaryException, SQLException, ParseException {

		String[] idcs = { "idcXXIV-22-04-2019.pdf", "idcXXIV-06-08-2019.pdf", "idcXXIV-14-03-2020.pdf",
				"idcXXIV-13-07-2020.pdf", "idcXXIV-01-11-2021.pdf", "idcXXIV-01-08-2022.pdf",
				"idcXXIV-01-10-2022.pdf" };

		AONContext aonContext = new AONContext(getConnection());

		String ccc = "06114494121";
		String naf = "061010662603";
		Date contractStartDate = getDate(22, Calendar.APRIL, 2019);
		String domainName = java.util.UUID.randomUUID().toString();
		ContractRecord contract = newContract(aonContext, domainName, contractStartDate, ccc, naf);
		Integer domainId = contract.getDomain();

		for (String idc : idcs) {
			try (InputStream is = IdcTest.class.getResourceAsStream(idc)) {
				byte data[] = is.readAllBytes();
				Date date = new SimpleDateFormat("dd-MM-yyyy").parse(idc.substring(8, 19));
				SistemaRED2AON.syncWithIdc(data, "userLogin", domainName, domainId, date, ccc, naf);
				System.out.println(idc + " : " + date);
			}
		}

		Bonus[] bonuses = PAYROLL.getBonuses(domainName, domainId, "userLogin", contract.getId());

		Arrays.sort(bonuses, (b1, b2) -> b1.getStartDate().compareTo(b2.getStartDate()));

		Arrays.stream(bonuses).forEach(
				b -> System.out.println(b.getDescription() + " : " + b.getStartDate() + "..." + b.getEndDate()));

		assertEquals("22-04-2019", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[0].getStartDate()));
		assertEquals("05-08-2019", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[0].getEndDate()));

		assertEquals("06-08-2019", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[1].getStartDate()));
		assertEquals("13-03-2020", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[1].getEndDate()));

		assertEquals("14-03-2020", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[2].getStartDate()));
		assertEquals("12-07-2020", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[2].getEndDate()));
		assertEquals("14-03-2020", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[3].getStartDate()));
		assertEquals("30-04-2020", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[3].getEndDate()));
		assertEquals("01-05-2020", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[4].getStartDate()));
		assertEquals("31-05-2020", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[4].getEndDate()));
		assertEquals("01-06-2020", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[5].getStartDate()));
		assertEquals("30-06-2020", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[5].getEndDate()));
		assertEquals("01-07-2020", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[6].getStartDate()));
		assertEquals("12-07-2020", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[6].getEndDate()));

		assertEquals("13-07-2020", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[7].getStartDate()));
		assertEquals("31-10-2021", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[7].getEndDate()));
		assertEquals("13-07-2020", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[8].getStartDate()));
		assertEquals("31-07-2020", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[8].getEndDate()));
		assertEquals("01-08-2020", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[9].getStartDate()));
		assertEquals("31-08-2020", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[9].getEndDate()));
		assertEquals("01-09-2020", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[10].getStartDate()));
		assertEquals("30-09-2020", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[10].getEndDate()));

		assertEquals("01-11-2021", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[11].getStartDate()));
		assertEquals("31-07-2022", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[11].getEndDate()));

		assertEquals("01-08-2022", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[12].getStartDate()));
		assertEquals("30-09-2022", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[12].getEndDate()));

		assertEquals("01-10-2022", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[13].getStartDate()));
		assertNull(bonuses[13].getEndDate());
	}

	@Test
	public void testSyncIdcXXIVBonusTwice() throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException,
			ExpressionException, SalaryException, SQLException, ParseException {

		String[] idcs = { "idcXXIV-22-04-2019.pdf", "idcXXIV-06-08-2019.pdf", "idcXXIV-14-03-2020.pdf",
				"idcXXIV-13-07-2020.pdf", "idcXXIV-01-11-2021.pdf", "idcXXIV-01-08-2022.pdf",
				"idcXXIV-01-10-2022.pdf" };

		AONContext aonContext = new AONContext(getConnection());

		String ccc = "06114494121";
		String naf = "061010662603";
		Date contractStartDate = getDate(22, Calendar.APRIL, 2019);
		String domainName = java.util.UUID.randomUUID().toString();
		ContractRecord contract = newContract(aonContext, domainName, contractStartDate, ccc, naf);
		Integer domainId = contract.getDomain();

		for (String idc : idcs) {
			try (InputStream is = IdcTest.class.getResourceAsStream(idc)) {
				byte data[] = is.readAllBytes();
				Date date = new SimpleDateFormat("dd-MM-yyyy").parse(idc.substring(8, 19));
				SistemaRED2AON.syncWithIdc(data, "userLogin", domainName, domainId, date, ccc, naf);
				System.out.println(idc + " : " + date);
			}
		}
		for (String idc : idcs) {
			try (InputStream is = IdcTest.class.getResourceAsStream(idc)) {
				byte data[] = is.readAllBytes();
				Date date = new SimpleDateFormat("dd-MM-yyyy").parse(idc.substring(8, 19));
				SistemaRED2AON.syncWithIdc(data, "userLogin", domainName, domainId, date, ccc, naf);
				System.out.println(idc + " : " + date);
			}
		}

		Bonus[] bonuses = PAYROLL.getBonuses(domainName, domainId, "userLogin", contract.getId());

		Arrays.sort(bonuses, (b1, b2) -> b1.getStartDate().compareTo(b2.getStartDate()));

		Arrays.stream(bonuses).forEach(
				b -> System.out.println(b.getDescription() + " : " + b.getStartDate() + "..." + b.getEndDate()));
		int i = 0;
		assertEquals("22-04-2019", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[i].getStartDate()));
		assertEquals("05-08-2019", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[i++].getEndDate()));

		assertEquals("06-08-2019", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[i].getStartDate()));
		assertEquals("13-03-2020", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[i++].getEndDate()));

		assertEquals("14-03-2020", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[i].getStartDate()));
		assertEquals("12-07-2020", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[i++].getEndDate()));
		assertEquals("14-03-2020", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[i].getStartDate()));
		assertEquals("30-04-2020", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[i++].getEndDate()));
		assertEquals("01-05-2020", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[i].getStartDate()));
		assertEquals("31-05-2020", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[i++].getEndDate()));
		assertEquals("01-06-2020", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[i].getStartDate()));
		assertEquals("30-06-2020", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[i++].getEndDate()));
		assertEquals("01-07-2020", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[i].getStartDate()));
		assertEquals("12-07-2020", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[i++].getEndDate()));

		assertEquals("13-07-2020", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[i].getStartDate()));
		assertEquals("31-10-2021", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[i++].getEndDate()));
		assertEquals("13-07-2020", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[i].getStartDate()));
		assertEquals("31-07-2020", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[i++].getEndDate()));
		assertEquals("01-08-2020", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[i].getStartDate()));
		assertEquals("31-08-2020", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[i++].getEndDate()));
		assertEquals("01-09-2020", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[i].getStartDate()));
		assertEquals("30-09-2020", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[i++].getEndDate()));

		assertEquals("01-11-2021", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[i].getStartDate()));
		assertEquals("31-07-2022", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[i++].getEndDate()));

		assertEquals("01-08-2022", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[i].getStartDate()));
		assertEquals("30-09-2022", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[i++].getEndDate()));

		assertEquals("01-10-2022", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[i].getStartDate()));
		assertNull(bonuses[i++].getEndDate());
	}

	@Test
	public void testSyncIdcXXIVBonusReverse() throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException,
			ExpressionException, SalaryException, SQLException, ParseException {

		String[] idcs = { "idcXXIV-22-04-2019.pdf", "idcXXIV-06-08-2019.pdf", "idcXXIV-14-03-2020.pdf",
				"idcXXIV-13-07-2020.pdf", "idcXXIV-01-11-2021.pdf", "idcXXIV-01-08-2022.pdf",
				"idcXXIV-01-10-2022.pdf" };

		AONContext aonContext = new AONContext(getConnection());

		String ccc = "06114494121";
		String naf = "061010662603";
		Date contractStartDate = getDate(22, Calendar.APRIL, 2019);
		String domainName = java.util.UUID.randomUUID().toString();
		ContractRecord contract = newContract(aonContext, domainName, contractStartDate, ccc, naf);
		Integer domainId = contract.getDomain();

		for (int i = idcs.length - 1; i >= 0; i--) {
			String idc = idcs[i];
			try (InputStream is = IdcTest.class.getResourceAsStream(idc)) {
				byte data[] = is.readAllBytes();
				Date date = new SimpleDateFormat("dd-MM-yyyy").parse(idc.substring(8, 19));
				SistemaRED2AON.syncWithIdc(data, "userLogin", domainName, domainId, date, ccc, naf);
				System.out.println(idc + " : " + date);
			}
		}

		Bonus[] bonuses = PAYROLL.getBonuses(domainName, domainId, "userLogin", contract.getId());

		Arrays.sort(bonuses, (b1, b2) -> b1.getStartDate().compareTo(b2.getStartDate()));

		Arrays.stream(bonuses).forEach(
				b -> System.out.println(b.getDescription() + " : " + b.getStartDate() + "..." + b.getEndDate()));

		assertEquals("22-04-2019", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[0].getStartDate()));
		assertEquals("05-08-2019", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[0].getEndDate()));

		assertEquals("06-08-2019", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[1].getStartDate()));
		assertEquals("13-03-2020", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[1].getEndDate()));

		assertEquals("14-03-2020", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[2].getStartDate()));
		assertEquals("12-07-2020", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[2].getEndDate()));
		assertEquals("14-03-2020", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[3].getStartDate()));
		assertEquals("30-04-2020", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[3].getEndDate()));
		assertEquals("01-05-2020", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[4].getStartDate()));
		assertEquals("31-05-2020", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[4].getEndDate()));
		assertEquals("01-06-2020", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[5].getStartDate()));
		assertEquals("30-06-2020", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[5].getEndDate()));
		assertEquals("01-07-2020", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[6].getStartDate()));
		assertEquals("12-07-2020", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[6].getEndDate()));

		assertEquals("13-07-2020", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[7].getStartDate()));
		assertEquals("31-10-2021", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[7].getEndDate()));
		assertEquals("13-07-2020", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[8].getStartDate()));
		assertEquals("31-07-2020", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[8].getEndDate()));
		assertEquals("01-08-2020", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[9].getStartDate()));
		assertEquals("31-08-2020", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[9].getEndDate()));
		assertEquals("01-09-2020", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[10].getStartDate()));
		assertEquals("30-09-2020", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[10].getEndDate()));

		assertEquals("01-11-2021", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[11].getStartDate()));
		assertEquals("31-07-2022", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[11].getEndDate()));

		assertEquals("01-08-2022", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[12].getStartDate()));
		assertEquals("30-09-2022", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[12].getEndDate()));

		assertEquals("01-10-2022", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[13].getStartDate()));
		assertNull(bonuses[13].getEndDate());
	}

	@Test
	public void testSyncIdcXXIVBonusReverseTwice() throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException,
			IOException, ExpressionException, SalaryException, SQLException, ParseException {

		String[] idcs = { "idcXXIV-22-04-2019.pdf", "idcXXIV-06-08-2019.pdf", "idcXXIV-14-03-2020.pdf",
				"idcXXIV-13-07-2020.pdf", "idcXXIV-01-11-2021.pdf", "idcXXIV-01-08-2022.pdf",
				"idcXXIV-01-10-2022.pdf" };

		AONContext aonContext = new AONContext(getConnection());

		String ccc = "06114494121";
		String naf = "061010662603";
		Date contractStartDate = getDate(22, Calendar.APRIL, 2019);
		String domainName = java.util.UUID.randomUUID().toString();
		ContractRecord contract = newContract(aonContext, domainName, contractStartDate, ccc, naf);
		Integer domainId = contract.getDomain();

		for (int i = idcs.length - 1; i >= 0; i--) {
			String idc = idcs[i];
			try (InputStream is = IdcTest.class.getResourceAsStream(idc)) {
				byte data[] = is.readAllBytes();
				Date date = new SimpleDateFormat("dd-MM-yyyy").parse(idc.substring(8, 19));
				SistemaRED2AON.syncWithIdc(data, "userLogin", domainName, domainId, date, ccc, naf);
				System.out.println(idc + " : " + date);
			}
		}

		for (int i = idcs.length - 1; i >= 0; i--) {
			String idc = idcs[i];
			try (InputStream is = IdcTest.class.getResourceAsStream(idc)) {
				byte data[] = is.readAllBytes();
				Date date = new SimpleDateFormat("dd-MM-yyyy").parse(idc.substring(8, 19));
				SistemaRED2AON.syncWithIdc(data, "userLogin", domainName, domainId, date, ccc, naf);
				System.out.println(idc + " : " + date);
			}
		}

		Bonus[] bonuses = PAYROLL.getBonuses(domainName, domainId, "userLogin", contract.getId());

		Arrays.sort(bonuses, (b1, b2) -> b1.getStartDate().compareTo(b2.getStartDate()));

		Arrays.stream(bonuses).forEach(
				b -> System.out.println(b.getDescription() + " : " + b.getStartDate() + "..." + b.getEndDate()));

		assertEquals("22-04-2019", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[0].getStartDate()));
		assertEquals("05-08-2019", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[0].getEndDate()));

		assertEquals("06-08-2019", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[1].getStartDate()));
		assertEquals("13-03-2020", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[1].getEndDate()));

		assertEquals("14-03-2020", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[2].getStartDate()));
		assertEquals("12-07-2020", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[2].getEndDate()));
		assertEquals("14-03-2020", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[3].getStartDate()));
		assertEquals("30-04-2020", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[3].getEndDate()));
		assertEquals("01-05-2020", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[4].getStartDate()));
		assertEquals("31-05-2020", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[4].getEndDate()));
		assertEquals("01-06-2020", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[5].getStartDate()));
		assertEquals("30-06-2020", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[5].getEndDate()));
		assertEquals("01-07-2020", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[6].getStartDate()));
		assertEquals("12-07-2020", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[6].getEndDate()));

		assertEquals("13-07-2020", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[7].getStartDate()));
		assertEquals("31-10-2021", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[7].getEndDate()));
		assertEquals("13-07-2020", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[8].getStartDate()));
		assertEquals("31-07-2020", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[8].getEndDate()));
		assertEquals("01-08-2020", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[9].getStartDate()));
		assertEquals("31-08-2020", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[9].getEndDate()));
		assertEquals("01-09-2020", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[10].getStartDate()));
		assertEquals("30-09-2020", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[10].getEndDate()));

		assertEquals("01-11-2021", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[11].getStartDate()));
		assertEquals("31-07-2022", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[11].getEndDate()));

		assertEquals("01-08-2022", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[12].getStartDate()));
		assertEquals("30-09-2022", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[12].getEndDate()));

		assertEquals("01-10-2022", new SimpleDateFormat("dd-MM-yyyy").format(bonuses[13].getStartDate()));
		assertNull(bonuses[13].getEndDate());
	}

	public static final DomainRecord newDomain(AONContext aonContext, String name) {
		return aonContext.getDslContext().insertInto(DOMAIN).set(DOMAIN.NAME, name).set(DOMAIN.OWNER, "")
				.set(DOMAIN.DESCRIPTION, "").returning().fetchOne();
	}

	public static ContractRecord newContract(AONContext aonContext, String domainName, Date startDate, String ccc,
			String nss) {
		return newContract(aonContext, domainName, startDate, ccc, nss, null);
	}

	public static ContractRecord newContract(AONContext aonContext, String domainName, Date startDate, String ccc,
			String nss, String doc) {
		DomainRecord domain = newDomain(aonContext, domainName);
		ScopeRecord scope = newScope(aonContext, domain.getId());
		EnterpriseActivityRecord enterpriseActivity = newEnterpriseActivity(aonContext, domain.getId(), scope.getId(),
				SSRegimeType.GENERAL);
		EnterpriseCccRecord enterpriseCcc = newEnterpriseCcc(aonContext, domain.getId(), scope.getId(),
				enterpriseActivity.getId(), CCCType.PRINCIPAL, ccc);
		WorkplaceRecord workplace = newWorkplace(aonContext, domain.getId(), scope.getId(),
				enterpriseActivity.getEnterprise());
		RegistryRecord person = newPerson(aonContext, domain.getId(), doc, nss);

		ContractRecord contract = newContract(aonContext, SSRegimeType.GENERAL, CCCType.PRINCIPAL, toSQL(startDate),
				null, Collections.emptyMap(), new String[] {}, new String[] {}, null, domain.getId(), // domainId,
				person.getId(), // personId,
				workplace.getId(), // workplaceId,
				enterpriseCcc.getId(), // enterpriseCccId,
				enterpriseActivity.getId() // enterpriseActivityId
		);

		return contract;
	}

	private static class IdcPEC {
		private String pec;
		private Date startDate;
		private Date endDate;
		private double portTipo;
		private String quota;

		public IdcPEC(String pec, Date startDate, Date endDate, double portTipo, String quota) {
			super();
			this.pec = pec;
			this.startDate = startDate;
			this.endDate = endDate;
			this.portTipo = portTipo;
			this.quota = quota;
		}

		@Override
		public boolean equals(Object obj) {
			IdcPEC other = (IdcPEC) obj;
			return pec.equals(other.pec) && startDate.equals(other.startDate) && endDate.equals(other.endDate)
					&& portTipo == other.portTipo && quota.equals(other.quota);
		}

	}

	private static <T> void marshall(T t, OutputStream os) {
		try {
			XMLStreamWriter xsw = new IndentXMLStreamWriter(XMLOutputFactory.newInstance().createXMLStreamWriter(os),
					"  ");
			Utils.marshal(t, xsw);
			os.close();
		} catch (JAXBException | IOException | XMLStreamException e) {
		}
	}

	private static Date getDate(int day, int month, int year) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.DATE, day);
		calendar.set(Calendar.MONTH, month);
		return calendar.getTime();
	}

	private static int assertTramoITPagoDirecto(Tramo tramo) {
		List<DatoSolicitado> datoSolicitados = tramo.getDatosTramo().getDatoSolicitado();

		assertDatosSolicitado(datoSolicitados, "C", "509", "B");
		try {
			assertDatosSolicitado(datoSolicitados, "C", "603", "B");
		} catch (AssertException e) {
			assertDatosSolicitado(datoSolicitados, "C", "613", "B");
		}
		return 2;
	}

	private static int assertTramoIT15PrimerosDias(Tramo tramo) {
		List<DatoSolicitado> datoSolicitados = tramo.getDatosTramo().getDatoSolicitado();
		assertDatosSolicitado(datoSolicitados, "C", "500", "B");
		try {
			assertDatosSolicitado(datoSolicitados, "C", "603", "B");
		} catch (AssertException e) {
			assertDatosSolicitado(datoSolicitados, "C", "613", "B");
		}
		return 2;
	}

	private static int assertTramoIT15PrimerosDiasDiario(Tramo tramo) {
		int count = assertTramoIT15PrimerosDias(tramo);
		List<DatoSolicitado> datoSolicitados = tramo.getDatosTramo().getDatoSolicitado();
		assertDatosSolicitado(datoSolicitados, "I", "51", "P");
		return count + 1;
	}

	private static int assertTramoITPagoDelegado(Tramo tramo) {
		int count = assertTramoIT15PrimerosDias(tramo);
		List<DatoSolicitado> datoSolicitados = tramo.getDatosTramo().getDatoSolicitado();
		assertDatosSolicitado(datoSolicitados, "C", "563", "B");
		return count + 1;
	}

	private static void assertTramoITPagoDelegadoBecarios(Tramo tramo) {
		List<DatoSolicitado> datoSolicitados = tramo.getDatosTramo().getDatoSolicitado();
		assertEquals(1, datoSolicitados.size());
		assertDatosSolicitado(datoSolicitados, "C", "663", "B");
	}

	private static int assertTramoITATEPPagoDelegado(Tramo tramo) {
		int count = assertTramoIT15PrimerosDias(tramo);
		List<DatoSolicitado> datoSolicitados = tramo.getDatosTramo().getDatoSolicitado();
		assertDatosSolicitado(datoSolicitados, "C", "663", "B");
		return count + 1;
	}

	private static int assertTramoERETotal(Tramo tramo) {
		List<DatoSolicitado> datoSolicitados = tramo.getDatosTramo().getDatoSolicitado();

		assertDatosSolicitado(datoSolicitados, "C", "509", "B");
		try {
			assertDatosSolicitado(datoSolicitados, "C", "603", "B");
		} catch (AssertException e) {
			assertDatosSolicitado(datoSolicitados, "C", "613", "B");
		}

		return 2;
	}

	private static int assertTramoActivoNormal(Tramo tramo) {
		List<DatoSolicitado> datoSolicitados = tramo.getDatosTramo().getDatoSolicitado();

		assertDatosSolicitado(datoSolicitados, "C", "500", "B");
		assertDatosSolicitado(datoSolicitados, "C", "501", "P");
		assertDatosSolicitado(datoSolicitados, "C", "502", "P");
		try {
			assertDatosSolicitado(datoSolicitados, "C", "601", "B");
		} catch (AssertException e) {
			assertDatosSolicitado(datoSolicitados, "C", "611", "B");
		}

		return 4;
	}

	private static int assertTramoTiempoParcial(Tramo tramo) {
		assertTramoActivoNormal(tramo);
		
		List<DatoSolicitado> datoSolicitados = tramo.getDatosTramo().getDatoSolicitado();
		assertDatosSolicitado(datoSolicitados, "H", "01", "B");
		assertDatosSolicitado(datoSolicitados, "H", "02", "P");
		

		return 4;
	}

	private static int assertTramoMaternidadTiempoCompleto(Tramo tramo) {
		List<DatoSolicitado> datoSolicitados = tramo.getDatosTramo().getDatoSolicitado();

		assertDatosSolicitado(datoSolicitados, "C", "509", "B");
		try {
			assertDatosSolicitado(datoSolicitados, "C", "603", "B");
		} catch (AssertException e) {
			assertDatosSolicitado(datoSolicitados, "C", "613", "B");
		}

		return 2;
	}

	private static void assertTramoActivoNormalFormacionEnAlternancia(Tramo tramo) {
		List<DatoSolicitado> datoSolicitados = tramo.getDatosTramo().getDatoSolicitado();
		assertNoDatosSolicitado(datoSolicitados, "I", "51");
		assertDatosSolicitado(datoSolicitados, "C", "500", "B");
		assertDatosSolicitado(datoSolicitados, "C", "301", "P");
		assertDatosSolicitado(datoSolicitados, "C", "601", "B");
		assertDatosSolicitado(datoSolicitados, "H", "03", "P");
		assertDatosSolicitado(datoSolicitados, "H", "04", "P");
		assertDatosSolicitado(datoSolicitados, "H", "06", "P");
		assertDatosSolicitado(datoSolicitados, "C", "737", "P");
		assertDatosSolicitado(datoSolicitados, "C", "501", "P");

	}

	private static void assertDatosSolicitado(Tramo tramo, String tipoDato, String codigo,
			String indicadorObligatoriedad) {
		assertDatosSolicitado(tramo.getDatosTramo().getDatoSolicitado(), tipoDato, codigo, indicadorObligatoriedad);
	}

	private static void assertDatosSolicitado(List<DatoSolicitado> datoSolicitados, String tipoDato, String codigo,
			String indicadorObligatoriedad) {
		for (DatoSolicitado datoSolicitado : datoSolicitados) {
			if (datoSolicitado.getCodigo().equals(codigo)) {
				assertEquals(tipoDato, datoSolicitado.getTipoDato());
				assertEquals(indicadorObligatoriedad, datoSolicitado.getIndicadorObligatoriedad());
				return;
			}
		}

		throw new AssertException("Dato Solicitado " + codigo + " Not Found");
	}

	private static void assertNoDatosSolicitado(Tramo tramo, String tipoDato, String codigo) {
		assertNoDatosSolicitado(tramo.getDatosTramo().getDatoSolicitado(), tipoDato, codigo);
	}

	private static void assertNoDatosSolicitado(List<DatoSolicitado> datoSolicitados, String tipoDato, String codigo) {
		for (DatoSolicitado datoSolicitado : datoSolicitados) {
			if (datoSolicitado.getCodigo().equals(codigo)) {
				throw new AssertException("Dato Solicitado " + codigo + " Found");
			}
		}

	}

	private static void assertDatosSolicitadosCount(int count, Tramo tramo) {
		assertEquals(count, tramo.getDatosTramo().getDatoSolicitado().size());
	}

	private ContractRecord newContract(AONContext aonContext, java.sql.Date startDate, Collection<PEC> ssBonuses) {
		return newContract(aonContext, startDate, ssBonuses, Collections.emptyList());
	}

	private ContractRecord newContract(AONContext aonContext, java.sql.Date startDate, Collection<PEC> ssBonuses,
			Collection<Data> datas) {
		return newContract(aonContext, startDate, ssBonuses, datas,
				new String[] { "1000.00 * DIAS_TRABAJADOS / DIAS_MES", "500.00*DIAS_TRABAJADOS/DIAS_MES", });
	}

	private ContractRecord newContract(AONContext aonContext, java.sql.Date startDate, Collection<PEC> ssBonuses,
			Collection<Data> datas, String[] payments) {

		cleanSystemData(aonContext);
		cleanSystemCosts(aonContext);
		cleanDeductionConcepts(aonContext);
		cleanSystemDeductions(aonContext);
		cleanSystemPayments(aonContext);

		addSystemData(aonContext, startDate, null, new HashMap<String, String>() {
			{
				put("PORCENTAJE_FP", "0.10");
				put("PORCENTAJE_FP_E", "0.60");

				put("PORCENTAJE_CGC", "4.70");
				put("PORCENTAJE_CGC_E", "23.60");

				put("OCUPACION_IT", "[" + "\"h\": 1.40]");
				put("OCUPACION_IMS", "[" + "\"h\": 2.20]");

				put("PORCENTAJE_DESMPL", "[ " + "\"100\": 1.55" + "][TC2]");
				put("PORCENTAJE_DESMPL_E", "[ " + "\"100\": 5.50" + "][TC2]");

				put("PORCENTAJE_FOGASA", "0.20");
			}
		});
		addSystemData(aonContext, getFirstDayOf(2023), null, new HashMap<String, String>() {
			{
				put("PORCENTAJE_MEI", "0.10");
				put("PORCENTAJE_MEI_E", "0.50");
			}
		});

		PaymentConceptRecord prestIT = addConcept(aonContext, ContextVariable.PREST_IT);

		addSSRegimePayment(aonContext, SSRegimeType.GENERAL, getFirstDayOfYear(getToday()), prestIT,
				PaymentType.CRA_0001, String.format("BASE_REGULADORA * 0.00 * %s_1_3", COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s", QUOTE_DAYS), "_P", SalaryType.SALARY);
		addSSRegimePayment(aonContext, SSRegimeType.GENERAL, getFirstDayOfYear(getToday()), prestIT,
				PaymentType.CRA_0001, String.format("BASE_REGULADORA * 0.60 * %s_4_15", COMMON_DISEASE_DAYS),
				String.format("BASE_REGULADORA * 1.00 * %s", QUOTE_DAYS), "_P", SalaryType.SALARY);

		addSSRegimeCost(aonContext, SSRegimeType.GENERAL, getFirstDayOfYear(startDate), "CGC_E",
				DeductionType.COMMON_CONTINGENCY, "BASE_CGC_E * PORCENTAJE_CGC_E/100");
		addSSRegimeCost(aonContext, SSRegimeType.GENERAL, getFirstDayOfYear(startDate), "IT_E",
				DeductionType.PROFESSIONAL_CONTINGENCY,
				"BASE_CGP_E * (isdef PORCENTAJE_IT ? PORCENTAJE_IT : (PORCENTAJE_IT=( isdef OCUPACION ? OCUPACION_IT[OCUPACION] : TARIFA_IT)))/100");
		addSSRegimeCost(aonContext, SSRegimeType.GENERAL, getFirstDayOfYear(startDate), "IMS_E",
				DeductionType.PROFESSIONAL_CONTINGENCY,
				"BASE_CGP_E * (isdef PORCENTAJE_IMS ? PORCENTAJE_IMS : (PORCENTAJE_IMS=( isdef OCUPACION ? OCUPACION_IMS[OCUPACION] : TARIFA_IMS)))/100");
		addSSRegimeCost(aonContext, SSRegimeType.GENERAL, getFirstDayOfYear(startDate), "DESMPL_E",
				DeductionType.UNEMPLOYMENT,
				"(PORCENTAJE_DESMPL == 0) ? 0.00 : ( BASE_CGP_E * ( isdef PORCENTAJE_DESMPL_E ? PORCENTAJE_DESMPL_E : PORCENTAJE_DESMPL_E=(INDEFINIDO ? 5.50 : (TIEMPO_COMPLETO ? 6.70 : 7.70)))/100)");
		addSSRegimeCost(aonContext, SSRegimeType.GENERAL, getFirstDayOfYear(startDate), "FOGASA_E",
				DeductionType.FOGASA, "BASE_CGP_E * PORCENTAJE_FOGASA / 100");
		addSSRegimeCost(aonContext, SSRegimeType.GENERAL, getFirstDayOfYear(startDate), "FP_E", DeductionType.FOGASA,
				"BASE_CGP_E * PORCENTAJE_FP_E/100");

		addSSRegimeCost(aonContext, SSRegimeType.GENERAL, getFirstDayOf(2023), "MEI_E",
			DeductionType.MEI, "BASE_CGC_E * PORCENTAJE_MEI_E/100");

		DeductionConceptRecord fpConcept = addDeductionConcept(aonContext, "FP", DeductionType.COMMON_CONTINGENCY);
		DeductionConceptRecord cgcConcept = addDeductionConcept(aonContext, "CGC", DeductionType.COMMON_CONTINGENCY);
		DeductionConceptRecord meiConcept = addDeductionConcept(aonContext, "MEI", DeductionType.MEI);
		DeductionConceptRecord desmplConcept = addDeductionConcept(aonContext, "DESMPL",
				DeductionType.MEI);

		addSSRegimeDeduction(aonContext, fpConcept, SSRegimeType.GENERAL, startDate, "BASE_CGC * PORCENTAJE_FP/100");
		addSSRegimeDeduction(aonContext, cgcConcept, SSRegimeType.GENERAL, startDate, "BASE_CGC * PORCENTAJE_CGC/100");
		addSSRegimeDeduction(aonContext, desmplConcept, SSRegimeType.GENERAL, startDate,
				"BASE_CGC * PORCENTAJE_DESMPL/100");
		addSSRegimeDeduction(aonContext, meiConcept, SSRegimeType.GENERAL, getFirstDayOf(2023), "BASE_CGC * PORCENTAJE_MEI/100");

		ContractRecord contract = newContract(aonContext, getFirstDayOfYear(startDate), new HashMap<String, String>() {
			{
				put(ContextVariable.OCCUPATION.getName(), "'h'");
				put(ContextVariable.TC2.getName(), "'100'");
				put(ContextVariable.MONTH_DAYS.getName(), "30.00");
			}
		}, payments, new String[] {

		}, null);

		PaymentConceptRecord ereConcept = addConcept(aonContext, "ERE");
		addPayment(aonContext, contract, ereConcept, "/*read-only*/DIAS_ERE * 0.00/**/", "DIAS_ERE * BASE_REGULADORA");

		PaymentConceptRecord ereFzaConcept = addConcept(aonContext, "ERE_FZA");
		addPayment(aonContext, contract, ereFzaConcept, "/*read-only*/DIAS_ERE_FZA * 0.00/**/",
				"DIAS_ERE_FZA * BASE_REGULADORA");

		PaymentConceptRecord ereFzaExoneradoConcept = addConcept(aonContext, "ERE_FZA_EXONERADO");
		addPayment(aonContext, contract, ereFzaExoneradoConcept, "/*read-only*/DIAS_ERE_FZA_EXONERADO * 0.00/**/",
				"DIAS_ERE_FZA_EXONERADO * BASE_REGULADORA");

		datas.forEach(d -> addData(aonContext, contract, toSQL(d.startDate), toSQL(d.endDate), d.name, d.expression));

		ssBonuses.stream().filter(pec -> isBonus(pec)).forEach(b -> addBonus(aonContext, contract,
				toSQL(b.getStartDate()), toSQL(b.getEndDate()), b.getFormula(), b.getDescription()));

		ssBonuses.stream().filter(pec -> isDeduction(pec)).forEach(d -> {
			ContractDeductionRecord deduction = addDeduction(aonContext, contract, toSQL(d.getStartDate()),
					toSQL(d.getEndDate()), d.getFormula(), d.getDescription(), d.getName());
			deduction.setType((byte) SistemaRED2AON.getDeductionType(d.getName()).ordinal());
			deduction.update();
		});

		ssBonuses.stream().filter(pec -> isCost(pec)).forEach(d -> {
			ContractCostRecord contractCost = addCost(aonContext, contract,
					toSQL(d.getStartDate()), toSQL(d.getEndDate()), d.getFormula(), d.getDescription(), d.getName());
			contractCost.setType((byte) SistemaRED2AON.getDeductionType(d.getName()).ordinal());
			contractCost.update();
			
		});
				

		return contract;
	}

	protected final void cleanDeductionConcepts(AONContext aonContext) {
		aonContext.getDslContext().execute("SET FOREIGN_KEY_CHECKS=0");

		aonContext.getDslContext().delete(DEDUCTION_CONCEPT).execute();

		aonContext.getDslContext().execute("SET FOREIGN_KEY_CHECKS=1");
	}

	public static final DeductionConceptRecord addDeductionConcept(AONContext aonContext, String code,
			DeductionType type) {
		aonContext.getDslContext().execute("SET FOREIGN_KEY_CHECKS=0");
		DeductionConceptRecord deductionConceptRecord = aonContext.getDslContext().insertInto(DEDUCTION_CONCEPT)
				.set(DEDUCTION_CONCEPT.DOMAIN, 0).set(DEDUCTION_CONCEPT.CODE, code)
				.set(DEDUCTION_CONCEPT.TYPE, (byte) type.ordinal()).set(DEDUCTION_CONCEPT.DESCRIPTION, code).returning()
				.fetchOne();
		aonContext.getDslContext().execute("SET FOREIGN_KEY_CHECKS=1");
		return deductionConceptRecord;
	}

	protected final void addSSRegimeDeduction(AONContext aonContext, DeductionConceptRecord concept,
			SSRegimeType ssRegimetype, java.sql.Date startDate, String expression) {
		aonContext.getDslContext().execute("SET FOREIGN_KEY_CHECKS=0");

		aonContext.getDslContext().insertInto(SYSTEM_DEDUCTION).set(SYSTEM_DEDUCTION.START_DATE, startDate)
				.set(SYSTEM_DEDUCTION.DOMAIN, (-1) * ssRegimetype.ordinal())
				.set(SYSTEM_DEDUCTION.DEDUCTION_CONCEPT, concept.getId()).set(SYSTEM_DEDUCTION.EXPRESSION, expression)
				.execute();

		aonContext.getDslContext().execute("SET FOREIGN_KEY_CHECKS=1");
	}

	protected final void addSystemPayment(AONContext aonContext, PaymentConceptRecord concept, java.sql.Date startDate,
			String description, String expression, String quoteExpression, String irpfExpression) {
		aonContext.getDslContext().execute("SET FOREIGN_KEY_CHECKS=0");

		aonContext.getDslContext().insertInto(SYSTEM_PAYMENT).set(SYSTEM_PAYMENT.DOMAIN, 0)
				.set(SYSTEM_PAYMENT.START_DATE, startDate).set(SYSTEM_PAYMENT.PAYMENT_CONCEPT, concept.getId())
				.set(SYSTEM_PAYMENT.DESCRIPTION, description).set(SYSTEM_PAYMENT.EXPRESSION, expression)
				.set(SYSTEM_PAYMENT.IRPF_EXPRESSION, irpfExpression)
				.set(SYSTEM_PAYMENT.QUOTE_EXPRESSION, quoteExpression).execute();

		aonContext.getDslContext().execute("SET FOREIGN_KEY_CHECKS=1");
	}

	@Test
	public void testIdcXXVBonus() throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException,
			ExpressionException, SalaryException, SQLException {

		try (InputStream is = IdcTest.class.getResourceAsStream("idcXXV.pdf")) {
			Collection<PEC> ssPecs = Idc.getSSPECs(is);
			assertTrue(ssPecs.size() == 1);

			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);

			calendar.set(Calendar.YEAR, 2022);
			calendar.set(Calendar.DAY_OF_MONTH, 18);
			calendar.set(Calendar.MONTH, Calendar.MARCH);

			Date march182023 = calendar.getTime();

			calendar.set(Calendar.YEAR, 2025);
			calendar.set(Calendar.DAY_OF_MONTH, 17);
			calendar.set(Calendar.MONTH, Calendar.MARCH);

			Date march172025 = calendar.getTime();

			ssPecs.stream().forEach(pec -> assertEquals(march182023, pec.getStartDate()));
			ssPecs.stream().forEach(pec -> assertEquals(march172025, pec.getEndDate()));

			ssPecs.forEach(pec -> System.out.println("[" + pec.getName() + "] " + pec.getDescription() + " = "
					+ pec.getFormula() + ", " + pec.getStartDate()));

			calendar.set(Calendar.YEAR, 2022);
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			calendar.set(Calendar.MONTH, Calendar.OCTOBER);

			Date october = calendar.getTime();

			List<Data> datas = new ArrayList<>();
			datas.add(new Data() {
				{
					expression = "0.50";
					name = ContextVariable.PARTIAL_FACTOR.getName();
					endDate = AonDateUtils.add(october, Calendar.DAY_OF_MONTH, 14);
					startDate = october;
				}
			});
			datas.add(new Data() {
				{
					expression = "0.90";
					name = ContextVariable.PARTIAL_FACTOR.getName();
					endDate = AonDateUtils.getLastDayOfMonth(october);
					startDate = AonDateUtils.add(october, Calendar.DAY_OF_MONTH, 15);
				}
			});

			Salary salary = calculate(ssPecs, datas, october);

			double costs = salary.getSalaryCosts().stream().collect(Collectors.summingDouble(SalaryCost::getAmount));
			double deductions = salary.getSalaryDeductions().stream()
					.collect(Collectors.summingDouble(SalaryDeduction::getAmount));

			salary.getSalaryCosts().forEach(
					c -> System.out.println("COST :" + c.getName() + " : " + c.getAmount() + ", " + c.getType()));
			salary.getSalaryDeductions().forEach(d -> System.out
					.println("DEDUCTION :" + d.getDeductionConcept() + " : " + d.getAmount() + ", " + d.getType()));
			salary.getSalaryBonus().forEach(d -> System.out
					.println("BONUS :" + d.getBonusConcept() + " : " + d.getAmount() + ", " + d.getType()));

			assertEquals(costs - 141.15, salary.getTotalEnterprise(), DELTA);
			// assertEquals(0.00, salary.getSocialSecurityContributions(), DELTA);

		}
	}

	@Test
	public void testIdcXXVIIBonus() throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException,
			ExpressionException, SalaryException, SQLException {

		try (InputStream is = IdcTest.class.getResourceAsStream("idcXXVII.pdf")) {
			Collection<PEC> ssPecs = Idc.getSSPECs(is);
			assertEquals(1, ssPecs.size());

			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);

			calendar.set(Calendar.YEAR, 2022);
			calendar.set(Calendar.DAY_OF_MONTH, 21);
			calendar.set(Calendar.MONTH, Calendar.DECEMBER);

			Date december212022 = calendar.getTime();

			calendar.set(Calendar.YEAR, 2023);
			calendar.set(Calendar.DAY_OF_MONTH, 4);
			calendar.set(Calendar.MONTH, Calendar.APRIL);

			Date april42023 = calendar.getTime();

			ssPecs.stream().forEach(pec -> assertEquals(december212022, pec.getStartDate()));
			ssPecs.stream().forEach(pec -> assertEquals(april42023, pec.getEndDate()));

			ssPecs.forEach(pec -> System.out.println("[" + pec.getName() + "] " + pec.getDescription() + " = "
					+ pec.getFormula() + ", " + pec.getStartDate()));


			List<Data> datas = new ArrayList<>();
			datas.add(new Data() {
				{
					endDate = null;
					startDate = december212022;
					expression = "0.75";
					name = ContextVariable.PARTIAL_FACTOR.getName();
				}
			});

			
			List<Double> notBonusCosts = new ArrayList<>();
			Salary salary = calculate(ssPecs, datas, december212022, new SalaryBuilder() {
			    public void addCost(Double amount, String description, Date start, Date end, IDeduction cost, java.util.Map<String,ITimedVariable<?>> context) {
				if ( AonDateUtils.getFirstDayOfMonth(start).equals(start) )
				    notBonusCosts.add(amount);
				super.addCost(amount, description, start, end, cost, context);
			    };
			});
			
			double totalEnterprise = notBonusCosts.stream().collect(Collectors.summingDouble(d->d));

			assertEquals(totalEnterprise, salary.getTotalEnterprise(), DELTA);

			calendar.set(Calendar.YEAR, 2023);
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			calendar.set(Calendar.MONTH, Calendar.FEBRUARY);
			
			Date february12023 = calendar.getTime();

			salary = calculate(ssPecs, datas, february12023);

			double meiCost = salary.getSalaryCosts().stream().filter( c -> "MEI_E".equals( c.getName())).collect(Collectors.summingDouble(SalaryCost::getAmount));

			assertEquals(meiCost, salary.getTotalEnterprise(), DELTA);
		}
	}

	@Test
	public void testIdcXXVIIIBonus() throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException,
			ExpressionException, SalaryException, SQLException {

		try (InputStream is = IdcTest.class.getResourceAsStream("idcXXVIII.pdf")) {
			Collection<PEC> ssPecs = Idc.getSSPECs(is);
			//assertEquals(5, ssPecs.size());

			ssPecs.forEach(pec -> System.out.println("[" + pec.getName() + "] " + pec.getDescription() + " = "
				+ pec.getFormula() + ", " + pec.getStartDate() + ".." + pec.getEndDate()));

			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);

			calendar.set(Calendar.YEAR, 2020);
			calendar.set(Calendar.DAY_OF_MONTH, 01);
			calendar.set(Calendar.MONTH, Calendar.OCTOBER);
			Date october01102020 = calendar.getTime();

			calendar.set(Calendar.YEAR, 2021);
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			calendar.set(Calendar.MONTH, Calendar.MAY);
			Date may01052021 = calendar.getTime();

			assertPECS(ssPecs, october01102020, may01052021, 1, pec -> pec.getFormula().contains("pec:16,quota:51") );
			
			//ssPecs.stream().forEach(pec -> assertEquals(april42023, pec.getEndDate()));

			calendar.set(Calendar.YEAR, 2021);
			calendar.set(Calendar.DAY_OF_MONTH, 31);
			calendar.set(Calendar.MONTH, Calendar.JANUARY);
			Date january31012021 = calendar.getTime();

			assertPECS(ssPecs, october01102020, january31012021, 1, pec -> pec.getFormula().contains("pec:37,quota:57") );

			calendar.set(Calendar.YEAR, 2021);
			calendar.set(Calendar.DAY_OF_MONTH, 01);
			calendar.set(Calendar.MONTH, Calendar.FEBRUARY);
			Date february01022021 = calendar.getTime();

			calendar.set(Calendar.YEAR, 2021);
			calendar.set(Calendar.DAY_OF_MONTH, 28);
			calendar.set(Calendar.MONTH, Calendar.FEBRUARY);
			Date february28022021 = calendar.getTime();

			assertPECS(ssPecs, february01022021, february28022021, 1, pec -> pec.getFormula().contains("pec:37,quota:57") );

			calendar.set(Calendar.YEAR, 2021);
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			calendar.set(Calendar.MONTH, Calendar.MARCH);
			Date march01032021 = calendar.getTime();

			calendar.set(Calendar.YEAR, 2021);
			calendar.set(Calendar.DAY_OF_MONTH, 31);
			calendar.set(Calendar.MONTH, Calendar.MARCH);
			Date march31032021 = calendar.getTime();

			assertPECS(ssPecs, march01032021, march31032021, 1, pec -> pec.getFormula().contains("pec:37,quota:57") );

			calendar.set(Calendar.YEAR, 2023);
			calendar.set(Calendar.DAY_OF_MONTH, 20);
			calendar.set(Calendar.MONTH, Calendar.MARCH);
			Date march20032023 = calendar.getTime();

			assertPECS(ssPecs, march20032023, null, 1, pec -> pec.getFormula().contains("pec:03,quota:03") );


			calendar.set(Calendar.YEAR, 2023);
			calendar.set(Calendar.DAY_OF_MONTH, 20);
			calendar.set(Calendar.MONTH, Calendar.MARCH);
			Date march01032023 = calendar.getTime();

			Salary salary = calculate(ssPecs, Collections.emptyList(), march01032023);
			
			double totalCgcE = 0.00;
			for (SalaryCost cost : salary.getSalaryCosts()) {
			    	if ("CGC_E".equals(cost.getName()) )
			    	    totalCgcE += cost.getAmount();
			}
			
			double totalBonus = 0.00;
			for (SalaryBonus bonus : salary.getSalaryBonus()) {
				totalBonus += bonus.getAmount();
				
			}
			
			assertEquals(totalCgcE * 0.75 * 12 / 31 , totalBonus, DELTA);

//			
//			double totalEnterprise = notBonusCosts.stream().collect(Collectors.summingDouble(d->d));
//
//			assertEquals(totalEnterprise, salary.getTotalEnterprise(), DELTA);
//
//			calendar.set(Calendar.YEAR, 2023);
//			calendar.set(Calendar.DAY_OF_MONTH, 1);
//			calendar.set(Calendar.MONTH, Calendar.FEBRUARY);
//			
//			Date february12023 = calendar.getTime();
//
//			salary = calculate(ssPecs, datas, february12023);
//
//			double meiCost = salary.getSalaryCosts().stream().filter( c -> "MEI_E".equals( c.getName())).collect(Collectors.summingDouble(SalaryCost::getAmount));
//
//			assertEquals(meiCost, salary.getTotalEnterprise(), DELTA);
		}
	}

	@Test
	public void testIdcXXIXBonus() throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException,
			ExpressionException, SalaryException, SQLException {

		try (InputStream is = IdcTest.class.getResourceAsStream("idcXXIX.pdf")) {
			Collection<PEC> ssPecs = Idc.getSSPECs(is);

			ssPecs.forEach(pec -> System.out.println("[" + pec.getName() + "] " + pec.getDescription() + " = "
				+ pec.getFormula() + ", " + pec.getStartDate() + ".." + pec.getEndDate()));
			//assertEquals(1, ssPecs.size());

			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);

			calendar.set(Calendar.YEAR, 2022);
			calendar.set(Calendar.DAY_OF_MONTH, 24);
			calendar.set(Calendar.MONTH, Calendar.AUGUST);
			Date august282023 = calendar.getTime();

			assertPECS(ssPecs, august282023, null, 1, pec -> pec.getFormula().contains("AVISO") );
			
			try {
			Salary salary = calculate(ssPecs, Collections.emptyList(), august282023, new  SalaryBuilder(), new GenericContractSalaryCalculator.Listener() {
			    public void onCheckError(IContractBonus bonus, String message) {
				throw new Error(message); 
			    };
			});
			} catch ( Error error ) {
			    System.out.println(error.getMessage());
			    return;
			}
			
			fail();
			

		}
	}

	@Test
	public void testIdcXXXBonus() throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException,
			ExpressionException, SalaryException, SQLException {

		try (InputStream is = IdcTest.class.getResourceAsStream("idcXXX.pdf")) {
			Collection<PEC> ssPecs = Idc.getSSPECs(is);

			ssPecs.forEach(pec -> System.out.println("[" + pec.getName() + "] " + pec.getDescription() + " = "
				+ pec.getFormula() + ", " + pec.getStartDate() + ".." + pec.getEndDate()));
			//assertEquals(1, ssPecs.size());

			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);

			calendar.set(Calendar.YEAR, 2023);
			calendar.set(Calendar.DAY_OF_MONTH, 9);
			calendar.set(Calendar.MONTH, Calendar.MARCH);
			Date march092023 = calendar.getTime();

			//assertPECS(ssPecs, march092023, null, 1, pec -> pec.getStartDate().equals(march092023) );
			
			calendar.set(Calendar.YEAR, 2023);
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			calendar.set(Calendar.MONTH, Calendar.APRIL);
			Date april2023 = calendar.getTime();
			Salary salary = calculate(ssPecs, Collections.emptyList(), april2023, new  SalaryBuilder(), new GenericContractSalaryCalculator.Listener());
			double cgcBase = salary.getCommonBase();
			//double cgpBase = salary.getProfessionalBase();
			assertEquals(cgcBase *  ( 0.25 ) / 100.00  , salary.getSocialSecurityContributions(), 0.00);
			assertEquals(cgcBase *  ( 1.30 + 1.40 + 2.20 ) / 100.00  , salary.getTotalEnterprise() , DELTA);
			
			double cgcEmployeePercent = salary.getSalaryData(ContextVariable.CGC_EMPLOYEE_PERCENT.getName(), Double.class);
			assertEquals(0.25 , cgcEmployeePercent, 0.00);
			
			double cgcEnterprisePercent = salary.getSalaryData(ContextVariable.CGC_ENTERPRISE_PERCENT.getName(), Double.class);
			assertEquals(1.30 , cgcEnterprisePercent, 0.00);
			
			calendar.set(Calendar.MONTH, Calendar.MARCH);
			Date march2023 = calendar.getTime();
			salary = calculate(ssPecs, Collections.emptyList(), march2023, new  SalaryBuilder(), new GenericContractSalaryCalculator.Listener());
			
			Set<SalaryData> salaryDatas = salary.getSalaryDatas();
			
			List<SalaryData> cgcBaseDatas = salaryDatas.stream()
				.filter(data -> data.getName().equals(ContextVariable.CGC_BASE.getName()))
				.sorted((d1, d2) -> d1.getStartDate().compareTo(d2.getStartDate())).toList();
			
			assertEquals(2, cgcBaseDatas.size() );
			assertEquals(march2023, cgcBaseDatas.get(0).getStartDate() );
			assertEquals(march092023, cgcBaseDatas.get(1).getStartDate() );
			
			List<SalaryData> cgcBaseEnterpriseDatas = salaryDatas.stream()
				.filter(data -> data.getName().equals(ContextVariable.CGC_BASE_ENTERPRISE.getName()))
				.sorted((d1, d2) -> d1.getStartDate().compareTo(d2.getStartDate())).toList();
			assertEquals(2, cgcBaseDatas.size() );
			assertEquals(march2023, cgcBaseEnterpriseDatas.get(0).getStartDate() );
			assertEquals(march092023, cgcBaseEnterpriseDatas.get(1).getStartDate() );
			
			salary.getSalaryDeductions().forEach( d -> System.out.println(d.getName() + ": " + d.getAmount()));
			
			assertEquals(
				Double.parseDouble(cgcBaseDatas.get(1).getExpression()) *  ( 0.25 ) / 100.00  
				+ Double.parseDouble(cgcBaseDatas.get(0).getExpression()) *  ( 4.7  + 0.10 + 1.55 + 0.10 ) / 100.00  
				, salary.getSocialSecurityContributions(), 0.01);
			
			salary.getSalaryCosts().forEach( d -> System.out.println(d.getName() + ": " + d.getAmount()));

			assertEquals(
				Double.parseDouble(cgcBaseDatas.get(1).getExpression()) *  ( 1.30 + 1.40 + 2.20  ) / 100.00  
				+ Double.parseDouble(cgcBaseDatas.get(0).getExpression()) *  ( 23.60 + 0.50 + 0.60 + 0.20 + 5.50 + 1.40 + 2.20 ) / 100.00  
				, salary.getTotalEnterprise(), 0.01);
			

		}
	}

	@Test
	public void testIdcXXXIBonus() throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException,
			ExpressionException, SalaryException, SQLException {

		try (InputStream is = IdcTest.class.getResourceAsStream("idcXXXI.pdf")) {
			Collection<PEC> ssPecs = Idc.getSSPECs(is);

			ssPecs.forEach(pec -> System.out.println("[" + pec.getName() + "] " + pec.getDescription() + " = "
				+ pec.getFormula() + ", " + pec.getStartDate() + ".." + pec.getEndDate()));
			//assertEquals(1, ssPecs.size());

			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);

			calendar.set(Calendar.YEAR, 2023);
			calendar.set(Calendar.DAY_OF_MONTH, 11);
			calendar.set(Calendar.MONTH, Calendar.JULY);
			Date july112023 = calendar.getTime();

			//assertPECS(ssPecs, july112023, null, 1, pec -> true);
			
			calendar.set(Calendar.YEAR, 2023);
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			calendar.set(Calendar.MONTH, Calendar.AUGUST);
			Date august2023 = calendar.getTime();
			Salary salary = calculate(ssPecs, Collections.emptyList(), august2023, new  SalaryBuilder(), new GenericContractSalaryCalculator.Listener());
			double cgcBase = salary.getCommonBase();
			assertEquals(cgcBase *  ( 0.10 ) / 100.00  , salary.getSocialSecurityContributions(), 0.00);
			assertEquals(cgcBase *  ( 0.50 ) / 100.00  , salary.getTotalEnterprise() , DELTA);
			
		}
	}

	@Test
	public void testIdcXXXIIBonus() throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException,
			ExpressionException, SalaryException, SQLException {

		try (InputStream is = IdcTest.class.getResourceAsStream("idcXXXII.pdf")) {
			Collection<PEC> ssPecs = Idc.getSSPECs(is);

			ssPecs.forEach(pec -> System.out.println("[" + pec.getName() + "] " + pec.getDescription() + " = "
				+ pec.getFormula() + ", " + pec.getStartDate() + ".." + pec.getEndDate()));
			
			//assertEquals(1, ssPecs.size());

			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);

			calendar.set(Calendar.YEAR, 2023);
			calendar.set(Calendar.DAY_OF_MONTH, 6);
			calendar.set(Calendar.MONTH, Calendar.NOVEMBER);
			Date november062023 = calendar.getTime();

			//assertPECS(ssPecs, july112023, null, 1, pec -> true);
			
			calendar.set(Calendar.YEAR, 2023);
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			calendar.set(Calendar.MONTH, Calendar.DECEMBER);
			Date december2023 = calendar.getTime();
			Salary salary = calculate(ssPecs, Collections.emptyList(), december2023, new  SalaryBuilder(), new GenericContractSalaryCalculator.Listener());
			double cgcBase = salary.getCommonBase();
			
			double fpPercent = 0.10;
			double fpEPercent = 0.60;
			
			double cgcPercent = 4.70;
			double cgcEPercent = 23.60;

			double itPercent = 1.40;
			double imsEPercent = 2.20;

			double unemployPercent = 1.55;
			double unemployEPercent = 5.50;

			double meiPercent = 0.10;
			double meiEPercent = 0.50;

			double fogasaPercent = 0.20;

			assertEquals(cgcBase *  ( cgcEPercent + unemployEPercent  + itPercent + imsEPercent + fogasaPercent + meiEPercent + fpEPercent) / 100.00 - 91.00  , salary.getTotalEnterprise() , DELTA);

			
			salary.getSalaryDeductions().forEach( d -> System.out.println(d.getDescription() + " = " + d.getExpression() + " , " + d.getAmount() ));
			
			assertEquals(cgcBase *  ( cgcPercent + unemployPercent  + meiPercent + fpPercent) / 100.00 - 28.00  , salary.getSocialSecurityContributions(), DELTA);
			
		}
	}

	@Test
	public void testIdcXXXIIIBonus() throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException,
			ExpressionException, SalaryException, SQLException {

		try (InputStream is = IdcTest.class.getResourceAsStream("idcXXXIII.pdf")) {
			Collection<PEC> ssPecs = Idc.getSSPECs(is);

			ssPecs.forEach(pec -> System.out.println("[" + pec.getName() + "] " + pec.getDescription() + " = "
				+ pec.getFormula() + ", " + pec.getStartDate() + ".." + pec.getEndDate()));
			
			//assertEquals(1, ssPecs.size());

			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);

			calendar.set(Calendar.YEAR, 2024);
			calendar.set(Calendar.DAY_OF_MONTH, 4);
			calendar.set(Calendar.MONTH, Calendar.FEBRUARY);
			Date february012024 = calendar.getTime();

			//assertPECS(ssPecs, july112023, null, 1, pec -> true);
			
			Salary salary = calculate(ssPecs, Collections.emptyList(), february012024, new  SalaryBuilder(), new GenericContractSalaryCalculator.Listener());
			double cgcBase = salary.getCommonBase();
			
			double cgcPercent = 4.70;
			double cgcEPercent = 23.60;

			//salary.getSalaryDeductions().forEach( d -> System.out.println(d.getDescription() + " = " + d.getExpression() + " , " + d.getAmount() ));

			assertEquals(cgcBase *  cgcPercent / 100.00 *  0.05 , salary.getSocialSecurityContributions() , DELTA);

			salary.getSalaryCosts().forEach( d -> System.out.println(d.getCostConcept() + " = " + d.getExpression() + " , " + d.getAmount() ));
			
			double itPercent = 1.40;
			double imsEPercent = 2.20;

			assertEquals(cgcBase * ( cgcEPercent *  0.05  + itPercent + imsEPercent ) / 100.00 , salary.getTotalEnterprise() , DELTA);
			
		}
	}

	@Test
	public void testIdcXXXVNoBonus() throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException,
			ExpressionException, SalaryException, SQLException {

		try (InputStream is = IdcTest.class.getResourceAsStream("idcXXXV.pdf")) {
			Collection<PEC> ssPecs = Idc.getSSPECs(is);

			ssPecs.forEach(pec -> System.out.println("[" + pec.getName() + "] " + pec.getDescription() + " = "
				+ pec.getFormula() + ", " + pec.getStartDate() + ".." + pec.getEndDate()));
			
			//assertEquals(1, ssPecs.size());

			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);

			calendar.set(Calendar.YEAR, 2024);
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			calendar.set(Calendar.MONTH, Calendar.JUNE);
			Date june012024 = calendar.getTime();

			//assertPECS(ssPecs, july112023, null, 1, pec -> true);
			
			Salary salary = calculate(ssPecs, Collections.emptyList(), june012024, new  SalaryBuilder(), new GenericContractSalaryCalculator.Listener());
			double cgcBase = salary.getCommonBase();
			
			double cgcPercent = 4.70;
			double cgcEPercent = 23.60;
			double meiPercent = 0.10;
			double meiEPercent = 0.50;
			
			assertEquals(salary.getSalaryBonus().size(), 0);

			salary.getSalaryDeductions().forEach( d -> System.out.println(d.getDescription() + " = " + d.getExpression() + " , " + d.getAmount() ));
			
			salary.getSalaryDeductions().stream()
			.forEach( d -> { 
				try {
					assertEquals(DeductionType.MEI, d.getType());
				} catch ( AssertionError failure ) {
					assertEquals(DeductionType.COMMON_CONTINGENCY, d.getType());
				}
			});
			
			double deductions = salary.getSalaryDeductions().stream().collect(Collectors.summingDouble(d -> d.getAmount()));
			
			assertEquals(cgcBase *  cgcPercent / 100.00 *  0.05 + cgcBase *  meiPercent / 100.00 , deductions  , DELTA);

			salary.getSalaryCosts().forEach( d -> System.out.println(d.getAmount() + ": " + d.getCostConcept() + " = " + d.getExpression() + " , " + d.getAmount() ));
			
			salary.getSalaryCosts().stream()
			.forEach( d -> {
				try {
					assertEquals(DeductionType.MEI, d.getType());
				} catch ( AssertionError failure ) {
					try {
						assertEquals(DeductionType.COMMON_CONTINGENCY, d.getType());
					} catch ( AssertionError f ) {
						assertEquals(DeductionType.PROFESSIONAL_CONTINGENCY, d.getType());
					}
				}
			});


			double costs = salary.getSalaryCosts().stream().collect(Collectors.summingDouble(d -> d.getAmount()));
			
			double itPercent = 1.40;
			double imsEPercent = 2.20;

			assertEquals(cgcBase * ( cgcEPercent *  0.05  + itPercent + imsEPercent + meiEPercent ) / 100.00 , costs , DELTA);
			
		}
	}

	@Test
	public void testIdcXXXVINoEscl() throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException,
			ExpressionException, SalaryException, SQLException {

		try (InputStream is = IdcTest.class.getResourceAsStream("idcXXXVI.pdf")) {
			Collection<PEC> ssPecs = Idc.getSSPECs(is);

			ssPecs.forEach(pec -> System.out.println("[" + pec.getName() + "] " + pec.getDescription() + " = "
				+ pec.getFormula() + ", " + pec.getStartDate() + ".." + pec.getEndDate()));
			
			assertEquals(0, ssPecs.size());

			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);

			calendar.set(Calendar.YEAR, 2024);
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			calendar.set(Calendar.MONTH, Calendar.JUNE);
			Date june012024 = calendar.getTime();
			
			Salary salary = calculate(ssPecs, Collections.emptyList(), june012024, new  SalaryBuilder(), new GenericContractSalaryCalculator.Listener());
			double cgcBase = salary.getCommonBase();
			
			double cgcPercent = 4.70;
			double cgcEPercent = 23.60;
			double meiPercent = 0.10;
			double meiEPercent = 0.50;
			double fpPercent = 0.10;
			double fpEPercent = 0.60;
			double desmplPercent = 1.55;
			double desmplEPercent = 5.50;
			double fogasaPercent = 0.20;


			assertEquals(salary.getSalaryBonus().size(), 0);

			salary.getSalaryDeductions().forEach( d -> System.out.println(d.getDescription() + " = " + d.getExpression() + " , " + d.getAmount() ));
			
			
			double deductions = salary.getSalaryDeductions().stream().collect(Collectors.summingDouble(d -> d.getAmount()));
			
			assertEquals(cgcBase *  cgcPercent / 100.00 
						+ cgcBase *  meiPercent / 100.00 
						+ cgcBase *  fpPercent / 100.00 
						+ cgcBase *  desmplPercent / 100.00 
						, deductions  
						, DELTA);

			


			double costs = salary.getSalaryCosts().stream().collect(Collectors.summingDouble(d -> d.getAmount()));
			
			double itPercent = 1.40;
			double imsEPercent = 2.20;

			assertEquals(cgcBase * ( cgcEPercent + itPercent + imsEPercent + meiEPercent + fpEPercent + desmplEPercent+ fogasaPercent ) / 100.00 , costs , DELTA);
			
		}
	}

	@Test
	public void testIdc986Bonus() throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException,
			ExpressionException, SalaryException, SQLException {

		try (InputStream is = IdcTest.class.getResourceAsStream("idc986.pdf")) {
			Collection<PEC> ssPecs = Idc.getSSPECs(is);

			ssPecs.forEach(pec -> System.out.println("[" + pec.getName() + "] " + pec.getDescription() + " = "
				+ pec.getFormula() + ", " + pec.getStartDate() + ".." + pec.getEndDate()));

			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);

			calendar.set(Calendar.YEAR, 2023);
			calendar.set(Calendar.DAY_OF_MONTH, 24);
			calendar.set(Calendar.MONTH, Calendar.APRIL);
			Date april242023 = calendar.getTime();

			assertPECS(ssPecs, april242023, null, 1, pec -> AonStringUtils.equals(pec.getName(),"FP") );
			assertPECS(ssPecs, april242023, null, 1, pec -> AonStringUtils.equals(pec.getName(),"FP_E") );
			assertPECS(ssPecs, april242023, null, 1, pec -> AonStringUtils.equals(pec.getName(),"DESMPL") );
			assertPECS(ssPecs, april242023, null, 1, pec -> AonStringUtils.equals(pec.getName(),"DESMPL_E") );
			assertPECS(ssPecs, april242023, null, 1, pec -> AonStringUtils.equals(pec.getName(),"FOGASA_E") );
			
			//assertPECS(ssPecs, april242023, null, 1, pec -> AonStringUtils.contains(pec.getFormula(),"-1 * CGC") );
			//assertPECS(ssPecs, april242023, null, 1, pec -> AonStringUtils.contains(pec.getFormula(),"CGC_E + IT_E + IMS_E") );

			calendar.set(Calendar.DAY_OF_MONTH, 1);
			calendar.set(Calendar.MONTH, Calendar.MAY);
			Date may012023 = calendar.getTime();
			Salary salary = calculate(ssPecs, Collections.emptyList(), may012023, new  SalaryBuilder(), new GenericContractSalaryCalculator.Listener() );
			
			assertEquals(0.00 , salary.getTotalEnterprise(), DELTA);
			assertEquals(0.00 , salary.getSocialSecurityContributions(), DELTA);

		}
	}

	@Test
	public void testIdcplnssTrabajadoresTramosXIX()
			throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException, JAXBException {
		try (InputStream is = IdcTest.class.getResourceAsStream("idcplnssXIX.pdf")) {
			TrabajadoresTramos trabajadoresTramos = Idcplnss.getTrabajadoresTramos(is,
					new TrabajadoresTramosCallback() {
						@Override
						public boolean isPartTimeEmployee(String ssNum, String ccc, Date start, Date end) {
							return true;
						}
			});
	
			marshal(trabajadoresTramos, System.out);
	
			Liquidacion liquidacion = trabajadoresTramos.getLiquidacion();
	
			assertEquals("0111", liquidacion.getCcc().getRegimen());
			assertEquals("41", liquidacion.getCcc().getProvincia());
			assertEquals("134362691", liquidacion.getCcc().getNumero());
	
			assertEquals("10", liquidacion.getPeriodoDesde().getMes());
			assertEquals("2022", liquidacion.getPeriodoDesde().getAnho());
			assertEquals("10", liquidacion.getPeriodoHasta().getMes());
			assertEquals("2022", liquidacion.getPeriodoHasta().getAnho());
	
			assertEquals(1, liquidacion.getLiquidacionMes().size());
	
			LiquidacionMes liquidacionesMes = liquidacion.getLiquidacionMes().get(0);
			assertEquals("10", liquidacionesMes.getMesLiquidativo().getMes());
			assertEquals("2022", liquidacionesMes.getMesLiquidativo().getAnho());
	
			Trabajadores trabajadores = liquidacionesMes.getTrabajadores();
			assertEquals(1, trabajadores.getTrabajador().size());
	
			for (Trabajador trabajador : trabajadores.getTrabajador()) {
	
				assertEquals("410161125294", trabajador.getNaf());
	
				assertEquals(1, trabajador.getTramos().getTramo().size());
	
				Tramo tramo = trabajador.getTramos().getTramo().get(0);
				assertEquals("09", tramo.getInformacionAfiliacion().getGrupoCotizacion());
				assertEquals("20", tramo.getFechaDesde().getDia());
				assertEquals("10", tramo.getFechaDesde().getMes());
				assertEquals("2022", tramo.getFechaDesde().getAnho());
				assertEquals("31", tramo.getFechaHasta().getDia());
				assertEquals("10", tramo.getFechaHasta().getMes());
				assertEquals("2022", tramo.getFechaHasta().getAnho());
				assertTramoTiempoParcial(tramo);	
			}
	
		}
	}


	@Test
	public void testIdcplcccTrabajadoresTramosV()
			throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException, JAXBException {
		
		
		try (InputStream is = IdcTest.class.getResourceAsStream("idcplcccV.pdf")) {
			TrabajadoresTramos trabajadoresTramos = Idcplccc.geTrabajadoresTramos(is, new TrabajadoresTramosCallback() {
			});
	
			//marshall(trabajadoresTramos, System.out);
	
			Liquidacion liquidacion = trabajadoresTramos.getLiquidacion();
	
			assertEquals("0111", liquidacion.getCcc().getRegimen());
			assertEquals("41", liquidacion.getCcc().getProvincia());
			assertEquals("124028555", liquidacion.getCcc().getNumero());
	
			assertEquals("09", liquidacion.getPeriodoDesde().getMes());
			assertEquals("2022", liquidacion.getPeriodoDesde().getAnho());
			assertEquals("09", liquidacion.getPeriodoHasta().getMes());
			assertEquals("2022", liquidacion.getPeriodoHasta().getAnho());
	
			assertEquals(1, liquidacion.getLiquidacionMes().size());
	
			LiquidacionMes liquidacionesMes = liquidacion.getLiquidacionMes().get(0);
			assertEquals("09", liquidacionesMes.getMesLiquidativo().getMes());
			assertEquals("2022", liquidacionesMes.getMesLiquidativo().getAnho());
	
			Trabajadores trabajadores = liquidacionesMes.getTrabajadores();
			assertEquals(19, trabajadores.getTrabajador().size());
	
			for (Trabajador trabajador : trabajadores.getTrabajador()) {
				if ("411011776004".equals(trabajador.getNaf())) {
					marshal(trabajador, System.out);
					assertEquals(2, trabajador.getTramos().getTramo().size());
					assertEquals((double)1.0, Double.valueOf(trabajador.getTramos().getTramo().get(0).getFechaDesde().getDia()), 0.00);
					assertEquals((double)5.0, Double.valueOf(trabajador.getTramos().getTramo().get(0).getFechaHasta().getDia()), 0.00);
					assertTramoIT15PrimerosDias(trabajador.getTramos().getTramo().get(0));
					assertEquals((double)6.0, Double.valueOf(trabajador.getTramos().getTramo().get(1).getFechaDesde().getDia()), 0.00);
					assertEquals((double)30.0, Double.valueOf(trabajador.getTramos().getTramo().get(1).getFechaHasta().getDia()), 0.00);
					assertTramoActivoNormal(trabajador.getTramos().getTramo().get(1));
					return;
				}
			}
			
			fail();
	
		}
	}

	@Test
	public void testIdcplcccTrabajadoresTramosVI()
			throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException, JAXBException {
		
		
		try (InputStream is = IdcTest.class.getResourceAsStream("idcplcccVI.pdf")) {
			TrabajadoresTramos trabajadoresTramos = Idcplccc.geTrabajadoresTramos(is, new TrabajadoresTramosCallback() {
			});
	
			//marshall(trabajadoresTramos, System.out);
	
			Liquidacion liquidacion = trabajadoresTramos.getLiquidacion();
	
			assertEquals("0111", liquidacion.getCcc().getRegimen());
			assertEquals("41", liquidacion.getCcc().getProvincia());
			assertEquals("124028555", liquidacion.getCcc().getNumero());
	
			assertEquals("05", liquidacion.getPeriodoDesde().getMes());
			assertEquals("2022", liquidacion.getPeriodoDesde().getAnho());
			assertEquals("05", liquidacion.getPeriodoHasta().getMes());
			assertEquals("2022", liquidacion.getPeriodoHasta().getAnho());
	
			assertEquals(1, liquidacion.getLiquidacionMes().size());
	
			LiquidacionMes liquidacionesMes = liquidacion.getLiquidacionMes().get(0);
			assertEquals("05", liquidacionesMes.getMesLiquidativo().getMes());
			assertEquals("2022", liquidacionesMes.getMesLiquidativo().getAnho());
	
			Trabajadores trabajadores = liquidacionesMes.getTrabajadores();
			//assertEquals(19, trabajadores.getTrabajador().size());
	
			for (Trabajador trabajador : trabajadores.getTrabajador()) {
				if ("411146304896".equals(trabajador.getNaf())) {
					marshal(trabajador, System.out);
					assertEquals(4, trabajador.getTramos().getTramo().size());
					assertEquals((double)1.0, Double.valueOf(trabajador.getTramos().getTramo().get(0).getFechaDesde().getDia()), 0.00);
					assertEquals((double)15.0, Double.valueOf(trabajador.getTramos().getTramo().get(0).getFechaHasta().getDia()), 0.00);
					assertTramoActivoNormal(trabajador.getTramos().getTramo().get(0));

					assertEquals((double)16.0, Double.valueOf(trabajador.getTramos().getTramo().get(1).getFechaDesde().getDia()), 0.00);
					assertEquals((double)20.0, Double.valueOf(trabajador.getTramos().getTramo().get(1).getFechaHasta().getDia()), 0.00);
					assertTramoIT15PrimerosDias(trabajador.getTramos().getTramo().get(1));

					assertEquals((double)21.0, Double.valueOf(trabajador.getTramos().getTramo().get(2).getFechaDesde().getDia()), 0.00);
					assertEquals((double)22.0, Double.valueOf(trabajador.getTramos().getTramo().get(2).getFechaHasta().getDia()), 0.00);
					assertTramoActivoNormal(trabajador.getTramos().getTramo().get(2));
					
					assertEquals((double)23.0, Double.valueOf(trabajador.getTramos().getTramo().get(3).getFechaDesde().getDia()), 0.00);
					assertEquals((double)31.0, Double.valueOf(trabajador.getTramos().getTramo().get(3).getFechaHasta().getDia()), 0.00);
					assertTramoIT15PrimerosDias(trabajador.getTramos().getTramo().get(3));
					return;
				}
			}
			
			fail();
	
		}
	}

	@Test
	public void testIdcplcccTrabajadoresTramosVII()
			throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException, JAXBException {
		
		
		try (InputStream is = IdcTest.class.getResourceAsStream("idcplcccVII.pdf")) {
			TrabajadoresTramos trabajadoresTramos = Idcplccc.geTrabajadoresTramos(is, new TrabajadoresTramosCallback() {
			});
	
			//marshall(trabajadoresTramos, System.out);
	
			Liquidacion liquidacion = trabajadoresTramos.getLiquidacion();
	
			assertEquals("0111", liquidacion.getCcc().getRegimen());
			assertEquals("41", liquidacion.getCcc().getProvincia());
			assertEquals("124028555", liquidacion.getCcc().getNumero());
	
			assertEquals("04", liquidacion.getPeriodoDesde().getMes());
			assertEquals("2022", liquidacion.getPeriodoDesde().getAnho());
			assertEquals("04", liquidacion.getPeriodoHasta().getMes());
			assertEquals("2022", liquidacion.getPeriodoHasta().getAnho());
	
			assertEquals(1, liquidacion.getLiquidacionMes().size());
	
			LiquidacionMes liquidacionesMes = liquidacion.getLiquidacionMes().get(0);
			assertEquals("04", liquidacionesMes.getMesLiquidativo().getMes());
			assertEquals("2022", liquidacionesMes.getMesLiquidativo().getAnho());
	
			Trabajadores trabajadores = liquidacionesMes.getTrabajadores();
	
			for (Trabajador trabajador : trabajadores.getTrabajador()) {
				if ("411146304896".equals(trabajador.getNaf())) {
					marshal(trabajador, System.out);
					assertEquals(3, trabajador.getTramos().getTramo().size());

					assertEquals((double)1.0, Double.valueOf(trabajador.getTramos().getTramo().get(0).getFechaDesde().getDia()), 0.00);
					assertEquals((double)11.0, Double.valueOf(trabajador.getTramos().getTramo().get(0).getFechaHasta().getDia()), 0.00);
					assertTramoActivoNormal(trabajador.getTramos().getTramo().get(0));

					assertEquals((double)12.0, Double.valueOf(trabajador.getTramos().getTramo().get(1).getFechaDesde().getDia()), 0.00);
					assertEquals((double)13.0, Double.valueOf(trabajador.getTramos().getTramo().get(1).getFechaHasta().getDia()), 0.00);
					assertTramoITATEPPagoDelegado(trabajador.getTramos().getTramo().get(1));

					assertEquals((double)14.0, Double.valueOf(trabajador.getTramos().getTramo().get(2).getFechaDesde().getDia()), 0.00);
					assertEquals((double)30.0, Double.valueOf(trabajador.getTramos().getTramo().get(2).getFechaHasta().getDia()), 0.00);
					assertTramoActivoNormal(trabajador.getTramos().getTramo().get(2));
					
					return;
				}
			}
			
			fail();
	
		}
	}

	@Test
	public void testIdcplcccTrabajadoresTramosVIII()
			throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException, JAXBException {
		
		
		try (InputStream is = IdcTest.class.getResourceAsStream("idcplcccVIII.pdf")) {
			TrabajadoresTramos trabajadoresTramos = Idcplccc.geTrabajadoresTramos(is, new TrabajadoresTramosCallback() {
			});
	
			//marshall(trabajadoresTramos, System.out);
	
			Liquidacion liquidacion = trabajadoresTramos.getLiquidacion();
	
			assertEquals("0111", liquidacion.getCcc().getRegimen());
			assertEquals("41", liquidacion.getCcc().getProvincia());
			assertEquals("017063249", liquidacion.getCcc().getNumero());
	
			assertEquals("10", liquidacion.getPeriodoDesde().getMes());
			assertEquals("2022", liquidacion.getPeriodoDesde().getAnho());
			assertEquals("10", liquidacion.getPeriodoHasta().getMes());
			assertEquals("2022", liquidacion.getPeriodoHasta().getAnho());
	
			assertEquals(1, liquidacion.getLiquidacionMes().size());
	
			LiquidacionMes liquidacionesMes = liquidacion.getLiquidacionMes().get(0);
			assertEquals("10", liquidacionesMes.getMesLiquidativo().getMes());
			assertEquals("2022", liquidacionesMes.getMesLiquidativo().getAnho());
	
			Trabajadores trabajadores = liquidacionesMes.getTrabajadores();
	
			for (Trabajador trabajador : trabajadores.getTrabajador()) {
				if ("410132761989".equals(trabajador.getNaf())) {
					marshal(trabajador, System.out);
					assertEquals(3, trabajador.getTramos().getTramo().size());

					assertEquals((double)1.0, Double.valueOf(trabajador.getTramos().getTramo().get(0).getFechaDesde().getDia()), 0.00);
					assertEquals((double)19.0, Double.valueOf(trabajador.getTramos().getTramo().get(0).getFechaHasta().getDia()), 0.00);
					assertTramoActivoNormal(trabajador.getTramos().getTramo().get(0));

					assertEquals((double)20.0, Double.valueOf(trabajador.getTramos().getTramo().get(1).getFechaDesde().getDia()), 0.00);
					assertEquals((double)21.0, Double.valueOf(trabajador.getTramos().getTramo().get(1).getFechaHasta().getDia()), 0.00);
					assertTramoITATEPPagoDelegado(trabajador.getTramos().getTramo().get(1));
					assertDatosSolicitado(trabajador.getTramos().getTramo().get(1), "I", "51", "P");

					assertEquals((double)22.0, Double.valueOf(trabajador.getTramos().getTramo().get(2).getFechaDesde().getDia()), 0.00);
					assertEquals((double)31.0, Double.valueOf(trabajador.getTramos().getTramo().get(2).getFechaHasta().getDia()), 0.00);
					assertTramoActivoNormal(trabajador.getTramos().getTramo().get(2));
					
					return;
				}
			}
			
			fail();
	
		}
	}

	@Test
	public void testIdcplcccTrabajadoresTramosIX()
			throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException, JAXBException {
		
		
		try (InputStream is = IdcTest.class.getResourceAsStream("idcplcccIX.pdf")) {
			TrabajadoresTramos trabajadoresTramos = Idcplccc.geTrabajadoresTramos(is, new TrabajadoresTramosCallback() {
				@Override
				public boolean isPartTimeEmployee(String ssNum, String ccc, Date start, Date end) {
					return "411087630408".equals(ssNum);
				}
			});
	
			//marshall(trabajadoresTramos, System.out);
	
			Liquidacion liquidacion = trabajadoresTramos.getLiquidacion();
	
			assertEquals("0111", liquidacion.getCcc().getRegimen());
			assertEquals("41", liquidacion.getCcc().getProvincia());
			assertEquals("134362691", liquidacion.getCcc().getNumero());
	
			assertEquals("10", liquidacion.getPeriodoDesde().getMes());
			assertEquals("2022", liquidacion.getPeriodoDesde().getAnho());
			assertEquals("10", liquidacion.getPeriodoHasta().getMes());
			assertEquals("2022", liquidacion.getPeriodoHasta().getAnho());
	
			assertEquals(1, liquidacion.getLiquidacionMes().size());
	
			LiquidacionMes liquidacionesMes = liquidacion.getLiquidacionMes().get(0);
			assertEquals("10", liquidacionesMes.getMesLiquidativo().getMes());
			assertEquals("2022", liquidacionesMes.getMesLiquidativo().getAnho());
	
			Trabajadores trabajadores = liquidacionesMes.getTrabajadores();
			
			
			for (Trabajador trabajador : trabajadores.getTrabajador()) {
				marshal(trabajador, System.out);
				if ("411043162473".equals(trabajador.getNaf())) {
					marshal(trabajador, System.out);
					assertEquals(1, trabajador.getTramos().getTramo().size());

					assertEquals((double)1.0, Double.valueOf(trabajador.getTramos().getTramo().get(0).getFechaDesde().getDia()), 0.00);
					assertEquals((double)24.0, Double.valueOf(trabajador.getTramos().getTramo().get(0).getFechaHasta().getDia()), 0.00);
					assertTramoActivoNormal(trabajador.getTramos().getTramo().get(0));
				}
				else if ("411087630408".equals(trabajador.getNaf())) {
					marshal(trabajador, System.out);
					assertEquals(1, trabajador.getTramos().getTramo().size());

					assertEquals((double)1.0, Double.valueOf(trabajador.getTramos().getTramo().get(0).getFechaDesde().getDia()), 0.00);
					assertEquals((double)31.0, Double.valueOf(trabajador.getTramos().getTramo().get(0).getFechaHasta().getDia()), 0.00);
					assertTramoTiempoParcial(trabajador.getTramos().getTramo().get(0));
				}
				
			}
			
	
		}
	}

	@Test
	public void testIdcplcccTrabajadoresTramosXXIV()
			throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException, JAXBException {
		try (InputStream is = IdcTest.class.getResourceAsStream("idcplcccXXIV.pdf")) {
			TrabajadoresTramos trabajadoresTramos = Idcplccc.geTrabajadoresTramos(is,
				new TrabajadoresTramosCallback() {
			    
			    		@Override
			    		public boolean isQuoteByRealDays(String ssNum, String ccc, Date start, Date end) {
			    		    return true;
			    		}
			    
			    		@Override
			    		public boolean isPartTimeEmployee(String ssNum, String ccc, Date start, Date end) {
			    		    return true;
			    		}
			    		
			    		
				});
	
			marshal(trabajadoresTramos, System.out);
	
			Liquidacion liquidacion = trabajadoresTramos.getLiquidacion();
	
			assertEquals("0163", liquidacion.getCcc().getRegimen());
			assertEquals("25", liquidacion.getCcc().getProvincia());
			assertEquals("107094626", liquidacion.getCcc().getNumero());
	
			assertEquals("04", liquidacion.getPeriodoDesde().getMes());
			assertEquals("2023", liquidacion.getPeriodoDesde().getAnho());
			assertEquals("04", liquidacion.getPeriodoHasta().getMes());
			assertEquals("2023", liquidacion.getPeriodoHasta().getAnho());
	
			assertEquals(1, liquidacion.getLiquidacionMes().size());
	
			LiquidacionMes liquidacionesMes = liquidacion.getLiquidacionMes().get(0);
			assertEquals("04", liquidacionesMes.getMesLiquidativo().getMes());
			assertEquals("2023", liquidacionesMes.getMesLiquidativo().getAnho());
	
			Trabajadores trabajadores = liquidacionesMes.getTrabajadores();
			assertEquals(1, trabajadores.getTrabajador().size());
	
			for (Trabajador trabajador : trabajadores.getTrabajador()) {
			    	assertEquals("251021626115", trabajador.getNaf());
				assertEquals(1, trabajador.getTramos().getTramo().size());
	
				Tramo tramo = trabajador.getTramos().getTramo().get(0);
				
				assertEquals("10", tramo.getInformacionAfiliacion().getGrupoCotizacion());
				
				assertEquals("01", tramo.getFechaDesde().getDia());
				assertEquals("04", tramo.getFechaDesde().getMes());
				assertEquals("2023", tramo.getFechaDesde().getAnho());
				assertEquals("30", tramo.getFechaHasta().getDia());
				assertEquals("04", tramo.getFechaHasta().getMes());
				assertEquals("2023", tramo.getFechaHasta().getAnho());
				assertTramoActivoNormal(tramo);	
				assertNoDatosSolicitado(tramo, "C", "51");
				assertNoDatosSolicitado(tramo, "H", "01");
			}
	
		}
	}

	@Test
	public void testIdcplcccTrabajadoresTramosXXV()
			throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException, JAXBException {
		try (InputStream is = IdcTest.class.getResourceAsStream("idcplcccXXV.pdf")) {
			TrabajadoresTramos trabajadoresTramos = Idcplccc.geTrabajadoresTramos(is, new TrabajadoresTramosCallback() {});
	
			marshal(trabajadoresTramos, System.out);
	
			Liquidacion liquidacion = trabajadoresTramos.getLiquidacion();
	
			assertEquals("0111", liquidacion.getCcc().getRegimen());
			assertEquals("10", liquidacion.getCcc().getProvincia());
			assertEquals("106571658", liquidacion.getCcc().getNumero());
	
			assertEquals("05", liquidacion.getPeriodoDesde().getMes());
			assertEquals("2023", liquidacion.getPeriodoDesde().getAnho());
			assertEquals("05", liquidacion.getPeriodoHasta().getMes());
			assertEquals("2023", liquidacion.getPeriodoHasta().getAnho());
	
			assertEquals(1, liquidacion.getLiquidacionMes().size());
	
			LiquidacionMes liquidacionesMes = liquidacion.getLiquidacionMes().get(0);
			assertEquals("05", liquidacionesMes.getMesLiquidativo().getMes());
			assertEquals("2023", liquidacionesMes.getMesLiquidativo().getAnho());
	
			Trabajadores trabajadores = liquidacionesMes.getTrabajadores();
			assertEquals(3, trabajadores.getTrabajador().size());
	
			for (Trabajador trabajador : trabajadores.getTrabajador()) {
			    if ("100029261143".equals(trabajador.getNaf())) {
				assertEquals(1, trabajador.getTramos().getTramo().size());
				
				Tramo tramo = trabajador.getTramos().getTramo().get(0);
				
				assertEquals("10", tramo.getInformacionAfiliacion().getGrupoCotizacion());
				
				assertEquals("01", tramo.getFechaDesde().getDia());
				assertEquals("05", tramo.getFechaDesde().getMes());
				assertEquals("2023", tramo.getFechaDesde().getAnho());
				assertEquals("31", tramo.getFechaHasta().getDia());
				assertEquals("05", tramo.getFechaHasta().getMes());
				assertEquals("2023", tramo.getFechaHasta().getAnho());
				assertTramoITPagoDelegado(tramo);	
				assertDatosSolicitado(tramo, "I", "51", "P");
				assertNoDatosSolicitado(tramo, "C", "601");
			    } else if ("100029281856".equals(trabajador.getNaf())) {
				
			    } else if ("100038464322".equals(trabajador.getNaf())){
				
			    } else {
				fail();
			    }
			}
	
		}
	}

	@Test
	public void testIdcplcccTrabajadoresTramos421()
			throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException, JAXBException {
		
		
		try (InputStream is = IdcTest.class.getResourceAsStream("idcplccc421.pdf")) {
			TrabajadoresTramos trabajadoresTramos = Idcplccc.geTrabajadoresTramos(is, new TrabajadoresTramosCallback() {
			    @Override
			    public boolean isTraining421Employee(String ssNum, String ccc, Date start, Date end) {
				switch (ssNum) {
				case "111078364778":
				case "111035757934":
				case "111052508016":
				case "111072485770":
				case "111061715235":
				case "111048258911":
				case "111067053972":
				case "111030755259":
				case "111087431349":
				case "111093814454":
				case "111046070246":
				    return true ;

				default:
				    return false;
				}
			    }
			});
	
			//marshall(trabajadoresTramos, System.out);
	
			Liquidacion liquidacion = trabajadoresTramos.getLiquidacion();
	
			assertEquals("0111", liquidacion.getCcc().getRegimen());
			assertEquals("11", liquidacion.getCcc().getProvincia());
			assertEquals("120424045", liquidacion.getCcc().getNumero());
	
			assertEquals("05", liquidacion.getPeriodoDesde().getMes());
			assertEquals("2023", liquidacion.getPeriodoDesde().getAnho());
			assertEquals("05", liquidacion.getPeriodoHasta().getMes());
			assertEquals("2023", liquidacion.getPeriodoHasta().getAnho());
	
			assertEquals(1, liquidacion.getLiquidacionMes().size());
	
			LiquidacionMes liquidacionesMes = liquidacion.getLiquidacionMes().get(0);
			assertEquals("05", liquidacionesMes.getMesLiquidativo().getMes());
			assertEquals("2023", liquidacionesMes.getMesLiquidativo().getAnho());
	
			Trabajadores trabajadores = liquidacionesMes.getTrabajadores();
			
			
//			| 111078364778           | FORNELL SUAREZ, DIEGO ALEJANDRO  |
//			| 111035757934           | ANDRADES GONZALEZ, ESTELA        |
//			| 111052508016           | PEREZ BAREA, DANIELA MARIA       |
//			| 111072485770           | ZUÑIGA ZAMORANO, PATRICIO ELIAS  |
//			| 111061715235           | MARÍN RUEDA, LAURA               |
//			| 111048258911           | GONZALEZ RONDAN, MIRIAM          |
//			| 111067053972           | HERRERA PICAZO, SARA CASARES     |
//			| 111030755259           | GALAN MARROQUIN, ALBA            |
//			| 111087431349           | PEREZ PEREZ, ISABEL MARIA        |
//			| 111093814454           | DIAZ BAREA, ADRIAN               |
//			| 111046070246           | PEREZ ENRIQUEZ, REGINA           |

			for (Trabajador trabajador : trabajadores.getTrabajador()) {
				switch (trabajador.getNaf()) {
				case "111078364778":
				case "111035757934":
				case "111052508016":
				case "111072485770":
				case "111061715235":
				case "111048258911":
				case "111067053972":
				case "111030755259":
				case "111087431349":
				case "111093814454":
				case "111046070246":
				    //marshal(trabajador, System.out);
				    trabajador.getTramos().getTramo().forEach(IdcTest::assertTramoActivoNormalFormacionEnAlternancia);
				    break;
				case "111046297689" :
				case "111064282806" : 
				    assertTramoActivoNormal(trabajador.getTramos().getTramo().get(0));
				    assertTramoIT15PrimerosDias(trabajador.getTramos().getTramo().get(1));
				    assertTramoActivoNormal(trabajador.getTramos().getTramo().get(2));
				    break;
				default:
				    trabajador.getTramos().getTramo().forEach(IdcTest::assertTramoActivoNormal);
				    break;
				}
				
			}
			
	
		}
	}

	@Test
	public void testIdcplcccTrabajadoresTramos421IT()
			throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException, JAXBException {
		
		
		try (InputStream is = IdcTest.class.getResourceAsStream("idcplccc421IT.pdf")) {
			TrabajadoresTramos trabajadoresTramos = Idcplccc.geTrabajadoresTramos(is, new TrabajadoresTramosCallback() {
			    @Override
			    public boolean isTraining421Employee(String ssNum, String ccc, Date start, Date end) {
				switch (ssNum) {
				case "411111039336":
				    return true ;

				default:
				    return false;
				}
			    }
			});
	
			//marshall(trabajadoresTramos, System.out);
	
			Liquidacion liquidacion = trabajadoresTramos.getLiquidacion();
	
			assertEquals("0111", liquidacion.getCcc().getRegimen());
			assertEquals("41", liquidacion.getCcc().getProvincia());
			assertEquals("134937520", liquidacion.getCcc().getNumero());
	
			assertEquals("05", liquidacion.getPeriodoDesde().getMes());
			assertEquals("2023", liquidacion.getPeriodoDesde().getAnho());
			assertEquals("05", liquidacion.getPeriodoHasta().getMes());
			assertEquals("2023", liquidacion.getPeriodoHasta().getAnho());
	
			assertEquals(1, liquidacion.getLiquidacionMes().size());
	
			LiquidacionMes liquidacionesMes = liquidacion.getLiquidacionMes().get(0);
			assertEquals("05", liquidacionesMes.getMesLiquidativo().getMes());
			assertEquals("2023", liquidacionesMes.getMesLiquidativo().getAnho());
	
			Trabajadores trabajadores = liquidacionesMes.getTrabajadores();
			
			
			for (Trabajador trabajador : trabajadores.getTrabajador()) {
				switch (trabajador.getNaf()) {
				// ISABEL BARBERO OVIEDO
				case "411111039336":
				    trabajador.getTramos().getTramo().sort((t1,t2) -> t1.getFechaDesde().getDia().compareTo(t2.getFechaDesde().getDia()) );
				    assertTramoITPagoDelegado (trabajador.getTramos().getTramo().get(0));
				    assertTramoMaternidadTiempoCompleto(trabajador.getTramos().getTramo().get(1));
				    break;
				// EDUARDO ALCON SALAMANCA
				case "411068669332" : 
				    marshal(trabajador, System.out);
				    trabajador.getTramos().getTramo().sort((t1,t2) -> t1.getFechaDesde().getDia().compareTo(t2.getFechaDesde().getDia()) );
				    assertTramoActivoNormal(trabajador.getTramos().getTramo().get(0));
				    assertTramoMaternidadTiempoCompleto(trabajador.getTramos().getTramo().get(1));
				    break;
				// JOSE GUILLERMO ALCON SALAMANCA
				default:
				    trabajador.getTramos().getTramo().forEach(IdcTest::assertTramoActivoNormal);
				    break;
				}
				
			}
			
	
		}
	}

	@Test
	public void testIdcplcccTrabajadoresTramos421ITIII()
			throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException, JAXBException {
		
		
		try (InputStream is = IdcTest.class.getResourceAsStream("idcplccc421ITIII.pdf")) {
			TrabajadoresTramos trabajadoresTramos = Idcplccc.geTrabajadoresTramos(is, new TrabajadoresTramosCallback() {
			    @Override
			    public boolean isTraining421Employee(String ssNum, String ccc, Date start, Date end) {
				switch (ssNum) {
				case "411111039336":
				    return true ;

				default:
				    return false;
				}
			    }
			});
	
			//marshall(trabajadoresTramos, System.out);
	
			Liquidacion liquidacion = trabajadoresTramos.getLiquidacion();
	
			assertEquals("0111", liquidacion.getCcc().getRegimen());
			assertEquals("41", liquidacion.getCcc().getProvincia());
			assertEquals("134937520", liquidacion.getCcc().getNumero());
	
			assertEquals("03", liquidacion.getPeriodoDesde().getMes());
			assertEquals("2023", liquidacion.getPeriodoDesde().getAnho());
			assertEquals("03", liquidacion.getPeriodoHasta().getMes());
			assertEquals("2023", liquidacion.getPeriodoHasta().getAnho());
	
			assertEquals(1, liquidacion.getLiquidacionMes().size());
	
			LiquidacionMes liquidacionesMes = liquidacion.getLiquidacionMes().get(0);
			assertEquals("03", liquidacionesMes.getMesLiquidativo().getMes());
			assertEquals("2023", liquidacionesMes.getMesLiquidativo().getAnho());
	
			Trabajadores trabajadores = liquidacionesMes.getTrabajadores();
			
			
			for (Trabajador trabajador : trabajadores.getTrabajador()) {
				switch (trabajador.getNaf()) {
				// ISABEL BARBERO OVIEDO
				case "411111039336":
				    marshal(trabajador, System.out);
				    trabajador.getTramos().getTramo().sort((t1,t2) -> t1.getFechaDesde().getDia().compareTo(t2.getFechaDesde().getDia()) );
				    assertTramoActivoNormalFormacionEnAlternancia(trabajador.getTramos().getTramo().get(0));
				    assertTramoIT15PrimerosDias(trabajador.getTramos().getTramo().get(1));
				    break;
				// EDUARDO ALCON SALAMANCA
				// JOSE GUILLERMO ALCON SALAMANCA
				default:
				    trabajador.getTramos().getTramo().forEach(IdcTest::assertTramoActivoNormal);
				    break;
				}
				
			}
			
	
		}
	}

	@Test
	public void testIdcXXXIV() throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException, ParseException {
		try (InputStream is = IdcTest.class.getResourceAsStream("idcXXXIV.pdf")) {
			EmployeeITListener employeeITListener = new EmployeeITListener();
			IdcParser.parse(is, employeeITListener);
			Collection<EmployeeIT> employeeIts = employeeITListener.getEmployeeITs();
			
			assertEquals(1, employeeIts.size());
			for (EmployeeIT employeeIT : employeeIts) {
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
				Date dateFrom = simpleDateFormat.parse("04-04-2024");
				Date dateTo = simpleDateFormat.parse("17-04-2024");
				assertEquals( dateFrom , employeeIT.getStartDate());
				assertEquals( dateTo , employeeIT.getEndDate().get());
				assertEquals( "41017063249" , employeeIT.getCcc());
				assertEquals( "411118345860" , employeeIT.getNss());
				assertEquals( "0111" , employeeIT.getRegime());
				assertEquals( "JUAN JOSE ROSENDO AMATE" , employeeIT.getName().get());
				assertEquals( "047349485Y" , employeeIT.getDni().get());
				assertEquals( ContractLeaveType.ACCIDENTE_LABORAL , employeeIT.getType());
			}
			
		} 
	}

	@Test
	public void testIdcXXV() throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException, ParseException {
		try (InputStream is = IdcTest.class.getResourceAsStream("idcXXV.pdf")) {
			EmployeeITListener employeeITListener = new EmployeeITListener();
			IdcParser.parse(is, employeeITListener);
			Collection<EmployeeIT> employeeIts = employeeITListener.getEmployeeITs();
			
			assertEquals(1, employeeIts.size());
			for (EmployeeIT employeeIT : employeeIts) {
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
				Date dateFrom = simpleDateFormat.parse("09-10-2022");
				Date dateTo = simpleDateFormat.parse("28-01-2023");
				assertEquals( dateFrom , employeeIT.getStartDate());
				assertEquals( dateTo , employeeIT.getEndDate().get());
				assertEquals( "29136287700" , employeeIT.getCcc());
				assertEquals( "111060977833" , employeeIT.getNss());
				assertEquals( "0111" , employeeIT.getRegime());
				assertEquals( "NAZARET CENA ROMERO" , employeeIT.getName().get());
				assertEquals( "032068178Z" , employeeIT.getDni().get());
				assertEquals( ContractLeaveType.MATERNIDAD , employeeIT.getType());
			}
		}
	}
	
	@Test
	public void testIdcX() throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException, ParseException {
		try (InputStream is = IdcTest.class.getResourceAsStream("idcX.pdf")) {
			EmployeeITListener employeeITListener = new EmployeeITListener();
			IdcParser.parse(is, employeeITListener);
			Collection<EmployeeIT> employeeIts = employeeITListener.getEmployeeITs();
			
			List<EmployeeIT> employeeItsList = new ArrayList<>(employeeIts);
			Collections.sort(employeeItsList, ( it1, it2 ) -> it1.getStartDate().compareTo(it2.getStartDate()));
			
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
			Date firstDateFrom = simpleDateFormat.parse("24-08-2020");
			Date firstDateTo = simpleDateFormat.parse("24-08-2020");
			Date secondDateFrom = simpleDateFormat.parse("25-08-2020");
			Date secondDateTo = simpleDateFormat.parse("21-10-2020");
			Date thirdDateFrom = simpleDateFormat.parse("20-05-2021");
			Date thirdDateTo = simpleDateFormat.parse("23-05-2021");
			
			EmployeeIT employeeIt1 = employeeItsList.get(0);
			assertEquals( firstDateFrom , employeeIt1.getStartDate());
			assertEquals( firstDateTo , employeeIt1.getEndDate().get());
			assertEquals( "28220848984" , employeeIt1.getCcc());
			assertEquals( "281521001362" , employeeIt1.getNss());
			assertEquals( "0163" , employeeIt1.getRegime());
			assertEquals( "FRANCIS LEONARDO VASQUEZ AGUILAR" , employeeIt1.getName().get());
			assertEquals( "0Y6672958M" , employeeIt1.getDni().get());
			assertEquals( ContractLeaveType.ACCIDENTE_LABORAL, employeeIt1.getType());
			
			EmployeeIT employeeIt2 = employeeItsList.get(1);
			assertEquals( secondDateFrom , employeeIt2.getStartDate());
			assertEquals( secondDateTo , employeeIt2.getEndDate().get());
			assertEquals( "28220848984" , employeeIt2.getCcc());
			assertEquals( "281521001362" , employeeIt2.getNss());
			assertEquals( "0163" , employeeIt2.getRegime());
			assertEquals( "FRANCIS LEONARDO VASQUEZ AGUILAR" , employeeIt2.getName().get());
			assertEquals( "0Y6672958M" , employeeIt2.getDni().get());
			assertEquals( null, employeeIt2.getType());
			
			EmployeeIT employeeIt3 = employeeItsList.get(2);
			assertEquals( thirdDateFrom , employeeIt3.getStartDate());
			assertEquals( thirdDateTo , employeeIt3.getEndDate().get());
			assertEquals( "28220848984" , employeeIt3.getCcc());
			assertEquals( "281521001362" , employeeIt3.getNss());
			assertEquals( "0163" , employeeIt3.getRegime());
			assertEquals( "FRANCIS LEONARDO VASQUEZ AGUILAR" , employeeIt3.getName().get());
			assertEquals( "0Y6672958M" , employeeIt3.getDni().get());
			assertEquals( ContractLeaveType.ENFERMEDAD_COMUN , employeeIt3.getType());
			
		} 
	}

	@Test
	public void testIdcVII() throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException, ParseException {
		try (InputStream is = IdcTest.class.getResourceAsStream("idcVII.pdf")) {
			EmployeeITListener employeeITListener = new EmployeeITListener();
			IdcParser.parse(is, employeeITListener);
			Collection<EmployeeIT> employeeIts = employeeITListener.getEmployeeITs();

			assertEquals(1, employeeIts.size());
			for (EmployeeIT employeeIT : employeeIts) {
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
				Date dateFrom = simpleDateFormat.parse("20-05-2021");
				assertEquals( dateFrom , employeeIT.getStartDate());
				assertEquals( "05102994184" , employeeIT.getCcc());
				assertEquals( "051009890558" , employeeIT.getNss());
				assertEquals( "0111" , employeeIT.getRegime());
				assertEquals( "IRENE PORTERO SUAREZ" , employeeIT.getName().get());
				assertEquals( "021165158Y" , employeeIT.getDni().get());
				assertEquals( ContractLeaveType.RIESGO_EMBARAZO , employeeIT.getType());
			}
		} 
	}
	
	@Test
	public void testIdcXXXIII() throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException {
		try (InputStream is = IdcTest.class.getResourceAsStream("idcXXXIII.pdf")) {
			EmployeeITListener employeeITListener = new EmployeeITListener();
			IdcParser.parse(is, employeeITListener);
			Collection<EmployeeIT> employeeIts = employeeITListener.getEmployeeITs();

			assertEquals(0, employeeIts.size());
		} 
	}
	
	@Test
	public void testIdcErroneo() throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException {
		try (InputStream is = IdcTest.class.getResourceAsStream("idcErroneo.pdf")) {
			EmployeeITListener employeeITListener = new EmployeeITListener();
			IdcParser.parse(is, employeeITListener);
			Collection<EmployeeIT> employeeIts = employeeITListener.getEmployeeITs();

			assertEquals(0, employeeIts.size());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static java.sql.Date toSQL(java.util.Date date) {
		return date == null ? null : new java.sql.Date(date.getTime());
	}
	
	private static java.sql.Date getFirstDayOf( int year) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, Calendar.JANUARY);

		return  new java.sql.Date(calendar.getTimeInMillis());
	    
	}
	
	private static void assertPECS(Collection<PEC> ssPecs, Date startDate, Date endDate, int size, Predicate<PEC> test ) {
	    PEC [] pecs = 
             ssPecs.stream()
	    .filter( pec -> Objects.equals(pec.getEndDate(),endDate))
	    .filter( pec -> Objects.equals(pec.getStartDate(),startDate))
	    .filter(test)
	    .toArray(PEC[]::new);
	    
	    assertEquals(1, pecs.length);
	    Arrays.stream(pecs).forEach(pec -> assertTrue(test.test(pec)));
	    
	}

}
