package com.esferalia.aon.ingenet.servlet;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;


public class TestIngenetDeliveryServlet  {

	private static String getValue(){
		String xml = "";
		
		String filePath = "/tmp/delivery.xml";
		filePath = "/home/eagirrezabal/Descargas/delivery.xml";
		
		
		try (
			BufferedReader xml_br = new BufferedReader(new FileReader(filePath))) {
			String sCurrentLine;
			while ((sCurrentLine = xml_br.readLine()) != null) {
				xml += sCurrentLine;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		
		return xml.substring(xml.indexOf("<"));
	}

	public static void main(String[] args) throws Exception {
		String path = "";
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
		xml = "";
		
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
