package com.code.aon.aio.servlet;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import com.esferalia.aon.gwt.payroll.client.EnterprisesService;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.watson.server.http.AonURIBuilder;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "UrlShortener", urlPatterns = {  "/" + EnterprisesService.URL_SHORTENER_PATH + "/*" })
public class URLShortenerServlet extends HttpServlet {

	private static class ForwardHttpServletRequestWrapper extends HttpServletRequestWrapper {
		private URI uri;
		private Map<String, String[]> parameterMap;

		public ForwardHttpServletRequestWrapper(HttpServletRequest request, String url) {
			super(request);
			this.uri = URI.create(url);
			this.parameterMap = new AonURIBuilder(uri).getQueryParamsMap();
		}

		@Override
		public String getQueryString() {
			return uri.getQuery();
		}

		@Override
		public Map<String, String[]> getParameterMap() {
			return Collections.unmodifiableMap(parameterMap);
		}
		
		@Override
		public String getParameter(String name) {
			return parameterMap.getOrDefault(name, new String[]{null})[0];
		}
		
		@Override
		public String[] getParameterValues(String name) {
			return parameterMap.getOrDefault(name, null);
		}
		
		@Override
		public Enumeration<String> getParameterNames() {
			return new Vector<String>(parameterMap.keySet()).elements();
		}

		public void forward(ServletResponse response) throws ServletException, IOException {
			super.getRequestDispatcher(uri.getPath()).forward(this, response);
		}

	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		this.doGet(req, resp);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String shortURL = req.getRequestURL().append('?').append(req.getQueryString()).toString();
		String longURL = AON.getURL(shortURL);
		new ForwardHttpServletRequestWrapper(req, longURL).forward(resp);
	}
	


}
