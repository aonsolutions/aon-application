package com.esferalia.aon.ingenet.servlet;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class TestIngenetElaborationServlet {

	
	private static String getValue(){
		String xml = "";
		
//		String filePath = "//tmp//consultaElaboraciones_example.xml";
//		try (
//			BufferedReader xml_br = new BufferedReader(new FileReader(filePath))) {
//			String sCurrentLine;
//			while ((sCurrentLine = xml_br.readLine()) != null) {
//				xml += sCurrentLine;
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
		xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
//				+ "<CONSULTA_ELABORACIONES><DATOS_CONSULTA_ELABORACIONES><PARAMETROS_BUSQUEDA>"
//				+ "<ACCION>RECUPERAR</ACCION>"
//				+ "<FECHA>20170125</FECHA>"
////				+ "<ESTADO>PENDIENTE</ESTADO>"
//				+ "<ESTADO>PROCESANDO</ESTADO>"
//				+ "</PARAMETROS_BUSQUEDA></DATOS_CONSULTA_ELABORACIONES></CONSULTA_ELABORACIONES>"
				;
		
//		xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
//				+ "<CONSULTA_ELABORACIONES><DATOS_CONSULTA_ELABORACIONES><PARAMETROS_BUSQUEDA>"
//				+ "<ACCION>CANCELAR</ACCION>"
//				+ "<ELABORACIONES>"
//				+ "<REFERENCIAS><SERIE>PV17</SERIE><NUMERO>25</NUMERO></REFERENCIAS>"
//				+ "<REFERENCIAS><SERIE>PV17</SERIE><NUMERO>24</NUMERO></REFERENCIAS>"
//				+ "</ELABORACIONES>"
//				+ "</PARAMETROS_BUSQUEDA></DATOS_CONSULTA_ELABORACIONES></CONSULTA_ELABORACIONES>"
//				;

		return xml;
	}
	
	public static void main(String[] args) throws Exception {
		String path = "http://";
		path += "udapa.esferalia.net";
		path += ":8080";
		path += "/aon-aio";
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
        conn.connect();
        conn.getOutputStream().write(postDataBytes);

        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8.name()));
        StringBuffer sb = new StringBuffer();
        for(String in; (in = br.readLine()) != null;) {
            sb.append(in + "\n");
        }
        System.out.println(sb);
        br.close();
	}

}
