package net.aonsolutions.aon.api.test.request;

import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

public class Request {

	
	public static JSONObject request(Method method, HttpServletRequest request, HttpServletResponse response, ServletVisitor visitor, JSONObject json) {
		try {
			when(request.getReader()).thenReturn(
			    new BufferedReader(new StringReader(json.toString())));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		when(request.getContentType()).thenReturn("application/json");
		when(request.getCharacterEncoding()).thenReturn("UTF-8");
	
	    StringWriter sw = new StringWriter();
	    PrintWriter pw = new PrintWriter(sw);
	         

		try {
			when(response.getWriter()).thenReturn(pw);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

	    if(Method.GET.equals(method)) {
	    	visitor.get(request, response);
	    } else if(Method.POST.equals(method)) {
	    	visitor.post(request, response);
	    } else if(Method.PUT.equals(method)) {
	    	visitor.put(request, response);
	    } else if(Method.DELETE.equals(method)) {
	    	visitor.delete(request, response);
	    } else if(Method.PATCH.equals(method)) {
	    	visitor.patch(request, response);
	    }
	    
	    String result = sw.getBuffer().toString().trim();
	    return new JSONObject(result);
	}
}
