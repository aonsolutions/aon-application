package solutions.aon.seg.social;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Calendar;

import java.util.GregorianCalendar;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;

import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import solutions.aon.seg.social.exceptions.SegSocialException;
import solutions.aon.seg.social.exceptions.certificate.CertificateNotFoundException;
import solutions.aon.seg.social.exceptions.certificate.InvalidCertificateException;

import solutions.aon.seg.social.exceptions.statusCode.StatusCodeException;
import solutions.aon.seg.social.objects.Employee;
import solutions.aon.seg.social.toolkit.HtmlUnitToolkit;
import solutions.aon.seg.social.toolkit.Toolkit;

public class SistemaREDMov {

	//HANDLE THE EXCEPTIONS OF ALTA METHOD
	public static Employee sendMov(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, Employee employee) throws SegSocialException{
		
		InvalidCertificateException.checkCertificate(certificateInputStream);
		
		try {return sendMovImpl(certificateInputStream, certificatePassword, certificateType, employee);} 
		catch (FailingHttpStatusCodeException e) {StatusCodeException.HandleStatusCodeException(e);} 
		catch (MalformedURLException e) {throw new SegSocialException(e);} 
		catch (IOException e) {throw new CertificateNotFoundException();} 
		catch (InterruptedException e) {throw new SegSocialException(e);}
		catch (Exception e) {throw new SegSocialException(e);}
		return null;
	}
	
