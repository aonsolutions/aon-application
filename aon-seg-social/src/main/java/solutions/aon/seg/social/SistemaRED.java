package solutions.aon.seg.social;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Function;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

public class SistemaRED {

	public SistemaRED() {
		// TODO Auto-generated constructor stub
	}

	public static void getITs(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String regime, String ccc, Optional<String> naf, Optional<Date> from,
			Optional<Date> to)
			throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
		try (WebClient webClient = getWebClient(certificateInputStream, certificatePassword, certificateType)) {

			HtmlPage htmlPage = webClient.getPage("https://w2.seg-social.es/isincaA/inicio.do");

			//           -----------------
			// Click on | Consulta partes |
			//           -----------------
			// htmlPage = htmlPage.getAnchorByHref("/isincaA/menu.do?opcion=C").click();
			 htmlPage = wait4(htmlPage, p -> p.getAnchorByHref("/isincaA/menu.do?opcion=C")).orElseThrow().click();

			//HtmlForm buscaPartesForm = htmlPage.getFormByName("BuscaPartesForm");
			HtmlForm buscaPartesForm = wait4(htmlPage, p -> p.getFormByName("BuscaPartesForm")).orElseThrow();
			
			// Regimen* :
			buscaPartesForm.getInputByName("regimen").setValueAttribute(regime);
			// C.C.C.* :
			buscaPartesForm.getInputByName("ccc1").setValueAttribute(ccc.substring(0, 2));
			buscaPartesForm.getInputByName("ccc2").setValueAttribute(ccc.substring(2));
			// Fecha desde :			
			buscaPartesForm.getInputByName("fechaDesde_dd").setValueAttribute("01");
			buscaPartesForm.getInputByName("fechaDesde_mm").setValueAttribute("01");
			buscaPartesForm.getInputByName("fechaDesde_aa").setValueAttribute("2015");
			// Fecha hasta :	
			buscaPartesForm.getInputByName("fechaHasta_dd").setValueAttribute("01");
			buscaPartesForm.getInputByName("fechaHasta_mm").setValueAttribute("01");
			buscaPartesForm.getInputByName("fechaHasta_aa").setValueAttribute("2020");

			htmlPage = buscaPartesForm.getInputByValue("Continuar").click();
//			HtmlInput continuarInput = (HtmlInput) buscaPartesForm
//					.getByXPath("div[@id='botonesANULAR']//input[@type='submit'][@value='Continuar']").get(0);
			
			

			System.out.println(htmlPage.asXml());

		}
	}
	
	
	
	
	public static <HtmlPage, R> Optional<R> wait4(HtmlPage htmlPage, Function<HtmlPage, R> function) throws InterruptedException {
		// try 20 times to wait .5 second each for filling the page.
		for (int i = 0; i < 20; i++) {
			R r = function.apply(htmlPage);
			if (r != null) {
				return Optional.of(r);
			}
			synchronized (htmlPage) {
				htmlPage.wait(500);
			}
		}
		return Optional.empty();
	}
	
	
	
	private static WebClient getWebClient(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType) {
		try (final WebClient webClient = new WebClient(BrowserVersion.BEST_SUPPORTED);) {
			webClient.getOptions().setCssEnabled(false);
			webClient.setAjaxController(new NicelyResynchronizingAjaxController());
			webClient.getOptions().setSSLClientCertificate(certificateInputStream, certificatePassword,
					certificateType);
			return webClient;

		}
	}

	public static void main(String[] args)
			throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
		try (final InputStream certificateInputStream = new FileInputStream(args[0])) {
			getITs(certificateInputStream, "jg@FNMT", "pkcs12", "0111", "01105360062", Optional.empty(), Optional.empty(),Optional.empty());
		}
	}

}
