package com.esferalia.aon.gwt.api.server;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import jakarta.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.api.client.IApi;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@WebServlet(name = "ApiGwtServlet", urlPatterns = { "/aon_gwt_aio/gwt_api",
													"/aon_gwt_fiscal/gwt_api"})
public class ApiImpl extends RemoteServiceServlet implements IApi{

	private static final long serialVersionUID = 1L;
	
	public String base(String str){
		return Base64.getEncoder().encodeToString(str.getBytes(StandardCharsets.UTF_8));
	}

}