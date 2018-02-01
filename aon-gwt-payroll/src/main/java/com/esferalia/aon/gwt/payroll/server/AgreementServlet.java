package com.esferalia.aon.gwt.payroll.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.eclipsesource.v8.JavaCallback;
import com.eclipsesource.v8.NodeJS;
import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Array;
import com.eclipsesource.v8.V8Function;
import com.eclipsesource.v8.V8Object;

@SuppressWarnings("serial")
@WebServlet(name = "Sergio-Agreement", urlPatterns = { "/aon_gwt_payroll/sergio/*" })
public class AgreementServlet extends HttpServlet {
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {	
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		StringBuilder buffer = new StringBuilder();
	    BufferedReader reader = req.getReader();
	    String line;
	    while ((line = reader.readLine()) != null) {
	        buffer.append(line);
	    }
	    String data = buffer.toString();
	    System.out.println(data);
	    
//	    String callback = "function (sql, next) {"
//		    +"connection.query({"
//			      +"sql: sql.text,"
//			      +"values: sql.values"
//			    +"},"
//			    +"function(error, results, fields) {"
//			      +"console.log(sql);"
//			      +"if ( error )"
//			        +"throw error;"
//			      +"next();"
//			    +"});"
//			  +"}";
//	    
	    
	   
//	    prueba2(data);
	    
	}
	
	private void prueba(String data){
//		V8 runtime = V8.createV8Runtime();
		NodeJS nodeJS = NodeJS.createNodeJS();
		V8Object object = nodeJS.require(new File("/Users/sergio/aon.js/lib/agreement.js"));
		object.executeJSFunction("saveAgreement", data);
		object.release();
		nodeJS.release();
	}
	
	private void prueba2(String data){
		final NodeJS nodeJS = NodeJS.createNodeJS();
		final V8Object jimp = nodeJS.require(new File("/Users/sergio/aon.js/lib/agreement.js"));
		    
//		V8Function callback = new V8Function(nodeJS.getRuntime(), new JavaCallback() {  
//			public Object invoke(V8Object receiver, V8Array parameters) {
//				final V8Object image = parameters.getObject(1);
//				image.executeJSFunction("saveAgreement", data);
//				image.release();
//				return null;
//			}
//		});
		  
		jimp.executeJSFunction("saveAgreement", data);
//		jimp.executeJSFunction("saveAgreement", data, callback);
		    
		while(nodeJS.isRunning()) {
			nodeJS.handleMessage();
		}
		
//		callback.release();
		jimp.release();
		nodeJS.release();
	}
}
