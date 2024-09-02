package com.esferalia.aon.ingenet.servlet;

import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.github.javafaker.Faker;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.aonsolutions.tests.request.Method;
import net.aonsolutions.tests.request.Request;

public class TestIngenetDeliveryServlet extends AbstractOccamTest {

	private static final String FILE_NAME = "delivery.xml";
	Faker faker = new Faker();
	
	@Mock
    HttpServletRequest request;
 
    @Mock
    HttpServletResponse response;
    
    @Mock
    private OutputStream myOutputStream;
 
    //@BeforeAll
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }
	
	private static String getValue() throws IOException{
		String xml = "";
		
		InputStream is = TestIngenetDeliveryServlet.class.getResourceAsStream(FILE_NAME);
		File file = File.createTempFile("delivery", "xml");
        FileUtils.copyInputStreamToFile(is, file);
		
		try (
			BufferedReader xml_br = new BufferedReader(new FileReader(file))) {
			String sCurrentLine;
			while ((sCurrentLine = xml_br.readLine()) != null) {
				xml += sCurrentLine;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return xml.substring(xml.indexOf("<"));
	}
	
	@Test
	@Disabled
	public void test() throws IOException {
		IngenetDeliveryServletTest servlet = new IngenetDeliveryServletTest();
		when(request.getParameter(AbstractIngenetServlet.PARAM_USERNAME)).thenReturn("ingenet");
		when(request.getParameter(AbstractIngenetServlet.PARAM_PASSWORD)).thenReturn("1ng3n3t");
		when(request.getParameter(AbstractIngenetServlet.PARAM_VALUE)).thenReturn(getValue());
		when(request.getServerName()).thenReturn(DOMAIN_NAME);

		Request.request(Method.POST, request, response, servlet, "");
	}

	public static void main(String[] args) throws Exception {
		String path = "http://";
//		path += "http://udapa.esferalia.net:8080/aon-aio";
		path += "http://udapa.aonsolutions.me:8080/aon-aio";
//		path += "https://udapa.aonsolutions.net";
//		path += "https://cau.aonsolutions.net";
		path += "/ingenet/delivery";
		
		Scanner scanner = new Scanner(System.in);
		System.out.println("URL: " + path);
		System.out.print("Proceed? (y/n) (default yes): ");
		
		boolean exit = false;
		String inputText = null;
		while(!exit && scanner.hasNextLine()){
			inputText = scanner.nextLine();
			if(!"n".equals(inputText) && !"no".equals(inputText)){
				execute(path);
				System.out.println("Done.");
			} else {
				System.out.println("Aborted.");
			}
			exit = true;
		}
		scanner.close();
	}
			
	public static void execute(String path) throws Exception {
		String user = "ingenet";
		String passwd = "1ng3n3t";
		
		String xml = getValue();
		
		StringBuilder postData = new StringBuilder();
		postData.append('&');
		postData.append(URLEncoder.encode(AbstractIngenetServlet.PARAM_USERNAME, "UTF-8"));
		postData.append('=');
		postData.append(URLEncoder.encode(user, "UTF-8"));
		postData.append('&');
		postData.append(URLEncoder.encode(AbstractIngenetServlet.PARAM_PASSWORD, "UTF-8"));
		postData.append('=');
		postData.append(URLEncoder.encode(passwd, "UTF-8"));
		postData.append('&');
		postData.append(URLEncoder.encode(AbstractIngenetServlet.PARAM_VALUE, "UTF-8"));
		postData.append('=');
		postData.append(URLEncoder.encode(xml, "UTF-8"));
		
		byte[] postDataBytes = postData.toString().getBytes(StandardCharsets.UTF_8.name());
		
		URL url = new URL(path);
		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		conn.setRequestProperty("User-Agent", "Mozilla/5.0");
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
		try {
			conn.connect();
			conn.getOutputStream().write(postDataBytes);
			System.out.print("Server response: ");
			System.out.print("[" + conn.getResponseCode() + "] ");
			System.out.println(conn.getResponseMessage());
			
			
			BufferedReader br = new BufferedReader(new InputStreamReader(
					conn.getInputStream(), StandardCharsets.UTF_8.name()));
			StringBuffer sb = new StringBuffer();
			for (String in; (in = br.readLine()) != null;) {
				sb.append(in + "\n");
			}
			System.out.println(sb);
			br.close();
		} catch (ConnectException e) {
			System.out.println("IMPOSIBLE CONECTAR. " + e.getMessage());
		}
	}
		
}
