package solutions.aon.seg.social;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.sun.jndi.ldap.Connection;

import solutions.aon.seg.social.SituacionEmpresa.SituacionEmpresaBuilder;

public class SistemaRED_I {
	private static WebClient getWebClient(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType) {
			final WebClient webClient = new WebClient(BrowserVersion.BEST_SUPPORTED);
			webClient.getOptions().setCssEnabled(false);
			webClient.setJavaScriptTimeout(10000);
			webClient.setAjaxController(new NicelyResynchronizingAjaxController());
			webClient.getOptions().setSSLClientCertificate(certificateInputStream, certificatePassword,
					certificateType);
			return webClient;
			
		
	}
	
	/*MÉTODO PARA OBTENER SOLO LOS NÚMEROS
	public static String soloEnteros(String cadena){
		String aux1="";
		for(int i=0;i<cadena.length();i++) {
			if((cadena.charAt(i)>='0')&&(cadena.charAt(i)<='9')) {
				aux1+=cadena.charAt(i);
			}
		}
		return aux1;
	}*/
	public static Boolean toBoolean(String bool) {
		if((bool.equalsIgnoreCase("SI"))||(bool.equalsIgnoreCase("SÍ"))) {
			return true;
		}
		else if(bool.equalsIgnoreCase("NO")) {
			return false;
		}
		return null;
	}
	
	
	public static String getTrimmedById(HtmlPage htmlPage, String id) {
		return removeNBSP(htmlPage.getElementById(id).getTextContent()).trim();
	}
	
	public static String removeInitialZeros(String id) {
		String ret=id;
		if(ret.length()>1){
			for(int i=1;i<ret.length();i++) {
				if(ret.charAt(i)!=ret.charAt(i-1)) {
					while(ret.charAt(0)=='0') {
						ret=ret.substring(1);
					}
					return ret;
				}
			}			
		}
		else {
			if(ret.length()==1) {
				if(ret.equals("0")) {
					return "";
				}
			}
		}
		return "";
	}
	
