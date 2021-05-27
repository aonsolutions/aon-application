package solutions.aon.seg.social;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
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
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.ScriptException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.javascript.JavaScriptErrorListener;
import com.gargoylesoftware.htmlunit.xml.XmlPage;

import solutions.aon.seg.social.exception.CertificateNotFoundException;
import solutions.aon.seg.social.exception.InvalidCertificateException;
import solutions.aon.seg.social.exception.SegSocialException;
import solutions.aon.seg.social.exception.StatusCodeException;
import solutions.aon.seg.social.exception.invalid.InvalidDataException;
import solutions.aon.seg.social.object.Employee;
import solutions.aon.seg.social.object.Employee.EmployeeBuilder;
import solutions.aon.seg.social.toolkit.HtmlUnitToolkit;
import solutions.aon.seg.social.toolkit.Toolkit;


class SistemaREDMov {
	
//	  Toolkit.buildFile(htmlPage.getWebResponse().getContentAsStream().readAllBytes(),"ipfxnaf.html");

	//HANDLE THE EXCEPTIONS OF Mov METHOD
	public static Employee sendMov(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, Employee employee) throws SegSocialException{
		try {return employee;} 
		catch (Exception e) {throw new SegSocialException(e);}
	}
	
	//HANDLE THE EXCEPTIONS OF ALTA METHOD
	public static Employee sendAlta(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, Employee employee) throws SegSocialException{
		
		InvalidCertificateException.checkCertificate(certificateInputStream);
		
		try {return sendAltaImpl(certificateInputStream, certificatePassword, certificateType, employee);} 
		catch (FailingHttpStatusCodeException e) {StatusCodeException.HandleStatusCodeException(e);} 
		catch (MalformedURLException e) {throw new SegSocialException(e);} 
		catch (IOException e) {throw new CertificateNotFoundException();} 
		catch (InterruptedException e) {throw new SegSocialException(e);}
		catch (Exception e) {throw new SegSocialException(e);}
		return null;
	}

	public static byte[] getReportAffiliateInMovPrev(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String regime, String ccc) throws SegSocialException{
		InvalidCertificateException.checkCertificate(certificateInputStream);
		try {return getReportAffiliateInMovPrevImpl(certificateInputStream, certificatePassword, certificateType, regime, ccc);}
		catch (FailingHttpStatusCodeException e) {StatusCodeException.HandleStatusCodeException(e);} 
		catch (MalformedURLException e) {throw new SegSocialException(e);} 
		catch (IOException e) {throw new CertificateNotFoundException();} 
		catch (InterruptedException e) {throw new SegSocialException(e);}
		catch (Exception e) {throw new SegSocialException(e);}
		return null;
	}
	
	public static byte[] getReportAffiliateInAlta(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String regime, String ccc) throws SegSocialException{
		InvalidCertificateException.checkCertificate(certificateInputStream);
		try {return getReportAffiliateInAltaImpl(certificateInputStream, certificatePassword, certificateType, regime, ccc);}
		catch (FailingHttpStatusCodeException e) {StatusCodeException.HandleStatusCodeException(e);} 
		catch (MalformedURLException e) {throw new SegSocialException(e);} 
		catch (IOException e) {throw new CertificateNotFoundException();} 
		catch (InterruptedException e) {throw new SegSocialException(e);}
		catch (Exception e) {throw new SegSocialException(e);}
		return null;
	}
	
	
	
	//HANDLE THE EXCEPTIONS OF ALTA METHOD
	public static Employee sendBaja(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, Employee employee) throws SegSocialException{
		
		InvalidCertificateException.checkCertificate(certificateInputStream);
		
		try {return sendBajaImpl(certificateInputStream, certificatePassword, certificateType, employee);} 
		catch (FailingHttpStatusCodeException e) {StatusCodeException.HandleStatusCodeException(e);} 
		catch (MalformedURLException e) {throw new SegSocialException(e);} 
		catch (IOException e) {throw new CertificateNotFoundException();} 
		catch (InterruptedException e) {throw new SegSocialException(e);}
		catch (Exception e) {throw new SegSocialException(e);}
		return null;
	}
	
