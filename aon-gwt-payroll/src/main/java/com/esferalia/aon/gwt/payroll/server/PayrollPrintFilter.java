package com.esferalia.aon.gwt.payroll.server;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.aonsolutions.core.pool.AonConnectionException;


@WebFilter(
	servletNames = {
			"Salary-MacLeod"
	}	
)
public class PayrollPrintFilter extends HttpFilter {
	
	@Override
	protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		response.sendError(HttpServletResponse.SC_GONE, "El enlace que has seguido ha caducado. Por favor, solicite uno nuevo para consultar la(s) nómina(s).");
	}
	
	

}
