package com.esferalia.aon.in.payroll.tgss.idc;

import static com.esferalia.aon.watson.util.AonDateUtils.get;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.OCTOBER;
import static java.util.Calendar.SEPTEMBER;
import static java.util.Calendar.YEAR;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Test;

import com.esferalia.aon.watson.util.AonDateUtils;

public class IdcTest {

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
	

}
