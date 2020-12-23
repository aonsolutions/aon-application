package solutions.aon.sepe;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Date;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.UnexpectedPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableCell;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import solutions.aon.sepe.exceptions.SepeException;
import solutions.aon.sepe.toolkit.HtmlUnitToolkit;
import solutions.aon.sepe.toolkit.Toolkit;

public class Contrato {
	
	public static byte[] contratoPdf(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, String ipf, Date fini, Date fend) throws Exception {
			return contratoPdfImpl(certificateInputStream, certificatePassword, certificateType, ipf, fini, fend);
	}
	
	public static byte[] getCopyBasicPdf(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String ipf, Date fini, Date fend) throws Exception {
			return getCopyBasicPdfImpl(certificateInputStream, certificatePassword, certificateType, ipf, fini, fend);
	}
	
	public static void anulacionContrato(final InputStream certificateInputStream, final String certificatePassword, 
			final String certificateType, String ide) throws SepeException {
		  try {
			anulacionContratoImpl(certificateInputStream, certificatePassword, certificateType, ide);
		} catch (FailingHttpStatusCodeException | ElementNotFoundException | SepeException | IOException
				| InterruptedException e) {
			throw new SepeException(e);
		}
	}
	
	public static void anulacionTransformation(final InputStream certificateInputStream, final String certificatePassword, 
			final String certificateType, String ide) throws SepeException{
		try {
			anulacionTransformationImpl(certificateInputStream, certificatePassword, certificateType, ide);
		} catch (FailingHttpStatusCodeException | ElementNotFoundException | SepeException | IOException
				| InterruptedException e) {
			throw new SepeException(e);
		}
	}
	
	public static byte[] transformacionsPdf(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String ipf, Date fini) throws Exception {
			return transformacionsPdfImpl(certificateInputStream, certificatePassword, certificateType, ipf, fini);
	}
	
