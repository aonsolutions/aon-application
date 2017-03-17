package com.esferalia.aon.ingenet.servlet;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class TestIngenetElaborationServlet {

	
	private static String getValue(){
		@SuppressWarnings("unused")
		String sampleText = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";
		String xml = "";
		
		String filePath = "/temp/consultaElaboraciones_example.xml";
		try (
			BufferedReader xml_br = new BufferedReader(new FileReader(filePath))) {
			String sCurrentLine;
			while ((sCurrentLine = xml_br.readLine()) != null) {
				xml += sCurrentLine;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
				+ "<CONSULTA_ELABORACIONES><DATOS_CONSULTA_ELABORACIONES><PARAMETROS_BUSQUEDA>"
				
				// RECUPERAR
				+ "<ACCION>RECUPERAR</ACCION>"
				+ "<FECHA>20170310</FECHA>"
				+ "<ESTADO>PENDIENTE</ESTADO>"
				+ "<ESTADO>PROCESANDO</ESTADO>"
				+ "<ESTADO>FINALIZADO</ESTADO>"

				// CANCELAR
//				+ "<ACCION>CANCELAR</ACCION>"
//				+ "<ELABORACIONES>"
//				+ "<REFERENCIAS>"
//				+ "<SERIE>PV17</SERIE><NUMERO>113</NUMERO><OBSERVACIONES>"+sampleText+"</OBSERVACIONES>"
//				+ "</REFERENCIAS>"
//				+ "<REFERENCIAS>"
//				+ "<SERIE>PV17</SERIE><NUMERO>112</NUMERO><OBSERVACIONES>"+sampleText+"</OBSERVACIONES>"
//				+ "</REFERENCIAS>"
//				+ "</ELABORACIONES>"

				+ "</PARAMETROS_BUSQUEDA></DATOS_CONSULTA_ELABORACIONES></CONSULTA_ELABORACIONES>";

		return xml;
	}
	
	public static void main(String[] args) throws Exception {
		String path = "";
		path += "http://udapa.esferalia.net:8080/aon-aio";
//		path += "https://udapa.aonsolutions.net";
		path += "/ingenet/elaboration";
		
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
        	
        	BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8.name()));
        	StringBuffer sb = new StringBuffer();
        	for(String in; (in = br.readLine()) != null;) {
        		sb.append(in + "\n");
        	}
        	System.out.println(sb);
        	br.close();
        } catch (ConnectException e) {
			System.out.println("IMPOSIBLE CONECTAR. " + e.getMessage());
		}
	}

}
