package solutions.aon.circe;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.security.KeyStore;

import org.htmlunit.FailingHttpStatusCodeException;
import org.htmlunit.WebClient;
import org.htmlunit.html.DomNode;
import org.htmlunit.html.DomNodeList;
import org.htmlunit.html.HtmlButton;
import org.htmlunit.html.HtmlForm;
import org.htmlunit.html.HtmlPage;
import org.htmlunit.html.HtmlSelect;

import solutions.aon.circe.exception.SegSocialException;
import solutions.aon.circe.toolkit.HtmlUnitToolkit;

public class Circe {
	public static void main(String[] args) throws Exception {
		
		String password = "Alma1981";
		FileInputStream certificateIs = new FileInputStream("/home/ndiaz/Descargas/aon.p12");
		String certificateType = KeyStore.getDefaultType();
		
		getCirce(certificateIs, password, certificateType);
	}
	
	public static void getCirce( InputStream certificateInputStream, String certificatePassword, String certificateType) throws FailingHttpStatusCodeException, MalformedURLException, IOException, SegSocialException, InterruptedException {
		
		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword,
				certificateType)) {
		HtmlPage htmlPage = webClient.getPage("https://paeelectronico.circe.es/Account/LoginVirtual?id=10240");
		HtmlUnitToolkit.checkStatusAndDown(htmlPage);
		
		HtmlForm sending = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("sending")).orElseThrow();
		
		htmlPage = sending.getButtonByName("send").click();
		HtmlUnitToolkit.checkStatusAndDown(htmlPage);
		
		HtmlSelect lenguageSelect = (HtmlSelect) htmlPage.getElementById("language-select");
		htmlPage = lenguageSelect.getOptionByText("Español").click();
		
		DomNodeList<DomNode> idpButtons = htmlPage.querySelectorAll(".idp-button");

		for (DomNode idpButton : idpButtons) {
			if (idpButton.getTextContent().contains("Certificado")) {
				htmlPage = ((HtmlButton) idpButton).click();
				break;
			}
		}
		
		DomNodeList<DomNode> dropdownToggles = htmlPage.querySelectorAll(".dropdown-toggle");
		for (DomNode dropdownToggle : dropdownToggles) {
			System.out.println(dropdownToggle);
			if (dropdownToggle.getTextContent().contains("Alta")) {
//				System.out.println(dropdownToggle);
//				htmlPage = ((HtmlButton) dropdownToggle).click();
//				break;
			}
		}
		
//		System.out.println(htmlPage.asXml());
		}

	}
}
		