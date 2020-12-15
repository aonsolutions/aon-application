package com.esferalia.aon.in.payroll.tgss.idc;

import static com.esferalia.aon.watson.util.AonDateUtils.get;
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
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.junit.Test;

import com.esferalia.aon.in.payroll.pdf.UnknownPDFException;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.tgss.creta.IndentXMLStreamWriter;
import com.mchange.util.AssertException;

import junit.framework.Assert;
import net.aonsolutions.core.tgss.creta.jaxb.Utils;
import net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.DatoSolicitado;
import net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.Liquidacion;
import net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.LiquidacionMes;
import net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.Trabajador;
import net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.Trabajadores;
import net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.TrabajadoresTramos;
import net.aonsolutions.core.tgss.creta.jaxb.trabajadorestramos.Tramo;

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

}
