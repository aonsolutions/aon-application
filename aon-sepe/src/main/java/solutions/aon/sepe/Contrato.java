package solutions.aon.sepe;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Date;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.UnexpectedPage;
import com.gargoylesoftware.htmlunit.WebClient;

import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput;

import com.gargoylesoftware.htmlunit.html.HtmlForm;

import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;

import com.gargoylesoftware.htmlunit.html.HtmlTableCell;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;


import solutions.aon.sepe.toolkit.HtmlUnitToolkit;
import solutions.aon.sepe.toolkit.Toolkit;

public class Contrato {
	
	public static byte[] contratoPdf(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, String ipf, Date fini, Date fend) throws Exception {
			return contratoPdfImpl(certificateInputStream, certificatePassword, certificateType, ipf, fini, fend);
	}
	
	public static byte[] getCopyBasicPdf(InputStream certificateInputStream,  String certificatePassword,
			String certificateType, String ipf, Date fini, Date fend) throws Exception {
			return getCopyBasicPdfImpl(certificateInputStream, certificatePassword, certificateType, ipf, fini, fend);
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

	        pdf = page_cont_or_cbasic(htmlPage, fini, fend, ipf);
	        return pdf;
		} 
	}
	
	private static byte[] getCopyBasicPdfImpl(InputStream certificateInputStream, 
			String certificatePassword, String certificateType, String ipf, Date fini, Date fend ) throws Exception  {
		byte[] pdf = null;
	    try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)) {
		    
	    	HtmlPage htmlPage = first_page_sepe_contrata(webClient);
			
	        htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/actionLogin.do?pagina=consultas").click(); 
	        htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/comunicacto/jsp/menu_consultasImpresion.jsp?origen=").click();
	        htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/servlet/ServletConsultaImpresionCB?pagina=entrada").click();
	        htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/servlet/ServletRegresar?ruta=menu_consultaImpCB&origen=consultaImpresionCB").click();
	        htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/servlet/ServletConsultaImpresionCB?pagina=idtrabajador&origen=consultaImpresionCB").click();//por identificador del trabajador
	        pdf = page_cont_or_cbasic(htmlPage, fini, fend, ipf);
	        return pdf;
		}
	}
	
	private static byte[] page_cont_or_cbasic(HtmlPage htmlPage, Date fini, Date fend, String ipf) throws Exception {
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
        formDatos = htmlPage.querySelector("form[name=datos]");
        UnexpectedPage document = formDatos.getInputByName("Boton_imprimir").click();
		InputStream inp = document.getWebResponse().getContentAsStream();
		byte[] pdf = inp.readAllBytes();
		inp.close();
        return pdf;
	}
	
	private static HtmlPage first_page_sepe_contrata(WebClient webClient) throws FailingHttpStatusCodeException, MalformedURLException, IOException {
	      HtmlPage htmlPage = webClient.getPage("https://www.sepe.es:444/ccomunicacto/servlet/ServletInicio?CCAA=99&idioma=14");
		  return htmlPage;
	}
	
	
		
	private static void handleSepeExceptions(HtmlPage htmlPage) throws Exception{
		try {
			String error = htmlPage.querySelector("#avisos > div > p:last-child").getVisibleText();
			if(!error.isEmpty()) {
				throw new Exception(error);
			}
		} catch (NullPointerException e) {}
	}
}