	public static String removeNBSP(String cadena) {
		String nbe=""+(char)160;
		cadena=cadena.replace(nbe, "");
		return cadena;
		
	}
	
	
	private static SituacionEmpresa getSituacionEmpresa(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String regime, String ccc) throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException, ParseException {
		try(WebClient webClient=getWebClient(certificateInputStream, certificatePassword, certificateType)){
			
			
			
			HtmlPage htmlPage=webClient.getPage("https://w2.seg-social.es/M/menuAFI-REMESAS.html");
			if(htmlPage.getWebResponse().getStatusCode()==403) {
				//throw new ForbidenException();
			}
			//System.out.print(htmlPage.asText());
			
			htmlPage = SistemaRED.wait4(htmlPage, p -> p.getAnchorByHref("/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ACR69&E=I&AP=AFIR")).orElseThrow().click();
			SistemaRED.wait4(htmlPage, p -> p.getElementsById("SDFREGCTA_ayuda"));
			//System.out.print(htmlPage.asXml());
			//System.out.println(htmlPage.getAnchorByHref("/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ACR69&E=I&AP=AFIR"));
			//htmlPage.getElementById("SDFREGCTA_ayuda").setAttribute("value", regime);
			
			HtmlForm jacadaform=SistemaRED.wait4(htmlPage, p -> p.getFormByName("jacadaform")).orElseThrow();
			
			//Separando el ccc
			String ccc1=ccc.substring(0, 2);
			String ccc2=ccc.substring(2);
			
			//htmlPage.getElementById("SDFTESCTA").setAttribute("value", ccc1);
			jacadaform.getInputByName("txt_SDFREGCTA_ayuda").setValueAttribute(regime);
			//htmlPage.getElementById("SDFNUMCTA").setAttribute("value", ccc2);
			jacadaform.getInputByName("txt_SDFTESCTA").setValueAttribute(ccc1);
			jacadaform.getInputByName("txt_SDFNUMCTA").setValueAttribute(ccc2);
			
			//click en 'Continuar'
			
			htmlPage = jacadaform.getInputByValue("Continuar").click();
			
			//System.out.println(htmlPage.asText());
			
			//Pasando los valores al objeto
			
			SituacionEmpresaBuilder seb1=new SituacionEmpresaBuilder();
			
			//DATOS IDENTIFICATIVOS
			seb1.setCcc(getTrimmedById(htmlPage, "SDFPROVINCIA3")
					+getTrimmedById(htmlPage, "SDFNISS3"));
			seb1.setIdEmpresario(getTrimmedById(htmlPage, "SDFTIPO3"));
			
			String nif=getTrimmedById(htmlPage, "SDFEMPRESARIO3");
			
			if(nif.length()>9) {
				nif=removeInitialZeros(nif);
			}
			seb1.setNif_empresa(nif);
			seb1.setRegimen(getTrimmedById(htmlPage, "SDFREGIMEN3"));
			seb1.setNss(getTrimmedById(htmlPage, "SDFPRONAF3")
					+getTrimmedById(htmlPage, "SDFNUMNAF30"));
			seb1.setCccp(getTrimmedById(htmlPage, "SDFPROVINCIACP3"));
			seb1.setUgtgss(getTrimmedById(htmlPage, "SDFTESORERIA3")
					+getTrimmedById(htmlPage, "SDFADMON3")
					+getTrimmedById(htmlPage, "SDFURE3"));
			seb1.setUgtgsscccp(getTrimmedById(htmlPage, "SDFTESPPAL")
					+getTrimmedById(htmlPage, "SDFADMPPAL")
					+getTrimmedById(htmlPage, "SDFUREPPAL"));
			seb1.setUgcentral(getTrimmedById(htmlPage, "SDFUNIDAD"));
			
			//SEGUIR CAMBIANDO AL MÉTODO ESTÁTICO
			
			seb1.setOgism(getTrimmedById(htmlPage, "SDFPROUGISM")
					+getTrimmedById(htmlPage, "SDFLOCUGISM"));
			seb1.setCccAnt(getTrimmedById(htmlPage, "SDFPROVCANT3")
					+getTrimmedById(htmlPage, "SDFNUMCANT3"));
			seb1.setCccSuc(getTrimmedById(htmlPage, "SDFPROSUC3")
					+getTrimmedById(htmlPage, "SDFNUMSUC3")
					+getTrimmedById(htmlPage, "SDFREGSUC3")
					+getTrimmedById(htmlPage, "SDFCCOASUC3"));
			seb1.setSit(getTrimmedById(htmlPage, "SDFSITUACION3")
					+getTrimmedById(htmlPage, "SDFTSITUACION3"));
			String cadFecha=getTrimmedById(htmlPage, "SDFFECHASIT");
			seb1.setfSit(new SimpleDateFormat("dd/MM/yyyy").parse(cadFecha));
			cadFecha=getTrimmedById(htmlPage, "SDFFECHAALTA");
			seb1.setfAltaInicial(new SimpleDateFormat("dd/MM/yyyy").parse(cadFecha));
			
			
			//String aux=removeNonBreakingSpaces(htmlPage.getElementById("SDFNROTRA3").getTextContent()).trim();
			try {
				seb1.setTrabajadorAlta(Integer.parseInt(removeNBSP(htmlPage.getElementById("SDFNROTRA3").getTextContent()).trim()));
			}catch(NumberFormatException e) {
				//TODO: handle exception
			}
				cadFecha=getTrimmedById(htmlPage, "SDFFECHAALTA13");
			seb1.setAltaPrTrab(new SimpleDateFormat("dd/MM/yyyy").parse(cadFecha));
			cadFecha=getTrimmedById(htmlPage, "SDFFECHABAJA93");
			seb1.setUltBajaEfCot(new SimpleDateFormat("dd/MM/yyyy").parse(cadFecha));
			seb1.setTrl(getTrimmedById(htmlPage, "SDFTIPCON")
					+getTrimmedById(htmlPage, "SDFDESTIPCON"));
			seb1.setcEsp_num(getTrimmedById(htmlPage, "SDFCOLECTIVO3"));
			seb1.setcEsp_cad(getTrimmedById(htmlPage, "SDFDESCOL3"));
			seb1.setCnae09_num(getTrimmedById(htmlPage, "SDFACTIV093"));
			seb1.setCnae93_num(getTrimmedById(htmlPage, "SDFACTIV933"));
			seb1.setCnae09_cad(getTrimmedById(htmlPage, "SDFTACTIV093"));
			seb1.setCnae93_cad(getTrimmedById(htmlPage, "SDFTACTIV933"));
			//System.out.println(htmlPage.getElementById("SDFCCONDIAS3").getTextContent().trim());
			try {
				seb1.setTa2Alta(Integer.parseInt(getTrimmedById(htmlPage, "SDFCCONDIAS3")));
			}catch (NumberFormatException e) {
				// TODO: handle exception
			}
			try {
				seb1.setTa2Baja(Integer.parseInt(getTrimmedById(htmlPage, "SDFCCONDPPB3")));
			}catch (NumberFormatException e) {
				// TODO: handle exception
			}
			
			
			
			
			try {
				seb1.setTiposATyEPIT(Float.parseFloat(getTrimmedById(htmlPage, "SDFTITN12").replace(",", ".")));
			}catch (NumberFormatException e) {
				// TODO: handle exception
			}
			try {
				seb1.setIms(Float.parseFloat(getTrimmedById(htmlPage, "SDFTIMSN12").replace(",", ".")));
			}
			catch (NumberFormatException e) {
			// TODO: handle exception
			}
			try {
				seb1.setTotal(Float.parseFloat(getTrimmedById(htmlPage, "SDFTTOTN12").replace(",", ".")));
			}
			catch (NumberFormatException e) {
				// TODO: handle exception
			}
			seb1.setCoeJub_num(getTrimmedById(htmlPage, "SDFCOREJU"));
			seb1.setCoeJub_cad(getTrimmedById(htmlPage, "SDFDSCOREJU"));
			seb1.setAconExtra(getTrimmedById(htmlPage, "SDFACTMEXTR")
					+getTrimmedById(htmlPage, "SDFDSACONT"));
			String esctaller=getTrimmedById(htmlPage, "SDFCESTAL");
			Boolean booltaller=null;
			if(esctaller.equalsIgnoreCase("NO")){
				booltaller=false;
			}
			else if(!esctaller.equalsIgnoreCase("")) {
				booltaller=true;
			}
			
			seb1.setEscTaller(booltaller);
			seb1.setAutorizacionRed(getTrimmedById(htmlPage, "SDFAUTORIDRED"));
			cadFecha=getTrimmedById(htmlPage, "SDFPLAZORED");
			seb1.setPlazoIncorpRed(new SimpleDateFormat("dd/MM/yyyy").parse(cadFecha));
			cadFecha=getTrimmedById(htmlPage, "SDFFECHAAUTRED");
			seb1.setFechaAutCan(new SimpleDateFormat("dd/MM/yyyy").parse(cadFecha));
			
			
			
			
			
		/*		PRUEBA CARACTER RARO EN UN CAMPO
		 * 
		 * 
		 * String prueba=htmlPage.getElementById("SDFNROTRA3").getTextContent();
			
			
			System.out.println(prueba.contains(" "));
			prueba=prueba.replace(" ","");
			
			System.out.println(prueba.length());
		for(int i=0;i<prueba.length();i++) {
			System.out.println((int)(prueba.charAt(i)));
		}
			String str=""+((char)160);
			System.out.println(str);
			System.out.println(prueba.contains(str));*/
			
			//Datos identificativos
			jacadaform=htmlPage.getFormByName("jacadaform");
			
			htmlPage=jacadaform.getInputByValue("Datos Iden.").click();
			SistemaRED.wait4(htmlPage, p -> p.getElementById("SDFLOCALIDAD4"));
			
			seb1.setAnagrama(getTrimmedById(htmlPage, "SDFANAGR3"));
			seb1.setEmbarcacion(getTrimmedById(htmlPage, "SDFTIPEMB")+
					getTrimmedById(htmlPage, "SDFEMB")+
					getTrimmedById(htmlPage, "SDFNOMEMBAR"));
			seb1.setTlfMovil(getTrimmedById(htmlPage, "SDFTELMOVIL3"));
			seb1.setTlfFijo(getTrimmedById(htmlPage, "SDFTELFIJO3"));
			seb1.setEmail(getTrimmedById(htmlPage, "txtconcat1_1"));
			
			
			
			//DIRECCIÓN DE LA EMPRESA
			seb1.setNotif_dom_empresa(toBoolean(getTrimmedById(htmlPage, "SDFNOTIF3")));
			seb1.setTipo_via_dir_empresa(getTrimmedById(htmlPage, "SDFVIA3"));
			seb1.setDir_emp_calle(getTrimmedById(htmlPage, "SDFDOMICILIO3"));
			seb1.setDir_emp_num(getTrimmedById(htmlPage, "SDFNUMERO3"));
			seb1.setDir_emp_bis(getTrimmedById(htmlPage, "SDFBIS3"));
			seb1.setDir_emp_bloq(getTrimmedById(htmlPage, "SDFBLOQUE3"));
			seb1.setDir_emp_es(getTrimmedById(htmlPage, "SDFESCALERA3"));
			seb1.setDir_emp_piso(getTrimmedById(htmlPage, "SDFPISO3"));
			seb1.setDir_emp_p(getTrimmedById(htmlPage, "SDFPUERTA3"));
			seb1.setDir_emp_CP(getTrimmedById(htmlPage, "SDFPOSTAL3"));
			seb1.setDir_emp_num_muni(getTrimmedById(htmlPage, "SDFLOCALIDAD13"));
			seb1.setDir_emp_nom_muni(getTrimmedById(htmlPage, "SDFLOCALIDAD23"));
			seb1.setDir_emp_tlf(getTrimmedById(htmlPage, "SDFNUM9TELEFONO3"));
			
			//DIRECCIÓN DE ACTIVIDAD
			seb1.setNotif_dom_actividad(toBoolean(getTrimmedById(htmlPage, "SDFNOTIF4")));
			seb1.setAct_ugtgss(getTrimmedById(htmlPage, "SDFTESORERIA3")
					+getTrimmedById(htmlPage, "SDFADMON3")
					+getTrimmedById(htmlPage, "SDFURE3"));
			seb1.setTipo_via_dir_actividad(getTrimmedById(htmlPage, "SDFVIA4"));
			seb1.setDir_act_calle(getTrimmedById(htmlPage, "SDFDOMICILIO4"));
			seb1.setDir_act_num(getTrimmedById(htmlPage, "SDFNUMERO3"));
			seb1.setDir_act_bis(getTrimmedById(htmlPage, "SDFBIS4"));
			seb1.setDir_act_bloq(getTrimmedById(htmlPage, "SDFBLOQUE4"));
			seb1.setDir_act_es(getTrimmedById(htmlPage, "SDFESCALERA4"));
			seb1.setDir_act_piso(getTrimmedById(htmlPage, "SDFPISO4"));
			seb1.setDir_act_p(getTrimmedById(htmlPage, "SDFPUERTA4"));
			seb1.setDir_act_CP(getTrimmedById(htmlPage, "SDFPOSTAL4"));
			seb1.setDir_act_num_muni(getTrimmedById(htmlPage, "SDFMUNICIPIO4"));
			seb1.setDir_act_nom_muni(getTrimmedById(htmlPage, "SDFLOCALIDAD4"));
			seb1.setDir_act_tlf(getTrimmedById(htmlPage, "SDFNUM9TELEFONO4"));
			
			
			return seb1.build();
		}
		
	}
	public static void main(String[] args)
			throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException, ParseException {
		try (final InputStream certificateInputStream = new FileInputStream(args[0])) {
			System.out.println(getSituacionEmpresa(certificateInputStream, "jg@FNMT", "pkcs12", "0111", "01105360062"));
		}
		
		
		
	}
	
}
