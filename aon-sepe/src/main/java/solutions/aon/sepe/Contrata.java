package solutions.aon.sepe;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;

import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;

import solutions.aon.sepe.toolkit.HtmlUnitToolkit;
import solutions.aon.sepe.toolkit.Toolkit;

public class Contrata {

	private static void contratoPdfImpl(final InputStream certificateInputStream, 
			final String certificatePassword, final String certificateType, String ipf, Date fini, Date fend ) throws Exception  {
		
	    try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)) {
	    	
	    	String[] fri = Toolkit.formatDate(fini);
	    	String[] fre = Toolkit.formatDate(fend);
	    	String[] fnow =  Toolkit.formatDate(new Date());
		    Integer ident  = 0; //NIF DEFAULT
		    if(identity(ipf).equals("6")) ident = 1; // NIE
		    
		    System.out.println(fri[0] + "/" + fri[1]+ "/" +fri[2]);
		    System.out.println(fre[0] + "-" + fre[1]+ "-" +fre[2]);
			System.out.println(ipf);
			
	    	HtmlPage htmlPage = first_page_sepe_contrata(webClient);
			
	        htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/actionLogin.do?pagina=consultas").click(); 
	        htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/comunicacto/jsp/menu_consultasImpresion.jsp?origen=").click();
	        htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/servlet/ServletRegresar?ruta=menu_consultasgeneral&origen=").click();
	        htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/servlet/ServletConsultaEmpresa?pagina=idtrabajador&origen=").click();//por identificador del trabajador
				  
			HtmlForm formDatos = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("datos")).orElseThrow();
			HtmlOption option = (HtmlOption)  formDatos.querySelectorAll("select[name=tipodoc2]>option").get(ident);				
			option.click();
		
			formDatos.getInputByName("nifnietrabajador").setValueAttribute(ipf);
			formDatos.getInputByName("diadesde").setValueAttribute(fri[0]);
			formDatos.getInputByName("mesdesde").setValueAttribute(fri[1]);
			formDatos.getInputByName("anniodesde").setValueAttribute(fri[2]);
			formDatos.getInputByName("desde").setValueAttribute(fri[0] + "/" + fri[1]+ "/" +fri[2]);
			
//			formDatos.getInputByName("hoy").setValueAttribute(fnow[0] + "/" + fnow[1]+ "/" +fnow[2]);
//			formDatos.getInputByName("DIA").setValueAttribute(fnow[0]);
//			formDatos.getInputByName("MES").setValueAttribute(fnow[1]);
//			formDatos.getInputByName("ANYO").setValueAttribute(fnow[2]);
			
			formDatos.getInputByName("diahasta").setValueAttribute(fre[0]);
			formDatos.getInputByName("meshasta").setValueAttribute(fre[1]);
			formDatos.getInputByName("anniohasta").setValueAttribute(fre[2]);
			
//			formDatos.getInputByName("hasta").setValueAttribute(fre[0] + "/" + fre[1]+ "/" +fre[2]);
		
			htmlPage = formDatos.getInputByName("aceptar").click();
			
			System.out.println(htmlPage.asXml());
  
		} 
	}
	
	private static HtmlPage first_page_sepe_contrata(WebClient webClient) throws FailingHttpStatusCodeException, MalformedURLException, IOException {
	      HtmlPage htmlPage = webClient.getPage("https://www.sepe.es:444/ccomunicacto/servlet/ServletInicio?CCAA=99&idioma=14");
		  return htmlPage;
	}
	
	
	private static String identity(String ipf) {
		ipf = Toolkit.removeExtraZeros(ipf);
		Pattern nif  = Pattern.compile(
				//  -------- LEGAL_PERSON_NIF PATTERN  
				// -------- (1) --> X00000000
					"^[A-JUV]"
					+"[\\s-_/]?"
					+"[0-9]{2}"
					+"[-_/\\.]?"
					+"[0-9]{3}"
					+"[-_/\\.]?"
					+"[0-9]{3}$"
					, Pattern.MULTILINE|Pattern.CASE_INSENSITIVE);
		Pattern dni  = Pattern.compile(
					"[0-9]?"
					+"[0-9]"
					+"[\\s-_/\\.]?"
					+"[0-9]{3}"
					+"[\\s-_/\\.]?"
					+"[0-9]{3}"
					+"[\\s-_/]?"
					+"[A-Z]"
					, Pattern.MULTILINE|Pattern.CASE_INSENSITIVE);
				//  -------- NIE PATTERN 
				// -------- (1) --> X0000000X
		Pattern nie  = Pattern.compile(
					"[XYZ]"
					+"[\\s-_/]?"
					+"[0-9]{7}"
					+"[\\s-_/]?"
					+"[A-HJ-NP-TV-Z]"
				, Pattern.MULTILINE|Pattern.CASE_INSENSITIVE);
		
		Map<Pattern, Integer> patterns = new HashMap<Pattern, Integer>();
		patterns.put(nif, 1);
		patterns.put(dni, 1);
		patterns.put(nie, 6);
		
		String identity = "";
		for (Entry<Pattern, Integer> entry : patterns.entrySet()) {
			if ( entry.getKey().matcher(ipf).matches()) { identity = entry.getValue().toString(); break; }
		}
		return identity;
	}
		
	private static void handleSepeExceptions(HtmlPage htmlPage) throws Exception{
		try {
			String error = htmlPage.querySelector("#contenido > form > p.formAviso").getVisibleText();
			if(!error.isEmpty()) {
				throw new Exception(error);
			}
		} catch (NullPointerException e) {}
	}
	

	public static void main(String[] args)  {
		try (final FileInputStream certificateInputStream =  new FileInputStream("src/test/resources/solutions/aon/SEPE.p12")) {	
			
				String certificatePassword = "aon@FNMT";
				String certificateType = "pkcs12";
//				String regimen = "0111";
//		     	String ctaCti = "01105360062";
//		     	String nss = "291136796369";
				String ipf = "Y7514970X";
//				String grup_ctz = "01";
				Date fini = Toolkit.parseDate("09-09-2020", "dd-MM-yyyy");
				Date fend = Toolkit.parseDate("09-09-2020", "dd-MM-yyyy");
				contratoPdfImpl(certificateInputStream, certificatePassword, certificateType, ipf, fini, fend);
		} 			  
		catch (Exception e) {
			System.out.println("errors>>" + e.getMessage());
//			e.printStackTrace();
		}
	}



}
