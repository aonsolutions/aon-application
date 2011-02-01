package com.code.aon.ui.resources.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

public class ExpiresFilter implements Filter {
	
	private static final String DEFAULT_EXPIRES = "defaultExpires";
	
	private long defaultExpires = 600000;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		if ( response instanceof HttpServletResponse ) {
			HttpServletResponse hsr = (HttpServletResponse) response;
			hsr.setHeader("Cache-Control", "Public");

			// Set Expires to current time + one year.
			long currentTime = System.currentTimeMillis();

			hsr.setDateHeader("Expires", currentTime + defaultExpires);	
		}
		chain.doFilter(request, response);
	}
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		String value = filterConfig.getInitParameter(DEFAULT_EXPIRES);
		if ( (!StringUtils.isBlank(value)) && NumberUtils.isNumber(value) ) {
			defaultExpires = Long.valueOf(value);
		}
	}
	
	@Override
	public void destroy() {
	}

}
