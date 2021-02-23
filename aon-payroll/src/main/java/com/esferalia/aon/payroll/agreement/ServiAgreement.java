package com.esferalia.aon.payroll.agreement;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.DefaultCredentialsProvider;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;

import solutions.aon.sepe.exceptions.certificate.InvalidCertificateException;

public class ServiAgreement {
	
	private final static String URL = "aonsolutions.serviconvenios.com/";
	private final static String USER = "aonsolutions";
	private final static String PASS = "lNgFdEw&65hD@";
	
	public enum Extension {
		PDF(".pdf"), 
		XLS(".xls"),
		XML(".xml");
		
		private String value;
		
		public String getValue() {
			return value;
		}
		
		private Extension(String value) {
			this.value = value;
		}
	}
	
	public static InputStream get_online_file(String serviAgreementCode, Extension extension) {
		String url = URL + serviAgreementCode + extension.getValue();
		return get_online_file(url, USER, PASS);
	}
	
	public static InputStream get_online_file(String url, String user, String password) {
		WebClient webClient;
		try {
			webClient = getWebClient(user, password);
			Page page = webClient.getPage("https://" + url);
			InputStream is = page.getWebResponse().getContentAsStream();
			return is;
		} catch (InvalidCertificateException | FailingHttpStatusCodeException | IOException e) {e.printStackTrace();}
		return null;
	}
	
	// GET THE WEB CLIENT OF HTMLUNIT
	public static WebClient getWebClient(final String user, final String password)
			throws InvalidCertificateException {
		try {
			WebClient webClient = new WebClient(BrowserVersion.BEST_SUPPORTED);
			webClient.getOptions().setCssEnabled(false);
			webClient.getOptions().setDownloadImages(false);
			webClient.setJavaScriptTimeout(10000);
			webClient.setAjaxController(new NicelyResynchronizingAjaxController());

			DefaultCredentialsProvider creds = new DefaultCredentialsProvider();
			creds.addCredentials(user,password);
			webClient.setCredentialsProvider(creds);

			return webClient;
		} catch (RuntimeException e) {
			throw new InvalidCertificateException();
		}
	}

	public static void main(String[] args) {
		InputStream is = get_online_file("c0000004", Extension.XML);
		try {
			System.out.println("writing....");
			FileOutputStream os = new FileOutputStream("/Users/sergio/Desktop/c0000004.xml");
			is.transferTo(os);
			os.close();
			System.out.println(">> DONE.");
		} catch (IOException e) {e.printStackTrace();}
		
	}
	
}