	private static byte[] contratoPdfImpl(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String ipf, Date fini, Date fend ) throws Exception  {
		byte[] pdf= null;
	    try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)) {
	    	HtmlPage htmlPage = first_page_sepe_contrata(webClient);
			
	        htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/actionLogin.do?pagina=consultas").click(); 
	        htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/comunicacto/jsp/menu_consultasImpresion.jsp?origen=").click();
	        htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/servlet/ServletRegresar?ruta=menu_consultasgeneral&origen=").click();
	        htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/servlet/ServletConsultaEmpresa?pagina=idtrabajador&origen=").click();//por identificador del trabajador

	        pdf = page_contrac_or_cbasic(htmlPage, fini, fend, ipf);
	        return pdf;
		} 
	}
	
	private static byte[] getCopyBasicPdfImpl(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String ipf, Date fini, Date fend ) throws Exception  {
		byte[] pdf = null;
	    try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)) {
		    
	    	HtmlPage htmlPage = first_page_sepe_contrata(webClient);
			
	        htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/actionLogin.do?pagina=consultas").click(); 
	        htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/comunicacto/jsp/menu_consultasImpresion.jsp?origen=").click();
	        htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/servlet/ServletConsultaImpresionCB?pagina=entrada").click();
	        htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/servlet/ServletRegresar?ruta=menu_consultaImpCB&origen=consultaImpresionCB").click();
	        htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/servlet/ServletConsultaImpresionCB?pagina=idtrabajador&origen=consultaImpresionCB").click();//por identificador del trabajador
	        pdf = page_contrac_or_cbasic(htmlPage, fini, fend, ipf);
	        return pdf;
		}
	}
	
	
	private static void anulacionContratoImpl(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String ide)  throws SepeException, FailingHttpStatusCodeException, MalformedURLException, IOException, ElementNotFoundException, InterruptedException {
	    try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)) {
	    	
	    	HtmlPage htmlPage = first_page_sepe_contrata(webClient);
	    	htmlPage = first_page_anulacion(htmlPage);
	        htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/servlet/ServletAnulComunic?pagina=initC").click(); 
	        htmlPage = last_page_anulacion(htmlPage, ide);
	        
		} 
	}
	
	private static void anulacionTransformationImpl(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String ide) throws SepeException, FailingHttpStatusCodeException, MalformedURLException, IOException, ElementNotFoundException, InterruptedException  {
	    try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)) {
	    	
	    	HtmlPage htmlPage = first_page_sepe_contrata(webClient);
	    	htmlPage = first_page_anulacion(htmlPage);
	        htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/servlet/ServletAnulComunic?pagina=initT").click(); 
	        htmlPage = last_page_anulacion(htmlPage, ide);
		} 
	}
	
	private static HtmlPage first_page_anulacion(HtmlPage htmlPage) throws SepeException, ElementNotFoundException, IOException  {
	        htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/actionLogin.do?pagina=anulacioncomunicacion").click(); 
	        htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/comunicacto/jsp/menu_anulacion_bajas.jsp?origen=anulacioncomunicacion").click();
	        return htmlPage;
	}
	
	private static HtmlPage last_page_anulacion(HtmlPage htmlPage, String ide) throws SepeException, ElementNotFoundException, IOException, InterruptedException  {
		
		HtmlForm formDatos = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("datos")).orElseThrow();
		String ide1 = ide.substring(0, 2); //2 digits
		String ide2 = ide.substring(2, 6); //4 digits
		String ide3 = ide.substring(6);    //7 digits
		formDatos.getInputByName("idcomunicacion1").setValueAttribute(ide1);
		formDatos.getInputByName("idcomunicacion2").setValueAttribute(ide2);
		formDatos.getInputByName("idcomunicacion3").setValueAttribute(ide3);
		
	
		HtmlElement inputSubmit = formDatos.querySelector("input[value=aceptar]");
		htmlPage = (HtmlPage)inputSubmit.click();
		handleSepeExceptions(htmlPage);
		
		//TESTIIIIIIIIINNN (errors)
		formDatos = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("datos")).orElseThrow();
	    inputSubmit = formDatos.querySelector("input[value=aceptar]");
		htmlPage = (HtmlPage)inputSubmit.click();
		handleSepeExceptions(htmlPage);
		
	    inputSubmit = htmlPage.querySelector("form [value=enviar]");
		htmlPage = (HtmlPage)inputSubmit.click();
		handleSepeExceptions(htmlPage);
        return htmlPage;
	}
	
	private static byte[] page_contrac_or_cbasic(HtmlPage htmlPage, Date fini, Date fend, String ipf) throws Exception {
    	String[] fri = Toolkit.formatDate(fini);
    	String[] fre = Toolkit.formatDate(fend);
	    Integer ident  = 0; //NIF DEFAULT
	    if(Toolkit.identity(ipf).equals("6")) ident = 1; // NIE
	    
		HtmlForm formDatos = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("datos")).orElseThrow();
		HtmlOption option = (HtmlOption)  formDatos.querySelectorAll("select[name=tipodoc2]>option").get(ident);				
		option.click();
		formDatos.getInputByName("nifnietrabajador").setValueAttribute(Toolkit.appendStringLeft(ipf, " ", 2));
		formDatos.getInputByName("diadesde").setValueAttribute(fri[0]);
		formDatos.getInputByName("mesdesde").setValueAttribute(fri[1]);
		formDatos.getInputByName("anniodesde").setValueAttribute(fri[2]);
		
		formDatos.getInputByName("diahasta").setValueAttribute(fre[0]);
		formDatos.getInputByName("meshasta").setValueAttribute(fre[1]);
		formDatos.getInputByName("anniohasta").setValueAttribute(fre[2]);
	
		htmlPage = formDatos.getInputByName("aceptar").click();
		handleSepeExceptions(htmlPage);
		
		String finicio = fri[0]+"/"+fri[1]+"/"+fri[2];
		HtmlTable table = (HtmlTable) htmlPage.querySelector("table.tableScroll");
		
		HtmlCheckBoxInput firstColumn;
		String columnCheck = "a0";
		for (final HtmlTableRow row : table.getRows()) {
			HtmlTableCell cell = row.getCell(5);
			if(cell.getVisibleText().indexOf(finicio)>= 0) {
				firstColumn = row.getCell(0).querySelector("input[name=indice]");
				columnCheck  = firstColumn.getValueAttribute();
				break;
			}
		}

        HtmlCheckBoxInput checkBox = htmlPage.querySelector("form[name=datos] input[value="+columnCheck+"]");
        htmlPage = (HtmlPage) checkBox.click();
        handleSepeExceptions(htmlPage);
    	HtmlForm formDatos1 = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("datos")).orElseThrow();
        UnexpectedPage document = formDatos1.getInputByName("Boton_imprimir").click();
		InputStream inp = document.getWebResponse().getContentAsStream();
		byte[] pdf = inp.readAllBytes();
		inp.close();
        return pdf;
	}
	
	private static byte[] transformacionsPdfImpl(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String ipf, Date fini ) throws Exception  {
		byte[] pdf= null;
	    try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)) {
	    	String[] fri = Toolkit.formatDate(fini);
		    Integer ident  = 0; //NIF DEFAULT
		    if(Toolkit.identity(ipf).equals("6")) ident = 1; // NIE
		    
	    	HtmlPage htmlPage = first_page_sepe_contrata(webClient);
		
	        htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/actionLogin.do?pagina=consultas").click(); 
	        htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/comunicacto/jsp/menu_consultasImpresion.jsp?origen=").click();
	        htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/servlet/ServletConsultaTransformacion?pagina=entrada").click();
	        HtmlUnitToolkit.manageStatusCode(htmlPage);

	    	HtmlForm formDatos = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("datos")).orElseThrow();
	    	formDatos.getInputByName("tipoacceso").click();
			HtmlOption option = (HtmlOption)  formDatos.querySelectorAll("select[name=tipodocumento]>option").get(ident);				
			option.click();
			formDatos.getInputByName("nifnietrabajador").setValueAttribute(ipf);
			formDatos.getInputByName("diafechaini").setValueAttribute(fri[0]);
			formDatos.getInputByName("mesfechaini").setValueAttribute(fri[1]);
			formDatos.getInputByName("anniofechaini").setValueAttribute(fri[2]);
			htmlPage = formDatos.getInputByName("aceptar").click();
			handleSepeExceptions(htmlPage);
			
			//Developing!
		    UnexpectedPage document = htmlPage.getElementByName("Boton_imprimir").click();
			InputStream inp = document.getWebResponse().getContentAsStream();
			pdf = inp.readAllBytes();
			inp.close();
	        return pdf;
		} 
	}
	
	private static HtmlPage first_page_sepe_contrata(WebClient webClient) throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		  webClient.getOptions().setJavaScriptEnabled(true);
		  webClient.getOptions().setThrowExceptionOnScriptError(false);
		  webClient.setJavaScriptErrorListener(HtmlUnitToolkit.jascriptFunctionExceptionError());
	      HtmlPage htmlPage = webClient.getPage("https://www.sepe.es:444/ccomunicacto/servlet/ServletInicio?CCAA=99&idioma=14");
		  return htmlPage;
	}
	
	
	private static void handleSepeExceptions(HtmlPage htmlPage) throws SepeException{
		try {
			String error = htmlPage.querySelector("#avisos > div > p:last-child").getVisibleText();
			if(!error.isEmpty()) 
				throw new SepeException(error);
		} catch (NullPointerException e) {}
	}
	
	
//	public static void main(String[] args)   {
//		try(final FileInputStream certificateInputStream =  new FileInputStream("src/test/resources/solutions/aon/SEPE.p12")){
//				String certificatePassword = "aon@FNMT";
//				String certificateType = "pkcs12";
//				String regimen = "0111";
//		     	String ctaCti = "01105360062";
//		     	String nss = "291136796369";
//				String ipf = "Y7514970X";
//				String grup_ctz = "01";
//				String nif = "72740703Y";
//				Date fecha = Toolkit.parseDate("06-05-2016", "dd-MM-yyyy");
//				transformacionsPdfImpl(certificateInputStream, certificatePassword, certificateType, nif, fecha);
//		} catch(Exception e) {
//			e.printStackTrace();
//		}
//	}
	
}
