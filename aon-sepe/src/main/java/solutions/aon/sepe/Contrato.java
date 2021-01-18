package solutions.aon.sepe;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Date;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.UnexpectedPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
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
import com.gargoylesoftware.htmlunit.html.HtmlTextArea;

import aon.sepe.objects.Contract;
import solutions.aon.sepe.exceptions.SepeException;
import solutions.aon.sepe.exceptions.certificate.CertificateNotFoundException;
import solutions.aon.sepe.exceptions.statusCode.StatusCodeException;
import solutions.aon.sepe.toolkit.HtmlUnitToolkit;
import solutions.aon.sepe.toolkit.Toolkit;

public class Contrato {
	
	public static String contrato(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, Contract cto) throws SepeException {
			try {
				return contratoImpl(certificateInputStream, certificatePassword, certificateType, cto);
			} 
			catch (FailingHttpStatusCodeException e) {StatusCodeException.HandleStatusCodeException(e);} 
			catch (MalformedURLException e) {throw new SepeException(e);} 
			catch (IOException e) {throw new CertificateNotFoundException();} 
			catch (InterruptedException e) {throw new SepeException(e);}
			catch (Exception e) {throw new SepeException(e);}
			return null;
	}
	
	private static String contratoImpl(InputStream certificateInputStream, String certificatePassword, String certificateType, Contract cto) 
			throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException, SepeException {
		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)) {
			HtmlPage htmlPage = first_page_sepe_contrata(webClient);
	        htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/actionLogin.do?pagina=comunicacion").click(); 
	        htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/comunicacto/jsp/tipos_comunicacion_contratacion.jsp").click();
	        htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/comunicacto/jsp/atraves_comunicacion.jsp").click();

	        htmlPage = contractPage(htmlPage, cto.getCodContract());
			HtmlSelect codcto = htmlPage.querySelector("select[name=codcontrato]");
			
			codcto.setSelectedAttribute(cto.getCodContract(), true);
			
			HtmlSubmitInput sb = htmlPage.querySelector("#enviar");
			htmlPage = sb.click();
			HtmlForm form = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("datos")).orElseThrow();
			// DATA ENTERPRISE
			{
				String ctaCti = cto.getCtaCti();
				String regimen = cto.getRegimen();
				if(cto.getCifEnterprise()!=null) {
					form.getInputByName("cifnif").setValueAttribute(cto.getCifEnterprise());
				}
				
				form.getInputByName("regimen").setValueAttribute(regimen);
				form.getInputByName("numsecprov").setValueAttribute(ctaCti.substring(0,2));
				form.getInputByName("numsec").setValueAttribute(ctaCti.substring(2,9));
				form.getInputByName("digcont").setValueAttribute(ctaCti.substring(9));
				form.getInputByName("cuentacotizacion").setValueAttribute(regimen+ctaCti);
			}

			//DATA EMPLOYEE
			{
				String tipodoc =  "D";
				if(Toolkit.identity(cto.getIpf()).equals("6")) tipodoc = "E"; // NIE
			
				String nss = cto.getNss();
				((HtmlSelect)form.querySelector("select[name=tipodoc]")).setSelectedAttribute(tipodoc, true);
				form.getInputByName("nif").setValueAttribute(cto.getIpf());
				form.getInputByName("nifnie").setValueAttribute(tipodoc+"  "+cto.getIpf());
	
				form.getInputByName("nombre").setValueAttribute(cto.getName());
				form.getInputByName("apellido1").setValueAttribute(cto.getSurname());
				if(cto.getLastSurname()!=null)form.getInputByName("apellido2").setValueAttribute(cto.getLastSurname());
				if(cto.getSex() > 0)((HtmlSelect)form.querySelector("select[name=codsexo]")).setSelectedAttribute(cto.getSex().toString(), true);//SELECT  ("-1"=>"","1"=>"HOMBRE","2"=>"MUJER")
				

				String[] dateBirth = Toolkit.dateString(cto.getDateBirth());
				form.getInputByName("diafechanac").setValueAttribute(dateBirth[0]);
				form.getInputByName("mesfechanac").setValueAttribute(dateBirth[1]);
				form.getInputByName("anniofechanac").setValueAttribute(dateBirth[2]);

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
			
			String ide = null;
			String message = getSuccessMessage(htmlPage);
			if(message!=null) {
				String[] parts = message.split(":");
				if(parts.length > 0) 
					ide = (parts[1]).trim().replaceAll("-", "").substring(1);
			}
//	        Toolkit.buildFile(htmlPage.getWebResponse().getContentAsStream().readAllBytes(),"testContrato.html");
	        return ide;
		} 
	}
	
	public static String contratoCopyBasic(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, String ipf, Date fini, Date ffin, TypeFirm typeFirm, String workAddress, String restContract) throws SepeException {
			try {
				return contratoCopyBasicImpl(certificateInputStream, certificatePassword, certificateType, ipf, fini, ffin, typeFirm, workAddress, restContract);
			} 
			catch (FailingHttpStatusCodeException e) {StatusCodeException.HandleStatusCodeException(e);} 
			catch (MalformedURLException e) {throw new SepeException(e);} 
			catch (IOException e) {throw new CertificateNotFoundException();} 
			catch (InterruptedException e) {throw new SepeException(e);}
			catch (Exception e) {throw new SepeException(e);}
			return null;
	}
	
	private static String contratoCopyBasicImpl(InputStream certificateInputStream, String certificatePassword, String certificateType, 
			String ipf, Date fini, Date ffin, TypeFirm typeFirm, String workAddress, String restContract) 
			throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException, SepeException {
		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)) {
			HtmlPage htmlPage = first_page_sepe_contrata(webClient);
	        htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/actionLogin.do?pagina=copiabasica").click(); 
			htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/comunicacto/jsp/menu_comunica_copiaBasicaContrato.jsp?origen=copiabasica").click();
			htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/servlet/ServletConsultaEmpresa?pagina=idtrabajador&origen=copiabasica").click(); 
 
	        HtmlForm form = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("datos")).orElseThrow();
	        
	        htmlPage = page_contrac_or_cbasic(htmlPage, fini, ffin, ipf);
	        
	        form = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("datos")).orElseThrow();
	        Integer nFirm = 0;
	        switch (typeFirm) {
				case FIRMADA_REPRESENTANTES_LEGALES:
					nFirm = 1;
				break;
				case NO_EXISTE_REPRESENTACION:
					nFirm = 2;
				break;
				case NO_FACILITADO_COPIA:
					nFirm = 3;
				break;
				case REHUSAN_FIRMAR:
					nFirm = 4;
				break;
			}
	      
	        ((HtmlSelect)form.querySelector("select[name=codtipofirma]")).setSelectedAttribute(nFirm.toString(), true);
	        ((HtmlTextArea)form.querySelector("[name=areadeDomicilio]")).setText(workAddress);
	        ((HtmlTextArea)form.querySelector("[name=areadeTexto]")).setText(restContract);
	        
	        htmlPage = ((HtmlSubmitInput)form.querySelector("[name=enviar]")).click();
	        handleSepeExceptions(htmlPage);
	        
	        String message = getSuccessMessage(htmlPage);
	        System.out.println(message);
	        return message;
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
	
	public static void removeContrato(final InputStream certificateInputStream, final String certificatePassword, 
			final String certificateType, String ide) throws SepeException {
		 try { removeContratoImpl(certificateInputStream, certificatePassword, certificateType, ide); }
		catch (FailingHttpStatusCodeException e) {throw new SepeException(e);} 
		catch (MalformedURLException e) {throw new SepeException(e);} 
		catch (IOException e) {throw new CertificateNotFoundException();} 
		catch (InterruptedException e) {throw new SepeException(e);}
		catch (Exception e) {throw new SepeException(e);}
	}
	
	public static void removeTransformation(final InputStream certificateInputStream, final String certificatePassword, 
			final String certificateType, String ide) throws SepeException{
		try { removeTransformationImpl(certificateInputStream, certificatePassword, certificateType, ide); }
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
	    try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)) {
	    	HtmlPage htmlPage = first_page_sepe_contrata(webClient);
			
	        htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/actionLogin.do?pagina=consultas").click(); 
	        htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/comunicacto/jsp/menu_consultasImpresion.jsp?origen=").click();
	        htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/servlet/ServletRegresar?ruta=menu_consultasgeneral&origen=").click();
	        htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/servlet/ServletConsultaEmpresa?pagina=idtrabajador&origen=").click();//por identificador del trabajador

	        htmlPage = page_contrac_or_cbasic(htmlPage, fini, fend, ipf);
	        HtmlForm formDatos1 = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("datos")).orElseThrow();
	        UnexpectedPage document = formDatos1.getInputByName("Boton_imprimir").click();
	        InputStream inp = document.getWebResponse().getContentAsStream();
	        byte[] pdf = inp.readAllBytes();
	        inp.close();
	        return pdf;
		} 
	}
	
	private static byte[] getCopyBasicPdfImpl(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String ipf, Date fini, Date fend ) throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException, SepeException  {
	    try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)) {
		    
	    	HtmlPage htmlPage = first_page_sepe_contrata(webClient);
			
	        htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/actionLogin.do?pagina=consultas").click(); 
	        htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/comunicacto/jsp/menu_consultasImpresion.jsp?origen=").click();
	        htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/servlet/ServletConsultaImpresionCB?pagina=entrada").click();
	        htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/servlet/ServletRegresar?ruta=menu_consultaImpCB&origen=consultaImpresionCB").click();
	        htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/servlet/ServletConsultaImpresionCB?pagina=idtrabajador&origen=consultaImpresionCB").click();//por identificador del trabajador
	        htmlPage = page_contrac_or_cbasic(htmlPage, fini, fend, ipf);
	        HtmlForm formDatos1 = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("datos")).orElseThrow();
	        UnexpectedPage document = formDatos1.getInputByName("Boton_imprimir").click();
	        InputStream inp = document.getWebResponse().getContentAsStream();
	        byte[] pdf = inp.readAllBytes();
	        inp.close();
	        return pdf;
		}
	}
	
	private static void removeContratoImpl(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String ide)  throws SepeException, FailingHttpStatusCodeException, MalformedURLException, IOException, ElementNotFoundException, InterruptedException {
	    try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)) {
	    	
	    	HtmlPage htmlPage = first_page_sepe_contrata(webClient);
	    	htmlPage = first_page_anulacion(htmlPage);
	        htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/servlet/ServletAnulComunic?pagina=initC").click(); 
	        htmlPage = last_page_anulacion(htmlPage, ide);
	        String message = getSuccessMessage(htmlPage);
	        System.out.println(message);
		} 
	}
	
	private static void removeTransformationImpl(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String ide) throws SepeException, FailingHttpStatusCodeException, MalformedURLException, IOException, ElementNotFoundException, InterruptedException  {
	    try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)) {
	    	
	    	HtmlPage htmlPage = first_page_sepe_contrata(webClient);
	    	htmlPage = first_page_anulacion(htmlPage);
	        htmlPage = htmlPage.getAnchorByHref("/ccomunicacto/servlet/ServletAnulComunic?pagina=initT").click(); 
	        htmlPage = last_page_anulacion(htmlPage, ide);
	        String message = getSuccessMessage(htmlPage);
	        System.out.println(message);
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
	
		formDatos = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("datos")).orElseThrow();
	    inputSubmit = formDatos.querySelector("#anular");
	   
		htmlPage = (HtmlPage)inputSubmit.click();
		handleSepeExceptions(htmlPage);
		
	    inputSubmit = htmlPage.querySelector("form input[name=enviar]");
		htmlPage = (HtmlPage)inputSubmit.click();
		handleSepeExceptions(htmlPage);
	
        return htmlPage;
	}
	
	private static HtmlPage page_contrac_or_cbasic(HtmlPage htmlPage, Date fini, Date fend, String ipf) throws InterruptedException, IOException, SepeException  {
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

        htmlPage = ((HtmlCheckBoxInput)htmlPage.querySelector("form[name=datos] input[value="+columnCheck+"]")).click();
        handleSepeExceptions(htmlPage);
        return htmlPage;
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
			System.out.println(error);
			if(!error.isEmpty()) 
				throw new SepeException(error);
		} catch (NullPointerException e) {}
	}
	
	private static HtmlPage contractPage(HtmlPage htmlPage, String codCto) throws ElementNotFoundException, IOException {
		String href = null;
        String oneCodCto = codCto.substring(0,1);
        switch (oneCodCto) {
			case "1": //INDEFINIDO_TIEMPO_COMPLETO
				 href = "/ccomunicacto/comunicacto/jsp/atraves_comunicacion2.jsp?com=1";
			break;
			case "2": // INDEFINIDO_TIEMPO_PARCIAL
				 href = "/ccomunicacto/comunicacto/jsp/atraves_comunicacion2.jsp?com=2";
			break;
			case "4": // TEMPORAL_TIEMPO_COMPLETO
				 href = "/ccomunicacto/comunicacto/jsp/atraves_comunicacion2.jsp?com=4";
			break;
			case "5": // TEMPORAL_TIEMPO_PARCIAL
				 href = "/ccomunicacto/comunicacto/jsp/atraves_comunicacion2.jsp?com=5";
			break;
		}
        htmlPage = htmlPage.getAnchorByHref(href).click();
        return htmlPage;
	}
	
	private static String getSuccessMessage(HtmlPage htmlPage) {
		DomNodeList<DomNode> texts = htmlPage.querySelectorAll("#contIzq p");
		String msg = null;
		for( DomNode p: texts) {
			String pStr = Toolkit.removeNBSP(p.getVisibleText()).trim();
			Integer pInt = pStr.length();
			if(pInt > 0) {
				if(pStr.indexOf("Identificador de la Comunicaci\u00F3n :")>=0) {
					msg = pStr;
					break;
				} else if(pStr.indexOf("se ha realizado correctamente")>=0) {
					msg = pStr;
					break;
				}
			}
		}
		return msg;
	}
	
	public enum TypeFirm{
		FIRMADA_REPRESENTANTES_LEGALES, 
		NO_EXISTE_REPRESENTACION,
		NO_FACILITADO_COPIA,
		REHUSAN_FIRMAR
	}
	
}
