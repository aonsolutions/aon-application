package solutions.aon.seg.social;

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
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;

import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;

import solutions.aon.seg.social.exceptions.SegSocialException;
import solutions.aon.seg.social.exceptions.certificate.CertificateNotFoundException;
import solutions.aon.seg.social.exceptions.certificate.InvalidCertificateException;
import solutions.aon.seg.social.exceptions.invalidData.InvalidDataException;
import solutions.aon.seg.social.exceptions.statusCode.StatusCodeException;
import solutions.aon.seg.social.objects.Employee;
import solutions.aon.seg.social.objects.Employee.EmployeeBuilder;
import solutions.aon.seg.social.toolkit.HtmlUnitToolkit;
import solutions.aon.seg.social.toolkit.Toolkit;


public class SistemaREDMov {

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
	
	private static Employee sendAltaImpl(
			final InputStream certificateInputStream, final String certificatePassword, final String certificateType, 
			Employee employee
	) throws SegSocialException, IOException, InterruptedException  {
	    	String situation = "01";
	    	Integer mov = 0;
			String ident = identity(employee.getIpf());
			String dni =  Toolkit.fillStringLeft(employee.getIpf(), "0", 10);
 			String[] fra = formatDate(employee.getFra());
	    	HtmlPage htmlPage = first_page_alta_baja(
					certificateInputStream,certificatePassword, certificateType,
					mov,  employee.getNss(), employee.getCtaCti().get(),
					employee.getRegime(),  dni, ident, employee.getFra()
	    	);

			HtmlForm jacadaForm1 = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("jacadaform")).orElseThrow();
			jacadaForm1.getInputByName("txt_SDFSITAFI_ayuda").setValueAttribute(situation); 
			jacadaForm1.getInputByName("txt_SDFFREALDD").setValueAttribute(fra[0]); 
			jacadaForm1.getInputByName("txt_SDFFREALMM").setValueAttribute(fra[1]); 
			jacadaForm1.getInputByName("txt_SDFFREALAA").setValueAttribute(fra[2]); 
			jacadaForm1.getInputByName("txt_SDFGRUCOT_ayuda").setValueAttribute(employee.getGc().get()); 
			jacadaForm1.getInputByName("txt_SDFTICO_ayuda").setValueAttribute(employee.getContract().get());
			jacadaForm1.getInputByName("txt_SDFCONVCOL_ayuda").setValueAttribute( employee.getColec() ); 
			
			if ("0163" == employee.getRegime() && !employee.getMdctz().isEmpty()) {
				jacadaForm1.getInputByName("txt_SDFMODCOTI_ayuda").setValueAttribute(employee.getMdctz().get());
			} else {
				if(!employee.getCoef().isEmpty()) jacadaForm1.getInputByName("txt_SDFCOEFCO_ayuda").setValueAttribute(employee.getCoef().get().toString()); 
				if (employee.getOcup()!=null) jacadaForm1.getInputByName("txt_SDFOCUPACION").setValueAttribute(employee.getOcup()); 
			}

			HtmlInput btnSubmit1 = htmlPage.querySelector("#Sub2207401004");
			htmlPage = btnSubmit1.click();
			HtmlUnitToolkit.manageStatusCode(htmlPage);
			
			DomNode msg1 = htmlPage.querySelector("#Sub0000201056");
			if(msg1!=null && msg1.getTextContent().trim().indexOf("LA MECANIZACION DE ESTE TIPO DE REGISTROS PUEDE IMPLICAR") !=-1) {
				HtmlInput btnSubmit2 = htmlPage.querySelector("input[value=\"Continuar\"]");
				htmlPage = btnSubmit2.click();
				HtmlUnitToolkit.manageStatusCode(htmlPage);
			}
			
			DomNode msg2 = htmlPage.querySelector("#Sub0600401054");
			if(msg2!=null && msg2.getTextContent().trim().indexOf("Revise el contenido del coeficiente a tiempo parcial") !=-1) {
				HtmlInput btnSubmit3 = htmlPage.querySelector("input[value=\"Continuar\"]");
				htmlPage = btnSubmit3.click();
				HtmlUnitToolkit.manageStatusCode(htmlPage);
			}			
	
