package com.esferalia.aon.htmlunit;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

import org.htmlunit.FailingHttpStatusCodeException;
import org.htmlunit.WebClient;
import org.htmlunit.html.HtmlForm;
import org.htmlunit.html.HtmlInput;
import org.htmlunit.html.HtmlPage;
import org.htmlunit.html.HtmlSubmitInput;
import org.htmlunit.html.HtmlTextInput;

public class Main {
	
	public static void main1(String[] args) throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		   try (final WebClient webClient = new WebClient()) {
			   
			   webClient.getOptions().setJavaScriptEnabled(false);
			   webClient.getOptions().setDownloadImages(false);
		        // Get the first page
		        final HtmlPage page1 = webClient.getPage("https://general-payroll-test.aonsolutions.org/");
		        

		        // Get the form that we are dealing with and within that form, 
		        // find the submit button and the field that we want to change.
		        final HtmlForm form = (HtmlForm) page1.getElementById("login");
		        
		        final HtmlSubmitInput button = form.getInputByName("login_btn");
		        final HtmlTextInput qField = form.getInputByName("j_username");
		        final HtmlInput p = form.getInputByName("j_password");


		        // Change the value of the text field
		        qField.type("admin");
		        p.type("org");

		        // Now submit the form by clicking the button and get back the second page.
		        final HtmlPage page2 = button.click();
		        
		        System.out.println(page2.asXml());
		        
		    }		
	}
	
	public static void main(String[] args) throws Exception {
		String urlParameters  = "j_username=admin&j_password=org";
		byte[] postData       = urlParameters.getBytes( StandardCharsets.UTF_8 );
		int    postDataLength = postData.length;
		URL url  = new URL( "http://payroll-test.aonsolutions.org:8080/aon-aio/login" );
		HttpURLConnection conn= (HttpURLConnection) url.openConnection();           
		conn.setDoOutput( true );
		conn.setInstanceFollowRedirects( false );
		conn.setRequestMethod( "POST" );
		conn.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded"); 
		conn.setRequestProperty( "charset", "utf-8");
		conn.setRequestProperty( "Content-Length", Integer.toString( postDataLength ));
		conn.setUseCaches( false );
		try( DataOutputStream wr = new DataOutputStream( conn.getOutputStream())) {
		   wr.write( postData );
		}
		
		Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

        for (int c; (c = in.read()) >= 0;)
            System.out.print((char)c);
        
		//conn.connect();
		
		//System.out.println(conn.getContent()); 
	}

}
