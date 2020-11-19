package solutions.aon.seg.social;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlLabel;
import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import solutions.aon.seg.social.exceptions.SegSocialException;
import solutions.aon.seg.social.exceptions.response.NotRespondingException;
import solutions.aon.seg.social.exceptions.statusCode.ForbiddenException;
import solutions.aon.seg.social.exceptions.statusCode.StatusCodeException;
import solutions.aon.seg.social.objects.Employee;
import solutions.aon.seg.social.objects.Employee.EmployeeBuilder;
import solutions.aon.seg.social.toolkit.HtmlUnitToolkit;

import solutions.aon.seg.social.toolkit.Toolkit;

public class SistemaREDMov {

	//HANDLE EXCEPTIONS OF deleteSecondaryUser
	public static void alta(
			final InputStream certificateInputStream, final String certificatePassword, final String certificateType, 
			String regimen, String ctaCti, String nss, String ident, String ipf, Date fecha, 
			String ocupacion, String mdl_ctz, String convenio, String grup_ctz, String type_cto, String coefparcial
	) throws Exception  {
	    try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)) {
	      	Integer code;
	      	Integer mov; //falta asignar
 			String dia="";
 			String mes="";
 			String dni = ipf;
 			//Date
 			GregorianCalendar calendar = new GregorianCalendar();
 			calendar.setTime(fecha);
 			String anio = ""+(calendar.get(Calendar.YEAR));
 			if(calendar.get(Calendar.DATE)<10) dia="0"+calendar.get(Calendar.DATE);
 			else dia=""+calendar.get(Calendar.DATE);
 			if((calendar.get(Calendar.MONTH)+1)<10) mes="0"+(calendar.get(Calendar.MONTH)+1);
 			else mes=""+(calendar.get(Calendar.MONTH)+1);
 	
 			
			HtmlPage htmlPage = webClient.getPage("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR01&E=I&AP=AFIR");
			HtmlUnitToolkit.manageStatusCode(htmlPage);
			
			HtmlForm jacadaForm = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("jacadaform")).orElseThrow();
			//form fist
			HtmlOption option = (HtmlOption) jacadaForm.querySelectorAll("select[name=cbo_ListaAltasBajas]>option").get(0);				
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
			exceptionSegSocial(htmlPage);
			String situacion = "01";
			HtmlForm jacadaForm1 = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("jacadaform")).orElseThrow();
			jacadaForm1.getInputByName("txt_SDFSITAFI_ayuda").setValueAttribute(situacion); 
			jacadaForm1.getInputByName("txt_SDFFREALDD").setValueAttribute(dia); 
			jacadaForm1.getInputByName("txt_SDFFREALMM").setValueAttribute(mes); 
			jacadaForm1.getInputByName("txt_SDFFREALAA").setValueAttribute(anio); 
			jacadaForm1.getInputByName("txt_SDFGRUCOT_ayuda").setValueAttribute(grup_ctz); 
			jacadaForm1.getInputByName("txt_SDFTICO_ayuda").setValueAttribute(type_cto);
			jacadaForm1.getInputByName("txt_SDFCONVCOL_ayuda").setValueAttribute(convenio); 
			if ("0163" == regimen) {
				jacadaForm1.getInputByName("txt_SDFMODCOTI_ayuda").setValueAttribute(mdl_ctz);
//				HtmlOption tipo_impresion = (HtmlOption) jacadaForm.querySelectorAll("select[name=cbo_ListaTipoImpresion]>option").get(1);				
//				tipo_impresion.click(); //diferido
			} else {
//				HtmlOption tipo_impresion = (HtmlOption) jacadaForm.querySelectorAll("select[name=cbo_ListaTipoImpresion001]>option").get(1);				
//				tipo_impresion.click(); //diferido
				if(coefparcial!=null) jacadaForm1.getInputByName("txt_SDFCOEFCO_ayuda").setValueAttribute(coefparcial); 
				if (ocupacion!=null) jacadaForm1.getInputByName("txt_SDFOCUPACION").setValueAttribute(ocupacion); 
			}
			
			HtmlInput btnSubmit1 = htmlPage.querySelector("#Sub2207401004");
			htmlPage = btnSubmit1.click();
			code = exceptionSegSocial(htmlPage);
			System.out.println(code);
			 
			//System.out.println(htmlPage.asXml());

		} catch (SegSocialException e) {
	    	 e.printStackTrace(); 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}

	public static Employee movprevdelete(final InputStream certificateInputStream, final String certificatePassword, final String certificateType, 
			Employee employee) throws Exception {
		
	    try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)) {
	    	Integer code;
 			String dia="";
 			String mes="";
 			Integer mov = employee.getSituacion() == "Alta" ? 0 : 1;
 			//Date
			GregorianCalendar calendar = new GregorianCalendar();
 			calendar.setTime(employee.getFra());
 			String anio = ""+(calendar.get(Calendar.YEAR));
 			if(calendar.get(Calendar.DATE)<10) dia="0"+calendar.get(Calendar.DATE);
 			else dia=""+calendar.get(Calendar.DATE);
 			if((calendar.get(Calendar.MONTH)+1)<10) mes="0"+(calendar.get(Calendar.MONTH)+1);
 			else mes=""+(calendar.get(Calendar.MONTH)+1);
 	
 			
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
			exceptionSegSocial(htmlPage);
			
			//second screen
			HtmlInput btnSubmit1 = htmlPage.querySelector("#Sub2207101004");
			htmlPage = btnSubmit1.click();
			exceptionSegSocial(htmlPage);
		} catch (SegSocialException e) {
	    	 e.printStackTrace(); 
		}
		return employee;
	}
	
	private static Integer exceptionSegSocial(HtmlPage htmlPage) throws Exception {
		String aviso = htmlPage.querySelector("#DIL").asText();
		Integer code = null;
		if(aviso!=null) {
			code =  Integer.parseInt(aviso.substring(0,4));
			switch (code) {
				case 3408: case 9125: case 9086:case 0350:
					break;
				default:
					throw new Exception(aviso);
			}
		}
		return code;
	}

	public static void main(String[] args) throws InterruptedException {
		try (final FileInputStream certificateInputStream =  new FileInputStream("src/test/resources/solutions/aon/FNMT.p12")) {			
			
			//first screen
			String situacion = "Alta";
			String regimen = "0111";
			String ctaCti = "01105360062";
			String nss = "010022757387";
			String ident = "1";
			String ipf = "016262835H";
			
			//second screen
			Date fecha = Toolkit.parseDate("28-12-2020", "dd-MM-yyyy");
			String ocupacion = "a";
			String coefparcial = "050";
			String mdl_ctz = ""; //modalida de cotz
			String convenio = "";
			String grup_ctz = "";
			String type_cto = "";
			
			
			//alta(certificateInputStream, "jg@FNMT", "pkcs12", regimen, ctaCti, nss, ident, ipf, fecha, ocupacion, mdl_ctz, convenio, grup_ctz, type_cto, coefparcial);
			
			
			//delete mov previos
			EmployeeBuilder builder = new EmployeeBuilder();
			Employee employee = builder.setNss(nss)
			.setSituation(situacion)
			.setNss(nss)
			.setRegime(regimen)
			.setCtaCti(ctaCti)
			.setFra(fecha)
			.build();
			
			movprevdelete(certificateInputStream, "jg@FNMT", "pkcs12", employee);

		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
