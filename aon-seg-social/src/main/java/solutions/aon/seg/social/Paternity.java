package solutions.aon.seg.social;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.Optional;

import javax.xml.transform.TransformerException;

import org.htmlunit.ElementNotFoundException;
import org.htmlunit.FailingHttpStatusCodeException;
import org.htmlunit.WebClient;
import org.htmlunit.html.DomNode;
import org.htmlunit.html.HtmlAnchor;
import org.htmlunit.html.HtmlButton;
import org.htmlunit.html.HtmlForm;
import org.htmlunit.html.HtmlInput;
import org.htmlunit.html.HtmlPage;
import org.htmlunit.html.HtmlTable;
import org.htmlunit.xml.XmlPage;

import solutions.aon.seg.social.exception.CertificateNotFoundException;
import solutions.aon.seg.social.exception.InvalidCertificateException;
import solutions.aon.seg.social.exception.PaternityException;
import solutions.aon.seg.social.exception.SegSocialException;
import solutions.aon.seg.social.exception.StatusCodeException;
import solutions.aon.seg.social.exception.invalid.InvalidDataException;
import solutions.aon.seg.social.exception.invalid.UnfilledMandatory;
import solutions.aon.seg.social.toolkit.HtmlUnitToolkit;
import solutions.aon.seg.social.toolkit.Toolkit;

public class Paternity {
	// Toolkit.buildFile(htmlPage.asXml().getBytes(),
	// System.getProperty("user.home")+"/testPaternity.html");

	private Paternity() {
		throw new IllegalStateException("Utility class");
	}

	private static final String DATE_FORMAT = "dd/MM/yyyy";

	static final String ADOPTERS = "Adopción/Tutela/Acogimiento";
	static final String BASE_URL = "https://w2.seg-social.es/ProsaInternet/OnlineAccess?ARQ.SPM.ACTION=LOGIN&ARQ.SPM.APPTYPE=SERVICE&ARQ.IDAPP=XV23H101";

	// Gets the pdf of the first element of the query
	public static byte[] getCertificatePdf(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, final String nss, final String regime, final String ccc, final Date dateFrom,
			final Date dateTo, final Optional<Date> startDate) throws SegSocialException {

		InvalidCertificateException.checkCertificate(certificateInputStream);
		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)){
			
			webClient.getOptions().setUseInsecureSSL(true);
			webClient.getOptions().setRedirectEnabled(true);
			webClient.getOptions().setJavaScriptEnabled(true);
			
			XmlPage xmlPage = webClient.getPage("https://w2.seg-social.es/ProsaInternet/OnlineAccess?ARQ.SPM.ACTION=LOGIN&ARQ.SPM.APPTYPE=SERVICE&ARQ.IDAPP=XV23H101");
			HtmlPage htmlPage = HtmlUnitToolkit.transformXmlPage(xmlPage);
			
			// Entrar en Consultar
			htmlPage = HtmlUnitToolkit.transformXmlPage(htmlPage.getElementById("Consulta").click());
			
			HtmlForm formDatos = (HtmlForm) htmlPage.getElementById("FORMULARIO_2");
			// REGIME
			formDatos.getInputByName("regimen").setValue(regime);
			// CCC
			formDatos.getInputByName("ccc").setValue(ccc);
			// NAF
			formDatos.getInputByName("naf").setValue(nss);
			// START DATE (OPTIONAL)
			if (!startDate.isEmpty())
				formDatos.getInputByName("fechaInicio")
						.setValue(Toolkit.formatDate(startDate.get(), DATE_FORMAT).get());


			// SUBMIT
			htmlPage = HtmlUnitToolkit.transformXmlPage(htmlPage.getElementById("ENVIO_4").click());

			// CHECKING IF THE PAGE THREW RESULTS
			try {
				
				HtmlTable resultTable = (HtmlTable) HtmlUnitToolkit.wait4(htmlPage, p -> p.getElementById("TABLA_9")).orElseThrow();
				HtmlInput firstOpt = (HtmlInput) resultTable.querySelector("input[name='isn']");
				firstOpt.click();
				
				// Ver detalle
				htmlPage = HtmlUnitToolkit.transformXmlPage(htmlPage.getElementById("ENVIO_10").click());
				
				// Imprimir
				HtmlButton docButton = (HtmlButton) HtmlUnitToolkit.wait4(htmlPage, p -> p.getElementById("ENVIO_12")).orElseThrow();
				htmlPage = HtmlUnitToolkit.transformXmlPage(docButton.click());
				
				HtmlAnchor anchor = htmlPage.getElementById("CONTENEDOR_prevdocumentoseinformes").querySelector("a");
				
				InputStream is = anchor.click().getWebResponse().getContentAsStream();
				byte[] ret = is.readAllBytes();
				is.close();
				return ret;

			} catch (NullPointerException | ElementNotFoundException | InterruptedException e) {
				checkErrors(htmlPage);
			}

		} catch (FailingHttpStatusCodeException e) {
			StatusCodeException.HandleStatusCodeException(e);
		} catch (MalformedURLException e) {
			throw new SegSocialException(e);
		} catch (IOException e) {
			throw new CertificateNotFoundException();
		} catch (NoSuchElementException e) {
			throw new PaternityException();
		} catch (StringIndexOutOfBoundsException e) {
			throw new UnfilledMandatory();
		} catch (TransformerException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;
	}

	private static void checkErrors(HtmlPage htmlPage) throws InvalidDataException {
		DomNode errors = htmlPage.querySelector("#ARQContenMensajePest>ul>.mensajeError[title='Error']");
		if (errors != null && !errors.getVisibleText().contains("NO HAY DATOS"))
			throw new InvalidDataException(errors.getVisibleText());
	}

}
