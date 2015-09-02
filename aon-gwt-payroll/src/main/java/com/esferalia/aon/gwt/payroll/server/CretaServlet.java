package com.esferalia.aon.gwt.payroll.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.gwt.payroll.shared.CretaService;
import com.esferalia.aon.payroll.tgss.creta.Borrador;
import com.esferalia.aon.payroll.tgss.creta.Calculo;
import com.esferalia.aon.payroll.tgss.creta.Confirmacion;
import com.esferalia.aon.payroll.tgss.creta.TrabajadoresTramos;



@SuppressWarnings("serial")
@WebServlet(name = "SLD-Solicitud", urlPatterns = { "/aon_gwt_payroll/sdl/*" })
public class CretaServlet extends HttpServlet implements CretaService.File.Visitor<HttpServletRequest, HttpServletResponse, Exception> {
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		String uri = req.getRequestURI();
		String fichero = AonServletUtils.getFileName(uri);
		
		CretaService.File file = CretaService.File.valueOf(fichero) ;
		
		try {
			resp.setContentType("text/xml;");
			file.accept(this, req, resp );
		} catch ( Exception e ){
			throw new ServletException(e);
		}
		
	}
	
	// ------------------------------------------------------------------------
	// CretaService.Solicitud.Visitor<HttpServletRequest, HttpServletResponse, Exception>
	
	@Override
	public void visitBases(HttpServletRequest t, HttpServletResponse l)
			throws Exception {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void visitBorrador(HttpServletRequest req, HttpServletResponse resp) throws Exception{
		String mes = req.getParameter(CretaService.Parameter.MES.name());
		String anho = req.getParameter(CretaService.Parameter.ANHO.name());
		String tipo = req.getParameter(CretaService.Parameter.TIPO.name());
		String cccs [] = req.getParameterValues(CretaService.Parameter.CCC.name());
		String autorizado = req.getParameter(CretaService.Parameter.AUTORIZADO.name());
		boolean aceptarBasesAnteriores= Boolean.parseBoolean(req.getParameter(CretaService.Parameter.ACEPTAR_BASES_ANTERIORES.name()));
		Borrador.generate(autorizado, mes, anho, tipo, aceptarBasesAnteriores, cccs, resp.getOutputStream());
	}
	
	@Override
	public void visitConfirmacion(HttpServletRequest req, HttpServletResponse resp) throws Exception{
		String mes = req.getParameter(CretaService.Parameter.MES.name());
		String anho = req.getParameter(CretaService.Parameter.ANHO.name());
		String tipo = req.getParameter(CretaService.Parameter.TIPO.name());
		String cccs [] = req.getParameterValues(CretaService.Parameter.CCC.name());
		String autorizado = req.getParameter(CretaService.Parameter.AUTORIZADO.name());
		Confirmacion.generate(autorizado, mes, anho, tipo, cccs, resp.getOutputStream());
	}
	
	@Override
	public void visitCalculos(HttpServletRequest req, HttpServletResponse resp) throws Exception{
		String mes = req.getParameter(CretaService.Parameter.MES.name());
		String anho = req.getParameter(CretaService.Parameter.ANHO.name());
		String tipo = req.getParameter(CretaService.Parameter.TIPO.name());
		String cccs [] = req.getParameterValues(CretaService.Parameter.CCC.name());
		String autorizado = req.getParameter(CretaService.Parameter.AUTORIZADO.name());
		Calculo.generate(autorizado, mes, anho, tipo, cccs, resp.getOutputStream());
	} 
	
	@Override
	public void visitTrabajadoresTramos(HttpServletRequest req, HttpServletResponse resp) throws Exception{
		String mes = req.getParameter(CretaService.Parameter.MES.name());
		String anho = req.getParameter(CretaService.Parameter.ANHO.name());
		String tipo = req.getParameter(CretaService.Parameter.TIPO.name());
		String cccs [] = req.getParameterValues(CretaService.Parameter.CCC.name());
		String autorizado = req.getParameter(CretaService.Parameter.AUTORIZADO.name());
		TrabajadoresTramos.generate(autorizado, mes, anho, tipo, cccs, resp.getOutputStream());
	}
	
	
	
}