	public static Collection<Employee> ipfxnaf(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, ArrayList<String> nssList) throws SegSocialException{
		
		InvalidCertificateException.checkCertificate(certificateInputStream);
		
		try {return ipfxnafImpl(certificateInputStream, certificatePassword, certificateType, nssList);} 
		catch (FailingHttpStatusCodeException e) {StatusCodeException.HandleStatusCodeException(e);} 
		catch (MalformedURLException e) {throw new SegSocialException(e);} 
		catch (IOException e) {throw new CertificateNotFoundException();} 
		catch (InterruptedException e) {throw new SegSocialException(e);}
		catch (Exception e) {throw new SegSocialException(e);}
		return null;
	}
	
	public static Employee nafxipf(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String ipf, String apellido1, String apellido2) throws SegSocialException{
		
		InvalidCertificateException.checkCertificate(certificateInputStream);
		
		try {return nafxipfImpl(certificateInputStream, certificatePassword, certificateType,  ipf, apellido1, apellido2);} 
		catch (FailingHttpStatusCodeException e) {StatusCodeException.HandleStatusCodeException(e);} 
		catch (MalformedURLException e) {throw new SegSocialException(e);} 
		catch (IOException e) {throw new CertificateNotFoundException();} 
		catch (InterruptedException e) {throw new SegSocialException(e);}
		catch (Exception e) {throw new SegSocialException(e);}
		return null;
	}
	
	public static void movPrevDelete(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String situation, String regimen, String ctaCti, String nss, Date fecha) throws SegSocialException{
		
		InvalidCertificateException.checkCertificate(certificateInputStream);
		
		try {movPrevDeleteImpl(certificateInputStream, certificatePassword, certificateType,situation, regimen, ctaCti, nss, fecha);} 
		catch (FailingHttpStatusCodeException e) {StatusCodeException.HandleStatusCodeException(e);} 
		catch (MalformedURLException e) {throw new SegSocialException(e);} 
		catch (IOException e) {throw new CertificateNotFoundException();} 
		catch (InterruptedException e) {throw new SegSocialException(e);}
		catch (Exception e) {throw new SegSocialException(e);}
		return;
	}
	
	public static void altaConsolidadaDelete(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String situation, String regimen, String ctaCti, String nss) throws SegSocialException{
		
		InvalidCertificateException.checkCertificate(certificateInputStream);
		
		try {altaConsolidadaDeleteImpl(certificateInputStream, certificatePassword, certificateType, situation, regimen, ctaCti, nss);} 
		catch (FailingHttpStatusCodeException e) {StatusCodeException.HandleStatusCodeException(e);} 
		catch (MalformedURLException e) {throw new SegSocialException(e);} 
		catch (IOException e) {throw new CertificateNotFoundException();} 
		catch (InterruptedException e) {throw new SegSocialException(e);}
		catch (Exception e) {throw new SegSocialException(e);}
	}
	
	public static void cambioGrupCtz(final InputStream certificateInputStream, 
			final String certificatePassword, final String certificateType, 
			String ipf, String regimen, String ctaCti, String nss, String grupCtz, Date fecha) throws SegSocialException{
		InvalidCertificateException.checkCertificate(certificateInputStream);
		try {cambioGrupCtzImpl(certificateInputStream, certificatePassword, certificateType, ipf, regimen, ctaCti, nss, grupCtz, fecha);} 
		catch (FailingHttpStatusCodeException e) {StatusCodeException.HandleStatusCodeException(e);} 
		catch (MalformedURLException e) {throw new SegSocialException(e);} 
		catch (IOException e) {throw new CertificateNotFoundException();} 
		catch (InterruptedException e) {throw new SegSocialException(e);}
		catch (Exception e) {throw new SegSocialException(e);}
		return;
	}
	
	public static void cambioOcupacion(final InputStream certificateInputStream, 
			final String certificatePassword, final String certificateType, 
			String ipf, String regimen, String ctaCti, String nss, String ocup, Date fecha) throws SegSocialException{
		InvalidCertificateException.checkCertificate(certificateInputStream);
		try {cambioOcupacionImpl(certificateInputStream, certificatePassword, certificateType, ipf, regimen, ctaCti, nss, ocup, fecha);} 
		catch (FailingHttpStatusCodeException e) {StatusCodeException.HandleStatusCodeException(e);} 
		catch (MalformedURLException e) {throw new SegSocialException(e);} 
		catch (IOException e) {throw new CertificateNotFoundException();} 
		catch (InterruptedException e) {throw new SegSocialException(e);}
		catch (Exception e) {throw new SegSocialException(e);}
		return;
	}	
	
