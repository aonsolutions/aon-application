package com.esferalia.aon.ui.payroll.file;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;

import com.code.aon.common.ManagerBeanException;
import com.esferalia.aon.file.payroll.afi.data.AYN;
import com.esferalia.aon.file.payroll.afi.data.DOM;
import com.esferalia.aon.file.payroll.afi.data.EMP;
import com.esferalia.aon.file.payroll.afi.data.ETF;
import com.esferalia.aon.file.payroll.afi.data.ETI;
import com.esferalia.aon.file.payroll.afi.data.FAB;
import com.esferalia.aon.file.payroll.afi.data.RZS;
import com.esferalia.aon.file.payroll.afi.data.TRA;

public class AFIReader {
	
	private static String ETI = "ETI";
	private static String EMP = "EMP";
	private static String RZS = "RZS";
	private static String TRA = "TRA";
	private static String AYN = "AYN";
	private static String DOM = "DOM";
	private static String FAB = "FAB";
	private static String ETF = "ETF";
	
	
	private SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd");

	public ETI readFile(InputStream input, String encoding) throws ManagerBeanException, IOException {
		
		ETI eti = null;
		eti = new ETI();
		eti.setEmpresas(new LinkedList<EMP>());
		EMP emp = null;
		RZS rzs = null;
		TRA tra = null;
		AYN ayn = null;
		DOM dom = null;
		FAB fab = null;
		ETF etf = null;
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(input, encoding));
		String currentLine;
		reader.readLine();
		while((currentLine = reader.readLine()) != null) {
			while(currentLine.length()<70){
				currentLine += " ";
			}
			
			try {
				if(currentLine.startsWith(ETI)){
					eti = new ETI();
					eti.setEmpresas(new LinkedList<EMP>());
				} 
				if(currentLine.startsWith(EMP)) {
					emp = new EMP();
					emp.setTrabajadores(new LinkedList<TRA>());
					emp.setCodigoCuentaCotizacionSeguridadSocial(currentLine.substring(3, 18));
					emp.setTipoDocumento(currentLine.substring(18, 19));
					emp.setPais(currentLine.substring(19, 22));
					emp.setNumeroIdentificacion(currentLine.substring(22, 36));
					// se ajusta el nif a 9 caracteres
					emp.setNumeroIdentificacion(emp.getNumeroIdentificacion().substring(emp.getNumeroIdentificacion().length()-9, emp.getNumeroIdentificacion().length()));
					emp.setCalificador(currentLine.substring(36, 38));
					emp.setCodigoCuentaCotizacionPrincipal(currentLine.substring(38, 53));
					eti.getEmpresas().add(emp);
				}
				if(currentLine.startsWith(RZS)) {
					rzs = new RZS();
					rzs.setIndicador(currentLine.substring(3, 4));
					rzs.setTipoAlfabeticoEmpresario(currentLine.substring(4, 5));
					rzs.setRazonSocial(currentLine.substring(5, 60));
					emp.setRzs(rzs);
				}
				if(currentLine.startsWith(TRA)) {
					tra = new TRA();
					tra.setNumeroAfiliacion(currentLine.substring(3, 15));
					tra.setIpf(currentLine.substring(15, 33));
//				private AYN ayn;
//				private DOM dom;
//				private FAB fab;
					emp.getTrabajadores().add(tra);
				} 
				if(currentLine.startsWith(AYN)) {
					ayn = new AYN();
					ayn.setApellido1(currentLine.substring(3, 23));
					ayn.setApellido2(currentLine.substring(23, 43));
					ayn.setNombre(currentLine.substring(43, 59));
					tra.setAyn(ayn);
				} 
				if(currentLine.startsWith(DOM)) {
					dom = new DOM();
					dom.setTipoDomicilio(currentLine.substring(3, 4));
					dom.setTipoVia(currentLine.substring(4, 6));
					dom.setNombreVia(currentLine.substring(6, 42));
					dom.setNumero(currentLine.substring(42, 47));
					dom.setBis(currentLine.substring(47, 49));
					dom.setBloque(currentLine.substring(49, 51));
					dom.setEscalera(currentLine.substring(51, 53));
					dom.setPiso(currentLine.substring(53, 55));
					dom.setPuerta(currentLine.substring(55, 58));
					dom.setMensajeSms(currentLine.substring(58, 67));
					dom.setBorrarSms(currentLine.substring(67, 68));
					tra.setDom(dom);
				} 
				if(currentLine.startsWith(FAB)) {
					fab = new FAB();
					fab.setAccion(currentLine.substring(3, 6));
					fab.setSituacion(currentLine.substring(6, 8));
					try {
						fab.setFechaReal(Integer.parseInt(dateFormatter.format(dateFormatter.parse(currentLine.substring(8, 16)))));
					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					fab.setGrupoCotizacion(Integer.parseInt(currentLine.substring(16, 18)));
					fab.setClaveContrato(Integer.parseInt(currentLine.substring(21, 24)));
					fab.setCondicionDesempleado(currentLine.substring(24, 25));
					fab.setMujerSubrepresentada(currentLine.substring(25, 26));
					fab.setCoeficienteTiempoParcial(currentLine.substring(26, 29));
					fab.setColectivoTrabajador(Integer.parseInt(currentLine.substring(29, 32)));
					fab.setIndicadorImpresion(currentLine.substring(32, 33));
					fab.setCategoriaProfesional(Integer.parseInt(currentLine.substring(33, 40)));
					fab.setFechaNacimiento(currentLine.substring(40, 48));
					fab.setSexo(Integer.parseInt(currentLine.substring(48, 49)));
					fab.setTipoInactividad(currentLine.substring(49, 50));
					fab.setExclusionDesempleo(Integer.parseInt(currentLine.substring(50, 51)));
					fab.setCoeficienteActividadHuelgaParcialEre(Integer.parseInt(currentLine.substring(51, 54)));
					fab.setMujerReincorporada(currentLine.substring(54, 55));
					fab.setIncapacitadoReadmitido(currentLine.substring(55, 56));
					fab.setTrabajadorDeAutonomo(currentLine.substring(56, 57));
					fab.setSemamaSegunConvenio5jr(currentLine.substring(57, 58));
					fab.setIndNumTrabajadoresEmpresa(currentLine.substring(58, 59));
					fab.setExclusionSocialVictimas(Integer.parseInt(currentLine.substring(67, 68)));
					fab.setRentaActivaInsercion(currentLine.substring(68, 69));
					fab.setCostratadasPostAlumbramiento(currentLine.substring(69, 70));
					
					tra.setFab(fab);
				} 
				if(currentLine.startsWith(ETF)) {
					etf = new ETF();
				}
			} catch (StringIndexOutOfBoundsException e) {
				// TODO Auto-generated catch block
//				e.printStackTrace();
			}
		}
		
		return eti;
	}
		
	
}
