package solutions.aon.seg.social;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.Assert;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

import solutions.aon.seg.social.exception.InvalidCertificateException;
import solutions.aon.seg.social.exception.OutOfServiceException;
import solutions.aon.seg.social.exception.SegSocialException;
import solutions.aon.seg.social.exception.StatusCodeException;
import solutions.aon.seg.social.exception.invalid.WrongRegimeException;
import solutions.aon.seg.social.object.SituacionEmpresa;
import solutions.aon.seg.social.object.SituationType;
import solutions.aon.seg.social.toolkit.HtmlUnitToolkit;
import solutions.aon.seg.social.toolkit.Toolkit;

public class AonSegSocialJuanma extends SegSocialException {

	public static void main(String[] args) throws FileNotFoundException {
		String name = "juanma";
		String password = "12345";

		String nss = "461052988590";
		String ident = "173578385M";
		String regimen = "0111";
		String cc = "11122534302";
		String cno = "0020";
		String certificate = "/tmp/AyudaTFNMT.p12";
		File file = new File(certificate);
		final InputStream certificateInputStream = new FileInputStream(file);

		
		System.out.println("Empezamos");
		try {
//			    AonSegSocialJuanma.setCno(name, password);
//			AonSegSocialJuanma.setCnoCertificate(certificateInputStream, nss, ident, regimen, cc, cno);
//			AonSegSocialJuanma.testSetCnoWrongCCC();
//			AonSegSocialJuanma.testSetCnoWrongIdent();
//			AonSegSocialJuanma.testSetCnoWrongRegimen();
//			AonSegSocialJuanma.testSetCnoWrongNSS();
			AonSegSocialJuanma.testSetCnoWrongCno();
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

//		    private static void setCno(String name, String password)
//			    throws FailingHttpStatusCodeException, IOException, InterruptedException {
//			try (WebClient webClient = new WebClient()) {
//
//			    webClient.getOptions().setCssEnabled(false);
//			    webClient.getOptions().setDownloadImages(false);
//			    webClient.setJavaScriptTimeout(10000);
//			    webClient.setAjaxController(new NicelyResynchronizingAjaxController());
//
//			    HtmlPage htmlPage = webClient.getPage("https://redmine.ayudat.es/login");
//
//			    // HtmlUnitToolkit.wait4(htmlPage, p -> p.getElementById("login-submit"));
//			    //
//			    // Este es el codigo para esperar a que el navegador carge la página,
//			    // en este caso como ves esperamos a que se 'construya' el botón de submit.
//			    // Como ves un simple buckle ...
//			    DomElement loginSubmit = null;
//			    for (int i = 0; i < 20; i++) {
//				loginSubmit = htmlPage.getElementById("login-submit");
//				if (loginSubmit != null)
//				    break;
//				synchronized (htmlPage) {
//				    htmlPage.wait(500);
//				}
//			    }
//			    // Algo fue mal y no se ha cargado la página .
//			    if (loginSubmit == null)
//				throw new InterruptedException();
//
//			    // El primer form, creo que sólo hay uno ;
//			    HtmlForm loginForm = null;
//			    for (HtmlForm form : htmlPage.getForms()) {
//				if (form.getActionAttribute().equals("/login")) {
//				    loginForm = form;
//				}
//			    }
////			    // Algo fue mal y no se ha cargado la página .
//			    if (loginForm == null)
//				throw new InterruptedException();
//
//			    // El codigo anterior se peude reemplazar por este otro mucho más elegante.
////			     HtmlForm loginForm = htmlPage.getForms().stream().filter( f ->
////			     f.getActionAttribute().equals("/login")
////			     ).findFirst().orElseThrow(InterruptedException::new);
//
//			    loginForm.getInputByName("username").setValueAttribute(name);
//			    loginForm.getInputByName("password").setValueAttribute(password);
//
//			    loginSubmit.click();
//
//			    // Aqui tenemos que comprobar hemos hecho login realmente , ej : buscando algun
//			    // elemento
//			     System.out.println(htmlPage.asXml());
//
//			}
//		    }

	private static void setCnoCertificate( final InputStream certificateInputStream,String nss, String ident, String regimen, String cc, String cno)
			throws Exception {

		String certificate = "/tmp/AyudaTFNMT.p12";
		File file = new File(certificate);
//		final InputStream certificateInputStream = new FileInputStream(file);
		final String certificatePassword = "123456";
		final String certificateType = "pkcs12";
		System.out.println(certificateInputStream + "hola");
		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword,
				certificateType)) {

			webClient.getOptions().setCssEnabled(false);
			webClient.getOptions().setDownloadImages(false);
			webClient.setJavaScriptTimeout(10000);
			webClient.setAjaxController(new NicelyResynchronizingAjaxController());
			HtmlPage htmlPage = webClient.getPage(
					"https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR55&E=I&AP=AFIR");

			DomElement formSubmit = null;
			for (int i = 0; i < 20; i++) {
				formSubmit = htmlPage.getElementById("Sub2207001004_42");
				if (formSubmit != null)
					break;
				synchronized (htmlPage) {
					htmlPage.wait(500);
				}
			}

			HtmlForm form = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("jacadaform")).orElseThrow();
			form.getInputByName("txt_SDFPROAFI").setValueAttribute(nss.substring(0, 2));
			form.getInputByName("txt_SDFCODAFI").setValueAttribute(nss.substring(2));

			form.getInputByName("txt_SDFTIPPFI_ayuda").setValueAttribute(ident.substring(0, 1));
			form.getInputByName("txt_SDFNUMPFI").setValueAttribute(ident.substring(1));

			form.getInputByName("txt_SDFREGAFI").setValueAttribute(regimen);

			form.getInputByName("txt_SDFTESCTACOT").setValueAttribute(cc.substring(0, 2));
			form.getInputByName("txt_SDFCTACOT").setValueAttribute(cc.substring(2));
			HtmlUnitToolkit.manageStatusCode(htmlPage);

			System.out.println(htmlPage.asXml());

			Page pageaux = ((HtmlSubmitInput) htmlPage.querySelector("input[value=Continuar]")).click();

			String pageAux = pageaux.toString();
			System.out.println(pageaux);

			htmlPage = webClient.getPage(pageAux.substring(9));
			HtmlForm form2 = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("jacadaform")).orElseThrow();