	public static void cambioCatProf(final InputStream certificateInputStream, 
			final String certificatePassword, final String certificateType, 
			String ipf, String regimen, String ctaCti, String nss, String cat, Date fecha) throws SegSocialException{
		InvalidCertificateException.checkCertificate(certificateInputStream);
		try {cambioCatProfImpl(certificateInputStream, certificatePassword, certificateType, ipf, regimen, ctaCti, nss, cat, fecha);} 
		catch (FailingHttpStatusCodeException e) {StatusCodeException.HandleStatusCodeException(e);} 
		catch (MalformedURLException e) {throw new SegSocialException(e);} 
		catch (IOException e) {throw new CertificateNotFoundException();} 
		catch (InterruptedException e) {throw new SegSocialException(e);}
		catch (Exception e) {throw new SegSocialException(e);}
		return;
	}	
	
	private static Employee sendAltaImpl(
			final InputStream certificateInputStream, final String certificatePassword, final String certificateType, 
			Employee employee
	) throws Exception  {
	    	String situation = "01";
	    	Integer mov = 0;
			String ident = identity(employee.getIpf());
			String dni =  Toolkit.fillStringLeft(employee.getIpf(), "0", 10);
 			String[] fra = formatDate(employee.getFra()); //fecha [dia,mes,año]
 			WebClient webclient = getWebClient(certificateInputStream,certificatePassword, certificateType);
	    	HtmlPage htmlPage = firstPageAltaBaja(
					webclient, mov, employee.getNss(), employee.getCtaCti().get(),
					employee.getRegime(),  dni, ident, employee.getFra()
	    	);
	
			HtmlForm form = (HtmlForm) HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("jacadaform")).orElseThrow();
			form.getInputByName("txt_SDFSITAFI_ayuda").setValueAttribute(situation); 
			form.getInputByName("txt_SDFFREALDD").setValueAttribute(fra[0]); 
			form.getInputByName("txt_SDFFREALMM").setValueAttribute(fra[1]); 
			form.getInputByName("txt_SDFFREALAA").setValueAttribute(fra[2]); 
			form.getInputByName("txt_SDFGRUCOT_ayuda").setValueAttribute(employee.getGc().get()); 
			form.getInputByName("txt_SDFTICO_ayuda").setValueAttribute(employee.getContract().get());
			if(form.getInputByName("txt_SDFCONVCOL_ayuda").getValueAttribute().isEmpty()) {
				form.getInputByName("txt_SDFCONVCOL_ayuda").setValueAttribute( employee.getColec() ); 
			}
			if ("0163" == employee.getRegime() && !employee.getMdctz().isEmpty()) {
				form.getInputByName("txt_SDFMODCOTI_ayuda").setValueAttribute(employee.getMdctz().get());
			} else {
				if(!employee.getCoef().isEmpty()) {
					String coef = Integer.toString(employee.getCoef().get().intValue());
					form.getInputByName("txt_SDFCOEFCO_ayuda").setValueAttribute(coef); 
				}
				if (employee.getOcup()!=null) form.getInputByName("txt_SDFOCUPACION").setValueAttribute(employee.getOcup().toUpperCase()); 		
			}
		    
			htmlPage = ((HtmlInput)form.querySelector("input[value=\"Continuar\"]")).click();
			HtmlUnitToolkit.manageStatusCode(htmlPage);
	
			DomNode msg1 = htmlPage.querySelector("#Sub0000201056");
			if(msg1!=null && msg1.getTextContent().trim().indexOf("LA MECANIZACION DE ESTE TIPO DE REGISTROS PUEDE IMPLICAR") !=-1) {
				htmlPage = ((HtmlInput)htmlPage.querySelector("input[value=\"Continuar\"]")).click();
				HtmlUnitToolkit.manageStatusCode(htmlPage);
			}
			
			DomNode msg2 = htmlPage.querySelector("#Sub0600401054");
			if(msg2!=null && msg2.getTextContent().trim().indexOf("Revise el contenido del coeficiente a tiempo parcial") !=-1) {
				htmlPage = ((HtmlInput)htmlPage.querySelector("input[value=\"Continuar\"]")).click();
				HtmlUnitToolkit.manageStatusCode(htmlPage);
			}			

