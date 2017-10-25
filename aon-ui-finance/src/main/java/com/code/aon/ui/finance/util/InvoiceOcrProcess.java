package com.code.aon.ui.finance.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Scanner;

import org.json.JSONException;
import org.json.JSONObject;

import com.code.aon.finance.Invoice;

public class InvoiceOcrProcess {
	
	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				
	public void execute(String path, String domain, byte[] data, Invoice invoice) throws Exception {
		if(data!=null){
			String user = "ingenet";
			String passwd = "1ng3n3t";
			StringBuilder postData = new StringBuilder();
			postData.append('&').append("username");
			postData.append('=').append(user);
			postData.append('&').append("password");
			postData.append('=').append(passwd);
			postData.append('&').append("domain");
			postData.append('=').append(domain);
			postData.append('&').append("value");
			postData.append('=').append(data);
			postData.append('&').append("data");
			postData.append('=').append(data);
			byte[] postDataBytes = postData.toString().getBytes(StandardCharsets.UTF_8.name());
			
			URL url = new URL((path.startsWith("http://")?"":"http://")+path);
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			conn.setDoOutput(true);
			conn.setInstanceFollowRedirects( false );
			conn.setRequestMethod("POST");
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
				fillInvoice(sb.toString(), invoice);
				br.close();
			} catch (IOException e) {
				System.out.println("IMPOSIBLE CONECTAR. " + e.getMessage());
			}
		} else {
			// TODO data is null
			data = "{ \"number\": \"F0123456789\", \"document\": A00000000, \"date\": \"2017-10-01\", \"amount\": 111.11 }".getBytes();
			fillInvoice(new String(data), invoice);
		}
	}
	
	private void fillInvoice(String value, Invoice invoice) {
		try {
			JSONObject obj = new JSONObject(value);
			String number = obj.getString("number");
			String document = obj.getString("document");
			String date = obj.getString("date");
			Double amount = obj.getDouble("amount");
			
			invoice.setReferenceCode(number);
			invoice.setRegistryDocument(document);
			invoice.setIssueDate(sdf.parse(date));
			invoice.setTaxDate(sdf.parse(date));
			invoice.setTotal(amount);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	
	public static void main(String[] args) throws Exception {
		String path = "";
		path = "http://example.domain/ocr";
		
		Scanner scanner = new Scanner(System.in);
		System.out.println("URL: " + path);
		System.out.print("Proceed? (y/n) (default yes): ");
		
		boolean exit = false;
		String inputText = null;
		while(!exit && scanner.hasNextLine()){
			inputText = scanner.nextLine();
			if(!"n".equals(inputText) && !"no".equals(inputText)){
				InvoiceOcrProcess ocr = new InvoiceOcrProcess();
				ocr.execute(path, null, null, new Invoice());
				System.out.println("Done.");
			} else {
				System.out.println("Aborted.");
			}
			exit = true;
		}
		scanner.close();
	}
	
}
