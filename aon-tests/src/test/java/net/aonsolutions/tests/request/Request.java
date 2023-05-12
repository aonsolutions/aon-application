package net.aonsolutions.tests.request;

import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

public class Request {

	public static JSONObject requestJSONObject(Method method, HttpServletRequest request, HttpServletResponse response, IServlet visitor, JSONObject json) {
		String result = request(method, request, response, visitor, json);
		return new JSONObject(result);
	}
	
	public static JSONArray requestJSONArray(Method method, HttpServletRequest request, HttpServletResponse response, IServlet visitor, JSONObject json) {
		String result = request(method, request, response, visitor, json);
		return new JSONArray(result);
	}

	public static String request(Method method, HttpServletRequest request, HttpServletResponse response, IServlet visitor, JSONObject json) {
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
	    
	    return sw.getBuffer().toString().trim();
	}
	
	public static String request(Method method, HttpServletRequest request, HttpServletResponse response, IServlet visitor, String data) {
		try {
			when(request.getReader()).thenReturn(
			    new BufferedReader(new StringReader(data)));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		when(request.getContentType()).thenReturn("application/x-www-form-urlencoded");
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
	    
	    return sw.getBuffer().toString().trim();
	}
}