			return employee;
	}
	
	private static Employee sendBajaImpl(
			final InputStream certificateInputStream, final String certificatePassword, final String certificateType, 
			Employee employee
	) throws Exception {
    	String situation = "63";
    	Integer mov = 1;
		String ident = identity(employee.getIpf());
		String dni =  Toolkit.fillStringLeft(employee.getIpf(), "0", 10);
		
		String[] fra = formatDate(employee.getFra()); //fecha [dia,mes,año]
		WebClient webClient = getWebClient(certificateInputStream,certificatePassword, certificateType);
    	HtmlPage htmlPage = firstPageAltaBaja(
    			webClient,
				mov, employee.getNss(), employee.getCtaCti().get(),
				employee.getRegime(),  dni, ident, employee.getFra()
    	);

		HtmlForm jacadaForm1 = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("jacadaform")).orElseThrow();
		jacadaForm1.getInputByName("txt_SDFSITAFI_ayuda").setValueAttribute(situation); 
		jacadaForm1.getInputByName("txt_SDFFREALDD").setValueAttribute(fra[0]); 
		jacadaForm1.getInputByName("txt_SDFFREALMM").setValueAttribute(fra[1]); 
		jacadaForm1.getInputByName("txt_SDFFREALAA").setValueAttribute(fra[2]); 
		
		if(!employee.getFrb().isEmpty()) {
			String[] fvac = formatDate(employee.getFrb().get()); // fecha de vacaciones
			jacadaForm1.getInputByName("txt_SDFFFINVDD").setValueAttribute(fvac[0]); 
			jacadaForm1.getInputByName("txt_SDFFFINVMM").setValueAttribute(fvac[1]);
			jacadaForm1.getInputByName("txt_SDFFFINVA").setValueAttribute(fvac[2]); 
		}
		
		HtmlInput btnSubmit1 = htmlPage.querySelector("#Sub2207401004");
		htmlPage = btnSubmit1.click();
		HtmlUnitToolkit.manageStatusCode(htmlPage);
	    return employee;
	}

	private static void movPrevDeleteImpl(final InputStream certificateInputStream, 
			final String certificatePassword, final String certificateType, 
			String situation, String regimen, String ctaCti, String nss, Date fecha) throws Exception  {
		
	    try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)) {
 			Integer mov = "AL".equalsIgnoreCase(situation) ? 0 : 1;
 			//Date
 			String[] fr = formatDate(fecha); //fecha [dia,mes,año]
 			
			HtmlPage htmlPage = webClient.getPage("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR42&E=I&AP=AFIR");
			HtmlUnitToolkit.manageStatusCode(htmlPage);
			
			HtmlForm jacadaForm = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("jacadaform")).orElseThrow();
			//form fist
			jacadaForm.getInputByName("txt_SDFTESORNAF").setValueAttribute(nss.substring(0,2));
			jacadaForm.getInputByName("txt_SDFNUMNAF").setValueAttribute(nss.substring(2));
			jacadaForm.getInputByName("txt_SDFREGCC_ayuda").setValueAttribute(regimen);
			jacadaForm.getInputByName("txt_SDFTESCC").setValueAttribute(ctaCti.substring(0,2));
			jacadaForm.getInputByName("txt_SDFNUMCC").setValueAttribute(ctaCti.substring(2));
			HtmlOption option = (HtmlOption) jacadaForm.querySelectorAll("select[name=cbo_ListaAltasBajas001]>option").get(mov);				
			option.click();
			jacadaForm.getInputByName("txt_SDFDIAB").setValueAttribute(fr[0]); 
			jacadaForm.getInputByName("txt_SDFMESB").setValueAttribute(fr[1]); 
			jacadaForm.getInputByName("txt_SDFAOB").setValueAttribute(fr[2]); 
			HtmlOption option1 = (HtmlOption) jacadaForm.querySelectorAll("select[name=cbo_ListaAltasBajas]>option").get(1);				
			option1.click();
			HtmlInput btnSubmit = htmlPage.querySelector("#Sub2207101004");
			htmlPage = btnSubmit.click();
			HtmlUnitToolkit.manageStatusCode(htmlPage);
			
			//second screen
			HtmlInput btnSubmit1 = htmlPage.querySelector("#Sub2207101004");
			htmlPage = btnSubmit1.click();
			HtmlUnitToolkit.manageStatusCode(htmlPage);
		} 
	}
	
	private static void altaConsolidadaDeleteImpl(final InputStream certificateInputStream, 
			final String certificatePassword, final String certificateType, 
			String situation, String regimen, String ctaCti, String nss) throws Exception  {
		
	    try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)) {
 			
			HtmlPage htmlPage = webClient.getPage("https://w2.seg-social.es/ProsaInternet/OnlineAccess?ARQ.SPM.ACTION=LOGIN&ARQ.SPM.APPTYPE=SERVICE&ARQ.IDAPP=XV24M00E");
			handleSegSocialExceptions(htmlPage);
			
			HtmlForm formDatos = (HtmlForm) HtmlUnitToolkit.wait4(htmlPage, p -> p.getElementById("formDatos")).orElseThrow();
			formDatos.getInputByName("NA5NumSegSocialCompleto").setValueAttribute(nss);
			formDatos.getInputByName("CC1EmpresaAut").setValueAttribute(regimen +  ctaCti);
	
			HtmlOption option = (HtmlOption) formDatos.querySelectorAll("select[name=tipoImpresion]>option").get(2);				
			option.click();
			
			htmlPage = formDatos.getInputByName("SPM.ACC.Confirmar").click();
		
			handleSegSocialExceptions(htmlPage);
		} 
	}
	
	private static Collection<Employee> ipfxnafImpl(final InputStream certificateInputStream, 
			final String certificatePassword, final String certificateType, 
			ArrayList<String> nssList) throws Exception  {
		
		if(nssList.size() >= 7) throw new Exception("nss max 7");
		
	    try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)) {
	      webClient.getOptions().setJavaScriptEnabled(true);
	      webClient.getOptions().setThrowExceptionOnScriptError(false);
	      webClient.setJavaScriptErrorListener(jascriptFunctionExceptionError());
	      HtmlPage htmlPage = webClient.getPage("https://w2.seg-social.es/ProsaInternet/OnlineAccess?ARQ.SPM.ACTION=LOGIN&ARQ.SPM.APPTYPE=SERVICE&ARQ.IDAPP=XV24M00C");
	      HtmlForm formDatos = (HtmlForm) HtmlUnitToolkit.wait4(htmlPage, p -> p.getElementById("FORMULARIO_1")).orElseThrow();
	      
    	  for (int i=0;i<nssList.size();i++) {
    		  String naf = nssList.get(i);
    		  naf  = naf.length() > 10 ? naf.substring(0, 10) : naf;
    		  HtmlInput inpt = formDatos.getInputByName("NA1NumSegSocialSinDC" + Integer.toString((i+1)));
    		  inpt.setValueAttribute(naf);
    	  }
    	  
    	  ArrayList<Employee> employees = new ArrayList<>();
 
    	  Page pageAux = ((HtmlButton)formDatos.querySelector("#ENVIO_3")).click();
    	  
    	  if(pageAux instanceof XmlPage) {
    		    XmlPage xmlPage = (XmlPage)pageAux;
    			DomNodeList<DomNode> employeesHtml = xmlPage.querySelectorAll("trabajador");
    			EmployeeBuilder builder = new EmployeeBuilder();
				for (DomNode employee : employeesHtml) {
					String ipf = employee.querySelector("ip9numdoc").getTextContent();
					String fieldName = employee.querySelector("nombre_completo").getTextContent();
					if(!ipf.isEmpty()) {
						String nss = employee.querySelector("na5numsegsocialcompleto").getTextContent().trim();
						Employee empl = builder
    					.setNss(nss)
    					.setName(fieldName.trim())
    					.setIpf(ipf)
    					.build();
    					employees.add(empl);
					} else if(!fieldName.isEmpty()) {
						throw new Exception(fieldName.trim());
					}
				}
			} else {
				htmlPage = (HtmlPage) pageAux;
				HtmlUnitToolkit.manageStatusCode(htmlPage);
			}
    	  return employees;
		} 
	}
	
	private static Employee nafxipfImpl(final InputStream certificateInputStream, 
			final String certificatePassword, final String certificateType, 
			String ipf, String apellido1, String apellido2) throws Exception  {
		
	    try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)) {
	      webClient.getOptions().setJavaScriptEnabled(true);
	      webClient.getOptions().setThrowExceptionOnScriptError(false);
	      webClient.setJavaScriptErrorListener(jascriptFunctionExceptionError());
	      HtmlPage htmlPage = webClient.getPage("https://w2.seg-social.es/ProsaInternet/OnlineAccess?ARQ.SPM.ACTION=LOGIN&ARQ.SPM.APPTYPE=SERVICE&ARQ.IDAPP=XV24M00D");
	      Integer ident  = 1; //NIF DEFAULT
	      if(identity(ipf).equals("6")) ident = 3; // NIE

	      HtmlForm formDatos = (HtmlForm) HtmlUnitToolkit.wait4(htmlPage, p -> p.getElementById("FORMULARIO_1")).orElseThrow();
     
		  ((HtmlOption) formDatos.querySelectorAll("select[name=tipo]>option").get(ident)).click();	
		  
		  formDatos.getInputByName("ipf6NumeroDocumento").setValueAttribute(ipf);
		  formDatos.getInputByName("primerApellido").setValueAttribute(apellido1);
		  if( apellido2 !=null && !apellido2.trim().isEmpty()) {
			  formDatos.getInputByName("segundoApellido").setValueAttribute(apellido2);
		  } 
		  else {
			  formDatos.getInputByName("checkApellido2").setChecked(true);
		  }
		  
		  EmployeeBuilder builder = new EmployeeBuilder();
	
	  	  Page pageAux = ((HtmlButton)formDatos.querySelector("#ENVIO_2")).click();
    	  if(pageAux instanceof XmlPage) {
    		    XmlPage xmlPage = (XmlPage)pageAux;
    		    DomNode employeeHtml = xmlPage.querySelector("usuario_red");
				String nss = employeeHtml.querySelector("na5numsegsocialcompleto").getTextContent().trim();
	
				if(!nss.isEmpty()) {
					String ipf1 = employeeHtml.querySelector("ip6numero_documento").getTextContent();
					String name = employeeHtml.querySelector("nombre_completo").getTextContent().trim();
					String ident1 = employeeHtml.querySelector("codigo_tipo").getTextContent().trim();
				    builder.setNss(nss).setName(name).setIpf(ipf1).setIdent(Integer.parseInt(ident1));
				} else {
					String messageError = "Sin datos para la consulta";
					DomNode textEl = xmlPage.querySelector("texto");
					if(textEl!=null && textEl.getTextContent()!=null) {
						messageError = textEl.getTextContent().trim();
					}
					throw new Exception(messageError);
				}

			} else {
				htmlPage = (HtmlPage) pageAux;
				HtmlUnitToolkit.manageStatusCode(htmlPage);
			}

    	  return builder.build();
		} 
	}
	
	private static void cambioGrupCtzImpl(final InputStream certificateInputStream, 
			final String certificatePassword, final String certificateType, 
			String ipf, String regimen, String ctaCti, String nss, String grupCtz, Date fecha) throws Exception  {
		
		  String url = "https://w2.seg-social.es/ProsaInternet/OnlineAccess?ARQ.SPM.ACTION=LOGIN&ARQ.SPM.APPTYPE=SERVICE&ARQ.IDAPP=XV24M00B";
		  String fieldValue = "codNuevoGrupo";
		  String fieldDate = "fechaSituacion";
		  pageCambioCatOcupGc(certificateInputStream, certificatePassword, certificateType, ipf, regimen, ctaCti, nss, fecha, grupCtz, fieldValue, fieldDate, url);
	}
	
	private static void cambioOcupacionImpl(final InputStream certificateInputStream, 
			final String certificatePassword, final String certificateType, 
			String ipf, String regimen, String ctaCti, String nss, String ocup, Date fecha) throws Exception  {
		
		  String url = "https://w2.seg-social.es/ProsaInternet/OnlineAccess?ARQ.SPM.ACTION=LOGIN&ARQ.SPM.APPTYPE=SERVICE&ARQ.IDAPP=XV24M00G";
		  String fieldValue = "NueOcu";
		  String fieldDate = "fecSit";
		  pageCambioCatOcupGc(certificateInputStream, certificatePassword, certificateType, ipf, regimen, ctaCti, nss, fecha, ocup, fieldValue, fieldDate, url);
	}
	
	private static void cambioCatProfImpl(final InputStream certificateInputStream, 
			final String certificatePassword, final String certificateType, 
			String ipf, String regimen, String ctaCti, String nss, String cat, Date fecha) throws Exception {
		
		  String url = "https://w2.seg-social.es/ProsaInternet/OnlineAccess?ARQ.SPM.ACTION=LOGIN&ARQ.SPM.APPTYPE=SERVICE&ARQ.IDAPP=XV24M00I";
		  String fieldValue = "NueCat";
		  String fieldDate = "fecSit";
		  pageCambioCatOcupGc(certificateInputStream, certificatePassword, certificateType, ipf, regimen, ctaCti, nss, fecha, cat, fieldValue, fieldDate, url);
	}
	
	private static void pageCambioCatOcupGc(final InputStream certificateInputStream, 
			final String certificatePassword, final String certificateType, 
			String ipf, String regimen, String ctaCti, String nss, Date fecha, String newValue, String fieldValue, String fieldDate, String url) throws Exception  {
		
	    try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)) {
	    	
	      HtmlPage htmlPage = webClient.getPage("https://w2.seg-social.es/ProsaInternet/OnlineAccess?ARQ.SPM.ACTION=LOGIN&ARQ.SPM.APPTYPE=SERVICE&ARQ.IDAPP=XV24M00B");
	      Integer ident  = 1; //NIF DEFAULT
	      if(identity(ipf).equals("6")) ident = 3; // NIE
	      
	      String[] fr = formatDate(fecha); //fecha [dia,mes,año]

	      HtmlForm formDatos = (HtmlForm) HtmlUnitToolkit.wait4(htmlPage, p -> p.getElementById("formDatos")).orElseThrow();
     
		  HtmlOption option = (HtmlOption) formDatos.querySelectorAll("select[name=tipo]>option").get(ident);	
		  option.click();
		
		  formDatos.getInputByName("NA5NumSegSocialCompleto").setValueAttribute(nss);
		  formDatos.getInputByName("IP9NumDoc").setValueAttribute(ipf);
		  formDatos.getInputByName("CC1EmpresaAut").setValueAttribute(regimen +  ctaCti);
		  htmlPage = formDatos.getInputByName("SPM.ACC.CONTINUAR_PANTALLA").click();
		  handleSegSocialExceptions(htmlPage);
		  
		  HtmlForm formDatos2 = (HtmlForm) HtmlUnitToolkit.wait4(htmlPage, p -> p.getElementById("formDatos")).orElseThrow();
		  formDatos2.getInputByName(fieldValue).setValueAttribute(newValue);
		  formDatos2.getInputByName(fieldDate).setValueAttribute(fr[0]+"/"+fr[1]+"/"+fr[2]);
		  htmlPage = formDatos2.getInputByName("SPM.ACC.CONFIRMAR").click();
		  handleSegSocialExceptions(htmlPage);
		} 
	}
	
	private static void handleSegSocialExceptions(HtmlPage htmlPage) throws InvalidDataException{
		try {
			String error=htmlPage.querySelector("#ARQContenMensaje>ul >.mensajeError").getVisibleText();
			if(!error.isEmpty()) {
				throw new InvalidDataException(error);
			}
		} catch (NullPointerException e) {}
		
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
	
	
	private static HtmlPage firstPageAltaBaja(WebClient webClient,
			Integer mov, String nss, String ctaCti, String regimen, String dni, String ident, Date fecha) throws Exception {
		HtmlPage htmlPage = webClient.getPage("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR01&E=I&AP=AFIR");
		HtmlUnitToolkit.manageStatusCode(htmlPage);
		
		HtmlForm form = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("jacadaform")).orElseThrow();
		//form fist
		HtmlOption option = (HtmlOption) form.querySelectorAll("select[name=cbo_ListaAltasBajas]>option").get(mov);				
		option.click();
		form.getInputByName("txt_SDFPROAFI").setValueAttribute(nss.substring(0,2));
		form.getInputByName("txt_SDFCODAFI").setValueAttribute(nss.substring(2));
		form.getInputByName("txt_SDFREGAFI_ayuda").setValueAttribute(regimen);
		form.getInputByName("txt_SDFTIPPFI_ayuda").setValueAttribute(ident);
		form.getInputByName("txt_SDFNUMPFI").setValueAttribute(dni);
		form.getInputByName("txt_SDFTESCTACOT").setValueAttribute(ctaCti.substring(0,2));
		form.getInputByName("txt_SDFCTACOT").setValueAttribute(ctaCti.substring(2));
		HtmlSubmitInput continueIn = form.querySelector("input[value=Continuar]");
		htmlPage = continueIn.click();
		HtmlUnitToolkit.manageStatusCode(htmlPage);
		return htmlPage;
	}
	
	private static WebClient getWebClient(final InputStream certificateInputStream, final String certificatePassword, final String certificateType) throws InvalidCertificateException {
		return HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType);
	}
	
	private static String[] formatDate(Date fecha) {
		String[] arr = new String[3]; 
		String dia="";
		String mes="";
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(fecha);
		String anio = ""+(calendar.get(Calendar.YEAR));
		if(calendar.get(Calendar.DATE)<10) dia="0"+calendar.get(Calendar.DATE);
		else dia=""+calendar.get(Calendar.DATE);
		if((calendar.get(Calendar.MONTH)+1)<10) mes="0"+(calendar.get(Calendar.MONTH)+1);
		else mes=""+(calendar.get(Calendar.MONTH)+1);
		arr[0] = dia;
		arr[1] = mes;
		arr[2] = anio;
		return arr;
	}
	
	private static byte[] getReportAffiliateInAltaImpl(InputStream certificateInputStream, String certificatePassword, String certificateType, String regime, String ccc) throws FailingHttpStatusCodeException, IOException, SegSocialException, InterruptedException {
		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)) {
			ArrayList<String> cccArr = 	Toolkit.splitStringMultiple(ccc,2);

			HtmlPage htmlPage = webClient.getPage("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR64&E=I&AP=AFIR");
			HtmlForm form = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("jacadaform")).orElseThrow();
			form.getInputByName("txt_SDFREG62_ayuda").setValueAttribute(regime);
			form.getInputByName("txt_SDFTESO62").setValueAttribute(cccArr.get(0));
			form.getInputByName("txt_SDFNUM62").setValueAttribute(cccArr.get(1));
		     
			((HtmlOption) form.querySelectorAll("select[name=cbo_ListaTipoImpresion]>option").get(1)).click();	
	
			HtmlSubmitInput continueIn = form.querySelector("input[value=Continuar]");
	
			Page page = continueIn.click();
			if(page.isHtmlPage()) {
				htmlPage = (HtmlPage) page;
				HtmlUnitToolkit.manageStatusCode(htmlPage);
			} else {
				try{
					byte[] pdf = page.getWebResponse().getContentAsStream().readAllBytes();
					return pdf;
				}
				catch(Exception e){throw new InvalidDataException();}
			}
		}
		return null;
	}
	
	private static byte[] getReportAffiliateInMovPrevImpl(InputStream certificateInputStream, String certificatePassword, String certificateType, String regime, String ccc) throws FailingHttpStatusCodeException, IOException, SegSocialException, InterruptedException {
		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)) {
			ArrayList<String> cccArr = 	Toolkit.splitStringMultiple(ccc,2);

			HtmlPage htmlPage = webClient.getPage("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR74&E=I&AP=AFIR");
			HtmlForm form = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("jacadaform")).orElseThrow();
			form.getInputByName("txt_SDFREGENT_ayuda").setValueAttribute(regime);
			form.getInputByName("txt_SDFTESCCCENT").setValueAttribute(cccArr.get(0));
			form.getInputByName("txt_SDFCODCCCENT").setValueAttribute(cccArr.get(1));
		     
			((HtmlOption) form.querySelectorAll("select[name=cbo_ListaTipoImpresion]>option").get(1)).click();	
	
			HtmlSubmitInput continueIn = form.querySelector("input[value=Continuar]");
	
			Page page = continueIn.click();
			if(page.isHtmlPage()) {
				htmlPage = (HtmlPage) page;
				HtmlUnitToolkit.manageStatusCode(htmlPage);
			} else {
				try{
					byte[] pdf = page.getWebResponse().getContentAsStream().readAllBytes();
					return pdf;
				}
				catch(Exception e){throw new InvalidDataException();}
			}
		}
		return null;
	}
	
	public static JavaScriptErrorListener jascriptFunctionExceptionError() {
		return new JavaScriptErrorListener() {
			@Override
			public void warn(String message, String sourceName, int line, String lineSource, int lineOffset) {}
			
			@Override
			public void timeoutError(HtmlPage page, long allowedTime, long executionTime) {}
			
			@Override
			public void scriptException(HtmlPage page, ScriptException scriptException) {}
			
			@Override
			public void malformedScriptURL(HtmlPage page, String url, MalformedURLException malformedURLException) {}
			
			@Override
			public void loadScriptError(HtmlPage page, URL scriptUrl, Exception exception) {}
		};
	}
	
}