	public static Employee movPrevDelete(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, Employee employee) throws SegSocialException{
		
		InvalidCertificateException.checkCertificate(certificateInputStream);
		
		try {return movPrevDeleteImpl(certificateInputStream, certificatePassword, certificateType, employee);} 
		catch (FailingHttpStatusCodeException e) {StatusCodeException.HandleStatusCodeException(e);} 
		catch (MalformedURLException e) {throw new SegSocialException(e);} 
		catch (IOException e) {throw new CertificateNotFoundException();} 
		catch (InterruptedException e) {throw new SegSocialException(e);}
		catch (Exception e) {throw new SegSocialException(e);}
		return null;
	}
	
	
	private static Employee sendMovImpl(
			final InputStream certificateInputStream, final String certificatePassword, final String certificateType, 
			Employee employee
	) throws SegSocialException, IOException, InterruptedException  {
	    try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)) {
	      	Integer mov = employee.getSituacion() == "AL" ? 0 : 1;
 			String dia="";
 			String mes="";
 			String dni =  padCharacter("0", 10, employee.getIpf());
 			System.out.println(dni);
 			String nss = employee.getNss();
 			String ctaCti = employee.getCtaCti().get();
 			
 			String ident = identity(employee.getIpf());
 			//Date
 			GregorianCalendar calendar = new GregorianCalendar();
 			calendar.setTime(employee.getFra());
 			String anio = ""+(calendar.get(Calendar.YEAR));
 			if(calendar.get(Calendar.DATE)<10) dia="0"+calendar.get(Calendar.DATE);
 			else dia=""+calendar.get(Calendar.DATE);
 			if((calendar.get(Calendar.MONTH)+1)<10) mes="0"+(calendar.get(Calendar.MONTH)+1);
 			else mes=""+(calendar.get(Calendar.MONTH)+1);
 	
 			
			HtmlPage htmlPage = webClient.getPage("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR01&E=I&AP=AFIR");
			HtmlUnitToolkit.manageStatusCode(htmlPage);
			
			HtmlForm jacadaForm = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("jacadaform")).orElseThrow();
			//form fist
			HtmlOption option = (HtmlOption) jacadaForm.querySelectorAll("select[name=cbo_ListaAltasBajas]>option").get(mov);				
			option.click();
			jacadaForm.getInputByName("txt_SDFPROAFI").setValueAttribute(nss.substring(0,2));
			jacadaForm.getInputByName("txt_SDFCODAFI").setValueAttribute(nss.substring(2));
			jacadaForm.getInputByName("txt_SDFREGAFI_ayuda").setValueAttribute(employee.getRegime());
			jacadaForm.getInputByName("txt_SDFTIPPFI_ayuda").setValueAttribute(ident);
			jacadaForm.getInputByName("txt_SDFNUMPFI").setValueAttribute(dni);
			jacadaForm.getInputByName("txt_SDFTESCTACOT").setValueAttribute(ctaCti.substring(0,2));
			jacadaForm.getInputByName("txt_SDFCTACOT").setValueAttribute(ctaCti.substring(2));
	
			HtmlInput btnSubmit = htmlPage.querySelector("#Sub2207401004");
			htmlPage = btnSubmit.click();
			HtmlUnitToolkit.manageStatusCode(htmlPage);
			String situacion = "01";
			HtmlForm jacadaForm1 = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("jacadaform")).orElseThrow();
			jacadaForm1.getInputByName("txt_SDFSITAFI_ayuda").setValueAttribute(situacion); 
			jacadaForm1.getInputByName("txt_SDFFREALDD").setValueAttribute(dia); 
			jacadaForm1.getInputByName("txt_SDFFREALMM").setValueAttribute(mes); 
			jacadaForm1.getInputByName("txt_SDFFREALAA").setValueAttribute(anio); 
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
		} 
	    return employee;
	}

	private static Employee movPrevDeleteImpl(final InputStream certificateInputStream, 
			final String certificatePassword, final String certificateType, 
			Employee employee) throws SegSocialException, IOException, InterruptedException  {
		
	    try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)) {
	    	Integer code;
 			String dia="";
 			String mes="";
 			Integer mov = employee.getSituacion().equals("AL") ? 0 : 1;
 			//Date
			GregorianCalendar calendar = new GregorianCalendar();
 			calendar.setTime(employee.getFra());
 			String anio = ""+(calendar.get(Calendar.YEAR));
 			if(calendar.get(Calendar.DATE)<10) dia="0"+calendar.get(Calendar.DATE);
 			else dia=""+calendar.get(Calendar.DATE);
 			if((calendar.get(Calendar.MONTH)+1)<10) mes="0"+(calendar.get(Calendar.MONTH)+1);
 			else mes=""+(calendar.get(Calendar.MONTH)+1);
 			System.out.println(dia +"/"+mes+ "/"+anio);
 			
			HtmlPage htmlPage = webClient.getPage("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR42&E=I&AP=AFIR");
			HtmlUnitToolkit.manageStatusCode(htmlPage);
			
			HtmlForm jacadaForm = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("jacadaform")).orElseThrow();
			//form fist
			jacadaForm.getInputByName("txt_SDFTESORNAF").setValueAttribute(employee.getNss().substring(0,2));
			jacadaForm.getInputByName("txt_SDFNUMNAF").setValueAttribute(employee.getNss().substring(2));
			jacadaForm.getInputByName("txt_SDFREGCC_ayuda").setValueAttribute(employee.getRegime());
			jacadaForm.getInputByName("txt_SDFTESCC").setValueAttribute(employee.getCtaCti().get().substring(0,2));
			jacadaForm.getInputByName("txt_SDFNUMCC").setValueAttribute(employee.getCtaCti().get().substring(2));
			HtmlOption option = (HtmlOption) jacadaForm.querySelectorAll("select[name=cbo_ListaAltasBajas001]>option").get(mov);				
			option.click();
			jacadaForm.getInputByName("txt_SDFDIAB").setValueAttribute(dia); 
			jacadaForm.getInputByName("txt_SDFMESB").setValueAttribute(mes); 
			jacadaForm.getInputByName("txt_SDFAOB").setValueAttribute(anio); 
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
		return employee;
	}
	
	private static Employee movConsolidadoDeleteImpl(final InputStream certificateInputStream, 
			final String certificatePassword, final String certificateType, 
			Employee employee) throws SegSocialException, IOException, InterruptedException  {
		
	    try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)) {
	    	Integer code;
 			String dia="";
 			String mes="";
 			Integer mov = employee.getSituacion().equals("AL") ? 0 : 1;
 			//Date
			GregorianCalendar calendar = new GregorianCalendar();
 			calendar.setTime(employee.getFra());
 			String anio = ""+(calendar.get(Calendar.YEAR));
 			if(calendar.get(Calendar.DATE)<10) dia="0"+calendar.get(Calendar.DATE);
 			else dia=""+calendar.get(Calendar.DATE);
 			if((calendar.get(Calendar.MONTH)+1)<10) mes="0"+(calendar.get(Calendar.MONTH)+1);
 			else mes=""+(calendar.get(Calendar.MONTH)+1);
 			System.out.println(dia +"/"+mes+ "/"+anio);
 			
			HtmlPage htmlPage = webClient.getPage("https://w2.seg-social.es/ProsaInternet/OnlineAccess?ARQ.SPM.ACTION=LOGIN&ARQ.SPM.APPTYPE=SERVICE&ARQ.IDAPP=XV24M00E");
			HtmlUnitToolkit.manageStatusCode(htmlPage);
			
			HtmlForm jacadaForm = (HtmlForm) HtmlUnitToolkit.wait4(htmlPage, p -> p.getElementById("formDatos")).orElseThrow();
			//form fist
			jacadaForm.getInputByName("NA5NumSegSocialCompleto").setValueAttribute(employee.getNss());
			jacadaForm.getInputByName("CC1EmpresaAut").setValueAttribute(employee.getRegime() + employee.getCtaCti().get());
	
			HtmlOption option = (HtmlOption) jacadaForm.querySelectorAll("select[name=tipoImpresion]>option").get(2);				
			option.click();
		
			HtmlInput btnSubmit = htmlPage.querySelector("#SPM.ACC.Confirmar");
			htmlPage = btnSubmit.click();

			
			//HtmlUnitToolkit.manageErrorMessages(htmlPage);
			
		} 
		return employee;
	}
	
	private static String padCharacter(String c, int num, String str){
	    for(int i=0;i<num-str.length()+1;i++){str = c+str;}
	     return str;
	}
	
	
	private static String identity(String ipf) {
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
//	public static void main(String[] args)  {
//		try (final FileInputStream certificateInputStream =  new FileInputStream("src/test/resources/solutions/aon/FNMT.p12")) {			
//		    
//			String certificatePassword = "jg@FNMT";
//		    String certificateType = "pkcs12";
//			//first screen
//			String situacion = "Alta";
//			String regimen = "0111";
//			String ctaCti = "01105360062";
//			String nss = "010022757387";
//			Integer ident = Integer.parseInt("1");
//			String ipf = "016262835H";
//			
//			//second screen
//			Date fecha = Toolkit.parseDate("28-12-2020", "dd-MM-yyyy");
//			String ocupacion = "a";
//			String coefparcial = null;
//			String convenio = "99001355011983";
//			String grup_ctz = "03";
//			String type_cto = "402";
//			String md_ctz = ""; //para regimen agrario
//			
//			EmployeeBuilder builder = new EmployeeBuilder();
//			Employee employee = builder.setSituation(situacion)
//			.setRegime(regimen)
//			.setCtaCti(ctaCti)
//			.setNss(nss)
//			.setIdent(ident)
//			.setIpf(ipf)
//			.setFra(fecha)
//			.setOcup(ocupacion)
//			.setCoef(coefparcial)
//			.setColec(convenio)
//			.setGc(grup_ctz)
//			.setContract(type_cto)
//			.setMdctz(md_ctz)
//			.build();
//			
//			alta(certificateInputStream, certificatePassword, certificateType, employee);
//			
//			
//			//delete mov previos
////			EmployeeBuilder builder = new EmployeeBuilder();
////			Employee employee = builder.setSituation(situacion)
////			.setRegime(regimen)
////			.setCtaCti(ctaCti)
////			.setNss(nss)
////			.setFra(fecha)
////			.build();
////			movprevdelete(certificateInputStream, certificatePassword, certificateType, employee);
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			System.out.println(e.getMessage());
//		}
//	}

}
