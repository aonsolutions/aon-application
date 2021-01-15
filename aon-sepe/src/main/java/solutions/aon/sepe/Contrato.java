package solutions.aon.sepe;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.UnexpectedPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableCell;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;

import aon.sepe.objects.Contract;
import solutions.aon.sepe.exceptions.SepeException;
import solutions.aon.sepe.exceptions.certificate.CertificateNotFoundException;
import solutions.aon.sepe.exceptions.statusCode.StatusCodeException;
import solutions.aon.sepe.toolkit.HtmlUnitToolkit;
import solutions.aon.sepe.toolkit.Toolkit;

public class Contrato {
	
	public static void contrato(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, Contract cto) throws SepeException {
			try {
				contratoImpl(certificateInputStream, certificatePassword, certificateType, cto);
			} 
			catch (FailingHttpStatusCodeException e) {StatusCodeException.HandleStatusCodeException(e);} 
			catch (MalformedURLException e) {throw new SepeException(e);} 
			catch (IOException e) {throw new CertificateNotFoundException();} 
			catch (InterruptedException e) {throw new SepeException(e);}
			catch (Exception e) {throw new SepeException(e);}
	}
	
	private static void contratoImpl(InputStream certificateInputStream, String certificatePassword, String certificateType, Contract cto) 
			throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException, SepeException {
		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)) {
			HtmlPage htmlPage = first_page_sepe_contrata(webClient);
	        htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/actionLogin.do?pagina=comunicacion").click(); 
	        htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/comunicacto/jsp/tipos_comunicacion_contratacion.jsp").click();
	        htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/comunicacto/jsp/atraves_comunicacion.jsp").click();
	        
	        htmlPage = contractPage(htmlPage);
			HtmlSelect codcto = htmlPage.querySelector("select[name=codcontrato]");
			
			codcto.setSelectedAttribute(cto.getCodContract(), true);
			
			HtmlSubmitInput sb = htmlPage.querySelector("#enviar");
			htmlPage = sb.click();
			HtmlForm form = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("datos")).orElseThrow();
			// DATA ENTERPRISE
			{
				String ctaCti = cto.getCtaCti();
				form.getInputByName("cifnif").setValueAttribute(cto.getCifEnterprise());
				form.getInputByName("regimen").setValueAttribute(cto.getRegimen());
				form.getInputByName("numsecprov").setValueAttribute(ctaCti.substring(0,2));
				form.getInputByName("numsec").setValueAttribute(ctaCti.substring(2,9));
				form.getInputByName("digcont").setValueAttribute(ctaCti.substring(9));
				System.out.println(ctaCti.substring(0,2));
				System.out.println(ctaCti.substring(2,9));
				System.out.println(ctaCti.substring(9));
			}

			//DATA EMPLOYEE
			{
				//DATA EMPLOYEE
				String tipodoc =  "D";
				if(identity(cto.getIpf()).equals("6")) tipodoc = "E"; // NIE
			
				String nss = cto.getNss();
				((HtmlSelect)form.querySelector("select[name=tipodoc]")).setSelectedAttribute(tipodoc, true);
				form.getInputByName("nombre").setValueAttribute(cto.getName());
				form.getInputByName("apellido1").setValueAttribute(cto.getSurname());
				if(cto.getLastSurname()!=null)form.getInputByName("apellido2").setValueAttribute(cto.getLastSurname());
				if(cto.getSex() > 0)((HtmlSelect)form.querySelector("select[name=codsexo]")).setSelectedAttribute(cto.getSex().toString(), true);//SELECT  ("-1"=>"","1"=>"HOMBRE","2"=>"MUJER")
				
				if(cto.getDateBirth()!=null) {
					String[] dateBirth = Toolkit.dateString(cto.getDateBirth());
					form.getInputByName("diafechanac").setValueAttribute(dateBirth[0]);
					form.getInputByName("mesfechanac").setValueAttribute(dateBirth[1]);
					form.getInputByName("anniofechanac").setValueAttribute(dateBirth[2]);
				}
				((HtmlSelect)form.querySelector("select[name=nacionalidad]")).setSelectedAttribute(cto.getCodNationality().toString(), true);
				((HtmlSelect)form.querySelector("select[name=codpaisdomicilio]")).setSelectedAttribute(cto.getCodPaisDom().toString(), true);
				form.getInputByName("municipio").setValueAttribute(cto.getCodMunDom().toString());
				form.getInputByName("nass1").setValueAttribute(nss.substring(0, 2));
				form.getInputByName("nass2").setValueAttribute(nss.substring(2, 10));
				form.getInputByName("nass3").setValueAttribute(nss.substring(10));
			}

			//DATA CONTRACT
			{
				String[] dateInitContract = Toolkit.dateString(cto.getDateIniContract());
				form.getInputByName("diafechaini").setValueAttribute(dateInitContract[0]);
				form.getInputByName("mesfechaini").setValueAttribute(dateInitContract[1]);
				form.getInputByName("anniofechaini").setValueAttribute(dateInitContract[2]);
				if(cto.getCodFormativo() > 0) ((HtmlSelect)form.querySelector("select[name=codnivelformativo]")).setSelectedAttribute(cto.getCodFormativo().toString(), true);
				form.getInputByName("ocupacion").setValueAttribute(cto.getCodOccupation().toString()); // disabled
				form.getInputByName("cocupacion").setValueAttribute(cto.getCodOccupation().toString());// repeat cod contract
				((HtmlSelect)form.querySelector("select[name=codpais]")).setSelectedAttribute(cto.getCodPaisWork().toString(), true);
				form.getInputByName("municipiocontrato").setValueAttribute(cto.getCodMunWork().toString());//disabled
				String offerStr = cto.getOffer() == true ? "S" : "N";
 				((HtmlSelect)form.querySelector("select[name=procedeDeOfertaEmpleo]")).setSelectedAttribute(offerStr, true);
			}
			
			//OTHERS DATA CONTRACT (OPTIONAL)
			{
				if(cto.getDateFinContract()!=null) {
					String[] dateFinContract = Toolkit.dateString(cto.getDateFinContract());
					form.getInputByName("diafechafin").setValueAttribute(dateFinContract[0]);
					form.getInputByName("mesfechafin").setValueAttribute(dateFinContract[1]);
					form.getInputByName("anniofechafin").setValueAttribute(dateFinContract[2]);
				}
			
				if(cto.getTypeJnd()!=null)
					((HtmlSelect)form.querySelector("select[name=codtipojornada]")).setSelectedAttribute(cto.getTypeJnd(), true); //review
			
				if(cto.getDurationTypeJndHour()!=null)
					form.getInputByName("horasduracionjornada").setValueAttribute(cto.getDurationTypeJndHour());
				
				if(cto.getDurationTypeJndMin()!=null)
					form.getInputByName("minutosduracionjornada").setValueAttribute(cto.getDurationTypeJndMin());
				
				if(cto.getDurationTypeCvnHour()!=null)
					form.getInputByName("horasduracionconvenio").setValueAttribute(cto.getDurationTypeCvnHour());
			
				if(cto.getDurationTypeCvnMin()!=null)
					form.getInputByName("minutosduracionconvenio").setValueAttribute(cto.getDurationTypeCvnMin());
				/*
				 	//TIEMPO PARCIAL

					//OPTIONAL
					form.getInputByName("horasduracionconvenio").setValueAttribute("");
					form.getInputByName("minutosduracionconvenio").setValueAttribute("");
				*/
			}
			
			form.getInputByName("contratoEscrito").setValueAttribute("N"); //  contratoEscrito si la fecha fin es menor a 28 
		
			HtmlSubmitInput accept = form.querySelector("[name=aceptar]");
			htmlPage = accept.click();
			
			handleSepeExceptions(htmlPage);
			
	        Toolkit.buildFile(htmlPage.getWebResponse().getContentAsStream().readAllBytes(),"testContrato.html");
		} 
	}
	
	public static byte[] contratoPdf(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, String ipf, Date fini, Date fend) throws SepeException {
			try {
				return contratoPdfImpl(certificateInputStream, certificatePassword, certificateType, ipf, fini, fend);
			} 
			catch (FailingHttpStatusCodeException e) {throw new SepeException(e);} 
			catch (MalformedURLException e) {throw new SepeException(e);} 
			catch (IOException e) {throw new CertificateNotFoundException();} 
			catch (InterruptedException e) {throw new SepeException(e);}
			catch (Exception e) {throw new SepeException(e);}
	}
	
	public static byte[] getCopyBasicPdf(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String ipf, Date fini, Date fend) throws SepeException {
			try {
				return getCopyBasicPdfImpl(certificateInputStream, certificatePassword, certificateType, ipf, fini, fend);
			} 
			catch (FailingHttpStatusCodeException e) {throw new SepeException(e);} 
			catch (MalformedURLException e) {throw new SepeException(e);} 
			catch (IOException e) {throw new CertificateNotFoundException();} 
			catch (InterruptedException e) {throw new SepeException(e);}
			catch (Exception e) {throw new SepeException(e);}
	}
	
	public static void anulacionContrato(final InputStream certificateInputStream, final String certificatePassword, 
			final String certificateType, String ide) throws SepeException {
		  try {
			anulacionContratoImpl(certificateInputStream, certificatePassword, certificateType, ide);
		  }
			catch (FailingHttpStatusCodeException e) {throw new SepeException(e);} 
			catch (MalformedURLException e) {throw new SepeException(e);} 
			catch (IOException e) {throw new CertificateNotFoundException();} 
			catch (InterruptedException e) {throw new SepeException(e);}
			catch (Exception e) {throw new SepeException(e);}
	}
	
	public static void anulacionTransformation(final InputStream certificateInputStream, final String certificatePassword, 
			final String certificateType, String ide) throws SepeException{
		try {
			anulacionTransformationImpl(certificateInputStream, certificatePassword, certificateType, ide);
		}
			catch (FailingHttpStatusCodeException e) {throw new SepeException(e);} 
			catch (MalformedURLException e) {throw new SepeException(e);} 
			catch (IOException e) {throw new CertificateNotFoundException();} 
			catch (InterruptedException e) {throw new SepeException(e);}
			catch (Exception e) {throw new SepeException(e);}
	}
	
	public static byte[] transformacionsPdf(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String ipf, Date fini) throws SepeException {
			try {
				return transformacionsPdfImpl(certificateInputStream, certificatePassword, certificateType, ipf, fini);
			}
			catch (FailingHttpStatusCodeException e) {throw new SepeException(e);} 
			catch (MalformedURLException e) {throw new SepeException(e);} 
			catch (IOException e) {throw new CertificateNotFoundException();} 
			catch (InterruptedException e) {throw new SepeException(e);}
			catch (Exception e) {throw new SepeException(e);}
	}
	
	private static byte[] contratoPdfImpl(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String ipf, Date fini, Date fend ) throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException, SepeException  {
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
			final String certificateType, String ipf, Date fini, Date fend ) throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException, SepeException  {
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
	
	private static byte[] page_contrac_or_cbasic(HtmlPage htmlPage, Date fini, Date fend, String ipf) throws InterruptedException, IOException, SepeException  {
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
			final String certificateType, String ipf, Date fini ) throws FailingHttpStatusCodeException, MalformedURLException, IOException, SepeException, InterruptedException {
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
	
	private static HtmlPage contractPage(HtmlPage htmlPage) throws ElementNotFoundException, IOException {
		String href = null;
		TypeContract typeContract= TypeContract.TEMPORAL_TIEMPO_COMPLETO; //change
        switch (typeContract) {
			case INDEFINIDO_TIEMPO_COMPLETO:
				 href = "/ccomunicacto/comunicacto/jsp/atraves_comunicacion2.jsp?com=1";
			break;
			case INDEFINIDO_TIEMPO_PARCIAL:
				 href = "/ccomunicacto/comunicacto/jsp/atraves_comunicacion2.jsp?com=2";
			break;
			case FIJO_CONTINUO:
				 href = "/ccomunicacto/comunicacto/jsp/atraves_comunicacion2.jsp?com=3";
			break;
			case TEMPORAL_TIEMPO_COMPLETO:
				 href = "/ccomunicacto/comunicacto/jsp/atraves_comunicacion2.jsp?com=4";
			break;
			case TEMPORAL_TIEMPO_PARCIAL:
				 href = "/ccomunicacto/comunicacto/jsp/atraves_comunicacion2.jsp?com=5";
			break;
			case FORMACION_APRENDIZAJE:
				 href = "/ccomunicacto/comunicacto/jsp/atraves_comunicacion2.jsp?com=6";
			break;
			case PRACTICA_TIEMPO_COMPLETO:
				 href = "/ccomunicacto/comunicacto/jsp/atraves_comunicacion2.jsp?com=7";
			break;
			case PRACTICA_TIEMPO_PARCIAL:
				 href = "/ccomunicacto/comunicacto/jsp/atraves_comunicacion2.jsp?com=8";
			break;
		}
        htmlPage = htmlPage.getAnchorByHref(href).click();
        return htmlPage;
	}
	
	public enum TypeContract {
		INDEFINIDO_TIEMPO_COMPLETO,
		INDEFINIDO_TIEMPO_PARCIAL,
		FIJO_CONTINUO,
		TEMPORAL_TIEMPO_COMPLETO,
		TEMPORAL_TIEMPO_PARCIAL,
		FORMACION_APRENDIZAJE,
		PRACTICA_TIEMPO_COMPLETO,
		PRACTICA_TIEMPO_PARCIAL
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
}