			HtmlUnitToolkit.manageStatusCode(htmlPage);

			for (int i = 0; i < 20; i++) {
				formSubmit = htmlPage.getElementById("Sub2207001004_85");
				if (formSubmit != null)
					break;
				synchronized (htmlPage) {
					htmlPage.wait(500);
				}
			}

			form2.getInputByName("txt_SDFCNOCUP_ayuda").setValueAttribute(cno);
			formSubmit.click();

			System.out.println(htmlPage.asXml());

		}

	}


	@Test
    public static void testSetCnoWrongCCC() throws Exception {
		String certificate = "/tmp/AyudaTFNMT.p12";
		File file = new File(certificate);
        try(final InputStream certificateInputStream = new FileInputStream(file)){
            AonSegSocialJuanma.setCnoCertificate(
                certificateInputStream, 
                "461052988590", //NSS OK
                "173578385M",  // DNI OK
                "0111", // REGIMEN OK  
                "1112253430",    // CCC ERRONEO 
                "0020"   // CNO OK                      
                );
            Assert.fail();
        } catch (SegSocialException e) {
            Assert.assertEquals("3823* CUENTA DE COTIZACION ERRONEA", e.getMessage());
        }
    }
	
	@Test
	public static void testSetCnoWrongIdent() throws Exception{
		String certificate = "/tmp/AyudaTFNMT.p12";
		File file = new File(certificate);
		   try(final InputStream certificateInputStream = new FileInputStream(file)){
	            AonSegSocialJuanma.setCnoCertificate(
	                certificateInputStream, 
	                "461052988590", //NSS OK
	                "17357838",  // DNI ERRONEO
	                "0111", // REGIMEN OK  
	                "11122534302",    // CCC OK 
	                "0020"   // CNO OK  
	                );
	            Assert.fail();
	        } catch (SegSocialException e) {
	            Assert.assertEquals("3595* IDENTIFICADOR ERRONEO", e.getMessage());
	        }
		
	}
	
	@Test
	public static void testSetCnoWrongRegimen() throws Exception{
		String certificate = "/tmp/AyudaTFNMT.p12";
		File file = new File(certificate);
		   try(final InputStream certificateInputStream = new FileInputStream(file)){
	            AonSegSocialJuanma.setCnoCertificate(
	                certificateInputStream, 
	                "461052988590", //NSS OK
	                "173578385M",  //DNI OK
	                "011", // REGIMEN ERRONEO  
	                "11122534302",    // CCC OK 
	                "0020"   // CNO OK
	                      
	                );
	            Assert.fail();
	        } catch (SegSocialException e) {
	            Assert.assertEquals("3605* REGIMEN INCOMPATIBLE", e.getMessage());
	        }
		
	}
	
	@Test
	public static void testSetCnoWrongNSS() throws Exception{
		String certificate = "/tmp/AyudaTFNMT.p12";
		File file = new File(certificate);
		   try(final InputStream certificateInputStream = new FileInputStream(file)){
	            AonSegSocialJuanma.setCnoCertificate(
	                certificateInputStream, 
	                "4610529885", // NSS ERRONEO
	                "173578385M", // DNI OK
	                "0111", // REGIMEN OK  
	                "11122534302",    // CCC OK 
	                "0020"   // CNO OK
	                     
	                );
	            Assert.fail();
	        } catch (SegSocialException e) {
	            Assert.assertEquals("3820* NUMERO DE AFILIADO INCORRECTO", e.getMessage());
	        }
		
	}
	
	@Test
	public static void testSetCnoWrongCno() throws Exception{
		String certificate = "/tmp/AyudaTFNMT.p12";
		File file = new File(certificate);
		   try(final InputStream certificateInputStream = new FileInputStream(file)){
	            AonSegSocialJuanma.setCnoCertificate(
	                certificateInputStream, 
	                "461052988590", // NSS OK
	                "173578385M", // DNI OK
	                "0111", // REGIMEN OK  
	                "11122534302",    // CCC OK 
	                "9999"   // CNO ERRONEO
	                );
	            
	            Assert.fail();
	        } catch (SegSocialException e) {
	            Assert.assertEquals("3820* CNO INCORRECTO", e.getMessage());
	        }
	}
	
	
	
	
	

}
