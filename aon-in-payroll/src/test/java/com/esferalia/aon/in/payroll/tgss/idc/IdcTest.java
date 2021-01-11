package com.esferalia.aon.in.payroll.tgss.idc;

import static com.esferalia.aon.jooq.tables.DeductionConcept.DEDUCTION_CONCEPT;
import static com.esferalia.aon.jooq.tables.SystemDeduction.SYSTEM_DEDUCTION;
import static com.esferalia.aon.jooq.tables.SystemPayment.SYSTEM_PAYMENT;
import static com.esferalia.aon.watson.util.AonDateUtils.get;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfYear;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.OCTOBER;
import static java.util.Calendar.SEPTEMBER;
import static java.util.Calendar.YEAR;
import static net.aonsolutions.core.tgss.creta.jaxb.Utils.marshal;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.junit.Ignore;
import org.junit.Test;

import com.esferalia.aon.in.payroll.pdf.UnknownPDFException;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.jooq.tables.records.DeductionConceptRecord;
import com.esferalia.aon.jooq.tables.records.PaymentConceptRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBonus;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.SalaryCost;
import com.esferalia.aon.payroll.SalaryData;
import com.esferalia.aon.payroll.SalaryPayment;
import com.esferalia.aon.payroll.calculator.SmartContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.sql.AbstractSQLTestCase;
import com.esferalia.aon.payroll.calculator.sql.ISQLContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.SSRegimeType;
import com.esferalia.aon.payroll.tgss.creta.IndentXMLStreamWriter;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.enumeration.DeductionType;
import com.esferalia.aon.salary.expression.ExpressionException;
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
		try ( InputStream is = IdcTest.class.getResourceAsStream("idcplccc.pdf") ){
			IdcplcccParser.parse(is, new IdcListener() {
				
				@Override
				public void onPeriod(Date date) {
					assertEquals("PERIODO DE LIQUIDACIÓN:", 2020, get(date, YEAR) );
					assertEquals("PERIODO DE LIQUIDACIÓN:", OCTOBER, get(date, MONTH ) );
				}
				
				@Override
				public void onEnterprise(String socialReason, String ccc, String nif, String economicActivityCode,
						String economicActivityDescription, String regime, String fullCCC) {
					
					assertEquals("RAZÓN SOCIAL:","AON SOLUTIONS S.L.", socialReason);
					assertEquals("C.C.C:","01105360062", ccc);
					assertEquals("DNI/NIE/CIF:","B01487271", nif);
					
					assertEquals("ACT ECONÓMICA:","6209", economicActivityCode);
					
				}
				
				@Override
				public void onEmployee(String nss, String name) {
					Map<String, String> NSS_NAME_MAP = 
							new HashMap<String, String>();
					NSS_NAME_MAP.put("010019805355", "ANA DIAZ PEREZ");
					NSS_NAME_MAP.put("011001022503", "EUGENIO CASTELLANO HURTADO");
					NSS_NAME_MAP.put("011005185924", "RAUL TREPIANA ZARATE");
					NSS_NAME_MAP.put("011006256964", "SHEILA RUESGAS GARCIA");
					NSS_NAME_MAP.put("011007308507", "PATRICIA COCA FUENTES");
					NSS_NAME_MAP.put("011011187190", "ANDER IBAÑEZ DE GAUNA NAVAZO");
					NSS_NAME_MAP.put("281468615302", "SERGIO VALDEPEÑAS DEL POZO");
					NSS_NAME_MAP.put("291136796369", "RAY DE JESUS VASQUEZ BEAUPERTHUY");
					
					assertEquals("NSS:",NSS_NAME_MAP.get(nss), name);
				}
				
				@Override
				public void onEmployeePerido(String nss, String ccc, Date startDate, Date endDate) {
					
					Date _1October2020 = getDate(1,Calendar.OCTOBER, 2020);
					Date _31October2020 = getDate(31,Calendar.OCTOBER, 2020);
					
					assertEquals("FECHA DESDE", _1October2020, startDate );
					assertEquals("FECHA HASTA", _31October2020, endDate );
					
					//System.out.println(nss + " " + startDate + " " + endDate );
					
				}
				
				@Override
				public void onEmployeeQuotePEC(String nss, String ccc, String pec, String description,
						String portTipo, String quota, Date startDate, Date endDate) {
					assertEquals("NSS:","291136796369", nss);
					assertEquals("C.C.C:","01105360062", ccc);
					assertEquals("TIPO DE PECULIARIDAD","04", pec);

					Map<String, Double> QUOTA_POR_TIPO_MAP = 
							new HashMap<String, Double>();
					QUOTA_POR_TIPO_MAP.put("05", 0.05);
					QUOTA_POR_TIPO_MAP.put("02", 1.20);
					
					NumberFormat numberFormat = DecimalFormat.getNumberInstance(new Locale("es","ES"));
					try {
						assertEquals("POR/TIPO",QUOTA_POR_TIPO_MAP.get(quota), numberFormat.parse(portTipo) );
					} catch (ParseException e) {
						fail(e.getMessage());
					}
				
					Date _1October2020 = getDate(1,Calendar.OCTOBER, 2020);
					Date _31October2020 = getDate(31,Calendar.OCTOBER, 2020);
					
					assertEquals("FECHA DESDE", _1October2020, startDate );
					assertEquals("FECHA HASTA", _31October2020, endDate );
				}

				
			});
		}
	}

	@Test
	public void testIdcplcccI() throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException {
		try ( InputStream is = IdcTest.class.getResourceAsStream("idcplcccI.pdf") ){
			IdcplcccParser.parse(is, new IdcListener() {
				
				@Override
				public void onPeriod(Date date) {
					assertEquals("PERIODO DE LIQUIDACIÓN:", 2020, get(date, YEAR) );
					assertEquals("PERIODO DE LIQUIDACIÓN:", OCTOBER, get(date, MONTH ) );
				}
				
				@Override
				public void onEnterprise(String socialReason, String ccc, String nif, String economicActivityCode,
						String economicActivityDescription, String regime, String fullCCC) {
					
					assertEquals("RAZÓN SOCIAL:","AON SOLUTIONS S.L.", socialReason);
					assertEquals("C.C.C:","01105577910", ccc);
					assertEquals("DNI/NIE/CIF:","B01487271", nif);
					
					assertEquals("ACT ECONÓMICA:","6209", economicActivityCode);
					
				}
				
				@Override
				public void onEmployee(String nss, String name) {
					Map<String, String> NSS_NAME_MAP = 
							new HashMap<String, String>();
					NSS_NAME_MAP.put("011006286569", "ESTHER ARANDA MARTIN");
					NSS_NAME_MAP.put("011017250195", "IKER GONZALEZ DIAZ");
					NSS_NAME_MAP.put("011021493543", "AKETZA EGUSQUIZA VAZQUEZ");
					NSS_NAME_MAP.put("141026133260", "MARIA LUISA BLASCO DE PORRES");
					
					assertEquals("NSS:",NSS_NAME_MAP.get(nss), name);
				}
				
				@Override
				public void onEmployeePerido(String nss, String ccc, Date startDate, Date endDate) {
					
					Date _1October2020 = getDate(1,Calendar.OCTOBER, 2020);
					Date _31October2020 = getDate(31,Calendar.OCTOBER, 2020);
					
					assertEquals("FECHA DESDE", _1October2020, startDate );
					assertEquals("FECHA HASTA", _31October2020, endDate );
				}
				
				@Override
				public void onEmployeeQuotePEC(String nss, String ccc, String pec, String description,
						String portTipo, String quota, Date startDate, Date endDate) {

					Map<String, String> PEC_QUOTA_MAP = 
							new HashMap<String, String>();
					PEC_QUOTA_MAP.put("01", "68");
					PEC_QUOTA_MAP.put("09", "53");
					
					assertEquals("TIPO DE PECULIARIDAD FRACCIÓN DE CUOTA:",PEC_QUOTA_MAP.get(pec), quota);
					
					NumberFormat numberFormat = DecimalFormat.getNumberInstance(new Locale("es","ES"));
					try {
						assertEquals("POR/TIPO",100.00, numberFormat.parse(portTipo).doubleValue() , 0.00);
					} catch (ParseException e) {
						fail(e.getMessage());
					}
				
					Date _1October2020 = getDate(1,Calendar.OCTOBER, 2020);
					Date _31October2020 = getDate(31,Calendar.OCTOBER, 2020);
					
					assertEquals("FECHA DESDE", _1October2020, startDate );
					assertEquals("FECHA HASTA", _31October2020, endDate );
				}

				
			});
		}
	}
	
	@Test
	@Ignore
	public void testIdcplnss() throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException {
		try ( InputStream is = IdcTest.class.getResourceAsStream("idcplnss.pdf") ){
			IdcplnssParser.parse(is, new IdcListener() {
				
				@Override
				public void onPeriod(Date date) {
					assertEquals("PERIODO SOLICITADO:", 2020, get(date, YEAR) );
					assertEquals("PERIODO SOLICITADO:", SEPTEMBER, get(date, MONTH ) );
				}
				
				@Override
				public void onEnterprise(String socialReason, String ccc, String nif, String economicActivityCode,
						String economicActivityDescription, String regime, String fullCCC) {
					assertEquals("C.C.C:","37106820136", ccc);
					assertEquals("RAZÓN SOCIAL:","EDUCAMOS SALAMANCA, S.L.", socialReason);
				}
				
				@Override
				public void onEmployee(String nss, String name) {
					assertEquals("CCC SOLICITADO:","371013120530", nss);
					assertEquals("NOMBRE Y APELLIDOS:","JOANA VAQUERO GARCIA", name);
				}
				
				@Override
				public void onEmployeePerido(String nss, String ccc, Date startDate, Date endDate) {
					
					Date _3July2020 = getDate(3,Calendar.JULY, 2020);
					Date _14September2020 = getDate(14,Calendar.SEPTEMBER, 2020);
					Date _15September2020 = getDate(15,Calendar.SEPTEMBER, 2020);
					Date _30September2020 = getDate(30,Calendar.SEPTEMBER, 2020);
					
					if ( startDate.equals(_3July2020 ))
						assertEquals("FECHA HASTA", _14September2020, endDate );
					else if ( startDate.equals(_15September2020 ))
						assertEquals("FECHA HASTA", _30September2020, endDate );
					else
						fail();

				}
				
				@Override
				public void onEmployeeQuotePEC(String nss, String ccc, String pec, String description,
						String portTipo, String quota, Date startDate, Date endDate) {
					assertEquals("NSS:","371013120530", nss);
					assertEquals("C.C.C:","37106820136", ccc);
					
					ArrayList<PEC> PECS = new ArrayList<PEC>(6);
					PECS.add(new PEC("23", getDate(1, Calendar.SEPTEMBER, 2020), getDate(7, Calendar.SEPTEMBER, 2020), 100.00, "57"));

					PECS.add(new PEC("37", getDate(3, Calendar.JULY, 2020), getDate(31, Calendar.JULY, 2020), 60.00, "01"));
					PECS.add(new PEC("15", getDate(3, Calendar.JULY, 2020), getDate(31, Calendar.JULY, 2020), 35.00, "57"));
					PECS.add(new PEC("18", getDate(3, Calendar.JULY, 2020), getDate(31, Calendar.JULY, 2020), 100.00, "08"));
					
					PECS.add(new PEC("37", getDate(1, Calendar.AUGUST, 2020), getDate(31, Calendar.AUGUST, 2020), 60.00, "01"));
					PECS.add(new PEC("15", getDate(1, Calendar.AUGUST, 2020), getDate(31, Calendar.AUGUST, 2020), 35.00, "57"));
					PECS.add(new PEC("18", getDate(1, Calendar.AUGUST, 2020), getDate(31, Calendar.AUGUST, 2020), 100.00, "08"));

					PECS.add(new PEC("37", getDate(1, Calendar.SEPTEMBER, 2020), getDate(14, Calendar.SEPTEMBER, 2020), 60.00, "01"));
					PECS.add(new PEC("15", getDate(1, Calendar.SEPTEMBER, 2020), getDate(14, Calendar.SEPTEMBER, 2020), 35.00, "57"));
					PECS.add(new PEC("18", getDate(1, Calendar.SEPTEMBER, 2020), getDate(14, Calendar.SEPTEMBER, 2020), 100.00, "08"));

					PECS.add(new PEC("37", getDate(15, Calendar.SEPTEMBER, 2020), getDate(30, Calendar.SEPTEMBER, 2020), 60.00, "01"));

					try {
						PEC actual = new PEC(pec, startDate, endDate, DecimalFormat.getNumberInstance(new Locale("es","ES")).parse(portTipo).doubleValue() , quota);
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
		try ( InputStream is = IdcTest.class.getResourceAsStream("idc.pdf") ){
			IdcParser.parse(is, new IdcListener() {
				
				@Override
				public void onPeriod(Date date) {
					assertEquals( "PERIODO:", getDate(14, Calendar.MAY, 2020), date );
				}
				
				@Override
				public void onEnterprise(String socialReason, String ccc, String cif, String economicActivityCode,
						String economicActivityDescription, String regime, String fullCCC) {
					
					assertEquals("RAZÓN SOCIAL:","SOUTHWEST GOLF S.L.", socialReason);
					assertEquals("C.C.C:","11112501771", ccc);
					assertEquals("DNI/NIE/CIF:","B85729648", cif);					
					assertEquals("ACT ECONÓMICA:","9311", economicActivityCode);
					assertEquals("C.C.C","011111112501771", fullCCC);
					
				}
				
				@Override
				public void onEmployee(String nss, String name) {
					assertEquals("NSS:","081053913352", nss);
					assertEquals("NOMBRE Y APELLIDOS:","MARC JOVE JOVE", name);
				}
				
				@Override
				public void onEmployeePerido(String nss, String ccc, Date startDate, Date endDate) {
					assertEquals("NSS:","081053913352", nss);
					assertEquals("C.C.C:","11112501771", ccc);
					if ( startDate.equals(getDate(14, Calendar.MAY, 2020)))
							assertEquals(getDate(31, Calendar.MAY, 2020), endDate);
					else if ( startDate.equals(getDate(1, Calendar.JUNE, 2020)))
						assertEquals(getDate(30, Calendar.JUNE, 2020), endDate);
					else 
						fail();
					
				}
				
				
				@Override
				public void onEmployeeQuotePEC(String nss, String ccc, String pec, String description,
						String portTipo, String quota, Date startDate, Date endDate) {
					assertEquals("NSS:","081053913352", nss);
					assertEquals("C.C.C:","11112501771", ccc);
					
					ArrayList<PEC> PECS = new ArrayList<PEC>(6);
					PECS.add(new PEC("37", getDate(14, Calendar.MAY, 2020), getDate(31, Calendar.MAY, 2020), 85.00, "01"));
					PECS.add(new PEC("15", getDate(14, Calendar.MAY, 2020), getDate(31, Calendar.MAY, 2020), 60.00, "57"));
					PECS.add(new PEC("18", getDate(14, Calendar.MAY, 2020), getDate(31, Calendar.MAY, 2020), 100.00, "08"));
					
					PECS.add(new PEC("37", getDate(1, Calendar.JUNE, 2020), getDate(30, Calendar.JUNE, 2020), 70.00, "01"));
					PECS.add(new PEC("15", getDate(1, Calendar.JUNE, 2020), getDate(30, Calendar.JUNE, 2020), 45.00, "57"));
					PECS.add(new PEC("18", getDate(1, Calendar.JUNE, 2020), getDate(30, Calendar.JUNE, 2020), 100.00, "08"));
					
					try {
						PEC actual = new PEC(pec, startDate, endDate, DecimalFormat.getNumberInstance(new Locale("es","ES")).parse(portTipo).doubleValue() , quota);
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
	public void testIdcII() throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException {
		try ( InputStream is = IdcTest.class.getResourceAsStream("idcII.pdf") ){
			IdcParser.parse(is, new IdcListener() {
				

				@Override
				public void onEnterprise(String socialReason, String ccc, String cif, String economicActivityCode,
						String economicActivityDescription, String regime, String fullCCC) {
					
					assertEquals("RAZÓN SOCIAL:","AON SOLUTIONS S.L.", socialReason);
					assertEquals("C.C.C:","01105360062", ccc);
					assertEquals("DNI/NIE/CIF:","B01487271", cif);
					
					assertEquals("ACT ECONÓMICA:","6209", economicActivityCode);
					
					
				}
				
				@Override
				public void onEmployee(String nss, String name) {
					assertEquals("NSS:","011005185924", nss);
					assertEquals("NOMBRE Y APELLIDOS:","RAUL TREPIANA ZARATE", name);
				}
				
				@Override
				public void onEmployeePerido(String nss, String ccc, Date startDate, Date endDate) {
				}
				
				
				@Override
				public void onEmployeeQuotePEC(String nss, String ccc, String pec, String description,
						String portTipo, String quota, Date startDate, Date endDate) {
						fail("Unknow PEC");
				}

				
			});
		}
	}

	@Test
	public void testIdcplcccBonus0() throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException {
		try ( InputStream is = IdcTest.class.getResourceAsStream("idcplccc.pdf") ){
			Collection<Bonus> ssBonuses = Idcplccc.getSSBonuses(is);
			assertEquals(0, ssBonuses.size());
		}
	}

	@Test
	public void testIdcplcccBonus() throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException {
		try ( InputStream is = IdcTest.class.getResourceAsStream("idcplcccI.pdf") ){
			Collection<Bonus> ssBonuses = Idcplccc.getSSBonuses(is);
			assertEquals(8, ssBonuses.size());
			assertEquals(4, ssBonuses.stream().filter(b -> b.isEmployee()).count());
			assertEquals(4, ssBonuses.stream().filter(b -> b.isEnterprise()).count());
			ssBonuses.forEach( b -> System.out.println(b));
		}
	}

	@Test
	public void testIdcplnssIVBonusI() throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException, ExpressionException, SQLException, SalaryException {
		
		try ( InputStream is = IdcTest.class.getResourceAsStream("idcplnssIV.pdf") ){
			Collection<Bonus> ssBonuses = Idcplnss.getSSBonuses(is);
			assertEquals(1, ssBonuses.size());
			//EXONE.ERE.F.MAY.COMP (100,00%) 01-12-2020 10-12-2020
			
			
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.DAY_OF_MONTH,1);
			calendar.set(Calendar.MONTH,Calendar.DECEMBER);
			calendar.set(Calendar.YEAR,2020);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			Date _01122020 = calendar.getTime();
			
			calendar.set(Calendar.DAY_OF_MONTH,10);
			Date _10122020 = calendar.getTime();
			
			calendar.set(Calendar.DAY_OF_MONTH,11);
			Date _11122020 = calendar.getTime();

			calendar.set(Calendar.DAY_OF_MONTH,31);
			Date _31122020 = calendar.getTime();
			
			Data ereFactor = new Data() { {
				expression = "1.0";
				endDate = _10122020;
				startDate = _01122020;
				name = ContextVariable.ERE_FACTOR_FORCE_OFF.getName();
			}
			};
			Salary salary = calculate(ssBonuses, Collections.singleton(ereFactor));	
			
			for ( ContextVariable var : new ContextVariable [] {
					ContextVariable.CGC_BASE,
					ContextVariable.CGP_BASE,
					}) {
				SalaryData[] salaryData = 
				salary.getSalaryDatas().stream()
				.filter( d->AonStringUtils.equals(d.getName(), var.getName()))
				.sorted((d1,d2)-> d1.getStartDate().compareTo(d2.getStartDate()))
				.toArray( SalaryData[]::new );
				
				assertEquals(var.getName(),1, salaryData.length);
				assertEquals(var.getName(),_11122020, salaryData[0].getStartDate());
				assertEquals(var.getName(),_31122020, salaryData[0].getEndDate());
			}
			
			for ( ContextVariable var : new ContextVariable [] {
					ContextVariable.CGC_BASE_ENTERPRISE,
					ContextVariable.CGP_BASE_ENTERPRISE,
					}) {
				SalaryData[] salaryData = 
				salary.getSalaryDatas().stream()
				.filter( d->AonStringUtils.equals(d.getName(), var.getName()))
				.sorted((d1,d2)-> d1.getStartDate().compareTo(d2.getStartDate()))
				.toArray( SalaryData[]::new );
				
				assertEquals(var.getName(),2, salaryData.length);
				assertEquals(var.getName(),_01122020, salaryData[0].getStartDate());
				assertEquals(var.getName(),_10122020, salaryData[0].getEndDate());
				
				assertEquals(var.getName(),_11122020, salaryData[1].getStartDate());
				assertEquals(var.getName(),_31122020, salaryData[1].getEndDate());
			}

			for (SalaryPayment payment : salary.getSalaryPayments()) {
				System.out.println( payment.getDescription() + ": " + payment.getAmount() + ", " + payment.getQuote());
			}
			
			double totalCost = 0.00;
			for (SalaryCost cost : salary.getSalaryCosts()) {
				totalCost += cost.getAmount();
				System.out.println( cost.getName() + ": " + cost.getAmount() );
			}
			
			assertEquals(1, salary.getSalaryBonus().size());
			double totalBonus = 0.00;
			for (SalaryBonus bonus : salary.getSalaryBonus()) {
				totalBonus += bonus.getAmount();
				System.out.println( bonus.getDescription() + ": " + bonus.getAmount() );
			}
			
			System.out.println("CUOTA EMPRESARIAL :" + totalCost );
			
			assertEquals(totalCost * 9 / 30, totalBonus, DELTA);

			assertEquals(totalCost * 21 / 30, salary.getTotalEnterprise(), DELTA);
		}
	}
	
	@Test
	public void testIdcplnssIVBonusII() throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException, ExpressionException, SQLException, SalaryException {
		
		try ( InputStream is = IdcTest.class.getResourceAsStream("idcplnssIV.pdf") ){
			Collection<Bonus> ssBonuses = Idcplnss.getSSBonuses(is);
			assertEquals(1, ssBonuses.size());
			//EXONE.ERE.F.MAY.COMP (100,00%) 01-12-2020 10-12-2020
			
			
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.DAY_OF_MONTH,1);
			calendar.set(Calendar.MONTH,Calendar.DECEMBER);
			calendar.set(Calendar.YEAR,2020);
			calendar.set(Calendar.HOUR, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			Date _01122020 = calendar.getTime();
			
			calendar.set(Calendar.DAY_OF_MONTH,10);
			Date _10122020 = calendar.getTime();
			
			calendar.set(Calendar.DAY_OF_MONTH,11);
			Date _11122020 = calendar.getTime();

			calendar.set(Calendar.DAY_OF_MONTH,31);
			Date _31122020 = calendar.getTime();
			

			Salary salary = calculate(ssBonuses);	
			
			for ( ContextVariable var : new ContextVariable [] {
					ContextVariable.CGC_BASE,
					ContextVariable.CGP_BASE,
					
//					ContextVariable.CGC_EMPLOYEE,
//					ContextVariable.CGC_ENTERPRISE,
//					ContextVariable.FP_EMPLOYEE,
//					ContextVariable.FP_ENTERPRISE,
//					
//					ContextVariable.EMPLOYEE_QUOTA,
//					ContextVariable.ENTERPRISE_QUOTA,
					}) {
				SalaryData[] salaryData = 
				salary.getSalaryDatas().stream()
				.filter( d->AonStringUtils.equals(d.getName(), var.getName()))
				.sorted((d1,d2)-> d1.getStartDate().compareTo(d2.getStartDate()))
				.toArray( SalaryData[]::new );
				
				assertEquals(var.getName(),2, salaryData.length);
				assertEquals(var.getName(),_01122020, salaryData[0].getStartDate());
				assertEquals(var.getName(),_10122020, salaryData[0].getEndDate());
				
				assertEquals(var.getName(),_11122020, salaryData[1].getStartDate());
				assertEquals(var.getName(),_31122020, salaryData[1].getEndDate());
			}
			
			

			
			
			
			double totalCost = 0.00;
			for (SalaryCost cost : salary.getSalaryCosts()) {
				totalCost += cost.getAmount();
				System.out.println( cost.getName() + ": " + cost.getAmount() );
			}
			
			assertEquals(1, salary.getSalaryBonus().size());
			double totalBonus = 0.00;
			for (SalaryBonus bonus : salary.getSalaryBonus()) {
				totalBonus += bonus.getAmount();
				System.out.println( bonus.getDescription() + ": " + bonus.getAmount() );
			}
			
			System.out.println("CUOTA EMPRESARIAL :" + totalCost );
			
			assertEquals(totalCost * 10 / 30, totalBonus, DELTA);

			assertEquals(totalCost * 20 / 30, salary.getTotalEnterprise(), DELTA);
		}
	}

	@Test
	@Ignore
	public void testIdcplnssVBonusI() throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException, ExpressionException, SQLException, SalaryException {
		testIdcplnssVBonus(ContextVariable.ERE_FACTOR);
	}
	
	@Test
	@Ignore
	public void testIdcplnssVBonusII() throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException, ExpressionException, SQLException, SalaryException {
		testIdcplnssVBonus(ContextVariable.ERE_FACTOR_FORCE);
	}
	
	@Test
	public void testIdcplnssVBonusIII() throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException, ExpressionException, SQLException, SalaryException {
		testIdcplnssVBonus(ContextVariable.ERE_FACTOR_FORCE_OFF);
	}

	public void testIdcplnssVBonus(ContextVariable ereFactorVariable) throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException, ExpressionException, SQLException, SalaryException {
		
		try ( InputStream is = IdcTest.class.getResourceAsStream("idcplnssV.pdf") ){
			Collection<Bonus> ssBonuses = Idcplnss.getSSBonuses(is);
			assertEquals(2, ssBonuses.size());
			//EXONE.ERE.F.MAY.COMP (100,00%) 01-12-2020 10-12-2020
			//EXONE.ERE.F.MAY.PARC ( 55,00%)
			
			
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.DAY_OF_MONTH,1);
			calendar.set(Calendar.MONTH,Calendar.DECEMBER);
			calendar.set(Calendar.YEAR,2020);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			Date _01122020 = calendar.getTime();
			
			calendar.set(Calendar.DAY_OF_MONTH,10);
			Date _10122020 = calendar.getTime();
			
			calendar.set(Calendar.DAY_OF_MONTH,11);
			Date _11122020 = calendar.getTime();

			calendar.set(Calendar.DAY_OF_MONTH,31);
			Date _31122020 = calendar.getTime();
			
			Collection<Data> ereFactors = new ArrayList<Data>() ;
			ereFactors.add(new Data() { {
				expression = "1.00";
				endDate = _10122020;
				startDate = _01122020;
				name = ereFactorVariable.getName();
			}
			});
			ereFactors.add(new Data() { {
				expression = "0.55";
				endDate = _31122020;
				startDate = _11122020;
				name = ereFactorVariable.getName();
			}
			});
			
			Salary salary = calculate(ssBonuses, ereFactors);	
			
			for ( ContextVariable var : new ContextVariable [] {
					ContextVariable.CGC_BASE,
					ContextVariable.CGP_BASE,
					}) {
				SalaryData[] salaryData = 
				salary.getSalaryDatas().stream()
				.filter( d->AonStringUtils.equals(d.getName(), var.getName()))
				.sorted((d1,d2)-> d1.getStartDate().compareTo(d2.getStartDate()))
				.toArray( SalaryData[]::new );
				
				assertEquals(var.getName(),1, salaryData.length);
				assertEquals(var.getName(),_11122020, salaryData[0].getStartDate());
				assertEquals(var.getName(),_31122020, salaryData[0].getEndDate());
			}
			
			for ( ContextVariable var : new ContextVariable [] {
					ContextVariable.CGC_BASE_ENTERPRISE,
					ContextVariable.CGP_BASE_ENTERPRISE,
					}) {
				SalaryData[] salaryData = 
				salary.getSalaryDatas().stream()
				.filter( d->AonStringUtils.equals(d.getName(), var.getName()))
				.sorted((d1,d2)-> d1.getStartDate().compareTo(d2.getStartDate()))
				.toArray( SalaryData[]::new );
				
				assertEquals(var.getName(),2, salaryData.length);
				assertEquals(var.getName(),_01122020, salaryData[0].getStartDate());
				assertEquals(var.getName(),_10122020, salaryData[0].getEndDate());
				
				assertEquals(var.getName(),_11122020, salaryData[1].getStartDate());
				assertEquals(var.getName(),_31122020, salaryData[1].getEndDate());
			}

			for (SalaryPayment payment : salary.getSalaryPayments()) {
				System.out.println( payment.getDescription() + ": " + payment.getAmount() + ", " + payment.getQuote());
			}
			
			double totalCost = 0.00;
			for (SalaryCost cost : salary.getSalaryCosts()) {
				totalCost += cost.getAmount();
				System.out.println( cost.getName() + ": " + cost.getAmount() );
			}
			
			assertEquals(2, salary.getSalaryBonus().size());
			double totalBonus = 0.00;
			for (SalaryBonus bonus : salary.getSalaryBonus()) {
				totalBonus += bonus.getAmount();
				System.out.println( bonus.getDescription() + ": " + bonus.getAmount() );
			}
			
			System.out.println("CUOTA EMPRESARIAL :" + totalCost );
			
			assertEquals(totalCost * 9 / 30 + totalCost * 21 / 30 * 0.55, totalBonus, DELTA);

			assertEquals(totalCost * 21 / 30 * 0.45, salary.getTotalEnterprise(), DELTA);
		}
	}
	
	
	
	protected Salary calculate(Collection<Bonus> ssBonuses) throws ExpressionException, SQLException, SalaryException {
		return calculate(ssBonuses, Collections.emptyList());
	}
		
	protected Salary calculate(Collection<Bonus> ssBonuses,Collection<Data> datas) throws ExpressionException, SQLException, SalaryException {
		Date bonusDate = ssBonuses.stream().map( b -> b.getStartDate()).sorted().findFirst().orElseThrow();
		
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		ContractRecord contract = newContract(aonContext, toSQL(bonusDate) , ssBonuses, datas);
		
		java.sql.Date startDate = toSQL(AonDateUtils.getFirstDayOfMonth(bonusDate));
		java.sql.Date endDate = toSQL(AonDateUtils.getLastDayOfMonth(bonusDate));
		
		ISQLContractSalaryCalculatorContext ctx = 
		getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);
		SmartContractSalaryCalculator<Salary> builder = 
		new SmartContractSalaryCalculator<Salary>(new SalaryBuilder());
		Salary salary = builder.calculate(ctx);
		return salary;
	}

	@Test
	public void testIdcplnssIIIBonus() throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException, ExpressionException, SalaryException, SQLException {
		
		try ( InputStream is = IdcTest.class.getResourceAsStream("idcplnssIII.pdf") ){
			Collection<Bonus> ssBonuses = Idcplnss.getSSBonuses(is);
			assertEquals(1, ssBonuses.size());
			//EXONE.ERE.F.MAY.COMP (100,00%)
			

			Salary salary = calculate(ssBonuses);		
			assertEquals(0.00, salary.getTotalEnterprise(), DELTA);
			
			
			double totalCost = 0.00;
			for (SalaryCost cost : salary.getSalaryCosts()) {
				totalCost += cost.getAmount();
				System.out.println( cost.getName() + ": " + cost.getAmount() );
			}
			
			assertEquals(1, salary.getSalaryBonus().size());
			double totalBonus = 0.00;
			for (SalaryBonus bonus : salary.getSalaryBonus()) {
				totalBonus += bonus.getAmount();
				System.out.println( bonus.getDescription() + ": " + bonus.getAmount() );
			}

			assertEquals(totalCost, totalBonus, DELTA);

		}
	}

	@Test
	public void testIdcplnssIBonus() throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException {
		try ( InputStream is = IdcTest.class.getResourceAsStream("idcplnssI.pdf") ){
			Collection<Bonus> ssBonuses = Idcplnss.getSSBonuses(is);
			assertEquals(0, ssBonuses.size());
		}
	}

	@Test
	public void testIdcplnssIIBonus() throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException, ExpressionException, SalaryException, SQLException {
		try ( InputStream is = IdcTest.class.getResourceAsStream("idcplnssII.pdf") ){
			Collection<Bonus> ssBonuses = Idcplnss.getSSBonuses(is);
			assertEquals(2, ssBonuses.size());
			assertEquals(1, ssBonuses.stream().filter(b -> b.isEmployee()).count());
			assertEquals(1, ssBonuses.stream().filter(b -> b.isEnterprise()).count());
			
			Salary salary = calculate(ssBonuses);
			
			System.out.println( salary.getTotalEnterprise() );
			
		}
	}

	@Test
	public void testIdcplcccTrabajadoresTramos() throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException, JAXBException {
		try ( InputStream is = IdcTest.class.getResourceAsStream("idcplccc.pdf") ){
			TrabajadoresTramos trabajadoresTramos = Idcplccc.geTrabajadoresTramos(is, new TrabajadoresTramosCallback() {});
			
			marshal(trabajadoresTramos, System.out);
			
			Liquidacion liquidacion = trabajadoresTramos.getLiquidacion();
			
			assertEquals("0111", liquidacion.getCcc().getRegimen());
			assertEquals("01", liquidacion.getCcc().getProvincia());
			assertEquals("105360062", liquidacion.getCcc().getNumero());
			
			assertEquals("10", liquidacion.getPeriodoDesde().getMes());
			assertEquals("2020", liquidacion.getPeriodoDesde().getAnho());
			assertEquals("10", liquidacion.getPeriodoHasta().getMes());
			assertEquals("2020", liquidacion.getPeriodoHasta().getAnho());
			
			assertEquals(1,liquidacion.getLiquidacionMes().size());
			
			LiquidacionMes liquidacionesMes = liquidacion.getLiquidacionMes().get(0);
			assertEquals("10", liquidacionesMes.getMesLiquidativo().getMes());
			assertEquals("2020", liquidacionesMes.getMesLiquidativo().getAnho());
			
			Trabajadores trabajadores = liquidacionesMes.getTrabajadores();
			assertEquals(8, trabajadores.getTrabajador().size() );
			
			for ( Trabajador trabajador : trabajadores.getTrabajador() ) {

				assertEquals(1, trabajador.getTramos().getTramo().size() );			
				for ( Tramo tramo : trabajador.getTramos().getTramo() ) {
					assertEquals("01", tramo.getFechaDesde().getDia());
					assertEquals("10", tramo.getFechaDesde().getMes());
					assertEquals("2020", tramo.getFechaDesde().getAnho());
					assertEquals("31", tramo.getFechaHasta().getDia());
					assertEquals("10", tramo.getFechaHasta().getMes());
					assertEquals("2020", tramo.getFechaHasta().getAnho());
					
					assertTramoActivoNormal(tramo);
				}
			}
			
		}
	}

	@Test
	public void testIdcplcccTrabajadoresTramosI() throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException, JAXBException {
		try ( InputStream is = IdcTest.class.getResourceAsStream("idcplcccI.pdf") ){
			TrabajadoresTramos trabajadoresTramos = Idcplccc.geTrabajadoresTramos(is, new TrabajadoresTramosCallback() {
				@Override
				public boolean isScholarEmployee(String ssNum, String ccc, Date start, Date end) {
					return true;
				}
			});
			
			marshall(trabajadoresTramos, System.out);
			
			Liquidacion liquidacion = trabajadoresTramos.getLiquidacion();
			
			assertEquals("0111", liquidacion.getCcc().getRegimen());
			assertEquals("01", liquidacion.getCcc().getProvincia());
			assertEquals("105577910", liquidacion.getCcc().getNumero());
			
			assertEquals("10", liquidacion.getPeriodoDesde().getMes());
			assertEquals("2020", liquidacion.getPeriodoDesde().getAnho());
			assertEquals("10", liquidacion.getPeriodoHasta().getMes());
			assertEquals("2020", liquidacion.getPeriodoHasta().getAnho());
			
			assertEquals(1,liquidacion.getLiquidacionMes().size());
			
			LiquidacionMes liquidacionesMes = liquidacion.getLiquidacionMes().get(0);
			assertEquals("10", liquidacionesMes.getMesLiquidativo().getMes());
			assertEquals("2020", liquidacionesMes.getMesLiquidativo().getAnho());
			
			Trabajadores trabajadores = liquidacionesMes.getTrabajadores();
			assertEquals(4, trabajadores.getTrabajador().size() );
			
			for ( Trabajador trabajador : trabajadores.getTrabajador() ) {
				assertEquals(1, trabajador.getTramos().getTramo().size() );	
				for ( Tramo tramo : trabajador.getTramos().getTramo() ) {
					assertEquals(0, tramo.getDatosTramo().getDato().size());		
				}
				
			}
			
		}
	}
	
	@Test
	public void testIdcContractData() throws IOException, UnknownPDFException {
		try ( InputStream is = IdcTest.class.getResourceAsStream("idc.pdf") ){
			Map<ContextVariable, Object> contractData = Idc.getContractData(is);
			org.junit.Assert.assertEquals(contractData.get(ContextVariable.TC2), "100");
			org.junit.Assert.assertEquals(contractData.get(ContextVariable.QUOTE_GROUP), "08");			
		}
	}

	@Test
	public void testIdcContractDataI() throws IOException, UnknownPDFException {
		try ( InputStream is = IdcTest.class.getResourceAsStream("idcI.pdf") ){
			Map<ContextVariable, Object> contractData = Idc.getContractData(is);
			org.junit.Assert.assertEquals(contractData.get(ContextVariable.TC2), "189");
			org.junit.Assert.assertEquals(contractData.get(ContextVariable.QUOTE_GROUP), "10");			
		}
	}

	@Test
	public void testIdcContractDataII() throws IOException, UnknownPDFException {
		try ( InputStream is = IdcTest.class.getResourceAsStream("idcII.pdf") ){
			Map<ContextVariable, Object> contractData = Idc.getContractData(is);
			org.junit.Assert.assertEquals(contractData.get(ContextVariable.TC2), "100");
			org.junit.Assert.assertEquals(contractData.get(ContextVariable.QUOTE_GROUP), "01");			
			org.junit.Assert.assertEquals(contractData.get(ContextVariable.OCCUPATION), "a");			
		}
	}

	@Test
	public void testIdcContractDataIII() throws IOException, UnknownPDFException {
		try ( InputStream is = IdcTest.class.getResourceAsStream("idcIII.pdf") ){
			Map<ContextVariable, Object> contractData = Idc.getContractData(is);
			org.junit.Assert.assertEquals(contractData.get(ContextVariable.TC2), "289");
			org.junit.Assert.assertEquals(contractData.get(ContextVariable.QUOTE_GROUP), "07");			
			org.junit.Assert.assertEquals(contractData.get(ContextVariable.PARTIAL_FACTOR), 0.750 );			
		}
	}

	@Test
	public void testIdcContractDataIV() throws IOException, UnknownPDFException {
		try ( InputStream is = IdcTest.class.getResourceAsStream("idcIV.pdf") ){
			Map<ContextVariable, Object> contractData = Idc.getContractData(is);
			org.junit.Assert.assertEquals(contractData.get(ContextVariable.TC2), "100");
			org.junit.Assert.assertEquals(contractData.get(ContextVariable.QUOTE_GROUP), "02");			
		}
	}

	@Test
	public void testIdcplcccTrabajadoresTramosII() throws com.esferalia.aon.in.payroll.pdf.UnknownPDFException, IOException, JAXBException {
		try ( InputStream is = IdcTest.class.getResourceAsStream("idcplcccII.pdf") ){
			TrabajadoresTramos trabajadoresTramos = Idcplccc.geTrabajadoresTramos(is, new TrabajadoresTramosCallback() {
				@Override
				public boolean isScholarEmployee(String ssNum, String ccc, Date start, Date end) {
					return true;
				}
			});
			
			marshall(trabajadoresTramos, System.out);
			
			Liquidacion liquidacion = trabajadoresTramos.getLiquidacion();
			
			assertEquals("0111", liquidacion.getCcc().getRegimen());
			assertEquals("01", liquidacion.getCcc().getProvincia());
			assertEquals("105577910", liquidacion.getCcc().getNumero());
			
			assertEquals("10", liquidacion.getPeriodoDesde().getMes());
			assertEquals("2020", liquidacion.getPeriodoDesde().getAnho());
			assertEquals("10", liquidacion.getPeriodoHasta().getMes());
			assertEquals("2020", liquidacion.getPeriodoHasta().getAnho());
			
			assertEquals(1,liquidacion.getLiquidacionMes().size());
			
			LiquidacionMes liquidacionesMes = liquidacion.getLiquidacionMes().get(0);
			assertEquals("10", liquidacionesMes.getMesLiquidativo().getMes());
			assertEquals("2020", liquidacionesMes.getMesLiquidativo().getAnho());
			
			Trabajadores trabajadores = liquidacionesMes.getTrabajadores();
			assertEquals(4, trabajadores.getTrabajador().size() );
			
			for ( Trabajador trabajador : trabajadores.getTrabajador() ) {
				if ( "141026133260".equals(trabajador.getNaf())) 
					continue;
				assertEquals(1, trabajador.getTramos().getTramo().size() );			
				for ( Tramo tramo : trabajador.getTramos().getTramo() ) {
					assertEquals(0, tramo.getDatosTramo().getDato().size());		
				}
			}
			
			for ( Trabajador trabajador : trabajadores.getTrabajador() ) {
				if ( !"141026133260".equals(trabajador.getNaf())) 
					continue;
				assertEquals(3, trabajador.getTramos().getTramo().size() );		
				assertTramoITPagoDelegadoBecarios(trabajador.getTramos().getTramo().get(1));
			}
			
			
		}
	}

	private static class PEC {
		private String pec;
		private Date startDate;
		private Date endDate;
		private double portTipo ;
		private String quota;
		
		public PEC(String pec, Date startDate, Date endDate, double portTipo, String quota) {
			super();
			this.pec = pec;
			this.startDate = startDate;
			this.endDate = endDate;
			this.portTipo = portTipo;
			this.quota = quota;
		}
		
		@Override
		public boolean equals(Object obj) {
			PEC other = ( PEC ) obj;
			return pec.equals(other.pec) 
				&& startDate.equals(other.startDate)
				&& endDate.equals(other.endDate)
				&& portTipo == other.portTipo
				&& quota.equals(other.quota)
				;
		}
		
	}
	
//	private static Date resetTime(Date date) {
//		if (date == null)
//			return null;
//
//		Calendar cal = Calendar.getInstance();
//		cal.setTime(date);
//
//		// Set time fields to zero
//		cal.set(Calendar.HOUR, 0);
//		cal.set(Calendar.MINUTE, 0);
//		cal.set(Calendar.SECOND, 0);
//		cal.set(Calendar.MILLISECOND, 0);
//
//		// Put iterator back in the Date object
//		return cal.getTime();
//	}
	
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
		calendar.set(Calendar.HOUR_OF_DAY,0);
		calendar.set(Calendar.MINUTE,0);
		calendar.set(Calendar.SECOND,0);
		calendar.set(Calendar.MILLISECOND,0);
		calendar.set(Calendar.YEAR,year);
		calendar.set(Calendar.DATE,day);
		calendar.set(Calendar.MONTH,month);
		return calendar.getTime();
	}
	
	private static void assertTramoITPagoDirecto(Tramo tramo) {
		List<DatoSolicitado> datoSolicitados = tramo.getDatosTramo().getDatoSolicitado();
		
		assertDatosSolicitado(datoSolicitados, "C", "509", "B");
		try {
			assertDatosSolicitado(datoSolicitados, "C", "603", "B");
		} catch ( AssertException e ) {
			assertDatosSolicitado(datoSolicitados, "C", "613", "B");
		}
	}

	private static void assertTramoIT15PrimerosDias(Tramo tramo) {
		List<DatoSolicitado> datoSolicitados = tramo.getDatosTramo().getDatoSolicitado();
		
		assertDatosSolicitado(datoSolicitados, "C", "500", "B");
		try {
			assertDatosSolicitado(datoSolicitados, "C", "603", "B");
		} catch ( AssertException e ) {
			assertDatosSolicitado(datoSolicitados, "C", "613", "B");
		}
	}
	private static void assertTramoIT15PrimerosDiasDiario(Tramo tramo) {
		assertTramoIT15PrimerosDias(tramo);
		List<DatoSolicitado> datoSolicitados = tramo.getDatosTramo().getDatoSolicitado();
		assertDatosSolicitado(datoSolicitados, "I", "51", "P");
	}

	private static void assertTramoITPagoDelegado(Tramo tramo) {
		assertTramoIT15PrimerosDias(tramo);
		List<DatoSolicitado> datoSolicitados = tramo.getDatosTramo().getDatoSolicitado();
		assertDatosSolicitado(datoSolicitados, "C", "563", "B");
	}

	private static void assertTramoITPagoDelegadoBecarios(Tramo tramo) {
		List<DatoSolicitado> datoSolicitados = tramo.getDatosTramo().getDatoSolicitado();
		assertEquals(1, datoSolicitados.size());
		assertDatosSolicitado(datoSolicitados, "C", "663", "B");
	}

	private static void assertTramoITATEPPagoDelegado(Tramo tramo) {
		assertTramoIT15PrimerosDias(tramo);
		List<DatoSolicitado> datoSolicitados = tramo.getDatosTramo().getDatoSolicitado();
		assertDatosSolicitado(datoSolicitados, "C", "663", "B");
	}

	private static void assertTramoActivoNormal(Tramo tramo) {
		List<DatoSolicitado> datoSolicitados = tramo.getDatosTramo().getDatoSolicitado();
		
		assertDatosSolicitado(datoSolicitados, "C", "500", "B");
		assertDatosSolicitado(datoSolicitados, "C", "501", "P");
		assertDatosSolicitado(datoSolicitados, "C", "502", "P");
		try {
			assertDatosSolicitado(datoSolicitados, "C", "601", "B");
		} catch ( AssertException e ) {
			assertDatosSolicitado(datoSolicitados, "C", "611", "B");
		}
	}
	
	private static void assertDatosSolicitado( List<DatoSolicitado> datoSolicitados, String tipoDato, String codigo, String  indicadorObligatoriedad) {
		for ( DatoSolicitado datoSolicitado: datoSolicitados ){
			if ( datoSolicitado.getCodigo().equals(codigo) ) {
				assertEquals(tipoDato, datoSolicitado.getTipoDato());
				assertEquals(indicadorObligatoriedad, datoSolicitado.getIndicadorObligatoriedad());
				return;
			}
		}
		
		throw new AssertException("Dato Solicitado " + codigo + " Not Found");
	}
	
	private ContractRecord newContract(AONContext aonContext, java.sql.Date startDate, Collection<Bonus> ssBonuses) {
		return newContract(aonContext, startDate, ssBonuses, Collections.emptyList());
	}
	
	private ContractRecord newContract(AONContext aonContext, java.sql.Date startDate, Collection<Bonus> ssBonuses, Collection<Data> datas) {
		
		cleanSystemData(aonContext);
		cleanSystemCosts(aonContext);
		cleanDeductionConcepts(aonContext);
		cleanSystemDeductions(aonContext);
		
		
		addSystemData(aonContext, startDate, null, new HashMap<String,String>(){
			{
				put("PORCENTAJE_FP", "0.10");
				put("PORCENTAJE_FP_E", "0.60");
				
				put("PORCENTAJE_CGC", "4.70");
				put("PORCENTAJE_CGC_E", "23.60");
				
				put("OCUPACION_IT", "["
						+ "\"h\": 1.40]");
				put("OCUPACION_IMS", "["
						+ "\"h\": 2.20]");
				
				put("PORCENTAJE_DESMPL", "[ "
						+ "\"100\": 1.55"
						+ "][TC2]");
				put("PORCENTAJE_DESMPL_E", "[ "
						+ "\"100\": 5.50"
						+ "][TC2]");

				put("PORCENTAJE_FOGASA", "0.20");
			}
			});
		
		addSSRegimeCost(aonContext, SSRegimeType.GENERAL,
				getFirstDayOfYear(startDate), "CGC_E",
				DeductionType.COMMON_CONTINGENCY, "BASE_CGC_E * PORCENTAJE_CGC_E/100");
		addSSRegimeCost(aonContext, SSRegimeType.GENERAL,
				getFirstDayOfYear(startDate), "IT_E",
				DeductionType.PROFESSIONAL_CONTINGENCY, "BASE_CGP_E * (isdef PORCENTAJE_IT ? PORCENTAJE_IT : (PORCENTAJE_IT=( isdef OCUPACION ? OCUPACION_IT[OCUPACION] : TARIFA_IT)))/100");
		addSSRegimeCost(aonContext, SSRegimeType.GENERAL,
				getFirstDayOfYear(startDate), "IMS_E",
				DeductionType.PROFESSIONAL_CONTINGENCY, "BASE_CGP_E * (isdef PORCENTAJE_IMS ? PORCENTAJE_IMS : (PORCENTAJE_IMS=( isdef OCUPACION ? OCUPACION_IMS[OCUPACION] : TARIFA_IMS)))/100");
		addSSRegimeCost(aonContext, SSRegimeType.GENERAL,
				getFirstDayOfYear(startDate), "DESEMPL_E",
				DeductionType.UNEMPLOYMENT, "(PORCENTAJE_DESMPL == 0) ? 0.00 : ( BASE_CGP_E * ( isdef PORCENTAJE_DESMPL_E ? PORCENTAJE_DESMPL_E : PORCENTAJE_DESMPL_E=(INDEFINIDO ? 5.50 : (TIEMPO_COMPLETO ? 6.70 : 7.70)))/100)");
		addSSRegimeCost(aonContext, SSRegimeType.GENERAL,
				getFirstDayOfYear(startDate), "FOGASA_E",
				DeductionType.FOGASA, "BASE_CGP_E * PORCENTAJE_FOGASA / 100");
		addSSRegimeCost(aonContext, SSRegimeType.GENERAL,
				getFirstDayOfYear(startDate), "FP_E",
				DeductionType.FOGASA, "BASE_CGP_E * PORCENTAJE_FP_E/100");
		
		
		
		DeductionConceptRecord fpConcept = addDeductionConcept(aonContext, "FP", DeductionType.COMMON_CONTINGENCY);
		DeductionConceptRecord cgcConcept = addDeductionConcept(aonContext, "CGC", DeductionType.COMMON_CONTINGENCY);
		DeductionConceptRecord desmplConcept = addDeductionConcept(aonContext, "DESMPL", DeductionType.COMMON_CONTINGENCY);
		
		addSSRegimeDeduction(aonContext, fpConcept, SSRegimeType.GENERAL, startDate, "BASE_CGC * PORCENTAJE_FP/100");
		addSSRegimeDeduction(aonContext, cgcConcept, SSRegimeType.GENERAL, startDate, "BASE_CGC * PORCENTAJE_CGC/100");
		addSSRegimeDeduction(aonContext, desmplConcept, SSRegimeType.GENERAL, startDate, "BASE_CGC * PORCENTAJE_DESMPL/100");
		
		ContractRecord contract = newContract(
				aonContext, 
				getFirstDayOfYear(startDate),
				new HashMap<String,String>(){
				{
					put(ContextVariable.OCCUPATION.getName(), "'h'");
					put(ContextVariable.TC2.getName(), "'100'");
					put(ContextVariable.MONTH_DAYS.getName(), "30.00");
				}
				},
				new String[] { 
						"1000.00 * DIAS_TRABAJADOS / DIAS_MES",
						"500.00*DIAS_TRABAJADOS/DIAS_MES",
						}, 
				new String[] {
						
				},
				null
			);	
		
		PaymentConceptRecord ereConcept = addConcept(aonContext, "ERE");
		addPayment(aonContext, 
		contract, 
		ereConcept, 
		"/*read-only*/DIAS_ERE * 0.00/**/", 
		"DIAS_ERE * BASE_REGULADORA");

		PaymentConceptRecord ereFzaConcept = addConcept(aonContext, "ERE_FZA");
		addPayment(aonContext, 
		contract, 
		ereFzaConcept, 
		"/*read-only*/DIAS_ERE_FZA * 0.00/**/", 
		"DIAS_ERE_FZA * BASE_REGULADORA");
		
		PaymentConceptRecord ereFzaExoneradoConcept = addConcept(aonContext, "ERE_FZA_EXONERADO");
		addPayment(aonContext, 
		contract, 
		ereFzaExoneradoConcept, 
		"/*read-only*/DIAS_ERE_FZA_EXONERADO * 0.00/**/", 
		"DIAS_ERE_FZA_EXONERADO * BASE_REGULADORA");

		datas.forEach( d-> addData(aonContext, contract, toSQL(d.startDate), toSQL(d.endDate), d.name, d.expression));
		
		ssBonuses.forEach( b -> addBonus(aonContext, contract, toSQL(b.getStartDate()), toSQL(b.getEndDate()), b.getFormula(), b.getDescription()) );
		
		return contract;
	}
	
	protected final void cleanDeductionConcepts(AONContext aonContext) {
		aonContext.getDslContext().execute("SET FOREIGN_KEY_CHECKS=0");

		aonContext.getDslContext().delete(DEDUCTION_CONCEPT).execute();

		aonContext.getDslContext().execute("SET FOREIGN_KEY_CHECKS=1");
	}
	
	public static final DeductionConceptRecord addDeductionConcept(AONContext aonContext, String code, DeductionType type) {
		aonContext.getDslContext().execute("SET FOREIGN_KEY_CHECKS=0");
		DeductionConceptRecord deductionConceptRecord = 
		aonContext.getDslContext()
				.insertInto(DEDUCTION_CONCEPT)
				.set(DEDUCTION_CONCEPT.DOMAIN,0)
				.set(DEDUCTION_CONCEPT.CODE, code)
				.set(DEDUCTION_CONCEPT.TYPE, (byte) type.ordinal())
				.set(DEDUCTION_CONCEPT.DESCRIPTION, code)
				.returning()
				.fetchOne();
		aonContext.getDslContext().execute("SET FOREIGN_KEY_CHECKS=1");
		return deductionConceptRecord;
	}

	protected final void addSSRegimeDeduction(AONContext aonContext, DeductionConceptRecord concept, SSRegimeType ssRegimetype, java.sql.Date startDate,
			String expression) {
		aonContext.getDslContext().execute("SET FOREIGN_KEY_CHECKS=0");

		aonContext.getDslContext()
				.insertInto(SYSTEM_DEDUCTION)
				.set(SYSTEM_DEDUCTION.START_DATE, startDate)
				.set(SYSTEM_DEDUCTION.DOMAIN, (-1) * ssRegimetype.ordinal())
				.set(SYSTEM_DEDUCTION.DEDUCTION_CONCEPT, concept.getId())
				.set(SYSTEM_DEDUCTION.EXPRESSION, expression)
				.execute();

		aonContext.getDslContext().execute("SET FOREIGN_KEY_CHECKS=1");
	}
	
	protected final void addSystemPayment(AONContext aonContext, PaymentConceptRecord concept, java.sql.Date startDate,
			String description, String expression, String quoteExpression, String irpfExpression ) {
		aonContext.getDslContext().execute("SET FOREIGN_KEY_CHECKS=0");

		aonContext.getDslContext()
				.insertInto(SYSTEM_PAYMENT)
				.set(SYSTEM_PAYMENT.DOMAIN, 0)
				.set(SYSTEM_PAYMENT.START_DATE, startDate)
				.set(SYSTEM_PAYMENT.PAYMENT_CONCEPT, concept.getId())
				.set(SYSTEM_PAYMENT.DESCRIPTION, description)
				.set(SYSTEM_PAYMENT.EXPRESSION, expression)
				.set(SYSTEM_PAYMENT.IRPF_EXPRESSION, irpfExpression)
				.set(SYSTEM_PAYMENT.QUOTE_EXPRESSION, quoteExpression)
				.execute();

		aonContext.getDslContext().execute("SET FOREIGN_KEY_CHECKS=1");
	}

	private static java.sql.Date toSQL(java.util.Date date) {
		return date == null ? null : new java.sql.Date(date.getTime());
	}
}