			return employee;
	}

	private static Employee sendBajaImpl(
			final InputStream certificateInputStream, final String certificatePassword, final String certificateType, 
			Employee employee
	) throws SegSocialException, IOException, InterruptedException  {
    	String situation = "63";
    	Integer mov = 1;
		String ident = identity(employee.getIpf());
		String dni =  Toolkit.fillStringLeft(employee.getIpf(), "0", 10);
		
		String[] fra = formatDate(employee.getFra()); //fecha real de baja [dia,mes,año]
		
    	HtmlPage htmlPage = first_page_alta_baja(
				certificateInputStream,certificatePassword, certificateType,
				mov,  employee.getNss(), employee.getCtaCti().get(),
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
			String situation, String regimen, String ctaCti, String nss, Date fecha) throws SegSocialException, IOException, InterruptedException  {
		
	    try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)) {
 			Integer mov = "AL".equalsIgnoreCase(situation) ? 0 : 1;
 			//Date
 			String[] fr = formatDate(fecha); //fecha real de baja [dia,mes,año]
 			
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
			String situation, String regimen, String ctaCti, String nss) throws SegSocialException, IOException, InterruptedException  {
		
	    try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)) {
 			
			HtmlPage htmlPage = webClient.getPage("https://w2.seg-social.es/ProsaInternet/OnlineAccess?ARQ.SPM.ACTION=LOGIN&ARQ.SPM.APPTYPE=SERVICE&ARQ.IDAPP=XV24M00E");
			handleSegSocialExceptions(htmlPage);
			
			HtmlForm formDatos = (HtmlForm) HtmlUnitToolkit.wait4(htmlPage, p -> p.getElementById("formDatos")).orElseThrow();
			//form fist
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
	    	
	      HtmlPage htmlPage = webClient.getPage("https://w2.seg-social.es/ProsaInternet/OnlineAccess?ARQ.SPM.ACTION=LOGIN&ARQ.SPM.APPTYPE=SERVICE&ARQ.IDAPP=XV24M00C");
	      HtmlForm formDatos = (HtmlForm) HtmlUnitToolkit.wait4(htmlPage, p -> p.getElementById("formDatos")).orElseThrow();
	      
    	  for (int i=0;i<nssList.size();i++) {
    		   formDatos.getInputByName("NA1NumSegSocialSinDC" + Integer.toString((i+1))).setValueAttribute(nssList.get(i).substring(0, 10));
    	  }
    	  htmlPage = formDatos.getInputByName("SPM.ACC.Consultar").click();
    	  
    	  HtmlTable table = htmlPage.querySelector("#ARQcapaPrincipal > fieldset > table");
    	  String nombre;
    	  String ipf;
    	  String nss;
    	  ArrayList<Employee> employees = new ArrayList<>();
    	  
    	  EmployeeBuilder builder = new EmployeeBuilder();
    	  for (int i = 1; i < table.getRowCount(); i++) {
    		  nss = table.getRow(i).getCell(1).getTextContent().trim().replaceAll("\u200b", ""); //nss
    		  ipf = table.getRow(i).getCell(2).getVisibleText().trim().replaceAll("\u200b", "").replaceAll("\\s",""); //ipf
    		  nombre = table.getRow(i).getCell(3).getTextContent().trim().replaceAll("\u200b", "");
			  if(!nombre.isEmpty() && !ipf.isEmpty()) {
					Employee employee = builder
					.setNss(nss)
					.setName(nombre)
					.setIpf(ipf)
					.build();
					employees.add(employee);
			  }
    	  }
    	  return employees;
		} 
	}
	
	private static Employee nafxipfImpl(final InputStream certificateInputStream, 
			final String certificatePassword, final String certificateType, 
			String ipf, String apellido1, String apellido2) throws Exception  {
		
	    try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)) {
	    	
	      HtmlPage htmlPage = webClient.getPage("https://w2.seg-social.es/ProsaInternet/OnlineAccess?ARQ.SPM.ACTION=LOGIN&ARQ.SPM.APPTYPE=SERVICE&ARQ.IDAPP=XV24M00D");
	      Integer ident  = 1; //NIF DEFAULT
	      if(identity(ipf).equals("6")) ident = 3; // NIE

	      HtmlForm formDatos = (HtmlForm) HtmlUnitToolkit.wait4(htmlPage, p -> p.getElementById("formDatos")).orElseThrow();
     
		  HtmlOption option = (HtmlOption) formDatos.querySelectorAll("select[name=tipo]>option").get(ident);	
		  option.click();
		  
		  formDatos.getInputByName("ipf6NumeroDocumento").setValueAttribute(ipf);
		  formDatos.getInputByName("primerApellido").setValueAttribute(apellido1);
		  if( apellido2 !=null && !apellido2.trim().isEmpty()) {
			  formDatos.getInputByName("segundoApellido").setValueAttribute(apellido2);
		  } 
		  else {
			  formDatos.getInputByName("checkApellido2").setChecked(true);
		  }
		  
    	  htmlPage = formDatos.getInputByName("SPM.ACC.Continuar").click();
    	  handleSegSocialExceptions(htmlPage);
   
    	  String nss = htmlPage.querySelector("#ARQcapaPrincipal > fieldset > div:nth-child(4) > div > p > span:nth-child(2)").getTextContent().trim();
    	  String name = htmlPage.querySelector("#ARQcapaPrincipal > fieldset > div:nth-child(3) > div > p > span:nth-child(2)").getTextContent().trim();
    	  EmployeeBuilder builder = new EmployeeBuilder();
    	  Employee employee = builder
			.setNss(nss)
			.setName(name)
			.build();
    	  return employee;
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
	
	private static HtmlPage first_page_alta_baja(final InputStream certificateInputStream, final String certificatePassword, final String certificateType, 
			Integer mov, String nss, String ctaCti, String regimen, String dni, String ident, Date fecha) throws SegSocialException, IOException, InterruptedException 
	{
		 try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)) {

				
			HtmlPage htmlPage = webClient.getPage("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR01&E=I&AP=AFIR");
			HtmlUnitToolkit.manageStatusCode(htmlPage);
			
			HtmlForm jacadaForm = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("jacadaform")).orElseThrow();
			//form fist
			HtmlOption option = (HtmlOption) jacadaForm.querySelectorAll("select[name=cbo_ListaAltasBajas]>option").get(mov);				
			option.click();
			jacadaForm.getInputByName("txt_SDFPROAFI").setValueAttribute(nss.substring(0,2));
			jacadaForm.getInputByName("txt_SDFCODAFI").setValueAttribute(nss.substring(2));
			jacadaForm.getInputByName("txt_SDFREGAFI_ayuda").setValueAttribute(regimen);
			jacadaForm.getInputByName("txt_SDFTIPPFI_ayuda").setValueAttribute(ident);
			jacadaForm.getInputByName("txt_SDFNUMPFI").setValueAttribute(dni);
			jacadaForm.getInputByName("txt_SDFTESCTACOT").setValueAttribute(ctaCti.substring(0,2));
			jacadaForm.getInputByName("txt_SDFCTACOT").setValueAttribute(ctaCti.substring(2));
	
			HtmlInput btnSubmit = htmlPage.querySelector("#Sub2207401004");
			htmlPage = btnSubmit.click();
			HtmlUnitToolkit.manageStatusCode(htmlPage);
			return htmlPage;
		}
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
	
	public static void main(String[] args)  {
//		try (final FileInputStream certificateInputStream =  new FileInputStream("src/test/resources/solutions/aon/FNMT.p12")) {			
//				String certificatePassword = "jg@FNMT";
//				String certificateType = "pkcs12";
////			String regimen = "0111";
////	     	String ctaCti = "01105360062";
////	     	String nss = "010022757387";
//				String ipf = "16262835H";
//				String apellido1 = "garcia";
//				String apellido2 = "perez";
//				String ipf = "y7514970x";
//				String apellido1 = "vasquez";
//				String apellido2 = "beauperthuy";
//				System.out.println(nafxipf(certificateInputStream, certificatePassword, certificateType, ipf, apellido1, apellido2));
////	     	Date fecha = Toolkit.parseDate("29-12-2020", "dd-MM-yyyy");
////		
////				EmployeeBuilder builder = new EmployeeBuilder();
////				Employee employee = builder
////				.setRegime(regimen)
////				.setCtaCti(ctaCti)
////				.setNss(nss)
////				.setIpf(ipf)
////				.setFra(fecha)
////				.build();
////			    String certificatePassword = "jg@FNMT";
////			    String certificateType = "pkcs12";
////				
////			    sendBaja(certificateInputStream, certificatePassword, certificateType, employee);
////			    
////			    System.out.println("Baja realizada");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}

}
